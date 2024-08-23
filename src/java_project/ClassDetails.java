package java_project;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java_project.models.ClassRoom;
import java_project.models.ClassMember;
import java_project.models.User;

public class ClassDetails extends JFrame {
    private ClassRoom classRoom;
    private User user;
    private boolean isAdmin;
    @SuppressWarnings("unused")
    private boolean isModerator;
    private List<JCheckBox> userCheckboxes = new ArrayList<>();

    public ClassDetails(ClassRoom classRoom, User user) {
        this.classRoom = classRoom;
        this.user = user;

        setTitle("Class Details - " + classRoom.getName());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel("Class Name: " + classRoom.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel descriptionLabel = new JLabel("Description: " + classRoom.getDescription());
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        detailsPanel.add(nameLabel);
        detailsPanel.add(descriptionLabel);

        loadAdditionalClassDetails(detailsPanel);

        add(detailsPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new HomePage(user).setVisible(true));
        });

        add(backButton, BorderLayout.SOUTH);

        checkUserRole();

        if (isAdmin) {
            JButton addMembersButton = new JButton("Add Members");
            addMembersButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openAddMembersDialog();
                }
            });
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            detailsPanel.add(addMembersButton);
        }
    }

    private void checkUserRole() {
        for (ClassMember member : classRoom.getMembers()) {
            String memberId = member.getUserId();
            String role = member.getRole();
            if (user.getId().equals(memberId)) {
                if ("ADMIN".equals(role)) {
                    isAdmin = true;
                } else if ("MODERATOR".equals(role)) {
                    isModerator = true;
                }
            }
        }
    }

    private void loadAdditionalClassDetails(JPanel detailsPanel) {
        JLabel additionalInfoLabel = new JLabel("Additional class details will be displayed here...");
        detailsPanel.add(additionalInfoLabel);
    }

    private void openAddMembersDialog() {
        String response = Utility.executeGet(
                "http://localhost:5000/api/classrooms/" + classRoom.getId() + "/available-members/" + user.getId());

        try {
            JSONParser parser = new JSONParser();
            JSONArray usersArray = (JSONArray) parser.parse(response);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(new JLabel("Select members to add to the class:"));

            userCheckboxes.clear();

            for (Object obj : usersArray) {
                JSONObject userObj = (JSONObject) obj;
                String userId = String.valueOf(userObj.get("id"));
                String userName = (String) userObj.get("name");

                JCheckBox userCheckBox = new JCheckBox(userName);
                userCheckBox.setActionCommand(userId);
                userCheckboxes.add(userCheckBox);
                panel.add(userCheckBox);
            }

            JButton addButton = new JButton("Add Selected Members");
            addButton.addActionListener(e -> {
                addSelectedMembers();
            });

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(addButton);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(panel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(buttonPanel);

            JOptionPane.showMessageDialog(this, mainPanel, "Add Members to Class", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addSelectedMembers() {
        List<String> selectedUserIds = new ArrayList<>();
        for (JCheckBox checkBox : userCheckboxes) {
            if (checkBox.isSelected()) {
                selectedUserIds.add(checkBox.getActionCommand());
            }
        }

        for (String userId : selectedUserIds) {
            addMemberToClass(userId);
        }

    }

    @SuppressWarnings("unchecked")
    private void addMemberToClass(String userId) {
        String url = "http://localhost:5000/api/classrooms/" + classRoom.getId() + "/add-member";
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("userId", userId);

        try {
            String response = Utility.executePost(url, jsonParam.toString());

            if (response != null && !response.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Member added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add member. No response from server.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
