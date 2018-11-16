package kaappo.androidchess;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kaappo.androidchess.askokaappochess.TtyUI;
import kaappo.androidchess.askokaappochess.piece;

public class ChessActivity extends AppCompatActivity {


    public static String inputString = null;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE_KEY);


        for (View imageView : Skeidat.getViews(this)) {
            imageView.setOnDragListener(new MyDragListener());
        }



        try {
            ChessRunner.run(bundle, ChessActivity.this);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }

    }






    public void onEnterPress (View view) {
        EditText editText = findViewById(R.id.input);
        TtyUI.move = editText.getText().toString();
        editText.setText("");




    }

    public static void setBoard (String board, ChessActivity context) {
        context.clearBoard();
        String[] splitted = board.split("\n");
        for (int x = 0; x < splitted.length; x++) {
            String row = splitted[x];

            for (int y = 0; y < row.length(); y++) {
                char _piece = row.charAt(y);

                switch (_piece) {
                    case 'P':
                        context.drawPiece(y, x, piece.PAWN, piece.BLACK);
                        break;
                    case 'R':
                        context.drawPiece(y, x, piece.ROOK, piece.BLACK);
                        break;
                    case 'N':
                        context.drawPiece(y, x, piece.KNIGHT, piece.BLACK);
                        break;
                    case 'B':
                        context.drawPiece(y, x, piece.BISHOP, piece.BLACK);
                        break;
                    case 'K':
                        context.drawPiece(y, x, piece.KING, piece.BLACK);
                        break;
                    case 'Q':
                        context.drawPiece(y, x, piece.QUEEN, piece.BLACK);
                        break;
                    case 'p':
                        context.drawPiece(y, x, piece.PAWN, piece.WHITE);
                        break;
                    case 'r':
                        context.drawPiece(y, x, piece.ROOK, piece.WHITE);
                        break;
                    case 'n':
                        context.drawPiece(y, x, piece.KNIGHT, piece.WHITE);
                        break;
                    case 'b':
                        context.drawPiece(y, x, piece.BISHOP, piece.WHITE);
                        break;
                    case 'k':
                        context.drawPiece(y, x, piece.KING, piece.WHITE);
                        break;
                    case 'q':
                        context.drawPiece(y, x, piece.QUEEN, piece.WHITE);
                        break;
                    case '.':
                        break;
                }
            }
        }    }

    public static void setMessage (String message, ChessActivity context) {
        TextView textView = (TextView) context.findViewById(R.id.ttyui_message);
        textView.setText(message);
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

    public void drawPiece (int pos_y, int pos_x, final int _piece, final int color) {
        String y = intTochar(pos_y);
        String x = String.valueOf(8 - pos_x);


        String tag = y + x;

        RelativeLayout squareToDrawIn = null;

        for (View v : Skeidat.getViews(this)) {
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
            imageView.setOnTouchListener(new MyTouchListener());

            squareToDrawIn.addView(imageView);


        }








    }
}
