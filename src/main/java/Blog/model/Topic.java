package Blog.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    @Id
    @SequenceGenerator(
            name = "topic_sequence",
            sequenceName = "topic_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "topic_sequence"
    )
    private Long id;

    private String titolo;

    private String testo;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Commenti> commenti;

}
