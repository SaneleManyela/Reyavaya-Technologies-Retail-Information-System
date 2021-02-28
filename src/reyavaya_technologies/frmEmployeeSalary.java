/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.time.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sanele
 * 
 * A class to calculate salaries of employees.
 * This abstraction is the least in all of the application's classes
 * that reflects accurately the real world model
 */
public class frmEmployeeSalary extends javax.swing.JFrame {

    /**
     * Creates new form frmEmployeeSalary
     */
    public frmEmployeeSalary() {
        initComponents();
        this.setTitle("Calculate Employee Salary"); // Sets the title of this form
        this.setResizable(false);
        this.setLocationRelativeTo(null); // Displays the form at center screen
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        new clsEmployeeManagementTransactions("Salary").setVisible(true);
        mSetEmployeeDetailsToGUI(strSelectedEmployee); // A call to a method that sets employee values to the GUI
        
        tblSalary = modelAndDataMethods.mTable("SELECT Employee_name, Employee_surname, "
                + "Basic_salary, Salary_after_deductions, isBonus_Calculated, Pay_date"
                + " FROM tblEmployees, tblSalary WHERE tblEmployees.Employee_id ='"+txtEmployeeID.getText()+" '",
                tblSalary, dmSalaryModel); // Add to query - AND tblSalary.Employee_id='"+txtEmployeeID.getText()
        
        btnSave.setEnabled(false);
        mPrepareFrmEmployeeGUI();
        this.setVisible(true);
    }
    
    private double dblNet;
    private int intBonus;
    private int intEmployeeID;
    static String strSelectedEmployee; // A static variable to hold the value of the selected employee in a combo box
    private DefaultTableModel dmSalaryModel = new DefaultTableModel();
    
    
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
    clsModelAndDataMethods modelAndDataMethods = new clsModelAndDataMethods();
    clsValidationMethods validation = new clsValidationMethods();
    frmEmployeePortal employeePortal = new frmEmployeePortal();
    frmLogin frmLogin = new frmLogin();
    frmMain frmMn = new frmMain();
    
    // A method that retrieves data from the database and set
    // data to the GUI text fields. The method takes a string argument of
    // a value of a selected employee in a combo box of clsEmployeeManagementTransactions
    private void mSetEmployeeDetailsToGUI(String strSelectedEmployee) {
        try{
            try
            {
                String strFirstName = strSelectedEmployee.substring(0, strSelectedEmployee.indexOf(" ")).trim(); 
                  
                String strLastName = strSelectedEmployee.substring(strSelectedEmployee.indexOf(" "),
                                    strSelectedEmployee.trim().length()).trim();
            
                intEmployeeID = clsSQLMethods.mGetNumericField("SELECT Employee_id FROM tblEmployees WHERE Employee_name ='"+
                        strFirstName+"' AND Employee_surname='"+strLastName+"'");
        
                String[] arrEmployeeInfo = 
                        clsSQLMethods.mFetchRecord("SELECT Employee_name, Employee_surname, Bank_acc_no,"
                        + " Basic_salary, Payment_method, Rate_Per_Hour FROM tblEmployees WHERE Employee_id='"+ 
                                intEmployeeID+"'");
            
                txtEmployeeID.setText(String.valueOf(intEmployeeID));
                txtFirstname.setText(arrEmployeeInfo[0]);
                txtSurname.setText(arrEmployeeInfo[1]);
                txtBackAccNo.setText(arrEmployeeInfo[2]);
                txtBasicSalary.setText(arrEmployeeInfo[3]);
                cboPaymentMethods.setSelectedItem(arrEmployeeInfo[4]);
                txtRatePerHour.setText(arrEmployeeInfo[5]);
                txtUIF.setText(String.valueOf(Double.parseDouble(arrEmployeeInfo[3]) * 0.02));
            } catch(NullPointerException e) {
            }
        } catch(NumberFormatException e){
        }
    }
    
    
    private void mPrepareFrmEmployeeGUI() {
        JTextField[] arrTextFields = new JTextField[]{
            txtEmployeeID, txtFirstname, txtSurname, txtBackAccNo, txtBasicSalary, txtRatePerHour,
            txtUIF 
        };
        
        for (JTextField field : arrTextFields) {
            field.setEditable(false);
        }
        
        cboPaymentMethods.setEnabled(false);
        txtRatePerHour.requestFocusInWindow();
        txtOvertimeHrs.setText("0");
        
        try{
            
            txtUIF.setText(String.valueOf(Double.parseDouble(clsSQLMethods.mGetTextField(
                    "SELECT Basic_salary FROM tblEmployees WHERE Employee_id="+intEmployeeID)) * 0.02));
            
        } catch(NullPointerException e) {
            
        }
    }
        
    private void mSetBonusPercentageValue(int intBonusPercent) {
        this.intBonus = intBonusPercent;
    }
        
    // A method to calculate an employee salary by doing deductions
    // and additions with a number of variables to an employee basic salary
    private double mCalculateSalary() {
        double dblSalary = Double.parseDouble(txtBasicSalary.getText());
        try{
            if(chkCalculateBonus.isSelected()) {
               dblSalary += modelAndDataMethods.mFormat(dblSalary * (intBonus / 100)); 
            }
            
            dblSalary += modelAndDataMethods.mFormat((Double.parseDouble(txtOvertimeHrs.getText()) *
                    Double.parseDouble(txtRatePerHour.getText())));
            
            dblNet = dblSalary;
            
            dblSalary -= modelAndDataMethods.mFormat(Double.parseDouble(txtUIF.getText()));
        } catch(NumberFormatException e) {
            
            JOptionPane.showMessageDialog(frmEmployeeSalary.this, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return dblSalary;
    }
    
    /* If an employee is being paid a bonus under the criteria of
     * Years at the company or number of sales handled by a cashier, 
     * this class is used.
    */
    private class clsBonus extends JDialog {
        public clsBonus() {
            super(null, "Bonus Criteria", ModalityType.APPLICATION_MODAL);
            this.setSize(300, 160);
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            mCreateGUI();
            this.setVisible(true);
        }
        
        private void mCreateGUI() {
            JPanel jpPanel = new JPanel(new GridLayout(2, 1, 0, 20));
            jpPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            jpPanel = gui.mPreparePanel(jpPanel);
            jpPanel.add(gui.mCreateButton(150, 30, "> 5 yrs in the company", this::mBonusBySigniority));
            jpPanel.add(gui.mCreateButton(150, 30, "> 1000 sales handled this month", this::mBonusByGreaterThan1000NoOfSales));
            this.add(jpPanel);
        }
                
        private void mBonusBySigniority(ActionEvent e) {
            this.dispose();
            new clsBonusPercentages("Signiority");
        } 
        
        private void mBonusByGreaterThan1000NoOfSales(ActionEvent e) {
            this.dispose();
            new clsBonusPercentages("Sales");
        }
    }
    
    /* This class is used to specify bonus percentage to
     * award to an employee.
    */
    private class clsBonusPercentages extends JDialog {

        public clsBonusPercentages(String strBonusCriterion) {
            super(null, "Select Bonus Percentage", ModalityType.APPLICATION_MODAL);
            this.setSize(250, 200);
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            mCreateGUI(strBonusCriterion);
            this.setVisible(true);
        }
        
        private JComboBox cboPercentages = new JComboBox();
        private final JButton btnOk = new JButton("Ok");
        
        private void mCreateGUI(String strBonusCriterion) {
            JPanel jpPanel = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
            jpPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); //Sets a border 
            jpPanel = gui.mPreparePanel(jpPanel);
            jpPanel.add(gui.mCreateLabel("Select Bonus Percentage", new Font("Tahoma", Font.BOLD, 14)), BorderLayout.NORTH);
            
            JPanel jpCenterPart = new JPanel(new BorderLayout());
            jpCenterPart.add(cboPercentages = new JComboBox(new String[]{
                "2.5%", "5%", "7.5%", "10%", "15%"
            }));
            jpPanel.add(jpCenterPart, BorderLayout.CENTER);
                    
            
            JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //A JPanel to contain the lower part of the GUI
            jpLowerPart = gui.mPreparePanel(jpLowerPart);
            
            switch(strBonusCriterion) {
                case "Signiority":
                    btnOk.setBackground(new Color(255, 255, 255));
                    btnOk.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            
                            if(Period.between((LocalDate.parse((clsSQLMethods.mGetTextField(
                                    "SELECT Date_of_Entry FROM tblEmployees WHERE Employee_id="+txtEmployeeID.getText())))),
                                LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))).getYears() < 5) {
                                
                                JOptionPane.showMessageDialog(clsBonusPercentages.this, "This employee is not legible to recieve a bonus based on signiority.\n"
                                        + "The employee must have at least 5 years as an employee at Reyavaya Tech");
                                
                            } else {
                                mSetBonusPercentageValue(Integer.parseInt(cboPercentages.getSelectedItem().toString().replace('%', ' ').trim()));
                            }
                        }
                    });
                    
                    jpLowerPart.add(btnOk, BorderLayout.SOUTH);
                    break;
                case "Sales":
                                        
                    String[] arrDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).split("-");
                    
                    btnOk.setBackground(new Color(255, 255, 255));
                    btnOk.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(clsSQLMethods.mGetNumericField("SELECT COUNT(Cashier_id) FROM tblTransaction WHERE "
                                    + "Transaction_date BETWEEN '01-"+arrDate[1]+"-"+arrDate[2]+"' AND '"+
                                    new SimpleDateFormat("yyyy-MM-dd").format(new Date())+"'") < 1000) {
                                
                                    JOptionPane.showMessageDialog(clsBonusPercentages.this, "This employee is not legible to recieve a bonus based on sales.\n"
                                        + "The employee must have at least handled 1000 says from the 1st of this month up to now.");
                                    
                            } else {
                                mSetBonusPercentageValue(Integer.parseInt(cboPercentages.getSelectedItem().toString().replace('%', ' ').trim()));
                                lblOutput.setText("Salary After Deductions R"+mCalculateSalary());
                            }
                        }
                    });
                    
                    jpLowerPart.add(btnOk, BorderLayout.SOUTH);
                    break;
            }
            
            jpPanel.add(jpLowerPart, BorderLayout.SOUTH);
            this.add(jpPanel);
        }
    }
    
    private void mSalaryCalculation() {
        if(chkCalculateBonus.isSelected()) {
            new clsBonus();
        } else {
            lblOutput.setText("Total Amount R"+mCalculateSalary());
            btnSave.setEnabled(true);
        }
    }
    
    private void mSaveSalary() {
        if(chkCalculateBonus.isSelected()) {
            
            String strQuery = "INSERT INTO tblSalary(Employee_id, Pay_date, Net_salary, UIF, Salary_after_deductions, isBonus_calculated)"
                + "VALUES('"+intEmployeeID+"','"+new SimpleDateFormat("yyyy/MM/dd").format(new Date())+"','"+dblNet+"','"+
                txtUIF.getText().trim()+"','"+ mCalculateSalary() + (Double.parseDouble(txtBasicSalary.getText()) * intBonus / 100) +"','"+
                    (chkCalculateBonus.isSelected() ? 1 : 0)+"')";
            
            if(clsSQLMethods.mCreateRecord(strQuery)) {
                
                JOptionPane.showMessageDialog(frmEmployeeSalary.this, "Employee has been Paid", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                
            }
            lblOutput.setText("Salary After Deductions R"+mCalculateSalary() + (Double.parseDouble(txtBasicSalary.getText()) * intBonus / 100));
            
        } else {
            
            String strQuery = "INSERT INTO tblSalary(Employee_id, Pay_date, Net_salary, UIF, Salary_after_deductions, isBonus_calculated)"
                + "VALUES('"+intEmployeeID+"','"+new SimpleDateFormat("yyyy/MM/dd").format(new Date())+"','"+dblNet+"','"+
                txtUIF.getText().trim()+"','"+ mCalculateSalary() +"','"+(chkCalculateBonus.isSelected() ? 1 : 0)+"')";
            
            if(clsSQLMethods.mCreateRecord(strQuery)) {
                
                JOptionPane.showMessageDialog(frmEmployeeSalary.this, "Employee has been Paid", "MESSAGE", JOptionPane.INFORMATION_MESSAGE);
                
            }
            lblOutput.setText("After Deductions R"+mCalculateSalary());
            
        }
        
        dmSalaryModel = new DefaultTableModel();
                
        tblSalary = modelAndDataMethods.mTable("SELECT Employee_name, Employee_surname, "
                + "Basic_salary, Salary_after_deductions, isBonus_Calculated, Pay_date"
                + " FROM tblEmployees, tblSalary WHERE tblEmployees.Employee_id ='"+txtEmployeeID.getText()+"'",
                tblSalary, dmSalaryModel);
        
        btnSave.setEnabled(false);
    }
    
    private void mClear() {
        this.dispose();
        frmMn.mUserAccessControl(clsSQLMethods.mGetTextField("SELECT Employee_position FROM tblEmployees WHERE Employee_id="+intEmployeeID));
        frmMn.setVisible(true);
        new frmEmployeeSalary().setVisible(true);
        frmMn.dispose();
    }
    
    private void mReturnToMainWndw() {
        strSelectedEmployee = null;
        frmMn.mUserAccessControl(clsSQLMethods.mGetTextField(
                "SELECT Employee_position FROM tblEmployees WHERE Employee_id ="+
                    new frmLogin().mGetEmployeeID()));
        frmMn.setVisible(true);
        this.dispose();
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
        lblEmployeeInfo = new javax.swing.JLabel();
        lblFirstname = new javax.swing.JLabel();
        txtFirstname = new javax.swing.JTextField();
        lblSurname = new javax.swing.JLabel();
        txtSurname = new javax.swing.JTextField();
        lblBankAccNo = new javax.swing.JLabel();
        txtBackAccNo = new javax.swing.JTextField();
        lblBasicSalary = new javax.swing.JLabel();
        txtBasicSalary = new javax.swing.JTextField();
        lblPaymentMethod = new javax.swing.JLabel();
        lblEmployeeID = new javax.swing.JLabel();
        txtEmployeeID = new javax.swing.JTextField();
        cboPaymentMethods = new javax.swing.JComboBox<>();
        jpEnterSalaryAmounts = new javax.swing.JPanel();
        lblSalaryAmountsHeading = new javax.swing.JLabel();
        lblUIF = new javax.swing.JLabel();
        lblOvertimeHrs = new javax.swing.JLabel();
        txtOvertimeHrs = new javax.swing.JTextField();
        lblRatePerHour = new javax.swing.JLabel();
        txtRatePerHour = new javax.swing.JTextField();
        txtUIF = new javax.swing.JTextField();
        jpTableAndButtons = new javax.swing.JPanel();
        jsScrollPane = new javax.swing.JScrollPane();
        tblSalary = new javax.swing.JTable();
        btnCalculate = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblOutput = new javax.swing.JLabel();
        btnReturnToMain = new javax.swing.JButton();
        chkCalculateBonus = new javax.swing.JCheckBox();
        jpEmptyPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        dskPane.setBackground(new java.awt.Color(255, 255, 255));
        dskPane.setForeground(new java.awt.Color(0, 0, 0));

        jpEmployeeInformation.setBackground(new java.awt.Color(255, 255, 255));
        jpEmployeeInformation.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jpEmployeeInformation.setForeground(new java.awt.Color(255, 255, 255));

        lblEmployeeInfo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblEmployeeInfo.setText("Employee Information");

        lblFirstname.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblFirstname.setText("Firstname");

        lblSurname.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSurname.setText("Surname");

        lblBankAccNo.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBankAccNo.setText("Bank Account No");

        lblBasicSalary.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblBasicSalary.setText("Basic Salary");

        lblPaymentMethod.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPaymentMethod.setText("Payment method");

        lblEmployeeID.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblEmployeeID.setText("EmployeeID");

        txtEmployeeID.setToolTipText("");

        cboPaymentMethods.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Per Week", "Per Month" }));

        javax.swing.GroupLayout jpEmployeeInformationLayout = new javax.swing.GroupLayout(jpEmployeeInformation);
        jpEmployeeInformation.setLayout(jpEmployeeInformationLayout);
        jpEmployeeInformationLayout.setHorizontalGroup(
            jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEmployeeInfo)
                    .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                        .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                                                .addComponent(lblFirstname)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpEmployeeInformationLayout.createSequentialGroup()
                                                .addComponent(lblEmployeeID)
                                                .addGap(98, 98, 98)))
                                        .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                                            .addComponent(lblSurname)
                                            .addGap(115, 115, 115)))
                                    .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                                        .addComponent(lblBankAccNo)
                                        .addGap(72, 72, 72)))
                                .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                                    .addComponent(lblBasicSalary)
                                    .addGap(98, 98, 98)))
                            .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                                .addComponent(lblPaymentMethod)
                                .addGap(68, 68, 68)))
                        .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cboPaymentMethods, 0, 120, Short.MAX_VALUE)
                            .addComponent(txtBasicSalary, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(txtBackAccNo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSurname, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmployeeID, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFirstname, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jpEmployeeInformationLayout.setVerticalGroup(
            jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEmployeeInformationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblEmployeeInfo)
                .addGap(27, 27, 27)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmployeeID)
                    .addComponent(txtEmployeeID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFirstname)
                    .addComponent(txtFirstname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSurname)
                    .addComponent(txtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBankAccNo)
                    .addComponent(txtBackAccNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBasicSalary, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBasicSalary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jpEmployeeInformationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPaymentMethod)
                    .addComponent(cboPaymentMethods, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        jpEnterSalaryAmounts.setBackground(new java.awt.Color(255, 255, 255));
        jpEnterSalaryAmounts.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jpEnterSalaryAmounts.setForeground(new java.awt.Color(255, 255, 255));

        lblSalaryAmountsHeading.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblSalaryAmountsHeading.setText("Please Enter The Amounts");

        lblUIF.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUIF.setText("UIF");

        lblOvertimeHrs.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblOvertimeHrs.setText("Enter Overtime hrs");

        lblRatePerHour.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblRatePerHour.setText("Rate Per Hour");

        javax.swing.GroupLayout jpEnterSalaryAmountsLayout = new javax.swing.GroupLayout(jpEnterSalaryAmounts);
        jpEnterSalaryAmounts.setLayout(jpEnterSalaryAmountsLayout);
        jpEnterSalaryAmountsLayout.setHorizontalGroup(
            jpEnterSalaryAmountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEnterSalaryAmountsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jpEnterSalaryAmountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblSalaryAmountsHeading)
                    .addGroup(jpEnterSalaryAmountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEnterSalaryAmountsLayout.createSequentialGroup()
                            .addComponent(lblOvertimeHrs)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtOvertimeHrs, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEnterSalaryAmountsLayout.createSequentialGroup()
                            .addComponent(lblUIF, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(152, 152, 152)
                            .addComponent(txtUIF))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEnterSalaryAmountsLayout.createSequentialGroup()
                            .addComponent(lblRatePerHour)
                            .addGap(126, 126, 126)
                            .addComponent(txtRatePerHour, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jpEnterSalaryAmountsLayout.setVerticalGroup(
            jpEnterSalaryAmountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEnterSalaryAmountsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblSalaryAmountsHeading)
                .addGap(20, 20, 20)
                .addGroup(jpEnterSalaryAmountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRatePerHour)
                    .addComponent(txtRatePerHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEnterSalaryAmountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOvertimeHrs)
                    .addComponent(txtOvertimeHrs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jpEnterSalaryAmountsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUIF, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUIF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpTableAndButtons.setBackground(new java.awt.Color(255, 255, 255));
        jpTableAndButtons.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jpTableAndButtons.setForeground(new java.awt.Color(255, 255, 255));

        tblSalary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jsScrollPane.setViewportView(tblSalary);

        btnCalculate.setBackground(new java.awt.Color(255, 255, 255));
        btnCalculate.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCalculate.setText("Calculate");
        btnCalculate.setToolTipText("");
        btnCalculate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalculateActionPerformed(evt);
            }
        });

        btnSave.setBackground(new java.awt.Color(255, 255, 255));
        btnSave.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSave.setText("Save");
        btnSave.setToolTipText("");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnClear.setBackground(new java.awt.Color(255, 255, 255));
        btnClear.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        btnReturnToMain.setBackground(new java.awt.Color(255, 255, 255));
        btnReturnToMain.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnReturnToMain.setText("Return to Main Wndw");
        btnReturnToMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnToMainActionPerformed(evt);
            }
        });

        chkCalculateBonus.setBackground(new java.awt.Color(255, 255, 255));
        chkCalculateBonus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chkCalculateBonus.setText("Calculate Bonus");

        javax.swing.GroupLayout jpTableAndButtonsLayout = new javax.swing.GroupLayout(jpTableAndButtons);
        jpTableAndButtons.setLayout(jpTableAndButtonsLayout);
        jpTableAndButtonsLayout.setHorizontalGroup(
            jpTableAndButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTableAndButtonsLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jpTableAndButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCalculate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReturnToMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkCalculateBonus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        jpTableAndButtonsLayout.setVerticalGroup(
            jpTableAndButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTableAndButtonsLayout.createSequentialGroup()
                .addGroup(jpTableAndButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpTableAndButtonsLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(chkCalculateBonus)
                        .addGap(20, 20, 20)
                        .addGroup(jpTableAndButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCalculate))
                        .addGap(20, 20, 20)
                        .addComponent(btnSave)
                        .addGap(20, 20, 20)
                        .addComponent(btnClear)
                        .addGap(20, 20, 20)
                        .addComponent(btnReturnToMain))
                    .addGroup(jpTableAndButtonsLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(58, Short.MAX_VALUE))
        );

        jpEmptyPanel.setBackground(new java.awt.Color(255, 255, 255));
        jpEmptyPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jpEmptyPanel.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jpEmptyPanelLayout = new javax.swing.GroupLayout(jpEmptyPanel);
        jpEmptyPanel.setLayout(jpEmptyPanelLayout);
        jpEmptyPanelLayout.setHorizontalGroup(
            jpEmptyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jpEmptyPanelLayout.setVerticalGroup(
            jpEmptyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        dskPane.setLayer(jpEmployeeInformation, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dskPane.setLayer(jpEnterSalaryAmounts, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dskPane.setLayer(jpTableAndButtons, javax.swing.JLayeredPane.DEFAULT_LAYER);
        dskPane.setLayer(jpEmptyPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout dskPaneLayout = new javax.swing.GroupLayout(dskPane);
        dskPane.setLayout(dskPaneLayout);
        dskPaneLayout.setHorizontalGroup(
            dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dskPaneLayout.createSequentialGroup()
                .addGap(0, 31, Short.MAX_VALUE)
                .addGroup(dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dskPaneLayout.createSequentialGroup()
                        .addComponent(jpEmployeeInformation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jpEmptyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jpEnterSalaryAmounts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jpTableAndButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35))
        );
        dskPaneLayout.setVerticalGroup(
            dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dskPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dskPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpEmptyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpEmployeeInformation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpEnterSalaryAmounts, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpTableAndButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
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
                .addComponent(dskPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCalculateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalculateActionPerformed
        mSalaryCalculation();
    }//GEN-LAST:event_btnCalculateActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        mSaveSalary();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        mClear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void btnReturnToMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnToMainActionPerformed
        mReturnToMainWndw();
    }//GEN-LAST:event_btnReturnToMainActionPerformed

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
            java.util.logging.Logger.getLogger(frmEmployeeSalary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmEmployeeSalary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmEmployeeSalary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmEmployeeSalary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmEmployeeSalary().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalculate;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnReturnToMain;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cboPaymentMethods;
    private javax.swing.JCheckBox chkCalculateBonus;
    private javax.swing.JDesktopPane dskPane;
    private javax.swing.JPanel jpEmployeeInformation;
    private javax.swing.JPanel jpEmptyPanel;
    private javax.swing.JPanel jpEnterSalaryAmounts;
    private javax.swing.JPanel jpTableAndButtons;
    private javax.swing.JScrollPane jsScrollPane;
    private javax.swing.JLabel lblBankAccNo;
    private javax.swing.JLabel lblBasicSalary;
    private javax.swing.JLabel lblEmployeeID;
    private javax.swing.JLabel lblEmployeeInfo;
    private javax.swing.JLabel lblFirstname;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblOvertimeHrs;
    private javax.swing.JLabel lblPaymentMethod;
    private javax.swing.JLabel lblRatePerHour;
    private javax.swing.JLabel lblSalaryAmountsHeading;
    private javax.swing.JLabel lblSurname;
    private javax.swing.JLabel lblUIF;
    private javax.swing.JTable tblSalary;
    private javax.swing.JTextField txtBackAccNo;
    private javax.swing.JTextField txtBasicSalary;
    private javax.swing.JTextField txtEmployeeID;
    private javax.swing.JTextField txtFirstname;
    private javax.swing.JTextField txtOvertimeHrs;
    private javax.swing.JTextField txtRatePerHour;
    private javax.swing.JTextField txtSurname;
    private javax.swing.JTextField txtUIF;
    // End of variables declaration//GEN-END:variables
}
