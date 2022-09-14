package Blog.service;


import Blog.Projection.SingleTopicInfo;
import Blog.Projection.TopicInfo;
import Blog.model.Topic;
import Blog.repo.TopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional // serve per la gestione delle ACID connection con il db
@Slf4j
public class ServizioTopic {


    private final TopicRepository topicRepository;

    private final ServizioCommenti servizioCommenti;

    public Topic saveTopic(Topic topic) {
        log.info("salvo un nuovo topic {} nel db", topic.getTitolo());
        return topicRepository.save(topic);
    }

    public List<TopicInfo> getTopicsAndUserName() {
        return topicRepository.TrovaTopic();
    }


    public SingleTopicInfo trovaSingleTopic(Long id) {
        return  topicRepository.FindSingleTopic(id);
    }


    public int UpdateTopic(String testo, LocalDateTime modifiedAt, Long id) {
        int risultato = topicRepository.UpdateTopic(testo, modifiedAt, id);
        return risultato;
    }


    public int deleteTopic(Long id) {
        int risultato = topicRepository.DeleteTopic(id);
        return risultato;
    }

    public Topic findById(Long id) {
        Topic topic =topicRepository.findById(id).orElse(null);;
        return  topic;
    }


}