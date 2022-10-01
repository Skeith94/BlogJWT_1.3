# BlogJWT_1.3
Parte Back end del blog https://github.com/Skeith94/BlogAngular realizzata con spring boot
Blog con sicurezza JWT,database MYSQL,possibilitá di postare, commentare in base alle autorizzazioni

Importante se vuoi provare il progetto settare come variabile d'ambiente  emailpass=TUAMAILPASSWORD e modificare email nella classe 
src/main/java/Blog/security/EmailConfig.java "javaMailSender.setUsername("TUAEMAIL@gmail.com");" ed abilitare SMTP

COLLECTION di postman pronte per fare i test sul applicazione,basta importarle per avere tutto giá pronto :)

blog.postman_collection.json

loginJWT.postman_collection.json

