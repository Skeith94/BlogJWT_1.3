package Blog.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.sql.Delete;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commenti {
    @Id
    @SequenceGenerator(
            name = "commenti_sequence",
            sequenceName = "commenti_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "commenti_sequence"
    )
    private Long id;

    private  String testo;

    private LocalDateTime createdAt;


    private LocalDateTime modifiedAt;


    @ManyToOne
    private  User user;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JsonBackReference
    private Topic topic;




}
