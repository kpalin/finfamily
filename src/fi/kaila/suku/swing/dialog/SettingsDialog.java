package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;

public class SettingsDialog extends JDialog implements ActionListener {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Logger logger= Logger.getLogger(this.getName());

	private JComboBox loca=null;
	private JComboBox repolang=null;
	private JComboBox dateFormat=null;
	private JButton ok;
	
	
//	private static final String []locatexts = {"Suomi - Finnish","Svenska - Swedish","English"};
//	private static final String []locas = {"fi","sv","en"};
	
	private String [] locatexts=null;
	private String [] locas=null;
	private String [] dateFormats=null;
	private String [] dateCodes=null;
	private int dateIndex=0;
	private int locaIndex=0;
	private JFrame owner=null;
	public SettingsDialog (JFrame owner){
		this.owner = owner;
		setLayout(null);
		int x = 20;
		int y = 20;
		locatexts = Resurses.getString("LOCALIZAT_TEXTS").split(";");
		locas = Resurses.getString("LOCALIZAT_CODES").split(";");
		dateFormats = Resurses.getString("LOCALIZAT_DATEFORMATS").split(";");
		dateCodes = Resurses.getString("LOCALIZAT_DATECODES").split(";");
		
		JLabel lbl = new JLabel(Resurses.getString("SETTING_LOCALE"));
		getContentPane().add(lbl);
		lbl.setBounds(x,y,200,20);
		y+= 20;
		loca = new JComboBox(locatexts);
		getContentPane().add(loca);
		loca.setBounds(x,y,200,20);
		
		String prevloca = Suku.kontroller.getPref(owner,Resurses.LOCALE, "fi");
		locaIndex=0;
		for (int i = 0; i < locas.length; i++) {
			if (prevloca.equals(locas[i])){
				locaIndex=i;
			}
		}
		loca.setSelectedIndex(locaIndex);
		
		y += 20;
		lbl = new JLabel(Resurses.getString("SETTING_REPOLANG"));
		getContentPane().add(lbl);
		lbl.setBounds(x,y,200,20);
		
		y += 20;
		
		
		
		String[] langnames = new String[Suku.getRepoLanguageCount()];
		for (int i = 0; i < langnames.length; i++){
			langnames[i] = Suku.getRepoLanguage(i, false);
		}
		
		
		
		
		repolang = new JComboBox(langnames);
		getContentPane().add(repolang);
		repolang.setBounds(x,y,200,20);
		
		prevloca = Suku.kontroller.getPref(owner,Resurses.REPOLANG, "fi");
		
		locaIndex=0;
		for (int i = 0; i < locas.length; i++) {
			if (prevloca.equals(locas[i])){
				locaIndex=i;
			}
		}
		
		if (locaIndex < repolang.getComponentCount()){
			repolang.setSelectedIndex(locaIndex);
		}
		
		y += 20;
		
		lbl = new JLabel(Resurses.getString("SETTING_DATEFORMAT"));
		getContentPane().add(lbl);
		lbl.setBounds(x,y,200,20);
		
		y += 20;
		dateFormat = new JComboBox(dateFormats);
		getContentPane().add(dateFormat);
		dateFormat.setBounds(x,y,200,20);
		
		prevloca = Suku.kontroller.getPref(owner,Resurses.DATEFORMAT, "FI");
		dateIndex=0;
		for (int i = 0; i < dateFormats.length; i++) {
			if (prevloca.equals(dateCodes[i])){
				dateIndex=i;
			}
		}
		
		
		dateFormat.setSelectedIndex(dateIndex);
		
		
		
		
		
		this.ok = new JButton(Resurses.OK);
		//this.ok.setDefaultCapable(true);
		getContentPane().add(this.ok);
		this.ok.setActionCommand(Resurses.OK);
		this.ok.addActionListener(this);
		this.ok.setBounds(400, 300, 100, 20);		

		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width/2-450,d.height/2-200,900,400);
		getRootPane().setDefaultButton(this.ok);

		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		
		if (cmd==null) return;
		
		if (cmd.equals(Resurses.OK)){
			
			int newLoca = loca.getSelectedIndex();
			Suku.kontroller.putPref(owner,Resurses.LOCALE, locas[newLoca]);
			
			int newLang = repolang.getSelectedIndex();
			Suku.kontroller.putPref(owner,Resurses.REPOLANG, Suku.getRepoLanguage(newLang, true));
			Resurses.setLanguage(locas[newLang]);

			int newDateIndex = dateFormat.getSelectedIndex();
			Suku.kontroller.putPref(owner,Resurses.DATEFORMAT, dateCodes[newDateIndex]);
			Resurses.setDateFormat(dateCodes[newDateIndex]);
			setVisible(false);
		}
	}

}
