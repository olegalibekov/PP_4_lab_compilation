package prod;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.util.*;
import java.util.List;

class BarChartAWT extends ApplicationFrame {
    DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();

    public BarChartAWT() {
        super("Number Statistics");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Which number do you like?",
                "Category", "Amount",
                categoryDataset, PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);

        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        setContentPane(chartPanel);

        pack();
        RefineryUtilities.centerFrameOnScreen(this);
        setVisible(true);

        for (Integer i = 0; i < 10; i++) {
            categoryDataset.setValue(0, i, "Frequency");
        }
    }

    void updateDataset(List<Map.Entry<Integer, Integer>> chartMap) {
        chartMap.forEach(currentMapEntry ->
                categoryDataset.setValue(currentMapEntry.getValue(), currentMapEntry.getKey(), "Frequency"));
    }
}