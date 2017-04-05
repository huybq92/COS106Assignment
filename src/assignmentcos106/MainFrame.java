/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentcos106;
import javax.swing.*;

public class MainFrame extends JFrame
{
    private final JTabbedPane tab;
    
    //Constructor
    public MainFrame()
    {
        super("Database Editing Tools");
        
        //frame's settings
        setResizable(false); //NOT allow to resize the Frame
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(300,200,600,400);
        
        //create TabbedPane
        tab = new JTabbedPane(JTabbedPane.TOP);
        
        //add Tabs to TabbedPane
        tab.addTab("Insert/Remove Data", new InsertRemoveDataTab());
        tab.addTab("Query Table", new QueryTableTab());
        tab.addTab("Create/Delete Table", new CreateDeleteTableTab());
        tab.addTab("Add/Remove Column", new AddRemoveColumnTab());
 
        getContentPane().add(tab);       
    } //End of constructor MainFrame  
}//End of MainFrame class
