package kaappo.androidchess.askokaappochess;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;

import java.util.*;

public class chessboard implements Serializable
{
	static final int DUMPMODE_SHORT = 1;
	static final int DUMPMODE_LONG = 2;
	static final int DUMPMODE_FULL = 3;
	
	static final int CB_MAXTIME = 10800;
	
	static final int PARALLEL_LEVEL = 2;	
	//static final int PARALLEL_LEVEL = 3;	
	//static final int PARALLEL_LEVEL = 4;	
	//static final int PARALLEL_LEVEL = 5;
	
	static final int CB_NPS_TARGETRATE = 22500;
	static final int MOSTORE_SORT_TIMEOUT = 500;
	
	static final boolean SHOWZEROBALANCES=false;
	
	static final int NALI_HORI = 1;
	static final int NALI_VERT = 2;
	static final int NALI_DIAG = 3;
	static final int NALI_HORI_VERT = 4;
	static final int NALI_VERT_DIAG = 5;
	static final int NALI_NONE = 6;
	
	piece blocks[][];
	
	boolean bWhiteCoverage[][];
	boolean bBlackCoverage[][];
	
	int iWhiteStrike[][];
	int iBlackStrike[][];
	
	//boolean bWhiteCoverageXP[][];  // "name for X-pawn" to cover blocks covered by 
	//boolean bBlackCoverageXP[][];  // pawns making a capture, not moving fwd
	
	Vector vWhites;
	Vector vBlacks;
	
	int iWhiteCovered;
	int iBlackCovered;
	
	Vector lm_vector;
	
	int iValSumWhite;
	int iValSumBlack;
	
	int iWhitePawnAdvance;
	int iBlackPawnAdvance;
	
	boolean bInsideCastling;
	
	int iMoveCounter;   // rounds (i.e. two moves per round)
	
	boolean bWasChecked = false;
	boolean bNoMoves = false;
	boolean bIsCheckMate = false;
	
	boolean bWhiteKingLost = false;
	boolean bBlackKingLost = false;
	
	int iWhiteUnprotThreat;
	int iBlackUnprotThreat;
	
	int iWhiteDoublePawns;
	int iBlackDoublePawns;
	
	int iWhiteCenterCtrlPts;
	int iBlackCenterCtrlPts;
	
	int iFileCol = -1;
	
	boolean bWhiteKingThreat = false;
	boolean bBlackKingThreat = false;
	
	int iMaxWhiteThreat;
	int iMaxBlackThreat;
	
	int iMaxWhiteThrProtBal;
	int iMaxBlackThrProtBal;
	
	int iWhiteKingCtrlBlks;
	int iBlackKingCtrlBlks;
	
	int iWhiteEarlyGamePenalty;
	int iBlackEarlyGamePenalty;
	
	int iWhiteKingSpace;
	int iBlackKingSpace;
	
	int iWhiteFreePawnPoints;
	int iBlackFreePawnPoints;
	
	int iWhiteFreePawnCtrlPoints;   // 2 for ctrling pawn or next block, 1 for each block all the way
	int iBlackFreePawnCtrlPoints;   // 2 for ctrling pawn or next block, 1 for each block all the way
	
	int iWhitePieceCount[];
	int iBlackPieceCount[];
		
	int valsum[];

	int iWhiteKingPromDest[];  // to capture free pawns
	int iBlackKingPromDest[];
	int iWhiteKingDefDest[];  // to capture free pawns
	int iBlackKingDefDest[];
	
	int iWhitePawnColMin[];
	int iWhitePawnColMax[];
	int iBlackPawnColMin[];
	int iBlackPawnColMax[];
	
	int iWhiteUPPDest[];
	int iBlackUPPDest[];
	
	// 140509 new ideas for movevalue assessments
	boolean bEndGameMode;    // both piecevalue sums < 1016  , more emp for iProtThreatDiff,iPawnAdvBalance,iKingCtrlDiff
	boolean bKillModeQRwK;   // heavy officer(s) against lone king mode, emp for iKingSpaceDiff, calculate optimal king locations iWhiteKingDistance,iBlackKingDistance
	boolean bPawnPromRace;   // race about pawn promotion (sums < 1010, must be at least one pawn)
	
	boolean bWhiteCastled; 
	boolean bBlackCastled; 

	int iWhiteCastlingMove ;
	int iBlackCastlingMove ;
	
	boolean bNoThreadlaunch = false;
	
	king m_kw = null;
	king m_kb = null;
	
	int iWhiteCheckMoves;
	int iBlackCheckMoves;
	
	int iWhiteMoves;
	int iBlackMoves;
	
	int iMaxWhitePawn;
	int iMaxBlackPawn;
	
	moveindex miWhiteMoveindex;
	moveindex miBlackMoveindex;
	
	boolean bWhiteBlocked;
	boolean bBlackBlocked;
	
	boolean bWhiteRiskOn;
	boolean bBlackRiskOn;
	
	int iWhiteQES, iBlackQES;
	
	int iWhiteKCS, iBlackKCS;
	int iWhiteKCSMoves, iBlackKCSMoves;
	
	int iWhiteBCS, iBlackBCS;
	int iWhiteBCSMoves, iBlackBCSMoves;
	
	Vector vWhiteBCTargets, vBlackBCTargets;
	Vector vWhiteBCHOF, vBlackBCHOF;
	
	boolean bWhiteInstWin, bBlackInstWin;
	int iInstWinCorr;
	
	int iWhiteZeroBal, iBlackZeroBal;
	
	int iWhitesInBlackBlock, iBlacksInWhiteBlock;
	
	int iPawnProtPenBal;
	
	int iMoveCount = 0;   // moves, i.e. two moves per round
	
	public int mMaxThreads = 8;
	
	int iWhiteMvLastProm, iBlackMvLastProm;
	
	String sMoveOrder;
	
	int iPromBonWhite, iPromBonBlack;
	int iWhiteKingCtrlFix, iBlackKingCtrlFix;
	
	int iSwapBalance;
	boolean bWhiteBRBishop, bWhiteWRBishop, bBlackBRBishop, bBlackWRBishop;
	
	int iEGSBal;
	
	int iHCBonus;
	
	int iQRKillBal;
	int iHCDrawBonusBal;
	
	Vector vTestDir;
	
	
	public static final int MAX_CHESS_RECURSION_DPTH = 2;
	
	public chessboard()
	{
		blocks = new piece [9][9];	
		bWhiteCoverage = new boolean[9][9];
		bBlackCoverage = new boolean[9][9];
		
		//bWhiteCoverageXP = new boolean[9][9];
		//bBlackCoverageXP = new boolean[9][9];
		
		iWhiteStrike = new int[9][9];
		iBlackStrike = new int[9][9];
		
		iWhiteKingPromDest = new int [9];
		iBlackKingPromDest = new int [9];
		iWhiteKingDefDest = new int [9];
		iBlackKingDefDest = new int [9];
		
		for (int i=0;i<9;i++)
		{
			iWhiteKingPromDest[i] = -1;
			iBlackKingPromDest[i] = -1;
			iWhiteKingDefDest[i] = -1;
			iBlackKingDefDest[i] = -1;
		}
		
		
		
		iWhitePieceCount = new int[piece.ROOK+1];
		iBlackPieceCount = new int[piece.ROOK+1];
		
		iWhiteUPPDest = new int[9];
		iBlackUPPDest = new int[9];
		
		
		valsum = new int[2];
		
		//System.out.println("chessboard()");
		//bWhiteCoverage[1][1] = true;
		bInsideCastling = false;
		
		bWhiteCastled = false; 
		bBlackCastled = false;
		
		iWhiteCastlingMove = 0;
		iBlackCastlingMove = 0;
		
		m_kw = null;
		m_kb = null;
		
		CMonitor.incChessboardInit();
	}
	
	void dbgPrintln(String s)
	{
		//System.out.println(s);
	}
	
	void dbgPrint(String s)
	{	
		//System.out.print()
	}
	
	int piecevalue (int i, int j)
	{
		i += 1;
		j += 1;

		if ((i<1) || (j < 1) || (i > 8) || (j > 8)) return -2;
		
		piece p = blocks[i][j];
		
		if (p == null) return -1;
		
		else return (p.iColor * 100) + p.iType;
	}
	
	Vector lastmoveVector()
	{
		return lm_vector;
	}
	
	String lastmoveString()
	{
		if (lm_vector == null) return null;
		
		String sRet = null;
		
		int x1 = ((int)lm_vector.elementAt(0)+64);
		int y1 = ((int)lm_vector.elementAt(1)+48);
		int x2 = ((int)lm_vector.elementAt(2)+64);
		int y2 = ((int)lm_vector.elementAt(3)+48);
		
		sRet = "" + (char)x1 + (char)y1 + (char)x2 + (char)y2;
		if (lm_vector.size()==5)
		{
			sRet=sRet+(char)lm_vector.elementAt(4);
		}
		return sRet;
	}
	
	String lastmoveString_bylib()
	{
		if (lm_vector == null) return null;
		
		String sRet = null;
		
		int x1 = ((int)lm_vector.elementAt(0)+96);
		int y1 = ((int)lm_vector.elementAt(1)+48);
		int x2 = ((int)lm_vector.elementAt(2)+96);
		int y2 = ((int)lm_vector.elementAt(3)+48);
		
		//sRet = "" + (char)x1 + (char)y1 + (char)x2 + (char)y2;
		piece p = blocks[(int)lm_vector.elementAt(2)][(int)lm_vector.elementAt(3)];
		
		if (p.iType == piece.PAWN)
		{
			if (x1==x2) sRet = "" + (char)x2 + (char)y2;
			else sRet = "" + (char)x1 + (char)x2 + (char)y2;
		}
		else if (p.iType == piece.KING)
		{
			if ((x1=='e') && (x2=='g')) sRet = "O-O";
			else if ((x1=='e') && (x2=='c')) sRet = "O-O-O";
			else sRet = "K" + (char)x2 + (char)y2;
		}
		else
		{
			if (p.iType == piece.QUEEN) sRet = "Q";
			if (p.iType == piece.ROOK) sRet = "R";
			if (p.iType == piece.BISHOP) sRet = "B";
			if (p.iType == piece.KNIGHT) sRet = "N";
			
			
			piece pOth = p.canReachBrotherPiece(this);
			if (pOth != null)
			{
				//System.out.println("Ambiguous move!!!");
				//sRet = sRet + "X";
				if (pOth.xk == p.xk) sRet = sRet + (char)y1;
				else sRet = sRet + (char)x1;
			}
			//else System.out.println("Not ambiguous at all :=)");
			
			
			sRet = sRet + (char)x2 + (char)y2;
		}
		
		return sRet;
	}
	
	void dump_to_file (PrintWriter pw)
	{
		pw.println(" abcdefgh ");
		for (int j=8;j>=1;j--)
		{
			pw.print(j);
			for (int i=1;i<=8;i++)
			{
				piece p = blocks[i][j];
				if (p == null) pw.print(".");
				else pw.print(p.dumpchr());

			}			
			pw.println(j);

		}
		
		pw.println(" abcdefgh ");
	}
	
	String FEN()
	{
		String sRet = "";
		int iNullC = 0;
		
		for (int j=8;j>=1;j--)
		{
			for (int i=1;i<=8;i++)
			{
				piece p = blocks[i][j];
				if (p == null) iNullC++;
				else
				{
					if (iNullC != 0)
					{
						sRet = sRet + iNullC;
						iNullC = 0;
					}
					if (p.iColor == piece.WHITE) sRet = sRet+ p.dumpchr().toUpperCase();
					else sRet = sRet+ p.dumpchr().toLowerCase();
					
				}
			}
			if (iNullC != 0)
			{
				sRet = sRet + iNullC;
				iNullC = 0;
			}
			
			if (j!= 1) sRet = sRet + "/";

		}
		
		if (lm_vector != null)
		{
			int x1 = ((int)lm_vector.elementAt(0));
			int y1 = ((int)lm_vector.elementAt(1));
			int x2 = ((int)lm_vector.elementAt(2));
			int y2 = ((int)lm_vector.elementAt(3));
			piece p = blocks[x2][y2];
			if (p.iColor == piece.WHITE) sRet = sRet + " b ";
			else sRet = sRet + " w ";
			
			String sCastle = "";
			piece pk = blocks[5][1];
			if ((pk==null) || (pk.iColor != piece.WHITE) || (pk.iType != piece.KING) || (pk.iLastMove != 0)) ;
			else
			{
				piece pr=blocks[8][1];
				if ((pr==null) || (pr.iColor != piece.WHITE) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "K";
				
				pr=blocks[1][1];
				if ((pr==null) || (pr.iColor != piece.WHITE) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "Q";
			}
		    
			pk = blocks[5][8];
			if ((pk==null) || (pk.iColor != piece.BLACK) || (pk.iType != piece.KING) || (pk.iLastMove != 0)) ;
			else
			{
				piece pr=blocks[8][8];
				if ((pr==null) || (pr.iColor != piece.BLACK) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "k";
				
				pr=blocks[1][8];
				if ((pr==null) || (pr.iColor != piece.BLACK) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "q";
			}
			
			if (sCastle.equals("")) sCastle = "-";
			sRet = sRet + sCastle;
			
			if ((p.iType == piece.PAWN) && (Math.abs(y1-y2) == 2))
			{
				sRet = sRet + " ";
				sRet = sRet + (char)(x1+96);
				sRet = sRet + ((y1+y2)/2);
			}
			else sRet = sRet + " -";
		
			sRet = sRet + " 0";
			
			//System.out.println("DBG160109: FEN movecounter calc! iC:" + p.iColor+ " iMC: " + iMoveCounter);
			
			if (p.iColor == piece.WHITE ) sRet = sRet + " " + iMoveCounter;
			else sRet = sRet + " " + (iMoveCounter+1);
		}
		else
		{
			//sRet = sRet + " <" + iFileCol + ">";
			if (iFileCol == piece.WHITE) sRet = sRet + " w ";
			else sRet = sRet + " b ";
			
			String sCastle = "";
			piece pk = blocks[5][1];
			if ((pk==null) || (pk.iColor != piece.WHITE) || (pk.iType != piece.KING) || (pk.iLastMove != 0)) ;
			else
			{
				piece pr=blocks[8][1];
				if ((pr==null) || (pr.iColor != piece.WHITE) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "K";
				
				pr=blocks[1][1];
				if ((pr==null) || (pr.iColor != piece.WHITE) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "Q";
			}
		    
			pk = blocks[5][8];
			if ((pk==null) || (pk.iColor != piece.BLACK) || (pk.iType != piece.KING) || (pk.iLastMove != 0)) ;
			else
			{
				piece pr=blocks[8][8];
				if ((pr==null) || (pr.iColor != piece.BLACK) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "k";
				
				pr=blocks[1][8];
				if ((pr==null) || (pr.iColor != piece.BLACK) || (pr.iType != piece.ROOK) || (pr.iLastMove != 0)) sCastle = sCastle + "-";
				else sCastle = sCastle + "q";
			}
			
			if (sCastle.equals("")) sCastle = "-";
			sRet = sRet + sCastle;
			
			//sRet = sRet +" - 0 1";
			sRet = sRet + " - 0";
			
			//System.out.println("DBG160109: FEN movecounter calc (B)! iC:" + iFileCol + " iMC: " + iMoveCounter);
			
			
			if (iFileCol == piece.WHITE ) sRet = sRet + " " + iMoveCounter;
			else sRet = sRet + " " + (iMoveCounter+1);
		}
		
		return sRet;
	}
	
	void prefixdump(String prefix, int iDumpMode)
	{
		System.out.println(prefix+" abcdefgh ");
		for (int j=8;j>=1;j--)
		{
			System.out.print(prefix+j);
			for (int i=1;i<=8;i++)
			{
				piece p = blocks[i][j];
				if (p == null) System.out.print(".");
				else System.out.print(p.dumpchr());

			}			
			System.out.println(j);

		}
		System.out.println(prefix+" abcdefgh ");
		System.out.println(prefix+"----------");
		
		if (iDumpMode == DUMPMODE_SHORT) return;
		
		System.out.println(prefix+"VALUES: w:" + pvaluesum(piece.WHITE) + " b:" + pvaluesum(piece.BLACK));
		
		//redoVectorsAndCoverages();  // IS THIS THE REASON CHECKING PIECE INFO LOSES ??? $$$$ 141106  YES!!!
		
		System.out.println(prefix+"Coverages:   w: " + iWhiteCovered + " b: " + iBlackCovered);
		System.out.println(prefix+"Pawn adv:    w: " + iWhitePawnAdvance + " b: " + iBlackPawnAdvance);
		System.out.println(prefix+"Unprot thr:  w: " + iWhiteUnprotThreat + " b : " + iBlackUnprotThreat);
		System.out.println(prefix+"King castled w: " + bKingInCastle(piece.WHITE) + " b: " + bKingInCastle(piece.BLACK));
		System.out.println(prefix+"KicPoints    w: " + iKicPoints(piece.WHITE) + " b: " + iKicPoints(piece.BLACK));	
		System.out.println(prefix+"Castled var  w: " + bWhiteCastled + " b: " + bBlackCastled);
		System.out.println(prefix+"Castle move  w: " + iWhiteCastlingMove + " b: " + iBlackCastlingMove);
		System.out.println(prefix+"Undev LO     w: " + iUndevelopedLightOfficers(piece.WHITE) + " b: " + iUndevelopedLightOfficers(piece.BLACK));
		System.out.println(prefix+"Double pwns  w: " + iWhiteDoublePawns + " b: " + iBlackDoublePawns);
		System.out.println(prefix+"Center ctrl  w: " + iWhiteCenterCtrlPts + " b: " + iBlackCenterCtrlPts);
		System.out.println(prefix+"King threat  w: " + bWhiteKingThreat + " b: " + bBlackKingThreat);
		System.out.println(prefix+"Max thr      w: " + iMaxWhiteThreat + " b: " + iMaxBlackThreat);
		System.out.println(prefix+"Max thr bal  w: " + iMaxWhiteThrProtBal + " b: " + iMaxBlackThrProtBal);
		System.out.println(prefix+"King blk ctr w: " + iWhiteKingCtrlBlks + " b: " + iBlackKingCtrlBlks);
		System.out.println(prefix+"King ctr fix w: " + iWhiteKingCtrlFix + " b: "+ iBlackKingCtrlFix);
		System.out.println(prefix+"Early pens   w: " + iWhiteEarlyGamePenalty + " b: " + iBlackEarlyGamePenalty);
		System.out.println(prefix+"King areas   w: " + iWhiteKingSpace + " b: " + iBlackKingSpace);
		System.out.println(prefix+"Freepawn pts w: " + iWhiteFreePawnPoints + " b: " + iBlackFreePawnPoints);
		System.out.println(prefix+"Max pawns    w: " + iMaxWhitePawn + " b: " + iMaxBlackPawn);
		String sWPC = "";
		String sBPC = "";
		for (int i=piece.PAWN; i<=piece.ROOK; i++)
		{
			sWPC = sWPC + iWhitePieceCount[i];
			sBPC = sBPC + iBlackPieceCount[i];
		}
		System.out.println(prefix+"Piece cnts   w: " + sWPC + " b: " + sBPC);
		System.out.println(prefix+"Check moves  w: " + iWhiteCheckMoves + " b: " + iBlackCheckMoves);
		System.out.println(prefix+"Moves        w: " + iWhiteMoves + " b: " + iBlackMoves);
		System.out.println(prefix+"Freepwn ctrl w: " + iWhiteFreePawnCtrlPoints + " b: " + iBlackFreePawnCtrlPoints);
		System.out.println(prefix+"Checked      w: " + bWhiteKingThreat + " b: " + bBlackKingThreat);
		System.out.println(prefix+"Queen ES     w: " + iWhiteQES + " b: " + iBlackQES);
		System.out.println(prefix+"KnightCoupS  w: " + iWhiteKCS + " b: " + iBlackKCS);
		System.out.println(prefix+"KnightCoupM  w: " + iWhiteKCSMoves + " b: " + iBlackKCSMoves);
		System.out.println(prefix+"B&Q CoupS    w: " + iWhiteBCS + " b: " + iBlackBCS);
		System.out.println(prefix+"B&Q CoupM    w: " + iWhiteBCSMoves + " b: " + iBlackBCSMoves);
		System.out.println(prefix+"Pcs @enemyB  w: " + iWhitesInBlackBlock + " b: " + iBlacksInWhiteBlock);
		System.out.println(prefix+"Last Prom    w: "+iWhiteMvLastProm+ " b: " + iBlackMvLastProm);
		System.out.println(prefix+"PromBonus    w: " + iPromBonWhite+ " b: " + iPromBonBlack);
		System.out.println(prefix+"King distance balance: " + kingDistanceBalance() );
		System.out.println(prefix+"Swap Balance         : " + iSwapBalance);
		System.out.println(prefix+"EGS Balance          : " + iEGSBal);
		System.out.println(prefix+"HC Bonus             : " + iHCBonus);
		
		System.out.print(prefix+"FP KING TARGETS: ");
		for (int i=1;i<=8;i++)
		{
			if (iWhiteKingPromDest[i] != -1) System.out.print("wP:"+(char)(i+64)+iWhiteKingPromDest[i]+ " ");
			if (iWhiteKingDefDest[i] != -1) System.out.print("wD:"+(char)(i+64)+iWhiteKingDefDest[i]+ " ");
			if (iBlackKingPromDest[i] != -1) System.out.print("bP:"+(char)(i+64)+iBlackKingPromDest[i]+ " ");
			if (iBlackKingDefDest[i] != -1) System.out.print("bD:"+(char)(i+64)+iBlackKingDefDest[i]+ " ");
			if (iWhiteUPPDest[i] != -1) System.out.print("wU:"+(char)(i+64)+iWhiteUPPDest[i]+" ");
			if (iBlackUPPDest[i] != -1) System.out.print("bU:"+(char)(i+64)+iBlackUPPDest[i]+" ");
		}
		System.out.println();
		System.out.print(prefix+"BCS Targets: ");
		if (vWhiteBCTargets != null) 
		{
			System.out.print("w: ");
			for (int i=0;i<vWhiteBCTargets.size();i++)
			{
				block b = (block)vWhiteBCTargets.elementAt(i);
				System.out.print(" "+(char)(b.xk+64)+b.yk);
			}	
		}
		System.out.print(" ");
		if (vBlackBCTargets != null) 
		{
			System.out.print("b: ");
			for (int i=0;i<vBlackBCTargets.size();i++)
			{
				block b = (block)vBlackBCTargets.elementAt(i);
				System.out.print(" "+(char)(b.xk+64)+b.yk);
			}	
			
		}
		System.out.println();
		
		if ((vWhites != null) && (vBlacks != null))
		{	
			System.out.print(prefix+"Piece vectors w: ");
			for (int i=0;i<vWhites.size();i++) 
			{
				piece p = (piece)vWhites.elementAt(i);
				System.out.print(p.dumpStr()+" ");
			}
			System.out.print(" b: ");
			for (int i=0;i<vBlacks.size();i++) 
			{
				piece p = (piece)vBlacks.elementAt(i);
				System.out.print(p.dumpStr()+" ");
			}
			System.out.println();
		}
		
		
		System.out.println(prefix+"iMoveCount: " + iMoveCount);
		System.out.println(prefix+"iMoveCounter: " + iMoveCounter);
		
		System.out.println(prefix+"Last move dump:");
		for (int j=8;j>=1;j--)
		{
			System.out.print(prefix+j+";");
			for (int i=1;i<=8;i++)
			{
				piece p = blocks[i][j];
				if (p == null) System.out.print(";");
				else System.out.print(p.iLastMove +";");

			}			
			System.out.println(j);

		}
		
		if (vTestDir != null)
		{
			System.out.println("Test directives:");
			for (int i=0;i<vTestDir.size();i++) System.out.println((String)vTestDir.elementAt(i));
		}
	}
	
	void dump()
	{
		//prefixdump("", DUMPMODE_FULL);
		prefixdump("", DUMPMODE_SHORT);
	}
	
	/*
	void prefixdump(String prefix)
	{
		System.out.println(prefix+" abcdefgh ");
		for (int j=8;j>=1;j--)
		{
			System.out.print(prefix+j);
			for (int i=1;i<=8;i++)
			{
				piece p = blocks[i][j];
				if (p == null) System.out.print(".");
				else System.out.print(p.dumpchr());

			}			
			System.out.println(j);

		}
		System.out.println(prefix+" abcdefgh ");
		System.out.println(prefix+"----------");
		System.out.println(prefix+"VALUES: w:" + pvaluesum(piece.WHITE) + " b:" + pvaluesum(piece.BLACK));
		
		updateCoverages();
		System.out.println(prefix+"Coverages: w: " + iWhiteCovered + " b: " + iBlackCovered);
		System.out.println(prefix+"Pawn adv:  w: " + iWhitePawnAdvance + " b: " + iBlackPawnAdvance);
	
	}
	*/
	
	public void init()
	{
		System.out.println("DBG151011:cb init!");
		for (int i=1;i<=8;i++)
		{
			pawn pw = new pawn(i,2,piece.WHITE);
			putpiece( pw);
			
			pawn pb = new pawn(i,7,piece.BLACK);
			putpiece( pb);
		}
		
		king k = new king(5,1,piece.WHITE);
		putpiece(k);
		k = new king(5,8,piece.BLACK);
		putpiece(k);
		
		queen q = new queen (4,1,piece.WHITE);
		putpiece(q);
		q = new queen (4,8,piece.BLACK);
		putpiece(q);
		
		rook r = new rook (1,1,piece.WHITE);
		putpiece(r);
		r = new rook (8,1,piece.WHITE);
		putpiece(r);
		r = new rook (1,8,piece.BLACK);
		putpiece(r);
		r = new rook (8,8,piece.BLACK);
		putpiece(r);
		
		knight kn = new knight (2,1,piece.WHITE);
		putpiece(kn);
		kn = new knight (7,1,piece.WHITE);
		putpiece(kn);
		kn = new knight (2,8,piece.BLACK);
		putpiece(kn);
		kn = new knight (7,8,piece.BLACK);
		putpiece(kn);
		
		bishop b = new bishop (3,1,piece.WHITE);
		putpiece(b);
		b = new bishop (6,1,piece.WHITE);
		putpiece(b);
		b = new bishop (3,8,piece.BLACK);
		putpiece(b);
		b = new bishop (6,8,piece.BLACK);
		putpiece(b);
		
		lm_vector = null;
	}
	
	public boolean init_from_file(String filename)
	{
		System.out.println("DBG151011:cb init from file!");
		String sEnp = null;
		try 
		{
		
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String sctr = br.readLine();

			String shead = br.readLine().trim();
			
			if (sctr.equalsIgnoreCase("white")) iFileCol = piece.WHITE;
			if (sctr.equalsIgnoreCase("black")) iFileCol = piece.BLACK;
			
			
			if (!shead.equals("abcdefgh")) return false;
			
			for (int j=8;j>=1;j--)
			{
				String sPieces=br.readLine();
			
				if (sPieces.charAt(0) != sPieces.charAt(9)) return false;
				if (((int)sPieces.charAt(0)-48) != j) return false;
				
				System.out.println(sPieces + " accepted");

				for (int i=1;i<=8;i++)
				{
					char cPiece = sPieces.charAt(i);
					
					switch (cPiece)
					{
						case 'P':
							pawn pb = new pawn(i,j,piece.BLACK);
							putpiece( pb);
							break;
						
						case 'p':
							pawn pw = new pawn(i,j,piece.WHITE);
							putpiece( pw);
							break;
						
						case 'k':
							king k = new king(i,j,piece.WHITE);
							putpiece(k);
							break;
		
						case 'K':
							k = new king(i,j,piece.BLACK);
							putpiece(k);
							break;

						case 'q':
							queen q = new queen (i,j,piece.WHITE);
							putpiece(q);
							break;

						case 'Q':
							q = new queen (i,j,piece.BLACK);
							putpiece(q);
							break;

						case 'r':	
							rook r = new rook (i,j,piece.WHITE);
							putpiece(r);
							break;
						
						case 'R':
							r = new rook (i,j,piece.BLACK);
							putpiece(r);
							break;

						case 'n':
							knight kn = new knight (i,j,piece.WHITE);
							putpiece(kn);
							break;
							
						case 'N':	
							kn = new knight (i,j,piece.BLACK);
							putpiece(kn);		
							break;
						
						case 'b':
							bishop b = new bishop (i,j,piece.WHITE);
							putpiece(b);
							break;
		
						case 'B':
							b = new bishop (i,j,piece.BLACK);
							putpiece(b);	
							break;
						
						case '.':
							break;
						
						default: 
							return false;
					}
				}

			
			}
			shead = br.readLine().trim();
			if (!shead.equals("abcdefgh")) return false;
			
			shead = br.readLine();
			if (shead != null) vTestDir = new Vector();
			while (shead != null)
			{
				shead = shead.trim();
				System.out.println("READ: " + shead);
				if (shead.charAt(0)=='L') vTestDir.addElement(shead);
				if (shead.charAt(0)=='W') vTestDir.addElement(shead);
				if (shead.charAt(0)=='e') sEnp = shead;
				if (shead.indexOf("MC") != -1)
				{
					System.out.println("chessboard.initfromfile: MC FOUND:" + shead);
					String sPart[]=shead.split(":");
					int iMC = new Integer(sPart[1]).intValue();
					System.out.println("MC value:" + iMC);
					iMoveCounter = iMC;
				}
				
				shead = br.readLine();
				
			}
			
		}
		catch (Exception e)
		{
			System.out.println("Exception " + e.getMessage() + " in chessboard.init_from_file()");
			return false;
		}
		
		if (sEnp != null)
		{
			String sEnpComp[]=sEnp.split("=");
			sEnpComp[1]=sEnpComp[1].toUpperCase();
			int iEnpX = sEnpComp[1].charAt(0)-64;
			int iEnpY = sEnpComp[1].charAt(1)-48;
			System.out.println("En passant:" + sEnpComp[1] + " x=" + iEnpX + "y=" + iEnpY);
			
			piece pEnp = blocks[iEnpX][iEnpY];
			if ((pEnp == null) || (pEnp.iType != piece.PAWN) || (iFileCol == pEnp.iColor) || 
				((iFileCol==piece.WHITE) && (iEnpY != 5)) ||
				((iFileCol==piece.BLACK) && (iEnpY != 4)))
			{
				System.out.println("Bad en passant command: " + sEnp);
				System.exit(0);
			}
			
			lm_vector = new Vector();
			lm_vector.addElement(iEnpX);
			if (iFileCol==piece.WHITE) lm_vector.addElement(7);
			else lm_vector.addElement(2);
			lm_vector.addElement(iEnpX);
			lm_vector.addElement(iEnpY);
			
		}
		
		return true;
	}
	
	boolean init_from_FEN(String sFEN)
	{
		System.out.println("DBG160109:init_from_FEN()");
		String sFENAttr[]=sFEN.split(" ");
		String sFENComp[]=sFENAttr[0].split("/");
		
		if (sFENAttr[1].equalsIgnoreCase("w")) iFileCol = piece.WHITE;
		if (sFENAttr[1].equalsIgnoreCase("b")) iFileCol = piece.BLACK;
		
		System.out.println("Attr5:" + sFENAttr[5]);
		System.out.println("Attr3:" + sFENAttr[3]);
		
		iMoveCounter = new Integer(sFENAttr[5]).intValue()-1;
		iMoveCount = (iMoveCounter-1)*2;
		if (sFENAttr[1].equalsIgnoreCase("b")) iMoveCount++;
		
		System.out.println("iMoveCounter:" + iMoveCounter);
		
		if (!sFENAttr[3].equals("-"))
		{
			int eny1, eny2;
			int enx = (int)sFENAttr[3].charAt(0)-96;
			if (iFileCol == piece.WHITE)
			{
				eny1 = (int)sFENAttr[3].charAt(1)-48+1;
				eny2 = (int)sFENAttr[3].charAt(1)-48-1;
			}
			else
			{
				eny1 = (int)sFENAttr[3].charAt(1)-48-1;
				eny2 = (int)sFENAttr[3].charAt(1)-48+1;
			}
			System.out.println("FEN en passant setup. enx:" + enx + " eny1:" + eny1 + " eny2:" + eny2);
			lm_vector = new Vector();
			lm_vector.addElement(enx);
			lm_vector.addElement(eny1);
			lm_vector.addElement(enx);
			lm_vector.addElement(eny2);
			
			
		}
		
		for(int j=8;j>0;j--)
		{
			String sFENLine=sFENComp[8-j];
			System.out.println("DBG160109:init_from_FEN() j:"+j+" " +sFENLine);
			int cur = 1;
			for (int ch=0;ch<sFENLine.length();ch++)
			{
				char cc = sFENLine.charAt(ch);
				if ((cc>='1') && (cc<='8'))
				{
					cur = cur + cc-48;
				}
				else
				{
					int i=cur;
					switch(cc)
					{
						case 'p':
							pawn pb = new pawn(i,j,piece.BLACK);
							putpiece( pb);
							break;
						
						case 'P':
							pawn pw = new pawn(i,j,piece.WHITE);
							putpiece( pw);
							break;
						
						case 'K':
							king k = new king(i,j,piece.WHITE);
							putpiece(k);
							break;
		
						case 'k':
							k = new king(i,j,piece.BLACK);
							putpiece(k);
							break;

						case 'Q':
							queen q = new queen (i,j,piece.WHITE);
							putpiece(q);
							break;

						case 'q':
							q = new queen (i,j,piece.BLACK);
							putpiece(q);
							break;

						case 'R':	
							rook r = new rook (i,j,piece.WHITE);
							putpiece(r);
							break;
						
						case 'r':
							r = new rook (i,j,piece.BLACK);
							putpiece(r);
							break;

						case 'N':
							knight kn = new knight (i,j,piece.WHITE);
							putpiece(kn);
							break;
							
						case 'n':	
							kn = new knight (i,j,piece.BLACK);
							putpiece(kn);		
							break;
						
						case 'B':
							bishop b = new bishop (i,j,piece.WHITE);
							putpiece(b);
							break;
		
						case 'b':
							b = new bishop (i,j,piece.BLACK);
							putpiece(b);	
							break;
					}
					cur++;
				}
			}
			//System.out.println();
		}
		
		return true;
	}
	
	Vector getTestDir()
	{
		return vTestDir;
	}
	
	chessboard flip(int iColor, int iAlg)
	{
		chessboard cb = this.copy();
		
		System.out.println("flip() called, par: " + iColor);
		
		piece tmpL, tmpH;
		
		for (int i = 1; i <= 8; i++)
			for (int j=1; j <= 4; j++)
		{
			tmpL = cb.blocks[i][j];
			tmpH = cb.blocks[i][9-j];
			
			if (tmpL != null)
			{
				tmpL.yk = 9-tmpL.yk;
				if ((tmpL.iColor == piece.BLACK) && (tmpL.iLastMove > 0)) tmpL.iLastMove++;
				tmpL.iColor = 1-tmpL.iColor;
			}
			
			if (tmpH != null)
			{
				tmpH.yk = 9-tmpH.yk;
				if ((tmpH.iColor == piece.BLACK) && (tmpH.iLastMove > 0)) tmpH.iLastMove++;
				tmpH.iColor = 1-tmpH.iColor;
			}
			
			cb.blocks[i][j] = tmpH;
			cb.blocks[i][9-j] = tmpL;
		}
		
		if (lm_vector != null)
		{
			Vector v = new Vector();
			v.addElement(lm_vector.elementAt(0));
			v.addElement(9-(int)lm_vector.elementAt(1));
			v.addElement(lm_vector.elementAt(2));
			v.addElement(9-(int)lm_vector.elementAt(3));
			if (lm_vector.size() == 5) v.addElement(lm_vector.elementAt(4));
			cb.lm_vector = v;
		}
		
		//System.out.println("DBG 141217: chessboard.flip() calling redo.");
		cb.redoVectorsAndCoverages(iColor, iAlg);
		//System.out.println("DBG 141217: chessboard.flip() redo done.");
		
		
		
		cb.iMoveCount++;
		//if (iColor == piece.BLACK) cb.iMoveCounter++;
		
		cb.bWhiteCastled = this.bBlackCastled; 
		cb.bBlackCastled = this.bWhiteCastled;
		
		cb.iWhiteCastlingMove = cb.iWhiteCastlingMove;
		cb.iBlackCastlingMove = cb.iBlackCastlingMove;
		
		cb.m_kw = null;
		cb.m_kb = null;
		
		System.out.println("DBG 141217: flip to return.");
		
		return cb;
	}
	
	chessboard nali_flip(int iFlipMode)
	{
		chessboard cb = this.copy();
		
		System.out.println("nali_flip() called, par: " + iFlipMode);
		
		piece tmpL, tmpH, tmpR;
		piece tmpRB, tmpLT;
		
		switch (iFlipMode)
		{
			case NALI_HORI:
					for (int i = 1; i <= 8; i++)
					for (int j=1; j <= 4; j++)
				{
					tmpL = cb.blocks[i][j];
					tmpH = cb.blocks[i][9-j];
					
					if (tmpL != null)
					{
						tmpL.yk = 9-tmpL.yk;
						//if ((tmpL.iColor == piece.BLACK) && (tmpL.iLastMove > 0)) tmpL.iLastMove++;
						//tmpL.iColor = 1-tmpL.iColor;
					}
					
					if (tmpH != null)
					{
						tmpH.yk = 9-tmpH.yk;
						//if ((tmpH.iColor == piece.BLACK) && (tmpH.iLastMove > 0)) tmpH.iLastMove++;
						//tmpH.iColor = 1-tmpH.iColor;
					}
					
					cb.blocks[i][j] = tmpH;
					cb.blocks[i][9-j] = tmpL;
				}
				break;
				
			case NALI_VERT:
				for (int i = 1; i <= 4; i++)
					for (int j=1; j <= 8; j++)
				{
					tmpL = cb.blocks[i][j];
					tmpR = cb.blocks[9-i][j];
					
					if (tmpL != null)
					{
						tmpL.xk = 9-tmpL.xk;
						//if ((tmpL.iColor == piece.BLACK) && (tmpL.iLastMove > 0)) tmpL.iLastMove++;
						//tmpL.iColor = 1-tmpL.iColor;
					}
					
					if (tmpR != null)
					{
						tmpR.xk = 9-tmpR.xk;
						//if ((tmpH.iColor == piece.BLACK) && (tmpH.iLastMove > 0)) tmpH.iLastMove++;
						//tmpH.iColor = 1-tmpH.iColor;
					}
					
					cb.blocks[i][j] = tmpR;
					cb.blocks[9-i][j] = tmpL;
				}
				break;
				
			case NALI_DIAG:	
				System.out.println("nali_diag called.");
				for (int i = 2; i <= 8; i++)
						for (int j=1; j < i; j++)
				{
					tmpRB=cb.blocks[i][j];
					tmpLT=cb.blocks[j][i];
					if (tmpRB != null)
					{
						tmpRB.xk=j;
						tmpRB.yk=i;
					}
					if (tmpLT!=null)
					{
						tmpLT.xk=i;
						tmpLT.yk=j;
					}
					cb.blocks[i][j]=tmpLT;
					cb.blocks[j][i]=tmpRB;
				}
				break;
			
			default:
				System.out.println("chessboard.nali_flip(): illegal flipmode " + iFlipMode);
				System.exit(0);
		}
		return cb;
	}
	
	boolean putpiece( piece p)
	{
		blocks[p.xk()][p.yk()] = p;
		return true;
	}
	
	int pvaluesum(int iColor)
	{
		if (valsum[iColor] == 0)
		{
			int ivalsum = 0;
			for (int i=1;i<=8;i++) 
				for (int j=1;j<=8;j++)
				{
					piece p = blocks[i][j];
					if ((p != null) && (p.iColor == iColor)) ivalsum = ivalsum + p.pvalue();
				}
			
			valsum[iColor] = ivalsum;
			
			return valsum[iColor];
		}
		else return valsum[iColor];
	}
	
	boolean bInEndGameMode()
	{
		if (pvaluesum(piece.WHITE) >= 1015 ) return false;
		return pvaluesum(piece.BLACK) < 1015;
	}
	
	boolean bKillModeQRwK()
	{
		if (((iBlackPieceCount[piece.PAWN] == 0) &&
		    (iBlackPieceCount[piece.QUEEN] == 0) &&
		    (iBlackPieceCount[piece.BISHOP] == 0) &&
		    (iBlackPieceCount[piece.ROOK] == 0) &&
		    (iBlackPieceCount[piece.KNIGHT] == 0)) &&
		   ((iWhitePieceCount[piece.QUEEN] != 0) ||
		    (iWhitePieceCount[piece.ROOK] != 0)))
			return true;

		return ((iWhitePieceCount[piece.PAWN] == 0) &&
				(iWhitePieceCount[piece.QUEEN] == 0) &&
				(iWhitePieceCount[piece.BISHOP] == 0) &&
				(iWhitePieceCount[piece.ROOK] == 0) &&
				(iWhitePieceCount[piece.KNIGHT] == 0)) &&
				((iBlackPieceCount[piece.QUEEN] != 0) ||
						(iBlackPieceCount[piece.ROOK] != 0));

	}
	
	boolean bPawnsOrMinorPieces()
	{
		return ((iBlackPieceCount[piece.PAWN] != 0) ||
				(iBlackPieceCount[piece.KNIGHT] != 0) ||
				(iBlackPieceCount[piece.BISHOP] != 0) ||
				(iWhitePieceCount[piece.PAWN] != 0) ||
				(iWhitePieceCount[piece.KNIGHT] != 0) ||
				(iWhitePieceCount[piece.BISHOP] != 0));
	}
	
	boolean bPawnPromRaceMode()
	{
		if ((iBlackPieceCount[piece.PAWN] == 0) && (iWhitePieceCount[piece.PAWN] == 0)) return false;
		
		if (pvaluesum(piece.WHITE) >= 1009 ) return false;
		return pvaluesum(piece.BLACK) < 1009;
	}
	
	int kingDistanceBalance()
	{
		if (bKillModeQRwK())
		{
			//System.out.println("kingDistanceBalance@killmode");
			
			king kw = locateKing(piece.WHITE);
			king kb = locateKing(piece.BLACK);
			
			piece pOwn = null;
			int ekx, eky, okx, oky, tx, ty;
			
			if (iBlackPieceCount[piece.ROOK] != 0) pOwn = locatePiece(piece.BLACK, piece.ROOK);
			if (iWhitePieceCount[piece.ROOK] != 0) pOwn = locatePiece(piece.WHITE, piece.ROOK);
			if (iBlackPieceCount[piece.QUEEN] != 0) pOwn = locatePiece(piece.BLACK, piece.QUEEN);
			if (iWhitePieceCount[piece.QUEEN] != 0) pOwn = locatePiece(piece.WHITE, piece.QUEEN);			
			
			if (pOwn == null) 
			{
				System.out.println("chessboard.kingDistanceBalance() : pOwn = null");
				System.exit(0);
			}
			
			if (pOwn.iColor == piece.WHITE)
			{      
				okx = kw.xk;
				oky = kw.yk;
				ekx = kb.xk;
				eky = kb.yk;
			}
			else
			{
				okx = kb.xk;
				oky = kb.yk;
				ekx = kw.xk;
				eky = kw.yk;
			}
			
			if (pOwn.xk > ekx) tx = pOwn.xk+1;
			else if (pOwn.xk == ekx) tx = pOwn.xk;
			else tx = pOwn.xk-1; 
			
			if (pOwn.yk > eky) ty = pOwn.yk+1;
			else if (pOwn.yk == eky) ty = pOwn.yk;
			else ty = pOwn.yk-1; 
			
			if (Math.abs(okx-ekx) >= 2) tx=pOwn.xk;
			if (Math.abs(oky-eky) >= 2) ty=pOwn.yk;
			
			int okdist = Math.max(Math.abs(okx-tx),Math.abs(oky-ty));
			int ekdist = Math.max(Math.abs(pOwn.xk-ekx),Math.abs(pOwn.yk-eky));
			
			/*
			System.out.println("DBG: okx: " + okx + " oky: " + oky + " tx: " + tx + " ty: " + ty);
			System.out.println("DBG: KingDistanceBalance (qRWK/EK): " + pOwn.xk + "-" + ekx + " , " + pOwn.yk + "-" +eky);
			System.out.println("DBG: KingDistanceBalance (qRWK): EK:" + ekdist + ", OK:" + okdist);
			*/
			if (pOwn.iColor == piece.WHITE) return ekdist - okdist;
			else return okdist - ekdist;
			
		}
		
		if (bPawnPromRaceMode())
		{
			//System.out.println("DBG150127:KingDistanceBalance / bPawnPromRaceMode");
			
			king kw = locateKing(piece.WHITE);
			king kb = locateKing(piece.BLACK);
			
			int wdist = 7;
			int bdist = 7;
			
			int tdist ;
			
			for (int i=1;i<=8;i++)
			{
				if (iWhiteKingPromDest[i] != -1)
				{
					tdist = Math.max(Math.abs(kw.xk-i),Math.abs(kw.yk-iWhiteKingPromDest[i]));
					wdist = Math.min(tdist,wdist);
				}
				
				if (iWhiteKingDefDest[i] != -1)
				{
					tdist = Math.max(Math.abs(kw.xk-i),Math.abs(kw.yk-iWhiteKingDefDest[i]));
					wdist = Math.min(tdist,wdist);
					//System.out.println("DBG150127: i=" + i + ", wkdd=" + iWhiteKingDefDest[i]);
				}
				
				if (iBlackKingPromDest[i] != -1)
				{
					tdist = Math.max(Math.abs(kb.xk-i),Math.abs(kb.yk-iBlackKingPromDest[i]));
					bdist = Math.min(tdist,bdist);
				}
				
				if (iBlackKingDefDest[i] != -1)
				{
					tdist = Math.max(Math.abs(kb.xk-i),Math.abs(kb.yk-iBlackKingDefDest[i]));
					bdist = Math.min(tdist,bdist);
				}
			
				
				
			}
			
			//System.out.println("DBG150127:Wdist:" + wdist);
			//System.out.println("DBG150127:Bdist:" + bdist);
			
			if ((wdist < 7) || (bdist < 7)) return bdist - wdist;
			else
			{
				// go after the unprotected ones
				for (int i=1;i<=8;i++)
				{
					if (iWhiteUPPDest[i] != -1)
					{
						tdist = Math.max(Math.abs(kw.xk-i),Math.abs(kw.yk-iWhiteUPPDest[i]));
						wdist = Math.min(tdist,wdist);
						
						tdist = Math.max(Math.abs(kb.xk-i),Math.abs(kb.yk-iWhiteUPPDest[i]));
						bdist = Math.min(tdist,bdist);
					}
					
					if (iBlackUPPDest[i] != -1)
					{
						tdist = Math.max(Math.abs(kw.xk-i),Math.abs(kw.yk-iBlackUPPDest[i]));
						wdist = Math.min(tdist,wdist);
						
						tdist = Math.max(Math.abs(kb.xk-i),Math.abs(kb.yk-iBlackUPPDest[i]));
						bdist = Math.min(tdist,bdist);
					}
					
				}
				
				//System.out.println("DBG: iKingDistanceBal (pawnrace) : " + wdist + "," + bdist);
				
				return bdist - wdist;
			}
			
		}
		
		return 0;
	}
	
	int iPromoteBonus(int iColor, int iTurn)
	{
		int kx, ky;
		king ke;
		int iBon = 0;
		
		//System.out.println("DBG150928: DBG: iPromoteBonus. iC:" + iColor + " iT:" + iTurn);
		
		if (iColor == piece.WHITE)
		{
			if ((iBlackPieceCount[piece.QUEEN] != 0) ||
			    (iBlackPieceCount[piece.ROOK] != 0) ||
				(iBlackPieceCount[piece.BISHOP] != 0) ||
				(iBlackPieceCount[piece.KNIGHT] != 0)) return 0;
			ke = locateKing(piece.BLACK);	
			kx = ke.xk;
			ky = ke.yk;
			
			boolean bKingMove = bKingCanMoveToPreventProm(ke);
			//System.out.println("bKingMove:" + bKingMove);
			
			for (int i=1;i<=8;i++)
			{
				if ((iWhitePawnColMax[i] > -1) && 
					(iWhitePawnColMax[i] > iBlackPawnColMax[i]) &&
					(iWhitePawnColMax[i] >= iBlackPawnColMax[i-1]) &&
					(iWhitePawnColMax[i] >= iBlackPawnColMax[i+1]))
				{
					int iDist = 8-iWhitePawnColMax[i];
					int iKDist = Math.max(8-ky,Math.abs(kx-i));
			
					//System.out.println("iPromoteBonus, WHITE, i:" + i + " iDist:" + iDist + " iKDist:"+ iKDist);
			
					int iTempBon = 0;
					
					if (iKDist > 0)
					{
						if (((iTurn == piece.WHITE) && (iDist < iKDist) ) || !bKingMove)  iTempBon = iWhitePawnColMax[i];
						if (((iTurn == piece.BLACK) && (iDist < iKDist-1)) || !bKingMove) iTempBon = iWhitePawnColMax[i];
						
					}
					
					if (iTempBon > iBon) iBon = iTempBon;
				}
			}
			//System.out.println("DBG150928: iPromB WHITE, ret: " + iBon);
			return iBon;
		}
		else
		{
			if ((iWhitePieceCount[piece.QUEEN] != 0) ||
			    (iWhitePieceCount[piece.ROOK] != 0) ||
				(iWhitePieceCount[piece.BISHOP] != 0) ||
				(iWhitePieceCount[piece.KNIGHT] != 0)) return 0;
			ke = locateKing(piece.WHITE);	
			kx = ke.xk;
			ky = ke.yk;
			
			boolean bKingMove = bKingCanMoveToPreventProm(ke);
			
			for (int i=1;i<=8;i++)
			{
				if ((iBlackPawnColMin[i] > -1) && 
					(iBlackPawnColMin[i] < iWhitePawnColMin[i]) &&
					(iBlackPawnColMin[i] <= iWhitePawnColMin[i-1]) &&
					(iBlackPawnColMin[i] <= iWhitePawnColMin[i+1]))
				{
					int iDist = iBlackPawnColMin[i]-1;
					int iKDist = Math.max(ky-1,Math.abs(kx-i));
					
					//System.out.println("DBG151006: iDist:" + iDist);
					
					int iTempBon = 0;
					
					if (iKDist > 0)
					{
						if (((iTurn == piece.BLACK) && (iDist < iKDist)) || !bKingMove) iTempBon = 9-iBlackPawnColMin[i];
						if (((iTurn == piece.WHITE) && (iDist < iKDist-1)) || !bKingMove) iTempBon = 9-iBlackPawnColMin[i];
					}
					
					if (iTempBon > iBon) iBon = iTempBon;
				}
			}
			//System.out.println("DBG151001 ret promb (black): " + iBon);
			return iBon;
		}
		
	}
	
	boolean bKingCanMoveToPreventProm(king k)
	{
		if ((k.iColor == piece.WHITE) && (k.yk != 1)) return true;
		if ((k.iColor == piece.BLACK) && (k.yk != 8)) return true;
		
		int iYP;
		
		if (k.iColor == piece.WHITE) iYP = 2;
		else iYP = 7;
		
		piece p = blocks[k.xk][iYP];
		
		if (p == null) return true;
		if ((p.iType != piece.PAWN) || (p.iColor == k.iColor))  return true;
		return !p.bProt;

	}
	
	int i6thLineDblPawnBon(int iColor, int iTurn)
	{
		int iBon = 0;
		
		int iColumn = -1;
		int jb, jbp, jbf;
		int iPlusB = 0;
		
		if (iColor == piece.WHITE)
		{
			for (int i=1;i<7;i++)
			{
				if ((iWhitePawnColMax[i] >= 6) && (iWhitePawnColMax[i+1] >= 6)) iColumn = i;
					
			}
			if (iColumn == -1) return 0;
			if (iWhitePawnColMax[iColumn] >6) iPlusB++; 
			if (iWhitePawnColMax[iColumn+1] >6) iPlusB++; 
		}
		else
		{
			for (int i=1;i<7;i++)
			{
				if ((iBlackPawnColMin[i] <= 3) && (iBlackPawnColMin[i+1] <= 3)) iColumn = i;
					
			}
			if (iColumn == -1) return 0;
			if (iBlackPawnColMin[iColumn] <3) iPlusB++; 
			if (iBlackPawnColMin[iColumn+1] <3) iPlusB++;
		}
		int qc,rc,nc,bc;
		if (iColor == piece.WHITE)
		{
			qc = iBlackPieceCount[piece.QUEEN] ;
			rc = iBlackPieceCount[piece.ROOK] ;
			bc = iBlackPieceCount[piece.BISHOP] ;
			nc = iBlackPieceCount[piece.KNIGHT];
			jb = 6;
			jbp = 7;
			jbf = 8;
		}
		else
		{
			qc = iWhitePieceCount[piece.QUEEN] ;
			rc = iWhitePieceCount[piece.ROOK] ;
			bc = iWhitePieceCount[piece.BISHOP] ;
			nc = iWhitePieceCount[piece.KNIGHT];
			jb=3;
			jbp = 2;
			jbf = 1;
		}
		if (qc > 0) return 0;
		if (qc+rc+nc+bc >= 2) return 0;
		
		
		if (iPlusB < 2)
		{
			king k = locateKing(1-iColor);
			if ((k.xk >= iColumn -1) && (k.xk <= iColumn +2) && 
				(((iColor == piece.WHITE) && (k.yk >= 6)) ||
				((iColor == piece.BLACK) && (k.yk <= 3)))) return 0;
		}
		else
		{
			//System.out.println("DBG160207: dbl6bon xxx: iPlusB:" + iPlusB);
			if (iTurn != iColor)
			{
				//System.out.println("DBG160207: iPlusBcheck: iT != iC");
				piece p1 = blocks[iColumn][jbp];
				piece p2 = blocks[iColumn+1][jbp];
				//System.out.println("p1.threat & prot " + p1.bThreat + " " + p1.bProt);
				//System.out.println("p2.threat & prot " + p2.bThreat + " " + p2.bProt);
				piece p1a = blocks[iColumn][jbf];
				piece p2a = blocks[iColumn+1][jbf];
				if (p1.bThreat && p2a != null) return 0;
				if (p2.bThreat && p1a != null) return 0;
			}
		}
		
		if (iTurn == iColor) 
		{
			//System.out.println("DBG160114:DBL6BON!!!");
			iBon = 5;
		}
		else
		{
			//System.out.println("DBG160114: wrong col anal iC:" + iColor);
			//dump();
			piece p1 = blocks[iColumn][jb];
			if ((p1 == null) || (p1.iType != piece.PAWN) || (p1.iColor != iColor)) p1 = blocks[iColumn][jbp];
			
			piece p2 = blocks[iColumn+1][jb];
			if ((p2 == null) || (p2.iType != piece.PAWN) || (p2.iColor != iColor)) p2 = blocks[iColumn+1][jbp];
			if ((p1.bThreat || p2.bThreat) && (iPlusB == 0)) return 0;
			//System.out.println("DBG160114:DBL6BON!!!");
			iBon = 4;
		}
		//System.out.println("DBG160207: dbl6bon: ret: " + (iBon+iPlusB));
		return iBon + iPlusB;
	}
	
	int iJustPromQProtBon(int iColor)
	{
		//System.out.println("DBG160114:iJustPromQProtBon()");
		
		final int UNTHREAT_PROT = 6;
		final int THREAT_PROT = 4;
		
		if ((iColor == piece.WHITE) && (iWhitePieceCount[piece.QUEEN] == 0)) return 0;
		if ((iColor == piece.BLACK) && (iBlackPieceCount[piece.QUEEN] == 0)) return 0;
		
		if (iColor == piece.WHITE)
		{
			//System.out.println("DBG160114:iJustPromQProtBon(WHITE)");
			for (int i=1;i<=8;i++)
			{
				if (iWhitePawnColMax[i] == 7)
				{
					if (iBlackStrike[i][8] == 0)
					{
						if (i>1) 
						{	
							piece p = blocks[i-1][8];
							if ((p!=null) && (p.iType == piece.QUEEN)  && (p.iColor == piece.WHITE)) 
							{
								if (!p.bThreat) return UNTHREAT_PROT;
								else return THREAT_PROT;
							}
						}
						if (i<8) 
						{	
							piece p = blocks[i+1][8];
							if ((p!=null) && (p.iType == piece.QUEEN)  && (p.iColor == piece.WHITE)) 
							{
								if (!p.bThreat) return UNTHREAT_PROT;
								else return THREAT_PROT;
							}
						}
					}
				}
			}
			//System.out.println("DBG160114:iJustPromQProtBon(WHITE) over...");
		}
		else
		{
			for (int i=1;i<=8;i++)
			{
				if (iBlackPawnColMin[i] == 2)
				{
					if (iWhiteStrike[i][1] == 0)
					{
						if (i>1) 
						{	
							piece p = blocks[i-1][1];
							if ((p!=null) && (p.iType == piece.QUEEN)  && (p.iColor == piece.BLACK)) 
							{
								if (!p.bThreat) return UNTHREAT_PROT;
								else return THREAT_PROT;
							}
						}
						if (i<8) 
						{	
							piece p = blocks[i+1][1];
							if ((p!=null) && (p.iType == piece.QUEEN)  && (p.iColor == piece.BLACK))
							{
								if (!p.bThreat) return UNTHREAT_PROT;
								else return THREAT_PROT;
							}
						}
					}
				}
			}
		}
		return 0;
	}
	
	int iOnePawnBonus(int iColor, int iTurn)
	{
		for (int pt = piece.QUEEN; pt <= piece.ROOK; pt++)
		{
			if (iWhitePieceCount[pt] > 0) return 0;
			if (iBlackPieceCount[pt] > 0) return 0;
		}
		//if (iWhitePieceCount[piece.PAWN] + iBlackPieceCount[piece.PAWN] >= 2) return 0;
		if ((iWhitePieceCount[piece.PAWN] >= 1) && (iBlackPieceCount[piece.PAWN] >= 1)) return 0;
		
		int px,py,iC, iRet;
		
		iRet = 0;
		px = 0;
		py = 0;
		iC = -1;
		king kOwn = null;
		king kEnemy = null;
		
		if (iWhitePieceCount[piece.PAWN] >= 1)
		{
			iC = piece.WHITE;
			kOwn = locateKing(piece.WHITE);
			kEnemy = locateKing(piece.BLACK);
		}
		else
		{
			iC = piece.BLACK;
			kOwn = locateKing(piece.BLACK);
			kEnemy = locateKing(piece.WHITE);
		}
		
		for (int i=1;i<=8;i++)
		{
			/*	if (iWhitePawnColMax[i] > 1)
				{
					px = i;
					py = iWhitePawnColMax[i];
					iC = piece.WHITE;
					kOwn = locateKing(piece.WHITE);
					kEnemy = locateKing(piece.BLACK);
				}	
				}
				else
				{
					for (int i=1;i<=8;i++) 
						if (iBlackPawnColMin[i] < 9)
						{
							px = i;
							py = iBlackPawnColMin[i];
							iC = piece.BLACK;
							kOwn = locateKing(piece.BLACK);
							kEnemy = locateKing(piece.WHITE);
						}
				}*/
			px = i;
			if (iC == piece.WHITE) py = iWhitePawnColMax[i];
			else py = iBlackPawnColMin[i];
			
			//int iRet = 0;
			
			if (((iC == piece.WHITE) && (py > 1)) || ((iC == piece.BLACK) && (py<9)))
			{
				//System.out.println("DBG160118: iOnePawnBonus A: iC:" + iC + " iColor:" + iColor + " px: " + px + " py:" + py);
				
				int iRetTmp =  iOPBSub(kOwn, kEnemy,px,py, iC, iTurn, iColor);
				if (iRetTmp > iRet) iRet = iRetTmp;
				//System.out.println("DBG160203: iOnePawnBonus B: iRet: " + iRet);
				
				/*
				if ((kOwn == null) || (kEnemy == null)) return 0;
				
				if (Math.abs(kOwn.xk-px) > 1) return 0;
				
				//System.out.println("DBG160118: iOnePawnBonus A2: iC:" + iC + " iColor:" + iColor + " iT:" + iTurn);
				
				int endist = Math.max(Math.abs(px-kEnemy.xk),Math.abs(py-kEnemy.yk));
				int owndist = Math.max(Math.abs(px-kOwn.xk),Math.abs(py-kOwn.yk));
				if (endist < owndist) return 0;
				
				//System.out.println("DBG160118: iOnePawnBonus B: iC:" + iC + " iColor:" + iColor);
				
				if ((iC == piece.WHITE) && (py >= kOwn.yk) && (py < 8)) return 0;
				if ((iC == piece.BLACK) && (py <= kOwn.yk) && (py > 1)) return 0;
				
				if (endist == owndist)
				{
					//System.out.println("DBG160118: iOnePawnBonus (end == od): iC:" + iC + " iColor:" + iColor);
					if (iTurn != iC) 
					{
						//if (!((kOwn.yk == kEnemy.yk) && (Math.abs(kOwn.xk-kEnemy.xk) == 2) && (kOwn.xk+kOwn.yk == 2*px))) return 0;
						if (!(owndist == 2))
						{
							if (owndist == 1)
							{
								//System.out.println("DBG160118: iOnePawnBonus (end == od == 1): iC:" + iC + " iColor:" + iColor);
								if (!((kOwn.yk == kEnemy.yk) && (Math.abs(kOwn.xk-kEnemy.xk) == 2) && (kOwn.xk+kEnemy.xk == 2*px))) return 0;
													
							}
							
						}
					}
				}
				
				
				//System.out.println("DBG160118: iOnePawnBonus C");

				if ((iC == piece.WHITE) && (iColor == piece.WHITE)) 
				{
					
					if (kOwn.yk > py+1) iEGSBal = 50;
					if ((kOwn.yk == py+1) && (kOwn.xk==px)) iEGSBal = -20;
					
					iEGSBal = iEGSBal + 10*(kOwn.yk-py);
					
					// good opposition
					if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn != iC))
						return (py-1);
					
					// bad opposition
					if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn == iC))
					{
						if ((Math.max(Math.abs(kOwn.xk-kEnemy.xk),Math.abs(kOwn.yk-kEnemy.yk)) <= 2) &&
							(kEnemy.yk > kOwn.yk) &&
							(Math.abs(kOwn.yk-py) <= 1))
						return 0;
					}
					
					
					if (kOwn.yk > py+1) return py-1;
					
					return (py-2);
				}
				if ((iC == piece.BLACK) && (iColor == piece.BLACK)) 
				{
					//System.out.println("DBG160118: iOnePawnBonus C Black");
					//if ((kOwn.yk == py-1) && (kOwn.xk == px))
					//{
					//if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) (iTurn != iC))
					//		return (6-py);
					//}
					
					// good opposition
					if (kOwn.yk < py-1) iEGSBal = -50;
					if ((kOwn.yk == py-1) && (kOwn.xk==px)) iEGSBal = 20;
					
					iEGSBal = iEGSBal + 10*(kOwn.yk-py);
					
					// good opposition
					if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn != iC))
						return (8-py);
					
					// bad opposition
					if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn == iC) )
					{
						if ((Math.max(Math.abs(kOwn.xk-kEnemy.xk),Math.abs(kOwn.yk-kEnemy.yk)) <= 2) &&	
							(kEnemy.yk < kOwn.yk) &&
							(Math.abs(kOwn.yk-py) <= 1))
						return 0;
					}
					
					if (kOwn.yk < py-1) return 8-py;
					return (7-py);
				} 
				*/
			}  // if py valid...	
		}
		//System.out.println("DBG160118: iOnePawnBonus D, iRet:" + iRet);
		
		return iRet;
	}
	
	int iOPBSub(king kOwn, king kEnemy,int px,int py, int iC, int iTurn, int iColor)
	{
				
		if ((kOwn == null) || (kEnemy == null)) return 0;
		
		if (Math.abs(kOwn.xk-px) > 1) return 0;
		
		//System.out.println("DBG160118: iOnePawnBonus A2: iC:" + iC + " iColor:" + iColor + " iT:" + iTurn);
		
		int endist = Math.max(Math.abs(px-kEnemy.xk),Math.abs(py-kEnemy.yk));
		int owndist = Math.max(Math.abs(px-kOwn.xk),Math.abs(py-kOwn.yk));
		if (endist < owndist) return 0;
		
		//System.out.println("DBG160118: iOnePawnBonus B: iC:" + iC + " iColor:" + iColor);
		
		if ((iC == piece.WHITE) && (py >= kOwn.yk) && (py < 8)) return 0;
		if ((iC == piece.BLACK) && (py <= kOwn.yk) && (py > 1)) return 0;
		
		if (endist == owndist)
		{
			//System.out.println("DBG160118: iOnePawnBonus (end == od): iC:" + iC + " iColor:" + iColor);
			if (iTurn != iC) 
			{
				//if (!((kOwn.yk == kEnemy.yk) && (Math.abs(kOwn.xk-kEnemy.xk) == 2) && (kOwn.xk+kOwn.yk == 2*px))) return 0;
				if (!(owndist == 2))
				{
					if (owndist == 1)
					{
						//System.out.println("DBG160118: iOnePawnBonus (end == od == 1): iC:" + iC + " iColor:" + iColor);
						if (!((kOwn.yk == kEnemy.yk) && (Math.abs(kOwn.xk-kEnemy.xk) == 2) && (kOwn.xk+kEnemy.xk == 2*px))) return 0;
											
					}
					
				}
			}
		}
		
		
		//System.out.println("DBG160118: iOnePawnBonus C");

		if ((iC == piece.WHITE) && (iColor == piece.WHITE)) 
		{
			
			if (kOwn.yk > py+1) iEGSBal = 50;
			if ((kOwn.yk == py+1) && (kOwn.xk==px)) iEGSBal = -20;
			
			iEGSBal = iEGSBal + 10*(kOwn.yk-py);
			
			// good opposition
			if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn != iC))
				return (py-1);
			
			// bad opposition
			if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn == iC))
			{
				if ((Math.max(Math.abs(kOwn.xk-kEnemy.xk),Math.abs(kOwn.yk-kEnemy.yk)) <= 2) &&
					(kEnemy.yk > kOwn.yk) &&
					(Math.abs(kOwn.yk-py) <= 1))
				return 0;
			}
			
			
			if (kOwn.yk > py+1) return py-1;
			
			return (py-2);
		}
		if ((iC == piece.BLACK) && (iColor == piece.BLACK)) 
		{
			//System.out.println("DBG160118: iOnePawnBonus C Black");
			/*if ((kOwn.yk == py-1) && (kOwn.xk == px))
			{
				if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn != iC))
					return (6-py);
			}
			*/
			// good opposition
			if (kOwn.yk < py-1) iEGSBal = -50;
			if ((kOwn.yk == py-1) && (kOwn.xk==px)) iEGSBal = 20;
			
			iEGSBal = iEGSBal + 10*(kOwn.yk-py);
			
			// good opposition
			if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn != iC))
				return (8-py);
			
			// bad opposition
			if ((((kOwn.xk+kOwn.yk) % 2) == ((kEnemy.xk+kEnemy.yk) % 2)) && (iTurn == iC) )
			{
				if ((Math.max(Math.abs(kOwn.xk-kEnemy.xk),Math.abs(kOwn.yk-kEnemy.yk)) <= 2) &&	
					(kEnemy.yk < kOwn.yk) &&
					(Math.abs(kOwn.yk-py) <= 1))
				return 0;
			}
			
			if (kOwn.yk < py-1) return 8-py;
			return (7-py);
		}
		
		return 0;

	}
	
	synchronized chessboard copy()
	{
		chessboard cb = new chessboard();
		cb.iMoveCounter = iMoveCounter;
		cb.mMaxThreads = mMaxThreads;
		cb.iWhiteMvLastProm = iWhiteMvLastProm;
		cb.iBlackMvLastProm = iBlackMvLastProm;
		
		CMonitor.incChessboardCopy();
		
		//System.out.println("DBG: CHESSBOARD COPY() CALLED.");
		
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				
				if (p != null)
				{
					switch (p.iType)
					{
						case piece.PAWN:
							pawn pa = new pawn(p.xk,p.yk,p.iColor);
							pa.iLastMove = p.iLastMove;
							pa.prev_xk = p.prev_xk;
							pa.prev_yk = p.prev_yk;
							cb.putpiece(pa);
							break;
							
						case piece.KING:
							king k = new king(p.xk,p.yk,p.iColor);
							k.iLastMove = p.iLastMove;
							k.prev_xk = p.prev_xk;
							k.prev_yk = p.prev_yk;
							cb.putpiece(k);
							break;	
						
						case piece.QUEEN:
							queen q = new queen(p.xk,p.yk,p.iColor);
							q.iLastMove = p.iLastMove;
							q.prev_xk = p.prev_xk;
							q.prev_yk = p.prev_yk;
							cb.putpiece(q);
							break;

						case piece.ROOK:
							rook r = new rook(p.xk,p.yk,p.iColor);
							r.iLastMove = p.iLastMove;
							r.prev_xk = p.prev_xk;
							r.prev_yk = p.prev_yk;
							cb.putpiece(r);
							break;

						case piece.KNIGHT:
							knight kn = new knight(p.xk,p.yk,p.iColor);
							kn.iLastMove = p.iLastMove;
							kn.prev_xk = p.prev_xk;
							kn.prev_yk = p.prev_yk;
							cb.putpiece(kn);
							break;

						case piece.BISHOP:
							bishop b = new bishop(p.xk,p.yk,p.iColor);
							b.iLastMove = p.iLastMove;
							b.prev_xk = p.prev_xk;
							b.prev_yk = p.prev_yk;
							cb.putpiece(b);
							break;							
						
						default:
							System.out.println("BAD TYPE!!!");
							break;
					}
					
					
				}
			}
	
		king kw = null;
		king kb = null;
	
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				if (p != null)
				{
					Vector m = p.moveVector(this);
					if ((p.iType == piece.KING) && (p.iColor == piece.WHITE)) kw = (king)p;
					if ((p.iType == piece.KING) && (p.iColor == piece.BLACK)) kb = (king)p;
				}
			}
	
		updateCoverages();
		
		if (kw != null) kw.dropCoveredMoves(this);
		else
		{
			System.out.println("Copying a board without white king @chessboard.copy()");
			dump();
		}
		
		if (kb != null) kb.dropCoveredMoves(this);
		else 
		{
			System.out.println("Copying a board without black king @chessboard.copy()");
			dump();
			System.out.println("Lastmove string:" + lastmoveString());
		}
		
		if ((kw == null) || (kb == null))
		{
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) 
			{
				System.out.println(ste);
			}
			
			System.exit(0);
		}
	
	
		// lastmovevector needs to be copied
		
		if (lm_vector != null)
		{
			cb.lm_vector = new Vector();
			for (int r = 0; r<lm_vector.size();r++)
			{
				cb.lm_vector.addElement(lm_vector.elementAt(r));
			}
		}
		
		cb.bWhiteCastled = bWhiteCastled; 
		cb.bBlackCastled = bBlackCastled;
		
		cb.iWhiteCastlingMove = iWhiteCastlingMove;
		cb.iBlackCastlingMove = iBlackCastlingMove;
		
		cb.bWhiteRiskOn = bWhiteRiskOn;
		cb.bBlackRiskOn = bBlackRiskOn;
		
		cb.iMoveCount = iMoveCount;
		
		cb.vTestDir = vTestDir;
		
		cb.iFileCol = iFileCol;
		
		cb.vTestDir = vTestDir;
		
		return cb;
	}
	
	//void redoVectorsAndCoverages()
	void redoVectorsAndCoverages(int iTurn, int iAlg)
	{
		//System.out.println("DBG: 150207: DPM for test: redoVectorsAndCoverages enter: (iTurn)" + iTurn);
		vWhites = new Vector();
		vBlacks = new Vector();
		
		king kw = null;
		king kb = null;
		
		Vector qVecWhite = new Vector();
		Vector qVecBlack = new Vector();
		
		//queen qw = null; // DEBUG 140123
	
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
		{
			// setting flags to false to reset 140328 issue $$$
			// moved here to resolve the 140408 issue 
			piece p = blocks[i][j];
			if (p != null)
			{
				p.bProt = false;
				p.bThreat = false;
				p.iProtCount = 0;
				p.iThreatCount = 0;
				p.iPinValue = 0;			// resetting new pin info 160126
				p.iPinDirection = 0;
				p.iPinningToDirection = piece.NO_DIR;
			}
			iWhiteStrike[i][j] = 0;
			iBlackStrike[i][j] = 0;
		}
	
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				
				if (p != null)
				{
					if (p.iColor == piece.WHITE) vWhites.addElement(p);
					else vBlacks.addElement(p);
					
					p.mMoveVector = null;
					if ((p.xk != i) || (p.yk != j))
					{
						System.out.println("chessboard.redoVectorsAndCoverages() : assertion failure!");
						System.out.println("i=" + i + ", j= " + j);
						System.exit(0);
					}
					
					// flag reset moved before the loop to reset 140408 issue
					
					Vector m = p.moveVector(this);
					if ((p.iType == piece.KING) && (p.iColor == piece.WHITE)) kw = (king)p;
					if ((p.iType == piece.KING) && (p.iColor == piece.BLACK)) kb = (king)p;
					if ((p.iType == piece.QUEEN) && (p.iColor == piece.WHITE)) qVecWhite.addElement(p);
					if ((p.iType == piece.QUEEN) && (p.iColor == piece.BLACK)) qVecBlack.addElement(p);
				}
				
			}
		//System.out.println("DBG141226: REDO before UC");
		
		updateCoverages();
		
		if (kw != null) kw.dropCoveredMoves(this);
		else bWhiteKingLost = true;
		
		if (kb != null) kb.dropCoveredMoves(this);
		else bBlackKingLost = true;
	
		if (kw != null) dropPinnedMoves(kw);
		
		if (kb != null) dropPinnedMoves(kb);
		
		calcMaxThreats();
		
		//System.out.println("DBG141226: REDO before UCMC");
		updateCheckMoveCounts();
		
		//System.out.println("DBG141226: REDO after UCMC");
		
		if (bWhiteKingThreat)
		{
			miWhiteMoveindex = miWhiteMoveindex.goodMoveIndex(this,kw);
			iWhiteMoves = miWhiteMoveindex.getSize();
			//System.out.println("iWhiteMoves="+iWhiteMoves);
			
			if (iWhiteMoves ==0) 
			{
				//System.out.println("White mated in redo ");
				return;
			}
			for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				if ((p != null) && (p.iColor == piece.WHITE))
				{
					p.mMoveVector = miWhiteMoveindex.getMoveVectorAt(i,j);
				}
				
			}
		}
		
		if (bBlackKingThreat)
		{
			miBlackMoveindex = miBlackMoveindex.goodMoveIndex(this,kb);
			iBlackMoves = miBlackMoveindex.getSize();
			
			if (iBlackMoves ==0) 
			{
				//System.out.println("Black mated in redo ");
				return;
			}
			for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				if ((p != null) && (p.iColor == piece.BLACK))
				{
					p.mMoveVector = miBlackMoveindex.getMoveVectorAt(i,j);
				}
			}
		}
		
		//System.out.println("DBG141226: REDO after king threats ");
	
		if (iWhiteMoves == 0)
		{
			//System.out.println("Redo returns, white blocked");
			bWhiteBlocked = true;
			return;
		}
		if (iBlackMoves == 0)
		{
			//System.out.println("Redo returns, black blocked");
			bBlackBlocked = true;
			return;
		}
		
		iWhiteQES=0;
		for (int i=0;i<qVecWhite.size();i++)
		{
			piece p=(piece)qVecWhite.elementAt(i);
			iWhiteQES=iWhiteQES+assessQESPoints(p.xk,p.yk);
		}
		
		iBlackQES=0;
		for (int i=0;i<qVecBlack.size();i++)
		{
			piece p=(piece)qVecBlack.elementAt(i);
			iBlackQES=iBlackQES+assessQESPoints(p.xk,p.yk);
		}
		
		//System.out.println("DBG 141225: Redo before set riskbits.");
		
		miWhiteMoveindex.setRiskBits(this);
		miBlackMoveindex.setRiskBits(this);
		
		iValSumWhite = pvaluesum(piece.WHITE);
		iValSumBlack = pvaluesum(piece.BLACK);
		
		//System.out.println("DBG 141225: Redo after set riskbits.");
		setZeroBalances(iTurn, iAlg);
		//System.out.println("DBG 141225: Redo completed.");
		
		iPromBonWhite =  iPromoteBonus(piece.WHITE,piece.WHITE) - iPromoteBonus(piece.BLACK,piece.WHITE);
		iPromBonBlack = iPromoteBonus(piece.WHITE,piece.BLACK) - iPromoteBonus(piece.BLACK,piece.BLACK);
		//System.out.println("DBG151012: prombonus set (A): W: " + iPromBonWhite + " B: " + iPromBonBlack);
		
		if ((iPromBonWhite == 0) && (iPromBonBlack == 0))
		{
			int wb = i6thLineDblPawnBon(piece.WHITE,iTurn);
			//if (wb != 0) System.out.println("DBG160114: i6thLineDblPawnBon call, white:" + wb);
			int bb = i6thLineDblPawnBon(piece.BLACK,iTurn);
			//if (bb != 0) System.out.println("DBG160114: i6thLineDblPawnBon call, black:" + bb);
			
			iPromBonWhite = i6thLineDblPawnBon(piece.WHITE,iTurn) - i6thLineDblPawnBon(piece.BLACK,iTurn); // -i6thLineDblPawnBon(piece.BLACK,iTurn);
			iPromBonBlack = - i6thLineDblPawnBon(piece.BLACK,iTurn) + i6thLineDblPawnBon(piece.WHITE,iTurn); // i6thLineDblPawnBon(piece.WHITE,iTurn)-i6thLineDblPawnBon(piece.BLACK,iTurn);
			//System.out.println("DBG160114: i6thLineDblPawnBon called. W:" + + iPromBonWhite + " B: " + iPromBonBlack);
			
			if ((iPromBonWhite == 0) && (iPromBonBlack == 0)) 
			{
				//System.out.println("DBG160114: i6thLineDblPawnBon called. W:" + + iPromBonWhite + " B: " + iPromBonBlack);
				//iPromBonBlack = 4;
				//System.out.println("DBG160114: jppb WHITE: " + iJustPromQProtBon(piece.WHITE));
				//System.out.println("DBG160114: jppb BLACK: " + iJustPromQProtBon(piece.BLACK));
				iPromBonWhite = iJustPromQProtBon(piece.WHITE) - iJustPromQProtBon(piece.BLACK);
				iPromBonBlack = iJustPromQProtBon(piece.WHITE) - iJustPromQProtBon(piece.BLACK);
				
				//System.out.println("DBG160114: iJustPromQProtBon called. W:" + + iPromBonWhite + " B: " + iPromBonBlack);
			}
			//System.out.println("DBG151012: prombonus set (A2): W: " + iPromBonWhite + " B: " + iPromBonBlack);
			if ((iPromBonWhite == 0) && (iPromBonBlack == 0)) 
			{
				iPromBonWhite = iOnePawnBonus(piece.WHITE,iTurn) - iOnePawnBonus(piece.BLACK,iTurn);
				iPromBonBlack = iOnePawnBonus(piece.WHITE,iTurn) - iOnePawnBonus(piece.BLACK,iTurn);
			}
			//System.out.println("DBG151012: prombonus set (B): W: " + iPromBonWhite + " B: " + iPromBonBlack);
			
		}
		
		/*
		if ((iPromBonWhite == 0) && (iPromBonBlack == 0))
		{
			hcdrawbon.setHCDrawBon(this, iTurn);
			iPromBonWhite = iHCDrawBonusBal;
			iPromBonBlack = iHCDrawBonusBal;
		}
		*/
		
		if (iValSumWhite == iValSumBlack)
		{
			iSwapBalance = 0;
		}
		else
		{
			if (iValSumWhite > iValSumBlack) iSwapBalance = 1039 - iValSumBlack;
			else iSwapBalance = iValSumWhite - 1039;
		
			//System.out.println("DBG151122: swapBalance(A):" + iSwapBalance);
			if ((iWhitePieceCount[piece.BISHOP] == 1) && (iBlackPieceCount[piece.BISHOP] == 1))
			{
				if ((bWhiteBRBishop && bBlackWRBishop) || (bWhiteWRBishop && bBlackBRBishop))
				{
					if (iValSumWhite > iValSumBlack) iSwapBalance = iSwapBalance - 10;
					else iSwapBalance = iSwapBalance + 10;
				}
			}
			//System.out.println("DBG151122: swapBalance(B):" + iSwapBalance);
		
		}
		
		
		//assessKingSafety(kw,kb);
		setQRKillBal(iTurn);
		
	}    // redo end
	
	void updateCheckMoveCounts()
	{
		iWhiteCheckMoves = 0;
		iBlackCheckMoves = 0;
		
		iWhiteMoves = 0;
		iBlackMoves = 0;
		
		//System.out.println("DBG 150207 UCMC enter. Under check status w:" + bWhiteKingThreat+ " b:"+bBlackKingThreat);
		
		miWhiteMoveindex = new moveindex(piece.WHITE, this);
		miBlackMoveindex = new moveindex(piece.BLACK, this);
		
		//miWhiteMoveindex.setRiskBits(this);
		//miBlackMoveindex.setRiskBits(this);
		
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				if (p != null)
				{
					if (p.iType == piece.KNIGHT) 
					{
						((knight)p).finish_KCSValue(this);
						int iKCS = ((knight)p).iKnightCoupState;
						if ((iKCS == knight.KNIGHT_KCS_PROT) || (iKCS == knight.KNIGHT_KCS_THREAT))
						{
							if (p.iColor == piece.WHITE) this.iWhiteKCS = this.iWhiteKCS+2;
							else this.iBlackKCS = this.iBlackKCS+2;
						}
						if (iKCS == knight.KNIGHT_KCS_UNPROT)
						{
							if (p.iColor == piece.WHITE) this.iWhiteKCS = this.iWhiteKCS+4;
							else this.iBlackKCS = this.iBlackKCS+4;
						}
						
						if ((this.iBlackKCS > 0) || (this.iWhiteKCS > 0))
						{
							//System.out.println("DBG150313: KCS SUSPECT! clr: " +p.iColor +" at "+p.xk +"," + p.yk + " prot:" + p.bProt + " protcount:" + p.iProtCount + " threat: " + p.bThreat + " threatcount: " + p.iThreatCount + " minthreat:" + p.iMinThreat);
							if (p.bThreat) 
							{
								if (p.iColor == piece.WHITE) this.iWhiteKCS = 0;
								if (p.iColor == piece.BLACK) this.iBlackKCS = 0;
							}
						}							
					}
					
					Vector mv = (Vector)p.moveVector(this);
					boolean bRev = false;
					
					if (p.iColor == piece.WHITE)
					{
						iWhiteMoves = iWhiteMoves + mv.size();
						miWhiteMoveindex.addMoveVector(mv);
					}
					
					if (p.iColor == piece.BLACK)
					{
						iBlackMoves = iBlackMoves + mv.size();
						miBlackMoveindex.addMoveVector(mv);
					}
					
					for (int ii=0;ii<mv.size();ii++)
					{
						move m = (move)mv.elementAt(ii);
						//System.out.println("DBG: @" + p.xk +"," + p.yk + " to " + m.xtar +"," + m.ytar + "   w: " + iWhiteCheckMoves + ", b:" + iBlackCheckMoves);
						if ((p.iColor==piece.WHITE) && (m.isCheck())) iWhiteCheckMoves++;
						if ((p.iColor==piece.BLACK) && (m.isCheck())) iBlackCheckMoves++;
						if (m.isRevCheck()) bRev = true;
						/*if ((p.iColor==piece.WHITE) && (m.bKCS) && (!bWhiteKingThreat))
						{
							//System.out.println("DBG141226: KCS WHITE MOVE !!!!!! + " + m.moveStr());
							m.analyzeRisk(p,this);
							if (!m.isRisky()) iWhiteKCSMoves++;
						}
						if ((p.iColor==piece.BLACK) && (m.bKCS) && (!bBlackKingThreat) )
						{
							//System.out.println("DBG141226: KCS BLACK MOVE !!!!!! + " + m.moveStr());
							m.analyzeRisk(p,this);
							if (!m.isRisky()) iBlackKCSMoves++;
						}
						*/
					}
					
					if ((p.iColor==piece.WHITE) && bRev) iWhiteCheckMoves++;
					if ((p.iColor==piece.BLACK) && bRev) iBlackCheckMoves++;
					
					//System.out.println("DBG: UCM " + p.iColor + "  :" + p.xk +"," + p.yk + " :" + iBlackCheckMoves);
				}
			}	
		
	}
	
	boolean domove (piece p,move m, int iPt)
	// pt about pawn promotion
	{
		boolean bSucc = false;
		
		//System.out.println("DBG 160126:domove called. " + p.iType +"@" + p.xk + "," +p.yk + " to " + m.xtar + "," + m.ytar + "," + m.bCapture + " " + m.moveStr());
		//if (p.iPinningToDirection != piece.NO_DIR) System.out.println("DBG160126: moving piece has pin to direction: " + p.iPinningToDirection);
		//System.out.println("chessboard.domove(): moveStr():" + m.moveStr());
		
		/* DOTHE MOVE CODE MOVED HERE 160126 It's a brutal disaster !!! will not work! ===========*/
		/*
		int iOldx = p.xk;
		int iOldy = p.yk;
		
		blocks[p.xk][p.yk] = null;
		p.prev_xk = p.xk;
		p.prev_yk = p.yk;
		p.xk = m.xtar;
		p.yk = m.ytar;
		p.iLastMove = iMoveCounter;
		*/
		/* END DOTHEMOVE CODE */
		
		Vector mv = (Vector)p.moveVector(this);
		
		//System.out.println("DBG 160126 @domove SPOT A");
		
		// ignore existing movevector if in middle of castling
		if (bInsideCastling)
		{
			bSucc = true;
			if (p.iColor == piece.WHITE) 
			{
				bWhiteCastled = true;
				iWhiteCastlingMove = iMoveCounter;
				//System.out.println("DBG:WhiteCastlingMove = " + iWhiteCastlingMove);
			}
			else
			{
				bBlackCastled = true;
				iBlackCastlingMove = iMoveCounter;
				//System.out.println("DBG:BlackCastlingMove = " + iBlackCastlingMove);
			}
		}
		
		for (int i = 0; i<mv.size();i++)
		{
			//System.out.println("DBG:mvl");
			move m2 = (move)mv.elementAt(i);
			if ((m.xtar == m2.xtar) && (m.ytar == m2.ytar)) bSucc = true;
		}
		
		if (!bSucc)
		{
			System.out.println("domove ret false 1");
			return false;
		}
		
		if (((m.bCapture == true) && (blocks[m.xtar][m.ytar] == null))  && (p.iType != piece.PAWN)) 
		return false;
		
		if ((m.bCapture == false) && (blocks[m.xtar][m.ytar] != null)) 
		{
			System.out.println("ERROR!  BAD MOVE, non capture to taken block!");
			return false;
		}
		
		
		// OLD SPOT FOR DOING THE MOVE CODE 160126 has to be before movevector init to avoid pinning failures
		// moving this causes big problems
		int iOldx = p.xk;
		int iOldy = p.yk;
		
		blocks[p.xk][p.yk] = null;
		p.prev_xk = p.xk;
		p.prev_yk = p.yk;
		p.xk = m.xtar;
		p.yk = m.ytar;
		p.iLastMove = iMoveCounter;
		
		
		//System.out.println("DBG 160126:domove coords now : p.xk: " + p.xk + " p.yk:" + p.yk);
		
		if ((p.iType == piece.PAWN) && (m.bCapture == true) && (blocks[m.xtar][m.ytar] == null))
		{
			// capture by en passant
			//System.out.println("DBG: Doing EPC at : " + m.xtar + "," + m.ytar);
			int iYcapt = -1;
			if (p.iColor == piece.WHITE) iYcapt = 5;
			else iYcapt = 4;
			piece pcaptpawn = blocks[m.xtar][iYcapt];
			
			if (pcaptpawn == null)
			{
				System.out.println("En passant failure. No pawn to be captured...");
				System.exit(0);
			}
			pcaptpawn.xk = -1;
			pcaptpawn.yk = -1;
			blocks[m.xtar][iYcapt] = null;
		}
		else if (m.bCapture == true) 
		{
			// regular capture
			piece pCapt = blocks[m.xtar][m.ytar];
			if (pCapt.iType == piece.KING)
			{
				//try {Thread.sleep(1000);} catch (Exception e) {};
				System.out.println("KING CAPTURED BY " + m.moveStr() + ". FATAL ERROR!!!!");
				prefixdump("FATAL:", DUMPMODE_SHORT);
				for (StackTraceElement ste : Thread.currentThread().getStackTrace()) 
				{
					System.out.println(ste);
				}
				System.exit(0);
			}
			pCapt.xk = -1;
			pCapt.yk = -1;
		}
		
		blocks[p.xk][p.yk] = p;
		
		// if castling
		if ((p.iType == piece.KING) && (iOldx == 5) && ((p.xk == 3) || (p.xk == 7)))
		{
			//System.out.println("Doing castling here....");
			if (p.xk == 3)
			{
				bInsideCastling = true;
				boolean ret = domove (1,p.yk,4,p.yk,-1);
				iMoveCount--;
				//System.out.println("long castling retcode : " + ret);
			}
			if (p.xk == 7) 
			{
				//System.out.println("G1 ROOK movevector size:" + blocks[8][1].mv.size());
				bInsideCastling = true;
				boolean ret = domove (8,p.yk,6,p.yk,-1);
				iMoveCount--;
				//System.out.println("short castling retcode : " + ret);
			}
			
		}
		
		char npc = '-';
		if ((p.iType == piece.PAWN) && ((p.yk == 1) || (p.yk == 8)))
		{
			// pawn promotion
			//System.out.println("DBG 150426: chessboard.domove(): Pawn Prom!" + iMoveCounter);
			
			if (p.iColor == piece.WHITE) iWhiteMvLastProm = iMoveCounter;
			else iBlackMvLastProm = iMoveCounter;
			
			if (m.iPromTo != -1) iPt = m.iPromTo;
			
			if (iPt == -1)
			{
				queen q = new queen (p.xk,p.yk,p.iColor);
				putpiece(q);
				q.iLastMove = iMoveCounter;
			}
			else
			{
				piece np = null;
				
				switch (iPt)
				{
					
					case piece.QUEEN:
						np = new queen(p.xk,p.yk,p.iColor);
						npc = 'Q';
						break;
						
					case piece.ROOK:
						np = new rook(p.xk,p.yk,p.iColor);
						npc = 'R';
						break;

					case piece.BISHOP:
						np = new bishop(p.xk,p.yk,p.iColor);
						npc = 'B';
						break;

					case piece.KNIGHT:
						np = new knight(p.xk,p.yk,p.iColor);
						npc = 'N';
						break;		
					
					default:
						System.out.println("Piece promotion failure: " + iPt);
						System.exit(0);
						break;
				}
				
				putpiece(np);
				np.iLastMove = iMoveCounter;
			}
			
			p.xk = -1;
			p.yk = -1;
			
		}
		
		//System.out.println("DBG: adding lm_vector " + iOldx + "," + iOldy + " to " + m.xtar + "," + m.ytar);
		lm_vector = new Vector();
		
		lm_vector.addElement(iOldx);
		lm_vector.addElement(iOldy);
		lm_vector.addElement(m.xtar);
		lm_vector.addElement(m.ytar);
		if (npc != '-') lm_vector.addElement(npc);
		
		//if (m.bCapture) lm_vector.addElement(1); $$$ 160625 removed
		//else lm_vector.addElement(0);
		
		iMoveCount++;
		
		valsum[0]=0;
		valsum[1]=0;
		
		return true;
	}
	
	boolean domove (int x1, int y1, int x2, int y2, int iPt)
	{
		//System.out.println("Entering domove (coord): " + x1 +"," + y1 +" -> " + x2 + "," + y2);
		piece p = blocks[x1][y1];
		if (p == null) return false;
		
		//System.out.println("Domove coord (B)");
		piece p2 = blocks[x2][y2];
		move m = null;
		
		if (p2 == null)
		{
			// $$$$ 141021 en passant code lacking here!!!!
			//System.out.println("DBG: HOW ABOUT EN PASSANT????");
			if (p.iType != piece.PAWN) m = new move (x2,y2,false,0, p);
			else
			{
				boolean bCapture;
				int iCapVal;
				
				if ((((y2 == 6) && (p.iColor == piece.WHITE)) || ((y2 == 3) && (p.iColor == piece.BLACK))) && (Math.abs(x1-x2) == 1))
				{
					bCapture = true;
					iCapVal = 1;
				}
				else
				{
					bCapture = false;
					iCapVal = 0;
				}
				m = new move (x2,y2,bCapture,iCapVal,p);
			}
		}else
		{
			if (p.iColor == p2.iColor) return false;
			m = new move (x2,y2,true,p2.pvalue(),p);
		}
		//System.out.println("coordmove calling real domove..");
		//ystem.out.println("capture flag: " + m.bCapture);
		return domove(p,m,iPt);
	}
	
	boolean domove (String sInput, int iColor)
	{
		int x1,y1,x2,y2, pt;
		
		//System.out.println("DBG160109:domove(a):" + sInput + " iC:" + iColor);
		
		if (sInput.length() < 4) return false;
		
		pt = -1;
		
		sInput = sInput.toUpperCase();
		
		x1 = (int)sInput.charAt(0)-64;
		y1 = (int)sInput.charAt(1)-48;
		x2 = (int)sInput.charAt(2)-64;
		y2 = (int)sInput.charAt(3)-48;
		
		
		if (sInput.length() > 4)
		{
			char promc = sInput.charAt(4);
			switch(promc)
			{
				case 'Q':
					pt = piece.QUEEN;
					break;
					
				case 'R':
					pt = piece.ROOK;
					break;
					
				case 'B':
					pt = piece.BISHOP;
					break;
					
				case 'N':
					pt = piece.KNIGHT;
					break;
					
				default:
					System.out.println("Warning: Illegal string at chessboard.domove() " + sInput);
					//System.exit(0);
					break;
			}
		}
		
		
		if (sInput.length() >= 6) pt = (int)sInput.charAt(5)-48;
		
		if  ((x1 < 1) || (x2 < 1) || (y1 < 1) || ( y2 < 1) || (x1 > 8) || (x2 > 8) || (y1 > 8) || (y2 > 8)) return false;
		
		//System.out.println("DBG160109:domove(a2):" + sInput);
		
		piece p = blocks[x1][y1];
		if (p == null) return false;
		//System.out.println("DBG160109:domove(b1):" + sInput);
		if (p.iColor != iColor) return false;
		
		//System.out.println("DBG160109:domove(b):" + sInput);
		return domove (x1,y1,x2,y2,pt);
		
	}
	
	boolean domove_bylib(String sInput, int iColor) throws Exception
	{
		int x1,y1,x2,y2, pt;
		int iType = -1;
		x1 = -1;
		y1 = -1;
		
		x1 = -1;
		x2 = -1;
		y1 = -1;
		y2 = -1;
		pt = -1;
		
		if (sInput.length() == 0)
		{
			System.out.println("domove_bylib: empty input string! Fatal");
			System.exit(0);
		}
		
		if (sInput.charAt(sInput.length()-1)=='#') sInput = sInput.substring(0,sInput.length()-1);
		
		moveindex mi;
		if (iColor == piece.WHITE) mi = miWhiteMoveindex;
		else mi = miBlackMoveindex;
		
		System.out.println("domove_bylib(): sInput:" + sInput + " iColor:" + iColor);
		dump();
		
		if (sInput.equals("O-O"))
		{
			x1 = 5;
			x2 = 7;
			if (iColor == piece.WHITE)
			{
				y1 = 1;
				y2 = 1;
			}
			else
			{
				y1 = 8;
				y2 = 8;
			}
		}
		else if (sInput.equals("O-O-O"))
		{
			x1 = 5;
			x2 = 3;
			if (iColor == piece.WHITE)
			{
				y1 = 1;
				y2 = 1;
			}
			else
			{
				y1 = 8;
				y2 = 8;
			}
		}
		else if (sInput.length() == 2)
		{
			x2 = (int)sInput.charAt(0)-96;
			y2 = (int)sInput.charAt(1)-48;
			iType = piece.PAWN;
		}
		else if (sInput.length() == 3)
		{
			if (sInput.charAt(0) == 'N') iType = piece.KNIGHT;
			if (sInput.charAt(0) == 'B') iType = piece.BISHOP;
			if (sInput.charAt(0) == 'K') iType = piece.KING;
			if (sInput.charAt(0) == 'R') iType = piece.ROOK;
			if (sInput.charAt(0) == 'Q') iType = piece.QUEEN;
			if (iType == -1)
			{
				iType = piece.PAWN;
				x1 = (int)sInput.charAt(0)-96;
			}
			x2 = (int)sInput.charAt(1)-96;
			y2 = (int)sInput.charAt(2)-48;
		}
		else if (sInput.length() == 4)
		{
			if (sInput.charAt(0) == 'N') iType = piece.KNIGHT;
			if (sInput.charAt(0) == 'B') iType = piece.BISHOP;
			if (sInput.charAt(0) == 'K') iType = piece.KING;
			if (sInput.charAt(0) == 'R') iType = piece.ROOK;
			if (sInput.charAt(0) == 'Q') iType = piece.QUEEN;
			
			if ((sInput.charAt(1) >= 97) && (sInput.charAt(1) <= 104)) x1 = (int)sInput.charAt(1)-96;
			else if ((sInput.charAt(1) >= 49) && (sInput.charAt(1) <= 56)) y1 = (int)sInput.charAt(1)-48;
			else 
			{
				System.out.println("sInput:" + sInput + ", can not process!");
				System.exit(0);
			}
			
			x2 = (int)sInput.charAt(2)-96;
			y2 = (int)sInput.charAt(3)-48;
		}
		else
		{
			System.out.println("Unprocessed move : " + sInput);
			System.exit(0);
		}
		
		/*System.out.println("DBG150924: Entering moveindex analysis. iType = " + iType);
		System.out.println("DBG160121: x1 = " + x1 + ", x2 = " + x2 + ", y1 = " + y1 + ", y2 = " + y2);
		mi.dump();
		*/
		
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			//System.out.println("mi-analysis: " + m.p.iType + "-> " + m.xtar + "," + m.ytar);
			if ((m.xtar == x2) && (m.ytar == y2))
			{
				if (m.p.iType == iType)
				{
					if ((x1 == -1) && (y1 == -1))
					{
						x1 = m.p.xk;
						y1 = m.p.yk;
					}
					else if (x1 == m.p.xk) y1 = m.p.yk;
					else if (y1 == m.p.yk) x1 = m.p.xk;

				}
			}
			
			if ((x1 != -1) && (y1 != -1)) break;
		}
		
		//System.out.println("DBG150924: x1 = " + x1 + ", x2 = " + x2 + ", y1 = " + y1 + ", y2 = " + y2);
		
		if ((x1==-1) || (y1 == -1) || (x2 == -1) || (y2 == -1))
		{
			System.out.println("chessboard.domove_bylib() fatal failure!");
			System.out.println("DBG150924: x1 = " + x1 + ", x2 = " + x2 + ", y1 = " + y1 + ", y2 = " + y2);
			System.out.println("sInput : " + sInput);
			dump();
			
			throw new Exception ("chessboard.domove_bylib() ");
		}
		
		return domove (x1,y1,x2,y2,pt);
		
	}

	void dumpCoverages()
	{
		System.out.println("Coverage dump");
		System.out.println("blocks    regwhite  regblack  s-white   s-black   owner");
		
		for(int j=8;j >=1;j--)
		{
			for (int i=1;i<=8;i++)
			{
				piece p = blocks[i][j];
				if (p == null) System.out.print(".");
				else System.out.print(p.dumpchr());
			}
			System.out.print("  ");
			
			for (int i=1;i<=8;i++) 
			{
				if (bWhiteCoverage[i][j]) System.out.print("w");
				else System.out.print(".");
			}
			
			System.out.print("  ");
			
			for (int i=1;i<=8;i++) 
			{
				if (bBlackCoverage[i][j]) System.out.print("b");
				else System.out.print(".");
			}
			System.out.print("  ");
			
			/*
			for (int i=1;i<=8;i++) 
			{
				if (bWhiteCoverageXP[i][j]) System.out.print("w");
				else System.out.print(".");
			}
			
			System.out.print("  ");
			
			for (int i=1;i<=8;i++) 
			{
				if (bBlackCoverageXP[i][j]) System.out.print("b");
				else System.out.print(".");
			}
			*/
			
			
			
			for (int i=1;i<=8;i++) System.out.print(iWhiteStrike[i][j]);
			System.out.print("  ");
			for (int i=1;i<=8;i++) System.out.print(iBlackStrike[i][j]);
			System.out.print("  ");
			for (int i=1;i<=8;i++)
			{
				if ((iWhiteStrike[i][j] - iBlackStrike[i][j]) >= 2) System.out.print("W");
				else if ((iWhiteStrike[i][j] - iBlackStrike[i][j]) >= 1) System.out.print("w");
				else if ((iWhiteStrike[i][j] - iBlackStrike[i][j]) == 0) System.out.print(".");
				else if ((iWhiteStrike[i][j] - iBlackStrike[i][j]) >= -1) System.out.print("b");
				else System.out.print("B");
			}
			System.out.print("  ");
		
			System.out.println();
		}
	}
	
	void dumpProtThreat()
	{
		System.out.println();
		System.out.println("Protection-threat matrix:");
		
		for(int j=8;j >=1;j--)
		{
			for (int i=1;i<=8;i++) 
			{
				piece p = blocks[i][j];
				if (p == null) System.out.print(".");
				else if ((p.bProt) && (p.bThreat)) System.out.print("X");
				else if (p.bProt) System.out.print("p");
				else if (p.bThreat) System.out.print("t");
				else System.out.print("E");
				
				
			}
			System.out.println();
		}
	}
	
	void updateCoverages()
	{
		//System.out.println("DBG: updateCoverages() start");
		
		int iwkx = 0;
		int iwky = 0;
		int ibkx = 0;
		int ibky = 0;
		
		int iMaxMove = 0;
		
		Vector vWhiteHOF = new Vector();
		Vector vBlackHOF = new Vector();
		Vector vWhiteBQ = new Vector();
		Vector vBlackBQ = new Vector();
		
		iWhiteKingCtrlBlks = 0;
		iBlackKingCtrlBlks = 0;
		
		iWhitePawnAdvance = 0;
		iBlackPawnAdvance = 0;
		
		iWhiteEarlyGamePenalty = 0;
		iBlackEarlyGamePenalty = 0;
		
		iWhiteFreePawnPoints = 0;
		iBlackFreePawnPoints = 0;
		
		iMaxWhiteThreat = 0;
		iMaxBlackThreat = 0;

		iMaxWhiteThrProtBal = 0;
		iMaxBlackThrProtBal = 0;
	
		iWhiteKingCtrlBlks = 0;
		iBlackKingCtrlBlks = 0;
	
		iWhiteEarlyGamePenalty = 0;
		iBlackEarlyGamePenalty = 0;
		
		iWhiteKingSpace = 0;
		iBlackKingSpace = 0;
	
		bWhiteKingThreat = false;
		bBlackKingThreat = false;
		
		iMaxWhitePawn = 0;
		iMaxBlackPawn = 0;
		
		//System.out.println("updcov enter.");
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++) 
			{
				//System.out.print("#");
				bWhiteCoverage[i][j] = false;
				bBlackCoverage[i][j] = false;
			}
		
		for (int i=piece.PAWN; i<=piece.ROOK; i++)
		{
			iWhitePieceCount[i]=0;
			iBlackPieceCount[i]=0;
		}
		

		
		iWhitePawnColMin = new int[10];
		iWhitePawnColMax = new int[10];
		iBlackPawnColMin = new int[10];
		iBlackPawnColMax = new int[10];
		
		iWhitePawnColMin[0]=10;
		iWhitePawnColMax[0]=-1;
		iWhitePawnColMin[9]=10;
		iWhitePawnColMax[9]=-1;
		
		iBlackPawnColMin[0]=10;
		iBlackPawnColMax[0]=-1;
		iBlackPawnColMin[9]=10;
		iBlackPawnColMax[9]=-1;
				
		for (int i=1;i<=8;i++)
		{
			iWhitePawnColMin[i]=10;
			iWhitePawnColMax[i]=-1;
			iBlackPawnColMin[i]=10;
			iBlackPawnColMax[i]=-1;
			
			iWhiteKingPromDest[i] = -1;  // to capture free pawns
			iBlackKingPromDest[i] = -1;
			iWhiteKingDefDest[i] = -1;  // to capture free pawns
			iBlackKingDefDest[i] = -1;
			
			iWhiteUPPDest[i] = -1;
			iBlackUPPDest[i] = -1;
			
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				if (p != null)
				{
					if (p.iLastMove > iMaxMove) iMaxMove = p.iLastMove;
					
					if ((p.yk != j) ||(p.xk != i))
					{
						System.out.println("copy failure, piece at wrong coords!");
						dump();
						System.out.println("i:" + i + " j:"+j+ " p.xk:"+p.xk+" p.yk:"+p.yk);
						System.exit(0);
					}
					
					if (p.iColor == piece.WHITE)
					{
						iWhiteStrike[i][j] =  iWhiteStrike[i][j] + p.iProtCount;
						//if ((i==5) && (j==7)) System.out.println("DBG160123 iWS: " + iWhiteStrike[i][j] + " protc:" + p.iProtCount );
						//System.out.print("PIECE at " + p.xk + "," + p.yk + "("+ p.iType + "):");
						//Vector mv = (Vector)p.moveVector(this);
						Vector mv = (Vector)p.threatVector(this);
						
						iWhitePieceCount[p.iType]++;
						
						if (p.iType == piece.BISHOP)
						{
							if (((i+j) % 2) == 0) bWhiteBRBishop=true;
							if (((i+j) % 2) == 1) bWhiteWRBishop=true;
						}
						
		 				for (int k=0;k<mv.size();k++)
						{
							move m = (move)mv.elementAt(k);
							bWhiteCoverage[m.xtar][m.ytar] = true;
							iWhiteStrike[m.xtar][m.ytar]++;
							//if (p.iType != piece.PAWN) bWhiteCoverageXP[m.xtar][m.ytar] = true;
						}
						
						if (p.mSuperVector != null)
						{
							for (int k=0;k<p.mSuperVector.size();k++)
							{
								move m = (move)p.mSuperVector.elementAt(k);
								iWhiteStrike[m.xtar][m.ytar]++;
							}
						}
						
						if (p.iType == piece.PAWN)
						{
							//System.out.println("w pawn at " + i + "," + p.yk);
							iWhitePawnAdvance = iWhitePawnAdvance + p.yk;
							if (p.yk < iWhitePawnColMin[i]) iWhitePawnColMin[i] = p.yk;
							if (p.yk > iWhitePawnColMax[i]) iWhitePawnColMax[i] = p.yk;
							//System.out.println("Now min: " + iWhitePawnColMin[i]);
							
							if  (p.yk > iMaxWhitePawn) iMaxWhitePawn = p.yk;
						}
						
						if (p.iType == piece.KING) 
						{
							//System.out.println("DBG: WHITE KING FOUND!");
							iwkx = i;
							iwky = j;
						}
						
					}
					 
					if (p.iColor == piece.BLACK)
					{
						//System.out.print("PIECE at " + p.xk + "," + p.yk + "("+ p.iType + "):");
						//Vector mv = (Vector)p.moveVector(this);
						
						iBlackStrike[i][j] =  iBlackStrike[i][j] + p.iProtCount;
						
						Vector mv = (Vector)p.threatVector(this);
						
						iBlackPieceCount[p.iType]++;
						
						if (p.iType == piece.BISHOP)
						{
							if (((i+j) % 2) == 0) bBlackBRBishop=true;
							if (((i+j) % 2) == 1) bBlackWRBishop=true;
						}
						
						for (int k=0;k<mv.size();k++)
						{
							move m = (move)mv.elementAt(k);
							bBlackCoverage[m.xtar][m.ytar] = true;
							iBlackStrike[m.xtar][m.ytar]++;
							//if (p.iType != piece.PAWN) bBlackCoverageXP[m.xtar][m.ytar] = true;
						}
						
						if (p.mSuperVector != null)
						{
							//System.out.println("NOT NULL BLACK SUPERVECTOR!!!");
							for (int k=0;k<p.mSuperVector.size();k++)
							{
								move m = (move)p.mSuperVector.elementAt(k);
								//System.out.println("BLACK SUPERMOVE From "+ p.xk+"," + p.yk+" To: " + m.xtar +","+m.ytar);
								iBlackStrike[m.xtar][m.ytar]++;
							}
						}
						
						if (p.iType == piece.PAWN)
						{
							iBlackPawnAdvance = iBlackPawnAdvance + 9 - p.yk;
							if (p.yk < iBlackPawnColMin[i]) iBlackPawnColMin[i] = p.yk;
							if (p.yk > iBlackPawnColMax[i]) iBlackPawnColMax[i] = p.yk;
							//System.out.println("BFP DEBUG:" +i + "," + iBlackPawnColMin[i]);
							if (p.yk < (9-iMaxBlackPawn)) iMaxBlackPawn = 9-p.yk;
						}
						
						if (p.iType == piece.KING) 
						{
							//System.out.println("DBG: BLACK KING FOUND!");
							ibkx = i;
							ibky = j;
						}
					}
					
					if (p.iType == piece.KING) 
					{
						block b = new block(i,j);
						if ((p.iColor) == piece.WHITE) vWhiteHOF.addElement(b);
						else vBlackHOF.addElement(b);
					}
					
					if (p.iType == piece.ROOK)
					{
						block b = new block(i,j);
						if ((p.iColor) == piece.WHITE) vWhiteHOF.addElement(b);
						else vBlackHOF.addElement(b);
					}
					
					if (p.iType == piece.QUEEN)
					{
						block b = new block(i,j);
						if ((p.iColor) == piece.WHITE) 
						{	
							vWhiteHOF.addElement(b);
							vWhiteBQ.addElement(b);
						}
						else
						{
							vBlackHOF.addElement(b);
							vBlackBQ.addElement(b);
						}
					}
					
					if (p.iType == piece.BISHOP)
					{
						//System.out.println("DBG150120:bishop!");
						block b = new block(i,j);
						if ((p.iColor) == piece.WHITE) vWhiteBQ.addElement(b);
						else vBlackBQ.addElement(b);
						//System.out.println("DBG150120:bishop!"+vWhiteBQ.size());
					}
				}				
			}
		}
		
		
		// == max threat calc starts 150207 observation !!!
		//calcMaxThreats();
		
		
		// max threat calc over !!!

		// double pawns
		iWhiteDoublePawns = 0;	
		iBlackDoublePawns = 0;	
		for (int i=1;i<=8;i++) 
		{
			int wp = 0;
			int bp = 0;
			for (int j=1;j<=8;j++) 
			{
				piece p = blocks[i][j];
				if (p!=null)
				{
					if (p.iType == piece.PAWN)
					{
						if (p.iColor == piece.WHITE) wp++;
						else bp++;
						
						if ((!p.bProt) && (!p.bThreat))
						{
							if (p.iColor == piece.WHITE) iWhiteUPPDest[i] = j;
							else if (iBlackUPPDest[i] == -1) iBlackUPPDest[i] = j;
						}
					}
				}
			}
			if (wp >= 2) iWhiteDoublePawns = iWhiteDoublePawns + wp - 1;
			if (bp >= 2) iBlackDoublePawns = iBlackDoublePawns + bp - 1;
		}
		
		// control of centerboard
		iWhiteCenterCtrlPts = 0;
		iBlackCenterCtrlPts = 0;
		
		for (int i=4; i <= 5; i++)
			for (int j=4; j <= 5; j++)
			{
				if (bWhiteCoverage[i][j]) iWhiteCenterCtrlPts++;
				if (bBlackCoverage[i][j]) iBlackCenterCtrlPts++;
				
				piece p = blocks[i][j];
				if (p != null)
				{
					if (p.iColor == piece.WHITE)
					{
						iWhiteCenterCtrlPts++;
						if (p.bProt) iWhiteCenterCtrlPts++;
						if (!p.bThreat) iWhiteCenterCtrlPts++;
					}
					else
					{
						iBlackCenterCtrlPts++;
						if (p.bProt) iBlackCenterCtrlPts++;
						if (!p.bThreat) iBlackCenterCtrlPts++;
					}
				}
			}
		
		// control of blocks close to king
		for (int i=Math.max(iwkx-1,1);i<=Math.min(iwkx+1,8);i++)
			for (int j = Math.max(iwky-1,1);j<=Math.min(iwky+1,8);j++)
			{
				if (bWhiteCoverage[i][j]) iWhiteKingCtrlBlks++;
				if (bBlackCoverage[i][j]) iBlackKingCtrlBlks++;
				piece p=blocks[i][j];
				if ((p!=null) && (p.iColor == piece.WHITE)) 
				{	
					iWhiteKingCtrlBlks++;
					iWhiteKingCtrlFix++;
				}
			}
		
		
		for (int i=Math.max(ibkx-1,1);i<=Math.min(ibkx+1,8);i++)
			for (int j = Math.max(ibky-1,1);j<=Math.min(ibky+1,8);j++)
			{		
				if (bWhiteCoverage[i][j]) iWhiteKingCtrlBlks++;
				if (bBlackCoverage[i][j]) iBlackKingCtrlBlks++;
				piece p=blocks[i][j];
				if ((p!=null) && (p.iColor == piece.BLACK)) 
				{
					iBlackKingCtrlBlks++;
					iBlackKingCtrlFix++;
				}
			}		
			
		//System.out.println("DBG160331: KING CTRL: WHITE:"+ iWhiteKingCtrlBlks + " BLACK:" + iBlackKingCtrlBlks);		
			
		if ((iwkx==1) || (iwkx ==8)) iWhiteKingCtrlFix=iWhiteKingCtrlFix+3;	
		if ((iwky==1) || (iwky ==8)) iWhiteKingCtrlFix=iWhiteKingCtrlFix+3;	
		if ((ibkx==1) || (ibkx ==8)) iBlackKingCtrlFix=iBlackKingCtrlFix+3;	
		if ((ibky==1) || (ibky ==8)) iBlackKingCtrlFix=iBlackKingCtrlFix+3;
		
		// early game penalty
		if (iMaxMove < 9)
		{
			if (!findpiece (piece.PAWN,piece.WHITE,1,2)) iWhiteEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.WHITE,2,2)) iWhiteEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.WHITE,3,2)) iWhiteEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.WHITE,6,2)) iWhiteEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.WHITE,7,2)) iWhiteEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.WHITE,8,2)) iWhiteEarlyGamePenalty++;
			if (!findpiece (piece.KING,piece.WHITE,5,1)) iWhiteEarlyGamePenalty=iWhiteEarlyGamePenalty+2;
			if (!findpiece (piece.QUEEN,piece.WHITE,4,1)) iWhiteEarlyGamePenalty=iWhiteEarlyGamePenalty+2;
			
			if (!findpiece (piece.PAWN,piece.BLACK,1,7)) iBlackEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.BLACK,2,7)) iBlackEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.BLACK,3,7)) iBlackEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.BLACK,6,7)) iBlackEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.BLACK,7,7)) iBlackEarlyGamePenalty++;
			if (!findpiece (piece.PAWN,piece.BLACK,8,7)) iBlackEarlyGamePenalty++;
			if (!findpiece (piece.KING,piece.BLACK,5,8)) iBlackEarlyGamePenalty=iBlackEarlyGamePenalty+2;
			if (!findpiece (piece.QUEEN,piece.BLACK,4,8)) iBlackEarlyGamePenalty=iBlackEarlyGamePenalty+2;
			
		}
		
		// assess king areas 
		iWhiteKingSpace = assessKingArea(iwkx,iwky);
		iBlackKingSpace = assessKingArea(ibkx,ibky);
		
		iWhiteFreePawnCtrlPoints = 0;
		iBlackFreePawnCtrlPoints = 0;
		
		// free pawn counters:
		for (int i=1; i <= 8; i++)
		{
			//System.out.println("FP_DEBUG:"+ i + " wmin: " + iWhitePawnColMin[i] + " wmax: " + iWhitePawnColMax[i]+"   bmin:" + iBlackPawnColMin[i] + "  bmax:" + iBlackPawnColMax[i]);
			
			if ((iWhitePawnColMax[i] > -1) && 
			    (iWhitePawnColMax[i] > iBlackPawnColMax[i]) &&
				(iWhitePawnColMax[i] >= iBlackPawnColMax[i-1]) &&
				(iWhitePawnColMax[i] >= iBlackPawnColMax[i+1]))
			{
				iWhiteFreePawnPoints = iWhiteFreePawnPoints + iWhitePawnColMax[i];
				if (iWhitePawnColMax[i] >=5) iWhiteFreePawnPoints++;
				if (iWhitePawnColMax[i] >=6) iWhiteFreePawnPoints = iWhiteFreePawnPoints+3;
				if (iWhitePawnColMax[i] >=7) iWhiteFreePawnPoints = iWhiteFreePawnPoints+8;
				
				//System.out.println("FP DEBUG(0) BLACK ADD:"+i + " bctrl:" + iBlackFreePawnCtrlPoints);
				
				piece p = blocks[i][iWhitePawnColMax[i]];
				if (p.bProt) iWhiteFreePawnCtrlPoints = iWhiteFreePawnCtrlPoints+2;
				if (p.bThreat) iBlackFreePawnCtrlPoints = iBlackFreePawnCtrlPoints+2;
				
				if (bWhiteCoverage[i][iWhitePawnColMax[i]+1]) iWhiteFreePawnCtrlPoints =iWhiteFreePawnCtrlPoints+2;
				if (bBlackCoverage[i][iWhitePawnColMax[i]+1]) iBlackFreePawnCtrlPoints =iBlackFreePawnCtrlPoints+2;
				
				for (int jj = iWhitePawnColMax[i]+1; jj <= 8; jj++)
				{
					if (bWhiteCoverage[i][jj]) iWhiteFreePawnCtrlPoints =iWhiteFreePawnCtrlPoints+1;
					if (bBlackCoverage[i][jj]) iBlackFreePawnCtrlPoints =iBlackFreePawnCtrlPoints+1;
				}
				
				iWhiteKingPromDest[i] = iWhitePawnColMax[i];  // to capture free pawns
				//iBlackKingDefDest[i] = 7;
				iBlackKingDefDest[i] = Math.min(7,iWhitePawnColMax[i]+1);  // 150127
				
			}
			
			if ((iBlackPawnColMin[i] < 10) && 
			    (iBlackPawnColMin[i] < iWhitePawnColMin[i]) &&
				(iBlackPawnColMin[i] <= iWhitePawnColMin[i-1]) &&
				(iBlackPawnColMin[i] <= iWhitePawnColMin[i+1]))
			{
				iBlackFreePawnPoints = iBlackFreePawnPoints + (9 - iBlackPawnColMin[i]);
				//System.out.println("FP DEBUG(1) BLACK ADD:"+i + " bctrl:" + iBlackFreePawnCtrlPoints);
				if (iBlackPawnColMin[i] <=4) iBlackFreePawnPoints++;
				if (iBlackPawnColMin[i] <=3) iBlackFreePawnPoints = iBlackFreePawnPoints+3;
				if (iBlackPawnColMin[i] <=2) iBlackFreePawnPoints = iBlackFreePawnPoints+8;
				
				piece p = blocks[i][iBlackPawnColMin[i]];
				
				if (p.bProt) iBlackFreePawnCtrlPoints = iBlackFreePawnCtrlPoints+2;
				if (p.bThreat) iWhiteFreePawnCtrlPoints = iWhiteFreePawnCtrlPoints+2;
				
				if (bWhiteCoverage[i][iBlackPawnColMin[i]-1]) iWhiteFreePawnCtrlPoints =iWhiteFreePawnCtrlPoints+2;
				if (bBlackCoverage[i][iBlackPawnColMin[i]-1]) iBlackFreePawnCtrlPoints =iBlackFreePawnCtrlPoints+2;
				
				for (int jj = iBlackPawnColMin[i]-1; jj >= 1; jj--)
				{
					if (bWhiteCoverage[i][jj]) iWhiteFreePawnCtrlPoints =iWhiteFreePawnCtrlPoints+1;
					if (bBlackCoverage[i][jj]) iBlackFreePawnCtrlPoints =iBlackFreePawnCtrlPoints+1;
				}
				
				//System.out.println("FP DEBUG(2) BLACK ADD:"+i + " bctrl:" + iBlackFreePawnCtrlPoints);
				
				iBlackKingPromDest[i] = iBlackPawnColMin[i];  // to capture free pawns
				//iWhiteKingDefDest[i] = 2;
				iWhiteKingDefDest[i] = Math.max(2,iBlackPawnColMin[i]-1);  // 150127
				
			}
			
		}
		
		int iWhitePawnProtPen = 0;
		int iBlackPawnProtPen = 0;
		for (int i=1; i<=8;i++)
		{
			if (iWhitePawnColMax[i] > -1)
			{
				piece p = blocks[i][iWhitePawnColMax[i]];
				if (!p.bProt) iWhitePawnProtPen = iWhitePawnProtPen + 2;
				else
				{
					boolean bPawnProt = false;
					if ((i>1) && (iWhitePawnColMax[i-1]+1 == iWhitePawnColMax[i])) bPawnProt = true;
					if ((i<8) && (iWhitePawnColMax[i+1]+1 == iWhitePawnColMax[i])) bPawnProt = true;
					if (!bPawnProt) iWhitePawnProtPen = iWhitePawnProtPen + 1;
				}
			}
			
			if (iBlackPawnColMin[i] < 10)
			{
				piece p = blocks[i][iBlackPawnColMin[i]];
				if (!p.bProt) iBlackPawnProtPen = iBlackPawnProtPen + 2;
				else
				{
					boolean bPawnProt = false;
					if ((i>1) && (iBlackPawnColMin[i-1]-1 == iBlackPawnColMin[i])) bPawnProt = true;
					if ((i<8) && (iBlackPawnColMin[i+1]-1 == iBlackPawnColMin[i])) bPawnProt = true;
					if (!bPawnProt) iBlackPawnProtPen = iBlackPawnProtPen + 1;
				}
			}
		}
		//System.out.println("DBG150420: iWhitePawnProtPen="+ iWhitePawnProtPen + " iBlackPawnProtPen="+iBlackPawnProtPen);
		iPawnProtPenBal = iBlackPawnProtPen - iWhitePawnProtPen;
		
		king wk = (king)blocks[iwkx][iwky];
		king bk = (king)blocks[ibkx][ibky];
		//if (bk == null) System.out.println("BK IS NULL! BLACK KING COORDS:" + ibkx + "," + ibky + "  White king at " + iwkx +"," + iwky);
		if ((wk != null) && (wk.bThreat)) bWhiteKingThreat = true;
		if ((bk != null) && (bk.bThreat)) bBlackKingThreat = true;
		
		analyzeBCS(vBlackHOF, vWhiteBQ, piece.WHITE);
		analyzeBCS(vWhiteHOF, vBlackBQ, piece.BLACK);
	}
	
	void calcMaxThreats()
	{
		//System.out.println("DBG150207: Enter calcMaxThreats() " + lastmoveString());
		
		iWhiteCovered = 0;
		iBlackCovered = 0;
		
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++) 
			{
				if (bWhiteCoverage[i][j]) iWhiteCovered++;
				if (bBlackCoverage[i][j]) iBlackCovered++;
			}	
		
		// protected pieces & pieces under threat
		iWhiteUnprotThreat = 0;	
		iBlackUnprotThreat = 0;	
		
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++) 
		{
			piece p = blocks[i][j];
			if ((p != null)  && (p.iType != piece.KING))
			{
				// $$$$ 140411 add processing for 
				//int iMaxWhiteThrProtBal;
				//int iMaxBlackThrProtBal;
				//System.out.println("CalcMaxThreats: piece at " + i +"," + j + " thr:" + p.bThreat);
				
				if ((iWhiteStrike[i][j] > iBlackStrike[i][j]) && (p.iColor == piece.BLACK)) iBlacksInWhiteBlock++;
				if ((iWhiteStrike[i][j] < iBlackStrike[i][j]) && (p.iColor == piece.WHITE)) iWhitesInBlackBlock++;
				
				if (p.bThreat)
				{
					if(!p.bProt)
					{
						if (p.iColor == piece.BLACK)
							iWhiteUnprotThreat = iWhiteUnprotThreat +  p.pvalue();
						else
							iBlackUnprotThreat = iBlackUnprotThreat + p.pvalue();
					}
					if (p.iColor == piece.WHITE)
					{
						if (p.pvalue() > iMaxWhiteThreat) iMaxWhiteThreat = p.pvalue();
						//System.out.println("DBG 140411a:" + p.iType +"," +p.pvalue()+ "," + p.iMinThreat + "," + p.bProt);
						if (p.bProt)
						{
							//System.out.println("DBG 140411a0: " + iMaxWhiteThrProtBal);
							if ((p.pvalue() - p.iMinThreat) > iMaxWhiteThrProtBal) iMaxWhiteThrProtBal = p.pvalue() - p.iMinThreat;
							//System.out.println("DBG 140411a1: " + iMaxWhiteThrProtBal);
						}
						else
						{
							//System.out.println("DBG 140411b0: " + iMaxWhiteThrProtBal);
							if (p.pvalue() > iMaxWhiteThrProtBal) iMaxWhiteThrProtBal = p.pvalue();
							//System.out.println("DBG 140411b1: " + iMaxWhiteThrProtBal);
						}
						//System.out.println("DBG 140411b: " + iMaxWhiteThrProtBal);
						
					}
					else
					{
						if (p.pvalue() > iMaxBlackThreat) iMaxBlackThreat = p.pvalue();
						if (p.bProt)
						{
							if ((p.pvalue() - p.iMinThreat) > iMaxBlackThrProtBal) iMaxBlackThrProtBal = p.pvalue() - p.iMinThreat;
						}
						else
						{
							if (p.pvalue() > iMaxBlackThrProtBal) iMaxBlackThrProtBal = p.pvalue();
						}
					}
				}
			}
		}
		//System.out.println("DBG150207: Leave calcMaxThreats("+ lastmoveString() + "): W:" + iMaxWhiteThreat + " B:"+ iMaxBlackThreat + " Bal: " + iMaxWhiteThrProtBal + " B: "+ iMaxBlackThrProtBal);
	}
	
	boolean findpiece(int itype, int icolor, int i, int j)
	{
		piece p = blocks[i][j];
		if (p==null) return false;
		if (p.iType != itype) return false;
		if (p.iColor != icolor) return false;
		return p.iLastMove == 0;
	}
	
	int assessKingArea(int i, int j)
	{
		piece p = blocks[i][j];
		
		if (p == null) return -1;
		if (p.iType != piece.KING) return -1;
		int iCol = p.iColor;
		
		boolean ka[][] = new boolean[9][9];
		boolean bBlock[][];
		
		for (int ii=1;ii<9;ii++) 
			for (int jj=1;jj<9;jj++) ka[ii][jj] = false;
		
		if (iCol == piece.BLACK) bBlock = bWhiteCoverage;
		else bBlock = bBlackCoverage;
		
		int iKa = 0;
		for (int ii=i-1; ii<=i+1;ii++)
			for (int jj=j-1;jj<=j+1;jj++)
		{
			if ((ii!=i) || (jj!=j)) 
			{
				iKa = extendKingArea(ii,jj,bBlock,ka,iKa);
			}
		}
		/*
		System.out.println("assessKingArea returns:" + iKa);
		for(int jj=8;jj >=1;jj--)
		{
			for (int ii=1;ii<=8;ii++) 
			{
				if (ka[ii][jj]) System.out.print("K");
				else System.out.print(".");
			}
			System.out.println();
		}
		*/
		return iKa;
	}
	
	int extendKingArea(int i, int j, boolean bBlock[][], boolean bKA[][], int iKa)
	{
		if ((i < 1) || (i > 8) || (j < 1) || (j > 8)) return iKa;
		
		if (bBlock[i][j]) return iKa ;
		
		if (blocks[i][j] != null) return iKa;
		
		if (!bKA[i][j])
		{
			bKA[i][j]=true;
			iKa++;
			for (int ii=i-1; ii<=i+1;ii++)
				for (int jj=j-1;jj<=j+1;jj++)
			{
				if ((ii!=i) || (jj!=j)) 
				{
					iKa = extendKingArea(ii,jj,bBlock,bKA,iKa);
				}
			}
			
			
			return iKa;
		}
		else return iKa;
		
	}
	
	int assessQESPoints(int xk, int yk)
	{
		piece p = blocks[xk][yk];
		if (p == null) return 0;
		if (p.iType != piece.QUEEN) return 0;
		
		boolean bBlock[][];
		int iEM = 0;
		
		if (p.iColor == piece.WHITE) bBlock = bBlackCoverage;
		else bBlock = bWhiteCoverage; 
		
		boolean [][] QESDir;
		QESDir = new boolean[3][3];
		
		for (int i=0;i<3;i++)
			for (int j=0;j<3;j++) QESDir[i][j] = false;
		
		Vector mv = p.moveVector(this);
		for (int i=0;i<mv.size();i++)
		{
			move m = (move)mv.elementAt(i);
			if ((!bBlock[m.xtar][m.ytar]) && (blocks[m.xtar][m.ytar] == null))
			{
				iEM++;
				//System.out.println("QESADD(m): " + p.iColor + " to " + m.xtar + "," + m.ytar);
				QESDir[1+(int)Math.signum(m.xtar-xk)][1+(int)Math.signum(m.ytar-yk)] = true;
			}
			if (m.bCapture)
			{
				piece p2 = blocks[m.xtar][m.ytar];
				if (!p2.bProt)
				{
					iEM++;
					//System.out.println("QESADD(c): " + p.iColor + " to " + m.xtar + "," + m.ytar);
					QESDir[1+(int)Math.signum(m.xtar-xk)][1+(int)Math.signum(m.ytar-yk)] = true;
				}
			}
		}
		
		int iEMDir = 0;
		
		for (int i=0;i<3;i++)
			for (int j=0;j<3;j++)
				if (QESDir[i][j]) iEMDir++;
		
		if (iEM >= 1) iEM = iEM+3;
		if (iEM >= 4) iEM = iEM+1;
		
		if (iEMDir >= 2) iEM = iEM +1;
		if (iEMDir >= 3) iEM = iEM +1;
		
		
		return iEM;
		
	}
	
	void analyzeBCS (Vector vHOF, Vector vBQ, int iColor)
	{
		
		//System.out.println("DBG150120: analyzeBCS() vector sizes: " + vHOF.size() + "," + vBQ.size());
		
		// plan 150121 -> candidates to vector of blocks, check if there are matches between cands and vbq:s. if yes, check if vbq piece can reach at least 2 vhof elements
		// use bishop.canReach() && queen.canReach()  !!!!
		// check if they are protected too -> if it's queen -> value:0, if bishop:2
		// check availability of clever countermove where one R under threat escapes to protected position between threat and other R -> nullify the BCS
		
		if (vHOF.size() < 2) return;
		if (vBQ.size() < 1) return;
		
		Vector vCand = new Vector();
		
		if (iColor == piece.WHITE) 
		{
			vWhiteBCTargets = new Vector();
			vBlackBCHOF = vHOF;
			
		}
		else
		{
			vBlackBCTargets = new Vector();
			vWhiteBCHOF = vHOF;
		}
		
		/*for (int i=0;i<vBQ.size();i++)
		{
			block bbq = (block)vBQ.elementAt(i);
			System.out.println("DBG:150120: BQ: " + bbq.xk +"," + bbq.yk);
			*/
			for (int j=0;j<vHOF.size();j++)
			{
				block blh = (block)vHOF.elementAt(j);
				//if (((bbq.xk+bbq.yk) %2) == ((blh.xk+blh.yk) %2))
				{
					for (int k=j+1;k<vHOF.size();k++)
					{
						block blh2 = (block)vHOF.elementAt(k);
						if (((blh2.xk+blh2.yk) %2) == ((blh.xk+blh.yk) %2))
						{
							//System.out.println("DBG150120: analyzeBCS(): candidate:" + blh.xk +"," + blh.yk + " & " + blh2.xk +"," + blh2.yk );
							int cx = 0;
							int cy = 0;
							if (blh.yk == blh2.yk)
							{
								cx = (blh.xk + blh2.xk) / 2;
								cy = blh.yk + Math.abs(blh.xk-cx);
								//System.out.println("DBG:150120: cand:" + cx +"," +cy);
								block bc = new block(cx,cy);
								vCand.addElement(bc);
								cy = blh.yk - Math.abs(blh.xk-cx);
								//System.out.println("DBG:150120: cand:" + cx +"," +cy);
								bc = new block(cx,cy);
								vCand.addElement(bc);
							}
							else if (blh.xk == blh2.xk)
							{
								cy = (blh.yk + blh2.yk) / 2;
								cx = blh.xk + Math.abs(blh.yk-cy);
								//System.out.println("DBG:150120: cand:" + cx +"," +cy);
								block bc = new block(cx,cy);
								vCand.addElement(bc);
								cx = blh.xk - Math.abs(blh.yk-cy);
								//System.out.println("DBG:150120: cand:" + cx +"," +cy);
								bc = new block(cx,cy);
								vCand.addElement(bc);
							}
							else if (Math.abs(blh.xk-blh2.xk) == Math.abs(blh.yk-blh2.yk))
							{
								// loop through them all
								block bxmin, bxmax;
								if (blh.xk < blh2.xk)
								{
									bxmin = blh;
									bxmax = blh2;
								}
								else 
								{
									bxmin = blh2;
									bxmax = blh;
								}
								int xx = bxmin.xk+1;
								while (xx<bxmax.xk)
								{
									int yy = bxmin.yk + (xx-bxmin.xk)* (int)(Math.signum(bxmax.yk-bxmin.yk));
									//System.out.println("DBG150120:cand:" + xx + "," + yy);
									block bc = new block(xx,yy);
									vCand.addElement(bc);
									xx++;
								}
								
							}
							else if (Math.abs(blh.xk-blh2.xk) > Math.abs(blh.yk-blh2.yk))
							{
								// calculate possibilities xdiff bigger than ydiff
								block bxmin, bxmax;
								block be1 = new block(-1,-1);
								block be2 = new block(-1,-1);
								if (blh.xk < blh2.xk)
								{
									bxmin = blh;
									bxmax = blh2;
								}
								else 
								{
									bxmin = blh2;
									bxmax = blh;
								}
								be1.yk=bxmin.yk;
								//System.out.println("DBG150127:AA:" + bxmin.xk + "+" + bxmax.yk +"-" + bxmin.yk);
								if (bxmax.yk > bxmin.yk) be1.xk=bxmin.xk+(bxmin.yk-bxmax.yk);
								else be1.xk = bxmax.xk - (bxmin.yk-bxmax.yk);
								//System.out.println("DBG150127: be1xk:" + be1.xk);
								
								be2.yk=bxmax.yk;
								if (bxmax.yk < bxmin.yk) be2.xk=bxmin.xk+(bxmin.yk-bxmax.yk);
								else be2.xk = bxmax.xk - (bxmax.yk-bxmin.yk);
								
								
								//System.out.println("DBG150120:"+bxmin.xk+","+bxmin.yk+"&"+bxmax.xk+","+bxmax.yk);
								int idiff = ((bxmin.xk-bxmin.yk)-(bxmax.xk-bxmax.yk)) / 2;
								//System.out.println("DBG150120:idiff:" + idiff);
								int x0,y0;
								x0 = bxmin.xk+Math.abs(idiff);
								y0 = bxmin.yk+(int)(Math.signum(idiff)*Math.abs(idiff));
								//System.out.println("DBG150120:cand:" + x0 + "," + y0);
								block bc = new block(x0,y0);
								vCand.addElement(bc);
								x0 = bxmax.xk - Math.abs(idiff);
								y0 = bxmax.yk - (int)(Math.signum(idiff)*Math.abs(idiff));
								bc = new block(x0,y0);
								vCand.addElement(bc);
								//System.out.println("DBG150120:cand:" + x0 + "," + y0);
								
								// $$$$ add horizontal move to protected square to block threat of other piece check !!!
								
								//System.out.println("DBG150127:escape at " + be1.xk +"," + be1.yk);
								//System.out.println("DBG150127:escape at " + be2.xk +"," + be2.yk);
								
								
							}
							else
							{
								// calculate possibilities ydiff bigger than xdiff
								block bymin, bymax;
								block be1 = new block(-1,-1);
								block be2 = new block(-1,-1);
								if (blh.yk < blh2.yk)
								{
									bymin = blh;
									bymax = blh2;
								}
								else 
								{
									bymin = blh2;
									bymax = blh;
								}
								
								be1.xk = bymin.xk;
								if (bymax.xk < bymin.xk) be1.yk = bymin.yk + (bymin.xk-bymax.xk);
								else be1.yk = bymax.yk - (bymax.xk-bymin.xk);
								
								be2.xk=bymax.xk;
								if (bymax.xk > bymin.xk) be2.yk = bymin.yk+(bymax.xk-bymin.xk);
								else be2.yk = bymax.yk - (bymax.xk-bymin.xk);
								
								//System.out.println("DBG150120:"+bymin.xk+","+bymin.yk+"&"+bymax.xk+","+bymax.yk);
								int jdiff = ((bymin.yk-bymin.xk)-(bymax.yk-bymax.xk)) / 2;
								//System.out.println("DBG150120:jdiff:" + jdiff);
								int x0,y0;
								y0 = bymin.yk+Math.abs(jdiff);
								x0 = bymin.xk+(int)(Math.signum(jdiff)*Math.abs(jdiff));
								//System.out.println("DBG150120:cand:" + x0 + "," + y0);
								block bc = new block(x0,y0);
								vCand.addElement(bc);
								y0 = bymax.yk - Math.abs(jdiff);
								x0 = bymax.xk - (int)(Math.signum(jdiff)*Math.abs(jdiff));
								//System.out.println("DBG150120:cand:" + x0 + "," + y0);
								bc = new block(x0,y0);
								vCand.addElement(bc);
								
								// $$$$ add horizontal move to protected square to block threat of other piece check !!!
								//System.out.println("DBG150127:escape at " + be1.xk +"," + be1.yk);
								//System.out.println("DBG150127:escape at " + be2.xk +"," + be2.yk);
							}
							
						}
					}
				}
			}
		//}  // vbq loop end
		
		
		//System.out.println("DBG150122: vCand size:" + vCand.size());
		for (int i=0;i<vCand.size();i++)
		{	
			block bC = (block)vCand.elementAt(i);
			
			int iProt = 0;
			int iUnProt = 0;
			//System.out.println("DBG150122:vCand: " + bC.xk +"," + bC.yk);
			
			if ((bC.xk >= 1) && (bC.xk <= 8) && (bC.yk >= 1) && (bC.yk <= 8))
			{
				if (iColor == piece.WHITE) vWhiteBCTargets.addElement(bC);
				else vBlackBCTargets.addElement(bC);
			}
			
			for (int j=0;j<vBQ.size();j++)
			{
				block bBQ = (block)vBQ.elementAt(j);
				if ((bC.xk == bBQ.xk) && (bC.yk == bBQ.yk)) 
				{
					//System.out.println("DBG150122: Cand piece at BCS found: " + bC.xk + "," + bC.yk);
					// check can reach using method
					piece pBQ = blocks[bC.xk][bC.yk];
					iColor = pBQ.iColor;
					//System.out.println("DBG150703: analBCS: BQType" + pBQ.iType);
					
					if (!pBQ.bThreat)
					{
						for (int k=0;k<vHOF.size();k++)
						{
							block bHO = (block)vHOF.elementAt(k);
							piece p = blocks[bHO.xk][bHO.yk];
							if (pBQ.canReach(bC.xk,bC.yk,p,this))
							{
								//System.out.println("Potential match at " + p.xk + "," + p.yk);
								if ((p.bProt) && (p.iType == piece.ROOK)) iProt++;
								else iUnProt++;
							}

						}
						
						// if yes, check protection for 2 pieces
						// king + unprot -> 4
						// 2 unprot -> 4
						// any 2 -> 2 for pawn, 0 for queen
						// own piece state threat or not
						if ((bC.xk >= 1) && (bC.xk <= 8) && (bC.yk >= 1) && (bC.yk <= 8))
						{
							// add to bcsblocks list in chessboard object to assess whether there are BCS moves by queens or bishops
							int iBCS;
							
							//System.out.println("DBG150122:Ready to Add " + bC.xk + "," + bC.yk + "with unprot = " + iUnProt + " prot = " + iProt);
							//System.out.println("DBG150122: pBQ type:" + pBQ.iType);
							
							if (iUnProt >= 2) iBCS = 4;
							else if (iUnProt + iProt >= 2) iBCS = 2;
							else iBCS = 0;
						
							//System.out.println("DBG150703 BCS: iProt:"+iProt + " iUnProt:" + iUnProt);
							
							if ((iUnProt <= 1) && (pBQ.iType == piece.QUEEN)) iBCS = 0;
								
							if (iColor == piece.WHITE) iWhiteBCS = iBCS;
							else iBlackBCS = iBCS;
							
				
							
							
						}
					}	
					//else System.out.println("DBG 150315: BCS not true, under threat!");	
				}
			}
			

		}
		//System.out.println("DBG150120: analyzeBCS() returns.");
	}
	
	boolean bMoveIsBCS(piece p, move m)
	{
		Vector v, vHOF;
		boolean bRet = false;
		
		if ((p.iType != piece.QUEEN) && (p.iType != piece.BISHOP)) return false;
		
		if (p.iColor == piece.WHITE)
		{
			v = vWhiteBCTargets;
			vHOF = vBlackBCHOF;
		}
		else
		{
			v = vBlackBCTargets;
			vHOF = vWhiteBCHOF;
		}
		
		//System.out.println("DBG150128:bMoveIsBCS called for:" + m.moveStr());
		if (vHOF == null) return false;
		//System.out.println("DBG150309: vHOF size:" + vHOF.size());
		
		if (v == null) return false;
		
		//System.out.println("DBG150128:bMoveIsBCS:A:" + m.moveStr() + " size:" + v.size());
		
		for (int i=0;i<v.size();i++)
		{
			block b = (block)v.elementAt(i);
			//System.out.println("DBG150128:bMoveIsBCS:AA:" + b.xk +"," + b.yk);
			if ((b.xk == m.xtar) && (b.yk == m.ytar))
			{
				//System.out.println("Maybe BCS!*******");
				int iReached = 0;
				piece p2 = blocks[b.xk][b.yk];
				blocks[b.xk][b.yk] = p;
				int vx, vy;
				vx = p.xk;
				vy = p.yk;
				p.xk = b.xk;
				p.yk = b.yk;
				if (p2 == null)
				{
					blocks[b.xk][b.yk] = p;
					
					for (int j=0;j<vHOF.size();j++)
					{
						block b2 = (block)vHOF.elementAt(j);
						//System.out.println("DBG150309: b2: " + b2.xk + "," + b2.yk);
						
						if (p.canReach(b2.xk,b2.yk,p,this))
						{
							//System.out.println("reachable: " + b2.xk +"," + b2.yk);
							if (p.iType != piece.QUEEN) iReached++;
							else
							{
								piece pr = blocks[b2.xk][b2.yk];
								//System.out.println("BCS cand at " + b2.xk + "," + b2.yk);
								if ((!pr.bProt) || (pr.iType == piece.KING)) iReached++;
							}
							
						}
					}
				}
				blocks[b.xk][b.yk] = p2;
				p.xk = vx;
				p.yk = vy;
				//System.out.println("DBG150128:bMoveIsBCS:B:" + m.moveStr() + " ireached :" + iReached) ;
				if (iReached >= 2) bRet = true;
			}
			//else System.out.println("DBG150309: no BCS");
			
		}
		
		//if (iReached <= 1) return false;
		//if (bRet) System.out.println("DBG150703: BCS returns: " + bRet + " for:" + m.moveStr());
		return bRet;
		
	}

	void analyzeMove(piece p, move m, int iAlg)
	{
		chessboard nb = new chessboard();
		nb = this.copy();
		
		piece np = nb.blocks[p.xk][p.yk];
		
		nb.domove(np,m,-1);
		nb.updateCoverages();
		
		System.out.println("Balances: " + nb.pvaluesum(piece.WHITE) + "-" + nb.pvaluesum(piece.BLACK) + "  ,  " +  nb.iWhiteCovered + "-" + nb.iBlackCovered);
		
		
	}
	
	chessboard findAndDoBestMove(int iColor, int iRounds, movevalue basemv, int iAlg, boolean bDebug, gamehistory ghist, mval_vector mvv, boolean bDeeper, dt_control dtc, movevalue l3best, movevalue l2best, regbest regb, mostore mos) throws Exception
	{
		//return findAndDoBestMove(iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv,bDeeper,dtc,l3best,l2best,null,CB_MAXTIME, false, regb, mos);
		String sTemp = "";
		return findAndDoBestMove(iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv,bDeeper,dtc,l3best,l2best,sTemp,CB_MAXTIME, false, regb, mos);
	}

	
	chessboard findAndDoBestMove(int iColor, int iRounds, movevalue basemv, int iAlg, boolean bDebug, gamehistory ghist, mval_vector mvv, boolean bDeeper, dt_control dtc, movevalue l3best, movevalue l2best, String sMoveOrder, int iTimeLimit, boolean bStrategyImpact, regbest regb, mostore mos) throws Exception
	{
		int bestBalDif = -10000;
		int bestCovDif = -10000;
		
		int balDif = 0;
		int covDif = 0;
		
		int iPieceCtr = 0;
		int iMoveTotal = 0;
		
		move mBestCand;
		chessboard bestBoard = null;
		chessboard nxtBoard = null;
		movevalue bestMval = basemv.copy();
		movevalue currMval = basemv.copy();
		boolean bBestInstWin = true;
		boolean bCurrInstWin = true;
		boolean bCurrRCWin = true;
		
		int iChecked = 0;
		int iBestChecked = 0;
		int iMoveless = 0;
		boolean bCheckMate = false;
		boolean bDrawChosen = false;
		boolean bOrderByMostore = false;
		boolean bRBExists = false;
		boolean bReSortDone = false;
		boolean bMoveAfterResortDone = false;
		
		dt_control ndtc = null;
		
		
	
		//bDebug = true;
		if (bDebug && (iRounds >= 3)) ChessUI.setMonitorMode(true);
		
		double dMidPOpenLimit = 0.125;
		//double dMidPOpenLimit = 1.0;
		
		/*
		System.out.println("DBG170803: Enter findanddo, iAlg = " + iAlg);
		System.out.println("bMidPawnOpenings(iColor):"+bMidPawnOpenings(iColor));
		System.out.println("bPawnPressureOpenings(iColor):"+bPawnPressureOpenings(iColor));
		System.out.println("bFianchettoPrepOpenings(iColor):"+bFianchettoPrepOpenings(iColor));
		System.out.println("bWeirdOpeningsExist(iColor):"+bWeirdOpeningsExist(iColor));
		*/
		//System.exit(0);
		
		//if ((iAlg == movevalue.ALG_ASK_FROM_ENGINE_RND) && (Math.random() < dMidPOpenLimit) && bMidPawnOpenings(iColor))
		if ((iAlg == movevalue.ALG_ASK_FROM_ENGINE_RND) && (Math.random() < dMidPOpenLimit) && bWeirdOpeningsExist(iColor))
		{
			System.out.println("DBG170803: WEIRD OPENINGS OPENINGS DETECTED, iAlg = " + iAlg);
			System.out.println("bMidPawnOpenings(iColor):"+bMidPawnOpenings(iColor));
			System.out.println("bPawnPressureOpenings(iColor):"+bPawnPressureOpenings(iColor));
			System.out.println("bFianchettoPrepOpenings(iColor):"+bFianchettoPrepOpenings(iColor));
			System.out.println("bBishopE3Openers(iColor):"+bBishopE3Openers(iColor));
			System.out.println("bF2StepOpeners(iColor):"+bF2StepOpeners(iColor));
			System.out.println("bPawnFrontOpeners(iColor):"+bPawnFrontOpeners(iColor));
			System.out.println("bBackRowRookOpeners(iColor):"+bBackRowRookOpeners(iColor));
			System.out.println("bKnightToMiddleMoves(iColor):"+bKnightToMiddleMoves(iColor));
			System.out.println("bQueenFirstMoves(iColor):"+bQueenFirstMoves(iColor));
			System.out.println("bC2StepOpeners(iColor):"+bC2StepOpeners(iColor));
			System.out.println("bBishopF4Openers(iColor):"+bBishopF4Openers(iColor));
			System.out.println("bWeirdOpeningsExist(iColor):"+bWeirdOpeningsExist(iColor));
			
			
			int iWeirdoAlg = 0;
			
			int iWeirdoRangeSize = movevalue.ALG_LAST_WEIRD_OPENING - movevalue.ALG_FIRST_WEIRD_OPENING + 1;
			int iWeirdoStart = (int)(Math.random()*iWeirdoRangeSize);
			
			for (int i=0;i<iWeirdoRangeSize;i++)
			{
				int iWeirdoCand = movevalue.ALG_FIRST_WEIRD_OPENING+ ((i+iWeirdoStart) % iWeirdoRangeSize);
				
				if ((iWeirdoCand == movevalue.ALG_GET_MIDPAWN_OPENING) && (bMidPawnOpenings(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_PAWNPRESS_OPENING) && (bPawnPressureOpenings(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_FIANCHETTOPREP_OPENING) && (bFianchettoPrepOpenings(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_BISHOPE3_OPENING) && (bBishopE3Openers(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_F2STEP_OPENING) && (bF2StepOpeners(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_PAWNFRONT_OPENING) && (bPawnFrontOpeners(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_BACKROWROOK_OPENING) && (bBackRowRookOpeners(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_KNIGHTTOMIDDLE_OPENING) && (bKnightToMiddleMoves(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_QUEENFIRSTMOVE_OPENING) && (bQueenFirstMoves(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_C2STEP_OPENING) && (bC2StepOpeners(iColor))) iWeirdoAlg = iWeirdoCand;
				if ((iWeirdoCand == movevalue.ALG_GET_BISHOPF4_OPENING) && (bBishopF4Openers(iColor))) iWeirdoAlg = iWeirdoCand;
				
				if ((iWeirdoAlg != 0) && (Math.random() < 0.5)) break;
			}
			
			System.out.println("iWeirdoAlg:" + iWeirdoAlg);
			//System.exit(0);
			
			chessboard cbdb = anyokmove.db_anymoveget(this.FEN(),iWeirdoAlg+"", false);
			if (cbdb != null) return cbdb;
			
			//System.exit(0);
			
			chessboard cbr = findAndDoBestMove(iColor,0,basemv,iWeirdoAlg,bDebug,ghist,mvv,bDeeper,dtc,l3best,l2best,sMoveOrder,iTimeLimit,bStrategyImpact,regb,mos);
			System.out.println("Received weirdo sugg("+iWeirdoAlg+"):" + cbr.sMoveOrder);
			String sMpSugg = cbr.sMoveOrder.trim();
			String[] sMPC = sMpSugg.split(" ");
			int iMPC = sMPC.length;
			System.out.println("iMPC:" + iMPC + " sMpSugg:<"+sMpSugg+">");
			if ((iMPC > 0) && (sMpSugg.length()>0))
			{
				anyokmove.db_anymovesave(this.FEN(),iWeirdoAlg+"",sMpSugg);
				
				int iChosen = (int)(Math.random()*iMPC);
				String sChosen = sMPC[iChosen];
				System.out.println("Chose:" + sChosen);
				
				chessboard cbn = this.copy();
				cbn.domove(sChosen,iColor);
				cbn.dump();
				//System.exit(0);
				return cbn;
			}
		}
		//System.out.println("DINGDONG!");
		
		double dAMLimit = 0.3;
		// if (play.ANYMOVE_MODE) dAMLimit = 1.0;
		
		if ((iAlg == movevalue.ALG_ASK_FROM_ENGINE_RND) && (Math.random() < dAMLimit))
		{
			iAlg = movevalue.ALG_ANY_OKMOVE;
			chessboard cbr = findAndDoBestMove(iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv,bDeeper,dtc,l3best,l2best,sMoveOrder,iTimeLimit,bStrategyImpact,regb,mos);
			return cbr;
			
		}
		
		if ((iAlg == movevalue.ALG_ASK_FROM_ENGINE_RND) && (Math.random() < 0.25))
		{
			iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
			iRounds = (int)(Math.random() * 4);
			bDeeper = false;
			iTimeLimit = 8;	
			
			chessboard cbr = anyokmove.db_anymoveget(this.FEN(),iRounds+":"+iAlg, false);
			
			if (cbr == null)
			{
				cbr = findAndDoBestMove(iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv,bDeeper,dtc,l3best,l2best,sMoveOrder,iTimeLimit,bStrategyImpact,regb,mos);
				
				anyokmove.db_anymovesave(this.FEN(),iRounds+":"+iAlg,cbr.lastmoveString());
			}
			
			return cbr;
		}
		
		if (iAlg == movevalue.ALG_ANY_OKMOVE) 
		{
			int NewMoveNr;
			if (iColor == piece.WHITE) NewMoveNr = this.iMoveCounter;
			else NewMoveNr = this.iMoveCounter+1;
			
			chessboard cbr = anyokmove.db_anymoveget(this.FEN(),movevalue.ALG_ANY_OKMOVE+"", false);
			
			if (cbr == null)
			{
				cbr = anyokmove.fad_anymove(this,iColor,NewMoveNr);
			}
			
			return cbr;
		}
		//fad_anymove(chessboard cb, int iColor, int NewMoveNr)
		
		if ((iAlg >= movevalue.ALG_ASK_FROM_ENGINE1) && (iAlg <= movevalue.ALG_ASK_FROM_ENGINE_LAST))
		{
			
			chessboard cbr = engine.findAndDoMove(this,iAlg, false, true);
			
			return cbr;
		}
		//System.out.println("DBG150924: FINDANDDO ENTER!");
		
		long lFindAndDoStart = System.currentTimeMillis();
		long lFindAndDoStartCBInit = CMonitor.iChessboardInit;

		dbgPrintln("findAndDoBestMove " + iColor + "," + iRounds + " basemv: " + basemv.dumpstr(iAlg));

		if ((iRounds == -1) && (dtc == null))
		{
			System.out.println("DBG150222: FINDANDDO -1! -> ZERO MOVE");
			return doBestZeroMove(iColor,iAlg, true);
		}

		if ((iRounds==4) || (iRounds ==3)) System.out.println("DBG151224: Before iTimeLim:" + iTimeLimit);
		iTimeLimit = CMonitor.getTimeLim(iRounds, iTimeLimit);
		if ((iRounds==4) || (iRounds ==3)) System.out.println("DBG151224: After iTimeLim:" + iTimeLimit);
		
		
		this.bWhiteRiskOn = false;
		this.bBlackRiskOn = false;
		
		if (iRounds < 0-2*MAX_CHESS_RECURSION_DPTH) 
		{	
			System.out.println("Fatal Error, recursion too wild!");
			System.out.println("iRounds = " + iRounds);
			System.exit(0);
		}

		boolean bMidPawnMode = false;
		boolean bPawnPressureMode = false;
		boolean bFianchettoPrevMode = false;
		boolean bBishopE3Mode = false;
		boolean bF2StepMode = false;
		boolean bPawnFrontMode = false;
		boolean bBackRowRookMode = false;
		boolean bKnightToMiddleMode = false;
		boolean bQueenFirstMoveMode = false;
		boolean bC2StepMode = false;
		boolean bBishopF4Mode = false;
		
		if (iAlg == movevalue.ALG_GET_MIDPAWN_OPENING) bMidPawnMode = true;
		if (iAlg == movevalue.ALG_GET_PAWNPRESS_OPENING) bPawnPressureMode = true;
		if (iAlg == movevalue.ALG_GET_FIANCHETTOPREP_OPENING) bFianchettoPrevMode = true;
		if (iAlg == movevalue.ALG_GET_BISHOPE3_OPENING) bBishopE3Mode = true;
		if (iAlg == movevalue.ALG_GET_F2STEP_OPENING) bF2StepMode = true;
		if (iAlg == movevalue.ALG_GET_PAWNFRONT_OPENING) bPawnFrontMode = true;
		if (iAlg == movevalue.ALG_GET_BACKROWROOK_OPENING) bBackRowRookMode = true;
		if (iAlg == movevalue.ALG_GET_KNIGHTTOMIDDLE_OPENING) bKnightToMiddleMode = true;
		if (iAlg == movevalue.ALG_GET_QUEENFIRSTMOVE_OPENING) bQueenFirstMoveMode = true;
		if (iAlg == movevalue.ALG_GET_C2STEP_OPENING) bC2StepMode = true;
		if (iAlg == movevalue.ALG_GET_BISHOPF4_OPENING) bBishopF4Mode = true;
		
		
		iAlg = movevalue.ALG_SUPER_PRUNING_KINGCFIX;
		
		
		CMonitor.incFindMoveCnt();
		
		if (bDeeper)
		{
			if (!bCanDoLevel(iRounds+1,iColor)) 
			{
				return findAndDoBestMove(iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv, false, dtc, null, null,null,iTimeLimit, bStrategyImpact, null , mos);
			}
			else
			{
				CMonitor.incLevel4Moves();
				iRounds++;
				if (iRounds == 3) iTimeLimit = 3;
			}
		}
		
		//if (iRounds == 4) System.out.println("DBG150602 SPOT 1"); 
		
		String sMovOrd = "";
		//String sMovOrd = null;   // 150727 -> try to get iRounds-1 to do moveorder... -> does not sort!!!
		
		//if (iRounds >= 2) System.out.println("DBG151202: spot 1 iR" + iRounds);
		
		String sMovOrdMos = mos.getMoveOrder(FEN(), iRounds-1);
		String sMovOrdMos2 = mos.getMoveOrder(FEN(), iRounds-2);
		//String sMovOrdMos = null;
		
		
		if ((iRounds >= 3) &&(regb != null))
		{
			bRBExists = regb.rb_exists(iRounds);
			//System.out.println("DBG160309: FAD REGB CHECK iR:" + iRounds + " bRBExists:"+bRBExists);
		}
		
		if (sMovOrdMos != null)
		{
			//Thread.sleep(500);
			//System.out.println("DBG151202: YAY! " + sMovOrdMos);
			//System.out.println("DBG151202: FEN: " + FEN() + " + iR:" + (iRounds-1) + " iC:" + iColor);
			bOrderByMostore = true;
			//System.exit(0);
		}
		
		
		//if (((iRounds >= 3) && (iRounds <= 8)) && (sMovOrdMos == null))
		//if ((iRounds == 1) || (((iRounds >= 3) && (iRounds <= 8)) && (sMovOrdMos == null)))
		if ((iRounds >= PARALLEL_LEVEL) && (sMovOrdMos == null))
		{
			//System.out.println("DBG150312: findanddo, to create a STRATEGY, LEVEL = " + iRounds);
			//System.out.println("DBG151205: INFAD smovordcreate.");
			int iSortHelperRounds = iRounds -1;
			//if (iRounds == PARALLEL_LEVEL) iSortHelperRounds = PARALLEL_LEVEL -2;
			
			movevalue mmval2 = new movevalue("");
			chessboard cb_cpy = this.copy();
			mval_vector mvx = null;
			if (iRounds != 1) mvx = mvv;
			chessboard cb = cb_cpy.findAndDoBestMove(iColor,iSortHelperRounds,mmval2,iAlg,bDebug,ghist,mvx,false,dtc,l3best,l2best,sMovOrd, 3*CB_MAXTIME, true, null, mos);
			// 150727 3nd last param (bstratimpact to true) didn't help!
			// toggle bstratimpact to true 151130 $$$$$
			if (cb != null)
			{
				sMovOrd = cb.sMoveOrder;
				//System.out.println("DBG150312: findanddo " +iRounds+ ", going with sMoveOrder:" + sMovOrd);
			}
			else
			{
				System.out.println("DBG150312: findanddo, strategy creation returned null bestboard");
			}
			//if (iRounds >= 2) System.out.println("DBG151202 INFAD: iR:" + iRounds + " SMOVORD : >" + sMovOrd + "<");
			/*
			if (iRounds ==3)
			{
				Thread.sleep(500);
				System.out.println("DBG151130: mostore dump at iRounds:" + iRounds + " iColor:" + iColor);
				mos.dump();
				System.exit(0);
			}
			*/
		}
		if (sMovOrdMos != null) sMovOrd = sMovOrdMos;
		
		if (iRounds >= 4) System.out.println("DBG151202: iR:" + iRounds + " SMOVORD : >" + sMovOrd + "<");
		if (iRounds >= 4) System.out.println("DBG151202: iR:" + iRounds + " SMOVORD2 : >" + sMovOrdMos2 + "<");
		sMovOrd = chessboard.mergeMovOrder(sMovOrd,sMovOrdMos2);
		if (iRounds >= 4) System.out.println("DBG151202: iR:" + iRounds + " SMOVORD (AM) : >" + sMovOrd + "<");
		
		//if (iRounds == 4) System.out.println("DBG150602 SPOT 2");
		
		king pKing = locateKing(iColor);
		if (pKing == null) 
		{
			return null;
		}
		
		//regb = null;
		
		if ((iAlg >= movevalue.ALG_SUPER_PRUNING) && (iAlg <= movevalue.ALG_SUPER_PRUNING_LAST))
		{
			if ((iRounds >= 1) && (regb == null))
			{
				regb = new regbest();
			}
		}
		
		
		if ((mvv != null) && (!bNoThreadlaunch))
		{
			//System.out.println("DBG150430:inside findanddo, creating mtf rounds:" + iRounds);
			mvv.addWait();
			
			//System.out.println("DBG CREATE MT_FINDER ENTER");
			
			synchronized (chessboard.this)
			{
				//chessboard c1 = this.copy();
				mt_finder mtf = new mt_finder (this,iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv,l3best, regb, mos);
				mtf.start();
			}
			//System.out.println("DBG CREATE MT_FINDER LEAVE");
			//try { Thread.sleep(200); } catch (Exception e) {};   // DBGDBG!!!!!
			return null;
			
			// multithr fadbm call here!!!
			//return null;
		}
		
		// Multithread control point is here!
		mval_vector mvec = null;
		if (iRounds == PARALLEL_LEVEL) mvec = new mval_vector(this,sMoveOrder, regb, mos);

		redoVectorsAndCoverages(1-iColor, iAlg);
		
		
		
		if (iRounds == 0)
		{
			//$$$$ 160402
			
			if (hcwinner.hcwinnable(this,iColor) != hcwinner.HCW_NONE)
			{
				//System.out.println("DBG160402: Going to HCWINNER BRANCH iAlg:" + iAlg);
				chessboard cbc = this.copy();
				cbc.redoVectorsAndCoverages(iColor, iAlg);
				
				chessboard cbret = hcwinner.hcwinnerFAD(cbc,iColor,basemv, iAlg);
				if (cbret != null)
				{
					basemv.setBalancesFromBoard (cbret, iColor, iAlg);
					//basemv.setInstWinnable(1-iColor);
					basemv.pushmove(cbret.lastmoveString());
					basemv.iPieceBalCorrWhite = basemv.iPieceBalance + cbret.iHCBonus;
					basemv.iPieceBalCorrBlack = basemv.iPieceBalance + cbret.iHCBonus;
					if (bDebug) System.out.println(iRounds+":"+iColor+":HCW:"+basemv.dumpstr(movevalue.DUMPMODE_LONG,iAlg));
					//System.exit(0);
					return cbret;
				}
			}
		}
		
		
		
		//System.out.println("DBG150116:findanddo:after redo. rounds:" + iRounds);
		
		// need to find out if king is under threat! If yes, most moves won't do

		
		int checkcount = iCountCheckers(pKing);
		//System.out.println("DBG161014: checkcount:" + checkcount);
		//piece pChecker = locateFirstChecker(pKing);
		Vector vCheck = locateCheckersVector(pKing);
		
		if (checkcount > 0) 
		{
			//System.out.println("I am being checked. " + iRounds + " Whoo.. king at " + pKing.xk + "," + pKing.yk + "  checkcount = " + checkcount);
			bWasChecked = true;
			if (checkcount > 1) 
			{
				//System.out.println("Two checkers, difficult case here! I'm trying to sort it out.");
				//System.out.println("Checkcount = " + checkcount);
				//dump();
				//System.exit(0);
			}
		}
		
		int iRiskFree;
		int iMaxMoveVal;
		int iRiskFreeCOS;
		boolean bDoTraverseFilter;
		int iMaxPressure;
		int iMaxEnemyCapt;
		boolean bPressure0 = false;
								
		if (iColor == piece.WHITE)
		{
			//iMaxMoveVal = miWhiteMoveindex.iMaxMoveVal();
			//miWhiteMoveindex.setRiskBits(this);
			
			miWhiteMoveindex.setRiskBits(this);
			iMaxMoveVal = miWhiteMoveindex.iMaxMoveVal(false);
			
			iRiskFree = miWhiteMoveindex.iNonRiskyMoves();
			iRiskFreeCOS = miWhiteMoveindex.iNonRiskyCapturesOrSpecials();
			iMaxPressure = miWhiteMoveindex.iGetMaxPressure();
			iMaxEnemyCapt = miBlackMoveindex.getBestNetCapture();
		}
		else
		{
			//iMaxMoveVal = miBlackMoveindex.iMaxMoveVal();
			//miBlackMoveindex.setRiskBits(this);
			
			miBlackMoveindex.setRiskBits(this);
			iMaxMoveVal = miBlackMoveindex.iMaxMoveVal(false);
			
			iRiskFree = miBlackMoveindex.iNonRiskyMoves();
			iRiskFreeCOS = miBlackMoveindex.iNonRiskyCapturesOrSpecials();
			iMaxPressure = miBlackMoveindex.iGetMaxPressure();
			iMaxEnemyCapt = miWhiteMoveindex.getBestNetCapture();
			
		}
		bDoTraverseFilter = iRiskFreeCOS > 0;
		//System.out.println("iRiskFree= " + iRiskFree + " iRiskFreeCOS=" + iRiskFreeCOS);
		//System.out.println("DBG150127: iMaxMoveVal:" + iMaxMoveVal);
		
		if ((dtc != null) && (dtc.getCheckColor() == iColor))
		{
			//System.out.println("DBG 141210: deepcheck,clr:" + iColor + " setting bestboard to this."); 
			bestBoard = this.copy();
			bestBoard.redoVectorsAndCoverages(iColor, iAlg);
			bestMval.setBalancesFromBoard(this,iColor,iAlg);
			//System.out.println("bestMval now " + bestMval.dumpstr(iAlg));
		}
		
		
		//System.out.print("DBG 141210: findAndDoBestMove: (inside before loop) iRounds " + iRounds);
		//if (dtc == null) System.out.println(" with null dtc.");
		//else System.out.println(" with NOT NULL dtc.");
		
		if (iRounds == 0)
		{
			//System.out.println("DBG 141221: Max Pressure: " + iMaxPressure + " MaxMoveVal " + iMaxMoveVal);
			if (iMaxPressure > iMaxMoveVal) 
			{
				//System.out.println("DBG 141221: Max Pressure > iMaxMoveVal");
				bPressure0 = true;
			}
		}
		// 150106 cut from here...
		/*
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				if (p != null)
				{
					 if (p.iColor == iColor)
					 {
						iPieceCtr++;
						
						Vector mv = (Vector)p.moveVector(this);
						
						for (int k=0; k < mv.size(); k++)
						{
							move m = (move)mv.elementAt(k);
						
		*/
		moveindex mAct;
		if (iColor == piece.WHITE) mAct = this.miWhiteMoveindex.goodMoveIndex(this,pKing).sortedcopy();
		else mAct = this.miBlackMoveindex.goodMoveIndex(this,pKing).sortedcopy();
		
		//System.out.println("MoveIndex dump");
		//mAct.dump();
		
		movevalue thisMVal = new movevalue("");
		thisMVal.setBalancesFromBoard (this, 1-iColor, iAlg);
		
		int iTrn = iColor;
		if ((iRounds % 2) == 0) iTrn = 1 - iColor;
		
		strategy strat = null;
		if ((iRounds >= 3) && (iRounds <= 8))
		//if ((iRounds >= 2) && (iRounds <= 8))   // 160513 $$$$$
		//if (((iRounds >= 3) && (iRounds <= 8)) || (iRounds < 2))    // 160429
		{
			System.out.println("DBG150322: ROUNDS " + iRounds+ ": CREATE STRATEGY? : sMoveOrder=" + sMoveOrder);
			//if (sMoveOrder != null)   $$$$ 150424 create strategy object anyway 
			strat = new strategy(this,iColor,iTrn,iAlg,iRounds,ghist);
		}
		
		int l1goal = 0;
		int l0goal = 0;
		
		int iMvecTotal = mAct.getSize();
		if (iRounds == ChessUI.getiAnalStartLevel()) ChessUI.setiAnalRoundsTotal(iMvecTotal);
		if ((iRounds == ChessUI.getiAnalStartLevel()-1) && (iColor == ChessUI.getiAnalStartColor())) ChessUI.setiPrelRoundsTotal(iMvecTotal);
		
		int iRepCycles = 0;
		boolean bCalcAgain = false;
		boolean bBranchDone = false;
		
		if (regb != null) regb.resetBest(iRounds);
		
		//if (iRounds == 4) System.out.println("DBG150602 SPOT 3");
		
		if ((sMovOrd != null) && (sMovOrd.length() > 0)) mAct.sortByMoveOrder(sMovOrd);
		
		//if (iRounds == 4) System.out.println("DBG150602 SPOT 4");
		
		while (((iRounds >0) && ( iRepCycles == 0)) || ((iRounds <= 0) && ((iRepCycles==0) || ((iRepCycles < 2) && bCalcAgain))))
		{
			iRepCycles++;
			//System.out.println("DBG151013 (S3):" + basemv.sRoute+ " mAct.size=" + mAct.getSize());
			
			/*if (iRounds == 1) mAct.dump(null);
			if (iRounds >= 3) 
			{
				System.out.println("DBG160308: Nr of interesting m: " + mAct.iInterestingCount());
				System.out.println("DBG160308: Last Interesting at: " + mAct.iLastInteresting());
			}
			*/
			//mAct.dump(true);
			//System.out.println("======");
			int iGFGResort = -1;
			boolean bGFGTimeOut = false;
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				//if (iRounds == 3) System.out.println("DBG160413 TIMING (IR3) CHECKER::" + this.lastmoveString() + ",moving1:" + m.moveStr());
							
				//System.out.println("DBG160326: mAct.get:" + m.moveStrLong());
				if (iRounds >= 3) 
				{	
					long lN2 = System.currentTimeMillis();
					if ((i > mAct.iSortedTo) || (((lN2-lFindAndDoStart) >= (MOSTORE_SORT_TIMEOUT * iTimeLimit)) && (bRBExists) && (!bReSortDone) ))
					
					{
						//Thread.sleep(300);
						System.out.println("DBG151205 GFG: FAILURE: unsorted move or timeout at FAD. iRounds:" + iRounds);
						//System.out.println("DBG151205 GFG: sMoveOrder:"+sMovOrd);
						System.out.println("DBG151205 GFG: move: " + m.moveStr() +  " i= " + i + " ind size:" + mAct.getSize());
						//System.out.print("DBG151205 GFG: inddump:");
						//mAct.dump();
						
						
						//////////////////////
											
						//System.out.println("DBG151205: INFAD smovordcreate.");
						movevalue mmval2 = new movevalue("");
						chessboard cb_cpy = this.copy();
						String sMovOrdx = "";
						chessboard cb = cb_cpy.findAndDoBestMove(iColor,2,mmval2,iAlg,bDebug,ghist,mvv,false,dtc,l3best,l2best,sMovOrdx, 3*CB_MAXTIME, true, null, mos);
						// 150727 3nd last param (bstratimpact to true) didn't help!
						// toggle bstratimpact to true 151130 $$$$$
						iGFGResort = i;
						if (cb != null)
						{
							sMovOrdx = cb.sMoveOrder;
							System.out.println("DBG150312: findanddo " +iRounds+ ", going with sMoveOrder:" + sMovOrd + " sMovOrdx: " + sMovOrdx);
						}
						else
						{
							System.out.println("DBG150312: findanddo, strategy creation returned null bestboard");
						}
						
						if (i<=mAct.iSortedTo)
						{
							System.out.print("DBG160309. It's a timeout. cutting sMovOrd for merge.");
							System.out.println("DBG160309 OLDORD: iR:" + iRounds + " SMOVORD : >" + sMovOrd + "<");
							//sMovOrd = sMovOrd.substring(0,5*i).trim();
							
							String mOrdComp[] = sMovOrd.trim().split("\\s+");
							sMovOrd = "";
							for (int ii=0;ii<i;ii++)
							{
								sMovOrd=sMovOrd+mOrdComp[ii]+" ";
							}
							sMovOrd = sMovOrd.trim();
							
							bGFGTimeOut = true;
							
						}
						
						//System.out.println("DBG151209 OLDORD: iR:" + iRounds + " SMOVORD : >" + sMovOrd + "<");
						//System.out.println("DBG151209 FIXORD: iR:" + iRounds + " SMOVORD : >" + sMovOrdx + "<");
						
						//////////////////////
						
						String sNewMovOrd = chessboard.mergeMovOrder(sMovOrd,sMovOrdx);
						System.out.println("DBG151209 FIXORD NEW: iR:" + iRounds + " SMOVORD : >" + sNewMovOrd + "<");
						
						mAct.sortByMoveOrder(sNewMovOrd);
						//mAct.dump();
						m = mAct.getMoveAt(i);
						//System.out.println("DBG151209 after resort. Continue from: " + m.moveStr());
						bReSortDone = true;
						//System.exit(0);
					}
				}
				/*
				long lN2 = System.currentTimeMillis();
				if ((iRounds >= 3) && ((lN2-lFindAndDoStart) >= (1000 * iTimeLimit)) && ((mAct.iUnProcessedInterestingCount()>0) && (bOrderByMostore)))
				{
					System.out.println("DBG160308 NEED TO SORT mAct to process interesting moves asap! iR:" + iRounds + " Time Elapsed = " + (lN2-lFindAndDoStart));
					System.out.println("DBG160308: unproc int count:" + mAct.iUnProcessedInterestingCount());
					System.out.println("DBG160308: loop counter:" + i);
					//moveindex misorted = mAct.getSortedByInterestCopy();
					//System.out.println("DBG160308: sort by interest:" + misorted.orderString());
					System.out.println("DBG160308: original " + mAct.orderString());
					//mAct.sortByMoveOrder(misorted.orderString());
					mAct.raiseInterestingUnprocs();
					mAct.dump();
					m = mAct.getMoveAt(i);
					System.out.println("DBG160308 after raising unprocs. Continue from: " + m.moveStr());
					//System.exit(0);
				}
				*/
				piece p = m.p;
				
				if ((bDebug) && (iRounds > 2)) ChessUI.monRefresh(false);
				
				if (iRounds == ChessUI.getiAnalStartLevel())
				{
					ChessUI.setBestMval(bestMval);
					ChessUI.setiAnalRoundsDone(i);
					ChessUI.setAnalCurrent(m.moveStr());
				}
				if ((iRounds == ChessUI.getiAnalStartLevel()-1) && (iColor == ChessUI.getiAnalStartColor()))
				{
					ChessUI.setBestPreMval(bestMval);
					ChessUI.setiPrelRoundsDone(i);
					ChessUI.setAnalPreCurrent(m.moveStr());
				}
				
				//System.out.println("DBG151013 (S2):" + basemv.sRoute+ " " + m.moveStr() + " " + i);
				
				//if ((iRounds==2) && (bestMval != null)) System.out.println(bestMval.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
				
				if ((regb != null) && (bestMval != null) && (iMoveTotal != 0) && (iRounds != PARALLEL_LEVEL ))
				{
					int iTurn = iColor;
					if ((iRounds % 2) == 0) iTurn = 1 - iColor; 
					
					//System.out.println("REGB SET BEST!: iRounds=" + iRounds + " iMoveTotal:"+ iMoveTotal);
					regb.setBest(bestMval,iRounds,iTurn,iAlg,iColor);
					
					if (regb.bBranchIsDone(iRounds,iTurn,iAlg,iColor))
					{
						bestMval.setCutBranchLevel(iRounds);
						
						//if (iRounds == 3) System.out.println("DBG151013 (TIMING) Finishing branch:" + basemv.sRoute+ " " + m.moveStr() + " " + i + " Best:" + bestMval.dumpstr(iAlg));
						
						bBranchDone = true;
						i = mAct.getSize();
					}

				}

				
				if ((bestMval != null) && (iRounds == 1) && (i!=0))
				{
					//System.out.println(i+ " Move " + m.moveStr() + ": diff : " + bestMval.movevaluediff(thisMVal,iColor));
					int l3g = 0;
					
					if (l3best == null) l1goal = bestMval.movevaluediff(thisMVal,iColor);
					else
					{
						l3g = l3best.movevaluediff(thisMVal,iColor);
						if (iColor == piece.BLACK) l3g = -l3g;
					}
					if (iColor == piece.BLACK) l1goal = -l1goal; 
					l1goal = Math.max(l1goal,l3g);
				}
				
				if ((l2best != null) && (iRounds == 0) && (i!=0))
				{
					l0goal = l2best.movevaluediff(thisMVal,iColor);
					if (iColor == piece.BLACK) l0goal = -l0goal;
					/*System.out.println("DBG150116: l0goal:" + l0goal);
					System.out.println("DBG150116:l2best:" + l2best.dumpstr(iAlg));
					System.out.println("DBG150116:thismval:" + thisMVal.dumpstr(iAlg));
					*/
				}
				
				if ((iRounds ==3) && (bestMval != null))
				{
					if (bestMval.bDiffMoveByRoute(l3best))
						System.out.println(i+"/"+mAct.getSize() + " : " + bestMval.dumpstr(iAlg, movevalue.DUMPMODE_SHORT) + "  L3B:");
					
	/*				if (l3best != null) System.out.println( l3best.dumpstr(iAlg, movevalue.DUMPMODE_SHORT)); 
						else System.out.println();
						System.out.println("DIFF: " + bestMval.bDiffMoveByRoute(l3best)); */
				}
				
				if ((iRounds == 3) && (bestMval != null) && (i!=0)) l3best = bestMval.copy();
				
				
				// 150106 cut to here..
				
					//boolean bOk = true;
					//bCanDoMove  experiment 150106
					
				//if (iRounds == 1) System.out.println("DBG160413 TIMING CHECKER::" + this.lastmoveString() + ",moving2:" + m.moveStr());	
					
				boolean bOk = bCanDoMove(m,iRounds,iColor,iAlg,iRiskFree,checkcount,dtc,bDoTraverseFilter,bPressure0,iMaxMoveVal,iMaxPressure,0,l1goal,l0goal);
				
				//System.out.println("DBG151013 (S1):" + basemv.sRoute+ " " + m.moveStr() + " bOK:" + bOk + " i=" + i);
				
				//if ((iRounds == 1) && (p.iType == piece.BISHOP) && (m.xtar == 2)) System.out.println("DBG150207 FUNNY BISHOP MOVE STARTS HERE!!!");
				//if ((iRounds == 0) && (p.iType == piece.QUEEN)) System.out.println("DBG150130: OKSTR:" + m.moveStrLong() + ", ok:" + bOk);
			
				// $$$ 141021 bOk added to condition
				
				/*if (!bMoveIsGood(m,p,pKing,vCheck))
				{
					System.out.println("DBG150116:bMoveIsGood=false even though not expected!");
					System.out.println(m.moveStrLong());
					System.exit(0);
				}*/
				// call to bMoveisGood above not a requirement! noticed on 150116 
				if ((iMoveTotal == 0) && (i == mAct.getSize()-1) && !bOk) 
				{
					//System.out.println("DBG150119: WARNING PHAPS NO MOVES!");
					bOk = true;
				}
				
				//System.out.println("DBG150126:R0:" + m.moveStr() + ", ok:" + bOk + " repcycles:" + iRepCycles+ " bCalcAgain:" + bCalcAgain);
				if ((iRounds == 0) && (bCalcAgain)) bOk = true;
				
				if ((CMonitor.iMonLevel == iRounds) && (CMonitor.sDrawMove.indexOf(m.moveStrCaps()) != -1)) bOk = false;  // analfile draw condition 151119
				
				long lNow = System.currentTimeMillis();
				long lFADCBInitNow = CMonitor.iChessboardInit;
				long lFACCBInitTarget = lFindAndDoStartCBInit + iTimeLimit * CB_NPS_TARGETRATE;
				/*
				if (iRounds >= 3) 
				{ 
					System.out.println("DBG160308: iR:" + iRounds + " unproc interest:" + mAct.iUnProcessedInterestingCount());
					System.out.println("DBG160308: iR:" + iRounds + "  unproc interest:" + mAct. dumpUnProcessedInteresting());
					System.out.println("DBG160308: iR:" + iRounds + "  bOrderByMostore:" + bOrderByMostore);
				}*/
				
				// $$$$ iUnProcessedInterestingCount seems to help with 160308 defect
				// need to enhance to avoid going to the branch when on top level
				
				//if (iRounds == 3) System.out.println("DBG160413 TIMING (IR1) CHECKER::" + this.lastmoveString() + ",moving3:" + m.moveStr() + " bOK:" + bOk + " bBranchDone:" + bBranchDone + " m.is.proc:"+ m.isProcessed() + " time: " + (lNow-lFindAndDoStart)+ " timelim:" + (1000 * iTimeLimit));
				
				if ((bOk) && !m.isProcessed() && 
					(((lNow-lFindAndDoStart) < (1000 * iTimeLimit)) // || (lFADCBInitNow < lFACCBInitTarget) 
					|| (iMoveTotal == 0) // || ((mAct.iUnProcessedInterestingCount()>0) && (bOrderByMostore)) 
					|| (bReSortDone && !bMoveAfterResortDone) ) 
					&& (!bBranchDone))
				{
					
					if (bReSortDone) bMoveAfterResortDone = true;
					
					//if (iRounds >= 3) System.out.println("DBG150525, TIMING iR:"+ iRounds+" MOVING:" + m.moveStr());
					
					if (iRounds >= 3)
					{
						int iNPS = 0;
						if ((lNow-lFindAndDoStart) == 0) iNPS = 9999999;
						else iNPS = (int) (1000*(lFADCBInitNow - lFindAndDoStartCBInit) / (lNow-lFindAndDoStart));
						
						//System.out.println("[DBG150322: ROUND " +iRounds + " elapsed: "+ (lNow-lFindAndDoStart) + " limit=" + (1000 * iTimeLimit) + " " + m.moveStr() + " nodes:" + (lFADCBInitNow - lFindAndDoStartCBInit) +  "=" + iNPS +  " n/s]" );
						System.out.print("R:"+iRounds+":"+m.moveStr()+" ");
					}
					iMoveTotal++;
					m.setProcessed(true);
					dbgPrintln("##########################################################");
					dbgPrintln("Analysis (" + iRounds + ") for Move from:" +p.xk+","+p.yk+" to:" + m.xtar + "," + m.ytar + " best Mval so far:" + bestMval.dumpstr(iAlg));
					if ((p.xk == m.xtar) && (p.yk == m.ytar))
					{
						System.out.println("BAAD VALUES ABOVE!!! STOPPING RIGHT NOW.");
						System.exit(1);
					}	
					
					chessboard nb = new chessboard();
					
					/*System.out.println("DBG160126:Just prior starting to move. "+ m.moveStr()+ " Dumping original board");
					dump();
					dump_pin_info();*/
					
					nb = this.copy();
					if (iColor == piece.BLACK) nb.iMoveCounter++;
					
					piece np = nb.blocks[p.xk][p.yk];
					
					
					//System.out.println("DBG150610: analyzing move:" + m.moveStrLong()+ " *********************************");
					//System.out.println("DBG160126: piece.iPinningToDirection = " + 	np.iPinningToDirection);
					//if (iRounds == 1) System.out.println("DBG160413 TIMING CHECKER::" + this.lastmoveString() + ",movingF:" + m.moveStr());
					
					nb.domove(np,m,-1);
					
					//if (((m.iCaptValue + 1 < iMaxMoveVal) && (iRounds == 1) && !m.isSpecial() && bDoTraverseFilter) || ((iRounds == 1) && (iRiskFree > 0) && (m.isRisky()) && (!m.isCheck()) && (!m.isRevCheck()) && (!m.bPressure) ))
					
					
					boolean bOk1 = true;
					
					if (iRounds == 1)
					{
						if (((m.iCaptValue + 1 < iMaxMoveVal) && (iRounds == 1) && !m.isSpecial() && bDoTraverseFilter) || ((iRounds == 1) && (iRiskFree > 0) && (m.isRisky()) && (!m.isCheck()) && (!m.isRevCheck())  )) bOk1 = false;
						
						if (iAlg != movevalue.ALG_OPT_TRAV_LEV1) bOk1 = true;
					
						if (bWasChecked) bOk1= true;
					
						//System.out.println("DBG: ROUND 1., move " + m.moveStr() + ":" + bOk1);
					
					}
					
					
					
					if (bOk1)
					{
					
						if ((m.isRisky()) && (iColor == piece.WHITE)) nb.bWhiteRiskOn = true;
						if ((m.isRisky()) && (iColor == piece.BLACK)) nb.bBlackRiskOn = true;
						
						//System.out.println("DBG 141216(A):nb risks w:" + nb.bWhiteRiskOn + ", b:" + nb.bBlackRiskOn);
						
						// DEEPCONTROL EXPERIMENT 141203 $$$$
						// is check being done right here??
						//if (dtc != null) dtc.dump();
						if (iAlg == movevalue.ALG_DEEP_CHECK)
						{
							ndtc = null;
							
							if ((iRounds <= 0) && (m.isCheck() || m.isRevCheck()))
							{
								//String pStr = "DBG 141210: CheckMove taking place on round " + iRounds + ".";
								//pStr = pStr + m.p.dumpchr()+" " + (char)(m.p.xk+64) + m.p.yk + (char)(m.xtar+64) + m.ytar + " ";
								//System.out.println(pStr);
								if (dtc == null) ndtc = new dt_control(iColor,MAX_CHESS_RECURSION_DPTH,0, dt_control.CHECK_AT_WILL);
								else ndtc = dtc.one_deeper();
							}	
							else if ((iRounds <= 0) && (dtc != null) && (dtc.getCheckColor() != iColor)) ndtc = dtc.one_deeper();
							
							//if (ndtc != null) System.out.println("DBG 141210 ndtc != null");
							//else System.out.println("DBG 141210 ndtc is null!");
						}
						
						//System.out.println("DBG150610: BEFORE REDO1 for nb:" + m.moveStrLong()+ " *********************************");
						nb.redoVectorsAndCoverages(1-iColor, iAlg);
						//System.out.println("DBG150610: AFTER REDO1 for nb:" + m.moveStrLong()+ " *********************************");						
						
						if ((((iColor == piece.WHITE) && (nb.iBlackCheckMoves != 0)) ||
						((iColor == piece.BLACK) && (nb.iWhiteCheckMoves != 0))) && (iAlg == movevalue.ALG_DEEP_CHECK) && (iRounds <= 0) && (!m.isCheck()) && (!m.isRevCheck()))	
						{
							//System.out.println("DBG 141203. WHOA, I can be checked, color:" + iColor + " irounds:" + iRounds);
							//if (iColor == piece.WHITE)
							//{
							//	System.out.println("color 0: (blackcheckmoves) " + nb.iBlackCheckMoves);
							//	if (nb.iBlackCheckMoves == 2) nb.miBlackMoveindex.dump();
							///
							//if (dtc != null) System.out.println("dtc exists, iRounds : "+ dtc.iRounds);	
							//else System.out.println("No dtc, though");
							if (dtc == null) ndtc = new dt_control(1-iColor,MAX_CHESS_RECURSION_DPTH,0, dt_control.MUST_CHECK);
						}
						
						//System.out.println("DBG151013 (S6):" + basemv.sRoute+ " " + m.moveStr() + " i=" + i);
						
						if (iColor == piece.WHITE) 
						{
							if ((iRounds > 0) || (ndtc != null)) 
							{
								movevalue mmval = basemv.copy();
								mmval.pushNewSeq(p.xk,p.yk,m.xtar,m.ytar,m.iPromTo);
								mmval.setbase(piece.BLACK);
								nxtBoard = nb.findAndDoBestMove(piece.BLACK,iRounds-1,mmval,iAlg,bDebug, null,mvec,false,ndtc,l3best,l2best, regb, mos);
								dbgPrintln("Received mmval from below: col("+iColor+") rnd("+iRounds+") " + mmval.dumpstr(iAlg));
								if ((bDebug) && (mvec == null)) System.out.println(iRounds+":"+iColor+":"+mmval.dumpstr(iAlg));
								currMval.copyfrom(mmval);
								
								if (nxtBoard != null)
								{
									balDif =  nxtBoard.pvaluesum(piece.WHITE) - nxtBoard.pvaluesum(piece.BLACK)  ;
									covDif =  nxtBoard.iWhiteCovered - nxtBoard.iBlackCovered;
									if (nxtBoard.bWasChecked) iChecked = 1;
									else iChecked = 0;
									if (nxtBoard.bNoMoves) iMoveless = 1;
									else iMoveless = 0;
									if (mmval.bBlackCheckMate) balDif = 100000;
									if (nxtBoard.bIsCheckMate) bCheckMate = true;
									if (nxtBoard.bIsCheckMate) dbgPrintln("Checkmate from down");
								}
								else
								{
									//System.out.println(iRounds+":"+iColor+": MATEEEE! DBG140410");
								}
								
							}
							else
							{
								// do white move without recursion
								//System.out.println("DBG:DOWHITEMOVE");
								
								movevalue mval = basemv.copy();
								mval.pushNewSeq(p.xk,p.yk,m.xtar,m.ytar,m.iPromTo);
								//System.out.println("DBG:A:1");
								//System.out.println("DBG 141216(C):nb risks w:" + nb.bWhiteRiskOn + ", b:" + nb.bBlackRiskOn);
								
								//bCurrInstWin = nb.bWinnableByOne(piece.WHITE,iAlg).bIW;
								
								//mval.setBalancesFromBoard(nb, piece.BLACK,iAlg);
								
								instWinRec iWR = nb.bWinnableByOne(piece.WHITE,iAlg);
								nb.bWhiteInstWin = iWR.bIW;
								nb.iInstWinCorr = iWR.iIWCorr;
								mval.setBalancesFromBoard(nb, piece.BLACK,iAlg);   // SWAP $$$$
								
								if ((!iWR.bIW) && (iWR.iIWCorr == 0))
								{
									//bCurrRCWin = nb.bWinnableByReCheck(piece.WHITE, iAlg);
									//nb.bBlackInstWin = bCurrRCWin;
									iWR = nb.bWinnableByReCheck(piece.WHITE, iAlg);
									//nb.bBlackInstWin = iWR.bIW;
									//nb.iInstWinCorr = iWR.iIWCorr;
									mval.bBlackInstWin = iWR.bIW;
									mval.iInstWinCorr = iWR.iIWCorr;
								}
								else
								{
									mval.bWhiteInstWin = iWR.bIW;
									mval.iInstWinCorr = iWR.iIWCorr;
								}
								
								
								//mval.setBalancesFromBoard(nb, piece.BLACK,iAlg);
								//System.out.println("DBG:A:2");
								
								if ((bDebug) && (mvec == null)) System.out.println(iRounds+":"+iColor+":"+mval.dumpstr(iAlg));
								
								currMval.copyfrom(mval);
								currMval.bWhiteCheckMate = false;
								dbgPrintln("currMval: " + currMval.dumpstr(iAlg));
								//System.out.println("DBG:WHITEMOVEDONE");
								
							}
						}
						else   // if white -> black processing starts here 
						{
							if ((iRounds > 0) || (ndtc != null)) 
							{
								movevalue mmval = basemv.copy();
								mmval.pushNewSeq(p.xk,p.yk,m.xtar,m.ytar,m.iPromTo);
								mmval.setbase(piece.WHITE);
								nxtBoard = nb.findAndDoBestMove(piece.WHITE,iRounds-1,mmval,iAlg,bDebug, null, mvec,false, ndtc,l3best,l2best,regb, mos);
								dbgPrintln("Received mmval from below: col("+iColor+") rnd("+iRounds+") " + mmval.dumpstr(iAlg));
								if ((bDebug) && (mvec == null)) System.out.println(iRounds+":"+iColor+":"+mmval.dumpstr(iAlg));
								currMval.copyfrom(mmval);
								
								if (nxtBoard != null)
								{
									balDif =  nxtBoard.pvaluesum(piece.BLACK) - nxtBoard.pvaluesum(piece.WHITE);
									covDif =  nxtBoard.iBlackCovered - nxtBoard.iWhiteCovered ;
									if (nxtBoard.bWasChecked) iChecked = 1;
									else iChecked = 0;
									if (nxtBoard.bNoMoves) iMoveless = 1;
									else iMoveless = 0;
									if (mmval.bWhiteCheckMate) balDif = 100000; 
									if (nxtBoard.bIsCheckMate) bCheckMate = true;
									if (nxtBoard.bIsCheckMate) dbgPrintln("Checkmate from down");
								}
								
								else
								{
									balDif = 2000;
									//System.out.println(iRounds+":"+iColor+": MATEEEE! DBG140410");
								}
							}
							else
							{
								// do black move without recursion
								//System.out.println("DBG150610: DOBLACKMOVE (A) ******");
								movevalue mval = basemv.copy();
								
								mval.pushNewSeq(p.xk,p.yk,m.xtar,m.ytar,m.iPromTo);
								
								
								//mval.iPieceBalance = -balDif;
								//mval.iCoverageBalance = -covDif;
								
								//bCurrInstWin = nb.bWinnableByOne(piece.BLACK,iAlg).bIW;
								//mval.setBalancesFromBoard(nb, piece.WHITE, iAlg);
								
								//System.out.println("DBG150610: DOBLACKMOVE (A2) ******");
								
								instWinRec iWR = nb.bWinnableByOne(piece.BLACK,iAlg);
								
								//System.out.println("DBG150610: DOBLACKMOVE (A3) ******");
								
								nb.bBlackInstWin = iWR.bIW;
								nb.iInstWinCorr = iWR.iIWCorr;
								mval.setBalancesFromBoard(nb, piece.WHITE, iAlg);
								
								if ((!iWR.bIW) && (iWR.iIWCorr == 0))
								{
									//bCurrRCWin = nb.bWinnableByReCheck(piece.BLACK, iAlg);
									//nb.bWhiteInstWin = bCurrRCWin;
									iWR = nb.bWinnableByReCheck(piece.BLACK,iAlg);
									nb.bWhiteInstWin = iWR.bIW;
									nb.iInstWinCorr = iWR.iIWCorr;
									mval.bWhiteInstWin = iWR.bIW;
									mval.iInstWinCorr = iWR.iIWCorr;
								}
								else
								{
									mval.bBlackInstWin = iWR.bIW;
									mval.iInstWinCorr = iWR.iIWCorr;
								}
								
								//System.out.println("DBG150610: DOBLACKMOVE (C) ******");
								
								//mval.setBalancesFromBoard(nb, piece.WHITE, iAlg);
								//mval.dump();
								
								/*
								if ((m.xtar==2) && (m.ytar==2)) 
								{
									System.out.println("DBG160125:"+m.moveStr());
									nb.prefixdump("DBG:: ",DUMPMODE_FULL);
									System.out.println("White Moves:");
									nb.miWhiteMoveindex.sortedcopy().dump(nb.miBlackMoveindex.sortedcopy());
									System.out.println("Black Moves:");
									nb.miBlackMoveindex.sortedcopy().dump(nb.miWhiteMoveindex.sortedcopy());
									System.out.println("DBG160125:=================================");
									
								}
								*/	
								
								if ((bDebug) && (mvec == null)) System.out.println(iRounds+":"+iColor+":"+mval.dumpstr(iAlg));
								currMval.copyfrom(mval);
								currMval.bBlackCheckMate = false;
								dbgPrintln("currMval: " + currMval.dumpstr(iAlg));
								
								//System.out.println("DBG150610: DOBLACKMOVE (Z) ******");	
							}
						}
						if ((currMval.getCutBranchLevel() == iRounds ) && (bestMval != null)) bestMval.setCutBranchLevel(iRounds);
						
						//System.out.println("DBG150525:after moves:" + m.moveStr());
						
						if (nxtBoard != null)
						{
							dbgPrintln("#########################");
							dbgPrintln("Analyzing : " + iColor + ", " + iRounds  + " move:"+nb.lastmoveString());
							//nxtBoard.dump();   // DEBUG  !!!!!
							dbgPrintln("iChecked = " + iChecked);
							dbgPrintln("#########################");
						}
						
						if (bCheckMate)
						{
							mBestCand = m;
							bestBoard = nb;
							//bIsCheckMate = true;
							System.out.println("****************We have a checkmate here, hurra!");
						}
						else
						{
							
							int iTurn = iColor;
							
							if ((iRounds % 2) == 0) iTurn = 1 - iColor; 
							
							if (bestBoard == null)
							{
								mBestCand = m;
								bestBoard = nb;
								bestMval.copyfrom(currMval);
								if (iRounds>=3) bestMval.sMessage=" pick: 0";
							}
					
							if (strat != null) strat.addCand(currMval);
					
							if ((currMval.isBetterthan(bestMval,iColor, iAlg, iTurn, iRounds)) ||
								(bDrawChosen && (currMval.equalBalance(iAlg,iTurn,bestMval))))
							{
								if ((!currMval.mateCondition()) && (ghist != null) && (ghist.bIsTieCandidate(nb,iColor)))
								{
									System.out.println("*************************************************");
									System.out.println("DRAW WARNING WE MIGHT BE PICKING MOVE LEADING TO DRAW ");
									System.out.println("iRounds: " + iRounds);
									System.out.println("gamehist sizes:" + ghist.vMoves.size() + "," + ghist.vTieCands.size()); 
									System.out.println("*************************************************");
									//System.exit(0);
									
									if (!currMval.equalBalance(iAlg, iTurn, bestMval))
									{
										mBestCand = m;
										bestBoard = nb;
										bestMval.copyfrom(currMval);
										System.out.println("Draw candidate chosen");
										bDrawChosen = true;
									}
								}
								else 
								{
									
									if (iRounds == 6)
									{
										System.out.println("DBG 151210: NEW BEST MVAL CHOSEN!");
										System.out.println("DBG 151210:new: " + currMval.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
										System.out.println("DBG 151210:old: " + bestMval.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
										System.out.println("DBG 151210: movevalue comparison params: iColor:" + iColor + " iAlg:" + iAlg + " iTurn: " + iTurn);
										System.out.println("DBG 151210: mval comp info:" + currMval.sDBG);
									}
									
									mBestCand = m;
									bestBoard = nb;
									bestMval.copyfrom(currMval);
									if (iRounds>=3) 
									{
										String sMessage=" pick:"+i;  // 160430 debugging for pruning problems
										if (iGFGResort != -1) sMessage = sMessage + " " + iGFGResort + bGFGTimeOut;
										bestMval.sMessage=sMessage;
									}
									//System.out.println(bestMval.dumpstr(iAlg));
									//System.out.println("DBG: bestMval.bBlackChecked:" + bestMval.bBlackChecked);
									//basemv.copyfrom(bestMval);
									//if (iRounds ==2 ) System.out.println("New best Mval found!!!!!");
									// $$$$$ 140320 HOW DO WE KNOW WHOSE TURN IT IS INSIDE MVAL OBJECT??? iMaxWhiteThreat and iMaxBlackThreatShould be taken care accordingly
									// iRounds = even -> enemy's turn ?
								}
							}
							else
							{	
								//if (iRounds == 2) System.out.println("Old best persisted... ");
							}
							/*
							if (balDif > bestBalDif) 
							{
								bestBalDif = balDif;
								bestCovDif = covDif;
								mBestCand = m;
								bestBoard = nb;
								basemv.copyfrom(bestMval);
							}
							else if ((balDif == bestBalDif) && (iChecked > iBestChecked))
							{
								iBestChecked = iChecked;
								mBestCand = m;
								bestBoard = nb;
								basemv.copyfrom(bestMval);
							}
							else if ((balDif == bestBalDif) && (iBestChecked == iChecked) && (covDif > bestCovDif))
							{
								bestCovDif = covDif;
								mBestCand = m;
								bestBoard = nb;
								basemv.copyfrom(bestMval);
							}
							*/
						}
					}  // if bOk1
				}
				else
				{
					//System.out.println("Move no good: "+m.moveStr() );
					//System.exit(0);
				}
				//System.out.println("DBG150610: MOVEINDEX LOOP END!!!!");
				//System.out.println("DBG151013 (S5):" + basemv.sRoute+ " mAct.size=" + mAct.getSize()+ " round: " + i + " done.");
			} // for loop for moveindex (mAct)
			
			if ((iRounds <=0) && (iMoveTotal < iMvecTotal))
			{
				if ((bestBoard == null) || (bestMval == null))
				{
					System.out.println("DBG150525: WILL CRASH SOON! NULLS AROUND!");
					if (bestBoard == null) System.out.println("bestBoard == null");
					if (bestMval == null) System.out.println("bestMval == null");
					System.out.println("bestMval:" + bestMval.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
				}
				
				if ((thisMVal.isBetterthan(bestMval,iColor,iAlg,1-iColor, iRounds)) || (thisMVal.equalBalance(iAlg,1-iColor,bestMval)) || (iMaxEnemyCapt>Math.abs(thisMVal.iPieceBalance - bestMval.iPieceBalance))) bCalcAgain = true;
				
				//System.out.println("DBG151013 (S4):" + basemv.sRoute+ " bCalcAgain:" + bCalcAgain);
				//bCalcAgain = true;
				/*
				System.out.println("DBG151012 calcagain factors:");
				System.out.println("..a:" + thisMVal.isBetterthan(bestMval,iColor,iAlg,1-iColor));
				System.out.println("..b:" + thisMVal.equalBalance(iAlg,1-iColor,bestMval));
				System.out.println("..c:" + (iMaxEnemyCapt>Math.abs(thisMVal.iPieceBalance - bestMval.iPieceBalance)));
				System.out.println("DBG150930: bCalcAgain:" + bCalcAgain + " iMaxPressure: " + iMaxPressure + " iMaxEnemyCapt:" + iMaxEnemyCapt);
				System.exit(0);	
				*/	
				if (((iColor == piece.WHITE) && (bestBoard.iBlackCheckMoves != 0) && (bestMval.bWhiteInstWin)) ||
				((iColor == piece.BLACK) && (bestBoard.iWhiteCheckMoves != 0) && (bestMval.bBlackInstWin))) bCalcAgain = true;
				
				/*if (bCalcAgain) System.out.println("DBG150311: NEW XYZZY, BCALCAGAIN = TRUE");
				else System.out.println("DBG150311: NEW XYZZY, BCALCAGAIN = FALSE");
				*/
			}
			
		}   // while repcycles etc.
		
		if (iRounds == ChessUI.getiAnalStartLevel())
		{
					ChessUI.setBestMval(bestMval);
					ChessUI.setiAnalRoundsDone(iMoveTotal);
					ChessUI.setAnalCurrent("done.");
					ChessUI.setlAnalEndTime(System.currentTimeMillis());
					ChessUI.monRefresh(true);
		}
		if ((iRounds == ChessUI.getiAnalStartLevel()-1) && (iColor == ChessUI.getiAnalStartColor()))
		{
			ChessUI.setBestPreMval(bestMval);
			ChessUI.setiPrelRoundsDone(ChessUI.getiPrelRoundsTotal());
			ChessUI.setAnalPreCurrent("done.");
		}
		
		//System.out.println("DBG150119 after loop:"+iRounds+":" + bestMval.dumpstr(iAlg,movevalue.DUMPMODE_SHORT) + " bb=null:" + (bestBoard==null));
		if (bestBoard==null)
		{
			//System.out.println("DBG:150119: null bestBoard");
			//System.out.println("DBG:150119: thismval:" + thisMVal.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
			//System.out.println("DBG:150119: bestmval:" + bestMval.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
			//bestMval.copyfrom(thisMVal);
		}
		
		
		dbgPrintln("#####################################");
		dbgPrintln("ALL MOVES ANALYZED NOW. LEVEL = " + iRounds + " ,bCheckMate = " + bCheckMate);
		dbgPrintln("bCheckMate = " + bCheckMate);
		dbgPrintln("#####################################");
		
		if (iMoveTotal == 0)
		{
			//System.out.println("MT0-branch 140410");
			bNoMoves = true;
			dbgPrintln("I could not bloody move at all. What now?" + checkcount);
			dbgPrintln("Checkmate = " + bCheckMate);
			dbgPrintln("Count of checkers = " + checkcount);
			
			movevalue mval = basemv.copy();
			
			// clean up defaults
			if (iColor == 0) mval.bWhiteCheckMate=false;
			else mval.bBlackCheckMate=false;
			
			dbgPrintln("InsideBlocked...:"+mval.dumpstr(iAlg));
			mval.pushNewSeq(0,0,0,0,-1);
			mval.iPieceBalance = balDif;
			mval.iCoverageBalance = covDif;
			mval.iMoveCount = iMoveCount;
		
			if (iColor == 0) mval.bWhiteBlocked = true;
			else mval.bBlackBlocked = true;
			
			if (checkcount > 0)
			{
				if (iColor ==0) 
				{
					mval.bWhiteCheckMate = true;
					mval.bWhiteChecked = true;
				}
				else 
				{
					mval.bBlackCheckMate = true;
					mval.bBlackChecked = true;
				}
			}
			
			dbgPrintln("Returning from blocked:"+mval.dumpstr(iAlg));
			basemv.copyfrom(mval);
			bestMval.copyfrom(mval);
		
			if (checkcount > 0)
			{
				/*
				if (iRounds == 2) 
				{
					System.out.println("Returning null from funny branch!!!! 140330");
					System.exit(0);
				}
				*/
				
				if (mvv != null)
				{
					mval_combo mvc = new mval_combo(bestMval,null, this);
					mvv.add(mvc, 1-iColor,iAlg,iColor);
				}
				
				return null;
			}
			//System.exit(0);
			
			
		}
		
		if (iRounds == 2)
		{
			//System.out.println("IR2 XX3 140410");
			//System.out.println("C:" +currMval.dumpstr(iAlg));
			//System.out.println("B:" +bestMval.dumpstr(iAlg));
			//if (bestBoard == null) System.out.println("bestBoard = null!");
		}
		
		if (bestBoard == null)
		{	
			/*
			System.out.println("ERROR: NULL BESTBOARD OCCURRED. Probably game is around to be over..");
			System.out.println("Call params: iColor" + iColor + ", iRounds: " + iRounds);
			System.out.println("bestBaldif = " + bestBalDif);
			System.out.println("iPieceCtr = " + iPieceCtr);
			System.out.println("iMoveTotal = " + iMoveTotal);
			//throw new Exception ("NULL BESTBOARD...");
			*/
		}
		
		if (mvv != null)
		{
			if ((bestMval.bBlackCheckMate) || (bestMval.bWhiteCheckMate)) bestBoard = null;
			
			mval_combo mvc = new mval_combo(bestMval,bestBoard, this);
			mvv.add(mvc, 1-iColor,iAlg,iColor);
		}
		
		if (mvec != null)
		{
			//System.out.println("MVEC IS NOT NULL");
			
			int iTurn = iColor;
			if ((iRounds % 2) == 0) iTurn = 1 - iColor; 
			
			//System.out.println("MVEC CONTENTS ARE:");
			//mvec.dumpall(iAlg);
			
			mval_combo mvc = mvec.get_best(iRounds,iColor,iAlg,iTurn,ghist, bDebug, bStrategyImpact, regb);
			//System.out.println("DBG150312: mvc.get_best returned. sMovOrd:" + sMovOrd+ " sMoveOrder:"+sMoveOrder);
			//System.out.println("DBG150312: mvc.sRet:" + mvec.sRet);
			sMoveOrder = mvec.sRet;
			
			if (mvc != null)
			{
				//System.out.println("MVEC BEST IS:" + mvc.mv.dumpstr(iAlg));
				//System.out.println("Going by multiprocessed result !!!");
				bestMval.copyfrom(mvc.mv);
				bestBoard = mvc.cb_base.copy();
			}
			else
			{
				//System.out.println("Setting bestBoard = null from null mvc");
				bestBoard = null;
			}
			
			
			/*
			if (mvc.mv.equals(bestMval)) 
			{
				basemv.copyfrom(mvc.mv);
				bestBoard = mvc.cb.copy();
				if (!bestBoard.equals(mvc.cb_base)) 
				{
					System.out.println("Board validation failed miserably.");
					System.exit(0);
				}
				else System.out.println("It's OK! Could return  from MVEC!!!!"); 
				
			}
			else
			{
				System.out.println("Not quite the same, but...");
				
				if (mvc.mv.similarMates(bestMval))
				{
					System.out.println("Similar mate state. It's OK!");
				}
				else
				{
					System.out.println("Discrepancy! bestMval is : " + bestMval.dumpstr());
					System.out.println("MVEC CONTENTS ARE:");
					mvec.dumpall();
					System.exit(0);
				}
			}
			*/
		}
		
		
		basemv.copyfrom(bestMval);
		
		if (bestBoard == null)
		{
			//System.out.println("DBG150119:AA:NBBUP!");
			return null;
			//System.out.println(iRounds +": NULL BESTBOARD COMING UP!!");
			//System.out.println("...." + bestMval.dumpstr(iAlg));
		}
		
		
		// DEEPCONTROL EXPERIMENT 141203 $$$$
		/*
		if (iAlg == movevalue.ALG_DEEP_CHECK)
		{
			if ((iRounds <= 0) && 
				((iColor == piece.WHITE) && (bestBoard.iBlackCheckMoves != 0)) ||
				((iColor == piece.BLACK) && (bestBoard.iWhiteCheckMoves != 0)))
			{
				if (dtc == null)
				{
					System.out.println("DBG 141203. WHOA, about to return a checkable bestBoard. Need to look further. Call again with all moves");
					System.out.println("basemv dumpstr is:" + basemv.dumpstr(iAlg));
					
					movevalue mvOrig = basemv.copy();
					
					basemv.popSeq();
					System.out.println("after pop: basemv dumpstr is:" + basemv.dumpstr(iAlg));
					dt_control ndtc2 = new dt_control(1-iColor,MAX_CHESS_RECURSION_DPTH+1,0);
					
					chessboard deep_bestboard = findAndDoBestMove(iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv, false, ndtc2);
					
					return deep_bestboard;
					
					
					//movevalue mvDeep = basemv.copy(); 
					
					//if (mvOrig.isBetterthan(mvDeep,iColor,iAlg,iColor)) return deep_bestboard;
					//else return bestBoard;
					
					
				}
			}
		}
		*/
		/*
		System.out.println("150207: XYZZY: DBG END OF LOOP: mvectotal:" + iMvecTotal + " movetotal: " + iMoveTotal + " mAct proc:" + mAct.getProcessedCount());
		boolean bFullCalc = false;
		if ((iRounds <= 0) && (iMoveTotal < iMvecTotal) && ((thisMVal.isBetterthan(bestMval,iColor,iAlg,1-iColor)) || (thisMVal.equalBalance(iAlg,1-iColor,bestMval))))
		{
			System.out.println("DBG150127:WHOA (XYZZY)! IT LOOKS LIKE WE MAKE THE SITUATION WORSE!");
			bFullCalc = true;
		}
		
		if ((iRounds <= 0)  && 
				((iColor == piece.WHITE) && (bestBoard.iBlackCheckMoves != 0) && (bestMval.bWhiteInstWin)) ||
				((iColor == piece.BLACK) && (bestBoard.iWhiteCheckMoves != 0) && (bestMval.bBlackInstWin)) ||
				bFullCalc)
		{
			if (dtc == null)
			{
				//System.out.println("DBG 141203. WHOA, about to return a checkable bestBoard. Need to look further. Call again with all moves");
				//System.out.println("basemv dumpstr is:" + basemv.dumpstr(iAlg));
				
				movevalue mvOrig = basemv.copy();
				
				basemv.popSeq();
				//System.out.println("after pop: basemv dumpstr is:" + basemv.dumpstr(iAlg));
				dt_control ndtc2 = new dt_control(1-iColor,1,0,dt_control.ALL_MOVES);
				
				chessboard deep_bestboard = findAndDoBestMove(iColor,iRounds,basemv,iAlg,bDebug,ghist,mvv, false, ndtc2,l3best,l2best);
				
				return deep_bestboard;
			}
			
		}
		*/
		
		if (strat != null )
		{
			strat.dump();
			sMoveOrder = strat.getMoveOrder();
			//if (iRounds >= 3) System.out.println("DBG151218: mosadd @FAD iR: " + iRounds + " iC:" + iColor+ " st.iC:" + strat.iColor+ " FEN: " + this.FEN() + " " + sMoveOrder);
			mos.addItem(this.FEN(),iRounds,sMoveOrder);
		}
		//if (iRounds >= 2) System.out.println("DBG150312: findanddo "+iRounds+" returning: " + sMoveOrder);
		
		//if (iRounds >= 2) System.out.println("DBG150425: Strategy can be impacted???" + bStrategyImpact + " iColor = " + iColor + " strat is null:" + (strat == null));
		
		if ((bStrategyImpact) && (strat != null) && (iAlg == movevalue.ALG_ALLOW_STRATEGY))
		{
			String sMove = strat.getBestMove();
			chessboard nb = new chessboard();
			nb = this.copy();
			nb.domove(sMove,iColor);
			bestBoard = nb.copy();
		}
		
		if (bMidPawnMode) 
		{
			System.out.println("Inside midpawn mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsMidPawnOpener()) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("MIDPAWNOPEN:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bPawnPressureMode) 
		{
			System.out.println("Inside bPawnPressureMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsPawnPressureOpener(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("PAWNPRESSOPEN:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bFianchettoPrevMode) 
		{
			System.out.println("Inside bFianchettoPrevMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsPrepForFianchetto(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("FIANCHETTOPREVOPEN:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bBishopE3Mode) 
		{
			System.out.println("Inside bBishopE3Mode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsBishopE3Opener()) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("BISHOPE3Open:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bF2StepMode) 
		{
			System.out.println("Inside bF2StepMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsF2StepOpener(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("bF2StepMode:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bPawnFrontMode)
		{
			System.out.println("Inside bPawnFrontMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsPawnFrontOpener(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("bPawnFrontMode:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bBackRowRookMode)
		{
			System.out.println("Inside bBackRowRookMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsBackRowRook(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("bBackRowRookMode:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bKnightToMiddleMode)
		{
			System.out.println("Inside bKnightToMiddleMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsKnightToMiddle()) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("bIsKnightToMiddle:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bQueenFirstMoveMode)
		{
			System.out.println("Inside bQueenFirstMoveMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.isQueenFirstMove(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("bQueenFirstMoveMode:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bC2StepMode) 
		{
			System.out.println("Inside bC2StepMode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsC2StepOpener(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("bC2StepMode:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		if (bBishopF4Mode) 
		{
			System.out.println("Inside bBishopF4Mode mode:");
			System.out.println("BestMval:" + bestMval.dumpstr(iAlg));
			String sMidPString = "";
			
			for (int i=0;i<mAct.getSize();i++)
			{
				move m = mAct.getMoveAt(i);
				if (m.bIsBishopF4Opener(this)) 
				{
					System.out.println(m.moveStrLong());
					chessboard cbmp = this.copy();
					cbmp.domove(m.moveStrCaps(),iColor);
					cbmp.redoVectorsAndCoverages(1-iColor,iAlg);
					movevalue mmp = new movevalue("");
					mmp.setBalancesFromBoard(cbmp,1-iColor,iAlg);
					System.out.println("bBishopF4Mode:" + mmp.dumpstr(iAlg));
					if (bestMval.equalBalance(iAlg,1-iColor,mmp)) 
					{
						System.out.println("GOODTOGO!!");
						sMidPString=sMidPString+" "+m.moveStrCaps();
					}
				}
			}
			
			System.out.println(sMidPString.trim());
			sMoveOrder = sMidPString.trim();
		}
		
		bestBoard.sMoveOrder = sMoveOrder;
		//System.out.println("DBG170803: returning smoveorder:" + bestBoard.sMoveOrder);
		return bestBoard;
			
	} // findAndDoBestMove ending here !!!!
	
king locateKing(int iColor)
{
	if ((iColor == piece.WHITE) && (m_kw != null)) return m_kw;
	if ((iColor == piece.BLACK) && (m_kb != null)) return m_kb;
	
	for (int i=1;i<=8;i++)
		for (int j=1;j<=8;j++)
		{
			piece p = blocks[i][j];
			if (p!= null)
			{
				if ((p.iType == piece.KING) && (p.iColor == iColor)) 
				{
					if (iColor == piece.WHITE) m_kw = (king)p;
					else m_kb = (king)p;
					return (king)p;
				}
			}
		}
		
	return null;	
}  

piece locatePiece (int iColor, int iType)
{
	if (iColor == piece.WHITE)
	{
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
			{
				piece p = blocks[i][j];
				if (p!= null)
				{
					if ((p.iType == iType) && (p.iColor == iColor)) return p;
				}
			}
		return null;	
	}	
	else 
	{
		for (int i=1;i<=8;i++)
			for (int j=8;j>=1;j--)
			{
				piece p = blocks[i][j];
				if (p!= null)
				{
					if ((p.iType == iType) && (p.iColor == iColor)) return p;
				}
			}
		return null;
	}
	
}

boolean bCanDoMove(move m, int iRounds, int iColor, int iAlg, int iRiskFree, int checkcount, dt_control dtc, boolean bDoTraverseFilter, boolean bPressure0, int iMaxMoveVal, int iMaxPressure, int iMCapTarget, int l1goal, int l0goal)
{
	boolean bOk = true;
							
	//if (iRounds == 1) System.out.println("  DBG 150126(A) bOK = " + bOk + " M: " +m.moveStr() + " max:" + iMaxMoveVal + " l1goal:" + l1goal + " iMaxPressure:" + iMaxPressure);
	//System.out.println("DBG150624:BCDM (1): " + m.moveStrLong());
	
	if (iAlg != movevalue.ALG_NO_OPT_TRAVERSE)
	{
		/*if ((p.xk==4) && (p.yk==4) && (p.iType == piece.KNIGHT))
			System.out.println("DBG 141115 PROBLEM KNIGHT HERE! going to:" + m.xtar +"," + m.ytar);
		System.out.println("m.isRisky() : " + m.isRisky());	
		*/

		bOk = ((m.iCaptValue + 1 >= iMaxMoveVal) || (iRounds != 0) || m.isSpecial() || !bDoTraverseFilter)
				&&
				((iRounds != 0) || (iRiskFree <= 0) || (!m.isRisky()) || (m.isCheck()) || (m.isRevCheck()));
		// 141221 prevent doing risky checks on level 0. 
		// 150126: WHY? This will prevent mates where risk is caused by king!!
		
		if (bPressure0) 
		{
			bOk = ((m.bPressure) && (m.p.pvalue() >= iMaxPressure) || ((m.iCaptValue >= 1)) || (m.isSpecial()) || (m.isCheck()) || (m.isRevCheck())) && (!m.bRisky);
			//System.out.println("DBG150120:AA,bOk:" + bOk);
		}
		// $$$$ 141217: rounds == 1 ehto tähän mukaan -> kopioi tämä domoven jälkeen. 
		/*
		if (((m.iCaptValue + 1 < iMaxMoveVal) && (iRounds == 1) && !m.isSpecial() && bDoTraverseFilter) || ((iRounds == 1) && (iRiskFree > 0) && (m.isRisky()) && (!m.isCheck()) && (!m.isRevCheck()) && (!m.bPressure) )) 
			bOk = false;
		else bOk = true;
		*/
		
		if (checkcount > 0) bOk = true;
		
	}
	else bOk = true;
	
	//if (iRounds == 1) System.out.println("  DBG 150126(C) bOK = " + bOk + " M: " +m.moveStr());
	
	if ((dtc != null) && (dtc.bMustCheck()) && (dtc.iCheckColor == iColor) && (!m.isCheck()) && (!m.isRevCheck())) bOk = false;
	
	
	// 141213 perf optimization attempt
	// if ((dtc != null) && (dtc.iCheckColor != iColor)) bOk = true;
	if ((dtc != null) && (dtc.iCheckColor != iColor))
	{
		if (!dtc.bLastLevel()) bOk = true;
		else
			bOk = ((m.iCaptValue + 1 >= iMaxMoveVal) || m.isSpecial() || !bDoTraverseFilter) && ((iRiskFree <= 0) || (!m.isRisky()));
	}
	
	//if (iRounds == 1) System.out.println("  DBG 150126(D) bOK = " + bOk + " M: " +m.moveStr());
	
	if ((iAlg == movevalue.ALG_DEEP_CHECK) && (iRounds == 0) && (((m.isCheck()) || (m.isRevCheck()))))
	{
		bOk = true;
	}
	
	if ((dtc != null) && (dtc.iCheckState == dt_control.ALL_MOVES)) bOk = true;
	
	//System.out.println(" 141227 FINDANDDO(E) bOK = " + bOk + " M: " +m.moveStr());
	
	if ((iRounds == 1) && (iAlg == movevalue.ALG_NEW_TRAVERSE1))
	{
		if ((m.iCaptValue < l1goal) && (!m.isCheck()) && (!m.isRevCheck()) && (!m.isSpecial()) && (!m.bPressure))
		{
			//System.out.println("DBG 150108: abandon " + m.moveStrLong() + " l1goal:" + l1goal);
			bOk = false;
		}
		//else System.out.println("DBG 150108: accept " + m.moveStrLong() + " l1goal:" + l1goal);
	}
	
	//if (iRounds == 1) System.out.println("  DBG 150126(E) bOK = " + bOk + " M: " +m.moveStr());
	
	if ((iRounds == 0) && (iAlg == movevalue.ALG_NEW_TRAVERSE1))
	{
		if ((m.iCaptValue < l0goal) && (!m.isCheck()) && (!m.isRevCheck()) && (!m.isSpecial()) && (!m.bPressure))
		{
			//System.out.println("DBG 150108: abandon " + m.moveStrLong() + " l1goal:" + l1goal);
			bOk = false;
		}
		//else System.out.println("DBG 150108: accept " + m.moveStrLong() + " l1goal:" + l1goal);
	} 
	
	if ((m.isCheck()) || (m.isRevCheck())) bOk = true;
	
	if (m.isSpecial()) bOk = true;
	
	//System.out.println("DBG150624:BCDM (2): " + m.moveStr());
	
	return bOk;
}


int iCountCheckers(king pking)
{
	int iColor;
	int iCount = 0;

	if (pking == null) return -1;
	
	for (int i=1;i<=8;i++)
		for (int j=1;j<=8;j++)
		{
			piece p = blocks[i][j];
			if (p!= null)
			{
				if (p.iColor != pking.iColor)
				{
					Vector v = p.moveVector(this);
					for (int k=0;k<v.size();k++)
					{
						move m2 = (move)v.elementAt(k);
						if ((m2.xtar == pking.xk) && (m2.ytar == pking.yk))
						{
							iCount++;
							dbgPrintln("Checker " + iCount + " found! at " +p.xk + "," + p.yk);
						}
					}
					
				}
			}
		}
	
	if (iCount > 1)
	{
		dbgPrintln("Highly suspicious!!! More than 1 checker");
	}
	
	return iCount;
}
		
piece locateFirstChecker(king pking)
{
	if (pking == null) return null;
	
	for (int i=1;i<=8;i++)
		for (int j=1;j<=8;j++)
		{
			piece p = blocks[i][j];
			if (p!= null)
			{
				if (p.iColor != pking.iColor)
				{
					Vector v = p.moveVector(this);
					for (int k=0;k<v.size();k++)
					{
						move m2 = (move)v.elementAt(k);
						if ((m2.xtar == pking.xk) && (m2.ytar == pking.yk)) return p;
					}
					
				}
			}
		}
	
	return null;
}

Vector locateCheckersVector (king pking)
{
	Vector vRet = new Vector();
	
	if (pking == null) return null;
	
	for (int i=1;i<=8;i++)
		for (int j=1;j<=8;j++)
		{
			piece p = blocks[i][j];
			if (p!= null)
			{
				if (p.iColor != pking.iColor)
				{
					Vector v = p.moveVector(this);
					for (int k=0;k<v.size();k++)
					{
						move m2 = (move)v.elementAt(k);
						if ((m2.xtar == pking.xk) && (m2.ytar == pking.yk)) vRet.addElement(p);
					}
					
				}
			}
		}

	return vRet;	
}
		
boolean bMoveIsGood(move m, piece p, king k,Vector vcheck)
{
	boolean bOneCP = false;
	
	if (vcheck == null) return true;
	if (vcheck.size() == 0) return true;
	if (vcheck.size() == 1) bOneCP = true;
	
	//System.out.println("DBG150205: bMoveIsGood called (0). There is a checker. Pretest: "+ m.moveStr() + " vcheck.size:" + vcheck.size());
	
	if ((p.iType != piece.KING) && (vcheck.size() > 1)) 
	{
		int xkc = -1;
		int ykc = -1;
		
		for (int ii=0;ii<vcheck.size();ii++)
		{
			piece pp = (piece)vcheck.elementAt(ii);
			//System.out.println("c piece: " + pp.xk +"," + pp.yk);
			
			if (((pp.xk != xkc) && (xkc != -1))  || ((pp.yk != ykc) && (ykc != -1))) return false;
			xkc = pp.xk;
			ykc = pp.yk;
		}
		//System.out.println("All the same!");
		bOneCP = true;
		//return false;
	}
	
	piece pcheck = (piece)vcheck.elementAt(0);
	
	//System.out.println("DBG150205: bMoveIsGood called. There is a checker. Testing move: "+ m.moveStr() + ", bOneCP:" + bOneCP);
	
	// kill the checking piece, if's not the king it's trivial if it's the only one
	if (((p.iType != piece.KING) && ((m.xtar == pcheck.xk) && (m.ytar == pcheck.yk))) && (bOneCP)) return true;
	
	if ((p.iType == piece.PAWN) && (m.bCapture) && (blocks[m.xtar][m.ytar] == null) && (pcheck.iType == piece.PAWN))
	{
		if (((p.iColor == piece.WHITE) && (m.ytar == 6) && (m.xtar==pcheck.xk)) ||
		((p.iColor == piece.BLACK) && (m.ytar == 3) && (m.xtar==pcheck.xk)))		
		//System.out.println("DBG150205: EN PASSANT CANDIDATE!");
		return true;
	}
	
	// castling moves under check are not allowed
	if ((p.iType == piece.KING) && (!bOneCP) && (p.xk == 5) && ((m.xtar == 3) || (m.xtar==7))) return false;
	
	Vector v = pcheck.moveVector(this);
	
	if ((p.iType != piece.KING) && (bOneCP))
	{
		// did we get in between? that would be good as well
		//System.out.println("DBG141220: bMoveIsGood: " + m.moveStr() + " inbtw check.");
		// 141220 old code didn't manage if the checking piece was pinned -> pinned moves didn't exist in the vector
		/*
		for (int i=0;i<v.size();i++)
		{
			move m2 = (move)v.elementAt(i);
			//System.out.println("DBG141220: bMoveIsGood: loop checker piece " + m2.moveStr());
			
			if ((m.xtar == m2.xtar) && (m.ytar == m2.ytar) &&  // we found a hit in checker's move list
			   (Integer.signum(pcheck.xk-m2.xtar) == Integer.signum(m2.xtar-k.xk)) && 
			   (Integer.signum(pcheck.yk-m2.ytar) == Integer.signum(m2.ytar-k.yk)))
			return true; 
		}
		*/
		
		return piece.directlyBetween(pcheck.xk,pcheck.yk,m.xtar,m.ytar,k.xk,k.yk);
		
	}
	else
	{
		if (this.blocks[m.xtar][m.ytar] == null) // did we get out of way
		{
			
			// from here on new code 140206 to handle multiple checkers
			// this is all about king's escape
			for (int r=0;r<vcheck.size();r++)
			{
				piece pc = (piece)vcheck.elementAt(r);
				if (p.iColor == piece.WHITE)
				{
					if (!bBlackCoverage[m.xtar][m.ytar])
					{
						if ((pc.moveinline(m,p)) &&(pc.couldhit(m.xtar,m.ytar))) return false;
					}
				}
				else
				{
					if (!bWhiteCoverage[m.xtar][m.ytar])
					{
						if ((pc.moveinline(m,p)) &&(pc.couldhit(m.xtar,m.ytar))) return false;
					}
				}
			}
			return true;
		}
		
		dbgPrintln("But ... he didn't.. to(" + m.xtar+"," + m.ytar + ")");
		
		// we move the king to beat either the checker or some other piece
		
		piece pOther = blocks[m.xtar][m.ytar];
		int iColOther = -1;
		if ((p.iType == piece.KING) && (pOther != null)) iColOther = pOther.iColor;
		
		
		if (((p.iType == piece.KING) && ((m.xtar == pcheck.xk) && (m.ytar == pcheck.yk))) || (iColOther == 1-p.iColor))
		{
			
			if (pOther.bProt) return false;   // can't capture a protected piece
			else
			{
				for (int r=0;r<vcheck.size();r++)  // and can't capture a piece that will be protected after the move
				{
					piece pc = (piece)vcheck.elementAt(r);
					if ((pc.moveinline(m,p)) &&(pc.couldhit(m.xtar,m.ytar))) return false;
				}
				
				return true;
			}
			
		}
		
	}
	
	dbgPrintln("About to return false here OIKS to(" + m.xtar+"," + m.ytar + ")");
	
	return false;
}	
	
void dropPinnedMoves (king k)
{
	int locx = k.xk;
	int locy = k.yk;
	
	int movX[] = {0,1,1,1,0,-1,-1,-1};
	int movY[] = {1,1,0,-1,-1,-1,0,1};
	
	boolean bEnpPin = false;
	
	int enp_x1 = -1;
	int enp_y1 = -1;
	int enp_x2 = -1;
	int enp_y2 = -1;
	
	if (lm_vector != null)
	{
		enp_x1 = ((int)lm_vector.elementAt(0));
		enp_y1 = ((int)lm_vector.elementAt(1));
		enp_x2 = ((int)lm_vector.elementAt(2));
		enp_y2 = ((int)lm_vector.elementAt(3));
		piece p=blocks[enp_x2][enp_y2];
		if ((p.iType == piece.PAWN) && (Math.abs(enp_y2-enp_y1)==2) && (p.iColor != k.iColor)) bEnpPin = true;
	}
	
	
	//System.out.println("DBG 150207: DPM ENTER. KING AT " + k.xk +"," + k.yk + "  lastmove: " + lastmoveString() + " benp:" + bEnpPin);
	/*
	piece p = blocks[6][8];
	if (p != null) 
	{
		System.out.println("DBG160519(A): Rook 6 8 mv:");
		p.dumpmoveVector(p.moveVector(this));
	}
	
	piece pb = blocks[6][6];
	if (pb != null) 
	{
		System.out.println("DBG160519(A): Bishop 6 6 mv:");
		pb.dumpmoveVector(pb.moveVector(this));
	}
	*/
	
	
	//prefixdump("DPM ");
	
	for (int iDir = 0; iDir < 8;iDir++)
	{
		piece p1 = null;
		piece p2 = null;
		
		//System.out.println("DPM GOING TO DIR " + iDir);
		
		int iEps = 0;
		for (int iStep = 1; iStep <= 7; iStep++)   // unbelievable, equality missing until 140412!!! 
		{
			int newX = k.xk + iStep * movX[iDir];
			int newY = k.yk + iStep * movY[iDir];
			
			if ((newX < 1) || (newX > 8) || (newY < 1) || (newY > 8)) break;
			
			if (p1 == null) p1 = blocks[newX][newY];
			
			if (p1 != null)
			{
				if ((p1 != null) && (p1.iType == piece.PAWN) && (p1.iColor != k.iColor) &&
				(newX == enp_x2) && (newY == enp_y2)) 
				{
					iEps = iStep;
					if ((p1.xk != newX) || (p1.yk != newY))
					{
						System.out.println("HERE'S AN ENPPIN SCENARIO at " + iEps + "!!! iDir:"+iDir + " newX:" + newX + " newY "+ newY + " p1.xk:" + p1.xk + " p1.yk:" + p1.yk);
						System.exit(0);
					}
					if ((iDir == 2) || (iDir == 6)) p1 = null;  // $$$ remove for start52w.dat 170608
					else iStep = 8;
					// see also enppin.dat enppin2.dat enpppin3.dat
				}
				else if (p1.iColor != k.iColor) iStep = 8;
				else 
				{
					// $$$$ 140412 debug
					//if (p1.iType == piece.KNIGHT) System.out.println("DPM KNIGTH PIN CANDIDATE exists at " + p1.xk +"," + p1.yk + " step:" + iStep + " Dir:" + iDir);
				}
			}
			
			if (p1 != null) 
			{
				//System.out.println("DBG150207 DPM finding p2 at :" + newX + "," + newY + " p1 at:" + p1.xk +"," + p1.yk + " iStep:" + iStep);
				p2 = blocks[newX][newY];
				boolean bENPPinSkip = false;
				
				if ((p2 != null) && (p2.iType == piece.PAWN) && (p1.iType == piece.PAWN) && (newX == enp_x2) && (newY == enp_y2) && (Math.abs(p1.xk-newX) == 1) && (bEnpPin))
				{
					//System.out.println("DBGDBG UHOH, HERE'S THE ENP PIN!!!");
					bENPPinSkip = true;
				}
				
				//System.out.println("DBG160519: AA0: " + (p2 != p1) + " " + (p2 != null) + " " +(!bENPPinSkip));
				
				if ((p2 != p1)  && (p2 != null) && (!bENPPinSkip))
				//if ((p2 != p1)  && (p2 != null))
				{
					if (p2.iColor != k.iColor)
					{
						//System.out.println("DBG160519:LOC AA");
						if ((p2.couldhit(k)) && (p2.iType != piece.PAWN))
						{
							//System.out.println("DBG150603:DPM POTENTIAL PINNING ISSUE!!! " + newX +"," + newY + " pinned piece at: " + p1.xk +"," + p1.yk + " bENPPinSkip:" + bENPPinSkip);
							if ((p1.iType == piece.PAWN ) && (bENPPinSkip))
							{
								pawn pa = (pawn)p1;
								pa.validatePinMoves( k, this,bENPPinSkip);
							}
							else p1.validatePinMoves( k, this);
						}
						else
						{							
							//System.out.println("iStep=7 (A)");
							iStep = 7; // scenario where the other piece behind pin candidate is enemy piece, but can't reach the king
						}
						
					}
					else
					{
						//System.out.println("iStep=7 (B)");
						iStep = 7; // can be a pinning issue since there's another piece on the way, scenario where the other piece behind pin candidate is own piece
					}
					
				}
			}
		}
	}
	
	/*
	p = blocks[6][8];
	if (p != null) 
	{
		System.out.println("DPM Leave DBG160519(B): Rook 6 8 mv:");
		p.dumpmoveVector(p.moveVector(this));
	}
	*/
	
}	
	
int clearMoveVectorsUnderCheck(int iColor, king kk, Vector vcheck)
{
	int mc = 0;
	
	for (int i=1;i<=8;i++)
		for (int j=1;j<=8;j++)
		{
			piece p = blocks[i][j];
			if (p != null)
			{
				if (p.iColor == iColor)
				{
					Vector mv = p.moveVector(this);
					for (int k=0;k<mv.size();k++)
					{
						move m = (move)mv.elementAt(k);
						
						if (!bMoveIsGood(m,p,kk,vcheck))
						{
							if ((k<0) || (k>=mv.size()))
							{
								System.out.println("DBG:: SOMETHING FISHY GOING ON!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								System.out.println("DBG:: k = " + k + ", mv.size() = " + mv.size());
								System.out.println("DBG:: IT CRASHED HERE ON 140123. NO IDEA WHY");
							
							}
							else
							{
							mv.remove(k);
							k--;
							}
						}
					}
					mc = mc + mv.size();
				}
				
			}
		}
		
	return mc;	
}

boolean bKingInCastle(int iColor)
{
	int sy, ky;
	int sxl, sxh;
	int kicpoints = 0;
	
	if (iColor == piece.WHITE)
	{
		sy = 2;
		ky = 1;
	}
	else
	{
		sy = 7;
		ky = 8;
	}
	
	piece pk1 = blocks[3][ky];
	piece pk2 = blocks[7][ky];
	
	if ((pk1 != null) && (pk1.iType == piece.KING) && (pk1.iColor == iColor))
	{
		sxl = 1;
		sxh = 3;
	} else
	if ((pk2 != null) &&(pk2.iType == piece.KING) && (pk2.iColor == iColor))
	{
		sxl = 6;
		sxh = 8;
	} else return false;
	
	for (int sx = sxl; sx <= sxh; sx++)
	{
		piece p = blocks[sx][sy];
		if (p == null) return false;
		if (p.iColor != iColor) return false;
		if (p.bThreat) return false;
	}
	
	return true;
}

int iKicPoints (int iColor)
// KingInCastlePoints
{
	int sy, ky;
	int sxl, sxh;
	int kicpoints = 0;
	/*
		return int kicpoints
		
		kuninkaan siirto vs. 
		chessboard.iMoveCounter - king.iLastMove  = diff  kicpoints+= 4-diff. If = 0 return
		pawnwall +1 for pawn in place +1 for nothreat
		if enemypiece -> return 0
		
	*/
	
	if (iColor == piece.WHITE)
	{
		sy = 2;
		ky = 1;
	}
	else
	{
		sy = 7;
		ky = 8;
	}
	
	piece pk1 = blocks[3][ky];
	piece pk2 = blocks[7][ky];
	int king_iLastMove = 0;
	
	if ((pk1 != null) && (pk1.iType == piece.KING) && (pk1.iColor == iColor) && (pk1.prev_xk == 5))
	{
		sxl = 1;
		sxh = 3;
		king_iLastMove = pk1.iLastMove;
	} else
	if ((pk2 != null) &&(pk2.iType == piece.KING) && (pk2.iColor == iColor) && ((pk2.prev_xk == 5)))
	{
		sxl = 6;
		sxh = 8;
		king_iLastMove = pk2.iLastMove;
	} else return 0;
	
	// king in place, check pawn wall
	
	kicpoints = 6;
	//int idiff = this.iMoveCounter - king_iLastMove;
	//int idiff = this.iMoveCounter - (2*king_iLastMove) + iColor;
	//int idiff = this.iMoveCount - (2*king_iLastMove) + iColor;    // 140624 is this better? nice for regular flip, fails with countermove checks 
	int idiff = this.iMoveCount - (2*king_iLastMove) - iColor ;  // trial 140615
	kicpoints = kicpoints - idiff;
	
	System.out.print("DBG:IKIC:"+iColor+";"+this.iMoveCount+";"+2*king_iLastMove+";"+kicpoints+";");
	
	if (kicpoints > 10) 
	{
		System.out.println("kicpoints failure! Kicpoints > 10!!!!");
		System.out.println("Movecounter: " + this.iMoveCounter);
		System.out.println("Movecount: " + this.iMoveCount);
		System.out.println("King last move:" + king_iLastMove);
		System.exit(0);
	}
	
	if (kicpoints <= 0) return 0;
	
	
	
	for (int sx = sxl; sx <= sxh; sx++)
	{
		piece p = blocks[sx][sy];
		if (p == null)
		kicpoints = kicpoints - 1;
		else
		{
			if (p.iColor != iColor) return 0;
			if (p.bThreat) kicpoints = kicpoints - 1;
		}
	}
	
	if (kicpoints <= 0) return 0;
	//return 0;
	//System.out.println(kicpoints);
	
	return kicpoints;
}


int iUndevelopedLightOfficers(int iColor)
{
	int sy;
	piece p;
	int iRet = 0;
	
	if (iColor == piece.WHITE) sy = 1;
	else sy = 8;
	
	p = blocks[2][sy];
	if ((p!= null) && (p.iLastMove == 0)) iRet++;
	
	p = blocks[3][sy];
	if ((p!= null) && (p.iLastMove == 0)) iRet++;
	
	p = blocks[6][sy];
	if ((p!= null) && (p.iLastMove == 0)) iRet++;
	
	p = blocks[7][sy];
	if ((p!= null) && (p.iLastMove == 0)) iRet++;
	
	return iRet;
}

boolean equals(chessboard cb)
{
	for (int i=1;i<=8;i++)
		for (int j=1;j<=8;j++)
		{
			piece p1 = blocks[i][j];
			piece p2 = cb.blocks[i][j];
			
			if ((p1 == null) && (p2 != null)) return false;
			if ((p2 == null) && (p1 != null)) return false;
			
			if ((p1 != null) && (p2 != null) && ((p1.iColor != p2.iColor) || (p1.iType != p2.iType))) return false;  
			
		}
	return true;	
}

boolean bCanDoLevel(int iLevel, int iColor)
{
	if (iLevel == 2)
	{
		int iComb = 30000;
		
		System.out.println("BCANDOLEVEL CALLED AT LEVEL 2 ");
		
		if ((iWhiteMoves > 10) && (iBlackMoves > 10))
		{
			iComb = iWhiteMoves * iBlackMoves;
			if (iColor == piece.WHITE) iComb = iComb * iWhiteMoves;
			else iComb = iComb * iBlackMoves;
		}
		else if (iWhiteMoves <= 10) iComb = iBlackMoves*iBlackMoves*iBlackMoves;
		else if (iBlackMoves <= 10) iComb = iWhiteMoves*iWhiteMoves*iWhiteMoves;
		
		System.out.println("BCANDOLEVEL CALLED AT LEVEL 2, iComb: " + iComb);
		
		if ((iMaxWhitePawn >= 7) || (iMaxBlackPawn >= 7)) return false;
		
		if (iComb > 30000) return false;
		
		System.out.println("BCANDOLEVEL RETURNING TRUE! " + iComb);
		
		return true;
		
	}
	
	if (iLevel == 3)
	{
		long iComb = 300000;
		
		if ((iWhiteMoves > 10) && (iBlackMoves > 10)) iComb = iWhiteMoves * iWhiteMoves * iBlackMoves * iBlackMoves;
		else if (iWhiteMoves <= 10) iComb = iBlackMoves*iBlackMoves*iBlackMoves*iBlackMoves;
		else if (iBlackMoves <= 10) iComb = iWhiteMoves*iWhiteMoves*iWhiteMoves*iWhiteMoves;
		
		if ((iMaxWhitePawn >= 7) || (iMaxBlackPawn >= 7)) return false;

		return iComb <= 300000;
	}
	
	if (iLevel == 4)
	{
		long iComb = 2000000; 
		
		if ((iWhiteMoves > 10) && (iBlackMoves > 10)) 
		{
			iComb = iWhiteMoves * iWhiteMoves * iBlackMoves * iBlackMoves;
			
			if (iColor == piece.WHITE) iComb = iComb * iWhiteMoves;
			else iComb = iComb * iBlackMoves;
		}
		else if (iWhiteMoves <= 10) iComb = iBlackMoves*iBlackMoves*iBlackMoves*iBlackMoves*iBlackMoves;
		else if (iBlackMoves <= 10) iComb = iWhiteMoves*iWhiteMoves*iWhiteMoves*iWhiteMoves*iBlackMoves;	
		
		if (iComb > 2000000) return false;

		return (iMaxWhitePawn < 7) && (iMaxBlackPawn < 7);
	}
	
	if (iLevel == 5)
	{
		if (mMaxThreads < 32) return false;
		
		long iComb = 15000000; 
		
		if ((iWhiteMoves > 10) && (iBlackMoves > 10)) 
		{
			iComb = (long)iWhiteMoves * (long)iWhiteMoves * (long)iWhiteMoves * (long)iBlackMoves * (long)iBlackMoves * (long)iBlackMoves;
		}
		else if (iWhiteMoves <= 10) iComb = (long)iBlackMoves * (long)iBlackMoves * (long)iBlackMoves * (long)iBlackMoves * (long)iBlackMoves * (long)iBlackMoves;
		else if (iBlackMoves <= 10) iComb = (long)iWhiteMoves * (long)iWhiteMoves * (long)iWhiteMoves * (long)iWhiteMoves * (long)iWhiteMoves * (long)iWhiteMoves ;
		
		if (iComb > 15000000) return false;

		return (iMaxWhitePawn < 7) && (iMaxBlackPawn < 7);

	}
	
	return false;
}

//boolean bWinnableByOne(int iColor, int iAlg)
instWinRec bWinnableByOne(int iColor, int iAlg)
{
	moveindex mi;
	
	movevalue mCurr, mBest, mThis;
	
	if ((iColor == piece.BLACK) && (this.bBlackKingThreat)) return new instWinRec(0,false);
	if ((iColor == piece.WHITE) && (this.bWhiteKingThreat)) return new instWinRec(0,false);
	
	if (iColor == piece.WHITE) mi = miBlackMoveindex;
	else mi = miWhiteMoveindex;
	
	//System.out.println("DBG150206: WBO ENTER " + iColor + " last move: " + lastmoveString());
	//dump();
	
	if (mi.iCheckMoveCount() == 0) return new instWinRec(0,false);
	
	chessboard WBOboard = this.copy();
	WBOboard.redoVectorsAndCoverages(1-iColor,iAlg); // $$$$$$$$$$$$$$$$$
	
	mCurr = new movevalue("");
	mBest = new movevalue("");
	mThis = new movevalue("");
	
	mThis.setBalancesFromBoard(WBOboard,1-iColor,iAlg);
	//System.out.println("DBG150610:WBO:STARTMVAL (START)" + mThis.dumpstr(iAlg,movevalue.DUMPMODE_LONG));
	
	int iRefPB = 0;
	if (iColor == piece.WHITE) iRefPB = mThis.iPieceBalCorrWhite;
	else iRefPB = mThis.iPieceBalCorrBlack;
	//System.out.println("DBG151001: iRefPB:" + iRefPB);
	int iBestImpact = 0;
	
	mBest.copyfrom(mThis);
	int iBestDiff = 0;
	if (iColor == piece.WHITE) iBestDiff = 10;
	else iBestDiff = -10;
	
	String sWBODbg = "WBO:" + (1-iColor) + " ("+ lastmoveString()+") <";
	
	for (int i=0;i< mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		
		if (m.isCheck() || m.isRevCheck())
		{
			//System.out.println("WBO validates as check move: " + m.moveStr());
			
			chessboard cb = WBOboard.copy();
			//System.out.println("DBG150610: WBO: " + m.moveStrLong());
			//cb.domove(m.p,m,-1);
			int xt = m.xtar;
			int yt = m.ytar;
			
			cb.domove(m.p.xk,m.p.yk,m.xtar,m.ytar,-1);
			
			//System.out.println("WBO Just Did move " + m.moveStr());
			//System.out.println("WBO ENTER REDO " + iColor);
			cb.redoVectorsAndCoverages(iColor, iAlg);
			//System.out.println("WBO REDO DONE " + iColor);
			
			//System.out.println("DBG150610: WBO: " + m.moveStrLong() + " before mate check.");
			if ((iColor == piece.WHITE) && (cb.iWhiteMoves == 0) && (cb.bWhiteKingThreat))
			{
				//System.out.println("WHITE IS WINNABLE (black wins!) BY ONE MOVE HERE!");
				//return true;
				sWBODbg = sWBODbg + m.moveStr() + " Wmate>";
				//System.out.println(sWBODbg);
				return new instWinRec(0,true);
			}
			else if ((iColor == piece.BLACK) && (cb.iBlackMoves == 0) && (cb.bBlackKingThreat))
			{
				//System.out.println("BLACK IS WINNABLE (white wins) BY ONE MOVE HERE!");
				//return true;
				sWBODbg = sWBODbg + m.moveStr() + " Bmate>";
				//System.out.println(sWBODbg);
				return new instWinRec(0,true);
			}
			
			//System.out.println("DBG150610: WBO: " + m.moveStrLong() + " mate check done.");
			movevalue mDBG = new movevalue(m.moveStr());
			mDBG.setBalancesFromBoard(cb,iColor,iAlg);
			//System.out.println("MDBG:" + mDBG.dumpstr(40,DUMPMODE_SHORT));
			
			int iImpact = 0;
			if (iColor == piece.WHITE)
			{
				iImpact = mDBG.iPieceBalCorrBlack - iRefPB;
				if (iImpact < iBestImpact) iBestImpact = iImpact;
				
			}
			else
			{
				iImpact = mDBG.iPieceBalCorrWhite - iRefPB;
				if (iImpact > iBestImpact) iBestImpact = iImpact;
			}
			//System.out.println("DBG151012: iBestImpact:" + iBestImpact + " iImpact:" + iImpact);
			
			/*
			piece px = cb.blocks[xt][yt];
			if (px.bThreat)
			{
				int pval;
				if (px.iColor == piece.WHITE) pval = px.pvalue();
				else pval = -px.pvalue();
				
				mCurr.setBalancesFromBoard(cb,iColor,iAlg);
				int idiff = mThis.iPieceBalance-mCurr.iPieceBalance+pval;
				//System.out.println("DBG150207: WBO: checker under threat: " + px.bThreat + " idiff:" + idiff+ " move.netcapture: " + m.iNetCapture);
				sWBODbg = sWBODbg + m.moveStr() + "/" + idiff +";";
				if (((iColor == piece.WHITE) && (idiff < iBestDiff)) || 
					((iColor == piece.BLACK) && (idiff > iBestDiff))) iBestDiff = idiff;
				
			}
			else if (!m.isRisky())
			{
				//mCurr.setBalancesFromBoard(cb,1-iColor,iAlg);
				mCurr.setBalancesFromBoard(cb,iColor,iAlg);
				//System.out.println("WBO:MVAL ("+ m.moveStr()+ ") clr:" +iColor + " " + mCurr.dumpstr(iAlg,movevalue.DUMPMODE_LONG));
				//else System.out.println("not winnable move");
				//if (mCurr.isBetterthan(mBest,iColor,iAlg,1-iColor)) mBest.copyfrom(mCurr);
				int idiff = mCurr.movevaluediffInCheck(mBest,iColor);
				
				//System.out.println("DBG150207:WBO:idiff:" + idiff);
				sWBODbg = sWBODbg + m.moveStr() + "/" + idiff +";";
				if (((iColor == piece.WHITE) && (idiff < 0)) ||
				   ((iColor == piece.BLACK) && (idiff > 0))) mBest.copyfrom(mCurr);
			}
			*/
			//System.out.println("DBG150610: WBO: " + m.moveStrLong() + " done.");
			
		}
		
		
	}
	//System.out.println("DBG 151004:WBO Loop done.");
	//System.out.println("WBO:MVAL:BEST: (BEST)" + mBest.dumpstr(iAlg,movevalue.DUMPMODE_LONG));
	//int iCorrFactor = mBest.movevaluediff(mThis,iColor);
	/*
	int iCorrFactor = mBest.movevaluediffInCheck(mThis,iColor);
	int iWBORet;
	
	if (iColor == piece.WHITE) iWBORet = Math.min(iCorrFactor,iBestDiff);
	else iWBORet = Math.max(iCorrFactor,iBestDiff);
	
	sWBODbg = sWBODbg + " best: " + iWBORet + ">";
	//System.out.println(sWBODbg);
	
	System.out.println("DBG150207: WBO: correction factor: " + iCorrFactor + " iBestDiff :" + iBestDiff + " iWBORet:" +iWBORet);
	//System.exit(0);
	//System.out.println("WBO RETURNING FALSE");
	
	
	return new instWinRec(iWBORet,false);
	*/
	//System.out.println("DBG151012: WBO Ret:" + iBestImpact);
	return new instWinRec (iBestImpact, false);
}

instWinRec bWinnableByReCheck(int iColor, int iAlg)
{
	moveindex mi;
	
	//System.out.println("DBG150207: WBRC ENTER " + iColor + " last move: " + lastmoveString());
	
	if ((iColor == piece.WHITE) && !this.bBlackKingThreat) return new instWinRec (0,false);
	if ((iColor == piece.BLACK) && !this.bWhiteKingThreat) return new instWinRec (0,false);
	
	if (iColor == piece.WHITE) mi = miBlackMoveindex;
	else mi = miWhiteMoveindex;
	
	boolean bEscape = false;
	
	int iNetBest;
	if (iColor == piece.WHITE) iNetBest = 10;
	else iNetBest = -10;
	
	String sWBRCDbg = "WBRC enter: "  + (iColor) + " ("+ lastmoveString()+") ";
	//System.out.println(sWBRCDbg);
	sWBRCDbg = "WBRC: " + (iColor) + " ("+ lastmoveString()+") ";
	
	for (int i=0;i< mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		chessboard cb = this.copy();
		//System.out.println("DBG150207: WBRC MOVE:" + m.moveStr());
		cb.domove(m.p.xk,m.p.yk,m.xtar,m.ytar,-1);
		cb.redoVectorsAndCoverages(iColor, iAlg);
		
		int iCap;
		if (iColor == piece.WHITE) iCap = -m.iCaptValue;
		else iCap = m.iCaptValue;
		
		//boolean bWinnable = cb.bWinnableByOne(1-iColor,iAlg);
		instWinRec iWR = cb.bWinnableByOne(1-iColor,iAlg);
		if (!iWR.bIW) bEscape = true;
		
		int iCurr = iCap + iWR.iIWCorr;
		sWBRCDbg = sWBRCDbg + "<" + m.moveStr()+"/"+iCurr + ">";
		
		//System.out.println("DBG150207: WBRC iCurr:" + iCurr);
		
		if ((iColor == piece.WHITE) && (iCurr < iNetBest)) iNetBest = iCurr;
		if ((iColor == piece.BLACK) && (iCurr > iNetBest)) iNetBest = iCurr;
	}
	//System.out.println("DBG150124: WBRC RETURN " + !bEscape);
	//int iCorrFactor = 0;
	//System.out.println("DBG150207: WBRC RETURN:" + iNetBest);
	
	sWBRCDbg = sWBRCDbg + " B: " + iNetBest + " " + !bEscape;
	
	//System.out.println(sWBRCDbg);
	
	return new instWinRec (iNetBest,!bEscape);
	
}


int iMoveIndexLength(int iColor)
{
	if (iColor == piece.WHITE) return miWhiteMoveindex.getSize();
	else return miBlackMoveindex.getSize();
}

chessboard doBestZeroMove(int iColor, int iAlg, boolean bAny)
{
	moveindex mi, mo;
	
	redoVectorsAndCoverages(iColor, iAlg);
	move m;
	
	if (iColor == piece.WHITE)
	{
		mi=miWhiteMoveindex;
		mo=miBlackMoveindex;
	}
	else 
	{
		mi = miBlackMoveindex;
		mo = miWhiteMoveindex;
	}
	
	if (bAny)
	{
		m = mi.sortedcopy().getEquallyGoodMoves(null,mo.sortedcopy()).getAnyMove();	
		if (m==null)
		{
			System.out.println("Null anymove @ chessboard.doBestZeroMove() , iColor=" + iColor);
			mi.dump();
			System.exit(0);
		}
			
	}
	else
	{
		m = mi.getBestMove(null,mo);
		System.out.println("DBG150222: doBestZeroMove. Best = " + m.moveStrLong());
		move m2 = mo.getBestMove(m,null);
		if (m2 == null)
		{
			System.out.println("DBG150222: doBestZeroMove: NULL m2 (CHECK???)");
			System.out.println("DBG150222: doBestZeroMove (C): Exp. capt diff: " + m.iNetCapture);
		}
		else
		{
			System.out.println("DBG150222: doBestZeroMove. Exp. response = " + m2.moveStrLong());
			int iexpd = 0;
			if (m.iCaptValue==m.iNetCapture) iexpd = m.iCaptValue - m2.iNetCapture;
			else if (m2.iCaptValue == (m.iCaptValue-m.iNetCapture)) iexpd = m2.iCaptValue-m2.iNetCapture;
			else iexpd = m.iNetCapture-m2.iNetCapture;
			System.out.println("DBG150222: doBestZeroMove (R): Exp. capt diff: " + iexpd);
		}
	}
	chessboard nb = new chessboard();
	nb = this.copy();
	
	nb.domove(m.p,m,-1);
	
	return nb;
}

void setZeroBalances(int iTurn, int iAlg)
{
	move m = null;
	move m2 = null;
	
	//System.out.println("DBG150223: enter setZeroBalances. iTurn :" + iTurn);
	
	/*miWhiteMoveindex.dump(null);
	miBlackMoveindex.dump(null);
	*/
	miWhiteMoveindex = miWhiteMoveindex.sortedcopy();
	miBlackMoveindex = miBlackMoveindex.sortedcopy();
	
	if (iTurn == piece.WHITE)
	{
		m = miWhiteMoveindex.getBestMove(null,miBlackMoveindex);

		m2 = miBlackMoveindex.getBestMove(m,null);
		
		if (SHOWZEROBALANCES)
		{
			if (m!=null) System.out.println("DBG150712: zerobalances W white:" + m.moveStrLong());
			if (m2!= null) System.out.println("DBG150712: zerobalances W black: " + m2.moveStrLong());
		}
		
		if ((m2 == null) || ((m.iCaptValue-m.iNetCapture) >= m2.iNetCapture))
		{
			iWhiteZeroBal = m.iNetCapture;
		}
		else
		{
			if (m.iCaptValue==m.iNetCapture) iWhiteZeroBal = m.iCaptValue - m2.iNetCapture;
			else if (m2.iCaptValue == (m.iCaptValue-m.iNetCapture)) iWhiteZeroBal = m.iCaptValue-m2.iNetCapture;
			else iWhiteZeroBal = m.iNetCapture-m2.iNetCapture;
		}
	}
	//System.out.println("iWhiteZeroBal:" + iWhiteZeroBal);
	
	if (iAlg == movevalue.ALG_SUPER_PRUNING_ZB)
	{
		if ((iTurn == piece.WHITE) && (iWhiteZeroBal > 0) && (Math.abs(iWhiteZeroBal) < 100))
		{
			System.out.print("DBG150609 ZEROBALANCES (TURN WHITE) W:" + iWhiteZeroBal + " B:" + iBlackZeroBal + " best white move: " + m.moveStrLong() + " ||  best black move: " + " turn:" + iTurn);
			if (m2 != null) System.out.println ("  " + m2.moveStrLong());
			else System.out.println(" m2 == null");
			
			chessboard cb = this.copy();
			int xt = m.xtar;
			int yt = m.ytar;
			cb.domove(m.p.xk,m.p.yk,m.xtar,m.ytar,-1);
			cb.redoVectorsAndCoverages(1-iTurn,iAlg);
			System.out.println("DBG150609 ZEROBALANCES (WHITE) NEW BOARD DONE. PIECEBAL: " + (cb.iValSumWhite - cb.iValSumBlack) + "  ZEROBALS: W:" + cb.iWhiteZeroBal + " B: " + cb.iBlackZeroBal);
			
			if ((iValSumWhite - iValSumBlack + iWhiteZeroBal) != (cb.iValSumWhite - cb.iValSumBlack - cb.iBlackZeroBal) ) 
			{
				System.out.println("DBG150609:  HERE's a potential case with " + m.moveStrLong());
				System.out.println(".. orig board: piecebal: " + (iValSumWhite - iValSumBlack) + "  whitezerobal: " + iWhiteZeroBal);
				System.out.println("... fix cand: WHITEZEROBAL TO: " + -(cb.iValSumWhite - cb.iValSumBlack + cb.iBlackZeroBal - iValSumWhite + iValSumBlack));
				//this.dump();
				//cb.dump();
				iWhiteZeroBal = -(cb.iValSumWhite - cb.iValSumBlack + cb.iBlackZeroBal - iValSumWhite + iValSumBlack);
				//System.exit(0);
			}
		}
	}
	
	if (iTurn == piece.BLACK)
	{
		m = miBlackMoveindex.getBestMove(null,miWhiteMoveindex);
		m2 = miWhiteMoveindex.getBestMove(m,null);
		
		if (SHOWZEROBALANCES)
		{
			if (m!=null) System.out.println("DBG150712: zerobalances B black:" + m.moveStrLong());
			if (m2!= null) System.out.println("DBG150712: zerobalances B white: " + m2.moveStrLong());
			else System.out.println(".. (zerobalances:white move = null)");
		}
		
		//if ((iTurn == piece.WHITE) && (iWhiteZeroBal > 0)) System.out.println("DBG150609 ZEROBALANCES (TURN WHITE): W:" + iWhiteZeroBal + " B:" + iBlackZeroBal+ " best white move: " + m.moveStrLong());	
		
		if ((m2 == null) || ((m.iCaptValue-m.iNetCapture) >= m2.iNetCapture))
		{
			iBlackZeroBal = m.iNetCapture;
		}
		else
		{
			if (m.iCaptValue==m.iNetCapture) iBlackZeroBal = m.iCaptValue - m2.iNetCapture;
			else if (m2.iCaptValue == (m.iCaptValue-m.iNetCapture))
				iBlackZeroBal = m.iCaptValue-m2.iNetCapture;
			else iBlackZeroBal = m.iNetCapture-m2.iNetCapture;
		}
	}
	//System.out.println("iBlackZeroBal:" + iBlackZeroBal);
	
	/*
	if ((iTurn == piece.BLACK) && (iBlackZeroBal > 0) && (iBlackZeroBal < 100) && (iWhiteZeroBal < 100)) System.out.println("DBG150313: setZeroBalances: Genuine Black Capture " + m.moveStr() + ": " + iBlackZeroBal + " CHECKZERO"); 
	if ((iTurn == piece.WHITE) && (iWhiteZeroBal > 0) && (iWhiteZeroBal < 100) && (iBlackZeroBal < 100)) System.out.println("DBG150313: setZeroBalances: Genuine White Capture "+m.moveStr()+": "+ iWhiteZeroBal + "  CHECKZERO");
	
	*/
	//System.out.println("DBG150313: leave setZeroBalances. Turn: " + iTurn+ " " + this.lastmoveString_bylib() + " w:" + iWhiteZeroBal + " b:"+ iBlackZeroBal);
	
	
	if (iAlg == movevalue.ALG_SUPER_PRUNING_ZB)
	{
		if ((iTurn == piece.BLACK) && (iBlackZeroBal > 0) && (Math.abs(iBlackZeroBal) < 100))
		{
			System.out.print("DBG150609 ZEROBALANCES (TURN BLACK) W:" + iWhiteZeroBal + " B:" + iBlackZeroBal + " best black move: " + m.moveStrLong() + " ||  best white move: " + " turn:" + iTurn);
			if (m2 != null) System.out.println ("  " + m2.moveStrLong());
			else System.out.println(" m2 == null");
			
			chessboard cb = this.copy();
			int xt = m.xtar;
			int yt = m.ytar;
			cb.domove(m.p.xk,m.p.yk,m.xtar,m.ytar,-1);
			cb.redoVectorsAndCoverages(1-iTurn,iAlg);
			System.out.println("DBG150609 ZEROBALANCES (BLACK) NEW BOARD DONE. PIECEBAL: " + (cb.iValSumWhite - cb.iValSumBlack) + "  ZEROBALS: W:" + cb.iWhiteZeroBal + " B: " + cb.iBlackZeroBal);
			
			if ((iValSumWhite - iValSumBlack - iBlackZeroBal) != (cb.iValSumWhite - cb.iValSumBlack + cb.iWhiteZeroBal) ) 
			{
				System.out.println("DBG150609:  HERE's a potential case with " + m.moveStrLong());
				System.out.println(".. orig board: piecebal: " + (iValSumWhite - iValSumBlack) + "  blackzerobal: " + iBlackZeroBal);
				System.out.println("... fix cand: BLACKZEROBAL TO: " + -(cb.iValSumWhite - cb.iValSumBlack + cb.iWhiteZeroBal - iValSumWhite + iValSumBlack));
				//this.dump();
				//cb.dump();
				iBlackZeroBal = -(cb.iValSumWhite - cb.iValSumBlack + cb.iWhiteZeroBal - iValSumWhite + iValSumBlack);
				//System.exit(0);
			}
		}
	}
	
	
	
	
}

boolean bGameIsClosed()
{
	int diffsum = 0;
	
	for (int i=4;i<=5;i++)
	{
		int wmax = -1;
		int bmin = 10;
		
		for (int j=1;j<=8;j++)
		{
			piece p=blocks[i][j];
			if ((p!=null) && (p.iType == piece.PAWN))
			{
				if ((p.iColor == piece.WHITE) && (j>wmax)) wmax = j;
				if ((p.iColor == piece.BLACK) && (j<bmin)) bmin = j;
			}
		}
		
		if (bmin > wmax) diffsum = diffsum + (bmin-wmax);
	}
	
	//System.out.println("DBG150426: chessboard.bGameisClosed:diffsum:" + diffsum);

	return diffsum <= 4;
}

boolean bGameIsOpen()
{
	if (!bPawnsAtColumn(4,piece.WHITE)) return true;
	if (!bPawnsAtColumn(5,piece.WHITE)) return true;
	if (!bPawnsAtColumn(4,piece.BLACK)) return true;
	return !bPawnsAtColumn(5, piece.BLACK);
}

boolean bPawnsAtColumn(int iColumn, int iColor)
{
	for (int j=1;j<=8;j++)
	{
		piece p=blocks[iColumn][j];
		if ((p!=null) && (p.iType == piece.PAWN))
		{
			if (p.iColor == iColor) return true;
		}
	}
	return false;
}

boolean bPawnProtectedAt(int x2,int y2,int iColor)
{
	int yp;
	if (iColor == piece.WHITE) yp = y2-1;
	else yp=y2+1;
	
	if (x2>1)
	{
		piece p = blocks[x2-1][yp];
		if ((p!=null) && (p.iType == piece.PAWN) && (p.iColor == iColor)) return true;
	}
	
	if (x2<8)
	{
		piece p = blocks[x2+1][yp];
		return (p != null) && (p.iType == piece.PAWN) && (p.iColor == iColor);
	}
	return false;
}

boolean bPawnIsFreeAt(int x1, int y1)
{
	piece pp = blocks[x1][y1];
	if (pp.iType != piece.PAWN)
	{
		System.out.println("Fatal error at chessboard.bPawnIsFreeAt(). iType:" + pp.iType);
		System.exit(0);
	}
	
	if (pp.iColor==piece.WHITE) for (int j= y1+1;j<8;j++)
	{
		piece p = blocks[x1][j];
		if ((p != null) && (p.iType == piece.PAWN) && (p.iColor == piece.BLACK)) return false;
	}
	if (pp.iColor==piece.BLACK) for (int j= y1-1;j>1;j--)
	{
		piece p = blocks[x1][j];
		if ((p != null) && (p.iType == piece.PAWN) && (p.iColor == piece.WHITE)) return false;
	}
	
	if (x1>1)
	{
		if (pp.iColor==piece.WHITE) for (int j= y1+1;j<8;j++)
		{
			piece p = blocks[x1-1][j];
			if ((p != null) && (p.iType == piece.PAWN) && (p.iColor == piece.BLACK)) return false;
		}
		if (pp.iColor==piece.BLACK) for (int j= y1-1;j>1;j--)
		{
			piece p = blocks[x1-1][j];
			if ((p != null) && (p.iType == piece.PAWN) && (p.iColor == piece.WHITE)) return false;
		}
	}	
	if (x1 <8)
	{
		if (pp.iColor==piece.WHITE) for (int j= y1+1;j<8;j++)
		{
			piece p = blocks[x1+1][j];
			if ((p != null) && (p.iType == piece.PAWN) && (p.iColor == piece.BLACK)) return false;
		}
		if (pp.iColor==piece.BLACK) 
		{
			for (int j= y1-1;j>1;j--)
			{
				piece p = blocks[x1+1][j];
				if ((p != null) && (p.iType == piece.PAWN) && (p.iColor == piece.WHITE)) return false;
			}
		}
	}
	
	return true;
}

void resetSkewers()
{
	if (miWhiteMoveindex != null)  miWhiteMoveindex.resetSkewers();
	if (miBlackMoveindex != null)  miBlackMoveindex.resetSkewers();
}

int iExpectedWinner()
{
	if (vTestDir == null) return -10;
	
	for (int i=0;i<vTestDir.size();i++)
	{
		String s = (String)vTestDir.elementAt(i);
		if (s.indexOf("W:") != -1)
		{
			if (s.indexOf("WHITE") != -1) return piece.WHITE;
			if (s.indexOf("BLACK") != -1) return piece.BLACK;
			if (s.indexOf("DRAW") != -1) return -1;
		}
	}
	
	return -10;
}

void dump_pin_info()
{
	System.out.println("Pininfo:");
	for (int i = 1; i <= 8; i++)
				for (int j=1; j <= 4; j++)
	{
		piece p = blocks[i][j];
		if (p!=null)
		{
			if ((p.iPinValue > 0) || (p.iPinningToDirection != piece.NO_DIR))
			{
				System.out.print(p.dumpchr()+p.sCoords()+":"+p.iPinValue+","+p.iPinDirection+","+p.iPinningToDirection+"  ");
			}
		}
	}
	System.out.println();
}

void assessKingSafety(king kw, king kb)
{
	System.out.println("DBG160331: AKS for " + lastmoveString() + " white king@ " +kw.xk +"," + kw.yk + " black king@ " + kb.xk + "," + kb.yk);
	kw.checkEscapes(this);
	kb.checkEscapes(this);
}

boolean bIsDrawByPieces()
{
	if ((iBlackPieceCount[piece.QUEEN] != 0) ||
		(iBlackPieceCount[piece.ROOK] != 0) ||
		(iBlackPieceCount[piece.PAWN] != 0) ||
		(iWhitePieceCount[piece.QUEEN] != 0) ||
		(iWhitePieceCount[piece.ROOK] != 0) ||
		(iWhitePieceCount[piece.PAWN] != 0)) return false;
				
	int lOffSum = iWhitePieceCount[piece.BISHOP] + iWhitePieceCount[piece.KNIGHT] + 
				  iBlackPieceCount[piece.BISHOP] + iBlackPieceCount[piece.KNIGHT];

	return lOffSum < 2;
}

void setQRKillBal(int iTurn)
{
	boolean bBP = false;
	boolean bWP = false;
	boolean bBQR = false;
	boolean bWQR = false;
	
	//System.out.println("DBG160509: ENTER iQRKillBal. iTurn: " + iTurn+ " iWZB:" + iWhiteZeroBal + " iBZB: "+ iBlackZeroBal);
	
	iQRKillBal = 0;
	
	if ((iBlackPieceCount[piece.QUEEN] != 0) ||
		(iBlackPieceCount[piece.ROOK] != 0) ||
		(iBlackPieceCount[piece.KNIGHT] != 0) ||
		(iBlackPieceCount[piece.BISHOP] != 0) ||
		(iBlackPieceCount[piece.PAWN] != 0)) bBP = true;
	
	if ((iWhitePieceCount[piece.QUEEN] != 0) ||
		(iWhitePieceCount[piece.ROOK] != 0) ||
		(iWhitePieceCount[piece.KNIGHT] != 0) ||
		(iWhitePieceCount[piece.BISHOP] != 0) ||
		(iWhitePieceCount[piece.PAWN] != 0)) bWP = true;	
	
	if ((iBlackPieceCount[piece.QUEEN] != 0) ||
		(iBlackPieceCount[piece.ROOK] != 0)) bBQR = true;
		
	if ((iWhitePieceCount[piece.QUEEN] != 0) ||
		(iWhitePieceCount[piece.ROOK] != 0)) bWQR = true;	
		
	if ((bBQR && !bWP) || (bWQR && !bBP))	
	{
		iQRKillBal = iValSumWhite-iValSumBlack;
		if ((iTurn == piece.WHITE) && (iWhiteZeroBal != 0)) iQRKillBal = 0;
		if ((iTurn == piece.BLACK) && (iBlackZeroBal != 0)) iQRKillBal = 0;
		//System.out.println("DBG160509: Setting iQRKillBal: " + iQRKillBal);
		//System.out.println("DBG160509: Setting iQRKillBal, bBP:" + bBP + ", bWP:" + bWP + ", bBQR:"+bBQR+", bWQR" + bWQR);
	}
	
}

boolean bMidPawnOpenings( int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsMidPawnOpener()) bFound = true;
	}
	
	return bFound;
}

boolean bPawnPressureOpenings(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsPawnPressureOpener(this)) bFound = true;
	}
	
	return bFound;
}

boolean bFianchettoPrepOpenings(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsPrepForFianchetto(this)) bFound = true;
	}
	
	return bFound;
}

boolean bBishopE3Openers(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsBishopE3Opener()) bFound = true;
	}
	
	return bFound;
}

boolean bF2StepOpeners(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsF2StepOpener(this)) bFound = true;
	}
	
	return bFound;
}

boolean bPawnFrontOpeners(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsPawnFrontOpener(this)) 
		{
			bFound = true;
			//System.out.println("pawn front found: "+m.moveStr());
		}
	}
	
	return bFound;
}

boolean bBackRowRookOpeners(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsBackRowRook(this)) 
		{
			bFound = true;
			//System.out.println("pawn front found: "+m.moveStr());
		}
	}
	
	return bFound;
}

boolean bKnightToMiddleMoves(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsKnightToMiddle()) 
		{
			bFound = true;
			//System.out.println("pawn front found: "+m.moveStr());
		}
	}
	
	return bFound;
}

boolean bQueenFirstMoves(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.isQueenFirstMove(this)) 
		{
			bFound = true;
			//System.out.println("pawn front found: "+m.moveStr());
		}
	}
	
	return bFound;
}

boolean bC2StepOpeners(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsC2StepOpener(this)) bFound = true;
	}
	
	return bFound;
}

boolean bBishopF4Openers(int iColor)
{
	moveindex mi;
	//int yBeg;
	boolean bFound = false;
	if (iColor == piece.WHITE)
	{
		mi = miWhiteMoveindex;
		//yBeg= 2;
	}
	else
	{
		mi = miBlackMoveindex;
		//yBeg = 7;
	}
	
	for (int i=0;i<mi.getSize();i++)
	{
		move m = mi.getMoveAt(i);
		if (m.bIsBishopF4Opener(this)) bFound = true;
	}
	
	return bFound;
}

boolean bWeirdOpeningsExist(int iColor)
{
	return (bMidPawnOpenings(iColor) || bPawnPressureOpenings(iColor) || (bFianchettoPrepOpenings(iColor) ) || bBishopE3Openers(iColor) || bF2StepOpeners(iColor) || bPawnFrontOpeners(iColor) || bBackRowRookOpeners(iColor) || bKnightToMiddleMoves(iColor) || bQueenFirstMoves(iColor) ||
	bC2StepOpeners(iColor) || bBishopF4Openers(iColor));
}

int iMovedPiecesFromStart()
{
	int iRet = 0;
	for (int i=1;i<=8;i++)
	{
		if (blocks[i][1] == null) iRet++;
		if (blocks[i][8] == null) iRet++;
		if (blocks[i][2] == null) iRet++;
		else 
		{
			piece p=blocks[i][2];
			if ((p.iType != piece.PAWN) || (p.iColor != piece.WHITE)) iRet++;
		}
		if (blocks[i][7] == null) iRet++;
		else 
		{
			piece p=blocks[i][7];
			if ((p.iType != piece.PAWN) || (p.iColor != piece.BLACK)) iRet++;
		}
	}
	return iRet;
}

private void writeObject(java.io.ObjectOutputStream out)
     throws IOException
{
	// napit-laudalla-matriisi
	// ilastmoved-arvot
	// lastmove-vektorit ja stringit
}

private void readObject(java.io.ObjectInputStream in)
     throws IOException, ClassNotFoundException
{
		// napit-laudalla-matriisi
	// ilastmoved-arvot
	// lastmove-vektorit ja stringit
}

private void readObjectNoData()
     throws ObjectStreamException
{
	// why is this needed?
}
	
static String mergeMovOrder(String ord1, String ord2)
{
	if (ord2 == null) return ord1;
	
	String o2Comp[] = ord2.split("\\s+");
	String sRet = new String(ord1).trim();
	for (int i=0;i<o2Comp.length;i++)
	{
		if (sRet.indexOf(o2Comp[i]) == -1)
		{
			sRet = sRet + " " +o2Comp[i];
		}
	}
	return sRet;
}
	
	
}

class mval_vector
{
	Vector v;
	int iPrgrCount;
	int iActCount;
	mval_combo bestMvc;
	chessboard root_cb;
	String sRet = null;
	regbest m_regb;
	mostore m_mos;
	boolean bDone;
	
	mval_vector(chessboard cb, String sRetOrder, regbest regb, mostore mos)
	{
		v = new Vector();
		iPrgrCount = 0;
		iActCount = 0;
		bestMvc = null;
		root_cb = cb.copy();
		sRet = sRetOrder;
		m_regb = regb;
		
		bDone = false;
		m_mos = mos;
		//if (sRet == null) System.out.println("DBG 150312: mval_vector created with null sRet");
		//else System.out.println("DBG 150312: mval_vector created with NOTNULL sRet");
		
	}
	
	synchronized void addWait()
	{	
		iPrgrCount++;
	}
	
	synchronized rb_control addActive()
	{
		iActCount++;
		if (m_regb != null)
		{
			if (bDone) iActCount=0;
			return new rb_control (m_regb.copy(), bDone);
		}
		else return null;
	}
	
	synchronized void add (mval_combo mvc, int iColor, int iAlg, int iTurn)
	{
		//System.out.println("DBG150116 mval_vector.add() " + iPrgrCount + " " + mvc.mv.dumpstr(iAlg));
		//System.out.println("DBG150116 mval_vector.add() iColor:" + iColor + " iAlg:" + iAlg + " iTurn:" + iTurn);
		v.addElement(mvc);
		
		if (bestMvc == null)
		{
			bestMvc = mvc;
			//System.out.println("DBG150116 mval_vector.add() first best is: " + bestMvc.mv.dumpstr(iAlg));
		}
		else if ((mvc.mv.isBetterthan(bestMvc.mv,iColor,iAlg,iTurn,chessboard.PARALLEL_LEVEL)) && !bDone)
		{
			bestMvc = mvc;
			//System.out.println("DBG160413 mval_vector.add() new best is: " + bestMvc.mv.dumpstr(iAlg));
			if (m_regb != null)
			{
				m_regb.setBest(bestMvc.mv.copy(),chessboard.PARALLEL_LEVEL,iTurn,iAlg,iColor);
				if (m_regb.bBranchIsDone(chessboard.PARALLEL_LEVEL,iTurn,iAlg,iColor))
				{
					bestMvc.mv.setCutBranchLevel(chessboard.PARALLEL_LEVEL);
					//System.out.println("DBG160413: BRANCH DONE BY mval_vector.add()! " + bestMvc.mv.dumpstr(iAlg));
					bDone = true;
				}
			}
		}
		
		iPrgrCount--;
		iActCount--;
	}
	
	void dumpall (int iAlg)
	{
		System.out.println("MVEC DUMP ALL:");
		
		for (int i= 0; i < v.size(); i++)
		{
			mval_combo mvc = (mval_combo)v.elementAt(i);
			System.out.println("mvec.dumpall.MVAL:"+mvc.mv.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
		}
	}
	
	mval_combo get_best(int iRounds,int iColor, int iAlg, int iTurn, gamehistory ghist, boolean bDebug, boolean bStrategyImpact, regbest regb)
	{
		mval_combo theBest = null;
		boolean bDraw = false;
		long lStart = System.currentTimeMillis();
		//System.out.println("DBG 150312: mval_vector.get_best() called. iColor:" + iColor + " iTurn:" + iTurn+ "iR: " + iRounds); 
		
		while ((iPrgrCount > 0) && (!bDone))
		{
			//System.out.println("mval gb loop! " + iPrgrCount);
			try { Thread.sleep(200); } catch (Exception e) {}
		}
		
		
		//System.out.println("DBG 150312: mval_vector.get_best() threads done. regb best: " + regb.getBestMv(iRounds).dumpstr(iAlg));
		
		/*
		System.out.println("DBG 150430: mval_vector.get_best() threads finished. iColor:" + iColor + " iTurn:" + iTurn);
		dumpall(iAlg);
		*/
		//System.exit(0);
		
		//if (ghist != null) System.out.println("MVEC:get_best gamehist sizes " + ghist.vMoves.size() + "," + ghist.vTieCands.size() + " iColor" + iColor); 
		//else System.out.println("MVEC: no gamehistory");
		
		strategy strat = null;
		
		if (sRet != null) strat = new strategy(root_cb,iColor,iTurn,iAlg,iRounds,null);
		
		if (bStrategyImpact) strat = new strategy(root_cb,iColor,iTurn,iAlg,iRounds,ghist);
		
		//System.out.println("DBG160413: mval_combo.get_best vector size: " + v.size());
		
		int iCBLevel = movevalue.CUTBRANCH_INIT;
		
		for (int i= 0; i < v.size(); i++)
		{
			mval_combo mvc = (mval_combo)v.elementAt(i);
			
			if (strat != null) strat.addCand(mvc.mv);
			
			// $$$$ tie detection fails because thebest.cb contains situation after x moves
			// comparison needs situation after 1 move!  140328  $$$$$ mval_combo.cb_base should contain this info ... try out asap :)
			
			if (mvc.mv.getCutBranchLevel() == iRounds)  iCBLevel = iRounds;
			
			if (bDebug) System.out.println(iRounds+":"+iColor+":"+mvc.mv.dumpstr(iAlg));
			
			if (theBest == null) 
			{
				theBest = mvc;
				if ((ghist != null) && (ghist.bIsTieCandidate(theBest.cb_base,iColor))) bDraw = true;
				//System.out.println("MVEC BEST A0: "  + theBest.mv.dumpstr(iAlg));
				/*
				System.out.println("THEBEST... BOARD AT END DUMP:");
				if (theBest.cb != null) theBest.cb.dump();
				else System.out.println("Null board. End is probably very near...");
				System.out.println("THEBEST... REFBOARD DUMP:");
				theBest.cb_base.dump();
				System.out.println("MVEC BEST A0: bDraw : " + bDraw);
				*/
			}
			else
			{
				if ((mvc.mv.isBetterthan(theBest.mv,iColor,iAlg,iTurn,chessboard.PARALLEL_LEVEL)) ||
					(bDraw && (mvc.mv.equalBalance(iAlg, iTurn, theBest.mv))))
				{
					// handle drawrisk correctly now it fails $$$$$ 140327
					/*
					if bdraw & equals & new not draw -> thebest = mvc
					if isbetter && new not draw -> thebest = mvc
					if isbetter && ! equals -> thebest = mvc
					
					*/
					
					if (ghist == null)
					{
						theBest = mvc;
						//System.out.println("MVEC BEST A00:" + theBest.mv.dumpstr(iAlg));
					}
					else if (bDraw && (mvc.mv.equalBalance(iAlg, iTurn, theBest.mv)) && (!ghist.bIsTieCandidate(mvc.cb_base,iColor)))
					{
						theBest = mvc;
						bDraw = false;
						//System.out.println("MVEC BEST A:" + theBest.mv.dumpstr(iAlg));
					}
					else if ((mvc.mv.isBetterthan(theBest.mv,iColor,iAlg,iTurn,chessboard.PARALLEL_LEVEL)) &&
					(!ghist.bIsTieCandidate(mvc.cb_base,iColor)))
					{
						theBest = mvc;
						bDraw = false;
						//System.out.println("MVEC BEST B:" + theBest.mv.dumpstr(iAlg));
					}
					else if  ((mvc.mv.isBetterthan(theBest.mv,iColor,iAlg,iTurn,chessboard.PARALLEL_LEVEL)) &&
					(!mvc.mv.equalBalance(iAlg, iTurn, theBest.mv)))
					{
						theBest = mvc;
						//System.out.println("MVEC BEST C:" + theBest.mv.dumpstr(iAlg));
						if (ghist.bIsTieCandidate(mvc.cb_base,iColor))
						{
							//System.out.println("MVEC Draw On@C");
							bDraw = true;
						}
					}
					

				}
			}
		}
		long lEnd = System.currentTimeMillis();
		long lDur = lEnd - lStart;
		//System.out.println("DBG 150312: mval_vector.get_best() returns (theBest==null):" + (theBest == null) + " bDraw:" + bDraw + " duration: " + lDur);
		//dumpall(iAlg);
		if (sRet != null)
		{
			strat.dump();
			sRet = strat.getMoveOrder();
			m_mos.addItem(root_cb.FEN(),iRounds,sRet);
			//System.out.println("DBG151130: kill at mos.addItem...HGHG iR:" + iRounds + " iC:" + iColor);
			//m_mos.dump();
			//System.exit(0);
		}
		
		/*
		if (iColor == 0)
		{
			try { Thread.sleep(500); } catch (Exception e) {}
			System.out.println("DBG151130: kill @DFGFH");
			if (sRet == null) System.out.println("DBG151130:sRet == null");
			if (strat == null) System.out.println("DBG151130:strat == null");
			//System.exit(0);
		}
		*/
		
		if ((bStrategyImpact) && (iAlg == movevalue.ALG_ALLOW_STRATEGY))
		{
			System.out.println("DBG 150430: mvc_get best trying to make a strategy decision! First, dump!");
			strat.dump();
			System.out.println("DBG150430: Then, decision!");
			System.out.println("DBG 150430: mvc_get best trying to make a strategy decision!");
			String sMove = strat.getBestMove();
			System.out.println("DBG150430: strategy decision: " + sMove);
			//System.exit(0);  // $$$$$ 150430
			mval_combo strat_mvc = strat.get_mval_combo();
			/*
			System.out.println(strat_mvc.mv.dumpstr(iAlg));
			strat_mvc.cb.dump();
			strat_mvc.cb_base.dump();
			System.exit(0); */
			if (iCBLevel == iRounds) strat_mvc.mv.setCutBranchLevel (iRounds);
			return strat_mvc;
		}
		
		//System.out.println("DBG 150312: mval_vector.get_best() sRet = "+ sRet);
		if (iCBLevel == iRounds) theBest.mv.setCutBranchLevel(iRounds);
		//if (theBest != null) System.out.println("DBG 160413: mval_vector.get_best()" + theBest.mv.dumpstr(iAlg,movevalue.DUMPMODE_SHORT));
		return theBest;
	}
}


class mt_finder extends Thread implements Runnable
{
	chessboard m_cb;
	
	int m_iColor; 
	int m_iRounds; 
	movevalue m_basemv; 
	int m_iAlg; boolean m_bDebug; 
	gamehistory m_ghist; 
	mval_vector m_mvv;
	movevalue m_l3best;
	regbest m_regb;
	mostore mos;
	
	mt_finder(chessboard cb, int iColor, int iRounds, movevalue basemv, int iAlg, boolean bDebug, gamehistory ghist, mval_vector mvv, movevalue l3best, regbest rbest, mostore ms)
	{
		m_cb = cb.copy();
		m_iColor = iColor;
		m_iRounds = iRounds;
		m_basemv = basemv.copy();
		m_iAlg = iAlg;
		m_bDebug = bDebug;
		if (ghist != null) m_ghist = ghist.copy();
		m_mvv = mvv;
		m_regb = rbest;
		mos = ms;
		
	}
	
	public void run()
	{
		//System.out.println("mt_finder entering queue:" + m_mvv.iActCount);
		
		regbest rb_copy  = null;
		
		synchronized(mt_finder.this)
		{
			while (m_mvv.iActCount >= m_cb.mMaxThreads)
			{
				try { Thread.sleep(50); } catch (Exception e) {}
			}
			rb_control rbc = m_mvv.addActive();
			
			if (rbc != null)
			{
				rb_copy = rbc.rb;
				if (rbc.bDone) return;
			}
		}
		//System.out.println("mt_finder started work:" + m_mvv.iActCount);
		// $$$$ a gate is needed here
		try
		{
			movevalue l3best = null;
			movevalue l2best = null;
			
			//System.out.println("DBG150116:mt_finder.run() starts for rounds: " + m_iRounds);
			if (m_mvv.bestMvc != null)
			{
				//System.out.println("DBG150116:mt_finder.run() bestMvc is:" + m_mvv.bestMvc.mv.dumpstr(m_iAlg));
				if (m_iRounds == 1) l2best = m_mvv.bestMvc.mv.copy();
				if (m_iRounds == 2) l3best = m_mvv.bestMvc.mv.copy();
				else l3best = m_l3best;
			}
			
			/*
			
			if (m_iRounds == 1)
			{
				l2best = new movevalue("");
				l2best.copyfrom(m_mvv.bestMvc.mv);
			}
			*/
			
			//regbest rb_copy = m_regb.copy();
			//regbest rb_copy = m_mvv.m_regb; //.copy();
			
			//if (m_iAlg == movevalue.ALG_SUPER_PRUNING) rb_copy = new regbest();
			//else rb_copy = null;
			
			//System.out.println("Copy taken once!");
			//System.exit(0);
			
			m_cb.bNoThreadlaunch = true;
			m_cb.findAndDoBestMove(m_iColor,m_iRounds,m_basemv,m_iAlg,m_bDebug,m_ghist,m_mvv,false, null,l3best,l2best, rb_copy, mos); // $$$$ 150116 fix appropriate values for l3best & l2best
		}
		catch (Exception e)
		{
		}
	}
}

class rb_control
{
	regbest rb;
	boolean bDone;
	
	rb_control(regbest r, boolean b)
	{
		rb = r;
		bDone = b;
	}
}

class dt_control
{
	int iCheckColor;
	int iRounds;
	int iRoundslimit;
	int iCheckState;
	
	static final int CHECK_AT_WILL = 1;
	static final int MUST_CHECK = 2;
	static final int ALL_MOVES = 3;
	
	dt_control(int iColor, int iLimit, int iR, int iState)
	{
		iCheckColor = iColor;
		iRoundslimit = iLimit;
		iRounds = iR;
		iCheckState = iState;
		//System.out.println("DBG: dt_control created, clr: " + iColor + ", limit:" + iLimit + " iRounds:" + iRounds + " checkstate:" + iCheckState);
	}
	
	int getCheckColor()
	{
		return iCheckColor;
	}
	
	boolean bMustCheck()
	{
		return iCheckState == MUST_CHECK;
	}
	
	void dump()
	{
		System.out.println("dt_control dump: clr:" + iCheckColor + " rounds:" + iRounds + " roundslimit:" + iRoundslimit + " cstate:" + iCheckState); 
	}
	
	dt_control one_deeper()
	{
		dt_control ndtc;
		
		//System.out.println("DBG 141210 inside dt_control.one_deeper()");
		if (iRounds+1 < iRoundslimit) ndtc = new dt_control(iCheckColor, iRoundslimit,iRounds+1, iCheckState);
		else ndtc = null;
		return ndtc;
	}
	
	boolean bLastLevel()
	{
		return iRounds + 1 >= iRoundslimit;
	}
}

class block
{
	int xk;
	int yk;
	
	block (int x, int y)
	{
		xk = x;
		yk = y;
	}
}

class instWinRec
{
	int iIWCorr;
	boolean bIW;
	
	instWinRec (int i, boolean b)
	{
		iIWCorr = i;
		bIW = b;
	}
}
