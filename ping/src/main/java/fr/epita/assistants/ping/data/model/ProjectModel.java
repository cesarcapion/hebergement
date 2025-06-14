package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "projects")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@With
public class ProjectModel {
    @Id
    public UUID uuid;

    @Column(name="owner_id")
    public UUID ownerId;

    public String name;
    public String path;


    @OneToMany(mappedBy = "projectUUID", fetch = FetchType.EAGER)
    public Set<ProjectMembersModel> members;
}
