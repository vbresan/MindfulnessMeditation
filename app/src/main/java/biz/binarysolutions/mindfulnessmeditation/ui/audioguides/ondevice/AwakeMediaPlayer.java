package biz.binarysolutions.mindfulnessmeditation.ui.audioguides.ondevice;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.PowerManager;

/**
 *
 */
public class AwakeMediaPlayer extends MediaPlayer {

    private Context context;

    /**
     *
     * @param context
     */
    public AwakeMediaPlayer(Context context) {
        this.context = context;
    }

    @Override
    public void start() throws IllegalStateException {
        setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        super.start();
    }
}
