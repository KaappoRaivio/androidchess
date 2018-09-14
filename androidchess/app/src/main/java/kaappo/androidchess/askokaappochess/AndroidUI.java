package kaappo.androidchess.askokaappochess;


import android.app.Activity;
import android.content.Context;

public class AndroidUI {
    int squares[][];
    chessboard chessboard;

    gamehistory ghistory;

    boolean undoEnabled;

    int turn = -1;

    private Activity context;

    public AndroidUI (chessboard cb, Activity context) {
        this.squares = new int[9][9];
        this.chessboard = cb;

        this.context = context;

        System.out.println("Android ui created");
    }

    public String getMove () {
        // todo
        return "";
    }

    public void doMove () {
        // todo
    }


    public void displayMsgDialog (String msg) {
        //todo
    }

    public int getUrgency () {
        //todo
        return 0;
    }

    public void updateData (chessboard cb) {

    }

}