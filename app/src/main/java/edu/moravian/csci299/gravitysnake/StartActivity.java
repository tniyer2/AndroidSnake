package edu.moravian.csci299.gravitysnake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
     * Initializes preferences, the start Button, the level select SeekBar,
     * and the mediaPlayer for the background music.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        preferences = getSharedPreferences("snake_game", Context.MODE_PRIVATE);

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

    /**
     * Updates the level and score text.
     */
    private void setLevelAndScoreText()
    {
        int currentLevel = levelSelectBar.getProgress();

        String text = getResources().getStringArray(R.array.level_name_array)[currentLevel];
        levelText.setText(text);

        int highScore = getHighScore(preferences, this, currentLevel);
        highScoreText.setText(String.format(getString(R.string.high_score_text), highScore));
    }

    /**
     * Called when the start Button is clicked.
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
     * Updates level and score text when the level select SeekBar's progress changes.
     * @param seekBar the SeekBar that had it's progress changed.
     * @param progress the new progress value.
     * @param fromUser true if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        setLevelAndScoreText();
    }

    /**
     * Updates level and score text when activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setLevelAndScoreText();
    }

    /**
     * Returns the high score for a level from a SharedPreferences.
     * @param preferences where you want to get the high score from.
     * @param context the context this function is being called from.
     * @param level the level you want the high score of.
     * @return The high score for the level you chose.
     */
    public static int getHighScore(SharedPreferences preferences, Context context, int level) {
        String key = String.format(context.getString(R.string.high_score_preference_key), level);

        return preferences.getInt(key, 0);
    }

    /**
     * Saves a given score for a level as the high score if it is
     * greater than the current existing high score.
     * @param preferences where you want to save the high score to.
     * @param context the context this function is being called from.
     * @param level the level to set the high score of.
     * @param score the new score.
     */
    public static void setHighScore(SharedPreferences preferences, Context context, int level, int score) {
        if (score > getHighScore(preferences, context, level))
        {
            String key = String.format(context.getString(R.string.high_score_preference_key), level);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, score);
            editor.apply();
        }
    }

    /**
     * Preparing the audio so that music can be played.
     */
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
