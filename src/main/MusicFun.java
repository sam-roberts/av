package main;

import animations.*;
import ddf.minim.AudioInput;
import ddf.minim.AudioOutput;
import ddf.minim.AudioRecorder;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import geomerative.RG;
import helpers.*;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;


public class MusicFun extends PApplet {

    public static final int LAG_COMPENSATION = 50;
    public static final int AVERAGE_LAG_COMPENSATION = 0;
    //home
    public String DATA_PATH = "D:/Users/Sam/Documents/GitHub/av/src/data/";

    //laptop
    //public final String DATA_PATH = "C:/Users/sam/Documents/GitHub/av/src/data/";
    Minim minim;
    FFT fft;
    Boolean keyTemp;

    Boolean DEBUG = false;

    DoubleClick doubleClick;

    SampleManager samples;
    AudioInput in;
    AudioOutput out;
    AudioRecorder recorder;

    boolean loopSample = false;

    PublicInformation info;
    ColourManager cm;

    ProcessingAnimation polygonAnimation;
    RotatePlayers rotatePlayers;

    ArrayList<MovableBox> movables;
    MovableBox dragTarget = null;
    MovableBox selectionTarget = null;
    VoiceRecordingManager voiceSampleList;


    PFont LARGE_TEXT;
    PFont MEDIUM_TEXT;

    public float SIZE_RATIO;


    RecordButton recordButton;

    BeatCounterAnimation beatCount;

    ArrayList<MovableBox> activeSounds;
    SampleLibrary sampleLibrary;


    CountdownAnimation count;

    boolean needClear = false;

    MenuUI menuUI;


    boolean hadSetup = false;
    long lastmemory = 0;
    int memoryTime = 0;
    private boolean touchScreen = false;
    Point lastMouse = null;


    boolean dynamicPerformance = false;
    int frameCategory = 0;
    private int timeSinceChange = 0;


    public static void main(String args[]) {
        PApplet.main(new String[] { "--present", "main.MusicFun" });
    }

    public void setup() {
        if (! hadSetup) {
            System.out.println("SETUPPPPPPPPPPPPPPPPPPPPPPPPP");

            System.out.println("directory: " + System.getProperty("user.dir"));

            DATA_PATH = System.getProperty("user.dir") + "/data/";


            minim = new Minim(this);

            size(displayWidth, displayHeight, P3D);
            SIZE_RATIO = displayWidth/1920f;
            background(255);
            noStroke();


            keyTemp = false;
            PFont LARGE_TEXT = createFont("Arial", 30, true);
            PFont MEDIUM = createFont("Arial", 24, true);

            textFont(LARGE_TEXT);
            info = new PublicInformation(DATA_PATH);
            info.setTempo(120);
            info.setMinim(minim);
            info.setAudioOut(out);
            info.setRatio(SIZE_RATIO);

            PImage circle = loadImage(info.getRootDirectory() + "images/soft_shadow.png");
            info.setCircle(circle);

            RG.init(this);
            smooth(8);
            //RG.ignoreStyles();


            cm = new ColourManager();
            info.setColourManager(cm);
            doubleClick = new DoubleClick();


            //clean up the jams
            File recordDir = new File(DATA_PATH + "recorded/");
            if (recordDir.exists()) {
                for (File f : recordDir.listFiles()) {
                    f.delete();
                }
            }




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

            // calculate averages based on a miminum octave width of 22 Hz
            // split each octave into three bands
            fft.logAverages(22, 12);
            rectMode(CORNERS);

            info.setInputFFT(fft);
            info.setInput(in);

            in.disableMonitoring();



            try {
                samples = new SampleManager(info, DATA_PATH + "sounds");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //getClass().getClassLoader().getResource()
            //samples = new SampleManager(info, "src/data/sounds");



            //ANIMTAIONS
            polygonAnimation = new PolygonAnimation(this, info);
            rotatePlayers = new RotatePlayers(this, info);
            try {
                sampleLibrary = new SampleLibrary(this, rotatePlayers, info);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            activeSounds = new ArrayList<MovableBox>();

            setupMovables();


            voiceSampleList = new VoiceRecordingManager();


            count = new CountdownAnimation(this, info);
            beatCount = new BeatCounterAnimation(this, info);


            menuUI = new MenuUI(this, info);

            hadSetup = true;
        }



    }

    private void setupMovables() {
        movables = new ArrayList<MovableBox>();
        float sizePadding = MovableBox.NODE_WIDTH * info.getRatio() * 1.6f;
        int numVerticalMax = (int) ((getHeight() - sizePadding) / sizePadding);
        int i = 0;
        int row = 0;
        for (Sample s: samples.getAllSamples()) {
            if (i == numVerticalMax) {
                row++;
                i=0;
            }
            MovableBox temp = new MovableBox(this,info,(MovableBox.NODE_WIDTH * SIZE_RATIO) + row*sizePadding,i*sizePadding + sizePadding,MovableBox.NODE_WIDTH * SIZE_RATIO,MovableBox.NODE_WIDTH * SIZE_RATIO);
            temp.setSound(s);

            movables.add(temp);

            i++;

        }
        recordButton = new RecordButton(this,info,getWidth()- MovableBox.RECORDED_WIDTH * SIZE_RATIO - 20,20,MovableBox.RECORDED_WIDTH * SIZE_RATIO ,MovableBox.RECORDED_HEIGHT * SIZE_RATIO );
        recordButton.setMovable(false);
        recordButton.setCircleShape(false);

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
        if (millis() - timeSinceChange > 200) {

            if (Math.round(frameRate) < 15 && millis() > 6000) {
                needClear = true;
                pressResetButton();
                System.out.println("frame rate getting too low - clearing some space");
            } else {
                timeSinceChange = millis();
            }
        }

        if (lastmemory != Runtime.getRuntime().totalMemory()) {
            lastmemory = Runtime.getRuntime().totalMemory();
            System.out.println("memory used: " + Runtime.getRuntime().totalMemory() + " num milliseconds since last update: " + (millis() - memoryTime ));
            memoryTime = millis();

        }

            background(246, 245, 241);

            rotatePlayers.play();
            recordButton.draw();

            count.drawTimed();

            beatCount.draw();


            //handles the case of clicking reset when it was already the last one clicked
            if (needClear) {
                resetSliders();
                resetMovables(movables);
                activeSounds.clear();
                needClear = false;
            }

            if (menuUI.getPresetClicked() != sampleLibrary.getCurrentLibrary()) {
                if (menuUI.isTempo()) {

                } else {
                    pressResetButton();
                }

            }
            drawMovables(movables);

            drawMovables(sampleLibrary.getCurrentLibraryList());


            if (polygonAnimation.isKeyTriggered()) {
                polygonAnimation.drawTimed();
            }

            //System.out.println(activeSounds.size() + " active sounds");


            for (MovableBox m : activeSounds) {
                int playing = info.getDurationMS(m.getSound().getLength()) * 4;
                int soundDelay = m.getSound().getDelay() * 4;
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


        if (recordButton.isCountdownFinished()) {
            recorder.beginRecord();
        }







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

            int i = 0;
            for (String category : cm.getSeedMap().keySet()) {
                Color c = cm.getRandomColor(category);
                fill(c.getRGB());

                text(category, 400 * info.getRatio(), 20 + (i * 20) * info.getRatio());
                i++;
            }

            if (DEBUG) {
                float time = millis();
                textSize(12);

                fill(Color.black.getRGB());
                text("frame rate: " + Math.round(frameRate) + " time is " + time + " / tempo is " + info.getTempo(), getWidth() / 2 + 600, 20);

                pushMatrix();

                translate(getWidth() - 200, 20);
                if (recorder != null && recorder.isRecording()) {
                    text("recording", 0, 0);
                } else {
                    text("not recording", 0, 0);
                }
                popMatrix();
            }




    }

    private void pressResetButton() {

        resetSliders();


        resetMovables(sampleLibrary.getCurrentLibraryList());
        sampleLibrary.setCurrentLibrary(menuUI.getPresetClicked());
        for (MovableBox m : sampleLibrary.getCurrentLibraryList()) {
            updateActiveSounds(m);
        }
    }

    private void resetSliders() {
        for (Rotater r: rotatePlayers.getRotators()) {
            r.setSpeed(r.getInitialSpeed());
            r.getSlider().setPositionGivenSpeed(r.getInitialSpeed());
            r.getSlider().fixPosition();
        }
        info.setTempo(120);

    }

    private void resetMovables(ArrayList<MovableBox> list) {
        Iterator<MovableBox> iterator = list.iterator();
        while (iterator.hasNext()) {
            MovableBox m = iterator.next();
            if (m.isClone() && !m.isOriginal()) {
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

            if (m.getSound() != null) {
                m.getSound().getMyPlayer().pause();
                m.getSound().getMyPlayer().rewind();


            }


        }
    }

    private void drawMovables(ArrayList<MovableBox> list) {
        int size = list.size();


        Iterator<MovableBox> iterator = list.iterator();
        while (iterator.hasNext()) {
            MovableBox box = iterator.next();
            if (box.isToDelete()) {
                box.deleteThis();
                iterator.remove();
            } else {
                //doCollisionStuff(box);
                if ((box.getxAcceleration() != 0 || box.getyAcceleration() != 0) && !box.isDragged() && box.isVisible()) {
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
                    stroke(box.getFill().darker().getRGB());
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

            float size = box.getWidth();
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
        /*


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



            }
            if (synth != null) {
                synth.setLoop(loopSample);


                (new Thread(synth)).start();


            }

        }
    */
    }

    private void tempoUp() {
        if (info.getTempo() < info.TEMPO_MAX) {
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
            int oldTempo = info.getTempo();
            info.setTempo(oldTempo - 10);
            updateActiveDelays();

        }
    }

    public void keyReleased() {
        if (keyCode == SHIFT) {
            loopSample = false;
        }

    }
    public void mousePressed() {
        findDragTarget(this.movables);
        if (dragTarget == null) {
            findDragTarget(sampleLibrary.getCurrentLibraryList());
        }
        MovableBox tempDragTarget = dragTarget;
        if (tempDragTarget != null) {
            if (doubleClick.isDoubleClick(tempDragTarget, this.millis()) && !tempDragTarget.isLockXMovement() && tempDragTarget != recordButton) {
                doubleClick.reset();
                System.out.println("double click");

                //TODO: add copy
                MovableBox temp= null;
                if (tempDragTarget == null) {
                    throw new RuntimeException("why is dragtarget null");
                }
                if (tempDragTarget.getLastChild() == null) {
                    try {
                        temp = tempDragTarget.cloneThis();
                        tempDragTarget.setChild(temp);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        temp = tempDragTarget.getLastChild().cloneThis();
                        tempDragTarget.getLastChild().setChild(temp);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

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

                if (movables.contains(tempDragTarget)) {
                    movables.add(temp);

                } else {
                    sampleLibrary.getCurrentLibraryList().add(temp);
                }
                updateActiveSounds(temp);


            } else {
                doubleClick.setClick(tempDragTarget, this.millis());

                if (!activeSounds.contains(tempDragTarget)) {
                    tempDragTarget.playsound();
                }
            }
        } else {
            pressedUI();
        }

        selectionTarget = dragTarget;
    }

    private void pressedUI() {
        Point mouseLocation = getMousePosition();
        if (mouseLocation != null) {
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
    }

    private void findDragTarget(ArrayList<MovableBox> movables) {
        Point p = getMousePosition();
        for (MovableBox m: movables) {

            if (p != null) {

                if (m.isCircleShape()) {
                    if (p.distance(new Point2D.Float(m.getxLocation(), m.getyLocation())) < m.getWidth()/2.0f) {
                        m.pressed();
                        dragTarget = m;
                        System.out.println("found target");
                        break;
                    }
                } else {
                    if (isPointInsideRectangle(m,p)) {
                        m.pressed();
                        dragTarget = m;
                        System.out.println("found target");
                        break;
                    }
                }
            }
        }
    }

    public void mouseReleased() {
        if (dragTarget != null) {
            dragTarget.setDragged(false);
            dragTarget.release();
            try {
                doReleaseStuff();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            dragTarget = null;
        }



    }

    private void doReleaseStuff() throws FileNotFoundException {
        //System.out.println("yolo " + movables.size());
        if (dragTarget.equals(recordButton)) {
            System.out.println("pressed record button");


            int numRecordings = samples.getNumSamplesKey("recorded");
            String voiceRecordingFilepath = DATA_PATH + "recorded/test" + (numRecordings + 1) + ".wav";

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
                MovableBox temp = new MovableBox(this, info, recordButton.getxLocation() - MovableBox.RECORDED_WIDTH * SIZE_RATIO,
                        recordButton.getyLocation() + MovableBox.RECORDED_HEIGHT* SIZE_RATIO, MovableBox.BIG_BUTTON * SIZE_RATIO, MovableBox.BIG_BUTTON * SIZE_RATIO);
                temp.setRecording(true);
                Sample s = samples.getNewestSample();
                s.setCategory("record");

                temp.setSound(s);
                movables.add(temp);

                updateActiveSounds(temp);
                recordButton.stopRecording();

            } else {
                recordButton.startRecording();
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
        if (!CollisionManager.isPointInsideRectangle(targetPoint, 0, 0, getWidth(), getHeight())) {
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

        Point p = getMousePosition();

        if (lastMouse == null && p != null) {
            lastMouse = p;
        }
        if (touchScreen) {
            if (p != null && lastMouse != null) {


                if (dragTarget == null) {
                    mousePressed();
                } else {

                    if (lastMouse.distance(p) < 30) {
                        mouseDragged();
                    } else {
                        mouseReleased();
                    }
                }
                lastMouse = p;

            }
        }



    }
    private void checkHoverTwo(ArrayList<UIButton> uiButtons) {
        for (UIButton m: uiButtons) {
            Point mousePosition = getMousePosition();
            if (mousePosition != null) {
                m.setHover(isPointInsideRectangle(m, mousePosition));
            }
        }
    }

    private boolean isPointInsideRectangle(MovableBox m, Point mousePosition) {
        Point2D.Float p = new Point2D.Float(m.getxLocation(), m.getyLocation());
        Rectangle2D.Float r = new Rectangle2D.Float(m.getxLocation(), m.getyLocation(), m.getWidth(), m.getHeight());
        return (p != null && r.contains(mousePosition));
    }

    private void checkHover(ArrayList<MovableBox> movables) {
        for (MovableBox m: movables) {
            Point mousePosition = getMousePosition();
            if (mousePosition != null) {
                Point2D.Float p = new Point2D.Float(m.getxLocation(), m.getyLocation());
                if (m.isCircleShape()) {
                    if (p != null && mousePosition.distance(p) < m.getWidth() / 2) {
                        m.setHover(true);
                    } else {
                        m.setHover(false);
                    }
                } else {
                    m.setHover(isPointInsideRectangle(m, mousePosition));
                }
            }
        }
    }
}