package kaappo.androidchess;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.GenericArrayType;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import kaappo.androidchess.askokaappochess.TtyUI;
import kaappo.androidchess.askokaappochess.piece;

public class ChessActivity extends AppCompatActivity {

    private static Drawable[] previousBackGroundColors = new Drawable[2];
    private static String[] previousBackGroundPositions = new String[2];


    public static String inputString = null;

    private static int playerSide;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        scaleBoardCorrectly();
        setDragListeners();
        initializeCatcher();

//        flipBoard();


        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE_KEY);
        getAndSetPlayerSide(bundle);
        setUIStuffFromBundle(bundle);

        try {
            ChessRunner.run(bundle, ChessActivity.this);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new RuntimeException(e);
        }

    }

    public void flipBoard () {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.chessboard);


        List<View> views = Skeidat.getViews(ChessActivity.this);
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(8);
        gridLayout.setRowCount(8);

        System.out.println(views.toString());

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                RelativeLayout temp = (RelativeLayout) views.get(x * 8 + y);
                System.out.println("id: " + MainActivity.getId(temp) + "x, y: " + x + " " + y);
                gridLayout.addView(temp, new GridLayout.LayoutParams(
                        GridLayout.spec(y),
                        GridLayout.spec(x)
                ));
            }
        }
    }

    public void setTurn (int turn) {
//        System.out.println("iTurn chess: " + turn);
//
//        if (turn == piece.WHITE) {
//            ((RelativeLayout) findViewById(R.id.activity_chess_player_info)).setBackground(getDrawable(R.drawable.highlight));
//            ((RelativeLayout) findViewById(R.id.activity_chess_opponent_info)).setBackground(getDrawable(R.drawable.white_blush));
//        } else {
//            ((RelativeLayout) findViewById(R.id.activity_chess_player_info)).setBackground(getDrawable(R.drawable.white_blush));
//            ((RelativeLayout) findViewById(R.id.activity_chess_opponent_info)).setBackground(getDrawable(R.drawable.highlight));
//        }
    }

    private void setUIStuffFromBundle (Bundle bundle) {
        int playerSide = bundle.getString(MainActivity.PLAYER_SIDE).equals("White") ? piece.WHITE : piece.BLACK;
        int whiteLevel = Integer.parseInt(bundle.getString(MainActivity.WHITE_LEVEL));
        int blackLevel = Integer.parseInt(bundle.getString(MainActivity.BLACK_LEVEL));

        TextView blackNameView = (TextView) findViewById(R.id.activity_chess_opponent_info_engine_name);
        TextView blackLevelView = (TextView) findViewById(R.id.activity_chess_opponent_info_engine_level);

        TextView whiteNameView = (TextView) findViewById(R.id.activity_chess_player_info_engine_name);
        TextView whiteLevelView = (TextView) findViewById(R.id.activity_chess_player_info_engine_level);


        if (playerSide == piece.WHITE) {
            blackNameView.setText(getResources().getText(R.string.activity_chess_askochess));
            blackLevelView.setText("Level" + blackLevel);

            whiteNameView.setText("Human");
            whiteLevelView.setText("Bad");

        } else {
            whiteNameView.setText(getResources().getText(R.string.activity_chess_askochess));
            whiteLevelView.setText("Level " + whiteLevel);

            blackNameView.setText("Human");
            blackLevelView.setText("Bad");
        }

    }

    private void getAndSetPlayerSide (Bundle bundle) {
        String playerSideString = bundle.getString(MainActivity.PLAYER_SIDE);

        if (playerSideString.equals(getResources().getStringArray(R.array.activity_main_white_and_black)[0])) {
            playerSide = piece.WHITE;
        } else {
            playerSide = piece.BLACK;
        }
    }

    public void onUndoButtonClick (View view) {
        TtyUI.move = "undo";
    }

    private void setDragListeners () {
        for (View imageView : Skeidat.getViews(this)) {
            imageView.setOnDragListener(new MyDragListener());

        }
    }

    private void initializeCatcher () {
        ((ConstraintLayout) findViewById(R.id.piece_catcher)).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View constraintLayout, DragEvent event) {
                int action = event.getAction();

                switch (action) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DROP:
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        break;
                }

                return true;
            }
        });
    }

    private void scaleBoardCorrectly () {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        System.out.println("dpWidth: " + dpWidth);

        int side_length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth / 8, getResources().getDisplayMetrics());


        for (View view : Skeidat.getViews(this)) {

            ViewGroup.LayoutParams layoutParams = ((RelativeLayout) view).getLayoutParams();

            layoutParams.width = side_length;
            layoutParams.height = side_length;

            System.out.println("layoutParams.height: " + layoutParams.height);

            view.setLayoutParams(layoutParams);
        }
    }

    private static String getStringFromLMoveV (Vector lastMoveVector) {
        if (lastMoveVector == null) {
            return null;
        }

        int x1 = (int)lastMoveVector.elementAt(0);
        int y1 = (int)lastMoveVector.elementAt(1);
        int x2 = (int)lastMoveVector.elementAt(2);
        int y2 = (int)lastMoveVector.elementAt(3);

        return ""+(char)(96 + x1) + y1 + ":" + (char)(96 + x2) + y2;
    }

    public static void setBoard (String board, ChessActivity context, Vector lastMoveVector) {
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
        }


        // Highlight last move
        String lastMove = getStringFromLMoveV(lastMoveVector);
        if (lastMove != null) {
            if (previousBackGroundColors != null && previousBackGroundPositions[0] != null && previousBackGroundPositions[1] != null) {
                RelativeLayout previousStartSquare = (RelativeLayout) context.findViewById(
                        context.getResources().getIdentifier(previousBackGroundPositions[0],
                        "id",
                        context.getPackageName()));

                RelativeLayout previousEndSquare = (RelativeLayout) context.findViewById(
                        context.getResources().getIdentifier(previousBackGroundPositions[1],
                                "id",
                                context.getPackageName()));

                previousStartSquare.setBackground(previousBackGroundColors[0]);
                previousEndSquare.setBackground(previousBackGroundColors[1]);
            }


            lastMove = lastMove.toUpperCase();

            String[] lastMoveArray = lastMove.split(":");

            previousBackGroundPositions = lastMoveArray.clone();

            RelativeLayout startSquare = (RelativeLayout) context.findViewById(context.getResources().getIdentifier(lastMoveArray[0], "id", context.getPackageName()));
            RelativeLayout endSquare = (RelativeLayout) context.findViewById(context.getResources().getIdentifier(lastMoveArray[1], "id", context.getPackageName()));

            previousBackGroundColors[0] = startSquare.getBackground();
            previousBackGroundColors[1] = endSquare.getBackground();

            startSquare.setBackground(context.getDrawable(R.drawable.highlight));
            endSquare.setBackground(context.getDrawable(R.drawable.highlight));

        }

    }

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
            imageView.setTag(tag);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPieceClicked(v);
                }
            });






            squareToDrawIn.addView(imageView);


        }
    }

    public void onPieceClicked (View view) {

    }

    public static int getPlayerSide() {
        return playerSide;
    }
}
