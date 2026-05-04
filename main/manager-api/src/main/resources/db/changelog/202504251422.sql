-- Add server.ota, used to configure the OTA address

delete from `sys_params` where id = 100;
delete from `sys_params` where id = 101;

delete from `sys_params` where id = 106;
INSERT INTO `sys_params` (id, param_code, param_value, value_type, param_type, remark) VALUES (106, 'server.websocket', 'null', 'string', 1, 'Websocket addresses, multiple separated by ;');

delete from `sys_params` where id = 107;
INSERT INTO `sys_params` (id, param_code, param_value, value_type, param_type, remark) VALUES (107, 'server.ota', 'null', 'string', 1, 'OTA address');


-- Add firmware information table
CREATE TABLE IF NOT EXISTS `ai_ota` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `firmware_name` varchar(200) DEFAULT NULL COMMENT 'Firmware Name',
  `type` varchar(100) DEFAULT NULL COMMENT 'Firmware Type',
  `version` varchar(100) DEFAULT NULL COMMENT 'Version Number',
  `size` bigint DEFAULT NULL COMMENT 'File Size (bytes)',
  `remark` varchar(1000) DEFAULT NULL COMMENT 'Remark/Description',
  `firmware_path` varchar(500) DEFAULT NULL COMMENT 'Firmware Path',
  `sort` int unsigned DEFAULT '0' COMMENT 'Sort Order',
  `updater` bigint DEFAULT NULL COMMENT 'Updater ID',
  `update_date` datetime DEFAULT NULL COMMENT 'Update Time',
  `creator` bigint DEFAULT NULL COMMENT 'Creator ID',
  `create_date` datetime DEFAULT NULL COMMENT 'Creation Time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Firmware Information Table';

update ai_device set auto_update = 1;