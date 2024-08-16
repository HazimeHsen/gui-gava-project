package java_project;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class HomePage extends JFrame {

    HomePage() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setTitle("Home Page");
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        JLabel welcomeMessage = new JLabel("Welcome to the Home Page!");
        welcomeMessage.setBounds(300, 250, 200, 40);
        this.add(welcomeMessage);

        this.setVisible(true);
    }

    public static void main(String[] args) {
        new HomePage();
    }
}
