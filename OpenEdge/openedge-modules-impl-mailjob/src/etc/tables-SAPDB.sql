DROP TABLE email_queue
--
CREATE TABLE email_queue (
  id fixed(19,0) NOT NULL,
  send_to varchar(254) NOT NULL default '',
  message long varchar NOT NULL,
  created fixed(19,0) NOT NULL default '0',
  sender varchar(254) NOT NULL default '',
  subject varchar(254) NOT NULL default '',
  content_type varchar(20) NOT NULL default 'text/plain',
  status varchar(10) NOT NULL default 'new',
  status_detail long varchar,
  attachments long varchar,
  PRIMARY KEY  (id)
)
