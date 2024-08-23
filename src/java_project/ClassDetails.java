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

        JPanel topPanel = new JPanel(new BorderLayout());

        ImageIcon backIcon = new ImageIcon(getClass().getResource("/java_project/back-icon.png"));
        ImageIcon scaledBackIcon = new ImageIcon(
                backIcon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH));
        JButton backButton = new JButton(scaledBackIcon);
        backButton.addActionListener(e -> navigateToHomePage());

        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

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

        // Panel to display files at the bottom
        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        loadingLabel = new JLabel("Loading files...");
        filesPanel.add(loadingLabel);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(filesPanel);

        // Wrap detailsPanel in a JScrollPane
        scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        checkUserRole();

        // Automatically fetch and display files when the panel is loaded
        fetchAndDisplayFiles();

        if (isAdmin) {
            JButton addMembersButton = new JButton("Add Members");
            addMembersButton.addActionListener(e -> openAddMembersDialog());
            detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            detailsPanel.add(addMembersButton);

            detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            JLabel fileTypeLabel = new JLabel("Select File Type:");
            detailsPanel.add(fileTypeLabel);

            JButton assignmentButton = new JButton("Assignment (PDF/DOCX)");
            assignmentButton.addActionListener(e -> {
                selectedFileType = "assignment";
                openFileChooser(new String[] { "pdf", "docx" });
            });
            detailsPanel.add(assignmentButton);

            JButton imageButton = new JButton("Image");
            imageButton.addActionListener(e -> {
                selectedFileType = "image";
                openFileChooser(new String[] { "jpg", "jpeg", "png", "gif" });
            });
            detailsPanel.add(imageButton);
        }
    }

    private void fetchAndDisplayFiles() {
        String url = "http://localhost:5000/api/classrooms/" + classRoom.getId() + "/files";
        SwingUtilities.invokeLater(() -> {
            // Clear previous files and show the loading label
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
                    // Clear the loading label and display new files
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
        filesPanel.add(new JLabel("Files in this class:"));

        for (Object obj : filesArray) {
            JSONObject fileObj = (JSONObject) obj;
            String fileName = (String) fileObj.get("fileName");
            String fileType = (String) fileObj.get("fileType");
            String filePath = (String) fileObj.get("filePath"); // Assuming the server provides this
            String uploadedBy = ((JSONObject) fileObj.get("user")).get("name").toString();

            JLabel fileLabel = new JLabel("File: " + fileName + " (Uploaded by: " + uploadedBy + ")");
            filesPanel.add(fileLabel);

            if (fileType.equals("assignment")) {
                JButton pdfButton = new JButton("Open PDF");
                pdfButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openFile(filePath);
                    }
                });
                filesPanel.add(pdfButton);
            } else if (fileType.startsWith("image")) {
                try {
                    URL imageUrl = new URL(filePath);
                    ImageIcon imageIcon = new ImageIcon(imageUrl);

                    // Scale the image
                    Image img = imageIcon.getImage();
                    Image scaledImg = img.getScaledInstance(400, -1, Image.SCALE_SMOOTH); // Maintain aspect ratio
                    imageIcon = new ImageIcon(scaledImg);

                    JLabel imageLabel = new JLabel(imageIcon);
                    filesPanel.add(imageLabel);
                } catch (Exception e) {
                    filesPanel.add(new JLabel("Error loading image: " + e.getMessage()));
                }
            }
        }
    }

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

    private String uploadToApi(File file) {
        String url = "http://localhost:5000/api/classrooms/upload"; // Change this to your upload API endpoint
        String charset = "UTF-8";
        String boundary = Long.toHexString(System.currentTimeMillis()); // Generate a unique boundary
        String CRLF = "\r\n"; // Line separator

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream output = connection.getOutputStream();
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true)) {
                // Send userId
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"userId\"").append(CRLF);
                writer.append(CRLF).append(user.getId()).append(CRLF); // Send userId
                writer.flush();

                // Send classId
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"classId\"").append(CRLF);
                writer.append(CRLF).append(classRoom.getId()).append(CRLF); // Send classId
                writer.flush();

                // Send file type
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"fileType\"").append(CRLF);
                writer.append(CRLF).append(selectedFileType).append(CRLF); // Send file type
                writer.flush();

                // Send file
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"")
                        .append(CRLF);
                writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(file.getName())).append(CRLF);
                writer.append(CRLF);
                writer.flush();

                // Read file
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

            // Get response
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                fetchAndDisplayFiles(); // Refresh files after upload

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
}
