package helpers;

/**
 * Created by Sam on 5/04/2014.
 */
public class BassMidi extends Synth implements Runnable {


    public BassMidi(PublicInformation info, int channel) {
        super(info, channel);
    }

    @Override
    void myNotes () {
        playNote(dummyNote(Notes.C3, 127), Duration.EIGHTH);
        playNote(dummyNote(Notes.C4, 127), Duration.EIGHTH);
        playNote(dummyNote(Notes.G3, 127), Duration.EIGHTH);
        playNote(dummyNote(Notes.D3_SHARP, 127), Duration.EIGHTH);
    }





}
