/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import philharmonic.service.IMCService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import philharmonic.utilities.JsonUtil;
import philharmonic.utilities.MessageSender;
import philharmonic.utilities.MessagesParser;

/**
 *
 * @author Kookie
 */
@Configuration
public class ControllerTestConfig extends WebMvcConfigurerAdapter {
         
        @Bean
    public IMCService imcService() {
        return Mockito.mock(IMCService.class);
    }
    
    @Bean
    public MessagesParser parser() {
        return Mockito.mock(MessagesParser.class);
    }
        
    
    @Bean
    public MessageSender sender() {
        return Mockito.mock(MessageSender.class);
    }
    
    @Bean
    public JsonUtil jsonUtil() {
        return Mockito.mock(JsonUtil.class);
    }
    
}
