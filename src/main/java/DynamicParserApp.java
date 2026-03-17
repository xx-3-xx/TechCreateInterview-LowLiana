import java.io.IOException;
import java.util.List;

public class DynamicParserApp {
    public static void main(String[] args) {
        try {
            // 1. Load Schema (Metaprogramming source)
            Schema schema = new Schema("schema.txt");

            // 2. Initialize Parser with that Schema
            UniversalParser parser = new UniversalParser(schema);

            // 3. Parse Data
            List<DynamicRecord> records = parser.parseFile("inputfile.txt");

            // 4. Output results
            for (DynamicRecord rec : records) {
                System.out.println(rec);
                // Example of accessing a specific field dynamically:
                // System.out.println("Name: " + rec.get("name"));
            }

        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }
}