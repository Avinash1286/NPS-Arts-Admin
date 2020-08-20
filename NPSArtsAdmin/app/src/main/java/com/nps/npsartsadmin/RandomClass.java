package com.nps.npsartsadmin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomClass extends AppCompatActivity {

    TextView showRV;
    Button generate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        showRV=(TextView)findViewById(R.id.showRandomNumber);
        generate=(Button)findViewById(R.id.generateNumber);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val=10;
                int getVal= ThreadLocalRandom.current().nextInt(1,val);
                String strval=String.valueOf(getVal);
                showRV.setText(strval);

            }
        });
    }
}
