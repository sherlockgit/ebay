package com.winit.controller;



		import com.aliyun.oss.OSSClient;
		import com.winit.VO.ResultVO;
		import com.winit.config.OSSConfig;
		import com.winit.enums.ResultEnum;
		import com.winit.utils.OSSUtil;
		import com.winit.utils.ResultVOUtil;
		import lombok.extern.slf4j.Slf4j;
		import org.springframework.web.bind.annotation.*;
		import org.springframework.web.multipart.MultipartFile;

/**
 * Created by yhy
 * 2017-10-22 14:27
 */
@RestController
@RequestMapping("/seller")
@Slf4j
public class UploadFileController {

	/**
	 * 文件上传具体方法
	 * @param file
	 * @return
	 */
	@PostMapping("/upload")
	public ResultVO UploadImage(@RequestParam("file") MultipartFile file){

		if(file.isEmpty()){
		    return ResultVOUtil.error(ResultEnum.FILE_ISNELL.getCode(),ResultEnum.FILE_ISNELL.getMessage());
		}

		if(!fileType(file.getOriginalFilename())){
			return ResultVOUtil.error(ResultEnum.IMAGE_TYPE_ERROR.getCode(),ResultEnum.IMAGE_TYPE_ERROR.getMessage());
		}

		OSSClient ossClient= OSSUtil.getOSSClient();
		String path = OSSUtil.uploadObject2OSS(ossClient,file, OSSConfig.folder);
		return ResultVOUtil.success(path);
	}

	public boolean fileType(String fileName) {

		// 获取文件后缀名并转化为写，用于后续比较
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
		// 创建图片类型数组
		String img[] = { "jpg", "jpeg", "png"};
		for (int i = 0; i < img.length; i++) {
			if (img[i].equals(fileType)) {
				return true;
			}
		}
		return false;
	}

}
