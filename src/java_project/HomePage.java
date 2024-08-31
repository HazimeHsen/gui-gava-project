package java_project;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.awt.geom.RoundRectangle2D;

import java_project.models.ClassMember;
import java_project.models.ClassRoom;
import java_project.models.User;
import raven.crazypanel.CrazyPanel;

public class HomePage extends CrazyPanel {
    private User user;
    private float opacity = 0.0f;

    public HomePage(User user) {
        this.user = user;

        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Label username = new Label(user.getName());
        username.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(username, BorderLayout.WEST);

        components.Button createClassButton = new components.Button("Create Class");
        createClassButton.setBackground(new java.awt.Color(157, 153, 255));
        createClassButton.setForeground(new java.awt.Color(255, 255, 255));
        createClassButton.setPreferredSize(new Dimension(150, 30));
        createClassButton.setEffectColor(new java.awt.Color(199, 196, 255));
        createClassButton.addActionListener(e -> openCreateClassDialog());
        buttonPanel.add(createClassButton, BorderLayout.EAST);

        add(buttonPanel, BorderLayout.NORTH);

        JPanel centeredPanel = new JPanel(new BorderLayout());
        centeredPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel classesPanel = new JPanel();
        classesPanel.setLayout(new GridBagLayout());
        classesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(classesPanel);
        scrollPane.setPreferredSize(new Dimension(1280, 600));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        centeredPanel.add(scrollPane, BorderLayout.CENTER);

        add(centeredPanel);

        loadUserClasses(classesPanel);

        Timer timer = new Timer(10, e -> {
            opacity += 0.05f;
            if (opacity > 1.0f) {
                opacity = 1.0f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        super.paintComponent(g2d);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    }

    private void openCreateClassDialog() {
        // Define the background color for both the dialog and the text fields
        Color dialogBackgroundColor = UIManager.getColor("Panel.background"); // Default color for the dialog's
                                                                              // background

        components.TextField nameField = new components.TextField();
        nameField.setBackground(dialogBackgroundColor);
        nameField.setLabelText("Name");
        nameField.setLineColor(new java.awt.Color(131, 126, 253));
        nameField.setSelectionColor(new java.awt.Color(157, 153, 255));

        components.TextField descriptionField = new components.TextField();
        descriptionField.setBackground(dialogBackgroundColor);
        descriptionField.setLabelText("Description");
        descriptionField.setLineColor(new java.awt.Color(131, 126, 253));
        descriptionField.setSelectionColor(new java.awt.Color(157, 153, 255));

        // Create and configure the main panel
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(dialogBackgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Class Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.2;
        panel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.8;
        panel.add(descriptionField, gbc);

        panel.setPreferredSize(new Dimension(400, 200)); // Increase size of the dialog

        components.Button createButton = new components.Button("Create");
        createButton.setBackground(new java.awt.Color(157, 153, 255));
        createButton.setForeground(new java.awt.Color(255, 255, 255));
        createButton.setPreferredSize(new Dimension(150, 30));
        createButton.setEffectColor(new java.awt.Color(199, 196, 255));
        createButton.addActionListener(e -> {
            String className = nameField.getText();
            String description = descriptionField.getText();

            if (!className.isEmpty() && !description.isEmpty()) {
                SwingUtilities.getWindowAncestor(panel).dispose();
                createClass(className, description);
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        components.Button cancelButton = new components.Button("Cancel");
        cancelButton.setBackground(new java.awt.Color(200, 0, 0));
        cancelButton.setForeground(new java.awt.Color(255, 255, 255));
        cancelButton.setPreferredSize(new Dimension(150, 30));
        cancelButton.setEffectColor(new java.awt.Color(199, 196, 255));
        cancelButton.addActionListener(e -> {
            SwingUtilities.getWindowAncestor(panel).dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(dialogBackgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0)); // Add padding below the buttons
        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        JPanel finalPanel = new JPanel(new BorderLayout());
        finalPanel.setBackground(dialogBackgroundColor);
        finalPanel.add(panel, BorderLayout.CENTER);
        finalPanel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Create a New Class", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(finalPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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
                loadUserClasses(
                        (JPanel) ((JScrollPane) ((JPanel) getComponent(1)).getComponent(0)).getViewport().getView()); // Reload
                                                                                                                      // classes
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

            classesPanel.removeAll();

            if (response != null && !response.isEmpty()) {
                JSONParser parser = new JSONParser();
                JSONArray classArray = (JSONArray) parser.parse(response);

                if (classArray.isEmpty()) {
                    JLabel noClassesLabel = new JLabel("No classes available yet.");
                    noClassesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                    noClassesLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    classesPanel.setLayout(new BorderLayout());
                    classesPanel.add(noClassesLabel, BorderLayout.CENTER);
                } else {
                    classesPanel.setLayout(new GridBagLayout());

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.insets = new Insets(0, 0, 10, 10); // No margin at the top, 10 pixels at the bottom, 10 pixels
                                                           // on the right

                    int count = 0;
                    for (int i = 0; i < classArray.size(); i++) {
                        JSONObject classObj = (JSONObject) classArray.get(i);
                        String className = (String) classObj.get("name");
                        String classDescription = (String) classObj.get("description");
                        String classId = String.valueOf(classObj.get("id"));

                        JSONArray membersArray = (JSONArray) classObj.get("members");
                        List<ClassMember> members = new ArrayList<>();
                        for (Object memberObj : membersArray) {
                            JSONObject memberJson = (JSONObject) memberObj;
                            String userId = String.valueOf(memberJson.get("userId"));
                            String role = (String) memberJson.get("role");
                            JSONObject userJson = (JSONObject) memberJson.get("user");
                            String userName = (String) userJson.get("name");
                            String userEmail = (String) userJson.get("email");

                            members.add(new ClassMember(userId, role, classId, userName, userEmail));
                        }

                        ClassRoom classRoom = new ClassRoom(classId, className, classDescription, members);
                        JPanel classCard = createClassCard(classRoom);

                        gbc.gridx = count % 4; // Position the card in the correct column
                        gbc.gridy = count / 4; // Position the card in the correct row

                        classesPanel.add(classCard, gbc);
                        count++;
                    }
                }

                classesPanel.revalidate();
                classesPanel.repaint();
            } else {
                JLabel noClassesLabel = new JLabel("Failed to load classes. No response from server.");
                noClassesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                noClassesLabel.setHorizontalAlignment(SwingConstants.CENTER);
                classesPanel.setLayout(new BorderLayout());
                classesPanel.add(noClassesLabel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            JLabel noClassesLabel = new JLabel("An error occurred while loading classes.");
            noClassesLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noClassesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            classesPanel.setLayout(new BorderLayout());
            classesPanel.add(noClassesLabel, BorderLayout.CENTER);
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
        cardPanel.setPreferredSize(new Dimension(260, 250));
        cardPanel.setOpaque(false);
        cardPanel.setBackground(Color.WHITE);

        String[] imagePaths = {
                "src/java_project/cover.png",
                "src/java_project/cover2.png",
                "src/java_project/cover3.jpg",
                "src/java_project/cover4.jpg",
                "src/java_project/cover5.png",
        };

        Random rand = new Random();
        String selectedImagePath = imagePaths[rand.nextInt(imagePaths.length)];

        ImageIcon icon = new ImageIcon(selectedImagePath);
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(300, 115, Image.SCALE_SMOOTH);

        JLabel imageLabel = new JLabel(new ImageIcon(scaledImg)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                super.paintComponent(g2d);
            }
        };

        imageLabel.setPreferredSize(new Dimension(300, 125));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        Component verticalStrut = Box.createVerticalStrut(10);

        JLabel nameLabel = new JLabel(classRoom.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        JLabel descriptionLabel = new JLabel(classRoom.getDescription());
        descriptionLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        nameLabel.setPreferredSize(new Dimension(300, 20));
        descriptionLabel.setPreferredSize(new Dimension(300, 20));

        cardPanel.add(imageLabel);
        cardPanel.add(verticalStrut);
        cardPanel.add(nameLabel);
        cardPanel.add(descriptionLabel);

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
            removeAll();
            setLayout(new BorderLayout());
            add(new ClassDetails(classRoom, user), BorderLayout.CENTER);
            revalidate();
            repaint();
        });
    }
}
