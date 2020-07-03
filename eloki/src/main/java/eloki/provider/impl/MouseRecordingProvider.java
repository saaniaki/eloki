package eloki.provider.impl;

import eloki.provider.AsDiskProvider;
import eloki.provider.FromDiskProvider;
import eloki.provider.model.MouseClickEvent;
import eloki.provider.model.MouseEvent;
import eloki.provider.model.MouseMovement;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Only available in `SeleniumClient`. Provides a random previously recorded mouse
 * movement to be replied on the targeted web page.
 */
@Service
@AsDiskProvider("mouseRecordings")
public class MouseRecordingProvider extends FromDiskProvider<List<MouseEvent>> {

    private List<MouseEvent> recordingSlots;

    @Override
    protected void convert(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null)
            this.toElement(line);
        this.elements.add(this.recordingSlots);
    }

    @Override
    protected List<MouseEvent> toElement(String line) {
        if (this.recordingSlots == null) this.recordingSlots = new LinkedList<>();

        if (line.equals("click"))
            this.recordingSlots.add(new MouseClickEvent());
        else {
            String[] parts = line.split(",");
            short mx = Short.parseShort(parts[0]);
            short my = Short.parseShort(parts[1]);
            float sx = Float.parseFloat(parts[2]);
            float sy = Float.parseFloat(parts[3]);
            this.recordingSlots.add(new MouseMovement(mx, my, sx, sy));
        }

        return this.recordingSlots;
    }

}
