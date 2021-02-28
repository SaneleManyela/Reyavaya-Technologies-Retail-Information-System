/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.*;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author Sanele
 */
public class clsSalesReport {
 
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsModelAndDataMethods modelsAndDataMethods = new clsModelAndDataMethods();
    
    //A method that returns the current day of the current week
    private int mGetDayOfTheWeek() {
        Calendar c = Calendar.getInstance();//Get a calendar instance
        return c.get(Calendar.DAY_OF_WEEK);
    }

    //A method that return the date at the begining of the current week
    private String mGetBeginningDateOfTheWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date()); // Now use today date
        c.add(Calendar.DAY_OF_WEEK, - mGetDayOfTheWeek());
        Date dt = c.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(dt);
    }
    
    //A method that returns a pie data set for a sales report
    private PieDataset mSalesReport(String strSales, String strQuery) {
        String[] arrProducts;
        java.util.List<String> lstFilteredProduct = new ArrayList<>();
        DefaultPieDataset data = new DefaultPieDataset();
        
        try(Statement stStatement = clsSQLMethods.mConnectToDatabase().prepareStatement(strQuery)){
            ResultSet rs = stStatement.executeQuery(strQuery);
            ResultSetMetaData rsmt = rs.getMetaData();
            int intColumnCount = rsmt.getColumnCount();
            while(rs.next()) {
                for(int i = 1; i <= intColumnCount; i++) {
                    lstFilteredProduct.add(String.valueOf(rs.getInt(i)));
                }
            }        
            switch(strSales) {
                case "Weekly Sales":
                    for(int i = 0; i < lstFilteredProduct.size(); i++) {
                        arrProducts = clsSQLMethods.mFetchRecord(mThisWeekSalesQuery(
                                Integer.parseInt(lstFilteredProduct.get(i))));
                        if(arrProducts.length != 0) {
                            data.setValue(arrProducts[0], Integer.parseInt(arrProducts[1]));
                        }
                    }
                    break;
                    
                case "Monthly Sales":
                    for(int i = 0; i < lstFilteredProduct.size(); i++) {
                        arrProducts = clsSQLMethods.mFetchRecord(mThisMonthSalesQuery(
                                Integer.parseInt(lstFilteredProduct.get(i))));
                        
                        if(arrProducts.length != 0) {
                            data.setValue(arrProducts[0], Integer.parseInt(arrProducts[1]));
                        }
                    }
                    break;
                    
                case "Yearly Sales":
                    for(int i = 0; i < lstFilteredProduct.size(); i++) {
                        arrProducts = clsSQLMethods.mFetchRecord(mThisYearSalesQuery(
                                Integer.parseInt(lstFilteredProduct.get(i))));
                        
                        if(arrProducts.length != 0) {
                            data.setValue(arrProducts[0], Integer.parseInt(arrProducts[1]));
                        }
                    }
                    break;
            }
            
        return data;
        }catch(SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return data;
    }
    
    private final String[] arrDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).split("-");
    
    // A query to get current week sales from the database
    private String mThisWeekSalesQuery(int intProdId) {
        return "SELECT DISTINCT Prod_name, (SELECT SUM(Sold_qty) FROM tblSales WHERE Prod_id ="+intProdId+") AS Sales_this_Week FROM tblSales, tblProducts"
                + " WHERE tblSales.Prod_id ="+intProdId+" AND tblProducts.Prod_id ="+intProdId+" AND Sale_Date BETWEEN '"+mGetBeginningDateOfTheWeek()+"' AND NOW()";
    }
    
    // A query to get current month sales from the database
    private String mThisMonthSalesQuery(int intProdId) {
        return "SELECT DISTINCT Prod_name, (SELECT SUM(Sold_qty) FROM tblSales WHERE Prod_id ="+intProdId+") AS Sales_This_Month "
                        + " FROM tblSales, tblProducts WHERE tblSales.Prod_id ="+intProdId+" AND tblProducts.Prod_id ="+intProdId+" AND Sale_Date LIKE '%"+arrDate[0]+"-"+arrDate[1]+"%'";
    }
        
    // A query to get current year sales from the database
    private String mThisYearSalesQuery(int intProdId) {
        return "SELECT DISTINCT Prod_name, (SELECT SUM(Sold_qty) FROM tblSales WHERE Prod_id ="+intProdId+") AS Sales_this_Week FROM tblSales, tblProducts"
                + " WHERE tblSales.Prod_id ="+intProdId+" AND tblProducts.Prod_id ="+intProdId+" AND Sale_Date LIKE '%"+arrDate[0]+"%'";
    }
    
    // Displays a GUI of the current month sales 
    public void mCurrentMonthSalesReport() {
        JDialog dialogSales = new JDialog(null, "Current Month Sales", Dialog.ModalityType.MODELESS);
        PieDataset dataset = mSalesReport("Monthly Sales",
                "SELECT DISTINCT tblSales.Prod_id FROM tblProducts, tblSales "
                        + "WHERE tblProducts.Prod_id = tblSales.Prod_id "
                        + "AND Sale_Date LIKE '%"+arrDate[0]+"-"+arrDate[1]+"%'");
        mCreateChartDialog(dataset, "Current Month Sales", dialogSales);
    }
    
    // Displays a GUI of the current week sales report
    public void mThisWeekSalesReport() {
        JDialog dialogSales = new JDialog(null, "Current Week Sales", Dialog.ModalityType.MODELESS);
        
        PieDataset dataset = mSalesReport("Weekly Sales", 
                "SELECT DISTINCT tblSales.Prod_id FROM tblProducts, tblSales"
                        + " WHERE tblProducts.Prod_id = tblSales.Prod_id "
                        + "AND Sale_Date BETWEEN '"+mGetBeginningDateOfTheWeek()+
                "' AND NOW()");
        
        mCreateChartDialog(dataset, "Current Week Sales", dialogSales);
    }
    
    // Displays a GUI of the current year sales report aggregating all the products sold
    public void mCurrentYearSales() {
        JDialog dialogSales = new JDialog(null, "Current Year Sales", Dialog.ModalityType.MODELESS);
        
        PieDataset dataset = mSalesReport("Yearly Sales", 
                "SELECT DISTINCT tblSales.Prod_id FROM tblProducts, tblSales "
                        + "WHERE Sale_Date LIKE '%"+arrDate[0]+"%'");
        mCreateChartDialog(dataset, "Current Year Sales", dialogSales);
    }
    
    // Create a chart dialog, a container of the sales report GUIs
    private void mCreateChartDialog(PieDataset dataset, String strTitle, JDialog dialogSales) {
        ChartPanel chartPanel = mCreateChartPanel(dataset, strTitle);
        Container content = dialogSales.getContentPane();
        content.add(chartPanel);
        
        dialogSales.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e){
                dialogSales.dispose();
            }
        });
        
        dialogSales.pack();
        dialogSales.setLocationRelativeTo(null);
        dialogSales.setVisible(true);
    }
        
    // As the method name says.
    private ChartPanel mCreateChartPanel(PieDataset dataset, String strTitle) {
        JFreeChart chart = ChartFactory.createPieChart(strTitle, dataset, true, true, false);
        chart.setBackgroundPaint(new Color(255, 255, 255));
        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.RIGHT);
        PiePlot plot = (PiePlot)chart.getPlot();
        plot.setBackgroundPaint(new Color(255, 255, 255));
        plot.setCircular(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {1} ({2})", NumberFormat.getNumberInstance(), 
                NumberFormat.getPercentInstance()));
        plot.setNoDataMessage("No data available");
        ChartPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
    }
}