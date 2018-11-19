package kaappo.androidchess.askokaappochess;

import java.util.*;
import java.io.*;

public class strategy
{
	int iColor;
	int iTurn;
	int iAlg;
	int iRounds;
	gamehistory ghist;
	int iCommon;
	
	PrintWriter pw;
	
	chessboard root_cb;
	Vector v;
	String sMoveOrder = "";
	mval_combo m_mval_combo;
	Vector dbg_Vector;
	
	strategy(chessboard cb, int iC, int iT, int iA, int iR, gamehistory gh)
	{
		root_cb = cb.copy();
		iColor = iC;
		iTurn = iT;
		iAlg = iA;
		iRounds = iR;
		v = new Vector();
		dbg_Vector = new Vector();
		ghist = gh;
		iCommon = 0;
		
		//System.out.println("strategy object created.");
	}
	
	synchronized void  addCand(movevalue mv)
	{
		
		movevalue m = mv.copy();
		//System.out.println("DBG150430: STRATEGY.addCand(" + iRounds+") " + m.dumpstr(29,movevalue.DUMPMODE_SHORT));
		
		if (v.size() == 0) 
		{
			v.addElement(new strat_entry(m, root_cb, ghist, dbg_Vector));
			//int iC1 = (m.sRoute.length()/5)-1;
			int iC1 = m.getComLength();
			iCommon = iC1 - iRounds;

			//if (iCommon < 0)
			if (m.sRoute.indexOf("@0@0") != -1)
			{
				//System.out.println("DBG151203: Strategy add cand iR: " + iRounds+ " rte:" + m.sRoute + " iCommon:" + iCommon);
				if (!m.sRoute.contains("@0@0")) {
					throw new RuntimeException("Exception in row 56 in strategy.java");
				} else
				{
					for (int i=0;i<=iC1;i++)
					{
						String sMov = m.getMoveN(i);
						//System.out.println("DBG151218:sMov:"+sMov);
						int iX = (int)(sMov.charAt(0))-64;
						int iY = (int)(sMov.charAt(1))-48;
						piece pp = root_cb.blocks[iX][iY];
						if ((pp!=null) && (pp.iColor == iColor)) iCommon = i;
					}
					if (iCommon < 0)
					{
						System.out.println("DBG151218: iCommon failureXXXX");
						throw new RuntimeException("DBG151218: iCommon failureXXXX");
					}
				}
			}
			return;
		}
		
		if (v.size() == 1)
		{
			/*
			String s1 = ((strat_entry)(v.elementAt(0))).mv.sRoute;
			String s2 = mv.sRoute;
			int ml = Math.min(s1.length(),s2.length());
			int i=0;
			while (i<ml && (s1.charAt(i) == s2.charAt(i))) i++;
			
			//System.out.println("DBG strat common!");
			int iCommon2 = i / 5;
			if (iCommon != iCommon2)
			{
				System.out.println("DBG151203: strategy, iCommon failure: iCommon:" + iCommon + " iCommon2:" + iCommon2+ "  iR:" + iRounds + " ifailureat: " + i);
				System.out.println("DBG151203: route[0]:" + s1);
				System.out.println("DBG151203: route[n]:" + s2);
				System.out.println("v.size() :" + v.size());
				//new Exception().printStackTrace();
				iCommon = iCommon2;
			}
			
			//System.out.println(s1 + " " + s2 + " i:" + i);
			*/;
			int iCompLen = Math.min(((strat_entry)(v.elementAt(0))).mv.getComLength(),mv.getComLength());
			int i=0;
			int iC2 = -1;
			while ((i<iCompLen) && (iC2 == -1))
			{
				String sm0=((strat_entry)(v.elementAt(0))).mv.getMoveN(i);
				String sm1=mv.getMoveN(i);
				if (!sm0.equals(sm1)) iC2 = i;
				i++;
			}
			if (iCommon != iC2)
			{
				String s1 = ((strat_entry)(v.elementAt(0))).mv.sRoute;
				String s2 = mv.sRoute;
				
				System.out.println("DBG160625: strategy, iCommon failure: iCommon:" + iCommon + " iCommon2:" + iC2+ "  iR:" + iRounds + " ifailureat: " + i);
				System.out.println("DBG160625: route[0]:" + s1);
				System.out.println("DBG160625: route[n]:" + s2);
				System.out.println("v.size() :" + v.size());
				//new Exception().printStackTrace();

				if ((!s1.contains("@0@0")) && (iC2 > 1)) {
					throw new RuntimeException("Exception in row 127 of stragey.java");
				}
				iCommon = iC2;
			}
			
		}
		
		for (int i=0;i<v.size();i++)
		{
			strat_entry se = (strat_entry)v.elementAt(i);
			movevalue m0 = se.mv;
			if (i == 0) // don't get to top of list if cut, below that it's OK! 160429
			{
				if (m.isBetterthan(m0,iColor,iAlg,iTurn,0))
				{
					///System.out.println("add@" + i);
					v.insertElementAt(new strat_entry(m,root_cb,ghist, dbg_Vector),i);
					return;
				}
			}
			else
			{
				if (m.isBetterthan(m0,iColor,iAlg,iTurn,0, false))
				{
					///System.out.println("add@" + i);
					v.insertElementAt(new strat_entry(m,root_cb,ghist, dbg_Vector),i);
					return;
				}
			}
		}
		//System.out.println("add end.");
		v.addElement(new strat_entry(m,root_cb, ghist, dbg_Vector));
	}
	
	boolean bIsPromotion (String sMove)
	{
        return sMove.equals("C2C1");
	}
	
	void dump()
	{
		boolean bSilent = true;
		
		movevalue m0 = null;
		if (v.size() != 0)
		{			
			strat_entry se = (strat_entry)v.elementAt(0);
			m0 = se.mv;
		}
		boolean bCut = false;
		
		sMoveOrder = "";
		if (!bSilent)
		{
			System.out.println("STRATEGY OBJECT DUMP:-------------------");
			System.out.println("root_cb: lastmove:" + root_cb.lastmoveString());	
			root_cb.prefixdump("",chessboard.DUMPMODE_SHORT);
		}
		for(int i=0;i<v.size();i++)
		{
			strat_entry se = (strat_entry)v.elementAt(i);
			movevalue m = se.mv;
			if ((!bCut) && (!m0.equalBalance(iAlg,iTurn,m) || !m0.equalStates(m) || (Math.abs(m0.iCalcValue(iAlg,iTurn)-m.iCalcValue(iAlg,iTurn)) > 5)))
			{
				if (!bSilent) System.out.println("----------------");
				bCut = true;
			}
			if (!bSilent) System.out.println(i+"(STRAT00)("+iRounds+"):" + m.dumpstr(iAlg, movevalue.DUMPMODE_SHORT) + " ("+ m.getFirstMove()+")");
			//sMoveOrder = sMoveOrder + m.getFirstMove() + " ";
			
			// 151218 debug code $$$
			String sMov = m.getMoveN(iCommon);
			//System.out.println("DBG151218:sMov:"+sMov);
			int iX = (int)(sMov.charAt(0))-64;
			int iY = (int)(sMov.charAt(1))-48;
			
			piece pp = root_cb.blocks[iX][iY];
			if (pp == null)
			{
				System.out.println("DBG151218: Null pp failure at strategy.dump()! sMov:" + sMov + " iC: " + iColor);
				System.out.println("DBG151218: strategy object size: " + v.size() + " iCommon: " + iCommon);
				System.out.println("DBG151218: movevalue route: " + m.sRoute);
				root_cb.dump();
				throw new RuntimeException("DBG151218: Null pp failure at strategy.dump()! sMov:" + sMov + " iC: " + iColor);
			}
			
			if (pp.iColor != iColor)
			{
				System.out.println("DBG151218: Wrong icolor pp failure at strategy.dump()! sMov:" + sMov + " iC: " + iColor);
				root_cb.dump();
				throw new RuntimeException("DBG151218: Wrong icolor pp failure at strategy.dump()! sMov:" + sMov + " iC: " + iColor);
			}
			// 151218 debug code end $$$
			
			sMoveOrder = sMoveOrder + m.getMoveN(iCommon) + " ";
		}
		sMoveOrder = sMoveOrder.trim();
		
		if (!bSilent)
		{
			System.out.println("STRATEGY OBJECT DUMP DONE.--------------");
			System.out.println("STRAT: iColor: " + iColor + " iRounds:" + iRounds);
			System.out.println("STRAT: Move Order:" + sMoveOrder);
		}
	}
	
	String getMoveOrder()
	{
		return sMoveOrder;
	}
	
	String getBestMove()
	{
		String sMove = null;
		boolean bGood = false;
		
		sMove = analyzeEntries();
		/*
		System.out.println("sMoveOrder now:" + sMoveOrder);
		
		while (!bGood)
		{
			try
			{
					java.io.InputStreamReader isr = new java.io.InputStreamReader( System.in );
					java.io.BufferedReader stdin = new java.io.BufferedReader( isr );

					System.out.print("STRATEGY: GIVE MOVE >");
					sMove = stdin.readLine();
			}
			catch (Exception e)
			{
			}
			sMove = sMove.toUpperCase().trim();
			if (sMove.length() == 4)
			{
				int iIdx = sMoveOrder.indexOf(sMove);
				if (iIdx != -1) bGood = true;
			}
			
		}
		*/
		
		/*
		System.out.println("Best Strategic Move:" + sMove + " ... HIT ENTER! >");
		try
		{
			java.io.InputStreamReader isr = new java.io.InputStreamReader( System.in );
			java.io.BufferedReader stdin = new java.io.BufferedReader( isr );
			String sz = stdin.readLine();
		}
		catch (Exception e)
		{
		}
		*/
		
		return sMove;
	}
	
	String analyzeEntries()
	{
		movevalue m0 = null;
		if (v.size() != 0)
		{			
			strat_entry se = (strat_entry)v.elementAt(0);
			m0 = se.mv;
		}
		boolean bCut = false;
		String sDesiredCap = "";
		String sUnDesiredCap = "";
		
		boolean bPreferClosed = false;
		boolean bPreferOpen = false;
		
		boolean bAvoidTie = false;
		boolean bPreferTie = false;
		
		int iPieceBalance = root_cb.pvaluesum(piece.WHITE)-root_cb.pvaluesum(piece.BLACK);
		
		sMoveOrder = "";
		System.out.println("STRATEGY OBJECT analysis starts.");
		System.out.println("PieceBalance: " + iPieceBalance );
		System.out.println("Gameisclosed:"+root_cb.bGameIsClosed());
		
		if ((iPieceBalance > 0) && (iColor == piece.WHITE)) bAvoidTie = true;
		if ((iPieceBalance < 0) && (iColor == piece.WHITE)) bPreferTie = true;
		if ((iPieceBalance < 0) && (iColor == piece.BLACK)) bAvoidTie = true;
		if ((iPieceBalance > 0) && (iColor == piece.BLACK)) bPreferTie = true;
		
		int iKnightBalance = root_cb.iWhitePieceCount[piece.KNIGHT]- root_cb.iBlackPieceCount[piece.KNIGHT];
		System.out.println("KnightBalance:" + root_cb.iWhitePieceCount[piece.KNIGHT] + "/" + root_cb.iBlackPieceCount[piece.KNIGHT]);
		
		int iBishopBalance = root_cb.iWhitePieceCount[piece.BISHOP]-root_cb.iBlackPieceCount[piece.BISHOP];
		System.out.println("BishopBalance:" + root_cb.iWhitePieceCount[piece.BISHOP] + "/" + root_cb.iBlackPieceCount[piece.BISHOP]);
		
		if ((iColor == piece.WHITE) && (iKnightBalance > 0)) bPreferClosed = true;
		if ((iColor == piece.BLACK) && (iKnightBalance < 0)) bPreferClosed = true;
		if ((iColor == piece.WHITE) && (iBishopBalance > 0)) bPreferOpen = true;
		if ((iColor == piece.BLACK) && (iBishopBalance < 0)) bPreferOpen = true;
		
		int iOffBal = iOfficerBalance();
		System.out.println("OfficerBalance: " + iOffBal);
		
		if ((iColor == piece.WHITE) && (iOffBal > 0)) sDesiredCap = sDesiredCap + "NBRQ";
		if ((iColor == piece.WHITE) && (iOffBal < 0)) sUnDesiredCap = sUnDesiredCap + "nbrq";
		if ((iColor == piece.BLACK) && (iOffBal < 0)) sDesiredCap = sDesiredCap + "nbrq";
		if ((iColor == piece.BLACK) && (iOffBal > 0)) sUnDesiredCap = sUnDesiredCap + "NBRQ";
		
		
		for(int i=0;((i<v.size()) && !bCut);i++) 
		{
			strat_entry se = (strat_entry)v.elementAt(i);
			movevalue m = se.mv;
			if ((!bCut) && (!m0.equalBalance(iAlg,iTurn,m) || !m0.equalStates(m) || (Math.abs(m0.iCalcValue(iAlg,iTurn)-m.iCalcValue(iAlg,iTurn)) > 5))) bCut = true;
			else se.bMadeCut = true;
		}
		
		System.out.println("STRATEGY OBJECTs that made the cut.");
		int seMax = -1000;
		String sBest = "";
		int iBestIndex = 0;
		
		strat_entry s0 = (strat_entry)v.elementAt(0);
		
		String sEngineMove = "";
		try
		{
			sEngineMove = engine.getMoveByAlg(engine.sEnginePerAlg(movevalue.ALG_ASK_FROM_ENGINE10),root_cb.FEN(),movevalue.ALG_ASK_FROM_ENGINE10).toUpperCase();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Exception in row 256 of strategy.java");
		}
		
		if (s0.isMate(iColor)) 
		{
			s0.doAnalysis(sDesiredCap,sUnDesiredCap,bPreferClosed,bPreferOpen, bAvoidTie, bPreferTie, s0.sFirstMove, sEngineMove);
			sBest = s0.sFirstMove;
			s0.bOrig = true;
			s0.bStrat = true;
			if (s0.sFirstMove.equals(sEngineMove)) s0.bEngine = true;
			
			s0.dbgFlush();
			
			printDbg("Strategy sees winning move " + s0.sFirstMove + " on move " + root_cb.iMoveCounter + " eng:" + sEngineMove);
			m_mval_combo = new mval_combo(s0.mv.copy(),s0.cb_getFinal(),s0.cb_getFirst());
			return s0.sFirstMove;
		}
		
		System.out.println("Starting from s0: " + s0.dumpstr(iAlg));
		s0.bOrig = true;
		
		
		for(int i=0;i<v.size();i++) 
		{
			strat_entry se = (strat_entry)v.elementAt(i);
			if (se.bMadeCut)
			{
				System.out.println(se.dumpstr(iAlg));
				
				se.doAnalysis(sDesiredCap,sUnDesiredCap,bPreferClosed,bPreferOpen, bAvoidTie, bPreferTie,  s0.sFirstMove, sEngineMove);
				
				if (se.seValue > seMax)  // $$$$ 150501 to neutralize stragegy for experiment by adding (i==0) condition
				{
					seMax = se.seValue;
					sBest = se.sFirstMove;
					iBestIndex = i;
					m_mval_combo = new mval_combo(se.mv.copy(),se.cb_getFinal(),se.cb_getFirst());
				}
				
				if (se.sFirstMove.equals(sEngineMove)) se.bEngine = true;
				
				se.dbgFlush();
				
			}
		}
		
		strat_entry se = (strat_entry)v.elementAt(iBestIndex);
		se.bStrat = true;
		String sStra = (String)dbg_Vector.elementAt(iBestIndex);
		sStra = sStra + " STRAT";
		//System.out.println(sStra);
		//System.out.println((String)dbg_Vector.elementAt(iBestIndex));
		dbg_Vector.removeElementAt(iBestIndex);
		dbg_Vector.insertElementAt(sStra,iBestIndex);
		//System.out.println((String)dbg_Vector.elementAt(iBestIndex));

		if (iBestIndex != 0)
		{
			if (s0 == null) System.out.println("s0 == null");
			strat_entry sebest = (strat_entry)v.elementAt(iBestIndex);
			if (sebest == null) System.out.println("sebest == null");
			
			sebest.bStrat = true;
			printDbg("Strategy made its mind: Change of " + s0.sFirstMove + " to " + sebest.sFirstMove + " on move " + root_cb.iMoveCounter + " eng:" + sEngineMove);
		}
		
		
		
		if ((iBestIndex == 0) && (v.size() > 1))
		{
			printDbg("Strategy agreed on move " + s0.sFirstMove + " on move " + root_cb.iMoveCounter + " eng:" + sEngineMove);
		}
		
		System.out.println("Analyzed best move: " + sBest + " with sevalue:" + seMax + " iBestIndex: " + iBestIndex);
		return sBest;
	}
	
	mval_combo get_mval_combo()
	{
		return m_mval_combo;
	}
	
	int iOfficerBalance()
	{
		int iOB = 0;
		
		int pvalue[];
		
		pvalue = new int[7];
		
		pvalue[piece.QUEEN] = 9;
		pvalue[piece.BISHOP] = 3;
		pvalue[piece.KNIGHT] = 3;
		pvalue[piece.ROOK] = 5;
		
		for (int pt = piece.QUEEN; pt <= piece.ROOK; pt++)
		{
			iOB = iOB+ (root_cb.iWhitePieceCount[pt]-root_cb.iBlackPieceCount[pt]) * pvalue[pt];
		}
		
		return iOB;
	}
	
	void printDbg(String s)
	{
		if (pw == null)
		{
			try
			{
				pw = new PrintWriter(new BufferedWriter(new FileWriter("dogmatix.out", true))); 
			}
			catch (IOException e) 
			{
				//exception handling left as an exercise for the reader
				System.out.println("IOException at strategy.printDbg() (A)");
				System.out.println(e.getMessage());
				throw new RuntimeException(e);
			}
		}
		
		
		try
		{
			pw.println(s);
			root_cb.dump_to_file(pw);
			pw.println(root_cb.FEN());
			for (int i=0;i<dbg_Vector.size();i++)
			{
				pw.println((String)dbg_Vector.elementAt(i));
			}
			pw.println("--");
			pw.close();
		}
		catch (Exception e)
		{
			System.out.println("IOException at strategy.printDbg() (B)");
			System.out.println(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
}

class strat_entry
{
	movevalue mv;
	chessboard root_cb;
	gamehistory ghist;
	
	Vector cb_vector;
	Vector dbg_vector;
	
	boolean bMadeCut;
	int seValue;
	String sFirstMove;
	
	String sDbg;
	boolean bOrig;
	boolean bEngine;
	boolean bStrat;
	
	static final int STRAT_ENTRY_LO_OPENING = 5;
	static final int STRAT_ENTRY_CASTLING = 5;
	static final int STRAT_ENTRY_QUEEN_FIRST_MOVE = -5;
	static final int STRAT_ENTRY_KING_FIRST_MOVE = -6;
	static final int STRAT_ENTRY_ROOK_FREECOLUMN = 5;
	static final int STRAT_ENTRY_ROOK_FROM_FREECOLUMN = -5;
	static final int STRAT_ENTRY_PAWN_NOPAWNPROT = -3;
	static final int STRAT_ENTRY_PAWN_PAWNPROT = 3;
	static final int STRAT_ENTRY_LO_RETURNTOZERO = -5;
	static final int STRAT_ENTRY_KNIGHT_TO_KING = 3;
	static final int STRAT_ENTRY_KNIGHT_FROM_KING = -3;
	static final int STRAT_ENTRY_DESIRED_CAPTURE = 5;
	static final int STRAT_ENTRY_UNDESIRED_CAPTURE = -5;
	static final int STRAT_ENTRY_PREFER_CLOSED = 5;
	static final int STRAT_ENTRY_PREFER_OPEN = 5;
	static final int STRAT_ENTRY_FREEPAWN_ADVANCE = 5;
	static final int STRAT_ENTRY_KINGSIDE_FIANCHETTO = 5;
	static final int STRAT_ENTRY_TIE_PREFERRED = 10;
	static final int STRAT_ENTRY_TIE_AVOIDED = -10;
	
	strat_entry(movevalue m, chessboard rcb, gamehistory gh, Vector dbgv)
	{
		mv = m.copy();
		root_cb = rcb.copy();
		bMadeCut = false;
		seValue = 0;
		ghist = gh;
		
		cb_vector = new Vector();
		dbg_vector = dbgv;
		
		sDbg = "";
		
		bOrig = false;
		bEngine = false;
		bStrat = false;
	}
	
	String dumpstr(int iAlg)
	{
		return mv.dumpstr(iAlg,movevalue.DUMPMODE_SHORT) + " / first:" + sFirstMove;
	}
	
	void doAnalysis(String sDesiredCap,String sUnDesiredCap, boolean bPreferClosed,boolean bPreferOpen, boolean bAvoidTie, boolean bPreferTie , String sOrig, String sEngine)
	{
		System.out.println("strat_entry.doAnalysis(): " + mv.sRoute + " moves done at cb:" + root_cb.iMoveCounter);
		String sMoves[] = mv.sRoute.split(";");
		//System.out.println(sMoves[1]);
		sFirstMove = new String (sMoves[1]);
		int x1,y1,x2,y2;
		x1 = (int)sMoves[1].charAt(0)-64;
		y1 = (int)sMoves[1].charAt(1)-48;
		x2 = (int)sMoves[1].charAt(2)-64;
		y2 = (int)sMoves[1].charAt(3)-48;
		
		piece p = root_cb.blocks[x1][y1];
		
		dbgEntry(mv.sRoute);
		
		if (((p.iType == piece.BISHOP) || (p.iType == piece.KNIGHT)) && (p.iLastMove == 0))
		{
			if (!((p.iType == piece.KNIGHT) && ((x2==1) || (x2==8))))
			{
				dbgEntry("STRAT_ENTRY_LO_OPENING");
				seValue = seValue + STRAT_ENTRY_LO_OPENING;
			}
		}
		
		if ((p.iType == piece.KING)  && (p.iLastMove == 0) && (x2 != 3) && (x2!= 7)) 
		{
			dbgEntry("STRAT_ENTRY_KING_FIRST_MOVE");
			seValue = seValue + STRAT_ENTRY_KING_FIRST_MOVE;
		}
		
		if ((p.iType == piece.QUEEN)  && (p.iLastMove == 0) && (root_cb.iMoveCounter < 20))
		{
			dbgEntry("STRAT_ENTRY_QUEEN_FIRST_MOVE");
			seValue = seValue + STRAT_ENTRY_QUEEN_FIRST_MOVE;
		}
		
		if ((p.iType == piece.KING) && (p.iLastMove == 0) && ((x2 == 3) || (x2== 7))) 
		{
			dbgEntry("STRAT_ENTRY_CASTLING");
			seValue = seValue + STRAT_ENTRY_CASTLING;
		}
		
		if ((p.iType == piece.ROOK) && (!root_cb.bPawnsAtColumn(x2,p.iColor)) && (root_cb.iMoveCounter < 25) && (y1 == y2))
		{
			dbgEntry("STRAT_ENTRY_ROOK_FREECOLUMN");
			seValue = seValue + STRAT_ENTRY_ROOK_FREECOLUMN;
		}
		
		if ((p.iType == piece.ROOK) && (!root_cb.bPawnsAtColumn(x1,p.iColor)) && (root_cb.iMoveCounter < 25) && (y1 == y2))
		{
			dbgEntry("STRAT_ENTRY_ROOK_FROM_FREECOLUMN");
			seValue = seValue + STRAT_ENTRY_ROOK_FROM_FREECOLUMN;
		}
		
		if ((p.iType == piece.PAWN) && (x1==x2) && !root_cb.bPawnProtectedAt(x2,y2,p.iColor) && (root_cb.iMoveCounter < 25))
		{
			dbgEntry("STRAT_ENTRY_PAWN_NOPAWNPROT");
			seValue = seValue + STRAT_ENTRY_PAWN_NOPAWNPROT;
		}
		
		if ((p.iType == piece.PAWN) && (x1==x2) && root_cb.bPawnProtectedAt(x2,y2,p.iColor))
		{
			dbgEntry("STRAT_ENTRY_PAWN_PAWNPROT");
			seValue = seValue + STRAT_ENTRY_PAWN_PAWNPROT;
		}
		
		if (((p.iType == piece.BISHOP) || (p.iType == piece.KNIGHT)) && (root_cb.iMoveCounter < 25))
		{
			if (((p.iColor == piece.WHITE) && (y2==1)) || ((p.iColor==piece.BLACK) && (y2==8)))
			{
				dbgEntry("STRAT_ENTRY_LO_RETURNTOZERO");
				//System.out.println("RT0:y2="+y2);
				seValue = seValue + STRAT_ENTRY_LO_RETURNTOZERO;
			}
		}
		
		
		if ((p.iType == piece.PAWN) && root_cb.bPawnIsFreeAt(x1,y1))
		{
			dbgEntry("STRAT_ENTRY_FREEPAWN_ADVANCE");
			seValue = seValue + STRAT_ENTRY_FREEPAWN_ADVANCE;
		}
		
		
		if ((p.iType == piece.BISHOP) && (x2==7) && (p.iLastMove == 0))
		{
			if (((p.iColor == piece.WHITE) && (y2==2)) || ((p.iColor == piece.BLACK) && (y2 == 7)))
			{
				dbgEntry("STRAT_ENTRY_KINGSIDE_FIANCHETTO");
				seValue = seValue + STRAT_ENTRY_KINGSIDE_FIANCHETTO;
			}
		}
		
		
		//System.out.println("Emulating moves by analyse:" + sMoves.length);
		chessboard cb_temp = new chessboard();
		cb_temp = root_cb.copy();
		String sCaptures = "";
		boolean bLeadsToClosed = false;
		for (int ii = 1; ii < sMoves.length; ii++)
		{
			//System.out.println(sMoves[ii]);
			
			int x01,y01,x02,y02;
			x01 = (int)sMoves[ii].charAt(0)-64;
			y01 = (int)sMoves[ii].charAt(1)-48;
			x02 = (int)sMoves[ii].charAt(2)-64;
			y02 = (int)sMoves[ii].charAt(3)-48;
			
			if ((x01 != 0) && (y01!=0))
			{
				piece pmov = cb_temp.blocks[x01][y01];
				
				piece pcap = cb_temp.blocks[x02][y02];
				if (pcap != null) sCaptures = sCaptures + pcap.dumpchr();
				else
				{
					if (pmov == null)
					{
						System.out.println("DBG 150426::Fatal Error pmov == null");
						System.out.println("ii="+ii + " x01="+x01+" y01="+y01);
						cb_temp.dump();
						throw new RuntimeException("DBG 150426::Fatal Error pmov == null");
					}
					
					if ((pmov.iType == piece.PAWN) && (x01!=x02))
					{
						// en passant
						if (p.iColor == piece.WHITE) sCaptures = sCaptures + "P";
						else sCaptures = sCaptures + "p";
					}
					else sCaptures = sCaptures + ".";
				}
				
				chessboard cb_new = new chessboard();
				cb_new = cb_temp.copy();
				//System.out.println("Trying to do move:"+sMoves[ii]);
				int iMcolor;
				if ((ii%2) == 1) iMcolor = p.iColor;
				else iMcolor = 1-p.iColor;
				boolean bRet = cb_new.domove(sMoves[ii],iMcolor);
				if (!bRet)
				{
					System.out.println("strat_entry.doAnalysis(): move failed!!!");
					throw new RuntimeException("strat_entry.doAnalysis(): move failed!!!");
				}

				if ((ghist != null) && (ii==1))
				{
					//System.out.println("Check for tie candidates!");
					if (ghist.bIsTieCandidate(cb_new,iMcolor))
					{
						if (bAvoidTie)
						{
							seValue = seValue + STRAT_ENTRY_TIE_AVOIDED;
							dbgEntry("STRAT_ENTRY_TIE_AVOIDED");	
						}
						if (bPreferTie)
						{
							seValue = seValue + STRAT_ENTRY_TIE_PREFERRED;
							dbgEntry("STRAT_ENTRY_TIE_PREFERRED");
						}
					}
				}
				
				System.out.println("round"+ii+" cb_new dumped:");
				cb_new.dump();
				
				chessboard cbv = new chessboard();
				cbv = cb_temp.copy();
				cb_vector.addElement(cbv);
				
				cb_temp = new chessboard();
				cb_temp = cb_new.copy();
			}
			
		}
		cb_vector.addElement(cb_temp.copy());
		
		if (p.iType == piece.KNIGHT)
		{
			king oppKing = root_cb.locateKing(1-p.iColor);
			int iCurrKingDist = knight.distanceToTarget(x1,y1,oppKing.xk,oppKing.yk);
			int iNewKingDist = knight.distanceToTarget(x2,y2,oppKing.xk,oppKing.yk);
			
			System.out.println("KnightDist: curr: " + iCurrKingDist + " new: " + iNewKingDist);
			if (iCurrKingDist > iNewKingDist)
			{
				String sNsymbol;
				if (p.iColor == piece.WHITE) sNsymbol = "n";
				else sNsymbol = "N";
				
				if (sCaptures.indexOf(sNsymbol) == -1)
				{
					seValue = seValue + STRAT_ENTRY_KNIGHT_TO_KING;
					dbgEntry("STRAT_ENTRY_KNIGHT_TO_KING");
				}
				else System.out.println("DBG150427: N-to-K invalidated, got captured!");
			}
			if (iCurrKingDist < iNewKingDist)
			{
				seValue = seValue + STRAT_ENTRY_KNIGHT_FROM_KING;
				dbgEntry("STRAT_ENTRY_KNIGHT_FROM_KING");
			}
			
		}
		
		
		if (bCapFound(sDesiredCap,sCaptures)) 
		{
			seValue = seValue + STRAT_ENTRY_DESIRED_CAPTURE;
			dbgEntry("STRAT_ENTRY_DESIRED_CAPTURE");			
		}
		if (bCapFound(sUnDesiredCap,sCaptures))
		{
			seValue = seValue + STRAT_ENTRY_UNDESIRED_CAPTURE;
			dbgEntry("STRAT_ENTRY_UNDESIRED_CAPTURE");	
		}
		
		if (cb_temp.bGameIsClosed()) bLeadsToClosed = true;
		System.out.println("Leads to Closed Game:" + bLeadsToClosed + ". Leads to Open Game:" + cb_temp.bGameIsOpen());	
		
		if (bLeadsToClosed && bPreferClosed)
		{
			seValue = seValue + STRAT_ENTRY_PREFER_CLOSED;
			dbgEntry("STRAT_ENTRY_PREFER_CLOSED");
		}
		if (cb_temp.bGameIsOpen() && bPreferOpen)
		{
			seValue = seValue + STRAT_ENTRY_PREFER_OPEN;
			dbgEntry("STRAT_ENTRY_PREFER_OPEN");
		}
		
		System.out.println("Scaptures analysis:" + sCaptures);
		
		System.out.println("Move1: " + sFirstMove + " piece:" + p.iType + " last move:" + p.iLastMove + " -> seVal:" + seValue);
		System.out.println("-----");
		dbgEntry("cap: "+ sCaptures);
		dbgEntry("val: " + seValue);
		
		//dbgFlush();
		
	}
	
	boolean bCapFound(String sCand, String sCapt)
	{
		//System.out.println("bCapFound called! sCand = " + sCand + " sCapt = " + sCapt);
		
		if (sCand.length() == 0) return false;
		
		for (int i=0;i<sCand.length();i++)
		{
			String sC = sCand.substring(i,i+1);
			//System.out.println("bCapFound: sC='"+sC+"'");
			if (sCapt.indexOf(sC) != -1) return true;
		}
		return false;
	}
	
	chessboard cb_getFirst()
	{
		chessboard cb;
		cb = (chessboard)cb_vector.elementAt(1);
		return cb.copy();
	}
	
	chessboard cb_getFinal()
	{
		chessboard cb;
		int i = cb_vector.size();
		cb = (chessboard)cb_vector.elementAt(i-1);
		return cb.copy();
	}
	
	void dbgEntry(String s)
	{
		String sCut = s.replace("STRAT_ENTRY_","");
		sDbg = sDbg + sCut + " ";
		System.out.println(s);
	}
	
	void setFlags(boolean bO, boolean bE, boolean bS)
	{
		bOrig = bO;
		bEngine = bE;
		bStrat = bS;
	}
	
	void dbgFlush()
	{
		//dbgEntry("HUOH!");
		
		if (bOrig) dbgEntry(" ORIG");
		if (bEngine) dbgEntry(" ENGINE");
		if (bStrat) dbgEntry(" STRAT");
		dbg_vector.addElement(sDbg);
	}
	
	boolean isMate (int iColor)
	{
		if ((iColor == piece.WHITE) && (mv.bBlackCheckMate)) return true;
        return (iColor == piece.BLACK) && (mv.bWhiteCheckMate);
    }
	
}