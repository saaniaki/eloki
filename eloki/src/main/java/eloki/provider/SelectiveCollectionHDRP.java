package eloki.provider;

import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The purpose of this class is to provide a random value out of a big buckets of valid values read and loaded from the hard disk. Each bucket is
 * associated with a specific key of type `K` to make the `Client` able to pick values more thoughtfully.
 *
 * @param <K>
 * @param <T>
 * @param <M>
 */
public abstract class SelectiveCollectionHDRP<K, T extends Collection<M>, M> extends HardDiskResourceReader implements SelectiveRandomProvider<K, T> {

    private final Map<K, List<T>> map = new HashMap<>();

    protected abstract List<K> extractKeys(String lineValue) throws Exception;

    protected abstract T instantiateElementCollection();

    public abstract M toElement(String line) throws Exception;

    public SelectiveCollectionHDRP(Environment environment) throws RuntimeException {
        super(environment);
        this.safeRegister();
    }

    @Override
    protected void convert(BufferedReader bufferedReader) throws Exception {
        String line = bufferedReader.readLine();
        String[] parts = line.split("keys=");
        if (parts.length != 2)
            throw new IOException("File format is invalid. Consider putting the keys in the first line. Use this format: keys=<element>,<another_element>");

        List<K> keys = this.extractKeys(parts[1]);

        T list = this.instantiateElementCollection();
        while ((line = bufferedReader.readLine()) != null)
            list.add(this.toElement(line));

        for (K key : keys) {
            List<T> listOfCollections = this.map.getOrDefault(key, new ArrayList<>());
            listOfCollections.add(list);
            this.map.put(key, listOfCollections);
        }
    }

    @Override
    public int getNumberOfElements() {
        return this.map.size();
    }

    @Override
    public T provideRandomElement(K key) {
        List<T> listOfCollections = this.map.get(key);
        if (listOfCollections == null) return this.instantiateElementCollection();
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, listOfCollections.size());
        return listOfCollections.get(randomAgentIndex);
    }

}
