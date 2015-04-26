/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import philharmonic.utilities.AddressesParser;
import philharmonic.utilities.JsonUtil;
import philharmonic.utilities.MappedEntityIdResolver;
import philharmonic.utilities.MessageSender;
import philharmonic.utilities.MessagesParser;


/**
 *
 * @author Kookie
 */
@Configuration
@PropertySource({"application.properties"})
public class AppConfig {
    
    
    @Bean
    public MappedEntityIdResolver Resolver() {
        return new MappedEntityIdResolver();
    }
    
    @Bean
    public MessagesParser MessagesParser() throws Exception {
        return new MessagesParser("Messages.xml");
    }
    
    @Bean
    public AddressesParser AddressesParser() throws Exception {
        return new AddressesParser("Components.xml");
    }
    
    @Bean
    public RestTemplate RestTemplate() {
        return new RestTemplate();
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
