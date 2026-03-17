import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DynamicParserAppTest {

    @TempDir
    Path tempDir;

    @Test
    public void testDynamicParserApp() throws IOException {
        // Create temporary schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 10",
            "age 11 12",
            "gender 13 13"
        ));

        // Create temporary input file
        Path inputFile = tempDir.resolve("inputfile.txt");
        Files.write(inputFile, List.of(
            "John      301",
            "Jane      252"
        ));

        // Simulate the app logic
        Schema schema = new Schema(schemaFile.toString());
        UniversalParser parser = new UniversalParser(schema);
        List<DynamicRecord> records = parser.parseFile(inputFile.toString());

        // Assertions
        assertEquals(2, records.size());

        DynamicRecord record1 = records.get(0);
        assertEquals("John", record1.get("name"));
        assertEquals("30", record1.get("age"));
        assertEquals("1", record1.get("gender"));

        DynamicRecord record2 = records.get(1);
        assertEquals("Jane", record2.get("name"));
        assertEquals("25", record2.get("age"));
        assertEquals("2", record2.get("gender"));
    }
}
