package fr.epita.assistants.ping.utils;

import com.arjuna.ats.jta.exceptions.NotImplementedException;
import fr.epita.assistants.ping.api.request.*;

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
        return request == null || request.subject == null || request.subject.isBlank();
    }

    public static boolean isInvalid(UpdateTicketRequest request) {
        return request == null || (request.subject == null && request.newOwnerId == null && request.ticketStatus == null)
                || (request.newOwnerId != null && isUUIDInvalid(request.newOwnerId));
    }



    public static boolean isInvalid(UserTicketRequest request)
    {
        return request == null || request.userId == null || request.userId.isBlank() || isUUIDInvalid(request.userId);
    }

    public static boolean isInvalid(NewRoleRequest request) {
        return request == null || request.name == null || request.name.isBlank();
    }

    public static boolean isInvalid(UpdateRoleRequest request) {
        return request == null || request.newName == null || request.newName.isBlank();
    }

    public static boolean isInvalid(TopicRoleRequest request) {
        return request == null || request.topicId == null;
    }

    public static boolean isInvalid(NewTopicRequest request) {
        return request == null || request.name == null || request.name.isBlank();
    }

    public static boolean isInvalid(UpdateTopicRequest request) {
        return request == null || request.newName == null || request.newName.isBlank();
    }

    public static boolean isInvalid(NewTicketHistoryRequest request) {
        return request == null || request.contentPath == null || request.contentPath.isBlank() /*|| (request.resourcePath != null && request.resourcePath.isBlank())*/;
    }

    public static boolean isInvalid(RelativePathRequest request) {
        return request == null || request.relativePath == null || request.relativePath.isBlank();
    }
}
