package Tests;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import philharmonic.service.IMCService;
import TestConfig.IMCControllerTestConfig;
import TestBuilders.JsonCPActionBuilder;


import static philharmonic.resources.StringConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Kookie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {IMCControllerTestConfig.class})
@WebAppConfiguration
public class IMCControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private IMCService serviceMock;

    public IMCControllerTest() {
    }
    
    @Before
    public void setUp() {
        reset(serviceMock);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // CPAction
 
    @Test
    public void postCPAction_ListensOnCorrectAddressAndCallsServiceCorrectly() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(post(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processPOSTRequest(json, orchestrComponentName, CPAction);
        verifyNoMoreInteractions(serviceMock);
    }
    
    @Test
    public void putCPAction_ListensOnCorrectAddressAndCallsServiceCorrectly() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(put(addressMiddleComponent + resourceAddressCPAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processPUTRequest(json, orchestrComponentName, CPAction);
        verifyNoMoreInteractions(serviceMock);
    }
    
    // External action
    
    @Test
    public void postExternalAction_ListensOnCorrectAddressAndCallsServiceCorrectly() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(post(addressMiddleComponent + resourceAddressExternalAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processPOSTRequest(json, rudolfComponentName, ExternalAction);
        verifyNoMoreInteractions(serviceMock);
    }
    
    @Test
    public void putExternalAction_ListensOnCorrectAddressAndCallsServiceCorrectly() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(put(addressMiddleComponent + resourceAddressExternalAction)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processPUTRequest(json, rudolfComponentName, ExternalAction);
        verifyNoMoreInteractions(serviceMock);
    }
    
    @Test
    public void deleteExternalAction_ListensOnCorrectAddressAndCallsServiceCorrectly1() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(delete(addressMiddleComponent + resourceAddressExternalAction + "/11")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processDELETERequest("11", json, rudolfComponentName, ExternalAction);
        verifyNoMoreInteractions(serviceMock);
    }
    
    @Test
    public void deleteExternalAction_ListensOnCorrectAddressAndCallsServiceCorrectly2() throws Exception {
        mockSetup();
        mockMvc.perform(delete(addressMiddleComponent + resourceAddressExternalAction + "/11")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isOk());
        verify(serviceMock, times(1)).processDELETERequest("11", "", rudolfComponentName, ExternalAction);
        verifyNoMoreInteractions(serviceMock);
    }
    
        // Item
    
    @Test
    public void postItem_ListensOnCorrectAddressAndCallsServiceCorrectly() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(post(addressMiddleComponent + resourceAddressItem)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processPOSTRequest(json, rudolfComponentName, Item);
        verifyNoMoreInteractions(serviceMock);
    }
    
    @Test
    public void putItem_ListensOnCorrectAddressAndCallsServiceCorrectly() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(put(addressMiddleComponent + resourceAddressItem)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processPUTRequest(json, rudolfComponentName, Item);
        verifyNoMoreInteractions(serviceMock);
    }
    
    @Test
    public void deleteItem_ListensOnCorrectAddressAndCallsServiceCorrectly1() throws Exception {
        String json = new JsonCPActionBuilder(12)
                .build();
        mockSetup();
        mockMvc.perform(delete(addressMiddleComponent + resourceAddressItem + "/11")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(serviceMock, times(1)).processDELETERequest("11", json, rudolfComponentName, Item);
        verifyNoMoreInteractions(serviceMock);
    }
    
        @Test
    public void deleteItem_ListensOnCorrectAddressAndCallsServiceCorrectly2() throws Exception {
        mockSetup();
        mockMvc.perform(delete(addressMiddleComponent + resourceAddressItem + "/11")
            .contentType(MediaType.APPLICATION_JSON)
            .content(""))
            .andExpect(status().isOk());
        verify(serviceMock, times(1)).processDELETERequest("11", "", rudolfComponentName, Item);
        verifyNoMoreInteractions(serviceMock);
    }
    
    //Setup
    
    private void mockSetup() {
        when(serviceMock.processPOSTRequest(anyString(), anyString(), anyString()))
                    .thenReturn(new ResponseEntity(HttpStatus.OK));
        when(serviceMock.processPUTRequest(anyString(), anyString(), anyString()))
                    .thenReturn(new ResponseEntity(HttpStatus.OK));
        when(serviceMock.processDELETERequest(anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(new ResponseEntity(HttpStatus.OK));
    }
    
}
