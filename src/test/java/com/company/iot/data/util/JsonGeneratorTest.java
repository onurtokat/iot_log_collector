package com.company.iot.data.util;

import com.company.iot.data.model.json.Data;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonGeneratorTest {

    private Gson gson = new Gson();

    @Test
    void generate() {
        String result = JsonGenerator.generate();
        assertNotNull(result);
        assertTrue(result.contains("deviceId"));
        assertTrue(result.contains("temperature"));
        assertTrue(result.contains("location"));
        assertTrue(result.contains("latitude"));
        assertTrue(result.contains("longitude"));
        assertTrue(result.contains("time"));
        assertEquals("com.company.iot.data.model.json.Data", gson.fromJson(result, Data.class).getClass().getName());
    }
}