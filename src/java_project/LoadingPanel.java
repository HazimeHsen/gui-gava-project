package java_project;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadingPanel extends JPanel {
    public LoadingPanel() {
        setLayout(new BorderLayout());
        JLabel loadingLabel = new JLabel("Loading...", JLabel.CENTER);
        add(loadingLabel, BorderLayout.CENTER);
        setPreferredSize(new Dimension(200, 200)); // Adjust size as needed
    }
}
