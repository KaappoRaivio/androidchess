package kaappo.androidchess;

import android.app.ActivityManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import kaappo.androidchess.askokaappochess.piece;

public class NewChessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess_new);

        drawPiece(3, 3, piece.PAWN, piece.WHITE);
        drawPiece(4, 4, piece.BISHOP, piece.BLACK);




    }

    public void updateBoard (String chessboard) {
        String[] splitted = chessboard.split("\n");
        for (int y = 0; y < splitted.length; y++) {
            String row = splitted[y];

            for (int x = 0; x < row.length(); x++) {
                char _piece = row.charAt(x);

                switch (_piece) {
                    case 'P':
                        drawPiece(x, y, piece.PAWN, piece.BLACK);
                        break;
                    case 'R':
                        drawPiece(x, y, piece.PAWN, piece.BLACK);
                        break;
                    case 'N':
                        drawPiece(x, y, piece.KNIGHT, piece.BLACK);
                        break;
                    case 'B':
                        drawPiece(x, y, piece.BISHOP, piece.BLACK);
                        break;
                    case 'K':
                        drawPiece(x, y, piece.KING, piece.BLACK);
                        break;
                    case 'Q':
                        drawPiece(x, y, piece.QUEEN, piece.BLACK);
                        break;
                    case 'p':
                        drawPiece(x, y, piece.PAWN, piece.WHITE);
                        break;
                    case 'r':
                        drawPiece(x, y, piece.PAWN, piece.WHITE);
                        break;
                    case 'n':
                        drawPiece(x, y, piece.KNIGHT, piece.WHITE);
                        break;
                    case 'b':
                        drawPiece(x, y, piece.BISHOP, piece.WHITE);
                        break;
                    case 'k':
                        drawPiece(x, y, piece.KING, piece.WHITE);
                        break;
                    case 'q':
                        drawPiece(x, y, piece.QUEEN, piece.WHITE);
                        break;
                    case '.':
                        break;







                }
            }
        }
    }

    @NonNull
    private static String intTochar (int y) {
        return String.valueOf((char) (65 + y));
    }

    public void clearBoard () {
        for (View view : Skeidat.getViews(this)) {
            ((RelativeLayout) view).removeAllViews();
        }
    }

    public void drawPiece (int pos_x, int pos_y, final int _piece, final int color) {
        String y = intTochar(pos_y);
        String x = String.valueOf(pos_x);


        String tag = y + x;
        System.out.println(tag);

        RelativeLayout squareToDrawIn = null;

        for (View v : Skeidat.getViews(this)) {
            System.out.println((String) v.getTag());
            if (((String) v.getTag()).equals(tag)) {

                squareToDrawIn = (RelativeLayout) v;
                break;
            }
        }

        if (squareToDrawIn == null) {

            throw new RuntimeException("not found");
        } else {
            int resId = -1;

            if (color == piece.WHITE) {
                switch (_piece) {
                    case piece.PAWN:
                        resId = R.drawable.white_pawn;
                        break;
                    case piece.ROOK:
                        resId = R.drawable.white_rook;
                        break;
                    case piece.KNIGHT:
                        resId = R.drawable.white_knight;
                        break;
                    case piece.BISHOP:
                        resId = R.drawable.white_bishop;
                        break;
                    case piece.QUEEN:
                        resId = R.drawable.white_queen;
                        break;
                    case piece.KING:
                        resId = R.drawable.white_king;
                        break;
                }
            } else if (color == piece.BLACK) {
                switch (_piece) {
                    case piece.PAWN:
                        resId = R.drawable.black_pawn;
                        break;
                    case piece.ROOK:
                        resId = R.drawable.black_rook;
                        break;
                    case piece.KNIGHT:
                        resId = R.drawable.black_knight;
                        break;
                    case piece.BISHOP:
                        resId = R.drawable.black_bishop;
                        break;
                    case piece.QUEEN:
                        resId = R.drawable.black_queen;
                        break;
                    case piece.KING:
                        resId = R.drawable.black_king;
                        break;
                }
            }

            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(getResources().getDrawable(resId));

            squareToDrawIn.addView(imageView);


        }








    }
}
