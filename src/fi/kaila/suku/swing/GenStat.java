package fi.kaila.suku.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

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
import fi.kaila.suku.util.SukuException;
import fi.kaila.suku.util.Utils;
import fi.kaila.suku.util.pojo.PersonShortData;
import fi.kaila.suku.util.pojo.PlaceLocationData;
import fi.kaila.suku.util.pojo.SukuData;

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
		statCombo.addItem(Resurses.getString("STAT_BIRTH_PLACES"));
		statCombo.addItem(Resurses.getString("STAT_DEATH_PLACES"));
		statCombo.addItem(Resurses.getString("STAT_FIRST_NAMES"));
		statCombo.addItem(Resurses.getString("STAT_LAST_NAMES"));

		statCombo.setActionCommand(Resurses.SHOWGRID);
		statCombo.addActionListener(this);

		chartPanel = new ChartPanel(null);
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

		this.pack();

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
		int x, x1 = 0, x2 = 0, x3 = 0, x4 = 0, x5 = 0, x6 = 0, x7 = 0, x8 = 0, x9 = 0, x10 = 0, x11 = 0, x12 = 0, x13 = 0, x14 = 0, x15 = 0, xx = 0;

		// add the chart to a panel...
		final DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
		final JFreeChart noOfChildrenChart = ChartFactory.createBarChart(
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
			if (x < 16) {
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
				}
			} else {
				xx++;
			}
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
		dataset1.addValue(xx, ">15", "");
	}

	private void statChildrenVsNoChildren() {
		int x0 = 0, x1 = 0;
		int x;

		// add the chart to a panel...
		final DefaultPieDataset dataset = new DefaultPieDataset();
		final JFreeChart ChildrenVsNoChildrenChart = ChartFactory
				.createPieChart(
						Resurses.getString("STAT_CHILDREN_VS_NO_CHILDREN"),
						dataset, false, true, false);

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
		final JFreeChart sexChart = ChartFactory.createPieChart(
				Resurses.getString("STAT_SEX"), dataset2, false, true, false);

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
		final JFreeChart marriedVsSingleChart = ChartFactory.createPieChart(
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

		String caption;
		if (birth) {
			caption = Resurses.getString("STAT_BIRTH_MONTHS");
		} else {
			caption = Resurses.getString("STAT_DEATH_MONTHS");
		}
		final JFreeChart birthAndDeathMothsChart = ChartFactory.createBarChart(
				caption, Resurses.getString("STAT_MONTHS"),
				Resurses.getString("STAT_PIECES"), dataset1,
				PlotOrientation.VERTICAL, true, true, false);

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

	private void statBirthDeathPlaces(boolean birth) {

		// add the chart to a panel...
		final DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();

		String caption;
		if (birth) {
			caption = Resurses.getString("STAT_BIRTH_PLACES");
		} else {
			caption = Resurses.getString("STAT_DEATH_PLACES");
		}
		final JFreeChart birthAndDeathPlacesChart = ChartFactory
				.createBarChart(caption, Resurses.getString("STAT_PLACES"),
						Resurses.getString("STAT_PIECES"), dataset1,
						PlotOrientation.VERTICAL, true, true, false);

		chartPanel.setChart(birthAndDeathPlacesChart);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = birthAndDeathPlacesChart.getCategoryPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		HashMap<String, String> ccodes = new HashMap<String, String>();
		try {
			SukuData countdata = Suku.kontroller.getSukuData("cmd=get",
					"type=ccodes");

			for (String nxt : countdata.generalArray) {
				if (nxt != null) {
					String parts[] = nxt.split(";");
					if (parts.length == 2) {
						ccodes.put(parts[0], parts[1]);
					}
				}
			}
		} catch (SukuException e1) {
			// Do nothing
		}

		HashMap<String, PlaceLocationData> paikat = new HashMap<String, PlaceLocationData>();
		int idx;
		String paikka;
		String maa;
		String ccode;
		String defaultCountry = Resurses.getDefaultCountry();
		PlaceLocationData place;

		for (idx = 0; idx < persons.length; idx++) {
			if (birth) {
				paikka = persons[idx].getBirtPlace();
			} else {
				paikka = persons[idx].getDeatPlace();
			}
			if (paikka != null) {

				if (birth) {
					maa = persons[idx].getBirthCountry();
				} else {
					maa = persons[idx].getDeatCountry();
				}
				if (maa != null)
					maa = maa.toUpperCase();

				if (maa == null) {
					ccode = defaultCountry;
				} else {
					ccode = ccodes.get(maa.toUpperCase());
					if (ccode == null) {
						ccode = defaultCountry;
					}
				}
				place = paikat.get(paikka.toUpperCase() + ";" + ccode);
				if (place == null) {
					place = new PlaceLocationData(paikka, ccode);

					paikat.put(paikka.toUpperCase() + ";" + ccode, place);
				} else {
					place.increment();
				}
			}
		}

		SukuData request = new SukuData();
		request.places = new PlaceLocationData[paikat.size()];

		Iterator<String> it = paikat.keySet().iterator();
		idx = 0;
		while (it.hasNext()) {
			request.places[idx] = paikat.get(it.next());
			idx++;
		}

		int x = request.places.length;
		quicksort(request.places, 0, x - 1);

		int y = 10;
		if (x > 10) {
			y = x - 10;
		} else {
			y = x;
		}

		for (int xx = y; xx < x; xx++) {
			dataset1.addValue(request.places[xx].getCount(),
					request.places[xx].getName(), "");
		}
	}

	private void statFirstLastNames(boolean birth) {

		// add the chart to a panel...
		final DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();

		String caption;
		if (birth) {
			caption = Resurses.getString("STAT_FIRST_NAMES");
		} else {
			caption = Resurses.getString("STAT_LAST_NAMES");
		}
		final JFreeChart firstAndLastNamesChart = ChartFactory.createBarChart(
				caption, Resurses.getString("STAT_NAMES"),
				Resurses.getString("STAT_PIECES"), dataset1,
				PlotOrientation.VERTICAL, true, true, false);

		chartPanel.setChart(firstAndLastNamesChart);

		// get a reference to the plot for further customisation...
		final CategoryPlot plot = firstAndLastNamesChart.getCategoryPlot();
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		// change the auto tick unit selection to integer units only...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		int idx;
		String sname;
		NameData name = null;
		HashMap<String, NameData> snames = new HashMap<String, NameData>();
		NameData[] names = null;

		for (idx = 0; idx < persons.length; idx++) {
			if (birth) {
				sname = persons[idx].getGivenname(0);
			} else {
				sname = persons[idx].getSurname(0);
			}
			name = snames.get(sname);
			if (sname != null) {
				if (name == null) {
					name = new NameData(sname);

					snames.put(sname, name);
				} else {
					name.increment();
				}
			}
		}

		names = new NameData[snames.size()];

		Iterator<String> it = snames.keySet().iterator();
		idx = 0;
		while (it.hasNext()) {
			names[idx] = snames.get(it.next());
			idx++;
		}

		int x = names.length;
		quicksortnames(names, 0, x - 1);

		int y = 10;
		if (x > 10) {
			y = x - 10;
		} else {
			y = x;
		}

		for (int xx = y; xx < x; xx++) {
			dataset1.addValue(names[xx].getCount(), names[xx].getName(), "");
		}
	}

	/**
	 * Quicksort.
	 * 
	 * @param array
	 *            the array
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	private static void quicksort(PlaceLocationData array[], int left, int right) {
		int leftIdx = left;
		int rightIdx = right;
		PlaceLocationData temp;

		if (right - left + 1 > 1) {
			int pivot = (left + right) / 2;
			while ((leftIdx <= pivot) && (rightIdx >= pivot)) {
				while ((array[leftIdx].getCount() < array[pivot].getCount())
						&& (leftIdx <= pivot)) {
					leftIdx = leftIdx + 1;
				}
				while ((array[rightIdx].getCount() > array[pivot].getCount())
						&& (rightIdx >= pivot)) {
					rightIdx = rightIdx - 1;
				}
				temp = array[leftIdx];
				array[leftIdx] = array[rightIdx];
				array[rightIdx] = temp;
				leftIdx = leftIdx + 1;
				rightIdx = rightIdx - 1;
				if (leftIdx - 1 == pivot) {
					pivot = rightIdx = rightIdx + 1;
				} else if (rightIdx + 1 == pivot) {
					pivot = leftIdx = leftIdx - 1;
				}
			}
			quicksort(array, left, pivot - 1);
			quicksort(array, pivot + 1, right);
		}
	}

	/**
	 * Quicksort.
	 * 
	 * @param array
	 *            the array
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	private static void quicksortnames(NameData array[], int left, int right) {
		int leftIdx = left;
		int rightIdx = right;
		NameData temp;

		if (right - left + 1 > 1) {
			int pivot = (left + right) / 2;
			while ((leftIdx <= pivot) && (rightIdx >= pivot)) {
				while ((array[leftIdx].getCount() < array[pivot].getCount())
						&& (leftIdx <= pivot)) {
					leftIdx = leftIdx + 1;
				}
				while ((array[rightIdx].getCount() > array[pivot].getCount())
						&& (rightIdx >= pivot)) {
					rightIdx = rightIdx - 1;
				}
				temp = array[leftIdx];
				array[leftIdx] = array[rightIdx];
				array[rightIdx] = temp;
				leftIdx = leftIdx + 1;
				rightIdx = rightIdx - 1;
				if (leftIdx - 1 == pivot) {
					pivot = rightIdx = rightIdx + 1;
				} else if (rightIdx + 1 == pivot) {
					pivot = leftIdx = leftIdx - 1;
				}
			}
			quicksortnames(array, left, pivot - 1);
			quicksortnames(array, pivot + 1, right);
		}
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
		case 6:
			statBirthDeathPlaces(true);
			break;
		case 7:
			statBirthDeathPlaces(false);
			break;
		case 8:
			statFirstLastNames(true);
			break;
		case 9:
			statFirstLastNames(false);
			break;
		}
		this.chartPanel.repaint();
	}

	private static class NameData implements Serializable {

		private static final long serialVersionUID = 1L;
		private String name = null;
		private int counter = 0;

		public NameData(String place) {
			this.name = place;
			this.counter = 1;

		}

		public void increment() {
			this.counter++;
		}

		public String getName() {
			return this.name;
		}

		public int getCount() {
			return this.counter;
		}
	}

}
