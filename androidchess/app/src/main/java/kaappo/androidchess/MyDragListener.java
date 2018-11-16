package kaappo.androidchess;

import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import kaappo.androidchess.askokaappochess.TtyUI;

import static java.lang.Thread.yield;

public class MyDragListener implements View.OnDragListener {

    public static TtyUI ttyUI;
    public static String move;
    public static boolean isMoveValid;

    public final int START_PARENT_ID = 0;

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
                System.out.println("Move: " + move);

                TtyUI.move = move;

                view.setVisibility(View.VISIBLE);
//                System.out.println("valid asd" + TtyUI.isMoveValid);
//                System.out.println("valid string" + TtyuiActivity.inputString);
//                while (this.move != null) {
//                    try {Thread.sleep(10);} catch (Exception ignored) {}
//                }




//                if (isMoveValid) {
//                    System.out.println("Move " + move + " is valid!");
//                    ViewGroup oldOwner = (ViewGroup) view.getParent();
//                    oldOwner.removeView(view);
//
//                    RelativeLayout newOwner = (RelativeLayout) relativeLayout;
//                    newOwner.addView(view);
//
//                    view.setVisibility(View.VISIBLE);
//                    isMoveValid = false;
//
//
//                } else {
//                System.out.println("Move " + move.toLowerCase() + " is not valid!");
//                isMoveValid = false;

//                }




















                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }
}