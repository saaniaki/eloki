package eloki.provider.impl;

import eloki.provider.AsDiskProvider;
import eloki.provider.FromDiskProvider;
import org.springframework.stereotype.Service;

/**
 * Only available in `HtmlUnit`. Provides a random word to be put
 * in the `referee` HTTP header of the request. This feature is not
 * being supported by the `SeleniumClient` and is being prevented
 * due to the security concerns. The list of keywords is located at
 * `resources/anchors`.
 */
@Service
@AsDiskProvider("keywords")
public class KeywordProvider extends FromDiskProvider<String> {

    @Override
    protected String toElement(String line) {
        return line;
    }

}
