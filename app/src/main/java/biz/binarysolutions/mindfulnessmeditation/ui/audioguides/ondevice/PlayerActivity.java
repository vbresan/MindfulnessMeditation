package biz.binarysolutions.mindfulnessmeditation.ui.audioguides.ondevice;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

import biz.binarysolutions.mindfulnessmeditation.Preferences;
import biz.binarysolutions.mindfulnessmeditation.R;
import biz.binarysolutions.mindfulnessmeditation.data.CompletedMeditation;
import biz.binarysolutions.mindfulnessmeditation.data.CompletedMeditationDao;
import biz.binarysolutions.mindfulnessmeditation.data.Meditation;
import biz.binarysolutions.mindfulnessmeditation.data.MeditationDatabase;

/**
 *
 */
public class PlayerActivity extends Activity
    implements
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener {

    private AwakeMediaPlayer player;
    private Handler          handler = new Handler();

    private String meditationId;

    /**
     *
     * @param id
     */
    private void setPlayIcon(int id) {

        ImageButton button = findViewById(R.id.imageButtonPlayOrPause);
        if (button != null) {
            button.setImageResource(id);
        }
    }

    /**
     *
     */
    private void onPlayOrPausePressed() {

        if (player == null) {
            return;
        }

        if (player.isPlaying()) {
            player.pause();
            setPlayIcon(R.drawable.play_arrow_24dp);
        } else {
            player.start();
            setPlayIcon(R.drawable.pause_24dp);
        }
    }

    /**
     *
     */
    private void releasePlayer() {

        if (player == null) {
            return;
        }

        if (player.isPlaying()) {
            player.stop();
        }

        player.release();
        player = null;
    }

    /**
     *
     */
    private void onStopPressed() {
        releasePlayer();
        finish();
    }

    /**
     *
     */
    private void setPlayerButtonListeners() {

        ImageButton button;

        button = findViewById(R.id.imageButtonPlayOrPause);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlayOrPausePressed();
                }
            });
        }

        button = findViewById(R.id.imageButtonStop);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onStopPressed();
                }
            });
        }
    }

    /**
     *
     * @param meditation
     */
    private void showPlayerData(Meditation meditation) {

        TextView textView;

        textView = findViewById(R.id.textViewTitle);
        if (textView != null) {
            textView.setText(meditation.getTitle());
        }

        textView = findViewById(R.id.textViewAuthor);
        if (textView != null) {
            textView.setText(meditation.getAuthor());
        }

        textView = findViewById(R.id.textViewToEnd);
        if (textView != null) {
            textView.setText("-" + meditation.getDuration());
        }

        View seekBar = findViewById(R.id.seekBarProgress);
        if (seekBar != null) {
            seekBar.setEnabled(false);
        }

        setPlayIcon(R.drawable.pause_24dp);
        setPlayerButtonListeners();
    }

    /**
     *
     * @param duration
     */
    private void setSeekBarDuration(int duration) {

        SeekBar seekBar = findViewById(R.id.seekBarProgress);
        if (seekBar != null) {
            seekBar.setMax(duration);
        }
    }

    /**
     *
     * @param seconds
     * @return
     */
    private String formatSeconds(int seconds) {

        int minutes   = seconds / 60;
        int remaining = seconds % 60;

        return String.format("%02d:%02d", minutes, remaining);
    }

    /**
     *
     */
    private void updateProgress() {

        if (player == null) {
            return;
        }

        int position = player.getCurrentPosition();
        int duration = player.getDuration();

        SeekBar seekBar = findViewById(R.id.seekBarProgress);
        if (seekBar != null) {
            seekBar.setProgress(position);
        }

        TextView textView;

        textView = findViewById(R.id.textViewFromStart);
        if (textView != null) {
            textView.setText(formatSeconds(position / 1000));
        }

        textView = findViewById(R.id.textViewToEnd);
        if (textView != null) {
            textView.setText("-" + formatSeconds((duration - position) / 1000));
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updateProgress();
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    /**
     *
     * @param meditation
     * @return
     */
    private Uri getFileUri(Meditation meditation) {

        File   directory = Preferences.getStorageDirectory(this);
        String fileName  = meditation.getId() + ".mp3";

        return Uri.fromFile(new File(directory, fileName));
    }

    /**
     *
     * @param meditation
     */
    private void initializeMediaPlayer(Meditation meditation) {

        try {
            player = new AwakeMediaPlayer(this);
            player.setOnPreparedListener(this);
            player.setOnCompletionListener(this);
            player.setDataSource(this, getFileUri(meditation));
            player.prepareAsync();
        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     *
     * @return
     */
    private boolean isScreenOn() {

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        return pm.isInteractive();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String key = getString(R.string.extra_key_meditation);
        Meditation meditation = (Meditation)
            getIntent().getSerializableExtra(key);

        if (meditation == null) {
            finish();
            return;
        }

        this.meditationId = meditation.getId();
        showPlayerData(meditation);
        initializeMediaPlayer(meditation);
    }

    @Override
    protected void onStop() {

        if (isScreenOn()) {
            // another activity has taken the screen or app is in background
            releasePlayer();
            finish();
        }

        super.onStop();
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        setSeekBarDuration(player.getDuration());
        player.start();
        updateProgress();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        final CompletedMeditation meditation = new CompletedMeditation(meditationId);

        final CompletedMeditationDao dao =
            MeditationDatabase.getDatabase(getApplicationContext())
                .completedMeditationDao();

        MeditationDatabase.databaseWriteExecutor.execute(
            () -> dao.insert(meditation)
        );

        releasePlayer();
        finish();
    }
}
