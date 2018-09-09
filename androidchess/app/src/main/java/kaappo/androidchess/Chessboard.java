package kaappo.androidchess;

class Chessboard {
    private static final Chessboard ourInstance = new Chessboard();

    static Chessboard getInstance() {
        return ourInstance;
    }

    private Chessboard() {
    }
}
