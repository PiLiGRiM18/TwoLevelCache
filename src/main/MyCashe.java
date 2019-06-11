package main;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyCashe extends HashMap {

    List<String> registry = Collections.EMPTY_LIST;


    public Object put(Object value) {
        return super.put(UUID.randomUUID(), value);
    }
}
