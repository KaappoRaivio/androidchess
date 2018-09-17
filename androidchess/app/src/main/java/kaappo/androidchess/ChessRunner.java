package kaappo.androidchess;

import java.io.PrintWriter;

import kaappo.androidchess.askokaappochess.CMonitor;
import kaappo.androidchess.askokaappochess.chess_ui;
import kaappo.androidchess.askokaappochess.chessboard;
import kaappo.androidchess.askokaappochess.fulfiller;
import kaappo.androidchess.askokaappochess.movelibrary;
import kaappo.androidchess.askokaappochess.movevalue;
import kaappo.androidchess.askokaappochess.play;

public class ChessRunner {
    static final int BLACKWIN = 1;
    static final int DRAW = 2;
    static final int WHITEWIN = 3;
    static final int INTERRRUPT = 4;
    static final int RES_ERROR = 5;

    static boolean USE_LIBMOVES = true;


    static boolean USE_MOVELIBRARY = false;  // starts to be awfully obsolete. time to rid 160205
    static boolean USE_ENGINEMOVES = true;

    static final int GAME_LENGTH = 150;

    static final int FEN_ENTRY_MOVELIMIT = 61;

    static final int M4LEVEL = 5;

    static final int FULLFILLER_LEVEL = 3;
    static final int FULLFILLER_LEVEL_MAX = 4;

    static int REAL_MOVE_LIMIT = 310;


    static int ANYMOVE_LIB_LIMIT = 2;
    static final int DRAW_RETRY_LIMIT = 3;

    static final boolean flip_test_on = false;

    static String sStartFile = null;

    static final int PLAYER = -10;

    static chessboard cb = null;
    static chessboard cb2 = null;
    static chessboard ohoboard = null;
    //static chesswindow cw = null;
    static chess_ui cui = null;

    static movelibrary mlib = null;

    static final boolean GUIWINDOW = true;

    static int UI_TYPE = chess_ui.UI_TYPE_ANDROID;
    //static int UI_TYPE = chess_ui.UI_TYPE_TTY;

    static final boolean ANYMOVE_MODE = false;  // true: always pick from ANYMOVE algorihm, false: regular 170425

    static PrintWriter pw;

    static fulfiller fufi;

    public static void run (ChessActivity context) throws Exception
    {
        System.out.println("Starting.");

        CMonitor.dumpValues();

        int results[] = new int[6];

        mlib = new movelibrary();
        mlib.init();
        mlib.setMode(movelibrary.MODE_RANDOM);
        mlib.setSeed(-1);



        System.out.println("DBG151011: A");
        cb = new chessboard();
        if (sStartFile == null) cb.init();
        else cb.init_from_file(sStartFile);
        System.out.println("DBG151011: B");


        cui = new chess_ui(UI_TYPE, cb, context);


        cui.updateData(cb);
        cui.setMessage("Start new game from menu Play->New Game");
        cui.setTurn(-1);
        cui.show();

        while (true)
        {

            String inStr = cui.getMove();
            System.out.println("Command:"+inStr);
            if (inStr.indexOf("PLAY:") == 0)
            {
                int lev[] = new int[2];
                int alg[] = new int[2];
                boolean bDeep[] = new boolean[2];

                String inpieces[] = inStr.split(":");
                lev [0] = Integer.valueOf(inpieces[1]);
                lev [1] = Integer.valueOf(inpieces[2]);

                int iAlgpick = Integer.valueOf(inpieces[3]);
                int iAlg = 0;

                System.out.println("play.main() levs: " +lev[0] +"," + lev[1]);
                System.out.println("iAlgpick:" + iAlgpick);
                if (iAlgpick==1)
                {
                    System.out.println("Stockfish: " + lev[0] + "," + lev[1]);
                    if (lev[0] >= 0) iAlg = movevalue.ALG_ASK_FROM_ENGINE1+lev[0];
                    else iAlg = movevalue.ALG_ASK_FROM_ENGINE1+lev[1];
                }
                else
                    iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;

                alg[0] =  iAlg;
                alg[1] =  iAlg;

                bDeep[0] = false;
                bDeep[1] = false;

                CMonitor.setTimeLimCtl(false);
                for (int levs = 0;levs < 2; levs++) {
                    if (lev[levs] == 9) {
                        lev[levs] = 5;
                        bDeep[levs] = false;
                    }
                    else if (lev[levs] == 7) {
                        System.out.println("Levs=7");
                        lev[levs] = 4;
                        bDeep[levs] = false;
                        CMonitor.setTimeLimCtl(true);
                        //System.exit(0);
                    }
                    else if (lev[levs] > 1)
                    {
                        if ((lev[levs] % 2) == 1) bDeep[levs] = true;
                        lev[levs] = (lev[levs] ) / 2;
                    }
                    else if (lev[levs] == 1)
                    {
                        lev[levs]=0;
                        bDeep[levs]=false;
                    }
                    else if (lev[levs]==0)
                    {
                        if (!bDeep[levs]) lev[levs]=-1;
                        bDeep[levs]=false;
                    }
                }



                cb = new chessboard();
                if (sStartFile == null ) cb.init();
                else cb.init_from_file(sStartFile);

                cb.mMaxThreads = cui.getMaxThreads();


                cui.updateData(cb);
                cui.setLastMoveVector(null);
                cui.setMessage("Starting new game. Levels: " + lev[0] +"," + lev[1] + " Algorithms : " + alg[0] +"," + alg[1] + " Deepflags: " + bDeep[0] +"," + bDeep[1]);
                cui.show();
                System.out.println("Starting new game. Levels: " + lev[0] +"," + lev[1] + " Algorithms : " + alg[0] +"," + alg[1] + " Deepflags: " + bDeep[0] +"," + bDeep[1]);
                play.playgame(lev,true, alg, bDeep);
            }
        }



    }
}
