package io.github.protino.codewatch;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import timber.log.Timber;

import static org.junit.Assert.assertTrue;

/**
 * @author Gurupad Mamadapur
 */

@RunWith(AndroidJUnit4.class)
public class TestGsonPerformance {

    private final static Type typeHashMap = new TypeToken<HashMap<String, Integer>>() {
    }.getType();
    private Map<String, Integer> languageMap;
    private List<Map<String, Integer>> dataList = new ArrayList<>();

    @Before
    public void initialize() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            languageMap = new HashMap<>();
            for (int j = 0; j < random.nextInt(4) + 1; j++) {
                languageMap.put(UUID.randomUUID().toString(), random.nextInt(100000));
            }
            dataList.add(languageMap);
        }
    }

    @Test
    public void testGson() {
        assertTrue("Incorrect dataList",dataList.size() == 1000);
        long start = System.currentTimeMillis();
        for (Map<String, Integer> map : dataList) {
            new Gson().toJson(map, typeHashMap);
        }
        Timber.d("Gson serialization - " + (System.currentTimeMillis() - start));
    }

    @Test
    public void testSimpleGson() throws JSONException {
        assertTrue("Incorrect dataList",dataList.size() == 1000);
        long start = System.currentTimeMillis();
        for (Map<String, Integer> map : dataList) {
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            jsonObject.toString();
        }
        Timber.d("Json serialization - " + (System.currentTimeMillis() - start));
    }
}
