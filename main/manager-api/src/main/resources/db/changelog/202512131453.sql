-- Delete server module token authentication enable parameter
delete from `sys_params` where param_code = 'server.auth.enabled';

-- Add server module token authentication enable parameter
INSERT INTO `sys_params` (id, param_code, param_value, value_type, param_type, remark) VALUES 
(122, 'server.auth.enabled', 'true', 'boolean', 1, 'server module token authentication enable');
 