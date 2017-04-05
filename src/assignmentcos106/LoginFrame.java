package assignmentcos106;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JButton buttonOK, buttonReset;
    private final JTextField unameField;
    private final JPasswordField pwordField;
    private final JLabel head_label,uname_label,pword_label;
    private final JPanel headPanel,unamePanel,fieldPanel,pwordPanel,buttonPanel;
    
    //Constructor
    public LoginFrame()
    {
        //Create frame and settings
        super("Login System!");
        
        //create components and Add Listener
        head_label = new JLabel("Welcome to Login System");
        
        unameField = new JTextField("Please enter username",20);
        unameField.addFocusListener(new FieldFocusListener());
        pwordField = new JPasswordField("Please enter password",20);
        pwordField.setEchoChar((char)0);
        pwordField.addFocusListener(new FieldFocusListener());
        pwordField.addActionListener(new ButtonListener());
        
        uname_label = new JLabel("Username:");
        pword_label = new JLabel("Password:");
        
        buttonOK = new JButton("OK");
        buttonOK.addActionListener(new ButtonListener());
        buttonReset = new JButton("Reset");
        buttonReset.addActionListener(new ButtonListener());
        
        //create Panels
        headPanel = new JPanel();
        headPanel.setLayout(new FlowLayout());
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        unamePanel = new JPanel();
        unamePanel.setLayout(new FlowLayout());
        pwordPanel = new JPanel();
        pwordPanel.setLayout(new FlowLayout());
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        
        //add components to Panels;
        headPanel.add(head_label);
        fieldPanel.add(unamePanel);
        fieldPanel.add(pwordPanel);        
        unamePanel.add(uname_label); unamePanel.add(unameField);
        pwordPanel.add(pword_label); pwordPanel.add(pwordField);
        buttonPanel.add(buttonOK); buttonPanel.add(buttonReset);
        
        //add Panels to frame
        add(headPanel,BorderLayout.NORTH);
        add(fieldPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
        
        //frame's settings
        setResizable(false); //NOT allow to resize the Frame
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(500,300,400,200);

    }//Constructor ends
    
    //static checkLogin to check user/pass
    static boolean checkLogin(String username, String password)
    {
        final String url ="jdbc:mysql://ec2-52-221-237-163.ap-southeast-1.compute.amazonaws.com:3306/COS106Assigment";    
        final String uname="root";
        final String pword="tr*baV4S";
        final String sql = "SELECT password FROM login_table WHERE user_id=";
        ResultSet resultLogin = null;
        boolean return_value = false;// return value
        
        //Establish db connection
        ConnectionMySQL.getConnection(url,uname,pword);
        
        //Starting check user/pass
        try {
            resultLogin = ConnectionMySQL.stmt.executeQuery(sql + "'" + username + "';");
            if (resultLogin.next()) //Check if the uname exists
            {
                if( resultLogin.getString(1).equals(password) ) //Return True if passwords match
                {                                               //Otherwise, return false
                    return_value = true;
                }
            }
            else return_value = false; //Return false if no such username

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Login Frame error: " + ex.getMessage());
        }
        finally 
        {
            try {
                //close ResultSet
                resultLogin.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                System.out.println("Login Frame error: " + ex.getMessage());
            }
        }

        return return_value;
    }//end of static checkLogin
    
    //####
    //Inner class that handles the Events of Buttons
    private class ButtonListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if ( e.getSource() == buttonOK ) //if buttonOK is clicked
            {
                //Check if entered password is correct
                if ( LoginFrame.checkLogin(unameField.getText(),new String(pwordField.getPassword())) ) 
                {
                    JOptionPane.showMessageDialog(null,"Welcome to the System!");
                    LoginFrame.this.dispose(); //Close Login window
                    new MainFrame(); //Start Application
                }
                else {//If password is incorrect
                    JOptionPane.showMessageDialog(null,"Incorrect username/password!");
                    pwordField.setText("");
                }
            }// if buttonReset is clicked
            else if ( e.getSource() == buttonReset )
                {
                    pwordField.setEchoChar((char)0);
                    pwordField.setText("Please enter password");
                    unameField.setText("Please enter username");
                }
            else { //If press Enter in pwordField
                //Check if entered password is correct
                if ( LoginFrame.checkLogin(unameField.getText(),new String(pwordField.getPassword())) ) 
                {
                    JOptionPane.showMessageDialog(null,"Welcome to the System!");
                    LoginFrame.this.dispose(); //Close Login window
                    new MainFrame(); //Start editing tools
                }
                else {//If password is incorrect
                    JOptionPane.showMessageDialog(null,"Incorrect username/password!");
                    pwordField.setText("");
                }
            }
            
        }
        
    }//End of ButtonListener
    
    //####
    //Inner class that handles the Focus Events of TextFields
    private class FieldFocusListener implements FocusListener
    {
        @Override
        public void focusGained(FocusEvent e) {
            if ( e.getSource() == unameField)
            {
                if ( "Please enter username".equals(unameField.getText()) )
                {
                    unameField.setText("");
                }
            }
            else if ( "Please enter password".equals(new String(pwordField.getPassword())) )
            {
                pwordField.setEchoChar('*');
                pwordField.setText("");
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if ( e.getSource() == unameField)
            {
                if ( "".equals(unameField.getText()) )
                {
                    unameField.setText("Please enter username");
                }
            }
            else if ( "".equals(new String(pwordField.getPassword())) )
            {
                pwordField.setEchoChar((char)0);
                pwordField.setText("Please enter password");
            }
        }
    }//End of FieldFocusListener
}//END of LoginFrame
