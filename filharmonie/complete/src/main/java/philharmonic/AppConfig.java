/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic;


import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import philharmonic.dao.IDao;
import philharmonic.dao.IDaoImpl;
import philharmonic.mailer.controller.EmailSender;
import philharmonic.utilities.JsonUtil;
import philharmonic.utilities.MappedEntityIdResolver;
import philharmonic.utilities.MessageSender;
import philharmonic.utilities.MessagesParser;


/**
 *
 * @author Kookie
 */
@Configuration
@PropertySource({"application.properties", "mailer.properties"})
public class AppConfig {
    
    
    @Bean
    public MappedEntityIdResolver Resolver() {
        return new MappedEntityIdResolver();
    }
    
    @Bean
    public MessagesParser Parser() throws Exception {
        return new MessagesParser("Messages.xml");
    }
    
    @Bean
    public MessageSender sender() {
        return new MessageSender();
    }
    
    @Bean
    public JsonUtil jsonUtil() {
        return new JsonUtil();
    }
    
    @Value("${mail.smtp.host}")
    private String smtpHost;
    
    @Value("${mail.smtp.socketFactory.port}")
    private String socketFactoryPort;
    
    @Value("${mail.smtp.socketFactory.class}")
    private String socketFactoryClass;
    
    @Value("${mail.smtp.auth}")
    private String auth;
    
    @Value("${mail.smtp.port}")
    private String smtpPort;
    
    @Bean
    public Properties properties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.socketFactory.port", socketFactoryPort);
        props.put("mail.smtp.socketFactory.class", socketFactoryClass);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.port", smtpPort);
        return props;
    }
    
    @Bean
    public EmailSender emailSender() {
        return new EmailSender(properties());
    }
    
    
}
