package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MyCashe extends LinkedHashMap {
    private static int MAX_SIZE = 0;
    Map<UUID, String> registry = new HashMap<>();

    public MyCashe(int initialCapacity) {
        super(initialCapacity);
        MAX_SIZE = initialCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        if (size() > MAX_SIZE) {
            File file = null;
            try {
                file = File.createTempFile("MyCashe_" + eldest.getKey(), "_temp");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (OutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write((byte[]) eldest.getValue());
                outputStream.flush();
                registry.put((UUID) eldest.getKey(), file.getAbsolutePath());
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
        Object result = null;
        if (containsKey(key)) {
            result = super.get(key);
        } else {
            try {
                Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(registry.get(key))).toURI());
                result = Files.readAllBytes(path);
                registry.remove(key);
                super.remove(key);
                super.put(key, result);
                new File(path.toUri()).delete();
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
