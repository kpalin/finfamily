--
-- improve speed 
--
create index RelationPidIndex on Relation (PID);
create index UnitNoticePidIndex on UnitNotice (PID);

create index UnitGivennameIndex on UnitNotice (Givenname);
create index UnitSurnameIndex on UnitNotice (Surname);
create index UnitPatronymIndex on UnitNotice (Patronym);


--
-- required in version 454 to use older database to manage relation languages
--



ALTER TABLE relationlanguage ADD COLUMN rid integer;

ALTER TABLE unitnotice ADD COLUMN state varchar;

drop view if exists unitNotice_fi;
drop view if exists unitNotice_sv;
drop view if exists unitNotice_en;
drop view if exists unitNotice_de;


create view unitNotice_fi as
select u1.pid,u1.Pnid,u1.surety,u1.privacy,u1.NoticeRow,u1.tag,
coalesce(u2.NoticeType,u1.NoticeType) as NoticeType ,
coalesce(u2.description,u1.description) as description ,
u1.DatePrefix,u1.FromDate,u1.ToDate,    
coalesce(u2.Place,u1.Place) as Place , u1. Village,u1.Farm,u1.Croft,
u1.Address,u1.PostOffice,u1.PostalCode,u1,State,u1.Country, u1.Email,
coalesce(u2.NoteText,u1.NoteText) as NoteText ,  
u1.MediaFilename,u1.MediaData,
coalesce(u2.MediaTitle,u1.MediaTitle) as MediaTitle ,   
u1.MediaWidth,u1.MediaHeight,
u1.Prefix,u1.Surname,u1.Givenname,u1.Patronym,u1.PostFix,
u1.RefNames,u1.RefPlaces,u1.SourceText,u1.PrivateText,u1.modified,u1.CreateDate
from unitNotice as u1 left join unitLanguage as u2 
on u1.pnid = u2.pnid and u2.langcode = 'fi';


create view unitNotice_sv as
select u1.pid,u1.Pnid,u1.surety,u1.privacy,u1.NoticeRow,u1.tag,
coalesce(u2.NoticeType,u1.NoticeType) as NoticeType ,
coalesce(u2.description,u1.description) as description ,
u1.DatePrefix,u1.FromDate,u1.ToDate,    
coalesce(u2.Place,u1.Place) as Place , u1. Village,u1.Farm,u1.Croft,
u1.Address,u1.PostOffice,u1.PostalCode,u1.state,u1.Country, u1.Email,
coalesce(u2.NoteText,u1.NoteText) as NoteText ,  
u1.MediaFilename,u1.MediaData,
coalesce(u2.MediaTitle,u1.MediaTitle) as MediaTitle ,   
u1.MediaWidth,u1.MediaHeight,
u1.Prefix,u1.Surname,u1.Givenname,u1.Patronym,u1.PostFix,
u1.RefNames,u1.RefPlaces,u1.SourceText,u1.PrivateText,u1.modified,u1.CreateDate
from unitNotice as u1 left join unitLanguage as u2 
on u1.pnid = u2.pnid and u2.langcode = 'sv';

create view unitNotice_en as
select u1.pid,u1.Pnid,u1.surety,u1.privacy,u1.NoticeRow,u1.tag,
coalesce(u2.NoticeType,u1.NoticeType) as NoticeType ,
coalesce(u2.description,u1.description) as description ,
u1.DatePrefix,u1.FromDate,u1.ToDate,    
coalesce(u2.Place,u1.Place) as Place , u1. Village,u1.Farm,u1.Croft,
u1.Address,u1.PostOffice,u1.PostalCode,u1.state,u1.Country, u1.Email,
coalesce(u2.NoteText,u1.NoteText) as NoteText ,  
u1.MediaFilename,u1.MediaData,
coalesce(u2.MediaTitle,u1.MediaTitle) as MediaTitle ,   
u1.MediaWidth,u1.MediaHeight,
u1.Prefix,u1.Surname,u1.Givenname,u1.Patronym,u1.PostFix,
u1.RefNames,u1.RefPlaces,u1.SourceText,u1.PrivateText,u1.modified,u1.CreateDate
from unitNotice as u1 left join unitLanguage as u2 
on u1.pnid = u2.pnid and u2.langcode = 'en';

create view unitNotice_de as
select u1.pid,u1.Pnid,u1.surety,u1.privacy,u1.NoticeRow,u1.tag,
coalesce(u2.NoticeType,u1.NoticeType) as NoticeType ,
coalesce(u2.description,u1.description) as description ,
u1.DatePrefix,u1.FromDate,u1.ToDate,    
coalesce(u2.Place,u1.Place) as Place , u1. Village,u1.Farm,u1.Croft,
u1.Address,u1.PostOffice,u1.PostalCode,u1,State,u1.Country, u1.Email,
coalesce(u2.NoteText,u1.NoteText) as NoteText ,  
u1.MediaFilename,u1.MediaData,
coalesce(u2.MediaTitle,u1.MediaTitle) as MediaTitle ,   
u1.MediaWidth,u1.MediaHeight,
u1.Prefix,u1.Surname,u1.Givenname,u1.Patronym,u1.PostFix,
u1.RefNames,u1.RefPlaces,u1.SourceText,u1.PrivateText,u1.modified,u1.CreateDate
from unitNotice as u1 left join unitLanguage as u2 
on u1.pnid = u2.pnid and u2.langcode = 'de';

create view fullTextView as
select pid,coalesce(NoticeType,'') || ' ' || coalesce(Description,'') || ' ' ||coalesce(Place,'') || ' ' ||
coalesce(Village,'') || ' ' ||coalesce(Farm,'') || ' ' ||coalesce(Croft,'') || ' ' ||coalesce(Address,'') || ' ' ||
coalesce(PostOffice,'') || ' ' ||coalesce(PostalCode,'') || ' ' ||coalesce(State,'') || ' ' ||coalesce(Country,'') || ' ' ||
coalesce(NoteText,'') || ' ' ||coalesce(MediaFilename,'') || ' ' ||coalesce(MediaTitle,'') || ' ' ||
coalesce(Prefix,'') || ' ' ||coalesce(Surname,'') || ' ' ||coalesce(Givenname,'') || ' ' ||coalesce(Patronym,'') || ' ' ||
coalesce(PostFix,'') || ' ' ||coalesce(SourceText,'') || ' ' ||coalesce(PrivateText,'') as fulltext
from unitnotice 
union 
select pid,coalesce(NoticeType,'') || ' ' || coalesce(Description,'') || ' ' ||coalesce(Place,'') || ' ' ||
coalesce(NoteText,'') || ' '  ||coalesce(MediaTitle,'') as fulltext
from unitlanguage
union 
select u.pid,coalesce(RelationType,'') || ' ' || coalesce(Description,'') || ' ' ||coalesce(Place,'') || ' ' ||
coalesce(NoteText,'') || ' ' ||coalesce(n.SourceText,'') || ' ' ||coalesce(n.PrivateText,'') as fulltext
from unit as u inner join relation as r on u.pid=r.pid inner join relationnotice as n on r.rid=n.rid
union 
select u.pid,coalesce(RelationType,'') || ' ' || coalesce(Description,'') || ' ' ||coalesce(Place,'') || ' ' ||
coalesce(NoteText,'')  as fulltext
from unit as u inner join relation as r on u.pid=r.pid inner join relationlanguage as n on r.rid=n.rid;
create view father as
select p1.pid as aid,p2.pid as bid,g1.rid ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('FATH'))
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('CHIL'))
where p1.pid <> p2.pid;


create view father_all as
select p1.pid as aid,p2.pid as bid,g1.rid ,g1.tag,g1.RelationRow,g1.surety
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('FATH'))
inner join Relation as g2 on g1.RID = g2.RID )
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('CHIL'))
where p1.pid <> p2.pid;

create view mother_all as
select p1.pid as aid,p2.pid as bid,g1.RID ,g1.tag,g1.RelationRow,g1.surety
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('MOTH'))
inner join Relation as g2 on g1.RID = g2.RID )
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('CHIL'))
where p1.pid <> p2.pid;

create view parent_all as
select p1.pid as aid,p2.pid as bid,g1.RID  ,g1.tag,g1.RelationRow,g1.surety
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('FATH','MOTH'))
inner join Relation as g2 on g1.RID = g2.RID )
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('CHIL'))
where p1.pid <> p2.pid;


create view child_all as
select p1.pid as aid,p2.pid as bid,g1.RID ,g1.tag,g1.RelationRow,g1.surety
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('CHIL'))
inner join Relation as g2 on g1.RID = g2.RID )
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('FATH','MOTH'))
where p1.pid <> p2.pid;


create view spouse_all as
select p1.pid as aid,p2.pid as bid,g1.RID  ,g1.tag,g1.RelationRow,g1.surety
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('HUSB','WIFE'))
inner join Relation as g2 on g1.RID = g2.RID )
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('HUSB','WIFE'))
where p1.pid <> p2.pid;

create view wife_all as
select p1.pid as aid,p2.pid as bid,g1.RID  ,g1.tag,g1.RelationRow,g1.surety
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag = 'WIFE')
inner join Relation as g2 on g1.RID = g2.RID )
inner join Unit as p2 on g2.pid = p2.pid and g2.tag = 'HUSB')
where p1.pid <> p2.pid;

create view husband_all as
select p1.pid as aid,p2.pid as bid,g1.RID ,g1.tag,g1.RelationRow,g1.surety
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag = 'HUSB')
inner join Relation as g2 on g1.RID = g2.RID )
inner join Unit as p2 on g2.pid = p2.pid and g2.tag = 'WIFE')
where p1.pid <> p2.pid;