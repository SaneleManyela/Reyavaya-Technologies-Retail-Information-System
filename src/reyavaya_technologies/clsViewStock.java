/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;

/**
 *
 * @author Sanele
 * 
 * This class defines a graphical user interface to
 * display available product stock details 
 */
public class clsViewStock extends JDialog {
        
    public clsViewStock() {
        super(null, "Product Stock", ModalityType.APPLICATION_MODAL);
        this.setSize(300, 160);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mCreateGUI();
    }
    
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
    
    private void mCreateGUI() {
        JPanel jpPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        jpPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        jpPanel = gui.mPreparePanel(jpPanel);
        jpPanel.add(gui.mCreateButton(120, 30, "View stock by product", this::mViewStockByProductCategory));
        jpPanel.add(gui.mCreateButton(120, 30, "View all stock", this::mViewAllStock));
        this.add(jpPanel);
    }
                
    private void mViewStockByProductCategory(ActionEvent e) {
        this.dispose();
        new clsFilterProductByCategory("View Stock");
        
    }
    
    private void mViewAllStock(ActionEvent e) {
        this.dispose();
        mDrawBarChart(new JDialog(null, ModalityType.APPLICATION_MODAL), "Unfiltered Product Stock", new clsModelAndDataMethods().mCreateCategoryDataset(
                    "SELECT Prod_name, Qty FROM tblProducts, tblStock WHERE tblProducts.Prod_id = tblStock.Prod_id", "All Products"),"All Products");
        
    }
             
    public void mDrawBarChart(JDialog stock, String strTitle, CategoryDataset dataset, String strCategory) {
        Container content = stock.getContentPane();
        content.setBackground(new Color(255, 255, 255));
        
        //Create the Chart
        JFreeChart chart = ChartFactory.createBarChart(
            strCategory+" Products Stock", strCategory, "Quantity", 
                    dataset, PlotOrientation.HORIZONTAL, false, true, false);
        
        content.add(new ChartPanel(chart));
        
        stock.setTitle(strTitle);
        stock.setSize(1000, 600);
        stock.setLocationRelativeTo(null);
        stock.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        stock.setVisible(true);
    }
}