package main;

import java.io.IOException;
import java.util.List;

/**
 * Main application entry point.
 */
/**
 * Main application entry point.
 */
public class App {
    public static void main(String[] args) {
        String schemaPath = "schema.txt";
        String dataPath = "inputfile.txt";

        try {
            List<Schema> schemaList = SchemaLoader.loadSchema(schemaPath);

            System.out.println("=== SCHEMA CONFIGURATION ===");
            schemaList.forEach(System.out::println);

            DataEngine engine = new FixedLengthEngine(dataPath);

            System.out.println("\n=== CURRENT DATA IN FILE ===");
            engine.displayData(schemaList);

            engine.addRecord(schemaList);
            JavaFileGenerator.generateJavaFiles(schemaList);

        } catch (IOException e) {
            System.err.println("Application error: " + e.getMessage());
        }
    }
}