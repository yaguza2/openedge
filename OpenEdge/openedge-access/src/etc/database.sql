#
# Table structure for table 'oeaccess_role'
#

CREATE TABLE `oeaccess_role` (
  `name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`name`),
  UNIQUE KEY `name` (`name`),
  KEY `name_2` (`name`)
) TYPE=MyISAM;


#
# Table structure for table 'oeaccess_user'
#

CREATE TABLE `oeaccess_user` (
  `name` varchar(30) NOT NULL default '',
  `password` varchar(250) NOT NULL default '',
  PRIMARY KEY  (`name`),
  UNIQUE KEY `name` (`name`),
  KEY `name2` (`name`)
) TYPE=MyISAM;


#
# Table structure for table 'oeaccess_user_attribs'
#

CREATE TABLE `oeaccess_user_attribs` (
  `user_name` varchar(30) NOT NULL default '',
  `attrib_key` varchar(250) NOT NULL default '',
  `attrib_value` blob,
  PRIMARY KEY  (`user_name`,`attrib_key`),
  KEY `id_2` (`user_name`),
  KEY `user_key` (`user_name`,`attrib_key`)
) TYPE=MyISAM;


#
# Table structure for table 'oeaccess_user_role'
#

CREATE TABLE `oeaccess_user_role` (
  `role_name` varchar(30) NOT NULL default '',
  `user_name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`role_name`,`user_name`),
  UNIQUE KEY `user_role` (`user_name`,`role_name`)
) TYPE=MyISAM;
