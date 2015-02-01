/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.utilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import philharmonic.model.Message;

/**
 *
 * @author Kookie
 */
public class MessagesParser {
File file;
DocumentBuilder dBuilder;
Document doc;

private void initialize() throws ParserConfigurationException, SAXException, IOException {
    file = new File("src/main/resources/components/Messages.xml");
    dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    doc = dBuilder.parse(file);
}

 
  public List<Message> getRequiredMessagesFor(String resourceName, String actionName) {
    try { 
        initialize(); 
	NodeList resources = doc.getElementsByTagName("resource");
        List<Message> ret = new ArrayList<Message>();
        Node resource = getFirstElementByAttributeName(resources, resourceName);
        NodeList actions = resource.getChildNodes();
        Element action = (Element) getFirstElementByAttributeName(actions, actionName);
        NodeList targets = action.getElementsByTagName("target");
        for (int i = 0; i < targets.getLength(); i++) {
            Element targetChild = (Element)targets.item(i);
            String targetComponentName = targetChild.getElementsByTagName("name").item(0).getTextContent();
            ret.add(assembleMessage(actionName, resourceName, targetComponentName ));
        }
        return ret;
        
        
 
    } catch (Exception e) {
	System.out.println(e.getMessage());
        return null;
    }
 
  }
 

    private Node getFirstElementByAttributeName(NodeList list, String nameValue) {
        // TODO: check not first attribute but attribute according its name
        for (int i = 0; i < list.getLength(); i++) {
            Node tempNode = list.item(i);
            if(tempNode.getNodeType() == Node.ELEMENT_NODE) {
                if(tempNode.getAttributes().item(0).getTextContent().equalsIgnoreCase(nameValue)) {
                    return tempNode;
                }
            }
        }
        return null;
    }
    
    private Message assembleMessage(String actionName, String resourceName, String targetComponentName) {
        return new Message(actionName, resourceName, targetComponentName);
    }
}
