
--
-- required in version 454 to use older database to manage relation languages
--

ALTER TABLE relationlanguage ADD COLUMN rid integer;

insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',1,'notice','BIRT');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',2,'notice','CHR');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',3,'notice','OCCU');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',4,'notice','EDUC');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',5,'notice','NOTE');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',6,'notice','PHOT');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',7,'notice','PHOTO');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',8,'notice','IMMI');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',9,'notice','EMIG');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',10,'notice','RESI');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',11,'notice','EVEN');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',12,'notice','DEAT');
insert into sukusettings (settingtype,settingindex,settingname,settingvalue) values ('order',13,'notice','BURI');