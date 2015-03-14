/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig.RealInstance;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import philharmonic.utilities.MessageSender;

/**
 *
 * @author Kookie
 */

@Configuration
public class MessageSenderRealInstance {
    @Bean
    public MessageSender sender() {
        return new MessageSender();
    }
    
    @Bean
    public RestTemplate rt() {
        return Mockito.mock(RestTemplate.class);
    }
}
