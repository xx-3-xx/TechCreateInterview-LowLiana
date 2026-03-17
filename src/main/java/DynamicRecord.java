import java.util.LinkedHashMap;
import java.util.Map;

class DynamicRecord {
    // Using LinkedHashMap to preserve the order of fields from the schema
    private final Map<String, Object> fields = new LinkedHashMap<>();

    public void set(String name, Object value) {
        fields.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) fields.get(name);
    }

    @Override
    public String toString() {
        return "Record" + fields;
    }
}