/*
NAVICAT MYSQL DATA TRANSFER

SOURCE SERVER         : 182.92.157.1
SOURCE SERVER VERSION : 50165
SOURCE HOST           : 182.92.157.1:3306
SOURCE DATABASE       : EBAYCHAIN_DB

TARGET SERVER TYPE    : MYSQL
TARGET SERVER VERSION : 50165
FILE ENCODING         : 65001

DATE: 2017-10-21 16:51:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- TABLE STRUCTURE FOR EBY_ACCOUNT
-- ----------------------------
DROP TABLE IF EXISTS `ACCOUNT`;
CREATE TABLE `ACCOUNT` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` VARCHAR(128) DEFAULT NULL COMMENT '会员ID',
  `USER_BALANCE` DECIMAL(65,0) DEFAULT NULL COMMENT '会员余额',
  `USER_DRAWABLE` DECIMAL(65,0) DEFAULT NULL COMMENT '用户提现冻结金额',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='会员账户表';

-- ----------------------------
-- TABLE STRUCTURE FOR EBY_ACCOUNT_CHECK
-- ----------------------------
DROP TABLE IF EXISTS `ACCOUNT_CHECK`;
CREATE TABLE `ACCOUNT_CHECK` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `TRADE_NO` VARCHAR(128) DEFAULT NULL COMMENT '交易编号',
  `PLATFORM_STATUS` VARCHAR(128) DEFAULT NULL COMMENT '平台状态',
  `PLATFORM_AMOUNT` VARCHAR(128) DEFAULT NULL COMMENT '平台金额',
  `PLATFORM_TIME` DATETIME DEFAULT NULL COMMENT '平台交易时间',
  `EBAY_STATUS` VARCHAR(128) DEFAULT NULL COMMENT 'EBAY状态',
  `EBAY_AMOUNT` VARCHAR(128) DEFAULT NULL COMMENT 'EBAY金额',
  `EBAY_TIME` DATETIME DEFAULT NULL COMMENT 'EBAY交易时间',
  `CHECK_STATUS` VARCHAR(255) DEFAULT NULL COMMENT '对账状态',
  `HANDLE_MEMO` VARCHAR(128) DEFAULT NULL COMMENT '处理过程',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='对账信息表';

-- ----------------------------
-- TABLE STRUCTURE FOR ACCOUNT_ITEM
-- ----------------------------
DROP TABLE IF EXISTS `ACCOUNT_ITEM`;
CREATE TABLE `ACCOUNT_ITEM` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` VARCHAR(128) DEFAULT NULL COMMENT '用户ID',
  `TRADE_NO` VARCHAR(128) DEFAULT NULL COMMENT '交易编号',
  `TRADE_TYPE` VARCHAR(128) DEFAULT NULL COMMENT '交易类型（0.收入 1.支出 2.提现）',
  `TRADE_STATUS` VARCHAR(128) DEFAULT NULL COMMENT '交易状态（0.失败 1.成功 2.处理中）',
  `TRADE_IN_AMOUNT` VARCHAR(128) DEFAULT NULL COMMENT '收入金额',
  `TRADE_OUT_AMOUNT` VARCHAR(128) DEFAULT NULL COMMENT '支出金额',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='账户明细表';

-- ----------------------------
-- TABLE STRUCTURE FOR ACCOUNT_WITHDRAW
-- ----------------------------
DROP TABLE IF EXISTS `ACCOUNT_WITHDRAW`;
CREATE TABLE `ACCOUNT_WITHDRAW` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` VARCHAR(128) DEFAULT NULL COMMENT '用户ID',
  `TRADE_NO` VARCHAR(128) DEFAULT NULL COMMENT '提现编号',
  `DRAW_AMOUNT` DECIMAL(65,0) DEFAULT NULL COMMENT '提现金额',
  `USER_BALANCE` DECIMAL(65,0) DEFAULT NULL COMMENT '用户余额',
  `AUDIT_STATUS` VARCHAR(1) DEFAULT NULL COMMENT '状态（0.待审核 1.已通过 2.不通过 3.暂不处理）',
  `AUDIT_MEMO` VARCHAR(500) DEFAULT NULL COMMENT '审核意见',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='提现管理表';

-- ----------------------------
-- TABLE STRUCTURE FOR DISTRIBUTION
-- ----------------------------
DROP TABLE IF EXISTS `DISTRIBUTION`;
CREATE TABLE `DISTRIBUTION` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `ORDER_NO` VARCHAR(128) DEFAULT NULL COMMENT '订单编号',
  `PRODUCT_NAME` VARCHAR(128) DEFAULT NULL COMMENT '商品名称',
  `PRODUCT_TOTAL_PRICE` DECIMAL(65,0) DEFAULT NULL COMMENT '商品总价',
  `BUY_USER_NAME` VARCHAR(128) DEFAULT NULL COMMENT '购买用户',
  `FIRST_DIST_NAME` VARCHAR(128) DEFAULT NULL COMMENT '一级分销姓名',
  `FIRST_DIST_RATIO` VARCHAR(128) DEFAULT NULL COMMENT '一级分销比例',
  `FIRST_COMMISSION` DECIMAL(65,0) DEFAULT NULL COMMENT '一级提成金额',
  `FIRST_WX_NAME` VARCHAR(128) DEFAULT NULL COMMENT '一级微信号',
  `SECOND_DIST_NAME` VARCHAR(255) DEFAULT NULL COMMENT '二级分销姓名',
  `SECOND_DIST_RATIO` VARCHAR(255) DEFAULT NULL COMMENT '二级分销比例',
  `SECOND_COMMISSION` VARCHAR(255) DEFAULT NULL COMMENT '二级提成金额',
  `TOTAL_COMMISSION` DECIMAL(65,0) DEFAULT NULL COMMENT '总提成(元)',
  `SECOND_WX_NAME` VARCHAR(255) DEFAULT NULL COMMENT '二级微信号',
  `AUDIT_VERDICT` VARCHAR(128) DEFAULT NULL COMMENT '审核结论',
  `AUDIT_OPINION` VARCHAR(128) DEFAULT NULL COMMENT '审核意见',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者(经办人)',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='分销结算表';

-- ----------------------------
-- TABLE STRUCTURE FOR DISTRIBUTION_RC
-- ----------------------------
DROP TABLE IF EXISTS `DISTRIBUTION_RC`;
CREATE TABLE `DISTRIBUTION_RC` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `PRODUCT_NO` VARCHAR(128) DEFAULT NULL COMMENT '订单编号',
  `PRODUCT_NAME` VARCHAR(128) DEFAULT NULL COMMENT '商品名称',
  `FROM_USER_ID` VARCHAR(128) DEFAULT NULL COMMENT '分销者USERID',
  `FROM_USER_NAME` VARCHAR(128) DEFAULT NULL COMMENT '分销者姓名',
  `USER_ID` VARCHAR(128) DEFAULT NULL COMMENT '被分销者USERID',
  `USER_NAME` VARCHAR(255) DEFAULT NULL COMMENT '被分销者姓名',
  `USER_WX_OPENID` DECIMAL(65,0) DEFAULT NULL COMMENT '被分销者微信ID',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者(经办人)',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='分销轨迹表';

-- ----------------------------
-- TABLE STRUCTURE FOR PRODUCT
-- ----------------------------
DROP TABLE IF EXISTS `PRODUCT_INFO`;
CREATE TABLE `PRODUCT` (
  `ID` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `PRODUCT_NO` VARCHAR(255) DEFAULT NULL COMMENT '商品编号',
  `PRODUCT_NANE` VARCHAR(255) DEFAULT NULL COMMENT '商品名称',
  `PRODUCT_PRICE` DECIMAL(10,0) DEFAULT NULL COMMENT '单价',
  `PRODUCT_TYPE` VARCHAR(255) DEFAULT NULL COMMENT '商品分类',
  `PRODUCT_PIC` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
  `PRODUCT_ICON` VARCHAR(255) DEFAULT NULL COMMENT '商品小图',
  `PRODUCT_STATUS` VARCHAR(255) DEFAULT NULL COMMENT '商品状态,0正常1下架',
  `category_type` int not null comment '类目编号',
  `AUDIT_STATUS` VARCHAR(255) DEFAULT NULL COMMENT '审核状态',
  `EBAY_ITEMID` VARCHAR(255) DEFAULT NULL COMMENT 'EBAY ITEMID',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间（翻译时间)',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者(翻译人员)',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  `AUDITED` DATETIME DEFAULT NULL COMMENT '审核时间',
  `AUDITEDBY` VARCHAR(255) DEFAULT NULL COMMENT '审核人',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='商品信息表';

-- ----------------------------
-- TABLE STRUCTURE FOR PRODUCT_ATTR
-- ----------------------------
DROP TABLE IF EXISTS `PRODUCT_ATTR`;
CREATE TABLE `PRODUCT_ATTR` (
  `ID` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `PRODUCT_ID` VARCHAR(255) DEFAULT NULL COMMENT '商品编号',
  `ATTR_NAME` VARCHAR(255) DEFAULT NULL COMMENT '属性名称',
  `ATTR_VALUE` DECIMAL(10,0) DEFAULT NULL COMMENT '属性值',
  `ATTR_TYPE` VARCHAR(255) DEFAULT NULL COMMENT '属性类型[单值 0,  多值 1] -颜色 ',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间（翻译时间)',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='商品属性扩展表';

-- ----------------------------
-- TABLE STRUCTURE FOR PRODUCT_MODE
-- ----------------------------
DROP TABLE IF EXISTS `PRODUCT_MODE`;
CREATE TABLE `PRODUCT_MODE` (
  `ID` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `MODE_NAME` VARCHAR(255) DEFAULT NULL COMMENT '数据模型名称',
  `MODE_JSON` VARCHAR(255) DEFAULT NULL COMMENT '数据模型JSON',
  `MODE_TYPE` VARCHAR(255) DEFAULT NULL COMMENT '模型类型[?]',
  `PRODUCT_TYPE` VARCHAR(255) DEFAULT NULL COMMENT '商品分类',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间（翻译时间)',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='商品数据模型表';

-- ----------------------------
-- TABLE STRUCTURE FOR ORDER
-- ----------------------------
DROP TABLE IF EXISTS `ORDER_MASTER`;
CREATE TABLE `ORDER_MASTER` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
  `ORDER_NO` VARCHAR(128) DEFAULT NULL COMMENT '订单编号',
  `BUYER_NAME` VARCHAR(32) NOT NULL COMMENT '买家名字',
  `BUYER_PHONE` VARCHAR(32) NOT NULL COMMENT '买家电话',
  `BUYER_ADDRESS` VARCHAR(128) NOT NULL COMMENT '买家地址',
  `BUYER_OPENID` VARCHAR(64) NOT NULL COMMENT '买家微信OPENID',
  `ORDER_AMOUNT` DECIMAL(8,2) NOT NULL COMMENT '订单总金额',
  `ORDER_STATUS` VARCHAR(1) DEFAULT NULL COMMENT '订单状态（0:待付款S 1:待发货 2:待签收 3:待取货 4:待取消 5:已取消）',
  `PAY_STATUS` TINYINT(3) NOT NULL DEFAULT '0' COMMENT '支付状态, 默认未支付',
  `EBAY_NO` VARCHAR(128) DEFAULT NULL COMMENT 'EBAY订单号',
  `EBAY_STATUS` VARCHAR(1) DEFAULT NULL COMMENT 'EBAY状态（0:待付款 1:已付款 2:卖家已发货 3:海外仓已签收）',
  `LOGISTICS_STATUS` VARCHAR(128) DEFAULT NULL COMMENT '物流状态（0:海外已入库 1:海外已出仓 2:清关中 3:派送中）',
  `CNEE_NAME` VARCHAR(128) DEFAULT NULL COMMENT '收货人姓名',
  `CNEE_PHONE` VARCHAR(128) DEFAULT NULL COMMENT '收货人号码',
  `CNEE_IDCARD` VARCHAR(128) DEFAULT NULL COMMENT '收货人身份证',
  `CNEE_ADDRESS` VARCHAR(128) DEFAULT NULL COMMENT '收货人地址',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织ID',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='订单主表';

-- 订单商品
CREATE TABLE `ORDER_DETAIL` (
    `ID` BIGINT(20) NOT NULL,
    `order_id` varchar(32) not null COMMENT '订单ID',
    `product_id` varchar(32) not null COMMENT '商品ID',
    `product_name` varchar(64) not null comment '商品名称',
    `product_price` decimal(8,2) not null comment '当前价格,单位分',
    `product_quantity` int not null comment '数量',
    `product_icon` varchar(512) comment '小图',
    `create_time` timestamp not null comment '创建时间',
    `update_time` timestamp not null comment '修改时间',
    PRIMARY KEY (`DETAIL_ID`),
    KEY `IDX_ORDER_ID` (`ORDER_ID`),
    FOREIGN KEY(`ORDER_ID`) REFERENCES ORDER_MASTER(`ORDER_ID`)
)ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='订单详情表';

-- ----------------------------
-- TABLE STRUCTURE FOR ORDER_ERROR
-- ----------------------------
DROP TABLE IF EXISTS `ORDER_ERROR`;
CREATE TABLE `ORDER_ERROR` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `ERROR_NO` VARCHAR(128) DEFAULT NULL COMMENT '异常编号',
  `ORDER_NO` VARCHAR(20) DEFAULT NULL,
  `ERROR_TYPE` VARCHAR(128) DEFAULT NULL COMMENT '异常类型',
  `ERROR_STATUS` VARCHAR(128) DEFAULT NULL COMMENT '异常状态（0:待解决 1:已解决 2:未解决 3:挂起）',
  `ERROR_MEMO` VARCHAR(128) DEFAULT NULL COMMENT '异常描述',
  `SLOVE_MEMO` VARCHAR(128) DEFAULT NULL COMMENT '解决说明',
  `HANDERBY` VARCHAR(11) DEFAULT NULL COMMENT '最后处理人',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='订单异常表';

-- ----------------------------
-- TABLE STRUCTURE FOR ORDER_ERROR_RC
-- ----------------------------
DROP TABLE IF EXISTS `ORDER_ERROR_RC`;
CREATE TABLE `ORDER_ERROR_RC` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `ERROR_NO` VARCHAR(128) DEFAULT NULL COMMENT '异常编号',
  `ERROR_TYPE` VARCHAR(128) DEFAULT NULL COMMENT '异常类型',
  `ERROR_STATUS` VARCHAR(128) DEFAULT NULL COMMENT '异常状态（0:待解决 1:已解决 2:未解决 3:挂起）',
  `ERROR_MEMO` VARCHAR(128) DEFAULT NULL COMMENT '异常说明',
  `SLOVE_MEMO` VARCHAR(128) DEFAULT NULL COMMENT '解决说明',
  `HANDER_TYPE` VARCHAR(11) DEFAULT NULL COMMENT '处理方式',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='订单异常跟踪表';

-- ----------------------------
-- TABLE STRUCTURE FOR ORDER_LOGISTICS
-- ----------------------------
DROP TABLE IF EXISTS `ORDER_LOGISTICS`;
CREATE TABLE `ORDER_LOGISTICS` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
  `ORDER_NO` VARCHAR(128) DEFAULT NULL COMMENT '订单编号',
  `LOGISTICS_INFO` VARCHAR(128) DEFAULT NULL COMMENT '物流信息',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织ID',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='订单物流信息表';

-- ----------------------------
-- TABLE STRUCTURE FOR USER
-- ----------------------------
DROP TABLE IF EXISTS `USER`;
CREATE TABLE `USER` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `USER_NAME` VARCHAR(50) DEFAULT NULL COMMENT '会员中文名',
  `USER_PHONE` VARCHAR(11) DEFAULT NULL COMMENT '手机号码',
  `USER_CTYPE` VARCHAR(1) DEFAULT NULL COMMENT '会员类型(1:分销商、2:普通用户)',
  `USER_ADDR` VARCHAR(245) DEFAULT NULL COMMENT '地址',
  `USER_TXPWD` VARCHAR(50) DEFAULT NULL COMMENT '提现密码',
  `USER_WX_NAME` VARCHAR(255) DEFAULT NULL COMMENT '微信号名称',
  `USER_WX_OPENID` VARCHAR(11) DEFAULT NULL COMMENT '微信号(OPENID)',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USER_ID` (`ID`),
  KEY `IDX_UCTYPE` (`USER_CTYPE`) USING BTREE
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='会员信息表';

-- ----------------------------
-- TABLE STRUCTURE FOR USER_CNEE
-- ----------------------------
DROP TABLE IF EXISTS `USER_CNEE`;
CREATE TABLE `USER_CNEE` (
  `ID` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `USER_ID` VARCHAR(50) DEFAULT NULL COMMENT '会员ID',
  `CNEE_NAME` VARCHAR(128) DEFAULT NULL COMMENT '收货人姓名',
  `CNEE_PHONE` VARCHAR(128) DEFAULT NULL COMMENT '收货人号码',
  `CNEE_IDCARD` VARCHAR(128) DEFAULT NULL COMMENT '收货人身份证',
  `CNEE_ADDRESS` VARCHAR(128) DEFAULT NULL COMMENT '收货人地址',
  `ORGANIZATION_ID` BIGINT(20) DEFAULT NULL COMMENT '组织',
  `IS_ACTIVE` CHAR(1) DEFAULT NULL COMMENT '是否有效(Y 有效  N 无效)',
  `IS_DELETE` CHAR(1) DEFAULT NULL COMMENT '是否删除(Y 删除 N 未删除)',
  `CREATED` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPDATED` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CREATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '创建者',
  `UPDATEDBY` VARCHAR(128) DEFAULT NULL COMMENT '修改者',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='会员收货人表';

-- ----------------------------
-- TABLE STRUCTURE FOR WX_MENU
-- ----------------------------
DROP TABLE IF EXISTS `WX_MENU`;
CREATE TABLE `WX_MENU` (
  `WX_MENU_ID` BIGINT(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `WX_MENU_PARENT` INT(11) DEFAULT NULL COMMENT '父菜单',
  `WX_MENU_LEVEL` INT(11) DEFAULT NULL COMMENT '菜单级别',
  `WX_MENU_NAME` VARCHAR(20) DEFAULT NULL COMMENT '菜单名称',
  `WX_MENU_TYPE` VARCHAR(2) DEFAULT NULL COMMENT '菜单类型(1:链接 2:消息)',
  `WX_MENU_SERI_NO` INT(11) DEFAULT NULL COMMENT '菜单顺序',
  `WX_MENU_CONTENT` VARCHAR(255) DEFAULT NULL COMMENT '菜单内容',
  `WX_MENU_FLAG` VARCHAR(2) DEFAULT NULL COMMENT '有效位(0:无效 1:有效)',
  `CRT_USER_ID` VARCHAR(20) DEFAULT NULL COMMENT '创建人',
  `CRT_TIME` DATETIME DEFAULT NULL COMMENT '创建时间',
  `UPT_USER_ID` VARCHAR(20) DEFAULT NULL COMMENT '修改人',
  `UPT_TIME` DATETIME DEFAULT NULL COMMENT '修改时间',
  `SYNC_TIME` DATETIME DEFAULT NULL COMMENT '同步时间',
  PRIMARY KEY (`WX_MENU_ID`),
  KEY `I_MENU` (`WX_MENU_LEVEL`,`WX_MENU_SERI_NO`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='微信菜单表';

-- ----------------------------
-- TABLE STRUCTURE FOR WX_RULE
-- ----------------------------
DROP TABLE IF EXISTS `WX_RULE`;
CREATE TABLE `WX_RULE` (
  `ID` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `RULE_NAME` VARCHAR(255) DEFAULT NULL COMMENT '规则名称',
  `RULE_TYPE` VARCHAR(1) DEFAULT NULL COMMENT '规则类型（1.关键字回复；2.关注回复；3.消息回复）',
  `KEY_WORDS` VARCHAR(255) DEFAULT NULL COMMENT '关键字，不同关键字用中文“，”分割',
  `REPLY_TYPE` VARCHAR(1) DEFAULT NULL COMMENT '回复消息类型，1：文本，2：图片，3：语音，4：视频，5：音乐，6：图文',
  `SOURCE_ID` VARCHAR(255) DEFAULT NULL COMMENT 'YBL_WX_SOURCE表MEDIA_ID',
  `COM_TYPE` VARCHAR(1) DEFAULT NULL COMMENT '匹配类型，1：完全匹配，2：模糊匹配',
  `STATUS` VARCHAR(1) DEFAULT NULL COMMENT '是否有效，1：有效，2：无效',
  `SORT` VARCHAR(10) DEFAULT NULL COMMENT '排序，按照数字从小到大来',
  `MEMO` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `CRT_TIME` DATETIME DEFAULT NULL COMMENT '新增时间',
  `UPT_TIME` DATETIME DEFAULT NULL COMMENT '修改时间',
  `CRT_ID` VARCHAR(10) DEFAULT NULL COMMENT '创建人ID',
  `UPT_ID` VARCHAR(10) DEFAULT NULL COMMENT '修改人ID',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='微信回复表';

-- ----------------------------
-- TABLE STRUCTURE FOR WX_SOURCE
-- ----------------------------
DROP TABLE IF EXISTS `WX_SOURCE`;
CREATE TABLE `WX_SOURCE` (
  `ID` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `TYPE` VARCHAR(1) DEFAULT NULL COMMENT '素材类型（1：图片，2：视频，3：语音，4：图文）',
  `SOURCE_NAME` VARCHAR(255) DEFAULT NULL COMMENT '名称',
  `MEDIA_ID` VARCHAR(255) DEFAULT NULL COMMENT '素材ID',
  `SOURCE_URL` VARCHAR(255) DEFAULT NULL COMMENT '链接',
  `CONTENT` VARCHAR(1000) DEFAULT NULL COMMENT '图文素材JSON',
  PRIMARY KEY (`ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='微信素材表';

-- ----------------------------
-- TABLE STRUCTURE FOR WX_TOKEN
-- ----------------------------
DROP TABLE IF EXISTS `WX_TOKEN`;
CREATE TABLE `WX_TOKEN` (
  `TOKEN_ID` BIGINT(11) NOT NULL AUTO_INCREMENT COMMENT '唯一主键',
  `ACCESS_TOKEN` VARCHAR(255) DEFAULT NULL COMMENT 'TOKEN值',
  `EXPIRES_IN` INT(11) DEFAULT NULL COMMENT '有效时长',
  `BEGIN_TIME` DATETIME DEFAULT NULL COMMENT '有效开始时间',
  `END_TIME` DATETIME DEFAULT NULL COMMENT '失效时间',
  PRIMARY KEY (`TOKEN_ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='微信TOKEN表';

-- ----------------------------
-- TABLE STRUCTURE FOR ZCODE
-- ----------------------------
DROP TABLE IF EXISTS `SYS_ZCODE`;
CREATE TABLE `SYS_ZCODE` (
  `CODE_ID` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `CODE_PID` VARCHAR(255) DEFAULT NULL COMMENT '父节点',
  `CODE_NAME` VARCHAR(255) DEFAULT NULL COMMENT '名称',
  `CODE_EN` VARCHAR(255) DEFAULT NULL COMMENT '英文',
  `CODE_VALUE` VARCHAR(255) DEFAULT NULL COMMENT '值',
  `CODE_SORT` VARCHAR(255) DEFAULT NULL COMMENT '排序',
  `CRT_TIME` VARCHAR(32) DEFAULT NULL COMMENT '创建时间',
  `UPT_TIME` VARCHAR(32) DEFAULT NULL,
  `CODE_ISENABLE` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`CODE_ID`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8 COMMENT='基础数据码表';