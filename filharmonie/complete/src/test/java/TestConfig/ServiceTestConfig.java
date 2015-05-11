/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig;

import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;
import philharmonic.dao.IDao;
import philharmonic.dao.IDaoImpl;
import philharmonic.service.IMCService;
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
public class ServiceTestConfig {

    @Bean
    public IDao Dao() {
        return Mockito.mock(IDaoImpl.class);
    }
    
    @Bean
    public IMCService Service() {
        return new IMCService();
    }
    
    @Bean
    public DataSource dataSource() {
        return Mockito.mock(DriverManagerDataSource.class);
    }
    
    @Bean
    public JdbcTemplate jt() {
        return Mockito.mock(JdbcTemplate.class); 
    }
    
    @Bean
    public MappedEntityIdResolver resolver() {
        return new MappedEntityIdResolver();      
    }
    
    @Bean
    public MessagesParser parser() {
        return Mockito.mock(MessagesParser.class);
    }
     
    @Bean
    public AddressesParser AddressesParser() throws Exception {
        return Mockito.mock(AddressesParser.class);
    }
    
    @Bean
    public MessageSender sender() {
        return Mockito.mock(MessageSender.class);
    }
        
    @Bean
    public JsonUtil jsonUtil() {
        return Mockito.mock(JsonUtil.class);
    }
    
        @Bean
    public RestTemplate rt() {
        return Mockito.mock(RestTemplate.class);
    }

}
