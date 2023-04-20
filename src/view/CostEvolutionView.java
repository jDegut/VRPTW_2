package view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * CostEvolutionView class : displays the cost evolution of the VRPTW problem (using JFreeChart)
 * @author Julian DEGUT
 */
public class CostEvolutionView {

	/**
	 * Display the chart of the cost evolution
	 * @param costs
	 */
	public static void displayChart(List<Double> costs) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Cost Evolution");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(createChartPanel(costs));
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

	/**
	 * Create the chart panel of the cost evolution
	 * @param costs
	 * @return
	 */
	private static JPanel createChartPanel(List<Double> costs) {
		String chartTitle = "Cost Evolution";
		String xAxisLabel = "Iteration";
		String yAxisLabel = "Cost (distance)";

		XYDataset dataset = createDataset(costs);

		JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.getXYPlot().getRenderer();
		renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // Augmente l'épaisseur du trait pour la série 0

		return new ChartPanel(chart);
	}

	/**
	 * Create the dataset of the cost evolution
	 * @param costs
	 * @return
	 */
	private static XYDataset createDataset(List<Double> costs) {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Costs");

		int index = 0;
		for (Double cost : costs) {
			series.add(index, cost);
			index++;
		}

		dataset.addSeries(series);
		return dataset;
	}

}
