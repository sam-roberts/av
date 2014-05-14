package helpers;

/**
 * Created by Sam on 5/04/2014.
 */
public class Synths {
    public static final int NUM_SYNTHS = 16;
    public static final int SYNTH_1 = 1;
    private Synth[] synthsArray;

    int tempo;

    public Synths(PublicInformation info) {
        this.synthsArray = new Synth[NUM_SYNTHS];
        synthsArray[SYNTH_1 - 1] = new BassMidi(info, SYNTH_1);


    }

    public Synth[] getSynthsArray() {
        return synthsArray;
    }

    public Synth getSynth(int which) {
        return synthsArray[which - 1];

    }

}
