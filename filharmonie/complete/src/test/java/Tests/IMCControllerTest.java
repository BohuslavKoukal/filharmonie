package Tests;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc; 
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.List;
import org.mockito.ArgumentMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import philharmonic.model.MappedEntity;
import philharmonic.model.MappedResource;
import philharmonic.model.Message;
import philharmonic.utilities.JsonUtil;
import philharmonic.utilities.MessageSender;
import philharmonic.utilities.MessagesParser;
import philharmonic.resources.mapping.CPActionEnumMapping;
import philharmonic.service.IMCService;
import TestConfig.ControllerTestConfig;
import TestBuilders.JsonBuilder;
import TestBuilders.MessagesBuilder;
import TestConfig.DaoTestConfig;
import TestConfig.RealInstance.ControllerRealInstance;
import TestConfig.RealInstance.ResolverRealInstance;
import TestConfig.ServiceTestConfig;

import static philharmonic.resources.StringConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author Kookie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ControllerTestConfig.class,
    ServiceTestConfig.class,
    DaoTestConfig.class,
    ControllerRealInstance.class,
    ResolverRealInstance.class})
@WebAppConfiguration
public class IMCControllerTest {
    
    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private IMCService serviceMock;
    
    @Autowired
    private MessagesParser parserMock;
    
    @Autowired
    private MessageSender senderMock;

    @Autowired
    private JsonUtil jsonUtilMock;
    
    public IMCControllerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        reset(serviceMock);
        reset(senderMock);
        reset(parserMock);
        reset(jsonUtilMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    
    @After
    public void tearDown() {
    }
    
    // CPAction POST
    
    @Test
    public void postCPAction_IdIs0_returnsBadRequestAndDoesNotCreateRequestSendsNoMessages() throws Exception {
        String json = new JsonBuilder(12)
                .withId(0)
                .build();

        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))                
                .andExpect(status().isBadRequest());    
        verifyNoMoreInteractions(serviceMock);
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
    }
    
    @Test
    public void postCPAction_noId_returnsBadRequestAndDoesNotCreateRequestAndSendsNoMessages() throws Exception {
        String json = new JsonBuilder(12)
                .withNoId();

        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))                
                .andExpect(status().isBadRequest());  
        verifyNoMoreInteractions(serviceMock);
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
    }
    
    @Test
    public void postCPAction_invalidJson_returnsBadRequestAndDoesNotCreateEntityAndSendsNoMessages() throws Exception {
        String json = JsonBuilder.invalidate(new JsonBuilder(12).build());                

        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(serviceMock, never()).saveMappedResource(any(MappedResource.class));
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
    }
    
    @Test
    public void postCPAction_JsonWithExistingId_returnsConflictAndDoesNotCreateEntityAndSendsNoMessages() throws Exception {
        String json = new JsonBuilder(12).build();
        when(serviceMock.getMappedEntity(eq(12), anyString(), anyString()))
                .thenReturn(new MappedEntity());
        
        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(serviceMock, never()).saveMappedResource(any(MappedResource.class));
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
    }
    
    @Test
    public void postCPAction_JsonWithNonExistingId_parsesMessagesAndSendsRightMessagesAndReturnsOk() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePOSTAction);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePOSTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.nullResourceIdInJSON(eq(json)))
                .thenReturn(json);
        when(jsonUtilMock.addResourceIdToJSON(eq(json), anyString(), anyInt()))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK));
        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(parserMock, times(1)).getRequiredMessagesFor(resourceNameCPAction, namePOSTAction);
        verify(jsonUtilMock, times(mockedMessages.size())).shiftEnumIdsInJSON(eq(json), anyString(), anyString());
        verify(jsonUtilMock, times(mockedMessages.size())).nullResourceIdInJSON(eq(json));        
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());        
        verify(jsonUtilMock, times(1)).addResourceIdToJSON(eq(json), anyString(), anyInt());
    }
    
    @Test
    public void postCPAction_JsonWithNonExistingId_CreatesRightMappedEntity() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePOSTAction);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePOSTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.nullResourceIdInJSON(eq(json)))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenReturn(new ResponseEntity<>("{\"id\" : \"3\" }", HttpStatus.OK));
        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        MappedResource res = new MappedResource();
        res.idOrchestr = 3;
        res.idRudolf = 3;
        verify(serviceMock, times(1)).saveMappedResource(argThat(new ObjectEqualityArgumentMatcher<>(res)));
    }
    
    @Test
    public void postCPAction_WithTargetConflict_ReturnsConflictAndSendsMessages() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePOSTAction);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePOSTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.nullResourceIdInJSON(eq(json)))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenReturn(new ResponseEntity<String>(HttpStatus.CONFLICT));
        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());  
    }
    
    @Test
    public void postCPAction_WithTargetExceptionThrown_ReturnsConflictAndSendsMessages() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePOSTAction);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePOSTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.nullResourceIdInJSON(eq(json)))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED));
        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());  
    }
    
    @Test
    public void postCPAction_JsonWithEnumMappingConflictOrEnumValue0_SendMessagesAndReturnsConflict() throws Exception {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePOSTAction);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePOSTAction))
                .thenReturn(mockedMessages);        
        String jsonError = new JsonBuilder(12).withPlaceId(0).build();
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(jsonError), anyString(), anyString()))
                .thenReturn(jsonError);
        when(jsonUtilMock.nullResourceIdInJSON(eq(jsonError)))
                .thenReturn(jsonError);
        when(senderMock.sendMessage(any(Message.class), eq(jsonError)))
                .thenReturn(new ResponseEntity<String>(HttpStatus.CONFLICT));
        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(jsonError)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(serviceMock, times(1)).saveMappedResource(any(MappedResource.class));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());  
    }
    
    
    // CPACtion PUT
    
    @Test
    public void putCPAction_IdIs0_returnsBadRequestAndSendsNoMessages() throws Exception {
        String json = new JsonBuilder(12)
                .withId(0)
                .build();

        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))                
                .andExpect(status().isBadRequest());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
    }
    
    @Test
    public void putCPAction_noId_returnsBadRequestAndSendsNoMessages() throws Exception {
        String json = new JsonBuilder(12)
                .withNoId();

        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))                
                .andExpect(status().isBadRequest());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
    }   
    
    
    @Test
    public void putCPAction_invalidJson_returnsBadRequestAndSendsNoMessages() throws Exception {
        String json = JsonBuilder.invalidate(new JsonBuilder(12).build());                

        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
    }
    
    @Test
    public void putCPAction_JsonWithNonExistingId_invokesPOSTAndReturnsCreated() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePOSTAction);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePOSTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.nullResourceIdInJSON(eq(json)))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK));
        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());        
    }
    
    @Test
    public void putCPAction_JsonWithExistingId_parsesMessagesAndSendsRightMessagesAndReturnsOk() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePUTAction);
        MappedEntity me = new MappedEntity();
        me.id = 12; me.idOrchestr = 12; me.idRudolf = 12;
        when(serviceMock.getMappedEntity(12, orchestrComponentName, CPActionEnumMapping.CPAction.getTableName()))
                .thenReturn(me);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePUTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.shiftResourceIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenReturn(new ResponseEntity<String>(HttpStatus.OK));
        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(parserMock, times(1)).getRequiredMessagesFor(resourceNameCPAction, namePUTAction);
        verify(jsonUtilMock, times(mockedMessages.size())).shiftEnumIdsInJSON(eq(json), anyString(), anyString());
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, orchestrComponentName, mockedMessages.get(0).getTargetComponentName());
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, orchestrComponentName, mockedMessages.get(1).getTargetComponentName());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());        
        verify(jsonUtilMock, times(0)).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(serviceMock, times(0)).saveMappedResource(any(MappedResource.class));
    }    
    
    
    @Test
    public void putCPAction_WithTargetConflict_ReturnsConflictAndSendsAllMessages() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePUTAction);
        MappedEntity me = new MappedEntity();
        me.id = 12; me.idOrchestr = 12; me.idRudolf = 12;
        when(serviceMock.getMappedEntity(12, orchestrComponentName, CPActionEnumMapping.CPAction.getTableName()))
                .thenReturn(me);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePUTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.shiftResourceIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenReturn(new ResponseEntity<String>(HttpStatus.CONFLICT));
        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());  
    }
    
    @Test
    public void putCPAction_WithTargetExceptionThrown_ReturnsConflictAndSendsMessages() throws Exception {
        String json = new JsonBuilder(12).build(); 
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePUTAction);
        MappedEntity me = new MappedEntity();
        me.id = 12; me.idOrchestr = 12; me.idRudolf = 12;
        when(serviceMock.getMappedEntity(12, orchestrComponentName, CPActionEnumMapping.CPAction.getTableName()))
                .thenReturn(me);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePUTAction))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.shiftResourceIdsInJSON(eq(json), anyString(), anyString()))
                .thenReturn(json);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED));
        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());  
    }
    
    @Test
    public void putCPAction_JsonWithEnumMappingConflictOrEnumValue0_SendMessagesAndReturnsConflict() throws Exception {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(namePUTAction);
        MappedEntity me = new MappedEntity();
        me.id = 12; me.idOrchestr = 12; me.idRudolf = 12;
        when(serviceMock.getMappedEntity(12, orchestrComponentName, CPActionEnumMapping.CPAction.getTableName()))
                .thenReturn(me);
        when(parserMock.getRequiredMessagesFor(resourceNameCPAction, namePUTAction))
                .thenReturn(mockedMessages);        
        String jsonError = new JsonBuilder(12).withPlaceId(0).build();
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(jsonError), anyString(), anyString()))
                .thenReturn(jsonError);
        when(jsonUtilMock.shiftResourceIdsInJSON(eq(jsonError), anyString(), anyString()))
                .thenReturn(jsonError);
        when(senderMock.sendMessage(any(Message.class), eq(jsonError)))
                .thenReturn(new ResponseEntity<String>(HttpStatus.CONFLICT));
        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(jsonError)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), anyString());
    }
    
    
    
    
    
    
    
    
    private class ObjectEqualityArgumentMatcher<T> extends ArgumentMatcher<T> {
    T thisObject;

    public ObjectEqualityArgumentMatcher(T thisObject) {
        this.thisObject = thisObject;
    }

    @Override
    public boolean matches(Object entity) {
            return thisObject.equals(entity);        
    }
}
    
    
//     @Test
//    public void findAll_TodosFound_ShouldReturnFoundTodoEntries() throws Exception {
//        MappedEntity first = new MappedEntityBuilder()
//                .withId(1)
//                .withIdWeb(2)
//                .build();
//        MappedEntity second = new MappedEntityBuilder()
//                .withId(4)
//                .withIdWeb(9)
//                .build();
// 
//        when(serviceMock.getMappedEntity(1, null, null)).thenReturn(first);
// 
//        mockMvc.perform(get("/api/todo"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[0].description", is("Lorem ipsum")))
//                .andExpect(jsonPath("$[0].title", is("Foo")))
//                .andExpect(jsonPath("$[1].id", is(2)))
//                .andExpect(jsonPath("$[1].description", is("Lorem ipsum")))
//                .andExpect(jsonPath("$[1].title", is("Bar")));
// 
//        verify(todoServiceMock, times(1)).findAll();
//        verifyNoMoreInteractions(todoServiceMock);
//    }
    
    
    
    
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}