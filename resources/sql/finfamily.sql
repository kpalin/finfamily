
-- define here the users schema

--create schema kalle;
-- set search_path to kalle;
--insert into testi (id,nimi) values (33,'Äänis');

drop view if exists father;
drop view if exists mother;
drop view if exists spouse;
drop view if exists parent;
drop view if exists wife;
drop view if exists husband;
drop view if exists child;

drop view if exists father_all;
drop view if exists mother_all;
drop view if exists spouse_all;
drop view if exists parent_all;
drop view if exists wife_all;
drop view if exists husband_all;
drop view if exists child_all;

drop view if exists fullTextView;

drop view if exists unitNotice_fi;
drop view if exists unitNotice_sv;
drop view if exists unitNotice_en;
drop view if exists unitNotice_de;

drop view if exists svNotice;
drop view if exists fiNotice;
drop view if exists enNotice;
drop view if exists deNotice;




drop index if exists SourceGroupIdx;

drop sequence if exists UnitSeq ;
drop sequence if exists UnitNoticeSeq;
drop sequence if exists RelationSeq;
drop sequence if exists RelationNoticeSeq;
drop sequence if exists ViewSeq;
drop sequence if exists GroupSeq;

drop table if exists UnitLanguage;
drop table if exists RelationLanguage;
drop table if exists UnitNotice;
drop table if exists Relation;
drop table if exists RelationNotice;
drop table if exists Unit;
drop table if exists Conversions;
drop table if exists PlaceFormat;
drop table if exists FarmFormat;
drop table if exists SukuVariables;
drop table if exists Views;
drop table if exists ViewUnits;
drop table if exists Types;
drop table if exists Texts;
drop table if exists QuickList;
drop table if exists PlaceOtherNames;
drop table if exists PlaceLocations;
drop table if exists Groups;
drop table if exists Farm;

drop table if exists SukuSettings;


--
-- lets do a vacuum of old tables
--

vacuum;




create sequence UnitSeq;
create sequence UnitNoticeSeq;
create sequence RelationSeq;
create sequence RelationNoticeSeq;
create sequence ViewSeq;
create sequence GroupSeq;

create table SukuVariables (
owner_name varchar,
owner_info varchar,
owner_address varchar,
owner_postalcode varchar,
owner_postoffice varchar,
owner_state varchar,
owner_country varchar,
owner_email varchar,
user_id varchar primary key default 'suku' ,    -- userid. network user only 
user_pwd varchar,    -- md5 encrypted password
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()    --  timestamp created  
);





create table Unit (
PID integer primary key,   -- Unit/Person Id for Unit/Person
tag varchar,               -- Tag. GEDCOM like INDI for individual
Privacy  char,             -- Privacy indicator, null = Public  
GroupId varchar,           -- Id of group for this unit  
Sex char not null,         -- Male = M, Female = F, Unknown = U  
SID integer,               -- temp storage for sourcieid for now
SourceText varchar ,       -- Source as text
PrivateText varchar,       -- Private researcher information  
UserREFN varchar,          -- Reference number to the unit. GEDCOM REFN tag  
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- timestamp created  
);


--
-- A Unit (Person) is connected to another person using two relations
-- the relations connect to each other using a unique RID (RelationId)
-- Relation has 0-* RelationNotices describing events of that relation
-- Such as MARR and DIV information
--
-- Following tags are used
-- STANDARD RELATIONS
-- FATH   = Father
-- MOTH = Mother
-- CHIL = Child
-- HUSB = Husband
-- WIFE = Wife
--
-- HISKI ADDITIONAL RELATIONS
-- 
-- GODC = Godchild  (kummilapsi) = Kastettu
-- GODP = Godparent (kummivanhempi)
--
-- HAUDATTU = Primary relative eli Haudattu
-- OMAINEN = Secondary relative (omainen haudatun päässä) eli Omainen
--
-- EMIG = Main emigrator (poismuuttaja)
-- IMMI = Main immigrator (sisäänmuuttaja)
-- KANSSA = Secondary traveller (kanssamuuttaja)
--
--
create table Relation (
RID integer not null,                         -- Relation Id
PID integer not null references Unit(PID),    -- Unit/Person Id
-- active boolean not null default true,         -- active when this relation is activated by reasearcher
surety integer not null default 100,          -- surety indicator
tag varchar,                                  -- tag of relation
RelationRow integer,                          -- row of relation at person
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- timestamp created  
) with oids;

create index RelationIndex on Relation (RID);
create index RelationPidIndex on Relation (PID);

--
-- Row number and Language code on notices
-- ====================================
--
-- Same row number indicates variation of  notice with 
-- 1) Specific Language version. It  overrides texts for specific language client
-- (L) indicates field that can have language variants
-- Field RefNames contains names for indexes
-- List of names within notice for index separated by ; Name parts separated by /
-- example: Jacob/Abrahamsson/von/Schoults/III;Vilhelm///Laaksonen
-- Field RefPlaces contains placess for indexes separated by ; Turku;Ii;Espoo

create table UnitNotice (
PNID integer not null primary key,    -- Numeric Id that identifies this Notice, supplied by the system  
PID integer not null references Unit(PID),  -- Id of the Unit for this UnitNotice 
-- active boolean not null default true,  -- active when this notice is activated by reasearcher
surety integer not null default 100,  -- surety indicator
-- MainItem boolean not null default false, -- true for primary name and other primary items
NoticeRow integer not null default 0, -- Row # of the Notice for the unit  
Tag varchar not null,            -- Tag of the Notice, Mostly Level 1 GEDCOM tags 
Privacy char,                    -- Privacy indicator, null = Public  
NoticeType varchar,              -- Notice type  (L)
Description varchar,             -- Description or remark  (L)
--FromDatePrefix varchar(8),       -- Prefix for the date (beginning date if date period)  
DatePrefix varchar(8),       -- Prefix for the date (beginning date if date period)  
FromDate varchar,                -- Date for the event described in this notice  
ToDate varchar,                  -- Date for the event described in this notice  
Place varchar,                   -- Place
Village varchar,                 -- Kyl  NEW
Farm varchar,                    -- Talo  NEW
Croft varchar,                   -- Torppa  NEW
Address varchar,                 -- Address line 1 / Village/Kylä
PostOffice varchar,              -- Place of the event, Postoffice, City  
PostalCode varchar,              -- Postal Code  
State varchar,
Country varchar,                 -- Country  
--Location point,                  -- Geographical location of place
Email varchar,                   -- Email-address or web-page of person  
NoteText varchar,                -- Note textfield  (L)
MediaFilename varchar,           -- Filename of the multimedia file  
MediaData bytea,                 -- Container of image
MediaTitle varchar,              -- text describing the multimedia file (L)  
MediaWidth integer,              -- media width in pixels
MediaHeight integer,             -- media height in pixels
Prefix varchar,                  -- Prefix of the surname  
Surname varchar,                 -- Surname  
Givenname varchar,               --  Givenname
Patronym varchar,                -- Patronyymi  NEW
PostFix varchar,                 --  Name Postfix  
RefNames varchar[],                -- List of names within notice for index
RefPlaces varchar[],               -- List of places within notice for index
SID integer,               -- temp storage for sourcieid for now
SourceText varchar ,       -- Source as text
PrivateText varchar,             --  Private researcher information  
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()    --  timestamp created  
);

create index UnitNoticePidIndex on UnitNotice (PID);
create index UnitGivennameIndex on UnitNotice (Givenname);
create index UnitSurnameIndex on UnitNotice (Surname);
create index UnitPatronymIndex on UnitNotice (Patronym);

create table UnitLanguage (
PNID integer not null references UnitNotice(PNID), -- Numeric Id that identifies this Notice, supplied by the system  
PID integer not null references Unit(PID),  -- Id of the Unit for this UnitNotice 
Tag varchar,            -- tag of the Notice, Mainly for debugging purposes
LangCode varchar not null,                -- Language code. ISO 639-1 Code 2 char code
NoticeType varchar,              -- Notice type  (L)
Description varchar,             -- Description or remark  (L)
Place varchar,                   -- Place
NoteText varchar,                -- Note textfield  (L)
MediaTitle varchar,              -- text describing the multimedia file (L)  
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()    --  timestamp created  
) with oids;



create unique index UnitLanguageIdx on UnitLanguage (PNID,LangCode);
create index UnitLanguagePidIdx on UnitLanguage (PID);


--
-- Tag defines the type of notice. Following tagsare used:
--
--  in HUSB-WIFE relation 
-- =======================
-- MARR = Marriage
-- DIV = Divorce
--
-- in CHIL - FATH / CHIL-MOTH relation
-- =====================================
-- ADOP = Adopted
--
create table RelationNotice (
RNID integer primary key,         -- Numeric Id that identifies this RelationNotice, supplied by the system  
RID integer not null ,            -- Relation Id of the relation  
-- active boolean not null default true,
surety integer not null default 100,  -- surety indicator
NoticeRow integer not null,      -- Row # of the Relation. See description for UnitNotice
Tag varchar not null,            -- Tag of the relation ( MARR,DIV,ADOP...) 
--LangCode varchar,                -- Language code. ISO 639-1 Code 2 char code
Description varchar,             -- Description of relation (for parent/child relations not null means adopted)  
RelationType varchar,            -- Type such as type of marriage  
DatePrefix varchar,              -- Prefix for the date (beginning date if date period)  
FromDate varchar,                -- Date for the (beginning) event described in this notice  
ToDate varchar,                  -- End Date for the beginning even  
Place varchar,                   -- Place for the (beginning of the) event  
NoteText varchar,                -- Note textfield  
SID integer,                     -- temp storage for sourcieid for now
SourceText varchar ,             -- Source as text
PrivateText varchar,             -- Private researcher information  
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- Date/time when created  
) ;

create table RelationLanguage (
RNID integer not null,         -- Numeric Id that identifies this RelationNotice,
RID integer not null,                  --  Relation Id of the relation  
LangCode varchar,                -- Language code. ISO 639-1 Code 2 char code
RelationType varchar,            -- Type such as type of marriage  
Description varchar,             -- Description of relation (for parent/child relations not null means adopted)  
Place varchar,                   --  Place for the (beginning of the) event  
NoteText varchar,                -- Note textfield  
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- Date/time when created  
) with oids;

create unique index RelationLanguageIdx on RelationLanguage (RNID,LangCode);
create index RelationLanguageRidIdx on RelationLanguage (RID);

create table Groups (
GroupId integer primary key,
Name varchar not null,
Description varchar,
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- Date/time when created  
);


create table Conversions (
ConversionId serial primary key,    -- serial primary key
FromText varchar,       -- text in data
LangCode varchar,       -- language_code
Rule varchar,           -- rule
ToText varchar          -- text in report
) ;

create table PlaceFormat (
Name varchar not null,                      -- name to convert  ex Helsinki
LangCode varchar not null default 'fi',     -- Language code. ISO 639-1 Code 2 char code  usually fi
inForm varchar,                             -- format of name when in name  (ex; Helsingiss�)
toForm varchar,                             -- format f name when to name  (ex; Helsinkiin)
fromForm varchar,                           -- format f name when from name  (ex; Helsingist�)
Modified timestamp,                         -- timestamp modified
CreateDate timestamp not null default now() -- Date/time when created  
);

--
-- Settinggroups for reports
--

create table SukuSettings (
SettingType varchar not null,
SettingIndex integer not null,
SettingName varchar not null,
SettingValue varchar
) with oids;


--
-- Types table replaces types.txt file in Suku 2004
--

create table Types (
TypeID serial primary key,                    -- primary key not much used
TagType varchar not null,		              -- type of tag (Notice/Relation/Text)
Tag varchar not null,                         -- tag of type
Rule varchar,
LangCode varchar not null,                    -- language code for type
Name varchar,                        -- value as displayed in program
ReportName varchar, 						  -- value in reports if not =O name
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- timestamp created  
);

create table Texts (
TypeID serial primary key,                    -- primary key not much used
TagType varchar not null,		              -- type of tag (Notice/Relation/Text)
Tag varchar not null,                         -- tag of type
LangCode varchar not null,                    -- language code for type
Name varchar,                        -- value as displayed in program
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- timestamp created  
);

create table QuickList (
QuickId integer primary key,
FieldName varchar not null,
LangCode varchar not null,                    -- language code for type
UsedValue varchar not null,                   -- Value used in field
LastUsed  timestamp not null default now(),   -- timestamp last used
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- timestamp created  
);


create table PlaceLocations (             -- Contains locations of known places 
PlaceName varchar primary key,
Location point
);

create table PlaceOtherNames (
OtherName varchar primary key,
PlaceName varchar references PlaceLocations(PlaceName) 
);

-- insert into PlaceLocations (PlaceName,Location) values ('BROMARV','(60,23)');
--select placename,location[0],location[1] from placelocations 
--insert into PlaceOtherNames (OtherName,PlaceName) values ('BROMARF','BROMARV')

create table views (
VID integer not null primary key,
name varchar,            -- name of view
Modified timestamp,                           -- timestamp modified
CreateDate timestamp not null default now()   -- Date/time when created  
);

create table viewunits (
PID integer not null,     -- id of the unit/person
VID integer not null,  -- id of the view
value integer             -- value given to this e.g. conversion value
);


create view father as
select p1.pid as aid,p2.pid as bid,g1.rid ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('FATH'))
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('CHIL'))
where p1.pid <> p2.pid;

create view mother as
select p1.pid as aid,p2.pid as bid,g1.RID ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('MOTH'))
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('CHIL'))
where p1.pid <> p2.pid;

create view parent as
select p1.pid as aid,p2.pid as bid,g1.RID  ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('FATH','MOTH'))
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('CHIL'))
where p1.pid <> p2.pid;


create view child as
select p1.pid as aid,p2.pid as bid,g1.RID ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('CHIL'))
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('FATH','MOTH'))
where p1.pid <> p2.pid;


create view spouse as
select p1.pid as aid,p2.pid as bid,g1.RID  ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag in ('HUSB','WIFE'))
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag  in ('HUSB','WIFE'))
where p1.pid <> p2.pid;

create view wife as
select p1.pid as aid,p2.pid as bid,g1.RID  ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag = 'WIFE')
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag = 'HUSB')
where p1.pid <> p2.pid;

create view husband as
select p1.pid as aid,p2.pid as bid,g1.RID ,g1.tag,g1.RelationRow
from (((Unit as p1 inner join Relation as g1 
on p1.pid = g1.pid and g1.tag = 'HUSB')
inner join Relation as g2 on g1.RID = g2.RID and g1.surety >=80 and g2.surety >= 80)
inner join Unit as p2 on g2.pid = p2.pid and g2.tag = 'WIFE')
where p1.pid <> p2.pid;
 
-- added 18.4.2010 views for relatives with all sureties
-- at least used in family tree 


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

--
-- fullTextView is used for search with "fill text"
--

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