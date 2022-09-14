package Blog.repo;



import Blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
    User findByEmail(String email);

    @Modifying
    @Query(nativeQuery = true,value = "CREATE TRIGGER update_comment_number_on_post AFTER INSERT ON commenti\n" +
            "FOR EACH ROW\n" +
            "    UPDATE topic AS t1 SET t1.numero_commenti = (SELECT COUNT(t2.id) FROM commenti AS t2 WHERE t1.id = t2.topic_id)")
    void createTrigger();


}