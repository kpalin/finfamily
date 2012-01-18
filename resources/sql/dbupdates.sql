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
ALTER TABLE SukuVariables ADD COLUMN owner_state varchar;
ALTER TABLE SukuVariables ADD COLUMN owner_webaddress varchar;



ALTER TABLE Unit ADD COLUMN createdBy varchar;
ALTER TABLE Unit ADD COLUMN modifiedBy varchar;

ALTER TABLE UnitNotice ADD COLUMN createdBy varchar;
ALTER TABLE UnitNotice ADD COLUMN modifiedBy varchar;

ALTER TABLE UnitLanguage ADD COLUMN createdBy varchar;
ALTER TABLE UnitLanguage ADD COLUMN modifiedBy varchar;

ALTER TABLE Relation ADD COLUMN createdBy varchar;
ALTER TABLE Relation ADD COLUMN modifiedBy varchar;

ALTER TABLE RelationNotice ADD COLUMN createdBy varchar;
ALTER TABLE RelationNotice ADD COLUMN modifiedBy varchar;

ALTER TABLE RelationLanguage ADD COLUMN createdBy varchar;
ALTER TABLE RelationLanguage ADD COLUMN modifiedBy varchar;


drop view if exists unitNotice_fi;
drop view if exists unitNotice_sv;
drop view if exists unitNotice_en;
drop view if exists unitNotice_de;

drop table if exists PlaceOtherNames;
drop table if exists PlaceLocations;

create table PlaceLocations (
PlaceName varchar,
CountryCode varchar,
Location_X double,
Location_Y double,
primary key (PlaceName,CountryCode)
);

create table PlaceOtherNames (
OtherName varchar,
CountryCode varchar,
PlaceName varchar,
primary key (OtherName,CountryCode),
FOREIGN KEY (PlaceName, CountryCode) REFERENCES PlaceLocations (PlaceName, CountryCode)
);


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


ALTER TABLE Unit ADD COLUMN createdBy varchar;
ALTER TABLE Unit ADD COLUMN modifiedBy varchar;

ALTER TABLE UnitNotice ADD COLUMN createdBy varchar;
ALTER TABLE UnitNotice ADD COLUMN modifiedBy varchar;

ALTER TABLE UnitLanguage ADD COLUMN createdBy varchar;
ALTER TABLE UnitLanguage ADD COLUMN modifiedBy varchar;

ALTER TABLE Relation ADD COLUMN createdBy varchar;
ALTER TABLE Relation ADD COLUMN modifiedBy varchar;

ALTER TABLE RelationNotice ADD COLUMN createdBy varchar;
ALTER TABLE RelationNotice ADD COLUMN modifiedBy varchar;

ALTER TABLE RelationLanguage ADD COLUMN createdBy varchar;
ALTER TABLE RelationLanguage ADD COLUMN modifiedBy varchar;