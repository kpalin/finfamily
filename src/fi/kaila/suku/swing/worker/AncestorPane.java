package fi.kaila.suku.swing.worker;

import java.awt.GridLayout;


import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fi.kaila.suku.util.Resurses;

public class AncestorPane extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private ButtonGroup ancestorNumberingFormatGroup=null;
	
	private JCheckBox ancestorShowFamily = null;
	private JTextField ancestorShowDescGen = null;
	
	public AncestorPane (){
		
		
		int rtypx=10;
		int rtypy=10;
		
		setLayout(null);
		
		JPanel pane = new JPanel();
		pane.setBorder(BorderFactory.createTitledBorder(Resurses.getString("REPORT.ANC.NUMBERING")));
		pane.setLayout(new GridLayout(0,1));
		pane.setBounds(rtypx, rtypy, 250, 100);
		
		
		
		ancestorNumberingFormatGroup= new ButtonGroup();
		JRadioButton formd=new JRadioButton(Resurses.getString("REPORT.ANC.NUMBERING.STRADONIZ"));
		formd.setActionCommand(ReportWorkerDialog.SET_ANC_STRADONIZ);
		ancestorNumberingFormatGroup.add(formd);
		pane.add(formd);
		formd=new JRadioButton(Resurses.getString("REPORT.ANC.NUMBERING.HAGER"));
		formd.setActionCommand(ReportWorkerDialog.SET_ANC_HAGER);
		ancestorNumberingFormatGroup.add(formd);
		pane.add(formd);
		formd=new JRadioButton(Resurses.getString("REPORT.ANC.NUMBERING.ESPOLIN"));
		formd.setActionCommand(ReportWorkerDialog.SET_ANC_ESPOLIN);
		ancestorNumberingFormatGroup.add(formd);
		pane.add(formd);
		
		add(pane);
		
		rtypy += 110;
		ancestorShowFamily = new JCheckBox(Resurses.getString("REPORT.ANC.SHOW.FAMILY"));
		ancestorShowFamily.setBounds(rtypx,rtypy,280,20);
		add(ancestorShowFamily);
		rtypy += 22;
		
		JPanel pp = new JPanel();
		pp.setLayout(null);
		pp.setBounds(rtypx,rtypy,300,50);
		
		ancestorShowDescGen = new JTextField();// (NumberFormat.getIntegerInstance());
		ancestorShowDescGen.setText("0");
		ancestorShowDescGen.setBounds(0,0,40,20);
		pp.add(ancestorShowDescGen);
		
	
		JLabel lb = new JLabel(Resurses.getString("REPORT.ANC.SHOW.DESC.GEN"));
		pp.add(lb);
		lb.setBounds(50,0,280,20);
		add(pp);
		

	}
	
	public ButtonGroup getNumberingFormat(){
		return ancestorNumberingFormatGroup;
	}
	
	public boolean getShowfamily(){
		return ancestorShowFamily.isSelected();
	}
	
	
	public void setShowFamily(boolean value) {
		ancestorShowFamily.setSelected(value);
		
	}
	public String getShowDescGen(){
		return ancestorShowDescGen.getText();
	}

	public void setDescGen(String string) {
		ancestorShowDescGen.setText(string);
		
	}
	
}
