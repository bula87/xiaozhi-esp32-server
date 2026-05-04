DROP TABLE IF EXISTS ai_agent_voice_print;
create table ai_agent_voice_print (
  id varchar(32) NOT NULL COMMENT 'Voiceprint ID',
  agent_id varchar(32)  NOT NULL COMMENT 'Associated Agent ID',
  source_name varchar(50)  NOT NULL COMMENT 'Source Person Name',
  introduce varchar(200) COMMENT 'Description of the source person',
  create_date DATETIME COMMENT 'Creation Time',
  creator bigint COMMENT 'Creator',
  update_date DATETIME COMMENT 'Modification Time',
  updater bigint COMMENT 'Modifier',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Agent Voiceprint Table'
 