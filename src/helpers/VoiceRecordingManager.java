package helpers;

import java.util.ArrayList;

/**
 * Created by Sam on 26/07/2014.
 */
public class VoiceRecordingManager {

    public ArrayList<Sample> voices;


    public VoiceRecordingManager() {
        this.voices = new ArrayList<Sample>();
    }

    public void insertSample(Sample s) {
        voices.add(s);
    }

    public int getNumSamples() {
        return voices.size();
    }

    public String getNextFilepath() {

        return "";
    }
}
