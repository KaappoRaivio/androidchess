package kaappo.androidchess;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import kaappo.androidchess.askokaappochess.TtyUI;
import kaappo.androidchess.askokaappochess.Piece;

public class ChessActivity extends AppCompatActivity {

    private static Drawable[] previousBackGroundColors = new Drawable[2];
    private static String[] previousBackGroundPositions = new String[2];


    public static String inputString = null;

    private static int playerSide;

    private List<View> doNotGarbageCollect;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        doNotGarbageCollect = Skeidat.getViews(ChessActivity.this);

        scaleBoardCorrectly();
        setDragListeners();
        initializeCatcher();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movehistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new RecyclerViewAdapter());


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
        System.out.println("iTurn chess: " + turn);

        if (turn == Piece.WHITE) {
            ((CardView) findViewById(R.id.activity_chess_player_info_card)).setBackground(getDrawable(R.drawable.turn));
            ((CardView) findViewById(R.id.activity_chess_opponent_info_card)).setBackground(getDrawable(R.drawable.white_blush));
        } else {
            ((CardView) findViewById(R.id.activity_chess_player_info_card)).setBackground(getDrawable(R.drawable.white_blush));
            ((CardView) findViewById(R.id.activity_chess_opponent_info_card)).setBackground(getDrawable(R.drawable.turn));
        }
    }

    private void setUIStuffFromBundle (Bundle bundle) {
        int playerSide = bundle.getString(MainActivity.PLAYER_SIDE).equals("White") ? Piece.WHITE : Piece.BLACK;
        int whiteLevel = Integer.parseInt(bundle.getString(MainActivity.WHITE_LEVEL));
        int blackLevel = Integer.parseInt(bundle.getString(MainActivity.BLACK_LEVEL));

        TextView blackNameView = (TextView) findViewById(R.id.activity_chess_opponent_info_engine_name);
        TextView blackLevelView = (TextView) findViewById(R.id.activity_chess_opponent_info_engine_level);

        TextView whiteNameView = (TextView) findViewById(R.id.activity_chess_player_info_engine_name);
        TextView whiteLevelView = (TextView) findViewById(R.id.activity_chess_player_info_engine_level);


        if (playerSide == Piece.WHITE) {
            blackNameView.setText(getResources().getText(R.string.activity_chess_askochess));
            blackLevelView.setText("Level " + blackLevel);

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
            playerSide = Piece.WHITE;
        } else {
            playerSide = Piece.BLACK;
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

        float length = dpWidth / 8.25f;

        int side_length = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, length, getResources().getDisplayMetrics());


        for (View view : Skeidat.getViews(this)) {

            ViewGroup.LayoutParams layoutParams = ((RelativeLayout) view).getLayoutParams();

            layoutParams.width = side_length;
            layoutParams.height = side_length;

            System.out.println("layoutParams.height: " + layoutParams.height);

            view.setLayoutParams(layoutParams);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movehistory);
        ViewGroup.LayoutParams recyclerParams = recyclerView.getLayoutParams();
        recyclerParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, length * 7.95f, getResources().getDisplayMetrics());
        recyclerView.setLayoutParams(recyclerParams);


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

    public void setBoard (String board, Vector lastMoveVector) {
        clearBoard();

        String[] splitted = board.split("\n");
        for (int x = 0; x < splitted.length; x++) {
            String row = splitted[x];

            for (int y = 0; y < row.length(); y++) {
                char piece = row.charAt(y);
                int pieceType = getPieceTypeFromChar(piece);
                int pieceColor = getPieceColorFromChar(piece);

                //Check for empty square
                if (pieceType == -1) {
                    continue;
                }

                if (pieceType != -2 && pieceColor != -1) {
                    drawPiece(x, y, pieceType, pieceColor);
                } else {
                    throw new RuntimeException("ChessActivity.setBoard: invalid board:\n" + board);
                }
            }
        }

        highlightLastMove(lastMoveVector);

        String lastMove = getStringFromLMoveV(lastMoveVector);

        // set movehistory
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.movehistory);
        RecyclerViewAdapter recyclerViewAdapter = (RecyclerViewAdapter) recyclerView.getAdapter();
        recyclerViewAdapter.addMove(lastMove);
        recyclerViewAdapter.notifyDataSetChanged();

    }

    private static int getPieceTypeFromChar (char piece) {
        piece = Character.toUpperCase(piece);

        switch (piece) {
            case 'P':
                return Piece.PAWN;
            case 'R':
                return Piece.ROOK;
            case 'N':
                return Piece.KNIGHT;
            case 'B':
                return Piece.BISHOP;
            case 'K':
                return Piece.KING;
            case 'Q':
                return Piece.QUEEN;
            case '.':
                return -1;
            default:
                return -2;
        }
    }

    private static int getPieceColorFromChar (char piece) {
        return Character.isUpperCase(piece) ? Piece.BLACK : Piece.WHITE;
    }



    public void highlightLastMove (Vector lastMoveVector) {

        // Highlight last move
        String lastMove = getStringFromLMoveV(lastMoveVector);
        if (lastMove != null) {
            if (previousBackGroundColors != null && previousBackGroundPositions[0] != null && previousBackGroundPositions[1] != null) {
                RelativeLayout previousStartSquare = (RelativeLayout) findViewById(
                        getResources().getIdentifier(previousBackGroundPositions[0],
                                "id",
                                getPackageName()));

                RelativeLayout previousEndSquare = (RelativeLayout) findViewById(
                        getResources().getIdentifier(previousBackGroundPositions[1],
                                "id",
                                getPackageName()));

                previousStartSquare.setBackground(previousBackGroundColors[0]);
                previousEndSquare.setBackground(previousBackGroundColors[1]);
            }


            lastMove = lastMove.toUpperCase();

            String[] lastMoveArray = lastMove.split(":");

            previousBackGroundPositions = lastMoveArray.clone();

            RelativeLayout startSquare = (RelativeLayout) findViewById(getResources().getIdentifier(lastMoveArray[0], "id", getPackageName()));
            RelativeLayout endSquare = (RelativeLayout) findViewById(getResources().getIdentifier(lastMoveArray[1], "id", getPackageName()));

            previousBackGroundColors[0] = startSquare.getBackground();
            previousBackGroundColors[1] = endSquare.getBackground();

            startSquare.setBackground(getDrawable(R.drawable.highlight));
            endSquare.setBackground(getDrawable(R.drawable.highlight));
        }
    }

    public void setMessage (String message) {
        TextView textView = (TextView) findViewById(R.id.ttyui_message);
        textView.setText(message);
    }


    @NonNull
    private static String intTochar (int y) {
        return String.valueOf((char) (65 + y));
    }

    private void clearBoard () {
        for (View view : Skeidat.getViews(this)) {
            ((RelativeLayout) view).removeAllViews();
        }
    }

    public void drawPiece (int pos_x, int pos_y, final int piece, final int color) {
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

            if (color == Piece.WHITE) {
                switch (piece) {
                    case Piece.PAWN:
                        resId = R.drawable.white_pawn;
                        break;
                    case Piece.ROOK:
                        resId = R.drawable.white_rook;
                        break;
                    case Piece.KNIGHT:
                        resId = R.drawable.white_knight;
                        break;
                    case Piece.BISHOP:
                        resId = R.drawable.white_bishop;
                        break;
                    case Piece.QUEEN:
                        resId = R.drawable.white_queen;
                        break;
                    case Piece.KING:
                        resId = R.drawable.white_king;
                        break;
                }
            } else if (color == Piece.BLACK) {
                switch (piece) {
                    case Piece.PAWN:
                        resId = R.drawable.black_pawn;
                        break;
                    case Piece.ROOK:
                        resId = R.drawable.black_rook;
                        break;
                    case Piece.KNIGHT:
                        resId = R.drawable.black_knight;
                        break;
                    case Piece.BISHOP:
                        resId = R.drawable.black_bishop;
                        break;
                    case Piece.QUEEN:
                        resId = R.drawable.black_queen;
                        break;
                    case Piece.KING:
                        resId = R.drawable.black_king;
                        break;
                }
            }

            ImageView imageView = new ImageView(this);
            imageView.setImageDrawable(getDrawable(resId));
            imageView.setOnTouchListener(new MyTouchListener());
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPieceClicked(v);
                }
            });
            imageView.setTag(tag);

            squareToDrawIn.addView(imageView);
        }
    }

    public void onPieceClicked (View view) {

    }

    public static int getPlayerSide() {
        return playerSide;
    }

    public void showExceptionMessage(final Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Exception occured:")
                .setMessage(e.getMessage())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        assert true;
                    }
                })
                .setNeutralButton(android.R.string.copy, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", e.getMessage());
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(getApplicationContext(), "Copied to clipboard!", Toast.LENGTH_LONG).show();

                    }
                })
        .setIcon(android.R.drawable.ic_dialog_alert);
        if (!((Activity) this).isFinishing()) {
            builder.show();
        } else {
            System.out.println("Exception occured and the activity crashed. Message was:" + e.getMessage());
        }
    }
}
