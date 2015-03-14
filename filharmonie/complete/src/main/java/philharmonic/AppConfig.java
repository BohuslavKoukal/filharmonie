/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import philharmonic.utilities.JsonUtil;
import philharmonic.utilities.MappedEntityIdResolver;
import philharmonic.utilities.MessageSender;
import philharmonic.utilities.MessagesParser;


/**
 *
 * @author Kookie
 */
@Configuration
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
    
    
}
