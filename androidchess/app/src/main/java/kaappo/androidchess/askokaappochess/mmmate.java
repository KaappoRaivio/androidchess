package kaappo.androidchess.askokaappochess;
import java.util.*;

public class mmmate
{
	public static final int MM_NONE = 0;
	public static final int MM_CHECK_NORISK = 1;
	public static final int MM_CHECK_WITHRISK = 2;
	public static final int MM_RISKY_MOVE = 3;
	public static final int MM_FARAWAY = 4;
	public static final int MM_KINGLIMIT = 5;
	public static final int MM_CHECK_ENROUTE = 6;
	public static final int MM_ASSIST_CHECK_ENROUTE = 7;
	public static final int MM_LAST = 8;
	
	public static final int CALCPOINT_LIMIT = 2;
	
	public static final boolean bDoRec = true;
	
	chessboard cb;
	Vector vCheckLoc;
	static int iPointArray[];
	
	mmmate(chessboard b)
	{
		cb = b;
		iPointArray = new int[16];
		iPointArray[MM_CHECK_NORISK] = 5;
		iPointArray[MM_CHECK_WITHRISK] = 3;
		iPointArray[MM_RISKY_MOVE] = -1;
		iPointArray[MM_FARAWAY] = -1;
		iPointArray[MM_KINGLIMIT] = 2;
		iPointArray[MM_CHECK_ENROUTE] = 2;
		iPointArray[MM_ASSIST_CHECK_ENROUTE] = 2;
		
	}
	
	void analyze(int iColor, int iRounds, int iRoundLimit, String sAnseq, Vector mCandVec)
	{
		boolean bCheckOnly = false;
		if (iRounds > iRoundLimit) bCheckOnly = true;
		
		if (iRounds > iRoundLimit+1) return;
		
		System.out.println("mmmate.analyze() starts. iRounds:" + iRounds + " iRoundLimit:" + iRoundLimit+ " " + sAnseq);
		cb.redoVectorsAndCoverages(iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
		cb.dump();
		moveindex mi, mien;
		boolean bChecking = false;
		int iEnemyMoves = -1;
		if (iColor == piece.WHITE) 
		{
			mi = cb.miWhiteMoveindex;
			mien = cb.miBlackMoveindex;
			
			bChecking = cb.bBlackKingThreat;
			iEnemyMoves = cb.iBlackMoves;
		}
		else
		{
			mi = cb.miBlackMoveindex;
			mien = cb.miWhiteMoveindex;
			bChecking = cb.bWhiteKingThreat;
			iEnemyMoves = cb.iWhiteMoves;
		}
		if (bChecking)
		{
			System.out.println("Checking now. Need to give turn to other..");
			if (iEnemyMoves==0)
			{
				System.out.println("But it's a mate too !");
				cb.dump();
				String sCand = new String (sAnseq);
				mCandVec.addElement(sCand);
				//System.exit(0);
			}
			else if (iRounds < iRoundLimit+2)
			{
				System.out.println("Under check, can try following moves");
				cb.dump();
				mien.dump(true);
				for (int i=0;i<mien.getSize();i++)
				{
					move m = mien.getMoveAt(i);
					String sMstr = m.moveStr();
					String sMstrCaps = m.moveStrCaps();
					System.out.println("Under check domove: " + sMstr);
					chessboard cb2 = cb.copy();
					cb2.dump();
					cb2.domove(sMstrCaps,1-iColor);
					mmmate mm2 = new mmmate(cb2);
					if( bDoRec) mm2.analyze(iColor,iRounds,iRoundLimit, sAnseq + ";" + sMstr, mCandVec);
				}
			}
			System.out.println("analyze returning from checking.");
			return;
		}
		
		if (!bCheckOnly)
		{
			vCheckLoc = new Vector();
			findCheckPositions(iColor, vCheckLoc);
			analyzeCheckPositions(cb, vCheckLoc, iColor, mi);
			System.out.println("=====");
			analyzeMoveIndex(mi,iColor,vCheckLoc, iRounds, iRoundLimit, sAnseq, mCandVec);
		}
		else
		{
			System.out.println("Do check moves only!");
			//cb.dump();
			//mi.dump(true);   
			for (int i=0;i<mi.getSize();i++)
			{
				move m = mi.getMoveAt(i);
				if (m.isCheck() || m.isRevCheck())
				{
					//System.out.println("Copying a board at 114!");
					//cb.dump();
					chessboard cb2 = cb.copy();
					String sMstr = m.moveStr();
					String sMstrCaps = m.moveStrCaps();
					boolean bRet = cb2.domove(sMstrCaps,iColor);
					if (!bRet)
					{
						System.out.println("move "+ sMstrCaps + " failed:");
						cb2.dump();
						System.exit(0);
					}
					System.out.println("Moved:" + sMstr + " bRet: " + bRet);
					mmmate mm2 = new mmmate(cb2);
					
					if( bDoRec) mm2.analyze(iColor,iRounds +1,iRoundLimit, sAnseq + ";" + sMstr, mCandVec);
				}
			}
		}
		
		
	}
	
	void analyzeMoveIndex(moveindex mi, int iColor, Vector vCheckLoc, int iRounds, int iRoundLimit, String sAnseq, Vector mCandVec)
	{
		king kEnemy = cb.locateKing(1-iColor);
		
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			analyzeMove(m,kEnemy, vCheckLoc, iColor, iRounds, iRoundLimit, sAnseq, mCandVec);
		}
	}
	
	void analyzeMove(move m, king k, Vector vCheckLoc, int iColor, int iRounds, int iRoundLimit, String sAnseq, Vector mCandVec)
	{
		mm_movewrap mw = new mm_movewrap(m);
		
		/*
		if (!m.isRisky()) 
		{
			boolean bTooFar = false;
			if (((m.p.iType == piece.KING) || (m.p.iType == piece.PAWN)) && (Math.abs(m.ytar-k.yk) >= 3)) bTooFar = true;
			if (!bTooFar) 
			{
				System.out.println(m.moveStrLong() + " is a candidate.") ;
			}
			else mw.setflag(MM_FARAWAY);
		}
		else
		{
			mw.setflag(MM_RISKY_MOVE);
		}*/
		if (m.isRisky()) mw.setflag(MM_RISKY_MOVE);
		if (((m.p.iType == piece.KING) || (m.p.iType == piece.PAWN)) && (Math.abs(m.ytar-k.yk) >= 3)) mw.setflag(MM_FARAWAY);
		if ((m.isCheck() || m.isRevCheck()) && m.isRisky()) mw.setflag(MM_CHECK_WITHRISK);
		if ((m.isCheck() || m.isRevCheck()) && !m.isRisky()) mw.setflag(MM_CHECK_NORISK);
		
		int iCPFlags = iMoveFlagsFromCheckLoc(m,vCheckLoc);
		if (iCPFlags == mm_checkloc.CL_FM_OK) mw.setflag(MM_CHECK_ENROUTE);
		
		if (bLimitsKingArea(m,k,cb)) mw.setflag(MM_KINGLIMIT);
		
		if (bAssistEnRoute(m,k,cb,vCheckLoc)) mw.setflag(MM_ASSIST_CHECK_ENROUTE);
		
		System.out.println(mw.mwrapString());
		
		if (mw.calcPoints() >= CALCPOINT_LIMIT) 
		{
			if ((iRounds < iRoundLimit) || m.isCheck() || m.isRevCheck())
			{
				if (iRounds < iRoundLimit + 1)
				{
					chessboard cb2 = cb.copy();
					String sMove = m.moveStrCaps();
					String sMstr = m.moveStr();
					System.out.println("This move should be tried: " + sMove + " iR:" + iRounds);
					cb2.domove(sMove,m.p.iColor);
					mmmate mm2 = new mmmate(cb2);
					if( bDoRec) mm2.analyze(iColor,iRounds +1,iRoundLimit, sAnseq + ";" + sMstr, mCandVec);
				}
			}
			//System.exit(0);
		}
	}
	
	void findCheckPositions(int iColor, Vector v)
	{
		king kEnemy = cb.locateKing(1-iColor);
		
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
		{
			piece p = cb.blocks[i][j];
			if ((p!= null) && (p.iColor == iColor)) switch (p.iType)
			{
				case piece.PAWN:
					findPawnCheckPositions((pawn) p, kEnemy, v);
					break;
					
				case piece.BISHOP:
					findBishopCheckPositions((bishop) p , kEnemy, v);
					break;	
					
				case piece.KNIGHT:
					findKnightCheckPositions((knight) p, kEnemy,v);
					break;		
					
				case piece.ROOK:
					findRookCheckPositions((rook) p, kEnemy,v);
					break;	
					
				case piece.QUEEN:
					findQueenCheckPositions((queen) p , kEnemy, v);
					break;		
			}
			
		}
	}
	
	void findPawnCheckPositions(pawn p, king kEnemy, Vector v)
	{
		if (Math.abs(p.xk-kEnemy.xk) > 1) return;
		if (Math.abs(p.xk-kEnemy.xk) == 0) return;
		if (Math.abs(p.yk-kEnemy.yk) >= 3) return;
		if ((p.iColor == piece.WHITE) && (p.yk >= kEnemy.yk)) return;
		if ((p.iColor == piece.BLACK) && (p.yk <= kEnemy.yk)) return;
		if (p.iColor == piece.WHITE) 
		{
			mm_checkloc mmc = new mm_checkloc(p.yk,kEnemy.yk-1,0,p,cb);
			v.addElement(mmc);
		}
		if (p.iColor == piece.BLACK) 
		{
			mm_checkloc mmc = new mm_checkloc(p.yk,kEnemy.yk+1,0,p,cb);
			v.addElement(mmc);
		}
	}
	
	void findRookCheckPositions(rook r, king kEnemy, Vector v)
	{
		for (int i=1;i<=8;i++)
		{
			if (i != kEnemy.xk) 
			{
				mm_checkloc mmc = new mm_checkloc(i,kEnemy.yk,0,r,cb);
				v.addElement(mmc);
			}
			if (i != kEnemy.yk) 
			{
				mm_checkloc mmc = new mm_checkloc(kEnemy.xk,i,0,r,cb);
				v.addElement(mmc);
			}
			
		}
		
	}
	
	void findKnightCheckPositions(knight k, king kEnemy, Vector v)
	{
		System.out.println("findKnightCheckPositions called for knight at " +k.xk + "," + k.yk);
		
		int clx, cly;
		
		clx = kEnemy.xk+2; cly = kEnemy.yk+1;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}	
		
		clx = kEnemy.xk+2; cly = kEnemy.yk-1;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}
		
		clx = kEnemy.xk-2; cly = kEnemy.yk+1;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}	
		
		clx = kEnemy.xk-2; cly = kEnemy.yk-1;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}
		
		clx = kEnemy.xk+1; cly = kEnemy.yk+2;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}	
		
		clx = kEnemy.xk+1; cly = kEnemy.yk-2;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}
		
		clx = kEnemy.xk-1; cly = kEnemy.yk+2;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}	
		
		clx = kEnemy.xk-1; cly = kEnemy.yk-2;
		if ((clx >= 1) && (clx <= 8) && (cly >=1) && (cly <= 8)) 
		{
			mm_checkloc mmc = new mm_checkloc(clx,cly,0,k,cb);
			v.addElement(mmc);
		}
		
		
		//System.exit(0);
	}
	 
	void findBishopCheckPositions(bishop b, king kEnemy, Vector v)
	{
		System.out.println("Bishop check pos: b at: " + b.xk + "," + b.yk + "  k at " + kEnemy.xk + ","+ kEnemy.yk);
		
		if (((b.xk+b.yk) % 2) != ((kEnemy.xk+kEnemy.yk) % 2))
		{
			System.out.println("diff colors!");
			return;
		}
		
		for (int i=1;i<=8;i++)
		{
			if (i != kEnemy.xk) 
			{
				int j1 = kEnemy.yk+(i-kEnemy.xk);
				int j2 = kEnemy.yk-(i-kEnemy.xk);
				
				if ((j1 >= 1) && (j1 <=8)) 
				{
					mm_checkloc mmc = new mm_checkloc(i,j1,0,b,cb);
					v.addElement(mmc);
				}
				if ((j1 >= 1) && (j1 <=8)) 
				{
					mm_checkloc mmc = new mm_checkloc(i,j2,0,b,cb);
					v.addElement(mmc);
				}
			}
			
		}
		
	}
	
	void findQueenCheckPositions(queen q, king kEnemy, Vector v)
	{
		System.out.println("mmmate.findQueenCheckPositions() not implemented.");
		System.exit(0);
	}
	
	void dumpCheckPositions(Vector v)
	{
		for (int i=0;i<v.size();i++)
		{
			mm_checkloc mmc = (mm_checkloc)v.elementAt(i);
			System.out.println(mmc.dumpStr());
		}
	}
	
	void analyzeCheckPositions(chessboard cb,  Vector v , int iColor, moveindex mi)
	{
		king kEnemy = cb.locateKing(1-iColor);
		
		for (int i=0;i<v.size();i++)
		{
			mm_checkloc mmc = (mm_checkloc)v.elementAt(i);
			//System.out.print(mmc.dumpStr());
			if (mmc.pi.canReach(mmc.xk,mmc.yk,kEnemy,cb)) 
			{
				if (((iColor == piece.WHITE) && (cb.iBlackStrike[mmc.xk][mmc.yk] > 0)) || 
					((iColor == piece.BLACK) && (cb.iWhiteStrike[mmc.xk][mmc.yk] > 0)))
					mmc.flags = mm_checkloc.CL_RISKYCHECK;
				else mmc.flags = mm_checkloc.CL_OKCHECK;
			}
			else mmc.flags = mm_checkloc.CL_NOCHECK;
			
			mmc.setEnrouteFlags( iColor,  mi);
		}
		System.out.println("OK CHECKS AVAILABLE:");
		for (int i=0;i<v.size();i++)
		{
			mm_checkloc mmc = (mm_checkloc)v.elementAt(i);
			if (mmc.flags == mm_checkloc.CL_OKCHECK ) System.out.println(mmc.dumpStr() + " " + mmc.sEnroute(iColor));
		}
		System.out.println("RISKY CHECKS AVAILABLE:");
		for (int i=0;i<v.size();i++)
		{
			mm_checkloc mmc = (mm_checkloc)v.elementAt(i);
			if (mmc.flags == mm_checkloc.CL_RISKYCHECK ) System.out.println(mmc.dumpStr() + " " + mmc.sEnroute(iColor));
		}
		
	}
	
	int iMoveFlagsFromCheckLoc(move m, Vector v)
	{
		System.out.println("IMFFCL for" + m.moveStr());
		for (int i=0;i<v.size();i++)
		{
			mm_checkloc mmc = (mm_checkloc)v.elementAt(i);
			if (mmc.flags == mm_checkloc.CL_OKCHECK )
			{
				Vector mmv = mmc.vDevMoves;
				for (int j=0;j<mmv.size();j++)
				{
					move m2 = (move)mmv.elementAt(j);
					if (m.samemove(m2)) 
					{
						System.out.println("iMoveFlagsFromCheckLoc: " + m.moveStr() + "/" + m2.moveStr());
						//System.exit(0);
						return m2.iMMEnrMoveStat;
					}
				}
			}
		}
		
		return mm_checkloc.CL_FM_NOTEXIST;
	}
	
	
	boolean bLimitsKingArea(move m,king k,chessboard cb)
	{
		for (int i=k.xk-1;i<=k.xk+1;i++)
			for (int j=k.yk-1;j<=k.yk+1;j++)
		{
			if (m.p.canReachCoord(m.xtar,m.ytar,i,j,cb)) return true;
		}
		return false;
	}
	
	boolean bAssistEnRoute(move m,king k,chessboard cb, Vector v)
	{
		for (int i=0;i<v.size();i++)
		{
			mm_checkloc mmc = (mm_checkloc)v.elementAt(i);
			if (mmc.flags == mm_checkloc.CL_OKCHECK )
			{
				Vector mmv = mmc.vDevMoves;
				for (int j=0;j<mmv.size();j++)
				{
					move m2 = (move)mmv.elementAt(j);
					if (m2.iMMEnrMoveStat == mm_checkloc.CL_FM_NOTEXIST)
					{
						if (piece.directlyBetween(m2.p.xk,m2.p.yk,m.p.xk,m.p.yk,m2.xtar,m2.ytar)) return true;
					}
				}
			}
		}
		return false;
	}
	
	void analyzeMateCands(int iColor, Vector v)
	{
		System.out.println("Mate candidate dump: " + v.size() + " candidates.");
		cb.dump();
		
		Vector vReach = new Vector();
		
		for (int i=0;i<v.size();i++)
		{
			String ss = (String)v.elementAt(i);
			System.out.println("CHECKING: " + ss);
			
			//chessboard cb1=cb.copy();
		
			chessboard cbarray[] = new chessboard[10];
			cbarray[1]=cb.copy();
			cbarray[1].redoVectorsAndCoverages(1-iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
			
			boolean bBlocked = false;
			
			String sMoves[] = ss.split(";");
			int iEnemyCount = 0;
			int iOwnCount = 0;
		
			for (int j=1;j<sMoves.length;j++)
			{
				String sMove1 = sMoves[j].substring(1,5).toUpperCase();
				System.out.println("moving:"+sMove1);
				char c = sMoves[j].charAt(0);
				
				int iMColor;
				if ((int)c > 96) iMColor = 0;
				else iMColor = 1;
				
				System.out.println("iColor:" + iColor + " iMColor:" + iMColor);
				
				
				cbarray[j].domove(sMove1,iMColor);
				cbarray[j].dump();
				
				if (iMColor == iColor) 
				{
					iOwnCount++;
					System.out.println("Enemy could move now!");
					System.out.println("Block to be reached:" + sMove1.substring(2,4));
					if (iOwnCount < 3) System.out.println("By 2");
					else System.out.println("By 3");
					
					int x = (int)sMove1.charAt(2)-64;
					int y = (int)sMove1.charAt(3)-48;
					
					reach_obj ro = new reach_obj(cbarray[j], x, y, 1-iMColor, 2);
					vReach.addElement(ro);
					
				}
				else iEnemyCount++;
				
				cbarray[j+1]=cbarray[j].copy();
				cbarray[j+1].redoVectorsAndCoverages(1-iMColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
				
			}
			System.out.println("Replay done, iEnemyCount:"+iEnemyCount);
		}
		
		
		
		/*
		for (int i=0;i<v.size;i++)
		{
			String ss = (String)v.elementAt(i);
			System.out.println("CHECKING: " + ss);
			String sMoves[] = ss.split(";");
			chessboard cb1=cb.copy();
			String sMove1 = sMoves[1].substring(1,5).toUpperCase();
			System.out.println("moving:"+sMove1);
			cb1.domove(sMove1,1);
			cb1.dump();
			cb1.redoVectorsAndCoverages(1-iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);  // fix icolor right!
			String sMove2 = sMoves[2].substring(1,5).toUpperCase();
			System.out.println("Next move:"+sMoves[2]);
			moveindex miw = cb1.miWhiteMoveindex;
			if (trytoblockmove(sMoves[2],cb1.miWhiteMoveindex,cb1)) bBlocked = true;
		}
		*/
		
	}
	
	boolean trytoblockmove(String sMove, moveindex mi, chessboard cb)
	{	
		System.out.println("trytoblockmove: enter for: " + sMove );
		int tx = (int)(sMove.charAt(3))-96;
		int ty = (int)(sMove.charAt(4))-48;
		System.out.println("Crit block: " + tx +"," + ty);
		
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			if (m.p.canReachCoord(m.xtar,m.ytar,tx,ty,cb)) 
			{
				System.out.println(m.moveStr() + " will block -> no mate!");
				return true;
			}
		}
		return false;
	}
}

class mm_movewrap
{
	move m;
	int moveflags;
	int iPoints;
	
	mm_movewrap(move mm)
	{
		m = mm;
	}
	
	void setflag(int flag)
	{
		System.out.println("setting flag: " + flag + " orvalue:" + (Math.pow(2,flag)));
		moveflags = moveflags | (int)(Math.pow(2,flag));
	}
	
	String mwrapString()
	{
		iPoints = calcPoints();
		return m.moveStrLong() + " " + moveflags + " " + moveflagString(moveflags) + "  " + iPoints;
	}
	
	int calcPoints()
	{
		int iRet = 0;
		for (int i=0;i<mmmate.MM_LAST;i++)
		{
			if ((((int)(Math.pow(2,i)) & moveflags)) != 0) iRet = iRet+mmmate.iPointArray[i];
		}
		return iRet;
	}
	
	String moveflagString (int flags)
	{
		String sRet = "";
		//System.out.println((int)Math.pow(2,mmmate.MM_CHECK_NORISK));
		if ((flags & (int)Math.pow(2,mmmate.MM_CHECK_NORISK)) != 0) sRet = sRet + "MM_CHECK_NORISK ";
		if ((flags & (int)Math.pow(2,mmmate.MM_CHECK_WITHRISK)) != 0) sRet = sRet + "MM_CHECK_WITHRISK ";
		if ((flags & (int)Math.pow(2,mmmate.MM_RISKY_MOVE)) != 0) sRet = sRet + "MM_RISKY_MOVE ";
		if ((flags & (int)Math.pow(2,mmmate.MM_FARAWAY)) != 0) sRet = sRet + "MM_FARAWAY ";
		if ((flags & (int)Math.pow(2,mmmate.MM_KINGLIMIT)) != 0) sRet = sRet + "MM_KINGLIMIT ";
		if ((flags & (int)Math.pow(2,mmmate.MM_CHECK_ENROUTE)) != 0) sRet = sRet + "MM_CHECK_ENROUTE ";
		if ((flags & (int)Math.pow(2,mmmate.MM_ASSIST_CHECK_ENROUTE)) != 0) sRet = sRet + "MM_ASSIST_CHECK_ENROUTE ";
		
		
		
		return sRet;
	}
}

class mm_checkloc
{
	int xk,yk,flags;
	piece pi;
	chessboard cb;
	int fmovestatus;
	
	Vector vDevMoves;
	
	public static final int CL_NOCHECK = 0;
	public static final int CL_RISKYCHECK = 1;
	public static final int CL_OKCHECK = 2;
	
	public static final int CL_FM_OK = 0;
	public static final int CL_FM_RISKY = 1;
	public static final int CL_FM_NOTEXIST = 2;
	
	mm_checkloc(int x, int y, int f, piece p, chessboard c)
	{
		xk = x;
		yk = y;
		flags = f;
		pi = p;
		cb = c;
		
		vDevMoves = new Vector();
	}
	
	String dumpStr()
	{
		String sRet = pi.dumpchr() + (char)(xk+96)+yk;
		return sRet;
		
	}
	
	String sEnroute(int iColor)
	{
		String sRet = "";
		
		for (int i=0;i<vDevMoves.size();i++)
		{
			move m = (move)vDevMoves.elementAt(i);
			sRet = sRet + m.moveStr();
			switch (m.iMMEnrMoveStat)
			{
				case CL_FM_OK:
					sRet = sRet + "_OK ";
					break;
					
				case CL_FM_RISKY:
					sRet = sRet + "_RI ";
					break;
					
				case CL_FM_NOTEXIST:
					sRet = sRet + "_NE ";
					break;
					
			}
			
		}
		
		return sRet;
	}
	
	void setEnrouteFlags(int iColor, moveindex mi)
	{
		int mx, my;
		move m;
		
		System.out.println("SENRF for " + xk + "," + yk);
		
		switch (pi.iType)
		{
			case piece.ROOK:
				mx = pi.xk;
				my = yk;
				if (my != pi.yk) doRookEnrouteFlags(mx,my,mi);
				mx = xk;
				my = pi.yk;
				if (mx != pi.xk) doRookEnrouteFlags(mx,my,mi);
				break;

			case piece.KNIGHT:
				doKnightEnrouteFlags(mi);
				break;
			
			case piece.BISHOP:
				System.out.println("bishop enroute called. xk:" + xk + " yk: " + yk + " pi.xk:" + pi.xk + " pi.yk:" +pi.yk);
				doBishopEnrouteFlags(mi);
				break;
			
			case piece.PAWN:	
				break;
				
			default:
				System.out.println("mm_checkloc.setEnrouteFlags(): unknown pi.iType!");
				System.out.println(dumpStr());
				System.exit(0);
				break;
		}
		
	}
	
	void doRookEnrouteFlags(int mx, int my, moveindex mi)
	{
		String s = pi.dumpchr()+(char)(pi.xk+96)+pi.yk+(char)(mx+96)+my;
		System.out.println("looking for:" + s);
		move m = mi.findByString(s);
		if (m!=null) 
		{
			move m2 = m.copy();
			if (m.isRisky()) m2.iMMEnrMoveStat = CL_FM_RISKY;
			else m2.iMMEnrMoveStat = CL_FM_OK;
			vDevMoves.addElement(m2);
		}
		else
		{
			System.out.println("nonexist move to " + mx +","+my);
			move m2 = new move(mx,my,false,0,pi);
			m2.iMMEnrMoveStat = CL_FM_NOTEXIST;
			vDevMoves.addElement(m2);
		}
	}
	
	void doKnightEnrouteFlags(moveindex mi)
	{
		System.out.println("mm_checkloc.doKnightEnrouteFlags() called");
		System.out.println("checkloc at "+xk+","+yk+" knight at: "+ pi.xk +"," + pi.yk);
		System.out.println("distance: " + knight.distanceToTarget(pi.xk,pi.yk,xk,yk));
		
		if (knight.distanceToTarget(pi.xk,pi.yk,xk,yk) == 2)
		{
			for (int i=1;i<=8;i++)
				for (int j=1;j<=8;j++)
			{
				if ((knight.distanceToTarget(i,j,pi.xk,pi.yk) <= 1) &&	
					(knight.distanceToTarget(i,j,xk,yk) <= 1) )
				{
					String s = pi.dumpchr()+(char)(pi.xk+96)+pi.yk+(char)(i+96)+j;
					System.out.println("trying to find a knight move:" + s);
					move m = mi.findByString(s);
					if (m != null)
					{
						move m2 = m.copy();
						System.out.println("Adding knight enroute:" + m2.moveStrLong());
						if (m.isRisky()) m2.iMMEnrMoveStat = CL_FM_RISKY;
						else m2.iMMEnrMoveStat = CL_FM_OK;
						vDevMoves.addElement(m2);
					}
					else
					{
						move m2 = new move(i,j,false,0,pi);
						m2.iMMEnrMoveStat = CL_FM_NOTEXIST;
						vDevMoves.addElement(m2);
					}
				}
			}
		}
		
		
		//System.exit(0);
	}
	
	void doBishopEnrouteFlags(moveindex mi)
	{
		System.out.println("mm_checkloc.doBishopEnrouteFlags() called");
		System.out.println("checkloc at "+xk+","+yk+" bishop at: "+ pi.xk +"," + pi.yk);
		
		if (((xk+yk) % 2) != ((pi.xk+pi.yk) % 2)) return;
		
		int xt1 =-1, yt1 = -1, xt2 = -1, yt2 = -1;
		
		int xdiff = xk-pi.xk;
		int ydiff = yk-pi.yk;
		
		System.out.println("xdiff:" + xdiff);
		System.out.println("ydiff:" + ydiff);
		
		if (ydiff == 0)
		{
			xt1=(xk+pi.xk)/2;
			xt2=(xk+pi.xk)/2;
			yt1=yk+(xk-pi.xk)/2;
			yt2=yk-(xk-pi.xk)/2;
		} 
		else if (xdiff == 0)
		{
			yt1=(yk+pi.yk)/2;
			yt2=(yk+pi.yk)/2;
			xt1=xk+(yk-pi.yk)/2;
			xt2=xk-(yk-pi.yk)/2;
		}
		else if (xdiff > ydiff)
		{
			
		}
		else if (ydiff > xdiff)
		{
			
		}
		else
		{
			// ydiff == xdiff !
		}
		
		System.out.println("Bishop enroutes via:" + xt1+","+yt1+ " & " + xt2+","+yt2);
		addBishopEnrouteMoves(mi,xt1,yt1);
		addBishopEnrouteMoves(mi,xt2,yt2);
		
		
		
		//System.exit(0);
		
		
	}
	
	void addBishopEnrouteMoves(moveindex mi, int xt, int yt)
	{
		String s = pi.dumpchr()+(char)(pi.xk+96)+pi.yk+(char)(xt+96)+yt;
		System.out.println("trying to find a bishop move:" + s);
		move m = mi.findByString(s);
		if (m != null)
		{
			move m2 = m.copy();
			System.out.println("Adding bishop enroute:" + m2.moveStrLong());
			if (m.isRisky()) m2.iMMEnrMoveStat = CL_FM_RISKY;
			else m2.iMMEnrMoveStat = CL_FM_OK;
			vDevMoves.addElement(m2);
		}
		else
		{
			if ((xt >= 1) && (xt <= 8) && (yt >= 1) && (yt >=8))
			{
				move m2 = new move(xt,yt,false,0,pi);
				m2.iMMEnrMoveStat = CL_FM_NOTEXIST;
				vDevMoves.addElement(m2);
			}
		}
	}
}

class reach_obj
{
	chessboard cb;
	int iColor;
	int iMoves;
	int bx, by;
	
	reach_obj(chessboard cbrd, int x, int y, int iC, int iM)
	{
		cb = cbrd;
		iColor = iC;
		iMoves = iM;
		bx = x;
		by = y;
		System.out.println("reach_obj, x:" +x +","+y);
	}
}