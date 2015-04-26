/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.utilities;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import philharmonic.model.Message;
import static philharmonic.resources.StringConstants.*;

/**
 *
 * @author Kookie
 */
public class MessageSender {

    
    private RestTemplate rt;
    
    @Autowired
    private AddressesParser parser;
    
    public MessageSender() {
        rt = new RestTemplate();
    }

    public ResponseEntity<String> sendMessage(Message message, String body) {
        String target = message.getTargetComponentName();
        String URI;
        URI = parser.getAddressForComponent(target)
                + "/" + target + "/" + message.getResourceName();

        HttpEntity entity = new HttpEntity(body, createHeaders(target));
        if (message.getAction().equals(namePOSTAction)) {
            return rt.exchange(URI, HttpMethod.POST, entity, String.class);
        }
        if (message.getAction().equals(namePUTAction)) {
            return rt.exchange(URI, HttpMethod.PUT, entity, String.class);
        }

        return null;
    }

    public ResponseEntity<String> sendMessage(Message message, int id) {
        String target = message.getTargetComponentName();
        String URI;
        URI = parser.getAddressForComponent(target)
                + "/" + target + "/" + message.getResourceName() + "/" + id;
        HttpEntity entity = new HttpEntity(createHeaders(target));
        if (message.getAction().equals(nameDELETEAction)) {
            return rt.exchange(URI, HttpMethod.DELETE, entity, String.class);
        }
        if (message.getAction().equals(nameGETAction)) {
            return rt.exchange(URI, HttpMethod.GET, entity, String.class);
        }
        return null;
    }
    
    private MultiValueMap createHeaders(String component) {
        String plainCreds = parser.getAuthorizationForComponent(component);
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }
}
