package eloki.provider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * `Provider<T>` is a generic class which provides random elements of type `T`.
 * All providers should be Spring Singleton beans in the service layer that provide
 * inputs to the clients in a random fashion.
 *
 * @param <T>
 */
public abstract class Provider<T> {

    protected final List<T> elements = new LinkedList<>();

    public T provideRandomElement() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, this.elements.size());
        return this.elements.get(randomAgentIndex);
    }

}
