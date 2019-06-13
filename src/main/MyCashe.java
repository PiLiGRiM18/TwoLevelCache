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
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyCashe extends LinkedHashMap {
    private static int CAPACITY;

    private Map<Object, Path> registry = new HashMap<>();
    private Map<Object, Integer> frequency = new HashMap<>();

    private static Logger LOGGER = Logger.getAnonymousLogger();

    public MyCashe(int initialCapacity) {
        super(initialCapacity);
        CAPACITY = initialCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        if (size() > CAPACITY && checkFrequency(eldest)) {
            dump(eldest);
            return size() > CAPACITY;
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
            LOGGER.log(Level.SEVERE, String.format("There is no key %s in the cache!", key));
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
                Files.delete(path);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can't get file: " + key, e);
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
            LOGGER.log(Level.SEVERE, "Can't create temp file: MyCashe_" + eldest.getKey() + "_temp", e);
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            // T ODO: 12.06.2019  https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
            outputStream.write((byte[]) eldest.getValue());
            outputStream.flush();
            LOGGER.log(Level.INFO, "The file has been dumped on disk: " + file.getAbsolutePath());
            registry.put(eldest.getKey(), Paths.get(file.getAbsolutePath()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cant create file: " + eldest.getKey(), e);
        }
        remove(eldest.getKey());
    }
}
//