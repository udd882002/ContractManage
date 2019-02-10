CREATE TABLE `contract` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `contract_name` varchar(100) DEFAULT NULL COMMENT '合同名',
  `contract_no` varchar(255) DEFAULT NULL COMMENT '合同编号',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `status` varchar(20) DEFAULT NULL COMMENT '状态 draft:草稿 signing:签约中  fail:失败 registered:上链中 complete:完成',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `secret_contract` varchar(10) DEFAULT NULL COMMENT '是否绝密合同 Y:是 N:否',
  `contract_url` text COMMENT '合同地址',
  `contract_start_block_url` text COMMENT '合同发起上链地址',
  `contract_end_block_url` text COMMENT '合同完成上链地址',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `tx_id` text DEFAULT NULL COMMENT 'tx_id',
  `program` text DEFAULT NULL COMMENT 'program',
  `valid_time` datetime DEFAULT NULL COMMENT '有效期',
  `contract_source_url` text COMMENT '合同原地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='contract';

CREATE TABLE `contract_join` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `contract_id` int(10) NOT NULL COMMENT '合同ID',
  `mid` int(10) DEFAULT NULL COMMENT '参于人mid',
  `name` varchar(100) DEFAULT NULL COMMENT '参于人名称',
  `phone` varchar(100) DEFAULT NULL COMMENT '参于人手机号',
  `sign_time` datetime DEFAULT NULL COMMENT '参于人签字时间',
  `file_hash` varchar(100) DEFAULT NULL COMMENT '参于人文件hash',
  `private_keys` text DEFAULT NULL COMMENT '参于人密文',
  `status` varchar(20) DEFAULT NULL COMMENT '状态 draft:草稿 waitmine:待我签 waitother:待其它人签 refuse:拒签 refuseother:被拒签 complete:完成',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `role` varchar(30) NOT NULL COMMENT '角色 initiator:发起者 join:参与者',
  `sort` int(10) DEFAULT NULL COMMENT '签约顺序',
  `label_id` int(10) DEFAULT NULL COMMENT '标签ID',
  `is_archive` varchar(20) DEFAULT 'N' COMMENT '是否已归档 N:否 Y:是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='contract_join';

CREATE TABLE `member` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(100) DEFAULT NULL COMMENT '用户名/企业名称',
  `public_keys` text DEFAULT NULL COMMENT '公钥',
  `phone` varchar(100) DEFAULT NULL COMMENT '手机号/管理员手机号',
  `id_card` varchar(100) DEFAULT NULL COMMENT '身份证号/法人身份证号',
  `member_img` text DEFAULT NULL COMMENT '用户照片/营业执照扫描件',
  `id_card_front_img` text DEFAULT NULL COMMENT '身份证正面照片',
  `id_card_back_img` text DEFAULT NULL COMMENT '身份证返面照片',
  `private_keys_file_url` text DEFAULT NULL COMMENT '私钥文件地址',
  `company` varchar(100) DEFAULT NULL COMMENT '公司',
  `position` varchar(100) DEFAULT NULL COMMENT '职位',
  `credit_code` varchar(255) DEFAULT NULL COMMENT '统一社会信用代码',
  `add_time` datetime DEFAULT NULL COMMENT '注册时间',
  `type` varchar(20) NOT NULL COMMENT '类型 personal：个人 company：企业',
  `ca_cert` text DEFAULT NULL COMMENT 'CA认证信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='member';

CREATE TABLE `contact` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mid` int(10) NOT NULL COMMENT '用户MID',
  `contact_mid` int(10) NOT NULL COMMENT '联系人MID',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='contact';

CREATE TABLE `signature` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mid` int(10) NOT NULL COMMENT '用户MID',
  `url` mediumtext DEFAULT NULL COMMENT '签章图片base64',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='signature';

CREATE TABLE `message` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mid` int(10) NOT NULL COMMENT '用户MID',
  `type` varchar(50) DEFAULT NULL COMMENT '消息类型',
  `content` text DEFAULT NULL COMMENT '消息内容',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  `read_flag` varchar(20) DEFAULT NULL COMMENT '已读标识 Y:已读 N:未读',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='message';

CREATE TABLE `label` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `mid` int(10) NOT NULL COMMENT '用户MID',
  `label_name` varchar(100) DEFAULT NULL COMMENT '标签名称',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='label';

