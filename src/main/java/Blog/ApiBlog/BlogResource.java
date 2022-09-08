package Blog.ApiBlog;


import Blog.Projection.SingleTopicInfo;
import Blog.Projection.TopicInfo;
import Blog.model.Commenti;
import Blog.model.Topic;
import Blog.model.User;
import Blog.service.ServizioCommenti;
import Blog.service.ServizioTopic;
import Blog.service.ServizioUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor

public class BlogResource {

    private final ServizioTopic servizioTopic;

    private final ServizioUser servizioUser;

    private final ServizioCommenti servizioCommenti;


    @GetMapping("/blog/home")
    public ResponseEntity<List<TopicInfo>> getTopics() {
        return ResponseEntity.ok( servizioTopic.getTopicsAndUserName());
    }


    @GetMapping("/blog/topic")
    public   ResponseEntity<SingleTopicInfo> topic(@RequestParam("idtopic") Long id) {
        SingleTopicInfo topic = servizioTopic.trovaSingleTopic(id);
        if(topic==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topic);
    }

    @PostMapping("/blog/topic/post")
    public   ResponseEntity<String> topic(@RequestBody TopicForm form,@CurrentSecurityContext(expression="authentication?.name") String username) {
        User loggato= servizioUser.getUser(username);
        Topic topic = new Topic(null,form.getTitolo(), form.getTesto(), LocalDateTime.now(),null,loggato,new ArrayList<>());
       Topic risultato= servizioTopic.saveTopic(topic);
       if(risultato==null) {
           return ResponseEntity.internalServerError().build();
       }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/blog/topic/comment")
    public   ResponseEntity<String> comment(@RequestBody CommentForm form,@CurrentSecurityContext(expression="authentication?.name") String username) {
        User loggato= servizioUser.getUser(username);
        Topic topic=servizioTopic.findById(form.getId());
        Commenti commenti=new Commenti(null, form.testo, LocalDateTime.now(),null,loggato,topic);
        Commenti risultato= servizioCommenti.saveCommenti(commenti);
        if(risultato==null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/blog/post/modified")
    public   ResponseEntity<String> Updatetopic(@RequestBody CommentForm form) {

        int risultato= servizioTopic.UpdateTopic(form.getTesto(),LocalDateTime.now(),form.getId());
        if(risultato==0) {
            return ResponseEntity.internalServerError().body("id non trovato");
        }
        return ResponseEntity.ok().build();
    }



    @PostMapping("/blog/topic/comment/modifica")
    public   ResponseEntity<String> modificacomment(@RequestBody CommentForm form,@CurrentSecurityContext(expression="authentication?.name") String username) {
        User loggato= servizioUser.getUser(username);
        boolean risultato= servizioCommenti.checkIFscrittore(form.getId(),loggato.getId());
        if(risultato==false) {
            return ResponseEntity.internalServerError().body("solo chi ha scritto il commento puo modificarlo");
        }
        int risultatoModifica= servizioCommenti.ModificaCommento(form.getTesto(),form.id);
        if(risultatoModifica==0) {
            return ResponseEntity.internalServerError().body("commento non modificato");
        }
        return ResponseEntity.ok().build();
    }



    @PostMapping("/blog/topic/delete/topic")
    public ResponseEntity<String> eliminaTopic(@RequestParam("id") Long id){
        int risultato=servizioTopic.deleteTopic(id);
        if(risultato==0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body("topic con id "+id+" cancellato");
    }


    @PostMapping("/blog/topic/delete/commento")
    public ResponseEntity<String> eliminaCommento(@RequestParam("id") Long id){
        int risultato=servizioCommenti.CancellaCommento(id);
        if(risultato==0){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body("commento con id "+id+" cancellato");
    }


    @AllArgsConstructor
    @Data
    static class TopicForm{
        private String titolo;
        private String testo;
    }

    @AllArgsConstructor
    @Data
    static class CommentForm{
        private Long id;
        private String testo;
    }




}
