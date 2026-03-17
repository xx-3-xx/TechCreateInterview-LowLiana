import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class RecordMergerTest {

    @TempDir
    Path tempDir;

    @Test
    public void testMergeRecords() throws IOException {
        // Create temporary data file
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of(
            String.format("%-20s%-1s%-4s", "John Doe", "M", "30"),
            String.format("%-20s%-1s%-4s", "Jane Smith", "F", "25"),
            String.format("%-20s%-1s%-4s", "John Doe", "M", "35")
        ));

        RecordMerger merger = new RecordMerger();
        Map<String, RecordMerger.Record> merged = merger.mergeRecords(dataFile.toString());

        assertEquals(2, merged.size());

        RecordMerger.Record john = merged.get("john doe");
        assertNotNull(john);
        assertEquals("John Doe", john.name());
        assertEquals("M", john.gender());
        assertEquals(35, john.age()); // Keeps the higher age

        RecordMerger.Record jane = merged.get("jane smith");
        assertNotNull(jane);
        assertEquals("Jane Smith", jane.name());
        assertEquals("F", jane.gender());
        assertEquals(25, jane.age());
    }

    @Test
    public void testParseLine() {
        RecordMerger merger = new RecordMerger();
        String line = String.format("%-20s%-1s%-4s", "John Doe", "M", "30");
        RecordMerger.Record record = merger.parseLine(line);

        assertNotNull(record);
        assertEquals("John Doe", record.name());
        assertEquals("M", record.gender());
        assertEquals(30, record.age());
    }

    @Test
    public void testParseLineInvalidAge() {
        RecordMerger merger = new RecordMerger();
        String line = String.format("%-20s%-1s%-4s", "John Doe", "M", "xx");
        RecordMerger.Record record = merger.parseLine(line);

        assertNull(record);
    }

    @Test
    public void testMergeRecordsEmptyFile() throws IOException {
        // Create empty data file
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of());

        RecordMerger merger = new RecordMerger();
        Map<String, RecordMerger.Record> merged = merger.mergeRecords(dataFile.toString());

        assertEquals(0, merged.size());
    }

    @Test
    public void testParseLineWithNegativeAge() {
        RecordMerger merger = new RecordMerger();
        String line = String.format("%-20s%-1s%-4s", "John Doe", "M", "-5");
        RecordMerger.Record record = merger.parseLine(line);

        assertNotNull(record);
        assertEquals("John Doe", record.name());
        assertEquals("M", record.gender());
        assertEquals(-5, record.age());
    }
}
