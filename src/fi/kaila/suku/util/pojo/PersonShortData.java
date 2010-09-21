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
 * The Class PersonShortData.
 * 
 * @author FIKAAKAIL
 * 
 *         POJO for person data for display on lists etc read only
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
	private String bCountry = null;
	private String dPlace = null;
	private String dCountry = null;
	private final String chred = null;
	private final String buried = null;
	private String occu = null;
	private String mediaTitle = null;
	private String mediaFilename = null;
	private int mediaDataNotice = 0;
	private int marrCount = 0;
	private int childCount = 0;
	private int pareCount = 0;
	private boolean hasUnkn = false;
	private byte[] imageData = null;

	private transient BufferedImage image = null;
	private final String imageName = null;
	private Utils.PersonSource dragSource = null;

	private final HashMap<String, String> tagMap = new HashMap<String, String>();
	/**
	 * Used by Relatives pane
	 */
	private transient int parentPid = 0;
	private transient String adopted = null;
	/**
	 * used by FamilyTree
	 */
	private transient int surety = 100;

	private int x = 0;
	private int y = 0;
	private int w = 0;
	private int h = 0;

	/**
	 * Sets the parent pid.
	 * 
	 * @param pid
	 *            the new parent pid
	 */
	public void setParentPid(int pid) {
		parentPid = pid;
	}

	/**
	 * Gets the parent pid.
	 * 
	 * @return pid
	 */
	public int getParentPid() {
		return parentPid;
	}

	/**
	 * Gets the graph row count.
	 * 
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
	 * Sets the pid.
	 * 
	 * @param pid
	 *            the new pid
	 */
	public void setPid(int pid) {
		this.pid = pid;
	}

	/**
	 * Sets the refn.
	 * 
	 * @param refn
	 *            the new refn
	 */
	public void setRefn(String refn) {
		this.refn = refn;
	}

	/**
	 * Sets the sex.
	 * 
	 * @param sex
	 *            the new sex
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * Sets the group.
	 * 
	 * @param group
	 *            the new group
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
	 * Sets the birt date.
	 * 
	 * @param birtDate
	 *            the new birt date
	 */
	public void setBirtDate(String birtDate) {
		this.bDate = birtDate;
	}

	/**
	 * Sets the birt place.
	 * 
	 * @param birtPlace
	 *            the new birt place
	 */
	public void setBirtPlace(String birtPlace) {
		this.bPlace = birtPlace;
	}

	/**
	 * Sets the birt country.
	 * 
	 * @param birtCountry
	 *            the new birt country
	 */
	public void setBirtCountry(String birtCountry) {
		this.bCountry = birtCountry;
	}

	/**
	 * Sets the birt tag.
	 * 
	 * @param birtTag
	 *            (can be CHR, BIRT or null)
	 */
	public void setBirtTag(String birtTag) {
		this.birtTag = birtTag;
	}

	/**
	 * Sets the deat date.
	 * 
	 * @param deatDate
	 *            the new deat date
	 */
	public void setDeatDate(String deatDate) {
		this.dDate = deatDate;
	}

	/**
	 * Sets the deat place.
	 * 
	 * @param deatPlace
	 *            the new deat place
	 */
	public void setDeatPlace(String deatPlace) {
		this.dPlace = deatPlace;
	}

	/**
	 * Sets the deat country.
	 * 
	 * @param deatCountry
	 *            the new deat country
	 */
	public void setDeatCountry(String deatCountry) {
		this.dCountry = deatCountry;
	}

	/**
	 * Sets the deat tag.
	 * 
	 * @param deatTag
	 *            the new deat tag
	 */
	public void setDeatTag(String deatTag) {
		this.deatTag = deatTag;
	}

	/**
	 * Sets the occupation.
	 * 
	 * @param occu
	 *            the new occupation
	 */
	public void setOccupation(String occu) {
		this.occu = occu;
	}

	/**
	 * Sets the todo.
	 * 
	 * @param hasTodo
	 *            i.e. a notice with tag Todo
	 */
	public void setUnkn(boolean hasUnkn) {
		this.hasUnkn = hasUnkn;
	}

	/**
	 * Sets the marr count.
	 * 
	 * @param marrCount
	 *            the new marr count
	 */
	public void setMarrCount(int marrCount) {
		this.marrCount = marrCount;
	}

	/**
	 * Sets the child count.
	 * 
	 * @param childCount
	 *            the new child count
	 */
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	/**
	 * Sets the pare count.
	 * 
	 * @param pareCount
	 *            the new pare count
	 */
	public void setPareCount(int pareCount) {
		this.pareCount = pareCount;
	}

	/**
	 * Sets the media title.
	 * 
	 * @param mediaTitle
	 *            the new media title
	 */
	public void setMediaTitle(String mediaTitle) {
		this.mediaTitle = mediaTitle;
	}

	/**
	 * Sets the media filename.
	 * 
	 * @param mediaFilename
	 *            the new media filename
	 */
	public void setMediaFilename(String mediaFilename) {
		this.mediaFilename = mediaFilename;
	}

	/**
	 * Sets the media data notice.
	 * 
	 * @param mediaDataNotice
	 *            the new media data notice
	 */
	public void setMediaDataNotice(int mediaDataNotice) {
		this.mediaDataNotice = mediaDataNotice;
	}

	/**
	 * If created with contructor that collects tag info then gives information
	 * of tags, else returns always false.
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
	 * Instantiates a new person short data.
	 */
	public PersonShortData() {

	}

	/**
	 * Add new name. This is intended for use with query database and default
	 * constructor
	 * 
	 * @param givenname
	 *            the givenname
	 * @param patronym
	 *            the patronym
	 * @param prefix
	 *            the prefix
	 * @param surname
	 *            the surname
	 * @param postfix
	 *            the postfix
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
	 * Constuctor for alias person used for indexes to add altarnatives name.
	 * 
	 * @param pid
	 *            the pid
	 * @param givenname
	 *            the givenname
	 * @param patronyme
	 *            the patronyme
	 * @param prefix
	 *            the prefix
	 * @param surname
	 *            the surname
	 * @param postfix
	 *            the postfix
	 * @param birtDate
	 *            the birt date
	 * @param deatDate
	 *            the deat date
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
	 * Copy constuctor.
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
					bCountry = n.getCountry();
				}
			} else if (n.getTag().equals("DEAT") || n.getTag().equals("BURI")) {
				if (deatTag == null || !deatTag.equals("DEAT")) {
					deatTag = n.getTag();
					dDate = n.getFromDate();
					dPlace = n.getPlace();
					dCountry = n.getCountry();
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
	 * Create short person dao with no list of all tags.
	 * 
	 * @param con
	 *            the con
	 * @param pid
	 *            the pid
	 * @throws SukuException
	 *             the suku exception
	 */
	public PersonShortData(Connection con, int pid) throws SukuException {
		helpConstruct(con, pid, true);
	}

	/**
	 * Create short person dao with list of all tags.
	 * 
	 * @param con
	 *            the con
	 * @param pid
	 *            the pid
	 * @param withAllTags
	 *            the with all tags
	 * @throws SukuException
	 *             the suku exception
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

		StringBuilder sql = new StringBuilder();
		sql.append("select u.sex,u.userrefn,u.groupid,"
				+ "u.tag,n.tag,n.givenname,");
		sql.append("n.patronym,n.prefix,n.surname,n.postfix,");
		sql.append("n.fromdate,n.Place,n.Description,"
				+ "n.pnid,n.mediadata,n.mediafilename,n.mediatitle ");
		sql.append("from unit as u left join unitnotice "
				+ "as n on u.pid = n.pid ");
		if (!withAllTags) {
			sql.append("and n.tag in "
					+ "('BIRT','DEAT','CHR','BURI','NAME','PHOT','OCCU','UNKN') ");
		}
		// sql.append("and n.surety >= 80 where u.pid = ? ");
		sql.append("where u.pid = ? ");
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
						ShortName nn = new ShortName(rs.getString(6),
								rs.getString(7), rs.getString(8),
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
					if ("UNKN".equals(tag)) {
						hasUnkn = true;
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
	 * Sets the location.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the size.
	 * 
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 */
	public void setSize(int w, int h) {
		this.w = w;
		this.h = h;
	}

	/**
	 * Gets the location.
	 * 
	 * @return Point of upper left corner withing container
	 */
	public Point getLocation() {
		return new Point(this.x, this.y);
	}

	/**
	 * Gets the size.
	 * 
	 * @return Dimension containing size of area
	 */
	public Dimension getSize() {
		return new Dimension(this.w, this.h);
	}

	/**
	 * Gets the pid.
	 * 
	 * @return PID
	 */
	public int getPid() {
		return this.pid;
	}

	/**
	 * Gets the refn.
	 * 
	 * @return user refn
	 */
	public String getRefn() {
		return this.refn;
	}

	/**
	 * Gets the sex.
	 * 
	 * @return sex
	 */
	public String getSex() {
		return this.sex;
	}

	/**
	 * Gets the group.
	 * 
	 * @return group
	 */
	public String getGroup() {
		return this.group;
	}

	/**
	 * Gets the name count.
	 * 
	 * @return name count
	 */
	public int getNameCount() {
		if (names == null)
			return 0;
		return this.names.length;
	}

	/**
	 * Gets the givenname.
	 * 
	 * @return givenname 0 (first)
	 */
	public String getGivenname() {
		return getGivenname(0);
	}

	/**
	 * Gets the givenname.
	 * 
	 * @param idx
	 *            the idx
	 * @return givenname idx
	 */
	public String getGivenname(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getGivenname();
	}

	/**
	 * Gets the patronym.
	 * 
	 * @return patronym for first name
	 */
	public String getPatronym() {

		return getPatronym(0);
	}

	/**
	 * Gets the patronym.
	 * 
	 * @param idx
	 *            the idx
	 * @return patronyme for name idx
	 */
	public String getPatronym(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getPatronyme();
	}

	/**
	 * Gets the prefix.
	 * 
	 * @return name prefix for first name
	 */
	public String getPrefix() {

		return getPrefix(0);
	}

	/**
	 * Gets the prefix.
	 * 
	 * @param idx
	 *            the idx
	 * @return prefix for name idx
	 */
	public String getPrefix(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getPrefix();
	}

	/**
	 * Gets the surname.
	 * 
	 * @return surname for first name
	 */
	public String getSurname() {

		return getSurname(0);
	}

	/**
	 * Gets the surname.
	 * 
	 * @param idx
	 *            the idx
	 * @return surname for name idx
	 */
	public String getSurname(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getSurname();
	}

	/**
	 * Gets the postfix.
	 * 
	 * @return name postfix for first name
	 */
	public String getPostfix() {

		return getPostfix(0);
	}

	/**
	 * Gets the postfix.
	 * 
	 * @param idx
	 *            the idx
	 * @return postfix for name idx
	 */
	public String getPostfix(int idx) {
		if (names == null || names.length == 0)
			return null;
		return names[idx].getPostfix();
	}

	/**
	 * Gets the morenames.
	 * 
	 * @return morenames
	 */
	public String getMorenames() {
		if (names == null || names.length < 2)
			return null;
		StringBuilder sb = new StringBuilder();
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
	 * Gets the birt date.
	 * 
	 * @return birth date main part
	 */
	public String getBirtDate() {
		return this.bDate;
	}

	/**
	 * Gets the birt year.
	 * 
	 * @return birth year as int
	 */
	public int getBirtYear() {
		if (bDate == null)
			return 0;
		return Integer.parseInt(bDate.substring(0, 4).trim());
	}

	/**
	 * Gets the deat year.
	 * 
	 * @return death year as int
	 */
	public int getDeatYear() {
		if (dDate == null)
			return 0;
		return Integer.parseInt(dDate.substring(0, 4).trim());
	}

	/**
	 * Gets the birt tag.
	 * 
	 * @return birth tag
	 */
	public String getBirtTag() {
		return this.birtTag;
	}

	/**
	 * Gets the birt place.
	 * 
	 * @return birt place
	 */
	public String getBirtPlace() {
		return this.bPlace;
	}

	/**
	 * Gets the birth country.
	 * 
	 * @return birth country
	 */
	public String getBirthCountry() {
		return this.bCountry;
	}

	/**
	 * Gets the deat date.
	 * 
	 * @return death data main part
	 */
	public String getDeatDate() {
		return this.dDate;
	}

	/**
	 * Gets the deat tag.
	 * 
	 * @return deat tag (DEAT, BURI, null)
	 */
	public String getDeatTag() {
		return this.deatTag;
	}

	/**
	 * Gets the deat place.
	 * 
	 * @return death place
	 */
	public String getDeatPlace() {
		return this.dPlace;
	}

	/**
	 * Gets the deat country.
	 * 
	 * @return death country
	 */
	public String getDeatCountry() {
		return this.dCountry;
	}

	/**
	 * Gets the chr date.
	 * 
	 * @return christianed
	 */
	public String getChrDate() {
		return this.chred;
	}

	/**
	 * Gets the buried date.
	 * 
	 * @return buried
	 */
	public String getBuriedDate() {
		return this.buried;
	}

	/**
	 * Gets the occupation.
	 * 
	 * @return occupation
	 */
	public String getOccupation() {
		return this.occu;
	}

	/**
	 * Gets the marr count.
	 * 
	 * @return # of marriages
	 */
	public int getMarrCount() {
		return this.marrCount;
	}

	/**
	 * Gets the child count.
	 * 
	 * @return # of children
	 */
	public int getChildCount() {
		return this.childCount;
	}

	/**
	 * Gets the pare count.
	 * 
	 * @return # of parents
	 */
	public int getPareCount() {
		return this.pareCount;
	}

	/**
	 * Gets the todo.
	 * 
	 * @return true if To do notice exists
	 */
	public boolean getUnkn() {
		return this.hasUnkn;
	}

	/**
	 * Gets the media title.
	 * 
	 * @return media title
	 */
	public String getMediaTitle() {
		return this.mediaTitle;
	}

	/**
	 * Gets the media filename.
	 * 
	 * @return media filename
	 */
	public String getMediaFilename() {
		return this.mediaFilename;
	}

	/**
	 * Gets the media data notice.
	 * 
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
	 * Checks for image.
	 * 
	 * @return true if person has image
	 */
	public boolean hasImage() {
		if (this.imageData == null)
			return false;
		return true;
	}

	/**
	 * Gets the image.
	 * 
	 * @return image
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
	 * Gets the image name.
	 * 
	 * @return image namefield
	 */
	public String getImageName() {
		return this.imageName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// return "id="+this.pid + "/name=" + this.givenname + " " +
		// this.surname + "/bd=" + this.bDate;
		// }
		StringBuilder sb = new StringBuilder();
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
	 * Gets the header.
	 * 
	 * @return used for copy to clipbpard
	 */
	public String getHeader() {
		StringBuilder sb = new StringBuilder();
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
	 * Gets the text name.
	 * 
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
	 * Gets the alfa name.
	 * 
	 * @return for sorting order w/o patronyme
	 */
	public String getAlfaName() {
		return getAlfaName(true);
	}

	/**
	 * Gets the alfa name.
	 * 
	 * @param withPatronyme
	 *            the with patronyme
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

	/** dataflavour for drag-and-drop. */
	public static final DataFlavor[] df = { new DataFlavor(
			PersonShortData.class, "PersonShortData") };

	/**
	 * Gets the person short data flavour.
	 * 
	 * @return dataflavour for drag-and-drop
	 */
	public static DataFlavor getPersonShortDataFlavour() {
		return df[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer
	 * .DataFlavor)
	 */
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {

		if (flavor.equals(df[0])) {
			return this;
		}
		return this.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {

		return df;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.
	 * datatransfer.DataFlavor)
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {

		if (flavor.equals(df[0])) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the drag source.
	 * 
	 * @param dragSource
	 *            the new drag source
	 */
	public void setDragSource(Utils.PersonSource dragSource) {
		this.dragSource = dragSource;
	}

	/**
	 * Gets the drag source.
	 * 
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

	/** collator according to language. */
	public static Collator fiCollator = Collator.getInstance(new Locale(
			Resurses.getLanguage()));

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
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
	 * Sets the adopted.
	 * 
	 * @param adopted
	 *            the new adopted
	 */
	public void setAdopted(String adopted) {
		this.adopted = adopted;
	}

	/**
	 * Gets the adopted.
	 * 
	 * @return adopted status
	 */
	public String getAdopted() {
		return adopted;
	}

	/**
	 * Sets the surety.
	 * 
	 * @param surety
	 *            the new surety
	 */
	public void setSurety(int surety) {
		this.surety = surety;
	}

	/**
	 * Gets the surety.
	 * 
	 * @return the surety
	 */
	public int getSurety() {
		return surety;
	}

	private class ShortName implements Serializable {

		/**  */
		private static final long serialVersionUID = 1L;
		private final String shGivenname;
		private final String shPatronyme;
		private final String shPrefix;
		private final String shSurname;
		private final String shPostfix;

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
