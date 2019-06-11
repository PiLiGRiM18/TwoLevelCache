package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyCashe extends HashMap {

    List<String> registry = new ArrayList<>();

    public Object put(Object value) {
        UUID uuid = UUID.randomUUID();
        registry.add(String.valueOf(uuid));
        return super.put(uuid, value);
    }
}
