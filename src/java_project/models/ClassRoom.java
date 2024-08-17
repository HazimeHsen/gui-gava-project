package java_project.models;

import java.util.List;

public class ClassRoom {
    private String id;
    private String name;
    private String description;
    private List<ClassMember> members;

    public ClassRoom(String id, String name, String description, List<ClassMember> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ClassMember> getMembers() {
        return members;
    }
}
