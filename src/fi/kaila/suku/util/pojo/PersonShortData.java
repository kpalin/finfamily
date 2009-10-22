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
import java.util.HashMap;

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
	private static final long serialVersionUID = -7101500237429825332L;

	private int pid = 0;
	private String refn = null;
	private String sex = null;
	private String group = null;
	private String nameTag = null;
	private String givenname = null;
	private String patronym = null;
	private String prefix = null;
	private String morenames = null;
	private String surname = null;
	private String postfix = null;
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
	private BufferedImage image = null;
	private String imageName = null;
	private Utils.PersonSource dragSource = null;

	private HashMap<String, String> tagMap = new HashMap<String, String>();
	/**
	 * Used by Relatives pane
	 */
	private int parentPid = 0;

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

	public int getGraphRowCount() {
		int count = 1;

		if (occu != null || dDate != null || dPlace != null) {
			count++;
		}
		return count;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setRefn(String refn) {
		this.refn = refn;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setNameTag(String nameTag) {
		this.nameTag = nameTag;
	}

	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}

	public void setPatronym(String patronym) {
		this.patronym = patronym;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}

	public void setMorenames(String morenames) {
		this.morenames = morenames;
	}

	public void setBirtDate(String birtDate) {
		this.bDate = birtDate;
	}

	public void setBirtPlace(String birtPlace) {
		this.bPlace = birtPlace;
	}

	public void setBirtTag(String birtTag) {
		this.birtTag = birtTag;
	}

	public void setDeatDate(String deatDate) {
		this.dDate = deatDate;
	}

	public void setDeatPlace(String deatPlace) {
		this.dPlace = deatPlace;
	}

	public void setDeatTag(String deatTag) {
		this.deatTag = deatTag;
	}

	public void setOccupation(String occu) {
		this.occu = occu;
	}

	public void setTodo(boolean hasTodo) {
		this.hasTodo = hasTodo;
	}

	public void setMarrCount(int marrCount) {
		this.marrCount = marrCount;
	}

	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}

	public void setPareCount(int pareCount) {
		this.pareCount = pareCount;
	}

	public void setMediaTitle(String mediaTitle) {
		this.mediaTitle = mediaTitle;
	}

	public void setMediaFilename(String mediaFilename) {
		this.mediaFilename = mediaFilename;
	}

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

	public PersonShortData() {

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
		for (int i = 0; i < lon.getNotices().length; i++) {
			UnitNotice n = lon.getNotices()[i];
			if (n.getTag().equals("NAME")) {
				if (nameTag == null) {
					nameTag = n.getTag();
					givenname = n.getGivenname();
					patronym = n.getPatronym();
					prefix = n.getPrefix();
					surname = n.getSurname();
					postfix = n.getPostfix();
				}
				if (morenames == null) {
					morenames = n.getSurname();
				} else {
					morenames += ";";
					morenames += n.getSurname();
				}
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

		this.givenname = this.patronym = this.prefix = this.surname = this.postfix = null;

		StringBuffer sql = new StringBuffer();
		sql
				.append("select u.sex,u.userrefn,u.groupid,u.tag,n.tag,n.givenname,");
		sql.append("n.patronym,n.prefix,n.surname,n.postfix,");
		sql
				.append("n.fromdate,n.Place,n.Description,n.pnid,n.mediadata,n.mediafilename,n.mediatitle ");
		sql
				.append("from unit as u left join unitnotice as n on u.pid = n.pid ");
		if (!withAllTags) {
			sql
					.append("and n.tag in ('BIRT','DEAT','CHR','BURI','NAME','PHOT','OCCU') ");
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
					if (this.nameTag == null && "NAME".equals(tag)
							&& this.givenname == null && this.patronym == null
							&& this.prefix == null && this.surname == null
							&& this.postfix == null) {
						this.givenname = rs.getString(6);
						this.patronym = rs.getString(7);
						this.prefix = rs.getString(8);
						this.surname = rs.getString(9);
						this.postfix = rs.getString(10);
						this.nameTag = tag;
					} else if (this.nameTag != null && "NAME".equals(tag)) {
						String restname = rs.getString(9);
						if (restname != null) {
							if (this.morenames == null) {
								this.morenames = restname;
							} else {
								this.morenames += ";";
								this.morenames += restname;
							}
						}
					}

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

	public String getNameTag() {
		return this.nameTag;
	}

	/**
	 * @return givenname
	 */
	public String getGivenname() {
		return this.givenname;
	}

	/**
	 * @return getter for patronym
	 */
	public String getPatronym() {
		return this.patronym;
	}

	/**
	 * @return name prefix
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * @return surname
	 */
	public String getSurname() {
		return this.surname;
	}

	/**
	 * @return name postfix
	 */
	public String getPostfix() {
		return this.postfix;
	}

	/**
	 * @return morenames
	 */
	public String getMorenames() {
		return this.morenames;
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

	public String getBirtTag() {
		return this.birtTag;
	}

	public String getBirtPlace() {
		return this.bPlace;
	}

	/**
	 * @return death data main part
	 */
	public String getDeatDate() {
		return this.dDate;
	}

	public String getDeatTag() {
		return this.deatTag;
	}

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

	public String getMediaTitle() {
		return this.mediaTitle;
	}

	public String getMediaFilename() {
		return this.mediaFilename;
	}

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

	public String getTextName() {

		StringBuilder sb = new StringBuilder();
		if (this.givenname != null) {
			sb.append(this.givenname);
		}
		if (this.prefix != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(this.prefix);
		}
		if (this.surname != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(this.surname);
		}
		if (this.postfix != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(this.postfix);
		}
		return sb.toString();
	}

	public String getAlfaName() {
		return getAlfaName(true);
	}

	public String getAlfaName(boolean withPatronyme) {

		StringBuilder sb = new StringBuilder();

		if (this.prefix != null) {
			sb.append(this.prefix);
		}

		if (this.surname != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(this.surname);
		}
		if (this.givenname != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(this.givenname);
		}
		if (withPatronyme) {
			if (patronym != null) {
				if (sb.length() > 0)
					sb.append(" ");
				sb.append(this.patronym);
			}
		}

		if (this.postfix != null) {
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(this.postfix);
		}
		return sb.toString();
	}

	public static final DataFlavor[] df = { new DataFlavor(
			PersonShortData.class, "PersonShortData") };

	// DataFlavor.stringFlavor};

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

	public void setDragSource(Utils.PersonSource dragSource) {
		this.dragSource = dragSource;
	}

	public Utils.PersonSource getDragSource() {
		return dragSource;
	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

	@Override
	public int compareTo(PersonShortData o) {
		int cc = (nv(getSurname()).compareTo(nv(o.getSurname())));
		if (cc != 0)
			return cc;
		cc = (nv(getGivenname()).compareTo(nv(o.getGivenname())));
		if (cc != 0)
			return cc;
		return (nv(getBirtDate()).compareTo(nv(o.getBirtDate())));

	}

}
