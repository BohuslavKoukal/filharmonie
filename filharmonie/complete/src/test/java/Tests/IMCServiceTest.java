/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import TestConfig.DaoTestConfig;
import TestConfig.RealInstance.ServiceRealInstance;
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
@ContextConfiguration(classes = {ServiceTestConfig.class,
    DaoTestConfig.class,
    ServiceRealInstance.class})
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
    
    @Test
    public void saveMappedResource_validArguments_DAOCreateCalled() throws Exception {
        MappedResource mr = new MappedResource();
        testee.saveMappedResource(mr);
        verify(daoMock, times(1)).create(mr);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void saveMappedResource_null_ThrowsException() throws Exception {
        testee.saveMappedResource(null);
    }
    
    @Test
    public void getMappedEntity_validData_DAOGetCalled() throws Exception {
        testee.getMappedEntity(12, "componentName", "tableName");
        verify(daoMock, times(1)).get(12, "componentName", "tableName");
    }
    
    @Test
    public void getMappedEntity_idIs0_DAOGetNotCalledAndReturnsNull() throws Exception {
        testee.getMappedEntity(0, "componentName", "tableName");
        verify(daoMock, times(0)).get(anyInt(), anyString(), anyString());
    }
    
    @Test
    public void getMappedEntity_componentNameEmpty_DAOGetNotCalledAndReturnsNull() throws Exception {
        testee.getMappedEntity(12, "", "tableName");
        verify(daoMock, times(0)).get(anyInt(), anyString(), anyString());
    }
    
    @Test
    public void getMappedEntity_tableNameEmpty_DAOGetNotCalledAndReturnsNull() throws Exception {
        testee.getMappedEntity(12, "componentName", null);
        verify(daoMock, times(0)).get(anyInt(), anyString(), anyString());
    }
    
}
