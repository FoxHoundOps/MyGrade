package edu.utep.cs.cs4330.mygrade;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

public class GradeActivity extends AppCompatActivity {
    private TextView dateGenerated;
    private TextView letterGrade;
    private TextView weightedTotal;
    private TextView nameLabel;
    private TextView maxLabel;
    private TextView earnedLabel;

    private ListView gradesList;
    private List<Grade.Score> scores = new ArrayList<>();
    private ScoreListAdapter scoresAdapter;

    private static class ScoreListAdapter extends ArrayAdapter<Grade.Score> {
        private final List<Grade.Score> scores;

        public ScoreListAdapter(Context ctx, List<Grade.Score> scores) {
            super (ctx, android.R.layout.simple_list_item_1, scores);
            this.scores = scores;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.grade_detail_row, parent, false);
            Grade.Score score = scores.get(position);
            TextView view = row.findViewById(R.id.nameView);
            view.setText(score.name);
            view = row.findViewById(R.id.maxView);
            view.setText(Integer.toString(score.max));
            view = row.findViewById(R.id.earnedView);
            view.setText(Integer.toString(score.earned));
            return row;

        }
    }

    private final WebClient web = new WebClient(new WebClient.GradeListener() {

        @Override
        public void onGrade(String date, Grade grade) {
            if (grade != null) {
                scores.addAll(grade.scores());
                runOnUiThread(() -> {
                    scoresAdapter.notifyDataSetChanged();
                    dateGenerated.setText(Html.fromHtml(getString(R.string.date_generated_template, date)));
                    letterGrade.setText(Html.fromHtml(getString(R.string.grade_template, grade.grade)));
                    weightedTotal.setText(Html.fromHtml(getString(R.string.weighted_total_template, grade.total)));
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

        nameLabel = findViewById(R.id.name_label);
        nameLabel.setText(Html.fromHtml(getString(R.string.name_label)));
        maxLabel = findViewById(R.id.max_title);
        maxLabel.setText(Html.fromHtml(getString(R.string.max_label)));
        earnedLabel = findViewById(R.id.earned_label);
        earnedLabel.setText(Html.fromHtml(getString(R.string.earned_label)));

        gradesList = (ListView) findViewById(R.id.grades_list);
        scoresAdapter = new ScoreListAdapter(this, scores);
        gradesList.setAdapter(scoresAdapter);


        Intent i = getIntent();
        String id = i.getStringExtra("id");
        String pin = i.getStringExtra("pin");
        new Thread(() -> web.query(id, pin)).start();
    }
}
