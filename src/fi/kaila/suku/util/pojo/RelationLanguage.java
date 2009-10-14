package fi.kaila.suku.util.pojo;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;


/**
 * Container class for RelationLanguage table
 * @author Kalle
 *
 */
public class RelationLanguage implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean toBeDeleted=false;
	private boolean toBeUpdated=false;
	
	int rnid=0;  
	int rid = 0; 	
	String langCode = null;
//	String tag = null; //  -- Tag of the Notice, Mostly Level 1 GEDCOM tags 
	String relationType = null; //    -- Notice type  (L)
	String description = null ; //      -- Description or remark  (L)
	String place = null; //        -- Place
	String noteText = null; // varchar,                -- Note textfield  (L)
	Timestamp modified = null; //timestamp,                           -- timestamp modified
	Timestamp createDate = null; //timestamp not null default now()    --  timestamp created  

	public RelationLanguage (ResultSet rs) throws SQLException {
		rnid=rs.getInt("rnid");  
		rid = rs.getInt("rid"); 
		langCode = rs.getString("langcode");  
		relationType = rs.getString("relationtype");
		description = rs.getString("description");
		place = rs.getString("place");
		noteText = rs.getString("notetext");
		modified = rs.getTimestamp("modified");
		createDate = rs.getTimestamp("createDate");  

	}
	
	public RelationLanguage(String langCode) {
		this.langCode = langCode;
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
//		toBeUpdated=true;
//	}
	public boolean isToBeUpdated(){
		return toBeUpdated;
	}
	
	
	
	public int getRnid(){
		return rnid;
	}
	


	public int getRid(){
		return rid;
	}
	
	public String getLangCode(){
		return langCode;
	}
	
	
	
	
	
	public String getRelationType(){
		return trim(relationType);
	}
	public void setRelationType(String text) {
		if (!nv(this.relationType).equals(nv(text))){
			toBeUpdated=true;
			this.relationType = vn(text);
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
	
	public String getPlace(){
		return trim(place);
	}
	public void setPlace(String text) {
		if (!nv(this.place).equals(nv(text))){
			toBeUpdated=true;
			this.place = vn(text);
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
