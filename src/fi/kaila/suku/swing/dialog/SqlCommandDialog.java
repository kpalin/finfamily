package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.SukuData;

public class SqlCommandDialog extends JDialog implements ActionListener,
		ComponentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	private static final String OK = "OK";

	private JSplitPane splitPane = null;
	private JTextArea inputArea = null;
	private Vector<Vector<String>> rowData = null;
	private Vector<String> columnNames = null;
	private JTable outputTable = null;
	private JTextField errorField = null;

	private JComboBox selectExisting = null;

	private String[] preparedCommands = null;

	private JComboBox viewList = null;
	private int[] viewIds = null;
	private JCheckBox resetView = null;

	JButton ok = null;
	JButton cancel = null;

	/**
	 * This class executes sql-commands to db.
	 * 
	 * sql command is executed calling the server
	 * 
	 * SukuData resu = Suku.kontroll
	 * 
	 * @param parent
	 * @throws SukuException
	 */
	public SqlCommandDialog(Suku parent) throws SukuException {
		super(parent, Resurses.getString("MENU_TOOLS_SQL"), false);
		setLayout(null);
		getRootPane().addComponentListener(this);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = new Dimension(650, 500);
		setBounds(d.width / 2 - 325, d.height / 2 - 250, size.width,
				size.height);
		String loca = Suku.kontroller.getPref(parent, Resurses.LOCALE, "xx");
		int y = 30;
		inputArea = new JTextArea();
		inputArea.setLineWrap(true);
		JScrollPane inputScroll = new JScrollPane(inputArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		inputArea.setWrapStyleWord(true);
		inputArea.setLineWrap(true);
		rowData = new Vector<Vector<String>>();
		columnNames = new Vector<String>();
		outputTable = new JTable(rowData, columnNames);

		JScrollPane outputScroll = new JScrollPane(outputTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
				inputScroll, outputScroll);
		add(this.splitPane);
		this.splitPane.setBounds(10, y, size.width - 40, size.height - 200);
		this.splitPane.setDividerLocation(0.5);
		this.errorField = new JTextField();
		add(this.errorField);
		this.errorField.setEditable(false);

		selectExisting = new JComboBox();
		selectExisting.addActionListener(this);
		add(selectExisting);

		selectExisting.addItem(Resurses.getString("TOOLS_SQL_SELECTCOMMAND"));

		extractCommands(loca);

		this.resetView = new JCheckBox(
				Resurses.getString("DIALOG_VIEW_ADD_EMPTY_VIEW"));
		add(this.resetView);

		SukuData vlist;

		vlist = Suku.kontroller.getSukuData("cmd=viewlist");

		String[] lista = vlist.generalArray;
		this.viewList = new JComboBox();

		add(this.viewList);
		// viewList.removeAllItems();
		viewList.addItem(Resurses.getString("TOOLS_SQL_SELECTVIEW"));
		viewIds = new int[lista.length];
		for (int i = 0; i < lista.length; i++) {
			String[] pp = lista[i].split(";");
			if (pp.length > 1) {
				int vid = 0;
				try {
					vid = Integer.parseInt(pp[0]);
					viewIds[i] = vid;
				} catch (NumberFormatException ne) {
					viewIds[i] = 0;
				}
				viewList.addItem(pp[1]);
			}
		}

		ok = new JButton(OK);
		add(ok);

		ok.addActionListener(this);
		ok.setDefaultCapable(true);

		getRootPane().setDefaultButton(ok);

		cancel = new JButton(Resurses.getString("CANCEL"));
		add(cancel);

		cancel.addActionListener(this);

	}

	private void extractCommands(String loca) {
		InputStream in = this.getClass()
				.getResourceAsStream("/sql/queries.sql");

		String rivi = null;
		String defaultrivi = null;
		StringBuilder sb = new StringBuilder();
		Vector<String> lista = new Vector<String>();
		try {
			while ((rivi = readRivi(in)) != null) {

				if (rivi.startsWith("--")) {
					if (rivi.startsWith("--en")) {
						defaultrivi = rivi;
					} else if (rivi.startsWith("--" + loca)) {
						defaultrivi = rivi.substring(4);
					}
				} else {
					if (rivi.trim().endsWith(";")) {
						int lo = rivi.lastIndexOf(";");
						sb.append(rivi.substring(0, lo));
						lista.add(sb.toString());
						selectExisting.addItem(defaultrivi);
						// System.out.println("sb:" + sb.toString());
						sb = new StringBuilder();
					} else {
						sb.append(rivi);
						if (rivi.length() > 0) {
							sb.append(" ");
						}
					}
				}

			}
			preparedCommands = lista.toArray(new String[0]);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Creating prepared commands", e);
		}

	}

	private String readRivi(InputStream in) throws IOException {
		int nextByte = 0;

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		while ((nextByte = in.read()) >= 0) {
			if (nextByte == '\n') {
				break;
			}
			if (nextByte != '\r') {
				if (bout.size() > 0 || nextByte != ' ') {
					bout.write(nextByte);
				}
			}
		}
		if (bout.size() == 0 && nextByte < 0) {
			return null;
		}
		return bout.toString("UTF-8");

	}

	private void executeSQL() {
		try {
			String sql = inputArea.getText();
			SukuData req = new SukuData();
			req.generalText = sql;
			this.columnNames.clear();
			this.rowData.clear();
			SukuData resu = null;
			boolean emptyView = this.resetView.isSelected();
			int viewIdx = this.viewList.getSelectedIndex();
			if (viewIdx > 0) {
				resu = Suku.kontroller.getSukuData(req, "cmd=sql",
						"type=select", "empty=" + emptyView, "vid="
								+ viewIds[viewIdx - 1]);
			} else {

				resu = Suku.kontroller.getSukuData(req, "cmd=sql",
						"type=select");
			}
			if (resu.resu != null) {
				errorField.setText(resu.resu);
				return;
			}
			if (resu.resuCount > 0) {
				errorField.setText(Resurses.getString("MENU_TOOLS_SQL_COUNT")
						+ " " + resu.resuCount);
			} else {

				rowData = new Vector<Vector<String>>();
				columnNames = new Vector<String>();
				if (resu.vvTexts != null) {
					for (int i = 0; i < resu.vvTexts.size(); i++) {
						String[] ss = resu.vvTexts.get(i);
						if (i == 0) {

							for (int j = 0; j < ss.length; j++) {
								columnNames.add(ss[j]);
							}

						} else {
							Vector<String> row = new Vector<String>();
							for (int j = 0; j < ss.length; j++) {
								row.add(ss[j]);
							}
							rowData.add(row);

						}

					}

					outputTable = new JTable(rowData, columnNames);
					errorField.setText(Resurses
							.getString("MENU_TOOLS_SQL_COUNT")
							+ " = "
							+ rowData.size());
					outputTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					TableColumnModel tc = outputTable.getColumnModel();
					TableColumn cc;
					for (int k = 0; k < columnNames.size(); k++) {
						cc = tc.getColumn(k);
						cc.setMinWidth(10);
					}

					JScrollPane outputScroll = new JScrollPane(outputTable,
							ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

					splitPane.setRightComponent(outputScroll);

					// outputTable.updateUI();
					// outputScroll.updateUI();

				}
			}

		} catch (SukuException e) {
			errorField.setText(e.getMessage());
			return;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == ok) {
			executeSQL();
		}
		if (e.getSource() == cancel) {
			setVisible(false);
		}
		if (e.getSource() == selectExisting) {
			int idx = selectExisting.getSelectedIndex();
			if (idx > 0) {
				splitPane.setDividerLocation(0.5);
				inputArea.setText(preparedCommands[idx - 1]);
			}
		}
	}

	@Override
	public void componentResized(ComponentEvent e) {

		Dimension size = getSize();
		int rootx = getRootPane().getLocation().x;
		int rooty = getRootPane().getLocation().y;

		this.splitPane.setBounds(10, 30, size.width - 40, size.height - 200);
		this.errorField.setBounds(10, 30 + this.splitPane.getHeight() + 5,
				this.splitPane.getWidth(), 24);

		selectExisting.setBounds(10, 30 + this.splitPane.getHeight() + 34,
				this.splitPane.getWidth() - 20, 24);

		viewList.setBounds(10, 30 + this.splitPane.getHeight() + 64,
				this.splitPane.getWidth() - 200, 24);
		resetView.setBounds(this.splitPane.getWidth() - 190,
				30 + this.splitPane.getHeight() + 64, 190, 24);
		ok.setBounds(rootx + size.width - 250, rooty + size.height - 95, 100,
				24);
		cancel.setBounds(rootx + size.width - 140, rooty + size.height - 95,
				100, 24);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// not used
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// not used
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// not used
	}

}
