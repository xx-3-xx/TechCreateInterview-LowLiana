package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Schema {
    String field;
    int start;
    int end;

    public Schema(String field, int start, int end) {
        this.field = field;
        this.start = start;
        this.end = end;
    }

    public static void main(String[] args) {
        List<Schema> schemaList = new ArrayList<>();
        String schemaPath = "schema.txt";
        String dataPath = "inputfile.txt";

        // 1. Load Schema
        try (BufferedReader br = new BufferedReader(new FileReader(schemaPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    schemaList.add(new Schema(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                }
            }
        } catch (IOException e) {
            System.err.println("Error: 'schema.txt' not found.");
            return;
        }

        // --- Display Schema ---
        System.out.println("=== SCHEMA CONFIGURATION ===");
        for (Schema s : schemaList) {
            System.out.printf("Field: %-10s | Range: %d-%d (Len: %d)%n", s.field, s.start, s.end, (s.end - s.start + 1));
        }

        // 2. Parse and Display Existing Data
        System.out.println("\n=== CURRENT DATA IN FILE ===");
        displayParsedData(dataPath, schemaList);

        // 3. Add New Record with Perfect Alignment
        handleUserInput(dataPath, schemaList);
        
        // 4. Generate Java Files
        generateJavaFiles(schemaList);
    }

    private static void displayParsedData(String path, List<Schema> schemaList) {
        File file = new File(path);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                StringBuilder sb = new StringBuilder("| ");
                for (Schema s : schemaList) {
                    // Precise substring logic: (start-1) to (end)
                    int begin = s.start - 1;
                    int finish = Math.min(s.end, line.length());
                    String val = (begin < line.length()) ? line.substring(begin, finish).trim() : "";
                    sb.append(s.field).append(": ").append(String.format("%-10s", val)).append(" | ");
                }
                System.out.println(sb.toString());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void handleUserInput(String path, List<Schema> schemaList) {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nAdd new record? (y/n): ");
        if (!sc.nextLine().equalsIgnoreCase("y")) return;

        // Determine total line length
        int totalLength = 0;
        for (Schema s : schemaList) if (s.end > totalLength) totalLength = s.end;

        // Create a blank line of spaces
        char[] lineBuffer = new char[totalLength];
        java.util.Arrays.fill(lineBuffer, ' ');

        for (Schema s : schemaList) {
            System.out.print("Enter " + s.field + ": ");
            String input = sc.nextLine();
            int fieldLen = s.end - s.start + 1;
            
            // Truncate if input is too long
            if (input.length() > fieldLen) input = input.substring(0, fieldLen);
            
            // Place characters into buffer at exact indices
            for (int i = 0; i < input.length(); i++) {
                lineBuffer[s.start - 1 + i] = input.charAt(i);
            }
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
            out.println(new String(lineBuffer));
            System.out.println("Record saved successfully.");
            
            System.out.println("\n=== UPDATED DATA VIEW ===");
            displayParsedData(path, schemaList);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void generateJavaFiles(List<Schema> schemaList) {
        // Generates the Record class
        StringBuilder rb = new StringBuilder("package main;\n\npublic class Record {\n");
        for (Schema s : schemaList) rb.append("    public String ").append(s.field).append(";\n");
        rb.append("\n    public Record(");
        for (int i = 0; i < schemaList.size(); i++) rb.append("String ").append(schemaList.get(i).field).append(i < schemaList.size()-1 ? ", " : "");
        rb.append(") {\n");
        for (Schema s : schemaList) rb.append("        this.").append(s.field).append(" = ").append(s.field).append(";\n");
        rb.append("    }\n}");
        
        saveFile("Record.java", rb.toString());
    }

    private static void saveFile(String name, String content) {
        File dir = new File("src/main");
        if (!dir.exists()) dir.mkdirs();
        try (PrintWriter out = new PrintWriter(new File(dir, name))) {
            out.println(content);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
/*VERSION 2*/
//package main;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Schema {
//    String field;
//    int start;
//    int end;
//
//    public Schema(String field, int start, int end) {
//        this.field = field;
//        this.start = start;
//        this.end = end;
//    }
//
//    public static void main(String[] args) {
//        List<Schema> schemaList = new ArrayList<>();
//        
//        // 1. Read the schema configuration
//        try (BufferedReader br = new BufferedReader(new FileReader("schema.txt"))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] parts = line.split("\\s+");
//                if (parts.length == 3) {
//                    schemaList.add(new Schema(parts[0].trim(), 
//                                   Integer.parseInt(parts[1].trim()), 
//                                   Integer.parseInt(parts[2].trim())));
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Ensure schema.txt exists with: FieldName Start End");
//            return;
//        }
//
//        // 2. Generate Record.java
//        generateRecordClass(schemaList);
//
//        // 3. Generate FixedLengthParser.java
//        generateParserClass(schemaList);
//    }
//
//    private static void generateRecordClass(List<Schema> schemaList) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("package main;\n\npublic class Record {\n");
//        
//        // Fields
//        for (Schema s : schemaList) {
//            sb.append("    private String ").append(s.field).append(";\n");
//        }
//
//        // Constructor
//        sb.append("\n    public Record(");
//        for (int i = 0; i < schemaList.size(); i++) {
//            sb.append("String ").append(schemaList.get(i).field)
//              .append(i < schemaList.size() - 1 ? ", " : "");
//        }
//        sb.append(") {\n");
//        for (Schema s : schemaList) {
//            sb.append("        this.").append(s.field).append(" = ").append(s.field).append(";\n");
//        }
//        sb.append("    }\n");
//
//        // Getters (needed for writing back to file)
//        for (Schema s : schemaList) {
//            sb.append("\n    public String get").append(capitalize(s.field)).append("() { return ").append(s.field).append("; }");
//        }
//
//        // toString
//        sb.append("\n\n    @Override\n    public String toString() {\n        return \"Record{\" + ");
//        for (int i = 0; i < schemaList.size(); i++) {
//            sb.append("\"").append(schemaList.get(i).field).append("='\" + ").append(schemaList.get(i).field).append(" + \"'\" + ");
//            if (i < schemaList.size() - 1) sb.append("\", \" + ");
//        }
//        sb.append("\"}\";\n    }\n}");
//
//        writeFile("Record.java", sb.toString());
//    }
//
//    private static void generateParserClass(List<Schema> schemaList) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("package main;\n\nimport java.io.*;\nimport java.util.*;\n\n");
//        sb.append("public class FixedLengthParser {\n");
//
//        // Constants
//        for (Schema s : schemaList) {
//            sb.append("    private static final int ").append(s.field.toUpperCase()).append("_START = ").append(s.start).append(";\n");
//            sb.append("    private static final int ").append(s.field.toUpperCase()).append("_END = ").append(s.end).append(";\n");
//        }
//
//        // Parse Method
//        sb.append("\n    public List<Record> parseFile(String filePath) throws IOException {\n");
//        sb.append("        List<Record> records = new ArrayList<>();\n");
//        sb.append("        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {\n");
//        sb.append("            String line;\n            while ((line = reader.readLine()) != null) {\n");
//        
//        int lastPos = schemaList.get(schemaList.size() - 1).end;
//        sb.append("                if (line.length() < ").append(lastPos).append(") continue;\n");
//
//        for (Schema s : schemaList) {
//            sb.append("                String ").append(s.field).append(" = extractField(line, ")
//              .append(s.field.toUpperCase()).append("_START, ").append(s.field.toUpperCase()).append("_END);\n");
//        }
//
//        sb.append("                records.add(new Record(");
//        for (int i = 0; i < schemaList.size(); i++) {
//            sb.append(schemaList.get(i).field).append(i < schemaList.size() - 1 ? ", " : "");
//        }
//        sb.append("));\n            }\n        }\n        return records;\n    }\n");
//
//        // Write Back Method
//        sb.append("\n    public void saveRecords(String filePath, List<Record> records) throws IOException {\n");
//        sb.append("        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {\n");
//        sb.append("            for (Record r : records) {\n");
//        sb.append("                StringBuilder line = new StringBuilder();\n");
//        // This logic pads strings to match the schema length
//        for (Schema s : schemaList) {
//            int len = s.end - s.start + 1;
//            sb.append("                line.append(String.format(\"%-").append(len).append("s\", r.get").append(capitalize(s.field)).append("()));\n");
//        }
//        sb.append("                writer.println(line.toString());\n            }\n        }\n    }\n");
//
//        // Helper and Main
//        sb.append("\n    private String extractField(String line, int start, int end) {\n");
//        sb.append("        return line.substring(start - 1, Math.min(end, line.length())).trim();\n    }\n");
//        
//        sb.append("\n    public static void main(String[] args) throws IOException {\n");
//        sb.append("        FixedLengthParser p = new FixedLengthParser();\n");
//        sb.append("        List<Record> list = p.parseFile(\"inputfile.txt\");\n");
//        sb.append("        list.forEach(System.out::println);\n");
//        sb.append("    }\n}");
//
//        writeFile("FixedLengthParser.java", sb.toString());
//    }
//
//    private static String capitalize(String str) {
//        return str.substring(0, 1).toUpperCase() + str.substring(1);
//    }
//
//    private static void writeFile(String fileName, String content) {
//        // Specify the path here. Ensure the folders exist!
//        File file = new File("src/main/" + fileName); 
//        try (PrintWriter out = new PrintWriter(file)) {
//            out.println(content);
//            System.out.println("Generated at: " + file.getAbsolutePath());
//        } catch (FileNotFoundException e) {
//            System.err.println("Could not create file. Check if the directory exists.");
//        }
//    }
//}

/*VERSION 2*/
//package main;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Schema {
//	
//	// read schema file
//	String field;
//	int start;
//	int end;
//	
//	// constructor
//	public Schema(String field, int start, int end) {
//		this.field = field;
//		this.start = start;
//		this.end = end;
//	}
//	
//	// tOString method for printing
//	@Override
//	public String toString() {
//		return "Schema [field=" + field + ", start=" + start + ", end=" + end + "]";
//	}
//	
//	public static void main(String[] args) {
//		
//		// reading schema file
//		List<Schema> schemaList = new ArrayList<>();
//		// read file and parse each line to create Schema objects and add to schemaList
//		try (BufferedReader br = new BufferedReader(new FileReader("schema.txt"))) {
//		    String line;
//		    while ((line = br.readLine()) != null) {
//		        String[] parts = line.split(" ");
//		        if (parts.length == 3) {
//		            String field = parts[0].trim();
//		            int start = Integer.parseInt(parts[1].trim());
//		            int end = Integer.parseInt(parts[2].trim());
//		            Schema schema = new Schema(field, start, end);
//		            schemaList.add(schema);
//		        }
//		    }
//		} catch (IOException e) {
//		    e.printStackTrace();
//		}
//				
//		System.out.println(schemaList.toString());
//		StringBuffer buf = new StringBuffer();
//		buf.append("import java.io.BufferedReader;\r\n"
//				+ "import java.io.FileReader;\r\n"
//				+ "import java.io.IOException;\r\n"
//				+ "import java.util.ArrayList;\r\n"
//				+ "import java.util.List;\");\r\n");
//		
//		// 2. Add Class Declaration
//        buf.append("public class ").append("FixedLengthParser").append(" {\n\n");
//        
//        // Static variables for field positions
//        // loop through schemaList to create static variables for each field
//        
//        int i=0;
//        while (i < schemaList.size()) {
//        	Schema schema = schemaList.get(i);
//			buf.append("    private static final int ").append(schema.field.toUpperCase()).append("_START = ").append(schema.start).append(";\n");
//			buf.append("    private static final int ").append(schema.field.toUpperCase()).append("_END = ").append(schema.end).append(";\n");
//			i++;
//		}       
//
//        // 3. Add a Method (using your schemaList)
//        buf.append("    public List<Record> parseFile(String filePath) throws IOException {\n");
//        buf.append("        List<Record> records = new ArrayList<>();\n");
//        buf.append("        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {\n");
//        buf.append("        	 String line;\r\n");
//        buf.append("        while ((line = reader.readLine()) != null) {\r\n");
//        
//        buf.append(" 			if (line.length() < ").append(schemaList.get(schemaList.size()-1).field.toUpperCase()).append("_END) {\n");
//        buf.append("                continue; // Skip lines that are too short\n");
//        buf.append(" 		}");
//        
//        int index=0;
//        while (index < schemaList.size()) {
//			Schema schema = schemaList.get(index);
//			// loop through schemaList to create code for parsing each field and adding to Record object
//			buf.append("                String ").append(schema.field).append(" = extractField(line,").append(schema.field.toUpperCase()).append("_START, ").append(schema.field.toUpperCase()).append("_END).trim();\n");
//			index++;
//        }
//        
//        // create Record object and add to records list
//        buf.append("                Record record = new Record(");
//        index=0;
//        while (index < schemaList.size()) {
//        	Schema schema = schemaList.get(index);
//        	buf.append(schema.field);
//        	if(index < schemaList.size()-1) {
//				buf.append(", ");
//			} else {
//				buf.append(");\n");
//			}
//        	index++;		
//        }
//        
//        // add record to records list
//        buf.append("                records.add(record);\n");
//        buf.append("                }\n");
//        buf.append("		}\n");
//        buf.append("		return records;\n");
//        buf.append("    }\n");
//
//        
//        // declare extractField method to extract field value from line
//        buf.append("	private String extractField(String line, int start, int end) {\n");
//        buf.append("        	return line.substring(start-1, end).trim();\n");
//        buf.append("    }\n");
//
//		System.out.println(buf.toString());
//
//
////        // 5. Write to File
////        try {
////            File file = new File("src/main/java/com/generated/" + className + ".java");
////            file.getParentFile().mkdirs(); // Create directories if they don't exist
////            
////            try (FileWriter writer = new FileWriter(file)) {
////                writer.write(buf.toString());
////                System.out.println("Successfully generated: " + file.getAbsolutePath());
////            }
////        } catch (IOException e) {
////            System.err.println("Error writing the Java file: " + e.getMessage());
////        }
//
//		
//		return;
//
//	}
//}
