#
# Table structure for table 'email_queue'
#

##DROP TABLE IF EXISTS email_queue;
CREATE TABLE email_queue (
  id bigint(20) NOT NULL auto_increment,
  send_to varchar(254) NOT NULL default '',
  message text NOT NULL,
  created bigint(20) NOT NULL default '0',
  sender varchar(254) NOT NULL default '',
  subject varchar(254) NOT NULL default '',
  content_type varchar(20) NOT NULL default 'text/plain',
  status varchar(10) NOT NULL default 'new',
  status_detail text,
  attachments text,
  PRIMARY KEY  (id)
) TYPE=MyISAM;

