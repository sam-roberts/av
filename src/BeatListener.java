import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.AudioPlayer;
import ddf.minim.analysis.BeatDetect;

/**
 * Created by Sam on 31/03/2014.
 */
class BeatListener implements AudioListener
{
    private BeatDetect beat;
    private AudioInput source;

    BeatListener(BeatDetect beat, AudioInput source)
    {
        this.source = source;
        this.source.addListener(this);
        this.beat = beat;
    }

    public void samples(float[] samps)
    {
        beat.detect(source.mix);
    }

    public void samples(float[] sampsL, float[] sampsR)
    {
        beat.detect(source.mix);
    }
}