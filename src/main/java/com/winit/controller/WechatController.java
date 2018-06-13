package com.winit.controller;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.winit.VO.ResultVO;
import com.winit.config.ProjectUrlConfig;
import com.winit.dataobject.*;
import com.winit.enums.MessageTypeEnum;
import com.winit.enums.ResultEnum;
import com.winit.enums.UserTypeEnum;
import com.winit.exception.SellException;
import com.winit.repository.WxSourceRepository;
import com.winit.service.UserService;
import com.winit.service.WechatService;
import com.winit.service.WxMessageService;
import com.winit.service.WxRuleService;
import com.winit.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMediaImgUploadResult;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by liyou
 * 2017-07-03 01:20
 */
@Api(tags = "微信接口")
@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private WxMpService wxOpenService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private WxRuleService wxRuleService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private WxMessageService wxMessageService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping(value = "/authorize/page", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView gotoAuthorize() throws WxErrorException {
        return new ModelAndView("wechat/authen");
    }

    @GetMapping("/signature")
    @ResponseBody
    public ResultVO createSignature(@RequestParam("url") String url) throws WxErrorException {
        url = url.replaceAll("wxOpenid","fxwxOpenid");
        log.info("signature:"+url);
        WxJsapiSignature wxJsapiSignature =  wxOpenService.createJsapiSignature(url);
        return ResultVOUtil.success(wxJsapiSignature);
    }

    @GetMapping("/authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");
        //1. 配置
        //2. 调用方法
        String url = projectUrlConfig.getWechatMpAuthorize() + "/sell/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO,URLEncoder.encode(returnUrl));

//        // https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3e3245c32dee0955&
//        // redirect_uri=http://www.wstsoftware.com/sell/wechat/userInfo
//        // &response_type=code&scope=snsapi_userinfo&state=
//        // http://www.wstsoftware.com/product/detail/327#wechat_redirect
        log.info("authorize:"+redirectUrl);

       return "redirect:" + redirectUrl;
    }

    /**
     * 返回微信授权URL地址
     * @param returnUrl
     * @param response
     * @return
     */
    @GetMapping("/authorizeUrl")
    @ResponseBody
    public ResultVO authorizeUrl(@RequestParam("returnUrl") String returnUrl,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin","*");
        String url = projectUrlConfig.getWechatMpAuthorize() + "/sell/wechat/userInfo";
        String redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAUTH2_SCOPE_USER_INFO,URLEncoder.encode(returnUrl));
       log.info("返回微信授权URL地址 authorizeUrl: "+redirectUrl);
        return ResultVOUtil.success(redirectUrl);
    }

    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("code") String code,  @RequestParam("state") String returnUrl, HttpServletResponse response) {

        response.setHeader("Access-Control-Allow-Origin","*");
        String openId = "";
        //如果Code相同
        log.info("userInfo-code"+code);
        if(stringRedisTemplate.hasKey(code)){
            openId =  (String)stringRedisTemplate.opsForValue().get(code);
        }else {
            try {
                WxMpOAuth2AccessToken  wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
                openId = wxMpOAuth2AccessToken.getOpenId();
                stringRedisTemplate.opsForValue().set(code, openId,60,TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间

                User user = userService.selectByUserWxOpenid(openId);
                if(null == user && !openId.isEmpty()){
                    WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, "");
                    user = new User();
                    user.setUserName(wxMpUser.getNickname());
                    user.setUserPhone("");
                    user.setUserWxName(wxMpUser.getNickname());
                    user.setUserWxOpenid(openId);
                    user.setUserWxPicture(wxMpUser.getHeadImgUrl());
                    user.setUserCtype(UserTypeEnum.ORIDINARY.getCode());
                    userService.create(user);
                    log.info("获取wxMpUser:"+wxMpUser.toString());
                    String winit = URLParser.fromURL(returnUrl).compile().getParameter("winit");
                    if(StringUtils.isNotEmpty(winit)) {
                        stringRedisTemplate.opsForValue().set(winit, openId, 60, TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间
                        log.info("向redis里存入winit: " + winit);
                    }
//                    //往cookie中添加值
//                    CookieUtils.addCookie("wxOpenidCookie",openId);
                    //处理returnUrl
                    if(returnUrl.contains("winit")){
                        String winitparam =  winit.concat("winit=").concat(winit);
                        returnUrl = returnUrl.replace(winitparam,"");
                    }
                    log.info("处理后的returnUrl:"+returnUrl);
                }
            } catch (WxErrorException e) {
                log.error("【微信网页授权】{}", e);
            }catch (Exception ex){}
        }

        if(returnUrl.contains("?")){
            returnUrl += "&wxOpenid="+openId;
        }else{
            returnUrl += "?wxOpenid="+openId;
        }

        return "redirect:" + returnUrl;
    }
    
    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getUserInfo")
    @ResponseBody
    public ResultVO getUserInfo(@RequestParam("openid") String openid,HttpServletResponse response) throws WxErrorException {
        response.setHeader("Access-Control-Allow-Origin","*");
        if(StringUtils.isNotEmpty(openid)){
            if(openid.contains("winit")){
              Object object =  stringRedisTemplate.opsForValue().get(openid);//从redis里取值
                return ResultVOUtil.success(object);
            }
        }
        User user = userService.selectByUserWxOpenid(openid);
        return ResultVOUtil.success(user);
    }

    @ApiOperation(value = "添加微信菜单")
    @PostMapping("/menu")
    @ResponseBody
    public ResultVO<Map> insert(@RequestBody WxMenu wxMenu, BindingResult bindingResult, HttpServletResponse response){

        wxMenu.setCrtTime(new Date());
        wxMenu.setUptTime(new Date());

        Map<String, String> reuslt = new HashMap<String, String>();
        reuslt = wechatService.addMenu(wxMenu);
        return ResultVOUtil.success(reuslt);
    }

    @ApiOperation(value = "修改微信菜单")
    @PatchMapping("/menu")
    @ResponseBody
    public ResultVO update(@RequestBody WxMenu wxMenu){
        wxMenu.setUptTime(new Date());
        wechatService.updateMenu(wxMenu);
        return ResultVOUtil.success();
    }

    @ApiOperation(value = "获取微信菜单信息")
    @GetMapping("/menu")
    @ResponseBody
    public ResultVO<WxMenu> getWxMenu(@RequestParam(value = "id") Long  id){
        WxMenu wxMenu = wechatService.getMenu(id);
        return ResultVOUtil.success(wxMenu);
    }

    @ApiOperation(value = "获取微信菜单列表")
    @GetMapping("/menu/list")
    @ResponseBody
    public ResultVO<WxMenu> getWxMenuList(@RequestParam(value = "id", required = false) Long  id){
        List<WxMenu> list =  null;
        if(null==id){
            list = wechatService.getTopMenu();
        }else{
            list = wechatService.getMenuByParentId(id);
        }
        return ResultVOUtil.success(list);
    }

    @ApiOperation(value = "删除微信菜单")
    @DeleteMapping("/menu")
    @ResponseBody
    public ResultVO<WxMenu> deleteWxMenu(@RequestParam(value = "id") Long  id){
        wechatService.deleteMenu(id);
        return ResultVOUtil.success();
    }

    @ApiOperation(value = "同步微信菜单接口")
    @PostMapping("/menu/synchro")
    @ResponseBody
    public ResultVO synchroWxMenu(){
        wechatService.synchroWxMenu();
        return ResultVOUtil.success();
    }

    @ApiOperation(value = "微信消息回复 微信通知调用")
    @RequestMapping(value = "/message/reply",method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String messageReply(HttpServletRequest request) throws IOException {

        WxMpXmlMessage message;
        try {
            message = WxMpXmlMessage.fromXml(request.getInputStream());
        }catch (Exception e){
            log.info(e.getMessage());
            message = null;
        }
        if(null != message) {
            String messageType = message.getMsgType();
            if ("text".equals(messageType) || "event".equals(messageType)) {
                String fromUser = message.getFromUser();
                String toUser = message.getToUser();

                String content = message.getContent();
                String eventKey = message.getEventKey();

                String keyWord = content == null ? content : eventKey;
                String messagXml = wxRuleService.createMessage(toUser, fromUser, keyWord);
                return messagXml;
            }
            return null;
        }else{
            String echostr = request.getParameter("echostr");
            return echostr;
        }

    }

    @ApiOperation("添加微信回复")
    @RequestMapping(value="/wxMessage",method=RequestMethod.GET,produces="application/json;charset=UTF-8")
    @ResponseBody
    public ResultVO<WxRule> addMessageReply(
                                            @ApiParam(value = "消息类型 1：文本，2：图片，3：语音，4：视频，5：音乐，6：图文'") @RequestParam ()String replyType,
                                            @ApiParam(value = "标题")@RequestParam ("messageName") String messageName,
                                            @ApiParam(value = "图片")@RequestParam (value = "picture",required = false) MultipartFile  picture,//内容海报（即上传的图片）
                                            @ApiParam(value = "关键字") @RequestParam ("keyWords") String keyWords,
                                            @ApiParam(value = "图文消息链接地址") @RequestParam (value = "newsUrl",required = false) String  newsUrl,
                                            @ApiParam(value = "内容")@RequestParam (value = "content",required = false) String content
    ) throws WxErrorException,IOException {

        WxMessage wxMessage = new WxMessage();

        if(null != picture){
            File file = changeFile(picture);

            WxMediaImgUploadResult wxMediaImgUploadResult = wxMpService.getMaterialService().mediaImgUpload(file);
            wxMessage.setPictureUrl(wxMediaImgUploadResult.getUrl());
        }

        wxMessage.setReplyType(replyType);
        wxMessage.setMessageName(messageName);
        wxMessage.setKeyWords(keyWords);
        wxMessage.setNewsUrl(newsUrl);
        wxMessage.setContent(content);

        wxMessageService.addWxMessage(wxMessage);
        return ResultVOUtil.success(wxMessage);

    }

    private File changeFile(MultipartFile  picture) throws IOException {

        String path = System.getProperty("user.dir");
        Long time = System.currentTimeMillis();
        File file = new File(path+"\\"+time+picture.getOriginalFilename());
        if(!file.exists()){
            Boolean result = file.createNewFile();
        }
        picture.transferTo(file);

    /*   InputStreamReader isr = new InputStreamReader(picture.getInputStream());

        String fileName = picture.getOriginalFilename();
        File file = new File("new"+fileName);

        if(!file.exists()){
            Boolean result = file.createNewFile();
        }

        OutputStream outputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        FileWriter fw = new FileWriter(file);


        System.out.println(file.getCanonicalPath());
        int value = isr.read();
        while (value>=0){
            outputStreamWriter.write(value);
            value = isr.read();
        }
        fw.flush();
        fw.close();
        isr.close();*/
        return file;
    }

    @ApiOperation("修改微信回复")
    @RequestMapping(value="/wxMessage/{id}",method=RequestMethod.GET, produces="application/json;charset=UTF-8")
    @ResponseBody
    public ResultVO<WxMessage> updateMessageReply(@PathVariable(value = "id") Long id,
                                                  @ApiParam(value = "消息类型 1：文本，2：图片，3：语音，4：视频，5：音乐，6：图文'") @RequestParam (value = "replyType",required = false)String replyType,
                                                  @ApiParam(value = "标题")@RequestParam ( value = "messageName",required = false) String messageName,
                                                  @ApiParam(value = "图片")@RequestParam (value = "picture",required = false) MultipartFile  picture,//内容海报（即上传的图片）
                                                  @ApiParam(value = "关键字") @RequestParam (value = "keyWords",required = false) String keyWords,
                                                  @ApiParam(value = "链接地址")@RequestParam (value = "newsUrl",required = false) String newsUrl,
                                                  @ApiParam(value = "内容")@RequestParam (value = "content",required = false) String content
    ) throws WxErrorException, IOException {

        WxMessage wxMessage = wxMessageService.getWxMessage(id);
        if(replyType.equals(MessageTypeEnum.TEXT.getCode())){
            wxMessage.setContent(content);
        }else if(replyType.equals(MessageTypeEnum.NEWS.getCode())){
            if(null!=picture){
                File file = changeFile(picture);
                WxMediaImgUploadResult wxMediaImgUploadResult = wxMpService.getMaterialService().mediaImgUpload(file);
                wxMessage.setPictureUrl(wxMediaImgUploadResult.getUrl());
            }
            wxMessage.setNewsUrl(newsUrl);
        }
        wxMessage.setKeyWords(keyWords);
        wxMessage.setMessageName(messageName);
        wxMessageService.updateWxMessage(wxMessage);

        return ResultVOUtil.success(wxMessage);
    }

    @ApiOperation("获取微信回复")
    @RequestMapping(value="/wxMessage/{id}",method=RequestMethod.GET)
    @ResponseBody
    public ResultVO<WxMessage> getWxMessage(@PathVariable(value = "id") Long id) throws WxErrorException {

        WxMessage wxMessage = wxMessageService.getWxMessage(id);

        return ResultVOUtil.success(wxMessage);
    }

    @ApiOperation("删除微信回复")
    @RequestMapping(value="/wxMessage/{id}",method=RequestMethod.DELETE)
    @ResponseBody
    public ResultVO<WxMessage> deleteWxMessage(@PathVariable(value = "id") Long id) throws WxErrorException {

        wxMessageService.deleteWxMessage(id);

        return ResultVOUtil.success();
    }

    @ApiOperation("分页查询微信回复")
    @RequestMapping(value="/wxMessage/list",method=RequestMethod.GET)
    @ResponseBody
    public ResultVO<Page> findWxMessagePage( @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size) throws WxErrorException {

        PageRequest request = new PageRequest(page, size);
        Page<WxMessage> wxMessagePage = wxMessageService.findWxMessagePage(request);

        return ResultVOUtil.success(wxMessagePage);
    }
}