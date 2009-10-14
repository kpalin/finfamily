package fi.kaila.suku.util.pojo;

import java.awt.Dimension;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.imageio.ImageIO;

/**
 * Container class for UnitNotice table
 * @author Kalle
 *
 */
public class UnitNotice implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean toBeDeleted=false;
	private boolean toBeUpdated=false;
	int pnid=0;  
	int pid = 0; 	
	int surety =100; //  -- surety indicator
	int noticeRow =0; //-- Row # of the Notice for the unit  
	String tag = null; //  -- Tag of the Notice, Mostly Level 1 GEDCOM tags 
	String privacy = null; //  -- Privacy indicator, null = Public  
	String noticeType = null; //    -- Notice type  (L)
	String description = null ; //      -- Description or remark  (L)
	String datePrefix = null; //    -- Prefix for the date (beginning date if date period)  
	String fromDate =null; //      -- Date for the event described in this notice  
	String toDate = null; //       -- Date for the event described in this notice  
	String place = null; //        -- Place
	String village = null; //varchar,                 -- Kyl  NEW
	String farm = null; //varchar,                    -- Talo  NEW
	String croft = null; // varchar,                   -- Torppa  NEW
	String address = null; // varchar,                 -- Address line 1 / Village/Kylä
	String postOffice = null; //varchar,              -- Place of the event, Postoffice, City  
	String postalCode = null; //varchar,              -- Postal Code  
	String country = null; // varchar,                 -- Country  
	String email = null; //  varchar,                   -- Email-address or web-page of person  
	String noteText = null; // varchar,                -- Note textfield  (L)
	String mediaFilename = null; //  varchar,           -- Filename of the multimedia file  
	byte [] mediaData = null; //bytea,                 -- Container of image
	String mediaTitle = null; // varchar,              -- text describing the multimedia file (L)  
	int mediaWidth = 0; // integer,              -- media width in pixels
	int mediaHeight = 0; // integer,             -- media height in pixels
	String prefix= null; // varchar,                  -- Prefix of the surname  
	String surname = null; //varchar,                 -- Surname  
	String givenname = null; // varchar,               --  Givenname
	String patronym = null; //varchar,                -- Patronyymi  NEW
	String postFix = null; // varchar,                 --  Name Postfix  
	String [] RefNames = null; //varchar,                -- List of names within notice for index
	String [] RefPlaces = null; //varchar,               -- List of places within notice for index
	String sourceText = null; // varchar ,       -- Source as text
	String privateText = null; //varchar,             --  Private researcher information  
	Timestamp modified = null; //timestamp,                           -- timestamp modified
	Timestamp createDate = null; //timestamp not null default now()    --  timestamp created  

	private BufferedImage image = null;
	
	
	private UnitLanguage[] unitlanguages=null;
	
	/**
	create table UnitNotice (
			PNID integer not null primary key,    -- Numeric Id that identifies this Notice, supplied by the system  
			PID integer not null references Unit(PID),  -- Id of the Unit for this UnitNotice 
			-- active boolean not null default true,  -- active when this notice is activated by reasearcher
			surety integer not null default 100,  -- surety indicator
			MainItem boolean not null default false, -- true for primary name and other primary items
			NoticeRow integer not null default 0, -- Row # of the Notice for the unit  
			Tag varchar not null,            -- Tag of the Notice, Mostly Level 1 GEDCOM tags 
			Privacy char,                    -- Privacy indicator, null = Public  
			NoticeType varchar,              -- Notice type  (L)
			Description varchar,             -- Description or remark  (L)
			FromDatePrefix varchar(8),       -- Prefix for the date (beginning date if date period)  
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
			Country varchar,                 -- Country  
			Location point,                  -- Geographical location of place
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
			RefNames varchar,                -- List of names within notice for index
			RefPlaces varchar,               -- List of places within notice for index
			SID integer,               -- temp storage for sourcieid for now
			SourceText varchar ,       -- Source as text
			PrivateText varchar,             --  Private researcher information  
			Modified timestamp,                           -- timestamp modified
			CreateDate timestamp not null default now()    --  timestamp created  
			);
			
			The sr has format select * from unitNotice 
	 * @throws SQLException 
*/
	
	public UnitNotice (ResultSet rs) throws SQLException {
		pnid=rs.getInt("pnid");  
		pid = rs.getInt("pid"); 
		surety = rs.getInt("surety"); 
		noticeRow = rs.getInt("noticerow");  
		tag = rs.getString("tag"); 
		privacy = rs.getString("privacy");  
		noticeType = rs.getString("noticetype");
		description = rs.getString("description");
		datePrefix = rs.getString("dateprefix");   
		fromDate = rs.getString("fromdate");  
		toDate = rs.getString("todate");  
		place = rs.getString("place");
		village = rs.getString("village");
		farm = rs.getString("farm");
		croft = rs.getString("croft"); 
		address = rs.getString("address");
		postOffice = rs.getString("postoffice");  
		postalCode = rs.getString("postalcode");  
		country = rs.getString("country");   
		email = rs.getString("email");   
		noteText = rs.getString("notetext");
		mediaFilename = rs.getString("mediafilename");  
		mediaData = rs.getBytes("mediadata");
		mediaTitle = rs.getString("mediatitle");  
		mediaWidth = rs.getInt("mediawidth");
		mediaHeight = rs.getInt("mediaheight"); 
		prefix= rs.getString("prefix");  
		surname = rs.getString("surname");  
		givenname = rs.getString("givenname"); 
		patronym = rs.getString("patronym");
		postFix = rs.getString("postfix");  
		@SuppressWarnings("unused")
		String [] RefNames = null; //varchar,                -- List of names within notice for index
		@SuppressWarnings("unused")
		String [] RefPlaces = null; //varchar,               -- List of places within notice for index
		sourceText = rs.getString("sourcetext"); 
		privateText = rs.getString("privatetext");   
		modified = rs.getTimestamp("modified");
		createDate = rs.getTimestamp("createDate");  

	}
	public void setToBeDeleted(boolean value) {
		toBeDeleted = value;
	}
	
	public void resetModified(){
		toBeUpdated=false;
	}
	public boolean isToBeDeleted(){
		return toBeDeleted;
	}
//	public void setToBeUpdated(){
//		
//		toBeUpdated=true;
//	}
	public boolean isToBeUpdated(){
		if (toBeUpdated) return true;
		if (unitlanguages == null) return false;
		for (int i = 0; i < unitlanguages.length; i++){
			if (unitlanguages[i].isToBeUpdated() || unitlanguages[i].isToBeDeleted()) return true;
		}
		return false;
	}
	
	public UnitNotice(String tag) {
		this.tag = tag;
	}

	public UnitNotice(String tag,int pid) {
		this.tag = tag;
		this.pid=pid;
	}
	
	public void setLanguages(UnitLanguage[] languages) {
		this.unitlanguages = languages;
	}

	public UnitLanguage[] getLanguages(){
		return this.unitlanguages;
	}
	
	public int getPnid(){
		return pnid;
	}
	


	public int getPid(){
		return pid;
	}
	
	
	public String getTag(){
		return tag;
	}
	public int getSurety(){
		return surety;
	}
	public void setSurety(int surety) {
		if(this.surety != surety){
			this.toBeUpdated=true;
			this.surety=surety;
		}
	}
	
	
	public String getPrivacy(){
		return privacy;
	}
	
	public void setPrivacy(String text){
		if (!nv(this.privacy).equals(nv(text))){
			toBeUpdated=true;
			this.privacy = vn(text);
		}
		
	}
	
	
	
	
	public String getNoticeType(){
		return trim(noticeType);
	}
	public void setNoticeType(String text) {
		if (!nv(this.noticeType).equals(nv(text))){
			toBeUpdated=true;
			this.noticeType = vn(text);
		}
		
	}
	
	
	public String getDescription(){
		return trim(description);
	}
	public void setDescription(String text) {
		if (!nv(this.description).equals(nv(text))){
			toBeUpdated=true;
			this.description = vn(text);
		}
		
	}
	
	
	public String getDatePrefix(){
		return datePrefix;
	}
	public void setDatePrefix(String text){
		if (!nv(this.datePrefix).equals(nv(text))){
			toBeUpdated=true;
			this.datePrefix = vn(text);
		}
		
	}
	
	
	public String getFromDate(){
		return fromDate;
	}
	public void setFromDate(String text){
		if (!nv(this.fromDate).equals(nv(text))){
			toBeUpdated=true;
			this.fromDate = vn(text);
		}
		
	}
//	public void setFromDate(String text){
//		this.fromDate=nv(text);
//	}
	
	public String getToDate(){
		return toDate;
	}
	
	public void setToDate(String text){
		if (!nv(this.toDate).equals(nv(text))){
			toBeUpdated=true;
			this.toDate = vn(text);
		}
		
	}
	public String getPlace(){
		return trim(place);
	}
	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))){
			toBeUpdated=true;
			this.place = vn(text);
		}
		
	}
	
//	public void setPlace(String text) {
//		this.place = nv(text);
//	}
	public String getVillage(){
		return trim(village);
	}
	
	public void setVillage(String text) {
		if (!nv(this.village).equals(nv(text))){
			toBeUpdated=true;
			this.village = vn(text);
		}
		
	}
	

	public String getFarm(){
		return trim(farm);
	}
	public void setFarm(String text){
		if (!nv(this.farm).equals(nv(text))){
			toBeUpdated=true;
			this.farm = vn(text);
		}

	}
	
	public String getCroft(){
		return trim(croft);
	}
	public void setCroft(String text) {
		if (!nv(this.croft).equals(nv(text))){
			toBeUpdated=true;
			this.croft = vn(text);
		}
		
	}
	
	public String getAddress(){
		return trim(address);
	}
	public void setAddress(String text) {
		if (!nv(this.address).equals(nv(text))){
			toBeUpdated=true;
			this.address = vn(text);
		}
	
	}
	public String getPostOffice(){
		return trim(postOffice);
	}
	public void setPostOffice(String text) {
		if (!nv(this.postOffice).equals(nv(text))){
			toBeUpdated=true;
			this.postOffice = vn(text);
		}
		
	}
	public String getPostalCode(){
		return trim(postalCode);
	}
	public void setPostalCode(String text) {
		if (!nv(this.postalCode).equals(nv(text))){
			toBeUpdated=true;
			this.postalCode = vn(text);
		}
		
	}
	public String getCountry(){
		return trim(country);
	}
	public void setCountry(String text) {
		if (!nv(this.country).equals(nv(text))){
			toBeUpdated=true;
			this.country = vn(text);
		}
		
	}
	public String getEmail(){
		return trim(email);
	}
	public void setEmail(String text) {
		if (!nv(this.email).equals(nv(text))){
			toBeUpdated=true;
			this.email = vn(text);
		}
		
	}
	
	
	public String getNoteText(){
		return trim(noteText);
	}
	public void setNoteText(String text) {
		if (!nv(this.noteText).equals(nv(text))){
			toBeUpdated=true;
			this.noteText = vn(text);
		}
		
	}
	
	public String getMediaFilename(){
		return trim(mediaFilename);
	}
	public void setMediaFilename(String text) {
		if (!nv(this.mediaFilename).equals(nv(text))){
			toBeUpdated=true;
			this.mediaFilename = vn(text);
		}
		
	}
	
	public String getMediaTitle(){
		return trim(mediaTitle);
	}

	public void setMediaTitle(String text) {
		if (!nv(this.mediaTitle).equals(nv(text))){
			toBeUpdated=true;
			this.mediaTitle = vn(text);
		}
		
	}
	
	public Dimension getMediaSize(){
		return new Dimension(mediaWidth,mediaHeight);
	}
	
	public void setMediaSize(Dimension sz){
		mediaWidth = sz.width;
		mediaHeight = sz.height;
		toBeUpdated=true;
	}


	/**
	 * @return image
	 * @throws IOException 
	 */
	public BufferedImage getMediaImage() {
		if (mediaData == null) return null;
		ByteArrayInputStream bb = new ByteArrayInputStream(mediaData);
		
		if (this.image != null) {
			return this.image;
		}
		try {
			this.image = ImageIO.read(bb);
			return this.image;
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public void setMediaData(byte[] data){
		mediaData = data;
		image=null;
		toBeUpdated=true;
	}
	
	public byte[] getMediaData(){
		return mediaData;
	}
	
	public String getGivenname(){
		
		return trim(givenname);
	}
	public void setGivenname(String text){
		if (!nv(this.givenname).equals(nv(text))){
			toBeUpdated=true;
			this.givenname = vn(text);
		}
	
	}


	public String getPatronym(){
		return trim(patronym);
	}
	public void setPatronym(String text) {
		if (!nv(this.patronym).equals(nv(text))){
			toBeUpdated=true;
			this.patronym = vn(text);
		}
	}
	
	public String getPrefix(){
		return trim(prefix);
	}

	public void setPrefix(String text){
		if (!nv(this.prefix).equals(nv(text))){
			toBeUpdated=true;
			this.prefix = vn(text);
		}
		
	}
	
	
	public String getSurname(){
		return trim(surname);
	}
	
	public void setSurname(String text) {
		if (!nv(this.surname).equals(nv(text))){
			toBeUpdated=true;
			this.surname = vn(text);
		}
		
	}
	
	public String getPostfix(){
		return trim(postFix);
	}
	
	public void setPostfix(String text) {
		if (!nv(this.postFix).equals(nv(text))){
			toBeUpdated=true;
			this.postFix = vn(text);
		}
		
	}
	
	

	
	public String getSource(){
		return trim(sourceText);
	}
	
	public void setSource(String text) {
		if (!nv(this.sourceText).equals(nv(text))){
			toBeUpdated=true;
			this.sourceText = vn(text);
		}
		
	}

	
	
	public String getPrivateText(){
		return trim(privateText);
	}
	

	public void setPrivateText(String text) {
		if (!nv(this.privateText).equals(nv(text))){
			toBeUpdated=true;
			this.privateText = vn(text);
		}
		
	}
//	public void setPlace(String text) {
//		this.place = nv(text);
//	}
	

	
//	public void setPNid(int pnid) {
//		this.pnid = pnid;
//	}
	
	

	


	
	public Timestamp getModified(){
		return modified;
	}
	
	public Timestamp getCreated(){
		return createDate;
	}

	private String trim(String text){
		if (text==null) return null;
		
		String tek = text.trim();
		if (tek.endsWith(".")){
			tek = tek.substring(0,tek.length()-1);	
		}
		return tek.trim();
	}
	
	private String nv(String text) {
		if (text==null) return "";
		return text;
	}
	private String vn(String text){
		if ("".equals(text)) {
			text=null;
		}
		return text;
	}
	
}
