package kaappo.androidchess.askokaappochess;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.io.PrintWriter;

public class play
{
	static final int BLACKWIN = 1;
	static final int DRAW = 2;
	static final int WHITEWIN = 3;
	static final int INTERRRUPT = 4;
	static final int RES_ERROR = 5;
	
	//static boolean USE_MOVELIBRARY = true;
	static boolean USE_LIBMOVES = true;
	
	//static boolean USE_LIBMOVES = false;
	static boolean USE_MOVELIBRARY = false;  // starts to be awfully obsolete. time to rid 160205
	static boolean USE_ENGINEMOVES = true;
	//static boolean USE_ENGINEMOVES = false;
	
	//static final int GAME_LENGTH = 6;    // for finding all move 5 20170406. NOW FOR FULFILLER FROM 3!!!!
	//static final int GAME_LENGTH = 7;    // for finding all move 6 20170406 *****************
	//static final int GAME_LENGTH = 8;    // for finding all move 7 20170406
	//static final int GAME_LENGTH = 9;  // m6 + 2 move 20170828
	static final int GAME_LENGTH = 150;
	
	static final int FEN_ENTRY_MOVELIMIT = 61;
	//static final int M4LEVEL = 7;
	//static final int M4LEVEL = 4;
	static final int M4LEVEL = 5;
	
	static final int FULLFILLER_LEVEL = 3;
	static final int FULLFILLER_LEVEL_MAX = 4;
	
	static int REAL_MOVE_LIMIT = 310;
	//static int REAL_MOVE_LIMIT = 3;
	//static int REAL_MOVE_LIMIT = 1;
	
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
	
	static int UI_TYPE = chess_ui.UI_TYPE_WINDOW;
	//static int UI_TYPE = chess_ui.UI_TYPE_TTY;
	
	static final boolean ANYMOVE_MODE = false;  // true: always pick from ANYMOVE algorihm, false: regular 170425
	
	static PrintWriter pw;
	
	static fulfiller fufi;
	
	public static void main (String args[]) throws Exception
	{
		System.out.println("Starting.");
		
		CMonitor.dumpValues();
		
		int results[] = new int[6];
		
		mlib = new movelibrary();
		mlib.init();
		mlib.setMode(movelibrary.MODE_RANDOM);
		mlib.setSeed(-1);
		
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("simu"))
			{
				System.out.println("There are args...");
				
				String lev[] = args[1].split(":");
				int l0 = new Integer(lev[0]).intValue();
				int l1 = new Integer(lev[1]).intValue();

				int alg0 = 0;
				int alg1 = 0;
				
				if (args.length>2)
				{
					String alg[] = args[2].split(":");
					alg0 = new Integer (alg[0]).intValue();
					alg1 = new Integer (alg[1]).intValue();
				}
				
				boolean bDeep0 = false;
				boolean bDeep1 = false;
				
				if (args.length>3)
				{
					String deepFlags[] = args[3].split(":");
					bDeep0 = deepFlags[0].equalsIgnoreCase("plus");
					bDeep1 = deepFlags[1].equalsIgnoreCase("plus");
				}

				
				long lStart = System.currentTimeMillis();
				
				fufi = new fulfiller();
				
				for (int i=0;i<mlib.size();i++)
				//for (int i=0;i<1;i++)
				{
					mlib.setSeed(i);
					int r = simu(l0,l1,alg0,alg1,bDeep0,bDeep1);
					results[r]++;
					CMonitor.dumpValues();
				}
				long lEnd = System.currentTimeMillis();
				/*
				System.out.println("Done . Results array (blackwin, draw, whitewin):");
				System.out.println("Game parameters: l0:" + l0 + " l1:" + l1 + " alg0:" + alg0 + " alg1:" + alg1);
				for (int r = BLACKWIN; r <= RES_ERROR; r++) System.out.println(r + ": " +results[r]);	
				*/
				System.out.println("----");
				System.out.println("Simulation completed. Duration: " + (lEnd-lStart) / 1000 + " sec");
				System.out.println("WHITE (lev "+l0+", alg " + alg0+", deep "+ bDeep0+ " ):" + results[WHITEWIN]);
				System.out.println("BLACK (lev "+l1+", alg " + alg1+", deep "+ bDeep1+ " ):" + results[BLACKWIN]);
				System.out.println("Draws : " + (results[DRAW]+results[RES_ERROR]) + "  by draw: " + results[DRAW] + ", interrupted: " + results[RES_ERROR]);
				
				CMonitor.dumpValues();
				fufi.dump();
				System.out.println("----");
				//System.out.println("Duration " + (lEnd-lStart) / 1000 + " sec");
				System.exit(0);
				return;
			}
			if (args[0].indexOf("file:") == 0)
			{
				analfile(args[0]);
			}
			
			if ((args[0].indexOf("filesimu:") == 0) || (args[0].indexOf("filesimux:") == 0) || (args[0].indexOf("filesimufen:") == 0))
			{
				String lev[] = args[1].split(":");
				int l0 = new Integer(lev[0]).intValue();
				int l1 = new Integer(lev[1]).intValue();

				int alg0 = 0;
				int alg1 = 0;
				
				if (args.length>2)
				{
					String alg[] = args[2].split(":");
					alg0 = new Integer (alg[0]).intValue();
					alg1 = new Integer (alg[1]).intValue();
				}
				
				boolean bDeep0 = false;
				boolean bDeep1 = false;
				
				if (args.length>3)
				{
					String deepFlags[] = args[3].split(":");
					bDeep0 = deepFlags[0].equalsIgnoreCase("plus");
					bDeep1 = deepFlags[1].equalsIgnoreCase("plus");
				}
				
				String aComp[] = args[0].split(":");
				String fname = aComp[1];
				
				USE_MOVELIBRARY = false;
				USE_LIBMOVES = false;

                USE_ENGINEMOVES = (args[0].indexOf("filesimux:") == 0) || (args[0].indexOf("filesimufen:") == 0);
				
				boolean bFENFlag = false;
				if (args[0].indexOf("filesimufen:") == 0) bFENFlag = true;
				
				int r = filesimu(l0,l1,alg0,alg1, bDeep0, bDeep1, fname, bFENFlag);
				
				System.exit(0);
			}
			
			if (args[0].indexOf("compare") == 0)
			{
				compare (args);
			}
			
			if (args[0].indexOf("replay") == 0)
			{
				replay (args);
			}
			
			if (args[0].indexOf("rpl_lib") == 0)
			{
				replay_lib (args);
			}
			
			if (args[0].indexOf("dump") == 0)
			{
				justdump (args);
			}
			
			if (args[0].indexOf("findlibs") == 0)
			{
				findlibs (args);
			}
			
			if (args[0].indexOf("tty") == 0)
			{
				UI_TYPE = chess_ui.UI_TYPE_TTY;
			}
			
			if (args[0].indexOf("startfile:") == 0)
			{
				String [] argComp=args[0].split(":");
				sStartFile=argComp[1];
				USE_MOVELIBRARY = false;
				USE_LIBMOVES = false;
			}
			
			if (args[0].indexOf("mmt") == 0)
			{
				mmtest (args);
			}
			
			
		}
		
		System.out.println("DBG151011: A");
		cb = new chessboard();
		if (sStartFile == null) cb.init();
		else cb.init_from_file(sStartFile);
		System.out.println("DBG151011: B");
		
		//if (GUIWINDOW) cw = new chesswindow (100,100,500,550,50);
		//else cw = new ttyui();
		cui = new chess_ui(UI_TYPE, cb);	
		
		/*
		cw.updateData(cb);
		cw.setMessage("Start new game from menu Play->New Game");
		cw.setTurn(-1);
		cw.show();
		*/
		cui.updateData(cb);
		cui.setMessage("Start new game from menu Play->New Game");
		cui.setTurn(-1);
		cui.show();
		
		if ((args.length > 0) && (args[0].equals("noblood"))) CMonitor.setNoBlood(true);
		else CMonitor.setNoBlood(false);
		
		while (true)
		{
			//String inStr = cw.getMove();
			String inStr = cui.getMove();
			System.out.println("Command:"+inStr);
			if (inStr.indexOf("PLAY:") == 0)
			{
				int lev[] = new int[2];
				int alg[] = new int[2];
				boolean bDeep[] = new boolean[2];
				
				String inpieces[] = inStr.split(":");
				lev [0] = new Integer(inpieces[1]).intValue();
				lev [1] = new Integer(inpieces[2]).intValue();
				
				int iAlgpick = new Integer(inpieces[3]).intValue();
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
				//System.exit(0);
				playgame (lev,true, alg, bDeep);
			}
		}
		

		
	}
	
	static int playgame (int lev[], boolean bMess, int alg[], boolean bDeep[]) throws Exception
	{
		PrintWriter pwEmoves;
		
		
		System.out.println("playgame called: " +lev[0] +"," + lev[1]+ "  Deepflags: " + bDeep[0] + "," + bDeep[1] + " .. cb.iFileCol = " + cb.iFileCol + "algs: " + alg[0] + "," + alg[1]);
		System.out.println("REAL_MOVE_LIMIT:"+REAL_MOVE_LIMIT);
		//System.exit(0);
		
		int iMove = 1;
		System.out.println("DBG160307: playgame:" + cb.iMoveCounter);
		if (cb.iMoveCounter != 0)
		{
			iMove = cb.iMoveCounter;
		}
	
		pwEmoves = new PrintWriter(new BufferedWriter(new FileWriter("ermoves.out", true)));
		pwEmoves.println("--NEW:");
		pwEmoves.flush();
	
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
		
		openings o = new openings();
		
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
						//System.exit(0);
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
								sLastEMove = new String(cb.FEN());
								pwEmoves.println("SUCC:"+sLastEMove + ":"+sLibMove);
								pwEmoves.flush();
								
							}
							else
							{
								System.out.println("LIBMOVE NOT FOUND. FAIL BY:"+cb.FEN());
								pwEmoves.println("FAIL:"+cb.FEN());
								pwEmoves.flush();
								pwEmoves.close();
							}
							//System.exit(0);
						}
					}
					
					
					if ((iMove == M4LEVEL) && (alg[clr] != movevalue.ALG_ASK_FROM_ENGINE_RND))
					{
						PrintWriter p4w = new PrintWriter(new BufferedWriter(new FileWriter("m4log.txt", true)));
						//p4w.println(cb.FEN());
						String sOut = cb.FEN();
						sOut = sOut.substring(0,sOut.length()-2).trim();
						sOut = sOut + " " + M4LEVEL;
						p4w.println(sOut);
						p4w.close();
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
						//System.exit(0);
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
						//System.exit(0);
						
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
									System.exit(0);
								}
								*/
							}
							iRealMoveCount++;
							System.out.println("Real move being done: iRealMoveCount: " + iRealMoveCount);
							//System.exit(0);
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
												//if (iRetryCount > 0) System.exit(0);
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
						//System.exit(0);
						
						
						
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
							
							System.exit(0);
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
								System.exit(0);
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
									//System.exit(0);
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
										//System.exit(0);
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
											
											System.exit(0);
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
					

					if (bGameOn) 
					{
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
				//System.exit(0);
				//cb.dumpCoverages();
				//cb.dumpProtThreat();
				pKing = cb.locateKing(1-clr);
				if (pKing == null)
				{
					System.out.println("No king on board any more");
					System.exit(0);
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
		//System.exit(0);
		
		return play.RES_ERROR;
	
	}
	
	static int simu(int l0, int l1, int alg0, int alg1, boolean bDeep0, boolean bDeep1) throws Exception
	{
		int lev[] = new int[2];
		lev[0] = l0;
		lev[1] = l1;
		int alg[] = new int[2];
		alg[0] = alg0;
		alg[1] = alg1;
		boolean bDeep[] = new boolean[2];
		bDeep[0] = bDeep0;
		bDeep[1] = bDeep1;
		
		String sAlg[] = new String[2];
		sAlg[0] = "";
		sAlg[1] = "";
		
		cb = new chessboard();
		cb.init();

		/*
		if (cw == null) cw = new chesswindow (100,100,500,550,50);
		cw.updateData(cb);
		cw.setLastMoveVector(null);
		cw.setMessage("Starting new game.");
		cw.show();  */

		if (cui == null) cui = new chess_ui(UI_TYPE, cb);
		cui.updateData(cb);
		cui.setLastMoveVector(null);
		cui.setMessage("Starting new game.");
		cui.show();
		
		if ((alg0 == movevalue.ALG_ASK_FROM_ENGINE_RND) || (alg1 == movevalue.ALG_ASK_FROM_ENGINE_RND)) 
		{
			REAL_MOVE_LIMIT = 1;
		//System.exit(0);
		}
		
		CMonitor.setTimeLimCtl(true);   // $$$ 160315, repro bug bloss47.dat
		CMonitor.setUrgency(0);
		
		int iRet = playgame (lev,false, alg, bDeep);
		return iRet;
	}
	
	static int filesimu(int l0, int l1, int alg0, int alg1, boolean bDeep0, boolean bDeep1, String fname, boolean bFENFlag) throws Exception
	{
		int lev[] = new int[2];
		lev[0] = l0;
		lev[1] = l1;
		int alg[] = new int[2];
		alg[0] = alg0;
		alg[1] = alg1;
		boolean bDeep[] = new boolean[2];
		bDeep[0] = bDeep0;
		bDeep[1] = bDeep1;
		
		String sAlg[] = new String[2];
		sAlg[0] = "";
		sAlg[1] = "";
		
		if (bFENFlag)
		{
			cb = new chessboard();
			boolean br = cb.init_from_FEN(fname);
			cb.iMoveCounter++;
			System.out.println("play.filesimu(): by FEN, iMoveCounter: " + cb.iMoveCounter);
		}
		else
		{
			cb = new chessboard();
			boolean br = cb.init_from_file(fname);
		}
		
		/*
		if (cw == null) cw = new chesswindow (100,100,500,550,50);
		cw.updateData(cb);
		cw.setLastMoveVector(null);
		cw.setMessage("Starting new game.");
		cw.show();
		*/
		if (cui == null) cui = new chess_ui(UI_TYPE, cb);
		cui.updateData(cb);
		cui.setLastMoveVector(null);
		cui.setMessage("Starting new game.");
		cui.show();

		mlib.setSeed(-2);
		
		if ((alg0 == movevalue.ALG_ASK_FROM_ENGINE_RND) || (alg1 == movevalue.ALG_ASK_FROM_ENGINE_RND)) 
		{
			REAL_MOVE_LIMIT = 1;
			System.out.println("Real move limit 1");
			//System.exit(0);
		}
		
		int iRet = playgame (lev,false, alg, bDeep);
		return iRet;
	}
	
	static void analfile(String arg) throws Exception
	{
		String aComp[] = arg.split(":");
		
		chessboard cb = new chessboard();
		chessboard cb2 = null;
		
		boolean br = cb.init_from_file(aComp[1]);
		
		cb.iMoveCounter = 1;
		cb.iMoveCount = cb.iFileCol;
		
		int iLev = new Integer(aComp[2]).intValue();
		
		String sPar = "";
		System.out.println("aComp.length:" + aComp.length);
		if (aComp.length>=3) sPar = aComp[3]; 
		
		int iAlg = 0;
		if (aComp.length>=4)
		{
			iAlg = new Integer(aComp[4]).intValue();
		}
		boolean bDeeper = false;
		if (aComp.length>=5)
		{
			if (aComp[5].equalsIgnoreCase("plus")) bDeeper = true;
		}
		if (aComp.length>6)
		{
			if (aComp[6].indexOf("draw=") != -1) 
			{
				System.out.println("Draw warning: " + aComp[6]);
				String sDrawMove=aComp[6].substring(5).toUpperCase().trim();
				System.out.println("sDrawMove:"+sDrawMove);
				CMonitor.iMonLevel=iLev;
				CMonitor.sDrawMove = sDrawMove;
				//System.exit(0);
			}
		}
		
		System.out.println("CB LOADED FILE COL = " + cb.iFileCol);
		if ((cb.iFileCol != piece.WHITE) && (cb.iFileCol != piece.BLACK)) System.exit(0);
		
		/*if (cw == null) cw = new chesswindow (100,100,500,550,50);
		cw.updateData(cb);
		cw.show();
		*/
		if (cui == null) cui = new chess_ui(chess_ui.UI_TYPE_WINDOW, cb);
		cui.updateData(cb);
		cui.show();
		
		cb.dump();
		cb.dumpCoverages();
		cb.dumpProtThreat();
		
		//movevalue mmval = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
		movevalue mmval = new movevalue("");
		mmval.setbase(cb.iFileCol);
		cb.redoVectorsAndCoverages(1-cb.iFileCol,iAlg);
		
		chess_ui.setAnalysisStart(iLev, cb.iFileCol);
		movevalue mv2 = new movevalue("");
		mv2.setBalancesFromBoard(cb,cb.iFileCol,iAlg);
		chess_ui.setAnalysisStartMval(mv2);
		
		mostore mos = new mostore();
		
		CMonitor.setTimeLimCtl(true);   // $$$ 160315, repro bug bloss47.dat
		CMonitor.setUrgency(0);
		
		if (iLev == 0) hcwinner.bHcwEnabled = true;
		
		cb2 = cb.findAndDoBestMove(cb.iFileCol,iLev,mmval,iAlg,true,null,null,false, null,null,null,null,chessboard.CB_MAXTIME,true,null, mos);
		
		if (cb2 != null) 
		{
			cb2.dump();
			cb2.dumpCoverages();
			cb2.dumpProtThreat();
			System.out.println(cb2.lastmoveString() + " mmval: " + mmval.dumpstr(iAlg));
		
			Vector vTestDir = cb2.getTestDir();
			if (vTestDir != null)
			{
				System.out.println("DBG150924:vTestDir dump");
				String sRight = cb2.lastmoveString();
				for (int i=0; i<vTestDir.size();i++)
				{
					String sDir = (String)vTestDir.elementAt(i);
					sDir = sDir.trim();
					String sDirComp[] = sDir.split(":");
					String sLev = sDirComp[0];
					int iResLev = new Integer(sLev.replaceAll("L","")).intValue();
					
					String sOpts[]=sDirComp[1].split(" ");
					System.out.println("sLev:"+sLev);
					int iSuccCrit=0;
					int iFailCrit=0;
					int iSucc=0;
					int iFail=0;
					if (iLev == iResLev) for (int j=0;j<sOpts.length;j++)
					{
						if (sOpts[j].indexOf("!") == -1) iSuccCrit++;
						else iFailCrit++;
						
						System.out.println("sOpts[j]:"+sOpts[j]);
						if (sRight.equals(sOpts[j])) iSucc++;
						if (new String("!"+sRight).equals(sOpts[j])) iFail++;
					}
					System.out.println("iFailCrit:"+iFailCrit + " iFail:" + iFail);
					if ((iSuccCrit > 0) && (iSucc == 0)) System.out.println("TESTRESULT " + arg + " FAILURE (A) move:" + sRight);
					if ((iFailCrit > 0) && (iFail == 0)) System.out.println("TESTRESULT " + arg + " SUCCESS (B) move:" + sRight);
					if (iSucc > 0) System.out.println("TESTRESULT " + arg + " SUCCESS (C) move:" + sRight);
					if (iFail > 0) System.out.println("TESTRESULT " + arg + " FAILURE (A) move:" + sRight);
				}	
			}
		
		}
		else System.out.println("No move, val=" + mmval.dumpstr(iAlg));
		
		
		//System.out.println("sPar:"+sPar);
		/*
		chessboard cb3 = cb.copy();
		cb3.redoVectorsAndCoverages(1-cb.iFileCol,iAlg);
		movevalue mv3 = new movevalue("");
		chessboard cb4=hcwinner.hcwinnerFAD(cb3,cb.iFileCol,mv3);
		if (cb4 != null) cb4.dump();
		*/
		
		if (sPar.equalsIgnoreCase("stay")) while (true) { Thread.sleep(1000); }
		else if (sPar.equalsIgnoreCase("quit")) System.exit(0);
		
	}
	
	static void compare (String par[])
	{
		System.out.println("Reading first board.");
		chessboard cb1 = new chessboard(); 
		cb1.init_from_file(par[1]);
		System.out.println("Reading second board.");
		chessboard cb2 = new chessboard(); 
		cb2.init_from_file(par[2]);
		
		System.out.println("Starting file comparison : " + par[1] + " " + par[2]);
		
		//movevalue mv1 = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
		movevalue mv1 = new movevalue("");
		//movevalue mv2 = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
		movevalue mv2 = new movevalue("");
		
		if (cb1.iFileCol != cb2.iFileCol)
		{
			System.out.println("File boards have different turn indicators.");
			System.exit(0);
		}
		
		int iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
		
		cb1.redoVectorsAndCoverages(cb1.iFileCol, iAlg);
		cb2.redoVectorsAndCoverages(cb1.iFileCol, iAlg);
		
		mv1.setBalancesFromBoard(cb1,cb1.iFileCol,0);
		mv2.setBalancesFromBoard(cb2,cb1.iFileCol,0);
		
		System.out.println("mv1: " + mv1.dumpstr(0));
		System.out.println("mv2: " + mv2.dumpstr(0));
		
		if (par.length<=3)
		{
		
			System.out.println("CLR  ALG  TURN   BETTER");
			for (int clr = 0; clr < 2; clr++)
				for (int alg = 0; alg <= 13; alg++)
					for (int turn = 0; turn < 2; turn ++)
			{
				System.out.print(clr + "    " + alg + "  " + turn);
				if (mv1.isBetterthan(mv2,clr,alg,turn,0)) System.out.println ("  " + par[1]);
				else if (mv2.isBetterthan(mv1,clr,alg,turn,0)) System.out.println ("  " + par[2]);
				else System.out.println("   equal");
				
			}
		}
		else
		{
			String sComp[] = par[3].split(":");
			int clr = new Integer(sComp[0]).intValue();
			int alg = new Integer(sComp[1]).intValue();
			int turn = new Integer(sComp[2]).intValue();
			System.out.println("Prec anal here! CLR:" + clr + " Alg = " +alg + " Turn = " + turn);
			
			System.out.println("mv1: " + mv1.dumpstr(alg));
			System.out.println("mv2: " + mv2.dumpstr(alg));
			
			boolean bIsBetter1 = mv1.isBetterthan(mv2,clr,alg,turn,0);
			boolean bIsBetter2 = mv2.isBetterthan(mv1,clr,alg,turn,0);
			System.out.println("First is better is: " + bIsBetter1);
			System.out.println("Second is better is: " + bIsBetter2);
			cb1.dump();
			cb2.dump();
			
			System.exit(0);
		}
	}
	
	static void replay (String par[])
	{	
		System.out.println("Replay starts");
		
		//String sReplay ="E2E4;B8C6;D2D4;G8F6;B1C3;E7E6;C1G5;C6D4;D1D4;E6E5;D4E5;F8E7;G5F6;G7F6;E5F5;E8G8;A1B1;A8B8";
		String sPreCastle = "E5F5";
		String sCastle = "E8G8";
		String sReplay2 = "";
		String sReplayFlip = "E4F4;E1G1;A8B8;A1B1";
		int FLIPVAR = 1;
		int iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
		
		/*
		String sReplay = "E2E4;C7C5;G1F3;D7D6;D2D4;C5D4;F3D4;G8F6;B1C3;A7A6;F1C4;D8A5;B2B4;A5B4;C4F7;E8F7;D1D3;A6A5;E1G1;A8A7;A1B1";
		String sPreCastle = "A6A5";
		String sCastle = "E1G1";
		String sReplayFlip = "A3A4;E8G8;A1A2;A8B8";
		int FLIPVAR = 0;
		*/
		
		//String sReplay ="E2E4;D7D5;F1B5;C7C6;B5D3;E7E6;E4E5;G8H6;G1H3;B8D7;D1H5;G7G5;F2F4;D8A5;H5G5;B7B5;E1F2;A7A6;A2A4;D7B6;C2C3;B5B4;C3B4;A5B4;G2G4;B6C4;D3C4;D5C4;G5F6;H6G4;F2F3;G4F6;E5F6;F8H6;A4A5;E8G8;H1G1;G8H8;H3F2;C8D7;H2H4;B4C5;D2D4;C4D3;";
		
		//String sReplay = "B2B4;H7H6;B4B5;C7C5;B5C6";
		String sReplay = "F2F4;G4G3";
		//String sReplay = "F2F4;E4E3";
		
		String sRepComp[] = sReplay.split(";");
		
		cb = new chessboard();
		//cb.init();
		cb.init_from_file("enppin2.dat");
		int iTurn = 0;
		
		chessboard cb_flip = null;
		
		for (int imc = 0; imc < sRepComp.length; imc++)
		{
			iTurn = imc % 2;
			if (iTurn == 0) cb.iMoveCounter++;
			
			if (sRepComp[imc].equals(sPreCastle))
			{
				cb_flip = cb.flip(FLIPVAR, iAlg);
				System.out.println("Flipped CB:");
				cb_flip.dump();
			}
			
			boolean br = cb.domove(sRepComp[imc],iTurn);
			System.out.println("Domove returned");
			cb.valsum[0] = 0;
			cb.valsum[1] = 0;
			cb.redoVectorsAndCoverages(1-iTurn, iAlg);
			
			if (!br)
			{
				System.out.println("Move: " + sRepComp[imc] + "(" + iTurn + ")  failed.");
				System.exit(0);
			}
			System.out.println("Move : " + sRepComp[imc] + "(" + iTurn + ")  successfully done.");
			
			//if (sRepComp[imc].equals(sCastle)) cb.prefixdump("T140710_O_C0MOVE:");
			//else cb.dump();
			//cb.prefixdump("T140710_O_MOVE:");
		}
		
		boolean br = false;
		
		/*
		cb.iMoveCounter++;
		boolean br = cb.domove("A1B1",0);
		if (!br) 
		{
			System.out.println("Move failed (1a)!");
			System.exit(0);
		}
		cb.valsum[0]=0;
		cb.valsum[1]=0;
		cb.redoVectorsAndCoverages();
		cb.prefixdump("T140710_O_C1MOVE:");
		
		br = cb.domove("A8B8",1);
		if (!br) 
		{
			System.out.println("Move failed (1b)!");
			System.exit(0);
		}
		cb.valsum[0]=0;
		cb.valsum[1]=0;
		cb.redoVectorsAndCoverages();
		cb.prefixdump("T140710_O_C2MOVE:");
		*/
		
		//movevalue mv1 = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
		movevalue mv1 = new movevalue("");
		mv1.setBalancesFromBoard(cb,iTurn,16);
		System.out.println(mv1.dumpstr(16));
		

		cb.dump();
		cb.dumpCoverages();
		cb.dumpProtThreat();
		System.out.println();
		
		System.out.println("FLIP DUMP STARTS*************************************");
		
		sRepComp = sReplayFlip.split(";");
		
		for (int imc2 = 0; imc2 < sRepComp.length; imc2++)
		{
			int iT = imc2 %2;
			
			if (FLIPVAR == 1) iTurn = (1-iT);
			else iTurn = iT;
			
			//iTurn = (FLIPVAR - imc2) % 2;
			if (iTurn == 0) cb_flip.iMoveCounter++;
			
			br = cb_flip.domove(sRepComp[imc2],iTurn);
			cb_flip.valsum[0] = 0;
			cb_flip.valsum[1] = 0;
			cb_flip.redoVectorsAndCoverages(1-iTurn, iAlg);
			
			if (!br)
			{
				System.out.println("Move: " + sRepComp[imc2] + "(" + iTurn + ") failed.");
				System.exit(0);
			}
			System.out.println("Move : " + sRepComp[imc2] + "(" + iTurn + ")  successfully done.");
			
			//if (sRepComp[imc].equals(sCastle)) cb.prefixdump("T140710_O_C0MOVE:");
			//else cb.dump();
			cb_flip.prefixdump("T140710_F_MOVE:", chessboard.DUMPMODE_FULL);
		}
		
		System.exit(0);
		
		/*
		br = cb_flip.domove("E4F4",1);
		if (!br) 
		{
			System.out.println("Move failed (0)!");
			System.exit(0);
		}
		cb_flip.valsum[0] = 0;
		cb_flip.valsum[1] = 0;
		cb_flip.redoVectorsAndCoverages();
		
		cb_flip.iMoveCounter++;
		br = cb_flip.domove("E1G1",0);
		if (!br) 
		{
			System.out.println("Move failed (2)!");
			System.exit(0);
		}
		
		cb_flip.valsum[0] = 0;
		cb_flip.valsum[1] = 0;
		cb_flip.redoVectorsAndCoverages();
		
		cb_flip.prefixdump("T140710_F_C0MOVE:");
		
		br = cb_flip.domove("A8B8",1);
		if (!br) 
		{
			System.out.println("Move failed (3)!");
			System.exit(0);
		}
		cb_flip.valsum[0] = 0;
		cb_flip.valsum[1] = 0;
		cb_flip.redoVectorsAndCoverages();
		cb_flip.prefixdump("T140710_F_C1MOVE:");

		cb_flip.iMoveCounter++;	
		br = cb_flip.domove("A1B1",0);
		if (!br) 
		{
			System.out.println("Move failed (4)!");
			System.exit(0);
		}
		cb_flip.valsum[0] = 0;
		cb_flip.valsum[1] = 0;
		cb_flip.redoVectorsAndCoverages();
		cb_flip.prefixdump("T140710_F_C2MOVE:");
		*/
		
		System.exit(0);
	}
	
	static void replay_lib (String par[]) throws Exception
	{
		System.out.println("Replay_lib starts");
		int ACCEPT_LEVEL = 70;
		
		HashMap hm;
		
		int iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
		
		openings o = new openings();
		int iTurn = -1;
		
		boolean bDoEngineCheck = false;
		
		PrintWriter pwr = new PrintWriter ("openings2.opn");
		
		hm = new HashMap();
		
		//for (int i=0;i<15;i++)
		for (int i=0;i<o.getSize();i++)
		if (o.isValid(i))
		{
			System.out.println("=============================================");
			System.out.println("STARTING GAME : " + i);
			cb = new chessboard();
			cb.init();
			cb.redoVectorsAndCoverages(piece.WHITE, iAlg);
			
			int oel = o.getOeLength(i);
			
			iTurn = -1;
			
			boolean bBadBlack = false;
			boolean bBadWhite = false;
			
			for (int im=1;im<=oel;im++)
			{
				String sWm = o.getMove(i,im,piece.WHITE);
				String sWb = o.getMove(i,im,piece.BLACK);
				System.out.println("DOING WHITE MOVE: " + sWm);
				cb.domove_bylib(sWm,piece.WHITE);
				cb = cb.copy();
				cb.redoVectorsAndCoverages(piece.BLACK, iAlg);
				iTurn = 1;
				cb.dump();
				String sMove = null;
				if (bDoEngineCheck) sMove = engine.getMoveByAlg(engine.sEnginePerAlg(movevalue.ALG_ASK_FROM_ENGINE10), cb.FEN(),movevalue.ALG_ASK_FROM_ENGINE10);
				int iScore = engine.iLastScore;
				System.out.println(i+":"+im+":Score:" + iScore);
				if (Math.abs(iScore) > ACCEPT_LEVEL) 
				{
					if (iScore < 0) bBadBlack = true;
					if (iScore > 0) bBadWhite = true;
					System.out.println("*** Score exceeds limit ***");
				}
				
				String sNewFen = cb.FEN();
				sNewFen = sNewFen.substring(0,sNewFen.length()-2).trim();
				sNewFen = sNewFen + " " + im;
				System.out.println("FEN ("+im+") :" + sNewFen);
				
				hm.put(sNewFen,"B");
				
				if (sWb != null)
				{
					System.out.println("DOING BLACK MOVE: " + sWb);
					cb.domove_bylib(sWb,piece.BLACK);
					cb = cb.copy();	
					cb.redoVectorsAndCoverages(piece.WHITE,iAlg);
					iTurn = 0;
					cb.dump();
					if (bDoEngineCheck) sMove = engine.getMoveByAlg(engine.sEnginePerAlg(movevalue.ALG_ASK_FROM_ENGINE10), cb.FEN(),movevalue.ALG_ASK_FROM_ENGINE10);
					iScore = engine.iLastScore;
					System.out.println(i+":"+im+":Score:" + iScore);
					if (Math.abs(iScore) > ACCEPT_LEVEL) 
					{
						if (iScore > 0) bBadBlack = true;
						if (iScore < 0) bBadWhite = true;
						System.out.println("*** Score exceeds limit ***");
					}
				}
				sNewFen = cb.FEN();
				sNewFen = sNewFen.substring(0,sNewFen.length()-2).trim();
				sNewFen = sNewFen + " " + (im+1);
				System.out.println("FEN ("+im+") :" + sNewFen);
				hm.put(sNewFen,"W");
				
				
			}
		
			//movevalue mv1 = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
		
			movevalue mv1 = new movevalue("");
			mv1.setBalancesFromBoard(cb,iTurn,0);
		
			System.out.println("mv" + i + ": " + mv1.dumpstr(0));
			
			System.out.print("mvs" +  i+": "+ o.dumpGame(i)+ " : " + mv1.dumpstr(0,movevalue.DUMPMODE_SHORT)+ " ");
			
			/*
			if ((mv1.bWhiteCheckMate) || (mv1.bWhiteChecked) || (mv1.bWhiteBlocked) || (mv1.iPieceBalance < 0) || (mv1.iPieceBalCorrWhite < 0) || (mv1.iPieceBalCorrBlack <0)) System.out.print("NOWHITE ");
			if ((mv1.bBlackCheckMate) || (mv1.bBlackChecked) || (mv1.bBlackBlocked) || (mv1.iPieceBalance > 0) || (mv1.iPieceBalCorrWhite > 0) || (mv1.iPieceBalCorrBlack > 0)) System.out.print("NOBLACK ");
			System.out.println();
			*/
			String sAdd = "";
			if ((mv1.bWhiteCheckMate) || (mv1.bWhiteChecked) || (mv1.bWhiteBlocked) || (mv1.iPieceBalance < 0) || (mv1.iPieceBalCorrWhite < 0) || (mv1.iPieceBalCorrBlack <0) || (bBadWhite)) sAdd = sAdd+" NOWHITE ";
			if ((mv1.bBlackCheckMate) || (mv1.bBlackChecked) || (mv1.bBlackBlocked) || (mv1.iPieceBalance > 0) || (mv1.iPieceBalCorrWhite > 0) || (mv1.iPieceBalCorrBlack > 0) || (bBadBlack)) sAdd = sAdd+" NOBLACK ";
			
			if (bBadWhite || bBadBlack) sAdd = sAdd+ "BY ENG ";
			
			System.out.println(sAdd);
			String sOut = sAdd + " " + o.dumpEntry(i) ; 
			pwr.println(sOut);
			pwr.flush();
			System.out.println("mv" + i + ": moveindex lengths: " + cb.iMoveIndexLength(piece.WHITE) +"," + cb.iMoveIndexLength(piece.BLACK));
		
			System.out.println("GAME " + i + " START SIMULATION OVER");
			System.out.println("=============================================");
			//System.exit(0);
			
			
		}
		pwr.close();
		
		Set set = hm.entrySet();
		Iterator i = set.iterator();
		System.out.println("Unique FEN DUMP:");
		int c = 0;
		while(i.hasNext())
		{
				Map.Entry me = (Map.Entry)i.next();
				System.out.println("java anyokmove " + (char)34 + me.getKey() + (char)34);
				c++;
		}
		System.out.println("FEN dump over. " + c+ " FEN values");
			
		System.exit(0);
	}
	
	static void justdump (String par[])
	{
		int iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
		
		for (int i=1;i<par.length; i++)
		{
			chessboard cb1 = new chessboard(); 
			cb1.init_from_file(par[i]);
			System.out.println("=========================================");
			//System.out.println("DBG151005: Before redo .");
			cb1.redoVectorsAndCoverages(cb1.iFileCol,iAlg);
			//System.out.println("DBG151005: Redo done.");
			
			
			cb1.prefixdump("",chessboard.DUMPMODE_FULL);
			//movevalue mv1 = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
			movevalue mv1 = new movevalue("");
			//System.out.println("DBG 141225: before setBalancesFromBoard");
			mv1.setBalancesFromBoard(cb1,cb1.iFileCol,iAlg);
			//System.out.println("DBG 141225: after setBalancesFromBoard");
			System.out.println("White Moves:");
			//boolean b = cb1.miWhiteMoveindex.setRiskBits(cb1);
			//b = cb1.miBlackMoveindex.setRiskBits(cb1);
			cb1.miWhiteMoveindex.sortedcopy().dump(cb1.miBlackMoveindex.sortedcopy());
			System.out.println("Black Moves:");
			cb1.miBlackMoveindex.sortedcopy().dump(cb1.miWhiteMoveindex.sortedcopy());
			System.out.println("Movevalue: " + mv1.dumpstr(iAlg));
			cb1.dumpCoverages();
			/*
			System.out.println("Equally good moves:");
			System.out.println("WHITE: ");
			cb1.miWhiteMoveindex.sortedcopy().getEquallyGoodMoves(null,cb1.miBlackMoveindex.sortedcopy()).dump(cb1.miBlackMoveindex.sortedcopy());
			System.out.println("Any WHITE MOVE: " + cb1.miWhiteMoveindex.sortedcopy().getEquallyGoodMoves(null,cb1.miBlackMoveindex.sortedcopy()).getAnyMove().moveStr());
			System.out.println("BLACK: ");
			cb1.miBlackMoveindex.sortedcopy().getEquallyGoodMoves(null,cb1.miWhiteMoveindex.sortedcopy()).dump(cb1.miWhiteMoveindex.sortedcopy());
			System.out.println("Any BLACK MOVE: " + cb1.miBlackMoveindex.sortedcopy().getEquallyGoodMoves(null,cb1.miWhiteMoveindex.sortedcopy()).getAnyMove().moveStr());
			*/
			System.out.println(cb1.FEN());	
			System.out.println("MidPawnOpenings: " + cb1.bMidPawnOpenings(cb1.iFileCol));
			System.out.println("PawnPressureOpenings: " + cb1.bPawnPressureOpenings(cb1.iFileCol));
			System.out.println("FianchettoPrepOpenings: " + cb1.bFianchettoPrepOpenings(cb1.iFileCol));
			System.out.println("BishopE3Openings: " + cb1.bBishopE3Openers(cb1.iFileCol));
			System.out.println("F2StepOpeners: " + cb1.bF2StepOpeners(cb1.iFileCol));
			System.out.println("PawnFrontOpeners: " + cb1.bPawnFrontOpeners(cb1.iFileCol));
			System.out.println("bBackRowRookOpeners: " + cb1.bBackRowRookOpeners(cb1.iFileCol));
			System.out.println("bKnightToMiddleMoves: " + cb1.bKnightToMiddleMoves(cb1.iFileCol));
			System.out.println("bQueenFirstMoves: " + cb1.bQueenFirstMoves(cb1.iFileCol));
			System.out.println("C2StepOpeners: " + cb1.bC2StepOpeners(cb1.iFileCol));
			System.out.println("BishopF4Openings: " + cb1.bBishopF4Openers(cb1.iFileCol));
			System.out.println("--");
			System.out.println("Moved pieces from start:"+cb1.iMovedPiecesFromStart());
		}
		
		System.out.println("Dump done.");
		System.exit(0);
	}
	
	static void findlibs(String par[]) throws Exception {
		int iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
		System.out.println("Findlibs starts."+par[1]);
		chessboard cb1 = new chessboard(); 
		cb1.init_from_file(par[1]);
		System.out.println("=========================================");
		
		cb1.redoVectorsAndCoverages(cb1.iFileCol,iAlg);
		System.out.println(cb1.FEN());
		cb1.prefixdump("",chessboard.DUMPMODE_SHORT);
		moveindex mi;
		if (cb1.iFileCol == piece.WHITE) mi = cb1.miWhiteMoveindex;
		else mi=cb1.miBlackMoveindex;
		//mi.dump();
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			System.out.print(m.moveStr() + " ");
			cb2 = cb1.copy();
			cb2.domove(m.moveStrCaps(),m.p.iColor);
			//cb2.dump();
			String sFEN = cb2.FEN();
			//System.out.println(sFEN);
			String sResp = enginerunner.getMove(sFEN);
			if (sResp != null) System.out.println(sResp);
			else System.out.println();
			//System.exit(0);
		}
		anyokmove.db_anymovelistdump(cb1.FEN());
		
		System.exit(0);
	}
	
	
	static void printFENEntry(String entry)
	{
		
		try
		{
			if (pw == null)
			{
				pw = new PrintWriter(new BufferedWriter(new FileWriter("sfinput.txt", true)));
			}
			//System.out.println("regbest:printdbg a");
			pw.println(entry);
			pw.close();
			pw = null;
		}
		catch (IOException e) 
		{
			//exception handling left as an exercise for the reader
			System.out.println("IOException at play.printFENEntry() (A)");
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
	
	
	static void printLastFoundFEN(String sLastEMove)
	{
		try
		{
			if (pw == null)
			{
				pw = new PrintWriter(new BufferedWriter(new FileWriter("blanaprev.out", true)));
			}
			//System.out.println("regbest:printdbg a");
			pw.println("java blana fen " + (char)34 + sLastEMove + (char)34+ " Q3");
			pw.close();
			pw = null;
		}
		catch (IOException e) 
		{
			//exception handling left as an exercise for the reader
			System.out.println("IOException at play.printFENEntry() (A)");
			System.out.println(e.getMessage());
			System.exit(0);
		}
	}
	
	static void dumpboard(int clr, chessboard cb, String fname) throws Exception
	{
		PrintWriter fbp = new PrintWriter(fname);
		if (clr==piece.WHITE) fbp.println("WHITE");
		else fbp.println("BLACK");
		cb.dump_to_file(fbp);
		//fbp.println("MC:" + cb.iMoveCounter);
		if (clr==piece.WHITE) fbp.println("MC:" + (cb.iMoveCounter+1));
		else fbp.println("MC:" + (cb.iMoveCounter));
		fbp.flush();
		fbp.close();	
	}
	
	static void mmtest (String par[])
	{
		int iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
		int iLev = new Integer(par[2]).intValue();
		
		long lStart = System.currentTimeMillis();
		
		Vector v = new Vector();
		chessboard cb1 = new chessboard(); 
		cb1.init_from_file(par[1]);
		System.out.println("About to start mmtest");
		mmmate mm = new mmmate(cb1);
		mm.analyze(cb1.iFileCol, 0,iLev, "", v);
		mm.analyzeMateCands(cb1.iFileCol,v);
			//cb1.dump();
		
		long lEnd = System.currentTimeMillis();
		System.out.println("MMMATE TEST DURATION: " + (lEnd-lStart) + " msec.");
		System.exit(0);
	}
}