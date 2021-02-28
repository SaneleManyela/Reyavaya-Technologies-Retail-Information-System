/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author Sanele
 */
public class clsDatabaseMethods {
    //A method that returns a connection to the database
    public Connection mConnectToDatabase() {
        String strDBConnectionString = "jdbc:mysql://localhost:3306/revaya_technologies";
        String strUser = "root";
        String strPassword = "password";
        Connection conMySQLConnectionString = null;
        try {
            return conMySQLConnectionString = DriverManager.getConnection(strDBConnectionString, 
                    strUser, strPassword);
        } catch(SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() ,
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    //A method that checks if details exist in the database
    public boolean mCheckIfDetailsExist(String strQuery) {
        boolean boolStatus = false;
        Statement stStatement = null;
        ResultSet rs = null;
        try{
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            boolStatus = rs.next();
            stStatement.close();
            rs.close();
        } catch(SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            try{
                stStatement.close();
                rs.close();
            } catch(SQLException | NullPointerException ex){
            }
        }
        return boolStatus;
    }
    
    //A method that adds a new record to the database
    public boolean mCreateRecord(String strQuery) {
        Statement stStatement = null;
        try{
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            stStatement.execute(strQuery);
            stStatement.close();
            return true;
        } catch(SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            try{
                stStatement.close();
            } catch(SQLException | NullPointerException ex){
            }
        }
        return false;
    }
    
    //A method that returns a numeric field from the database 
    public int mGetNumericField(String strQuery) {
        Statement stStatement = null;
        ResultSet rs = null;
        try{
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            while(rs.next()){
                return rs.getInt(1);
            }
            stStatement.close();
            rs.close();
        } catch(SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }   finally {
             try{
                    stStatement.close();
                    rs.close();
                } catch(SQLException | NullPointerException ex) {
                }
            }
        return 0;
    }
    
    //A method that returns a text field from the database
    public String mGetTextField(String strQuery) {
        Statement stStatement = null;
        ResultSet rs = null;
        try {
            stStatement = mConnectToDatabase().prepareStatement(strQuery);
            rs = stStatement.executeQuery(strQuery);
            while(rs.next()){
                return rs.getString(1);
            }
            stStatement.close();
            rs.close();
        } catch(SQLException | NullPointerException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        } finally {
            try{
                stStatement.close();
                rs.close();
            } catch(SQLException | NullPointerException ex) {
            }
        }
        return null;
    }
          
    //A method that fetches data from the database and 
    //populate an array of type string with values and return the array
    public String[] mFetchRecord(String strQuery) {
        String[] arrRecordDetails = null;
        try {
            try (Statement stStatement = mConnectToDatabase().prepareStatement(strQuery)) {
                stStatement.execute(strQuery);
                try (ResultSet rs = stStatement.getResultSet()) {
                    ResultSetMetaData rsmt = rs.getMetaData();
                    arrRecordDetails = new String[rsmt.getColumnCount()+1];
                    while(rs.next()) {
                        for(int i = 1; i < arrRecordDetails.length; i++){
                            arrRecordDetails[i] = String.valueOf(rs.getString(i));                    
                        }
                    }
                    stStatement.close();
                    rs.close();
                }
                arrRecordDetails = new clsModelAndDataMethods().mRemoveEmptyIndexes(arrRecordDetails).toArray(
                        new String[new clsModelAndDataMethods().mRemoveEmptyIndexes(
                                        arrRecordDetails).size()]);
                return arrRecordDetails;
            }
	} catch(SQLException | NullPointerException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return arrRecordDetails;
    }
    
    //A method that updates record details in the database
    public boolean mUpdateRecord(String strQuery) {
        Statement stStatement = null;
         try {
             stStatement = mConnectToDatabase().prepareStatement(strQuery);
             stStatement.executeUpdate(strQuery);
             stStatement.close();
             return true;
         } catch(SQLException | NullPointerException ex) {
             JOptionPane.showMessageDialog(null, ex.getMessage(), 
                     "Error while updating details", JOptionPane.ERROR_MESSAGE);
         } finally {
             try{
                 stStatement.close();
             }catch(SQLException | NullPointerException ex){
             }
         }
        return false;
    }
}