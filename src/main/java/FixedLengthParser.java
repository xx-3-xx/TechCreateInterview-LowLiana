import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FixedLengthParser {
	
	private static final int NAME_START = 1;
	private static final int NAME_END = 20;
    private static final int GENDER_START = 20;
    private static final int GENDER_END = 21;
    private static final int AGE_START = 22;
    private static final int AGE_END = 25;

   public List<Record> parseFile (String filePath) throws IOException{
	   List <Record> records = new ArrayList <>();
	   try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
		   String line;
		   while((line = reader.readLine())!=null) {
			   if(line.length()<AGE_END) {
				   continue;
			   }
			   
			   String name = extractField(line, NAME_START, NAME_END);
			   String gender = extractField(line, GENDER_START, GENDER_END);
			   String age = extractField(line, AGE_START, AGE_END);
			   
			   Record record = new Record(name,gender,age);
			   records.add(record);
		   }
	   }
	   return records;
   }
   
   public String extractField(String line, int start, int end) {
	   return line.substring(start-1,end);
   }
   
   public static void main(String[] args) {
	   FixedLengthParser parser = new FixedLengthParser();
	   try {
		   List<Record> records = parser.parseFile("");
		   for (Record record:records) {
			   System.out.println(record);
		   }
	   }catch(IOException e) {
		   e.printStackTrace();
	   }
   } 
}
