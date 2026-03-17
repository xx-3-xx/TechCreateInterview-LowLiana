public class Record {
	
	public final String name;
	private final String gender;
	private final String age;
	
	public Record(String name, String gender, String age) {
		this.name = name;
		this.gender = gender;
		this.age = age;
	}
	
	@Override
	public String toString() {
		return "Record{name='" + name + "', gender='" + 
	gender + "', age='" + age ;
	}
	
}
