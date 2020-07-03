package eloki.provider.impl;

import eloki.provider.AsDiskProvider;
import eloki.provider.FromDiskProvider;
import org.springframework.stereotype.Service;

/**
 * Provides an agent name from all the available agent names available
 * in the text files located under `resources/agents`. As of now, there
 * 9448 agents listed.
 */
@Service
@AsDiskProvider("agents")
public class BrowserProvider extends FromDiskProvider<String> {

    @Override
    protected String toElement(String line) {
        return line;
    }

}
