import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SchemaTest {

    @TempDir
    Path tempDir;

    @Test
    public void testSchemaLoading() throws IOException {
        // Create a temporary schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 20",
            "age 21 24",
            "gender 25 25"
        ));

        Schema schema = new Schema(schemaFile.toString());

        List<FieldDefinition> fields = schema.getFields();
        assertEquals(3, fields.size());

        FieldDefinition nameField = fields.get(0);
        assertEquals("name", nameField.name());
        assertEquals(1, nameField.start());
        assertEquals(20, nameField.end());

        FieldDefinition ageField = fields.get(1);
        assertEquals("age", ageField.name());
        assertEquals(21, ageField.start());
        assertEquals(24, ageField.end());

        FieldDefinition genderField = fields.get(2);
        assertEquals("gender", genderField.name());
        assertEquals(25, genderField.start());
        assertEquals(25, genderField.end());
    }

    @Test
    public void testFieldDefinitionConvert() {
        FieldDefinition field = new FieldDefinition("name", 1, 20);
        Object result = field.convert("John");
        assertEquals("John", result);
    }

    @Test
    public void testSchemaWithInvalidLines() throws IOException {
        // Create temporary schema file with invalid lines
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of(
            "name 1 10",
            "invalid line",
            "age abc 12"
        ));

        Schema schema = new Schema(schemaFile.toString());
        List<FieldDefinition> fields = schema.getFields();

        // Only the valid line should be parsed
        assertEquals(1, fields.size());
        assertEquals("name", fields.get(0).name());
    }

    @Test
    public void testSchemaEmptyFile() throws IOException {
        // Create empty schema file
        Path schemaFile = tempDir.resolve("schema.txt");
        Files.write(schemaFile, List.of());

        Schema schema = new Schema(schemaFile.toString());
        List<FieldDefinition> fields = schema.getFields();

        assertEquals(0, fields.size());
    }
}
