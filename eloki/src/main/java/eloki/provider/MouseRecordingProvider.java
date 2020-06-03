package eloki.provider;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
@AsProvider("mouseRecordings")
public class MouseRecordingProvider extends FromDiskProvider<List<MouseCoordination>> {

    private List<MouseCoordination> recordingSlots;

    @Override
    protected void convert(BufferedReader bufferedReader) throws IOException {
        String line;
        while ((line = bufferedReader.readLine()) != null)
            this.toElement(line);
        this.elements.add(this.recordingSlots);
    }

    @Override
    protected List<MouseCoordination> toElement(String line) {
        if(this.recordingSlots == null) this.recordingSlots = new LinkedList<>();

        if(line.equals("\"click\""))
            return null;

        String[] parts = line.split(",");
        short mx = Short.parseShort(parts[0].split(":")[1]);
        short my = Short.parseShort(parts[1].split(":")[1]);
        float sx = Float.parseFloat(parts[2].split(":")[1]);
        float sy = Float.parseFloat(parts[3].split(":")[1]);
        this.recordingSlots.add(new MouseCoordination(mx, my, sx, sy));
        return this.recordingSlots;
    }

}
