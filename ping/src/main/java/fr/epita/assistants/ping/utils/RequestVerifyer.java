package fr.epita.assistants.ping.utils;

import com.arjuna.ats.jta.exceptions.NotImplementedException;
import fr.epita.assistants.ping.api.request.ExecFeatureRequest;
import fr.epita.assistants.ping.api.request.NewTicketRequest;
import fr.epita.assistants.ping.api.request.UpdateTicketRequest;
import fr.epita.assistants.ping.api.request.UserTicketRequest;

import java.util.UUID;

public class RequestVerifyer {
    private static boolean isUUIDInvalid(String uuid)
    {
        try {
            UUID.fromString(uuid);
        }
        catch (Exception e) {
            return true;
        }
        return false;
    }

    public static boolean isInvalid(Object request) throws NotImplementedException {
        throw new NotImplementedException("Request verification for request " + request.getClass().toString() + " does not exist, Unreachable");
    }

    public static boolean isInvalid(ExecFeatureRequest request) {
        if (request == null || request.feature == null ||
                request.command == null || request.params == null ||
                Feature.valueOfLabel(request.feature) == null) {
            return true;
        }
        if (Feature.valueOfLabel(request.feature) == Feature.GIT) {
            return switch (request.command) {
                case "init" -> !request.params.isEmpty();
                case "add" -> request.params.isEmpty();
                case "commit" -> request.params.size() != 1;
                default -> true;
            };
        }
        return true;
    }

    public static boolean isInvalid(NewTicketRequest request) {
        return request == null || request.name == null || request.name.isEmpty();
    }

    public static boolean isInvalid(UpdateTicketRequest request) {
        return request == null || (request.name == null && request.newOwnerId == null) || (request.newOwnerId != null && isUUIDInvalid(request.newOwnerId));
    }



    public static boolean isInvalid(UserTicketRequest request)
    {
        return request == null || request.userId == null || request.userId.isEmpty() || isUUIDInvalid(request.userId);
    }
}
