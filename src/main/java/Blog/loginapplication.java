//login api JWT #SecurityFilterChain implement #token cryptography #hardened cookie no Token Sidejacking XSS



package Blog;

import Blog.model.Commenti;
import Blog.model.Role;
import Blog.model.Topic;
import Blog.model.User;
import Blog.security.TokenCipher;
import Blog.service.ServizioCommenti;
import Blog.service.ServizioTopic;
import Blog.service.ServizioUser;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;


//login api JWT #SecurityFilterChain implement #token cryptography #hardened cookie no Token Sidejacking XSS
@SpringBootApplication
public class loginapplication {



    private static final SecureRandom secureRandom = new SecureRandom();

    public static TokenCipher tokenCipher;
    public static KeysetHandle keyCiphering;


    public static final transient byte[] keyHMAC =new byte[500];

    public static void main(String[] args) {

        secureRandom.nextBytes(keyHMAC);

        try {
            tokenCipher = new TokenCipher();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            keyCiphering = KeysetHandle.generateNew(AeadKeyTemplates.AES128_GCM);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }



           
        SpringApplication.run(loginapplication.class, args);

    }





    @Bean
    CommandLineRunner run(ServizioUser userService, ServizioTopic servizioTopic, ServizioCommenti servizioCommenti) {
        return args -> {
            userService.createTrigger();
            userService.saveRole(new Role(null, "ROLE_GUEST"));
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_SCRITTORE"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            User uservito=new User(null, "vito malato", "vito", "1234", new ArrayList<>(),"vito@gmail.com");
            Topic topicvito=new Topic(null,"primo topic","speriamo funziona", LocalDateTime.now(),null,uservito,new ArrayList<>(),"testo anteprima primo topic parla di come il mondo non gira perche se girava era meglio viva i quadrati troppo belli",0);
            userService.saveUser(uservito);
            servizioTopic.saveTopic(topicvito);

            User userdanilo=new User(null, "danilo bella", "danilo", "1234", new ArrayList<>(),"danilo@gmail.com");
            userService.saveUser(userdanilo);
            Topic topicdanilo=new Topic(null,"Topic test ","io lo faccio meglio xD", LocalDateTime.now(),null,userdanilo,new ArrayList<>(),"testo anteprima topic danilo",0);
            servizioTopic.saveTopic(topicdanilo);

            User usermilos=new User(null, "ricardo milos", "milos", "1234", new ArrayList<>(),"milos@gmail.com");
            userService.saveUser(usermilos);
            servizioCommenti.saveCommenti(new Commenti(null,"questo post e bellissimo",LocalDateTime.now(),null,usermilos,topicdanilo));

            User userpeppe=new User(null, "zio peppe", "peppe", "1234", new ArrayList<>(),"peppe@gmail.com");
            userService.saveUser(userpeppe);
           servizioCommenti.saveCommenti(new Commenti(null,"bestiaa",LocalDateTime.now(),null,userpeppe,topicvito));
            servizioCommenti.saveCommenti(new Commenti(null,"sei scarso ad apex",LocalDateTime.now(),null,userpeppe,topicdanilo));


            userService.addRoleToUser("vito", "ROLE_SUPER_ADMIN");
            userService.addRoleToUser("danilo", "ROLE_ADMIN");
            userService.addRoleToUser("milos", "ROLE_SCRITTORE");
            userService.addRoleToUser("peppe", "ROLE_USER");



        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}











