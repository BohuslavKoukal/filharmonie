/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tests;

import TestConfig.JsonUtilTestConfig;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertSame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import philharmonic.model.MappedEntity;
import static philharmonic.resources.StringConstants.*;
import static philharmonic.resources.mapping.EnumMapping.*;
import philharmonic.service.IMCService;
import philharmonic.utilities.JsonUtil;

/**
 *
 * @author Kookie
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JsonUtilTestConfig.class})
@WebAppConfiguration
public class JsonUtilTest {

    @Autowired
    private IMCService serviceMock;

    @Autowired
    private JsonUtil testee;

    private String JSON;

    public JsonUtilTest() {

    }

    @Before
    public void setUp() {
        reset(serviceMock);
        JSON = "{\n"
                + "\"id\": \"1\",\n"
                + "\"name\": \"Tučňáci OPĚT v Rudolfinu\",\n"
                + "\"placeId\": \"12\",\n"
                + "\"categoryId\": \"3\"\n"
                + "}";
    }

    @Test
    public void nullResourceId_returnsJSONwithId0() throws JSONException {
        String nulled = testee.nullResourceIdInJSON(JSON, "id");
        JSONObject jo = new JSONObject(nulled);
        int idValue = jo.getInt("id");
        assertSame(0, idValue);
    }

    @Test
    public void shiftResourceIds_returnsJSONWithShiftedIds() throws JSONException, IOException {
        MappedEntity me = new MappedEntity();
        me.id = 12;
        me.idOrchestr = 12;
        me.idRudolf = 12;
        when(serviceMock.getMappedEntity(eq(1), anyString(), anyString()))
                .thenReturn(me);
        when(serviceMock.shouldBeMapped(anyString()))
                .thenReturn(true);
        String shifted = testee.shiftResourceIdsInJSON(JSON, CPAction, orchestrComponentName, rudolfComponentName);
        JSONObject jo = new JSONObject(shifted);
        int idValue = jo.getInt("id");
        assertSame(12, idValue);
    }

    @Test
    public void addResourceId_returnsJSONWithAddedIds() throws JSONException, IOException {
        MappedEntity me = new MappedEntity();
        me.id = 12;
        me.idOrchestr = 12;
        me.idRudolf = 12;
        when(serviceMock.getMappedEntity(eq(1), anyString(), anyString()))
                .thenReturn(me);
        String added = testee.addResourceIdToJSON(JSON, rudolfComponentName, 44);
        added = testee.addResourceIdToJSON(added, orchestrComponentName, 14);
        JSONObject jo = new JSONObject(added);
        int idRudolf = jo.getInt("idRudolf");
        int idOrchestr = jo.getInt("idOrchestr");
        assertSame(44, idRudolf);
        assertSame(14, idOrchestr);
    }

    @Test
    public void shiftEnumIds_returnsJSONWithCorrectlyShiftedEnumIds() throws JSONException, IOException {
        MappedEntity me1 = new MappedEntity();
        me1.id = 120;
        me1.idOrchestr = 121;
        me1.idRudolf = 122;
        MappedEntity me2 = new MappedEntity();
        me2.id = 30;
        me2.idOrchestr = 31;
        me2.idRudolf = 32;
        when(serviceMock.getMappedEntity(eq(12), anyString(), anyString()))
                .thenReturn(me1);
        when(serviceMock.getMappedEntity(eq(3), anyString(), anyString()))
                .thenReturn(me2);
        String shifted = testee.shiftEnumIdsInJSON(JSON, CPAction,
                orchestrComponentName, rudolfComponentName);
        assertSame(122, new JSONObject(shifted).getInt("placeId"));
        assertSame(32, new JSONObject(shifted).getInt("categoryId"));
    }

    @Test
    public void getResourceIdInTarget_returnsCorrectId() {
        MappedEntity me1 = new MappedEntity();
        me1.id = 10;
        me1.idRudolf = 12;
        me1.idWeb = 13;
        when(serviceMock.getMappedEntity(12, ExternalAction, rudolfComponentName))
                .thenReturn(me1);
        when(serviceMock.getMappedEntity(100, ExternalAction, rudolfComponentName))
                .thenReturn(null);
        int id1 = testee.getResourceIdInTarget(12, ExternalAction, rudolfComponentName, webComponentName);
        int id2 = testee.getResourceIdInTarget(100, ExternalAction, rudolfComponentName, ticketingComponentName);
        assertSame(13, id1);
        assertSame(0, id2);
    }

    @Test
    public void getEnumId_returnsCorrectId() throws JSONException, IOException {
        int id1 = testee.getEnumId(JSON, category.getIdName());
        int id2 = testee.getEnumId(JSON, itemSubject.getIdName());
        assertSame(3, id1);
        assertSame(0, id2);
    }

}
