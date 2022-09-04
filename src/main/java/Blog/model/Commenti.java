package Blog.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @JsonBackReference
    @ManyToOne()
    private Topic topic;




}
