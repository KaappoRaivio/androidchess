package kaappo.androidchess.askokaappochess;

import java.util.*;

public class piece
{
	int xk;
	int yk;
	public int iColor;
	int iType;
	int iLastMove;
	Vector mMoveVector;
	Vector mSuperVector;
	move mSkewerMove;
	int iPosCaptureCount;
	
	int prev_xk;
	int prev_yk;
	
	boolean bThreat;
	boolean bProt;
	int iMinThreat;
	
	int iThreatCount;
	int iProtCount;
	
	int iThrDirFlags;
	
	boolean bPinned;  // for king only
	
	int iPinValue;			// other pieces pin this one
	int iPinDirection;
	
	int iPinningToDirection; 
	
	public static final int PAWN = 1;
	public static final int KING = 2;
	public static final int QUEEN = 3;
	public static final int BISHOP = 4;
	public static final int KNIGHT = 5;
	public static final int ROOK = 6;
	
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	
	static final int NO_DIR = -10;
	
	static final int TRY_FALSE = 1;
	static final int TRY_TRUE = 2;
	static final int TRY_EXTEND = 3;
	
	static final int EXTEND_SUPER = 1;
	static final int EXTEND_SKEWER = 2;
	
	piece (int x, int y, int col)
	{
		xk = x;
		yk = y;
		iColor = col;
		iLastMove = 0;
		
		prev_xk = 0;
		prev_yk = 0;
		
		iPosCaptureCount = 0;
		
		
		mMoveVector = null;
		mSuperVector = null;
		
		iPinningToDirection = NO_DIR;
		iPinValue = 0;
		iPinDirection = NO_DIR;
		
	}	

	int pvalue()
	{
		return 0;
	}
	
	String dumpchr()
	{
		return "x";
	}
	
	String sCoords()
	{
		return ""+(char)(xk+64)+yk;
	}
	
	String dumpStr()
	{
		String sRet;
		sRet = dumpchr() + sCoords();
		if (bPinned) sRet = sRet + "x";
		return sRet;
	}
	
	int xk ()
	{
		return xk;
	}

	int yk ()
	{
		return yk;
	}
	
	public Vector moveVector(chessboard cb) {
		return null;
	}
	
	void dumpmoveVector (Vector mv)
	{
		
		if (mv == null) 
		{
			System.out.println("null");
			return;
		}
		
		if (mv.size() == 0)
		{
			System.out.println("empty");
			return;
		}
		
		for (int i=0;i<mv.size();i++)
		{
			move m = (move)mv.elementAt(i);
			System.out.print("["+m.xtar + "," + m.ytar + ","  + m.bCapture + "]");
		}
		System.out.println();
	}
	
	//boolean trymoveat(int xk, int yk, Vector mv, chessboard cb, int iDir)
	int trymoveat(int xk, int yk, Vector mv, chessboard cb, int iDir)
	{
		//if ((iColor ==0 ) && (iType == piece.PAWN)) 
		//System.out.println("DBG:Piece at " + this.xk + "," + this.yk + " tries to move to " + xk + "," + yk);
		
		if ((xk < 1) || (xk >8) || (yk <1) || (yk > 8)) return TRY_FALSE;
		
		piece p = cb.blocks[xk][yk];
		
		/*
		if (iType == piece.KING) 
		{
			// check out if this square is under threat; if yes, return false -> KING can not move there
			//System.out.println("King at " + this.xk + "," + this.yk + " checks block " + xk + "," + yk);
			for (int i=1; i<=8;i++)
				for (int j=1;j<=8;j++)
				{
					piece px = cb.blocks[i][j];
					
					if (px != null) 
						if ((px.iColor != iColor) && (i != this.xk) && (j != this.yk))
						{
							Vector mvec = cb.blocks[i][j].moveVector(cb);
							for (int k = 0; k < mvec.size(); k++)
							{
								move mm = (move)mvec.elementAt(k);
								if ((mm.xtar == xk) && (mm.ytar == yk))
								{
									System.out.println("DGB:King move to " + xk + "," + yk + " invalidated.");
									return false;
								}
							}
						}
				}
		}
		*/
		
		
		if (p == null)
		{		
			//mv.addElement(new move(xk,yk,false));
			move mm = new move (xk,yk,false,0, this);
			//System.out.println("DBG160327:N move dump@piece.trymoveat(A):" + mm.moveStrLong());
			mm.analyzeCheck(this,cb);
			//System.out.println("DBG160327:N move dump@piece.trymoveat(A2):" + mm.moveStrLong());
			mv.addElement (mm);
			// non-capture move set bCheck $$$$ 140818
			/*
			if (iType == piece.KING)
			{
				System.out.println("DBG:Valid King move (king at "+this.xk+","+this.yk+") to " + xk + "," + yk + ".");
				System.out.println("DBG:Mv size now : " + mv.size());
			} 
			*/
			//System.out.println("DBG160327:N move dump@piece.trymoveat(B):" + mm.moveStrLong());
			return TRY_TRUE;
		}		
		
		if (iColor == p.iColor) 
		// found own piece, it's protected!
		{
			
			//if (!p.bProt) p.iProtCount++;
			p.iProtCount++;
			p.bProt = true;
			//if ((xk==5) && (yk==7)) System.out.println("DBG150304: PROT@5,7 inc! value now:" + p.iProtCount+ " by piece type:" + iType);
			
			if (((iType == BISHOP) || (iType == QUEEN)) && ((p.iType == BISHOP) || (p.iType == QUEEN) || (p.iType == PAWN)) && ((iDir %2) == 1))
			{
				boolean bPawn = false;
				if (p.iType == PAWN) 
				{
					if ((p.iColor == WHITE) && (iDir != 1) && (iDir != 7)) return TRY_FALSE;
					if ((p.iColor == BLACK) && (iDir != 3) && (iDir != 5)) return TRY_FALSE;
					//System.out.println("DBG150319: trymoveat: potential BQ over pawn extend @" + xk +","+ yk);
					bPawn = true;
				}
				
				extendMoves(cb,iDir,xk,yk,bPawn, EXTEND_SUPER);
				return TRY_EXTEND;
			}
			if (((iType == ROOK) || (iType == QUEEN)) && ((p.iType == ROOK) || (p.iType == QUEEN)) && ((iDir %2) == 0))
			{
				//System.out.println("DBG150319: trymoveat: potential RQ extend @" + xk +","+ yk);
				extendMoves(cb,iDir,xk,yk,false, EXTEND_SUPER);
				return TRY_EXTEND;
			}
			
			return TRY_FALSE;
		}
		else
		{
			if ((((iType == BISHOP) || (iType == QUEEN)) && ((iDir %2) == 1)) ||
				(((iType == ROOK) || (iType == QUEEN)) && ((iDir %2) == 0)))
			{
				//System.out.println("DBG150924: skewer check at "+xk+","+yk + " iDir:" + iDir);
				extendMoves(cb,iDir,xk,yk,false, EXTEND_SKEWER);
				
			}
			
		}
		
		move mm = new move (xk,yk,true, p.pvalue(), this);
		mm.analyzeCheck(this,cb);
		if (mm.bParallelWith(mSkewerMove))
		{			
			if (!mm.fully_equals(mSkewerMove)) mm.mSkewerMove = mSkewerMove;
			/*if (mm.fully_equals(mSkewerMove))
			{
				System.out.println("DBG151217: Trying to add skewer " + mSkewerMove.moveStr() + " to:" + mm.moveStr());
			}*/
			//System.out.println("DBG151022: skewer by parallelism: " + mm.moveStr() + " skew: " + mSkewerMove.moveStr());
		}
		mv.addElement(mm);
		
		//mv.addElement(new move(xk,yk,true));
		// capture move set bCheck $$$$ 140818
		// enemy piece, it's under threat
		p.bThreat = true;
		p.iThreatCount++;
		p.setThrDirFlags(this);
		if (p.iType == piece.KING)
		{
			//System.out.println("DBG 141028 KING UNDER THREAT!!!");
			if (p.iColor == piece.WHITE) cb.bWhiteKingThreat = true;
			else cb.bBlackKingThreat = true;
			//cb.dump();
			//System.out.println("DBG141028 KING UNDER THREAT DONE !!!!");
			//System.out.println("FLAGS:" + cb.bWhiteKingThreat +"," + cb.bBlackKingThreat);
		}
		if ((p.iMinThreat == 0) || (p.iMinThreat > this.pvalue())) p.iMinThreat = this.pvalue();
		
		return TRY_FALSE;
	}
	
	void addBishopMoves(Vector mv, chessboard cb)
	{
		int ret = TRY_TRUE;
		int lc = 1;
		
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk-lc,yk-lc,mv,cb,5);
			lc++;
		}
		//if (ret==TRY_EXTEND) System.out.println("Extend 5");
		lc = 1;
		ret = TRY_TRUE;
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk-lc,yk+lc,mv,cb,7);
			lc++;
		}
		//if (ret==TRY_EXTEND) System.out.println("Extend 7");
		lc = 1;
		ret = TRY_TRUE;
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk+lc,yk+lc,mv,cb,1);
			lc++;
		}
		//if (ret==TRY_EXTEND) System.out.println("Extend 1");
		lc = 1;
		ret = TRY_TRUE;
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk+lc,yk-lc,mv,cb,3);
			lc++;
		}
		//if (ret==TRY_EXTEND) System.out.println("Extend 3");
	}
	
	void addRookMoves(Vector mv, chessboard cb)
	{
		int ret = TRY_TRUE;
		int lc = 1;
		
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk,yk-lc,mv,cb,4);  // was 0 ??
			lc++;
		}
		lc = 1;
		ret = TRY_TRUE;
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk-lc,yk,mv,cb,6);
			lc++;
		}
		lc = 1;
		ret = TRY_TRUE;
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk,yk+lc,mv,cb,0);   // was 4
			lc++;
		}
		lc = 1;
		ret = TRY_TRUE;
		while ((lc < 8) && (ret==TRY_TRUE))
		{
			ret = trymoveat(xk+lc,yk,mv,cb,2);
			lc++;
		}
	}
	
	Vector threatVector(chessboard cb)
	{
		return moveVector(cb);
	}
	
	boolean couldhit( piece p)
	{
		boolean bFound = false;
		
		Vector oldtv = mMoveVector;
		mMoveVector = null;
		
		chessboard eb = new chessboard();
		mMoveVector = moveVector(eb);
		
		for (int i=0; i<mMoveVector.size();i++)
		{
			move m = (move)mMoveVector.elementAt(i);
			if ((m.xtar == p.xk) && (m.ytar == p.yk)) bFound = true;
		}
		
		//System.out.println("Could hit called " + xk + "," + yk + "to " + p.xk + "," + p.yk);
		
		mMoveVector = oldtv;
		
		return bFound;
	}
	
	boolean couldhit( int xt, int yt)
	{
		boolean bFound = false;
		
		Vector oldtv = mMoveVector;
		mMoveVector = null;
		
		chessboard eb = new chessboard();
		mMoveVector = moveVector(eb);
		
		for (int i=0; i<mMoveVector.size();i++)
		{
			move m = (move)mMoveVector.elementAt(i);
			if ((m.xtar == xt) && (m.ytar == yt)) bFound = true;
		}
		
		//System.out.println("Could hit called " + xk + "," + yk + "to " + xt + "," + yt);
		
		mMoveVector = oldtv;
		
		return bFound;
	}
	
	void validatePinMoves(king k, chessboard cb)
	{
		Vector v = mMoveVector;
		
		//System.out.println("DBG150603: piece.validatePinMoves enter: ("+xk+","+yk+")");
		
		for (int i=0;i<v.size();i++)
		{
			move m = (move)v.elementAt(i);
			
			piece pt = cb.blocks[m.xtar][m.ytar];
			
			bPinned = true;
			
			if (((pt == null) || (pt.iType != KING)) && (!moveinline(m,k)))
			{
				v.remove(i);
				i--;
				//System.out.println("DBG150207: piece.validatePinMoves(): RPM removed move " + m.moveStr() + " due to pinning. bPinned=" + bPinned);
				piece ptarg = cb.blocks[m.xtar][m.ytar];
				if (ptarg != null)
				{
					//System.out.println("DBG150207: piece.validatePinMoves() ptarg iThreatCount=" + ptarg.iThreatCount + " bThreat=" + ptarg.bThreat);
					if (ptarg.iColor != m.p.iColor) ptarg.iThreatCount--;
					if (ptarg.iThreatCount == 0) ptarg.bThreat = false;
				}
				else
				{
					if (iColor == piece.WHITE)
					{
						cb.iWhiteStrike[m.xtar][m.ytar]--;
					}
					else
					{
						cb.iBlackStrike[m.xtar][m.ytar]--;
					}
				}

			} 
		}
		
		if ((mSuperVector != null) && (mSuperVector.size() != 0))
		{
			//System.out.println("DBG150320: bValidatePin : supermoves: " + mSuperVector.size());
			for (int i=0;i<mSuperVector.size();i++)
			{
				move m = (move)mSuperVector.elementAt(i);
				//System.out.println("DBG150320: pinned supermove to:" + m.xtar + "," + m.ytar);
				if (iColor == piece.WHITE)
				{
					cb.iWhiteStrike[m.xtar][m.ytar]--;
				}
				else
				{
					cb.iBlackStrike[m.xtar][m.ytar]--;
				}
			}
		}
		//System.out.println("ValidatePin before decp");
		bDecrementProt(cb);
		//System.out.println("ValidatePin Leave, after decp");
		
	}
	
	boolean moveinline(move m, piece p)
	{
		if (xk == p.xk)
		{
            return m.xtar == xk;
		}
		else if (yk == p.yk)
		{
            return m.ytar == yk;
		}
		
		//System.out.println("moveinline called from "+ xk+","+ yk+ " & "+ m.xtar +"," +m.ytar + "  &   " + p.xk +"," + p.yk);
		
		int xdiff = p.xk - xk;
		int ydiff = p.yk - yk;
		
		int mxdiff = xk - m.xtar;
		int mydiff = yk - m.ytar;
		/*
		System.out.println("xdiff:" + xdiff);
		System.out.println("ydiff:" + ydiff);
		
		System.out.println("mxdiff:" + mxdiff);
		System.out.println("mydiff:" + mydiff);
		*/
		/*if ((Integer.signum(xdiff) != Integer.signum(mxdiff)) || (Integer.signum(ydiff) != Integer.signum(mydiff))) return false;
		// Wrong condition, debugged 140205
		*/
		
		if ((ydiff == 0) && (mydiff != 0)) return false;
		if ((mydiff == 0) && (ydiff != 0)) return false;
		
		if ((xdiff/ydiff) != (mxdiff/mydiff)) return false;

        return (Math.abs(xdiff) == Math.abs(ydiff)) && (Math.abs(mxdiff) == Math.abs(mydiff));
    }
	
	boolean canReach(int xk, int yk, piece k, chessboard cb)
	{
		return false;
	}
	
	boolean canReachCoord(int x1,int y1,int x2, int y2, chessboard cb)
	{
		return false;
	}
	
	boolean revealsChecker(king k, chessboard cb, move m)
	{
		//if ((xk == 4) && (iType == PAWN)) System.out.print("DBG141210: revcheck with d pawn");
		
		if (k == null) return false;
		
		//System.out.println("DBG160411: piece.revealsChecker for " + m.moveStr());
		
		if (this.xk == k.xk)
		{
			if (m.xtar == this.xk) return false;
			
			int js = Integer.signum(this.yk-k.yk);
			int x,y;
			
			y = this.yk+js;
			x = this.xk;
			while ((y>0) && (y<9))
			{
				piece p = cb.blocks[x][y];
				if (p != null)
				{
					if ((p.iColor == this.iColor) &&
					   ((p.iType == piece.ROOK) || (p.iType == piece.QUEEN)))
					{
						//return true;
						y = this.yk-js;
						while ((y>0) && (y<9))
						{
							p = cb.blocks[x][y];
							if ((p != null) && (p.iType == piece.KING) && (p.iColor != this.iColor)) return true;
							if ((p != null) && (p.iType != piece.KING)) return false;
						
							y = y-js;
						}
					}
					else return false;
				}
				y = y+js;
			}
		}
		//if ((xk == 4) && (iType == PAWN)) System.out.print("A");
		
		if (this.yk == k.yk)
		{
			if (m.ytar == this.yk) return false;
			
			int is = Integer.signum(this.xk-k.xk);
			int x,y;
			//System.out.println("Revc: ky:" + k.yk + " this " + this.xk +","+this.yk);
			x = this.xk+is;
			y = this.yk;
			
			while ((x>0) && (x<9))
			{
				piece p = cb.blocks[x][y];
				if (p != null)
				{
					if ((p.iColor == this.iColor) &&
					   ((p.iType == piece.ROOK) || (p.iType == piece.QUEEN)))
					{
						//return true;
						x = this.xk-is;
						while ((x>0) && (x<9))
						{
							p = cb.blocks[x][y];
							if ((p != null) && (p.iType == piece.KING) && (p.iColor != this.iColor)) return true;
							if ((p != null) && (p.iType != piece.KING)) return false;
							
							x = x -is;
						}
						
					}
					else return false;
					
				}
				x = x + is;
			}   
		}
		//if ((xk == 4) && (iType == PAWN)) System.out.print("B");
		
		if (Math.abs(this.xk-k.xk) == Math.abs(this.yk-k.yk))
		{
			//System.out.println("revcheck dbg c! from x: " + this.xk + "," + this.yk);
			
			int js = Integer.signum(this.yk-k.yk);
			int is = Integer.signum(this.xk-k.xk);
			
			int x,y;
			x = this.xk+is;
			y = this.yk+js;
			
			while ((x>0) && (x<9) && (y>0) && (y<9))
			{
				piece p = cb.blocks[x][y];
				if (p != null)
				{
					//System.out.println("revcehck c: x:" + x + " y: " + y);
					if ((p.iColor == this.iColor) &&
					   ((p.iType == piece.BISHOP) || (p.iType == piece.QUEEN)))
					{ 
						//return true;
						x = this.xk-is;
						y = this.yk-js;
						while ((x>0) && (x<9))
						{
							p = cb.blocks[x][y];
							if ((p != null) && (p.iType == piece.KING) && (p.iColor != this.iColor)) return true;
							if ((p != null) && (p.iType != piece.KING)) return false;
							
							x = x - is;
							y = y - js;
						}
						
					}
					else return false;
				}
				x = x + is;
				y = y + js;
			}
		}
		//if ((xk == 4) && (iType == PAWN)) System.out.print("C");
		
		return false;
	}
	
	piece canReachBrotherPiece(chessboard cb)
	{
		return null;
	}
	
	static boolean directlyBetween(int x0, int y0, int x1, int y1, int x2, int y2)
	{
		//System.out.println("DBG150930:piece.directlyBetween("+x0+","+y0+","+x1+","+y1+","+x2+","+y2+")");
		if ((x0==x2) && (x1 != x0)) return false;
		if ((y0==y2) && (y1 != y0)) return false;
		
		if ((x0==x2) && (x1 == x0))
		{
			int ymin = Math.min(y0,y2);
			int ymax = Math.max(y0,y2);
            return (y1 > ymin) && (y1 < ymax);
		}
		
		if ((y0==y2) && (y1 == y0))
		{
			int xmin = Math.min(x0,x2);
			int xmax = Math.max(x0,x2);
            return (x1 > xmin) && (x1 < xmax);
		}
		
		if (Math.abs(x1-x0) != Math.abs(y1-y0)) return false;
		if (Math.abs(x1-x2) != Math.abs(y1-y2)) return false;
		
		if (Math.signum(x1-x0) != Math.signum(x2-x1)) return false;
        return !(Math.signum(y1 - y0) != Math.signum(y2 - y1));
    }
	
	boolean bDecrementProt(chessboard cb)
	{
		//System.out.println("DBG150207: piece.bDecrementProt() call for " + dumpchr() +" at ("+xk+","+yk+")");
		
		int movX[] = {0,1,1,1,0,-1,-1,-1};
		int movY[] = {1,1,0,-1,-1,-1,0,1};
		
		for (int iDir = 0; iDir < 8;iDir++)
		{
			if ((iType == piece.QUEEN) ||
			    ((iType == piece.ROOK) && ((iDir % 2) == 0)) ||
				((iType == piece.BISHOP) && ((iDir %2) == 1)))
			
			{
				for (int iStep = 1; iStep <= 7; iStep++)   // unbelievable, equality missing until 140412!!! 
				{
					int newX = xk + iStep * movX[iDir];
					int newY = yk + iStep * movY[iDir];
				
					if ((newX < 1) || (newX > 8) || (newY < 1) || (newY > 8)) break;
				
					piece p = cb.blocks[newX][newY];
					
					if (p!=null)
					{
						//if ((newX==5) && (newY==6)) System.out.println("DBG150603. Called here!!!");
						bDecrementProtOfPiece(cb,newX,newY,iColor);
						break;
					}
				}
			}
		}
		return false;
	}
	
	boolean bDecrementProtOfPiece(chessboard cb, int xk, int yk, int iColor)
	{
		//System.out.println("DBG150207:bDecrementProtOfPiece("+xk+","+yk+") enter");
		if ((xk<1) || (xk > 8) || (yk <1) || (yk >8)) return false;
		piece p = cb.blocks[xk][yk];
		//System.out.println("DBG150207:bDecrementProtOfPiece() piece found.");
		if ((p== null) || (p.iColor != iColor)) return false;
		p.iProtCount--;
		/*if ((xk==5) && (yk == 6))
		{
			System.out.println("DBG150207:bDecrementProtOfPiece(5,6) p.iProtCount=" + p.iProtCount);
			//new Exception().printStackTrace();
		}*/
		//System.out.println("DBG150207:bDecrementProtOfPiece() decr.prot.");
		return true;
	}
	
	void extendMoves(chessboard cb, int iDir, int sx, int sy, boolean bPawn, int iMode)
	{
		int movX[] = {0,1,1,1,0,-1,-1,-1};
		int movY[] = {1,1,0,-1,-1,-1,0,1};
		
		int i=1;
		int nx,ny;
		boolean bContinue = true;
		
		//System.out.println("DBG160124: piece.extendMoves: sx="+sx + " sy="+sy + " iDir="+iDir + " iType=" +iType + " xk:" + xk+ " yk:"+yk+ " iMode:" + iMode);
		
		nx = sx + i*movX[iDir];
		ny = sy + i*movY[iDir];
		
		
		
		while (bContinue && (nx>0) && (nx<9) && (ny>0) && (ny<9))
		{
			if ((nx==xk) && (ny==yk))
			{
				System.out.println("piece.extendMoves(): extending to self? FATAL?");
				throw new RuntimeException("piece.extendMoves(): extending to self? FATAL?");
			}
			
			
			piece p = cb.blocks[nx][ny];
			
			if (p==null)
			{
				//System.out.println("DBG150319: piece.extend(): null at " + nx +"," + ny);
				move mm = new move (nx,ny,false,0, this);
				if (iMode == EXTEND_SUPER)
				{
					if (mSuperVector == null) mSuperVector = new Vector();
					mSuperVector.addElement(mm);
				}
				
			}
			else if (p.iColor == iColor)
			{
				//System.out.println("DBG150319: piece.extend(): own piece " + nx +"," + ny);
				
				//if ((nx==5) && (ny ==7)) System.out.println("DBG160124 @5,7:extendMoves i="+i);
				
				if (i!=1) p.iProtCount++;
				
				//if ((nx==5) && (ny ==7)) System.out.println("DBG160124 @5,7:extendMoves: p.iProtCount=" + p.iProtCount);
				
				if (((iDir % 2) == 0) && (p.iType != QUEEN) && (p.iType != ROOK)) return;
				if (((iDir % 2) == 1) && (p.iType != QUEEN) && (p.iType != BISHOP)) return;
				
			}
			else
			{
				if (iMode == EXTEND_SUPER)
				{
					p.iThreatCount++;
					move mm = new move (nx,ny,true, p.pvalue(), this);
					if (mSuperVector == null) mSuperVector = new Vector();
					mSuperVector.addElement(mm);
					//System.out.println("DBG150319: piece.extend(): enemy piece " + nx +"," + ny);
					return;
				}
				else
				{
					piece p0 = cb.blocks[sx][sy];
					
					/*
					System.out.println("DBG150924: SKEW piece at :" + nx + "," + ny + " from : " + sx + "," + sy + " by type: " + iType + " s1type:" + p0.iType + " s2type: " + p.iType);
					
					System.out.println("skewering piece type: " + iType);
					System.out.println(" skewered 1 piece type: " + p0.iType);
					System.out.println(" skewered 2 piece type " + p.iType);
					*/
					
					
					if ((p.pvalue() > p0.iPinValue) && (p.pvalue() < 1000))
					{
						p0.iPinValue = p.pvalue();
						p0.iPinDirection = getDir(xk,yk,p0.xk,p0.yk);
						//System.out.println("DBG160124: set pinned piece @:"+p0.xk+","+p0.yk+ " by value:"+p0.iPinValue);
						this.iPinningToDirection = iDir;
					}
					
					move mm = new move (nx,ny,true, p.pvalue(), this);
					//System.out.println("DBG150924: extended skewer:" + mm.moveStr());
					//mm.analyzeRisk(this,cb);
					
					//System.out.println("DBG151022:Skewermove is:" + mm.moveStrLong());
					mSkewerMove = mm;
					return;
				}
				
			}
			
			
			i=i+1;
			if (bPawn) i = 8;
			nx = sx + i*movX[iDir];
			ny = sy + i*movY[iDir];
			
		}
		
		
	}
	
	void setThrDirFlags(piece thrpiece)
	{
	}
	
	boolean bEscMoveisSafe(move esm)
	{
		return true;
	}
	
	static int getDir(int sx, int sy, int tx, int ty)
	{
		if (sx==tx)
		{
			if (ty>sy) return 0;
			else if (ty<sy) return 4;
			else return -1;
		}
		if (sy==ty)
		{
			if (tx>sx) return 2;
			else if (tx<sx) return 6;
			else return -1;
		}
		if (Math.abs(sx-tx) != Math.abs(sy-ty)) return -1;
		if ((tx>sx) && (ty > sy)) return 1;
		if ((tx>sx) && (ty < sy)) return 3;
		if ((tx<sx) && (ty < sy)) return 5;
		if ((tx<sx) && (ty > sy)) return 7;
		return -1;
		
	}
	
	boolean bPotentiallyReachable(int x1, int y1, int x2, int y2)
	{
		return false;
	}
}












