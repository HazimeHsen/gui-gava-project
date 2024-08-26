package java_project;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java_project.models.ClassRoom;
import java_project.models.User;
import java_project.models.Assignment;
import java_project.models.Submission;
import java.util.concurrent.ExecutionException;

public class AssignmentGradesPanel extends JPanel {
    private ClassRoom classRoom;
    private User user;
    private Gson gson;

    public AssignmentGradesPanel(ClassRoom classRoom, User user) {
        this.classRoom = classRoom;
        this.user = user;
        this.gson = new Gson();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> navigateToAssignmentsPanel());
        topPanel.add(backButton, BorderLayout.WEST);
        JLabel titleLabel = new JLabel("Set Grades", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        showLoadingPanel();

        new LoadDataTask().execute();
    }

    private void showLoadingPanel() {
        removeAll();
        add(new LoadingPanel(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void updatePanel(List<Assignment> assignments) {
        JPanel assignmentsPanel = new JPanel();
        assignmentsPanel.setLayout(new BoxLayout(assignmentsPanel, BoxLayout.Y_AXIS));

        for (Assignment assignment : assignments) {
            JPanel assignmentPanel = new JPanel();
            assignmentPanel.setLayout(new BoxLayout(assignmentPanel, BoxLayout.Y_AXIS));
            assignmentPanel.setBorder(BorderFactory.createTitledBorder(assignment.getTitle()));

            List<User> users = fetchUsersInClass(classRoom.getId());
            for (User member : users) {
                JPanel userPanel = new JPanel(new BorderLayout());

                Submission submission = fetchSubmissionForUser(assignment.getId(), member.getId());
                System.out.println(submission);
                JLabel submissionLabel;
                if (submission != null) {
                    submissionLabel = new JLabel(member.getName() + ": " + submission.getFileName());
                } else {
                    submissionLabel = new JLabel(member.getName() + ": No submission yet from this user");
                }
                userPanel.add(submissionLabel, BorderLayout.WEST);

                JPanel gradePanel = new JPanel(new FlowLayout());
                JLabel gradeLabel = new JLabel("Grade: ");
                JTextField gradeField = new JTextField(String.valueOf(submission.getGrade()), 5);
                JButton gradeButton = new JButton("Set Grade");

                gradeButton.addActionListener(e -> openGradeDialog(assignment.getId(), member.getId(), gradeField));

                gradePanel.add(gradeLabel);
                gradePanel.add(gradeField);
                gradePanel.add(gradeButton);

                userPanel.add(gradePanel, BorderLayout.EAST);
                assignmentPanel.add(userPanel);
            }

            assignmentsPanel.add(assignmentPanel);
        }

        removeAll();
        add(new JScrollPane(assignmentsPanel), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void navigateToAssignmentsPanel() {
        removeAll();
        add(new ClassDetails(classRoom, user));
        revalidate();
        repaint();
    }

    private void openGradeDialog(int assignmentId, String userId, JTextField gradeField) {
        String gradeStr = JOptionPane.showInputDialog(this, "Enter grade:", gradeField.getText());
        if (gradeStr != null && !gradeStr.trim().isEmpty()) {
            try {
                int grade = Integer.parseInt(gradeStr.trim());
                saveGradeForUser(assignmentId, userId, grade);
                gradeField.setText(String.valueOf(grade));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid grade input. Please enter a valid number.");
            }
        }
    }

    private List<Assignment> fetchAssignmentsForClass(String classId) {
        String url = "http://localhost:5000/api/classrooms/" + classId + "/assignments";
        String response = Utility.executeGet(url);
        return gson.fromJson(response, new TypeToken<List<Assignment>>() {
        }.getType());
    }

    private List<User> fetchUsersInClass(String classId) {
        String url = "http://localhost:5000/api/classrooms/" + classId + "/users/" + user.getId();
        System.out.println(url);
        String response = Utility.executeGet(url);
        System.out.println(response);
        return gson.fromJson(response, new TypeToken<List<User>>() {
        }.getType());
    }

    private Submission fetchSubmissionForUser(int assignmentId, String userId) {
        String url = "http://localhost:5000/api/classrooms/" + assignmentId + "/users/" + userId + "/submission";
        String response = Utility.executeGet(url);
        return gson.fromJson(response, Submission.class);
    }

    private void saveGradeForUser(int assignmentId, String userId, int grade) {
        String url = "http://localhost:5000/api/classrooms/" + assignmentId + "/users/" + userId + "/grade";
        String payload = "{\"grade\":" + "\"" + grade + "\"" + "}";
        Utility.executePost(url, payload);
    }

    private class LoadDataTask extends SwingWorker<List<Assignment>, Void> {
        @Override
        protected List<Assignment> doInBackground() throws Exception {
            return fetchAssignmentsForClass(classRoom.getId());
        }

        @Override
        protected void done() {
            try {
                List<Assignment> assignments = get();
                updatePanel(assignments);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(AssignmentGradesPanel.this, "Error loading data.");
            }
        }
    }
}
