package fr.epita.assistants.ping.api.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;


@With
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponse {
    public Long id;
    public String name;
}
