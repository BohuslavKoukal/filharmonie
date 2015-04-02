/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.utilities;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import philharmonic.model.Message;
import static philharmonic.resources.StringConstants.*;
/**
 *
 * @author Kookie
 */


public class MessageSender {

    private RestTemplate rt;

    public MessageSender() {
        rt = new RestTemplate();
    }
    
    public MessageSender(RestTemplate rt) {
        this.rt = rt;
    }

    public ResponseEntity<String> sendMessage(Message message, String body) {
        String target = message.getTargetComponentName();
        String URI = "";
        try {
            URI = new AddressesParser("Components.xml").getAddressForComponent(target) +
                    "/" + target + "/" + message.getResourceName();
        }
        catch(ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }        
        HttpEntity entity = new HttpEntity(body);
        if (message.getAction().equals(namePOSTAction)) {
            return rt.exchange(URI, HttpMethod.POST, entity, String.class);
        }
        if (message.getAction().equals(namePUTAction)) {
            return rt.exchange(URI, HttpMethod.PUT, entity, String.class);
        }

        return null;
    }
}
