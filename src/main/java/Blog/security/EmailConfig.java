package Blog.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${emailpass}") // set Environment Variables  for SMTP "emailpass=yourpassword"
    String emailpass;


    @Bean
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setUsername("xxreydyganxx@gmail.com");

        javaMailSender.setPassword(emailpass);
        javaMailSender.setPort(25);

        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }
    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.", "true");
        properties.setProperty("mail.debug", "false");
        return properties;
    }

}
