/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig.RealInstance;

import javax.sql.DataSource;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import philharmonic.dao.IDao;
import philharmonic.dao.IDaoImpl;
import philharmonic.utilities.MappedEntityIdResolver;

/**
 *
 * @author Kookie
 */
@Configuration
public class IDaoImplRealInstance {
    @Bean
    public IDao Dao() {
        return new IDaoImpl();
    }
    
    @Bean
    public MappedEntityIdResolver resolver() {
        return new MappedEntityIdResolver();      

    }
    
}
