package kaappo.androidchess.askokaappochess;


public class AndroidUI {
    int squares[][];
    chessboard chessboard;

    gamehistory ghistory;

    boolean undoEnabled;

    int turn = -1;

    public AndroidUI (chessboard cb) {
        this.squares = new int[9][9];
        this.chessboard = cb;

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