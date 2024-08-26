package java_project.models;

public class Assignment {
    private int id;
    private String title;
    private String description;
    private int classId;

    public Assignment() {
    }

    public Assignment(int id, String title, String description, int classId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.classId = classId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }
}
