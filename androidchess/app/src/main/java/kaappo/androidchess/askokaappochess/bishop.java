package kaappo.androidchess.askokaappochess;


import java.util.*;

public class bishop extends piece
{
	bishop (int x, int y, int col)
	{
		super (x,y,col);
		iType = piece.BISHOP;		
	}

	int pvalue()
	{
		return 3;
	}	
	
	String dumpchr()
	{
		if (iColor == WHITE) return "b";
		else return "B";
	}
	
	Vector moveVector (chessboard cb)
	{
		if (mMoveVector != null) return mMoveVector;
		
		Vector mv = new Vector();
		boolean ret = true;
		int lc = 1;
		
		addBishopMoves(mv,cb);
		
		mMoveVector = mv;
		
		return mv;
	}
	
	boolean canReach(int xk, int yk, piece k, chessboard cb)
	{
		if (k==null) return false;
		//System.out.println("Bishop CR: " + k.iColor + " from " +k.xk + "," + k.yk + " to ( " + xk+"," + yk +")" );
		return canReachCoord(xk,yk,k.xk,k.yk,cb);
		/*
		boolean bRet = canReachCoord(xk,yk,k.xk,k.yk,cb);
		System.out.println("bRet:" + bRet);
		
		if ((Math.abs(xk-k.xk)) == (Math.abs(yk-k.yk)))
		{
			int is = Integer.signum(k.xk-xk);
			int js = Integer.signum(k.yk-yk);
			
			int x,y;
			x = xk+is;
			y = yk+js;
			
			while ((x>0) && (x<9) && (y>0) && (y<9))
			{
				//System.out.println("Bishop CR: " + x +","+y);
				piece p = cb.blocks[x][y];
				if (p != null)
				{
					if ((x==k.xk) && (y==k.yk)) 
					{
						if (!bRet) 
						{
							System.out.println("FailA");
							cb.dump();
							System.out.println("xk:" + xk + " yk:" + yk + " k.xk:" + k.xk + " k.yk:" + k.yk);
							System.exit(0);
						}
						return true;
					}
					else return false;
				}
				if ((x==k.xk) && (y==k.yk)) 
				{
					if (!bRet) 
					{
						System.out.println("FailB");
						System.exit(0);
					}
					return true;
				}
				x = x + is;
				y = y + js;
			}
		}
		*/
		
		/*
		if ((Math.abs(xk-k.xk)) == (Math.abs(yk-k.yk)))
		{
			for (int i=1;i<Math.abs(xk-k.xk);i++)
			{
				
				int ii = Math.min(xk,k.xk);
				int jj = Math.min(yk,k.yk);
				System.out.print("#"+i+"/"+(ii+i)+"/"+(jj+i)+"#");
				if (cb.blocks[ii+i][jj+i] != null) return false;
			}
			System.out.println("Bishop CR: RT");
			return true;
		}
		*/
		
		
		//System.out.println("RF2");
		//return revealsChecker(k,cb);
		//return false;
	}
	
	boolean canReachCoord(int x1,int y1,int x2, int y2, chessboard cb)
	{
		//System.out.println("Bishop CRC in:" + x1 +","+y1 + " to " + x2+","+y2);
		if ((Math.abs(x1-x2)) == (Math.abs(y1-y2)))
		{
			int is = Integer.signum(x2-x1);
			int js = Integer.signum(y2-y1);
			
			int x,y;
			x = x1+is;
			y = y1+js;
			
			while ((x>0) && (x<9) && (y>0) && (y<9))
			{
				//System.out.println("Bishop CRC: " + x +","+y);
				piece p = cb.blocks[x][y];
				if (p != null)
				{
					//System.out.println("Bishop CRC:  ret false AA");
					return (x == x2) && (y == y2);
				}
				if ((x==x2) && (y==y2)) return true;
				x = x + is;
				y = y + js;
			}
		}
		//System.out.println("Bishop CRC:  ret false A");
		return false;
	}
	
	piece canReachBrotherPiece(chessboard cb)
	{
		// not awfully necessary, only after somebody has promoted a bishop :)
		piece pb = null;
		
		for (int i=1;i<=7;i++)
		{
			if ((xk+i > 8) || (yk+i > 8)) break;
			pb = cb.blocks[xk+i][yk+i];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.BISHOP)) return pb;
				else break;
			}
		}
		
		for (int i=1;i<=7;i++)
		{
			if ((xk-i < 1) || (yk-i < 1)) break;
			pb = cb.blocks[xk-i][yk-i];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.BISHOP)) return pb;
				else break;
			}
		}
		
		for (int i=1;i<=7;i++)
		{
			if ((xk-i < 1) || (yk+i > 8)) break;
			pb = cb.blocks[xk-i][yk+i];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.BISHOP)) return pb;
				else break;
			}
		}
		
		for (int i=1;i<=7;i++)
		{
			if ((xk+i > 8) || (yk-i < 1)) break;
			pb = cb.blocks[xk+i][yk-i];   
			if (pb != null)
			{
				if ((pb.iColor == iColor) && (pb.iType == piece.BISHOP)) return pb;
				else break;
			}
		}
		
		return null;
	}
	
	boolean bPotentiallyReachable(int x1, int y1, int x2, int y2)
	{
		//System.out.println("DBG160125 bishop.bPotentiallyReachable("+x1+","+y1+" to " +x2+","+y2+"):");
		return (Math.abs(x1 - x2)) == (Math.abs(y1 - y2));
	}
	
}