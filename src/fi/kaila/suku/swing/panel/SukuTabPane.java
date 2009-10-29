package fi.kaila.suku.swing.panel;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JScrollPane;

import fi.kaila.suku.util.Resurses;

/**
 * A container for the contents of each tab on the right hand side
 * 
 * @author Kalle
 * 
 */
public class SukuTabPane extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String title;
	Icon icon = null;
	Component pnl;
	String tip;

	SukuTabPane(String title, Component pnl) {
		super(pnl);
		this.title = Resurses.getString(title);
		this.pnl = pnl;
		this.tip = Resurses.getString(title + "_TIP");
	}

	SukuTabPane(String title, Component pnl, String tip) {
		super(pnl);
		this.title = title;
		this.pnl = pnl;
		this.tip = tip;
	}

	/**
	 * @return pid of pane
	 */
	public int getPid() {
		if (pnl instanceof PersonMainPane) {
			return ((PersonMainPane) pnl).getPersonPid();
		}
		return 0;
	}
}
