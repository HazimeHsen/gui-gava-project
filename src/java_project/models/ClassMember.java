package java_project.models;

public class ClassMember {
    private String userId;
    private String role;
    private String classId;
    private String userName;
    private String userEmail;

    public ClassMember(String userId, String role, String classId, String userName, String userEmail) {
        this.userId = userId;
        this.role = role;
        this.classId = classId;
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }

    public String getClassId() {
        return classId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
