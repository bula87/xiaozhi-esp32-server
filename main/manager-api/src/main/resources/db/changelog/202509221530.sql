-- Add SM2 National Cryptographic Algorithm Key Parameters
-- Used for server-side SM2 encryption and decryption functionality

-- Add SM2 key parameters
INSERT INTO `sys_params` (id, param_code, param_value, value_type, param_type, remark) VALUES 
(120, 'server.public_key', '', 'string', 1, 'Server SM2 Public Key'),
(121, 'server.private_key', '', 'string', 1, 'Server SM2 Private Key');
 