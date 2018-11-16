package kaappo.androidchess;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import kaappo.androidchess.askokaappochess.AndroidUI;

public class ChessActivity extends AppCompatActivity {

    public int whiteLevel = -1;
    public int blackLevel = -1;

    public void setTextField (String message) {
        TextView textView = (TextView) findViewById(R.id.info);
        textView.setText(message);
    }

//    public void runOnUIThread(Runnable runnable) {
//        runnable.run();
//    }

    public static String getId(View view) {
        if (view.getId() == 0xffffffff) return "no-id";
        else return view.getResources().getResourceName(view.getId());
    }

    public int getViewIDByString (String name) {
        Resources res = getResources();
        return res.getIdentifier(name, "id", getPackageName());

    }

    public static boolean isPlayersTurn () {
        if (AndroidUI.turn != 1) {
            return true;
        } else {
            return false;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE_KEY);




//        List<View> squares = Skeidat.getViews(this);
//
//        List<View> pieces = Skeidat.getPieces(this);
//
//        for (View i : squares) {
//            i.setOnDragListener(new MyDragListener());
//        }
//
//        for (View i : pieces) {
//            i.setOnTouchListener(new MyTouchListener());
//        }

//        try {
//            ChessRunner.run(bundle, ChessActivity.this);
//            } catch (Exception e) {
//                System.out.println(e.toString());
//                throw new RuntimeException(e);
//        }
    }

//    public void onFlipBlackClick (View view) {
//        for (View i : Skeidat.getBlackPieces(this)) {
//            if (i.getScaleY() == -1f) {
//                i.setScaleY(1f);
//            } else {
//                i.setScaleY(-1f);
//            }
//        }
//
//
//    }

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

    public RelativeLayout getSquareByPosition (int pos_x, int pos_y) {
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
                throw new RuntimeException(new Exception("invalid pos_x of " + pos_x));
        }

        try {
            return (RelativeLayout) findViewById(getViewIDByString(letter + String.valueOf(pos_y + 1)));
        } catch (Exception e) {
            return null;
        }

    }
    public ImageView getPieceByPosition (int pos_x, int pos_y) {
        try{
            return (ImageView) getSquareByPosition(pos_x, pos_y).getChildAt(0);
        } catch (Exception e) {
            return null;
        }

    }

    public void changeParentView (RelativeLayout oldParent, View view, RelativeLayout newParent) {
        oldParent.removeView(view);
        newParent.addView(view);
    }





}
