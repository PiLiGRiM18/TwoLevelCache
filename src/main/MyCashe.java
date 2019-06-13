package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

public class MyCashe extends LinkedHashMap {
    private static int MAX_SIZE = 0;

//    private Map<CasheItem, Path> registry = new HashMap<>();
//    private Map<Object, Integer> frequency = new HashMap<>();

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
        CasheItem casheItem = new CasheItem(value);
        if (key != null) {
            return super.put(key, casheItem);
        }
        return super.put(casheItem.hashCode(), value);
    }

    @Override
    public Object get(Object key) {
        CasheItem result = null;
        if (!containsKey(key)) {
            System.err.println(String.format("There is no key %s in the cache!", key));
        }
        if (containsKey(key)) {
            result = (CasheItem) super.get(key);
        } else {
            try {
                Path path = ((CasheItem) super.get(key)).getPath();
                result = Files.readAllBytes(path);

                super.remove(key);
                super.put(key, result);
                new File(path.toUri()).delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        result.incrementFrequency();
        return result;
    }

    private boolean checkFrequency(Map.Entry eldest) {
        // TODO: 12.06.2019
//        frequency.entrySet()
//                .stream()
//                .sorted(Map.Entry.comparingByValue());
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
            // TODO: 12.06.2019  https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
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

class CasheItem {
    private Object value;
    private long frequency;
    private Path path;

    public CasheItem(Object value) {
        this.value = value;
        this.frequency = 0;
        this.path = null;
    }

    public Object getValue() {
        return value;
    }

    public long getFrequency() {
        return frequency;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void incrementFrequency() {
        frequency++;
    }
}
