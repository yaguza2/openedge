#
# Table structure for table 'oeaccess_group'
#

DROP TABLE IF EXISTS oeaccess_group;
CREATE TABLE oeaccess_group (
  name varchar(250) NOT NULL default '',
  PRIMARY KEY  (name),
  UNIQUE KEY name (name),
  KEY name_2 (name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_group_permission'
#

DROP TABLE IF EXISTS oeaccess_group_permission;
CREATE TABLE oeaccess_group_permission (
  resource_name varchar(250) NOT NULL default '',
  group_name varchar(250) NOT NULL default '',
  permission int(11) NOT NULL default '0',
  PRIMARY KEY  (resource_name,group_name),
  KEY resource_name (resource_name,group_name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_resource'
#

DROP TABLE IF EXISTS oeaccess_resource;
CREATE TABLE oeaccess_resource (
  name varchar(250) NOT NULL default '',
  permission int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (name),
  UNIQUE KEY name (name),
  KEY name_2 (name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user'
#

DROP TABLE IF EXISTS oeaccess_user;
CREATE TABLE oeaccess_user (
  id int(11) NOT NULL auto_increment,
  name varchar(100) NOT NULL default '',
  PRIMARY KEY  (id),
  UNIQUE KEY id (id),
  KEY id_2 (id)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user_attribs'
#

DROP TABLE IF EXISTS oeaccess_user_attribs;
CREATE TABLE oeaccess_user_attribs (
  id int(11) NOT NULL auto_increment,
  user_id int(11) NOT NULL default '0',
  attrib_key varchar(250) NOT NULL default '',
  attrib_value blob,
  PRIMARY KEY  (id),
  UNIQUE KEY id (id),
  KEY id_2 (id,user_id),
  KEY user_key (user_id,attrib_key)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user_group'
#

DROP TABLE IF EXISTS oeaccess_user_group;
CREATE TABLE oeaccess_user_group (
  id int(11) NOT NULL auto_increment,
  group_name varchar(250) NOT NULL default '',
  user_id int(11) NOT NULL default '0',
  PRIMARY KEY  (id),
  UNIQUE KEY id (id),
  KEY id_2 (id),
  KEY user_group (user_id,group_name)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user_permission'
#

DROP TABLE IF EXISTS oeaccess_user_permission;
CREATE TABLE oeaccess_user_permission (
  user_id int(11) NOT NULL default '0',
  resource_name varchar(250) NOT NULL default '',
  permission int(11) NOT NULL default '0',
  PRIMARY KEY  (user_id,resource_name),
  KEY user_id (user_id,resource_name)
) TYPE=MyISAM;

