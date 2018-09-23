package kaappo.androidchess;

import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyDragListener implements View.OnDragListener {

    public static String move = null;
    public static boolean isMoveAvailable = false;

    public static String getMove () {

        if (move != null) {
            return move;
        } else {
            return null;
        }
    }

    public final int START_PARENT_ID = 0;


    private synchronized void _notify() {
        isMoveAvailable = true;
        notifyAll();
        System.out.println("notified!");
    }

    public boolean onDrag(View v, DragEvent event) {
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
                if (ChessActivity.isPlayersTurn()) {
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    RelativeLayout container = (RelativeLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);

                    move = view.getTag().toString().substring(23, 25) + MainActivity.getId((v)).substring(23, 25);
                    System.out.println(move);

                    System.out.println("notifying");
                    _notify();

                } else {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                }


                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }
}