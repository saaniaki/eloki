package eloki.provider.impl;

import eloki.provider.AsHardDiskResourceReader;
import eloki.provider.ElementHDRP;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Only available in `HtmlUnit`. Provides a random link href to be clicked
 * on. The list of anchors is located at `resources/anchors`. `SeleniumClient`
 * is not using this provides since is uses the reply of a mouse movement to
 * click on any of the page elements.
 */
@Service
@AsHardDiskResourceReader("providers.anchorsPath")
public final class AnchorProvider extends ElementHDRP<String> {

    public AnchorProvider(Environment environment) throws RuntimeException {
        super(environment);
    }

    @Override
    protected String toElement(String line) {
        return line;
    }

}
