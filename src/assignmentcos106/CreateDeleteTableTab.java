/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignmentcos106;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Objects;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CreateDeleteTableTab extends JPanel {
    private final JPanel tableNamePanel, tablePanel, buttonPanel;
    private final JTable table;
    private final JLabel tableNameLabel;
    private final JTextField tableNameField;
    private final JButton buttonCreate, buttonAddRow, buttonDel;
    private TableModel model;
        
    //Constructor
    public CreateDeleteTableTab()
    {
        //create Panels
        tableNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tablePanel = new JPanel();
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        //create Label
        tableNameLabel = new JLabel("  Table name: ");
        
        //create Field
        tableNameField = new JTextField(10);
        tableNameField.setToolTipText("Enter the name of the table!");
        
        //create Table using class CreateTabTableModel extends from DefaultTableModel
        table = new JTable(model = new TableModel());
        
        //create Button and add Action handler
        buttonCreate = new JButton("Create");
        buttonCreate.addActionListener(new CreateDeleteTableTab.ButtonListener());
        buttonAddRow = new JButton("Add row");
        buttonAddRow.addActionListener(new CreateDeleteTableTab.ButtonListener());
        buttonDel = new JButton("  Delete table");
        buttonDel.addActionListener(new CreateDeleteTableTab.ButtonListener());
        buttonDel.setToolTipText("CAUTION: This action can be undone!!");

        //add Components to Panels
        tableNamePanel.add(tableNameLabel); 
        tableNamePanel.add(tableNameField);
        tableNamePanel.add(buttonDel);
        tablePanel.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        buttonPanel.add(buttonAddRow);
        buttonPanel.add(buttonCreate);
        
        //setLayout for CreateTableTab
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        //add Panels to CreateTableTab
        add(tableNamePanel);
        add(tablePanel);
        add(buttonPanel);
    }
    
    //booleanToPrimaryKey
    static String booleanToPrimaryKey(Boolean value)
    {
        if (Objects.equals(value, Boolean.TRUE))
            return "PRIMARY KEY";
        else
            return "";
    }
    
     //booleanToNotNull
    static String booleanToNotNull(Boolean value)
    {
        if (Objects.equals(value, Boolean.TRUE))
            return "NOT NULL";
        else
            return "";
    }
    
     //booleanToAutoIncrement
    static String booleanToAutoIncrement(Boolean value)
    {
        if (Objects.equals(value, Boolean.TRUE))
            return "AUTO_INCREMENT";
        else
            return "";
    }
    
    //Create actionlistener for buttonExecute
    private class ButtonListener implements ActionListener
    {  
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            //if click buttonCreate
            if ( e.getSource() == buttonCreate )
            {
                //prepare SQL command to execute
                String sqlBegin = "CREATE TABLE " + tableNameField.getText()+ " (";
                String sqlEnd = "); ";
                String sqlMiddle[] = new String[model.getRowCount()];
                String sqlTotalMiddle = "";
                String sql;
                for (int i=0; i < model.getRowCount(); i++)
                {
                    sqlMiddle[i] = ""; //Initialize value of element i of sqlMiddle array to be ""
                    for (int j=0; j < 5; j++)
                    {
                        switch (j)
                        {
                            case 2:
                                sqlMiddle[i] += booleanToPrimaryKey( (Boolean)model.getValueAt(i,2) ) + " ";
                                break;
                            case 3:
                                sqlMiddle[i] += booleanToNotNull( (Boolean)model.getValueAt(i,3) ) + " ";
                                break;
                            case 4:
                                sqlMiddle[i] += booleanToAutoIncrement( (Boolean)model.getValueAt(i,4) ) + " ";
                                break;
                            default:
                                sqlMiddle[i] += model.getValueAt(i,j).toString() + " ";
                                break;
                        }//end of switch (j) loop
                    } //end of for (j) loop
                    
                    //Concatenate elements of sqlMiddle[]
                    if ( i == (model.getRowCount()-1) )
                        sqlTotalMiddle += sqlMiddle[i];
                    else
                        sqlTotalMiddle += sqlMiddle[i] + ",";
                } //end of for (i) loop
                
                sql = sqlBegin + sqlTotalMiddle + sqlEnd;
                
                int isTableCreated = 0; //used to check if sql command is carried out successfully
                try {
                    isTableCreated = 0; //Reset checking veriable before executing SQL command
                    ConnectionMySQL.stmt.execute(sql);                  
                    isTableCreated = 1; //if above statements succeeded without exception, assign isTableCreated=1
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("CREATING TABLE error: " + ex.getMessage());
                }
                if (isTableCreated == 1) {
                    JOptionPane.showMessageDialog(null, "Congratulations! Table created!");
                    tableNameField.setText("");
                } else
                    JOptionPane.showMessageDialog(null, "Creating table failed. Try again!");             
            }
            else if ( e.getSource() == buttonAddRow ){ //if click buttonAddRow
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{"", "", Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});
            } else { //If buttonDel is clicked
                String sql = "DROP TABLE " + tableNameField.getText() + ";";
                int isTableDropped = 0; //used to check if sql command is carried out successfully
                try {
                    isTableDropped = 0; //Reset checking veriable before executing SQL command
                    ConnectionMySQL.stmt.execute(sql);                  
                    isTableDropped = 1; //if above statements succeeded without exception, assign isTableCreated=1      
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    System.out.println("DELETING TABLE error: " + ex.getMessage());
                }
                if (isTableDropped == 1) {
                    JOptionPane.showMessageDialog(null, "Congratulations! Table is deleted!");
                    tableNameField.setText("");
                } else
                    JOptionPane.showMessageDialog(null, "Deleting table failed. Try again!");
            }
        }//end of method perform
    }//end of button Listener
    
    //Inner class CreateTabTableModel of CreateTableTab
    class TableModel extends DefaultTableModel 
    {
        public TableModel() {
            super(new String[]{"Column Name", "Data Type", "Primary key?", "Not null?", "Auto_increment?"}, 0); //set columns name and 10 initial null cells
        }

        //Override getColumnClass() to force the column 2,3,4 return Boolean class type
        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            Class clazz = String.class;
            switch (columnIndex) {
            case 2:
                clazz = Boolean.class; //force column "Primary key?" to return Class object of Boolean type
                break;
            case 3:
                clazz = Boolean.class; //force column "Not null?" to return Class object of Boolean type
                break;
            case 4:
                clazz = Boolean.class; //force column "Auto_increment?" to return Class object of Boolean type
                break;
          }
          return clazz;
        }//end of getColumnClass
    }//end of CreateTabTableModel
} //End of CreateTableTab