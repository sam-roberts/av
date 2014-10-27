package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Created by Sam on 13/10/2014.
 */
public class MenuUI extends ProcessingAnimation {

    ArrayList<UIButton> buttons;
    private final int BUTTON_WIDTH = 65;
    private final int NUM_PRESETS = 7;
    private final int NUM_OTHER = 2;
    private final int DIVIDER_BORDER = 20;

    public static final int TEMPO_DOWN = 1;
    public static final int TEMPO_UP = 2;


    private final float CENTER_OFFSET = (BUTTON_WIDTH * (NUM_PRESETS+ NUM_OTHER)) / 2.0f;

    private int presetClicked = 0;
    public MenuUI(PApplet p, PublicInformation info) {
        super(p, info);
        buttons = new ArrayList<UIButton>();
        for (int i = 0; i < NUM_PRESETS + NUM_OTHER; i++) {
            String buttonText;
            Color c;
            if (i < NUM_PRESETS) {
                if (i == 0) {
                    buttonText = "Reset";
                    c = new Color(144,189,128);

                } else {
                    buttonText = Integer.toString(i);
                    c = new Color(186,178,168);

                }
            } else {
                if (i == (NUM_PRESETS + NUM_OTHER - 2)) {
                    buttonText = "-";
                    c = new Color(128,163,189);

                } else {
                    buttonText = "+";
                    c = new Color(203,114,116);
                }

            }
            UIButton button = new UIButton(p,info, (int) ((int) p.getWidth()/2 + (i*BUTTON_WIDTH - CENTER_OFFSET)),p.getHeight()-BUTTON_WIDTH, buttonText, BUTTON_WIDTH,BUTTON_WIDTH);
            button.setInitialFill(c);
            buttons.add(button);
        }
    }

    @Override
    protected void drawAnimation() {
        for (int i = 0; i < buttons.size(); i++) {
            if (i == presetClicked && i > 0 && i < NUM_PRESETS) {
                buttons.get(i).turnOn();
            } else {
                buttons.get(i).turnOff();

            }

            buttons.get(i).draw();
        }
        p.fill(86,82,74);
        p.stroke(86, 82, 74, 150);

        p.textSize(24);
        float left = p.getWidth()/2.0f - CENTER_OFFSET;
        p.text("Presets", left + (((NUM_PRESETS/2) + 1) * BUTTON_WIDTH), p.getHeight() - BUTTON_WIDTH - 30);
        p.text("Change Tempo", left + ((NUM_PRESETS + NUM_OTHER/2) * BUTTON_WIDTH), p.getHeight() - BUTTON_WIDTH - 30);

        for (int i = 0; i < NUM_OTHER + NUM_PRESETS - 1; i++) {
            float xLocation = left + (i+1)*BUTTON_WIDTH;
            p.line(xLocation, p.getHeight() - (BUTTON_WIDTH )+ DIVIDER_BORDER, xLocation, p.getHeight()-DIVIDER_BORDER);
        }

    }

    @Override
    protected void resetTimedAnimation() {

    }

    public ArrayList<UIButton> getButtons() {
        return buttons;
    }

    public Rectangle2D.Float getWholeSize() {
        float left = buttons.get(0).getxLocation();
        return new Rectangle2D.Float(left, buttons.get(0).getyLocation(), left + ((NUM_OTHER + NUM_PRESETS) * BUTTON_WIDTH), p.getHeight());
    }

    public void clickedAt(Point mouseLocation) {
        float xDistance = (float) (mouseLocation.getX() - buttons.get(0).getxLocation());
        int which = (int) (xDistance/BUTTON_WIDTH);
        presetClicked = which;
    }

    public int getPresetClicked() {
        return presetClicked;
    }

    public boolean isTempo() {
        return (presetClicked >= NUM_PRESETS) ;

    }

    public int getTempoButton() {
        if (presetClicked == NUM_PRESETS) {
            return TEMPO_DOWN;
        } else if (presetClicked == NUM_PRESETS + 1) {
            return TEMPO_UP;
        } else {
            return 0;
        }
    }
}
