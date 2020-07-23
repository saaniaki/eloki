package eloki.provider;

import eloki.provider.impl.path.PathProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Providers can read values from disk, network or they can even calculate them.
 * As of now, all providers read values from Disk and therefore, the abstract
 * `HardDiskResourceReader` class has been created which then can be extended for
 * more specific use cases. Any class that extends `HardDiskResourceReader` must also
 * be annotated with `@AsHardDiskResourceReader(String path)`.
 */
public abstract class HardDiskResourceReader {
    private static final Logger logger = LoggerFactory.getLogger(HardDiskResourceReader.class);

    private final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    private final String path;
    private final URL url;

    public HardDiskResourceReader(Environment environment) throws RuntimeException {
        AsHardDiskResourceReader annotation = this.getClass().getAnnotation(AsHardDiskResourceReader.class);
        if (annotation == null)
            throw new RuntimeException("Make sure the provider as the 'AsHardDiskResourceReader' annotation.");
        this.path = environment.getProperty(annotation.value());
        if (this.path == null)
            throw new RuntimeException("Make sure " + annotation.value() + " property exists and corresponds to a valid file on resources.");
        this.url = PathProvider.class.getClassLoader().getResource(this.path);
    }

    protected abstract void convert(BufferedReader bufferedReader) throws Exception;

    protected abstract int getNumberOfElements();

    protected void safeRegister() {
        try {
            this.registerValuesFromSingleDirectory();
        } catch (Exception e) {
            logger.error("Could not load " + this.path);
            e.printStackTrace();
        }
    }

    private void registerValuesFromSingleDirectory() throws Exception {
        logger.debug("Registering " + this.path + ".");
        if (this.jarFile.isFile()) {  // Running via the fat JAR file
            try (JarFile jar = new JarFile(this.jarFile)) {
                final Enumeration<JarEntry> entries = jar.entries(); // Gives ALL entries in jar
                while (entries.hasMoreElements()) {
                    final String name = entries.nextElement().getName();
                    if (!name.startsWith(this.path + "/") && !name.equals(this.path) || name.equals(this.path + "/"))
                        continue;
                    InputStream resourceAsStream = HardDiskResourceReader.class.getClassLoader().getResourceAsStream(name);
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
        logger.info("Registered " + this.getNumberOfElements() + " element(s) by loading " + this.path);
    }

    private void registerValuesFromFile(File singleFile) {
        if (singleFile == null) return;
        try (FileReader reader = new FileReader(singleFile)) {
            this.registerValuesFromReader(reader, singleFile.getPath());
        } catch (Exception e) {
            logger.error("Could not read single file at " + singleFile.getPath(), e);
        }
    }

    private void registerValuesFromInputStream(InputStream inputStream, String path) {
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

}
