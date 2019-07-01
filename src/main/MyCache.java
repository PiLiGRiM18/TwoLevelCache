package main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyCache<K, V extends Serializable> extends LinkedHashMap<K, V> {

    private static int MAX_SIZE;
    private static Logger LOG;

    private Map<K, Path> registry;

    public MyCache(int initialCapacity, boolean accessOrder) {
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
    public V put(K key, V value) {
        registry.put(key, null);
        return super.put(key, value);
    }

    public V put(V value) {
        K key = (K) UUID.randomUUID();
        registry.put(key, null);
        return super.put(key, value);
    }

    @Override
    public V get(Object key) {
        V result = null;
        if (!registry.keySet().contains(key))
            LOG.log(Level.SEVERE, String.format("There is no key %s in the cache!", key));
        if (super.containsKey(key)) result = super.get(key);
        else try {
            Path path = registry.get(key);
            result = (V) Files.readAllBytes(path);
            put((K) key, result);
            Files.delete(path);
            registry.replace((K) key, null);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't get file: " + key, e);
        }
        return result;
    }

    private void dump(Map.Entry eldest) {
        File file = null;
        try {
            file = File.createTempFile("my-cache." + eldest.getKey(), ".tmp");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't create temp file: my-cache." + eldest.getKey() + ".tmp", e);
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write((byte[]) eldest.getValue());
            outputStream.flush();
            registry.replace((K) eldest.getKey(), Paths.get(file.getAbsolutePath()));
            LOG.log(Level.INFO, "Dumped on disk: " + file.getAbsolutePath());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Cant create file: " + eldest.getKey(), e);
        }
        remove(eldest.getKey());
    }

    @Override
    public String toString() {
        return "MyCache{" +
                "registry=\n" + registry.toString().replace(",", "\n") +
                '}';
    }
}
