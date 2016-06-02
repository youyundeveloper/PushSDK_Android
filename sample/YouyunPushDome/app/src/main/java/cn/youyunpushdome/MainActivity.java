package cn.youyunpushdome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.helloworld);
        Intent intent = getIntent();
        String msg = "";
        if(intent != null)
            msg = intent.getStringExtra("pushMsg");
        textView.setText(msg);
    }

    public void handleEnter(View v){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void handleExit(View v){
        System.exit(0);
    }

}
