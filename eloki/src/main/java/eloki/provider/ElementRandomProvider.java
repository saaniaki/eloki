package eloki.provider;

/**
 * `Provider<T>` is an interface which provides random elements of type `T`.
 * All providers should be Spring Singleton beans in the service layer that provide
 * inputs to the clients in a random fashion.
 *
 * @param <T>
 */
public interface ElementRandomProvider<T> extends Provider {
    T provideRandomElement();
}
