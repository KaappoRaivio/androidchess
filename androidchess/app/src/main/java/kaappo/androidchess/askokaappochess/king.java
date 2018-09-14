package kaappo.androidchess.askokaappochess;
import java.util.*;

public class king extends piece
{
	
	king (int x, int y, int col)
	{
		super (x,y,col);
		//System.out.println("King created at " + x + "," + y);
		iType = piece.KING;		
	}
	
	int pvalue()
	{
		return 1000;
	}
	
	String dumpchr()
	{
		if (iColor == WHITE) return "k";
		else return "K";
	}
	
	Vector moveVector (chessboard cb)
	{
		//System.out.println("DBG 141221: Asking for king's move vector for king at : " + xk + "," + yk);
		//dumpmoveVector (mMoveVector);
		if (mMoveVector != null) return mMoveVector;
		//System.out.println("DBG: Need to create a new one...");
		
		Vector mv = new Vector();
		Vector mvold = mv;
		
		//System.out.println("DBG: returning king movevector @0: " + xk + "," + yk);
		
		trymoveat(xk-1,yk-1,mv,cb, piece.NO_DIR);
		trymoveat(xk,yk-1,mv,cb, piece.NO_DIR);
		trymoveat(xk+1,yk-1,mv,cb, piece.NO_DIR);
		//System.out.println("DBG:King moveVector size @l3: " + mv.size());
		trymoveat(xk+1,yk,mv,cb, piece.NO_DIR);
		//System.out.println("DBG:King moveVector size @14: " + mv.size());
		trymoveat(xk+1,yk+1,mv,cb, piece.NO_DIR);
		trymoveat(xk,yk+1,mv,cb, piece.NO_DIR);
		trymoveat(xk-1,yk+1,mv,cb, piece.NO_DIR);
		trymoveat(xk-1,yk,mv,cb, piece.NO_DIR);
		
		// castling code 
		
		if  ((xk == 5) && ((yk == 1) || (yk == 8)))
		{
			//System.out.println("DBG160730:King castling, iColor:" + iColor + ", bThreat:" + bThreat);
			// king is in place, try long castling
			//if (yk==1 )System.out.println("DBG150602: white king castling code: iLastMove:" + iLastMove);
			if (cb.blocks[1][yk] != null)
				if ((cb.blocks[1][yk].iType == piece.ROOK) && 
					(cb.blocks[1][yk].iColor == iColor) && 
					(cb.blocks[2][yk] == null) && 
					(cb.blocks[3][yk] == null) && 
					(cb.blocks[4][yk] == null) &&
					(iLastMove == 0) &&
					(cb.blocks[1][yk].iLastMove == 0) && 
					(notCovered(xk-1,yk,iColor,cb)) &&
					(notCovered(xk-2,yk,iColor,cb) ))  
					// (cb.iCountCheckers(this) == 0) )  countcheckers will call this method again -> no good
					trymoveat(xk-2,yk,mv,cb, piece.NO_DIR);
			
			// king is in place, try short castling
			if (cb.blocks[8][yk] != null)
			{
				//if (yk==1) System.out.println("DBG150602 White King Castle SPOT 2: "+cb.blocks[8][yk].iLastMove + ";"+ cb.blocks[8][yk].iType+";"+cb.blocks[8][yk].iColor+";"+(cb.blocks[7][yk] == null)+";"+(cb.blocks[6][yk] == null)+";"+(notCovered(xk+1,yk,iColor,cb))+";"+(notCovered(xk+2,yk,iColor,cb)));
				if ((cb.blocks[8][yk].iType == piece.ROOK) && 
					(cb.blocks[8][yk].iColor == iColor) && 
					(cb.blocks[7][yk] == null) && 
					(cb.blocks[6][yk] == null) &&
					(iLastMove == 0) &&
					(cb.blocks[8][yk].iLastMove == 0) &&
					(notCovered(xk+1,yk,iColor,cb)) &&
					(notCovered(xk+2,yk,iColor,cb) ) ) 
					//(cb.iCountCheckers(this) == 0))  countcheckers will call this method again -> no good
					{
						trymoveat(xk+2,yk,mv,cb, piece.NO_DIR);
					}
			}
			// $$$ need to check whether any of squares are under threat ... coverage bits would probably work here
		}
		
		//System.out.println("DBG:King moveVector size @1: " + mv.size());
		
		mMoveVector = mv;
		
		/*
		System.out.println("About to locate other king");
		king otherking = cb.locateKing(1- this.iColor);  // locate other king
		if (otherking.mMoveVector == null) 
		{
			Vector vOther =  otherking.moveVector(cb);
			System.out.println("king vectors initialized. time to remove duplicates.");
			
			int iCl1 = -1;
			int iCl2 = -1;
			
			for (int i=0;i<mv.size();i++)
				for (int j=0;j<vOther.size();j++)
				{
					move m1 = (move)mv.elementAt(i);
					move m2 = (move)vOther.elementAt(j);
					if (m1.equals(m2)) 
					{
						iCl1 = i;
						iCl2 = j;
					}
				}
			
			if ((iCl1 != -1) && (iCl2 != -1)) 
			{
				System.out.println("Duplicate found. Will delete");
				mv.remove(iCl1);
				vOther.remove(iCl2);
			}
		}
		*/
		
		return mv;
		
	}
	
	void dropCoveredMoves(chessboard cb)
	{
		boolean dropArray[][];
		
		Vector mv = moveVector(cb);
		
		cb.dbgPrintln("King at " + xk +"," + yk + " movevector before cleanup:");  
		//dumpmoveVector(mv);
		cb.dbgPrintln("Coverage dump:");
		//cb.dumpCoverages();
		
		if (iColor == piece.WHITE) dropArray = cb.bBlackCoverage;
		else dropArray = cb.bWhiteCoverage;
		
		for (int i=0; i<mv.size();i++)
		{
			move m = (move)mv.elementAt(i);
			
			if (Math.abs((m.xtar - xk)) == 2)
			{
				//System.out.println("DBG150825: castling!: " + m.moveStr() + " ... " + m.xtar + "," + m.ytar + ".." + dropArray[3][m.ytar]+dropArray[4][m.ytar]);
				if (((m.xtar==7) && ((dropArray[6][m.ytar]) || (dropArray[7][m.ytar]))) ||
					((m.xtar==3) && ((dropArray[3][m.ytar]) || (dropArray[4][m.ytar]))) ||
					bThreat)
				{
					//System.out.println("DBG 150825: Have to remove castling:" + m.moveStr());
					mv.remove(i);
					i--;
				}
				
			}
			else if (dropArray[m.xtar][m.ytar])
			{
				mv.remove(i);
				i--;
				cb.dbgPrintln("king at " + xk +"," + yk + " had move to " + m.xtar + "," + m.ytar + " removed from list.");
			}
			else 
			{
				piece p = cb.blocks[m.xtar][m.ytar];
				if ((p != null) && (p.iColor != iColor) && p.bProt)
				{
					mv.remove(i);
					i--;
				}
			} 
			
			
		}
		
		cb.dbgPrintln("King at " + xk +"," + yk + " movevector after cleanup:");  
		//dumpmoveVector(mv);
	}
	
	boolean notCovered(int i, int j, int iColor, chessboard cb)
	{
		if (iColor == piece.WHITE)
		{
			return !cb.bBlackCoverage[i][j];
		}
		else
		{
			return !cb.bWhiteCoverage[i][j];
		}
	}
	
	boolean canReach(int xk, int yk, piece k, chessboard cb)
	{
        return (Math.abs(k.xk - xk) <= 1) && (Math.abs(k.yk - yk) <= 1);
		//return false;
		//return revealsChecker(k,cb);
	}
	
	boolean bPotentiallyReachable(int x1, int y1, int x2, int y2)
	{
        return (Math.abs(x1 - x2) <= 1) && (Math.abs(y1 - y2) <= 1);
    }
	
	boolean canReachCoord(int x1, int y1, int x2, int y2, chessboard cb)
	{
        return (Math.abs(x1 - x2) <= 1) && (Math.abs(y1 - y2) <= 1);
    }
	
	void checkEscapes(chessboard cb)
	{
		System.out.print("DBG160331: King CheckEscapes @" + xk + "," + yk+" ");
		Vector mv = moveVector(cb);
		for (int i=0;i<mv.size();i++)
		{
			move m=(move)mv.elementAt(i);
			System.out.print(m.moveStrLong() + " ");
		}
		if (mv.size() == 0) System.out.print("NO ESCAPES");
		if ((mv.size() >= 1) && (mv.size() <= 3)) checkVulnerability(cb,mv);
		if (mv.size() >= 4) System.out.print(" lots of room");
		System.out.println();
	}
	
	void checkVulnerability(chessboard cb, Vector mv)
	{
		if (mv.size() == 1)
		{
			System.out.println("King has 1 escape block!");
		}
		
		if (mv.size() == 2)
		{
			move m0 = (move)mv.elementAt(0);
			move m1 = (move)mv.elementAt(1);
			if (m0.xtar == m1.xtar) 
			{
				System.out.print(" vertical vulnerability!");
				moveindex miother;
				if (iColor == piece.WHITE) miother = cb.miBlackMoveindex;
				else miother = cb.miWhiteMoveindex;
				// a rook or queen, left or right!
				// queen, bishop or pawn, one up
				// knight two up or behind!
				int iMp = xk+2*(m0.xtar-yk);
				int i2Mp = xk+3*(m0.xtar-yk);
				int iMm = xk-(m0.xtar-yk);
				for (int i=0;i<miother.getSize();i++)
				{
					move moth=miother.getMoveAt(i);
					if (((moth.p.iType == piece.ROOK) || (moth.p.iType == piece.QUEEN)) && (moth.xtar == m0.xtar) && (!m0.bRisky) ) System.out.println("VULMOVE!"+moth.moveStrLong());
					if (((moth.p.iType == piece.BISHOP) || (moth.p.iType == piece.QUEEN)) && (moth.xtar == iMp) && (moth.ytar==yk) && (!m0.bRisky) ) System.out.println("VULMOVE!"+moth.moveStrLong());
					if ((moth.p.iType == piece.KNIGHT) && ((moth.ytar == i2Mp) || (moth.ytar == iMm) ) && (moth.ytar==yk) && (!m0.bRisky)) System.out.println("VULMOVE!"+moth.moveStrLong());
				}
			}
			if (m0.ytar == m1.ytar) 
			{
				System.out.print(" horizontal vulnerability check!");
				moveindex miother;
				if (iColor == piece.WHITE) miother = cb.miBlackMoveindex;
				else miother = cb.miWhiteMoveindex;
				// a rook or queen, left or right!
				// queen, bishop or pawn, one up
				// knight two up or behind!
				int iUp = yk+2*(m0.ytar-yk);
				int i2Up = yk+3*(m0.ytar-yk);
				int i2D = yk-(m0.ytar-yk);
				//System.out.println("iUp:"+iUp+ " i2Up:"+i2Up+" i2D:" + i2D);
				for (int i=0;i<miother.getSize();i++)
				{
					move moth=miother.getMoveAt(i);
					if (((moth.p.iType == piece.ROOK) || (moth.p.iType == piece.QUEEN)) && (moth.ytar == m0.ytar) && (!m0.bRisky) ) System.out.println("VULMOVE!"+moth.moveStrLong());
					if (((moth.p.iType == piece.BISHOP) || (moth.p.iType == piece.QUEEN)) && (moth.ytar == iUp) && (moth.xtar==xk) && (!m0.bRisky) ) System.out.println("VULMOVE!"+moth.moveStrLong());
					if ((moth.p.iType == piece.KNIGHT) && ((moth.ytar == i2Up) || (moth.ytar == i2D) ) && (moth.xtar==xk) && (!m0.bRisky)) System.out.println("VULMOVE!"+moth.moveStrLong());
					if (((iColor == piece.BLACK) && (iUp < yk) ) || (((iColor == piece.WHITE) && (iUp > yk) )))
					{
						if ((moth.p.iType == piece.PAWN) && (moth.ytar == iUp) && (moth.xtar==xk) && (!m0.bRisky) ) System.out.println("VULMOVE!"+moth.moveStrLong());
					}
					
				}
			}
			if (Math.abs((double)m0.xtar-(double)m1.xtar) == Math.abs((double)m0.ytar-(double)m1.ytar)) System.out.print(" diagonal vulnerability!");
		}	
	}
	
}