import java.io.*;
import java.util.*;

public class UniversalParser {
    private final Schema schema;

    public UniversalParser(Schema schema) {
        this.schema = schema;
    }

    public List<DynamicRecord> parseFile(String dataPath) throws IOException {
        List<DynamicRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(dataPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines

                DynamicRecord record = new DynamicRecord();
                for (FieldDefinition field : schema.getFields()) {
                    int start = field.start() - 1; // Convert 1-based to 0-based
                    int end = field.end();

                    String extracted;
                    if (start >= line.length()) {
                        extracted = ""; // Line is too short for this field
                    } else {
                        // Ensure we don't go past the end of the actual string
                        int actualEnd = Math.min(end, line.length());
                        extracted = line.substring(start, actualEnd).trim();
                    }

                    record.set(field.name(), field.convert(extracted));
                }
                records.add(record);
            }
        }
        return records;
    }
}