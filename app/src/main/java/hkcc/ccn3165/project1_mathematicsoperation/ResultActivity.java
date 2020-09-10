package hkcc.ccn3165.project1_mathematicsoperation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class ResultActivity extends Activity {

    private Button restart;
    private TextView resultDisplay;
    private String displayText;
    private ImageView resultPicture;
    private Button BacktoMainMenu;
    private TextToSpeech mTTS;
    private String audioResult;

    int correctAnswer, answeredQuestion, savedTime, timeLeft_minutes, timeLeft_seconds, numberOfQuestion;
    String timeLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        restart = (Button)findViewById(R.id.restart);
        resultDisplay = (TextView)findViewById(R.id.resultDisplay);
        resultPicture = (ImageView)findViewById(R.id.resultPicture);
        BacktoMainMenu = (Button)findViewById(R.id.backToMainMenu);

        restart.setOnClickListener(restartApp);
        BacktoMainMenu.setOnClickListener(backToMM);

        //getting the ResultActivity from MainActivity.class
        Intent MAIntent = getIntent();
        correctAnswer = MAIntent.getIntExtra("correctAnswer",0);
        answeredQuestion = MAIntent.getIntExtra("answeredQ", 0);
        savedTime = MAIntent.getIntExtra("originalTime", 0);
        timeLeft = MAIntent.getStringExtra("timeLeft" );
        timeLeft_minutes = MAIntent.getIntExtra("timeLeft_Minute", 0);
        timeLeft_seconds = MAIntent.getIntExtra("timeLeft_Second", 0);
        numberOfQuestion = MAIntent.getIntExtra("numberOfQ", 0);

        //set the display ResultActivity
        displayText = String.format("Total question answered: %d \n " +
                "Total correct answer: %d \n" +
                "Total wrong answer: %d \n" +
                "Time left: %s",answeredQuestion-1,  correctAnswer, answeredQuestion-1-correctAnswer, timeLeft);
        resultDisplay.setText(displayText);

        //set the text to speech
        audioResult = String.format(" %d questions you have answered: \n " +
                "%d of them are correct answers: \n" +
                "%d of them are wrong answers: \n", answeredQuestion - 1, correctAnswer, answeredQuestion - 1 - correctAnswer);

        if(timeLeft_minutes == 0 && timeLeft_seconds == 0){
            audioResult = audioResult + "no time left";
        }else if(timeLeft_minutes == 0) {
            audioResult = audioResult + String.format("Time left: %d seconds", timeLeft_seconds);
        }else{
            audioResult = audioResult + String.format("Time left: %d minutes and %d seconds", timeLeft_minutes, timeLeft_seconds);
        }

        //initialize the text to speech object
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        speak(audioResult);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        //depend on the accuracy rate to display 3 different picture
        if( (float)correctAnswer/(float)(answeredQuestion - 1) >= 0.75){
            resultPicture.setImageResource(R.drawable.welldone);
        }else if( (float)correctAnswer/(float)(answeredQuestion - 1) >= 0.5){
            resultPicture.setImageResource(R.drawable.goodjob);
        }else{
            resultPicture.setImageResource(R.drawable.workhard);
        }
    }

    //restart the application
    private Button.OnClickListener restartApp = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            speak("");
            Intent ReIntent = new Intent(ResultActivity.this, MainActivity.class);
            ReIntent.putExtra("userSetNumberOfQuestion", numberOfQuestion);
            ReIntent.putExtra("userSetTimeDuration", savedTime);
            ReIntent.setFlags(ReIntent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ReIntent);
            finish();
        }
    };

    //back to the main menu
    private Button.OnClickListener backToMM = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            speak("");
            Intent ReIntent = new Intent(ResultActivity.this, WelcomeActivity.class);
            ReIntent.putExtra("userSetNumberOfQuestion", (numberOfQuestion));
            ReIntent.putExtra("TimeDuration", savedTime);
            ReIntent.setFlags(ReIntent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(ReIntent);
            finish();
        }

    };

    //word to speak by the text to speech object
    private void speak(String answerStatus) {
        float pitch =  1.0f;
        float speed =  0.8f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        mTTS.speak(answerStatus, TextToSpeech.QUEUE_FLUSH, null);
    }
}
