DROP TABLE IF EXISTS sys_user;
DROP TABLE IF EXISTS sys_params;
DROP TABLE IF EXISTS sys_user_token;
DROP TABLE IF EXISTS sys_dict_type;
DROP TABLE IF EXISTS sys_dict_data;

-- System User
CREATE TABLE sys_user (
  id bigint NOT NULL COMMENT 'id',
  username varchar(150) NOT NULL COMMENT 'Username',
  password varchar(255) COMMENT 'Password',
  super_admin tinyint unsigned COMMENT 'Super Admin 0:No 1:Yes',
  status tinyint COMMENT 'Status 0:Disabled 1:Normal',
  create_date datetime COMMENT 'Creation Time',
  updater bigint COMMENT 'Updater ID',
  creator bigint COMMENT 'Creator ID',
  update_date datetime COMMENT 'Update Time',
  primary key (id),
  unique key uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System User';

-- System User Token
CREATE TABLE sys_user_token (
  id bigint NOT NULL COMMENT 'id',
  user_id bigint NOT NULL COMMENT 'User ID',
  token varchar(255) NOT NULL COMMENT 'User Token',
  expire_date datetime COMMENT 'Expiration Time',
  update_date datetime COMMENT 'Update Time',
  create_date datetime COMMENT 'Creation Time',
  PRIMARY KEY (id),
  UNIQUE KEY user_id (user_id),
  UNIQUE KEY token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System User Token';

-- Parameter Management
create table sys_params
(
  id                   bigint NOT NULL COMMENT 'id',
  param_code           varchar(150) COMMENT 'Parameter Code',
  param_value          varchar(4000) COMMENT 'Parameter Value',
  param_type           tinyint unsigned default 1 COMMENT 'Type 0:System Param 1:Non-system Param',
  remark               varchar(500) COMMENT 'Remark',
  creator              bigint COMMENT 'Creator ID',
  create_date          datetime COMMENT 'Creation Time',
  updater              bigint COMMENT 'Updater ID',
  update_date          datetime COMMENT 'Update Time',
  primary key (id),
  unique key uk_param_code (param_code)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='Parameter Management';

-- Dictionary Type
create table sys_dict_type
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type            varchar(150) NOT NULL COMMENT 'Dictionary Type',
    dict_name            varchar(255) NOT NULL COMMENT 'Dictionary Name',
    remark               varchar(500) COMMENT 'Remark',
    sort                 int unsigned COMMENT 'Sort Order',
    creator              bigint COMMENT 'Creator ID',
    create_date          datetime COMMENT 'Creation Time',
    updater              bigint COMMENT 'Updater ID',
    update_date          datetime COMMENT 'Update Time',
    primary key (id),
    UNIQUE KEY(dict_type)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='Dictionary Type';

-- Dictionary Data
create table sys_dict_data
(
    id                   bigint NOT NULL COMMENT 'id',
    dict_type_id         bigint NOT NULL COMMENT 'Dictionary Type ID',
    dict_label           varchar(255) NOT NULL COMMENT 'Dictionary Label',
    dict_value           varchar(255) COMMENT 'Dictionary Value',
    remark               varchar(500) COMMENT 'Remark',
    sort                 int unsigned COMMENT 'Sort Order',
    creator              bigint COMMENT 'Creator ID',
    create_date          datetime COMMENT 'Creation Time',
    updater              bigint COMMENT 'Updater ID',
    update_date          datetime COMMENT 'Update Time',
    primary key (id),
    unique key uk_dict_type_value (dict_type_id, dict_value),
    key idx_sort (sort)
)ENGINE=InnoDB DEFAULT CHARACTER SET utf8mb4 COMMENT='Dictionary Data';