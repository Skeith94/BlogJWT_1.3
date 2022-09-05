package Blog.service;

import Blog.model.Topic;
import Blog.repo.CommentiRepository;
import Blog.repo.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional // serve per la gestione delle ACID connection con il db
@Slf4j
public class ServizioTopic {


    private final TopicRepository topicRepository;
    private final ServizioCommenti servizioCommenti;

    public Topic saveTopic(Topic topic) {
        log.info("salvo un nuovo topic {} nel db",topic.getTitolo());
        return topicRepository.save(topic);
    }

    public List<Topic> getTopicsAndUserName() {
        List<Topic> topic=topicRepository.findAll();
        for(int i=0; i<topic.size();i++){
            topic.get(i).getUser().setPassword("");
            topic.get(i).getUser().setName("");
            topic.get(i).getUser().setEmail("");
        }
        return topic;
    }


    public Topic findById(Long id) {
        Topic topic =topicRepository.findById(id).orElse(null);;
        topic.getUser().setPassword("");
        topic.getUser().setName("");
        topic.getUser().setEmail("");
        topic.setCommenti(servizioCommenti.findBytopic_id(topic.getId()));
        return  topic;
 }


 public int UpdateTopic(String testo, LocalDateTime modifiedAt, Long id) {
      int risultato= topicRepository.UpdateTopic(testo, modifiedAt, id);
      return risultato;
 }



    public int deleteTopic(Long id) {
        int risultato= topicRepository.DeleteTopic(id);
        return risultato;
    }








}
