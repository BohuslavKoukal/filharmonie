/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Kookie
 */
@ComponentScan
@EnableAutoConfiguration
public class Application 
{
    
    
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
