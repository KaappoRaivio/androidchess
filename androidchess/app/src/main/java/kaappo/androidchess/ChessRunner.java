package kaappo.androidchess;

import android.os.Bundle;

import kaappo.androidchess.askokaappochess.play;

public class ChessRunner {
    public static void run (final Bundle kamat, final ChessActivity context) throws Exception {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    play.main(new String[1], context, kamat.getString(MainActivity.WHITE_LEVEL), kamat.getString(MainActivity.BLACK_LEVEL), "start.dat");
                } catch (Exception e) {throw new RuntimeException(e);}
            }
        };
        thread.start();

    }

}
