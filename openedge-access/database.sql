# MySQL-Front Dump 2.2
#
# Host: localhost   Database: openedge_website
#--------------------------------------------------------
# Server version 4.0.12-max-nt


#
# Table structure for table 'oeaccess_group'
#

CREATE TABLE `oeaccess_group` (
  `name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`name`),
  UNIQUE KEY `name` (`name`),
  KEY `name_2` (`name`)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_group_permission'
#

CREATE TABLE `oeaccess_group_permission` (
  `resource_name` varchar(250) NOT NULL default '',
  `group_name` varchar(30) NOT NULL default '',
  `permission` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`resource_name`,`group_name`),
  KEY `resource_name` (`resource_name`,`group_name`)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_resource'
#

CREATE TABLE `oeaccess_resource` (
  `name` varchar(250) NOT NULL default '',
  `permission` varchar(30) NOT NULL default '',
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
# Table structure for table 'oeaccess_user_group'
#

CREATE TABLE `oeaccess_user_group` (
  `group_name` varchar(30) NOT NULL default '',
  `user_name` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`group_name`,`user_name`),
  UNIQUE KEY `user_group` (`user_name`,`group_name`)
) TYPE=MyISAM;



#
# Table structure for table 'oeaccess_user_permission'
#

CREATE TABLE `oeaccess_user_permission` (
  `user_name` varchar(30) NOT NULL default '0',
  `resource_name` varchar(250) NOT NULL default '',
  `permission` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`user_name`,`resource_name`),
  KEY `user_id` (`user_name`,`resource_name`)
) TYPE=MyISAM;

