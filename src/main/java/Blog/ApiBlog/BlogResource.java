package Blog.ApiBlog;


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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<List<Topic>> getTopics() {
        return ResponseEntity.ok(servizioTopic.getTopicsAndUserName());
    }


    @GetMapping("/blog/topic")
    public   ResponseEntity<Topic> topic(@RequestParam("idtopic") Long id) {
    return ResponseEntity.ok(servizioTopic.findById(id));
    }

    @PostMapping("/blog/topic/post")
    public   ResponseEntity<String> topic(@RequestBody TopicForm form,@CurrentSecurityContext(expression="authentication?.name") String username) {
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/blog/topic/post").toUriString());
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
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/blog/topic/post").toUriString());
        User loggato= servizioUser.getUser(username);
        Topic topic=servizioTopic.findById(form.id);
        Commenti commenti=new Commenti(null, form.testo, LocalDateTime.now(),null,loggato,topic);
        Commenti risultato= servizioCommenti.saveCommenti(commenti);
        if(risultato==null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping("/blog/post/modified")
    public   ResponseEntity<String> Updatetopic(@RequestBody CommentForm form) {

        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/blog/post/modified").toUriString());

        int risultato= servizioTopic.UpdateTopic(form.getTesto(),LocalDateTime.now(),form.getId());
        if(risultato==0) {
            return ResponseEntity.internalServerError().body("id non trovato");
        }
        return ResponseEntity.ok().build();
    }



    @PostMapping("/blog/topic/comment/modifica")
    public   ResponseEntity<String> modificacomment(@RequestBody CommentForm form,@CurrentSecurityContext(expression="authentication?.name") String username) {
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/blog/topic/comment/modifica").toUriString());
        User loggato= servizioUser.getUser(username);
        Topic topic=servizioTopic.findById(form.id);
        boolean risultato= servizioCommenti.checkIFscrittore(form.getId(),loggato.getId());
        if(risultato==false) {
            return ResponseEntity.internalServerError().body("errore");
        }
        int risultatoModifica= servizioCommenti.ModificaCommento(form.getTesto(),form.id);
        if(risultatoModifica==0) {
            return ResponseEntity.internalServerError().body("errore");
        }
        return ResponseEntity.ok().build();
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
