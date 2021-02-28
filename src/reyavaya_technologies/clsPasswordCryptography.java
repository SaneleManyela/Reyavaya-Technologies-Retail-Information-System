/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.io.UnsupportedEncodingException;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.swing.JOptionPane;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Sanele
 * 
 * A class with functions that encrypt and decrypt text
 */
public class clsPasswordCryptography {
    //Declaration of a static and final string variable
    //to be used as a key for encrypting and decrypting a password
    private static final String KEY = "p@($){$}[w]0|R|D";
    
    private static final String INITVECTOR = "~A#E*S[J)A(V]A|>";
    
    //A method that accepts as an argument a password and
    //encrypt it then return it for further use in the system
    public static String mEncryptPassword(String password)
    {
        try{
            IvParameterSpec iv = new IvParameterSpec(INITVECTOR.getBytes("UTF-8"));
            SecretKey skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            
            byte[] encrypted = cipher.doFinal(password.getBytes());
            return Base64.encodeBase64String(encrypted);
            
        }catch(UnsupportedEncodingException | InvalidAlgorithmParameterException |
                InvalidKeyException | NoSuchAlgorithmException | BadPaddingException 
                | IllegalBlockSizeException | NoSuchPaddingException eX){
            
            JOptionPane.showMessageDialog(null, eX.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    //A method that accepts as an argument an encrpted password and
    //decrypt it with the key declared above then return it for further use in the system
    public static String mDecryptPassword(String encryptedPassword)
    {
        try{
            IvParameterSpec iv = new IvParameterSpec(INITVECTOR.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] userGeneratedPassword = cipher.doFinal(Base64.decodeBase64(encryptedPassword));
            
            return new String(userGeneratedPassword);
            
        }catch(UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException |
                InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException |
                NoSuchPaddingException eX){
            
            JOptionPane.showMessageDialog(null, eX.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}