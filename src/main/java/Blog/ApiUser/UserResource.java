

package Blog.ApiUser;

import Blog.filtrer.CustomRefreshFilter;
import Blog.model.Role;
import Blog.model.TokenEmail;
import Blog.model.User;
import Blog.service.ServizioEmail;
import Blog.service.ServizioUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserResource {


    private final ServizioUser ServizioUser;

    @Autowired
    private final  ServizioEmail servizioEmail;




    @GetMapping("/api/user/guest/sendagain")
    public ResponseEntity<String>sendagain(@CurrentSecurityContext(expression="authentication?.name") String username) throws MessagingException {
       User loggato= ServizioUser.getUser(username);
      boolean risultato=servizioEmail.RimandaEmail(loggato);
      if(risultato==true) {
          return ResponseEntity.ok().build();
      }
      return ResponseEntity.badRequest().body("redo_login");
    }


    @GetMapping("/api/user")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(ServizioUser.getUsers());
    }


    @PostMapping("/api/user/registration")
    public ResponseEntity<String> saveUsers(@RequestBody User User) throws MessagingException {
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/registration").toUriString());
        if(ServizioUser.checkUser(User.getUsername())) {
            return ResponseEntity.created(uri).body("username_occupato");
        }
        if(ServizioUser.checkEmail(User.getEmail())) {
            return ResponseEntity.created(uri).body("email_occupata");
        }
        ServizioUser.saveUser(User);
        ServizioUser.addRoleToUser(User.getUsername(),"ROLE_GUEST");
       servizioEmail.PreparaEmail(User);
        return ResponseEntity.created(uri).body("account_creato");
    }


    @PostMapping("/api/role/addroletouser")
    public ResponseEntity<Role> saveRole(@RequestBody RoleToUserForm form) {
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/addrole").toUriString());
        ServizioUser.addRoleToUser(form.username,form.rolename);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/role/newrole")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/newrole").toUriString());
        return ResponseEntity.created(uri).body(ServizioUser.saveRole(role));
    }

    @GetMapping("/api/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CustomRefreshFilter customRefreshFilter=new CustomRefreshFilter();
        customRefreshFilter.RefreshCheck(request.getHeader(AUTHORIZATION), ServizioUser, request,response);
    }

    @GetMapping( "/api/user/registration/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        URI uri= URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/registration/confirm").toUriString());
         TokenEmail tokenEmail=servizioEmail.ConvalidaEmail(token);

        if(tokenEmail==null){
            return ResponseEntity.created(uri).body("token errato o scaduto ");
        }
        log.info("CONTROLLO {}",tokenEmail.getUser().getUsername());
        ServizioUser.addRoleToUser(tokenEmail.getUser().getUsername(),"ROLE_USER");
        ServizioUser.RemoveRole(tokenEmail.getUser().getUsername(),"ROLE_GUEST");
        return ResponseEntity.created(uri).body("accout_verificato");
    }



    @AllArgsConstructor
    @Data
    static class RoleToUserForm{
        private String username;
        private String rolename;
    }

}
