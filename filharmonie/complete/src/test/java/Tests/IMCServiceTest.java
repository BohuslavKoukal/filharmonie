/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import TestBuilders.JsonCPActionBuilder;
import TestBuilders.MessagesBuilder;
import TestConfig.ServiceTestConfig;
import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import philharmonic.dao.IDao;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import philharmonic.model.MappedEntity;
import philharmonic.model.Message;
import static philharmonic.resources.ErrorMessages.*;
import static philharmonic.resources.StringConstants.*;
import static philharmonic.resources.mapping.EnumMapping.*;
import philharmonic.service.IMCService;
import philharmonic.utilities.JsonUtil;
import philharmonic.utilities.MessageSender;
import philharmonic.utilities.MessagesParser;

/**
 *
 * @author Kookie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestConfig.class})
@WebAppConfiguration
public class IMCServiceTest {

    @Autowired
    private IDao daoMock;

    @Autowired
    private MessagesParser parserMock;

    @Autowired
    private MessageSender senderMock;

    @Autowired
    private JsonUtil jsonUtilMock;

    @Autowired
    private IMCService testee;

    public IMCServiceTest() {
    }

    @Before
    public void setUp() {
        reset(daoMock);
        reset(parserMock);
        reset(senderMock);
        reset(jsonUtilMock);
    }

    /**
     * ***********************************************************************
     */
    // Process POST
    @Test
    public void processPOST_NewEntity_SendsCorrectMessagesAndSavesEntityAndReturnsCREATED() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CREATED);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePOSTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(2)).nullResourceIdInJSON(json, idName);
        verify(jsonUtilMock, times(1)).addResourceIdToJSON(json, rudolfComponentName, 13);
        MappedEntity savedEntity = new MappedEntity();
        savedEntity.idOrchestr = 12;
        savedEntity.idTicketing = 13;
        savedEntity.idRudolf = 13;
        verify(daoMock, times(1)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedEntity)), eq(CPAction));
    }

    @Test
    public void processPOST_ExistingEntity_SendsNoMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPOST_UnmappedEnum_GetsEnumFromSourcePostsEnumToTargetSavesEnumAndThenSendsOriginalMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetupForNonExistingEnums(json);
        mockSetup(false, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));
        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CREATED);
        Message getPlace = new Message(nameGETAction, place.getTableName(), orchestrComponentName, null);
        Message postPlaceToRudolf = new Message(namePOSTAction, place.getTableName(), rudolfComponentName, null);
        Message postPlaceToTicketing = new Message(namePOSTAction, place.getTableName(), ticketingComponentName, null);
        verify(senderMock, times(2)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(getPlace)), eq(12), anyString());
        verify(senderMock, times(1)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToRudolf)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToTicketing)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        MappedEntity savedPlace = new MappedEntity();
        savedPlace.idOrchestr = 12;
        verify(daoMock, times(2)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedPlace)), eq(place.getTableName()));
        verify(daoMock, times(1)).update(place.getTableName(), rudolfComponentName, 13, orchestrComponentName, 12);
        verify(daoMock, times(1)).update(place.getTableName(), ticketingComponentName, 13, orchestrComponentName, 12);
    }

    @Test
    public void processPOST_WithId0_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).withId(0).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorId0());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPOST_WithNoId_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).withNoId();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorInvalidId());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPOST_WithInvalidBody_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = "blabla";
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorInvalidJson());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPOST_WithTargetConflict_SendsCorrectMessagesAndSavesEntityAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.CONFLICT));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePOSTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(2)).nullResourceIdInJSON(json, idName);
        verify(jsonUtilMock, times(1)).addResourceIdToJSON(json, rudolfComponentName, 0);
        MappedEntity savedEntity = new MappedEntity();
        savedEntity.idOrchestr = 12;
        savedEntity.idTicketing = 0;
        savedEntity.idRudolf = 0;
        verify(daoMock, times(1)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedEntity)), eq(CPAction));
    }

    @Test
    public void processPOST_WithTargetResourceAccessException_SendsCorrectMessagesAndSavesEntityAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResourceAccessException("Component not running."));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePOSTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(2)).nullResourceIdInJSON(json, idName);
        verify(jsonUtilMock, times(1)).addResourceIdToJSON(json, rudolfComponentName, 0);
        MappedEntity savedEntity = new MappedEntity();
        savedEntity.idOrchestr = 12;
        savedEntity.idTicketing = 0;
        savedEntity.idRudolf = 0;
        verify(daoMock, times(1)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedEntity)), eq(CPAction));
    }

    @Test
    public void processPOST_WithTargetHttpClientErrorException_SendsCorrectMessagesAndSavesEntityAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new HttpClientErrorException(HttpStatus.PAYMENT_REQUIRED));

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePOSTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(2)).nullResourceIdInJSON(json, idName);
        verify(jsonUtilMock, times(1)).addResourceIdToJSON(json, rudolfComponentName, 0);
        MappedEntity savedEntity = new MappedEntity();
        savedEntity.idOrchestr = 12;
        savedEntity.idTicketing = 0;
        savedEntity.idRudolf = 0;
        verify(daoMock, times(1)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedEntity)), eq(CPAction));
    }

    @Test
    public void processPOST_WithTargetRuntimeException_SendsCorrectMessagesAndSavesEntityAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new RuntimeException());

        ResponseEntity res = testee.processPOSTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePOSTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(2)).nullResourceIdInJSON(json, idName);
        verify(jsonUtilMock, times(1)).addResourceIdToJSON(json, rudolfComponentName, 0);
        MappedEntity savedEntity = new MappedEntity();
        savedEntity.idOrchestr = 12;
        savedEntity.idTicketing = 0;
        savedEntity.idRudolf = 0;
        verify(daoMock, times(1)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedEntity)), eq(CPAction));
    }

    // **********************************************************************************/
    //Process PUT request
    @Test
    public void processPUT_ExistingEntity_SendsCorrectMessagesAndReturnsOK() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.OK);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePUTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPUT_NewEntity_SendsPOSTMessagesAndReturnsCREATED() throws JSONException, IOException {
        List<Message> mockedPUTMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        List<Message> mockedPOSTMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedPUTMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CREATED);
        verify(senderMock, times(1)).sendMessage(eq(mockedPOSTMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedPOSTMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePOSTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(2)).nullResourceIdInJSON(json, idName);
        verify(jsonUtilMock, times(1)).addResourceIdToJSON(json, rudolfComponentName, 13);
        MappedEntity savedEntity = new MappedEntity();
        savedEntity.idOrchestr = 12;
        savedEntity.idTicketing = 13;
        savedEntity.idRudolf = 13;
        verify(daoMock, times(1)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedEntity)), eq(CPAction));
    }

    @Test
    public void processPUT_UnmappedEnum_GetsEnumFromSourcePostsEnumToTargetSavesEnumAndThenSendsOriginalMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(false, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));
        mockSetupForNonExistingEnums(json);
        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.OK);
        Message getPlace = new Message(nameGETAction, place.getTableName(), orchestrComponentName, null);
        Message postPlaceToRudolf = new Message(namePOSTAction, place.getTableName(), rudolfComponentName, null);
        Message postPlaceToTicketing = new Message(namePOSTAction, place.getTableName(), ticketingComponentName, null);
        verify(senderMock, times(2)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(getPlace)), eq(12), anyString());
        verify(senderMock, times(1)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToRudolf)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToTicketing)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        MappedEntity savedPlace = new MappedEntity();
        savedPlace.idOrchestr = 12;
        verify(daoMock, times(2)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedPlace)), eq(place.getTableName()));
        verify(daoMock, times(1)).update(place.getTableName(), rudolfComponentName, 13, orchestrComponentName, 12);
        verify(daoMock, times(1)).update(place.getTableName(), ticketingComponentName, 14, orchestrComponentName, 12);
    }

    @Test
    public void processPUT_WithId0_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).withId(0).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorId0());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPUT_WithNoId_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).withNoId();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorInvalidId());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPUT_WithInvalidBody_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = "blabla";
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorInvalidJson());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPUT_WithTargetConflict_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.CONFLICT));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePUTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPUT_WithTargetResourceAccessException_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResourceAccessException("Component not running."));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePUTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPUT_WithTargetHttpClientErrorException_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new HttpClientErrorException(HttpStatus.GONE));

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePUTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processPUT_WithTargetException_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePUTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new RuntimeException());

        ResponseEntity res = testee.processPUTRequest(json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, namePUTAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftResourceIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    // **********************************************************************************/
    //Process DELETE request
    @Test
    public void processDELETE_ExistingEntity_SendsCorrectMessagesDeletesEntityAndReturnsOK() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).withNoId();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.OK);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(0), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(0), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, nameDELETEAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, times(1)).delete(12, CPAction, orchestrComponentName);
    }

    @Test
    public void processDELETE_WithNoBody_SendsCorrectMessagesDeletesEntityAndReturnsOK() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        mockSetup(true, mockedMessages, null);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processDELETERequest("12", null, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.OK);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(0), eq(""));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(0), eq(""));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, nameDELETEAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, times(1)).delete(12, CPAction, orchestrComponentName);
    }

    @Test
    public void processDELETE_NonExistingEntity_SendsNoMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).withNoId();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).delete(anyInt(), anyString(), anyString());
    }

    @Test
    public void processDELETE_UnmappedEnum_GetsEnumFromSourcePostsEnumToTargetSavesEnumAndThenSendsOriginalMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).withNoId();
        mockSetup(false, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));
        mockSetupForNonExistingEnums(json);

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.OK);
        Message getPlace = new Message(nameGETAction, place.getTableName(), orchestrComponentName, null);
        Message postPlaceToRudolf = new Message(namePOSTAction, place.getTableName(), rudolfComponentName, null);
        Message postPlaceToTicketing = new Message(namePOSTAction, place.getTableName(), ticketingComponentName, null);
        verify(senderMock, times(2)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(getPlace)), eq(12), anyString());
        verify(senderMock, times(1)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToRudolf)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(0), eq(json));
        verify(senderMock, times(1)).sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToTicketing)), anyString());
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(0), eq(json));
        verifyNoMoreInteractions(senderMock);
        MappedEntity savedPlace = new MappedEntity();
        savedPlace.idOrchestr = 12;
        verify(daoMock, times(2)).create(argThat(new ObjectEqualityArgumentMatcher<>(savedPlace)), eq(place.getTableName()));
        verify(daoMock, times(1)).update(place.getTableName(), rudolfComponentName, 13, orchestrComponentName, 12);
        verify(daoMock, times(1)).update(place.getTableName(), ticketingComponentName, 14, orchestrComponentName, 12);
    }

    @Test
    public void processDELETE_WithId0_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).withNoId();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processDELETERequest("0", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorId0());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processDELETE_WithNonNumericId_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).withNoId();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processDELETERequest("huh", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorId0());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processDELETE_WithNoId_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        mockSetup(true, mockedMessages, null);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processDELETERequest(null, null, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorInvalidId());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processDELETE_WithInvalidBody_SendsNoMessagesAndReturnsBADREQUESTWithCorrectMessage() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = "blabla";
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.OK));

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(res.getBody(), errorInvalidJson());
        verify(senderMock, never()).sendMessage(any(Message.class), anyString());
        verify(daoMock, never()).create(any(MappedEntity.class), anyString());
    }

    @Test
    public void processDELETE_WithTargetConflict_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResponseEntity<>(HttpStatus.CONFLICT));

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(0), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(0), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, nameDELETEAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, times(1)).delete(12, CPAction, orchestrComponentName);
    }

    @Test
    public void processDELETE_WithTargetResourceAccessException_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new ResourceAccessException("Target not running."));

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(0), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(0), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, nameDELETEAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, times(1)).delete(12, CPAction, orchestrComponentName);
    }

    @Test
    public void processDELETE_WithTargetHttpClientErrorException_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(0), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(0), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, nameDELETEAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, times(1)).delete(12, CPAction, orchestrComponentName);
    }

    @Test
    public void processDELETE_WithTargetRuntimeException_SendsCorrectMessagesAndReturnsCONFLICT() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, nameDELETEAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(true);
        mockSetupResponse(new RuntimeException());

        ResponseEntity res = testee.processDELETERequest("12", json, orchestrComponentName, CPAction);

        assertSame(res.getStatusCode(), HttpStatus.CONFLICT);
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(0)), eq(0), eq(json));
        verify(senderMock, times(1)).sendMessage(eq(mockedMessages.get(1)), eq(0), eq(json));
        verifyNoMoreInteractions(senderMock);
        verify(parserMock, times(1)).getRequiredMessagesFor(CPAction, nameDELETEAction);
        verifyNoMoreInteractions(parserMock);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).shiftEnumIdsInJSON(json, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, rudolfComponentName);
        verify(jsonUtilMock, times(1)).getResourceIdInTarget(12, CPAction, orchestrComponentName, ticketingComponentName);
        verify(jsonUtilMock, never()).addResourceIdToJSON(anyString(), anyString(), anyInt());
        verify(jsonUtilMock, never()).nullResourceIdInJSON(anyString(), anyString());
        verify(daoMock, times(1)).delete(12, CPAction, orchestrComponentName);
    }

    // Exceptions in JSON util
    @Test
    public void processPOST_WithErrorAddingResource() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(CPAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, json);
        mockSetupEntityExists(false);
        mockSetupResponse(new ResponseEntity<>("{\"id\":\"13\"}", HttpStatus.OK));
        mockSetupErrorWhileAddingResourceId();

        testee.processPOSTRequest(json, orchestrComponentName, CPAction);
    }

    @Test
    public void processPOST_WithErrorDeletingResource() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(ExternalAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, ExternalAction);
        mockSetupErrorWhileDeletingResourceId();
        testee.processPOSTRequest(json, ticketingComponentName, ExternalAction);
    }

    @Test
    public void processPOST_WithErrorShiftingEnumIds() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(ExternalAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, ExternalAction);
        mockSetupErrorWhileShiftingEnumIds();
        testee.processPOSTRequest(json, ticketingComponentName, ExternalAction);
    }

    @Test
    public void processPOST_WithErrorNullingResourceId() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(ExternalAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, ExternalAction);
        mockSetupErrorWhileDeletingResourceId();
        testee.processPOSTRequest(json, ticketingComponentName, ExternalAction);     
    }

    @Test
    public void processPOST_WithErrorWhileProcessing() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(ExternalAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, ExternalAction);        
        mockSetupErrorWhileProcessing();
        testee.processPOSTRequest(json, ticketingComponentName, ExternalAction);
    }

    @Test
    public void processPUT_WithErrorWhileProcessing() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(ExternalAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, ExternalAction);        
        mockSetupErrorWhileProcessing();
        testee.processPUTRequest(json, ticketingComponentName, ExternalAction);
    }

    @Test
    public void processDELETE_WithErrorWhileProcessing() throws JSONException, IOException {
        List<Message> mockedMessages = new MessagesBuilder().buildSampleMessages(ExternalAction, namePOSTAction);
        String json = new JsonCPActionBuilder(12).build();
        mockSetup(true, mockedMessages, ExternalAction);        
        mockSetupErrorWhileProcessing();
        testee.processDELETERequest(json, null, ticketingComponentName, ExternalAction);
    }

    // Mock setup
    private void mockSetup(boolean enumMapped, List<Message> mockedMessages, String json) throws JSONException, IOException {
        MappedEntity me = new MappedEntity();
        me.idOrchestr = 12;
        me.idRudolf = 13;
        me.idTicketing = 14;
        when(daoMock.get(12, category.getTableName(), orchestrComponentName))
                .thenReturn(me);
        if (enumMapped) {
            when(daoMock.get(12, place.getTableName(), orchestrComponentName))
                    .thenReturn(me);
        } else {
            when(daoMock.get(12, place.getTableName(), orchestrComponentName))
                    .thenReturn(null);
        }
        when(parserMock.getRequiredMessagesFor(anyString(), anyString()))
                .thenReturn(mockedMessages);
        when(jsonUtilMock.shiftEnumIdsInJSON(eq(json), eq(CPAction), eq(orchestrComponentName), anyString()))
                .thenReturn(json);
        when(jsonUtilMock.shiftResourceIdsInJSON(eq(json), eq(CPAction), eq(orchestrComponentName), anyString()))
                .thenReturn(json);

        // For POST
        when(jsonUtilMock.nullResourceIdInJSON(eq(json), eq(idName)))
                .thenReturn(json);
        when(jsonUtilMock.addResourceIdToJSON(eq(json), anyString(), anyInt()))
                .thenReturn(json);

    }

    private void mockSetupEntityExists(boolean entityExists) {
        if (!entityExists) {
            when(daoMock.get(12, CPAction, orchestrComponentName))
                    .thenReturn(null);
        } else {
            when(daoMock.get(12, CPAction, orchestrComponentName))
                    .thenReturn(new MappedEntity());
        }
    }

    private void mockSetupResponse(ResponseEntity response) {
        when(senderMock.sendMessage(any(Message.class), anyInt(), anyString()))
                .thenReturn(response);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenReturn(response);
    }

    private void mockSetupResponse(Exception e) {
        when(senderMock.sendMessage(any(Message.class), anyInt(), anyString()))
                .thenThrow(e);
        when(senderMock.sendMessage(any(Message.class), anyString()))
                .thenThrow(e);
    }

    private void mockSetupErrorWhileShiftingResourceIds() throws JSONException, IOException {
        when(jsonUtilMock.shiftResourceIdsInJSON(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException());
    }

    private void mockSetupErrorWhileAddingResourceId() throws JSONException {
        when(jsonUtilMock.addResourceIdToJSON(anyString(), anyString(), anyInt()))
                .thenThrow(new RuntimeException());
    }

    private void mockSetupErrorWhileShiftingEnumIds() throws JSONException, IOException {
        when(jsonUtilMock.shiftEnumIdsInJSON(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException());
    }

    private void mockSetupErrorWhileDeletingResourceId() throws JSONException {
        when(jsonUtilMock.nullResourceIdInJSON(anyString(), anyString()))
                .thenThrow(new RuntimeException());
    }

    private void mockSetupErrorWhileProcessing() {
        when(daoMock.get(anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException());
    }

    private void mockSetupForNonExistingEnums(String json) throws IOException {
        Message getPlace = new Message(nameGETAction, place.getTableName(), orchestrComponentName, null);
        Message postPlaceToRudolf = new Message(namePOSTAction, place.getTableName(), rudolfComponentName, null);
        Message postPlaceToTicketing = new Message(namePOSTAction, place.getTableName(), ticketingComponentName, null);
        when(senderMock.sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(getPlace)), eq(12), anyString()))
                .thenReturn(new ResponseEntity("{\"value\":\"Dvorana\"}", HttpStatus.OK));
        when(senderMock.sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToRudolf)), anyString()))
                .thenReturn(new ResponseEntity("{\"id\":\"13\"}", HttpStatus.OK));
        when(senderMock.sendMessage(argThat(new ObjectEqualityArgumentMatcher<>(postPlaceToTicketing)), anyString()))
                .thenReturn(new ResponseEntity("{\"id\":\"14\"}", HttpStatus.OK));
        when(jsonUtilMock.getEnumId(json, place.getIdName())).thenReturn(12);
    }

    /**
     * **********************************************************************************
     */
    // get mapped entity
    @Test
    public void getMappedEntity_validData_DAOGetCalled() {
        testee.getMappedEntity(12, "tableName", "componentName");
        verify(daoMock, times(1)).get(12, "tableName", "componentName");
    }

    @Test
    public void getMappedEntity_idIs0_DAOGetNotCalledAndReturnsNull() {
        testee.getMappedEntity(0, "tableName", "componentName");
        verify(daoMock, never()).get(anyInt(), anyString(), anyString());
    }

    @Test
    public void getMappedEntity_tableNameEmpty_DAOGetNotCalledAndReturnsNull() {
        testee.getMappedEntity(12, "", "componentName");
        verify(daoMock, never()).get(anyInt(), anyString(), anyString());
    }

    @Test
    public void getMappedEntity_componentNameEmpty_DAOGetNotCalledAndReturnsNull() {
        testee.getMappedEntity(12, "tableName", null);
        verify(daoMock, never()).get(anyInt(), anyString(), anyString());
    }

    // Equality matcher
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
}
