package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class MyCashe extends LinkedHashMap {

    private static int MAX_SIZE = 0;
    List<Object> registry = new ArrayList<>();

    public MyCashe(int initialCapacity) {
        super(initialCapacity);
        MAX_SIZE = initialCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        if (size() > MAX_SIZE) {
            registry.add(eldest.getKey());
            try {
                File file = File.createTempFile(String.valueOf(eldest.getKey()), "_temp");
                OutputStream os = new FileOutputStream(file);
                os.write((byte[]) eldest.getValue());
                os.flush();
                os.close();
            } catch (IOException e) {
                System.err.println("Cant create file: " + eldest.getKey());
                e.printStackTrace();
            }
            remove(eldest.getKey());
            return size() > MAX_SIZE;
        }
        return super.removeEldestEntry(eldest);
    }

    @Override
    public Object put(Object key, Object value) {
        if (key != null) {
            return super.put(key, value);
        }
        return super.put(UUID.randomUUID(), value);
    }

    @Override
    public Object get(Object key) {
        return super.get(key);
    }
}
