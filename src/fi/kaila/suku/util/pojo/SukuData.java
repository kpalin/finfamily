package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;


/**
 * @author FIKAAKAIL
 *
 *This is a structure for family with both short persons and short relations
 */
public class SukuData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8438517229487947671L;

	/**
	 * cmd parameter value
	 */
	public String cmd =null;

	/**
	 * result string
	 */
	public String resu = null;
	/**
	 * array of pids fromn relative fect e.g.
	 */
	public int [] pidArray=null;
	/**
	 * e.g. settings result
	 */
	public String [] generalArray=null;
	/**
	 * array vector storage (Types.xls page types) 
	 */
	public Vector<String[]> vvTypes = null;
	/**
	 * array vector storage (Types.xls page texts) 
	 */
	public Vector<String[]> vvTexts = null;
	/**
	 * array of persons. Subject in [0]
	 */
	public PersonShortData[] pers = null;
	/**
	 * Array of relations
	 */
	public RelationShortData[] rels= null;
	
	public PlaceLocationData[] places=null;
	
	/**
	 * Single full person data
	 */
	
	public PersonLongData persLong=null;
	
	public PersonLongData [] persons = null;
	public Relation [] relations=null; 
	/**
	 * Count can be used to return count to caller
	 */
	public int resuCount=0;
	
	/**
	 * @return true if person only
	 */
	public boolean isPersonOnly(){
		if (this.rels == null && this.pers.length == 1) return true;
		return false;
	}
	
	/**
	 * Constructor for single person carrier
	 * @param pojo
	 */
	public SukuData (PersonShortData pojo){
		this.pers = new PersonShortData[1];
		this.pers[0] = pojo;
	}
	
	/**
	 * default constructor
	 */
	public SukuData() {
		//
	}
	
	/**
	 * @return the subject i.e. owner of this family or only person
	 */
	public PersonShortData getSubject(){
		return this.pers[0];
	}
	
	@Override
	public String toString(){
		if (this.pers == null) return null;
		return "Family of " + this.pers[0].toString();
	}
	
	
	public HashMap<Integer,ReportUnit> reportUnits=null;

	public Vector<ReportUnit> tables=null;

	public int resultPid=0;
}
