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
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Sanele
 */
public class dialogStakeholders extends JDialog {

    /**
     * Creates new form dialogStakeholders
     * @param strPerson
     */
    public dialogStakeholders(String strPerson) {
        super(null, strPerson+" Account", JDialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        
        lblDialogHeading.setText(strPerson+" Account");
        this.strPerson = strPerson;
        
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        txtAddress.setWrapStyleWord(true);
    }
    
    private String[] arrStakeholderUpdateDetails; //A An array to store values from the database
    private String strId; //An string variable to store the ID of the stakeholder worked with
    private String strPerson; //The stakeholder worked with
    
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    clsGUIDesignMethods gui = new clsGUIDesignMethods();
    clsModelAndDataMethods modelAndDataMethods = new clsModelAndDataMethods();
    clsValidationMethods validation = new clsValidationMethods();
    
    //A method that sets values to the GUI
    private void mSetDetailsToGUI() {
        this.strId = arrStakeholderUpdateDetails[0];
        this.txtName.setText(arrStakeholderUpdateDetails[1]);
        this.txtEmail.setText(arrStakeholderUpdateDetails[4]);
        this.txtAddress.setText(arrStakeholderUpdateDetails[2]);
        this.txtContactNo.setText(arrStakeholderUpdateDetails[3]);
    }
        
    //A method that retrieves and assign to an array input
    //from the GUI
    private String[] mGetDetailsFromGUI() {
        String[] arrGUIValues = new String[5];
        arrGUIValues[0] = this.txtName.getText();
        arrGUIValues[1] = this.txtAddress.getText();
        arrGUIValues[2] = this.txtContactNo.getText();
        arrGUIValues[3] = this.txtEmail.getText();
        return arrGUIValues;
    }
       
    //A method that verifies if input controls have been passed values
    private String mVerifyInput() {
        if(this.txtName.getText().equals("")) {
            return "Provide persons name";
            
        } else if(this.txtEmail.getText().equals("")) {
            return "Provide persons email";
            
        }else if(this.txtAddress.getText().equals("")) {
            return "Provide persons address";
            
        } else if(this.txtContactNo.getText().equals("")) {
            return "Provide persons contact no.";
            
        } else if(this.txtContactNo.getText().length() != 10) {
            return "A valid South African contact number can only be 10 digits";
        } 
        return ""; 
    }
    
    //A method that Adds a new customer or supplier account to the database
    private void mCreateStakeholder() {
        if(mVerifyInput().equals("")) {
            if(validation.mValidateEmail(txtEmail.getText()).equals("") && 
                    validation.mValidateContactNumber(txtContactNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()).equals("") && 
                    !clsSQLMethods.mCheckIfDetailsExist(
                            "SELECT Cus_name FROM tblCustomers WHERE Cus_name='"+txtName.getText()+"'")) {
                
                String[] arrStakeholderDetails = mGetDetailsFromGUI();
                switch(strPerson)
                {
                    case "Customer":
                        if(clsSQLMethods.mCreateRecord("INSERT INTO tblCustomers(Cus_name, Cus_address, Cus_contact, Cus_email)"
                             + "VALUES('"+arrStakeholderDetails[0]+"','"+arrStakeholderDetails[1]+"','"+arrStakeholderDetails[2]+
                                "','"+arrStakeholderDetails[3]+"')")) {
                    
                            JOptionPane.showMessageDialog(this, "Customer account has been created", "MESSAGE",
                            JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                        
                    case "Supplier":
                        if(clsSQLMethods.mCreateRecord(
                                "INSERT INTO tblSupplier(Supp_name, Supp_address, Supp_contact, Supp_email)"
                                + "VALUES('"+arrStakeholderDetails[0]+"', '"+arrStakeholderDetails[1]+"', '"+
                                        arrStakeholderDetails[2]+"', '"+arrStakeholderDetails[3]+"')")) {
                    
                            JOptionPane.showMessageDialog(this, "Supplier account has been created", "MESSAGE",
                            JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                }
                
            } else if(validation.mValidateEmail(txtEmail.getText()).equals("")) {
                
                    JOptionPane.showMessageDialog(this, validation.mValidateEmail(txtEmail.getText()), "WARNING",
                            JOptionPane.WARNING_MESSAGE);
                    
            } else if(!validation.mValidateContactNumber(txtContactNo.getText()).equals("")) {
                
                JOptionPane.showMessageDialog(this, validation.mValidateContactNumber(txtContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            } else if(!validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()).equals("")) {
                
                JOptionPane.showMessageDialog(this, validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()), "WARNING",
                        JOptionPane.WARNING_MESSAGE);
                
            }else if(strPerson.equals("Customer")) {
                
                if(clsSQLMethods.mCheckIfDetailsExist(
                        "SELECT Cus_name FROM tblCustomers WHERE Cus_name='"+txtName.getText()+"'")) {
            
                    JOptionPane.showMessageDialog(this, "This customer already exists, please provide an alternative.", "WARNING",
                    JOptionPane.WARNING_MESSAGE);
                }
                
            } else if(strPerson.equals("Supplier")) {
                
                if(clsSQLMethods.mCheckIfDetailsExist(
                        "SELECT Supp_name FROM tblSuppliers WHERE Supp_name='"+txtName.getText()+"'")) {
            
                    JOptionPane.showMessageDialog(this, "This supplier already exists, please provide an alternative.", 
                        "WARNING", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, mVerifyInput(),
                "WARNING", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    //A method that instantiates an object of class clsSelectStakeholderToUpdateDialog.
    //This instance will be used to select a stakeholder to update
    private void mSelectStakeholderToUpdate(){    
        switch(strPerson){
            case "Customer":
                clsSelectStakeholderToUpdateDialog customerUpdate = new clsSelectStakeholderToUpdateDialog("SELECT Cus_name FROM tblCustomers");
                break;
            case "Supplier":
                clsSelectStakeholderToUpdateDialog supplierUpdate = new clsSelectStakeholderToUpdateDialog("SELECT Supp_name FROM tblSupplier");
                break;    
        }
    }
    
    //A method that saves updated stakeholder account details to the database
    private void mSaveUpdatedStakeholderDetails() {
        if(mVerifyInput().equals("")) {
            if(validation.mValidateEmail(txtEmail.getText()).equals("") && 
                    validation.mValidateContactNumber(txtContactNo.getText()).equals("") &&
                    validation.mCheckIfFieldIsOnlyDigits(txtContactNo.getText()).equals("")){
                
                String[] arrStakeholderDetails = mGetDetailsFromGUI(); 
                switch(strPerson) {
                    case "Customer":
                        if(clsSQLMethods.mUpdateRecord("UPDATE tblCustomers SET Cus_name ='"+arrStakeholderDetails[0]+"', Cus_address='"+
                                arrStakeholderDetails[1] + "', Cus_contact ='"+ arrStakeholderDetails[2] +"', Cus_email ='"+
                                arrStakeholderDetails[3] +"' WHERE Cus_ID ='"+strId+"'")) {
                    
                            JOptionPane.showMessageDialog(this, "Customer account is updated", "MESSAGE",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                        }
                        break;
                    case "Supplier":
                        if(clsSQLMethods.mUpdateRecord(
                                "UPDATE tblSupplier SET Supp_name ='"+arrStakeholderDetails[0]+"', Supp_address='"+
                                arrStakeholderDetails[1] + "', Supp_contact ='"+ arrStakeholderDetails[2] +"', Supp_email ='"+
                                arrStakeholderDetails[3] +"' WHERE Supp_ID ='"+strId+"'")) {
                    
                            JOptionPane.showMessageDialog(this, "Supplier account is updated", "MESSAGE",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
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
            }
        }
    }
        
    //A method that clears values in the GUI text boxes
    private void mClear() {
        txtName.setText("");
        txtAddress.setText("");
        txtContactNo.setText("");
        txtEmail.setText("");
        btnUpdate.setText("Update");
    }
    
    /* A dialog class that is used to select a
    * stakeholder to update
    */
    public class clsSelectStakeholderToUpdateDialog extends JDialog {
        public clsSelectStakeholderToUpdateDialog(String strQuery) {
            super(dialogStakeholders.this, "Update "+strPerson, Dialog.ModalityType.APPLICATION_MODAL); //Set the dialog to rquire all the focus
            this.setSize(400, 200); //sets size of the dialog
            this.setResizable(false);
            this.setLocationRelativeTo(null); //displays the dialog at the very center
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Causes the dialog to be exited but not the entire app
            this.add(mUpdateDialogGUI(strQuery));
            this.setVisible(true);
        }   
        //A combo box to hold vales from the database
        JComboBox cboCombo = new JComboBox();
        
        private JPanel mUpdateDialogGUI(String strQuery) {
            JPanel jpContainer = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
            jpContainer.setBorder(new EmptyBorder(20, 20, 20, 20)); //Sets a border 
            jpContainer = gui.mPreparePanel(jpContainer); //Sets background colour to the JPanel
            
            jpContainer.add(gui.mCreateLabel(
                    "Select a "+strPerson, new Font("Tahoma", Font.BOLD, 14)), BorderLayout.NORTH);
            
            jpContainer.add(mCreateDialogCenter(strQuery));  
            
            switch (strPerson) {
                case "Customer":
                    jpContainer.add(mCreateDialogBottom(this::mFetchForCustomerUpdate), BorderLayout.SOUTH);
                    break;
                case "Supplier":
                    jpContainer.add(mCreateDialogBottom(this::mFetchForSupplierUpdate), BorderLayout.SOUTH);
                    break;
                case "Supplier Account":
                    jpContainer.add(mCreateDialogBottom(this::mFetchSupplierId), BorderLayout.SOUTH);
                    break;
                default:
                    break;
            }
            return jpContainer;
        }
        
        //A method that returns a JPanel containing the center part
        //of this class' GUI
        private JPanel mCreateDialogCenter(String strQuery) {
            JPanel jpCenterPart = new JPanel(new BorderLayout()); //A JPanel to contain the center part of the dialog GUI
            jpCenterPart.add(cboCombo, BorderLayout.CENTER);
            modelAndDataMethods.mLoadToComboBox(strQuery, cboCombo);
            return jpCenterPart;
        }
        
        //A method that returns a JPanel containing the lower part
        //of this class' GUI    
        private JPanel mCreateDialogBottom(ActionListener listener) {
            JPanel jpLowerPart = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //A JPanel to contain the lower part of the GUI
            jpLowerPart = gui.mPreparePanel(jpLowerPart);
            JButton btn = gui.mCreateButton(90, 25, "Ok", listener);
            jpLowerPart.add(btn);
            return jpLowerPart;
        }
        
        //An action event method that is triggered when the class
        //object is instantiated for purposes of updating a customer account.
        //On the event that this method is triggered, details of a single customer are queryied
        //from the database and displayed to the GUI of the parent class, dialogStakeholders
        private void mFetchForCustomerUpdate(ActionEvent e) {
            
            arrStakeholderUpdateDetails = clsSQLMethods.mFetchRecord("SELECT Cus_id, Cus_name, Cus_address, Cus_contact, "
                    + "Cus_email FROM tblCustomers WHERE Cus_name ='"+cboCombo.getSelectedItem().toString()+"'");
                
            mSetDetailsToGUI();
            this.setVisible(false);
        }
        
        //An action event method that is triggered when the class
        //object is instantiated for purposes of updating a supplier account.
        //On the event that this method is triggered, details of a single supplier are queryied
        //from the database and displayed to the GUI of the parent class, dialogStakeholders
        private void mFetchForSupplierUpdate(ActionEvent e) {
            
            arrStakeholderUpdateDetails = clsSQLMethods.mFetchRecord("SELECT Supp_name, Supp_address, Supp_contact, "
                    + "Supp_email FROM tblSupplier WHERE Supp_name ='"+cboCombo.getSelectedItem().toString()+"'");
            
            arrStakeholderUpdateDetails[0] = String.valueOf(clsSQLMethods.mGetNumericField(
                    "SELECT Supp_id FROM tblSupplier WHERE Supp_name ='"+cboCombo.getSelectedItem().toString()+"'"));
            
            mSetDetailsToGUI();
            this.setVisible(false);
        }
        
        //A method that fetches from the database and identifying number
        //associated with the selected supplier
        private void mFetchSupplierId(ActionEvent e) {
            strId = String.valueOf(clsSQLMethods.mGetNumericField(
                    "SELECT Supp_id FROM tblSupplier WHERE Supp_name ='"+cboCombo.getSelectedItem()+"'"));
            this.setVisible(false);
        }
    }
    
    //A class that displays stakeholder details on a GUI table
    public class clsViewStakeholder extends JDialog {
        public clsViewStakeholder(String strQuery, String strStakeholder) {
            super(null, strStakeholder+" Details", JDialog.ModalityType.APPLICATION_MODAL);
            this.setSize(800, 400);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            switch(strStakeholder) {
                case"Supplier":
                tblStakeholder = modelAndDataMethods.mTable(strQuery, tblStakeholder, dmTableModel);
                break;
                
                case "Customer":
                    tblStakeholder = modelAndDataMethods.mTable(strQuery, tblStakeholder, dmTableModel);
                    break;
            }
                       
            mViewSupplierGUI(); //Calls a method that creates the GUI of this class
        }
        
        // This constructor is called when displaying alphabetically filtered stakeholder details
        public clsViewStakeholder() {
        }
        
        //A table model to store table data that will be displayed
        private DefaultTableModel dmTableModel = new DefaultTableModel();
        
        //A table that will display stakeholder details
        private JTable tblStakeholder = new JTable();
        
        //This methods creates in a JPanel the GUI of this class
        private void mViewSupplierGUI() {
            JPanel jpContainer = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
            jpContainer.setBorder(new EmptyBorder(20, 20, 20, 20)); //Sets a border 
            jpContainer = gui.mPreparePanel(jpContainer); //Sets background colour to the JPanel
            jpContainer.add(new JScrollPane(tblStakeholder));
            this.add(jpContainer);
        }
        
        public void mFilterStakeholderByAlphabetLetter(String strLetter, String strStakeholder) {
            switch(strStakeholder) {
            case"Supplier":
                new clsViewStakeholder("SELECT Supp_name, Supp_address, Supp_contact, Supp_email FROM tblSupplier WHERE Supp_name LIKE '"+strLetter+"%'", strStakeholder).setVisible(true);
                break;
                
            case "Customer":    
                new clsViewStakeholder("SELECT Cus_name, Cus_address, Cus_contact, Cus_email FROM tblCustomers WHERE Cus_name LIKE '"+strLetter+"%'", strStakeholder).setVisible(true);
                break;
            }
        }
    }
    
    //A class that is used to filter by alphabet
    public class clsFilterByAlphabetDialog extends JDialog {

        public clsFilterByAlphabetDialog(String strStakeholder) {
            super(null, "Select Alphabet to Filter by", Dialog.ModalityType.APPLICATION_MODAL);
            this.setSize(500, 300);
            this.setResizable(false);
            this.setLocationRelativeTo(null);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            this.setLayout(new BorderLayout());
            mCreateDialogWindow(strStakeholder);
        }
        
        //A method that creates a tablet-like GUI with alphabets A to Z
        private void mCreateDialogWindow(String strStakeholder) {
            JPanel jpParentPanel = new JPanel(new BorderLayout(0, 20));
            jpParentPanel = gui.mPreparePanel(jpParentPanel);
            jpParentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            jpParentPanel.add(gui.mCreateLabel("Select Alphabet to Filter by:", new Font("Tahoma", Font.BOLD, 14)), BorderLayout.NORTH);
            
            JPanel jpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            jpPanel = gui.mPreparePanel(jpPanel);
            
            for (char c = 'A'; c <= 'Z'; c++) {
                jpPanel.add(mColourButtons(mButton("" + c, strStakeholder)));
            }
            jpParentPanel.add(jpPanel, BorderLayout.CENTER);
            this.add(jpParentPanel);
        }
        
        private JButton mColourButtons(JButton btnButton) {
            btnButton.setBackground(new Color(255, 255, 255));
            return btnButton;
        }
        
        private JButton mButton(String strText, String strStakeholder) {
            JButton btnButton = new JButton(strText);
            btnButton.setPreferredSize(new Dimension(50, 30));
            btnButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {  
                    new clsViewStakeholder().mFilterStakeholderByAlphabetLetter(btnButton.getText(), strStakeholder);
                }
            });
            return btnButton;
        }
    }
    
    private void mUpdate() {
        if(btnUpdate.getText().equals("Update")) {
                
            mSelectStakeholderToUpdate();
            btnUpdate.setText("Save");
                
        } else if(btnUpdate.getText().equals("Save")) {
                
            mSaveUpdatedStakeholderDetails();
            btnUpdate.setText("Update");
            
        }
    }
    
    private void mAddStakeholder() {
        mCreateStakeholder();   
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
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblAddress = new javax.swing.JLabel();
        lblContactNo = new javax.swing.JLabel();
        txtContactNo = new javax.swing.JTextField();
        btnClear = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        spPane = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpDialogPanel.setBackground(new java.awt.Color(255, 255, 255));

        lblDialogHeading.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblDialogHeading.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblDialogHeading.setText("Stakeholder Account");

        lblName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblName.setText("Name");

        lblEmail.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblEmail.setText("Email");

        lblAddress.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblAddress.setText("Address");

        lblContactNo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblContactNo.setText("Contact no.");

        txtContactNo.setToolTipText("");

        btnClear.setBackground(new java.awt.Color(255, 255, 255));
        btnClear.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

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

        txtAddress.setColumns(20);
        txtAddress.setRows(5);
        spPane.setViewportView(txtAddress);

        javax.swing.GroupLayout jpDialogPanelLayout = new javax.swing.GroupLayout(jpDialogPanel);
        jpDialogPanel.setLayout(jpDialogPanelLayout);
        jpDialogPanelLayout.setHorizontalGroup(
            jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDialogPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblEmail))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                            .addComponent(txtName)))
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addComponent(btnCreate)
                        .addGap(50, 50, 50)
                        .addComponent(btnUpdate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                        .addComponent(btnClear))
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblContactNo)
                            .addComponent(lblAddress))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtContactNo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDialogPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblDialogHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(96, 96, 96))
        );
        jpDialogPanelLayout.setVerticalGroup(
            jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDialogPanelLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(lblDialogHeading, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblName)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDialogPanelLayout.createSequentialGroup()
                        .addComponent(lblAddress)
                        .addGap(71, 71, 71))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpDialogPanelLayout.createSequentialGroup()
                        .addComponent(spPane, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)))
                .addGap(3, 3, 3)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblContactNo)
                    .addComponent(txtContactNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addGroup(jpDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnClear)
                    .addComponent(btnCreate)
                    .addComponent(btnUpdate))
                .addGap(73, 73, 73))
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
        mAddStakeholder();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        mUpdate();
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
            java.util.logging.Logger.getLogger(dialogStakeholders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(dialogStakeholders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(dialogStakeholders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(dialogStakeholders.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                dialogStakeholders dialog = new dialogStakeholders("");
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
    private javax.swing.JPanel jpDialogPanel;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblContactNo;
    private javax.swing.JLabel lblDialogHeading;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblName;
    private javax.swing.JScrollPane spPane;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtContactNo;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
}
