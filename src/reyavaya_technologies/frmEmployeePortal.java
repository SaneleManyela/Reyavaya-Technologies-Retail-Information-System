/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import javax.swing.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Sanele
 * 
 * This class is used to manage employee accounts -
 * add account; update account; deactivate account;
 * promote account; demote account
 */
public class frmEmployeePortal extends javax.swing.JFrame {

    /**
     * Creates new form frmAdminPortal
     */
    public frmEmployeePortal() {
        initComponents();
        this.setTitle("Employee Portal"); //This sets the title of the form
        this.setResizable(false);
        this.setLocationRelativeTo(null); //Displays the form at the center of the window
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);  //When closed, this form is destroyed/exited, not the entire apllication
        
        if(frmLogin.mGetEmployeeID() == 0) {
            txtPassword.setText("manager");
            btnAddCashier.setText("Add Manager");
            btnDeactivateCashier.setText("Deactivate Manager");
            btnUpdateCashier.setEnabled(false);
            btnGrantRights.setEnabled(false);
            btnRevokeRights.setEnabled(false);
        }
        
        tblEmployee = new clsModelAndDataMethods().mTable("SELECT Employee_name, Employee_address, Employee_contact,"
                + " Employee_email FROM tblEmployees"
                + " WHERE Discharged = 0", tblEmployee, dmEmployeesModel); //Displays data on table
        txtFirstname.requestFocusInWindow(); //Places the cursor on the JTextField txtFirstname
    }
    
    DefaultTableModel dmEmployeesModel = new DefaultTableModel();
    
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
    clsValidationMethods validation = new clsValidationMethods();
    
    frmLogin frmLogin = new frmLogin();
    
    //A method to get from the GUI values passed by an admin
    private String[] mGetValuesFromGUI() {
        return new String[] {
            txtFirstname.getText().trim(), txtLastname.getText().trim(), 
            txtAddress.getText().trim(), txtContactNo.getText().trim(),
            txtEmail.getText().trim(), txtUsername.getText().trim(), txtPassword.getText().trim(),
            txtBasicSalary.getText().trim(), txtBankAccNo.getText().trim(), cboMethodOfPay.getSelectedItem().toString(),
            txtRatePerHour.getText().trim()
        };
    }
    
    private void mSetValuesToGUI() {
        int intEmployee = new clsEmployeeManagementTransactions("").mSelectedEmployeeID();
        
        
        String[] arrValues = 
                    clsSQLMethods.mFetchRecord("SELECT Employee_name, Employee_surname,"
                            + " Employee_address, Employee_contact, Employee_email, Username, "
                            + "Employee_password, Basic_salary, Payment_method, Bank_acc_no, "
                            + "Rate_Per_Hour FROM tblEmployees WHERE Employee_id ="+intEmployee);
            
        
        txtFirstname.setText(arrValues[0]);
        txtLastname.setText(arrValues[1]);
        txtAddress.setText(arrValues[2]);
        txtContactNo.setText(arrValues[3]);
        txtEmail.setText(arrValues[4]);
        txtUsername.setText(arrValues[5]);
        txtPassword.setText(clsPasswordCryptography.mDecryptPassword(arrValues[6]));
        txtBasicSalary.setText(arrValues[7]);
        cboMethodOfPay.setSelectedItem(arrValues[8]);
        txtBankAccNo.setText(arrValues[9]);
        txtRatePerHour.setText(arrValues[10]);
        btnUpdateCashier.setText("Save Cashier");
    }
      
    //A method that returns a string variable, a query to insert and
    //create a new employee record
    private String mAddEmployeeDetailsQuery() {
        return "INSERT INTO tblEmployees(Employee_name, Employee_surname,"
                + " Employee_address, Employee_contact, Employee_email, Username, Employee_password, Employee_password_last_update,"
                + " Employee_position, Basic_salary, Bank_acc_no, Payment_method, Rate_Per_Hour, Date_of_Entry, Discharged)"+
                "VALUES('"+ mGetValuesFromGUI()[0] + "','" + mGetValuesFromGUI()[1] + "','"+mGetValuesFromGUI()[2]+"','"
                + mGetValuesFromGUI()[3] +"','"+mGetValuesFromGUI()[4]+"','"+mGetValuesFromGUI()[5]+"','"
                +clsPasswordCryptography.mEncryptPassword(mGetValuesFromGUI()[6].trim())+"','"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+
                "','"+(frmLogin.mGetEmployeeID() == 0 ? "Manager": "Cashier")+
                "','"+mGetValuesFromGUI()[7]+"','"+mGetValuesFromGUI()[8]+"','"+
                mGetValuesFromGUI()[9]+"','"+mGetValuesFromGUI()[10]+"','"+new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+"', 0)";
    }
   
    
    
    //A method that returns a string variable, a query to update an
    //employee record in the database
    private String mUpdateEmployeeRecordQuery() {
        String strLastPasswordUpdate;
        
        mGetValuesFromGUI();
        if(clsSQLMethods.mGetTextField(
                        "SELECT Employee_password FROM tblEmployees WHERE Employee_name='"+txtFirstname.getText().trim()
                        +"' AND Employee_surname ='"+txtLastname.getText().trim()+"'").equals(clsPasswordCryptography.mEncryptPassword(txtPassword.getText()))) {
            strLastPasswordUpdate = clsSQLMethods.mGetTextField(
                        "SELECT Employee_password_last_update FROM tblEmployees WHERE Employee_name='"+txtFirstname.getText().trim()
                        +"' AND Employee_surname ='"+txtLastname.getText().trim()+"'");
        } else {
            strLastPasswordUpdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date());
        }
        
        return "UPDATE tblEmployees SET Employee_name ='"+mGetValuesFromGUI()[0]+
                "', Employee_surname='"+mGetValuesFromGUI()[1]+"', Employee_address ='"+
                mGetValuesFromGUI()[2]+"', Employee_contact ='"+mGetValuesFromGUI()[3]+
                "', Employee_email ='"+mGetValuesFromGUI()[4]
                +"', Username ='"+mGetValuesFromGUI()[5]+"', Employee_password='"+
                clsPasswordCryptography.mEncryptPassword(mGetValuesFromGUI()[6])+"', Employee_password_last_update='"
                +strLastPasswordUpdate+"', Basic_salary='"+
                mGetValuesFromGUI()[7]+"', Bank_acc_no='"+mGetValuesFromGUI()[8]+"', Payment_method='"+
                mGetValuesFromGUI()[9]+"', Rate_Per_Hour='"+mGetValuesFromGUI()[10]+"' WHERE Employee_id="+
                clsSQLMethods.mGetNumericField("SELECT Employee_id FROM tblEmployees WHERE Employee_name ='"+
                    txtFirstname.getText().trim()+"' AND Employee_surname ='"+ txtLastname.getText().trim()+"'");
    }
    
    private String mVerifyInput() {
        try{
            if(txtFirstname.getText().equals("")) {
                return "Provide an employee first name";
                
            } else if(txtLastname.getText().equals("")) {
                return "Provide an employee last name";
                
            } else if(txtAddress.getText().equals("")) {
                return "Provide an employee address";
                
            } else if(txtContactNo.getText().equals("")) {
                return "Provide an employee contact number";
                
            } else if(txtContactNo.getText().length() != 10) {
                return "A valid South African contact number can only be 10 digits";
                
            } else if(txtEmail.getText().equals("")) {
                return "Provide an employee email address";
                
            } else if(txtUsername.getText().equals("")) {
                return "Provide employee system username";
                
            } else if(txtPassword.getText().equals("")) {
                return "Provide an employee password";
                
            } else if(frmLogin.mGetEmployeeID() == 0 && !txtPassword.getText().startsWith("manager")) {
                return "Manager passwords must begin with 'manager'";
                
            } else if(txtBasicSalary.getText().equals("")){
                return "Provide basic salary of an employee";
                
            } else if(txtBankAccNo.getText().equals("")) {
                return "Provide employee bank account number";
                
            } else if(Double.parseDouble(txtRatePerHour.getText()) <= 0) {
                return "Unacceptable rate of pay per hour";
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frmEmployeePortal.this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }
    
    private void mAddEmployeeAccount() {
        if(mVerifyInput().equals("")) {
            if(validation.mValidateEmail(txtEmail.getText()).equals("") &&
                    validation.mValidateContactNumber(txtContactNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtBasicSalary.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtBankAccNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtRatePerHour.getText()).equals("") &&
                    !clsSQLMethods.mCheckIfDetailsExist(
                            "SELECT Employee_name, Employee_surname FROM tblEmployees"
                                    + " WHERE Employee_name='"+txtFirstname.getText()
                                    +"' AND Employee_surname ='"+txtLastname.getText()+"'") &&
                    !clsSQLMethods.mCheckIfDetailsExist(
                            "SELECT Username FROM tblEmployees"
                                    + " WHERE Username='"+txtUsername.getText()+"'")) {
                
                if(frmLogin.mGetEmployeeID() == 0 && !txtPassword.getText().startsWith("manager")) {
                    
                    JOptionPane.showMessageDialog(this, "Manager passwords must begin with 'manager'", "WARNING", JOptionPane.WARNING_MESSAGE);
                    
                } else if(frmLogin.mGetEmployeeID() == 0 && !validation.mEnsureStoreHasOnlyOneManager().equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mEnsureStoreHasOnlyOneManager(), "WARNING", JOptionPane.WARNING_MESSAGE);
                    
                } else if(frmLogin.mGetEmployeeID() == 0 && txtPassword.getText().equals("manager")) {
                    
                    JOptionPane.showMessageDialog(this, "The password prefix cannot be made a password", "WARNING", JOptionPane.WARNING_MESSAGE);
                    
                } else if(txtPassword.getText().startsWith("manager") && frmLogin.mGetEmployeeID() != 0) { 
                    
                    JOptionPane.showMessageDialog(this, "This employee is a cashier.\n"
                            + "Choose an alternative password", "WARNING", JOptionPane.WARNING_MESSAGE);
                } else {
                    mGetValuesFromGUI();
                    if(clsSQLMethods.mCreateRecord(mAddEmployeeDetailsQuery())) {
                        
                        JOptionPane.showMessageDialog(this, "Employee account has been created", "MESSAGE",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else if(!validation.mValidateEmail(txtEmail.getText()).equals("")) {
                
                JOptionPane.showMessageDialog(this, validation.mValidateEmail(txtEmail.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            } else if(!validation.mValidateContactNumber(txtContactNo.getText()).equals("")) {
                
                JOptionPane.showMessageDialog(this, validation.mValidateContactNumber(txtContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            } else if(!validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()).equals("")) {
                
                    JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);              
                    
            } else if(!validation.mCheckIfFieldIsOnlyDigits(txtBasicSalary.getText()).equals("")) {
                
                JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtBasicSalary.getText()), "WARNING",
                    JOptionPane.WARNING_MESSAGE);
                
            } else if(!validation.mCheckIfFieldIsOnlyDigits(txtBankAccNo.getText()).equals("")) {
                
                JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtBankAccNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            } else if(!validation.mCheckIfFieldIsOnlyDigits(txtRatePerHour.getText()).equals("")) {
                
                JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtRatePerHour.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            } else if(clsSQLMethods.mCheckIfDetailsExist("SELECT Employee_name, Employee_surname FROM tblEmployees"
                                    + " WHERE Employee_name='"+txtFirstname.getText()
                                    +"' AND Employee_surname ='"+txtLastname.getText()+"'")) {
                
                JOptionPane.showMessageDialog(this, "This employee account already exists.", 
                        "WARNING", JOptionPane.WARNING_MESSAGE);
                
            } else if(clsSQLMethods.mCheckIfDetailsExist(
                            "SELECT Username FROM tblEmployees"
                                    + " WHERE Username='"+txtUsername.getText()+"'")) {
                
                JOptionPane.showMessageDialog(this, "This username already exists, Provide alternative.", 
                       "WARNING", JOptionPane.WARNING_MESSAGE);
                
            }
        } else {
            JOptionPane.showMessageDialog(this, mVerifyInput(), "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void mUpdateEmployeeAccount() {
        if(btnUpdateCashier.getText().equals("Update Cashier")) {
            
            clsEmployeeManagementTransactions clsEmployeeMngmtDialog = new clsEmployeeManagementTransactions("Update");
            clsEmployeeMngmtDialog.setVisible(true);
            mSetValuesToGUI();
            btnAddCashier.setEnabled(false);
            btnDeactivateCashier.setEnabled(false);
            txtFirstname.setEditable(false);
            txtLastname.setEditable(false);
            
        } else if(btnUpdateCashier.getText().equals("Save Cashier")) {
            if(mVerifyInput().equals("")) {
                
                if(validation.mValidateEmail(txtEmail.getText()).equals("") &&
                    validation.mValidateContactNumber(txtContactNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtBasicSalary.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtBankAccNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtRatePerHour.getText()).equals("")) {
                    
                    if(!clsSQLMethods.mGetTextField("SELECT Username FROM tblEmployees WHERE Employee_name ='"+
                            txtFirstname.getText()+"' AND Employee_surname='"+txtLastname.getText().trim()+"'").equals(txtUsername.getText()) && 
                            clsSQLMethods.mCheckIfDetailsExist("SELECT Username FROM tblEmployees"
                                    + " WHERE Username='"+txtUsername.getText().trim()+"'")) {
                        
                        JOptionPane.showMessageDialog(this, "This username already exists, Provide alternative.", 
                            "WARNING", JOptionPane.WARNING_MESSAGE);
                        
                    } else if(clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_name ='"+
                        txtFirstname.getText().trim()+"' AND Employee_surname ='"+
                            txtLastname.getText().trim()+"'").equals("Manager") && !txtPassword.getText().startsWith("manager")) {
                        
                        JOptionPane.showMessageDialog(this, "Manager passwords must begin with 'manager'", "WARNING", JOptionPane.WARNING_MESSAGE);
                        
                    } else if(clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_name ='"+
                        txtFirstname.getText().trim()+"' AND Employee_surname ='"+
                            txtLastname.getText().trim()+"'").equals("Cashier") && txtPassword.getText().startsWith("manager")) {
                        
                        JOptionPane.showMessageDialog(this, "A cashier password is not allowed to be prefixes with 'manager'", "WARNING",
                            JOptionPane.WARNING_MESSAGE);
                    } 
                    else if(clsSQLMethods.mUpdateRecord(mUpdateEmployeeRecordQuery())) {
                        
                        JOptionPane.showMessageDialog(this, "Employee details have been updated", "MESSAGE",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        txtFirstname.setEditable(true);
                        txtLastname.setEditable(true);
                        btnUpdateCashier.setText("Update Cashier");
                        btnAddCashier.setEnabled(true);
                        btnDeactivateCashier.setEnabled(true);
                        
                    }
                } else if(!validation.mValidateEmail(txtEmail.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mValidateEmail(txtEmail.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                    
                } else if(!validation.mValidateContactNumber(txtContactNo.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mValidateContactNumber(txtContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                    
                } else if(!validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);              
                    
                } else if(!validation.mCheckIfFieldIsOnlyDigits(txtBasicSalary.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtBasicSalary.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                    
                } else if(!validation.mCheckIfFieldIsOnlyDigits(txtBankAccNo.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtBankAccNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                    
                } else if(!validation.mCheckIfFieldIsOnlyDigits(txtRatePerHour.getText()).equals("")) {
                    
                    JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtRatePerHour.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                    
                }
            } else  {
                JOptionPane.showMessageDialog(frmEmployeePortal.this, mVerifyInput(), "WARNING", JOptionPane.WARNING_MESSAGE);
            }                  
        }
    }
    
    private void mDeactivateEmployeeAccount() {
        if(btnDeactivateCashier.getText().equals("Deactivate Cashier")) {
            
            clsEmployeeManagementTransactions clsEmployeeMngmtDialog = new clsEmployeeManagementTransactions("Deactivate");
            clsEmployeeMngmtDialog.setVisible(true);
            
        } else if (btnDeactivateCashier.getText().equals("Deactivate Manager")) {
            
            new clsPasswordManagement();
        }
    }
    
    private void mRefreshWindow() {
        dmEmployeesModel = new DefaultTableModel();
        tblEmployee = new clsModelAndDataMethods().mTable("SELECT Employee_name, Employee_address, Employee_contact,"
                + " Employee_email FROM tblEmployees"
                + " WHERE Discharged = 0", tblEmployee, dmEmployeesModel); //Displays data on table
        mClearGUIFields();
    }
    
    private void mReturnToMainWndw() {
        if(frmLogin.mGetEmployeeID() == 0) {
            frmLogin.setVisible(true);
            this.dispose();
        } else {
            frmMain frmMn = new frmMain();
            frmMn.mUserAccessControl(clsSQLMethods.mGetTextField(
                    "SELECT Employee_position FROM tblEmployees WHERE Employee_id ="+
                        new frmLogin().mGetEmployeeID()));
            frmMn.setVisible(true);
            this.dispose();
        }
    }
    
    private void mClearGUIFields() {
        txtFirstname.setText("");
        txtLastname.setText(""); 
        txtAddress.setText("");
        txtContactNo.setText("");
        txtEmail.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        txtBasicSalary.setText(""); 
        txtBankAccNo.setText("");
        txtRatePerHour.setText("");
    }
    
    private void mGrantManegerialRights() {
        new clsEmployeeManagementTransactions("Grant Rights").setVisible(true);
    }
    
    private void mRevokeManagerialRights() {
        // instantiate clsPasswordAuthentication()
        new clsPasswordManagement();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dskPane = new javax.swing.JDesktopPane();
        jpEmployeeInformation = new javax.swing.JPanel();
        txtFirstname = new javax.swing.JTextField();
        lblEmployeeInfo = new javax.swing.JLabel();
        lblFirstname = new javax.swing.JLabel();
        lblLastName = new javax.swing.JLabel();
        txtLastname = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        lblContactNo = new javax.swing.JLabel();
        txtContactNo = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblBasicSalary = new javax.swing.JLabel();
        txtBasicSalary = new javax.swing.JTextField();
        lblUsername = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        lblBankAccountNumber = new javax.swing.JLabel();
        txtBankAccNo = new javax.swing.JTextField();
        lblMethodOfPay = new javax.swing.JLabel();
        cboMethodOfPay = new javax.swing.JComboBox<>();
        lblRatePerHour = new javax.swing.JLabel();
        txtRatePerHour = new javax.swing.JTextField();
        jpEmployeeDetailsView = new javax.swing.JPanel();
        jsScrollPane = new javax.swing.JScrollPane();
        tblEmployee = new javax.swing.JTable();
        lblEmployeeMgmtPortal = new javax.swing.JLabel();
        jpCommandsPanel = new javax.swing.JPanel();
        lblCommands = new javax.swing.JLabel();
        btnAddCashier = new javax.swing.JButton();
        btnUpdateCashier = new javax.swing.JButton();
        btnDeactivateCashier = new javax.swing.JButton();
        btnClearText = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnReturnToMainWndw = new javax.swing.JButton();
        btnGrantRights = new javax.swing.JButton();
        btnRevokeRights = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        dskPane.setBackground(new java.awt.Color(255, 255, 255));

        jpEmployeeInformation.setBackground(new java.awt.Color(255, 255, 255));
        jpEmployeeInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblEmployeeInfo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblEmployeeInfo.setText("Employee Information");

        lblFirstname.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFirstname.setText("First name");

        lblLastName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblLastName.setText("Last name");

        lblAddress.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblAddress.setText("Address");

        lblContactNo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblContactNo.setText("Contact No.");

        lblEmail.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEmail.setText("Email Address");

        lblBasicSalary.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBasicSalary.setText("Basic Salary");

        txtBasicSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBasicSalaryActionPerformed(evt);
            }
        });

        lblUsername.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUsername.setText("Username");

        lblPassword.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPassword.setText("Password");

        txtAddress.setColumns(20);
        txtAddress.setRows(5);
        jScrollPane2.setViewportView(txtAddress);

        lblBankAccountNumber.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBankAccountNumber.setText("Bank Account No.");

        txtBankAccNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBankAccNoActionPerformed(evt);
            }
        });

        lblMethodOfPay.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblMethodOfPay.setText("Method of Pay");

        cboMethodOfPay.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Per week", "Per month" }));

        lblRatePerHour.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRatePerHour.setText("Rate Per Hour");

        txtRatePerHour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRatePerHourActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpEmployeeInformationLayout = new javax.swing.GroupLayout(jpEmployeeInformation);
        jpEmployeeInformation.setLayout(jpEmployeeInformationLayout);
        jpEmployeeInformationLayout.setHorizontalGroup(
            jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                        .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblPassword, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblUsername, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblContactNo, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblAddress, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblLastName, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                                    .addComponent(lblFirstname)
                                    .addGap(20, 20, 20)))
                            .addComponent(lblBasicSalary))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                        .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtBasicSalary)
                            .addComponent(txtFirstname, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtEmail)
                            .addComponent(txtContactNo, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPassword)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(txtLastname, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtUsername, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                        .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBankAccountNumber)
                            .addComponent(lblMethodOfPay)
                            .addComponent(lblRatePerHour))
                        .addGap(69, 69, 69)
                        .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRatePerHour)
                            .addComponent(txtBankAccNo)
                            .addComponent(cboMethodOfPay, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEmployeeInformationLayout.createSequentialGroup()
                        .addComponent(lblEmployeeInfo)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(40, 40, 40))
        );
        jpEmployeeInformationLayout.setVerticalGroup(
            jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lblEmployeeInfo)
                .addGap(18, 18, 18)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFirstname)
                    .addComponent(txtFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLastName)
                    .addComponent(txtLastname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAddress)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContactNo))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBasicSalary)
                    .addComponent(txtBasicSalary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBankAccountNumber)
                    .addComponent(txtBankAccNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMethodOfPay)
                    .addComponent(cboMethodOfPay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRatePerHour)
                    .addComponent(txtRatePerHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        jpEmployeeDetailsView.setBackground(new java.awt.Color(255, 255, 255));
        jpEmployeeDetailsView.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tblEmployee.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jsScrollPane.setViewportView(tblEmployee);

        javax.swing.GroupLayout jpEmployeeDetailsViewLayout = new javax.swing.GroupLayout(jpEmployeeDetailsView);
        jpEmployeeDetailsView.setLayout(jpEmployeeDetailsViewLayout);
        jpEmployeeDetailsViewLayout.setHorizontalGroup(
            jpEmployeeDetailsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEmployeeDetailsViewLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );
        jpEmployeeDetailsViewLayout.setVerticalGroup(
            jpEmployeeDetailsViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEmployeeDetailsViewLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        lblEmployeeMgmtPortal.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblEmployeeMgmtPortal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEmployeeMgmtPortal.setText("Reyavaya Tech Employee Management Portal");

        jpCommandsPanel.setBackground(new java.awt.Color(255, 255, 255));
        jpCommandsPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblCommands.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblCommands.setText("Commands");

        btnAddCashier.setBackground(new java.awt.Color(255, 255, 255));
        btnAddCashier.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnAddCashier.setText("Add Cashier");
        btnAddCashier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCashierActionPerformed(evt);
            }
        });

        btnUpdateCashier.setBackground(new java.awt.Color(255, 255, 255));
        btnUpdateCashier.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnUpdateCashier.setText("Update Cashier");
        btnUpdateCashier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateCashierActionPerformed(evt);
            }
        });

        btnDeactivateCashier.setBackground(new java.awt.Color(255, 255, 255));
        btnDeactivateCashier.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnDeactivateCashier.setText("Deactivate Cashier");
        btnDeactivateCashier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeactivateCashierActionPerformed(evt);
            }
        });

        btnClearText.setBackground(new java.awt.Color(255, 255, 255));
        btnClearText.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnClearText.setText("Clear Text");
        btnClearText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearTextActionPerformed(evt);
            }
        });

        btnRefresh.setBackground(new java.awt.Color(255, 255, 255));
        btnRefresh.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnRefresh.setText("Refresh Window");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnReturnToMainWndw.setBackground(new java.awt.Color(255, 255, 255));
        btnReturnToMainWndw.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnReturnToMainWndw.setText("Return to Main Wndw");
        btnReturnToMainWndw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnToMainWndwActionPerformed(evt);
            }
        });

        btnGrantRights.setBackground(new java.awt.Color(255, 255, 255));
        btnGrantRights.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnGrantRights.setText("Grant Rights");
        btnGrantRights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGrantRightsActionPerformed(evt);
            }
        });

        btnRevokeRights.setBackground(new java.awt.Color(255, 255, 255));
        btnRevokeRights.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnRevokeRights.setText("Revoke Rights");
        btnRevokeRights.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRevokeRightsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpCommandsPanelLayout = new javax.swing.GroupLayout(jpCommandsPanel);
        jpCommandsPanel.setLayout(jpCommandsPanelLayout);
        jpCommandsPanelLayout.setHorizontalGroup(
            jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpCommandsPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpCommandsPanelLayout.createSequentialGroup()
                        .addGroup(jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAddCashier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnClearText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addGroup(jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnRefresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnUpdateCashier, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnRevokeRights, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                            .addComponent(btnGrantRights, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addGroup(jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnDeactivateCashier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnReturnToMainWndw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(lblCommands))
                .addGap(25, 25, 25))
        );
        jpCommandsPanelLayout.setVerticalGroup(
            jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpCommandsPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblCommands)
                .addGap(35, 35, 35)
                .addGroup(jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddCashier)
                    .addComponent(btnUpdateCashier)
                    .addComponent(btnGrantRights)
                    .addComponent(btnDeactivateCashier))
                .addGap(20, 20, 20)
                .addGroup(jpCommandsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClearText)
                    .addComponent(btnRefresh)
                    .addComponent(btnReturnToMainWndw)
                    .addComponent(btnRevokeRights))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dskPane.setLayer(jpEmployeeInformation, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dskPane.setLayer(jpEmployeeDetailsView, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dskPane.setLayer(lblEmployeeMgmtPortal, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dskPane.setLayer(jpCommandsPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout dskPaneLayout = new javax.swing.GroupLayout(dskPane);
        dskPane.setLayout(dskPaneLayout);
        dskPaneLayout.setHorizontalGroup(
            dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dskPaneLayout.createSequentialGroup()
                .addGroup(dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dskPaneLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jpEmployeeInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addGroup(dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jpCommandsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jpEmployeeDetailsView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(dskPaneLayout.createSequentialGroup()
                        .addGap(298, 298, 298)
                        .addComponent(lblEmployeeMgmtPortal)))
                .addGap(35, 35, 35))
        );
        dskPaneLayout.setVerticalGroup(
            dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dskPaneLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lblEmployeeMgmtPortal)
                .addGap(15, 15, 15)
                .addGroup(dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dskPaneLayout.createSequentialGroup()
                        .addComponent(jpEmployeeDetailsView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(jpCommandsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jpEmployeeInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dskPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(dskPane)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddCashierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCashierActionPerformed
         mAddEmployeeAccount();
    }//GEN-LAST:event_btnAddCashierActionPerformed

    private void btnUpdateCashierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateCashierActionPerformed
        mUpdateEmployeeAccount();
    }//GEN-LAST:event_btnUpdateCashierActionPerformed

    private void btnDeactivateCashierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeactivateCashierActionPerformed
        mDeactivateEmployeeAccount();
    }//GEN-LAST:event_btnDeactivateCashierActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        mRefreshWindow();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnReturnToMainWndwActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnToMainWndwActionPerformed
        mReturnToMainWndw();
    }//GEN-LAST:event_btnReturnToMainWndwActionPerformed

    private void btnClearTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearTextActionPerformed
        mClearGUIFields();
    }//GEN-LAST:event_btnClearTextActionPerformed

    private void txtBasicSalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBasicSalaryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBasicSalaryActionPerformed

    private void txtBankAccNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBankAccNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBankAccNoActionPerformed

    private void txtRatePerHourActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRatePerHourActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRatePerHourActionPerformed

    private void btnGrantRightsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGrantRightsActionPerformed
        mGrantManegerialRights();
    }//GEN-LAST:event_btnGrantRightsActionPerformed

    private void btnRevokeRightsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRevokeRightsActionPerformed
        mRevokeManagerialRights();
    }//GEN-LAST:event_btnRevokeRightsActionPerformed

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
            java.util.logging.Logger.getLogger(frmEmployeePortal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmEmployeePortal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmEmployeePortal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmEmployeePortal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmEmployeePortal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddCashier;
    private javax.swing.JButton btnClearText;
    private javax.swing.JButton btnDeactivateCashier;
    private javax.swing.JButton btnGrantRights;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReturnToMainWndw;
    private javax.swing.JButton btnRevokeRights;
    private javax.swing.JButton btnUpdateCashier;
    private javax.swing.JComboBox<String> cboMethodOfPay;
    private javax.swing.JDesktopPane dskPane;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel jpCommandsPanel;
    private javax.swing.JPanel jpEmployeeDetailsView;
    private javax.swing.JPanel jpEmployeeInformation;
    private javax.swing.JScrollPane jsScrollPane;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblBankAccountNumber;
    private javax.swing.JLabel lblBasicSalary;
    private javax.swing.JLabel lblCommands;
    private javax.swing.JLabel lblContactNo;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblEmployeeInfo;
    private javax.swing.JLabel lblEmployeeMgmtPortal;
    private javax.swing.JLabel lblFirstname;
    private javax.swing.JLabel lblLastName;
    private javax.swing.JLabel lblMethodOfPay;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblRatePerHour;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTable tblEmployee;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtBankAccNo;
    private javax.swing.JTextField txtBasicSalary;
    private javax.swing.JTextField txtContactNo;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFirstname;
    private javax.swing.JTextField txtLastname;
    private javax.swing.JTextField txtPassword;
    private javax.swing.JTextField txtRatePerHour;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
