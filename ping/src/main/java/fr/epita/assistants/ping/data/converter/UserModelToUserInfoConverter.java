package fr.epita.assistants.ping.data.converter;

import fr.epita.assistants.ping.api.response.UserInfoResponse;
import fr.epita.assistants.ping.data.model.UserModel;
import fr.epita.assistants.ping.utils.IConverter;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserModelToUserInfoConverter implements IConverter<UserModel, UserInfoResponse>
{
    @Override
    public UserInfoResponse convert(UserModel userModel) {
        return new UserInfoResponse(userModel.getUuid().toString(), userModel.getDisplayName(), userModel.getAvatar());
    }
}
