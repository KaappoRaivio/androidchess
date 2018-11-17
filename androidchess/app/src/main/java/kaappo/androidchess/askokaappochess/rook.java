package kaappo.androidchess.askokaappochess;

import java.util.*;

public class rook extends piece
{
	rook (int x, int y, int col)
	{
		super (x,y,col);
		iType = piece.ROOK;		
	}
	
	int pvalue() 
	{
		return 5;
	}
	
	String dumpchr()
	{
		if (iColor == WHITE) return "r";
		else return "R";
	}
	
	public Vector moveVector (chessboard cb)
	{
		if (mMoveVector != null) return mMoveVector;
		
		Vector mv = new Vector();
		
		addRookMoves(mv,cb);
		
		mMoveVector = mv;
		return mv;
		
	}
	
	boolean canReach(int xk, int yk, piece k, chessboard cb)
	{
		if (k==null) return false;
		//System.out.println("DBG:150207:Rook CR " + xk +"," + yk + " going to:" + k.xk + "," + k.yk);
		
		if (xk == k.xk)
		{
			//System.out.println("DBG:150207:Rook CR A"); 
			for (int j = Math.min(yk,k.yk) + 1; j < Math.max(yk,k.yk); j++)
			{
				if (cb.blocks[xk][j] != null) return false;
			}
			return true;
		}
		else if (yk == k.yk)
		{
			//System.out.println("DBG:150207:Rook CR B");
			for (int i = Math.min(xk,k.xk) + 1; i < Math.max(xk,k.xk); i++)
			{
				if (cb.blocks[i][yk] != null) return false;
			}
			return true;
		}
		else return false; // revealsChecker(k,cb);
	}
	
	piece canReachBrotherPiece(chessboard cb)
	{
		// not awfully necessary, only after somebody has promoted a bishop :)
		piece pb = null;
		
		for (int i=1;i<=7;i++)
		{
			if (xk+i > 8) break;
			pb = cb.blocks[xk+i][yk];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.ROOK)) return pb;
				else break;
			}
		}
		
		for (int i=1;i<=7;i++)
		{
			if (xk-i < 1) break;
			pb = cb.blocks[xk-i][yk];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.ROOK)) return pb;
				else break;
			}
		}
		
		for (int i=1;i<=7;i++)
		{
			if (yk+i > 8) break;
			pb = cb.blocks[xk][yk+i];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.ROOK)) return pb;
				else break;
			}
		}
		
		for (int i=1;i<=7;i++)
		{
			if (yk-i < 1) break;
			pb = cb.blocks[xk][yk-i];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.ROOK)) return pb;
				else break;
			}
		}
		
		return null;
	}
	
	boolean bPotentiallyReachable(int x1, int y1, int x2, int y2)
	{
        return (x1 == x2) || (y1 == y2);
    }
}