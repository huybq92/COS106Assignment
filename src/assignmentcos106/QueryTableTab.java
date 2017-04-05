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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class QueryTableTab extends JPanel 
{
    private final JPanel selectPanel, buttonPanel;
    private JTable table;
    private final JLabel selectLabel, fromLabel, whereLabel, likeLabel;
    private final JTextField selectField, fromField, whereField, likeField;
    private final JButton buttonExecute;
    
    //Constructor
    public QueryTableTab()
    {
        //create Labels
        selectLabel = new JLabel("SELECT ");
        fromLabel = new JLabel("  FROM ");
        whereLabel = new JLabel("  WHERE ");
        likeLabel = new JLabel("  LIKE ");
        
        //create TextFields
        selectField = new JTextField("",20);
        selectField.setToolTipText("selectField");
        fromField = new JTextField("",20);
        fromField.setToolTipText("fromField");
        whereField = new JTextField("",20);
        whereField.setToolTipText("whereField");
        likeField = new JTextField("",20);
        likeField.setToolTipText("likeField");
        
        //create Panels
        selectPanel = new JPanel();
        selectPanel.setLayout(new FlowLayout());
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        //create Button
        buttonExecute = new JButton("Execute");
        buttonExecute.addActionListener(new ButtonListener());

        //add Components to Panels
        selectPanel.add(selectLabel);
        selectPanel.add(selectField);
        selectPanel.add(fromLabel);
        selectPanel.add(fromField);
        selectPanel.add(whereLabel);
        selectPanel.add(whereField);
        selectPanel.add(likeLabel);
        selectPanel.add(likeField);
        buttonPanel.add(buttonExecute);
        
        //setLayout for QueryTableTab
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        //add Panels to QueryTableTab
        add(selectPanel);
        add(buttonPanel);
    }//end of Constructor

    //buildTableModel
    private static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException 
    {
        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++)
        {
            columnNames.add(metaData.getColumnName(column));
        }
        
        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) //Run from 1st to the last row
        {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++)
            {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }//buildTableModel  
    
    //Create actionlistener for buttonExecute
    private class ButtonListener implements ActionListener
    {  
        String sql = "";
        ResultSet result = null;
        
        @Override
        public void actionPerformed(ActionEvent e) 
        {
            String sql = "";
            ResultSet result = null;
            
            if ( "".equals(whereField.getText()) ) //Check if Where Field is empty
            {
                sql = "SELECT " + selectField.getText() + 
                      " FROM " + fromField.getText() +
                      ";";
            }
            else if ( "".equals(likeField.getText()) ) //Check if Like Field is empty
                {
                    sql = "SELECT " + selectField.getText() + 
                          " FROM " + fromField.getText() +
                          " WHERE " + whereField.getText() +
                          ";";
                } else {
                    sql = "SELECT " + selectField.getText() + 
                          " FROM " + fromField.getText() +
                          " WHERE " + whereField.getText() +
                          " LIKE '" + likeField.getText() +
                          "';";
                }
                
            int isSuccessful = 0;
            try {
                isSuccessful = 0; //Reset checking variable before executing SQL command
                result = ConnectionMySQL.stmt.executeQuery(sql);
                table = new JTable(QueryTableTab.buildTableModel(result));
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //table must not auto-resize the columns
                JFrame extraFrame = new JFrame();
                extraFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                extraFrame.setBounds(400,300,400,300);
                extraFrame.setVisible(true);
                extraFrame.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
                isSuccessful = 1;
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("QUERYING TABLE error:" + ex.getMessage());
            }
               
            if (isSuccessful == 0)
            JOptionPane.showMessageDialog(null, "ERROR! Please check input information!");
        }//end of method perform
    }//end of button Listener
}//End of Class
