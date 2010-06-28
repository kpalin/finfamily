package fi.kaila.suku.util.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 * 
 * Class used as container to transport pojo objects between client and server
 * using the kontroller. Fields are used directly and are very specific to the
 * call used.
 * 
 * 
 * 
 * @author FIKAAKAIL
 * 
 */
public class SukuData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The main cmd parameter value
	 */
	public String cmd = null;

	/**
	 * result string. This should be null if request was successfull. In case of
	 * error it should contain the error message
	 * 
	 */
	public String resu = null;
	/**
	 * array of pids fromn relative fect e.g.
	 */
	public int[] pidArray = null;

	/**
	 * Transfer of general binary object
	 */
	public byte[] buffer = null;
	/**
	 * e.g. settings result
	 */
	public String[] generalArray = null;
	/**
	 * Text value returned
	 */
	public String generalText = null;
	/**
	 * array vector storage (Types.xls page types)
	 */
	public Vector<String[]> vvTypes = null;
	/**
	 * array vector storage (Conversions texts)
	 */
	public Vector<String[]> vvTexts = null;
	/**
	 * array of persons. Subject in [0]
	 */
	public PersonShortData[] pers = null;
	/**
	 * Array of relations
	 */
	public RelationShortData[] rels = null;
	/**
	 * Array of place locations (used by map)
	 */
	public PlaceLocationData[] places = null;

	/**
	 * Single full person data
	 */

	public PersonLongData persLong = null;

	/**
	 * array of long persons
	 */
	public PersonLongData[] persons = null;
	/**
	 * array of relations
	 */
	public Relation[] relations = null;
	/**
	 * Count can be used to return count to caller
	 */
	public int resuCount = 0;

	/**
	 * map of reportUnits
	 */
	public HashMap<Integer, ReportUnit> reportUnits = null;

	/**
	 * vector of reportunits
	 */
	public Vector<ReportUnit> tables = null;

	/**
	 * answer as a pid
	 */
	public int resultPid = 0;
}
