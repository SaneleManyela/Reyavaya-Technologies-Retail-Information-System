/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reyavaya_technologies;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Sanele
 * 
 * This class is used to in filtering products sold
 * and products in stock. The filtering is done by
 * category of products - computer electronics - sold
 */
public class clsFilterProductByCategory extends JDialog{
    public clsFilterProductByCategory(String btnOfOrigin) {
           super(null, "Filter stock by category", JDialog.ModalityType.APPLICATION_MODAL);
           this.setSize(400, 200);
           this.setResizable(false);
           this.setLocationRelativeTo(null);
           this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
           mGUI(btnOfOrigin);
           this.setVisible(true);
        }
        
        // Definition of a string array that has sold electronics categories
        String[] arrCategories = new String[] {
            "CPU", "Fan", "Hard Drive",
            "Keyboards/Mouse", "Memory Card",
            "Monitor Screen", "Motherboard",
            "Power Supply Unit", "Chasis",
        };
        
        private final JComboBox cboCategories = new JComboBox(arrCategories); // has as a model computer electonics categories
        public static String strCategory; // To hold a selected product category
            
        clsGUIDesignMethods gui = new clsGUIDesignMethods();
        clsModelAndDataMethods modelsAndDataMethods = new clsModelAndDataMethods();
        
        // A method that defines the GUI  of this class, clsFilterProductByCatergory
        // The method uses the text of button of origin to compare with defined cases
        // and implement an action event of the dialog button
        private void mGUI(String btnOfOrigin) {
            JPanel jpContainer = new JPanel(new BorderLayout(0, 20));  //Creates a JPanel container object and sets its layout
            jpContainer.setBorder(new EmptyBorder(20, 20, 20, 20)); //Sets a border 
            jpContainer = gui.mPreparePanel(jpContainer);
            
            jpContainer.add(new JLabel("Select Category to filter by"), BorderLayout.NORTH);
            jpContainer.add(cboCategories);  
            
            jpContainer.add(gui.mCreateButton(90, 25, "Ok", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    
                    clsFilterProductByCategory.this.dispose();
                    
                    if(btnOfOrigin.equals("View Stock"))
                    {
                        new clsViewStock().mDrawBarChart(new JDialog(null, Dialog.ModalityType.APPLICATION_MODAL),
                            "Filtered Product Stock", modelsAndDataMethods.mCreateCategoryDataset(
                                "SELECT Prod_Name, Qty FROM tblProducts, tblStock WHERE Prod_name LIKE '%"+
                                cboCategories.getSelectedItem().toString()+"' AND "
                                        + "tblProducts.Prod_id = tblStock.Prod_id",
                            cboCategories.getSelectedItem().toString()),
                        cboCategories.getSelectedItem().toString());
                        
                    } else if(btnOfOrigin.equals("Filter Products")) {
                        
                        strCategory = cboCategories.getSelectedItem().toString();
                    }
                }
            }), BorderLayout.SOUTH);
            this.add(jpContainer);
        }
}