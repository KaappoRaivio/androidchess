package kaappo.androidchess.askokaappochess;

import java.util.*;

public class queen extends piece
{
	queen (int x, int y, int col)
	{
		super (x,y,col);
		iType = piece.QUEEN;		
	}
	
	int pvalue()
	{
		return 9;
	}
	
	String dumpchr()
	{
		if (iColor == WHITE) return "q";
		else return "Q";
	}
	
	public Vector moveVector (chessboard cb)
	{
		/*if (iColor == piece.WHITE) DBG
		{
			System.out.print("moveVector white queen called :");
			if (mMoveVector == null) System.out.println(" null");
			else System.out.println(mMoveVector.size());
		}*/
		
		if (mMoveVector != null) return mMoveVector;
		
		Vector mv = new Vector();
		
		addBishopMoves(mv,cb);
		addRookMoves(mv,cb);
		
		mMoveVector = mv;
		return mv;
	}
	
	boolean canReach(int xk, int yk, piece k, chessboard cb)
	{
		if (k==null) return false;
		
		if (xk == k.xk)
		{
			for (int j = Math.min(yk,k.yk) + 1; j < Math.max(yk,k.yk); j++)
			{
				if (cb.blocks[xk][j] != null) return false;
			}
			return true;
		}
		else if (yk == k.yk)
		{
			for (int i = Math.min(xk,k.xk) + 1; i < Math.max(xk,k.xk); i++)
			{
				if (cb.blocks[i][yk] != null) return false;
			}
			return true;
		}
		else if ((Math.abs(xk-k.xk)) == (Math.abs(yk-k.yk)))
		{
			int is = Integer.signum(k.xk-xk);
			int js = Integer.signum(k.yk-yk);
			
			int x,y;
			x = xk+is;
			y = yk+js;
			
			while ((x>0) && (x<9) && (y>0) && (y<9))
			{
				piece p = cb.blocks[x][y];
				if (p != null)
				{
                    return (x == k.xk) && (y == k.yk);
				}
				if ((x==k.xk) && (y==k.yk)) return true;
					x = x + is;
					y = y + js;
			}
			
			return true;
		}
		else return false;
		/*{
			// old faulty bishop canreach logic, should be removed
			for (int i=1;i<Math.abs(xk-k.xk);i++)
			{
				int ii = Math.min(xk,k.xk);
				int jj = Math.min(yk,k.yk);
				if (cb.blocks[ii+i][jj+i] != null) return false;
			}
			return true;
		}
		*/
		
	}
	
	void setThrDirFlags(piece thrpiece)
	{
		if ((thrpiece.iType == piece.PAWN) || (thrpiece.iType == piece.KNIGHT) || (thrpiece.iType == piece.KING)) return;
		
		int iThrDir = piece.getDir(thrpiece.xk,thrpiece.yk,xk,yk);
		
		if ((thrpiece.iType == piece.BISHOP) && (iThrDir%2 == 0)) return;
		if ((thrpiece.iType == piece.ROOK) && (iThrDir%2 == 1)) return;
		
		//System.out.print("queen.setThrDirFlags(): " +xk+"," + yk + "  ThrDir:" + iThrDir + " prioflags: " + iThrDirFlags);
		
		iThrDirFlags = iThrDirFlags | (int)(Math.pow(2,iThrDir));
		//System.out.println(" flags now:" + iThrDirFlags);
	}
	
	boolean bEscMoveisSafe(move esm)
	{
		int iEsmDir = piece.getDir(xk,yk,esm.xtar,esm.ytar);
		
		//System.out.println(esm.moveStr()+" dir:" + iEsmDir);
        return ((int) Math.pow(2, iEsmDir) & iThrDirFlags) == 0;
    }
	
	boolean bPotentiallyReachable(int x1, int y1, int x2, int y2)
	{
		if ((Math.abs(x1-x2)) == (Math.abs(y1-y2))) return true;
        return (x1 == x2) || (y1 == y2);
    }
}