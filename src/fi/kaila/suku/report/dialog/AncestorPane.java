package fi.kaila.suku.report.dialog;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

/**
 * 
 * <h1>Ancestor report settings pane</h1>
 * 
 * @author Kalle
 * 
 */
public class AncestorPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ButtonGroup ancestorNumberingFormatGroup = null;

	private JCheckBox ancestorShowFamily = null;
	private JTextField ancestorShowDescGen = null;
	private JTextField generations = null;

	/**
	 * Constructor sets up ths fields
	 */
	public AncestorPane() {

		int rtypx = 10;
		int rtypy = 10;

		setLayout(null);

		JPanel pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(rtypx, rtypy, 200, 20);
		generations = new JTextField();
		generations.setText("" + 99);
		pp.add(generations);
		generations.setBounds(0, 0, 40, 20);
		JLabel lb = new JLabel(Resurses.getString("REPORT.DESC.GENERATIONS"));
		pp.add(lb);
		lb.setBounds(50, 0, 180, 20);
		add(pp);
		rtypy += 24;

		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses
				.getString("REPORT.ANC.NUMBERING")));
		pane.setLayout(new GridLayout(0, 1));
		pane.setBounds(rtypx, rtypy, 250, 100);

		ancestorNumberingFormatGroup = new ButtonGroup();
		JRadioButton formd = new JRadioButton(Resurses
				.getString("REPORT.ANC.NUMBERING.STRADONIZ"));
		formd.setActionCommand(ReportWorkerDialog.SET_ANC_STRADONIZ);
		ancestorNumberingFormatGroup.add(formd);
		formd.setSelected(true);
		pane.add(formd);
		formd = new JRadioButton(Resurses
				.getString("REPORT.ANC.NUMBERING.HAGER"));
		formd.setActionCommand(ReportWorkerDialog.SET_ANC_HAGER);
		ancestorNumberingFormatGroup.add(formd);
		pane.add(formd);
		formd = new JRadioButton(Resurses
				.getString("REPORT.ANC.NUMBERING.ESPOLIN"));
		formd.setActionCommand(ReportWorkerDialog.SET_ANC_ESPOLIN);
		ancestorNumberingFormatGroup.add(formd);
		pane.add(formd);

		add(pane);

		rtypy += 110;
		ancestorShowFamily = new JCheckBox(Resurses
				.getString("REPORT.ANC.SHOW.FAMILY"));
		ancestorShowFamily.setBounds(rtypx, rtypy, 280, 20);
		add(ancestorShowFamily);
		rtypy += 22;
		pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(rtypx, rtypy, 300, 50);

		ancestorShowDescGen = new JTextField();// (NumberFormat.getIntegerInstance());
		ancestorShowDescGen.setText("0");
		ancestorShowDescGen.setVisible(false);
		// TODO decide if this will be implemented
		ancestorShowDescGen.setBounds(0, 0, 40, 20);
		pp.add(ancestorShowDescGen);
		rtypy += 22;
		formd = new JRadioButton(Resurses.getString("REPORT.ANCESTOR.TABLES"));
		formd.setActionCommand(ReportWorkerDialog.SET_ANC_TABLES);
		formd.setBounds(10, rtypy, 200, 20);
		ancestorNumberingFormatGroup.add(formd);
		add(formd);

		// JLabel lb = new
		// JLabel(Resurses.getString("REPORT.ANC.SHOW.DESC.GEN"));
		// pp.add(lb);
		// lb.setBounds(50, 0, 280, 20);
		// add(pp);

	}

	/**
	 * @return no of generations
	 */
	public int getGenerations() {

		int gen = 0;
		try {
			gen = Integer.parseInt(generations.getText());
		} catch (NumberFormatException ne) {
			gen = 99;
			generations.setText("99");
		}
		return gen;
	}

	void setGenerations(String string) {
		generations.setText(string);

	}

	/**
	 * 
	 * Value in the buttomngroup is the getActionCommand()
	 * 
	 * @return the ButtonGroup for numbering of ancestors
	 */
	public ButtonGroup getNumberingFormat() {
		return ancestorNumberingFormatGroup;
	}

	/**
	 * @return true if ancestor family is to be shown also
	 */
	public boolean getShowfamily() {
		return ancestorShowFamily.isSelected();
	}

	/**
	 * @param value
	 *            set true to show also ancestro family
	 */
	public void setShowFamily(boolean value) {
		ancestorShowFamily.setSelected(value);

	}

	/**
	 * TODO decide if this is needed
	 * 
	 * @return no of descendant geneartions to print
	 */
	public String getShowDescGen() {
		return ancestorShowDescGen.getText();
	}

	/**
	 * TODO decide if this is needed
	 * 
	 * @param string
	 *            no of descendants generations for the ancestor
	 */
	public void setDescGen(String string) {
		ancestorShowDescGen.setText(string);

	}

}
