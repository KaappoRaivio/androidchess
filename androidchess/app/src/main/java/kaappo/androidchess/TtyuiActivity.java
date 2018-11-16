package kaappo.androidchess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class TtyuiActivity extends AppCompatActivity {


    public static String inputString = null;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttyui);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        ((EditText) findViewById(R.id.input)).setInputType(InputType.TYPE_NULL);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE_KEY);



        ((TextView) findViewById(R.id.output)).setTypeface(Typeface.MONOSPACE);

        try {
            ChessRunner.run(bundle, TtyuiActivity.this);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }

    }



    public void onEnterPress (View view) {
        EditText editText = findViewById(R.id.input);
        inputString = editText.getText().toString();
        editText.setText("");




    }

    public static void setBoard (String board, TtyuiActivity context) {
//        System.out.println(board);
        TextView textView = (TextView) context.findViewById(R.id.output);
        textView.setText(board);
    }

    public static void setMessage (String message, TtyuiActivity context) {
        TextView textView = (TextView) context.findViewById(R.id.ttyui_message);
        textView.setText(message);
    }

}
