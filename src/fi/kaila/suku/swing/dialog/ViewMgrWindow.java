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
import javax.swing.JCheckBox;
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
 * <h1>View Manager Dialog</h1>
 * 
 * <p>
 * Persons in the database can be grouped in groups and in views. A person can
 * have only one group and he/she can belong to several views. View Manager
 * manages views.
 * </p>
 * 
 * <p>
 * The View manager consists of a non-modal Dialog that is requested from the
 * Tools menu.
 * </p>
 * 
 * <p>
 * Views are held in two database tabels, the Views table that contains a list
 * of the named views and their corresponding numeric ViewId's and table
 * viewunits with a list of persons (units) for each view. A person is
 * considered to be a member of a view if he/she exists (at least once) in the
 * viewunits table. The person can exist in more than one view.
 * </p>
 * 
 * <h2>Create a View</h2>
 * 
 * <p>
 * Write the name of the view to be created in the textfield and press cerate.
 * If a view with the name already exists an error is thrown.
 * </p>
 * 
 * <h2>Remove View</h2>
 * 
 * <p>
 * Removes the selected view from the database.
 * </p>
 * 
 * <h2>Add to View</h2>
 * 
 * <p>
 * This group of commands adds the persons selected in database view to the
 * view. If only one person is selected in database view then it is possible to
 * add also all descendants of that person to the group with or without the
 * spouses of the descendant. It will also be possible to add all ancestors of
 * the single selected person to the group.
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
public class ViewMgrWindow extends JDialog implements ActionListener {

	private static Logger logger = Logger.getLogger(ViewMgrWindow.class
			.getName());

	private static final long serialVersionUID = 1L;
	ButtonGroup removes = null;
	private Suku parent = null;
	private JButton close = null;
	private JPanel removeViewGroup = null;
	private JButton remove = null;
	private JButton removeView = null;
	private JComboBox viewlist = null;
	private JTextField newViewName = null;
	ButtonGroup addes = null;
	private JPanel addViewGroup = null;
	private JButton addView = null;
	private JButton add = null;
	private JLabel addedCount = null;
	private JRadioButton addDescendant = null;
	private JRadioButton addDescAndSpouses = null;
	private JRadioButton addAncestors = null;
	private JLabel selectedName = null;
	private JLabel selectedViews = null;
	private JCheckBox emptyView = null;
	private JTextField generations = null;
	private int[] viewids = null;
	private String[] viewnames = null;

	private javax.swing.Timer t = null;

	public ViewMgrWindow(Suku parent) throws SukuException {
		super(parent, Resurses.getString("DIALOG_VIEW_MGR"), false);
		this.parent = parent;

		initMe();
	}

	private void initMe() throws SukuException {
		setLayout(null);

		JLabel lbl = new JLabel(Resurses.getString("DIALOG_VIEW_VIEWS"));
		add(lbl);
		lbl.setBounds(10, 10, 200, 20);

		int yy = 30;

		viewlist = new JComboBox();
		viewlist.setBounds(10, yy, 200, 20);
		add(viewlist);
		initViewlist();
		yy += 22;
		removeView = new JButton(Resurses.getString("DIALOG_VIEW_REMOVE_VIEW"));
		add(removeView);
		removeView.addActionListener(this);
		removeView.setBounds(10, yy, 200, 24);

		lbl = new JLabel(Resurses.getString("DIALOG_VIEW_NEW"));
		add(lbl);
		lbl.setBounds(300, 10, 200, 20);

		newViewName = new JTextField();
		add(newViewName);
		newViewName.setBounds(300, 30, 200, 20);

		addView = new JButton(Resurses.getString("DIALOG_VIEW_ADD_VIEW"));
		add(addView);
		addView.addActionListener(this);
		addView.setBounds(300, 54, 200, 24);

		yy += 22;
		selectedName = new JLabel();
		add(selectedName);
		selectedName.setBounds(10, yy, 460, 20);
		yy += 20;
		selectedViews = new JLabel();
		add(selectedViews);
		selectedViews.setBounds(10, yy, 460, 20);

		yy += 22;

		JRadioButton formd;

		addViewGroup = new JPanel();
		add(addViewGroup);
		addViewGroup.setBounds(10, yy, 280, 300);

		addViewGroup.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.RAISED), Resurses
				.getString("DIALOG_VIEW_ADD_CAPTION")));
		addViewGroup.setLayout(null);
		addes = new ButtonGroup();
		;
		formd = new JRadioButton(Resurses.getString("DIALOG_VIEW_ADD_SELECTED"));
		formd.setActionCommand("SELECTED");
		formd.setSelected(true);
		addViewGroup.add(formd);
		addes.add(formd);
		formd.setBounds(10, 22, 240, 20);

		// formd=new
		// JRadioButton(Resurses.getString("DIALOG_VIEW_ADD_SELECTED_EMPTY"));
		// formd.setActionCommand("SELECTEDEMPTY");
		//		
		// addViewGroup.add(formd);
		// addes.add(formd);
		// formd.setBounds(10,44,240,20);

		addDescendant = new JRadioButton(Resurses
				.getString("DIALOG_VIEW_ADD_DESC"));
		addDescendant.setActionCommand("DESC");
		addViewGroup.add(addDescendant);
		addes.add(addDescendant);
		addDescendant.setBounds(10, 44, 260, 20);

		addDescAndSpouses = new JRadioButton(Resurses
				.getString("DIALOG_VIEW_ADD_DESC_SPOUSES"));
		addDescAndSpouses.setActionCommand("DESC_SPOUSES");
		addViewGroup.add(addDescAndSpouses);
		addes.add(addDescAndSpouses);
		addDescAndSpouses.setBounds(10, 66, 260, 20);

		addAncestors = new JRadioButton(Resurses
				.getString("DIALOG_VIEW_ADD_ANC"));
		addAncestors.setActionCommand("ANC");
		addViewGroup.add(addAncestors);
		addes.add(addAncestors);
		addAncestors.setBounds(10, 88, 260, 20);

		generations = new JTextField();
		addViewGroup.add(generations);
		generations.setBounds(10, 226, 60, 20);

		lbl = new JLabel(Resurses.getString("DIALOG_VIEW_ADD_GENERATIONS"));
		addViewGroup.add(lbl);
		lbl.setBounds(75, 226, 180, 20);

		emptyView = new JCheckBox(Resurses
				.getString("DIALOG_VIEW_ADD_EMPTY_VIEW"));
		addViewGroup.add(emptyView);
		emptyView.setBounds(10, 250, 260, 20);

		add = new JButton(Resurses.getString("DIALOG_VIEW_ADD"));
		addViewGroup.add(add);
		add.setBounds(10, 270, 120, 24);
		add.addActionListener(this);

		addedCount = new JLabel();
		addViewGroup.add(addedCount);
		addedCount.setBounds(140, 270, 110, 20);

		removeViewGroup = new JPanel();
		add(removeViewGroup);
		removeViewGroup.setBounds(300, yy, 280, 300);
		removeViewGroup.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.RAISED), Resurses
						.getString("DIALOG_VIEW_REMOVE_CAPTION")));
		removeViewGroup.setLayout(null);
		removes = new ButtonGroup();

		int rivi = 22;
		formd = new JRadioButton(Resurses
				.getString("DIALOG_VIEW_REMOVE_SELECTED"));
		formd.setActionCommand("SELECTED");
		formd.setSelected(true);
		removeViewGroup.add(formd);
		removes.add(formd);
		formd.setBounds(10, rivi, 260, 20);
		rivi += 22;

		formd = new JRadioButton(Resurses.getString("DIALOG_VIEW_EMPTY_VIEW"));
		formd.setActionCommand("ALL");
		formd.setSelected(true);
		removeViewGroup.add(formd);
		removes.add(formd);
		formd.setBounds(10, rivi, 260, 20);
		rivi += 22;

		remove = new JButton(Resurses.getString("DIALOG_VIEW_REMOVE"));
		removeViewGroup.add(remove);
		remove.setBounds(10, 270, 120, 24);
		remove.addActionListener(this);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

		setLocation(d.width / 2 - 305, d.height / 2 - 250);
		Dimension sz = new Dimension(610, 500);
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

	private void initViewlist() throws SukuException {
		viewlist.removeAllItems();
		viewlist.addItem("");
		SukuData vlist = Suku.kontroller.getSukuData("cmd=viewlist");

		viewnames = new String[vlist.generalArray.length + 1];
		viewids = new int[vlist.generalArray.length + 1];
		viewids[0] = -1;
		viewnames[0] = "";
		for (int i = 0; i < vlist.generalArray.length; i++) {
			String[] tmps = vlist.generalArray[i].split(";");

			viewids[i + 1] = Integer.parseInt(tmps[0]);
			viewnames[i + 1] = tmps[1];
			viewlist.addItem(tmps[1]);
		}

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
						.getString("DIALOG_VIEW_SELECTED_COUNT")
						+ " " + pids.length);
			} else {
				SukuData resp = Suku.kontroller.getSukuData("cmd=view",
						"action=get", "pid=" + pp.getPid());

				StringBuffer sb = new StringBuffer();
				if (resp.generalArray != null) {
					for (int i = 0; i < resp.generalArray.length; i++) {
						if (sb.length() > 0)
							sb.append(";");
						sb.append(resp.generalArray[i]);
					}
				}
				selectedName.setText(Resurses
						.getString("DIALOG_VIEW_SELECTED_NAME")
						+ " " + pp.getAlfaName());
				selectedViews.setText(Resurses
						.getString("DIALOG_VIEW_SELECTED_VIEWS")
						+ " " + sb.toString());

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
		} else if (e.getSource() == removeView) {
			try {
				int idx = viewlist.getSelectedIndex();
				if (idx > 0) {
					int seleview = viewids[idx];
					SukuData resp = Suku.kontroller.getSukuData("cmd=view",
							"action=removeview", "viewid=" + seleview);
					if (resp.resu != null) {
						JOptionPane.showMessageDialog(parent, Resurses
								.getString("DIALOG_VIEW_ERROR")
								+ " " + resp.resu);
						return;
					}
					initViewlist();
					viewlist.updateUI();
				}
			} catch (SukuException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == addView) {
			try {

				String viewname = newViewName.getText();
				if (viewname.equals(""))
					return;
				for (int i = 0; i < viewnames.length; i++) {
					if (viewname.equals(viewnames[i])) {
						JOptionPane.showMessageDialog(parent, Resurses
								.getString("DIALOG_VIEW_EXISTS")
								+ " [" + viewname + "]");
						return;
					}
				}

				SukuData resp = Suku.kontroller.getSukuData("cmd=view",
						"action=addview", "viewname=" + viewname);
				if (resp.resu != null) {
					JOptionPane.showMessageDialog(parent, Resurses
							.getString("DIALOG_VIEW_ERROR")
							+ " " + resp.resu);
					return;
				}
				newViewName.setText("");
				initViewlist();
				viewlist.updateUI();

			} catch (SukuException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == remove) {
			int idx = viewlist.getSelectedIndex();

			if (idx <= 0) {
				JOptionPane.showMessageDialog(parent, Resurses
						.getString("DIALOG_VIEW_NOTSELECTED"));
				return;
			}
			int seleview = viewids[idx];
			try {
				SukuData request = new SukuData();
				ButtonModel model = removes.getSelection();
				if (model != null) {
					String remocmd = model.getActionCommand();
					if (remocmd == null)
						return;
					SukuData response = null;
					if (remocmd.equals("ALL")) {

						response = Suku.kontroller.getSukuData("cmd=view",
								"action=remove", "key=all", "viewid="
										+ seleview);

					} else if (remocmd.equals("SELECTED")) {

						request.pidArray = parent.getSelectedPids();
						if (request.pidArray != null
								&& request.pidArray.length > 0) {
							response = Suku.kontroller.getSukuData(request,
									"cmd=view", "action=remove",
									"key=pidarray", "viewid=" + seleview);
						}
					} else {
						System.out.println("NOT HERE");
					}
					if (response == null) {
						String messu = "View remove Response missing";
						logger.warning(messu);
						JOptionPane.showMessageDialog(parent, messu);
					}

				}
			} catch (SukuException e1) {
				logger.log(Level.WARNING, "Remove group failed", e1);
				e1.printStackTrace();
			}

		} else if (e.getSource() == add) {
			addedCount.setText("");
			try {
				SukuData request = new SukuData();
				ButtonModel model = addes.getSelection();
				if (model != null) {
					String addcmd = model.getActionCommand();
					if (addcmd == null)
						return;

					SukuData response = null;
					if (addcmd.equals("SELECTED")) {
						boolean emptyIt = emptyView.isSelected();
						int idx = viewlist.getSelectedIndex();
						if (idx > 0) {
							int seleview = viewids[idx];
							request.pidArray = parent.getSelectedPids();

							response = Suku.kontroller.getSukuData(request,
									"cmd=view", "action=add", "key=pidarray",
									"viewid=" + seleview, "empty=" + emptyIt);
							emptyView.setSelected(false);
							addedCount.setText("" + response.resuCount);
						} else {
							JOptionPane.showMessageDialog(parent, Resurses
									.getString("DIALOG_VIEW_NOTSELECTED"));
							return;
						}
					} else if (addcmd.startsWith("DESC")
							|| addcmd.equals("ANC")) {
						boolean emptyIt = emptyView.isSelected();
						int idx = viewlist.getSelectedIndex();
						if (idx > 0) {
							int seleview = viewids[idx];
							int[] ii = parent.getSelectedPids();
							if (ii.length == 1) {
								String gene = generations.getText();
								try {
									Integer.parseInt(gene);
								} catch (NumberFormatException ne) {
									gene = "";
								}
								response = Suku.kontroller.getSukuData(
										"cmd=view", "action=add", "pid="
												+ ii[0], "key=" + addcmd,
										"viewid=" + seleview, "empty="
												+ emptyIt, "gen=" + gene);

								emptyView.setSelected(false);
								addedCount.setText("" + response.resuCount);
							}
						} else {
							JOptionPane.showMessageDialog(parent, Resurses
									.getString("DIALOG_VIEW_NOTSELECTED"));
							return;
						}

					}
					if (response == null) {
						String messu = Resurses
								.getString("DIALOG_VIEW_ADD_ERROR");
						logger.warning(messu);
						JOptionPane.showMessageDialog(parent, messu);
					}
				}
			} catch (Exception e1) {
				logger.log(Level.WARNING, "Remove view failed", e1);
				e1.printStackTrace();
			}
		}
	}

}
