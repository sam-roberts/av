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
import processing.core.PFont;
import themidibus.*; //Import the library

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MusicFun extends PApplet {

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

    Boolean DEBUG = false;


    DoubleClick doubleClick;
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
    MovableBox selectionTarget = null;
    VoiceRecordingManager voiceSampleList;


    RecordButton recordButton;

    BeatCounterAnimation beatCount;

    ArrayList<MovableBox> activeSounds;
    SampleLibrary sampleLibrary;


    CountdownAnimation count;

    boolean needClear = false;

    MenuUI menuUI;
    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "main.MusicFun" });
    }

    public void setup() {


        minim = new Minim(this);
        myBus = new MidiBus(this, 0, 3); // Create a new MidiBus with no input device and the default Java Sound Synthesizer as the output device.

        size(1920, 1080, P3D);
        background(255);
        noStroke();
        smooth();
        keyTemp = false;
        PFont font36 = createFont("Arial", 36, true);
        textFont(font36);
        info = new PublicInformation(DATA_PATH);
        info.setTempo(120);
        info.setMidi(myBus);
        info.setMinim(minim);
        info.setAudioOut(out);


        cm = new ColourManager();
        info.setColourManager(cm);
        doubleClick = new DoubleClick();

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
        sampleLibrary = new SampleLibrary(this, rotatePlayers, info);
        activeSounds = new ArrayList<MovableBox>();

        setupMovables();


        voiceSampleList = new VoiceRecordingManager();


        count = new CountdownAnimation(this, info);
        beatCount = new BeatCounterAnimation(this,info);



        menuUI = new MenuUI(this, info);







    }

    private void setupMovables() {
        movables = new ArrayList<MovableBox>();
        int sizePadding = 80;
        int numVerticalMax = getHeight()/sizePadding;
        int i = 0;
        int row = 0;
        for (Sample s: samples.getAllSamples()) {
            if (i == numVerticalMax) {
                row++;
                i=0;
            }
            MovableBox temp = new MovableBox(this,info,sizePadding + row*sizePadding,i*sizePadding + sizePadding,MovableBox.NODE_WIDTH,MovableBox.NODE_WIDTH);
            temp.setSound(s);
            if (s.getSimpleFilename().equals("kick1.wav")) {
                System.out.println("needs more kick");
            }
            movables.add(temp);

            i++;

        }
        recordButton = new RecordButton(this,info,getWidth()-100,getHeight()/4,100,100);
        recordButton.setMovable(false);

        movables.add(recordButton);

        ArrayList<Rotater> tempRotates = rotatePlayers.getRotators();
        for (Rotater r: tempRotates) {
            movables.add(r.getSlider().getSlider());
        }
        for (MovableBox m: sampleLibrary.getCurrentLibraryList()) {
            updateActiveSounds(m);
        }
    }

    public void draw() {

        background(246,245,241);

        rotatePlayers.play();
        recordButton.draw();

        count.drawTimed();

        if (DEBUG) {
            beatCount.draw();
        }

        //handles the case of clicking reset when it was already the last one clicked
        if (needClear) {
            resetMovables(movables);
            activeSounds.clear();
            needClear = false;
        }

        if (menuUI.getPresetClicked() != sampleLibrary.getCurrentLibrary()) {
                if (menuUI.isTempo()) {

                } else {
                    resetMovables(sampleLibrary.getCurrentLibraryList());
                    sampleLibrary.setCurrentLibrary(menuUI.getPresetClicked());
                    for (MovableBox m : sampleLibrary.getCurrentLibraryList()) {
                        updateActiveSounds(m);
                    }
                }

        }
        drawMovables(movables);

        drawMovables(sampleLibrary.getCurrentLibraryList());



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
            m.getExtraAnimation().setFill(m.getFill());
            m.getExtraAnimation().setHackyPosition(m.getxLocation(), m.getyLocation());

            m.getExtraAnimation().draw();
        }


        menuUI.draw();





    /*

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
                translate(width / 2 + 600, height / 2 + 300,1);
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

*/
        int channel = 0;
        int pitch = 64;
        int velocity = 127;

        //myBus.sendNoteOn(channel, pitch, velocity); // Send a Midi noteOn
        //delay(200);
        //myBus.sendNoteOff(channel, pitch, velocity); // Send a Midi nodeOff

        //myBus.sendControlzlerChange();

        int number = 0;
        int value = 90;


        if (keyTemp) {
            fill(Color.RED.getRGB());
            rect(0, 0, 25, 25);
        }

        if (DEBUG) {
            float time = millis();
            textSize(12);

            fill(Color.black.getRGB());
            text("frame rate: " + frameRate + " time is " + time + " / tempo is " + info.getTempo(), getWidth()/2 + 400, 20);

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

    private void resetMovables(ArrayList<MovableBox> list) {
        Iterator<MovableBox> iterator = list.iterator();
        while (iterator.hasNext()) {
            MovableBox m = iterator.next();
            if (m.isClone() && m.isOriginal() == false) {
                m.getParent().setChild(null);

                iterator.remove();
            } else {
                //don't move sliders


                if (!m.isLockXMovement()) {
                    m.setxLocation(m.getInitialSpawnX());
                    m.setyLocation(m.getInitialSpawnY());

                }

            }
            updateActiveSounds(m);
            if (activeSounds.contains(m)) {
                activeSounds.remove(m);
            }


        }
    }

    private void drawMovables(ArrayList<MovableBox> list) {
        int size = list.size();

        MovableBox test = null;

        Iterator<MovableBox> iterator = list.iterator();
        while (iterator.hasNext()) {
            MovableBox box = iterator.next();
            if (box.isToDelete()) {
                box.deleteThis();
                iterator.remove();
            } else {
                //doCollisionStuff(box);
                if ((box.getxAcceleration() != 0 || box.getyAcceleration() != 0) && box.isDragged() == false && box.isVisible()) {
                    updateActiveSounds(box);
                }

                if (selectionTarget == box) {
                    //add play button over it
                    fill(0);
                    pushMatrix();
                    translate(box.getxLocation(), box.getyLocation());

                    triangle(0, 0, 0, 10, 5, 5);

                    popMatrix();

                } else {
                }
                box.draw();

                if (box.getChild() != null) {

                    float p1x = box.getxLocation();
                    float p2x = box.getChild().getxLocation();

                    float p1y = box.getyLocation();
                    float p2y = box.getChild().getyLocation();

                    line(p1x, p1y, p2x, p2y);

                    if (DEBUG) {

                        text("connecting " + box.hashCode() + " to " + box.getChild().hashCode(), (p1x + p2x) / 2.0f, (p1y + p2y) / 2.0f);

                        MovableBox temp1 = box.getChild();
                        MovableBox temp2 = box.getParent();


                        if (temp1 != null && temp2 != null) {
                            MovableBox temp3 = temp1.getParent();
                            MovableBox temp4 = temp2.getChild();

                            if (temp3 != null && temp4 != null) {
                                text("\n\n\n(reverse) connecting:\n 'child's parent'" + temp3.hashCode() + " to 'parents child'" + temp4.hashCode(), (p1x + p2x) / 2.0f, (p1y + p2y) / 2.0f);
                            }
                        }
                    }

                }
            }
        }

        if (size != list.size()) {
            System.out.println("we removed some stuff!");
        }
    }

    private void doCollisionStuff(MovableBox box) {
        boolean isHit = false;
        Rotater whichHit= null;
        float nearestDistance = MAX_FLOAT;
        Rotater closestRotater = null;
        float gain = 0.0f;

        box.setHit(false, null);
        for (Rotater r: rotatePlayers.getRotators()) {

            //rotor center

            float l1x= r.getxOrigin();
            float l1y = r.getyOrigin();
            Point2D.Float p1 = new Point2D.Float(l1x,l1y);

            //rotor outside
            Point p2 = r.getSecondPoint();
            float l2x = (int) p2.getX();
            float l2y = (int) p2.getY();

            float p1x = box.getxLocation();
            float p1y = box.getyLocation();
            Point2D.Float boxPoint = new Point2D.Float(p1x, p1y);

            int size = box.getWidth();
            if (CollisionManager.isLineInsideCircle(l1x, l1y, l2x, l2y, p1x, p1y, size)) {
                isHit = true;
                whichHit =r;
                box.setHit(true, r);

                float distance = (float) Math.abs(p1.distance(boxPoint));


                break;

            }

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
                    MovableBox temp = new MovableBox(this,info,800,600,MovableBox.BIG_BUTTON,MovableBox.BIG_BUTTON);

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
            int oldTempo = info.getTempo();
            info.setTempo(oldTempo + 10);
            updateActiveDelays();
        }

    }

    private void updateActiveDelays() {
        for (MovableBox m: activeSounds) {
            Rotater attached = m.getHitBy();
            double angle = m.getAngleToRotor();
            if (attached != null) {
                int soundDelay = Quantizer.getDelay(info.getDurationMS(Duration.WHOLE), m.getHitBy().getSpeed(), m.getAngleToRotor());

                //System.out.println("update active rotor speed is " + m.getHitBy().getSpeed() + " delay is " + soundDelay + " old delay: " + m.getSound().getDelay());

                m.getSound().setDelay(soundDelay);
                m.getSound().setLength(m.getHitBy().getSpeed());
            } else {
                throw new NullPointerException("active sound not attached to anything");
            }
        }
    }

    private void tempoDown() {
        if (info.getTempo() > info.TEMPO_MIN) {
            myBus.sendControllerChange(0, 2, 50); // Send a controllerChange
            int oldTempo = info.getTempo();
            info.setTempo(oldTempo - 10);
            updateActiveDelays();

        }
    }

    public void keyReleased() {
        if (keyCode == SHIFT) {
            loopSample = false;
        }
        //myBus.sendNoteOff(3,32,128);

    }
    public void mousePressed() {
        findDragTarget(this.movables);
        if (dragTarget == null) {
            findDragTarget(sampleLibrary.getCurrentLibraryList());
        }

        if (dragTarget != null) {
            if (doubleClick.isDoubleClick(dragTarget, this.millis()) && dragTarget.isLockXMovement() == false) {
                doubleClick.reset();
                System.out.println("double click");

                //TODO: add copy
                MovableBox temp;
                if (dragTarget.getLastChild() == null) {
                    temp = dragTarget.cloneThis();
                    dragTarget.setChild(temp);
                } else {
                    temp = dragTarget.getLastChild().cloneThis();
                    dragTarget.getLastChild().setChild(temp);

                }

                if (temp.getxLocation() < getWidth()) {
                    temp.setxLocation(temp.getxLocation() + 50);
                } else {
                    temp.setxLocation(temp.getxLocation() - 50);
                }

                if (temp.getyLocation() < getHeight()) {
                    temp.setyLocation(temp.getyLocation() + 50);
                } else {
                    temp.setyLocation(temp.getyLocation() - 50);
                }

                if (movables.contains(dragTarget)) {
                    movables.add(temp);

                } else {
                    sampleLibrary.getCurrentLibraryList().add(temp);
                }
                updateActiveSounds(temp);


            } else {
                doubleClick.setClick(dragTarget, this.millis());

                dragTarget.playsound();
            }
        } else {
            pressedUI();
        }

        selectionTarget = dragTarget;
    }

    private void pressedUI() {
        Point mouseLocation = getMousePosition();
        if (menuUI.getWholeSize().contains(mouseLocation)) {
            menuUI.clickedAt(mouseLocation);

            if (menuUI.getPresetClicked() == 0) {
                needClear = true;
            }
            if (menuUI.isTempo()) {
                if (menuUI.getTempoButton() == menuUI.TEMPO_DOWN) {
                    tempoDown();
                } else if (menuUI.getTempoButton() == menuUI.TEMPO_UP) {
                    tempoUp();
                }
            }




        }
    }

    private void findDragTarget(ArrayList<MovableBox> movables) {
        for (MovableBox m: movables) {
            if (getMousePosition() != null) {
                if (getMousePosition().distance(new Point2D.Float(m.getxLocation(), m.getyLocation())) < m.getWidth()) {
                    m.pressed();
                    dragTarget = m;
                    System.out.println("found target");
                    break;
                }
            }
        }
    }

    public void mouseReleased() {
        if (dragTarget != null) {
            dragTarget.setDragged(false);
            dragTarget.release();
            doReleaseStuff();
            dragTarget = null;
        }



    }

    private void doReleaseStuff() {
        //System.out.println("yolo " + movables.size());
        if (dragTarget.equals(recordButton)) {
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
                MovableBox temp = new MovableBox(this, info, recordButton.getxLocation() - MovableBox.BIG_BUTTON, recordButton.getyLocation() + MovableBox.BIG_BUTTON, MovableBox.RECORDED_WIDTH, MovableBox.RECORDED_WIDTH);
                temp.setRecording(true);
                temp.setSound(samples.getNewestSample());
                movables.add(temp);
                updateActiveSounds(temp);
                recordButton.stopRecording();

            } else {
                recordButton.startRecording();
                recorder.beginRecord();

            }
        }
    }

    public void mouseDragged() {
        Point mousePos = getMousePosition();

        /*
        if (dragTarget == null && mousePos != null) {
            for (MovableBox m : movables) {
                Point2D.Float p = new Point2D.Float(m.getxLocation(), m.getyLocation());
                //if we find the thing we are looking for break
                if (p != null && mousePos.distance(p) < m.getWidth()) {
                    break;

                }
            }
        }
        */
        if (dragTarget != null && mousePos != null && dragTarget.isMovable() && dragTarget.isVisible()) {
            float xDistance = (float) (mousePos.getX() - dragTarget.getxLocation());
            float yDistance = (float) (mousePos.getY() - dragTarget.getyLocation());


            if (! dragTarget.isLockXMovement()) {
                dragTarget.setxLocation((float)mousePos.getX());
                dragTarget.setxAcceleration(dragTarget.getxAcceleration() + xDistance);
                dragTarget.setyAcceleration(dragTarget.getyAcceleration() + yDistance);
                dragTarget.setyLocation((float) mousePos.getY());
            } else {
                //really gross why am i doing this
                Rotater tempRotater = null;
                for (Rotater r : rotatePlayers.getRotators()) {
                    if (r.getSlider().getSlider() == dragTarget) {
                        tempRotater = r;
                        break;
                    }
                }
                if (tempRotater != null) {
                    float newX = tempRotater.getSlider().getClosestXPosition((float) mousePos.getX());
                    dragTarget.setxLocation(newX);
                    if (tempRotater.getSlider().isHasClicked()) {
                        tempRotater.setSpeed(tempRotater.getSlider().getSpeed());
                        System.out.println("updating delay to " + tempRotater.getSpeed());
                        updateActiveDelays();
                    }

                }
            }
            dragTarget.setDragged(true);



            updateActiveSounds(dragTarget);
        }
    }

    private void updateActiveSounds(MovableBox target) {

        for (Rotater r: rotatePlayers.getRotators()) {
            if (target.getSound() != null) {

                if (CollisionManager.isPointInsideCircle(target, r)) {
                    if (!activeSounds.contains(target)) {
                        activeSounds.add(target);
                    }

                    target.setHit(true, r);
                    target.getSound().setLength(r.getSpeed());

                    double angleToCenter = CollisionManager.getAngle(target.getxLocation(), target.getyLocation(), r.getxOrigin(), r.getyOrigin());
                    target.setAngleToRotor(angleToCenter);
                    int soundDelay = Quantizer.getDelay(info.getDurationMS(Duration.WHOLE), r.getSpeed(), angleToCenter);
                    target.getSound().setDelay(soundDelay);

                    Point2D.Float p1 = new Point2D.Float(target.getxLocation(), target.getyLocation());
                    Point2D.Float p2 = new Point2D.Float(r.getxOrigin(), r.getyOrigin());
                    float distance = (float) Math.abs(p2.distance(p1));
                    float gain = 0.0f;


                    gain = map(distance, 0, r.getLength(), -12.0f, 0.0f);
                    target.getSound().setGain(gain);



                } else {
                    if (target.isHit() && target.getHitBy() == r) {
                        target.setHit(false, null);
                        target.getSound().setLoop(false);
                        if (activeSounds.contains(target)) {
                            activeSounds.remove(target);
                        }

                    }
                }
            }
        }

        Point2D.Float targetPoint = new Point2D.Float(target.getxLocation(), target.getyLocation());
        if (CollisionManager.isPointInsideRectangle(targetPoint, 0,0,getWidth(), getHeight()) == false ) {
            if (target.isClone()|| target.isRecording()) {
                //worried that this isn't causing an error

                //deleting from movables too tricky?
                //TODO: THISb
                target.flagToDelete();



                //target.setHide(true);

            } else {
                target.setxAcceleration(0.0f);
                target.setyAcceleration(0.0f);

                target.setxLocation(target.getInitialSpawnX());
                target.setyLocation(target.getInitialSpawnY());

            }

        }
    }

    public void mouseMoved() {
        MovableBox target = null;
        checkHover(movables);
        checkHover(sampleLibrary.getCurrentLibraryList());
        checkHoverTwo(menuUI.getButtons());


    }
    private void checkHoverTwo(ArrayList<UIButton> uiButtons) {
        for (UIButton m: uiButtons) {
            Point mousePosition = getMousePosition();
            if (mousePosition != null) {
                Point2D.Float p = new Point2D.Float(m.getxLocation(), m.getyLocation());
                Rectangle2D.Float r = new Rectangle2D.Float(m.getxLocation(), m.getyLocation(), m.getWidth(), m.getWidth());

                if (p != null && r.contains(mousePosition)) {
                    m.setHover(true);
                }else {
                    m.setHover(false);
                }
            }
        }
    }
    private void checkHover(ArrayList<MovableBox> movables) {
        for (MovableBox m: movables) {
            Point mousePosition = getMousePosition();
            if (mousePosition != null) {
                Point2D.Float p = new Point2D.Float(m.getxLocation(), m.getyLocation());
                if (p != null && mousePosition.distance(p) < m.getWidth()/2) {
                    m.setHover(true);
                }else {
                    m.setHover(false);
                }
            }
        }
    }
}