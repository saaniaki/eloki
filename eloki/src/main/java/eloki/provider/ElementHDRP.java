package eloki.provider;

import org.springframework.core.env.Environment;

import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ElementHDRP<T> extends HardDiskResourceReader implements ElementRandomProvider<T> {

    protected final List<T> elements = new LinkedList<>();

    protected abstract T toElement(String line) throws Exception;

    public ElementHDRP(Environment environment) throws RuntimeException {
        super(environment);
        this.safeRegister();
    }

    @Override
    protected void convert(BufferedReader bufferedReader) throws Exception {
        String line;
        while ((line = bufferedReader.readLine()) != null)
            this.elements.add(this.toElement(line));
    }

    @Override
    protected int getNumberOfElements() {
        return this.elements.size();
    }

    @Override
    public T provideRandomElement() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, this.elements.size());
        return this.elements.get(randomAgentIndex);
    }

}
