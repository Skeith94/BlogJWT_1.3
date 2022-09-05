package Blog.service;


import Blog.model.Commenti;
import Blog.repo.CommentiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional // serve per la gestione delle ACID connection con il db
@Slf4j
public class ServizioCommenti {

    private final CommentiRepository commentiRepository;

    public Commenti saveCommenti(Commenti commenti) {
        commentiRepository.save(commenti);
        return commenti;
    }


    public ArrayList<Commenti> findBytopic_id(long topicId) {
        ArrayList<Commenti>commenti= commentiRepository.getCommentiByTopicId(topicId);
        for (Commenti value : commenti) {
            value.getUser().setName("");
            value.getUser().setPassword("");
            value.getUser().setEmail("");
        }
        return commenti;
    }

    public boolean checkIFscrittore(Long id_commenti, Long id_user){
        return commentiRepository.CheckIFscrittore(id_commenti,id_user);
    }


    public int ModificaCommento(String testo, Long id){
        return commentiRepository.ModificaCommento(testo,id);
    }


    public int CancellaCommento(long id){
        return commentiRepository.CancellaCommento(id);
    }



}
