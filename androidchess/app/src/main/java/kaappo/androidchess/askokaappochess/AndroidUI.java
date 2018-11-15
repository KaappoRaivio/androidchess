package kaappo.androidchess.askokaappochess;


import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kaappo.androidchess.ChessActivity;
import kaappo.androidchess.MyDragListener;
import kaappo.androidchess.R;

public class AndroidUI {
    private int squares[][];
    chessboard chessboard;

    gamehistory ghistory;

    boolean undoEnabled;

    public static int turn = -1;

    private ChessActivity context;

    public AndroidUI (chessboard cb, ChessActivity context) {
        this.squares = new int[8][8];
        this.chessboard = cb;

        this.context = context;

        System.out.println("Android ui created");
    }

    public String getMove () {

        boolean ready = false;
        String move = null;
        System.out.println("Waiting");
//        while (!MyDragListener.isMoveAvailable) {
//            try {
//                wait();
//            } catch (InterruptedException e) {
//                System.out.println("thing works");
//                break;
//            }
//        }
        while (!MyDragListener.isMoveAvailable) {}
        System.out.println("Stopped waiting");

        move = MyDragListener.getMove();

        System.out.println("Move in AndroidUI: " + move);

        if (move != null) {
            System.out.println("Found a move!");

//            MyDragListener.move = null;


        } else {
            throw new RuntimeException("getMOve not wrking!");
        }
        return move;
    }




    public void doMove () {
        // todo
    }


    public void displayMsgDialog (String msg) {
        System.out.println("MSGDIALOG: " + msg);
    }

    public int getUrgency () {
        //todo
        return 0;
    }
    
    public void setMessage (final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TextView textView = context.findViewById(R.id.info);
                textView.setText(message);
            }
        };
        context.runOnUiThread(runnable);
    }

    private int counter = 0;

    public void setTurn (int i) {
        System.out.println("changing turn to: " + i);
        turn =  (i != 0) ? 1 : 0;
    }



    public void updateData (chessboard cb) {
        this.chessboard = cb;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                this.squares[x][y] = cb.piecevalue(x, y);
            }
        }
        System.out.println("data updated");


    }
    public void updateBoard () throws Exception {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<ImageView> unusedViews = new ArrayList<>();

                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        if (context.getPieceByPosition(x, y) != null) {
                            unusedViews.add(context.getPieceByPosition(x, y));
                            context.getSquareByPosition(x, y).removeView(context.getPieceByPosition(x, y));
                        }
                    }
                }

                System.out.println("Trying to updateBoard");
                for (int y = 0; y < 8; y++) {
                    for (int x = 0; x < 8; x++) {
                        int square = squares[x][y];

                        ImageView toBePlaced = null;
                        if (square == -1 || square == -2)
                            continue;
                        else if (square < 100) {

                            switch (square) {
                                case piece.PAWN:
                                    toBePlaced = searchPiece(unusedViews, "white_pawn");
                                    break;
                                case piece.BISHOP:
                                    toBePlaced = searchPiece(unusedViews, "white_bishop");
                                    break;
                                case piece.KNIGHT:
                                    toBePlaced = searchPiece(unusedViews, "white_knight");
                                    break;
                                case piece.ROOK:
                                    toBePlaced = searchPiece(unusedViews, "white_rook");
                                    break;
                                case piece.QUEEN:
                                    toBePlaced = searchPiece(unusedViews, "white_queen");
                                    break;
                                case piece.KING:
                                    toBePlaced = searchPiece(unusedViews, "white_king");
                                    break;

                            }
                        } else {
                            switch (square - 100) {
                                case piece.PAWN:
                                    toBePlaced = searchPiece(unusedViews, "black_pawn");
                                    break;
                                case piece.BISHOP:
                                    toBePlaced = searchPiece(unusedViews, "black_bishop");
                                    break;
                                case piece.KNIGHT:
                                    toBePlaced = searchPiece(unusedViews, "black_knight");
                                    break;
                                case piece.ROOK:
                                    toBePlaced = searchPiece(unusedViews, "black_rook");
                                    break;
                                case piece.QUEEN:
                                    toBePlaced = searchPiece(unusedViews, "black_queen");
                                    break;
                                case piece.KING:
                                    toBePlaced = searchPiece(unusedViews, "black_king");
                                    break;

                            }
                        }
                        if (toBePlaced == null) {
                            System.out.println("Continuing" + x + " " + y);
                            continue;
                        }

                        context.getSquareByPosition(x, y).addView(toBePlaced);
                        unusedViews.remove(toBePlaced);

                    }


                }
                System.out.println("success!");
            }
        };
        context.runOnUiThread(runnable);


    }

    public static ImageView searchPiece (List<ImageView> pieces, String string) {
        for (ImageView imageView : pieces) {
            if (((String) imageView.getTag()).equals(string)) {
                return imageView;
            }
        }
        return null;
    }

}