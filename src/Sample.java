import ddf.minim.AudioPlayer;
import themidibus.Note;

/**
 * Created by Sam on 5/04/2014.
 */
public class Sample implements Runnable{

    String filepath;
    private boolean loop;
    PublicInformation info;
    private boolean running;

    AudioPlayer myPlayer;
    public Sample(PublicInformation info,String filepath) {
        this.filepath = filepath;
        this.info = info;

        myPlayer = info.getMinim().loadFile(filepath);

    }


    @Override
    public void run() {
        running = true;

        do {
            getMyPlayer().rewind();
            getMyPlayer().play();
            if (isLoop()) {
                long durationMS = getDurationMS(Duration.QUARTER);
                System.out.println("looping " + filepath + " sleeping for a  " + Duration.QUARTER + " note");
                try {
                    Thread.sleep(durationMS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }

        }while (isLoop() && running);

    }

    long getDurationMS (double duration) {
        return (long)(60/(float)(info.getTempo() * duration)*4* 1000);

    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public AudioPlayer getMyPlayer() {
        return myPlayer;
    }

    public void setMyPlayer(AudioPlayer myPlayer) {
        this.myPlayer = myPlayer;
    }
}