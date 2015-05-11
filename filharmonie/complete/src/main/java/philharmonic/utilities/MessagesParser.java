/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import philharmonic.model.Message;
import static philharmonic.resources.LoggingConstants.*;

/**
 *
 * @author Kookie
 */
public class MessagesParser {

    File file;
    
    DocumentBuilder dBuilder;
    Document doc;
    
    private static final Logger logger = Logger.getLogger(MessagesParser.class);
    
    public MessagesParser(String path) {  
        initialize(path);        
    }

    private void initialize(String path) {
        try {
            file = new File(path);
            dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = dBuilder.parse(file);  
        }
        catch(Exception e) {
            logger.error(exceptionThrown + "While initializing MessagesParser - either cannot find \n" + path + " or there is some problem with the file.");
        }    
    }

    public List<Message> getRequiredMessagesFor(String resourceName, String actionName) {
        try {
            NodeList resources = doc.getElementsByTagName("resource");
            List<Message> ret = new ArrayList<>();
            Node resource = getFirstElementByAttributeName(resources, resourceName);
            NodeList actions = resource.getChildNodes();
            Element action = (Element) getFirstElementByAttributeName(actions, actionName);
            NodeList targets = action.getElementsByTagName("target");
            for (int i = 0; i < targets.getLength(); i++) {
                Element targetChild = (Element) targets.item(i);
                String targetComponentName = targetChild.getElementsByTagName("name").item(0).getTextContent();
                List<String> neededIds = new ArrayList<>();
                NodeList neededIdsNodes = targetChild.getElementsByTagName("needsIdOf");
                for (int j = 0; j < neededIdsNodes.getLength(); j++) {
                    neededIds.add(neededIdsNodes.item(j).getTextContent());
                }
                ret.add(assembleMessage(actionName, resourceName, targetComponentName, neededIds));
            }
            return ret;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    private Node getFirstElementByAttributeName(NodeList list, String nameValue) {
        for (int i = 0; i < list.getLength(); i++) {
            Node tempNode = list.item(i);
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                Element tempElement = (Element) tempNode;
                if (tempElement.getAttribute("name").equalsIgnoreCase(nameValue)) {
                    return tempNode;
                }
            }
        }
        return null;
    }

    private Message assembleMessage(String actionName,
            String resourceName,
            String targetComponentName,
            List<String> neededIds) {
        return new Message(actionName, resourceName, targetComponentName, neededIds);
    }
    
    public void setFilePath(String filePath) {
        initialize(filePath);
    }

}
