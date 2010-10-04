package fi.kaila.suku.swing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import fi.kaila.suku.util.Resurses;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;

/**
 * Shows genealogical statistics.
 * 
 * @author halonmi
 */
public class GenStat extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private ISuku parent;
	private GenStat me;
	private JComboBox statCombo;
	private ChartPanel chartPanel;
	private JFreeChart noOfChildrenChart;
	private JFreeChart ChildrenVsNoChildrenChart;
	private JFreeChart sexChart;
	private JFreeChart marriedVsSingleChart;
	private JFreeChart birthAndDeathMothsChart;
	private PersonShortData[] persons = null;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent
	 * @param persons
	 *            the persons
	 */
	public GenStat(ISuku parent, PersonShortData[] persons) {
		this.parent = parent;
		this.me = this;
		this.persons = persons;
		initMe();
	}

	private void initMe() {

		JLabel lblC = new JLabel(Resurses.getString("STAT_SELECT_STATISTICS"));

		statCombo = new JComboBox();

		statCombo.addItem(Resurses.getString("STAT_NO_OF_CHILDREN"));
		statCombo.addItem(Resurses.getString("STAT_CHILDREN_VS_NO_CHILDREN"));
		statCombo.addItem(Resurses.getString("STAT_SEX"));
		statCombo.addItem(Resurses.getString("STAT_MARRIED_VS_SINGLE"));
		statCombo.addItem(Resurses.getString("STAT_BIRTH_MONTHS"));
		statCombo.addItem(Resurses.getString("STAT_DEATH_MONTHS"));

		statCombo.setActionCommand(Resurses.SHOWGRID);
		statCombo.addActionListener(this);

		chartPanel = new ChartPanel(noOfChildrenChart);
		statNoOfChildren();

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lblC, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(statCombo, 250, 250,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(chartPanel, 0, GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.BASELINE)
								.addGroup(
										layout.createSequentialGroup()
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
																.addComponent(
																		lblC))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.LEADING)
																.addComponent(
																		statCombo,
																		GroupLayout.PREFERRED_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
																.addComponent(
																		chartPanel,
																		0,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)))));

		setSize(new Dimension(700, 700));

		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				if (parent != null) {
					parent.SukuFormClosing(me);
				}
				e.getClass();

			}
		});
	}

	private void statNoOfChildren() {
		int x, x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 = 0, x11 = 0, x12 = 0, x13 = 0, x14 = 0, x15 = 0, x16 = 0, x17 = 0, x18 = 0, x19 = 0, x20 = 0, xx = 0;

		// add the chart to a panel...
		final DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		noOfChildrenChart = ChartFactory.createBarChart(
				Resurses.getString("STAT_NO_OF_CHILDREN"),
				Resurses.getString("STAT_CHILDREN"),
				Resurses.getString("STAT_PIECES"), dataset1,
				PlotOrientation.VERTICAL, true, true, false);

		chartPanel.setChart(noOfChildrenChart);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = noOfChildrenChart.getCategoryPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		for (int xy = 0; xy < this.persons.length; xy++) {
			x = this.persons[xy].getChildCount();
			switch (x) {
			case 0:
				// Do nothing
				break;
			case 1:
				x1++;
				break;
			case 2:
				x2++;
				break;
			case 3:
				x3++;
				break;
			case 4:
				x4++;
				break;
			case 5:
				x5++;
				break;
			case 6:
				x6++;
				break;
			case 7:
				x7++;
				break;
			case 8:
				x8++;
				break;
			case 9:
				x9++;
				break;
			case 10:
				x10++;
				break;
			case 11:
				x11++;
				break;
			case 12:
				x12++;
				break;
			case 13:
				x13++;
				break;
			case 14:
				x14++;
				break;
			case 15:
				x15++;
				break;
			case 16:
				x16++;
				break;
			case 17:
				x17++;
				break;
			case 18:
				x18++;
				break;
			case 19:
				x19++;
				break;
			case 20:
				x20++;
				break;
			}
			//
		}
		dataset1.addValue(x1, "1", "");
		dataset1.addValue(x2, "2", "");
		dataset1.addValue(x3, "3", "");
		dataset1.addValue(x4, "4", "");
		dataset1.addValue(x5, "5", "");
		dataset1.addValue(x6, "6", "");
		dataset1.addValue(x7, "7", "");
		dataset1.addValue(x8, "8", "");
		dataset1.addValue(x9, "9", "");
		dataset1.addValue(x10, "10", "");
		dataset1.addValue(x11, "11", "");
		dataset1.addValue(x12, "12", "");
		dataset1.addValue(x13, "13", "");
		dataset1.addValue(x14, "14", "");
		dataset1.addValue(x15, "15", "");
		dataset1.addValue(x16, "16", "");
		dataset1.addValue(x17, "17", "");
		dataset1.addValue(x18, "18", "");
		dataset1.addValue(x19, "19", "");
		dataset1.addValue(x20, "20", "");
		dataset1.addValue(xx, ">20", "");

	}

	private void statChildrenVsNoChildren() {
		int x0 = 0, x1 = 0;
		int x;

		// add the chart to a panel...
		final DefaultPieDataset dataset = new DefaultPieDataset();
		ChildrenVsNoChildrenChart = ChartFactory.createPieChart(
				Resurses.getString("STAT_CHILDREN_VS_NO_CHILDREN"), dataset,
				false, true, false);

		chartPanel.setChart(ChildrenVsNoChildrenChart);

		final PiePlot plot = (PiePlot) ChildrenVsNoChildrenChart.getPlot();
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}: {2} ({1} " + Resurses.getString("STAT_PIECES") + " )"));
		plot.setNoDataMessage(Resurses.getString("STAT_NO_DATA"));

		for (int xy = 0; xy < this.persons.length; xy++) {
			x = this.persons[xy].getChildCount();
			if (x == 0) {
				x0++;
			} else {
				x1++;
			}
		}
		dataset.setValue(Resurses.getString("STAT_NO_CHILDREN"), x0);
		dataset.setValue(Resurses.getString("STAT_CHILDREN"), x1);

	}

	private void statSex() {
		int x0 = 0, x1 = 0, x2 = 0;
		String x;

		// add the chart to a panel...
		final DefaultPieDataset dataset2 = new DefaultPieDataset();
		sexChart = ChartFactory.createPieChart(Resurses.getString("STAT_SEX"),
				dataset2, false, true, false);

		chartPanel.setChart(sexChart);

		final PiePlot plot = (PiePlot) sexChart.getPlot();
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}: {2} ({1} " + Resurses.getString("STAT_PIECES") + " )"));
		plot.setNoDataMessage(Resurses.getString("STAT_NO_DATA"));

		for (int xy = 0; xy < this.persons.length; xy++) {
			x = this.persons[xy].getSex();
			if (x.equals("M")) {
				x0++;
			} else if (x.equals("F")) {
				x1++;
			} else {
				x2++;
			}
		}
		dataset2.setValue(Resurses.getString("STAT_SEX_M"), x0);
		dataset2.setValue(Resurses.getString("STAT_SEX_F"), x1);
		dataset2.setValue(Resurses.getString("STAT_SEX_U"), x2);

	}

	private void statMarriedVsSingle() {
		int x0 = 0, x1 = 0;
		int x;

		// add the chart to a panel...
		final DefaultPieDataset dataset = new DefaultPieDataset();
		marriedVsSingleChart = ChartFactory.createPieChart(
				Resurses.getString("STAT_MARRIED_VS_SINGLE"), dataset, false,
				true, false);

		chartPanel.setChart(marriedVsSingleChart);

		final PiePlot plot = (PiePlot) marriedVsSingleChart.getPlot();
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}: {2} ({1} " + Resurses.getString("STAT_PIECES") + " )"));
		plot.setNoDataMessage(Resurses.getString("STAT_NO_DATA"));

		for (int xy = 0; xy < this.persons.length; xy++) {
			x = this.persons[xy].getMarrCount();
			if (x == 0) {
				x0++;
			} else {
				x1++;
			}
		}
		dataset.setValue(Resurses.getString("STAT_MARRIED"), x0);
		dataset.setValue(Resurses.getString("STAT_SINGLE"), x1);

	}

	private void statBirthAndDeathMoths(boolean birth) {
		int x, x0 = 0, x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 = 0, x11 = 0, x12 = 0;
		// add the chart to a panel...
		final DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		if (birth) {
			birthAndDeathMothsChart = ChartFactory.createBarChart(
					Resurses.getString("STAT_BIRTH_MONTHS"),
					Resurses.getString("STAT_MONTHS"),
					Resurses.getString("STAT_PIECES"), dataset1,
					PlotOrientation.VERTICAL, true, true, false);
		} else {
			birthAndDeathMothsChart = ChartFactory.createBarChart(
					Resurses.getString("STAT_DEATH_MONTHS"),
					Resurses.getString("STAT_MONTHS"),
					Resurses.getString("STAT_PIECES"), dataset1,
					PlotOrientation.VERTICAL, true, true, false);
		}

		chartPanel.setChart(birthAndDeathMothsChart);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = birthAndDeathMothsChart.getCategoryPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		for (int xy = 0; xy < this.persons.length; xy++) {
			if (birth) {
				x = Utils.textDateMonth(this.persons[xy].getBirtDate());
			} else {
				x = Utils.textDateMonth(this.persons[xy].getDeatDate());
			}

			switch (x) {
			case 0:
				x0++;
				break;
			case 1:
				x1++;
				break;
			case 2:
				x2++;
				break;
			case 3:
				x3++;
				break;
			case 4:
				x4++;
				break;
			case 5:
				x5++;
				break;
			case 6:
				x6++;
				break;
			case 7:
				x7++;
				break;
			case 8:
				x8++;
				break;
			case 9:
				x9++;
				break;
			case 10:
				x10++;
				break;
			case 11:
				x11++;
				break;
			case 12:
				x12++;
				break;
			}
			//
		}
		dataset1.addValue(x0, Resurses.getString("STAT_NO_MONTH"), "");
		dataset1.addValue(x1, Resurses.getString("STAT_JAN"), "");
		dataset1.addValue(x2, Resurses.getString("STAT_FEB"), "");
		dataset1.addValue(x3, Resurses.getString("STAT_MAR"), "");
		dataset1.addValue(x4, Resurses.getString("STAT_APR"), "");
		dataset1.addValue(x5, Resurses.getString("STAT_MAY"), "");
		dataset1.addValue(x6, Resurses.getString("STAT_JUN"), "");
		dataset1.addValue(x7, Resurses.getString("STAT_JUL"), "");
		dataset1.addValue(x8, Resurses.getString("STAT_AUG"), "");
		dataset1.addValue(x9, Resurses.getString("STAT_SEP"), "");
		dataset1.addValue(x10, Resurses.getString("STAT_OCT"), "");
		dataset1.addValue(x11, Resurses.getString("STAT_NOV"), "");
		dataset1.addValue(x12, Resurses.getString("STAT_DEC"), "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		int x = this.statCombo.getSelectedIndex();
		switch (x) {
		case 0:
			statNoOfChildren();
			break;
		case 1:
			statChildrenVsNoChildren();
			break;
		case 2:
			statSex();
			break;
		case 3:
			statMarriedVsSingle();
			break;
		case 4:
			statBirthAndDeathMoths(true);
			break;
		case 5:
			statBirthAndDeathMoths(false);
			break;
		}
		this.chartPanel.repaint();
	}

}
