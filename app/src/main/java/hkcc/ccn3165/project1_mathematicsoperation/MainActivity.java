package hkcc.ccn3165.project1_mathematicsoperation;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static int START_TIME;
    private int savedTime;
    private TextView questionNumber;
    private TextView question;
    private Button nextQuestion;
    private Button skipQuestion;
    private Button submit;
    private Button checkResult;
    private EditText userAns;
    private TextView messagePrompt;
    private ImageView messagePicture;
    private TextToSpeech mTTS;
    private String wordToSpeak;

    private TextView countDownClock;
    private CountDownTimer CountDownTimer;
    private int minutes;
    private int seconds;
    private long TimerLeftInMills;
    private String timeLeft;
    private int numberOfQuestion;

    private int num1, num2, operator, questionAns, userInput=-1;
    private int correctAns=0, answeredQuestion=0;
    private String display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionNumber = (TextView)findViewById(R.id.questionNumber);
        question = (TextView)findViewById(R.id.question);
        nextQuestion = (Button)findViewById(R.id.nextQuestion);
        submit = (Button)findViewById(R.id.submit);
        skipQuestion = (Button)findViewById(R.id.skip);
        checkResult = (Button)findViewById(R.id.checkResult);
        userAns = (EditText)findViewById(R.id.userAnswer);
        messagePrompt = (TextView)findViewById(R.id.messagePrompt);
        messagePicture = (ImageView)findViewById(R.id.messagePicture);
        countDownClock = (TextView)findViewById(R.id.countDown);

        nextQuestion.setOnClickListener(nextQ);
        submit.setOnClickListener(submitAnswer);
        skipQuestion.setOnClickListener(skipQ);
        checkResult.setOnClickListener(checkRes);

        //getting the data from the SettingActivity page
        Intent SettingIntent = getIntent();
        numberOfQuestion = SettingIntent.getIntExtra("userSetNumberOfQuestion",10);
        START_TIME = SettingIntent.getIntExtra("userSetTimeDuration", 5);
        TimerLeftInMills = START_TIME * 1000 * 60;
        savedTime = START_TIME;

        watcher(userAns, submit);
        answeredQuestion = answeredQuestion + 1;
        questionNumber.setText("Question " + Integer.toString(answeredQuestion));
        nextQuestion.setVisibility(View.INVISIBLE);
        generateQ();
        startTimer();

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
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

    }

    //After submit the answer, then go to the next question
    private Button.OnClickListener nextQ = new Button.OnClickListener(){
        public void onClick(View v) {
            userAns.setEnabled(true);
            answeredQuestion = answeredQuestion + 1;
            mTTS.stop();
            if (answeredQuestion < numberOfQuestion+1) {
                nextQuestion.setVisibility(View.INVISIBLE);
                messagePrompt.setText("");
                userAns.setText("");
                messagePicture.setImageResource(android.R.color.transparent);
                questionNumber.setText("Question " + Integer.toString(answeredQuestion));
                submit.setVisibility(View.VISIBLE);
                skipQuestion.setVisibility(View.VISIBLE);
                generateQ();
            }else{
                userAns.setText("");
                userAns.setEnabled(false);
                questionNumber.setText("");
                question.setText("Check your result");
                messagePrompt.setText("");
                stopTimer();
                messagePicture.setImageResource(0);
                submit.setVisibility(View.INVISIBLE);
                nextQuestion.setVisibility(View.INVISIBLE);
                skipQuestion.setVisibility(View.INVISIBLE);
                checkResult.setVisibility(View.VISIBLE);
                countDownClock.setVisibility(View.INVISIBLE);
                userAns.setVisibility(View.INVISIBLE);
                speak("You have finished the Test. Please check your result");
            }
        }
    };

    //submit the answer input buy the user
    private Button.OnClickListener submitAnswer = new Button.OnClickListener(){
        public void onClick(View V){

            Drawable picture;

            submit.setVisibility(View.INVISIBLE);
            skipQuestion.setVisibility(View.INVISIBLE);
            nextQuestion.setVisibility(View.VISIBLE);
            userInput = Integer.parseInt(userAns.getText().toString());
            if(userInput == questionAns){
                messagePrompt.setText("Correct Answer");
                picture = getResources().getDrawable(R.drawable.correct);
                messagePicture.setImageDrawable(picture);
                correctAns = correctAns+1;
                userAns.setEnabled(false);
                speak("Correct Answer");
            }else{
                messagePrompt.setText("Wrong Answer");
                picture = getResources().getDrawable(R.drawable.incorrect);
                messagePicture.setImageDrawable(picture);
                userAns.setEnabled(false);
                userAns.setText(Integer.toString(questionAns));
                speak("Wrong Answer");
            }

        }
    };

    //let the user to skip the question if they need
    private Button.OnClickListener skipQ = new Button.OnClickListener(){
        public void onClick(View v) {
            Drawable picture;

            submit.setVisibility(View.INVISIBLE);
            skipQuestion.setVisibility(View.INVISIBLE);
            nextQuestion.setVisibility(View.VISIBLE);
            messagePrompt.setText("Wrong Answer");
            picture = getResources().getDrawable(R.drawable.incorrect);
            messagePicture.setImageDrawable(picture);
            userAns.setEnabled(false);
            userAns.setText(Integer.toString(questionAns));
            speak("You skip this question");
        }
    };

    //checking the answer input by the user is correct or not
    private Button.OnClickListener checkRes = new Button.OnClickListener(){
        public void onClick(View v) {
            speak(" ");
            Intent MAIntent = new Intent(MainActivity.this, ResultActivity.class);
            MAIntent.putExtra("correctAnswer",correctAns);
            MAIntent.putExtra("answeredQ", answeredQuestion);
            MAIntent.putExtra("numberOfQ", numberOfQuestion);
            MAIntent.putExtra("timeLeft",timeLeft);
            MAIntent.putExtra("timeLeft_Minute",minutes);
            MAIntent.putExtra("timeLeft_Second",seconds);
            MAIntent.putExtra("originalTime",savedTime);
            MAIntent.setFlags(MAIntent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(MAIntent);
            finish();
        }
    };

    /*to check that is there are any input on the EditText
    if not it will disable some button function
     */
        void watcher(final EditText message_body, final Button Send)
    {
        message_body.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s)
            {
                if(message_body.length() == 0)
                    Send.setEnabled(false); //disable send button if no text entered
                else
                    Send.setEnabled(true);  //otherwise enable

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            }
        });
        if(message_body.length() == 0) Send.setEnabled(false);//disable at app start
    }

    //method to generate the question
    void generateQ (){
        Random rand = new Random();

        //determine +, -, *, /
        operator = rand.nextInt(4);

        switch (operator) {

            //handle the question with num1 + num2
            case 0:
                num1 = rand.nextInt(99) + 1;
                do {
                    num2 = rand.nextInt(99) + 1;
                }while(num2 == num1);
                questionAns = num1 + num2;
                display = Integer.toString(num1) + " + " + Integer.toString(num2);
                question.setText(display);
                break;

            //handle the question with num1 - num2
            case 1:
                num1 = rand.nextInt(99) + 1;
                num2 = num1 + rand.nextInt(99 - num1) + 1;
                questionAns = num2 - num1;
                display = Integer.toString(num2) + " - " + Integer.toString(num1);
                question.setText(display);
                break;

            //handle the question with num1 * num2
            case 2:
                num1 = rand.nextInt(19) + 1;
                do {
                    num2 = rand.nextInt(19) +1;
                } while (num2 == num1);
                questionAns = num1 * num2;
                display = Integer.toString(num1) + " * " + Integer.toString(num2);
                question.setText(display);
                break;

            //handle the question with num1 / num2
            case 3:
                int[] num2Choice = new int[20];
                int num2place = 0;
                int choseNum2;
                num1 = rand.nextInt(99) + 1;
                //find the factor of num1 and store it to num2Choice[]
                for(int i=1;i<num1;i++){
                    if(num1%i==0){
                        num2Choice[num2place] = i;
                        num2place = num2place + 1;
                    }
                }
                //random select a factor of num1
                choseNum2 = rand.nextInt(num2place);
                num2 = num2Choice[choseNum2];
                questionAns = num1 / num2;
                display = Integer.toString(num1) + " / " + Integer.toString(num2);
                question.setText(display);
                break;
        }
    }

    //start the timer
    private void startTimer(){
        CountDownTimer = new CountDownTimer(TimerLeftInMills, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                TimerLeftInMills = millisUntilFinished;
                updateCountDownText();
            }

            //what will happen when the timer is 0
            @Override
            public void onFinish() {
                submit.setVisibility(View.INVISIBLE);
                skipQuestion.setVisibility(View.INVISIBLE);
                nextQuestion.setVisibility(View.INVISIBLE);
                checkResult.setVisibility(View.VISIBLE);
                userAns.setEnabled(false);
                userAns.setVisibility(View.INVISIBLE);
                questionNumber.setVisibility(View.INVISIBLE);
                question.setText("Time is up");
                countDownClock.setVisibility(View.INVISIBLE);
                speak("Time is up");
            }
        }.start();
    }

    //stop the timer
    private void stopTimer(){
        CountDownTimer.cancel();
    }


    //update the number in the timer
    private void updateCountDownText(){
        minutes = (int) TimerLeftInMills / 1000 / 60;
        seconds = (int) TimerLeftInMills / 1000 % 60;

        timeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        countDownClock.setText(timeLeft);
    }

    //speak function of the text to speech
    private void speak(String answerStatus) {
        float pitch =  1.0f;
        float speed =  0.8f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        if(answerStatus == "Correct Answer") {
            wordToSpeak = "Correct";
        }else if(answerStatus == "Wrong Answer"){
            wordToSpeak = answerStatus + ". The correct answer is" + Integer.toString(questionAns);
        }else if(answerStatus == "You skip this question"){
            wordToSpeak = answerStatus + ". The correct answer is" + Integer.toString(questionAns);
        } else{
            wordToSpeak = answerStatus;
        }
        mTTS.speak(wordToSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }


}
