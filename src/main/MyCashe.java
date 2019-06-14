package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyCashe extends LinkedHashMap {
    private static int CAPACITY;
    private static Logger LOGGER;

    private Map<Object, Map.Entry<Integer, Path>> registry;

    public MyCashe(int initialCapacity) {
        super(initialCapacity);
        LOGGER = Logger.getAnonymousLogger();
        CAPACITY = initialCapacity;
        registry = new HashMap<>();
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
            registry.put(key, new SimpleEntry<>(0, null));
            return super.put(key, value);
        }
        registry.put(value.hashCode(), new SimpleEntry<>(0, null));
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
                Path path = registry.get(key).getValue();
                result = Files.readAllBytes(path);
                super.remove(key);
                super.put(key, result);
                Files.delete(path);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Can't get file: " + key, e);
            }
        }
        registry.replace(key, new SimpleEntry<>(registry.get(key).getKey() + 1, registry.get(key).getValue()));
        return result;
    }

    private boolean checkFrequency(Map.Entry eldest) {
        registry.entrySet().stream().sorted(Comparator.comparing(o -> o.getValue().getKey()));
//        ArrayList arrayList = (ArrayList) registry.entrySet().stream()
//                .collect(Collectors.toMap(k -> (UUID) k.getKey(), e -> (Map.Entry<Integer, Path>) e.getValue()))
//                .entrySet().stream()
//                .collect(Collectors.toList());
//
//        arrayList.get(1);
        // TODO: 12.06.2019
//        frequency.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue());
        return true;
    }

    private void dump(Map.Entry eldest) {
        File file = null;
        try {
            file = File.createTempFile("my-cashe." + eldest.getKey(), ".tmp");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Can't create temp file: my-cashe." + eldest.getKey() + ".tmp", e);
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            // T ODO: 12.06.2019  https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
            outputStream.write((byte[]) eldest.getValue());
            outputStream.flush();
            registry.replace(eldest.getKey(), new SimpleEntry((registry.get(eldest.getKey())).getKey(), Paths.get(file.getAbsolutePath())));
            LOGGER.log(Level.INFO, "Dump on disk: " + file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cant create file: " + eldest.getKey(), e);
        }
        remove(eldest.getKey());
    }
}
//