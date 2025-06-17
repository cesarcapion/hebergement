package fr.epita.assistants.ping.common.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MoveFileRequest {
    public String src;
    public String dst;
}