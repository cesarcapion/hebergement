package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.UUID;

@Entity
@Table(name = "project_members")
@With
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMembersModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column(name="project_uuid")
    public UUID projectUUID;

    @Column(name="member_uuid")
    public UUID memberUUID;
}
