package eloki.provider;

import eloki.provider.impl.PathProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Providers can read values from disk, network or they can even calculate them.
 * As of now, all providers read values from Disk and therefore, the abstract
 * `FromDiskProvider<T>` class has been created which then can be extended for
 * more specific use cases. Any clas that extends `FromDiskProvider<T>` must also
 * be annotated with `@AsDiskProvider(String path)`.
 * @param <T>
 */
public abstract class FromDiskProvider<T> implements Provider<T> {
    private static final Logger logger = LoggerFactory.getLogger(FromDiskProvider.class);

    private final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    private final String path;
    private final URL url;
    protected final List<T> elements = new LinkedList<>();

    public FromDiskProvider() throws RuntimeException {
        AsDiskProvider annotation = this.getClass().getAnnotation(AsDiskProvider.class);
        if (annotation == null)
            throw new RuntimeException("Make sure the provider as the 'AsDiskProvider' annotation.");
        this.path = annotation.value();
        this.url = PathProvider.class.getClassLoader().getResource(this.path);
        try {
            this.registerValuesFromSingleDirectory();
        } catch (Exception e) {
            logger.error("Could not load " + this.path);
            e.printStackTrace();
        }
    }

    @Override
    public T provideRandomElement() {
        int randomAgentIndex = ThreadLocalRandom.current().nextInt(0, this.elements.size());
        return this.elements.get(randomAgentIndex);
    }

    protected void registerValuesFromSingleDirectory() throws Exception {
        logger.debug("Registering " + this.path + ".");
        if (this.jarFile.isFile()) {  // Running via the fat JAR file
            try (JarFile jar = new JarFile(this.jarFile)) {
                final Enumeration<JarEntry> entries = jar.entries(); // Gives ALL entries in jar
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (!name.startsWith(this.path + "/") && !name.equals(this.path) || name.equals(this.path + "/"))
                        continue;
                    InputStream resourceAsStream = FromDiskProvider.class.getClassLoader().getResourceAsStream(name);
                    this.registerValuesFromInputStream(resourceAsStream, name);
                }
            }
        } else { // Running via the IDE, resources are on hard disk
            File file = new File(Objects.requireNonNull(this.url).toURI());
            File[] directoryListing;
            if (file.isDirectory() && (directoryListing = file.listFiles()) != null)
                for (File singleFile : directoryListing)
                    this.registerValuesFromFile(singleFile);
            else
                this.registerValuesFromFile(file);
        }
        logger.info("Registered " + this.elements.size() + " element(s) by loading " + this.path);
    }

    protected void registerValuesFromFile(File singleFile) {
        if (singleFile == null) return;
        try (FileReader reader = new FileReader(singleFile)) {
            this.registerValuesFromReader(reader, singleFile.getPath());
        } catch (Exception e) {
            logger.error("Could not read single file at " + singleFile.getPath(), e);
        }
    }

    protected void registerValuesFromInputStream(InputStream inputStream, String path) {
        if (inputStream == null) return;
        try (Reader reader = new InputStreamReader(inputStream)) {
            this.registerValuesFromReader(reader, path);
        } catch (IOException e) {
            logger.error("Could not create Reader when trying to read " + path, e);
        }
    }

    private void registerValuesFromReader(Reader reader, String path) {
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            this.convert(bufferedReader);
        } catch (Exception e) {
            logger.error("Could not inputStream at " + path, e);
        }
    }

    protected void convert(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null)
            this.elements.add(this.toElement(line));
    }

    protected abstract T toElement(String line);

}
