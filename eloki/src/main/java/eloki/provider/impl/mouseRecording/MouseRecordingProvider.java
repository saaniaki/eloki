package eloki.provider.impl.mouseRecording;

import eloki.provider.AsHardDiskResourceReader;
import eloki.provider.SelectiveCollectionHDRP;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Only available in `SeleniumClient`. Provides a random previously recorded mouse
 * movement to be replied on the targeted web page.
 */
@Service
@AsHardDiskResourceReader("providers.mouseRecordingsPath")
public final class MouseRecordingProvider extends SelectiveCollectionHDRP<String, List<MouseEvent>, MouseEvent> {

    public MouseRecordingProvider(Environment environment) throws RuntimeException {
        super(environment);
    }

    @Override
    protected List<String> extractKeys(String lineValue) {
        return new ArrayList<>(Arrays.asList(lineValue.split(",")));
    }

    @Override
    protected List<MouseEvent> instantiateElementCollection() {
        return new ArrayList<>();
    }

    public MouseEvent toElement(String line) {
        if (line.equals("click"))
            return new MouseClickEvent();
        else {
            String[] parts = line.split(",");
            short mx = Short.parseShort(parts[0]);
            short my = Short.parseShort(parts[1]);
            float sx = Float.parseFloat(parts[2]);
            float sy = Float.parseFloat(parts[3]);
            return new MouseMovement(mx, my, sx, sy);
        }
    }

}
