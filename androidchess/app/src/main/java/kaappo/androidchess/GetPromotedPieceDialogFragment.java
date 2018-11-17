package kaappo.androidchess;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class GetPromotedPieceDialogFragment extends Dialog implements View.OnClickListener {



    public Activity c;
    public Dialog d;
    public ImageView queen, rook, knight, bishop;

    public GetPromotedPieceDialogFragment(Activity a) {
        super(a);

        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_get_promoted_piece_dialog);

        queen = (ImageView) findViewById(R.id.frag_white_queen);
        rook = (ImageView) findViewById(R.id.frag_white_rook);
        knight = (ImageView) findViewById(R.id.frag_white_knight);
        bishop = (ImageView) findViewById(R.id.frag_white_bishop);

        queen.setOnClickListener(this);
        rook.setOnClickListener(this);
        knight.setOnClickListener(this);
        bishop.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frag_white_queen:
                System.out.println("Selected queen!");
                break;
            case R.id.frag_white_rook:
                System.out.println("Selected rook!");
                break;
            case R.id.frag_white_knight:
                System.out.println("Selected knight!");
                break;
            case R.id.frag_white_bishop:
                System.out.println("Selected bishop!");
                break;


            default:
                break;
        }
        dismiss();
    }
}

