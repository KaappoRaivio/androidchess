package kaappo.androidchess;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

public final class MyTouchListener implements View.OnTouchListener {

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.setTag(MainActivity.getId((View) view.getParent()));

                ClipData data = ClipData.newPlainText("", "");

                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);

                return true;
            case MotionEvent.ACTION_UP:
                view.performClick();
                return false;
            default:
                return false;
        }



    }

}