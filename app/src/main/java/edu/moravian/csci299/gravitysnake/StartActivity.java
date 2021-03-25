package edu.moravian.csci299.gravitysnake;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * This class is the StartActivity. It gets created in the beginning activity_start layout.
 */
public class StartActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private MediaPlayer mediaPlayer;
    private SharedPreferences preferences;

    private SeekBar levelSelectBar;
    private TextView levelText;
    private TextView highScoreText;

    /**
     * Initializes gameModel, the start Button, and the level select SeekBar.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        preferences = getPreferences(Context.MODE_PRIVATE);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        levelSelectBar = findViewById(R.id.levelSelectBar);
        levelSelectBar.setOnSeekBarChangeListener(this);

        levelText = findViewById(R.id.levelText);
        highScoreText = findViewById(R.id.highScoreText);
        setLevelAndScoreText();

        //set up for music, mediaPlayer and music switch
        mediaPlayer = new MediaPlayer();
        setAudioResource();

        SwitchCompat musicSwitch = findViewById(R.id.musicSwitch);
        musicSwitch.setChecked(true);
        musicSwitch.setText(R.string.music);
        musicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                mediaPlayer.start();
            else
                mediaPlayer.pause();
        });
    }

    private void setLevelAndScoreText()
    {
        int currentLevel = levelSelectBar.getProgress();

        String text = getResources().getStringArray(R.array.level_name_array)[currentLevel];
        levelText.setText(text);
        int score = getHighScore(currentLevel);
        highScoreText.setText(String.format(getString(R.string.high_score_text), score));
    }

    /**
     * Called when the start Button was clicked.
     * Sends an intent to start GameActivity with extra 'level' being the level chosen.
     * @param v View that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", levelSelectBar.getProgress());
        startActivity(intent);
    }

    /**
     * Called when level select SeekBar's progress changes.
     * Sets the gameModel's current level to the new progress.
     * @param seekBar SeekBar that had it's progress changed.
     * @param progress number that is the new progress.
     * @param fromUser true if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        setLevelAndScoreText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;

        int level = data.getIntExtra("level", 0);
        int score = data.getIntExtra("score", 0);
        Log.d("StartActivity", "OnActivityResult, level: " + level);
        Log.d("StartActivity", "OnActivityResult, score: " + score);

        setHighScore(level, score);
        setLevelAndScoreText();
    }

    private int getHighScore(int level) {
        String key = String.format(getString(R.string.high_score_preference_key), level);

        return preferences.getInt(key, 0);
    }

    private void setHighScore(int level, int score) {
        if (score > getHighScore(level))
        {
            String key = String.format(getString(R.string.high_score_preference_key), level);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, score);
            editor.apply();
        }
    }

    private void setAudioResource() {
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.booamf);
        if (afd != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                mediaPlayer.prepare();
                afd.close();
                mediaPlayer.start();
            } catch (IOException ex) {
                Log.e("MainActivity", "set audio resource failed:", ex);
            }
        }
    }

    /** Does nothing but must be provided. */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    /** Does nothing but must be provided. */
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
