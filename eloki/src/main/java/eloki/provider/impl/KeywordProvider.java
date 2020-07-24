package eloki.provider.impl;

import eloki.provider.AsHardDiskResourceReader;
import eloki.provider.ElementHDRP;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Only available in `HtmlUnit`. Provides a random word to be put
 * in the `referee` HTTP header of the request. This feature is not
 * being supported by the `SeleniumClient` and is being prevented
 * due to the security concerns.
 */
@Service
@AsHardDiskResourceReader("providers.keywordsPath")
public final class KeywordProvider extends ElementHDRP<String> {

    public KeywordProvider(Environment environment) throws RuntimeException {
        super(environment);
    }

    @Override
    public String toElement(String line) {
        return line;
    }

}
