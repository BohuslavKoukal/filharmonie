/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import TestConfig.ServiceTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import philharmonic.dao.IDao;
import static org.mockito.Mockito.*;
import philharmonic.model.MappedResource;
import philharmonic.service.IMCService;


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
    private IMCService testee;
    
    public IMCServiceTest() {
    }    
        
    @Before
    public void setUp() {
        reset(daoMock);
    }
    
    /*************************************************************************************/
    // save mapped resource
    
    @Test
    public void saveMappedResource_validArguments_DAOCreateCalled() throws Exception {
        MappedResource mr = new MappedResource();
        testee.saveMappedResource(mr, "name");
        verify(daoMock, times(1)).create(mr, "name");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void saveMappedResource_null_ThrowsException() throws Exception {
        testee.saveMappedResource(null, "name");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void saveMappedResource_emptyResourceName_ThrowsException() throws Exception {
        testee.saveMappedResource(new MappedResource(), "");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void saveMappedResource_nullName_ThrowsException() throws Exception {
        testee.saveMappedResource(new MappedResource(), null);
    }
    
    /*************************************************************************************/
    // get mapped entity
    
    @Test
    public void getMappedEntity_validData_DAOGetCalled() throws Exception {
        testee.getMappedEntity(12, "tableName", "componentName");
        verify(daoMock, times(1)).get(12, "tableName", "componentName");
    }
    
    @Test
    public void getMappedEntity_idIs0_DAOGetNotCalledAndReturnsNull() throws Exception {
        testee.getMappedEntity(0, "tableName", "componentName");
        verify(daoMock, never()).get(anyInt(), anyString(), anyString());
    }
    
    @Test
    public void getMappedEntity_tableNameEmpty_DAOGetNotCalledAndReturnsNull() throws Exception {
        testee.getMappedEntity(12, "", "componentName");
        verify(daoMock, never()).get(anyInt(), anyString(), anyString());
    }
    
    @Test
    public void getMappedEntity_componentNameEmpty_DAOGetNotCalledAndReturnsNull() throws Exception {
        testee.getMappedEntity(12, "tableName", null);
        verify(daoMock, never()).get(anyInt(), anyString(), anyString());
    }
    
    /*************************************************************************************/
    // delete entity
    
    @Test
    public void deleteEntity_validData_DAODeleteCalledWithCorrectParameters() throws Exception {
        testee.deleteEntity(12, "tableName", "componentName");
        verify(daoMock, times(1)).delete(12, "tableName", "componentName");
    }
    
    @Test
    public void deleteEntity_idIs0_DAODeleteNotCalled() throws Exception {
        testee.deleteEntity(0, "tableName", "componentName");
        verify(daoMock, never()).delete(anyInt(), anyString(), anyString());
    }
    
    @Test
    public void deleteEntity_tableNameEmpty_DAODeleteNotCalled() throws Exception {
        testee.deleteEntity(12, "", "componentName");
        verify(daoMock, never()).delete(anyInt(), anyString(), anyString());
    }
    
    @Test
    public void deleteEntity_componentNameEmpty_DAODeleteNotCalled() throws Exception {
        testee.deleteEntity(12, "tableName", null);
        verify(daoMock, never()).delete(anyInt(), anyString(), anyString());
    }
    
}
