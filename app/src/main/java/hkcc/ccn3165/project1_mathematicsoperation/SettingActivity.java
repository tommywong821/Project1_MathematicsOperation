package hkcc.ccn3165.project1_mathematicsoperation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity {

    private EditText numberOfQuestion;
    private EditText timeDuration;
    private Button back;
    private Button reset;
    private Button submit;

    private int numberOfQuestionOnApp;
    private int timeDurationOnApp;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        numberOfQuestion = (EditText)findViewById(R.id.numberOfQuestion);
        timeDuration = (EditText)findViewById(R.id.timeDuration);
        back = (Button)findViewById(R.id.backToMainMenu);
        reset = (Button)findViewById(R.id.reset);
        submit = (Button)findViewById(R.id.submit);

        back.setOnClickListener(backToMM);
        reset.setOnClickListener(resetValue);
        submit.setOnClickListener(submitUserInput);

        Intent SettingIntent = getIntent();
        numberOfQuestionOnApp = SettingIntent.getIntExtra("userSetNumberOfQuestion",10);
        timeDurationOnApp = SettingIntent.getIntExtra("userSetTimeDuration", 5);

        watcher(numberOfQuestion, timeDuration,  submit);
    }

    //Star the Welcome.class with the number of question and time that are setted by the user
    private Button.OnClickListener backToMM = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent settingIntent = new Intent(SettingActivity.this, WelcomeActivity.class);
            settingIntent.putExtra("userSetNumberOfQuestion",numberOfQuestionOnApp);
            settingIntent.putExtra("TimeDuration",timeDurationOnApp);
            startActivity(settingIntent);            //handle the question with num1 + num2

            finish();
        }
    };

    //Reset the number of question and time to default value
    private Button.OnClickListener resetValue = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            numberOfQuestion.setText(Integer.toString(10));
            timeDuration.setText(Integer.toString(5));
        }
    };

    //Change the number of question and time of this program
    private Button.OnClickListener submitUserInput = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            numberOfQuestionOnApp = Integer.parseInt(numberOfQuestion.getText().toString());
            timeDurationOnApp = Integer.parseInt(timeDuration.getText().toString());
            Intent settingIntent = new Intent(SettingActivity.this, WelcomeActivity.class);
            settingIntent.putExtra("userSetNumberOfQuestion",numberOfQuestionOnApp);
            settingIntent.putExtra("TimeDuration",timeDurationOnApp);
            startActivity(settingIntent);
            finish();
        }
    };

    //check the value inserted by the user is valid or not
    void watcher(final EditText message_body, final EditText message_body2, final Button Send)
    {
        if(message_body.length() == 0 || message_body2.length() == 0)
            Send.setEnabled(false);//disable at app start

        TextWatcher checkUserInput = new TextWatcher(){
            public void afterTextChanged(Editable s)
            {
                if(message_body.length() == 0 || message_body2.length() == 0) {
                    Send.setEnabled(false); //disable send button if no text entered
                }
                else {
                    if(Integer.parseInt(message_body.getText().toString()) >= 10 && Integer.parseInt(message_body2.getText().toString()) >= 5) {
                        Send.setEnabled(true);
                    }else{
                        Send.setEnabled(false);  //otherwise enable
                    }
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count){
            }
        };

        message_body.addTextChangedListener(checkUserInput);
        message_body2.addTextChangedListener(checkUserInput);

    }
}
