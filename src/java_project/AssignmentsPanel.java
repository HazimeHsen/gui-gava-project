package java_project;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java_project.models.ClassRoom;
import java_project.models.User;

public class AssignmentsPanel extends JPanel {
    private ClassRoom classRoom;
    private User user;
    private JPanel assignmentsPanel;
    private JLabel loadingLabel;

    public AssignmentsPanel(ClassRoom classRoom, User user) {
        this.classRoom = classRoom;
        this.user = user;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Assignments", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        assignmentsPanel = new JPanel();
        assignmentsPanel.setLayout(new BoxLayout(assignmentsPanel, BoxLayout.Y_AXIS));
        loadingLabel = new JLabel("Loading assignments...");
        assignmentsPanel.add(loadingLabel);

        JScrollPane scrollPane = new JScrollPane(assignmentsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        fetchAndDisplayAssignments();
    }

    private void fetchAndDisplayAssignments() {
        String url = "http://localhost:5000/api/classrooms/" + classRoom.getId() + "/" + user.getId() + "/assignments";
        SwingUtilities.invokeLater(() -> {
            assignmentsPanel.removeAll();
            assignmentsPanel.add(loadingLabel);
            revalidate();
            repaint();
        });

        new Thread(() -> {
            try {
                String response = Utility.executeGet(url);
                JSONParser parser = new JSONParser();
                JSONArray assignmentsArray = (JSONArray) parser.parse(response);

                // Debugging: Print out the JSON structure
                System.out.println("Assignments JSON Array: " + assignmentsArray.toJSONString());

                SwingUtilities.invokeLater(() -> {
                    assignmentsPanel.remove(loadingLabel);
                    if (assignmentsArray.isEmpty()) {
                        assignmentsPanel.add(new JLabel("No assignments found for this class."));
                    } else {
                        displayAssignments(assignmentsArray);
                    }
                    revalidate();
                    repaint();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    assignmentsPanel.remove(loadingLabel);
                    assignmentsPanel.add(new JLabel("An error occurred while fetching assignments: " + e.getMessage()));
                    revalidate();
                    repaint();
                });
            }
        }).start();
    }

    private void displayAssignments(JSONArray assignmentsArray) {
        assignmentsPanel.removeAll(); // Clear previous content

        for (Object obj : assignmentsArray) {
            if (!(obj instanceof JSONObject)) {
                continue; // Skip if it's not a JSONObject
            }

            JSONObject assignmentObj = (JSONObject) obj;
            String assignmentId = String.valueOf(assignmentObj.get("id"));
            String assignmentTitle = (String) assignmentObj.get("title");
            String assignmentDescription = (String) assignmentObj.get("description");
            JSONArray submissionsArray = (JSONArray) assignmentObj.get("submissions");
            JSONObject submission = submissionsArray.isEmpty() ? null : (JSONObject) submissionsArray.get(0);
            JSONObject file = (JSONObject) assignmentObj.get("file");

            JPanel assignmentPanel = new JPanel();
            assignmentPanel.setLayout(new BoxLayout(assignmentPanel, BoxLayout.Y_AXIS));
            assignmentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel(assignmentTitle);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            assignmentPanel.add(titleLabel);

            JLabel descriptionLabel = new JLabel(
                    "<html><p style=\"width: 400px;\">" + assignmentDescription + "</p></html>");
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            assignmentPanel.add(descriptionLabel);

            if (submission != null) {
                displaySubmissionDetails(assignmentPanel, submission);
            } else {
                JButton submitButton = new JButton("Submit Assignment");
                submitButton.addActionListener(e -> openFileChooser(assignmentId));
                assignmentPanel.add(submitButton);
            }

            if (file != null) {
                String filePath = (String) file.get("filePath");
                JButton viewFileButton = new JButton("View Assignment File");
                viewFileButton.addActionListener(e -> openFile(filePath));
                assignmentPanel.add(viewFileButton);
            }

            assignmentsPanel.add(assignmentPanel);
            assignmentsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between assignments
        }

        // Refresh the panel to display new content
        SwingUtilities.invokeLater(() -> {
            assignmentsPanel.revalidate();
            assignmentsPanel.repaint();
        });
    }

    private void displaySubmissionDetails(JPanel assignmentPanel, JSONObject submission) {
        String fileName = (String) submission.get("fileName");
        String filePath = (String) submission.get("filePath");
        String grade = submission.get("grade") != null ? submission.get("grade").toString() : "Pending";

        JLabel submittedLabel = new JLabel("Submitted File: " + fileName);
        assignmentPanel.add(submittedLabel);

        JButton openFileButton = new JButton("Open Submitted File");
        openFileButton.addActionListener(e -> openFile(filePath));
        assignmentPanel.add(openFileButton);

        JLabel gradeLabel = new JLabel("Grade: " + grade);
        assignmentPanel.add(gradeLabel);
    }

    private void openFileChooser(String assignmentId) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF and DOCX files", "pdf", "docx");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                uploadAssignmentSubmission(assignmentId, selectedFile);
            }
        }
    }

    private void uploadAssignmentSubmission(String assignmentId, File file) {
        String url = "http://localhost:5000/api/assignments/" + assignmentId + "/submit";
        String boundary = "===" + System.currentTimeMillis() + "===";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream outputStream = connection.getOutputStream()) {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

                // Add file part
                String fileName = file.getName();
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(fileName)
                        .append("\"\r\n");
                writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append("\r\n");
                writer.append("Content-Transfer-Encoding: binary\r\n");
                writer.append("\r\n");
                writer.flush();

                try (FileInputStream inputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }

                writer.append("\r\n");
                writer.flush();

                // Add userId part
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"userId\"\r\n");
                writer.append("\r\n");
                writer.append(String.valueOf(user.getId())).append("\r\n");
                writer.flush();

                // End of multipart/form-data.
                writer.append("--").append(boundary).append("--").append("\r\n");
                writer.close();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(this, "Assignment submitted successfully.");
                fetchAndDisplayAssignments();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit assignment. Please try again.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while submitting the assignment: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openFile(String filePath) {
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URL(filePath).toURI());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while opening the file: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
