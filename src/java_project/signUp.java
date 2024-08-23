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
import org.json.simple.parser.JSONParser;
import java_project.models.User;
import javax.swing.ImageIcon;

public class signUp extends JFrame implements ActionListener {
    JTextField nameField, emailField;
    JPasswordField passwordField;
    JLabel lbl_name, lbl_email, lbl_password, message;
    JButton btn_signup, btn_reset, btn_login;
    JCheckBox show_password;

    signUp() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("Sign Up Page");
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setIconImage(new ImageIcon("C:\\Users\\USER\\Documents\\images\\frame_icon.png").getImage());

        lbl_name = new JLabel("Name:");
        lbl_name.setBounds(200, 100, 100, 40);
        lbl_name.setFont(new Font("Arial", Font.BOLD, 16));

        nameField = new JTextField();
        nameField.setBounds(300, 100, 300, 40);

        lbl_email = new JLabel("Email:");
        lbl_email.setBounds(200, 160, 100, 40);
        lbl_email.setFont(new Font("Arial", Font.BOLD, 16));

        emailField = new JTextField();
        emailField.setBounds(300, 160, 300, 40);

        lbl_password = new JLabel("Password:");
        lbl_password.setBounds(200, 220, 100, 40);
        lbl_password.setFont(new Font("Arial", Font.BOLD, 16));

        passwordField = new JPasswordField();
        passwordField.setBounds(300, 220, 300, 40);

        message = new JLabel(" ");
        message.setBounds(300, 370, 300, 40);

        btn_signup = new JButton("Sign up");
        btn_signup.setBounds(300, 290, 100, 40);
        btn_signup.addActionListener(this);

        btn_reset = new JButton("Reset");
        btn_reset.setBounds(500, 290, 100, 40);
        btn_reset.addActionListener(this);

        show_password = new JCheckBox("Show password");
        show_password.setBounds(300, 260, 150, 40);
        show_password.addActionListener(this);

        btn_login = new JButton("Log in");
        btn_login.setBounds(300, 340, 100, 40);
        btn_login.addActionListener(this);

        this.add(lbl_name);
        this.add(nameField);
        this.add(lbl_email);
        this.add(emailField);
        this.add(lbl_password);
        this.add(passwordField);
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
            String nameText = nameField.getText();
            String emailText = emailField.getText();
            String pwdText = new String(passwordField.getPassword());

            try {
                JSONObject json = new JSONObject();
                json.put("name", nameText);
                json.put("email", emailText);
                json.put("password", pwdText);

                String urlParameters = json.toString();

                String response = Utility.executePost("http://localhost:5000/api/users/signup", urlParameters);

                if (response != null && !response.isEmpty()) {
                    JSONParser parser = new JSONParser();
                    JSONObject responseObject = (JSONObject) parser.parse(response);
                    JSONObject userJson = (JSONObject) responseObject.get("user");

                    if (userJson != null) {
                        String id = String.valueOf(userJson.get("id"));
                        String name = (String) userJson.get("name");
                        String email = (String) userJson.get("email");
                        User user = new User(id, name, email);

                        JOptionPane.showMessageDialog(this, "Sign up successful!");
                        this.dispose(); 

                        new HomePage(user);
                    } else {
                        JOptionPane.showMessageDialog(this, "Sign up failed: " + responseObject.get("error"));
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error: No response from server.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage());
            }
        }

        if (e.getSource() == btn_reset) {
            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");
            message.setText("");
        }

        if (e.getSource() == show_password) {
            if (show_password.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        }

        if (e.getSource() == btn_login) {
            this.dispose(); 
            new log_user();
        }
    }

    public static void main(String[] args) {
        new signUp();
    }
}
