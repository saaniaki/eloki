package eloki.provider;

/**
 * A generic interface that provides random elements of type `T`.
 * All providers of this type read and load the valid values into
 * the memory and then provide them when the `provideRandomElement`
 * method has been called.
 *
 * @param <T>
 */
public interface ElementRandomProvider<T> extends Provider {
    T provideRandomElement();
}
