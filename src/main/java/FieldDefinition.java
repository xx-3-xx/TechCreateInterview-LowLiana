import java.io.*;
import java.util.*;

public record FieldDefinition(String name, int start, int end) {
    public Object convert(String extracted) {
        return extracted; // Assuming string type for now
    }
}

