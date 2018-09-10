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

        List<View> squares = Skeidat.getViews(this);

        List<View> pieces = Skeidat.getPieces(this);

        for (View i : squares) {
            i.setOnDragListener(new MyDragListener());
        }

        for (View i: pieces) {
            i.setOnTouchListener(new MyTouchListener());
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
