import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UniversalParserTest {

    @TempDir
    Path tempDir;

    @Test
    public void testParseFile() throws IOException {
        // Create temporary schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 10",
            "age 11 12",
            "gender 13 13"
        ));

        // Create temporary data file
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of(
            "John      301",
            "Jane      252"
        ));

        Schema schema = new Schema(schemaFile.toString());
        UniversalParser parser = new UniversalParser(schema);
        List<DynamicRecord> records = parser.parseFile(dataFile.toString());

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

    @Test
    public void testParseFileWithShortLine() throws IOException {
        // Create temporary schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 10"
        ));

        // Create temporary data file with short line
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of(
            "John"
        ));

        Schema schema = new Schema(schemaFile.toString());
        UniversalParser parser = new UniversalParser(schema);
        List<DynamicRecord> records = parser.parseFile(dataFile.toString());

        assertEquals(1, records.size());
        DynamicRecord record = records.get(0);
        assertEquals("John", record.get("name"));
    }

    @Test
    public void testParseFileSkipsEmptyLines() throws IOException {
        // Create temporary schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 10"
        ));

        // Create temporary data file with empty line
        Path dataFile = tempDir.resolve("data.txt");
        Files.write(dataFile, List.of(
            "John",
            "",
            "Jane"
        ));

        Schema schema = new Schema(schemaFile.toString());
        UniversalParser parser = new UniversalParser(schema);
        List<DynamicRecord> records = parser.parseFile(dataFile.toString());

        assertEquals(2, records.size());
    }

    @Test
    public void testParseFileEmptyInput() throws IOException {
        // Create temporary schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 10"
        ));

        // Create empty input file
        Path inputFile = tempDir.resolve("inputfile.txt");
        Files.write(inputFile, List.of());

        Schema schema = new Schema(schemaFile.toString());
        UniversalParser parser = new UniversalParser(schema);
        List<DynamicRecord> records = parser.parseFile(inputFile.toString());

        assertEquals(0, records.size());
    }

    @Test
    public void testParseFileWithTabs() throws IOException {
        // Create temporary schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 10",
            "age 11 12"
        ));

        // Create input file with spaces
        Path inputFile = tempDir.resolve("inputfile.txt");
        Files.write(inputFile, List.of(
            "John      301"
        ));

        Schema schema = new Schema(schemaFile.toString());
        UniversalParser parser = new UniversalParser(schema);
        List<DynamicRecord> records = parser.parseFile(inputFile.toString());

        assertEquals(1, records.size());
        DynamicRecord record = records.get(0);
        assertEquals("John", record.get("name"));
        assertEquals("30", record.get("age"));
    }
}
