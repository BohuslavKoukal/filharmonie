package philharmonic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import philharmonic.model.CPAction;
import static philharmonic.resources.StringConstants.*;

/**
 *
 * @author Kookie
 */
@Controller
@RequestMapping(addressOrchestrWrapper)
public class OrchestrController {
    private RestTemplate rt = new RestTemplate();

    @RequestMapping(method=RequestMethod.GET)
    public String postCPAction()
    {        
        return viewOrchestr;
    }

    @RequestMapping(method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postCPAction(@ModelAttribute CPAction Action)
    {   
        String address = serverAddress + addressMiddleComponent + resourceAddressCPAction;
        rt.postForEntity(address, Action, ResponseEntity.class);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(method=RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putCPAction(@ModelAttribute CPAction Action)
    {   
        String address = serverAddress + addressMiddleComponent + resourceAddressCPAction;
        rt.put(address, Action);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
        // This is here only for the reason of simulating UI inputs
    @RequestMapping(value = "/put", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> putCPActionFromUI(@ModelAttribute CPAction Action)
    {   
        putCPAction(Action);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    
}
