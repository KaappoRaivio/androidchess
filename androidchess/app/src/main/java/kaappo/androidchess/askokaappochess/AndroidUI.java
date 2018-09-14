package kaappo.androidchess.askokaappochess;


import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import kaappo.androidchess.ChessActivity;

public class AndroidUI {
    int squares[][];
    chessboard chessboard;

    gamehistory ghistory;

    boolean undoEnabled;

    int turn = -1;

    private ChessActivity context;

    public AndroidUI (chessboard cb, ChessActivity context) {
        this.squares = new int[8][8];
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
        this.chessboard = cb;

        for (int x = 1; x <= 8; x++) {
            for (int y = 1; y <= 8; y++) {
                this.squares[x][y] = cb.piecevalue(x, y);
            }
        }


    }
    public void updateBoard () throws Exception{
        List<Integer> alreadyMovedPieces = new ArrayList<>();
        List<ImageView> unusedViews = new ArrayList<>();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (context.getPieceByPosition(x, y) != null) {
                    unusedViews.add(context.getPieceByPosition(x, y));
                }
            }
        }

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int square = this.squares[x][y];

                ImageView toBePlaced;
                if (square == -1)
                    continue;
                else if (square < 100) {

                    switch (square - 100) {
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
                        default:
                            throw new Exception();
                    }
                } else {
                    switch (square) {
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
                        default:
                            throw new Exception();
                }
            }
            context.getSquareByPosition(x, y).addView(toBePlaced);
        }



    }

    public static ImageView searchPiece (List<ImageView> pieces, String string) {
        for (ImageView imageView : pieces) {
            if (((String) imageView.getTag()).equals(string)) {
                return imageView;
            }
        }
    }

}