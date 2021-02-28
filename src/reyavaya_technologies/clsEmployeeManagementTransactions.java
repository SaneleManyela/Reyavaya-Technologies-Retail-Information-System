/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.*;;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sanele
 * 
 * The class assists in executing different employee
 * management functions such as updating employee details,
 * facilitating employee (7 day) password change,
 * deactivating an employee account, granting and revoking 
 * managerial rights
 */
public class clsEmployeeManagementTransactions extends JDialog{
    
    public clsEmployeeManagementTransactions(String str) {
        super(null, str, Dialog.ModalityType.APPLICATION_MODAL); // Sets title of this JDialog box then set it to rquire all the focus
        this.setSize(400, 200); // sets size of the dialog
        this.setResizable(false);
        this.setLocationRelativeTo(null); // displays the dialog at the very center
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Causes the dialog to be exited but not the entire app
        mCreateDialog(str); // Calls a method to create the dialog GUI
    }
        
    private final JComboBox cboEmployeeNames = new JComboBox(); // A combo box object to hold employee data
    private String[] arrValues; //A string array to hold employee details from the database
        
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods(); // gives access to methods for working with the database  
    clsGUIDesignMethods gui = new clsGUIDesignMethods(); // A class with definition of method to help with GUI design
    clsModelAndDataMethods modelAndDataMethods = new clsModelAndDataMethods();  
        
    private static int intID; // To hold the ID of an employee being worked with
         
    // This returns the employee ID of the last employee worked with
    public int mSelectedEmployeeID() {
        return intID;
    }
        
    // A method that is called when a cashier is being granted managerial rights and promoted
    private String mInput() {
        return JOptionPane.showInputDialog(clsEmployeeManagementTransactions.this,
                "Enter a password for this employee - prefix with 'manager'",
                "Managerial Password", JOptionPane.INFORMATION_MESSAGE);
    }
        
    //A method that creates the GUI of this dialog by specifying how each component
    //should be positioned
    private void mCreateDialog(String str) {
        JPanel jpPanel = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
        jpPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        jpPanel = gui.mPreparePanel(jpPanel); // A call to GUI design method to set the background colour
            
        // Text of the heading label reflects the function being executed
        switch (str) {
            case "Update":
                jpPanel.add(gui.mCreateLabel("Select Employee Account to Update", new Font("Tahoma", Font.BOLD, 16)), BorderLayout.NORTH);
                break;
                    
            case "Deactivate":
                jpPanel.add(gui.mCreateLabel("Select Employee Account to Deactivate", new Font("Tahoma", Font.BOLD, 16)), BorderLayout.NORTH);
                break;
                    
            case "Salary":
                jpPanel.add(gui.mCreateLabel("Select Employee to Calculate Salary for", new Font("Tahoma", Font.BOLD, 16)), BorderLayout.NORTH);
                break;
                    
            case "Grant Rights":
                jpPanel.add(gui.mCreateLabel("Select cashier to grant rights to", new Font("Tahoma", Font.BOLD, 16)), BorderLayout.NORTH);
                break;
        }
                        
        JPanel jpCenterPart = new JPanel(new BorderLayout()); //A JPanel to contain the center part of this dialog GUI
        jpCenterPart.add(cboEmployeeNames, BorderLayout.CENTER); // Insert in the center the combo box that will be populated
            
        if(str.equals("Grant Rights") || str.equals("Deactivate") || str.equals("Update")){
                
            mLoadToComboBox("SELECT Employee_name, Employee_surname FROM tblEmployees WHERE Discharged = 0 AND Employee_position ='Cashier'");
                                
        }  else {
            mLoadToComboBox("SELECT Employee_name, Employee_surname FROM tblEmployees WHERE Discharged = 0");
        }
            
        jpPanel.add(jpCenterPart);
            
        JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //A JPanel to contain the lower part of the GUI
        jpLowerPart = gui.mPreparePanel(jpLowerPart);
            
        // The following switch statement is beingg used to make sure an event 
        // of this dialog button reflects the function being executed
        JButton btnButton = new JButton();
        btnButton.setPreferredSize(new Dimension(100, 25));
        switch (str) {
            case "Update":
                btnButton.setText("Ok"); //Instantiate a button object and set its text to Ok
                btnButton.setBackground(new Color(255, 255, 255));
                    
                btnButton.addActionListener(new ActionListener() { 
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mSetSelectedEmployeeID();
                        clsEmployeeManagementTransactions.this.dispose();
                    }
                }); 
                jpLowerPart.add(btnButton);
                jpPanel.add(jpLowerPart, BorderLayout.SOUTH);
                break;
                    
            case "Deactivate":
                btnButton.setText("Deactivate");
                btnButton.setBackground(new Color(255, 255, 255));
                btnButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) { 
                        String strEmployeeFirstName = cboEmployeeNames.getSelectedItem().toString().substring(
                                        0, cboEmployeeNames.getSelectedItem().toString().indexOf(" ")).trim();
                           
                        String strEmployeeLastName = cboEmployeeNames.getSelectedItem().toString().substring(
                                        cboEmployeeNames.getSelectedItem().toString().indexOf(" "),
                                                cboEmployeeNames.getSelectedItem().toString().length()).trim();
                           
                        int intEmployeeId = clsSQLMethods.mGetNumericField(
                                            "SELECT Employee_id FROM tblEmployees WHERE Employee_name='"+strEmployeeFirstName
                                                +"' AND Employee_surname ='"+strEmployeeLastName+"'");
                           
                        if(clsSQLMethods.mCreateRecord("INSERT INTO tblDeactivatedEmployeeAccount(Deactivated_account, Account_type)"
                                + "VALUES('"+intEmployeeId+"','"+ clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_id="+intEmployeeId)+"')")
                                    && clsSQLMethods.mUpdateRecord("Update tblEmployees SET Discharged = 1 WHERE Employee_id ="+intEmployeeId)){
                                
                            JOptionPane.showMessageDialog(clsEmployeeManagementTransactions.this, "Cashier account has been deactivated",
                                    "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                        }
                        clsEmployeeManagementTransactions.this.dispose();
                    }
                });
                jpLowerPart.add(btnButton);
                jpPanel.add(jpLowerPart, BorderLayout.SOUTH);
                break;
                    
            case "Salary":
                btnButton.setText("Ok");
                btnButton.setBackground(new Color(255, 255, 255));
                btnButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                            
                        frmEmployeeSalary.strSelectedEmployee = cboEmployeeNames.getSelectedItem().toString();
                        clsEmployeeManagementTransactions.this.dispose();
                    }
                }); 
                jpLowerPart.add(btnButton);
                jpPanel.add(jpLowerPart, BorderLayout.SOUTH);
                break;
                    
            case "Grant Rights":
                btnButton.setText("Grant");
                btnButton.setBackground(new Color(255, 255, 255));
                btnButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                            
                    if(new frmLogin().mGetEmployeeID() != 0) {
                                
                        new clsPasswordManagement(cboEmployeeNames.getSelectedItem().toString(),
                                clsEmployeeManagementTransactions.class.getName(), btnButton.getText());
                                
                        clsEmployeeManagementTransactions.this.dispose();
                    } else {
                                
                        String strFirstName = cboEmployeeNames.getSelectedItem().toString()
                                .substring(0, cboEmployeeNames.getSelectedItem().toString().indexOf(" ")).trim(); 
                    
            
                        String strLastName = cboEmployeeNames.getSelectedItem().toString()
                                .substring(cboEmployeeNames.getSelectedItem().toString().indexOf(" "),
                                    cboEmployeeNames.getSelectedItem().toString().trim().length()).trim();
                                
                        int intEmployeeID = clsSQLMethods.mGetNumericField("SELECT Employee_id FROM tblEmployees WHERE Employee_name ='"+
                                    strFirstName+"' AND Employee_surname='"+strLastName+"'");
                                
                        String strInput = mInput();
                        if(strInput.equals("")) { 
                            while(mInput().equals("") && !mInput().startsWith("manager")){
                                strInput = mInput();
                            }
                        } else if(clsSQLMethods.mUpdateRecord("UPDATE tblEmployees SET Employee_password='"+
                                    clsPasswordCryptography.mEncryptPassword(strInput)+"', Employee_password_last_update='"+
                                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+
                                        "', Employee_position ='Manager' WHERE Employee_id="+intEmployeeID)) {
                                    
                            JOptionPane.showMessageDialog(null, "Manager rights have been granted", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                                    
                            clsEmployeeManagementTransactions.this.dispose();
                        }
                    }
                }
            });
            jpLowerPart.add(btnButton);
            jpPanel.add(jpLowerPart, BorderLayout.SOUTH);
            break;
        }
        //Specifies where the lower part should be positioned
        this.add(jpPanel);
    }
        
    //A method to set an Employee ID of the Employee being worked with
    private void mSetSelectedEmployeeID() {
        String strFirstName = cboEmployeeNames.getSelectedItem().toString()
                .substring(0, cboEmployeeNames.getSelectedItem().toString()
                        .indexOf(" ")).trim(); 
                    
            
        String strLastName = cboEmployeeNames.getSelectedItem().toString()
                .substring(cboEmployeeNames.getSelectedItem().toString().indexOf(" "),
                                cboEmployeeNames.getSelectedItem().toString().trim().length());
        
        
        intID = clsSQLMethods.mGetNumericField("SELECT Employee_id FROM tblEmployees WHERE Employee_name ='"+strFirstName.trim()+
                "' AND Employee_surname ='"+strLastName.trim()+"'");
    }   
                              
    //A methos to fetch details from the database and populate thie dialog's conbo box
    private void mLoadToComboBox(String strQuery) {
        try {
            try (Statement stStatement = 
                    clsSQLMethods.mConnectToDatabase().prepareStatement(strQuery)) {
                
                stStatement.execute(strQuery);
                try (ResultSet rs = stStatement.getResultSet()) {
                    while(rs.next()) {
                        cboEmployeeNames.addItem(rs.getString(1) + " " + rs.getString(2));
                    }
                    stStatement.close();
                    rs.close();
                }
            }
        } catch(SQLException | NullPointerException e) {
            JOptionPane.showMessageDialog(null,"A technical error has been encountered\n"+e.getMessage());
        }
    }
}