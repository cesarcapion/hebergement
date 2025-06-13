package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "projects")
public class ProjectModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer uuid;

    @Column(name="owner_id")
    public String owner;

    public String name;
    public String path;

//     TODO handle links between project and members
//    @OneToMany(mappedBy = "projects")
//    public Set<String> members;
}
