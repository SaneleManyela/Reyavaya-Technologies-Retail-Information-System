/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sanele
 * 
 * This is the main interface of the inner (logged-in) interface.
 * The form handles transactions - the selling of computer electronics, 
 * and displays in a table products data. It is from this interface that other
 * interfaces can be displayed, either directly or indirectly.
 */
public class frmMain extends javax.swing.JFrame {

    /**
     * Creates new form frmMain
     */
    public frmMain() {
        initComponents();
        this.setTitle("Reyavaya Technologies"); //This sets the title of the form
        this.setSize(new Dimension(1120, 609));
        this.setResizable(false);
        this.setLocationRelativeTo(null); //Displays the form at the center of the window
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);  //When closed, the entire application exits
        
        tblProducts = modelAndDataMethods.mTable("SELECT Prod_Id, Prod_Name, Brand, Selling_price FROM tblProducts", tblProducts, dmTableModel); //Displays data on table
        modelAndDataMethods.mLoadToComboBox("SELECT Prod_name FROM tblProducts", cboProducts);
        modelAndDataMethods.mLoadToComboBox("SELECT Cus_name FROM tblCustomers", cboCustomer);
        
        txtTotal.setEditable(false);
        txtTotal.setToolTipText("This field is for output");
    } 
    
    /* A User Acess Control bestows to each user
     * minimum rights so that they can do their jobs.
     * An employee is prevented access to functions that
     * are not included in their job description.
    */
    public void mUserAccessControl(String strUser) {
        try{
            switch(strUser) {
                case "Manager":
                    mnuFile.setVisible(true);
                    mnuProductManagement.setVisible(true);
                    mnuSupplierManagement.setVisible(true);
                    mnuManageYourAccount.setVisible(true);
                    mnuEmployeeAccountsAndPayroll.setVisible(true);
                    mnuItemManageCustomers.setVisible(false);
                    btnTransact.setEnabled(false);
                    btnCustomer.setEnabled(false);
                    break;
                        
            case "Cashier":
                    mnuFile.setVisible(true);
                    mnuProductManagement.setVisible(false);
                    mnuSupplierManagement.setVisible(false);
                    mnuEmployeeAccountsAndPayroll.setVisible(false);
                    mnuManageYourAccount.setVisible(true);
                    mnuCustomerManagement.setVisible(true);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    } 
    
       
    private DefaultTableModel dmTableModel = new DefaultTableModel();
    
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
    clsModelAndDataMethods modelAndDataMethods = new clsModelAndDataMethods();
    clsSalesReport salesReport = new clsSalesReport();
    
    frmLogin frmLogin = new frmLogin();
    
    int intProductId;
    int intProductSupplierId;
    int intSaleId;
    double dblSellingPrice;
    double dblSaleTotal;
        
    //A method that gets values from the GUI
    private String[] mGetDetailsFromGUI() {
        return new String[] {
            cboProducts.getSelectedItem().toString(), txtQuantity.getText(),
            cboCustomer.getSelectedItem().toString()
        };
    }
    
    //A method that returns an ID that can be used for a record in the database
    private int mGetId(String strQuery, int intIdentifier) {
        //Get Id array of SalesId
        String[] arrIds = clsSQLMethods.mFetchRecord(strQuery);
        
        int intId = 0;
        if(arrIds != null) {
            if(arrIds.length == 0) {
                intId = 1;
            } else {
                intId = Integer.parseInt(arrIds[(arrIds.length - 1)]);
            }
        } 
        return intId + intIdentifier;
    }
    
    //A method that records to the database a sale 
    private boolean mRecordSalesQuery() {
        intSaleId = mGetId("SELECT Sale_Id FROM tblSales ORDER BY Sale_Id ASC", 6);
        
        return clsSQLMethods.mCreateRecord("INSERT INTO tblSales(Prod_id, Supp_id,"
                + " Sale_Id, Sold_qty, Selling_price, Sale_Total, Sale_Date)"
                + "VALUES('"+intProductId+"','"+intProductSupplierId+"','"+intSaleId+
                "','"+mGetDetailsFromGUI()[1]+"','"+dblSellingPrice+"','"+
                dblSaleTotal+"','"+new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+"')");
    }
        
    //A method that returns a string query to record a transaction to the database
    private String mRecordTransactionQuery() {
        return "INSERT INTO tblTransaction(Cashier_id, Cus_id, Sale_id, Transaction_date)"
                + "VALUES('"+frmLogin.mGetEmployeeID()+"','"+clsSQLMethods.mGetNumericField(
                        "SELECT Cus_id FROM tblCustomers WHERE Cus_name ='"+mGetDetailsFromGUI()[2]+"'")
                +"','"+intSaleId+"','"+new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+"')";
    }
           
    //A method that update stock when a product is bought
    private String mUpdateStock() {
        int intProductStockQuantity =
                clsSQLMethods.mGetNumericField("SELECT Qty FROM tblStock WHERE Prod_id='"+intProductId+"'");
        
        return "UPDATE tblStock SET Qty ='"+(intProductStockQuantity - Integer.parseInt(txtQuantity.getText()))+
                "' WHERE Prod_id='"+intProductId+"'";
    }
    
    private double mHandlePayment(Double dblTotal) {
        String strInput;
        strInput = JOptionPane.showInputDialog(null, "Enter Payment amount", "Payment",
                            JOptionPane.PLAIN_MESSAGE); //Get input value
        
        try{
            if(!(strInput.equals("")) && !(Double.parseDouble(strInput) < dblTotal)){
                
                return Double.parseDouble(strInput); 
                
            } else if(Double.parseDouble(strInput) < dblTotal) {
                
                JOptionPane.showMessageDialog(null, "The Payment amount must be equal or"
                        + " greater than the transaction total amount.", "ERROR", 
                        JOptionPane.ERROR_MESSAGE);
                
            }
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Input the total amount of the transaction.",
                "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return 0.0;
    }
    
    private double mHandleShortPaymentAmount(Double dblChange) {
        String strInput;
        strInput = JOptionPane.showInputDialog(null, "Payment amount is short with R "+ (dblChange * -1)+
                ".\nAsk for the remaining amount.", "Cash Payment",
                            JOptionPane.PLAIN_MESSAGE); //Get input value
        
        try{
            if(!(strInput.equals("")) && !(Double.parseDouble(strInput) < (dblChange * -1))){
                
                return Double.parseDouble(strInput); 
                
            }
        } catch(NumberFormatException | NullPointerException e){
            if(strInput.equals("") && Double.parseDouble(strInput) < (dblChange * -1)) {
                
                JOptionPane.showMessageDialog(null, "There must be input and it must be the amount short or more.\n"
                        + "The entered amount cannot be less than the amount short.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
                
            } else if(strInput.equals("")) {
                
                JOptionPane.showMessageDialog(null, "There must be input and it must be the amount short or more.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
                
            } else if(Double.parseDouble(strInput) < (dblChange * -1)) {
                
                JOptionPane.showMessageDialog(null,"The entered amount cannot be less than the amount short.",
                    "ERROR", JOptionPane.ERROR_MESSAGE);
                
            }
        }
        return 0.0;
    }
    
    
    private void mTransact() {
        if(mRecordSalesQuery()){
            if(clsSQLMethods.mCreateRecord(mRecordTransactionQuery())) {
                if(clsSQLMethods.mUpdateRecord(mUpdateStock())) {
                    
                    JOptionPane.showMessageDialog(this, "Transaction Completed", "MESSAGE",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
    
    // The actual implementation of the transacting process
    private void mTransaction() {
        try{
            intProductId = clsSQLMethods.mGetNumericField(
                "SELECT Prod_id FROM tblProducts WHERE Prod_name='"+mGetDetailsFromGUI()[0]+"'");
            
            int intStockQuantity = clsSQLMethods.mGetNumericField("SELECT Qty FROM tblStock WHERE Prod_id ="+intProductId);
            
            if(intStockQuantity == 0) {
                
                JOptionPane.showMessageDialog(this, cboProducts.getSelectedItem().toString()+" is out of stock", "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            } else if(intStockQuantity <= 25) {
                
                JOptionPane.showMessageDialog(this, "There are "+intStockQuantity+
                        " "+cboProducts.getSelectedItem().toString()+" left in stock", "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            } else {
                
                dblSellingPrice = Double.parseDouble(clsSQLMethods.mGetTextField(
                        "SELECT Selling_price FROM tblProducts WHERE Prod_id ='"+intProductId+"'"));
                
                dblSaleTotal = modelAndDataMethods.mFormat(dblSellingPrice  * Double.parseDouble(txtQuantity.getText()));
                txtTotal.setText(String.valueOf(dblSaleTotal));
                
                double dblPaymentAmount = mHandlePayment(dblSaleTotal);
                
                Double dblChange = modelAndDataMethods.mFormat(dblPaymentAmount - dblSaleTotal);
            
                if(dblPaymentAmount == 0.0){
                    
                    JOptionPane.showMessageDialog(null, "Transaction Cancelled!!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    
                } else if(dblChange < 0){
                    
                    dblChange = modelAndDataMethods.mFormat(dblPaymentAmount + mHandleShortPaymentAmount(dblChange) - dblSaleTotal);
                    lblOutput.setText(String.valueOf(dblChange));
                    
                    intProductSupplierId = clsSQLMethods.mGetNumericField(
                        "SELECT Supp_name FROM tblProducts WHERE Prod_id='"+intProductId+"'");
                    mTransact();
                    
                } else {
                    lblOutput.setText("The change is "+dblChange);
                    
                    intProductSupplierId = clsSQLMethods.mGetNumericField(
                        "SELECT Supp_name FROM tblProducts WHERE Prod_id='"+intProductId+"'");
                    mTransact();
                }
            }
        } catch(IllegalArgumentException | StackOverflowError e) {
            JOptionPane.showMessageDialog(this, "Something is wrong with the quantity or payment amount provided",
                "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mClear() {
        txtQuantity.setText("");
        txtTotal.setText("");
        lblOutput.setText("");
    }
           
    private void mRefreshWindow() {
        dmTableModel = new DefaultTableModel();
        tblProducts = modelAndDataMethods.mTable(
                "SELECT Prod_Id, Prod_Name, Brand, Selling_price FROM tblProducts",
                tblProducts, dmTableModel);
        mClear();
    }
    
    private void mLogOut() {
        frmLogin.mSetEmployeeID(0);
        frmLogin.setVisible(true);
        this.dispose();
    }
    
    private void mExit() {
        System.exit(0);
    }
    
    private void mProductManagement() {
        new dialogProduct().setVisible(true);;
    }
    
    private void mManageProductStock() {
        new clsManageStock();
    }
    
    private void mViewStock() {
        new clsViewStock().setVisible(true);
    }
    
    private void mFilterProducts(String strButtonOfOrigin) {
        dmTableModel = new DefaultTableModel();
        new clsFilterProductByCategory(strButtonOfOrigin);            
        tblProducts = modelAndDataMethods.mTable("SELECT Prod_Id, Prod_Name, Brand, Selling_price"
                + " FROM tblProducts WHERE Prod_Name  LIKE '%"+clsFilterProductByCategory.strCategory+"'",
                    tblProducts, dmTableModel);
    }
       
    private void mEmployeeAccountManagement() {
        dialogEmployee cashier = new dialogEmployee();
        cashier.setVisible(true);
    }
    
    private void mAddAndManageEmployeeAccount() {
        new frmEmployeePortal().setVisible(true);
        this.dispose();
    }
    
    private void mCalculateEmployeeSalary() {
        new frmEmployeeSalary().setVisible(true);
        this.dispose();
    }
    
    private void mManageSupplierAccount() {
        new dialogStakeholders("Supplier").setVisible(true);
    }
    
    private void mCustomerAccountManagement() {
        dialogStakeholders customerDialog = new dialogStakeholders("Customer");
        customerDialog.setVisible(true);
    }
    
    private void mViewAllSuppliers() {
        new dialogStakeholders("").new clsViewStakeholder(
                "SELECT Supp_name, Supp_address, Supp_contact, Supp_email FROM tblSupplier",
                "Supplier").setVisible(true);
    }
    
    private void mViewAllCustomers() {
        new dialogStakeholders("").new clsViewStakeholder("SELECT Cus_name, Cus_address, Cus_contact, Cus_email FROM tblCustomers",
                "Customer").setVisible(true);
    }
    
    private void mViewFilteredSuppliers() {
        new dialogStakeholders("").new clsFilterByAlphabetDialog("Supplier").setVisible(true);
    }
    
    private void mViewFilteredCustomers() {
        new dialogStakeholders("").new clsFilterByAlphabetDialog("Customer").setVisible(true);
    }
    
    private void mCurrentMonthSalesReport() {
        salesReport.mCurrentMonthSalesReport();
    }
    
    private void mCurrentWeekSalesReport() {
        salesReport.mThisWeekSalesReport();
    }
    
    private void mCurrentYearSalesReport() {
        salesReport.mCurrentYearSales();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPanelMain = new javax.swing.JPanel();
        lblHeading = new javax.swing.JLabel();
        jpMain = new javax.swing.JPanel();
        lblProducts = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        cboProducts = new javax.swing.JComboBox<>();
        lblCustomer = new javax.swing.JLabel();
        cboCustomer = new javax.swing.JComboBox<>();
        btnCustomer = new javax.swing.JButton();
        txtTotal = new javax.swing.JTextField();
        lblTotal = new javax.swing.JLabel();
        btnTransact = new javax.swing.JButton();
        btnStock = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        lblOutput = new javax.swing.JLabel();
        spTablePane = new javax.swing.JScrollPane();
        tblProducts = new javax.swing.JTable();
        btnFilterProducts = new javax.swing.JButton();
        mbMenuBar = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuItemRefresh = new javax.swing.JMenuItem();
        mnuItemLogout = new javax.swing.JMenuItem();
        mnuItemExit = new javax.swing.JMenuItem();
        mnuProductManagement = new javax.swing.JMenu();
        mnuItemProjectManagement = new javax.swing.JMenuItem();
        mnuItemManageProductStock = new javax.swing.JMenuItem();
        mnuSupplierManagement = new javax.swing.JMenu();
        mnuItemManageSupplierAccount = new javax.swing.JMenuItem();
        mnuViewSupplier = new javax.swing.JMenu();
        mnuItemViewAllSuppliers = new javax.swing.JMenuItem();
        mnuItemViewFilteredSupplier = new javax.swing.JMenuItem();
        mnuSalesReport = new javax.swing.JMenu();
        mnuItemCurrentMonthSalesReport = new javax.swing.JMenuItem();
        mnuItemWeeklySalesReport = new javax.swing.JMenuItem();
        mnuItemYearlySalesReport = new javax.swing.JMenuItem();
        mnuManageYourAccount = new javax.swing.JMenu();
        mnuItemCashierAccount = new javax.swing.JMenuItem();
        mnuCustomerManagement = new javax.swing.JMenu();
        mnuItemManageCustomers = new javax.swing.JMenuItem();
        mnuViewCustomers = new javax.swing.JMenu();
        mnuItemViewAllCustomers = new javax.swing.JMenuItem();
        mnuItemViewFilteredCustomers = new javax.swing.JMenuItem();
        mnuEmployeeAccountsAndPayroll = new javax.swing.JMenu();
        mnuItemAddAndManageEmployeeAccounts = new javax.swing.JMenuItem();
        mnuItemCalculateEmployeeSalary = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jpPanelMain.setBackground(new java.awt.Color(255, 255, 255));

        lblHeading.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblHeading.setText("Point Of Sales");

        jpMain.setBackground(new java.awt.Color(255, 255, 255));

        lblProducts.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblProducts.setText("Products");

        lblQuantity.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblQuantity.setText("Quantity");

        lblCustomer.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblCustomer.setText("Customer");

        btnCustomer.setBackground(new java.awt.Color(255, 255, 255));
        btnCustomer.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCustomer.setText("Add");
        btnCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCustomerActionPerformed(evt);
            }
        });

        txtTotal.setToolTipText("");

        lblTotal.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblTotal.setText("Total");

        btnTransact.setBackground(new java.awt.Color(255, 255, 255));
        btnTransact.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnTransact.setText("Transact");
        btnTransact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactActionPerformed(evt);
            }
        });

        btnStock.setBackground(new java.awt.Color(255, 255, 255));
        btnStock.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnStock.setText("Stock");
        btnStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStockActionPerformed(evt);
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

        javax.swing.GroupLayout jpMainLayout = new javax.swing.GroupLayout(jpMain);
        jpMain.setLayout(jpMainLayout);
        jpMainLayout.setHorizontalGroup(
            jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpMainLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpMainLayout.createSequentialGroup()
                        .addComponent(lblCustomer)
                        .addGap(152, 152, 152)
                        .addComponent(cboCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpMainLayout.createSequentialGroup()
                        .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblQuantity)
                            .addComponent(lblProducts)
                            .addComponent(lblTotal))
                        .addGap(156, 156, 156)
                        .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jpMainLayout.createSequentialGroup()
                        .addComponent(btnTransact, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(45, 45, 45)
                        .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpMainLayout.createSequentialGroup()
                                .addComponent(btnStock, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(45, 45, 45)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(12, 12, 12))
        );
        jpMainLayout.setVerticalGroup(
            jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpMainLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCustomer)
                    .addComponent(cboCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCustomer))
                .addGap(20, 20, 20)
                .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblQuantity)
                    .addGroup(jpMainLayout.createSequentialGroup()
                        .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblProducts))
                        .addGap(20, 20, 20)
                        .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20)
                .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTotal)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(lblOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(172, 172, 172)
                .addGroup(jpMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnTransact)
                    .addComponent(btnStock)
                    .addComponent(btnClear))
                .addGap(42, 42, 42))
        );

        tblProducts.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tblProducts.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        spTablePane.setViewportView(tblProducts);

        btnFilterProducts.setBackground(new java.awt.Color(255, 255, 255));
        btnFilterProducts.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnFilterProducts.setText("Filter Products");
        btnFilterProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterProductsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpPanelMainLayout = new javax.swing.GroupLayout(jpPanelMain);
        jpPanelMain.setLayout(jpPanelMainLayout);
        jpPanelMainLayout.setHorizontalGroup(
            jpPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPanelMainLayout.createSequentialGroup()
                .addGroup(jpPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPanelMainLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(lblHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPanelMainLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jpMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addGroup(jpPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spTablePane, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilterProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );
        jpPanelMainLayout.setVerticalGroup(
            jpPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPanelMainLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jpPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFilterProducts))
                .addGroup(jpPanelMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPanelMainLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(spTablePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPanelMainLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35))
        );

        mnuFile.setText("File");

        mnuItemRefresh.setText("Refresh");
        mnuItemRefresh.setToolTipText("");
        mnuItemRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemRefreshActionPerformed(evt);
            }
        });
        mnuFile.add(mnuItemRefresh);

        mnuItemLogout.setText("Log out");
        mnuItemLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemLogoutActionPerformed(evt);
            }
        });
        mnuFile.add(mnuItemLogout);

        mnuItemExit.setText("Exit");
        mnuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuItemExit);

        mbMenuBar.add(mnuFile);

        mnuProductManagement.setText("Manage Products");

        mnuItemProjectManagement.setText("Products Management");
        mnuItemProjectManagement.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemProjectManagementActionPerformed(evt);
            }
        });
        mnuProductManagement.add(mnuItemProjectManagement);

        mnuItemManageProductStock.setText("Manage Product Stock");
        mnuItemManageProductStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemManageProductStockActionPerformed(evt);
            }
        });
        mnuProductManagement.add(mnuItemManageProductStock);

        mbMenuBar.add(mnuProductManagement);

        mnuSupplierManagement.setText("Manage Supplier Account");

        mnuItemManageSupplierAccount.setText("Manage Supplier Account");
        mnuItemManageSupplierAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemManageSupplierAccountActionPerformed(evt);
            }
        });
        mnuSupplierManagement.add(mnuItemManageSupplierAccount);

        mnuViewSupplier.setText("View Supplier");

        mnuItemViewAllSuppliers.setText("View All Suppliers");
        mnuItemViewAllSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemViewAllSuppliersActionPerformed(evt);
            }
        });
        mnuViewSupplier.add(mnuItemViewAllSuppliers);

        mnuItemViewFilteredSupplier.setText("View Filtered Suppliers");
        mnuItemViewFilteredSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemViewFilteredSupplierActionPerformed(evt);
            }
        });
        mnuViewSupplier.add(mnuItemViewFilteredSupplier);

        mnuSupplierManagement.add(mnuViewSupplier);

        mbMenuBar.add(mnuSupplierManagement);

        mnuSalesReport.setText("Sales Report");

        mnuItemCurrentMonthSalesReport.setText("Current Month Sales Report");
        mnuItemCurrentMonthSalesReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemCurrentMonthSalesReportActionPerformed(evt);
            }
        });
        mnuSalesReport.add(mnuItemCurrentMonthSalesReport);

        mnuItemWeeklySalesReport.setText("Weekly Sales Report");
        mnuItemWeeklySalesReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemWeeklySalesReportActionPerformed(evt);
            }
        });
        mnuSalesReport.add(mnuItemWeeklySalesReport);

        mnuItemYearlySalesReport.setText("Yearly Sales Report");
        mnuItemYearlySalesReport.setToolTipText("");
        mnuItemYearlySalesReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemYearlySalesReportActionPerformed(evt);
            }
        });
        mnuSalesReport.add(mnuItemYearlySalesReport);

        mbMenuBar.add(mnuSalesReport);

        mnuManageYourAccount.setText("Manage Your Account");

        mnuItemCashierAccount.setText("Manage Account");
        mnuItemCashierAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemCashierAccountActionPerformed(evt);
            }
        });
        mnuManageYourAccount.add(mnuItemCashierAccount);

        mbMenuBar.add(mnuManageYourAccount);

        mnuCustomerManagement.setText("Customer Management");

        mnuItemManageCustomers.setText("Manage Customers");
        mnuItemManageCustomers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemManageCustomersActionPerformed(evt);
            }
        });
        mnuCustomerManagement.add(mnuItemManageCustomers);

        mnuViewCustomers.setText("View Customers");

        mnuItemViewAllCustomers.setText("View All Customers");
        mnuItemViewAllCustomers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemViewAllCustomersActionPerformed(evt);
            }
        });
        mnuViewCustomers.add(mnuItemViewAllCustomers);

        mnuItemViewFilteredCustomers.setText("View Filtered Customers");
        mnuItemViewFilteredCustomers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemViewFilteredCustomersActionPerformed(evt);
            }
        });
        mnuViewCustomers.add(mnuItemViewFilteredCustomers);

        mnuCustomerManagement.add(mnuViewCustomers);

        mbMenuBar.add(mnuCustomerManagement);

        mnuEmployeeAccountsAndPayroll.setText("Employee Accounts & Payroll");

        mnuItemAddAndManageEmployeeAccounts.setText("Add & Manage Employee Accounts");
        mnuItemAddAndManageEmployeeAccounts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemAddAndManageEmployeeAccountsActionPerformed(evt);
            }
        });
        mnuEmployeeAccountsAndPayroll.add(mnuItemAddAndManageEmployeeAccounts);

        mnuItemCalculateEmployeeSalary.setText("Calculate Employee Salary");
        mnuItemCalculateEmployeeSalary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuItemCalculateEmployeeSalaryActionPerformed(evt);
            }
        });
        mnuEmployeeAccountsAndPayroll.add(mnuItemCalculateEmployeeSalary);

        mbMenuBar.add(mnuEmployeeAccountsAndPayroll);

        setJMenuBar(mbMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPanelMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jpPanelMain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuItemRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemRefreshActionPerformed
        mRefreshWindow();
    }//GEN-LAST:event_mnuItemRefreshActionPerformed

    private void mnuItemLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemLogoutActionPerformed
       mLogOut();
    }//GEN-LAST:event_mnuItemLogoutActionPerformed

    private void mnuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemExitActionPerformed
        mExit();
    }//GEN-LAST:event_mnuItemExitActionPerformed

    private void mnuItemProjectManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemProjectManagementActionPerformed
        mProductManagement();
    }//GEN-LAST:event_mnuItemProjectManagementActionPerformed

    private void mnuItemManageProductStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemManageProductStockActionPerformed
        mManageProductStock();
    }//GEN-LAST:event_mnuItemManageProductStockActionPerformed

    private void mnuItemManageSupplierAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemManageSupplierAccountActionPerformed
        mManageSupplierAccount();
    }//GEN-LAST:event_mnuItemManageSupplierAccountActionPerformed

    private void mnuItemCurrentMonthSalesReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemCurrentMonthSalesReportActionPerformed
        mCurrentMonthSalesReport();
    }//GEN-LAST:event_mnuItemCurrentMonthSalesReportActionPerformed

    private void mnuItemWeeklySalesReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemWeeklySalesReportActionPerformed
        mCurrentWeekSalesReport();
    }//GEN-LAST:event_mnuItemWeeklySalesReportActionPerformed

    private void mnuItemCashierAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemCashierAccountActionPerformed
        mEmployeeAccountManagement();
    }//GEN-LAST:event_mnuItemCashierAccountActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
        mCustomerAccountManagement();
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnTransactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactActionPerformed
        mTransaction();
    }//GEN-LAST:event_btnTransactActionPerformed

    private void btnStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStockActionPerformed
        mViewStock();
    }//GEN-LAST:event_btnStockActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        mClear();
    }//GEN-LAST:event_btnClearActionPerformed

    private void mnuItemViewAllSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemViewAllSuppliersActionPerformed
        mViewAllSuppliers();
    }//GEN-LAST:event_mnuItemViewAllSuppliersActionPerformed

    private void mnuItemYearlySalesReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemYearlySalesReportActionPerformed
        mCurrentYearSalesReport();
    }//GEN-LAST:event_mnuItemYearlySalesReportActionPerformed

    private void mnuItemViewAllCustomersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemViewAllCustomersActionPerformed
        mViewAllCustomers();
    }//GEN-LAST:event_mnuItemViewAllCustomersActionPerformed

    private void mnuItemViewFilteredSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemViewFilteredSupplierActionPerformed
        mViewFilteredSuppliers();
    }//GEN-LAST:event_mnuItemViewFilteredSupplierActionPerformed

    private void mnuItemViewFilteredCustomersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemViewFilteredCustomersActionPerformed
        mViewFilteredCustomers();
    }//GEN-LAST:event_mnuItemViewFilteredCustomersActionPerformed

    private void mnuItemManageCustomersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemManageCustomersActionPerformed
        mCustomerAccountManagement();
    }//GEN-LAST:event_mnuItemManageCustomersActionPerformed

    private void btnFilterProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterProductsActionPerformed
        mFilterProducts(btnFilterProducts.getText());
    }//GEN-LAST:event_btnFilterProductsActionPerformed

    private void mnuItemAddAndManageEmployeeAccountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemAddAndManageEmployeeAccountsActionPerformed
        mAddAndManageEmployeeAccount();
    }//GEN-LAST:event_mnuItemAddAndManageEmployeeAccountsActionPerformed

    private void mnuItemCalculateEmployeeSalaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuItemCalculateEmployeeSalaryActionPerformed
       mCalculateEmployeeSalary();
    }//GEN-LAST:event_mnuItemCalculateEmployeeSalaryActionPerformed

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
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new frmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnFilterProducts;
    private javax.swing.JButton btnStock;
    private javax.swing.JButton btnTransact;
    private javax.swing.JComboBox<String> cboCustomer;
    private javax.swing.JComboBox<String> cboProducts;
    private javax.swing.JPanel jpMain;
    private javax.swing.JPanel jpPanelMain;
    private javax.swing.JLabel lblCustomer;
    private javax.swing.JLabel lblHeading;
    private javax.swing.JLabel lblOutput;
    private javax.swing.JLabel lblProducts;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JMenuBar mbMenuBar;
    private javax.swing.JMenu mnuCustomerManagement;
    private javax.swing.JMenu mnuEmployeeAccountsAndPayroll;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuItemAddAndManageEmployeeAccounts;
    private javax.swing.JMenuItem mnuItemCalculateEmployeeSalary;
    private javax.swing.JMenuItem mnuItemCashierAccount;
    private javax.swing.JMenuItem mnuItemCurrentMonthSalesReport;
    private javax.swing.JMenuItem mnuItemExit;
    private javax.swing.JMenuItem mnuItemLogout;
    private javax.swing.JMenuItem mnuItemManageCustomers;
    private javax.swing.JMenuItem mnuItemManageProductStock;
    private javax.swing.JMenuItem mnuItemManageSupplierAccount;
    private javax.swing.JMenuItem mnuItemProjectManagement;
    private javax.swing.JMenuItem mnuItemRefresh;
    private javax.swing.JMenuItem mnuItemViewAllCustomers;
    private javax.swing.JMenuItem mnuItemViewAllSuppliers;
    private javax.swing.JMenuItem mnuItemViewFilteredCustomers;
    private javax.swing.JMenuItem mnuItemViewFilteredSupplier;
    private javax.swing.JMenuItem mnuItemWeeklySalesReport;
    private javax.swing.JMenuItem mnuItemYearlySalesReport;
    private javax.swing.JMenu mnuManageYourAccount;
    private javax.swing.JMenu mnuProductManagement;
    private javax.swing.JMenu mnuSalesReport;
    private javax.swing.JMenu mnuSupplierManagement;
    private javax.swing.JMenu mnuViewCustomers;
    private javax.swing.JMenu mnuViewSupplier;
    private javax.swing.JScrollPane spTablePane;
    private javax.swing.JTable tblProducts;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
