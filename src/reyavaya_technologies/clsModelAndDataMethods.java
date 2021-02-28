/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.data.category.*;
/**
 *
 * @author Sanele
 * 
 * Defined in this class are methods that deals with data 
 * - data models, data sets, displaying data. 
 */
public class clsModelAndDataMethods {
    
    clsDatabaseMethods clsSQLMethods = new clsDatabaseMethods();
    
    // A method to get CategoryDataset for a Bar chart that displays stock information
    public CategoryDataset mCreateCategoryDataset(String strQuery, String strCategory) {
    
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] arrValues;
        
        try(Statement stStatement = clsSQLMethods.mConnectToDatabase().prepareStatement(strQuery)){
            
            ResultSet rs = stStatement.executeQuery(strQuery);
            ResultSetMetaData rsmt = rs.getMetaData();
            int intColumnCount = rsmt.getColumnCount();
            
            while(rs.next()) {
                
                Object[] arrRow = new Object[intColumnCount + 1];
                for(int i = 1; i <= intColumnCount; i++) {
                    arrRow[i] = (rs.getObject(i));
                }
                
                arrValues = new String[arrRow.length];
                for(int i = 1; i < arrRow.length; i++) {
                    if(!(arrRow[i] == null)) {
                        arrValues[i] = arrRow[i].toString();
                    }
                }          
                
                arrValues = mRemoveEmptyIndexes(arrValues).toArray(
                        new String[mRemoveEmptyIndexes(arrValues).size()]);
                
                if(arrValues.length != 0) {
                    dataset.addValue(Integer.parseInt(arrValues[1]), strCategory, arrValues[0]);
                }
            }
            return dataset;
        }catch(SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return dataset;
    }
        
    //A method that removes empty indices in an array
    public java.util.List<String> mRemoveEmptyIndexes(String[] array) {
        
        java.util.List<String> values = new ArrayList<>();
        
        try {
            for (String element : array) {
                if (element != null) {
                    values.add(element);
                }
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return values;
    }
    
    //A method that returns a table model with product data from the database
    private DefaultTableModel mProductsData(String strQuery, DefaultTableModel model) {
        try {
            try (Statement stStatement = clsSQLMethods.mConnectToDatabase().prepareStatement(strQuery)) {
                
                ResultSet rs = stStatement.executeQuery(strQuery);
                ResultSetMetaData rsmt = rs.getMetaData();
                int intColumnCount = rsmt.getColumnCount();
                
                for(int i = 1; i <= intColumnCount; i++) {
                    model.addColumn(rsmt.getColumnName(i));
                }
                
                while(rs.next()) {
                    
                    Object[] arrRow = new Object[intColumnCount + 1];
                    for(int i = 1; i <= intColumnCount; i++) {
                        arrRow[i] = (rs.getObject(i));
                    }               
                    
                    String[] arrRowData = new String[arrRow.length];
                    for(int i = 1; i < arrRow.length; i++) {
                        if(!(arrRow[i] == null)) {
                            arrRowData[i] = arrRow[i].toString();
                        }
                    }
                    
                    model.addRow(mRemoveEmptyIndexes(arrRowData).toArray(
                            new String[mRemoveEmptyIndexes(arrRowData).size()]));
                }
                return model;
            } 
        }catch(SQLException | NullPointerException e) {
                
        } 
        return model;
    }  
    
    //A method that set the model of a table and make it look pretty and all white -
    //serious white is beautiful on a GUI
    public JTable mTable(String strQuery, JTable tbl, DefaultTableModel model) {
        
        model = mProductsData(strQuery, model);
        tbl.setModel(model);
        tbl.setFillsViewportHeight(true);
        tbl.validate();
        
        return tbl;
    }
        
    //A method that loads the combo box with values from the database
    public void mLoadToComboBox(String strQuery, JComboBox cbo) {
        Statement stStatement = null;
        ResultSet rs = null;
        Connection conConnection = clsSQLMethods.mConnectToDatabase();
        
        try {
            stStatement = conConnection.prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            
            while (rs.next()) {
                cbo.addItem(rs.getString(1));
            }
            
            stStatement.close();
            rs.close();
            conConnection.close();
            
        } catch (SQLException | NullPointerException ex) {
            
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try {
                stStatement.close();
                rs.close();
                conConnection.close();
            } catch (SQLException | NullPointerException ex) {
            }
        }
    }
    
    //A method that accepts an argument of type double and return a value
    //of type double which is the same value passed but rounded up to two decimal places
    public double mFormat(double var) {
        DecimalFormat dformat = new DecimalFormat("#.##");
        dformat.setRoundingMode(RoundingMode.UP);
        var = Double.parseDouble(dformat.format(var));
        return var;
    }
}