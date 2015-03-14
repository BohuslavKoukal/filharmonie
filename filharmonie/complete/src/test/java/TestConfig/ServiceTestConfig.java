/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import philharmonic.dao.IDao;
import philharmonic.dao.IDaoImpl;

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

}
