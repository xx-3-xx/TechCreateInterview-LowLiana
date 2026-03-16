package main;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable schema definition using Java record.
 */
public record Schema(String field, int start, int end) {
    public Schema {
        if (start <= 0 || end < start) {
            throw new IllegalArgumentException("Invalid schema range: " + start + "-" + end);
        }
        Objects.requireNonNull(field, "Field name cannot be null");
    }

    public int length() { return end - start + 1; }

    @Override
    public String toString() {
        return String.format("Field: %-10s | Range: %d-%d (Len: %d)", field, start, end, length());
    }
}

/**
 * Loads schema definitions from a file using NIO.
 */
class SchemaLoader {
    public static List<Schema> loadSchema(String schemaPath) throws IOException {
        List<Schema> schemaList = Files.lines(Paths.get(schemaPath))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split("\\s+"))
                .filter(parts -> parts.length == 3)
                .map(parts -> new Schema(parts[0],
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])))
                .collect(Collectors.toList());

        return Collections.unmodifiableList(schemaList);
    }
}

/**
 * Abstraction for data storage engines.
 */
interface DataEngine {
    void displayData(List<Schema> schemaList) throws IOException;
    void addRecord(List<Schema> schemaList) throws IOException;
}

/**
 * File-based implementation of DataEngine.
 */
class FixedLengthEngine implements DataEngine {
    private final Path dataPath;

    public FixedLengthEngine(String dataPath) {
        this.dataPath = Paths.get(dataPath);
    }

    @Override
    public void displayData(List<Schema> schemaList) throws IOException {
        if (!Files.exists(dataPath)) {
            System.out.println("No data file found.");
            return;
        }

        Files.lines(dataPath).filter(line -> !line.isEmpty()).forEach(line -> {
            StringBuilder sb = new StringBuilder("| ");
            for (Schema s : schemaList) {
                int begin = s.start() - 1;
                int finish = Math.min(s.end(), line.length());
                String val = (begin < line.length()) ? line.substring(begin, finish).trim() : "";
                sb.append(s.field()).append(": ").append(String.format("%-10s", val)).append(" | ");
            }
            System.out.println(sb);
        });
    }

    @Override
    public void addRecord(List<Schema> schemaList) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nAdd new record? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) return;

        int totalLength = schemaList.stream().mapToInt(Schema::end).max().orElse(0);
        char[] lineBuffer = new char[totalLength];
        Arrays.fill(lineBuffer, ' ');

        for (Schema s : schemaList) {
            System.out.print("Enter " + s.field() + ": ");
            String input = sc.nextLine().trim();

            if (input.length() > s.length()) {
                System.out.println("Input too long, truncating...");
                input = input.substring(0, s.length());
            }

            for (int i = 0; i < input.length(); i++) {
                lineBuffer[s.start() - 1 + i] = input.charAt(i);
            }
        }

        Files.writeString(dataPath, new String(lineBuffer) + System.lineSeparator(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        System.out.println("Record saved successfully.");
        System.out.println("\n=== UPDATED DATA VIEW ===");
        displayData(schemaList);
    }
}

/**
 * Generates Java files dynamically based on schema.
 */
class JavaFileGenerator {
    public static void generateJavaFiles(List<Schema> schemaList) throws IOException {
        StringBuilder rb = new StringBuilder("package main;\n\npublic class Record {\n");
        for (Schema s : schemaList) {
            rb.append("    private String ").append(s.field()).append(";\n");
        }

        rb.append("\n    public Record(");
        for (int i = 0; i < schemaList.size(); i++) {
            rb.append("String ").append(schemaList.get(i).field());
            if (i < schemaList.size() - 1) rb.append(", ");
        }
        rb.append(") {\n");
        for (Schema s : schemaList) {
            rb.append("        this.").append(s.field()).append(" = ").append(s.field()).append(";\n");
        }
        rb.append("    }\n}");

        Path dir = Paths.get("src/main");
        if (!Files.exists(dir)) Files.createDirectories(dir);
        Files.writeString(dir.resolve("Record.java"), rb.toString(), StandardOpenOption.CREATE);
    }
}


