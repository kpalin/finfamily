package fi.kaila.suku.util.pojo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Vector;

import javax.imageio.ImageIO;

import fi.kaila.suku.swing.util.ISukuGraphicalItem;
import fi.kaila.suku.util.Utils;

/**
 * Container for the table (family).
 * 
 * @author fikaakail
 */
public class TableShortData implements Serializable, ISukuGraphicalItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Vector<PersonShortData> famMember = new Vector<PersonShortData>();
	private final Vector<RelationShortData> famMemRel = new Vector<RelationShortData>();

	private final Vector<PersonRectangle> personAreas = new Vector<PersonRectangle>();

	private int spouseIdx = 1;
	private int spouseCount = 0;

	private int childIdx = 1;
	private int childCount = 0;

	private int fatherIdx = 1;
	private int fatherCount = 0;

	private int motherIdx = 1;
	private int motherCount = 0;

	private final Dimension imageSize = new Dimension(70, 100);
	private final int separatorHeight = 5;
	private final int separatorWidth = 5;
	private final int headerHeight = 15;
	private final int rowHeight = 15;

	private final Font textFont = new Font("Serif", Font.PLAIN, 14);
	// private FontMetrics fm = null;// g.getFontMetrics();
	private int xdate = 0;
	private int xdatew = 0;

	private Rectangle currentArea = null;

	private Point p = new Point();

	/**
	 * Gets the area.
	 * 
	 * @return the area for the table
	 */
	public Rectangle getArea() {
		return currentArea;
	}

	/**
	 * Sets the location.
	 * 
	 * @param p
	 *            the new location
	 */
	public void setLocation(Point p) {
		this.p = p;
		// p.x += 10;
		// p.y += 10;
	}

	/**
	 * Exists person.
	 * 
	 * @param pid
	 *            person pid to check
	 * @return true if person exists in table
	 */
	public boolean existsPerson(int pid) {
		for (PersonShortData p : famMember) {
			if (p.getPid() == pid) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Draw me.
	 * 
	 * @param g
	 *            the g
	 * @param color
	 *            the color
	 */
	public void drawMe(Graphics g, Color color) {
		if (famMember.size() == 1) {
			drawSubject(g, color);
		} else {
			drawTable(g, color);
		}
	}

	private void drawSubject(Graphics g, Color color) {
		g.setFont(textFont);
		personAreas.clear();

		Dimension d = getSize(g);

		currentArea = new Rectangle(p.x, p.y, d.width, d.height);
		int xmargin = 10;
		int y = p.y;
		PersonShortData person = famMember.get(0);

		g.setColor(Color.white);

		g.fillRect(p.x, p.y, d.width + xmargin, d.height);
		g.setColor(color);
		g.drawRect(p.x, p.y, d.width + xmargin, d.height);
		if (person.getPareCount() > 0) {
			int rx = p.x + (d.width + xmargin) / 2;
			g.setColor(Color.lightGray);
			g.drawLine(rx - 20, p.y, rx + 20, p.y);
		}
		g.setColor(Color.black);
		y += headerHeight;
		g.drawLine(p.x, y, p.x + d.width, y);
		BufferedImage img;
		int imgx;
		PersonRectangle pr;
		pr = new PersonRectangle();
		pr.person = person;
		pr.rectangle = new Rectangle(0, 0, d.width, d.height);
		personAreas.add(pr);

		int imgc = (person.getMediaFilename() == null) ? 0 : 1;
		if (imgc > 0) {
			y += separatorHeight;
			imgx = p.x + separatorWidth;
			img = person.getImage();

			if (img != null) {
				float yh = img.getHeight();
				float xw = img.getWidth();
				if (xw > imageSize.width) {
					yh = yh * imageSize.width / xw;
					xw = xw * imageSize.width / xw;
				}
				if (yh > imageSize.height) {
					xw = xw * imageSize.height / yh;
					yh = yh * imageSize.height / yh;
				}

				g.drawImage(img, imgx, y, (int) xw, (int) yh, Color.red, null);

			}

			y += imageSize.height;
			y += headerHeight;
		}
		g.drawLine(p.x, y, p.x + d.width, y);

		y += rowHeight;

		g.drawString(person.getTextName(), p.x, y);
		y += rowHeight;
		if (person.getOccupation() != null) {
			g.drawString(person.getOccupation(), p.x, y);
			y += rowHeight;
		}
		if (person.getBirtPlace() != null) {
			g.drawString("  " + person.getBirtPlace(), p.x + xdatew, y);
		}

		if (person.getBirtDate() != null) {
			g.drawString("* " + Utils.textDate(person.getBirtDate(), false),
					p.x, y);
		}
		if (person.getBirtDate() != null || person.getBirtPlace() != null) {
			y += rowHeight;
		}
		if (person.getDeatPlace() != null) {
			g.drawString("  " + person.getDeatPlace(), p.x + xdatew, y);
		}

		if (person.getDeatDate() != null) {
			g.drawString("† " + Utils.textDate(person.getDeatDate(), false),
					p.x, y);
		}

		if (person.getDeatDate() != null || person.getDeatPlace() != null) {
			y += rowHeight;
		}

	}

	private void drawTable(Graphics g, Color color) {

		if (famMember.size() == 0)
			return;
		g.setFont(textFont);
		personAreas.clear();
		Dimension d = getSize(g);

		int sujet = 0;

		FontMetrics fm = g.getFontMetrics(textFont);

		int datew = fm.stringWidth(" *13.04.1944"); // date width

		currentArea = new Rectangle(p.x, p.y, d.width, d.height);
		int xmargin = 25;
		int y = p.y;
		PersonShortData person;

		g.setColor(Color.white);

		g.fillRect(p.x, p.y, d.width + xmargin, d.height);
		g.setColor(color);
		g.drawRect(p.x, p.y, d.width + xmargin, d.height);
		g.setColor(Color.black);

		y += headerHeight;
		g.drawLine(p.x, y, p.x + d.width, y);
		y += headerHeight;

		BufferedImage img;
		int imgx;
		PersonRectangle pr;
		int imgc = mainImageCount();
		if (imgc > 0) {
			y += separatorHeight;
			imgx = p.x + separatorWidth;

			for (int i = 0; i < spouseIdx + spouseCount; i++) {
				person = famMember.get(i);
				img = person.getImage();

				if (img != null) {
					float yh = img.getHeight();
					float xw = img.getWidth();
					if (xw > imageSize.width) {
						yh = yh * imageSize.width / xw;
						xw = xw * imageSize.width / xw;
					}
					if (yh > imageSize.height) {
						xw = xw * imageSize.height / yh;
						yh = yh * imageSize.height / yh;
					}

					g.drawImage(img, imgx, y, (int) xw, (int) yh, Color.red,
							null);

					pr = new PersonRectangle();
					pr.person = person;
					pr.rectangle = new Rectangle(imgx - p.x, y - p.y, (int) xw,
							(int) yh);
					personAreas.add(pr);

					imgx += imageSize.width + separatorWidth;
				}

			}
			y += imageSize.height;
			y += headerHeight;

			g.drawLine(p.x, y, p.x + d.width, y);
			y += rowHeight;
		}

		person = famMember.get(0);
		sujet = person.getPid();
		pr = new PersonRectangle();
		pr.person = person;
		pr.rectangle = new Rectangle(0, y - p.y - rowHeight, d.width,
				person.getGraphRowCount() * rowHeight);
		personAreas.add(pr);

		g.drawString(person.getTextName(), p.x, y);

		if (person.getBirtDate() != null) {
			g.drawString("* " + Utils.textDate(person.getBirtDate(), false),
					p.x + xdate, y);
		}
		if (person.getBirtPlace() != null) {
			g.drawString("  " + person.getBirtPlace(), p.x + xdate + datew, y);
		}
		if (person.getDeatDate() != null) {

			g.drawString("† " + Utils.textDate(person.getDeatDate(), false),
					p.x + xdate, y + rowHeight);
		}
		if (person.getDeatPlace() != null) {
			g.drawString("  " + person.getDeatPlace(), p.x + xdate + datew
					+ xmargin, y + rowHeight);
		}
		if (person.getOccupation() != null) {

			g.drawString(person.getOccupation(), p.x, y + rowHeight);
		}
		y += person.getGraphRowCount() * rowHeight;

		for (int i = spouseIdx; i < spouseIdx + spouseCount; i++) {
			person = famMember.get(i);
			g.drawString("" + i, p.x, y);
			if (person.getSurety() < 100) {
				drawSurety(g, "" + (person.getSurety() / 20), p.x + 10, y);
				// g.drawString("?", p.x + 10, y);
			}
			g.drawString(person.getTextName(), p.x + xmargin, y);
			pr = new PersonRectangle();
			pr.person = person;
			pr.rectangle = new Rectangle(0, y - p.y - rowHeight, d.width,
					person.getGraphRowCount() * rowHeight);
			personAreas.add(pr);

			if (person.getBirtDate() != null) {
				g.drawString(
						"* " + Utils.textDate(person.getBirtDate(), false), p.x
								+ xdate, y);
			}
			if (person.getBirtPlace() != null) {
				g.drawString("  " + person.getBirtPlace(), p.x + xdate + datew,
						y);
			}
			if (person.getDeatDate() != null) {
				g.drawString(
						"† " + Utils.textDate(person.getDeatDate(), false), p.x
								+ xdate, y + rowHeight);
			}
			if (person.getDeatPlace() != null) {
				g.drawString("  " + person.getDeatPlace(), p.x + xdate + datew,
						y + rowHeight);
			}
			if (person.getOccupation() != null) {
				g.drawString(person.getOccupation(), p.x + xmargin, y
						+ rowHeight);
			}
			y += person.getGraphRowCount() * rowHeight;
		}
		g.drawLine(p.x, y, p.x + d.width, y);
		y += rowHeight;
		if (childImageCount() > 0) {
			imgx = p.x + separatorWidth;

			for (int i = childIdx; i < childIdx + childCount; i++) {
				person = famMember.get(i);
				img = person.getImage();

				if (img != null) {
					float yh = img.getHeight();
					float xw = img.getWidth();
					if (xw > imageSize.width) {
						yh = yh * imageSize.width / xw;
						xw = xw * imageSize.width / xw;
					}
					if (yh > imageSize.height) {
						xw = xw * imageSize.height / yh;
						yh = yh * imageSize.height / yh;
					}

					g.drawImage(img, imgx, y, (int) xw, (int) yh, Color.red,
							null);
					pr = new PersonRectangle();
					pr.person = person;
					pr.rectangle = new Rectangle(imgx - p.x, y - p.y, (int) xw,
							(int) yh);
					personAreas.add(pr);

					imgx += imageSize.width + separatorWidth;
				}
			}

			y += imageSize.height;
			y += headerHeight;
		}

		for (int i = childIdx; i < childIdx + childCount; i++) {
			person = famMember.get(i);
			g.drawString(person.getTextName(), p.x + xmargin, y);
			RelationShortData rr = famMemRel.get(i);
			int[] rrr = rr.getParentArray();
			// StringBuilder sb = new StringBuilder();
			int pareNo = 0;
			if (rrr != null) {
				for (int j = 0; j < rrr.length; j++) {
					if (rrr[j] != sujet) {
						for (int k = spouseIdx; k < spouseIdx + spouseCount; k++) {
							if (famMember.get(k).getPid() == rrr[j]) {
								pareNo = k;
								break;
							}
						}
					}
				}
				if (pareNo > 0) {
					g.drawString("" + pareNo, p.x, y);
				}
			}

			pr = new PersonRectangle();
			pr.person = person;
			String extra = "";
			if (pr.person.getAdopted() != null) {
				extra += "A";
			}
			if (pr.person.getSurety() < 100) {
				extra += (pr.person.getSurety() / 20);
			}

			if (!extra.isEmpty()) {
				drawSurety(g, extra, p.x + 10, y);
				// g.drawString(extra, p.x + 10, y);
			}
			pr.rectangle = new Rectangle(0, y - p.y - rowHeight, d.width,
					person.getGraphRowCount() * rowHeight);
			personAreas.add(pr);
			if (person.getBirtDate() != null) {
				g.drawString(
						"* " + Utils.textDate(person.getBirtDate(), false), p.x
								+ xdate, y);
			}
			if (person.getBirtPlace() != null) {
				g.drawString("  " + person.getBirtPlace(), p.x + xdate + datew,
						y);
			}
			if (person.getDeatDate() != null) {
				g.drawString(
						"† " + Utils.textDate(person.getDeatDate(), false), p.x
								+ xdate, y + rowHeight);
			}
			if (person.getDeatPlace() != null) {
				g.drawString("  " + person.getDeatPlace(), p.x + xdate + datew,
						y + rowHeight);
			}
			if (person.getChildCount() > 0) {

				int rx = currentArea.x + currentArea.width + xmargin;
				int ry = y - rowHeight / 2;
				Color colo = g.getColor();
				g.setColor(person.getSex().equals("M") ? Color.blue : Color.red);
				g.drawLine(rx, ry, rx + 10, ry);
				g.setColor(colo);

			}

			// if (person.getSurety() != 100) {
			// g.drawString("SU:" + person.getSurety(), p.x + xmargin, y
			// + rowHeight);
			// } else
			if (person.getOccupation() != null) {
				g.drawString(person.getOccupation(), p.x + xmargin, y
						+ rowHeight);
			}
			y += person.getGraphRowCount() * rowHeight;
		}

	}

	private void drawSurety(Graphics g, String suretyCode, int x, int y) {
		if (suretyCode != null) {
			String imgLocation = "/images/rela" + suretyCode + ".jpg";
			InputStream in = null;

			byte imbytes[] = new byte[2048];
			BufferedImage img = null;
			try {
				in = this.getClass().getResourceAsStream(imgLocation);
				if (in != null) {
					in.read(imbytes);
					ByteArrayInputStream bb = new ByteArrayInputStream(imbytes);
					img = ImageIO.read(bb);
					g.drawImage(img, x, y - img.getHeight(), null);
				}
			} catch (IOException e) {

				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ignored) {
						// Exception ignored
					}
				}

			}

		}
	}

	/**
	 * Gets the size.
	 * 
	 * @param g
	 *            the g
	 * @return size of table
	 */
	public Dimension getSize(Graphics g) {
		if (famMember.size() == 1) {
			return getPersonSize(g);
		} else {
			return getTableSize(g);
		}
	}

	private Dimension getPersonSize(Graphics g) {
		int height = headerHeight;
		int imagew = 0;
		PersonShortData person = famMember.get(0);
		if (person.getMediaFilename() != null) {
			imagew = 1;
		}

		if (imagew > 0) {
			height += imageSize.height;
			height += headerHeight;
			imagew = imagew * (imageSize.width + separatorWidth);
		}
		height += rowHeight; // name
		if (person.getOccupation() != null) {
			height += rowHeight;
		}

		height += getSubjectRows() * rowHeight;
		if (person.getBirtDate() != null || person.getBirtPlace() != null) {
			height += rowHeight;
		}

		if (person.getDeatDate() != null || person.getDeatPlace() != null) {
			height += rowHeight;
		}

		xdate = 0;
		xdatew = 0;
		FontMetrics fm = g.getFontMetrics(textFont);
		int namew = 0;
		int occuw = 0;
		int birtw = 0;
		int deatw = 0;
		xdatew = fm.stringWidth(" *13.04.1944    "); // date width
		int datemargin = fm.stringWidth("    ");
		int placew = 0;
		namew = fm.stringWidth(person.getTextName());
		String xx;
		xx = person.getOccupation();
		if (xx != null) {
			occuw = fm.stringWidth(xx);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("* ");
		if (person.getBirtDate() != null) {
			sb.append(" * 13.04.1944 ");
		}
		if (person.getBirtPlace() != null) {
			placew = fm.stringWidth(person.getBirtPlace());
			if (placew > xdate)
				xdate = placew;
			sb.append(person.getBirtPlace());
		}
		birtw = fm.stringWidth(sb.toString());
		if (birtw > 0 && birtw < xdatew + placew) {
			birtw = xdatew + placew;
		}
		sb = new StringBuilder();
		sb.append("* ");
		if (person.getDeatDate() != null) {
			sb.append(" * 13.04.1944 ");
		}
		if (person.getDeatPlace() != null) {
			placew = fm.stringWidth(person.getDeatPlace());
			if (placew > xdate)
				xdate = placew;

			sb.append(person.getDeatPlace());
		}
		deatw = fm.stringWidth(sb.toString());
		if (deatw > 0 && deatw < xdatew + placew) {
			deatw = xdatew + placew;
		}
		xdate += datemargin;

		if (occuw > namew)
			namew = occuw;
		imagew += separatorWidth;
		if (birtw > namew)
			namew = birtw;
		if (deatw > namew)
			namew = deatw;

		return new Dimension(namew, height);
	}

	private Dimension getTableSize(Graphics g) {
		int xmargin = 25;
		int height = headerHeight * 2;
		int imagew = mainImageCount();
		if (imagew > 0) {
			height += imageSize.height;
			height += headerHeight;
			imagew = imagew * (imageSize.width + separatorWidth);

		}
		height += getSubjectRows() * rowHeight;
		height += getSpouseRows() * rowHeight;
		if (getChildCount() > 0) {
			height += separatorHeight;
			int childic = childImageCount();
			if (childic > 0) {
				height += imageSize.height;
				height += headerHeight;
				childic = childic * (imageSize.width + separatorWidth);
				if (childic > imagew)
					imagew = childic;
			}
			height += getChildRows() * rowHeight;
		}
		height += rowHeight + separatorHeight;

		int datew = 0;
		FontMetrics fm = g.getFontMetrics(textFont);
		int namew = 0;
		int occuw = 0;
		datew = fm.stringWidth(" *13.04.1944"); // date width

		for (int i = 0; i < childIdx + childCount; i++) {
			PersonShortData person = famMember.get(i);
			String txt = person.getAlfaName();
			if (txt == null) {
				txt = "";
			}
			int xx = fm.stringWidth(txt);
			if (xx > namew) {
				namew = xx;
			}
			txt = person.getOccupation();
			if (txt == null)
				txt = "";
			xx = fm.stringWidth(txt);
			if (xx > occuw)
				occuw = xx;
		}

		if (occuw > namew) {
			namew = occuw;
		}
		imagew += separatorWidth;
		xdate = namew + xmargin;

		if (namew + datew < imagew) {
			namew = imagew;
		} else {
			namew = namew + datew;
		}

		namew += datew;
		return new Dimension(namew, height);

	}

	/**
	 * Main image count.
	 * 
	 * @return count of images for parents
	 */
	public int mainImageCount() {
		int resu = 0;
		for (int i = 0; i < spouseIdx + spouseCount; i++) {

			if (famMember.get(i).getMediaFilename() != null) {
				resu++;
			}
		}
		return resu;
	}

	/**
	 * Child image count.
	 * 
	 * @return count of child images
	 */
	public int childImageCount() {
		int resu = 0;

		for (int i = childIdx; i < childIdx + childCount; i++) {
			if (famMember.get(i).getMediaFilename() != null) {
				resu++;
			}
		}
		return resu;
	}

	/**
	 * Gets the subject rows.
	 * 
	 * @return no of rows for subject
	 */
	public int getSubjectRows() {
		return famMember.get(0).getGraphRowCount();
	}

	/**
	 * Gets the spouse rows.
	 * 
	 * @return no of rows for spouses
	 */
	public int getSpouseRows() {
		int i = 0;
		for (int j = spouseIdx; j < spouseIdx + spouseCount; j++) {
			i += famMember.get(j).getGraphRowCount();
		}
		return i;
	}

	/**
	 * Gets the child rows.
	 * 
	 * @return no of rows for children
	 */
	public int getChildRows() {
		int i = 0;
		for (int j = childIdx; j < childIdx + childCount; j++) {
			i += famMember.get(j).getGraphRowCount();
		}
		return i;
	}

	/**
	 * Sets the subject.
	 * 
	 * @param subject
	 *            the new subject
	 */
	public void setSubject(PersonShortData subject) {
		famMember.clear();
		famMemRel.clear();
		famMember.add(subject);
		famMemRel.add(new RelationShortData(0, 0, 0, null, 0));
		spouseIdx = 1;
		spouseCount = 0;
		childIdx = 1;
		childCount = 0;
		fatherIdx = 1;
		fatherCount = 0;
		motherIdx = 1;
		motherCount = 0;

	}

	/**
	 * Gets the subject.
	 * 
	 * @return teh subject
	 */
	public PersonShortData getSubject() {
		if (famMember.size() == 0)
			return null;
		return famMember.get(0);
	}

	/**
	 * Gets the child count.
	 * 
	 * @return count of children
	 */
	public int getChildCount() {
		return childCount;
	}

	/**
	 * Gets the child.
	 * 
	 * @param index
	 *            the index
	 * @return child at index
	 */
	public PersonShortData getChild(int index) {
		if (index < childCount) {
			return famMember.get(childIdx + index);
		}
		return null;
	}

	/**
	 * Gets the spouse count.
	 * 
	 * @return no of spouses
	 */
	public int getSpouseCount() {
		return spouseCount;
	}

	/**
	 * Gets the spouse.
	 * 
	 * @param index
	 *            the index
	 * @return spouse at index
	 */
	public PersonShortData getSpouse(int index) {
		if (index < spouseCount) {
			return famMember.get(spouseIdx + index);
		}
		return null;
	}

	/**
	 * Gets the father count.
	 * 
	 * @return count of fathers
	 */
	public int getFatherCount() {
		return fatherCount;
	}

	/**
	 * Gets the father.
	 * 
	 * @param index
	 *            the index
	 * @return fatehr at index
	 */
	public PersonShortData getFather(int index) {
		if (index < fatherCount) {
			return famMember.get(fatherIdx + index);
		}
		return null;
	}

	/**
	 * Gets the mother count.
	 * 
	 * @return count of mothers
	 */
	public int getMotherCount() {
		return motherCount;
	}

	/**
	 * Gets the mother.
	 * 
	 * @param index
	 *            the index
	 * @return mother at index
	 */
	public PersonShortData getMother(int index) {
		if (index < motherCount) {
			return famMember.get(motherIdx + index);
		}
		return null;
	}

	/**
	 * init structure.
	 * 
	 * @param pers
	 *            the pers
	 * @param rels
	 *            the rels
	 */
	public void initRelatives(PersonShortData[] pers, RelationShortData[] rels) {

		Vector<PersonShortData> spouVec = new Vector<PersonShortData>();
		Vector<RelationShortData> spouRelVec = new Vector<RelationShortData>();

		Vector<PersonShortData> chilVec = new Vector<PersonShortData>();
		Vector<RelationShortData> chilRelVec = new Vector<RelationShortData>();

		Vector<PersonShortData> fathVec = new Vector<PersonShortData>();
		Vector<RelationShortData> fathRelVec = new Vector<RelationShortData>();

		Vector<PersonShortData> mothVec = new Vector<PersonShortData>();
		Vector<RelationShortData> mothRelVec = new Vector<RelationShortData>();

		RelationShortData reld;
		PersonShortData perd;
		for (int i = 0; i < rels.length; i++) {

			reld = rels[i];
			int relid = reld.getRelationPid();

			if (reld.getTag().equals("WIFE") || reld.getTag().equals("HUSB")) {

				for (int j = 0; j < pers.length; j++) {
					perd = pers[j];
					if (relid == perd.getPid()) {
						perd.setSurety(reld.surety);
						perd.setAdopted(reld.getAdopted());

						spouVec.add(perd);
						spouRelVec.add(reld);
						break;
					}
				}
			} else if (reld.getTag().equals("CHIL")) {

				for (int j = 0; j < pers.length; j++) {
					perd = pers[j];
					if (relid == perd.getPid()) {
						perd.setSurety(reld.surety);
						perd.setAdopted(reld.getAdopted());
						chilVec.add(perd);
						chilRelVec.add(reld);
						break;
					}
				}
			} else if (reld.getTag().equals("FATH")) {

				for (int j = 0; j < pers.length; j++) {
					perd = pers[j];
					if (relid == perd.getPid()) {
						perd.setSurety(reld.surety);
						perd.setAdopted(reld.getAdopted());
						fathVec.add(perd);
						fathRelVec.add(reld);
						break;
					}
				}
			} else if (reld.getTag().equals("MOTH")) {

				for (int j = 0; j < pers.length; j++) {
					perd = pers[j];
					if (relid == perd.getPid()) {
						perd.setSurety(reld.surety);
						perd.setAdopted(reld.getAdopted());
						mothVec.add(perd);
						mothRelVec.add(reld);
						break;
					}
				}
			}
		}

		famMember.addAll(spouVec);
		famMemRel.addAll(spouRelVec);
		spouseCount = spouVec.size();
		childIdx = famMember.size();

		famMember.addAll(chilVec);
		famMemRel.addAll(chilRelVec);
		fatherIdx = famMember.size();
		childCount = chilVec.size();

		famMember.addAll(fathVec);
		famMemRel.addAll(fathRelVec);
		motherIdx = famMember.size();
		fatherCount = fathVec.size();
		famMember.addAll(mothVec);
		famMemRel.addAll(mothRelVec);
		motherCount = mothVec.size();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < famMember.size(); i++) {
			sb.append("Tab:[" + i + "]: " + famMember.get(i).getTextName()
					+ "\n");

		}
		Rectangle r = getArea();
		if (r == null)
			r = new Rectangle();
		Point p = getLocation();
		if (p == null)
			p = new Point();
		sb.append("x=" + p.x + ",y=" + p.y + ",w=" + r.width + ",h=" + r.height
				+ "\n");

		return sb.toString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fi.kaila.suku.swing.util.ISukuGraphicalItem#getPersonAtPoint(java.awt
	 * .Point)
	 */
	@Override
	public PersonShortData getPersonAtPoint(Point point) {

		for (int i = 0; i < personAreas.size(); i++) {
			PersonRectangle pr = personAreas.get(i);
			if (pr.rectangle.contains(point)) {
				return pr.person;
			}
		}
		return null;
	}

	/**
	 * The Class PersonRectangle.
	 */
	class PersonRectangle {

		/** The person. */
		PersonShortData person;

		/** The rectangle. */
		Rectangle rectangle;
	}

	/**
	 * Gets the location.
	 * 
	 * @return location
	 */
	public Point getLocation() {

		return this.p;
	}
}
