import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RecordMerger {
	
	// Merges two records by concatenating their fields with a space in between
	
	public record Record(String name, String gender, int age) {}
	
	public Map<String, Record> mergeRecords(String filePath) {
		Map<String, Record> mergeMap = new HashMap<>();
	    
		// Read record from the file
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				//basic validation to ensure the line has enough characters to parse
				if (line.length() < 25) {
					continue; // Skip lines that are too short
			
				}
				
				// Parse the line to create a Record object
				Record current = parseLine(line);
												
				// use merge method to merge records with the same name
				mergeMap.merge(current.name().toLowerCase(), current, (existing, replacement) -> {
					return replacement.age() > existing.age() ? replacement : existing; // Keep the record with the greater age
				});
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mergeMap;
	}
	
	public Record parseLine(String line) {
		String name = line.substring(0, 20).trim();
		String gender = line.substring(20, 21).trim();
		int age;
		try {
			age = Integer.parseInt(line.substring(21, 25).trim());
		} catch (NumberFormatException e) {
			return null; // Skip lines with invalid age format
		}
		return new Record(name,gender,age);		
	}
}
