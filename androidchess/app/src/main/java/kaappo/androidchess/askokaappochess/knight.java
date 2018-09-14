package kaappo.androidchess.askokaappochess;
import java.util.*;

public class knight extends piece
{	
	int iKnightCoupState = KNIGHT_KCS_NONE;
	public static final int KNIGHT_KCS_NONE = 0;
	public static final int KNIGHT_KCS_INIT = 1;
	public static final int KNIGHT_KCS_PROT = 2;     // value = 2?
	public static final int KNIGHT_KCS_UNPROT = 3;   // value = 4?
	public static final int KNIGHT_KCS_THREAT = 4;   // value = 4?
	
	knight (int x, int y, int col)
	{
		super (x,y,col);
		iType = piece.KNIGHT;		
	}
	
	int pvalue()
	{
		return 3;
	}
	
	String dumpchr()
	{
		if (iColor == WHITE) return "n";
		else return "N";
	}
	
	Vector moveVector (chessboard cb)
	{
		if (mMoveVector != null) return mMoveVector;
		//System.out.println("DBG 141221 Knight at " + xk +","+yk + " mv build."); 
		Vector mv = new Vector();
		
		trymoveat(xk-2,yk-1,mv,cb, piece.NO_DIR);
		trymoveat(xk-1,yk-2,mv,cb, piece.NO_DIR);
		trymoveat(xk+1,yk-2,mv,cb, piece.NO_DIR);
		trymoveat(xk+2,yk-1,mv,cb, piece.NO_DIR);
		trymoveat(xk+2,yk+1,mv,cb, piece.NO_DIR);
		trymoveat(xk+1,yk+2,mv,cb, piece.NO_DIR);
		trymoveat(xk-1,yk+2,mv,cb, piece.NO_DIR);
		trymoveat(xk-2,yk+1,mv,cb, piece.NO_DIR);
		
		int iKCc = 0;
		for (int i=0;i<mv.size();i++)
		{
			move m = (move)mv.elementAt(i);
			//System.out.println("DBG160327:N move dump:" + m.moveStrLong());
			if (m.iCaptValue >= 5) iKCc++;
		}
		if (iKCc >= 2)
		{
			iKnightCoupState = KNIGHT_KCS_INIT;
			//System.out.println("DBG 141221 Knight at " + xk +","+yk + " has a coup.");
		}
		
		mMoveVector = mv;
		return mv;
	}
	
	boolean canReach(int xk, int yk, piece k, chessboard cb)
	{
		if (k==null) return false;
		
		if ((xk+2 == k.xk) && (yk+1 == k.yk)) return true;
		if ((xk+2 == k.xk) && (yk-1 == k.yk)) return true;
		if ((xk-2 == k.xk) && (yk+1 == k.yk)) return true;
		if ((xk-2 == k.xk) && (yk-1 == k.yk)) return true;
		
		if ((xk+1 == k.xk) && (yk+2 == k.yk)) return true;
		if ((xk+1 == k.xk) && (yk-2 == k.yk)) return true;
		if ((xk-1 == k.xk) && (yk+2 == k.yk)) return true;
        return (xk - 1 == k.xk) && (yk - 2 == k.yk);

    }
	
	piece canReachBrotherPiece(chessboard cb)
	{
		piece pb = null;
		
		pb = null;
		if ((xk>2) && (yk> 1)) pb = cb.blocks[xk-2][yk-1];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		pb = null;
		if ((xk>1) && (yk> 2)) pb = cb.blocks[xk-1][yk-2];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		pb = null;
		if ((xk<8) && (yk< 7)) pb = cb.blocks[xk+1][yk+2];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		pb = null;
		if ((xk<7) && (yk< 8)) pb = cb.blocks[xk+2][yk+1];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		pb = null;
		if ((xk>2) && (yk< 8)) pb = cb.blocks[xk-2][yk+1];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		pb = null;
		if ((xk>1) && (yk< 7)) pb = cb.blocks[xk-1][yk+2];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		pb = null;
		if ((xk<7) && (yk>1)) pb = cb.blocks[xk+2][yk-1];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		pb = null;
		if ((xk<8) && (yk>2)) pb = cb.blocks[xk+1][yk-2];
		if ((pb != null) && (pb.iType == piece.KNIGHT) && (pb.iColor == iColor)) return pb;
		
		return null;
	}
	
	void finish_KCSValue(chessboard cb)
	{
		if (iKnightCoupState == KNIGHT_KCS_NONE) return;
		
		//System.out.println("DBG 141221: Knight at " + xk +"," + yk + " finish_KCSValue() entry"); 
		
		int iUnProt = 0;
		for (int i=0;i<mMoveVector.size();i++)
		{
			move m = (move)mMoveVector.elementAt(i);
		
			if (m.iCaptValue >= 5) 
			{
				piece p = cb.blocks[m.xtar][m.ytar];
				if ((p == null) || (p.pvalue() < 5))
				{
					System.out.println("fatal error at knight.finish_KCSValue()");
					System.exit(0);
				}
				if ((!p.bProt) || (p.iType == piece.KING))
				{
					boolean bCheck = false;
					for (int j=0;j<p.moveVector(cb).size();j++)
					{
						Vector v = p.moveVector(cb);
						move mm=(move)v.elementAt(j);
						if (mm.isCheck() || mm.isRevCheck()) bCheck = true;
					}
					
					if (!bCheck) iUnProt++;
				}
			}
		}
		
		if (!this.bThreat)
		{
			if (iUnProt >= 2) iKnightCoupState = KNIGHT_KCS_UNPROT;
			else iKnightCoupState =  KNIGHT_KCS_PROT;
		}
		else
		{
			iKnightCoupState = KNIGHT_KCS_THREAT;
		}
		//System.out.println("DBG 141221: Knight at " + xk +"," + yk + " KCS : " +iKnightCoupState); 
	}
	
	boolean bDecrementProt(chessboard cb)
	{
		//System.out.println("DBG150207: KNIGHT DECREMENT PROT CALLED: xk:" + xk + " yk:"+ yk);
		boolean b;
		
		b=bDecrementProtOfPiece(cb,xk+2,yk+1,iColor);
		b=bDecrementProtOfPiece(cb,xk+2,yk-1,iColor);
		b=bDecrementProtOfPiece(cb,xk-2,yk+1,iColor);
		b=bDecrementProtOfPiece(cb,xk-2,yk-1,iColor);
		b=bDecrementProtOfPiece(cb,xk+1,yk+2,iColor);
		b=bDecrementProtOfPiece(cb,xk+1,yk-2,iColor);
		b=bDecrementProtOfPiece(cb,xk-1,yk+2,iColor);
		b=bDecrementProtOfPiece(cb,xk-1,yk-2,iColor);
		
		return false;
	}
	
	static int distanceToTarget(int x1,int y1,int x2, int y2)
	{
		int iDist[][] = {{0,3,2,3,2,3,4,5},
						 {3,2,1,2,3,4,3,4},
						 {2,1,4,3,2,3,4,5},
						 {3,2,3,2,3,4,3,4},
						 {2,3,2,3,4,3,4,5},
						 {3,4,3,4,3,4,5,4},
						 {4,3,4,3,4,5,4,5},
						 {5,4,5,4,5,4,5,6}};
						 
		int xDiff = Math.abs(x2-x1);
		int yDiff = Math.abs(y2-y1);
		
		return iDist[xDiff][yDiff];
	}

}