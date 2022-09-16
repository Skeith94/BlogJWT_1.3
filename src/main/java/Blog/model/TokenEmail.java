package Blog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenEmail {
    @javax.persistence.Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Id
    @SequenceGenerator(
            name = " TokenEmail_token_sequence",
            sequenceName = "TokenEmail_token_sequence",
            allocationSize = 1
    )

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "TokenEmail_token_sequence"
    )


    private String token;


    private LocalDateTime createdAt;


    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @OneToOne
    private User user;

}
