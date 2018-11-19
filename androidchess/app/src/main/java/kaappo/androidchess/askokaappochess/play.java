package kaappo.androidchess.askokaappochess;

import android.content.Context;

import java.util.*;
import java.sql.*;
import java.io.PrintWriter;

import kaappo.androidchess.ChessActivity;

public class play
{
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
	
	public static final int PLAYER = -10;
	
	static chessboard cb = null;
	static chessboard cb2 = null;
	static chessboard ohoboard = null;
	//static chesswindow cw = null;
	static ChessUI cui = null;
	
	static movelibrary mlib = null;
	
	static final boolean GUIWINDOW = true;
	
	static int UI_TYPE = ChessUI.UI_TYPE_WINDOW;
	//static int UI_TYPE = ChessUI.UI_TYPE_TTY;
	
	static final boolean ANYMOVE_MODE = false;  // true: always pick from ANYMOVE algorihm, false: regular 170425
	
	static PrintWriter pw;
	
	static fulfiller fufi;
	
	public static void main (String[] args, ChessActivity context, String white_level, String black_level, String pathToStartFile) throws Exception
	{
		System.out.println("Starting.");

		CMonitor.dumpValues();

		int results[] = new int[6];

		mlib = new movelibrary();
		mlib.init();
		mlib.setMode(movelibrary.MODE_RANDOM);
		mlib.setSeed(-1);

		sStartFile = pathToStartFile;

		System.out.println("DBG151011: A");
		cb = new chessboard();
		if (sStartFile == null) {
			cb.init();
		} else {
			USE_MOVELIBRARY = false;
			USE_LIBMOVES = false;
			cb.init_from_file(sStartFile, context);
		}
		System.out.println("DBG151011: B");


		cui = new ChessUI(UI_TYPE, cb, context);


		cui.updateData(cb);
		cui.setMessage("Start new game from menu Play->New Game");
		cui.setTurn(-1);
		cui.show();

		boolean firstTime = true;
		String inStr;
		while (true)
		{
			if (firstTime) {
				firstTime = false;
				inStr = "PLAY:" + white_level + ":" + black_level + ":0";
			} else {
				inStr = cui.getMove();
			}
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
				for (int levs=0;levs<2;levs++)
				{
					if (lev[levs] == 9)
					{
						lev[levs] = 5;
						bDeep[levs] = false;
					}
					else if (lev[levs] == 7)
					{
						System.out.println("Levs=7");
						lev[levs] = 4;
						bDeep[levs] = false;
						CMonitor.setTimeLimCtl(true);
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
				else cb.init_from_file(sStartFile, context);

				// $$$$ 150127 to play games from position in file
				//cb.init_from_file("fail6.dat");
				//cb.init_from_file("kind.dat");
				//cb.mMaxThreads = cw.mMaxThreads;
				cb.mMaxThreads = cui.getMaxThreads();

				/*
				cw.updateData(cb);
				cw.setLastMoveVector(null);
				cw.setMessage("Starting new game. Levels: " + lev[0] +"," + lev[1] + " Algorithms : " + alg[0] +"," + alg[1] + " Deepflags: " + bDeep[0] +"," + bDeep[1]);
				cw.show();
				*/
				cui.updateData(cb);
				cui.setLastMoveVector(null);
				cui.setMessage("Starting new game. Levels: " + lev[0] +"," + lev[1] + " Algorithms : " + alg[0] +"," + alg[1] + " Deepflags: " + bDeep[0] +"," + bDeep[1]);
				cui.show();
				System.out.println("Starting new game. Levels: " + lev[0] +"," + lev[1] + " Algorithms : " + alg[0] +"," + alg[1] + " Deepflags: " + bDeep[0] +"," + bDeep[1]);
				playgame (lev,true, alg, bDeep, context);
			}
		}



	}

	public static int playgame (int lev[], boolean bMess, int alg[], boolean bDeep[], Context context) throws Exception
	{
		PrintWriter pwEmoves;
		
		
		System.out.println("playgame called: " +lev[0] +"," + lev[1]+ "  Deepflags: " + bDeep[0] + "," + bDeep[1] + " .. cb.iFileCol = " + cb.iFileCol + "algs: " + alg[0] + "," + alg[1]);
		System.out.println("REAL_MOVE_LIMIT:"+REAL_MOVE_LIMIT);

		int iMove = 1;
		System.out.println("DBG160307: playgame:" + cb.iMoveCounter);
		if (cb.iMoveCounter != 0)
		{
			iMove = cb.iMoveCounter;
		}
	
//		pwEmoves = new PrintWriter(new BufferedWriter(new FileWriter("ermoves.out", true)));
//		pwEmoves.println("--NEW:");
//		pwEmoves.flush();
//
		boolean bGameOn = true;
		boolean bCheckMate = false;
		boolean bNoMoves = false;
		int iWinner = -1;
		int iUndoCount = 0;
		
		int checkcount = 0;
		king pKing = null;
		Vector vCheck = null;
		
		gamehistory gh = new gamehistory();
		
		//if (GUIWINDOW) cw.setgamehistory(gh);
		cui.setgamehistory(gh);
		
		String sNext = null;
		String sMove = null;
		
		int[] iRightGuess = {0,0};
		int[] iWrongGuess = {0,0};
		
		int[] iTimeLim;
		iTimeLim = new int[2];
		
		String sLastEMove = null;
		
		for (int p =0;p < 2;p++)
		{
			if (lev[p] != PLAYER)
			{
				if (lev[p] >= 2) 
				{
					if (lev[p] == 2)
					{
						iTimeLim[p] = 2;
					}
					if (lev[p]==3)
					{
						if (bDeep[p]) iTimeLim[p] = 43;
						else iTimeLim[p] = 8;
					}
					if (lev[p]==4)
					{
						if (bDeep[p]) iTimeLim[p] = chessboard.CB_MAXTIME;
						else iTimeLim[p] = 115;
						bDeep[p] = true;
					}
					if (lev[p]==5)
					{
						iTimeLim[p] = 295;
						bDeep[p] = false;
					}
					//lev[p] = 3;
				}
				else iTimeLim[p] = chessboard.CB_MAXTIME;
			}
		}
		
		long[] lLatency;
		lLatency = new long[2];
		
		openings o = new openings(context);
		
		System.out.println("playgame about to start: " +lev[0] +"," + lev[1]+ "  Deepflags: " + bDeep[0] + "," + bDeep[1] + " timelims:" + iTimeLim[0]+","+iTimeLim[1] + " .. cb.iFileCol = " + cb.iFileCol);
		
		cb.redoVectorsAndCoverages(1, alg[1]);
		
		int iRealMoveCount = 0;
		
		int iRandEngLibLimit = 0;		
		if (!ANYMOVE_MODE) iRandEngLibLimit = 1+(int)(Math.random()*6);
		
		
		
		movevalue mvalc = null;
		
		while ((iMove < GAME_LENGTH) && bGameOn)
		{
			/*
			System.out.println("************************");
			System.out.println("GAME HISTORY DUMP BEGINS");
			gh.dump();
			System.out.println("GAME HISTORY DUMP ENDS");
			System.out.println("************************");
			*/
			
			System.out.println("DBG161010: GAME LOOP iMove: " + iMove);
			
			if (iRealMoveCount >= REAL_MOVE_LIMIT) return play.RES_ERROR;
			
			
			
			for (int clr = piece.WHITE; clr <= piece.BLACK; clr++)
			{
				System.out.println("DBG161010: GAME LOOP clr: " + clr+ " ,@move:" + iMove);
				
				// FULFILLER TEST 180416
				if ((iMove >= FULLFILLER_LEVEL) && (iMove <= FULLFILLER_LEVEL_MAX) && (alg[clr] == movevalue.ALG_ASK_FROM_ENGINE_RND) && (fufi != null))
				{
					fufi.fulfill(cb.FEN(), iMove);
				}
				
				if (cb.iFileCol == piece.BLACK)
				{
					clr = piece.BLACK;
					cb.iFileCol = -1;
					System.out.println("Skipping white move. Starting from black");
				}
				
				System.out.println("Game so far: " + gh.sMovehistory());
				System.out.println("In lib format: " + gh.sMovehistory_bylib());
				System.out.println("Latencies: " + lLatency[0] + " " + lLatency[1]);
				System.out.println("Possible openings:");
				o.searchByGame(gh.sMovehistory_bylib());
				if (lev[clr] == PLAYER)   // $$$ 140412 debug
				{
					long lStart = System.currentTimeMillis();
					System.out.println("Player turn " + iMove + " starts.");
					//cw.setTurn(clr);
					cui.setTurn(clr);
					
					/*if (checkcount == 0) cw.setMessage(" Move " + iMove + ". Your move.");
					else cw.setMessage("Move " + iMove + ". CHECK");
					
					cw.repaint();
					*/
					if (checkcount == 0) cui.setMessage(" Move " + iMove + ". Your move.");
					else cui.setMessage("Move " + iMove + ". CHECK");
					
					cui.setLatencies(lLatency);
					cui.repaint();
					
					
					boolean bCont = false;
					
					
					//cb.miWhiteMoveindex.sortedcopy().dump(cb.miBlackMoveindex.sortedcopy());
					
					while (!bCont)
					{
						//String inStr = cw.getMove();
						String inStr = cui.getMove();
						if (inStr.equalsIgnoreCase("EXIT")) return play.INTERRRUPT;
						if ((inStr.equalsIgnoreCase("OHO"))  && (ohoboard != null))
						{
							System.out.println("Executing OHO");
							cb = ohoboard.copy();
							cb.redoVectorsAndCoverages(clr,alg[clr]);
							/*
							cw.updateData(cb);
							cw.setLastMoveVector(cb.lastmoveVector());
							cw.repaint();
							*/
							cui.updateData(cb);
							cui.setLastMoveVector(cb.lastmoveVector());
							cui.repaint();
							
							iMove = iMove-1;
							ohoboard = null;
							//cw.ch_item_undo.setEnabled(false);
							cui.enableUndo(false);
							gh.ohomove();
							iUndoCount++;
							// $$$$$ gamehistory, will it work?
						}
						else
						{
							// here's the actual move by player
							ohoboard = cb.copy();
							cb.iMoveCounter = iMove;
							cb.domove(inStr,clr);
							
							chessboard cb3 = cb.copy();
							/*
							// perhaps skewer processing made the code below unsafe 150926
							cb.resetSkewers();
							System.out.println("DBG150925: skewercrash ...");
							cb.dump();
							cb.redoVectorsAndCoverages(clr,alg[clr]);
							cui.setLastMoveVector(cb.lastmoveVector());
							cui.enableUndo(true);
							*/
							cb3.resetSkewers();
							cb3.redoVectorsAndCoverages(clr,alg[clr]);
							cui.setLastMoveVector(cb3.lastmoveVector());
							cui.enableUndo(true);
							
							cb = cb3;
							
							System.out.println("Player turn " + iMove + " done.");
							bCont = true;
							long lEnd = System.currentTimeMillis();
						
							lLatency[clr] = lLatency[clr]+(lEnd-lStart);
							
						}
					}
				}
				else
				{
					if ((clr == piece.WHITE) && (cb.iMoveCounter == iMove)) cb.iMoveCounter--;
					
					System.out.println("AskoChess turn " + iMove + " starts. FEN: " + cb.FEN() + " cb.iMoveCounter: " + cb.iMoveCounter+ " clr:" + clr);
					if (iMove != enginerunner.getFENMoveCount(cb.FEN()) && (cb.lm_vector != null))
					{
						System.out.println("Warning!! Glitch in move count:"  );
					}
					System.out.println("Urgency: "+  cui.getUrgency());
					long lStart = System.currentTimeMillis();
					
					/*
					if (checkcount == 0) cw.setMessage(" Move " + iMove + ". Thinking.");
					else cw.setMessage("Move " + iMove + ". CHECK. Thinking. ");
					cw.updateData(cb);
					cw.repaint();
					*/
					if (checkcount == 0) cui.setMessage(" Move " + iMove + ". Thinking.");
					else cui.setMessage("Move " + iMove + ". CHECK. Thinking. ");
					cui.updateData(cb);
					cui.repaint();
					
					String sLibMove = null;
					
					if ((USE_MOVELIBRARY) && (alg[clr] != movevalue.ALG_ASK_FROM_ENGINE_RND))
					{
						if (mlib.getSeed() != -1) sLibMove=mlib.sMoveBySeed(iMove,clr);
						else sLibMove=mlib.sNextMove(gh.sMovehistory());
					}
					
					String sSugg = null;
					if (((USE_LIBMOVES) && (alg[clr] != movevalue.ALG_ASK_FROM_ENGINE_RND)) ||
					   ((alg[clr] == movevalue.ALG_ASK_FROM_ENGINE_RND) && (iMove < iRandEngLibLimit) && (USE_LIBMOVES)))
					{
						sSugg = o.getFittingMove(gh.sMovehistory_bylib(),iMove,clr);
						System.out.println("sSugg returned:" + sSugg);
					}
					
					/*
					if ((alg[clr]==movevalue.ALG_ASK_FROM_ENGINE_RND) && (iMove ==1))
					{
						if (clr==0) sSugg = "c4";
						else sSugg="g6";
					}
					*/
					
					// $$$$ 170214 hack!
					
					/*if ((alg[clr] < movevalue.ALG_ASK_FROM_ABROK1) && (alg[clr] > movevalue.ALG_ASK_FROM_ABROK4)) USE_ENGINEMOVES = true;
					else USE_ENGINEMOVES = false;
					*/
					
					if (USE_ENGINEMOVES &&(sLibMove == null) && (sSugg == null) && ((lev[clr]>0) || (iMove < ANYMOVE_LIB_LIMIT)) && (alg[clr] != movevalue.ALG_ASK_FROM_ENGINE_RND))
					{
						if (!CMonitor.bNoBlood())
						{
							String fname;
							System.out.println("ENGINE move " + sSugg);
							if (clr ==piece.WHITE) fname = "blood"+(cb.iMoveCounter+1)+".dat";
							else fname = "blood"+(cb.iMoveCounter)+".dat";
							dumpboard(clr,cb,fname);
							
							sLibMove = enginerunner.getMove(cb.FEN());
							System.out.println("Got Move from Enginerunner:" + sLibMove);
							if (sLibMove != null) 
							{
								sLastEMove = cb.FEN();
//								pwEmoves.println("SUCC:"+sLastEMove + ":"+sLibMove);
//								pwEmoves.flush();
//
							}
							else
							{
								System.out.println("LIBMOVE NOT FOUND. FAIL BY:"+cb.FEN());
//								pwEmoves.println("FAIL:"+cb.FEN());
//								pwEmoves.flush();
//								pwEmoves.close();
							}
						}
					}
					
					
					if ((iMove == M4LEVEL) && (alg[clr] != movevalue.ALG_ASK_FROM_ENGINE_RND))
					{
					}
					
					//sLibMove = null;   // DEBUG 150322
					//sSugg = null;
					
					if (sLibMove != null)
					{
						System.out.println("trying libmove:" + sLibMove);
						cb.iMoveCounter = iMove;
						cb.domove(sLibMove,clr);
						cb2 = cb.copy();
						cb2.redoVectorsAndCoverages(clr,alg[clr]);
						cui.setLastMoveVector(cb2.lastmoveVector());
					}
					else if (sSugg != null)
					{
						if (!CMonitor.bNoBlood())
						{
							String fname;
							System.out.println("trying largelib move " + sSugg);
							if (clr ==piece.WHITE) fname = "blood"+(cb.iMoveCounter+1)+".dat";
							else fname = "blood"+(cb.iMoveCounter)+".dat";
							dumpboard(clr,cb,fname);
						}
						
						cb.iMoveCounter = iMove;
						cb.domove_bylib(sSugg,clr);
						System.out.println("trying largelib move bef redo" + sSugg);
						/*
						cb.redoVectorsAndCoverages(clr,alg[clr]);
						System.out.println("trying largelib move aft redo" + sSugg);
						//if (GUIWINDOW) cw.setLastMoveVector(cb.lastmoveVector());
						cui.setLastMoveVector(cb.lastmoveVector());
						System.out.println("trying largelib move bef copy" + sSugg);
						cb2 = cb.copy();
						System.out.println("largelib move done " + sSugg);
						*/
						cb2 = cb.copy();
						cb2.redoVectorsAndCoverages(clr,alg[clr]);
						cui.setLastMoveVector(cb2.lastmoveVector());
						
					}
					else
					{
						
						System.out.println("Doing regular move. clr = " + clr + " by alg: " + alg[clr]);

						//movevalue mmval = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0, 0,false, false,false,false);
						movevalue mmval = new movevalue("");
						mmval.setbase(clr);
						if ((iMove < FEN_ENTRY_MOVELIMIT) && (alg[clr] != movevalue.ALG_ASK_FROM_ENGINE_RND)) 
						{
							if (!CMonitor.bNoBlood()) 
							{
								printFENEntry(cb.FEN());
								printLastFoundFEN(sLastEMove);
								System.out.println("FEN:"+cb.FEN() + " move count:" + enginerunner.getFENMoveCount(cb.FEN()));
								if (sLastEMove != null) System.out.println("sLastEMove:"+sLastEMove + " move count:" + enginerunner.getFENMoveCount(sLastEMove));
								
								/*
								int iFENMove = enginerunner.getFENMoveCount(cb.FEN());
								int iPrevFENMove = -1;
								if (sLastEMove != null) iPrevFENMove = enginerunner.getFENMoveCount(sLastEMove);
								
								if ((iPrevFENMove != -1) && ((iFENMove != iMove) || (iFENMove != (iPrevFENMove+1))))
								{
									System.out.println("Glitch in move counts. iMove:" + iMove);
								}
								*/
							}
							iRealMoveCount++;
							System.out.println("Real move being done: iRealMoveCount: " + iRealMoveCount);
							if (iRealMoveCount<=1)
							{
								/*String fname;
								if (iRealMoveCount==0)
								{
								if (clr ==piece.WHITE) fname = "blood"+(cb.iMoveCounter+1)+".dat";
								else fname = "blood"+(cb.iMoveCounter)+".dat";
								}
								else fname = "firstblood.dat";
								
								PrintWriter fbp = new PrintWriter(fname);
								if (clr==piece.WHITE) fbp.println("WHITE");
								else fbp.println("BLACK");
								cb.dump_to_file(fbp);
								//fbp.println("MC:" + cb.iMoveCounter);
								if (clr==piece.WHITE) fbp.println("MC:" + (cb.iMoveCounter+1));
								else fbp.println("MC:" + (cb.iMoveCounter));
								fbp.flush();
								fbp.close();
								*/
								if (!CMonitor.bNoBlood()) dumpboard(clr,cb,"firstblood.dat");
							}
						}
						
						
						cb.iMoveCounter = iMove;
						
						
						
						//if (GUIWINDOW) cb.mMaxThreads = cw.mMaxThreads;
						//else cb.mMaxThreads = 64;
						cb.mMaxThreads = cui.getMaxThreads();
						
						int iUrg = cui.getUrgency();
						//int iUrg = 1;  // $$$ 160315
						int iTL = iTimeLim[clr];
						int iLEV = lev[clr];
						
						if ((iUrg != 0) && (lev[clr] == 3) && (!bDeep[clr]))
						{
							if (iUrg==1) iTL = 4;
							if (iUrg==2) 
							{
								iTL = chessboard.CB_MAXTIME;
								iLEV = 2;
							}
						}
						
						if ((iUrg != 0) && (lev[clr] == 4) && (!bDeep[clr]))
						{
							if (iUrg==1) iTL = 60;
							if (iUrg==2) 
							{
								iTL = 13;
								iLEV = 3;
							}
						}
						
						CMonitor.setUrgency(iUrg);
						
						System.out.println("MAXTHREADS = " + cb.mMaxThreads + " timelim:" + iTL +  " lev:"+ iLEV+" urg:" + iUrg+ " cb.iMoveCounter:" + cb.iMoveCounter);
						
						mostore mos = new mostore();
						//cb.iFileCol = clr;
						
						if ((cb.iMoveCounter == 1) && (clr == 0)) cb.iFileCol = 0;
						
						boolean bDebug = false;
						
						
						
						if (cb.bKillModeQRwK() && !cb.bPawnsOrMinorPieces())
						{
							if ((((cb.iWhitePieceCount[piece.ROOK] != 0) || 
							     (cb.iWhitePieceCount[piece.QUEEN] != 0)) &&
								 (clr == piece.WHITE)) 
								 ||
							   (((cb.iBlackPieceCount[piece.ROOK] != 0) || 
							     (cb.iBlackPieceCount[piece.QUEEN] != 0)) &&
								 (clr == piece.BLACK)) ) 
								 {
									System.out.println("DBG171113:bKillModeQRwK mode on, setting iLev = 0");
									iLEV = 0;
								 }
						}
						
						if (iLEV == 0) 
						{
							bDebug = true;	
							hcwinner.bHcwEnabled = true;
						}
						
										
						//cb2 = cb.findAndDoBestMove(clr,iLEV,mmval,alg[clr],bDebug,gh,null,bDeep[clr], null,null,null,null,iTL, true, null, mos);
						// $$$$ 160609, calling without gh
						cb2 = cb.findAndDoBestMove(clr,iLEV,mmval,alg[clr],bDebug,null,null,bDeep[clr], null,null,null,null,iTL, true, null, mos);
						
						
						mvalc = mmval.copy();
						// alg5=debug
						//cb2 = cb.findAndDoBestMove(clr,lev[clr],mmval,alg[clr],true,gh,null,bDeep[clr], null,null,null);
						
						// $$$$ 160609 different draw detection
						if (gh.bIsTieCandidate(cb2,clr))
						{
							System.out.println("Repetition draw here. Color: " + clr);
							cb.dump();
							System.out.println(mvalc.dumpstr(alg[clr], movevalue.DUMPMODE_SHORT));
							cb2.dump();
							
							int iCPB;
							if ((iLEV % 2) == 0) iCPB = mvalc.getCorrPieceBal(1-clr);
							else iCPB = mvalc.getCorrPieceBal(clr);
							
							System.out.println("iCPB:" + iCPB);
							
							boolean bRetry = false;
							
							if (((clr == piece.WHITE) && ((iCPB < 0) || mvalc.bWhiteCheckMate)) || ((clr == piece.BLACK) && ((iCPB > 0) || mvalc.bBlackCheckMate))) System.out.println("Take the draw, IT'S GOOD MOVE NOW!");
							else bRetry = true;
							
							if ((bRetry) && (alg[clr] < movevalue.ALG_ASK_FROM_ENGINE1))
							{
								int iRetryCount = 0;
								boolean bGoodAltFound = false;
								
								System.out.println("mvalc:" + mvalc.dumpstr(alg[clr],movevalue.DUMPMODE_SHORT));
								System.out.println(mvalc.sMove());
								
								CMonitor.sDrawMove = new String(mvalc.sMove());
								CMonitor.iMonLevel=iLEV;
								System.out.println("Draw move is:" + CMonitor.sDrawMove);
								
								
								while ((iRetryCount < DRAW_RETRY_LIMIT) && !bGoodAltFound)
								{
								
									System.out.println("Inside draw loop, iRetryCount: " + iRetryCount);
									System.out.println("CMonitor.sDrawMove:" + CMonitor.sDrawMove);	
									movevalue dmval = new movevalue("");
									chessboard cb3 = cb.findAndDoBestMove(clr,iLEV,dmval,alg[clr],bDebug,null,null,bDeep[clr], null,null,null,null,iTL, true, null, mos);
									System.out.println("To avoid draw:");
									
									if (cb3 != null) cb3.dump();
									else iRetryCount = DRAW_RETRY_LIMIT;
									
									System.out.println(dmval.dumpstr(alg[clr], movevalue.DUMPMODE_SHORT));
								
									if ((iLEV % 2) == 0) iCPB = dmval.getCorrPieceBal(1-clr);
									else iCPB = dmval.getCorrPieceBal(clr);
									
									iRetryCount++;
									if (gh.bIsTieCandidate(cb3,clr))
									{
										CMonitor.sDrawMove = CMonitor.sDrawMove+";"+new String(dmval.sMove());
									}
									else 
									{
										System.out.println("iCPB:" + iCPB);
										if (((clr == piece.WHITE) && (iCPB < 0)) || ((clr == piece.BLACK) && (iCPB > 0))) System.out.println("We are behind");
										else
										{
											if (cb3 != null)
											{
												System.out.println("OK, we are even or ahead. This won't be a draw!");
												bGoodAltFound = true;
												cb2=cb3;
												mvalc = dmval;
											}
											else System.out.println("Null CB3, have to pick a move on way to draw.");
										}
									}
									
								}
								if (!bGoodAltFound) System.out.println("Had to choose a draw");

							}
						}
						
						CMonitor.sDrawMove = "";
						
						if (cb2 != null)
						{
							System.out.println("move done.: " + cb2.iMoveCounter);
							cb2.dump();
							cb2.iFileCol =  -1 ;
							System.out.println("CB2 iFileCol set to -1!");
						}
						else
						{
							System.out.println("Null cb2, it's a draw?");
						}

						
						
						long lEnd = System.currentTimeMillis();
						
						lLatency[clr] = lLatency[clr]+(lEnd-lStart);
						
						sMove = mmval.sMove();
						if ((sNext != null) && (sMove != null) && !sMove.equals(sNext))
						{
							System.out.println("Guessed wrong at round " + iMove + ". Guess was:" + sNext + ". real move was: " + sMove);
							iWrongGuess[clr]++;
						}
						else 
						{
							if (sMove != null) 
							{
								System.out.println("Guess was right. Round: " + iMove + " move:" + sMove);
								iRightGuess[clr]++;
							}
							else System.out.println("Move & guess both null.");
						}
						sNext = mmval.sGuessNext();
						System.out.println(mmval.dumpstr(alg[clr]));
						System.out.println("M:" + mmval.sMove() + " N:" + mmval.sGuessNext());
						
						
						//compare algs 0 & secondop debug code 140412
						/*
						int iSecondOp = -1;
						if (alg[clr] == 40) iSecondOp = 34;
						else iSecondOp = 40;
						
						movevalue mmval2 = new movevalue("");
						
						mmval.setbase(clr);
						chessboard cb_second = cb.findAndDoBestMove(clr,lev[clr],mmval2,iSecondOp,false,gh,null,bDeep[clr],null,null,null,null,iTimeLim[clr],false,null);
						
						if ((cb2 != null) && (!cb_second.equals(cb2)))
						{
							System.out.println("===========DEBUBDEBUGDEBUG=======");
							System.out.println("Discrepancy between algs " + alg[clr] + " & " + iSecondOp + ". Investigate immediately DBG 140412 !");
							cb.dump();
							System.out.println("---");
							System.out.println("Alg " + alg[clr] + " output is:");
							System.out.println(mmval.dumpstr(alg[clr]));
							cb2.dump();
							System.out.println("---");
							System.out.println("Alg " + iSecondOp + " output is:");
							System.out.println(mmval2.dumpstr(iSecondOp));
							cb_second.dump();
							System.out.println("---");
							
						}
						else System.out.println("Algs " + alg[clr]+ "  & " + iSecondOp + "  agree!");
						*/
						// end compare
						
						
						
						// Second guess by flipping 140515
						
						if (flip_test_on)
						{
						
							//movevalue mmval2 = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
							movevalue mmval2 = new movevalue("");
							chessboard cb_flip = cb.flip(clr, alg[clr]);
							System.out.println("FLIPPED BOARD:");
							cb_flip.dump();
							// clr or 1-clr? iterating 140624
							
							if ((cb.iKicPoints(piece.WHITE) != cb_flip.iKicPoints(piece.BLACK)) || (cb.iKicPoints(piece.BLACK) != cb_flip.iKicPoints(piece.WHITE)))
							{
								System.out.println("DISCREPANCY IN KIC POINTS!!! ***********************");
								System.out.println("clr value " + clr);
								
								king kow = cb.locateKing(piece.WHITE);
								king kob = cb.locateKing(piece.BLACK);
								king kfw = cb_flip.locateKing(piece.WHITE);
								king kfb = cb_flip.locateKing(piece.BLACK);
								
								System.out.println("ORIG: w: " + cb.iKicPoints(piece.WHITE) + " b: " + cb.iKicPoints(piece.BLACK) + " FLIP w: " + cb_flip.iKicPoints(piece.WHITE) + " b: " + cb_flip.iKicPoints(piece.BLACK) + " mc: cb: " + cb.iMoveCount + " cb_flip: " + cb_flip.iMoveCount + ".  Move counters cb:" + cb.iMoveCounter + " flip: " + cb_flip.iMoveCounter+ " king last moves: Orig w: " + kow.iLastMove + " b: " + kob.iLastMove + " Flip w: " +kfw.iLastMove + " b: " + kfb.iLastMove);
								
								cb_flip.dump();
								throw new RuntimeException("DISCREPANCY IN KIC POINTS!!! ***********************");
							}	
							else
							{
								System.out.println("kic points checked ok.");
								
								king kow = cb.locateKing(piece.WHITE);
								king kob = cb.locateKing(piece.BLACK);
								king kfw = cb_flip.locateKing(piece.WHITE);
								king kfb = cb_flip.locateKing(piece.BLACK);
								
								System.out.println("ORIG: w: " + cb.iKicPoints(piece.WHITE) + " b: " + cb.iKicPoints(piece.BLACK) + " FLIP w: " + cb_flip.iKicPoints(piece.WHITE) + " b: " + cb_flip.iKicPoints(piece.BLACK) + " mc: cb: " + cb.iMoveCount + " cb_flip: " + cb_flip.iMoveCount + ".  Move counters cb:" + cb.iMoveCounter + " flip: " + cb_flip.iMoveCounter+ " king last moves: Orig w: " + kow.iLastMove + " b: " + kob.iLastMove + " Flip w: " +kfw.iLastMove + " b: " + kfb.iLastMove);
								
							}
							
							chessboard cb_second = cb_flip.findAndDoBestMove(1-clr,lev[clr],mmval2,alg[clr],false,null,null,bDeep[clr], null,null,null,null,null);
							// remove game history from flip checker to avoid tie conflicts
							// 140624
							
							chessboard cb_flip2 = null;
							
							if (cb_second != null) 
							{
								cb_flip2 = cb_second.flip(1-clr, alg[1-clr]);
							}
							
							if ((cb_flip2 != null) && (!cb_flip2.equals(cb2)))
							{
								System.out.println("Flip check detected problem. Go and investigate! Lev: " + lev[clr] + " Alg: " + alg[clr]);
								System.out.println("Original move was : " + mmval.dumpstr(alg[clr]));
								System.out.println("Flipped move was: " + mmval2.dumpstr(alg[clr]));
								
								if (mmval.isEqualMirror (mmval2, alg[clr],  clr) || (mmval.mirrorMates(mmval2)))
								{
									System.out.println("It was OK!");
								}
								else
								{
									System.out.println("Failed, move value diff!");
									System.out.println("Original board:");
									cb.dump();
									System.out.println("Last move:" + cb.lastmoveString());
									System.out.println("-----");
									System.out.println("Flipped orig board:");
									cb_flip.dump();
									System.out.println("Last move:" + cb_flip.lastmoveString());
									System.out.println("Potential failure flip.");
									System.out.println("Reflip to avoid tie bias.");
									//movevalue mmval3 = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
									movevalue mmval3 = new movevalue("");
									chessboard cb_third = cb.findAndDoBestMove(clr,lev[clr],mmval3,alg[clr],true,null,null,bDeep[clr],null,null,null,null,null);
									if (!cb_flip2.equals(cb_third)  )
									{
										System.out.println("It was a real discrepancy!!!!");
										System.out.println("1st move: " + mmval.dumpstr(alg[clr]));
										System.out.println("2nd move: " + mmval2.dumpstr(alg[clr]));
										System.out.println("3rd move: " + mmval3.dumpstr(alg[clr]));
										if (mmval3.isEqualMirror (mmval2, alg[clr],  clr))
										{
											System.out.println("But, still equal values..");
										}
										else
										{
											System.out.println("mmval values are different :(");
											
											System.out.println("Original board:");
											cb.dump();
											System.out.println("Flipped board:");
											cb_flip.dump();
											System.out.println("Game so far: " + gh.sMovehistory());
											
											throw new RuntimeException("mmval values are different :(");
										}
										
									}
									
								}
							}
						}  // flip test end
						
						
					}
					if (cb2 == null) 
					{
						king kk = cb.locateKing(clr);
						checkcount = cb.iCountCheckers(kk);
						
						if (checkcount > 0) 
						{
							System.out.println("GAME OVER:: AskoChess has lost!");
							
							/*cw.setMessage("GAME OVER. CHECKMATE on move " +iMove+".");
							cw.repaint();
							*/
							cui.setMessage("GAME OVER. CHECKMATE on move " +iMove+".");
							cui.repaint();
							
							//if (bMess) cw.displayMsgDialog("GAME OVER. CHECKMATE on move " +iMove+".");
							if (bMess) cui.displayMsgDialog("GAME OVER. CHECKMATE on move " +iMove+".");
							if (clr == piece.WHITE) return play.BLACKWIN;
							else return play.WHITEWIN;
						}
						else
						{
							System.out.println("GAME OVER:: No Moves. It's a tie.");
							/*
							cw.setMessage("GAME OVER. No available moves. It's a tie on move " +iMove+".");
							if (bMess) cw.displayMsgDialog("GAME OVER. No more moves on move " +iMove+".");
							cw.repaint();
							*/
							cui.setMessage("GAME OVER. No available moves. It's a tie on move " +iMove+".");
							if (bMess) cui.displayMsgDialog("GAME OVER. No more moves on move " +iMove+".");
							cui.repaint();
							
							//return play.DRAW;
							bGameOn = false;
							iWinner = -1;
						}
					}
					

					if (bGameOn) {
						/*
						cw.setLastMoveVector(cb2.lastmoveVector());
						cw.updateData(cb2);
						*/
						cui.setLastMoveVector(cb2.lastmoveVector());
						cui.updateData(cb2);
						
						pKing = cb2.locateKing(1-clr);
						if (pKing == null) 
						{
							System.out.println("King has gone. Game is over.");
						}
						else 
						{
							cb = cb2.copy();
							cb.redoVectorsAndCoverages(clr,alg[clr]);
						}
					}
					
					/*
					cw.updateData(cb);
					cw.repaint();
					*/
					cui.updateData(cb);
					cui.repaint();
					
					System.out.println("AskoChess turn " + iMove + " done.");
				}
				/*
				System.out.println("Lastmove string: " + cb.lastmoveString_bylib());
				System.out.println("Move histories:");
				System.out.println(gh.sMovehistory());
				System.out.println(gh.sMovehistory_bylib());
				*/
				System.out.println("Move done ... checking checkers etc. Clr =  " + clr);
				cb.dump();
				System.out.println(cb.FEN());
				//cb.dumpCoverages();
				//cb.dumpProtThreat();
				pKing = cb.locateKing(1-clr);
				if (pKing == null)
				{
					System.out.println("No king on board any more");
					throw new RuntimeException("No king on board any more");
				}
				System.out.println("King at " + pKing.xk + "," + pKing.yk + " color " + pKing.iColor);
				checkcount = cb.iCountCheckers(pKing);
				vCheck = cb.locateCheckersVector(pKing);
				
				gh.addmove(cb,clr,mvalc);
				if (gh.bRepetition())
				{
					System.out.println("Repetition detected. It's a draw!");
					bGameOn = false;
					iWinner = -1;
					//return play.DRAW;
				}
				
				if (checkcount > 0)
				{
					System.out.println("CHECKCOUNT = " + checkcount);
					
					int mc = cb.clearMoveVectorsUnderCheck(1-clr,pKing,vCheck);
					System.out.println("mc = " + mc);
					if (mc == 0)
					{
						System.out.println("Game ended in CHECKMATE ( " + clr + ")");
						cb.dump();
						cui.updateData(cb);
						cui.repaint();
						bGameOn = false;
						iWinner = clr;
						clr = 2;
						bCheckMate = true;
						//if (bMess) cw.displayMsgDialog("Game ended in CHECKMATE.");
						if (bMess) cui.displayMsgDialog("Game ended in CHECKMATE.");
						
					}
				}
				
				/*
					if no moves -> patissa ...
					bNoMoves = true;
					bGameOn = false;
				*/
				if (cb.bIsDrawByPieces())
				{
					System.out.println("DrawByPieces detected");
					bGameOn = false;
					iWinner = -1;
				}
				
			}
			iMove++;
		}
		
		String sConcl = iMove + " moves played.";
		if (iWinner == piece.WHITE) sConcl = sConcl + " WHITE has won.";
		else if (iWinner == piece.BLACK) sConcl = sConcl + " BLACK has won.";
		else sConcl = sConcl + " Game drawn.";
		gh.addNote(sConcl);
		
		if (cb.iExpectedWinner() != -10)
		{
			if (cb.iExpectedWinner() == iWinner) gh.addNote("TESTRESULT: SUCCESS.");
			if (cb.iExpectedWinner() != iWinner) gh.addNote("TESTRESULT: FAILURE.");
		}
		
		if ((cb.vTestDir != null) && (cb.vTestDir.size() >0 )) gh.addNote((String)cb.vTestDir.elementAt(0));
		gh.addNote("Next move estimate summary:  WHITE: Correct: " + iRightGuess[0] + ", Failed: " + iWrongGuess[0] + "  BLACK: Correct: " + iRightGuess[1] + ", Failed: " + iWrongGuess[1]);
		System.out.println("Method play about to be over. Play finished. ");
		
		if ((lev[0] != PLAYER) && (lev[1] != PLAYER))
		{
			// all simulated games are dumped
			String s0, s1;
			if (lev[0] >= 0) s0 = ""+lev[0];
			else s0 = "z";
			if (lev[1] >= 0) s1 = ""+lev[1];
			else s1 = "z";
			
			String sFile = "AskoChess_Si("+s0+s1+")_";
			
			sFile = sFile + new Timestamp(System.currentTimeMillis()) + ".out";
			String sFile2 = sFile.substring(0,21) + sFile.substring(22,24) + sFile.substring(25,27)+sFile.substring(28,30)+sFile.substring(31,33) + sFile.substring(34,36) + ".out";
			System.out.println("File name <" + sFile2 + ">");
			PrintWriter pw = new PrintWriter(sFile2);
			
			gh.dump_to_file(pw);
			pw.println("Cumulative response times: WHITE: " + lLatency[0] + ", BLACK: " + lLatency[1]);
			pw.close();
		}
		
		String sMess = "";
		String sUndo = "";
		if (iUndoCount > 0) sUndo = " UNDOCOUNT:" + iUndoCount;
		
		if (bCheckMate)
		{
			//if (GUIWINDOW) cw.repaint();
			cui.repaint();
			System.out.println("Final repaint done.");
			
			sMess = "Game finished in Checkmate on move " + (iMove-1) + ". ";
			
			if (lev[iWinner] < 0) 
			{
				if ((alg[1-iWinner] >= movevalue.ALG_ASK_FROM_ENGINE1) && (alg[1-iWinner] <= movevalue.ALG_ASK_FROM_ENGINE_LAST)) sMess = sMess + "You win against Stockfish on level " + (alg[1-iWinner] - movevalue.ALG_ASK_FROM_ENGINE1 + 1) + sUndo;
				else
				{
					int iFicLev = lev[1-iWinner]*2 - 1;
					if (bDeep[1-iWinner]) iFicLev++;
					if (iFicLev < 0) iFicLev = 0;
					
					sMess = sMess + "You win against AskoChess on level " + iFicLev+  sUndo;
				}
			}			
			else
			{
				System.out.println("alg[iWinner]:"+ alg[iWinner]);
				if ((alg[iWinner] >= movevalue.ALG_ASK_FROM_ENGINE1) && (alg[iWinner] <= movevalue.ALG_ASK_FROM_ENGINE_LAST)) sMess = sMess + "Stockfish on level " + (alg[iWinner] - movevalue.ALG_ASK_FROM_ENGINE1 + 1)+ " wins."  + sUndo;
				else
				{
					int iFicLev = lev[iWinner]*2 - 1;
					if (bDeep[iWinner]) iFicLev++;
					if (iFicLev < 0) iFicLev = 0;
					
					sMess = sMess + "AskoChess on level " + iFicLev + " wins."  + sUndo; 
				}
			}
			/*
			cw.setMessage(sMess);
			cw.setTurn(-1);
			if (bMess) cw.displayMsgDialog(sMess);
			cw.repaint();
			*/
			cui.setMessage(sMess);
			cui.setTurn(-1);
			if (bMess) cui.displayMsgDialog(sMess);
			cui.repaint();
			
			if (iWinner == 0) return play.WHITEWIN;
			else return play.BLACKWIN;
		} 
		else if ((bNoMoves) || (iWinner == -1))
		{	
			sMess = "Game has finished in a draw on move " + (iMove-1) +  sUndo + ".";
			//cw.setTurn(-1);
			cui.setTurn(-1);
			
			cui.setMessage(sMess);
			cui.setTurn(-1);
			if (bMess) cui.displayMsgDialog(sMess);
			
			return play.DRAW;
		}
		
		System.out.println("Returning an error in simu. Probably running to 100 moves. ");

		return play.RES_ERROR;
	
	}
	



	
	static void printFENEntry(String entry)
	{

	}
	
	
	private static void printLastFoundFEN(String sLastEMove)
	{

	}
	
	private static void dumpboard(int clr, chessboard cb, String fname) throws Exception
	{
//		PrintWriter fbp = new PrintWriter(fname);
//		if (clr==piece.WHITE) fbp.println("WHITE");
//		else fbp.println("BLACK");
//		cb.dump_to_file(fbp);
//		//fbp.println("MC:" + cb.iMoveCounter);
//		if (clr==piece.WHITE) fbp.println("MC:" + (cb.iMoveCounter+1));
//		else fbp.println("MC:" + (cb.iMoveCounter));
//		fbp.flush();
//		fbp.close();
	}

}