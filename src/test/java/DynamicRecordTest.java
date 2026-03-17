import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DynamicRecordTest {

    @Test
    public void testSetAndGet() {
        DynamicRecord record = new DynamicRecord();
        record.set("name", "John");
        record.set("age", 30);

        assertEquals("John", record.get("name"));
        assertEquals(30, (Integer) record.get("age"));
    }

    @Test
    public void testGetNonExistent() {
        DynamicRecord record = new DynamicRecord();
        assertNull(record.get("nonexistent"));
    }

    @Test
    public void testToString() {
        DynamicRecord record = new DynamicRecord();
        record.set("name", "John");
        record.set("age", 30);

        String expected = "Record{name=John, age=30}";
        assertEquals(expected, record.toString());
    }

    @Test
    public void testSetNullValue() {
        DynamicRecord record = new DynamicRecord();
        record.set("name", null);
        assertNull(record.get("name"));
    }

    @Test
    public void testOverwriteValue() {
        DynamicRecord record = new DynamicRecord();
        record.set("name", "John");
        record.set("name", "Jane");
        assertEquals("Jane", record.get("name"));
    }
}
