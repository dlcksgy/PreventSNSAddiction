package kr.ac.kau.sw.a2016125063.servicepr;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView usageText;

    private long accumulatedTime;

    private long min;

    private long sec;

    private Button serviceButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        min = accumulatedTime/60;
        sec = accumulatedTime%60;
        usageText = (TextView) findViewById(R.id.usageText);
        usageText.setText(getString(R.string.not_running));

        serviceButton = (Button) findViewById(R.id.serviceButton);
        serviceButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),PracticeService2.class);
                intent.putExtra("accumulatedTime",accumulatedTime);
                startService(intent);
            }
        });

    }
}
