package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faq")
@With
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FAQModel {
    @Id
    @GeneratedValue
    private Long id;

    private String question;

    private String answer;

    @ManyToOne
    private CategoryModel category;
}
