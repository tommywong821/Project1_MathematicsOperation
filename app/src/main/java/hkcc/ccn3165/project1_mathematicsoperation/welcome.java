package hkcc.ccn3165.project1_mathematicsoperation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class welcome extends Activity {

    private Button start;
    private Button setting;
    private TextView welcomeText;

    int defualtNumberOfQuestion;
    int defualtTimeDuration;
    String TextToShow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        start = (Button) findViewById(R.id.start);
        setting = (Button) findViewById(R.id.setting);
        welcomeText = (TextView) findViewById(R.id.welcomeText);

        start.setOnClickListener(startApp);
        setting.setOnClickListener(goSettingPage);

        Intent SettingIntent = getIntent();
        defualtNumberOfQuestion = SettingIntent.getIntExtra("userSetNumberOfQuestion", 10);
        defualtTimeDuration = SettingIntent.getIntExtra("TimeDuration", 5);

        TextToShow = String.format("You have %d question needed to be answered in %d minutes", defualtNumberOfQuestion, defualtTimeDuration);
        welcomeText.setText(TextToShow);

    }


    //start the MainActivity.class
    private Button.OnClickListener startApp = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent WCIntent = new Intent(welcome.this, MainActivity.class);
            //pass the number of question and time that are setted by the user or the program to MainActivity.class
            WCIntent.putExtra("userSetNumberOfQuestion", defualtNumberOfQuestion);
            WCIntent.putExtra("userSetTimeDuration", defualtTimeDuration);
            WCIntent.setFlags(WCIntent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(WCIntent);
            //clear this activity
            finish();
        }
    };

    //Start setting.class
    private Button.OnClickListener goSettingPage = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent WCIntent = new Intent(welcome.this, setting.class);
            //pass the number of question and time that are setted by the user or the program to MainActivity.class
            WCIntent.putExtra("userSetNumberOfQuestion", defualtNumberOfQuestion);
            WCIntent.putExtra("userSetTimeDuration", defualtTimeDuration);
            WCIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(WCIntent);
            finish();
        }
    };



}
