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
import philharmonic.utilities.MappedEntityIdResolver;

/**
 *
 * @author Kookie
 */
@Configuration
public class DaoTestConfig {
    
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
