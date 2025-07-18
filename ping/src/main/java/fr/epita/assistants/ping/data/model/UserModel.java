package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserModel {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String password;

    @Column(name="display_name")
    private String displayName;
    @Column(columnDefinition = "TEXT")
    private String resetToken;

    private String avatar;

    @Column(name= "created_on")
    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name="role_id")
    private RoleModel role;

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    public List<TicketModel> tickets;
}
