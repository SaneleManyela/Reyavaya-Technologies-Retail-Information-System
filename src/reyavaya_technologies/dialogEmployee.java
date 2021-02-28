/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.text.SimpleDateFormat;
import javax.swing.*;

/**
 *
 * @author Sanele
 * 
 * This class allows each employees to update their details themselves
 */
public class dialogEmployee extends JDialog {

    /**
     * Creates new form dialogCashier
     */
    public dialogEmployee() {
        super(null, "Employee Dialog", JDialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        txtEmployeeAddress.setWrapStyleWord(true);
    }
    
    //An array that stores details from the database
    private String[] arrEmployeeDetails;
    
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
    clsValidationMethods validation = new clsValidationMethods();
    frmLogin frmLogin = new frmLogin();
    
    //A method that sets to the GUI values from the database
    private void mSetDetailsToGUI() {
        txtEmployeeName.setText(arrEmployeeDetails[0]);
        txtEmployeeSurname.setText(arrEmployeeDetails[1]);
        txtEmployeeEmail.setText(arrEmployeeDetails[2]);
        txtEmployeeAddress.setText(arrEmployeeDetails[3]);
        txtEmployeeContactNo.setText(arrEmployeeDetails[4]);
        txtEmployeePassword.setText(clsPasswordCryptography.mDecryptPassword(arrEmployeeDetails[5]));
        txtEmployeeUsername.setText(arrEmployeeDetails[6]);
    }
    
    private String[] mGetDetailsFromGUI() {
        return new String[] {
            txtEmployeeName.getText().trim(), txtEmployeeSurname.getText().trim(), 
            txtEmployeeEmail.getText().trim(), txtEmployeeAddress.getText().trim(),
            txtEmployeeContactNo.getText().trim(), txtEmployeePassword.getText().trim(), 
            txtEmployeeUsername.getText().trim()
        };
    }
       
    //A method that verifies if GUI textboxes have been passed a value
    private String mVerifyInput() {
        if(txtEmployeeName.getText().equals("")) {
            return "Provide your name!";
            
        } else if(txtEmployeeSurname.getText().equals("")) {
            return "Provide your surname!";
            
        } else if(txtEmployeeEmail.getText().equals("")) {
            return "Provide your email!";
            
        }else if(txtEmployeeAddress.getText().equals("")) {
            return "Provide your address!";
            
        } else if(txtEmployeeContactNo.getText().equals("")) {
            return "Provide your contact no!";
            
        } else if(txtEmployeeContactNo.getText().length() != 10) {
            return "A valid South African contact number can only be 10 digits!";
            
        }  else if(txtEmployeePassword.getText().equals("")) {
            return "Provide your password!";
            
        } else if(txtEmployeeUsername.getText().equals("")) {
            return "Provide your username!";
        }
        return ""; 
    }
    
    // A query to update employee details in the database
    private void mFetchForEmployeeUpdate() {
        arrEmployeeDetails = clsSQLMethods.mFetchRecord("SELECT Employee_name, Employee_surname, "
                + "Employee_email, Employee_address, Employee_contact, Employee_password, Username FROM tblEmployees "
                + "WHERE Employee_id ='"+frmLogin.mGetEmployeeID()+"'");
        mSetDetailsToGUI();
    }
    
    //A method that gets from GUI updated values and save them to the database 
    private void mSaveEmployeeUpdate() {
        if(mVerifyInput().equals("")) {
            if(validation.mValidateEmail(txtEmployeeEmail.getText()).equals("") &&
                    validation.mValidateContactNumber(txtEmployeeContactNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtEmployeeContactNo.getText()).equals("")) {
                
                if(!clsSQLMethods.mGetTextField("SELECT Username FROM tblEmployees WHERE Employee_id ="+frmLogin.mGetEmployeeID()).equals(
                        txtEmployeeUsername.getText()) &&  clsSQLMethods.mCheckIfDetailsExist(
                            "SELECT Username FROM tblEmployees"
                                    + " WHERE Username='"+txtEmployeeUsername.getText()+"'")) {
                    
                    JOptionPane.showMessageDialog(this, "This username already exists, Provide alternative.", 
                        "WARNING", JOptionPane.WARNING_MESSAGE);
                    
                } else if(clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_id ="+frmLogin.mGetEmployeeID()
                        ).equals("Manager") && !txtEmployeePassword.getText().startsWith("manager")) {
                    
                        JOptionPane.showMessageDialog(this, "Manager passwords must begin with 'manager'", "WARNING", JOptionPane.WARNING_MESSAGE);
                        
                } else if(clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_id ="+frmLogin.mGetEmployeeID()
                        ).equals("Cashier") && txtEmployeePassword.getText().startsWith("manager")){
                    
                    JOptionPane.showMessageDialog(this, "Consider entering a difeerent password!", "WARNING", JOptionPane.WARNING_MESSAGE);
                    
                } else {
                    arrEmployeeDetails = mGetDetailsFromGUI();
                    
                    String strLastPasswordUpdate;
                    
                    if(clsSQLMethods.mGetTextField(
                        "SELECT Employee_password FROM tblEmployees WHERE Employee_id="+frmLogin.mGetEmployeeID()).equals(
                                clsPasswordCryptography.mEncryptPassword(txtEmployeePassword.getText().trim()))) {
                        
                            strLastPasswordUpdate = clsSQLMethods.mGetTextField(
                        "SELECT Employee_password_last_update FROM tblEmployees WHERE Employee_id="+frmLogin.mGetEmployeeID());
                            
                    } else {
                        
                        strLastPasswordUpdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date());
                        
                    }
                    
                    if(clsSQLMethods.mUpdateRecord("UPDATE tblEmployees SET Employee_name ='"+arrEmployeeDetails[0]+
                            "', Employee_surname ='"+arrEmployeeDetails[1]+"', Employee_email='"+
                            arrEmployeeDetails[2] + "', Employee_address ='"+ arrEmployeeDetails[3] +"', Employee_contact ='"+
                                arrEmployeeDetails[4] +"', Employee_password='"+ clsPasswordCryptography.mEncryptPassword(arrEmployeeDetails[5]) +
                            "', Username='"+arrEmployeeDetails[6]+"', Employee_password_last_update='"+strLastPasswordUpdate+
                            "' WHERE Employee_id ="+frmLogin.mGetEmployeeID())) {
                        
                        JOptionPane.showMessageDialog(this, "Your details have been updated", "MESSAGE",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                    }
                }
            } else if(!validation.mValidateEmail(txtEmployeeEmail.getText()).equals("")) {
                
                    JOptionPane.showMessageDialog(this, validation.mValidateEmail(txtEmployeeEmail.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                    
                } else if(!validation.mValidateContactNumber(txtEmployeeContactNo.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mValidateContactNumber(txtEmployeeContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                    
                } else if(!validation.mCheckIfFieldIsOnlyDigits(txtEmployeeContactNo.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtEmployeeContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);                 
                }
        } else {
            JOptionPane.showMessageDialog(this, mVerifyInput(), "WARNING", JOptionPane.WARNING_MESSAGE);
        }   
    }
    
    //A method that clears textboxes of the GUI
    private void mClear() {
        txtEmployeeName.setText("");
        txtEmployeeSurname.setText("");
        txtEmployeeEmail.setText("");
        txtEmployeeAddress.setText("");
        txtEmployeeContactNo.setText("");
        txtEmployeePassword.setText("");
        txtEmployeeUsername.setText("");
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
        lblEmployeeName = new javax.swing.JLabel();
        txtEmployeeName = new javax.swing.JTextField();
        lblEmployeeEmail = new javax.swing.JLabel();
        txtEmployeeEmail = new javax.swing.JTextField();
        lblEmployeeAddress = new javax.swing.JLabel();
        lblEmployeeContactNo = new javax.swing.JLabel();
        txtEmployeeContactNo = new javax.swing.JTextField();
        lblEmployeePassword = new javax.swing.JLabel();
        txtEmployeePassword = new javax.swing.JTextField();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        spAddressScrollPane = new javax.swing.JScrollPane();
        txtEmployeeAddress = new javax.swing.JTextArea();
        lblEmployeeUsername = new javax.swing.JLabel();
        txtEmployeeUsername = new javax.swing.JTextField();
        lblEmployeeSurname = new javax.swing.JLabel();
        txtEmployeeSurname = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpDialogPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblDialogHeading.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblDialogHeading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDialogHeading.setText("Employee Account");

        lblEmployeeName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmployeeName.setText("Employee name");

        lblEmployeeEmail.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmployeeEmail.setText("Employee email");

        lblEmployeeAddress.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmployeeAddress.setText("Employee address");

        lblEmployeeContactNo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmployeeContactNo.setText("Employee contact no.");

        txtEmployeeContactNo.setToolTipText("");

        lblEmployeePassword.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmployeePassword.setText("Employee Password");

        btnSave.setBackground(new java.awt.Color(255, 255, 255));
        btnSave.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
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

        txtEmployeeAddress.setColumns(20);
        txtEmployeeAddress.setRows(5);
        spAddressScrollPane.setViewportView(txtEmployeeAddress);

        lblEmployeeUsername.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmployeeUsername.setText("Employee Username");

        lblEmployeeSurname.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmployeeSurname.setText("Employee surname");

        javax.swing.GroupLayout jpDialogPanelLayout = new javax.swing.GroupLayout(jpDialogPanel);
        jpDialogPanel.setLayout(jpDialogPanelLayout);
        jpDialogPanelLayout.setHorizontalGroup(
            jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(lblDialogHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addComponent(lblEmployeeSurname)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addComponent(lblEmployeeUsername)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblEmployeeName)
                                    .addComponent(lblEmployeeEmail)
                                    .addComponent(lblEmployeeAddress)
                                    .addComponent(lblEmployeeContactNo)
                                    .addComponent(lblEmployeePassword))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtEmployeeName)
                                    .addComponent(txtEmployeeEmail)
                                    .addComponent(txtEmployeeContactNo)
                                    .addComponent(txtEmployeePassword, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                    .addComponent(spAddressScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(txtEmployeeUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                    .addComponent(txtEmployeeSurname)))
                            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                                .addComponent(btnUpdate)
                                .addGap(40, 40, 40)
                                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40))))
        );
        jpDialogPanelLayout.setVerticalGroup(
            jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblDialogHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmployeeName)
                    .addComponent(txtEmployeeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmployeeSurname)
                    .addComponent(txtEmployeeSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmployeeEmail)
                    .addComponent(txtEmployeeEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmployeeAddress)
                    .addComponent(spAddressScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmployeeContactNo)
                    .addComponent(txtEmployeeContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmployeePassword)
                    .addComponent(txtEmployeePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmployeeUsername)
                    .addComponent(txtEmployeeUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnSave)
                    .addComponent(btnClear))
                .addGap(30, 30, 30))
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

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        mSaveEmployeeUpdate();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        mFetchForEmployeeUpdate();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        mClear();
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
            java.util.logging.Logger.getLogger(dialogEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dialogEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dialogEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dialogEmployee.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                dialogEmployee dialog = new dialogEmployee();
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
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JPanel jpDialogPanel;
    private javax.swing.JLabel lblDialogHeading;
    private javax.swing.JLabel lblEmployeeAddress;
    private javax.swing.JLabel lblEmployeeContactNo;
    private javax.swing.JLabel lblEmployeeEmail;
    private javax.swing.JLabel lblEmployeeName;
    private javax.swing.JLabel lblEmployeePassword;
    private javax.swing.JLabel lblEmployeeSurname;
    private javax.swing.JLabel lblEmployeeUsername;
    private javax.swing.JScrollPane spAddressScrollPane;
    private javax.swing.JTextArea txtEmployeeAddress;
    private javax.swing.JTextField txtEmployeeContactNo;
    private javax.swing.JTextField txtEmployeeEmail;
    private javax.swing.JTextField txtEmployeeName;
    private javax.swing.JTextField txtEmployeePassword;
    private javax.swing.JTextField txtEmployeeSurname;
    private javax.swing.JTextField txtEmployeeUsername;
    // End of variables declaration//GEN-END:variables
}
