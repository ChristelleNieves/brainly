// All code below was written by Christelle Nieves
// Date: 07/06/2020

package com.example.bohrd;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    Button startButton;
    TextView roundTextView;
    TextView recordTextView;
    Button [] buttons = new Button[9];
    int turnNumber = 0; // Start at turn number zero
    int record = 0; // The record rounds the player has lasted
    int patternLength = 3; // The length of the randomized pattern starts at 3
    int numClicks = 0; // The number of button choices the player has made
    boolean playerLost = false;
    ArrayList<Button> playerChoices = new ArrayList<>(); // Holds the player's button choices
    ArrayList<Button> patternButtons = new ArrayList<>(); // Holds the buttons corresponding to the pattern

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the buttons
        buttons[0] = findViewById(R.id.b1);
        buttons[1] = findViewById(R.id.b2);
        buttons[2] = findViewById(R.id.b3);
        buttons[3] = findViewById(R.id.b4);
        buttons[4] = findViewById(R.id.b5);
        buttons[5] = findViewById(R.id.b6);
        buttons[6] = findViewById(R.id.b7);
        buttons[7] = findViewById(R.id.b8);
        buttons[8] = findViewById(R.id.b9);
        startButton = findViewById(R.id.startButton);

        // Get the textViews
        roundTextView = findViewById(R.id.roundTextView);
        recordTextView = findViewById(R.id.recordTextView);

        // Set the textView text
        roundTextView.setText("Round: " + turnNumber);
        recordTextView.setText("Record: " + record);

        // Set the onTouch Listeners for the board buttons
        for (Button button : buttons) {
            button.setOnTouchListener(this);
        }

        // Set the onClick Listener for the start button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the player hasn't started yet but has clicked some buttons, set the number of clicks back to 0
                if (turnNumber == 0 && numClicks > 0) {
                    numClicks = 0;
                }

                // If the start button says play again, reset the playerLost variable to false since its a new round
                if (startButton.getText() == "Play Again") {
                    playerLost = false;
                }

                // Increment the turn number and update the textViews
                turnNumber++;
                startButton.setText("Start");
                roundTextView.setText("Round: " + turnNumber);
                recordTextView.setText("Record: " + record);

                // Generate the new randomized pattern
                int [] newPattern = generatePattern(); //length 3
                patternButtons.clear();

                // Add the buttons for the newly generated pattern into the patternButtons arrayList
                for (int value : newPattern) {
                    patternButtons.add(buttons[value]);
                }

                // Display the blink effect for the pattern
                blinkEffect(patternButtons);
            }
        });
    }

    // OnTouch listener for the board buttons will allow us to save the buttons the player clicks on
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return onTouchHelper(v, event);
    }

    // Changes the color of the button the player clicks on to red
    // Keeps track of the buttons that are clicked and adds them to an arrayList
    // Then compares the buttons the players clicks on to the buttons in the pattern
    // If they match, the player will move on to the next round
    // If they do not match, the player loses and everything is reset
    public boolean onTouchHelper(View v, MotionEvent event) {
        v.setBackgroundColor(Color.rgb( 203, 248, 98));

        if(event.getAction() == MotionEvent.ACTION_UP) {
            Button clickedButton = (Button) v;
            v.setBackgroundColor(0xFF0E0E33);

            if (turnNumber != 0) {
                // Add each button that is clicked to the playerChoices arrayList & increment the number of clicks
                playerChoices.add(clickedButton);
                numClicks++;

                // If the user finishes choosing their buttons, compare them to the pattern
                if (numClicks == patternButtons.size()) {
                    // Go through the playerChoices arrayList and compare each button to the pattern buttons
                    for (int i = 0; i < playerChoices.size(); i++) {
                        if (playerChoices.get(i) != patternButtons.get(i)) {
                            // If we encounter buttons that do not match, display a toast telling the player they lost
                            Toast.makeText(getApplicationContext(), "You Lose :(", Toast.LENGTH_SHORT).show();

                            // Reset the game values and set the start button to say "play again"
                            startButton.setText("Play Again");
                            numClicks = 0;
                            turnNumber = 0;
                            playerLost = true;
                            patternLength = 3;
                        }
                    }
                    // Make sure to clear the patternButtons and playerChoices arrayLists
                    patternButtons.clear();
                    playerChoices.clear();

                    // If the player did not lose, increment the record and add one to the patternLength
                    if (!playerLost) {
                        record++;
                        patternLength++;
                        numClicks = 0;

                        // Click the start button to generate and display a new pattern and start the process again
                        startButton.performClick();
                    }
                }
            }

            return true;
        }

        return false;
    }

    // Returns a random number 0-8
    public int getRandomNumber() {
        Random rand = new Random();
        return rand.nextInt(9);
    }

    // Generate a random pattern array of the specified length
    public int[] generatePattern() {
        int [] pattern = new int[patternLength];

        for (int i = 0; i < pattern.length; i++) {
            pattern[i] = getRandomNumber();
        }

        return pattern;
    }

    // This method manages the blink effect for the buttons.
    public void blinkEffect(ArrayList<Button> b) {
        final AnimatorSet pattern = new AnimatorSet();
        ObjectAnimator [] anims = new ObjectAnimator[b.size()];

        // Create Object Animators for each button in the pattern
        for (int i = 0; i < b.size(); i++) {
            ObjectAnimator anim = ObjectAnimator.ofInt(b.get(i), "backgroundColor",  Color.rgb( 203, 248, 98), 0xFF0E0E33);
            anim.setDuration(800);
            anim.setEvaluator(new ArgbEvaluator());
            //anim.setRepeatMode(ValueAnimator.REVERSE);
            anim.setRepeatCount(0);
            anims[i] = anim;
        }

        // Play the pattern animations sequentially
        pattern.playSequentially(anims);
        pattern.start();
    }
}

