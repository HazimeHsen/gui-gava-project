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

public class logIn extends JFrame implements ActionListener {
    JPasswordField password;
    JTextField email;
    JLabel lbl_password, lbl_email, message;
    JButton btn_log, btn_reset, btn_signup;
    JCheckBox show_password;

    logIn() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("Login Page");
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        lbl_email = new JLabel("Email:");
        lbl_email.setBounds(200, 100, 100, 40);
        lbl_email.setFont(new Font("Arial", Font.BOLD, 16));

        email = new JTextField();
        email.setBounds(300, 100, 300, 40);

        lbl_password = new JLabel("Password:");
        lbl_password.setBounds(200, 200, 100, 40);
        lbl_password.setFont(new Font("Arial", Font.BOLD, 16));

        password = new JPasswordField();
        password.setBounds(300, 200, 300, 40);

        message = new JLabel(" ");
        message.setBounds(300, 370, 300, 40);

        btn_log = new JButton("Sign in");
        btn_log.setBounds(300, 290, 100, 40);
        btn_log.addActionListener(this);

        btn_reset = new JButton("Reset");
        btn_reset.setBounds(500, 290, 100, 40);
        btn_reset.addActionListener(this);

        show_password = new JCheckBox("Show password");
        show_password.setBounds(300, 240, 150, 40);
        show_password.addActionListener(this);

        btn_signup = new JButton("Sign up");
        btn_signup.setBounds(300, 340, 100, 40);
        btn_signup.addActionListener(this);

        this.add(lbl_email);
        this.add(email);
        this.add(lbl_password);
        this.add(password);
        this.add(show_password);
        this.add(btn_log);
        this.add(btn_reset);
        this.add(btn_signup);
        this.add(message);

        this.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_log) {
            String emailText = email.getText();
            String pwdText = new String(password.getPassword());

            try {
                JSONObject json = new JSONObject();
                json.put("email", emailText);
                json.put("password", pwdText);

                String urlParameters = json.toString();

                String response = Utility.excutePost("http://localhost:5000/api/users/login", urlParameters);

                if (response != null && !response.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
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

        if (e.getSource() == btn_signup) {
            this.dispose(); 
            new SignUp(); 
        }
    }

    public static void main(String[] args) {
        new logIn();
    }
}