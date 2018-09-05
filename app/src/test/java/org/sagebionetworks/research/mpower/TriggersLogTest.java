package org.sagebionetworks.research.mpower;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import com.google.gson.Gson;

import org.junit.Test;
import org.sagebionetworks.research.domain.JsonAssetUtil;
import org.sagebionetworks.research.mpower.tracking.view_model.logs.SimpleTrackingItemLog;
import org.threeten.bp.Instant;

import java.net.URL;

public class TriggersLogTest {
    private static final Gson GSON = DaggerTrackingTestComponent.builder().build().gson();

    private static final String UNRECORDED_ITEM = "hot";

    private static final String RECORDED_ITEM = "Bedtime, late";

    private static final Instant RECORDED_TIMESTAMP = Instant.parse("2018-08-29T12:24:26.552-07:00");

    @Test
    public void test_serializeUnrecorded() {
        SimpleTrackingItemLog log = SimpleTrackingItemLog.builder()
                .setIdentifier(UNRECORDED_ITEM)
                .setText(UNRECORDED_ITEM)
                .build();
        String serialized = GSON.toJson(log).replaceAll("\\s+", "");
        URL url = TriggersLogTest.class.getClassLoader().getResource("triggers/TriggersLog_Unrecorded.json");
        String expected = JsonAssetUtil.readJsonFileHelper(GSON, url);
        assertNotNull("Error loading expected resource file", expected);
        expected = expected.replaceAll("\\s+", "");
        assertEquals("Log serialization produced an unexpected result", expected, serialized);
    }

    @Test
    public void test_serializeRecorded() {
        SimpleTrackingItemLog log = SimpleTrackingItemLog.builder()
                .setIdentifier(RECORDED_ITEM)
                .setText(RECORDED_ITEM)
                .setTimestamp(RECORDED_TIMESTAMP)
                .build();
        String serialized = GSON.toJson(log).replaceAll("\\s+", "");
        URL url = TriggersLogTest.class.getClassLoader().getResource("triggers/TriggersLog_Recorded.json");
        String expected = JsonAssetUtil.readJsonFileHelper(GSON, url);
        assertNotNull("Error loading expected resource file", expected);
        expected = expected.replaceAll("\\s+", "");
        assertEquals("Log serialization produced an unexpected result", expected, serialized);
    }

    @Test
    public void test_deserializeUnrecorded() {
        SimpleTrackingItemLog log =
                JsonAssetUtil.readJsonFile(GSON, "triggers/TriggersLog_Unrecorded.json", SimpleTrackingItemLog.class);
        assertNotNull(log);
        assertEquals("Log had unexpected identifier", UNRECORDED_ITEM, log.getIdentifier());
        assertEquals("Log had unexpected text", UNRECORDED_ITEM, log.getText());
        assertNull("Log had non-null timestamp", log.getTimestamp());
    }

    @Test
    public void test_deserializeRecorded() {
        SimpleTrackingItemLog log =
                JsonAssetUtil.readJsonFile(GSON, "triggers/TriggersLog_Unrecorded.json", SimpleTrackingItemLog.class);
        assertNotNull(log);
        assertEquals("Log had unexpected identifier", RECORDED_ITEM, log.getIdentifier());
        assertEquals("Log had unexpected text", RECORDED_ITEM, log.getText());
        assertEquals("Log had unexpected timestamp", RECORDED_TIMESTAMP, log.getTimestamp());
    }
}
