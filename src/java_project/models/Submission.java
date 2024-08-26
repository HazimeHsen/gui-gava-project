package java_project.models;

public class Submission {
    private int id;
    private int assignmentId;
    private int userId;
    private String fileName;
    private String filePath;
    private int grade;

    public Submission() {
    }

    public Submission(int id, int assignmentId, int userId, String fileName, String filePath, int grade) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.userId = userId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
