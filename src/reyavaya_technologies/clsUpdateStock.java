/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import com.sun.java.swing.plaf.motif.MotifBorders;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sanele
 * 
 * This class is used to update product stalk that
 * already exist in the database. Everything else is
 * self-explanatory.
 */
public class clsUpdateStock extends JDialog{
    public clsUpdateStock() {
        super(null, "Update Stock", JDialog.ModalityType.APPLICATION_MODAL);
        this.setSize(500, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mUpdateGUI();
        this.setVisible(true);
    }
    
    private final JList lstProducts = new JList();
    private final JTextField txtProductQuantity = new JTextField();
        
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
        
    private int prod_id; // The ID of the product currently worked with 
        
    private void mAssignProdId() {            
        this.prod_id = clsSQLMethods.mGetNumericField(
                "SELECT Prod_id FROM tblStock WHERE Prod_id ="+ 
                        clsSQLMethods.mGetNumericField(
                            "SELECT Prod_id FROM tblProducts WHERE Prod_name ='"+
                                    lstProducts.getSelectedValue().toString()+"'"));
    }
        
    private void mUpdateGUI() {
        JPanel jpUpdate = new JPanel(new BorderLayout(10, 20));
        jpUpdate = gui.mPreparePanel(jpUpdate);
        jpUpdate.setBorder(new EmptyBorder(20, 10, 20, 10));
            
        jpUpdate.add(gui.mAddComponent("Product Quantity", 
                gui.mTextFieldDimensions(
                    txtProductQuantity, 155, 30, "Enter stock quantity of the product")),
                        BorderLayout.NORTH);   
            
        jpUpdate.add(mCreateList(lstProducts, mGetModel("SELECT Prod_id FROM tblStock"), 100, 60), BorderLayout.CENTER);
        jpUpdate.add(mCreateDialogBottom(), BorderLayout.SOUTH);
            
        this.add(jpUpdate);
    }
      
    private JScrollPane mCreateList(JList list, DefaultListModel model,
        int intWidth, int intHeight) {
            
        list.setModel(model);
        list.setEnabled(true);
        list.setOpaque(true);
        list.setBackground(new Color(255, 255, 255));
            
        JScrollPane scroll = new JScrollPane(list);
        scroll.setPreferredSize(new Dimension(intWidth, intHeight));
        scroll.setBorder(new MotifBorders.FrameBorder(scroll));
        return scroll;
    }
        
    private DefaultListModel mGetModel(String strQuery) {
        DefaultListModel model = new DefaultListModel();
        try{
            try (Statement stSQLQuery = clsSQLMethods.mConnectToDatabase().prepareStatement(strQuery)) {
                    
                ResultSet rs = stSQLQuery.executeQuery(strQuery);
                ArrayList lst = new ArrayList();
                    
                while(rs.next()) {
                    lst.add(rs.getString(1));
                }
                    
                String[] arrProductsNames = new String[clsSQLMethods.mGetNumericField(
                        "SELECT COUNT(Prod_id) FROM tblStock")];
                    
                for (int i = 0; i < lst.size(); i++) {
                    arrProductsNames[i] = (clsSQLMethods.mGetTextField(
                        "SELECT Prod_name FROM tblProducts WHERE Prod_id='" + lst.get(i) + "'"));
                }
                    
                for(String prodName : arrProductsNames) {
                    model.addElement(prodName);
                }
            }
        } catch(SQLException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Error while getting product names for stock update", JOptionPane.ERROR_MESSAGE);
        } 
        return model;
    }
        
    private JPanel mCreateDialogBottom() {
        JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); //A JPanel to contain the lower part of the GUI
        jpLowerPart = gui.mPreparePanel(jpLowerPart);
            
        jpLowerPart.add(gui.mCreateButton(100, 20, "Update Stock", this::mUpdateStock));
        jpLowerPart.add(gui.mCreateButton(100, 20, "Clear Text", this::mClear));
        jpLowerPart.add(gui.mCreateButton(100, 20, "Go Back", this::mReturnToPreviousPage));
        return jpLowerPart;
    }
        
    private String mUpdateStockQuery() {
        mAssignProdId();
        int intProductStockQuantity = clsSQLMethods.mGetNumericField("SELECT Qty FROM tblStock WHERE Prod_id='"+prod_id+"'");
        return "UPDATE tblStock SET Qty ='"+(intProductStockQuantity + Integer.parseInt(txtProductQuantity.getText()))+
            "' WHERE Prod_id='"+prod_id+"'";
    }
        
    private void mUpdateStock(ActionEvent e) {
        if(!txtProductQuantity.getText().equals("") && !lstProducts.isSelectionEmpty() && Integer.parseInt(txtProductQuantity.getText()) > 0) {
                
            if(new clsDatabaseMethods().mUpdateRecord(mUpdateStockQuery())) {
                    
                JOptionPane.showMessageDialog(this, "Product stock has been updated",
                            "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if(txtProductQuantity.getText().equals("")) {
                
            JOptionPane.showMessageDialog(this, "Provide product quantity", 
                        "WARNING", JOptionPane.WARNING_MESSAGE);
            txtProductQuantity.requestFocusInWindow();
                
        } else if(lstProducts.isSelectionEmpty()) {
                
            JOptionPane.showMessageDialog(this, "Select in the list a product to update",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
                
        } else if(Integer.parseInt(txtProductQuantity.getText()) <= 0) {
                
            JOptionPane.showMessageDialog(this, "Enter a practical product stock quantity",
                    "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }
        
    private void mClear(ActionEvent e) {
        txtProductQuantity.setText("");
    }
        
    // Objects of this class are instantiated in clsManageStock GUI
    private void mReturnToPreviousPage(ActionEvent e) {
        clsUpdateStock.this.dispose();
        new clsManageStock();
    }
}