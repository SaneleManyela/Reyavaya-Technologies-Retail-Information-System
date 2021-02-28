/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.util.regex.*;

/**
 *
 * @author Sanele
 * 
 * This class contains validation methods to validate
 * user input and ensure certain user policies - the story
 * can have only one manager
 */
public class clsValidationMethods {
    //A method that validates if the passed email is in
    //the correct format 
    public String mValidateEmail(String strEmail) {
        Pattern patternEmail = Pattern.compile("^(.+)@(.+)$");
        Matcher matchEmail = patternEmail.matcher(strEmail);
        if(!matchEmail.matches()) {
            return "The provided email address is invalid";
        }
        return "";
    }
    
    // As the name implies
    public String mCheckIfFieldIsOnlyDigits(String strContactNumber) {
        for (char c : strContactNumber.toCharArray()) {
            if (c != '.' && !Character.isDigit(c)) {
                return "This number contains non-digits";
            }
        }
        return "";
    }
    
    //A method that validates if the passed contact number is in
    //the valid format 
    public String mValidateContactNumber(String strContactNumber) {
        Pattern pattern = Pattern.compile("(0)?[0-9]{9}");
        Matcher matcher = pattern.matcher(strContactNumber);
               
        if(!matcher.matches()) {
            return "The provided contact number is not a valid number"; 
        }
        return "";
    }
    
    // Ensures the store has only one manager - the apllication demonstrates small business environment
    public String mEnsureStoreHasOnlyOneManager() {
        String strQuery = "SELECT Employee_id FROM tblEmployees WHERE Employee_position = 'Manager' AND Discharged = 0";
        if(new clsDatabaseMethods().mCheckIfDetailsExist(strQuery)) {
            return "An active manager account already exist";
        }
        return "";
    }
}