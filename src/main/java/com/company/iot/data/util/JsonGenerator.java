package com.company.iot.data.util;

import com.company.iot.data.model.json.Data;
import com.company.iot.data.model.json.Location;
import com.google.gson.Gson;

import java.time.Instant;
import java.util.*;

public class JsonGenerator {

    private static Gson gson = new Gson();
    private static Random random = new Random();

    // Three devices exist with separate ids
    private static List<String> deviceList = Arrays.asList("ff96ae60-005a-4614-a9ef-a567d3be9863",
            "ff77a55b-0b0a-4430-9e25-6df51cdb0b05",
            "ff6e0f54-3c31-4085-8e03-1a4067dc2f05");

    public static String generate() {
        String jsonText = gson.toJson(new Data(deviceList.get(random.nextInt(3)), random
                .nextInt(100), new Location(
                Long.valueOf(random.nextLong()), Long.valueOf(random.nextLong())),
                Instant.now().getEpochSecond()));
        return jsonText;
    }
}
