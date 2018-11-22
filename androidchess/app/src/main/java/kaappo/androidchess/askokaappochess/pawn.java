package kaappo.androidchess.askokaappochess;


import java.util.*;

public class pawn extends Piece
{
	//static int pmvc_b2 = 0;
	
	pawn (int x, int y, int col)
	{
		super (x,y,col);
		iType = Piece.PAWN;
	}
	
	int pvalue()
	{
		return 1;
	}
	
	String dumpchr()
	{
		if (iColor == WHITE) return "p";
		else return "P";
	}
	
	public Vector moveVector(chessboard cb)
	{
		if (mMoveVector != null) return mMoveVector;
		
		//System.out.println("pawn at " + xk + "," + yk + " moveVector(cb) called");
		
		Vector mv = new Vector();
		int ny =0;
		int ny2=0;
		
		/*if ((iColor ==0) && (xk==4) && (yk==4)) 
		{
			//pmvc_b2++;
			System.out.println("DBG: (141106) Pawn D4 move vector called. " );
		}
		*/
		
		if (iColor == Piece.WHITE) ny = yk+1;
		else ny = yk - 1;

		//System.out.println("pawn.moveVector(cb) : " + xk + "," + ny);
		Piece p = cb.blocks[xk][ny];
		if (p == null)
		{
			move m = new move(xk,ny,false,0, this);
			// $$$$ add bCheck 140818
			m.analyzeCheck(this,cb);
			mv.addElement(m);
			setPawnFork(m,cb);
			
			//System.out.println("added " + xk + "," + ny);
		}
		//else System.out.println("Piece is not null at:" + xk + "," + ny);
		
		if ((yk == 2) && (iColor== Piece.WHITE)) ny2 = yk+2;
		if ((yk == 7) && (iColor== Piece.BLACK)) ny2 = yk-2;
		
		if ((ny2 != 0) && (p == null))
		{
			p = cb.blocks[xk][ny2]; 
			if (p == null)
			{
				move m = new move(xk,ny2,false,0, this);
				// $$$$ add bCheck 140818
				m.analyzeCheck(this,cb);
				mv.addElement(m);
				setPawnFork(m,cb);
				//System.out.println("added " + xk + "," + ny2);
			}
		}
		
		p = cb.blocks[xk-1][ny];
		if ((p != null) && (p.iColor != iColor)) 
		{
			move m = new move(xk-1,ny,true,p.pvalue(), this);
			m.analyzeCheck(this,cb);
			// $$$$ add bCheck 140818
			mv.addElement(m);
			setPawnFork(m,cb);
			p.bThreat=true;
			p.iThreatCount++;
			
			if ((p.iMinThreat == 0) || (p.iMinThreat > this.pvalue())) p.iMinThreat = this.pvalue();
			
			if (p.iType == Piece.KING)
			{
				if (p.iColor == Piece.WHITE) cb.bWhiteKingThreat = true;
				else cb.bBlackKingThreat = true;
			}
		}
		if ((p!=null) && (p.iColor == iColor))
		{
			//if ((xk==6) && (ny ==6)) System.out.println("DBG150603: enter: pawn@5,6 iProtCount++ value now: " + p.iProtCount);
			//if (!p.bProt) p.iProtCount++;
			p.iProtCount++;
			p.bProt = true;
			//if ((xk==6) && (ny ==6)) System.out.println("DBG150603: pawn@5,6, protCount now: " + p.iProtCount);
		}
		
		if (xk <= 7)
		{
			p = cb.blocks[xk+1][ny];
			if ((p != null) && (p.iColor != iColor)) 
			{
				move m = new move(xk+1,ny,true,p.pvalue(), this);
				// $$$$ add bCheck 140818
				m.analyzeCheck(this,cb);
				mv.addElement(m);
				setPawnFork(m,cb);
				p.bThreat=true;
				p.iThreatCount++;
				if ((p.iMinThreat == 0) || (p.iMinThreat > this.pvalue())) p.iMinThreat = this.pvalue();
				
				if (p.iType == Piece.KING)
				{
					if (p.iColor == Piece.WHITE) cb.bWhiteKingThreat = true;
					else cb.bBlackKingThreat = true;
				}
			}
			if ((p!=null) && (p.iColor == iColor))
			{
				//if (!p.bProt) 
				p.iProtCount++;
				p.bProt = true;
			}
		}
		
		
		if ((p != null) && (p.iColor == iColor)) 
		{
			
			//if (!p.bProt) 
			p.iProtCount++;
			p.bProt = true;
		}
		
		// check the availability of en passant 
		// last move from cb, if it is enemy pawn by 2 steps to same yk and xk = +-1,
		//	we can do en passant -> one row behind enemy pawn
		//	new move with capture = true
		
		if ((((yk ==5) && (iColor == Piece.WHITE)) || ((yk == 4) && (iColor == Piece.BLACK))) && (cb.lastmoveString() != null))
		{
			//System.out.println("EPC check active for " + xk + "," + yk + " last move " + cb.lastmoveString());
			String sLast = cb.lastmoveString();
			int ly2 = sLast.charAt(3)-48;
			int lx2 = sLast.charAt(2)-64;
			int ly1 = sLast.charAt(1)-48;
			
			if (cb.blocks[lx2][ly2] != null)
			{
				Piece pe = (Piece)cb.blocks[lx2][ly2];
				if ((pe.iType == Piece.PAWN) && (pe.iColor != iColor) && (Math.abs(ly1-ly2) == 2) && (Math.abs(xk-lx2) == 1))
				{
					int ty = -1;
					if (iColor == Piece.WHITE) ty = 6 ;
					else ty = 3;
					
					//System.out.println("DBG En passant possible! col:" + iColor +"  (" + xk + "," + yk+ ") to " + lx2 +"," + ty +")" );
					//cb.dump();
					
					move m = new move (lx2,ty,true,pe.pvalue(), this);
					// $$$$ add bCheck 140818
					m.analyzeCheck(this,cb);
					mv.addElement(m);
					setPawnFork(m,cb);
					//System.out.println("DBG, move vector @ enpassant");
					//dumpmoveVector(mv);
				}
			}
	
		}
		
		//System.out.print("Pawn (Col:" + iColor + ") move vector: yn = " + ny + "."); 
		//dumpmoveVector(mv);
		
		
		if (((iColor == Piece.WHITE) && (ny == 8)) ||
			((iColor == Piece.BLACK) && (ny == 1))) mv = addUnderPromotions(mv);
		 
		
		//if ((iColor ==0) && (xk==4) && (yk==4)) 
		
		mMoveVector = mv;
		return mv;
		
	}
	
	Vector addUnderPromotions(Vector v)
	{
		Vector retv = new Vector();
		
		for (int i=0;i<v.size();i++)
		{
			move m=(move)v.elementAt(i);
			move mn = m.copy();
			mn.iPromTo = Piece.QUEEN;
			retv.addElement(mn);
			mn = m.copy();
			mn.iPromTo = Piece.ROOK;
			retv.addElement(mn);
			mn = m.copy();
			mn.iPromTo = Piece.BISHOP;
			retv.addElement(mn);
			mn = m.copy();
			mn.iPromTo = Piece.KNIGHT;
			retv.addElement(mn);
		}
		return retv;
	}
	
	Vector threatVector(chessboard cb)
	{
		Vector tv = new Vector();
		Piece p = null;
		
		int ny;
		
		//if (iColor == piece.BLACK) System.out.println("DBG: pawn.threatVector() enter :" + xk +"," + yk);
		
		if (iColor == Piece.WHITE) ny = yk+1;
		else ny = yk - 1;

		if ((ny > 0) && (ny < 9))
		{
			p = cb.blocks[xk-1][ny];
			if (p == null) 
			{
				move m = new move (xk-1,ny,false,0, this);
				tv.addElement(m);
			}
			else if (p.iColor == iColor)
			{
				if (!p.bProt) p.iProtCount++;
				p.bProt = true;
				
			}

			if (xk<=7) 
			{
				p = cb.blocks[xk+1][ny];
				if (p == null) 
				{
					move m = new move (xk+1,ny,false,0, this);
					tv.addElement(m);
				}
				else if (p.iColor == iColor) 
				{
					if (!p.bProt) p.iProtCount++;
					p.bProt = true;
				}
			}
		}
		
		//if (iColor == piece.BLACK) System.out.println("DBG: returning pawn.threatVector(), " + xk +"," + yk + " vector size : " + tv.size());
		return tv;
		
	}
	
	boolean canReach(int xk, int yk, Piece k, chessboard cb)
	{
		if (k==null) return false;
		
		//System.out.println("pawn.canReach " + xk + "," + yk + "  to king at: " + k.xk + "," + k.yk);
		
		if (Math.abs(xk-k.xk) != 1) return false; //revealsChecker(k,cb); 
		if (Math.abs(yk-k.yk) != 1) return false; //revealsChecker(k,cb);
		/*
		if ((k.iColor == piece.WHITE) && (yk>k.yk)) return true;
		if ((k.iColor == piece.BLACK) && (yk<k.yk)) return true;
		*/
		if ((iColor == Piece.WHITE) && (yk<k.yk)) return true;
        return (iColor == Piece.BLACK) && (yk > k.yk);
		
		//return revealsChecker(k,cb);
    }
	
	boolean bDecrementProt(chessboard cb)
	{
		boolean b;
		
		if (iColor == WHITE)
		{
			b=bDecrementProtOfPiece(cb,xk-1,yk+1,iColor);
			b=bDecrementProtOfPiece(cb,xk+1,yk+1,iColor);
		}
		else
		{
			b=bDecrementProtOfPiece(cb,xk-1,yk-1,iColor);
			b=bDecrementProtOfPiece(cb,xk+1,yk-1,iColor);
		}
		return false;
	}
	
	void validatePinMoves(king k, chessboard cb, boolean bEnpPinSkip)
	{
		//System.out.println("DBG 150530: pawn.validatePinMoves() at:" + xk + "," + yk);
		Vector v = mMoveVector;
		
		int lmx = ((int)cb.lm_vector.elementAt(2));
		int lmy = ((int)cb.lm_vector.elementAt(3));
		
		Piece pp = cb.blocks[lmx][lmy];
		if (pp.iType != Piece.PAWN)
		{
			System.out.println("FATAL ERROR at pawn.validatePinMoves()");
			throw new RuntimeException("FATAL ERROR at pawn.validatePinMoves()");
		}
		
		for (int i=0;i<v.size();i++)
		{
			move m = (move)v.elementAt(i);
			System.out.println(m.moveStr());
			if (m.xtar==lmx) v.remove(i);
			
		}
	}
	
	void setPawnFork(move m, chessboard cb)
	{
		//System.out.println("DBG160113: setPawnFork() called for: " + m.moveStr());
		
		if ((xk == 1) || (xk ==8)) return;
		int yy;
		if (iColor == Piece.WHITE) yy=yk+2;
		else yy=yk-2;
		
		if ((yy>8) || (yy<1)) return;
		
		//System.out.println("DBG160113: setPawnFork() at A for: " + m.moveStr() + "  xk=" + xk + " yy=" + yy );
		
		Piece p1 = cb.blocks[xk-1][yy];
		
		if ((p1 == null)  || (p1.iColor == iColor) || (p1.pvalue() < 3)) return;
		
		//System.out.println("DBG160113: setPawnFork() at B for: " + m.moveStr());
		
		Piece p2 = cb.blocks[xk+1][yy];
		if ((p2 == null)  || (p2.iColor == iColor) || (p2.pvalue() < 3)) return;
		
		//System.out.println("DBG160113: setPawnFork() SET for: " + m.moveStr());
		m.bPawnFork = true;
		
	}
	
	boolean bCoveredBehindbyQR(chessboard cb)
	{
		int iDir;
		
		if (iColor == Piece.BLACK) iDir = 1;
		else iDir = -1;
		
		int ii = yk+iDir;
		while ((ii>0) && (ii<9))
		{
			Piece px = cb.blocks[xk][ii];
			if (px !=null)
			{
				if (px.iColor != iColor) return false;
				if ((px.iType != Piece.ROOK) && (px.iType != Piece.QUEEN)) return false;
                return px.iPinValue <= 0;
            }
			
			ii = ii + iDir;
		}
		
		return false;
	}
	
}