package fr.epita.assistants.ping.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;


public class MoveFileRequest {
    public String src;
    public String dst;
}