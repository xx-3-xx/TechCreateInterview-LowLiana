import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Schema {
    private final List<FieldDefinition> fields = new ArrayList<>();

    public Schema(String schemaPath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(schemaPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 3) {
                    try {
                        fields.add(new FieldDefinition(
                                parts[0],
                                Integer.parseInt(parts[1]),
                                Integer.parseInt(parts[2])
                        ));
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        }
    }

    public List<FieldDefinition> getFields() { return fields; }
}
