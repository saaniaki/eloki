package eloki.provider;

/**
 * A generic interface that provides a random elements of type `T`
 * from a group of elements that are grouped together with key `K`.
 * All providers of this type read and load the valid values into
 * the memory and then provide them when the `provideRandomElement`
 * method has been called.
 *
 * @param <K>
 * @param <T>
 */
public interface SelectiveRandomProvider<K, T> extends Provider {
    T provideRandomElement(K key);
}
