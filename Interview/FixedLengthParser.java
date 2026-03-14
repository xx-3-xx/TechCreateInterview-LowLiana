package main;

import java.io.*;
import java.util.*;

public class FixedLengthParser {
    private static final int NAME_START = 1;
    private static final int NAME_END = 20;
    private static final int GENDER_START = 20;
    private static final int GENDER_END = 21;
    private static final int AGE_START = 22;
    private static final int AGE_END = 25;

    public List<Record> parseFile(String filePath) throws IOException {
        List<Record> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() < 25) continue;
                String name = extractField(line, NAME_START, NAME_END);
                String gender = extractField(line, GENDER_START, GENDER_END);
                String age = extractField(line, AGE_START, AGE_END);
                records.add(new Record(name, gender, age));
            }
        }
        return records;
    }

    public void saveRecords(String filePath, List<Record> records) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (Record r : records) {
                StringBuilder line = new StringBuilder();
                line.append(String.format("%-20s", r.getName()));
                line.append(String.format("%-2s", r.getGender()));
                line.append(String.format("%-4s", r.getAge()));
                writer.println(line.toString());
            }
        }
    }

    private String extractField(String line, int start, int end) {
        return line.substring(start - 1, Math.min(end, line.length())).trim();
    }

    public static void main(String[] args) throws IOException {
        FixedLengthParser p = new FixedLengthParser();
        List<Record> list = p.parseFile("data.txt");
        list.forEach(System.out::println);
    }
}
