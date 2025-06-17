package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(name="display_name")
    private String displayName = "";
    @Column(name="is_admin")
    private Boolean isAdmin = false;

    private String avatar = "";



    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean admin) { isAdmin = admin; }
}
