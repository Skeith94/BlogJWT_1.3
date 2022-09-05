package Blog.repo;

import Blog.model.Commenti;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;


@Repository
@Transactional(readOnly = true)

public interface CommentiRepository extends CrudRepository<Commenti,Long> {


   ArrayList<Commenti> getCommentiByTopicId(long topicId);

   @Query("select (count(c) > 0) from Commenti c where c.id = ?1 and c.user.id = ?2")
   boolean CheckIFscrittore(Long id_commenti, Long id_user);


   @Transactional
   @Modifying
   @Query("update Commenti c set c.testo = ?1 where c.id = ?2")
   int ModificaCommento(String testo, Long id);


   @Transactional
   @Modifying
   @Query("delete from Commenti c where c.id = ?1")
   int CancellaCommento(Long id);



}



