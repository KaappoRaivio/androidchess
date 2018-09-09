package kaappo.androidchess;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

public class ChessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        findViewById(R.id.pawn).setOnTouchListener(new MyTouchListener());

        List<View> views = Arrays.asList(

            findViewById(R.id.A8),
            findViewById(R.id.A6),
            findViewById(R.id.B6),
            findViewById(R.id.C6),
            findViewById(R.id.D6),
            findViewById(R.id.E6)

        );

        for (View i : views) {
            i.setOnDragListener(new MyDragListener());
        }
    }

    public static class ChessSquare {
        private LinearLayout container;

        public ChessSquare (String position, ChessActivity context) {
            int resID = context.getResources().getIdentifier(position, "layout", context.getPackageName());
            container = context.findViewById(resID);
        }

        public LinearLayout getContainer() {
            return container;
        }
    }

}
