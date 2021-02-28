/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import javax.swing.*;
/**
 *
 * @author Sanele
 * 
 * This is the interface used to log into the system,
 * literally the face of the application. It allows a user
 * to specify their account name and password to login or
 * add a managerial account which can be used to login and
 * add cashier accounts, amongst other functions.
 */
public class frmLogin extends javax.swing.JFrame {

    /**
     * Creates new form frmLogin
     */
    public frmLogin() {
        initComponents();
        this.setTitle("Login");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        txtUsername.requestFocusInWindow();
    }
    
    static private int intId; // A static variable to store logged in employee ID
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods(); // Defines methods to carry out database transactions
    
    // This method is called outside this class and returns an ID of the currently logged in employee
    public int mGetEmployeeID() {
        return intId;
    }
    
    // Called outside this class, the method sets (re-initialise) the ID of a recently logged in employee,
    // in the same run of the program. That is, the programmed has not been exited between logins.
    public void mSetEmployeeID(int id) {
        intId = id;
    }
    
    /* A method that authenticates and logs in a user.
     * It begins by evaluating if the GUI text fields have been passed values,
     * then it checks in the database if such an account with that username and password exists.
     * If the account indeed exists and it has not been deactiaved, the Employee ID is set and
     * the employee password is checked if its more than 7 days old. Should this be so,
     * the user is prompted to update their password while at the same time the system
     * guards against the re-use of the current password. Otherwise
     * the program handler passes control to the application's inner interface, frmMain.
    */
    private void mLogin() {
        if(!txtUsername.getText().equals("") || !txtPassword.getText().equals("")){
            try{
                if(clsSQLMethods.mCheckIfDetailsExist(
                    "SELECT Employee_name, Employee_password FROM tblEmployees WHERE Username ='"+
                        txtUsername.getText()+"' AND Employee_password ='"+clsPasswordCryptography.mEncryptPassword(txtPassword.getText().trim())+"'")
                            && !clsSQLMethods.mCheckIfDetailsExist(
                            "SELECT Deactivated_account FROM tblDeactivatedEmployeeAccount WHERE Deactivated_account="+
                                new clsDatabaseMethods().mGetNumericField("SELECT Employee_id FROM tblEmployees WHERE Username='"+txtUsername.getText()+
                                        "' AND Employee_password ='"+clsPasswordCryptography.mEncryptPassword(txtPassword.getText().trim())+"'"))){
                    
                        mSetEmployeeID(clsSQLMethods.mGetNumericField(
                                "SELECT Employee_id FROM tblEmployees WHERE Username ='"+txtUsername.getText()+"' AND "
                           + "Employee_password ='"+clsPasswordCryptography.mEncryptPassword(txtPassword.getText().trim())+"'"));
                        
                        if(Period.between((LocalDate.parse((clsSQLMethods.mGetTextField(
                                    "SELECT Employee_password_last_update FROM tblEmployees WHERE Username='"+
                                            txtUsername.getText().trim()+"' AND Employee_password ='"+clsPasswordCryptography.mEncryptPassword(txtPassword.getText().trim())+"'").substring(0, 10).trim()))),
                                LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()))).getDays() > 7) {
                
                                new clsPasswordManagement();
                        } else {
                            frmMain frmMn = new frmMain();
                            frmMn.mUserAccessControl(clsSQLMethods.mGetTextField(
                                    "SELECT Employee_position FROM tblEmployees WHERE Username ='"+
                                        txtUsername.getText()+"' AND Employee_password ='"+clsPasswordCryptography.mEncryptPassword(txtPassword.getText().trim())+"'"));
                            frmMn.setResizable(false);
                            this.dispose();
                            frmMn.setVisible(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Attempt to login failed", "WARNING",
                            JOptionPane.WARNING_MESSAGE);
                    }
            }catch(HeadlessException ex){
                JOptionPane.showMessageDialog(frmLogin.this, "Incorrect Credentials", "WARNING",
                            JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frmLogin.this, "Both fields are required",
                    "WARNING",JOptionPane.WARNING_MESSAGE);
            if(txtUsername.getText().equals("") && !txtPassword.getText().equals("")){
                txtUsername.requestFocusInWindow();
            } else if(txtPassword.getText().equals("") && !txtUsername.getText().equals("")){
                txtPassword.requestFocusInWindow();
            } else if(txtUsername.getText().equals("") && txtPassword.getText().equals("")) {
                txtUsername.requestFocusInWindow();
            }
        }
    }

    /* The methods creates an object of the form frmEmployeePortal for purposes
     * of creating a new manager account if there is not an active manager at the store
     * or to deactivate an existing manager.
    */
    private void mAddManagerAccount() {
        new frmEmployeePortal().setVisible(true); //Instantiation of a class to add & manage employee account
        this.dispose();
    }
    
    private void mClear() {
        txtUsername.setText("");
        txtPassword.setText("");
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dsktpLogin = new javax.swing.JDesktopPane();
        jpLogin = new javax.swing.JPanel();
        lblLogin = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        btnClear = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        btnCreateAcc = new javax.swing.JButton();
        txtPassword = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        dsktpLogin.setForeground(new java.awt.Color(255, 255, 255));

        jpLogin.setBackground(new java.awt.Color(255, 255, 255));

        lblLogin.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblLogin.setText("Login");

        lblUsername.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblUsername.setText("Username");

        lblPassword.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblPassword.setText("Password");

        btnClear.setBackground(new java.awt.Color(255, 255, 255));
        btnClear.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnLogin.setBackground(new java.awt.Color(255, 255, 255));
        btnLogin.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnLogin.setText("Login");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        btnCreateAcc.setBackground(new java.awt.Color(255, 255, 255));
        btnCreateAcc.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCreateAcc.setText("Add Acc");
        btnCreateAcc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateAccActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpLoginLayout = new javax.swing.GroupLayout(jpLogin);
        jpLogin.setLayout(jpLoginLayout);
        jpLoginLayout.setHorizontalGroup(
            jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpLoginLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpLoginLayout.createSequentialGroup()
                        .addComponent(btnCreateAcc, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58)
                        .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpLoginLayout.createSequentialGroup()
                        .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUsername)
                            .addComponent(lblPassword))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtUsername)
                            .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))))
                .addGap(70, 70, 70))
            .addGroup(jpLoginLayout.createSequentialGroup()
                .addGap(263, 263, 263)
                .addComponent(lblLogin)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpLoginLayout.setVerticalGroup(
            jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpLoginLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(lblLogin)
                .addGap(64, 64, 64)
                .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71)
                .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(88, 218, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpLoginLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogin)
                    .addComponent(btnClear)
                    .addComponent(btnCreateAcc))
                .addGap(77, 77, 77))
        );

        dsktpLogin.setLayer(jpLogin, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout dsktpLoginLayout = new javax.swing.GroupLayout(dsktpLogin);
        dsktpLogin.setLayout(dsktpLoginLayout);
        dsktpLoginLayout.setHorizontalGroup(
            dsktpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dsktpLoginLayout.createSequentialGroup()
                .addGap(230, 230, 230)
                .addComponent(jpLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(232, Short.MAX_VALUE))
        );
        dsktpLoginLayout.setVerticalGroup(
            dsktpLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dsktpLoginLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jpLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dsktpLogin, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dsktpLogin)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateAccActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateAccActionPerformed
        mAddManagerAccount();
    }//GEN-LAST:event_btnCreateAccActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        mClear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        mLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

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
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCreateAcc;
    private javax.swing.JButton btnLogin;
    private javax.swing.JDesktopPane dsktpLogin;
    private javax.swing.JPanel jpLogin;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
