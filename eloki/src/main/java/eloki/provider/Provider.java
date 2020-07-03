package eloki.provider;

/**
 * `Provider<T>` is a generic class which provides random elements of type `T`.
 * All providers should be Spring Singleton beans in the service layer that provide
 * inputs to the clients in a random fashion.
 * @param <T>
 */
public interface Provider<T> {
    T provideRandomElement();
}
