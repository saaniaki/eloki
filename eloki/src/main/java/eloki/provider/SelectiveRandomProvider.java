package eloki.provider;

public interface SelectiveRandomProvider<K, T> extends Provider {
    T provideRandomElement(K key);
}
