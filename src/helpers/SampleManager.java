package helpers;

import ddf.minim.AudioPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sam on 5/04/2014.
 */
public class SampleManager {
    String filepath;
    final File folder;

    // we map a key like "ambient" to a sample
    HashMap<String, ArrayList<Sample>> files;

    PublicInformation info;
    ArrayList<AudioPlayer> audioPlayers;
    Sample newestSample;

    public SampleManager(PublicInformation info, String filepath) throws FileNotFoundException {
        this.filepath = filepath;
        folder = new File(filepath);
        files = new HashMap<String, ArrayList<Sample>>();
        this.info = info;
        audioPlayers = new ArrayList<AudioPlayer>();

        loadAllSamplesInFolder(folder);


    }

    public void loadAllSamplesInFolder(final File folder) throws FileNotFoundException {
        if (folder != null && folder.exists()) {
            for (final File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    loadAllSamplesInFolder(fileEntry);
                } else {
                    System.out.println(fileEntry.getAbsolutePath() + " is in folder " + folder.getName());
                    insertItem(fileEntry, folder);

                }
            }
        }
    }
    public int getNumSamples(){
        int i=0;
        for (String key : files.keySet()) {
            i += files.get(key).size();
        }
        return i;
    }

    public void insertIndividualItem(String filepath) throws FileNotFoundException {
        File f = new File(filepath);
        File folderName = f.getParentFile();
        if (f != null && folderName != null){
            insertItem(f, folderName);
        }  else {
            System.out.println("Couldn't insert item");
        }

    }
    private void insertItem(File fileEntry, File folder) throws FileNotFoundException {
        Sample s = new Sample(info, fileEntry.getAbsolutePath());
        s.setCategory(folder.getName());
        if (files.containsKey(folder.getName())) {
            ArrayList<Sample> list = files.get(folder.getName());
            list.add(s);

        } else {
            ArrayList<Sample> list = new ArrayList();
            list.add(s);
            files.put(folder.getName(), list);
        }
        newestSample = s;
        audioPlayers.add(s.getMyPlayer());

    }

    public ArrayList<Sample> getAmbient() {
        return files.get("ambient");
    }

    public ArrayList<Sample> getAllSamples() {
        ArrayList<Sample> allPaths = new ArrayList<Sample>();
        for (String key : files.keySet()) {
            ArrayList<Sample> current = files.get(key);
            for (Sample s: current) {
                allPaths.add(s);
            }
        }
        return allPaths;
    }

    public Sample getNewestSample() {
        return newestSample;
    }


    public Sample getMetronome() {
        return files.get("metronome").get(0);
    }

    public int getNumSamplesKey(String s) {
        if (files.containsKey(s)) {
            return files.get(s).size();
        } else {
            return 0;
        }

    }
}
