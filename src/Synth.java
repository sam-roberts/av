import themidibus.MidiBus;
import themidibus.Note;

import java.util.ArrayList;

/**
 * Created by Sam on 5/04/2014.
 */
public class Synth implements Runnable {
    ArrayList<Note> notes;
    int channel;
    private boolean loop;
    PublicInformation info;
    private boolean running;

    public Synth(PublicInformation info, int channel) {
        this.channel = channel - 1;
        notes = new ArrayList<Note>();
        this.info = info;
        running = false;
    }

    public Synth() {

    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }


    @Override
    public void run() {
        running = true;
        do {
            myNotes();
        }while (isLoop() && running);

    }

    void playNote (Note n, double duration) {
        long durationMS = getDurationMS(duration);
        System.out.println("duration is milliseconds" + duration);
        info.getMidi().sendNoteOn(n);
        try {
            Thread.sleep(durationMS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        info.getMidi().sendNoteOff(n);
    }
    public Note dummyNote(int pitch, int velocity) {
        return new Note(this.channel, pitch, velocity);
    }
    long getDurationMS (double duration) {
        return (long)(60/(float)(info.getTempo() * duration)*4* 1000);

    }
    void playNotes (ArrayList<Note> n, int duration) {
        long durationMS = getDurationMS(duration);
        for (Note note : n) {
            info.getMidi().sendNoteOn(note);

        }
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        for (Note note : n) {
            info.getMidi().sendNoteOff(note);

        }
        this.notes.clear();
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }

    void myNotes () {
    }

    public boolean isRunning() {
        return running;
    }
    public void stop() {
        running = false;
    }
}

