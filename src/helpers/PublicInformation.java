package helpers;

import animations.ColourManager;
import animations.LinkedAnimations;
import ddf.minim.AudioOutput;
import ddf.minim.Minim;
import processing.core.PImage;
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
    private ColourManager colourManager;
    private AudioOutput audioOut;

    private final String rootDirectory;
    private LinkedAnimations links;
    private PImage circle;

    public PublicInformation(String rootDirectory) {
        this.rootDirectory = rootDirectory;
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

    public int getMsCount(double duration) {
            return (int)(60/(float)(getTempo() * duration)*4* 1000) * 4;


    }

    public ColourManager getColourManager() {
        return colourManager;
    }

    public void setColourManager(ColourManager colourManager) {
        this.colourManager = colourManager;
    }

    public int getDurationMS (double DURATION) {
        return (int)(60/(float)(getTempo() * DURATION)*4* 1000);

    }

    public void setAudioOut(AudioOutput audioOut) {
        this.audioOut = audioOut;
    }

    public AudioOutput getAudioOut() {
        return audioOut;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public LinkedAnimations getLinks() {
        return links;
    }

    public void setLinks(LinkedAnimations links) {
        this.links = links;
    }

    public PImage getCircle() {
        return circle;
    }

    public void setCircle(PImage circle) {
        this.circle = circle;
    }
}
