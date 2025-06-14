package fr.epita.assistants.ping.api.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@With
public class ProjectResponse {
    public String id;
    public String name;

    public ArrayList<UserInfoResponse> members;

    public UserInfoResponse owner;
}
