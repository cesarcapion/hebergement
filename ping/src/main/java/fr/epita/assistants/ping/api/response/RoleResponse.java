package fr.epita.assistants.ping.api.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;


@With
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    public Long id;
    public String name;

    public List<TopicInfoResponse> topics;
}
