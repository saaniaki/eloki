package eloki.provider.impl;

import eloki.provider.AsDiskProvider;
import eloki.provider.FromDiskProvider;
import org.springframework.stereotype.Service;

/**
 * Only available in `HtmlUnit`. Provides a random link href to be clicked
 * on. The list of anchors is located at `resources/anchors`. `SeleniumClient`
 * is not using this provides since is uses the reply of a mouse movement to
 * click on any of the page elements.
 */
@Service
@AsDiskProvider("anchors")
public class AnchorProvider extends FromDiskProvider<String> {

    @Override
    protected String toElement(String line) {
        return line;
    }

}
