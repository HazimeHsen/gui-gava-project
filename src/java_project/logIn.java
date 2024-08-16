package java_project;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    JButton btn_log, btn_reset;
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
        message.setBounds(300, 320, 300, 40);

        btn_log = new JButton("Sign in");
        btn_log.setBounds(300, 290, 100, 40);
        btn_log.addActionListener(this);

        btn_reset = new JButton("Reset");
        btn_reset.setBounds(500, 290, 100, 40);
        btn_reset.addActionListener(this);

        show_password = new JCheckBox("Show password");
        show_password.setBounds(300, 240, 150, 40);
        show_password.addActionListener(this);

        this.add(lbl_email);
        this.add(email);
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

        if (e.getSource() == btn_log) {
            String emailText = email.getText();
            String pwdText = new String(password.getPassword());

            try {
                // Create JSON object
                JSONObject json = new JSONObject();
                json.put("email", emailText);
                json.put("password", pwdText);

                // Send POST request
                URL url = new URL("http://localhost:5000/api/users/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);

                // Send JSON as a string
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read response code
                int responseCode = conn.getResponseCode();

                // Read response
                BufferedReader reader;
                StringBuilder response = new StringBuilder();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                }

                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                reader.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JOptionPane.showMessageDialog(this, "You logged in successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + response.toString());
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
    }

    public static void main(String[] args) {
        new logIn();
    }
}
