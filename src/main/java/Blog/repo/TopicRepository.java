
package Blog.repo;


import Blog.Projection.SingleTopicInfo;
import Blog.Projection.TopicInfo;
import Blog.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Repository
@Transactional
public interface TopicRepository extends JpaRepository<Topic,Long> {




    @Modifying
    @Query("update Topic t set t.testo = ?1, t.modifiedAt = ?2 where t.id = ?3")
    int UpdateTopic(String testo, LocalDateTime modifiedAt, Long id);




    @Modifying
    @Query("delete Topic t where upper(t.id) = upper(?1)")
    int DeleteTopic(Long id);

    @Query("select t from Topic t ORDER BY t.createdAt")
    List<TopicInfo> TrovaTopic();

    @Query("select t from Topic t where t.id = ?1")
    SingleTopicInfo FindSingleTopic(Long id);





}
