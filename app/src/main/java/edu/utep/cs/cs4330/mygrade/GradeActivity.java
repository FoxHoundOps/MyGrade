package edu.utep.cs.cs4330.mygrade;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GradeActivity extends AppCompatActivity {
    private TextView dateGenerated;
    private TextView letterGrade;
    private TextView weightedTotal;
    private LinearLayout gradesTable;
    private final WebClient web = new WebClient(new WebClient.GradeListener() {

        @Override
        public void onGrade(String date, Grade grade) {
            if (grade != null) {
                runOnUiThread(() -> {
                    dateGenerated.setText(Html.fromHtml(getString(R.string.date_generated_template, date)));
                    letterGrade.setText(Html.fromHtml(getString(R.string.grade_template, grade.grade)));
                    weightedTotal.setText(Html.fromHtml(getString(R.string.weighted_total_template, grade.total)));
                    for (Grade.Score s : grade.scores())
                        addScoreToTable(s);
                });
            }
        }

        @Override
        public void onError(String msg) {
            runOnUiThread(() -> {
                dateGenerated.setText(Html.fromHtml(getString(R.string.error_message)));
                letterGrade.setText("");
                weightedTotal.setText("");
            });
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        dateGenerated = findViewById(R.id.date_generated);
        letterGrade = findViewById(R.id.letter_grade);
        weightedTotal = findViewById(R.id.weighted_total);
        gradesTable = findViewById(R.id.grades_table);

        Intent i = getIntent();
        String id = i.getStringExtra("id");
        String pin = i.getStringExtra("pin");
        new Thread(() -> web.query(id, pin)).start();
    }

    private void addScoreToTable(Grade.Score score) {
        TextView temp = new TextView(this);
        temp.setText(Html.fromHtml(getString(R.string.score_row_template, score.name, score.earned, score.max)));
        gradesTable.addView(temp);
    }
}
