package eloki.provider.impl.path;

import eloki.provider.AsHardDiskResourceReader;
import eloki.provider.ElementHDRP;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Provides a random path under the main domain to distribute the
 * requests evenly. All the available paths should be listed in
 * `resources/paths`.
 */
@Service
@AsHardDiskResourceReader("providers.pathsPath")
public final class PathProvider extends ElementHDRP<PathInfo> {

    public PathProvider(Environment environment) throws RuntimeException {
        super(environment);
    }

    @Override
    protected PathInfo toElement(String line) throws Exception {
        String[] parts = line.split("::", 3);
        if (parts.length == 1)
            return new PathInfo(parts[0]);
        else if (parts.length == 2)
            return new PathInfo(parts[0], Short.parseShort(parts[1]));
        else if (parts.length == 3)
            return new PathInfo(parts[0], Short.parseShort(parts[1]), parts[2]);
        throw new Exception("Something went wrong while parsing the paths.");
    }

}
