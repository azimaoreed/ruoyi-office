CREATE TABLE IF NOT EXISTS `bpm_process_instance_version` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `process_instance_id` varchar(64) NOT NULL COMMENT '流程实例编号',
  `version_no` int NOT NULL COMMENT '版本号',
  `version_status` tinyint NOT NULL COMMENT '版本状态',
  `source_reject_id` bigint DEFAULT NULL COMMENT '来源驳回记录编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bpm_process_instance_version` (`process_instance_id`, `version_no`, `deleted`),
  KEY `idx_bpm_process_instance_version_instance` (`process_instance_id`, `version_status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例版本表';

CREATE TABLE IF NOT EXISTS `bpm_reject_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `process_instance_id` varchar(64) NOT NULL COMMENT '流程实例编号',
  `task_id` varchar(64) NOT NULL COMMENT '发起驳回的任务编号',
  `source_task_definition_key` varchar(255) NOT NULL COMMENT '来源节点定义 key',
  `target_task_definition_key` varchar(255) DEFAULT NULL COMMENT '目标节点定义 key',
  `reject_mode` tinyint NOT NULL COMMENT '驳回模式',
  `reject_reason_type` tinyint DEFAULT NULL COMMENT '驳回原因分类',
  `reject_detail` varchar(500) DEFAULT NULL COMMENT '驳回说明',
  `from_version_no` int DEFAULT NULL COMMENT '驳回前版本号',
  `to_version_no` int DEFAULT NULL COMMENT '驳回后版本号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_bpm_reject_history_instance` (`process_instance_id`, `deleted`),
  KEY `idx_bpm_reject_history_task` (`task_id`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程驳回历史表';
