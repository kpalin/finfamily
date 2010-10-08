package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import fi.kaila.suku.util.Resurses;

public class LicenseDialog extends JDialog implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String OK = "OK";
	private JTextArea aboutArea;

	/**
	 * Instantiates a new about dialog.
	 * 
	 * @param owner
	 *            the owner
	 */
	public LicenseDialog(JFrame owner) {
		super(owner, Resurses.getString("LICENSE"), true);
		setLayout(null);

		aboutArea = new JTextArea();
		aboutArea.setEditable(false);
		aboutArea.setLineWrap(true);

		add(aboutArea);
		aboutArea.setBounds(20, 20, 750, 460);

		StringBuilder about = new StringBuilder();

		about.append("Copyright 2010 Kaarle Kaila. All rights reserved.\n");
		about.append("\n");
		about.append("Redistribution and use in source and binary forms, with or without modification, are\n");
		about.append("permitted provided that the following conditions are met:\n");
		about.append("\n");
		about.append("   1. Redistributions of source code must retain the above copyright notice, this list of\n");
		about.append("      conditions and the following disclaimer.\n");
		about.append("\n");
		about.append("   2. Redistributions in binary form must reproduce the above copyright notice, this list\n");
		about.append("      of conditions and the following disclaimer in the documentation and/or other materials\n");
		about.append("      provided with the distribution.\n");
		about.append("\n");
		about.append("THIS SOFTWARE IS PROVIDED BY KAARLE KAILA ''AS IS'' AND ANY EXPRESS OR IMPLIED\n");
		about.append("WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND\n");
		about.append("FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL KAARLE KAILA OR\n");
		about.append("CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR\n");
		about.append("CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR\n");
		about.append("SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON\n");
		about.append("ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING\n");
		about.append("NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF\n");
		about.append("ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n");
		about.append("\n");
		about.append("The views and conclusions contained in the software and documentation are those of the\n");
		about.append("authors and should not be interpreted as representing official policies, either expressed\n");
		about.append("or implied, of Kaarle Kaila.\n");

		aboutArea.setText(about.toString());

		JButton ok = new JButton(OK);
		getContentPane().add(ok);
		ok.setBounds(670, 500, 100, 24);
		ok.addActionListener(this);
		ok.setDefaultCapable(true);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(d.width / 2 - 400, d.height / 2 - 400, 800, 570);
		setResizable(false);

		getRootPane().setDefaultButton(ok);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		this.setVisible(false);

	}

}
