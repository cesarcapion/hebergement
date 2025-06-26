package fr.epita.assistants.ping.api.response;

import fr.epita.assistants.ping.data.model.FAQModel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class CategoryResponse {
    public Long id;
    public String name;
    //public List<Long> questions;
    public CategoryResponse(Long id, String name/*,List<FAQResponse> questions*/) {
        this.id = id;
        this.name = name;

        //this.questions = questions;
    }
}
