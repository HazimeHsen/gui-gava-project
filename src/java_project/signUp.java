package java_project;

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
import org.json.simple.JSONObject;

public class SignUp extends JFrame implements ActionListener {
    JPasswordField password;
    JTextField name, email;
    JLabel lbl_name, lbl_email, lbl_password, message;
    JButton btn_signup, btn_reset, btn_login;
    JCheckBox show_password;

    SignUp() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("Signup Page");
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        lbl_name = new JLabel("Name:");
        lbl_name.setBounds(200, 50, 100, 40);
        lbl_name.setFont(new Font("Arial", Font.BOLD, 16));

        name = new JTextField();
        name.setBounds(300, 50, 300, 40);

        lbl_email = new JLabel("Email:");
        lbl_email.setBounds(200, 150, 100, 40);
        lbl_email.setFont(new Font("Arial", Font.BOLD, 16));

        email = new JTextField();
        email.setBounds(300, 150, 300, 40);

        lbl_password = new JLabel("Password:");
        lbl_password.setBounds(200, 250, 100, 40);
        lbl_password.setFont(new Font("Arial", Font.BOLD, 16));

        password = new JPasswordField();
        password.setBounds(300, 250, 300, 40);

        message = new JLabel(" ");
        message.setBounds(300, 370, 300, 40);

        btn_signup = new JButton("Sign up");
        btn_signup.setBounds(300, 320, 100, 40);
        btn_signup.addActionListener(this);

        btn_reset = new JButton("Reset");
        btn_reset.setBounds(500, 320, 100, 40);
        btn_reset.addActionListener(this);

        btn_login = new JButton("Log in");
        btn_login.setBounds(300, 370, 100, 40);
        btn_login.addActionListener(this);

        show_password = new JCheckBox("Show password");
        show_password.setBounds(300, 290, 150, 40);
        show_password.addActionListener(this);

        this.add(lbl_name);
        this.add(name);
        this.add(lbl_email);
        this.add(email);
        this.add(lbl_password);
        this.add(password);
        this.add(show_password);
        this.add(btn_signup);
        this.add(btn_reset);
        this.add(btn_login);
        this.add(message);

        this.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_signup) {
            String nameText = name.getText();
            String emailText = email.getText();
            String pwdText = new String(password.getPassword());

            try {
                JSONObject json = new JSONObject();
                json.put("name", nameText);
                json.put("email", emailText);
                json.put("password", pwdText);

                String urlParameters = json.toString();

                String response = Utility.excutePost("http://localhost:5000/api/users/signup", urlParameters);

                if (response != null && !response.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    this.dispose(); 
                    new HomePage(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Error: No response from server.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage());
            }
        }

        if (e.getSource() == btn_reset) {
            name.setText("");
            email.setText("");
            password.setText("");
            message.setText("");
        }

        if (e.getSource() == show_password) {
            if (show_password.isSelected()) {
                password.setEchoChar((char) 0);
            } else {
                password.setEchoChar('*');
            }
        }

        if (e.getSource() == btn_login) {
            this.dispose(); 
            new logIn(); 
        }
    }

    public static void main(String[] args) {
        new SignUp();
    }
}
