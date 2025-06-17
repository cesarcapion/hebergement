package fr.epita.assistants.ping.common.Response;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetFolderResponse {
    public String name;
    public String path;
    public boolean isDirectory;
}
