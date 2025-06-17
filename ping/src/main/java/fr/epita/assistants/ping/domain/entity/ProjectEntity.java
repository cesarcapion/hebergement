package fr.epita.assistants.ping.domain.entity;

import fr.epita.assistants.ping.data.model.UserModel;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
public class ProjectEntity {
    public String project_id;
    public UUID owner_id;
    public String displayName;
    public String avatar;

    public ArrayList<UserModel> members;
}
