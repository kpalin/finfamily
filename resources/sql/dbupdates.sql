
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