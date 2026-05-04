-- Fix tenant filtering for existing BPM extension tables.
-- Run this once if the tables were created before tenant_id was added.
-- The current local tenant in the error log is 1. Change 1 below if your data belongs to another tenant.

ALTER TABLE `bpm_process_instance_version`
  ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号' AFTER `deleted`;
UPDATE `bpm_process_instance_version` SET `tenant_id` = 1 WHERE `tenant_id` = 0;

ALTER TABLE `bpm_reject_history`
  ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号' AFTER `deleted`;
UPDATE `bpm_reject_history` SET `tenant_id` = 1 WHERE `tenant_id` = 0;

ALTER TABLE `bpm_parent_child_process_link`
  ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号' AFTER `deleted`;
UPDATE `bpm_parent_child_process_link` SET `tenant_id` = 1 WHERE `tenant_id` = 0;

ALTER TABLE `bpm_frozen_task`
  ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号' AFTER `deleted`;
UPDATE `bpm_frozen_task` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
