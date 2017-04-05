/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentcos106;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author huyquang
 */
public class AddRemoveColumnTab extends JPanel 
{
    private final JPanel tableNamePanel, tablePanel, addPanel;
    private final JTable table;
    private final JLabel tableNameLabel;
    private final JTextField tableNameField, columnNameField, columnDataTypeField;
    private final JButton buttonAdd, buttonShow, buttonDel;
    private AddRemoveColumnTab.ColumnTableModel model;
    private String tableName="";
        
    //Constructor
    public AddRemoveColumnTab()
    {
        //create Panels
        tableNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePanel = new JPanel();
        addPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        //create Label
        tableNameLabel = new JLabel("  Table name: ");
        
        //create Field
        tableNameField = new JTextField("",10);
        tableNameField.setToolTipText("Enter the name of the table!");
        columnNameField = new JTextField("",8);
        columnNameField.setToolTipText("Name of column");
        columnDataTypeField = new JTextField("",8);
        columnDataTypeField.setToolTipText("Data type");
        
        //create Table using class CreateTabTableModel extends from DefaultTableModel
        table = new JTable(model = new AddRemoveColumnTab.ColumnTableModel());
        
        //create Button and add Action handler
        buttonAdd = new JButton("Add column");
        buttonAdd.addActionListener(new AddRemoveColumnTab.ButtonListener());
        buttonDel = new JButton("Delete Column");
        buttonDel.addActionListener(new AddRemoveColumnTab.ButtonListener());
        buttonShow = new JButton("Show");
        buttonShow.addActionListener(new AddRemoveColumnTab.ButtonListener());

        //add Components to Panels
        tableNamePanel.add(tableNameLabel); tableNamePanel.add(tableNameField); tableNamePanel.add(buttonShow);
        tablePanel.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        addPanel.add(columnNameField);
        addPanel.add(columnDataTypeField);       
        addPanel.add(buttonAdd);
        addPanel.add(buttonDel);
        
        //setLayout for AddRemoveColumnTab
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        //add Panels to AddRemoveColumnTab
        add(tableNamePanel);
        add(tablePanel);
        add(addPanel);
    } //End of Constructor      
        
    //Update table when click buttonShow
    private static void updateTableModel(JTable table, ResultSet rs) throws SQLException 
    {       
        // names of columns
        Vector<String> columnNames = new Vector<String>();
        columnNames.add("Column Name");
        columnNames.add("Data Type");	
	columnNames.add("Del?");
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) //Run from 1st to the last row
        {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= 2; columnIndex++)//Run from 1st to 2nd column
            {
                vector.add(rs.getObject(columnIndex));
            }
            vector.add(Boolean.FALSE); //The 3rd element of each row vector
            data.add(vector);
        }   
        model.setDataVector(data, columnNames);
    }//End of updateTableModel 
    
    /*
    //getData of table and return Object[][3] data value
    static Object[][] getTableData (JTable table) 
    {
        TableModel model = table.getModel();
        int nRow = model.getRowCount();
        Object[][] data = new Object[nRow][3];
        for (int i = 0 ; i < nRow ; i++)
                if (Objects.equals((Boolean)model.getValueAt(i,2), Boolean.TRUE)) {
                    continue; //start a new loop
                }
                else
                    for (int j=0; j < 3; j++)
                    {
                        data[i][j] = model.getValueAt(i,j);
                    }
        return data;
    }
*/
    
    //Create actionlistener for buttonExecute
    private class ButtonListener implements ActionListener
    {  
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            String sql = "";
            ResultSet result = null;
            
            //if buttonShow is clicked
            if ( e.getSource() == buttonShow )
            {
                try {
                    sql = "select COLUMN_NAME,DATA_TYPE\n" +
                          "from INFORMATION_SCHEMA.COLUMNS\n" +
                          "where TABLE_NAME='" + tableNameField.getText() + "'; ";
                    tableName = tableNameField.getText(); //Save current table name for use in buttonDel and buttonAdd
                    result = ConnectionMySQL.stmt.executeQuery(sql);
                    updateTableModel(table, result);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("SHOWING TABLE error: " + ex.getMessage());
                }
                finally 
                {
                    try {
                        //close ResultSet
                        result.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        System.out.println("CLOSING RESULTSET error: " + ex.getMessage());
                    }
                }
            }//if buttonAdd is clicked 
            else if (e.getSource() == buttonAdd) {//if buttonAdd is clicked
                sql = "ALTER TABLE " + tableName +
                      " ADD " + columnNameField.getText() +
                      " " + columnDataTypeField.getText();
                        
                int isUpdateSuccessful = 0; //Used to check if inserting record succeeds
                try {
                    isUpdateSuccessful = 0; //reset value to 0 before executing SQL command
                    ConnectionMySQL.stmt.execute(sql);
                    isUpdateSuccessful = 1;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("ADDING NEW COLUMN error: " + ex.getMessage());                            
                }
                
                if (isUpdateSuccessful == 1) {
                    //Display new record to the table
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.addRow(new Object[]{columnNameField.getText(), columnDataTypeField.getText(), Boolean.FALSE});
                } else
                    JOptionPane.showMessageDialog(null, "Error! Please check input information!");
                    
            } //if buttonDel is clicked
            else { //if buttonDel is clicked
                
                //Create a Vector that cantains columns need being deleted and a Object[][] of data that need remain unchanged
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int nRow = model.getRowCount();
                Object[][] data = new Object[nRow][3];
                String[] sqlDel = new String[nRow];
                int delIndex = 0;
                for (int i = 0 ; i < nRow ; i++)
                {
                    if ( Objects.equals((Boolean)model.getValueAt(i,2), Boolean.TRUE) ) 
                    {
                        sqlDel[delIndex] = (String)model.getValueAt(i,0);
                        delIndex++;
                    }
                    else
                        for (int j=0; j < 3; j++)
                        {
                            data[i][j] = model.getValueAt(i,j);
                        }
                }
                        
                int isUpdateSuccessful = 0; //Used to check if inserting record succeeds
                try {
                    isUpdateSuccessful = 0; //reset before carrying out SQL command
                    for (String sqlDel1 : sqlDel) { //Using enhanced for loop
                        if (sqlDel1 != null) {
                            //just 
                            sql = "ALTER TABLE " + tableName +
                                    " DROP " + sqlDel1 + ";";
                            ConnectionMySQL.stmt.execute(sql);  
                        }
                    }
                    isUpdateSuccessful = 1;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("DROPPING COLUMN error: " + ex.getMessage());                            
                }
                
                if (isUpdateSuccessful == 1) 
                {
                    try {
                        sql = "select COLUMN_NAME,DATA_TYPE\n" +
                              "from INFORMATION_SCHEMA.COLUMNS\n" +
                              "where TABLE_NAME='" + tableName + "'; ";
                        //tableName = tableNameField.getText(); //Save current table name for use in buttonDel and buttonAdd
                        result = ConnectionMySQL.stmt.executeQuery(sql);
                        updateTableModel(table, result);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        System.out.println("REFRESHING TABLE error: " + ex.getMessage());
                    }
                    finally 
                    {
                        try {
                            //close ResultSet
                            result.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            System.out.println("CLOSING RESULTSET error: " + ex.getMessage());
                        }
                    }
                }
            } //end of listener for buttonDel   
        }//end of method perform
    }//end of button Listener
    
    //##
    //Inner class CreateTabTableModel of CreateTableTab
    private class ColumnTableModel extends DefaultTableModel 
    {
        public ColumnTableModel() {
            super(new String[]{"Column Name", "Data type", "Del?"}, 0); //set columns name without any rows
        }
        
        //Override method isCellEditable() to make sure that only column "Del?" is editable
        @Override
        public boolean isCellEditable(int row, int column) {
          return column == 2; //return true if columnIndex is 2
        }
    
        //Override getColumnClass() to force the column "Del?" return Boolean class type
        @Override
        public Class<?> getColumnClass(int columnIndex) 
        {
            Class clazz = String.class;
            
            if ( columnIndex == 2)
                clazz = Boolean.class;
            return clazz;
        }//end of getColumnClass
    }//end of CreateTabTableModel  
}//End of class AddRemoveColumn
