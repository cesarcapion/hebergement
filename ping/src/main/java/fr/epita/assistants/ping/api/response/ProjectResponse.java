package fr.epita.assistants.ping.api.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.ArrayList;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@With
public class GetProjectResponse {
    public String id;
    public String name;

    public ArrayList<UserInfoResponse> members;

    public UserInfoResponse owner;
}
