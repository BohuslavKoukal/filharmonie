/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import philharmonic.service.IMCService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import philharmonic.controller.IMCController;
import philharmonic.dao.IDao;
import philharmonic.dao.IDaoImpl;
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
public class IMCControllerTestConfig extends WebMvcConfigurerAdapter {
         
    @Bean
    public IMCService imcService() {
        return Mockito.mock(IMCService.class);
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
    public RestTemplate RestTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public JsonUtil jsonUtil() {
        return Mockito.mock(JsonUtil.class);
    }
    
    @Bean
    public MappedEntityIdResolver Resolver() {
        return new MappedEntityIdResolver();
    }
    
    @Bean
    public IMCController imcController() {
        return new IMCController();
    }
    
    @Bean
    public IDao Dao() {
        return Mockito.mock(IDaoImpl.class);
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
    
}
