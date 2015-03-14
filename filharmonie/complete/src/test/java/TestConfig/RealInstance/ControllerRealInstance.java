/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestConfig.RealInstance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import philharmonic.controller.IMCController;

/**
 *
 * @author Kookie
 */
@Configuration
public class ControllerRealInstance {
            @Bean
    public IMCController imcController() {
        return new IMCController();
    }
}
