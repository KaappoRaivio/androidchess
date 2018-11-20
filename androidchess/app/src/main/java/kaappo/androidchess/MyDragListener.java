package kaappo.androidchess;

import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Vector;

import kaappo.androidchess.askokaappochess.TtyUI;
import kaappo.androidchess.askokaappochess.chessboard;
import kaappo.androidchess.askokaappochess.move;
import kaappo.androidchess.askokaappochess.piece;

import static java.lang.Thread.yield;

public class MyDragListener implements View.OnDragListener {

    public static TtyUI ttyUI;
    public static String move;

    private static int iTurn = -3;

    public final int START_PARENT_ID = 0;

    public static int getiTurn() {
        return iTurn;
    }

    public static void setiTurn(int iTurn) {
        MyDragListener.iTurn = iTurn;
    }

    public boolean onDrag(View relativeLayout, DragEvent event) {
        int action = event.getAction();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:

                break;
            case DragEvent.ACTION_DRAG_EXITED:

                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup

                View view = (View) event.getLocalState();

                String move = view.getTag().toString().substring(23, 25) + MainActivity.getId(relativeLayout).substring(23, 25);

                if (isIsMoveValid(move)) {
//                    System.out.println("Move: " + move);
                    ((RelativeLayout) view.getParent()).removeView(view);
                    ((RelativeLayout) relativeLayout).addView(view);
                    view.setVisibility(View.VISIBLE);
                } else {
//                    System.out.println("Move: " + move);
                    view.setVisibility(View.VISIBLE);
                }

                TtyUI.move = move;
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }


    private static boolean isIsMoveValid (String move) {
        if (iTurn != ChessActivity.getPlayerSide()) {
            return false;
        }

        int x1 = (int) move.charAt(0) - 64;
        int y1 = (int) move.charAt(1) - 48;
        int x2 = (int) move.charAt(2) - 64;
        int y2 = (int) move.charAt(3) - 48;


        chessboard chessboard = MyDragListener.ttyUI.getmCb();

        piece p = chessboard.blocks[x1][y1];
        boolean bValid = false;


        if (p == null) {
            return false;
        }
        if (p.iColor != MyDragListener.ttyUI.getiTurn()) {
            return false;
        }


        Vector mv = p.moveVector(chessboard);

        for (int i = 0; i < mv.size(); i++)
        {
            kaappo.androidchess.askokaappochess.move m = (move)mv.elementAt(i);

            if ((m.xtar == x2) && (m.ytar == y2)) {
                bValid = true;
            }

        }

        return bValid;
    }
}