package fi.kaila.suku.util;

import java.util.logging.Logger;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.swing.dialog.SearchCriteria;
import fi.kaila.suku.util.pojo.PersonShortData;

//import java.awt.image.BufferedImage;

/**
 * 
 * One row of databalse list. Database window consists of rows containing
 * SukuRow objects
 * 
 * @author FIKAAKAIL
 * 
 */
public class SukuRow {

	// PersonShortData person;
	int pid;
	Suku suku = null;
	// String refn=null;
	// String tsex=null; // col 0
	//	
	// // BufferedImage sex = null;
	//	
	// // String name=null; // col 1
	// String patro=null;
	// String givenName=null; // col 1.?
	// String prefix = null;
	// String surName = null;
	// String morenames = null;
	//	
	// String birthDate=null; // col 2
	// String birtPlace=null;
	// String deatDate=null; // col 3
	// String deatPlace=null;
	// String occupation=null;
	// String group=null;
	// int pid=0;
	// int marrc = 0;
	// int childc = 0;
	// int parec = 0;
	// boolean todo;
	//	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(SukuRow.class.getName());

	/**
	 * Constuctor of a Sukurow
	 * 
	 * @param suku
	 * @param model
	 * @param person
	 */
	public SukuRow(Suku suku, SukuModel model, PersonShortData person) {
		// this.person = person;
		this.suku = suku;
		this.pid = person.getPid();
	}

	// String refn,String sex,String group,
	// int marrc,int chilc, int parec,boolean todo,
	// String prefix,String surName, String givenName,String patro,String
	// morenames,
	// String birthDate,String birtPlace,String deatDate,String deatPlace,
	// String occupation,int pid){
	// //this.model = model;
	// this.refn= refn;
	// this.tsex=sex;
	// this.prefix=prefix;
	// this.surName=surName;
	// this.givenName = givenName;
	// this.morenames = morenames;
	// this.patro = patro;
	// this.birthDate = birthDate;
	// this.birtPlace = birtPlace;
	// this.deatDate = deatDate;
	// this.deatPlace = deatPlace;
	// this.occupation = occupation;
	// this.group = group;
	//
	// this.pid=pid;
	// this.marrc = marrc;
	// this.childc = chilc;
	// this.parec = parec;
	// this.todo = todo;
	// this.pid=pid;
	// logger.fine("Contrs: " + todo + "/" + surName + "/" + givenName);
	// }

	SukuRow(Suku suku) {
		this.suku = suku;
	}

	/**
	 * Set value into column
	 * 
	 * @param idx
	 * @param value
	 */
	public void set(int idx, Object value) {

		// switch (idx) {
		// case 0: this.tsex = (String)value;
		// break;
		// case 1: this.name = (String)value;
		// break;
		// case 2: this.patro = (String)value;
		// break;
		// case 3: this.birthDate = (String)value;
		// break;
		// case 4: this.deatDate = (String)value;
		// break;
		// case 5: this.idInfo = (String)value;
		// break;
		//
		//
		//		
		// }
		//		

		// this.taulu[idx]=(String)value;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(" (");
		PersonShortData person = suku.getPerson(pid);
		if (person.getBirtDate() != null) {
			sb.append(person.getBirtDate());
		}
		if (person.getDeatDate() != null) {
			sb.append(" - ");
			sb.append(person.getDeatDate());
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Get item from column
	 * 
	 * @param idx
	 * @return content on column
	 */
	public Object get(int idx) {
		StringBuffer sb;

		// int fullIdx;
		SearchCriteria crit;
		try {
			crit = SearchCriteria.getCriteria(null);
		} catch (SukuException e) {
			// This should never come here
			e.printStackTrace();
			return null;
		}
		PersonShortData person = suku.getPerson(pid);
		// fullIdx = this.model.getFullIndex(idx);
		// fullIdx = idx;
		// System.out.println("get(" + idx+") : fullIdx = " + fullIdx);

		if (idx == crit.getColIndex(Resurses.COLUMN_T_SEX)) {
			// case SukuModel.TSEX_COL: //return this.sex ;
			if (person.getSex().equals("M")) {
				return SukuModel.manIcon;
			} else if (person.getSex().equals("F")) {
				return SukuModel.womanIcon;
			}
			return SukuModel.unknownIcon;
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_NAME)) {

			// case SukuModel.TNAME_COL:
			sb = new StringBuffer();
			if (person.getAlfaName(false) != null) {
				sb.append(person.getAlfaName(false) + " ");
			}

			if (crit.isPropertySet(Resurses.COLUMN_T_ALL_NAMES)) {
				if (person.getMorenames() != null) {
					sb.append(";");
					sb.append(person.getMorenames());
				}
			}

			return sb.toString();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_ISMARR)) {

			if (person.getMarrCount() == 0)
				return "";
			return "" + person.getMarrCount();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_ISCHILD)) {
			if (person.getChildCount() == 0)
				return "";
			return "" + person.getChildCount();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_ISPARE)) {
			if (person.getPareCount() == 0)
				return "";
			return "" + person.getPareCount();
			// } else if (fullIdx ==
			// crit.getColIndex(Resurses.COLUMN_T_GIVENNAME)){
			//			
			// return this.givenName;
			// } else if (fullIdx ==
			// crit.getColIndex(Resurses.COLUMN_T_SURNAME)){
			//			
			// return this.surName;
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_TODO)) {

			if (!person.getTodo())
				return "";
			return "*"; // this.todo;
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_PATRONYME)) {

			return person.getPatronym();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_BIRT)) {

			return Utils.textDate(person.getBirtDate(), false);
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_BIRTPLACE)) {

			return person.getBirtPlace();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_DEAT)) {

			return Utils.textDate(person.getDeatDate(), false);
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_DEATPLACE)) {

			return person.getDeatPlace();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_OCCUPATION)) {

			return person.getOccupation();

		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_GROUP)) {

			return person.getGroup();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_REFN)) {

			return person.getRefn();
		} else if (idx == crit.getColIndex(Resurses.COLUMN_T_PID)) {

			return Integer.valueOf(person.getPid());
		} else {

			// case 1:
			//		
			// if (this.prefix == null) {
			// return this.surName ;
			// }
			//			
			// return this.prefix + " "+ this.surName ;
			// case 2: return this.givenName;
			// case 3: return this.patro;
			// case 4: return this.birthDate ;
			//	
			// case 5: return this.deatDate;
			//	
			// case 6: return this.idInfo;

			return null;

		}

	}

	public PersonShortData getPerson() {
		return suku.getPerson(pid);
	}

	/**
	 * @return person name
	 */
	public String getName() {
		return getPerson().getTextName();
	}

	/**
	 * @return the todo text
	 */
	public String getTodo() {
		return (getPerson().getTodo()) ? "*" : "";
	}

	/**
	 * @return person pid
	 */
	public String getRefn() {
		return getPerson().getRefn();
	}

	public String getGroup() {
		return getPerson().getGroup();
	}

	public int getPid() {
		return pid;
	}

}
