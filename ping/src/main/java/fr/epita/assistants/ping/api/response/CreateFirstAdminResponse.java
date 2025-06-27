package fr.epita.assistants.ping.api.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@With
public class CreateFirstAdminResponse {
    UserResponse user;

    List<RoleResponse> createdRoles;
}
