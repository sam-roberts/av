package helpers;

import animations.MovableBox;
import animations.RotatePlayers;
import animations.Rotater;
import processing.core.PApplet;

import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Sam on 12/10/2014.
 */
public class SampleLibrary {
    ArrayList<ArrayList<MovableBox>> libraries;

    PublicInformation info;
    String fileRoot;
    PApplet p;
    private int currentLibrary;
    public SampleLibrary(PApplet p, RotatePlayers rotors, PublicInformation information) {
        this.info = information;
        fileRoot = information.getRootDirectory() + "sounds\\";
        this.p = p;
        currentLibrary = 0;
        libraries = new ArrayList<ArrayList<MovableBox>>();

        ArrayList<MovableBox> library0 = new ArrayList<MovableBox>();
        libraries.add(library0);

        generatePresetOne(rotors);
        generatePresetTwo(rotors);
        generatePresetOne(rotors);
        generatePresetOne(rotors);
        generatePresetOne(rotors);
        generatePresetOne(rotors);
        generatePresetOne(rotors);
        generatePresetOne(rotors);


    }

    private void generatePresetOne(RotatePlayers rotors) {


        ArrayList<MovableBox> library1 = new ArrayList<MovableBox>();
        libraries.add(library1);

        int whichRotor = RotatePlayers.BOTTOM_RIGHT;
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "percussion\\808_snare.wav",0, 100);
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "bass\\kick1.wav",50, 100);


        whichRotor = RotatePlayers.TOP_RIGHT;
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "percussion\\closed_hihat1.wav",0, 80);

        addToLibrary(library1, rotors.getRotators().get(RotatePlayers.BOTTOM_LEFT), "bass\\kick1.wav",50 + 100/8.0f + (100/16.0f), 80);
    }

    private void generatePresetTwo(RotatePlayers rotors) {


        ArrayList<MovableBox> library1 = new ArrayList<MovableBox>();
        libraries.add(library1);

        int whichRotor = RotatePlayers.BOTTOM_RIGHT;
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "percussion\\reverb_clap.wav",0, 100);
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "layers\\slightly_sample1.wav",25, 90);
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "layers\\noisy_synth_da_chord.wav",50, 90);
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "bass\\kick1.wav",50, 100);



        whichRotor = RotatePlayers.BOTTOM_LEFT;
        addToLibrary(library1, rotors.getRotators().get(whichRotor), "layers\\dark_chord.wav",12.5f, 100);
        clonePreviousToLibrary(library1, rotors.getRotators().get(whichRotor), 50, 100);
        clonePreviousToLibrary(library1, rotors.getRotators().get(whichRotor), 62.5f, 100);
        clonePreviousToLibrary(library1, rotors.getRotators().get(whichRotor), 87.5f, 100);





    }

    public ArrayList<MovableBox> getLibrary (int n) {
        return libraries.get(n);
    }

    private void addToLibrary(ArrayList<MovableBox> library, Rotater whichRotor, String filepath, float pathPercent, float volumePercent) {
        Sample s = new Sample(info, fileRoot + filepath);
        if (s != null) {
            Point2D.Float point = whichRotor.getPositionFromPercentage(pathPercent,volumePercent);
            MovableBox m = new MovableBox(p, info, (float) point.getX(), (float) point.getY(), MovableBox.NODE_WIDTH, MovableBox.NODE_WIDTH);
            m.setSound(s);
            library.add(m);
        } else {
            throw new NullPointerException("Couldn't set up the presets with this sample: " + filepath);
        }

    }

    private void clonePreviousToLibrary(ArrayList<MovableBox> library, Rotater whichRotor, float pathPercent, float volumePercent) {
        if (p != null) {
            Point2D.Float point = whichRotor.getPositionFromPercentage(pathPercent,volumePercent);
            MovableBox parent = library.get(library.size()-1);
            MovableBox m = parent.cloneThis();

            //don't want them to get deleted
            m.setOriginal(true);
            m.setxLocation((float) point.getX());
            m.setyLocation((float) point.getY());

            m.setInitialSpawnX((float) point.getX());
            m.setInitialSpawnY((float) point.getY());
            library.add(m);

            parent.setChild(m);


        } else {
            throw new NullPointerException("Couldn't clone to library (null parent)");
        }

    }


    public ArrayList<MovableBox> getCurrentLibraryList() {
        if (currentLibrary >= 0 && currentLibrary < libraries.size()) {
            return getLibrary(currentLibrary);
        } else {
            return  null;
        }
    }

    public void setCurrentLibrary(int currentLibrary) {
        this.currentLibrary = currentLibrary;
    }

    public int getCurrentLibrary() {
        return currentLibrary;
    }
}
