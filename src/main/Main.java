package main;

import animations.*;
import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import helpers.*;
import processing.core.PApplet;
import themidibus.*; //Import the library

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends PApplet {

    public static final int LAG_COMPENSATION = 50;
    public static final int AVERAGE_LAG_COMPENSATION = 0;
    //home
    public final String DATA_PATH = "D:\\Users\\Sam\\Documents\\GitHub\\av\\src\\data\\";

    //laptop
    //public final String DATA_PATH = "C:\\Users\\sam\\Documents\\GitHub\\av\\src\\data\\";
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
    AudioRecorder recorder;

    boolean loopSample = false;

    PublicInformation info;
    ColourManager cm;
    ExecutorService es = Executors.newCachedThreadPool();

    Synths synths;

    int kickRadius;
    RotateVoiceInput rotateVoice;
    ProcessingAnimation polygonAnimation;
    RotatePlayers rotatePlayers;

    ArrayList<MovableBox> movables;
    MovableBox dragTarget = null;
    VoiceRecordingManager voiceSampleList;

    Sample metronome;

    RecordButton recordButton;

    BeatCounterAnimation beatCount;


    ArrayList<MovableBox> activeSounds;


    CountdownAnimation count;


    public void setup() {


        minim = new Minim(this);
        myBus = new MidiBus(this, 0, 3); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.

        size(1920, 1080, P3D);
        background(255);
        noStroke();
        smooth();
        keyTemp = false;
        info = new PublicInformation();
        info.setTempo(120);
        info.setMidi(myBus);
        info.setMinim(minim);
        cm = new ColourManager();
        info.setColourManager(cm);

        //clean up the jams
        File recordDir = new File(DATA_PATH + "recorded\\");
        for (File f: recordDir.listFiles()) {
            f.delete();
        }


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
        //System.out.println("test current directory " + System.getProperty("user.dir"));

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

        in.disableMonitoring();

        kickRadius = width/(4);


        synths = new Synths(info);
        samples = new SampleManager(info, DATA_PATH + "sounds");
        //getClass().getClassLoader().getResource()
        //samples = new SampleManager(info, "src\\data\\sounds");

        keyboardMap = new KeyTriggerManager(samples);


        //ANIMTAIONS
        rotateVoice = new RotateVoiceInput(this, info);
        polygonAnimation = new PolygonAnimation(this,info);
        rotatePlayers = new RotatePlayers(this,info);

        movables = new ArrayList<MovableBox>();
        int sizePadding = 50;
        int numVerticalMax = getHeight()/sizePadding;
        int i = 0;
        int row = 0;
        for (Sample s: samples.getAllSamples()) {
            if (i == numVerticalMax) {
                row++;
                i=0;
            }
            MovableBox temp = new MovableBox(this,info,sizePadding + row*sizePadding,i*sizePadding + sizePadding,25,25);
            temp.setSound(s);
            if (s.getSimpleFilename().equals("kick1.wav")) {
                System.out.println("needs more kick");
            }
            movables.add(temp);

            i++;

        }
        recordButton = new RecordButton(this,info,3*getWidth()/4,3*getHeight()/4,100,100);
        recordButton.setMovable(false);

        movables.add(recordButton);


        voiceSampleList = new VoiceRecordingManager();
        metronome = samples.getMetronome();


        count = new CountdownAnimation(this, info);
        beatCount = new BeatCounterAnimation(this,info);



        activeSounds = new ArrayList<MovableBox>();




    }

    public void draw() {

        background(255);

        rotatePlayers.play();
        recordButton.draw();

        count.drawTimed();
        beatCount.draw();

        for (MovableBox box: movables) {
            //doCollisionStuff(box);
            box.draw();
        }


        if (polygonAnimation.isKeyTriggered()) {
            polygonAnimation.drawTimed();
        }

        //System.out.println(activeSounds.size() + " active sounds");
        for (MovableBox m: activeSounds) {
            int playing = info.getDurationMS(m.getSound().getLength()) * 4;
            int soundDelay =  m.getSound().getDelay()*4;
            int currentTime = (millis() - AVERAGE_LAG_COMPENSATION) % playing;
            int result = ((currentTime) % (playing)) - soundDelay;
            if ((result < LAG_COMPENSATION || result > playing - LAG_COMPENSATION) && result >= 0) {
                m.playsound();
                //System.out.println("time is " + currentTime + " playing is " + playing + " sound has delay of " + soundDelay);
            } else {
                //System.out.println("MISSED is " + currentTime + " playing is " + playing + " sound has delay of " + soundDelay);
            }
        }







        //fill(Color.white.getRGB(), 30);
        //rect(0,0,width,height);

        //beatDetect.detect(in.mix);
        /*
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
        */
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
        /* PERFORMANCE REMOVE LATER
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
                translate(width / 2 + 600, height / 2 + 300);
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
        translate(width / 2 + 600, height / 2 + 300);
        ellipse(0, 0, maxHeight * 8, maxHeight * 8);
        ellipse(0,0,maxHeight*8,maxHeight*8);
        popMatrix();


        int channel = 0;
        int pitch = 64;
        int velocity = 127;

        //myBus.sendNoteOn(channel, pitch, velocity); // Send a Midi noteOn
        //delay(200);
        //myBus.sendNoteOff(channel, pitch, velocity); // Send a Midi nodeOff

        //myBus.sendControlzlerChange();

        int number = 0;
        int value = 90;

*/
        if (keyTemp) {
            fill(Color.RED.getRGB());
            rect(0, 0, 25, 25);
        }

        boolean debug = true;
        if (debug) {
            float time = millis();
            fill(Color.black.getRGB());
            text("frame rate: " + frameRate + " time is " + time + " / tempo is " + info.getTempo(), getWidth()/2, 20);

            pushMatrix();
            translate(getWidth()-200, 20);
            if(recorder != null && recorder.isRecording()) {
                text("recording", 0, 0);
            } else {
                text("not recording", 0, 0);
            }
            popMatrix();
        }




    }

    private void doCollisionStuff(MovableBox box) {
        boolean isHit = false;
        Rotater whichHit= null;
        float nearestDistance = MAX_FLOAT;
        Rotater closestRotater = null;
        for (Rotater r: rotatePlayers.getRotators()) {

/*
            Point2D middleOfRotor = new Point(r.getxOrigin(), r.getyOrigin());
            Point2D boxLocation = new Point(box.getxLocation(), box.getyLocation());
            float distance = (float) middleOfRotor.distance(boxLocation);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                closestRotater = r;
            }
*/
            int l1x= r.getxOrigin();
            int l1y = r.getyOrigin();

            Point p2 = r.getSecondPoint();
            int l2x = (int) p2.getX();
            int l2y = (int) p2.getY();

            int p1x = box.getxLocation();
            int p1y = box.getyLocation();

            int size = box.getWidth();
            if (CollisionManager.isLineInsideCircle(l1x, l1y, l2x, l2y, p1x, p1y, size)) {
                isHit = true;
                whichHit =r;

            }

        }
        // first time this box has ever been hit
        if (box.getHitBy() != null) {
            System.out.println("first time this box has been hit by a rotor");


            //we hit are in a new window
            if (box.getHitBy() != whichHit) {
                box.setHit(isHit, whichHit);
            } else {
                //make sure we aren't nulling the last hit by
                box.setHit(isHit, box.getHitBy());


            }

        } else {
            //the box has never been touched
            box.setHit(isHit, whichHit);
        }
        if (box.getSound() != null) {
            box.getSound().setGain(0.0f);
        }

/*
            //move a box outside make sure it goes back to full volume
            if (box.getHitBy() != null) {
                float distance = (float) Point2D.distance(box.getHitBy().getxOrigin(), box.getHitBy().getyOrigin(), box.getxLocation(), box.getyLocation());
                if (distance > box.getHitBy().getLength()) {
                    box.setHit(false, null);
                    box.getSound().setGain(0.0f);
                }
            }
*/
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
        //helpers.BassMidi tempo = new helpers.BassMidi(myBus, 17);



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
            } else if (key == 'r' || key == 'R') {
                int numRecordings = samples.getNumSamplesKey("recorded");
                String voiceRecordingFilepath = DATA_PATH + "recorded\\test"+(numRecordings+1)+".wav";

                //first time we create one
                if (recorder == null) {
                    recorder = minim.createRecorder(in,voiceRecordingFilepath);
                }
                if (recorder.isRecording()) {
                    recorder.endRecord();
                    recorder.save();
                    recorder=null;


                    //add to sample manager?
                    System.out.println("samples before: " + samples.getNumSamples());
                    samples.insertIndividualItem(voiceRecordingFilepath);
                    System.out.println("samples after: " + samples.getNumSamples());

                    //update movables
                    MovableBox temp = new MovableBox(this,info,800,600,40,40);

                    temp.setSound(samples.getNewestSample());

                    movables.add(temp);
                } else {
                    recorder.beginRecord();

                }
            } else {
                Sample thisSample = keyboardMap.getSampleOfKey(Character.toLowerCase(key));
                if (thisSample != null) {
                    thisSample.setLoop(loopSample);
                    thisSample.setAnimation(polygonAnimation);
                    thisSample.playAnimation();

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
        if (info.getTempo() < info.TEMPO_MAX) {
            myBus.sendControllerChange(0, 1, 50); // Send a controllerChange
            info.setTempo(info.getTempo() + 10);
        }

    }
    private void tempoDown() {
        if (info.getTempo() > info.TEMPO_MIN) {
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
    public void mousePressed() {
        for (MovableBox m: movables) {
            if (getMousePosition() != null) {
                if (getMousePosition().distance(new Point(m.getxLocation(), m.getyLocation())) < m.getWidth()) {
                    m.pressed();
                    dragTarget = m;
                    break;
                }
            }
        }
    }
    public void mouseReleased() {
        dragTarget = null;
        ListIterator<MovableBox> list = movables.listIterator();
        while (list.hasNext()) {
            //System.out.println("yolo " + movables.size());
            MovableBox m = list.next();
            if (getMousePosition() != null) {
                if (getMousePosition().distance(new Point(m.getxLocation(), m.getyLocation())) < m.getWidth()) {
                    m.release();



                    if (m.equals(recordButton)) {
                        System.out.println("pressed record button");


                        int numRecordings = samples.getNumSamplesKey("recorded");
                        String voiceRecordingFilepath = DATA_PATH + "recorded\\test" + (numRecordings + 1) + ".wav";

                        //first time we create one
                        if (recorder == null) {
                            recorder = minim.createRecorder(in, voiceRecordingFilepath);
                        }
                        if (recorder.isRecording()) {
                            recorder.endRecord();
                            recorder.save();
                            recorder = null;


                            //add to sample manager?
                            System.out.println("samples before: " + samples.getNumSamples());
                            samples.insertIndividualItem(voiceRecordingFilepath);
                            System.out.println("samples after: " + samples.getNumSamples());

                            //update movables
                            MovableBox temp = new MovableBox(this, info, 800, 600, 40, 40);

                            temp.setSound(samples.getNewestSample());

                            movables.add(temp);
                            recordButton.stopRecording();

                        } else {
                            recordButton.startRecording();

                            recorder.beginRecord();

                        }
                    break;
                    }
                }
            //System.out.println("made it to the bottom");
            }
        }
    }

    public void mouseDragged() {
        if (dragTarget == null) {
            for (MovableBox m : movables) {
                if (getMousePosition() != null) {
                    Point p = new Point(m.getxLocation(), m.getyLocation());
                    //if we find the thing we are looking for break
                    if (p != null && getMousePosition().distance(p) < m.getWidth()) {
                        break;
                    }
                }
            }
        }
        if (dragTarget != null && getMousePosition() != null && dragTarget.isMovable()) {
            dragTarget.setxLocation((int)getMousePosition().getX());
            dragTarget.setyLocation((int)getMousePosition().getY());

            for (Rotater r: rotatePlayers.getRotators()) {
                if (CollisionManager.isPointInsideCircle(dragTarget,r)) {

                    if (! activeSounds.contains(dragTarget)) {
                        activeSounds.add(dragTarget);
                    }

                    dragTarget.setHit(true, r);
                    dragTarget.getSound().setLength(r.getSpeed());

                    double angleToCenter = CollisionManager.getAngle(dragTarget.getxLocation(), dragTarget.getyLocation(), r.getxOrigin(), r.getyOrigin());
                    int soundDelay = Quantizer.getDelay(info.getDurationMS(Duration.WHOLE), r.getSpeed(), angleToCenter);
                    dragTarget.getSound().setDelay(soundDelay);

                } else {
                    if (dragTarget.isHit() && dragTarget.getHitBy() == r) {
                        dragTarget.setHit(false, null);
                        dragTarget.getSound().setLoop(false);
                        if (activeSounds.contains(dragTarget)) {
                            activeSounds.remove(dragTarget);
                        }

                    }
                }
            }
        }
    }

    public void mouseMoved() {
        MovableBox target = null;
        for (MovableBox m: movables) {
            Point mousePosition = getMousePosition();
            if (mousePosition != null) {
                Point p = new Point(m.getxLocation(), m.getyLocation());
                if (p != null && mousePosition.distance(p) < m.getWidth()/2) {
                    m.setHover(true);
                    System.out.println("hovering on " + m.toString());
                }else {
                    m.setHover(false);
                }
            }
        }

    }
}