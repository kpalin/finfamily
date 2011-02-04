--en fetch persons without any relatives (orphans)
--fi etsi kaikki henkilöt, joilla ei ole sukulaisia (orvot)
--sv hämta alla personer som inte har några släktskaper 

select pid from unit where pid not in (select distinct pid from relation);

--en fetch persons whose have children without birth date
--fi etsi henkilöt joilla on lapsia ilman syntymäaikaa
--hämta personerna som har barn utan födelsedata 

select a.pid from relation as a inner join relation as b on a.rid=b.rid and a.pid <> b.pid 
left join unitnotice as n on b.pid=n.pid and n.tag='BIRT' 
where a.tag='CHIL' and n.fromdate is null;

--en fecth persons that have more than 1 occupation
--fi etsi henkilöt joilla on useampi kuin yksi ammatti
--sv hämta personer som har mer än ett yrke

select pid from unitnotice where tag='OCCU' group by pid having count(*)>1;

--en fetch persons that have adoption relations
--fi etsi henkilöt joilla on adoptiosuhteita
--sv hämta personer som har adoptions släktskaper

select pid from relation as a inner join relationnotice as n on a.rid=n.rid 
where n.tag = 'ADOP';

--en fetch persons that have been married between 1968 and  1970
--fi etsi henkilöt joiden vihkipäivä on välillä 1968 ja  1970
--sv hämta personer vars vigsel inträffat mellan 1968 och 1970

select r.pid from relationnotice as n inner join relation as r on n.rid=r.rid 
where n.tag='MARR' and n.fromdate between '1968' and '1970';
