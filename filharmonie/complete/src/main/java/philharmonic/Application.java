/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


/**
 *
 * @author Kookie
 */
@ComponentScan
@EnableAutoConfiguration
public class Application 
{   
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
    

}
