/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.time.*;
import javax.swing.*;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sanele
 * 
 * A class that handles password management, be it when a cashier
 * is promoted, a manager demoted or deactivated, or after seven days
 * have passed since an employee has updated their password
 */
public class clsPasswordManagement extends JDialog {
    
    // Constructs objects for purposes of deactivating a manager or
    // updating a password after every seven days
    public clsPasswordManagement() {
        super(null, "Password Authentication", Dialog.ModalityType.APPLICATION_MODAL);
        this.setSize(320, 190);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        mCreateGUI();
        this.setVisible(true);
    }
        
    // Constructs objects for granting managerial rights and promote a cashier
    public clsPasswordManagement(String strEmployee, String strGUIOfOrigin, String strButtonOfOrigin) {
        super(null, "Password Authentication", Dialog.ModalityType.APPLICATION_MODAL);
        this.setSize(330, 190);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                                
            
        if(strGUIOfOrigin.equals(clsEmployeeManagementTransactions.class.getName())) {
            
            String strFirstName = strEmployee.substring(0, strEmployee.indexOf(" ")).trim(); 
                    
            String strLastName = strEmployee.substring(strEmployee.indexOf(" "),
                                    strEmployee.trim().length()).trim();
            
            intEmployeeID = clsSQLMethods.mGetNumericField("SELECT Employee_id FROM tblEmployees WHERE Employee_name ='"+
                    strFirstName+"' AND Employee_surname='"+strLastName+"'");
                
            if(clsSQLMethods.mGetTextField("SELECT Employee_position "
                    + "FROM tblEmployees WHERE Employee_id="+
                    intEmployeeID).equals("Cashier")) {
                    
                txtUserPassword.setText("manager");
            }
        } 
            
        this.strButtonOfOrigin = strButtonOfOrigin;
        mCreateGUI();
        this.setVisible(true);
    }
        
    private final JPasswordField txtManagerPassword = new JPasswordField();
    private final JTextField txtUserPassword = new JTextField();
    private final JButton btnButton = new JButton();
        
    private int intEmployeeID;
    private String strButtonOfOrigin;
        
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
        
    private void mCreateGUI(){
        JPanel jpPanel = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
        jpPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); //Sets a border 
        jpPanel = gui.mPreparePanel(jpPanel);
        
        JLabel lblLabel = new JLabel();          
        JPanel jpCenterPart = new JPanel(new BorderLayout()); //A JPanel to contain the center part of the dialog GUI
            
        JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //A JPanel to contain the lower part of the GUI
        jpLowerPart = gui.mPreparePanel(jpLowerPart);
        btnButton.setBackground(new Color(255, 255, 255));
            
        if(new frmLogin().mGetEmployeeID() == 0){
                
            lblLabel.setText("Enter password of the current manager:"); //Creates an instance of JLabel and set its text                     
            jpCenterPart.add(txtManagerPassword, BorderLayout.CENTER);
                
            btnButton.setText("Deactivate");
            btnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    int intIdentifier = clsSQLMethods.mGetNumericField(
                            "SELECT Employee_id FROM tblEmployees WHERE Employee_password ='"+
                                    clsPasswordCryptography.mEncryptPassword(txtManagerPassword.getText().trim())+
                                    "' AND Employee_position ='Manager'");
                    
                    if(intIdentifier != 0) {
                        
                        if(clsSQLMethods.mCreateRecord("INSERT INTO tblDeactivatedEmployeeAccount(Deactivated_account, Account_type)"
                                        + "VALUES('"+intIdentifier+"', 'Manager')") 
                                && clsSQLMethods.mUpdateRecord("Update tblEmployees SET Discharged = 1 WHERE Employee_id ="+intIdentifier)) {
                        
                            JOptionPane.showMessageDialog(clsPasswordManagement.this, "Manager account has been deactivated",
                                        "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                        }
                        
                        clsPasswordManagement.this.dispose();
                            
                        if(JOptionPane.showConfirmDialog(null, "Would you like to promote a cashier to a managerial position?", 
                                "Grant Managerial Rights", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            
                            new clsEmployeeManagementTransactions("Grant Rights").setVisible(true);
                        } else{
                            JOptionPane.showMessageDialog(null, "Create a new manager account, the store needs a manager.", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(clsPasswordManagement.this, "WRONG Password!!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }                        
                }
            });
            jpLowerPart.add(btnButton);
        } else if(Period.between((LocalDate.parse((clsSQLMethods.mGetTextField(
                    "SELECT Employee_password_last_update FROM tblEmployees WHERE Employee_id="+
                            new frmLogin().mGetEmployeeID()).substring(0, 10).trim()))),
                                LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()))).getDays() > 7) {
            
            lblLabel.setText("Update your password."
                + " Last update: "+clsSQLMethods.mGetTextField(
                        "SELECT Employee_password_last_update FROM tblEmployees WHERE Employee_id="+
                                new frmLogin().mGetEmployeeID()).substring(0, 11));
                
            jpCenterPart.add(txtUserPassword, BorderLayout.CENTER);
                
            btnButton.setText("Update");
            btnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(txtUserPassword.getText().equals("")) {
                        
                        JOptionPane.showMessageDialog(clsPasswordManagement.this, "No value entered!", 
                                "WARNING", JOptionPane.WARNING_MESSAGE);
                        txtUserPassword.requestFocusInWindow();
                        
                    } else if(clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_id ="+new frmLogin().mGetEmployeeID()).equals("Manager")
                                && !txtUserPassword.getText().startsWith("manager")) {
                        
                        JOptionPane.showMessageDialog(clsPasswordManagement.this, "Manager passwords must begin with 'manager'", "WARNING", JOptionPane.WARNING_MESSAGE);
                            
                    } else if(clsSQLMethods.mUpdateRecord("UPDATE tblEmployees SET Employee_password='"+
                        clsPasswordCryptography.mEncryptPassword(txtUserPassword.getText().trim())+"', Employee_password_last_update='"+
                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+"' WHERE Employee_id="+new frmLogin().mGetEmployeeID())) {
                            
                        JOptionPane.showMessageDialog(clsPasswordManagement.this, "Your password has been updated.", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                        clsPasswordManagement.this.dispose();
                        frmMain frmMn = new frmMain();
                        frmMn.mUserAccessControl(clsSQLMethods.mGetTextField(
                            "SELECT Employee_position FROM tblEmployees WHERE Employee_id ="+new frmLogin().mGetEmployeeID()));
                        frmMn.setVisible(true);
                    }
                }
            });
            jpLowerPart.add(btnButton);
        } else {
                
            jpCenterPart.add(txtUserPassword, BorderLayout.CENTER);
                
            if(clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_id="+new frmLogin().mGetEmployeeID()).equals("Manager")
                    || clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_id="+new frmLogin().mGetEmployeeID()).equals("Cashier")) {
                if(strButtonOfOrigin == null) {
                    lblLabel.setText("Update manager password - remove 'manager'");
                    txtUserPassword.setText(clsPasswordCryptography.mDecryptPassword(clsSQLMethods.mGetTextField("SELECT Employee_password FROM tblEmployees WHERE Employee_id="+new frmLogin().mGetEmployeeID())));
                    btnButton.setText("Revoke");
                    btnButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            //Change manager password - remove 'manager' prefix, and employee_position to Cashier
                            if(txtUserPassword.getText().equals("")){
                                JOptionPane.showMessageDialog(clsPasswordManagement.this, "Provide employee new password.\n", "WARNING", JOptionPane.WARNING_MESSAGE);
                            
                            }else if(txtUserPassword.getText().startsWith("manager")) {
                                
                                JOptionPane.showMessageDialog(clsPasswordManagement.this, "This employee is being demoted.\n"
                                    + "Only Managers can have a password that is prifixed with 'manager'.", "WARNING", JOptionPane.WARNING_MESSAGE);
                                    
                            } else if(clsSQLMethods.mUpdateRecord("UPDATE tblEmployees SET Employee_password='"+ //Update password, password_last_update, and employee_position
                                clsPasswordCryptography.mEncryptPassword(txtUserPassword.getText().trim())+"', Employee_password_last_update='"+
                                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+"', Employee_position ='Cashier' WHERE Employee_id="+new frmLogin().mGetEmployeeID())) {
                                
                                JOptionPane.showMessageDialog(clsPasswordManagement.this, "Manager rights of this employee have been revoked.\n"
                                    + "Create a new manager account or promote a cashier. \nThe store needs a manager.", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                                clsPasswordManagement.this.dispose();
                            }
                        }
                    });
                    jpLowerPart.add(btnButton);
                } else if(strButtonOfOrigin.equals("Grant")) {
                    lblLabel.setText("Update this cashier password - add 'manager'");
                    //Change cashier password - add 'manager' prefix, and employee_position to Manager
                    btnButton.setText("Grant");
                    btnButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(txtUserPassword.getText().equals("")){
                                JOptionPane.showMessageDialog(clsPasswordManagement.this, "Provide employee new password.\n", "WARNING", JOptionPane.WARNING_MESSAGE);
                            }else if(!txtUserPassword.getText().startsWith("manager")) {
                                JOptionPane.showMessageDialog(clsPasswordManagement.this, "Manager passwords must begin with 'manager'", "WARNING", JOptionPane.WARNING_MESSAGE);
                            } else {
                                                    
                                if(!new clsValidationMethods().mEnsureStoreHasOnlyOneManager().equals("")){
                                
                                    String strPassword = clsPasswordCryptography.mDecryptPassword(clsSQLMethods.mGetTextField("SELECT Employee_password FROM tblEmployees WHERE Employee_id="+new frmLogin().mGetEmployeeID()));
                                    strPassword = strPassword.substring(7, strPassword.length()).trim();
                                    clsSQLMethods.mUpdateRecord("UPDATE tblEmployees SET Employee_password='"+
                                        clsPasswordCryptography.mEncryptPassword(strPassword)+"', Employee_password_last_update='"+
                                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+"', Employee_position ='Cashier' WHERE Employee_id="+new frmLogin().mGetEmployeeID());
                                }
                                //Update password, password_last_update, and employee_position
                                if(clsSQLMethods.mUpdateRecord("UPDATE tblEmployees SET Employee_password='"+
                                    clsPasswordCryptography.mEncryptPassword(txtUserPassword.getText().trim())+"', Employee_password_last_update='"+
                                    new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+"', Employee_position ='Manager' WHERE Employee_id="+intEmployeeID)) {
                                
                                    clsPasswordManagement.this.dispose();
                                    JOptionPane.showMessageDialog(clsPasswordManagement.this, "Manager rights have been granted", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                                    JOptionPane.showMessageDialog(clsPasswordManagement.this, "The current manager has been demoted to cashier.\n"
                                        + "Your Password is your old password without the 'manager' prefix.", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                    });
                    jpLowerPart.add(btnButton);
                }
            }
        }
        jpPanel.add(lblLabel, BorderLayout.NORTH);
        jpPanel.add(jpCenterPart);           
        jpPanel.add(jpLowerPart, BorderLayout.SOUTH); //Specifies where the lower part should be positioned
        this.add(jpPanel);
    }
}