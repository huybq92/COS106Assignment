/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentcos106;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author huyquang
 */
public class InsertRemoveDataTab extends JPanel
{
    private final JPanel tableNamePanel, tablePanel, addPanel;
    private final JTable table;
    private final JLabel tableNameLabel;
    private final JTextField tableNameField;
    private final JButton buttonAdd, buttonShow, buttonUpdate, buttonDel;

    private String tableName=""; //name of current displayed table
    private final ListSelectionModel listSelectionModel;

    private Vector<Vector<Object>> dataTable; //data of current displayed table
    private Vector<Integer> selectedIndexVector; //vector contains selected rows
    
    private Vector<String> columnNames; //vector contains column names of current displayed table
    
    private String[] sqlDelList; //List of DELETE commands
    private String[] sqlUpdateList; //List of INSERT commands
    
    private int rowCount=0; //Number of records in Database
    private int totalRow=0; //Number of rows of displayed Table
    
    public InsertRemoveDataTab()
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
        
        //create Table using class CreateTabTableModel extends from DefaultTableModel
        table = new JTable();
        listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionHandler());
        
        //Allow user to select multiple rows
        listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	table.setSelectionModel(listSelectionModel);
        
        //create Button and add Action handler
        buttonAdd = new JButton("New Record");
        buttonAdd.addActionListener(new InsertRemoveDataTab.ButtonListener());
        buttonUpdate = new JButton("Update");
        buttonUpdate.addActionListener(new InsertRemoveDataTab.ButtonListener());
        buttonShow = new JButton("Show");
        buttonShow.addActionListener(new InsertRemoveDataTab.ButtonListener());
        buttonDel = new JButton("Delete Records");
        buttonDel.addActionListener(new InsertRemoveDataTab.ButtonListener());

        //add Components to Panels
        tableNamePanel.add(tableNameLabel); tableNamePanel.add(tableNameField); tableNamePanel.add(buttonShow);
        tablePanel.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));       
        addPanel.add(buttonAdd);
        addPanel.add(buttonUpdate);
        addPanel.add(buttonDel);
        
        //setLayout for AddRemoveColumnTab
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        //add Panels to AddRemoveColumnTab
        add(tableNamePanel);
        add(tablePanel);
        add(addPanel);
    }
    
    //Get data of table from Database and displayed the table
    private void updateTableModel(JTable table, ResultSet rs) throws SQLException 
    {
        ResultSetMetaData metaData = rs.getMetaData();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        // names of columns
        columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++)
        {
            columnNames.add(metaData.getColumnName(column));
        }
        
        // data of the table
        dataTable = new Vector<Vector<Object>>();
        rowCount=0; //reset number of row
        while (rs.next()) //Run from 1st to the last row
        {     
            rowCount++;
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++)
            {
                vector.add(rs.getObject(columnIndex));
            }
            dataTable.add(vector);
        }   
        totalRow=rowCount;
        model.setDataVector(dataTable, columnNames);
    }//End of updateTableModel 
    
    //create a single DELETE command
    private String createDelSQL(Vector v)
    {
        String sql = "DELETE FROM " + tableName + " WHERE ";
        
        for (int i=0; i < columnNames.size(); i++)
        {
            if ( i == (columnNames.size()-1) )
                sql += columnNames.get(i) + "='" + v.get(i) + "';";
            else
                sql += columnNames.get(i) + "='" + v.get(i) + "' AND ";
        }      
        return sql;
    }
    
    //create a single INSERT command
    private String createUpdateSQL(Vector v)
    {
        String sql = "INSERT INTO " + tableName + " VALUES (";
        
        for (int i=0; i < columnNames.size(); i++)
        {
            if ( i == (columnNames.size()-1) )
                sql += "'" + v.get(i) + "');";
            else
                sql += "'" + v.get(i) + "',";
        }
        return sql;
    }
    
    //Create actionlistener for buttonExecute
    private class ButtonListener implements ActionListener
    {         
        String sql ="";
        
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            ResultSet result = null;
            
            if ( e.getSource() == buttonShow ) //if buttonShow is clicked
            {
                try {
                    sql = "SELECT * FROM " + tableNameField.getText() + "; "; //select all data of table in DB
                    tableName = tableNameField.getText(); //Save current table name for use in buttonDel and buttonAdd
                    result = ConnectionMySQL.stmt.executeQuery(sql);
                    updateTableModel(table, result); //Refresh displayed table
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("SHOWING TABLE error: " + ex.getMessage());
                }
                finally
                {
                    try {
                        //close ResultSet
                        result.close(); //close ResultSet
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        System.out.println("CLOSING RESULTSET error: " + ex.getMessage());
                    }
                }
            }
            else if (e.getSource() == buttonDel)
            { //if buttonDel is clicked
                int isSuccessful=0; //check if SQL commands executed successfully
                try {
                    isSuccessful=0;
                    for (String sqlDelList1 : sqlDelList) { //Using enhanced for loop to access every elements of sqlDelList[]
                        ConnectionMySQL.stmt.execute(sqlDelList1); 
                    }
                    isSuccessful=1; // SQL commands executed successfully
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("DELETING RECORDS error: " + ex.getMessage());
                }
                finally 
                {
                    try {
                        if ( isSuccessful==1 ) {
                            sql = "SELECT * FROM " + tableName + "; ";
                            result = ConnectionMySQL.stmt.executeQuery(sql);
                            updateTableModel(table, result); //refresh displayed table
                        }
                        
                        //close ResultSet
                        result.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        System.out.println("CLOSING RESULTSET error: " + ex.getMessage());
                    }
                }
            } else if (e.getSource() == buttonAdd) //if buttonAdd is clicked
            {
                Vector<Object> vector = new Vector<Object>(); //create a new vector to contain initial values of new row
                for (int i=0; i < columnNames.size(); i++)
                {
                    vector.add(""); //initialize new values of column
                }
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(vector); //insert new blank row to displayed table
                totalRow++; //increase the total number of rows displayed in the table
            } else if (e.getSource() == buttonUpdate)
            {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                sqlUpdateList = new String[totalRow-rowCount];
                int isSuccessful=0; //check if SQL commands succeed
                for (int i = rowCount; i < totalRow; i++)
                {
                    Vector<Object> vector = new Vector<Object>(); //vector contains data of new row(s)
                    for (int j=0; j < columnNames.size(); j++)
                    {
                        vector.add(model.getValueAt(i,j)); //add value of each column to vector
                    }
                    sqlUpdateList[i-rowCount] = createUpdateSQL(vector); //assign new INSERT command to the list
                }

                try {
                    isSuccessful=0; //reset check success
                    for (String sqlUpdateList1 : sqlUpdateList) { //Using enhanced for loop to access every elements of sqlUpdateList[]
                        ConnectionMySQL.stmt.execute(sqlUpdateList1); 
                    }
                    isSuccessful=1; //SQL commands succeed
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("INSERTING NEW RECORD(S) error: " + ex.getMessage());
                }
                finally 
                {
                    try {
                        if ( isSuccessful==1 ) {
                            sql = "SELECT * FROM " + tableName + "; ";
                            result = ConnectionMySQL.stmt.executeQuery(sql);
                            updateTableModel(table, result); //refresh displayed table
                            JOptionPane.showMessageDialog(null,"Insert " + sqlUpdateList.length + " new record(s) successfully!");
                        }
                        
                        //close ResultSet
                        result.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        System.out.println("CLOSING RESULTSET error: " + ex.getMessage());
                    }
                }
            }
        } //End of actionPerformed
    } //End of class Button Listener

    //ListSelectionHandler
    class ListSelectionHandler implements ListSelectionListener 
    {
        @Override
        public void valueChanged(ListSelectionEvent e) 
        { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            boolean isAdjusting = e.getValueIsAdjusting(); 

            selectedIndexVector = new Vector<Integer>();
    
            // Find out which indexes are selected
            int minIndex = lsm.getMinSelectionIndex();
            int maxIndex = lsm.getMaxSelectionIndex();
            for (int i = minIndex; i <= maxIndex; i++) 
            {
                if (lsm.isSelectedIndex(i)) {
                    selectedIndexVector.add(i); //Create a Vector containing selectedIndex of table
                }                
            }
            
            //Create a Vector<Vector<Object>> to contain data of selected rows
            int  index;
            sqlDelList = new String[selectedIndexVector.size()];
            
            for (int i=0; i < selectedIndexVector.size(); i++)
            {
                index = selectedIndexVector.get(i); //set index to be the selectedIndex value
                sqlDelList[i] = createDelSQL(dataTable.get(index)); //add new DELETE command to the list
            }            
        } //End of valueChanged()
    } //End of ListSelectionListener 
}// END OF class InsertRemoveDataTab