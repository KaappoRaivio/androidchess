package kaappo.androidchess;

import android.media.Image;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Skeidat {
    public static List<View> getViews(TtyuiActivity context) {
        return Arrays.asList(

                context.findViewById(R.id.A1),
                context.findViewById(R.id.A2),
                context.findViewById(R.id.A3),
                context.findViewById(R.id.A4),
                context.findViewById(R.id.A5),
                context.findViewById(R.id.A6),
                context.findViewById(R.id.A7),
                context.findViewById(R.id.A8),
                context.findViewById(R.id.B1),
                context.findViewById(R.id.B2),
                context.findViewById(R.id.B3),
                context.findViewById(R.id.B4),
                context.findViewById(R.id.B5),
                context.findViewById(R.id.B6),
                context.findViewById(R.id.B7),
                context.findViewById(R.id.B8),
                context.findViewById(R.id.C1),
                context.findViewById(R.id.C2),
                context.findViewById(R.id.C3),
                context.findViewById(R.id.C4),
                context.findViewById(R.id.C5),
                context.findViewById(R.id.C6),
                context.findViewById(R.id.C7),
                context.findViewById(R.id.C8),
                context.findViewById(R.id.D1),
                context.findViewById(R.id.D2),
                context.findViewById(R.id.D3),
                context.findViewById(R.id.D4),
                context.findViewById(R.id.D5),
                context.findViewById(R.id.D6),
                context.findViewById(R.id.D7),
                context.findViewById(R.id.D8),
                context.findViewById(R.id.E1),
                context.findViewById(R.id.E2),
                context.findViewById(R.id.E3),
                context.findViewById(R.id.E4),
                context.findViewById(R.id.E5),
                context.findViewById(R.id.E6),
                context.findViewById(R.id.E7),
                context.findViewById(R.id.E8),
                context.findViewById(R.id.F1),
                context.findViewById(R.id.F2),
                context.findViewById(R.id.F3),
                context.findViewById(R.id.F4),
                context.findViewById(R.id.F5),
                context.findViewById(R.id.F6),
                context.findViewById(R.id.F7),
                context.findViewById(R.id.F8),
                context.findViewById(R.id.G1),
                context.findViewById(R.id.G2),
                context.findViewById(R.id.G3),
                context.findViewById(R.id.G4),
                context.findViewById(R.id.G5),
                context.findViewById(R.id.G6),
                context.findViewById(R.id.G7),
                context.findViewById(R.id.G8),
                context.findViewById(R.id.H1),
                context.findViewById(R.id.H2),
                context.findViewById(R.id.H3),
                context.findViewById(R.id.H4),
                context.findViewById(R.id.H5),
                context.findViewById(R.id.H6),
                context.findViewById(R.id.H7),
                context.findViewById(R.id.H8)

        );

    }

    public static List<View> getPiecesOnBoard (TtyuiActivity context) {
        List<View> temp = new ArrayList<>();

        GridLayout layout = context.findViewById(R.id.chessboard);

        for (int i = 0; i < layout.getChildCount(); i++) {
            RelativeLayout subView = (RelativeLayout) layout.getChildAt(i);

            System.out.println(subView.getChildCount() + "asd");

            System.out.println(subView.toString() + ", " + subView.getChildAt(0));

            ImageView piece = (ImageView) subView.getChildAt(0);

            if (piece != null) {
                temp.add(piece);
            }
        }

        return temp;
    }
}
