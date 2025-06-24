package fr.epita.assistants.ping.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name= "categories")
@With
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryModel {
    @Id
    @GeneratedValue
    private Long id;

//    @Column(unique=true)
    private String name;

    @Column(name= "parent_id")
    private Integer parentId;

    @OneToMany(mappedBy = "category")
    private List<FAQModel> questions;
}
