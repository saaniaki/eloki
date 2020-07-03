package eloki.provider.impl;

import eloki.provider.AsDiskProvider;
import eloki.provider.FromDiskProvider;
import org.springframework.stereotype.Service;

/**
 * Provides a random path under the main domain to distribute the
 * requests evenly. All the available paths should be listed in
 * `resources/paths`.
 */
@Service
@AsDiskProvider("paths")
public class PathProvider extends FromDiskProvider<String> {

    @Override
    protected String toElement(String line) {
        return line;
    }

}
