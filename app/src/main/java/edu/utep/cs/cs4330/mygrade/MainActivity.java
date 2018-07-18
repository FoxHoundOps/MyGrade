package edu.utep.cs.cs4330.mygrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText userID;
    private EditText userPIN;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userID = findViewById(R.id.user_id_input);
        userPIN = findViewById(R.id.user_pin_input);
        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this::submitClicked);
    }

    private void submitClicked(View v) {
        String id = userID.getText().toString();
        String pin = userPIN.getText().toString();
        Intent gradeActivity = new Intent("edu.utep.cs.cs4330.mygrade.GradeActivity");
        gradeActivity.putExtra("id", id);
        gradeActivity.putExtra("pin", pin);
        startActivity(gradeActivity);
    }
}
