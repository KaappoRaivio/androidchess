package kaappo.androidchess;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Arrays;
import java.util.List;

public class ChessActivity extends AppCompatActivity {

    public static String getId(View view) {
        if (view.getId() == 0xffffffff) return "no-id";
        else return view.getResources().getResourceName(view.getId());
    }

    public int getViewIDByString (String name) {
        Resources res = getResources();
        return res.getIdentifier(name, "id", getPackageName());

    }

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

    public String getMove () {
        String move;
        while (true) {
            move = MyDragListener.getMove();
            if (move != null) {
                return move;
            }
        }
    }

    public RelativeLayout getSquareByPosition (int pos_x, int pos_y) throws Exception {
        char letter;

        switch (pos_x) {
            case 0:
                letter = 'A';
                break;
            case 1:
                letter = 'B';
                break;
            case 2:
                letter = 'C';
                break;
            case 3:
                letter = 'D';
                break;
            case 4:
                letter = 'E';
                break;
            case 5:
                letter = 'F';
                break;
            case 6:
                letter = 'G';
                break;
            case 7:
                letter = 'H';
                break;
            default:
                throw new Exception("invalid pos_x of " + pos_x);
        }

        try {
            return (RelativeLayout) findViewById(getViewIDByString(letter + String.valueOf(pos_y + 1)));
        } catch (Exception e) {
            return null;
        }

    }
    public ImageView getPieceByPosition (int pos_x, int pos_y) throws Exception {
        try{
            return (ImageView) getSquareByPosition(pos_x, pos_y).getChildAt(0);
        } catch (Exception e) {
            return null;
        }

    }

    public void changeParentView (RelativeLayout oldParent, View view, RelativeLayout newParent) {
        oldParent.removeView(view);
        newParent.addView(view);

        return;
    }





}
