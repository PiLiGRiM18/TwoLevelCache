package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyCashe extends LinkedHashMap {
    private static int MAX_SIZE = 0;

    private Map<Object, Path> registry = new HashMap<>();
    private Map<Object, Integer> frequency = new HashMap<>();

    public MyCashe(int initialCapacity) {
        super(initialCapacity);
        MAX_SIZE = initialCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        if (size() > MAX_SIZE && checkFrequency(eldest)) {
            dump(eldest);
            return size() > MAX_SIZE;
        }
        return super.removeEldestEntry(eldest);
    }

    @Override
    public Object put(Object key, Object value) {
        if (key != null) {
            frequency.put(key, 0);
            return super.put(key, value);
        }
        frequency.put(value.hashCode(), 0);
        return super.put(value.hashCode(), value);
    }

    @Override
    public Object get(Object key) {
        Object result = null;
        if (!containsKey(key) && !registry.keySet().contains(key)) {
            System.err.println(String.format("There is no key %s in the cache!", key));
        }
        if (containsKey(key)) {
            result = super.get(key);
        } else {
            try {
                Path path = registry.get(key);
                result = Files.readAllBytes(path);
                registry.remove(key);
                super.remove(key);
                super.put(key, result);
                new File(path.toUri()).delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        frequency.replace(key, frequency.get(key) + 1);
        return result;
    }

    private boolean checkFrequency(Map.Entry eldest) {
        // TODO: 12.06.2019
        frequency.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue());
        return true;
    }

    private void dump(Map.Entry eldest) {
        File file = null;
        try {
            file = File.createTempFile("MyCashe_" + eldest.getKey(), "_temp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write((byte[]) eldest.getValue());
            outputStream.flush();
            System.out.println(String.format("The file has been dumped on disk: %s", file.getAbsolutePath()));
            registry.put(eldest.getKey(), Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            System.err.println("Cant create file: " + eldest.getKey());
            e.printStackTrace();
        }
        remove(eldest.getKey());
    }
}
