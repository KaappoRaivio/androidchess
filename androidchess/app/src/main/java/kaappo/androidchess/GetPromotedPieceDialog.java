package kaappo.androidchess;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import kaappo.androidchess.askokaappochess.Piece;

public class GetPromotedPieceDialog extends Dialog implements View.OnClickListener {

    public static int _piece = -1;



    public Activity c;
    public Dialog d;
    public ImageView queen, rook, knight, bishop;

    int color;

    public GetPromotedPieceDialog(Activity a, int color) {
        super(a);

        this.c = a;

        this.color = color;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        int resId = -1;

        if (this.color == Piece.WHITE) {
            resId = R.layout.fragment_get_promoted_piece_dialog_white;
        } else if (this.color == Piece.BLACK) {
            resId = R.layout.fragment_get_promoted_piece_dialog_black;
        }

        if (resId == -1) {
            throw new RuntimeException("Invalid color " + this.color + "!");
        }

        setContentView(resId);

        queen = (ImageView) findViewById(R.id.frag_queen);
        rook = (ImageView) findViewById(R.id.frag_rook);
        knight = (ImageView) findViewById(R.id.frag_knight);
        bishop = (ImageView) findViewById(R.id.frag_bishop);

        queen.setOnClickListener(this);
        rook.setOnClickListener(this);
        knight.setOnClickListener(this);
        bishop.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int selectedPiece = -1;

        switch (v.getId()) {
            case R.id.frag_queen:
                selectedPiece = Piece.QUEEN;
                break;
            case R.id.frag_rook:
                selectedPiece = Piece.ROOK;
                break;
            case R.id.frag_knight:
                selectedPiece = Piece.KNIGHT;
                break;
            case R.id.frag_bishop:
                selectedPiece = Piece.BISHOP;
                break;
            default:
                break;
        }

        _piece = selectedPiece;

        System.out.println("GetPromotedPiece.onClick: dismissing now!");

        dismiss();
    }
}

