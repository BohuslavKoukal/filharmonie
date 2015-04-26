/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import philharmonic.service.IMCService;

import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import philharmonic.dao.IDao;
import philharmonic.dao.IDaoImpl;
import philharmonic.utilities.AddressesParser;
import philharmonic.utilities.MappedEntityIdResolver;
import philharmonic.utilities.JsonUtil;
import philharmonic.utilities.MessageSender;
import philharmonic.utilities.MessagesParser;
/**
 *
 * @author Kookie
 */

@Configuration
public class JsonUtilTestConfig {
    
    @Bean
    public IMCService imcService() {
        return Mockito.mock(IMCService.class);
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
    public JsonUtil JsonUtil() {
        return new JsonUtil();
    }
    
    @Bean
    public IDao Dao() {
        return Mockito.mock(IDaoImpl.class);
    }
    
    @Bean
    public AddressesParser AddressesParser() throws Exception {
        return Mockito.mock(AddressesParser.class);
    }
    
    @Bean
    public MessagesParser parser() {
        return Mockito.mock(MessagesParser.class);
    }
    
@Bean
    public MessageSender sender() {
        return Mockito.mock(MessageSender.class);
    }
    
}
