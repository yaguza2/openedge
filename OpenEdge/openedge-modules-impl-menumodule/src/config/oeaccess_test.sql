#
# Table structure for table 'oeaccess_group'
#

CREATE TABLE oeaccess_group (
  name varchar(100) NOT NULL default '',
  description varchar(254) default NULL,
  PRIMARY KEY  (name),
  UNIQUE KEY name (name),
  KEY name_2 (name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_role'
#

CREATE TABLE oeaccess_role (
  name varchar(100) NOT NULL default '',
  description varchar(254) default NULL,
  PRIMARY KEY  (name),
  UNIQUE KEY name (name),
  KEY name_2 (name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user'
#

CREATE TABLE oeaccess_user (
  id bigint(20) NOT NULL auto_increment,
  name varchar(100) NOT NULL default '',
  password varchar(100) NOT NULL default '',
  email varchar(254) default NULL,
  first_name varchar(50) default NULL,
  last_name varchar(50) default NULL,
  mobile varchar(30) default NULL,
  telephone varchar(30) default NULL,
  PRIMARY KEY  (id),
  UNIQUE KEY id (id,name),
  KEY id_2 (id,name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user_group'
#

CREATE TABLE oeaccess_user_group (
  user_id bigint(20) NOT NULL default '0',
  group_name char(100) NOT NULL default '',
  PRIMARY KEY  (user_id,group_name),
  UNIQUE KEY u (group_name,user_id),
  KEY user_id (user_id,group_name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user_role'
#

CREATE TABLE oeaccess_user_role (
  user_id bigint(20) NOT NULL default '0',
  role_name varchar(100) NOT NULL default '',
  PRIMARY KEY  (user_id,role_name),
  UNIQUE KEY u (role_name,user_id),
  KEY user_id (user_id,role_name)
) TYPE=MyISAM;

