package java_project;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java_project.models.ClassRoom;
import java_project.models.ClassMember;
import java_project.models.User;

public class ClassDetails extends JPanel {
    private ClassRoom classRoom;
    private User user;
    private boolean isAdmin;
    private boolean isModerator;
    private List<JCheckBox> userCheckboxes = new ArrayList<>();
    private String selectedFileType;
    private JPanel filesPanel;
    private JLabel loadingLabel;
    private JScrollPane scrollPane;

    public ClassDetails(ClassRoom classRoom, User user) {
        this.classRoom = classRoom;
        this.user = user;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 400));

        checkUserRole();

        JPanel topPanel = new JPanel(new BorderLayout());

        ImageIcon backIcon = new ImageIcon(getClass().getResource("/java_project/back-icon.png"));
        ImageIcon scaledBackIcon = new ImageIcon(
                backIcon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH));
        JButton backButton = new JButton(scaledBackIcon);
        backButton.addActionListener(e -> navigateToHomePage());

        topPanel.add(backButton, BorderLayout.WEST);

        JMenuBar menuBar = new JMenuBar();

        if (isAdmin || isModerator) {
            JButton addMembersButton = new JButton("Add Members");
            addMembersButton.addActionListener(e -> openAddMembersDialog());
            menuBar.add(addMembersButton);
        }

        if (isAdmin) {
            JMenu uploadMenu = new JMenu("Upload Files");

            JMenuItem assignmentItem = new JMenuItem("Assignment (PDF/DOCX)");
            assignmentItem.addActionListener(e -> {
                selectedFileType = "assignment";
                openFileChooser(new String[] { "pdf", "docx" });
            });
            uploadMenu.add(assignmentItem);

            JMenuItem imageItem = new JMenuItem("Image");
            imageItem.addActionListener(e -> {
                selectedFileType = "image";
                openFileChooser(new String[] { "jpg", "jpeg", "png", "gif" });
            });
            uploadMenu.add(imageItem);

            menuBar.add(uploadMenu);

            JButton manageUsersButton = new JButton("Manage Users");
            manageUsersButton.addActionListener(e -> openManageUsersDialog());
            menuBar.add(manageUsersButton);
        }

        topPanel.add(menuBar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel nameLabel = new JLabel(classRoom.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel descriptionLabel = new JLabel(classRoom.getDescription());
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        detailsPanel.add(nameLabel);
        detailsPanel.add(descriptionLabel);

        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        loadingLabel = new JLabel("Loading files...");
        filesPanel.add(loadingLabel);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(filesPanel);

        scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplayFiles();
    }

    private void fetchAndDisplayFiles() {
        String url = "http://localhost:5000/api/classrooms/" + classRoom.getId() + "/files";
        SwingUtilities.invokeLater(() -> {
            filesPanel.removeAll();
            filesPanel.add(loadingLabel);
            revalidate();
            repaint();
        });

        new Thread(() -> {
            try {
                String response = Utility.executeGet(url);
                JSONParser parser = new JSONParser();
                JSONArray filesArray = (JSONArray) parser.parse(response);

                SwingUtilities.invokeLater(() -> {
                    filesPanel.remove(loadingLabel);
                    if (filesArray.isEmpty()) {
                        filesPanel.add(new JLabel("No files found for this class."));
                    } else {
                        displayFiles(filesArray);
                    }
                    revalidate();
                    repaint();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    filesPanel.remove(loadingLabel);
                    filesPanel.add(new JLabel("An error occurred while fetching files: " + e.getMessage()));
                    revalidate();
                    repaint();
                });
            }
        }).start();
    }

    private void displayFiles(JSONArray filesArray) {
        filesPanel.removeAll(); // Clear previous content

        for (Object obj : filesArray) {
            JSONObject fileObj = (JSONObject) obj;
            String fileName = (String) fileObj.get("fileName");
            String fileType = (String) fileObj.get("fileType");
            String filePath = (String) fileObj.get("filePath");
            String uploadedBy = ((JSONObject) fileObj.get("user")).get("name").toString();
            JSONArray commentsArray = (JSONArray) fileObj.get("comments");
            System.out.println(fileObj.toJSONString());
            JPanel filePanel = new JPanel();
            filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));

            JLabel fileLabel = new JLabel("Uploaded by: " + uploadedBy + " - " + fileName);
            filePanel.add(fileLabel);

            if (fileType.equals("assignment")) {
                JButton fileButton = new JButton(fileName);
                fileButton.addActionListener(e -> openFile(filePath));
                filePanel.add(fileButton);
            } else if (fileType.startsWith("image")) {
                try {
                    URL imageUrl = new URL(filePath);
                    ImageIcon imageIcon = new ImageIcon(imageUrl);
                    Image img = imageIcon.getImage();
                    Image scaledImg = img.getScaledInstance(400, -1, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaledImg);

                    JLabel imageLabel = new JLabel(imageIcon);
                    filePanel.add(imageLabel);
                } catch (Exception e) {
                    filePanel.add(new JLabel("Error loading image: " + e.getMessage()));
                }
            }

            // Display existing comments

            // Button to open comments dialog
            JButton viewCommentsButton = new JButton("View Comments");
            viewCommentsButton.addActionListener(e -> openCommentsDialog(fileObj));
            filePanel.add(viewCommentsButton);

            filesPanel.add(filePanel);
        }

        // Refresh the panel to display new content
        SwingUtilities.invokeLater(() -> {
            filesPanel.revalidate();
            filesPanel.repaint();
        });
    }

    private void openCommentsDialog(JSONObject fileObj) {
        JDialog commentDialog = new JDialog((Frame) null, "Comments", true);
        commentDialog.setLayout(new BorderLayout());

        // Create panel for comments
        JPanel commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));

        // Load existing comments
        JSONArray commentsArray = (JSONArray) fileObj.get("comments");
        if (commentsArray != null) {
            for (Object commentObj : commentsArray) {
                System.out.println(commentObj);
                JSONObject comment = (JSONObject) commentObj;
                String commentText = (String) comment.get("content");
                String commentUser = ((JSONObject) comment.get("author")).get("name").toString();
                JLabel commentLabel = new JLabel(commentUser + ": " + commentText);
                commentsPanel.add(commentLabel);
            }
        }

        // Create input field and button for adding a new comment
        JTextField commentField = new JTextField(30);
        JButton addCommentButton = new JButton("Add Comment");
        addCommentButton.addActionListener(e -> {
            String commentText = commentField.getText();
            if (!commentText.isEmpty()) {
                addComment(fileObj, commentText);
                commentDialog.dispose(); // Close the dialog
            } else {
                JOptionPane.showMessageDialog(commentDialog, "Comment cannot be empty.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.add(commentField);
        inputPanel.add(addCommentButton);

        commentDialog.add(new JScrollPane(commentsPanel), BorderLayout.CENTER);
        commentDialog.add(inputPanel, BorderLayout.SOUTH);

        commentDialog.pack();
        commentDialog.setLocationRelativeTo(null);
        commentDialog.setVisible(true);
    }

    private void addComment(JSONObject fileObj, String commentText) {
        String fileId = ((Number) fileObj.get("id")).toString(); // Convert Long to String
        String url = "http://localhost:5000/api/classrooms/" + classRoom.getId() + "/files/" + fileId + "/comments";

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("authorId", user.getId());
        jsonParam.put("content", commentText);

        new Thread(() -> {
            try {
                String response = Utility.executePost(url, jsonParam.toString());
                if (response != null && !response.isEmpty()) {
                    System.out.println(response);
                    JOptionPane.showMessageDialog(this, "Comment added successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    fetchAndDisplayFiles(); // Refresh the file list to show the new comment
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add comment. No response from server.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    @SuppressWarnings("deprecation")
    private void openFile(String filePath) {
        try {
            Desktop.getDesktop().browse(new URL(filePath).toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to open file: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateToHomePage() {
        removeAll();
        add(new HomePage(user));
        revalidate();
        repaint();
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
            addButton.addActionListener(e -> addSelectedMembers());

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
        }
    }

    private void openFileChooser(String[] fileTypes) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Files", fileTypes);
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String response = uploadToApi(selectedFile);
            JOptionPane.showMessageDialog(this, response, "Upload Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @SuppressWarnings("deprecation")
    private String uploadToApi(File file) {
        String url = "http://localhost:5000/api/classrooms/upload";
        String charset = "UTF-8";
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream output = connection.getOutputStream();
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true)) {

                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"userId\"").append(CRLF);
                writer.append(CRLF).append(user.getId()).append(CRLF);
                writer.flush();

                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"classId\"").append(CRLF);
                writer.append(CRLF).append(classRoom.getId()).append(CRLF);
                writer.flush();

                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"fileType\"").append(CRLF);
                writer.append(CRLF).append(selectedFileType).append(CRLF);
                writer.flush();

                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"fileName\"").append(CRLF);
                writer.append(CRLF).append(file.getName()).append(CRLF);
                writer.flush();

                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"")
                        .append(CRLF);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(CRLF);
                writer.append(CRLF);
                writer.flush();

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                output.flush();
                writer.append(CRLF).append("--" + boundary + "--").append(CRLF);
                writer.flush();
            }

            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                fetchAndDisplayFiles();

            } else {
                response.append("Upload failed: ").append(responseCode);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while uploading file: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void openManageUsersDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Manage Users:"));

        List<JComboBox<String>> roleSelectors = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        String currentUserId = user.getId(); // Get the current user's ID

        for (ClassMember member : classRoom.getMembers()) {
            String userId = member.getUserId();

            if (userId.equals(currentUserId)) {
                continue;
            }

            JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            String userName = member.getUserName();
            String userRole = member.getRole();

            JLabel userLabel = new JLabel(userName);
            userPanel.add(userLabel);

            JComboBox<String> roleSelector = new JComboBox<>(new String[] { "NORMAL", "MODERATOR" });
            roleSelector.setSelectedItem(userRole);
            roleSelectors.add(roleSelector);
            userIds.add(userId);

            userPanel.add(roleSelector);
            panel.add(userPanel);
        }

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> updateUserRoles(userIds, roleSelectors));
        panel.add(updateButton);

        JOptionPane.showMessageDialog(this, panel, "Manage Users", JOptionPane.PLAIN_MESSAGE);
    }

    private void updateUserRoles(List<String> userIds, List<JComboBox<String>> roleSelectors) {
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            String selectedRole = (String) roleSelectors.get(i).getSelectedItem();

            updateUserRole(userId, selectedRole);
        }
    }

    private void updateUserRole(String userId, String role) {
        String url = "http://localhost:5000/api/classrooms/" + classRoom.getId() + "/update-member-role";

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("userId", userId);
        jsonParam.put("role", role);
        System.out.println(jsonParam);
        new Thread(() -> {
            try {
                String response = Utility.executePut(url, jsonParam.toString());
                System.out.println(response.toString());
                if (response != null && !response.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "User roles updated successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update user roles. No response from server.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }
}
