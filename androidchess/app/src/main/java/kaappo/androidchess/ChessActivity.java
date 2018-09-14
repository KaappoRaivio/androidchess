package kaappo.androidchess;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

public class ChessActivity extends AppCompatActivity {

    public static boolean isPlayersTurn;

    public void togglePlayerTurn (View view) {
        isPlayersTurn = !isPlayersTurn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        isPlayersTurn = false;

        List<View> squares = Skeidat.getViews(this);

        List<View> pieces = Skeidat.getPieces(this);

        for (View i : squares) {
            i.setOnDragListener(new MyDragListener());
        }

        for (View i: pieces) {
            i.setOnTouchListener(new MyTouchListener());
        }

    }

    public void onFlipBlackClick (View view) {
        for (View i : Skeidat.getBlackPieces(this)) {
            if (i.getScaleY() == -1f) {
                i.setScaleY(1f);
            } else {
                i.setScaleY(-1f);
            }
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
