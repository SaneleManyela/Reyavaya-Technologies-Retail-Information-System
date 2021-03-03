/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Sanele
 * 
 * Defined in this class are methods that help
 * create the GUI of most of the classes of this
 * application. As the saying goes, this class guide
 * others to a treasure (GUI) it cannot possess.
 */
public class clsGUIDesignMethods {
    
    // This method gives the interfaces their etherial white background,
    public JPanel mPreparePanel(JPanel jpPanel) {
        jpPanel.setOpaque(true);
        jpPanel.setBackground(new Color(255, 255, 255));
        return jpPanel;
    }
    
    // A method that creates and return a label with set
    // text, font, and background
    public JLabel mCreateLabel(String strText, Font f) {
        JLabel lblLabel = new JLabel(strText);
        lblLabel.setHorizontalAlignment(JLabel.CENTER);
        lblLabel.setOpaque(true);
        lblLabel.setBackground(new Color(255, 255, 255));
        return lblLabel;
    }
    
    // A method that creates a button with an actionListener
    // for an action event
    public JButton mCreateButton(int intWidth, int intHeight,
            String strText, ActionListener listener) {
            
        JButton btnButton = new JButton(strText);
        btnButton.addActionListener(listener);
        btnButton.setPreferredSize(new Dimension(intWidth, intHeight));
            
        btnButton.setBackground(new Color(255, 255, 255));
        btnButton.setBorder(new BevelBorder(BevelBorder.RAISED));
        btnButton.setText(strText);
            
        return btnButton;
    }
    
    // Adjust the dimensions of the GUI text fields
    public JTextField mTextFieldDimensions(JTextField txt, int intWidth,
            int intHeight, String strToolTip){
            
        txt.setEnabled(true);
        txt.setPreferredSize(new Dimension(intWidth, intHeight));
        txt.setToolTipText(strToolTip);
        return txt;
    }
    
    // Adjust the dimensions of the GUI combo boxes
    public JComboBox mComboBoxDimensions(JComboBox cbo, int intWidth, int intHeight) {
        cbo.setSize(new Dimension(intWidth, intHeight));
        return cbo;
    }
        
    // This method adds components to a JPanel, it creates the GUI
    public JPanel mAddComponent(String str, Component component) {
        JPanel jpComponent = new JPanel(new GridLayout(1, 2, 40, 0));
        jpComponent = new clsGUIDesignMethods().mPreparePanel(jpComponent);
            
        JLabel lblLabel = new JLabel(str);
        lblLabel.setSize(new Dimension(80, 30));
            
        jpComponent.add(lblLabel);
            component.setSize(new Dimension(200, 30));
            jpComponent.add(component);
            
        return jpComponent;
    }
}
