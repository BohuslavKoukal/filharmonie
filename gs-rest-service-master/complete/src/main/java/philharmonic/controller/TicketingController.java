/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import static philharmonic.resources.StringConstants.*;

/**
 *
 * @author Kookie
 */
@Controller
@RequestMapping(addressTicketingWrapper)
public class TicketingController {
    
    @RequestMapping(value = resourceAddressCPAction, method=RequestMethod.POST)
        @ResponseBody
    public ResponseEntity<String> postCPAction(@RequestBody String cpAction) {
            // logic here, calling db procedures
        return new ResponseEntity<String>(getRandomIdJSON(), HttpStatus.OK);
    }
    
    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putCPAction(@RequestBody String cpAction) {
            // logic here, saving to database
        return new ResponseEntity<String>(getRandomIdJSON(), HttpStatus.OK);
    }
}
