package fi.kaila.suku.util.pojo;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.imageio.ImageIO;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;

/**
 * @author FIKAAKAIL
 * 
 *         POJO for person data for display on lists etc read only
 * 
 */
public class PersonShortData implements Serializable, Transferable,
		Comparable<PersonShortData> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int pid = 0;
	private String refn = null;
	private String sex = null;
	private String group = null;
	private ShortName[] names = null;

	private String bDate = null;
	private String dDate = null;
	private String birtTag = null;
	private String deatTag = null;
	private String bPlace = null;
	private String dPlace = null;
	private String chred = null;
	private String buried = null;
	private String occu = null;
	private String mediaTitle = null;
	private String mediaFilename = null;
	private int mediaDataNotice = 0;
	private int marrCount = 0;
	private int childCount = 0;
	private int pareCount = 0;
	private boolean hasTodo = false;
	private byte[] imageData = null;

	private transient BufferedImage image = null;
	private String imageName = null;
	private Utils.PersonSource dragSource = null;

	private HashMap<String, String> tagMap = new HashMap<String, String>();
	/**
	 * Used by Relatives pane
	 */
	private transient int parentPid = 0;
	private transient String adopted = null;

	private int x = 0;
	private int y = 0;
	private int w = 0;
	private int h = 0;

	/**
	 * 
	 * @param pid
	 */
	public void setParentPid(int pid) {
		parentPid = pid;
	}

	/**
	 * 
	 * @return pid
	 */
	public int getParentPid() {
		return parentPid;
	}

	/**
	 * @return row count for graph is 2 if occu, death exists, else its 1
	 */
	public int getGraphRowCount() {
		int count = 1;

		if (occu != null || dDate != null || dPlace != null) {
			count++;
		}
		return count;
	}

	/**
	 * @param pid
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * @param refn
	 */
	public void setRefn(String refn) {
		this.refn = refn;
	}

	/**
	 * @param sex
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @param group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	// /**
	// * @param nameTag
	// */
	// public void setNameTag(String nameTag) {
	// this.nameTag = nameTag;
	// }
	//
	// /**
	// * @param givenname
	// */
	// public void setGivenname(String givenname) {
	// this.givenname = givenname;
	// }
	//
	// /**
	// * @param patronym
	// */
	// public void setPatronym(String patronym) {
	// this.patronym = patronym;
	// }
	//
	// /**
	// * @param prefix
	// * for name
	// */
	// public void setPrefix(String prefix) {
	// this.prefix = prefix;
	// }
	//
	// /**
	// * @param surname
	// */
	// public void setSurname(String surname) {
	// this.surname = surname;
	// }
	//
	// /**
	// * @param postfix
	// */
	// public void setPostfix(String postfix) {
	// this.postfix = postfix;
	// }
	//
	// /**
	// * @param morenames
	// * is names later than first
	// */
	// public void setMorenames(String morenames) {
	// this.morenames = morenames;
	// }

	/**
	 * @param birtDate
	 */
	public void setBirtDate(String birtDate) {
		this.bDate = birtDate;
	}

	/**
	 * @param birtPlace
	 */
	public void setBirtPlace(String birtPlace) {
		this.bPlace = birtPlace;
	}

	/**
	 * @param birtTag
	 *            (can be CHR, BIRT or null)
	 */
	public void setBirtTag(String birtTag) {
		this.birtTag = birtTag;
	}

	/**
	 * @param deatDate
	 */
	public void setDeatDate(String deatDate) {
		this.dDate = deatDate;
	}

	/**
	 * @param deatPlace
	 */
	public void setDeatPlace(String deatPlace) {
		this.dPlace = deatPlace;
	}

	/**
	 * @param deatTag
	 */
	public void setDeatTag(String deatTag) {
		this.deatTag = deatTag;
	}

	/**
	 * @param occu
	 */
	public void setOccupation(String occu) {
		this.occu = occu;
	}

	/**
	 * @param hasTodo
	 *            i.e. a notice with tag Todo
	 */
	public void setTodo(boolean hasTodo) {
		this.hasTodo = hasTodo;
	}

	/**
	 * @param marrCount
	 */
	public void setMarrCount(int marrCount) {
		this.marrCount = marrCount;
	}

	/**
	 * @param childCount
	 */
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	/**
	 * @param pareCount
	 */
	public void setPareCount(int pareCount) {
		this.pareCount = pareCount;
	}

	/**
	 * @param mediaTitle
	 */
	public void setMediaTitle(String mediaTitle) {
		this.mediaTitle = mediaTitle;
	}

	/**
	 * @param mediaFilename
	 */
	public void setMediaFilename(String mediaFilename) {
		this.mediaFilename = mediaFilename;
	}

	/**
	 * @param mediaDataNotice
	 */
	public void setMediaDataNotice(int mediaDataNotice) {
		this.mediaDataNotice = mediaDataNotice;
	}

	/**
	 * 
	 * If created with contructor that collects tag info then gives information
	 * of tags, else returns always false
	 * 
	 * @param tag
	 *            to query
	 * @return true if person has tag, falks otherwise
	 */
	public boolean existsTag(String tag) {
		String tagx = tagMap.get(tag);
		if (tagx == null)
			return false;
		return true;
	}

	/**
	 * 
	 */
	public PersonShortData() {

	}

	/**
	 * Add new name. This is intended for use with query database and default
	 * constructor
	 * 
	 * @param givenname
	 * @param patronym
	 * @param prefix
	 * @param surname
	 * @param postfix
	 */
	public void addName(String givenname, String patronym, String prefix,
			String surname, String postfix) {
		ShortName nn = new ShortName(givenname, patronym, prefix, surname,
				postfix);
		int namesize = 0;
		if (names != null) {
			namesize = names.length;
			ShortName[] nnn = names;
			names = new ShortName[namesize + 1];
			for (int i = 0; i < nnn.length; i++) {
				names[i] = nnn[i];
			}
		} else {
			names = new ShortName[1];
		}
		names[namesize] = nn;

	}

	/**
	 * Constuctor for alias person used for indexes to add altarnatives name
	 * 
	 * @param pid
	 * @param givenname
	 * @param patronyme
	 * @param prefix
	 * @param surname
	 * @param postfix
	 * @param birtDate
	 * @param deatDate
	 */
	public PersonShortData(int pid, String givenname, String patronyme,
			String prefix, String surname, String postfix, String birtDate,
			String deatDate) {

		this.pid = pid;
		addName(givenname, patronyme, prefix, surname, postfix);
		this.bDate = birtDate;
		this.dDate = deatDate;

	}

	/**
	 * Copy constuctor
	 * 
	 * @param lon
	 *            the PersonLongData to construct the short object from
	 */
	public PersonShortData(PersonLongData lon) {

		pid = lon.getPid();
		sex = lon.getSex();
		Vector<ShortName> sn = new Vector<ShortName>();

		for (int i = 0; i < lon.getNotices().length; i++) {
			UnitNotice n = lon.getNotices()[i];
			if (n.getTag().equals("NAME")) {

				ShortName nn = new ShortName(n.getGivenname(), n.getPatronym(),
						n.getPrefix(), n.getSurname(), n.getPostfix());

				sn.add(nn);

			} else if (n.getTag().equals("BIRT") || n.getTag().equals("CHR")) {
				if (birtTag == null || !birtTag.equals("BIRT")) {
					birtTag = n.getTag();
					bDate = n.getFromDate();
					bPlace = n.getPlace();
				}
			} else if (n.getTag().equals("DEAT") || n.getTag().equals("BURI")) {
				if (deatTag == null || !deatTag.equals("DEAT")) {
					deatTag = n.getTag();
					dDate = n.getFromDate();
					dPlace = n.getPlace();
				}
			} else if (n.getTag().equals("OCCU")) {
				if (occu == null) {
					occu = n.getDescription();

				}
			} else if (n.getTag().equals("PHOT")) {
				this.mediaDataNotice = n.getPnid();
				this.mediaFilename = n.getMediaFilename();
				this.mediaTitle = n.getMediaTitle();
				if (this.mediaFilename != null) {
					this.imageData = n.mediaData;
				}
			}

		}
		names = sn.toArray(new ShortName[0]);

	}

	/**
	 * Create short person dao with no list of all tags
	 * 
	 * @param con
	 * @param pid
	 * @throws SukuException
	 */
	public PersonShortData(Connection con, int pid) throws SukuException {
		helpConstruct(con, pid, false);
	}

	/**
	 * Create short person dao with list of all tags
	 * 
	 * @param con
	 * @param pid
	 * @param withAllTags
	 * @throws SukuException
	 */
	public PersonShortData(Connection con, int pid, boolean withAllTags)
			throws SukuException {
		helpConstruct(con, pid, withAllTags);
	}

	private void helpConstruct(Connection con, int pid, boolean withAllTags)
			throws SukuException {
		this.pid = pid;
		// this.famType = famType;
		Vector<ShortName> sn = new Vector<ShortName>();
		// int nameIdx = 0;
		// this.givenname = this.patronym = this.prefix = this.surname =
		// this.postfix = null;

		StringBuffer sql = new StringBuffer();
		sql.append("select u.sex,u.userrefn,u.groupid,"
				+ "u.tag,n.tag,n.givenname,");
		sql.append("n.patronym,n.prefix,n.surname,n.postfix,");
		sql.append("n.fromdate,n.Place,n.Description,"
				+ "n.pnid,n.mediadata,n.mediafilename,n.mediatitle ");
		sql.append("from unit as u left join unitnotice "
				+ "as n on u.pid = n.pid ");
		if (!withAllTags) {
			sql.append("and n.tag in "
					+ "('BIRT','DEAT','CHR','BURI','NAME','PHOT','OCCU') ");
		}
		sql.append("and n.surety >= 80 where u.pid = ? ");
		sql.append("order by n.noticerow ");

		PreparedStatement pstm;

		try {
			pstm = con.prepareStatement(sql.toString());
			pstm.setInt(1, pid);
			ResultSet rs = pstm.executeQuery();
			String tag;
			while (rs.next()) {
				if (this.sex == null) {
					this.refn = rs.getString(2);
					this.sex = rs.getString(1);
					this.group = rs.getString(3);
				}
				tag = rs.getString(5);
				if (tag != null) {
					if (tag.equals("NAME")) {
						ShortName nn = new ShortName(rs.getString(6), rs
								.getString(7), rs.getString(8),
								rs.getString(9), rs.getString(10));

						sn.add(nn);
					}
					// if (this.nameTag == null && "NAME".equals(tag)
					// && this.givenname == null && this.patronym == null
					// && this.prefix == null && this.surname == null
					// && this.postfix == null) {
					// this.givenname = rs.getString(6);
					// this.patronym = rs.getString(7);
					// this.prefix = rs.getString(8);
					// this.surname = rs.getString(9);
					// this.postfix = rs.getString(10);
					// this.nameTag = tag;
					// } else if (this.nameTag != null && "NAME".equals(tag)) {
					// String restname = rs.getString(9);
					// if (restname != null) {
					// if (this.morenames == null) {
					// this.morenames = restname;
					// } else {
					// this.morenames += ";";
					// this.morenames += restname;
					// }
					// }
					// }

					if (tag.equals("BIRT") || tag.equals("CHR")) {

						if (this.birtTag == null || tag.equals("BIRT")) {
							this.birtTag = tag;
							this.bDate = rs.getString(11);
							this.bPlace = rs.getString(12);
						}
					}

					if (tag.equals("DEAT") || tag.equals("BURI")) {

						if (this.deatTag == null || tag.equals("DEAT")) {
							this.deatTag = tag;
							this.dDate = rs.getString(11);
							this.dPlace = rs.getString(12);
						}
					}

					if ("OCCU".equals(tag)) {
						this.occu = rs.getString(13);
					}

					if ("PHOT".equals(tag)) {
						this.mediaDataNotice = rs.getInt(14);
						this.mediaFilename = rs.getString(16);
						this.mediaTitle = rs.getString(17);
						if (this.mediaFilename != null) {
							this.imageData = rs.getBytes(15);
						}
					}
					tagMap.put(tag, tag);

				}
			}
			rs.close();
			pstm.close();

			names = sn.toArray(new ShortName[0]);
		} catch (Exception e) {
			throw new SukuException(e);
		}

	}

	/**
	 * @param x
	 * @param y
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @param w
	 * @param h
	 */
	public void setSize(int w, int h) {
		this.w = w;
		this.h = h;
	}

	/**
	 * @return Point of upper left corner withing container
	 */
	public Point getLocation() {
		return new Point(this.x, this.y);
	}

	/**
	 * @return Dimension containing size of area
	 */
	public Dimension getSize() {
		return new Dimension(this.w, this.h);
	}

	/**
	 * @return PID
	 */
	public int getPid() {
		return this.pid;
	}

	/**
	 * @return user refn
	 */
	public String getRefn() {
		return this.refn;
	}

	/**
	 * @return sex
	 */
	public String getSex() {
		return this.sex;
	}

	/**
	 * @return group
	 */
	public String getGroup() {
		return this.group;
	}

	/**
	 * @return name count
	 */
	public int getNameCount() {
		if (names == null)
			return 0;
		return this.names.length;
	}

	/**
	 * @return givenname 0 (first)
	 */
	public String getGivenname() {
		return getGivenname(0);
	}

	/**
	 * @param idx
	 * @return givenname idx
	 */
	public String getGivenname(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getGivenname();
	}

	/**
	 * @return patronym for first name
	 */
	public String getPatronym() {

		return getPatronym(0);
	}

	/**
	 * @param idx
	 * @return patronyme for name idx
	 */
	public String getPatronym(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getPatronyme();
	}

	/**
	 * @return name prefix for first name
	 */
	public String getPrefix() {

		return getPrefix(0);
	}

	/**
	 * @param idx
	 * @return prefix for name idx
	 */
	public String getPrefix(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getPrefix();
	}

	/**
	 * @return surname for first name
	 */
	public String getSurname() {

		return getSurname(0);
	}

	/**
	 * @param idx
	 * @return surname for name idx
	 */
	public String getSurname(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getSurname();
	}

	/**
	 * @return name postfix for first name
	 */
	public String getPostfix() {

		return getPostfix(0);
	}

	/**
	 * @param idx
	 * @return postfix for name idx
	 */
	public String getPostfix(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getPostfix();
	}

	/**
	 * @return morenames
	 */
	public String getMorenames() {
		if (names == null || names.length < 2)
			return null;
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < names.length; i++) {
			if (i > 1) {
				sb.append(";");
			}
			if (names[i].getPrefix() != null) {
				sb.append(names[i].getPrefix());
				sb.append(" ");
			}
			if (names[i].getSurname() != null) {
				sb.append(names[i].getSurname());
			}
		}

		return sb.toString();
	}

	/**
	 * @return birth date main part
	 */
	public String getBirtDate() {
		return this.bDate;
	}

	/**
	 * 
	 * @return birth uear as int
	 */
	public int getBirtYear() {
		if (bDate == null)
			return 0;
		return Integer.parseInt(bDate.substring(0, 4));
	}

	/**
	 * @return birth tag
	 */
	public String getBirtTag() {
		return this.birtTag;
	}

	/**
	 * @return birt place
	 */
	public String getBirtPlace() {
		return this.bPlace;
	}

	/**
	 * @return death data main part
	 */
	public String getDeatDate() {
		return this.dDate;
	}

	/**
	 * @return deat tag (DEAT, BURI, null)
	 */
	public String getDeatTag() {
		return this.deatTag;
	}

	/**
	 * @return death place
	 */
	public String getDeatPlace() {
		return this.dPlace;
	}

	/**
	 * @return christianed
	 */
	public String getChrDate() {
		return this.chred;
	}

	/**
	 * @return buried
	 */
	public String getBuriedDate() {
		return this.buried;
	}

	/**
	 * @return occupation
	 */
	public String getOccupation() {
		return this.occu;
	}

	/**
	 * @return # of marriages
	 */
	public int getMarrCount() {
		return this.marrCount;
	}

	/**
	 * @return # of children
	 */
	public int getChildCount() {
		return this.childCount;
	}

	/**
	 * @return # of parents
	 */
	public int getPareCount() {
		return this.pareCount;
	}

	/**
	 * @return true if To do notice exists
	 */
	public boolean getTodo() {
		return this.hasTodo;
	}

	/**
	 * @return media title
	 */
	public String getMediaTitle() {
		return this.mediaTitle;
	}

	/**
	 * @return media filename
	 */
	public String getMediaFilename() {
		return this.mediaFilename;
	}

	/**
	 * @return media notice id
	 */
	public int getMediaDataNotice() {
		return this.mediaDataNotice;
	}

	// /**
	// * Family member type is
	// *
	// * null = main person/subject
	// * FAM_TYPE_SPOUSE = spouse
	// * FAM_TYPE_CHILD = child
	// *
	// * @return family member type
	// */
	// public String getFamType(){
	// return this.famType;
	// }

	/**
	 * 
	 * @return true if person has image
	 */
	public boolean hasImage() {
		if (this.imageData == null)
			return false;
		return true;
	}

	/**
	 * @return image
	 * @throws IOException
	 */
	public BufferedImage getImage() {
		if (this.imageData == null)
			return null;
		ByteArrayInputStream bb = new ByteArrayInputStream(this.imageData);

		if (this.image != null) {
			return this.image;
		}
		try {
			this.image = ImageIO.read(bb);
			return this.image;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @return image namefield
	 */
	public String getImageName() {
		return this.imageName;
	}

	@Override
	public String toString() {
		// return "id="+this.pid + "/name=" + this.givenname + " " +
		// this.surname + "/bd=" + this.bDate;
		// }
		StringBuffer sb = new StringBuffer();
		sb.append(getPid() + "\t" + Resurses.getString("SEX_" + getSex())
				+ "\t" + nv(getPrefix()) + "\t" + nv(getSurname()) + "\t"
				+ nv(getMorenames()) + "\t" + nv(getGivenname()) + "\t"
				+ nv(getPatronym()) + "\t" + nv(getPostfix()) + "\t"
				+ nv(Utils.textDate(getBirtDate(), false)) + "\t"
				+ nv(getBirtPlace()) + "\t"
				+ nv(Utils.textDate(getDeatDate(), false)) + "\t"
				+ nv(getDeatPlace()) + nv(getOccupation()));
		int ii = getMarrCount();
		String ttxt = (ii == 0) ? "" : "" + ii;
		sb.append("\t" + ttxt);
		ii = getChildCount();
		ttxt = (ii == 0) ? "" : "" + ii;
		sb.append("\t" + ttxt);
		ii = getPareCount();
		ttxt = (ii == 0) ? "" : "" + ii;
		sb.append("\t" + ttxt);

		return sb.toString();
	}

	/**
	 * @return used for copy to clipbpard
	 */
	public String getHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append(Resurses.getString("T_PID") + "\t"
				+ Resurses.getString("T_SEX") + "\t"
				+ Resurses.getString("T_PREFIX") + "\t"
				+ Resurses.getString("T_SURNAME") + "\t"
				+ Resurses.getString("T_MORENAMES") + "\t"
				+ Resurses.getString("T_GIVENNAME") + "\t"
				+ Resurses.getString("T_PATRONYME") + "\t"
				+ Resurses.getString("T_POSTFIX") + "\t"
				+ Resurses.getString("T_BIRT") + "\t"
				+ Resurses.getString("T_BIRTPLACE") + "\t"
				+ Resurses.getString("T_DEAT") + "\t"
				+ Resurses.getString("T_DEATPLACE")
				+ Resurses.getString("T_OCCUPATION"));
		sb.append("\t" + Resurses.getString("T_ISMARR") + "\t"
				+ Resurses.getString("T_ISCHILD") + "\t"
				+ Resurses.getString("T_ISPARE"));
		return sb.toString();
	}

	/**
	 * @return name as written
	 */
	public String getTextName() {

		StringBuilder sb = new StringBuilder();
		if (getGivenname() != null) {
			sb.append(getGivenname());
		}
		if (getPrefix() != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(getPrefix());
		}
		if (getSurname() != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(getSurname());
		}
		if (getPostfix() != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(getPostfix());
		}
		return sb.toString();
	}

	/**
	 * @return for sorting order w/o patronyme
	 */
	public String getAlfaName() {
		return getAlfaName(true);
	}

	/**
	 * @param withPatronyme
	 * @return name for sorting order with optional patronyme
	 */
	public String getAlfaName(boolean withPatronyme) {

		StringBuilder sb = new StringBuilder();

		if (getPrefix() != null) {
			sb.append(getPrefix());
		}

		if (getSurname() != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(getSurname());
		}
		if (getGivenname() != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(getGivenname());
		}
		if (withPatronyme) {
			if (getPatronym() != null) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append(getPatronym());
			}
		}

		if (getPostfix() != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(getPostfix());
		}
		return sb.toString();
	}

	/**
	 * dataflavour for drag-and-drop
	 */
	public static final DataFlavor[] df = { new DataFlavor(
			PersonShortData.class, "PersonShortData") };

	/**
	 * @return dataflavour for drag-and-drop
	 */
	public static DataFlavor getPersonShortDataFlavour() {
		return df[0];
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {

		if (flavor.equals(df[0])) {
			return this;
		}
		return this.toString();
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {

		return df;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {

		if (flavor.equals(df[0])) {
			return true;
		}
		return false;
	}

	/**
	 * @param dragSource
	 */
	public void setDragSource(Utils.PersonSource dragSource) {
		this.dragSource = dragSource;
	}

	/**
	 * @return PersonSource enumerator
	 */
	public Utils.PersonSource getDragSource() {
		return dragSource;
	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	/**
	 * collator according to language
	 */
	public static Collator fiCollator = Collator.getInstance(new Locale(
			Resurses.getLanguage()));

	@Override
	public int compareTo(PersonShortData o) {

		int cl = fiCollator.compare(nv(getSurname()), nv(o.getSurname()));
		if (cl != 0) {
			return cl;
		}
		cl = fiCollator.compare(nv(getGivenname()), nv(o.getGivenname()));
		if (cl != 0) {
			return cl;
		}
		return (nv(getBirtDate()).compareTo(nv(o.getBirtDate())));

	}

	/**
	 * @param adopted
	 */
	public void setAdopted(String adopted) {
		this.adopted = adopted;
	}

	/**
	 * @return adopted status
	 */
	public String getAdopted() {
		return adopted;
	}

	private class ShortName implements Serializable {

		/**  */
		private static final long serialVersionUID = 1L;
		private String shGivenname;
		private String shPatronyme;
		private String shPrefix;
		private String shSurname;
		private String shPostfix;

		ShortName(String given, String patro, String pre, String sur,
				String postf) {
			shGivenname = given;
			shPatronyme = patro;
			shPrefix = pre;
			shSurname = sur;
			shPostfix = postf;
		}

		/**
		 * @return the shGivenname
		 */
		public String getGivenname() {
			return shGivenname;
		}

		/**
		 * @return the shPatronyme
		 */
		public String getPatronyme() {
			return shPatronyme;
		}

		/**
		 * @return the shPrefix
		 */
		public String getPrefix() {
			return shPrefix;
		}

		/**
		 * @return the shSurname
		 */
		public String getSurname() {
			return shSurname;
		}

		/**
		 * @return the shPostfix
		 */
		public String getPostfix() {
			return shPostfix;
		}
	}

}
