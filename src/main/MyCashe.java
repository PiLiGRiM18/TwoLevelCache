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

    private static int MAX_SIZE;
    private static Logger LOG;

    private Map<Object, Path> registry;

    public MyCashe(int initialCapacity, boolean accessOrder) {
        super(initialCapacity, .75f, accessOrder);
        LOG = Logger.getAnonymousLogger();
        MAX_SIZE = initialCapacity;
        registry = new HashMap<>();
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        if (size() > MAX_SIZE) {
            dump(eldest);
            return size() > MAX_SIZE;
        }
        return super.removeEldestEntry(eldest);
    }

    @Override
    public Object put(Object key, Object value) {
        if (key == null) key = value.hashCode();
        registry.put(key, null);
        return super.put(key, value);
    }

    @Override
    public Object get(Object key) {
        Object result = null;
        if (!registry.keySet().contains(key))
            LOG.log(Level.SEVERE, String.format("There is no key %s in the cache!", key));
        if (super.containsKey(key)) result = super.get(key);
        else try {
            Path path = registry.get(key);
            result = Files.readAllBytes(path);
            super.put(key, result);
            Files.delete(path);
            registry.replace(key, null);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't get file: " + key, e);
        }
        return result;
    }

    private void dump(Map.Entry eldest) {
        File file = null;
        try {
            file = File.createTempFile("my-cashe." + eldest.getKey(), ".tmp");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't create temp file: my-cashe." + eldest.getKey() + ".tmp", e);
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            // TODO: 12.06.2019  https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
            outputStream.write((byte[]) eldest.getValue());
            outputStream.flush();
            registry.replace(eldest.getKey(), Paths.get(file.getAbsolutePath()));
            LOG.log(Level.INFO, "Dumped on disk: " + file.getAbsolutePath());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Cant create file: " + eldest.getKey(), e);
        }
        remove(eldest.getKey());
    }
}
