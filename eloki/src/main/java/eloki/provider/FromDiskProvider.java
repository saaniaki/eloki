package eloki.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public abstract class FromDiskProvider<T> implements Provider<T> {

    private final String path;
    private final URL url;
    protected final List<T> elements = new LinkedList<>();

    public FromDiskProvider() throws RuntimeException {
        AsProvider annotation = this.getClass().getAnnotation(AsProvider.class);
        if (annotation == null)
            throw new RuntimeException("Make sure the provider as the 'AsProvider' annotation.");
        this.path = annotation.value();
        this.url = PathProvider.class.getClassLoader().getResource(this.path);
        try {
            this.registerValuesFromSingleDirectory();
        } catch (URISyntaxException e) {
            System.out.println("Could not load " + this.path);
            e.printStackTrace();
        }
    }

    @Override
    public T provideRandomElement() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, this.elements.size());
        return this.elements.get(randomAgentIndex);
    }

    protected void registerValuesFromSingleDirectory() throws URISyntaxException {
        System.out.println("Registering " + this.path + "...");
        File dir = new File(Objects.requireNonNull(this.url).toURI());
        File[] directoryListing;
        if (dir.isDirectory() && (directoryListing = dir.listFiles()) != null)
            for (File singleFile : directoryListing)
                this.registerValuesFromSingleFile(singleFile);
        else
            this.registerValuesFromSingleFile(dir);
        System.out.println("Registered " + this.elements.size() + " element(s) by loading " + this.path);
    }

    protected void registerValuesFromSingleFile(File singleFile) {
        if (singleFile == null) return;
        try (FileReader reader = new FileReader(singleFile);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            this.convert(bufferedReader);
        } catch (Exception e) {
            System.out.println("Could not read single file at " + singleFile.getPath());
            e.printStackTrace();
        }
    }

    protected void convert(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null)
            this.elements.add(this.toElement(line));
    }

    protected abstract T toElement(String line);

}
