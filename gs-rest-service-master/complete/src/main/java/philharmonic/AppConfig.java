/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import philharmonic.dao.IDao;
import philharmonic.dao.PostgresDao;

/**
 *
 * @author Kookie
 */
@Configuration
public class AppConfig {

    @Bean
    public IDao PostgresDao() {
        return new PostgresDao();
    }
}
