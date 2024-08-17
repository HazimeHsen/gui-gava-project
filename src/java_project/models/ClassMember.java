package java_project.models;

public class ClassMember {
    private String userId;
    private String role;
    private String classId;

    public ClassMember(String userId, String role, String classId) {
        this.userId = userId;
        this.classId = classId;
        this.role = role;
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
}
