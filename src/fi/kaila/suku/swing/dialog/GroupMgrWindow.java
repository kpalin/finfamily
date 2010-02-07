package fi.kaila.suku.swing.dialog;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import fi.kaila.suku.swing.Suku;
import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.SukuData;

/**
 * 
 * <h1>Group Manager Dialog</h1>
 * 
 * <p>
 * Persons in the database can be grouped in groups and in views. A person can
 * have only one group and he/she can belong to several views. Group Manager
 * manages the groups.
 * </p>
 * 
 * <p>
 * The Group manager consists of a non-modal Dialog that is requested from the
 * Tools menu.
 * </p>
 * 
 * <p>
 * GroupId in Unit table can contain any string or be null. Updating of GroupId
 * is made with requests from here. Update possibility for GroupId from
 * PersonView has been removed to avoid updateing from two sources
 * simultaneuosly.
 * </p>
 * 
 * <h2>Remove Group</h2>
 * 
 * <p>
 * This group of commands can remove groupId from all persons, from persons
 * selected in database view or from persons found from selected view.
 * </p>
 * 
 * <h2>Add to Group</h2>
 * 
 * <p>
 * This group of commands adds the GroupId to persons selected in database view
 * or persons found from selected view. If only one person is selected in
 * database view then it is possible to add also all descendants of that person
 * to the group with or without the spouses of the descendant. It is also
 * possible to add all ancestors of the single selected person to the group.
 * </p>
 * 
 * <p>
 * Only persons without a groupid are added to the group. Finding descendants or
 * ancestors to the group stops at a person that already has a group.
 * </p>
 * 
 * <p>
 * The selected person or # of selected persons in the database view is
 * refreshed once a second.
 * </p>
 * 
 * @author Kalle
 * 
 */
public class GroupMgrWindow extends JDialog implements ActionListener {

	private static Logger logger = Logger.getLogger(GroupMgrWindow.class
			.getName());

	private static final long serialVersionUID = 1L;
	ButtonGroup removes = null;
	private Suku parent = null;
	private JButton close = null;
	private JPanel removeGroup = null;
	private JButton remove = null;
	private JComboBox viewlist = null;

	ButtonGroup addes = null;
	private JPanel addGroup = null;
	private JButton add = null;
	private JLabel addedCount = null;
	private JRadioButton addDescendant = null;
	private JRadioButton addDescAndSpouses = null;
	private JRadioButton addAncestors = null;
	private JLabel selectedName = null;
	private JLabel selectedGroup = null;
	private JTextField generations = null;
	private JTextField groupId = null;

	private int[] viewids = null;

	private javax.swing.Timer t = null;

	/**
	 * @param parent
	 * @throws SukuException
	 */
	public GroupMgrWindow(Suku parent) throws SukuException {
		super(parent, Resurses.getString("DIALOG_GROUP_MGR"), false);
		this.parent = parent;

		initMe();
	}

	private void initMe() throws SukuException {
		setLayout(null);

		JLabel lbl = new JLabel(Resurses.getString("DIALOG_GROUP_VIEW"));
		add(lbl);
		lbl.setBounds(10, 10, 200, 20);

		SukuData vlist = Suku.kontroller.getSukuData("cmd=viewlist");
		int yy = 30;
		String[] viewnames = new String[vlist.generalArray.length + 1];
		viewids = new int[vlist.generalArray.length + 1];
		viewids[0] = -1;
		viewnames[0] = "";
		for (int i = 0; i < vlist.generalArray.length; i++) {
			String[] tmps = vlist.generalArray[i].split(";");

			viewids[i + 1] = Integer.parseInt(tmps[0]);
			viewnames[i + 1] = tmps[1];

		}

		viewlist = new JComboBox(viewnames);
		viewlist.setBounds(10, yy, 200, 20);
		add(viewlist);

		lbl = new JLabel(Resurses.getString("DIALOG_GROUP"));
		add(lbl);
		lbl.setBounds(300, 10, 200, 20);

		groupId = new JTextField();
		add(groupId);
		groupId.setBounds(300, yy, 200, 20);

		yy += 22;
		selectedName = new JLabel();
		add(selectedName);
		selectedName.setBounds(10, yy, 360, 20);
		yy += 20;
		selectedGroup = new JLabel();
		add(selectedGroup);
		selectedGroup.setBounds(10, yy, 360, 20);

		yy += 22;

		// lbl = new JLabel();
		// add(lbl);
		// lbl.setBounds(10,53,200,20);
		JRadioButton formd;

		addGroup = new JPanel();
		add(addGroup);
		addGroup.setBounds(10, yy, 260, 300);
		addGroup.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED), Resurses
				.getString("DIALOG_GROUP_ADD_CAPTION")));
		addGroup.setLayout(null);
		addes = new ButtonGroup();
		;
		formd = new JRadioButton(Resurses
				.getString("DIALOG_GROUP_ADD_SELECTED"));
		formd.setActionCommand("SELECTED");
		formd.setSelected(true);
		addGroup.add(formd);
		addes.add(formd);
		formd.setBounds(10, 22, 240, 20);

		formd = new JRadioButton(Resurses.getString("DIALOG_GROUP_ADD_VIEW"));
		formd.setActionCommand("VIEW");

		addGroup.add(formd);
		addes.add(formd);
		formd.setBounds(10, 44, 240, 20);

		addDescendant = new JRadioButton(Resurses
				.getString("DIALOG_GROUP_ADD_DESC"));
		addDescendant.setActionCommand("DESC");
		addGroup.add(addDescendant);
		addes.add(addDescendant);
		addDescendant.setBounds(10, 66, 240, 20);

		addDescAndSpouses = new JRadioButton(Resurses
				.getString("DIALOG_GROUP_ADD_DESC_SPOUSES"));
		addDescAndSpouses.setActionCommand("DESC_SPOUSES");
		addGroup.add(addDescAndSpouses);
		addes.add(addDescAndSpouses);
		addDescAndSpouses.setBounds(10, 88, 240, 20);

		addAncestors = new JRadioButton(Resurses
				.getString("DIALOG_GROUP_ADD_ANC"));
		addAncestors.setActionCommand("ANC");
		addGroup.add(addAncestors);
		addes.add(addAncestors);
		addAncestors.setBounds(10, 110, 240, 20);

		generations = new JTextField();
		addGroup.add(generations);
		generations.setBounds(10, 226, 60, 20);

		lbl = new JLabel(Resurses.getString("DIALOG_VIEW_ADD_GENERATIONS"));
		addGroup.add(lbl);
		lbl.setBounds(75, 226, 180, 20);

		add = new JButton(Resurses.getString("DIALOG_GROUP_ADD"));
		addGroup.add(add);
		add.setBounds(10, 270, 120, 24);
		add.addActionListener(this);

		addedCount = new JLabel();
		addGroup.add(addedCount);
		addedCount.setBounds(140, 270, 110, 20);

		removeGroup = new JPanel();
		add(removeGroup);
		removeGroup.setBounds(300, yy, 260, 300);
		removeGroup.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED), Resurses
				.getString("DIALOG_GROUP_REMOVE_CAPTION")));
		removeGroup.setLayout(null);
		removes = new ButtonGroup();

		int rivi = 22;
		formd = new JRadioButton(Resurses
				.getString("DIALOG_GROUP_REMOVE_SELECTED"));
		formd.setActionCommand("SELECTED");
		formd.setSelected(true);
		removeGroup.add(formd);
		removes.add(formd);
		formd.setBounds(10, rivi, 240, 20);
		rivi += 22;

		formd = new JRadioButton(Resurses.getString("DIALOG_GROUP_REMOVE_VIEW"));
		formd.setActionCommand("VIEW");

		removeGroup.add(formd);
		removes.add(formd);
		formd.setBounds(10, rivi, 240, 20);
		rivi += 22;
		formd = new JRadioButton(Resurses
				.getString("DIALOG_GROUP_REMOVE_GROUP"));
		formd.setActionCommand("GROUP");
		formd.setSelected(true);
		removeGroup.add(formd);
		removes.add(formd);
		formd.setBounds(10, rivi, 240, 20);
		rivi += 22;

		formd = new JRadioButton(Resurses.getString("DIALOG_GROUP_REMOVE_ALL"));
		formd.setActionCommand("ALL");

		removeGroup.add(formd);
		removes.add(formd);
		formd.setBounds(10, rivi, 240, 20);
		rivi += 22;
		remove = new JButton(Resurses.getString("DIALOG_GROUP_REMOVE"));
		removeGroup.add(remove);
		remove.setBounds(10, 270, 120, 24);
		remove.addActionListener(this);

		updateSelectStatus();

		// lbl = new JLabel(Resurses.getString("DIALOG_GROUP_UPDATE_INFO"));
		// add(lbl);
		// lbl.setBounds(10,400,300,20);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		// setBounds(d.width/2-200,d.height/2-200,600,300);

		setLocation(d.width / 2 - 200, d.height / 2 - 250);
		Dimension sz = new Dimension(600, 500);
		setSize(sz);

		close = new JButton(Resurses.getString("CLOSE"));
		add(close);
		close.setBounds(sz.width - 160, sz.height - 70, 120, 24);
		close.addActionListener(this);

		setVisible(true);

		t = new javax.swing.Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSelectStatus();
			}
		});
		t.start();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (parent != null) {
					parent.GroupWindowClosing();
					t.stop();
				}
				e.getClass();

			}
		});

	}

	private void updateSelectStatus() {
		try {
			PersonShortData pp = parent.getSelectedPerson();
			addDescendant.setEnabled(pp != null);
			addDescAndSpouses.setEnabled(pp != null);
			addAncestors.setEnabled(pp != null);
			if (pp == null) {
				int pids[] = parent.getSelectedPids();
				if (pids == null)
					return;
				selectedName.setText(Resurses
						.getString("DIALOG_GROUP_SELECTED_COUNT")
						+ " " + pids.length);
			} else {
				selectedName.setText(Resurses
						.getString("DIALOG_GROUP_SELECTED_NAME")
						+ " " + pp.getAlfaName());
				selectedGroup.setText(Resurses
						.getString("DIALOG_GROUP_SELECTED_GROUP")
						+ " " + nv(pp.getGroup()));

				if (groupId.getText().isEmpty()) {
					groupId.setText(nv(pp.getGroup()));
				}
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "updateSelectStatus()", e);
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// System.out.println("CLOSE GROUP FOR " + e.toString());
		if (e.getSource() == close) {
			setVisible(false);
			if (parent != null) {
				parent.GroupWindowClosing();
				t.stop();
			}
		} else if (e.getSource() == remove) {
			try {
				SukuData request = new SukuData();
				ButtonModel model = removes.getSelection();
				if (model != null) {
					String remocmd = model.getActionCommand();
					if (remocmd == null)
						return;
					SukuData response = null;
					if (remocmd.equals("ALL")) {
						response = Suku.kontroller.getSukuData(request,
								"cmd=group", "action=remove", "key=all");
					} else if (remocmd.equals("SELECTED")) {
						request.pidArray = parent.getSelectedPids();

						response = Suku.kontroller.getSukuData(request,
								"cmd=group", "action=remove", "key=pidarray");
					} else if (remocmd.equals("VIEW")) {
						int isele = viewlist.getSelectedIndex();
						if (isele < 1) {
							JOptionPane
									.showMessageDialog(
											parent,
											Resurses
													.getString("DIALOG_GROUP_VIEW_NOTSELECTED"));
							return;
						}
						response = Suku.kontroller.getSukuData(request,
								"cmd=group", "action=remove", "view="
										+ viewids[isele]);
					} else if (remocmd.equals("GROUP")) {
						String grp = groupId.getText();
						if (grp.isEmpty()) {
							JOptionPane.showMessageDialog(parent, Resurses
									.getString("DIALOG_GROUP_MISSING"));
							return;
						}
						response = Suku.kontroller.getSukuData(request,
								"cmd=group", "action=remove", "group=" + grp);
					} else {
						System.out.println("NOT HERE");
					}
					if (response == null || response.pidArray == null) {
						String messu = "Group remove Response missing";
						logger.warning(messu);
						JOptionPane.showMessageDialog(parent, messu);
					} else {
						for (int i = 0; i < response.pidArray.length; i++) {
							parent.updateDbGroup(response.pidArray[i], null);
						}
						parent.refreshDbView();
						String messu = Resurses
								.getString("DIALOG_GROUP_REMOVED")
								+ " [" + response.pidArray.length + "]";
						JOptionPane.showMessageDialog(parent, messu);
						logger.info("REMOVE " + remocmd + " GROUPS: " + messu);

					}

				}
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "Remove group failed", e1);
				e1.printStackTrace();
			}

			// request.pidArray
		} else if (e.getSource() == add) {
			try {
				SukuData request = new SukuData();
				ButtonModel model = addes.getSelection();
				if (model != null) {
					String addcmd = model.getActionCommand();
					if (addcmd == null)
						return;
					String grp = groupId.getText();
					if (grp.isEmpty()) {
						JOptionPane.showMessageDialog(parent, Resurses
								.getString("DIALOG_GROUP_MISSING"));
						return;
					}

					SukuData response = null;
					if (addcmd.equals("SELECTED")) {
						request.pidArray = parent.getSelectedPids();

						response = Suku.kontroller.getSukuData(request,
								"cmd=group", "action=add", "key=pidarray",
								"group=" + grp);
						addedCount.setText("" + response.resuCount);
					} else if (addcmd.equals("VIEW")) {
						int isele = viewlist.getSelectedIndex();
						if (isele < 1) {
							JOptionPane
									.showMessageDialog(
											parent,
											Resurses
													.getString("DIALOG_GROUP_VIEW_NOTSELECTED"));
							return;
						}
						response = Suku.kontroller.getSukuData("cmd=group",
								"action=add", "view=" + viewids[isele],
								"group=" + grp);
						addedCount.setText("" + response.resuCount);
					} else if (addcmd.startsWith("DESC")
							|| addcmd.equals("ANC")) {
						int[] ii = parent.getSelectedPids();
						if (ii.length == 1) {
							String gene = generations.getText();
							try {
								Integer.parseInt(gene);
							} catch (NumberFormatException ne) {
								gene = "";
							}
							response = Suku.kontroller.getSukuData("cmd=group",
									"action=add", "pid=" + ii[0], "key="
											+ addcmd, "group=" + grp, "gen="
											+ gene);
							addedCount.setText("" + response.resuCount);
						}
					}
					if (response == null || response.pidArray == null) {
						String messu = "Group add Response missing";
						logger.warning(messu);
						JOptionPane.showMessageDialog(parent, messu);
					} else {
						for (int i = 0; i < response.pidArray.length; i++) {
							parent.updateDbGroup(response.pidArray[i], grp);
						}
						parent.refreshDbView();
						String messu = Resurses.getString("DIALOG_GROUP_ADDED")
								+ " [" + response.pidArray.length + "]";
						JOptionPane.showMessageDialog(parent, messu);
						logger.info("REMOVE " + addcmd + " GROUPS: " + messu);

					}
				}
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "Remove group failed", e1);
				e1.printStackTrace();
			}
		}
	}

	private String nv(String text) {
		if (text == null)
			return "";
		return text;
	}

}
