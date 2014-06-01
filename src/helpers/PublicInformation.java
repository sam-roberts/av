package helpers;

import ddf.minim.Minim;
import themidibus.MidiBus;

/**
 * Created by Sam on 5/04/2014.
 */
public class PublicInformation {
    public static final int TEMPO_MIN = 50;
    public static final int TEMPO_MAX = 200;

    public int tempo;
    MidiBus midi;
    Minim minim;
    public PublicInformation() {
    }

    public int getTempo() {
        return tempo;
    }


    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public MidiBus getMidi() {
        return midi;
    }

    public void setMidi(MidiBus midi) {
        this.midi = midi;
    }

    public Minim getMinim() {
        return minim;
    }

    public void setMinim(Minim minim) {
        this.minim = minim;
    }
}
