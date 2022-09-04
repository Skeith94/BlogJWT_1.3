package Blog.service;


import Blog.model.TokenEmail;
import Blog.model.User;
import Blog.repo.TokenEmailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

import javax.transaction.Transactional;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Transactional // serve per la gestione delle ACID connection con il db
@Slf4j
public class ServizioEmail implements MailSender {

    private final TokenEmailRepository tokenEmailRepository;


    @Autowired
    private JavaMailSender mailSender;


    public static long id=0;




    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        mailSender.send(simpleMessage);
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        mailSender.send(simpleMessages);
    }


    public void PreparaEmail(User user) throws MessagingException {

       String token= RandomString.make(60);
        log.info("token: {}  ",token);
       LocalDateTime  createdAt=LocalDateTime.now();
       LocalDateTime expiresAt=createdAt.plusMinutes(10);
       tokenEmailRepository.save(new TokenEmail(id++,token, createdAt, expiresAt, null,user));
       log.info("token di conferma salvato {} ");
       String text="http://localhost:8080/api/user/registration/confirm?token="+token;
       SimpleMailMessage simpleMessage = new SimpleMailMessage();
       simpleMessage.setTo(user.getEmail());
       simpleMessage.setFrom("ersito@divito.com");
       simpleMessage.setText(text);
       simpleMessage.setSubject("email di conferma del sito di vito");
        send(simpleMessage);
    }

public TokenEmail ConvalidaEmail(String token){
       TokenEmail tokenEmail =tokenEmailRepository.findByToken(token);
       if( tokenEmail==null)
       {
           log.info("token non trovato {} ");
           return null;
       }

       if(tokenEmail.getConfirmedAt()!=null){
           log.info("token gia usato {} ");
           return null;
       }

       LocalDateTime expiresAt = tokenEmail.getExpiresAt();
       if(expiresAt.isBefore(LocalDateTime.now())){
           log.info("token scaduto {} ");
           return null;
       }
       tokenEmailRepository.updateConfirmedAt(tokenEmail.getToken(),LocalDateTime.now());

     return tokenEmail;
    }

    public boolean RimandaEmail(User user){

        TokenEmail tokenEmail = tokenEmailRepository.findByUser(user);
        if( tokenEmail==null)
        {
            log.info("token non trovato {} ");
            return false;
        }

        if(tokenEmail.getConfirmedAt()!=null){
            log.info("token gia usato {} ");
            return false;
        }

        LocalDateTime expiresAt = tokenEmail.getExpiresAt();
        if(expiresAt.isBefore(LocalDateTime.now())){
            log.info("token scaduto {} ");
            return false;
        }

        String text="http://localhost:8080/api/user/registration/confirm?token="+tokenEmail.getToken();
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(user.getEmail());
        simpleMessage.setFrom("ersito@divito.com");
        simpleMessage.setText(text);
        simpleMessage.setSubject("email di conferma del sito di vito");
        send(simpleMessage);

        return true;
    }

}


