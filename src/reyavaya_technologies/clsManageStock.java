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
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sanele
 * 
 * This class carries out the stock management
 * functions - adding a new product to stock, updating
 * existing stock, and viewing all stock.
 */
public class clsManageStock extends JDialog {
    
    public clsManageStock() {
        super(null, "Manage Product Stock", Dialog.ModalityType.APPLICATION_MODAL);
        this.setSize(400, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 10));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            
        mCreateDialogWindow();
        modelsAndDataMethods.mLoadToComboBox("SELECT Prod_name FROM tblProducts", cboProducts);
        this.setVisible(true);
    }
        
    private final JComboBox cboProducts = new JComboBox(); // to hold products names
    private final JTextField txtProductQuantity = new JTextField(); // input field for quantint of the product stock
        
    private int prod_id; // to hold the id of the product being worked with
        
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
    clsModelAndDataMethods modelsAndDataMethods = new clsModelAndDataMethods();
        
    // Assign value the variable that holds an ID of the current product being worked with
    private void mAssignProdId() {            
        this.prod_id = clsSQLMethods.mGetNumericField(
                "SELECT Prod_id FROM tblProducts WHERE Prod_name ='"+cboProducts.getSelectedItem().toString()+"'");
    }
        
    private void mCreateDialogWindow() {
        JPanel jpPanel = new JPanel(new BorderLayout(10, 20));
        jpPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        jpPanel = gui.mPreparePanel(jpPanel);
        
        jpPanel.add(mCreateWindowTop(), BorderLayout.NORTH);
        jpPanel.add(mCreateWindowCenter());
            
        this.add(jpPanel);  
    }
        
    private JPanel mCreateWindowTop() {
        JPanel jpTop = new JPanel(new BorderLayout());
        jpTop.setBorder(new EmptyBorder(10, 10, 10, 10));
        jpTop = gui.mPreparePanel(jpTop);
            
        jpTop.add(gui.mCreateLabel("Manage Product Stock", new Font("Tahoma", Font.BOLD, 28)),
                BorderLayout.NORTH);
            
        return jpTop;
    }
        
    private JPanel mCreateWindowCenter() {
        JPanel jpCenter = new JPanel(new GridLayout(10, 10, 10, 10));
        jpCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
        jpCenter = gui.mPreparePanel(jpCenter);
            
        jpCenter.add(gui.mAddComponent("Product", gui.mComboBoxDimensions(cboProducts, 80, 30)));
        jpCenter.add(gui.mAddComponent("Product Quantity", gui.mTextFieldDimensions(txtProductQuantity, 80, 30, 
                "Enter product quantity")));

        jpCenter.add(gui.mCreateLabel("", new Font("Tahoma", Font.BOLD, 28)));
        jpCenter.add(gui.mCreateLabel("", new Font("Tahoma", Font.BOLD, 28)));
            
        jpCenter.add(gui.mCreateButton(90, 30, "Save Stock", this::mSaveStock));
        jpCenter.add(gui.mCreateButton(90, 30, "View All Stock", this::mViewAllStock));
        jpCenter.add(gui.mCreateButton(90, 30, "Update Stock", this::mUpdateStock));
        jpCenter.add(gui.mCreateButton(90, 30, "Clear Text", this::mClear));
            
        return jpCenter;
    }
        
    private void mClear(ActionEvent e) {
        txtProductQuantity.setText("");
    }
        
    // Saves to the database stock details of a newly added product
    private void mSaveStock(ActionEvent e) {
        cboProducts.setEnabled(true);
        mAssignProdId();
        if(!txtProductQuantity.getText().equals("")) {
            if(clsSQLMethods.mCheckIfDetailsExist("SELECT Prod_id "
                    + "FROM tblStock WHERE Prod_id='"+prod_id+"'")) {
                
                JOptionPane.showMessageDialog(clsManageStock.this, "This product has a stock recording already! "
                        + "\nConsider updating the stock", "WARNING", JOptionPane.WARNING_MESSAGE);
                    
            } else if(!new clsValidationMethods().mCheckIfFieldIsOnlyDigits(
                    txtProductQuantity.getText()).equals("")){
                    
                JOptionPane.showMessageDialog(clsManageStock.this, 
                    new clsValidationMethods().mCheckIfFieldIsOnlyDigits(txtProductQuantity.getText()),
                    "WARNING", JOptionPane.WARNING_MESSAGE);
                    
            } else {
                    
                if(clsSQLMethods.mCreateRecord("INSERT INTO tblStock(Prod_id, Qty)"
                        + "VALUES('"+prod_id+"','"+Integer.parseInt(txtProductQuantity.getText())+"')")) {
                    
                    JOptionPane.showMessageDialog(clsManageStock.this, "Stock details saved", 
                            "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                        
                }
            }
        } else {
            JOptionPane.showMessageDialog(clsManageStock.this, "Provide product quantity", "WARNING", 
                    JOptionPane.WARNING_MESSAGE);
        }
    }
        
    private void mViewAllStock(ActionEvent e) {
            
        new clsViewStock().mDrawBarChart(new JDialog(null, Dialog.ModalityType.APPLICATION_MODAL),
                "Unfiltered Product Stock", modelsAndDataMethods.mCreateCategoryDataset(
                    "SELECT Prod_name, Qty FROM tblProducts, tblStock"
                        + " WHERE tblProducts.Prod_id = tblStock.Prod_id", "All Products"),
                        "All Products");
    }
        
    private void mUpdateStock(ActionEvent e) {
        clsManageStock.this.dispose();
        new clsUpdateStock();
    }
}