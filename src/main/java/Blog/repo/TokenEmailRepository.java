
package Blog.repo;

import Blog.model.TokenEmail;
import Blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Repository
@Transactional(readOnly = true)
public interface TokenEmailRepository extends JpaRepository<TokenEmail,Long> {

   TokenEmail findByToken(String token);

   @Transactional
   @Modifying
   @Query("UPDATE TokenEmail c " +
           "SET c.confirmedAt = ?2 " +
           "WHERE c.token = ?1")
   int updateConfirmedAt(String token, LocalDateTime confirmedAt);

   TokenEmail findByUser(User user);
}







