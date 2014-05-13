import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import themidibus.*; //Import the library

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends PApplet {

    MidiBus myBus; // The MidiBus
    Minim minim;
    FFT fft;
    Boolean keyTemp;

    BeatDetect beatDetect;
    BeatListener beatListener;

    SampleManager samples;
    KeyTriggerManager keyboardMap;
    AudioInput in;
    AudioOutput out;

    boolean loopSample = false;

    PublicInformation info;

    ExecutorService es = Executors.newCachedThreadPool();

    Synths synths;

    int kickRadius;
    public void setup() {
        minim = new Minim(this);
        myBus = new MidiBus(this, 0, 3); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.

        size(1024, 768, P2D);
        background(255);
        noStroke();
        smooth();
        keyTemp = false;
        info = new PublicInformation();
        info.setTempo(120);
        info.setMidi(myBus);
        info.setMinim(minim);



        MidiBus.list(); // List all available Midi devices on STDOUT. This will show each device's index and name.

        // Either you can
        //                   Parent In Out
        //                     |    |  |
        //myBus = new MidiBus(this, 0, 1); // Create a new MidiBus using the device index to select the Midi input and output devices respectively.

        // or you can ...
        //                   Parent         In                   Out
        //                     |            |                     |
        //myBus = new MidiBus(this, "IncomingDeviceName", "OutgoingDeviceName"); // Create a new MidiBus using the device names to select the Midi input and output devices respectively.

        // or for testing you could ...
        //                 Parent  In        Out
        //                   |     |          |


        in = minim.getLineIn();
        out = minim.getLineOut();


        // loop the file
        // create an FFT object that has a time-domain buffer the same size as jingle's sample buffer
        // note that this needs to be a power of two
        // and that it means the size of the spectrum will be 1024.
        // see the online tutorial for more info.
        fft = new FFT(in.bufferSize(), in.sampleRate());
        beatDetect = new BeatDetect(in.bufferSize(), in.sampleRate());
        beatDetect.setSensitivity(50);
        beatListener = new BeatListener(beatDetect,in);
        // calculate averages based on a miminum octave width of 22 Hz
        // split each octave into three bands
        fft.logAverages(22, 12);
        rectMode(CORNERS);
        in.enableMonitoring();

        kickRadius = width/(4);


        synths = new Synths(info);
        samples = new SampleManager(info, "D:\\Users\\Sam\\Documents\\Dropbox\\Uni\\Semester 1 2014\\SOMA3610 - Digital Portfolio\\Processing_Synth_MIDI\\src\\data\\sounds");
        keyboardMap = new KeyTriggerManager(samples);


    }

    public void draw() {
        fill(Color.white.getRGB(), 30);
        rect(0,0,width,height);
        beatDetect.detect(in.mix);

        if (beatDetect.isKick()) {
            System.out.println("kick detected");
            fill(Color.white.getRGB());
            ellipse(kickRadius/2 + 20,height/2,kickRadius,kickRadius);
        }

        if (beatDetect.isSnare()) {
            System.out.println("snare detected");
            fill(Color.red.getRGB());
            ellipse(width - (kickRadius/2 + 20),height/2,kickRadius,kickRadius);
        }
        //logarithmic visualiser
        //fill(255);
        // perform a forward FFT on the samples in jingle's mix buffer
        // note that if jingle were a MONO file, this would be the same as using jingle.left or jingle.right
        fft.forward(in.mix);
        // avgWidth() returns the number of frequency bands each average represents
        // we'll use it as the width of our rectangles
        int w = width/fft.avgSize();
        int colourWidth = 255 / fft.avgSize();
        float maxHeight =0;
        noStroke();
        for(int i = 0; i < fft.avgSize(); i++)

        {

            float freqStrength = fft.getAvg(i);
            if (freqStrength > 1) {
                freqStrength = (float) (freqStrength + (Math.pow(i,1.05)/2));


                if (freqStrength>maxHeight) {
                    maxHeight = freqStrength;
                }
                // draw a rectangle for each average, multiply the value by 5 so we can see it better
                pushMatrix();
                translate(width / 2, height / 2);
                float rotateDegree = map(i, 0, fft.avgSize(), 0, 380);
                rotate((float) Math.toRadians(rotateDegree));


                //rotate(i);
                //rect(i*5, 0, i*w + w, height - fft.getAvg(i)*5);
                int red = Math.max(100, (i * colourWidth));
                int green = Math.max(100, (i * colourWidth));
                int blue = Math.max(100, (i * colourWidth));

                Color c = new Color(red, green , blue);


                fill(c.getRGB());

                rect(0, 0, w, freqStrength * 2);
                popMatrix();
            }
        }

        noFill();
        int strokeColour = (int)map(maxHeight,0,50,0,100);
        stroke(strokeColour);
        strokeWeight(3);
        pushMatrix();
        translate(width / 2, height / 2);
        ellipse(0, 0, maxHeight * 8, maxHeight * 8);
        ellipse(0,0,maxHeight*8,maxHeight*8);
        popMatrix();

        int channel = 0;
        int pitch = 64;
        int velocity = 127;

        //myBus.sendNoteOn(channel, pitch, velocity); // Send a Midi noteOn
        //delay(200);
        //myBus.sendNoteOff(channel, pitch, velocity); // Send a Midi nodeOff

        //myBus.sendControllerChange();

        int number = 0;
        int value = 90;


        if (keyTemp) {
            fill(Color.RED.getRGB());
            rect(0, 0, 25, 25);
        }


    }

    public void noteOn(int channel, int pitch, int velocity) {
        // Receive a noteOn
        println();
        println("Note On:");
        println("--------");
        println("Channel:" + channel);
        println("Pitch:" + pitch);
        println("Velocity:" + velocity);
        keyTemp=true;


    }

    public void noteOff(int channel, int pitch, int velocity) {
        // Receive a noteOff
        println();
        println("Note Off:");
        println("--------");
        println("Channel:" + channel);
        println("Pitch:" + pitch);
        println("Velocity:" + velocity);
        keyTemp = false;
    }

    public void controllerChange(int channel, int number, int value) {
        // Receive a controllerChange
        println();
        println("Controller Change:");
        println("--------");
        println("Channel:" + channel);
        println("Number:" + number);
        println("Value:" + value);
    }

    public void delay(int time) {
        int current = millis();
        while (millis() < current + time) Thread.yield();
    }

    public void keyPressed()
    {


        Synth synth = null;
        Sample sample = null;
        //BassMidi tempo = new BassMidi(myBus, 17);



        //myBus.sendNoteOn(1,32,128);

        //System.out.println ("Key pressed: " + keyCode);
        if (keyCode == SHIFT) {
            loopSample = true;
        } else {
            key = Character.toLowerCase(key);

            if (key == '=') {
                tempoUp();
            } else if (key == '-') {
                tempoDown();
            } else if (key == 'b') {
                synth = synths.getSynth(synths.SYNTH_1);


            } else if (key == 'm' || key == 'M') {
                if (in.isMonitoring()) {
                    in.disableMonitoring();
                } else {
                    in.enableMonitoring();
                }
            } else {
                Sample thisSample = keyboardMap.getSampleOfKey(Character.toLowerCase(key));
                if (thisSample != null) {
                    thisSample.setLoop(loopSample);


                    SampleAnimation sa = new SampleAnimation(this);
                    sa.draw();
                    (new Thread(thisSample)).start();
                }

            /*
                //thisPlayer.pause();
                //thisPlayer.rewind()
                System.out.println("Start playing " + key);
                if (loopSample) {
                    thisPlayer.loop();
                } else {
                    thisPlayer.rewind();
                    thisPlayer.play();
                }
*/


            }
            if (synth != null) {
                synth.setLoop(loopSample);


                    (new Thread(synth)).start();


            }

        }
    }

    private void tempoUp() {
        if (info.getTempo() < 300) {
            myBus.sendControllerChange(0, 1, 50); // Send a controllerChange
            info.setTempo(info.getTempo() + 10);
        }

    }
    private void tempoDown() {
        if (info.getTempo() > 50) {
            myBus.sendControllerChange(0, 2, 50); // Send a controllerChange
            info.setTempo(info.getTempo() - 10);
        }
    }

    public void keyReleased() {
        if (keyCode == SHIFT) {
            loopSample = false;
        }
        //myBus.sendNoteOff(3,32,128);

    }
}