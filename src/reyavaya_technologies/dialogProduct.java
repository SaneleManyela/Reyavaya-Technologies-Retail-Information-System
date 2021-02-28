/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author Sanele
 * 
 * This class define methods to add or update
 * a product
 */
public class dialogProduct extends javax.swing.JDialog {

    /**
     * Creates new form dialogProduct
     */
    public dialogProduct() {
        super(null, "Product Management", JDialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout(10, 20));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mFillComboSupplier();
    }
    
    private int intProdId; //an integer variable to hold a product id
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsModelAndDataMethods modelAndDataMethods = new clsModelAndDataMethods();
    
    // A public method to set product values to the graphical user interface
    public void mSetDetailsToGUI(int intProdId) {
        
        this.intProdId = intProdId; //assignment of a product id to the class integer variable
        
        txtProductName.setText(clsSQLMethods.mGetTextField(
                "SELECT Prod_Name FROM tblProducts WHERE Prod_id ='"+intProdId+"'"));
        
        txtProductBrand.setText(clsSQLMethods.mGetTextField(
                "SELECT Brand FROM tblProducts WHERE Prod_id ='"+intProdId+"'"));
        
        cboSupplierName.setSelectedItem(new clsDatabaseMethods().mGetTextField(
                "SELECT Supp_name FROM tblSupplier WHERE Supp_id ='"+
                clsSQLMethods.mGetNumericField("SELECT Supp_name FROM tblProducts "
                        + "WHERE Prod_id='"+intProdId+"'")+"'"));
        
        txtProductPurchasedPrice.setText(clsSQLMethods.mGetTextField(
                "SELECT Purchased_price FROM tblProducts WHERE Prod_id ='"+intProdId+"'"));
        
        txtProductSellingPrice.setText(clsSQLMethods.mGetTextField(
                "SELECT Selling_price FROM tblProducts WHERE Prod_id ='"+intProdId+"'"));
    }
        
    // A method that returns an array containing values from the GUI as its elements
    private String[] mGetDetailsFromGUI() {
        return new String[] {
            txtProductName.getText(), txtProductBrand.getText(),
            cboSupplierName.getSelectedItem().toString(),
            txtProductPurchasedPrice.getText(),
            txtProductSellingPrice.getText()
        };
    }
        
    // A method that returns a query that inserts product details to the database
    private String mCreateProductQuery() {
        return "INSERT INTO tblProducts(Prod_Name, Brand, Supp_name, Purchased_price, Selling_price)"
                + "VALUES('"+mGetDetailsFromGUI()[0]+"', '"+mGetDetailsFromGUI()[1]+"', '"
                    + clsSQLMethods.mGetNumericField(
                        "SELECT Supp_id FROM tblSupplier WHERE Supp_name ='"
                                + mGetDetailsFromGUI()[2]+"'")+"','"+Double.parseDouble(mGetDetailsFromGUI()[3])+
                "','"+Double.parseDouble(mGetDetailsFromGUI()[4])+"')";
    }
        
    // A method that returns a query that updates product details in the database
    private String mUpdateProductQuery() {
        return "UPDATE tblProducts SET Prod_Name ='"+mGetDetailsFromGUI()[0]+"', Brand='"+
                mGetDetailsFromGUI()[1] + "', Supp_name ='"+ clsSQLMethods.mGetNumericField(
                        "SELECT Supp_id FROM tblSupplier WHERE Supp_name ='"+mGetDetailsFromGUI()[2]+"'")
                +"', Purchased_price ='"+Double.parseDouble(mGetDetailsFromGUI()[3])+
                "', Selling_price ='"+Double.parseDouble(mGetDetailsFromGUI()[4]) +"' WHERE Prod_id ='"+intProdId+"'";
    }
    
    private void mFillComboSupplier() {
        modelAndDataMethods.mLoadToComboBox("SELECT Supp_Name FROM tblSupplier", cboSupplierName);
    }

    // A method that verifies if input has been passed to the GUI text boxes
    // and also validate data correctness in some fields. The method returns a 
    // string value that is related to the user if any or
    // all of the textboxes are not passed values
    private String mVerifyInput() {
        if(txtProductName.getText().equals("")) {
            return "Provide product name";
        } else if(txtProductBrand.getText().equals("")) {
            return "Provide product brand";
        }else if(txtProductPurchasedPrice.getText().equals("")) {
            return "Provide product purchased price";
        } else if(Double.parseDouble(txtProductPurchasedPrice.getText()) <= 0) {
            return "Provide a valid product purchased price";
        } else if(txtProductSellingPrice.getText().equals("")) {
            return "Provide product selling price";
        } else if(Double.parseDouble(txtProductSellingPrice.getText()) <= 0) {
            return "Provide a valid product selling price";
        }
        return "";
    }
    
    // A method that makes sure each product has a descriptive term of its category
    private String mValidateProductName() {
        if (!txtProductName.getText().endsWith("CPU") && !txtProductName.getText().endsWith("Fan")
                && !txtProductName.getText().endsWith("Hard Drive")
                && !txtProductName.getText().endsWith("Keyboards/Mouse")
                && !txtProductName.getText().endsWith("Memory Card")
                && !txtProductName.getText().endsWith("Monitor Screen")
                && !txtProductName.getText().endsWith("Motherboard")
                && !txtProductName.getText().endsWith("Power Supply Unit")
                && !txtProductName.getText().endsWith("Chasis")) {
                return "A product name must end with a\n category name of that product e.g CPU or Motherboard.";
        }
        return "";
    }
    
    // A method that creates a new product record in the database provided
    // the product doesn't already exist
    private void mAddProduct() {
        if(mVerifyInput().equals("") && !clsSQLMethods.mCheckIfDetailsExist(
                "SELECT Prod_name FROM tblProducts WHERE Prod_name='"+txtProductName.getText()+"'")
                && mValidateProductName().equals("")) {
            
            if(clsSQLMethods.mCreateRecord(mCreateProductQuery())) {
                JOptionPane.showMessageDialog(this, "Product recorded", "MESSAGE",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            
        } else if(clsSQLMethods.mCheckIfDetailsExist(
                "SELECT Prod_name FROM tblProducts WHERE Prod_name='"+txtProductName.getText()+"'")) {
            
             JOptionPane.showMessageDialog(this, "This product name already exist", "WARNING", JOptionPane.WARNING_MESSAGE);
        
        } else if(!mValidateProductName().equals("")){
        
            JOptionPane.showMessageDialog(this, mValidateProductName(), "WARNING", JOptionPane.WARNING_MESSAGE);
        
        } else{
            
            JOptionPane.showMessageDialog(this, mVerifyInput(),
                "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }
        
    // A method that instantiates clsSelectProductToUpdateDialog object to select a product to update
    private void mSelectProductToUpdate() { 
        new clsSelectProductToUpdateDialog("SELECT Prod_Name FROM tblProducts");  
    }
    
    // A method that saves updated product details to the database if
    // all text boxes contain values and are correct values
    private void mSaveUpdatedProductDetails() {
        if(!mVerifyInput().equals("") && mValidateProductName().equals("")) {
            
            JOptionPane.showMessageDialog(this, mVerifyInput(),
                "WARNING", JOptionPane.WARNING_MESSAGE);
            
        } else if(!mValidateProductName().equals("")){
            
            JOptionPane.showMessageDialog(this, mValidateProductName(), "WARNING", JOptionPane.WARNING_MESSAGE);
        } else {
            
            if(clsSQLMethods.mUpdateRecord(mUpdateProductQuery())) {
                
                JOptionPane.showMessageDialog(this, "Product updated", "MESSAGE",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // A method that clears text in GUI text boxes
    private void mClearTextFields() {
        txtProductName.setText("");
        txtProductBrand.setText("");
        txtProductPurchasedPrice.setText("");
        txtProductSellingPrice.setText("");
        btnUpdate.setText("Update");
    }
    
    /* A class that displays a GUI with a combo box populated
    *  with available products names. Accompanying this combo box
    *  is an 'ok' button that when selected, fetches from the database
    *  record details of the product that was selected in the combo box.
    *  The fetched details are set to the GUI
    */
    public class clsSelectProductToUpdateDialog extends JDialog {
        
        public clsSelectProductToUpdateDialog(String strQuery) {
            super(dialogProduct.this, "Update Product", Dialog.ModalityType.APPLICATION_MODAL); //Set the dialog to require all the focus
            this.setSize(400, 200); //sets size of the dialog
            this.setLocationRelativeTo(null); //displays the dialog at the very center
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Causes the dialog to be exited but not the entire app
            this.add(mUpdateDialogGUI(strQuery)); 
            this.setVisible(true);
        }   
               
        JComboBox cboCombo = new JComboBox();
        clsGUIDesignMethods gui = new clsGUIDesignMethods();
        
        //A method that creates this dialog's GUI then return it in a JPanel container
        private JPanel mUpdateDialogGUI(String strQuery) {
            JPanel jpContainer = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
            jpContainer.setBorder(new EmptyBorder(20, 20, 20, 20)); //Sets a border 
            jpContainer = gui.mPreparePanel(jpContainer); //Sets background colour to the JPanel
            
            jpContainer.add(gui.mCreateLabel("Select a product to update", new Font("Tahoma", Font.BOLD, 14)), BorderLayout.NORTH);
            jpContainer.add(mCreateDialogCenter(strQuery));  
            jpContainer.add(mCreateDialogBottom(this::mFetchForProductUpdate), BorderLayout.SOUTH);
            return jpContainer;
        }
        
        //A method that creates and populates with values the combo box seen in the GUI
        private JPanel mCreateDialogCenter(String strQuery) {
            JPanel jpCenterPart = new JPanel(new BorderLayout()); //A JPanel to contain the center part of the dialog GUI
            jpCenterPart.add(cboCombo, BorderLayout.CENTER);
            modelAndDataMethods.mLoadToComboBox(strQuery, cboCombo);
            return jpCenterPart;
        }

        //A method that creates the bottom part of the dialog GUI, it accepts
        //an argument of type ActionListener, this is for the dialog button
        private JPanel mCreateDialogBottom(ActionListener listener) {
            JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //A JPanel to contain the lower part of the GUI
            jpLowerPart = gui.mPreparePanel(jpLowerPart);
            JButton btn = gui.mCreateButton(90, 25, "Ok", listener);
            jpLowerPart.add(btn);
            return jpLowerPart;
        }
        
        /* A method that is invoked by an ActionEvent associated
         * with the 'Ok' button of this dialog screen.
         * This method in turn calls the mSetDetailsToGUI(Prod_id) method
         * and pass the mSet method a product id that is associated with
         * the product selected in the combo box  
        */
        private void mFetchForProductUpdate(ActionEvent e) {
            mSetDetailsToGUI(new clsDatabaseMethods().mGetNumericField(
                    "SELECT Prod_id FROM tblProducts WHERE Prod_name ='"+
                                cboCombo.getSelectedItem().toString()+"'"));
            this.setVisible(false);
        }
    }
    
    private void mUpdateProduct() {
        if(btnUpdate.getText().equals("Update")) {
            
            mSelectProductToUpdate();
            btnUpdate.setText("Save");
            
        } else if(btnUpdate.getText().equals("Save")) {
            
            mSaveUpdatedProductDetails();
            btnUpdate.setText("Update");
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpDialogPanel = new javax.swing.JPanel();
        lblDialogHeading = new javax.swing.JLabel();
        lblProductName = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        lblProductBrand = new javax.swing.JLabel();
        txtProductBrand = new javax.swing.JTextField();
        lblProductSupplier = new javax.swing.JLabel();
        txtProductPurchasedPrice = new javax.swing.JTextField();
        lblPurchasedPrice = new javax.swing.JLabel();
        cboSupplierName = new javax.swing.JComboBox<>();
        lblProductSellingPrice = new javax.swing.JLabel();
        txtProductSellingPrice = new javax.swing.JTextField();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpDialogPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblDialogHeading.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblDialogHeading.setText("Product Management");

        lblProductName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProductName.setText("Product Name");

        lblProductBrand.setBackground(new java.awt.Color(255, 255, 255));
        lblProductBrand.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProductBrand.setText("Product brand");

        lblProductSupplier.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProductSupplier.setText("Supplier name");

        lblPurchasedPrice.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblPurchasedPrice.setText("Product purchased price");

        lblProductSellingPrice.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProductSellingPrice.setText("Product selling price");

        btnCreate.setBackground(new java.awt.Color(255, 255, 255));
        btnCreate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(255, 255, 255));
        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(255, 255, 255));
        btnClear.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpDialogPanelLayout = new javax.swing.GroupLayout(jpDialogPanel);
        jpDialogPanel.setLayout(jpDialogPanelLayout);
        jpDialogPanelLayout.setHorizontalGroup(
            jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addComponent(btnCreate)
                        .addGap(39, 39, 39)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnClear))
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProductName)
                            .addComponent(lblProductBrand)
                            .addComponent(lblProductSupplier)
                            .addComponent(lblPurchasedPrice)
                            .addComponent(lblProductSellingPrice))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtProductBrand, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboSupplierName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductPurchasedPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtProductSellingPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40))
            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(lblDialogHeading)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jpDialogPanelLayout.setVerticalGroup(
            jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lblDialogHeading)
                .addGap(53, 53, 53)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductName)
                    .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductBrand)
                    .addComponent(txtProductBrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductSupplier)
                    .addComponent(cboSupplierName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPurchasedPrice)
                    .addComponent(txtProductPurchasedPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductSellingPrice)
                    .addComponent(txtProductSellingPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate)
                    .addComponent(btnUpdate)
                    .addComponent(btnClear))
                .addGap(44, 44, 44))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpDialogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpDialogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        mAddProduct();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        mUpdateProduct();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        mClearTextFields();
    }//GEN-LAST:event_btnClearActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(dialogProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dialogProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dialogProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dialogProduct.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                dialogProduct dialog = new dialogProduct();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cboSupplierName;
    private javax.swing.JPanel jpDialogPanel;
    private javax.swing.JLabel lblDialogHeading;
    private javax.swing.JLabel lblProductBrand;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblProductSellingPrice;
    private javax.swing.JLabel lblProductSupplier;
    private javax.swing.JLabel lblPurchasedPrice;
    private javax.swing.JTextField txtProductBrand;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtProductPurchasedPrice;
    private javax.swing.JTextField txtProductSellingPrice;
    // End of variables declaration//GEN-END:variables
}
