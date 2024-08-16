/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package java_project;

/**
 *
 * @author USER
 */
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class logIn extends JFrame implements ActionListener {
    JPasswordField password;
    JTextField username;
    JLabel lbl_password, lbl_username, message,title;
    JButton btn_log, btn_reset;
    JCheckBox show_password;
    
    logIn(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800,600);
        this.setTitle("Login Page");
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        
        lbl_password = new JLabel("Password:");
        lbl_password.setBounds(200,200,100,40);
        lbl_password.setFont(new Font("Arial",Font.BOLD,16));
        
        password = new JPasswordField();
        password.setBounds(300,200,300,40);
        
        message = new JLabel(" ");
        message.setBounds(300,320,300,40);
        
        lbl_username = new JLabel("Username:");
        lbl_username.setBounds(200,150,100,40);
        lbl_username.setFont(new Font("Arial",Font.BOLD,16));
        
        username = new JTextField();
        username.setBounds(300,150,300,40);
        
        btn_log = new JButton("Sign in");
        btn_log.setBounds(300,290,100,40);
        btn_log.addActionListener(this);
        
        
        btn_reset = new JButton("Reset ");
        btn_reset.setBounds(500,290,100,40);
        btn_reset.addActionListener(this);
        
        show_password = new JCheckBox("show password");
        show_password.setBounds(300,240,100,40);
        show_password.addActionListener(this);
        
        this.add(lbl_username);
        this.add(username);
        this.add(lbl_password);
        this.add(password);
        this.add(show_password);
        this.add(btn_log);
        this.add(btn_reset);
        this.add(message);
        
        
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getSource() == btn_log){
            String userText = username.getText();
            String pwdText = new String(password.getPassword());
            
            if(userText.equalsIgnoreCase("Kely")&& pwdText.equalsIgnoreCase("pass")){
//                message.setText("You succesfully logged in!");
                   JOptionPane.showMessageDialog(this, "you logged in successfully!!");
            }
            else {
//                message.setText("Invalid username and pasword");
                JOptionPane.showMessageDialog(this, "Invalid username and pasword");

            }
        }
        
        
          if(e.getSource() == btn_reset){
              username.setText("");
              password.setText("");
              message.setText("");
           
        }
        
            
        
          if(e.getSource() == show_password){
              if(show_password.isSelected()){
                  password.setEchoChar((char) 0);
              }
              else {
                  password.setEchoChar('*');
              }
        }
          
  
//        String msg = "Your username is : " + username.getText() + " and your password is: " + new String(password.getPassword());
//        message.setText(msg);
        
    }
    
}
