package main;

public class Record {
    private String name;
    private String gender;
    private String age;

    public Record(String name, String gender, String age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getAge() { return age; }

    @Override
    public String toString() {
        return "Record{" + "name='" + name + "'" + ", " + "gender='" + gender + "'" + ", " + "age='" + age + "'" + "}";
    }
}
