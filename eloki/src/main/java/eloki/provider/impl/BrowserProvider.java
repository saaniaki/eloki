package eloki.provider.impl;

import eloki.provider.AsHardDiskResourceReader;
import eloki.provider.ElementHDRP;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Provides an agent name from all the available agent names available.
 */
@Service
@AsHardDiskResourceReader("providers.agentsPath")
public final class BrowserProvider extends ElementHDRP<String> {

    public BrowserProvider(Environment environment) throws RuntimeException {
        super(environment);
    }

    @Override
    public String toElement(String line) {
        return line;
    }

}
