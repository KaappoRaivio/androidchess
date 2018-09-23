package kaappo.androidchess;

import android.os.Bundle;

import java.io.PrintWriter;

import kaappo.androidchess.askokaappochess.CMonitor;
import kaappo.androidchess.askokaappochess.chess_ui;
import kaappo.androidchess.askokaappochess.chessboard;
import kaappo.androidchess.askokaappochess.fulfiller;
import kaappo.androidchess.askokaappochess.movelibrary;
import kaappo.androidchess.askokaappochess.movevalue;
import kaappo.androidchess.askokaappochess.play;

public class ChessRunner {
    public static void run (final Bundle kamat, final ChessActivity context) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    play.main(new String[1], context, kamat.getString(MainActivity.WHITE_LEVEL), kamat.getString(MainActivity.BLACK_LEVEL));
                } catch (Exception e) {throw new RuntimeException(e);}
            }
        };
        thread.start();

    }

}
