/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.controller;

import philharmonic.service.IMCService;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import static philharmonic.resources.StringConstants.*;
import static philharmonic.resources.LoggingConstants.*;

/**
 *
 * @author Kookie
 */
@Controller
@RequestMapping(addressMiddleComponent)
public class IMCController {

    private static final Logger logger = Logger.getLogger(IMCController.class);

    @Autowired
    private IMCService service;

    /**
     * **********************************************************************
     */
    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postCPAction(@RequestBody String actionJSON) {
            logger.info(invokingCPActionPOST + actionJSON);
            String sourceName = orchestrComponentName;
            String resource = CPAction;
            return service.processPOSTRequest(actionJSON, sourceName, resource);
    }

    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putCPAction(@RequestBody String actionJSON) {
            logger.info(invokingCPActionPUT + actionJSON);
            String sourceName = orchestrComponentName;
            String resource = CPAction;
            return service.processPUTRequest(actionJSON, sourceName, resource);
    }

    /**
     * **********************************************************************
     */
    @RequestMapping(value = resourceAddressExternalAction, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postExternalAction(@RequestBody String actionJSON) {
        logger.info(invokingExternalActionPOST + actionJSON);
        String sourceName = rudolfComponentName;
        String resource = ExternalAction;
        return service.processPOSTRequest(actionJSON, sourceName, resource);
    }

    @RequestMapping(value = resourceAddressExternalAction, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putExternalAction(@RequestBody String actionJSON) {
        logger.info(invokingExternalActionPUT + actionJSON);
        String sourceName = rudolfComponentName;
        String resource = ExternalAction;
        return service.processPUTRequest(actionJSON, sourceName, resource);
    }
    
    @RequestMapping(value = {resourceAddressExternalAction + "/{id}", resourceAddressExternalAction + "/", resourceAddressExternalAction}, method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteExternalAction
        (@PathVariable("id") String id, @RequestBody(required=false) String actionJSON) {
        logger.info(invokingExternalActionDELETE + " with id " + id + " and body " + actionJSON);
        String sourceName = rudolfComponentName;
        String resource = ExternalAction;
        return service.processDELETERequest(id, actionJSON, sourceName, resource);        
    }
    

    /**
     * *************************************************************************************************
     */
    @RequestMapping(value = resourceAddressItem, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postItem(@RequestBody String itemJSON) {
        logger.info(invokingItemPOST + itemJSON);
        String sourceName = rudolfComponentName;
        String resource = Item;
        return service.processPOSTRequest(itemJSON, sourceName, resource);
    }

    @RequestMapping(value = resourceAddressItem, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putItem(@RequestBody String itemJSON) {
        logger.info(invokingItemPUT + itemJSON);
        String sourceName = rudolfComponentName;
        String resource = Item;
        return service.processPUTRequest(itemJSON, sourceName, resource);
    }

    @RequestMapping(value = {resourceAddressItem + "/{id}"}, method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteItem
        (@PathVariable("id") String id, @RequestBody(required=false) String itemJson) {
        logger.info(invokingItemDELETE + " with id " + id);
        String sourceName = rudolfComponentName;
        String resource = Item;
        return service.processDELETERequest(id, itemJson, sourceName, resource);        
    }
    
}
