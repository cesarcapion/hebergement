package fr.epita.assistants.ping.utils;

public enum UserStatus {
    MEMBER,
//    ADMIN,
    OWNER,
    NOT_A_MEMBER,
    /// no project found, impossible to link with a User
    ERROR // when the project is not found so we can't get the role
}
