package helpers;

import animations.ProcessingAnimation;
import ddf.minim.AudioPlayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sam on 5/04/2014.
 */
public class Sample implements Runnable{

    String filepath;
    String simpleFilename;
    private boolean loop;
    PublicInformation info;
    private boolean running;

    int timeLastPlayed = 0;
    ProcessingAnimation animation = null;

    double length = Duration.QUARTER;
    float gain = 0.0f;
    AudioPlayer myPlayer;
    public Sample(PublicInformation info,String filepath) {
        this.filepath = filepath;
        this.info = info;

        myPlayer = info.getMinim().loadFile(filepath);

        String regex = "(\\w*)\\.wav$";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(getFilepath());
        if (m.find()) {
            for (int i = 0; i < m.groupCount(); i++) {
                simpleFilename = m.group(i);
                simpleFilename = simpleFilename.replaceAll("_", " ");
            }
        }

    }


    @Override
    public void run() {
        if (! running) {
            running=true;
            //System.out.println("gain is " + myPlayer.getGain());
            do {
                getMyPlayer().setGain(gain);
                getMyPlayer().rewind();
                getMyPlayer().play();
                if (isLoop()) {
                    long durationMS = getDurationMS();
                    System.out.println("looping " + filepath + " sleeping for a  " + Duration.QUARTER + " note");

                    if (animation != null) {
                    }
                    try {
                        Thread.sleep(durationMS);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }

            } while (isLoop() && running);
            running=false;
        }
    }

    public int getDurationMS () {
        return (int)(60/(float)(info.getTempo() * this.length)*4* 1000);

    }

    public int getTimeLastPlayed() {
        return timeLastPlayed;
    }

    public void setTimeLastPlayed(int timeLastPlayed) {
        this.timeLastPlayed = timeLastPlayed;
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

    public ProcessingAnimation getAnimation() {
        return animation;
    }
    public void playAnimation() {
        if (this.animation.isKeyTriggered()) {
            this.animation.killAnimation();

        }
        this.animation.setTriggered(true);
        this.animation.setLoop(isLoop());
        this.animation.setDuration(getDurationMS());
    }
    public void setAnimation(ProcessingAnimation animation) {
        this.animation = animation;
    }

    public String getSimpleFilename() {
        return simpleFilename;
    }

    public float getGain() {
        return gain;
    }

    public void setGain(float gain) {
        this.gain = gain;
    }
}
