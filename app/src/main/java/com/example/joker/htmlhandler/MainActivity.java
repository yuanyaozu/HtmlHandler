package com.example.joker.htmlhandler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import static com.example.joker.htmlhandler.HtmlTagHandler.TAG_CUSTOM_FONT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView txt1 = findViewById(R.id.txt1);
        TextView txt2 = findViewById(R.id.txt2);
        TextView txt3 = findViewById(R.id.txt3);

        String html2 = "<font size=12>hello world!</font>";
        String html3 = "<font size=24><font size=12>hello</font> world!</font>";

        html2 = html2.replaceAll("font", TAG_CUSTOM_FONT);
        html3 = html3.replaceAll("font", TAG_CUSTOM_FONT);

        txt2.setText(Html.fromHtml(html2, null, new HtmlTagHandler()));
        txt3.setText(Html.fromHtml(html3, null, new HtmlTagHandler()));

    }
}
