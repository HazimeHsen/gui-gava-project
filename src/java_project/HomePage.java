package java_project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java_project.models.ClassMember;
import java_project.models.ClassRoom;
import java_project.models.User;
import raven.crazypanel.CrazyPanel;
import java.awt.geom.RoundRectangle2D;

public class HomePage extends CrazyPanel {
    private User user;

    public HomePage(User user) {
        this.user = user;

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        JButton createClassButton = new components.Button();

        createClassButton.setBackground(new java.awt.Color(157, 153, 255));
        createClassButton.setForeground(new java.awt.Color(255, 255, 255));
        createClassButton.setText("Create Class");
        createClassButton.setPreferredSize(new Dimension(150, 30));
        // createClassButton.setEffectColor(new java.awt.Color(199, 196, 255));
        createClassButton.addActionListener(e -> openCreateClassDialog());
        buttonPanel.add(createClassButton);
        add(buttonPanel, BorderLayout.NORTH);

        JPanel classesPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(classesPanel);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);

        loadUserClasses(classesPanel);
    }

    private void openCreateClassDialog() {
        JTextField nameField = new JTextField(10);
        JTextField descriptionField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Class Name:"));
        panel.add(nameField);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Create a New Class", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String className = nameField.getText();
            String description = descriptionField.getText();

            if (!className.isEmpty() && !description.isEmpty()) {
                createClass(className, description);
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void createClass(String className, String description) {
        String url = "http://localhost:5000/api/classrooms";

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("name", className);
        jsonParam.put("description", description);
        jsonParam.put("creatorId", user.getId());

        try {
            String response = Utility.executePost(url, jsonParam.toString());

            if (response != null && !response.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Class created successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create class. No response from server.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadUserClasses(JPanel classesPanel) {
        String url = "http://localhost:5000/api/classrooms/" + user.getId() + "/classes";

        try {
            String response = Utility.executeGet(url);

            if (response != null && !response.isEmpty()) {
                JSONParser parser = new JSONParser();
                JSONArray classArray = (JSONArray) parser.parse(response);
                classesPanel.removeAll();

                for (Object obj : classArray) {
                    JSONObject classObj = (JSONObject) obj;
                    String className = (String) classObj.get("name");
                    String classDescription = (String) classObj.get("description");
                    String classId = String.valueOf(classObj.get("id"));

                    JSONArray membersArray = (JSONArray) classObj.get("members");
                    List<ClassMember> members = new ArrayList<>();
                    for (Object memberObj : membersArray) {
                        JSONObject memberJson = (JSONObject) memberObj;
                        String userId = String.valueOf(memberJson.get("userId"));
                        String role = (String) memberJson.get("role");
                        members.add(new ClassMember(userId, role, classId));
                    }

                    ClassRoom classRoom = new ClassRoom(classId, className, classDescription, members);
                    JPanel classCard = createClassCard(classRoom);
                    classesPanel.add(classCard);
                }

                classesPanel.revalidate();
                classesPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load classes. No response from server.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private JPanel createClassCard(ClassRoom classRoom) {
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };

        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setPreferredSize(new Dimension(300, 250));
        cardPanel.setOpaque(false);
        cardPanel.setBackground(Color.WHITE);

        String[] imagePaths = {
                "src/java_project/cover.png",
        };

        Random rand = new Random();
        String selectedImagePath = imagePaths[rand.nextInt(imagePaths.length)];

        ImageIcon icon = new ImageIcon(selectedImagePath);
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(300, 100, Image.SCALE_SMOOTH);

        JLabel imageLabel = new JLabel(new ImageIcon(scaledImg)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g2d);
            }
        };

        imageLabel.setPreferredSize(new Dimension(300, 100));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); // Center align the image

        Component verticalStrut = Box.createVerticalStrut(20);

        JLabel nameLabel = new JLabel(classRoom.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(JLabel.WIDTH); // Align left

        JLabel descriptionLabel = new JLabel(classRoom.getDescription());
        descriptionLabel.setAlignmentX(JLabel.WIDTH); // Align left

        nameLabel.setPreferredSize(new Dimension(280, 20));
        descriptionLabel.setPreferredSize(new Dimension(280, 20));

        cardPanel.add(imageLabel);
        cardPanel.add(verticalStrut);
        cardPanel.add(nameLabel);
        cardPanel.add(descriptionLabel);

        // Add ActionListener to open ClassDetails when cardPanel is clicked
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                openClassDetails(classRoom);
            }
        });

        return cardPanel;
    }

    private void openClassDetails(ClassRoom classRoom) {
        SwingUtilities.invokeLater(() -> {
            removeAll(); // Remove all components from HomePage
            setLayout(new BorderLayout());
            add(new ClassDetails(classRoom, user), BorderLayout.CENTER); // Add ClassDetails panel
            revalidate();
            repaint();
        });
    }

    public static void main(String[] args) {
        User user = new User("1", "John Doe", "john.doe@example.com");
        JFrame frame = new JFrame("Home Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new HomePage(user));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
