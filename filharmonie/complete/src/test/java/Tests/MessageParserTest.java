/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import TestConfig.MessagesParserTestConfig;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import philharmonic.model.Message;
import philharmonic.utilities.MessagesParser;

/**
 *
 * @author Kookie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MessagesParserTestConfig.class})
@WebAppConfiguration
public class MessageParserTest {
    
    @Autowired
    MessagesParser testee;
    
    Logger loggerMock;
    
    @Before
    public void setUp() {
        testee.setFilePath("src/main/resources/components/MessagesTest.xml");
        loggerMock = Mockito.mock(Logger.class);
    }
    
    @Test
    public void getRequiredMessages_returnsCorrectMessages1() throws JSONException {
        List<Message> messages = testee.getRequiredMessagesFor("CPAction", "POST");
        assertEquals(3, messages.size());
        Message message0 = messages.get(0);
        Message message1 = messages.get(1);
        Message message2 = messages.get(2);
        assertEquals("POST", message0.getAction());
        assertEquals("CPAction", message0.getResourceName());
        assertEquals("rudolf", message0.getTargetComponentName());
        assertEquals(0, message0.getNeededIds().size());
        
        assertEquals("POST", message1.getAction());
        assertEquals("CPAction", message1.getResourceName());
        assertEquals("ticketing", message1.getTargetComponentName());
        assertEquals(1, message1.getNeededIds().size());
        assertEquals("rudolf", message1.getNeededIds().get(0));
        
        assertEquals("POST", message2.getAction());
        assertEquals("CPAction", message2.getResourceName());
        assertEquals("web", message2.getTargetComponentName());
        assertEquals(2, message2.getNeededIds().size());
        assertEquals("ticketing", message2.getNeededIds().get(0));
        assertEquals("rudolf", message2.getNeededIds().get(1));
        
        Mockito.verifyZeroInteractions(loggerMock);
    }
    
    @Test
    public void getRequiredMessages_returnsCorrectMessages2() throws JSONException {
        List<Message> messages = testee.getRequiredMessagesFor("ExternalAction", "PUT");
        assertEquals(3, messages.size());
        Message message0 = messages.get(0);
        Message message1 = messages.get(1);
        Message message2 = messages.get(2);
        assertEquals("PUT", message0.getAction());
        assertEquals("ExternalAction", message0.getResourceName());
        assertEquals("ticketing", message0.getTargetComponentName());
        assertEquals(0, message0.getNeededIds().size());
        
        assertEquals("PUT", message1.getAction());
        assertEquals("ExternalAction", message1.getResourceName());
        assertEquals("web", message1.getTargetComponentName());
        assertEquals(0, message0.getNeededIds().size());
        
        assertEquals("PUT", message2.getAction());
        assertEquals("ExternalAction", message2.getResourceName());
        assertEquals("mailer", message2.getTargetComponentName());
        assertEquals(0, message0.getNeededIds().size());
        
        Mockito.verifyZeroInteractions(loggerMock);
    }
    
    
}
