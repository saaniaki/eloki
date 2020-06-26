package eloki.provider.impl;

import eloki.provider.AsProvider;
import eloki.provider.FromDiskProvider;
import org.springframework.stereotype.Service;

@Service
@AsProvider("agents")
public class BrowserProvider extends FromDiskProvider<String> {

    @Override
    protected String toElement(String line) {
        return line;
    }

}
