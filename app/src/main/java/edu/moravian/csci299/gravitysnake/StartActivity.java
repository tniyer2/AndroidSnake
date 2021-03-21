package edu.moravian.csci299.gravitysnake;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * This class is the StartActivity. It gets created in the beginning activity_start layout.
 */
public class StartActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private GameModel gameModel;

    /**
     * Initializes gameModel, the start Button, and the level select SeekBar.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        gameModel = (new ViewModelProvider(this)).get(GameModel.class);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        SeekBar bar = findViewById(R.id.levelSelectBar);
        bar.setOnSeekBarChangeListener(this);
        gameModel.setCurrentLevel(bar.getProgress());
    }

    /**
     * Called when the start Button was clicked.
     * Sends an intent to start GameActivity with extra 'level' being the level chosen.
     * @param v View that was clicked.
     */
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("level", gameModel.getCurrentLevel());
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
        gameModel.setCurrentLevel(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
