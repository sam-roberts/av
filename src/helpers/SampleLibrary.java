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
    public SampleLibrary(PApplet p, RotatePlayers rotors, PublicInformation information) {
        this.info = information;
        fileRoot = information.getRootDirectory() + "sounds\\";
        this.p = p;

        libraries = new ArrayList<ArrayList<MovableBox>>();
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

        addToLibrary(library1, rotors.getRotators().get(RotatePlayers.BOTTOM_LEFT), "bass\\kick2.wav",50 + 100/8.0f + (100/16.0f), 80);
    }

    public ArrayList<MovableBox> getLibrary (int n) {
        return libraries.get(n);
    }

    private void addToLibrary(ArrayList<MovableBox> library, Rotater whichRotor, String filepath, float pathPercent, float volumePercent) {
        Sample s = new Sample(info, fileRoot + filepath);
        if (s != null) {
            Point2D.Float point = whichRotor.getPositionFromPercentage(pathPercent,volumePercent);
            MovableBox m = new MovableBox(p, info, (float) point.getX(), (float) point.getY(), 25, 25);
            m.setSound(s);
            library.add(m);
        } else {
            throw new NullPointerException("Couldn't set up the presets with this sample: " + filepath);
        }

    }

    public ArrayList<MovableBox> getCurrentLibrary() {
        return getLibrary(0);
    }
}
