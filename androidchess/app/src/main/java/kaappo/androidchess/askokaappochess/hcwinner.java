package kaappo.androidchess.askokaappochess;
import java.util.*;

public class hcwinner
{
	public static final int HCW_NONE = 0;
	public static final int HCW_ROOK_ALONE = 1;
	public static final int HCW_QUEEN_ALONE = 2;
	public static final int HCW_ROOK_AND_PAWNS = 3;
	public static final int HCW_QUEEN_AND_PAWNS = 4;
	
	public static final int HCW_CORNER_NONE = 0;
	public static final int HCW_CORNER_UPPER_RIGHT = 1;
	public static final int HCW_CORNER_LOWER_RIGHT = 2;
	public static final int HCW_CORNER_LOWER_LEFT = 3;
	public static final int HCW_CORNER_UPPER_LEFT = 4;
	
	public static final int HC_BONUS_FOR_QR_ALONE = 3;
	
	public static boolean bHcwEnabled = false;
	
	static int hcwinnable(chessboard cb, int iTurn)
	{
		if (!bHcwEnabled) return HCW_NONE;
		
		if (iTurn == Piece.WHITE)
		{
			if ((cb.iBlackPieceCount[Piece.QUEEN] != 0) ||
			    (cb.iBlackPieceCount[Piece.ROOK] != 0) ||
				(cb.iBlackPieceCount[Piece.BISHOP] != 0) ||
				(cb.iBlackPieceCount[Piece.KNIGHT] != 0) ||
				(cb.iBlackPieceCount[Piece.PAWN] != 0)) return HCW_NONE;
		
			if ((cb.iWhitePieceCount[Piece.QUEEN] == 0) &&
				(cb.iWhitePieceCount[Piece.ROOK] == 0)) return HCW_NONE;
				
			if (cb.iWhitePieceCount[Piece.ROOK] == 1)
			{
				if ((cb.iWhitePieceCount[Piece.QUEEN] != 0) ||
					(cb.iWhitePieceCount[Piece.BISHOP] != 0) ||
					(cb.iWhitePieceCount[Piece.KNIGHT] != 0)) return HCW_NONE;
				
				if (cb.iWhitePieceCount[Piece.PAWN] != 0) return HCW_ROOK_AND_PAWNS;
				
				return HCW_ROOK_ALONE;
			}
			
			if (cb.iWhitePieceCount[Piece.QUEEN] == 1)
			{
				if ((cb.iWhitePieceCount[Piece.ROOK] != 0) ||
					(cb.iWhitePieceCount[Piece.BISHOP] != 0) ||
					(cb.iWhitePieceCount[Piece.KNIGHT] != 0)) return HCW_NONE;
				
				if (cb.iWhitePieceCount[Piece.PAWN] != 0) return HCW_QUEEN_AND_PAWNS;
				
				return HCW_QUEEN_ALONE;
			}
		}
		else
		{
			if ((cb.iWhitePieceCount[Piece.QUEEN] != 0) ||
			    (cb.iWhitePieceCount[Piece.ROOK] != 0) ||
				(cb.iWhitePieceCount[Piece.BISHOP] != 0) ||
				(cb.iWhitePieceCount[Piece.KNIGHT] != 0) ||
				(cb.iWhitePieceCount[Piece.PAWN] != 0)) return HCW_NONE;
				
			if ((cb.iBlackPieceCount[Piece.QUEEN] == 0) &&
				(cb.iBlackPieceCount[Piece.ROOK] == 0)) return HCW_NONE;
				
			if (cb.iBlackPieceCount[Piece.ROOK] == 1)
			{
				if ((cb.iBlackPieceCount[Piece.QUEEN] != 0) ||
					(cb.iBlackPieceCount[Piece.BISHOP] != 0) ||
					(cb.iBlackPieceCount[Piece.KNIGHT] != 0)) return HCW_NONE;
				
				if (cb.iBlackPieceCount[Piece.PAWN] != 0) return HCW_ROOK_AND_PAWNS;
				
				return HCW_ROOK_ALONE;	
			}
			
			if (cb.iBlackPieceCount[Piece.QUEEN] == 1)
			{
				if ((cb.iBlackPieceCount[Piece.ROOK] != 0) ||
					(cb.iBlackPieceCount[Piece.BISHOP] != 0) ||
					(cb.iBlackPieceCount[Piece.KNIGHT] != 0)) return HCW_NONE;
				
				if (cb.iBlackPieceCount[Piece.PAWN] != 0) return HCW_QUEEN_AND_PAWNS;
				
				return HCW_QUEEN_ALONE;	
			}
		}
		
		return HCW_NONE;
	}
	
	static chessboard hcwinnerFAD(chessboard cb, int iTurn, movevalue mval, int iAlg)
	{
		
		int iHCW = hcwinnable(cb,iTurn);
		//System.out.println("DBG160402:hcwinner.hcwinnerFAD() called. iHCW:" + iHCW);
		if (iHCW == HCW_NONE) return null;
		
		switch (iHCW)
		{
			case HCW_ROOK_ALONE:
				return do_hcw_rookalone(cb,iTurn,mval, iAlg);
			
			case HCW_QUEEN_ALONE:
				return do_hcw_queenalone(cb,iTurn,mval, iAlg);
				
			case HCW_ROOK_AND_PAWNS:
				return do_hcw_rook_and_pawns(cb,iTurn,mval, iAlg);
			
			case HCW_QUEEN_AND_PAWNS:
				return do_hcw_queen_and_pawns(cb,iTurn,mval, iAlg);
			
			default:
				return null;
		}
	}
	
	static chessboard do_hcw_rookalone(chessboard cb, int iTurn, movevalue mval, int iAlg)
	{
		king kOwn, kEnemy;
		rook rr = null;
		int iNewRX, iNewRY;
		
		/*System.out.println("DBG160402: hcwinner.do_hcw_rookalone() starts.");
		cb.dump();
		*/
		
		if (iTurn == Piece.WHITE)
		{
			kOwn = cb.locateKing(Piece.WHITE);
			kEnemy = cb.locateKing(Piece.BLACK);
		}
		else 
		{
			kOwn = cb.locateKing(Piece.BLACK);
			kEnemy = cb.locateKing(Piece.WHITE);
		}
		
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
		{
			if (cb.blocks[i][j] != null)
			{
				Piece p = cb.blocks[i][j];
				if (p.iType == Piece.ROOK) rr = (rook)cb.blocks[i][j];
			}
		}
		//System.out.println("DBG160402: kown at" + kOwn.xk + "," + kOwn.yk+ " rook at " + rr.xk + "," + rr.yk);
		
		int iEKDistance = (int)Math.max(Math.abs((double)(rr.xk-kEnemy.xk)),Math.abs((double)(rr.yk-kEnemy.yk)));
		int iOKDistance = (int)Math.max(Math.abs((double)(rr.xk-kOwn.xk)),Math.abs((double)(rr.yk-kOwn.yk)));
		
		if (((kEnemy.xk==1) && (rr.xk == 2)) ||
				((kEnemy.xk==8) && (rr.xk == 7)) ||
				((kEnemy.yk==1) && (rr.yk == 2)) ||
				((kEnemy.yk==8) && (rr.yk == 7)))
		{
				//System.out.println("Close to MATE!");
				if (iEKDistance < 3)
				{
					Vector v = rr.moveVector(cb);
					move mdo = null;
					boolean bHor, bVer;
                    bVer = ((kEnemy.xk == 1) && (rr.xk == 2)) || ((kEnemy.xk == 8) && (rr.xk == 7));

                    bHor = ((kEnemy.yk == 1) && (rr.yk == 2)) || ((kEnemy.yk == 8) && (rr.yk == 7));
					
					//System.out.println("bhor:" + bHor + " bver:" + bVer);
					
					for (int i=0;i<v.size();i++)
					{
						move m = (move)v.elementAt(i);
						if (((bHor) && (m.ytar == rr.yk) && (Math.abs(m.xtar-kEnemy.xk) >= 4)) || 
						    ((bVer) && (m.xtar == rr.xk) && (Math.abs(m.ytar-kEnemy.yk) >= 4))) 
						{
							mdo = m;
							i=v.size();	
						}
					}
					//System.out.println("Doing move: " + mdo.moveStrLong());
					if (mdo == null) return null;
					
					cb.domove(rr,mdo,-1);
					if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
					else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
					return cb;
				}
				if 	(((kEnemy.xk==1) && (kOwn.xk==3) && (kEnemy.yk==kOwn.yk)) ||
					((kEnemy.xk==8) && (kOwn.xk==6) && (kEnemy.yk==kOwn.yk)) ||
					((kEnemy.yk==8) && (kOwn.yk==6) && (kEnemy.xk==kOwn.xk)) ||
					((kEnemy.yk==1) && (kOwn.yk==3) && (kEnemy.xk==kOwn.xk)))
				{
					int rtarx = rr.xk;
					int rtary = rr.yk;
					//System.out.println("DBG160402: Perhaps it's mateable from here????");
					if ((kEnemy.xk==1) && (rr.xk==2)) rtarx = 1;
					if ((kEnemy.xk==8) && (rr.xk==7)) rtarx = 8;
					if ((kEnemy.yk==1) && (rr.yk==2)) rtary = 1;
					if ((kEnemy.yk==8) && (rr.yk==7)) rtary = 8;
					
					Vector v = rr.moveVector(cb);
					move mdo = null;
					for (int i=0;i<v.size();i++)
					{
						move m = (move)v.elementAt(i);
						if ((m.xtar == rtarx) && (m.ytar== rtary)) 
						{
							mdo = m;
							i=v.size();	
						}
					}
					//System.out.println("Doing move: " + mdo.moveStrLong());
					if (mdo == null) return null;
					
					cb.domove(rr,mdo,-1);
					if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
					else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
					
					//cb.dump();
					//System.out.println("Is it checkmate?");
					return cb;
				}
				else
				{
					//System.out.println("DBG160402: Not directly mateable, we'll need to get king in place");
					int ktarx, ktary;
					ktarx=kOwn.xk;
					ktary=kOwn.yk;
					
					if (((kEnemy.xk==1) && (kOwn.xk==3) && (rr.xk==2)) ||
						((kEnemy.xk==8) && (kOwn.xk==6) && (rr.xk==7)))
					{
						//if ((Math.abs(kEnemy.yk-kOwn.yk)) == 1) ktary = kEnemy.yk;	
						ktary = kOwn.yk+(int)Math.signum(kEnemy.yk-kOwn.yk);
					}
						
					if (((kEnemy.yk==1) && (kOwn.yk==3) && (rr.yk==2)) ||
						((kEnemy.yk==8) && (kOwn.yk==6) && (rr.yk==7)))
					{
						//if ((Math.abs(kEnemy.xk-kOwn.xk)) == 1) ktarx = kEnemy.xk;
						ktarx = kOwn.xk+(int)Math.signum(kEnemy.xk-kOwn.xk);						
					}
					//System.out.println("King target set to:"+ ktarx +","+ktary);
					Vector v = kOwn.moveVector(cb);
					move mdo = null;
					for (int i=0;i<v.size();i++)
					{
						move m = (move)v.elementAt(i);
						if ((m.xtar == ktarx) && (m.ytar == ktary))
						{
							mdo = m;
							i=v.size();	
						}
					}
					if (mdo == null) return null;
					//System.out.println("Doing move: " + mdo.moveStrLong());
			
					cb.domove(kOwn,mdo,-1);
					if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
					else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
					return cb;
					
				}
				
		}
		
		if (iEKDistance < iOKDistance)
		{
			// move rook towards own king, goal iEKD == iOKD, choose bigger king area
			int rmx = 0;
			int rmy = 0;
			int iEKAreaX = -1;
			int iEKAreaY = -1;
			int inewrrx = -1;
			int inewrry = -1;
			if (rr.xk!=kOwn.xk)
			{
				if (rr.xk>kOwn.xk) rmx = -1;
				if (rr.xk<kOwn.xk) rmx=1;
				
				int iStep = 0;
				boolean bProceed = true;
				inewrrx = rr.xk+rmx*iStep;
				int iBestXMod = -1;
				
				while ((inewrrx >= 1) && (inewrrx <= 8) && bProceed)
				{
					iStep++;
					inewrrx = rr.xk+rmx*iStep;
					
					iEKDistance = (int)Math.max(Math.abs((double)(inewrrx-kEnemy.xk)),Math.abs((double)(rr.yk-kEnemy.yk)));
					iOKDistance = (int)Math.max(Math.abs((double)(inewrrx-kOwn.xk)),Math.abs((double)(rr.yk-kOwn.yk)));
					if (iOKDistance < iEKDistance)
					{
						bProceed = false;
						iBestXMod = inewrrx;
					}
					
				}
				
				switch (rmx)
				{
					case 1:
						if (rr.yk==kEnemy.yk) iEKAreaX = 8*(inewrrx-1);
						if (rr.yk>kEnemy.yk) iEKAreaX =  (rr.yk-1) * (inewrrx-1);
						if (rr.yk<kEnemy.yk) iEKAreaX =  (8-rr.yk) * (inewrrx-1);
						break;
						
					case -1:
						if (rr.yk==kEnemy.yk) iEKAreaX = 8*(inewrrx-1);
						if (rr.yk>kEnemy.yk) iEKAreaX =  (rr.yk-1) * (inewrrx-1);
						if (rr.yk<kEnemy.yk) iEKAreaX =  (8-rr.yk) * (inewrrx-1);
						break;	
				
				}
				//System.out.println("DBG160402: inewrrx:" + inewrrx); 
				if ((inewrrx <1) || (inewrrx > 8)) iEKAreaX = -1;
				
			}
			
			if (rr.yk!=kOwn.yk)
			{
				if (rr.yk>kOwn.yk) rmy = -1;
				if (rr.yk<kOwn.yk) rmy=1;
				
				int iStep = 0;
				boolean bProceed = true;
				inewrry = rr.yk+rmy*iStep;
				int iBestYMod = -1;
				
				while ((inewrry >= 1) && (inewrry <= 8) && bProceed)
				{
					iStep++;
					inewrry = rr.yk+rmy*iStep;
					
					iEKDistance = (int)Math.max(Math.abs((double)(rr.xk-kEnemy.xk)),Math.abs((double)(inewrry-kEnemy.yk)));
					iOKDistance = (int)Math.max(Math.abs((double)(rr.xk-kOwn.xk)),Math.abs((double)(inewrry-kOwn.yk)));
					if (iOKDistance < iEKDistance)
					{
						bProceed = false;
						iBestYMod = inewrry;
					}
					
				}
				
				switch (rmy)
				{
					case 1:
						if (rr.xk==kEnemy.xk) iEKAreaY = 8*(inewrry-1);
						if (rr.xk>kEnemy.xk) iEKAreaY =  (rr.xk-1) * (inewrry-1);
						if (rr.xk<kEnemy.xk) iEKAreaY =  (8-rr.xk) * (inewrry-1);
						break;
						
					case -1:
						//System.out.println("DBG160402: inewrry/minus:" + inewrry); 
						if (rr.xk==kEnemy.xk) iEKAreaY = 8*(inewrry-1);
						if (rr.xk>kEnemy.xk) iEKAreaY =  (rr.xk-1) * (8-inewrry);
						if (rr.xk<kEnemy.xk) iEKAreaY =  (8-rr.xk) * (inewrry-1);
						break;	
				
				}
				if ((inewrry <1) || (inewrry > 8)) iEKAreaY = -1;
				//System.out.println("DBG160402: inewrry:" + inewrry); 
				
			}
			//System.out.println("DBG160402: move areas: x:" + iEKAreaX + " y:"+iEKAreaY);
			if (((iEKAreaX < iEKAreaY) || (iEKAreaY == -1)) && (iEKAreaX != -1))
			{
				iNewRX = inewrrx;
				iNewRY = rr.yk;
			}
			else
			{
				iNewRX = rr.xk;
				iNewRY = inewrry;
			}
			//System.out.println("Rook move to: " + iNewRX + "," + iNewRY);
			//cb.dump();
			
			Vector v = rr.moveVector(cb);
			move mdo = null;
			for (int i=0;i<v.size();i++)
			{
				move m = (move)v.elementAt(i);
				if ((m.xtar == iNewRX) && (m.ytar== iNewRY)) 
				{
					mdo = m;
					i=v.size();	
				}
			}
			if (mdo == null) return null;
			//System.out.println("Doing move: " + mdo.moveStrLong());
		
			cb.domove(rr,mdo,-1);
			if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
			else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
			return cb;
		
		}
		else
		{
			//System.out.println("DBG160402:do_hcw_rookalone no need to escape with rook");
			
			if (iOKDistance > 1)
			{
				int kmx = 0;
				int kmy = 0;
				
				if (kOwn.xk > rr.xk) kmx = -1;
				if (kOwn.xk < rr.xk) kmx = 1;
				if (kOwn.yk > rr.yk) kmy = -1;
				if (kOwn.yk < rr.yk) kmy = 1;
				int kNewX = kOwn.xk + kmx;
				int kNewY = kOwn.yk + kmy;
			
				Vector v = kOwn.moveVector(cb);
				move mdo = null;
				for (int i=0;i<v.size();i++)
				{
					move m = (move)v.elementAt(i);
					if ((m.xtar == kNewX) && (m.ytar== kNewY)) 
					{
						mdo = m;
						i=v.size();	
					}
				}
				if (mdo == null) return null;
				//System.out.println("Doing move: " + mdo.moveStrLong());
			
				cb.domove(kOwn,mdo,-1);
				if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
				else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
				return cb;
			}
			else
			{
				//System.out.println("DBG160402:do_hcw_rookalone: own king next to rook");
				if (iEKDistance == 1)
				{
					//System.out.println("DBG160402:do_hcw_rookalone: enemy king next to rook as well. Move king appropriately!");
					Vector v = kOwn.moveVector(cb);
					move mdo = null;
					for (int i=0;i<v.size();i++)
					{
						move m = (move)v.elementAt(i);
						if (((m.xtar == rr.xk) || (m.ytar== rr.yk)) &&
						   (Math.abs(m.xtar-rr.xk) <= 1) &&
						   (Math.abs(m.ytar-rr.yk) <= 1))
						{
							mdo = m;
							i=v.size();	
						}
					}
					//System.out.println("Doing move: " + mdo.moveStrLong());
					if (mdo == null) return null;
					cb.domove(kOwn,mdo,-1);
					if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
					else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
					return cb;
					
				}
				else
				{
					//System.out.println("DBG160402:enemy king gave some space. What do do????");
					int kdx = kEnemy.xk-kOwn.xk;
					int kdy = kEnemy.yk-kOwn.yk;
					if ((Math.abs(kdx) == 2) && (Math.abs(kdy) == 2))
					{
						//System.out.println("DBG160402:enemy king gave some space. Put the rook in between!!");
						int rtarx = (kEnemy.xk+kOwn.xk) / 2;
						int rtary = (kEnemy.yk+kOwn.yk) / 2;
						Vector v = rr.moveVector(cb);
						move mdo = null;
						for (int i=0;i<v.size();i++)
						{
							move m = (move)v.elementAt(i);
							if ((m.xtar == rtarx) && (m.ytar== rtary)) 
							{
								mdo = m;
								i=v.size();	
							}
						}
						//System.out.println("Doing move: " + mdo.moveStrLong());
						if (mdo == null) return null;
						cb.domove(rr,mdo,-1);
						if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
						else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
						return cb;
					}
					if (((Math.abs(kdx) == 1) && (Math.abs(kdy) == 3)) || 
						((Math.abs(kdx) == 3) && (Math.abs(kdy) == 1)))
					{
						int ktarx,ktary;
						
						//System.out.println("DBG160402:case 1/3!");
						if (kEnemy.xk>kOwn.xk) ktarx=kOwn.xk+1;
						else ktarx=kOwn.xk-1;
						if (kEnemy.yk>kOwn.yk) ktary=kOwn.yk+1;
						else ktary=kOwn.yk-1;
						Vector v = kOwn.moveVector(cb);
						move mdo = null;
						//System.out.println("ktar:" + ktarx+","+ktary);
						for (int i=0;i<v.size();i++)
						{
							move m = (move)v.elementAt(i);
							if ((m.xtar == ktarx) && (m.ytar== ktary)) 
							{
								mdo = m;
								i=v.size();	
							}
						}
						//System.out.println("Doing move: " + mdo.moveStrLong());
						if (mdo == null) return null;
				
						cb.domove(kOwn,mdo,-1);
						if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
						else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
						return cb;
						
					}
					else
					{
						//System.out.println("DBG160402: hope to find a good rook move to narrow the space!");
						if ((Math.abs(kEnemy.xk-rr.xk)>=2) && (Math.abs(kEnemy.yk-rr.yk) >= 2))
						{
							int rdirx, rdiry;
							if (kEnemy.xk > rr.xk) rdirx = 1+rr.xk;
							else rdirx = -1+rr.xk;
							if (kEnemy.yk > rr.yk) rdiry = 1+rr.yk;
							else rdiry = -1+rr.yk;
							
							Vector v = rr.moveVector(cb);
							move mdo = null;
							for (int i=0;i<v.size();i++)
							{
								move m = (move)v.elementAt(i);
								if ((m.xtar == rdirx) || (m.ytar== rdiry) && (!m.bRisky)) 
								{
									mdo = m;
									i=v.size();	
								}
							}
							//System.out.println("Doing move: " + mdo.moveStrLong());
							if (mdo == null) return null;
							cb.domove(rr,mdo,-1);
							if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
							else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
							return cb;
						}
						else if (iEKDistance < 3)
						{
							//System.out.println("DBG160402: not a clue. ek close to rook. get own king to help!");
							Vector v = kOwn.moveVector(cb);
							move mdo = null;
							for (int i=0;i<v.size();i++)
							{
								move m = (move)v.elementAt(i);
								if (((m.xtar == rr.xk) || (m.ytar== rr.yk)) &&
								   (Math.abs(m.xtar-rr.xk) <= 1) &&
								   (Math.abs(m.ytar-rr.yk) <= 1))
								{
									mdo = m;
									i=v.size();	
								}
							}
							//System.out.println("Doing move: " + mdo.moveStrLong());
							if (mdo == null) return null;
							cb.domove(kOwn,mdo,-1);
							if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
							else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
							return cb;
						} 
						else
						{
							//System.out.println("DBG160402: perhaps we could move the rook. iEKDistance="+iEKDistance);
							int rdirx = rr.xk;
							int rdiry = rr.yk;
							
							if (Math.abs(kEnemy.xk-rr.xk)>=2) rdirx = rr.xk+(int)Math.signum(kEnemy.xk-rr.xk);
							if (Math.abs(kEnemy.yk-rr.yk)>=2) rdiry = rr.yk+(int)Math.signum(kEnemy.yk-rr.yk);	
								
							if ((rdirx==kOwn.xk) && (rdiry==kOwn.yk))
							{
								//System.out.println("Attempt to ram own king at: " + rdirx +","+rdiry);
								//System.out.println("Need to move king away");
								int kdirx = kOwn.xk;
								int kdiry = kOwn.yk;
								if (rdirx==rr.xk) kdirx = kOwn.xk+(int)Math.signum(4.5-kOwn.xk);
								if (rdiry==rr.yk) kdiry = kOwn.yk+(int)Math.signum(4.5-kOwn.yk);
								Vector v = kOwn.moveVector(cb);
								move mdo = null;
								for (int i=0;i<v.size();i++)
								{
									move m = (move)v.elementAt(i);
									if ((m.xtar == kdirx) && (m.ytar== kdiry))
									{
										mdo = m;
										i=v.size();	
									}
								}
								//System.out.println("Doing move: " + mdo.moveStrLong());
								if (mdo == null) return null;
								cb.domove(kOwn,mdo,-1);
								if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
								else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
								return cb;	
								
							}
								
							Vector v = rr.moveVector(cb);
							move mdo = null;
							for (int i=0;i<v.size();i++)
							{
								move m = (move)v.elementAt(i);
								if ((m.xtar == rdirx) && (m.ytar== rdiry))
								{
									mdo = m;
									i=v.size();	
								}
							}
							//System.out.println("Doing move: " + mdo.moveStrLong());
							if (mdo == null) return null;
							cb.domove(rr,mdo,-1);
							if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
							else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
							return cb;	
								
							
						}
					}
					
				}
			}
		}
		
		
		
		//return cb;
	}
	
	static chessboard do_hcw_queenalone(chessboard cb, int iTurn, movevalue mval, int iAlg)
	{
		moveindex mi;
		king kOwn,kEnemy;
		queen q = null;
		
		System.out.println("hcwinner.do_hcw_queenalone() called. Available moves are:");
		if (iTurn == Piece.WHITE)
		{
			kOwn = cb.locateKing(Piece.WHITE);
			kEnemy = cb.locateKing(Piece.BLACK);
		}
		else 
		{
			kOwn = cb.locateKing(Piece.BLACK);
			kEnemy = cb.locateKing(Piece.WHITE);
		}
		
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
		{
			if (cb.blocks[i][j] != null)
			{
				Piece p = cb.blocks[i][j];
				if (p.iType == Piece.QUEEN) q = (queen)cb.blocks[i][j];
			}
		}
		//System.out.println("DBG160402: queen at " + q.xk + "," + q.yk);
		
		if (iTurn== Piece.WHITE) mi = cb.miWhiteMoveindex;
		else mi = cb.miBlackMoveindex;
		
		move mbest = null;
		
		if (inCorner(kOwn.xk,kOwn.yk) != HCW_CORNER_NONE)	
		{
			//System.out.println("Own king at corner. Not good. Move it away pronto!");
			double iDistCtr = Math.max(Math.abs(kOwn.xk-4.5),Math.abs(kOwn.yk-4.5));
			mbest = null;
			Vector v = kOwn.moveVector(cb);
			for (int i=0;i<v.size();i++)
			{
				move m=(move)v.elementAt(i);
				//System.out.println(m.moveStrLong());
				double iDistCurr = Math.max(Math.abs(m.xtar-4.5),Math.abs(m.ytar-4.5));
				if (iDistCurr < iDistCtr)
				{
					mbest = m;
					iDistCtr = iDistCurr;
					
				}
			}
			if (mbest != null)
			{
				String sMov = mbest.moveStrCaps();
				if (CMonitor.sDrawMove.indexOf(sMov) != -1) 
				{
					System.out.println("DRAWISH!!! RETURN NULL!!!!");
					return null;
				}
				
				
				//System.out.println("Best move: " + mbest.moveStrLong());
				cb.domove( kOwn,mbest,-1);
				if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
				else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
				//mval.setBalancesFromBoard (cb, iTurn, iAlg);
				//System.out.println(mval.dumpstr(mval.DUMPMODE_LONG,iAlg));
				System.out.println("DBG180406:A");
				return cb;
			}
			
		}
		
		int iEKBefore =iEKARea(kEnemy.xk,kEnemy.yk,q.xk,q.yk);
		if (iEKBefore ==3)
		{
			//System.out.println("EK possibly at corner. Cool!");
			if (inQuadrant(q.xk,q.yk) == inCorner(kEnemy.xk,kEnemy.yk))
			{
				int iCorn = inCorner(kEnemy.xk,kEnemy.yk);
				int okdx = kingSweetSpotXByCorner(iCorn);
				int okdy = kingSweetSpotYByCorner(iCorn);
				
				if ((okdx==kOwn.xk) && (okdy==kOwn.yk))
				{
					int iqdx = queenSweetSpotXByCorner(iCorn);
					int iqdy = queenSweetSpotYByCorner(iCorn);
					//System.out.println("Moving queen to " + iqdx + "," + iqdx);
					Vector v = q.moveVector(cb);
					for (int i=0;i<v.size();i++)
					{
						move m=(move)v.elementAt(i);
						//System.out.println(m.moveStrLong());
						if ((m.xtar == iqdx) && (m.ytar == iqdy))
						{
							mbest = m;
						}
						
					}
					if (mbest != null)
					{
						String sMov = mbest.moveStrCaps();
						if (CMonitor.sDrawMove.indexOf(sMov) != -1) 
						{
							System.out.println("DRAWISH!!! RETURN NULL!!!!");
							return null;
						}
						
						
						//System.out.println("Best move (queen): " + mbest.moveStrLong());
						cb.domove( q,mbest,-1);
						if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
						else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
						System.out.println("DBG180406:B");
						return cb;
					}
				}
				
				//System.out.println("Moving king to " + okdx + "," + okdy);
				int iOKDMin = 100;
				mbest = null;
				Vector v = kOwn.moveVector(cb);
				for (int i=0;i<v.size();i++)
				{
					move m=(move)v.elementAt(i);
					//System.out.println(m.moveStrLong());
					int iOKDCurr = (int)Math.max((double)Math.abs(m.xtar-okdx),Math.abs(m.ytar-okdy));
					if (iOKDCurr < iOKDMin)
					{
						mbest = m;
						iOKDMin = iOKDCurr;
						
					}
				}
				if (mbest != null)
				{
					//System.out.println("Best move (k1): " + mbest.moveStrLong());
					String sMov = mbest.moveStrCaps();
					if (CMonitor.sDrawMove.indexOf(sMov) != -1) 
					{
						System.out.println("DRAWISH!!! RETURN NULL!!!!");
						return null;
					}
					
					
					cb.domove( kOwn,mbest,-1);
					if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
					else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
					System.out.println("DBG180406:C");
					return cb;
				}
				
			}
			//else System.out.println("But it wasn't.");
			
		}
		
		int iBestEKA = 100;
		//System.out.println("Searching for queen moves!");
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			//System.out.print("move:"+m.moveStrLong() + " ");
			
			int iEKDistance = (int)Math.max(Math.abs((double)(m.xtar-kEnemy.xk)),Math.abs((double)(m.ytar-kEnemy.yk)));
			int iOKDistance = (int)Math.max(Math.abs((double)(m.xtar-kOwn.xk)),Math.abs((double)(m.ytar-kOwn.yk)));
			
			int iEKArea = -1;
			
			iEKArea = iEKARea(kEnemy.xk,kEnemy.yk,m.xtar,m.ytar);
			
			//System.out.println(iOKDistance+" "+iEKDistance+ "  " + iEKArea);
			if ((iEKArea < iBestEKA) && ((iEKDistance >= 2) || (iOKDistance ==1)) && (m.p.iType== Piece.QUEEN) && (iEKArea >= 3) && (iEKArea < iEKBefore))
			{
				mbest = m;
				iBestEKA = iEKArea;
				//System.out.println("New best move! " + m.moveStrLong());
			}
		}
		
		if (mbest != null)
		{
			//System.out.println("Best move xx: " + mbest.moveStrLong());
			//String moveStr = mbest.moveStr();
			
			String sMov = mbest.moveStrCaps();
			if (CMonitor.sDrawMove.indexOf(sMov) != -1) 
			{
				System.out.println("DRAWISH!!! RETURN NULL!!!!");
				return null;
			}
			
			
			cb.domove( q,mbest,-1);
			if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
			else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
			//mval.setBalancesFromBoard (cb, iTurn, iAlg);
			//mval.setInstWinnable(1-iTurn);
			//mval.pushmove(moveStr); 
			//System.out.println(mval.dumpstr(mval.DUMPMODE_LONG,iAlg));
			System.out.println("DBG180406:D");
			return cb;
		}
		
		//System.out.println("Searching for king moves. Bring own king closer!");
		int iOKBestDist = (int)Math.max(Math.abs((double)(q.xk-kOwn.xk)),Math.abs((double)(q.yk-kOwn.yk)));
		
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			if (m.p.iType == Piece.KING)
			{
				//System.out.print("move:"+m.moveStrLong() + " ");
				
				int iOKDistance = (int)Math.max(Math.abs((double)(m.xtar-q.xk)),Math.abs((double)(m.ytar-q.yk)));
					
				if (iOKDistance <= iOKBestDist)
				{
					mbest = m;
					iOKBestDist = iOKDistance;
						
					//System.out.println("New best move! " + m.moveStrLong());
				}
				

			}
		}
		//System.out.println();
		if (mbest != null)
		{
			//System.out.println("Best move (king): " + mbest.moveStrLong());
			System.out.println("DBG180406:E");
			System.out.println("CMon.drawstring:"+ CMonitor.sDrawMove);
			System.out.println("Our move:" + mbest.moveStrCaps());
			
			String sMov = mbest.moveStrCaps();
			if (CMonitor.sDrawMove.indexOf(sMov) != -1) 
			{
				System.out.println("DRAWISH!!! RETURN NULL!!!!");
				return null;
			}
			
			
			cb.domove( kOwn,mbest,-1);
			if (iTurn == Piece.WHITE) cb.iHCBonus = HC_BONUS_FOR_QR_ALONE;
			else cb.iHCBonus = -HC_BONUS_FOR_QR_ALONE;
			//mval.setBalancesFromBoard (cb, iTurn, iAlg);
			//System.out.println(mval.dumpstr(mval.DUMPMODE_LONG,iAlg));
			
			return cb;
		}
		//System.out.println("No better move found!");
		//cb.dump();
		return null;
		
		
	}
	
	static chessboard do_hcw_rook_and_pawns(chessboard cb, int iTurn, movevalue mval, int iAlg)
	{
		//System.out.println("hcw:do_hcw_rook_and_pawns");
		king kOwn, kEnemy;
		rook rr = null;
		int iNewRX, iNewRY;
		int pbx,pby;
		
		//System.out.println("DBG160402: hcwinner.do_hcw_rook_and_pawns() starts.");
		
		if (iTurn == Piece.WHITE)
		{
			kOwn = cb.locateKing(Piece.WHITE);
			kEnemy = cb.locateKing(Piece.BLACK);
		}
		else 
		{
			kOwn = cb.locateKing(Piece.BLACK);
			kEnemy = cb.locateKing(Piece.WHITE);
		}
		
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
		{
			if (cb.blocks[i][j] != null)
			{
				Piece p = cb.blocks[i][j];
				if (p.iType == Piece.ROOK) rr = (rook)cb.blocks[i][j];
			}
		}
		//System.out.println("DBG160402: kown at" + kOwn.xk + "," + kOwn.yk+ " rook at " + rr.xk + "," + rr.yk);
		
		int iBestPawn=-1;
		if (iTurn == Piece.BLACK) iBestPawn = 10;
		pbx = -1;
		for (int i=1;i<=8;i++)
		{
			//System.out.println(i + " W:" + cb.iWhitePawnColMax[i] + " B:" + cb.iBlackPawnColMin[i]);
			if (iTurn == Piece.WHITE)
			{
				if ((iBestPawn==-1) && (cb.iWhitePawnColMax[i] > -1))
				{
					iBestPawn = cb.iWhitePawnColMax[i];
					pbx = i;
				}
				else if (cb.iWhitePawnColMax[i] > iBestPawn) 
				{
					iBestPawn = cb.iWhitePawnColMax[i];
					pbx = i;
				}
			}
			else
			{
				if ((iBestPawn==10) && (cb.iBlackPawnColMin[i]<10)) 
				{
					iBestPawn = cb.iBlackPawnColMin[i];
					pbx = i;
				}
				else if (cb.iBlackPawnColMin[i] < iBestPawn) 
				{
					iBestPawn = cb.iBlackPawnColMin[i];
					pbx = i;
				}
			}
		}
		pby=iBestPawn;
		//System.out.println("iBestPawn at:" + pbx + "," + pby);
		
				
		pawn p = (pawn)cb.blocks[pbx][pby];
		
		Vector v = rr.moveVector(cb);
		//System.out.println("Rook move dump:");
		for (int i=0;i<v.size();i++)
		{
			move m = (move)v.elementAt(i);
			//System.out.println(m.moveStrLong());
			if (m.bPressure) 
			{
				//System.out.println("Rook under pressure. HCW not good! Revert to normal.");
				return null;
			}
		}
		
		v = p.moveVector(cb);
		for (int i=0;i<v.size();i++)
		{
			move m = (move)v.elementAt(i);
			//System.out.println(m.moveStrLong());
			if (m.bPressure) 
			{
				//System.out.println("Head pawn under pressure. HCW not good! Revert to normal.");
				return null;
			}
		}
		//System.out.println("NOPRESSURE! rr:" + rr.xk+","+rr.yk + " pbx:" + pbx + " pby:"+pby + " iTurn:"+iTurn);
		
		if ((Math.abs(rr.xk-kEnemy.xk) <=1) && (Math.abs(rr.yk-kEnemy.yk) <=1))
		{
			//System.out.println("Enemy king too close!");
			return null;
		}
		
		if ((rr.xk == pbx) &&
		    (((iTurn == Piece.WHITE) && (rr.yk < pby)) ||
			 ((iTurn == Piece.BLACK) && (rr.yk > pby))))
		{
			//System.out.println("rook behind pawn, move the pawn.");
			v = p.moveVector(cb);
			move mbest = null;
			for (int i=0;i<v.size();i++)
			{
				move m = (move)v.elementAt(i);
				if (!m.bRisky) mbest = m;
			}
			if (mbest != null)
			{
				cb.domove( p,mbest,-1);
				if (iTurn == Piece.WHITE) cb.iHCBonus = mbest.ytar;
				else cb.iHCBonus = 9-mbest.ytar;
				return cb;
			}
			else return null;
		}
		
		if (((iTurn == Piece.WHITE) && (rr.yk < pby)) ||
			 ((iTurn == Piece.BLACK) && (rr.yk > pby)))
		{
			//System.out.println("Could get rook behind pawn. Try moving rook.");
			v = rr.moveVector(cb);
			move mbest = null;
			for (int i=0;i<v.size();i++)
			{
				move m = (move)v.elementAt(i);
				//System.out.println(m.moveStrLong());
				if ((m.xtar == pbx) && (m.ytar == rr.yk) && (!m.bRisky)) 
				{
					mbest = m;
				}
			}	
			if (mbest != null)
			{
				cb.domove (rr,mbest,-1);
				if (iTurn == Piece.WHITE) cb.iHCBonus = pby;
				else cb.iHCBonus = 9-pby;
				return cb;
			}
			else
			{
				return null;
			}
			
		}
		
		if (((kEnemy.xk < rr.xk) && (rr.xk < pbx)) || 
			((kEnemy.xk > rr.xk) && (rr.xk > pbx)))
		{
			//System.out.println("Rook nicely in between to protect! Go on with pawn.");
			
			v = p.moveVector(cb);
			move mbest = null;
			for (int i=0;i<v.size();i++)
			{
				move m = (move)v.elementAt(i);
				if (!m.bRisky) mbest = m;
			}
			if (mbest != null)
			{
				cb.domove( p,mbest,-1);
				if (iTurn == Piece.WHITE) cb.iHCBonus = mbest.ytar;
				else cb.iHCBonus = 9-mbest.ytar;
				return cb;
			}
			else return null;
		}
		
		//System.out.println("Need to do something! Pawn up, reverse rook, whatever...");
		
		// find any non-risky pawn move far from EK
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
		{
			Piece pp = cb.blocks[i][j];
			if (pp != null)
			{
				if ((pp.iType == Piece.PAWN) && (pp.iColor == iTurn))
				{
					v = pp.moveVector(cb);
					//System.out.println("pawn cand @" + i +","+j + " vector size:" + v.size());
					move mbest = null;
					for (int ii=0;ii<v.size();ii++)
					{
						move m = (move)v.elementAt(ii);
						//System.out.println(m.moveStrLong());
						if ((!m.bRisky) && (i != kEnemy.xk)) mbest = m;
					}
					
					if (mbest != null)
					{
						cb.domove( pp,mbest,-1);
						if (iTurn == Piece.WHITE) cb.iHCBonus = mbest.ytar;
						else cb.iHCBonus = 9-mbest.ytar;
						return cb;
					}
					
				}
					
			}
		}
		
		return null;
	}
	
	static chessboard do_hcw_queen_and_pawns(chessboard cb, int iTurn, movevalue mval, int iAlg)
	{
		king kOwn, kEnemy;
		queen q = null;
		int iNewRX, iNewRY;
		int pbx,pby;
		
		//System.out.println("DBG160402: hcwinner.do_hcw_queen_and_pawns() starts.");
		
		if (iTurn == Piece.WHITE)
		{
			kOwn = cb.locateKing(Piece.WHITE);
			kEnemy = cb.locateKing(Piece.BLACK);
		}
		else 
		{
			kOwn = cb.locateKing(Piece.BLACK);
			kEnemy = cb.locateKing(Piece.WHITE);
		}
		
		for (int i=1;i<=8;i++)
			for (int j=1;j<=8;j++)
		{
			if (cb.blocks[i][j] != null)
			{
				Piece p = cb.blocks[i][j];
				if (p.iType == Piece.QUEEN) q = (queen)cb.blocks[i][j];
			}
		}
		//System.out.println("DBG160402: kown at" + kOwn.xk + "," + kOwn.yk+ " queen at " + q.xk + "," + q.yk);
		
		int iBestPawn=-1;
		if (iTurn == Piece.BLACK) iBestPawn = 10;
		pbx = -1;
		for (int i=1;i<=8;i++)
		{
			//System.out.println(i + " W:" + cb.iWhitePawnColMax[i] + " B:" + cb.iBlackPawnColMin[i]);
			if (iTurn == Piece.WHITE)
			{
				if ((iBestPawn==-1) && (cb.iWhitePawnColMax[i] > -1))
				{
					iBestPawn = cb.iWhitePawnColMax[i];
					pbx = i;
				}
				else if (cb.iWhitePawnColMax[i] > iBestPawn) 
				{
					iBestPawn = cb.iWhitePawnColMax[i];
					pbx = i;
				}
			}
			else
			{
				if ((iBestPawn==10) && (cb.iBlackPawnColMin[i]<10)) 
				{
					iBestPawn = cb.iBlackPawnColMin[i];
					pbx = i;
				}
				else if (cb.iBlackPawnColMin[i] < iBestPawn) 
				{
					iBestPawn = cb.iBlackPawnColMin[i];
					pbx = i;
				}
			}
		}
		//System.out.println("iBestPawn:" + iBestPawn);
		pby=iBestPawn;
				
		pawn p = (pawn)cb.blocks[pbx][pby];
		
		
		if (((kEnemy.xk < q.xk) && (q.xk < pbx)) || 
			((kEnemy.xk > q.xk) && (q.xk > pbx)))
		{
			//System.out.println("Queen nicely in between to protect! Go on with pawn.");
			
			Vector v = p.moveVector(cb);
			move mbest = null;
			for (int i=0;i<v.size();i++)
			{
				move m = (move)v.elementAt(i);
				if (!m.bRisky) mbest = m;
			}
			if (mbest != null)
			{
				cb.domove( p,mbest,-1);
				if (iTurn == Piece.WHITE) cb.iHCBonus = mbest.ytar;
				else cb.iHCBonus = 9-mbest.ytar;
				return cb;
			}
			else return null;
		}
		
		if ((q.xk == pbx) &&
		    (((iTurn == Piece.WHITE) && (q.yk < pby)) ||
			 ((iTurn == Piece.BLACK) && (q.yk > pby))))
		{
			//System.out.println("queen behind pawn, move the pawn.");
			Vector v = p.moveVector(cb);
			move mbest = null;
			for (int i=0;i<v.size();i++)
			{
				move m = (move)v.elementAt(i);
				if (!m.bRisky) mbest = m;
			}
			if (mbest != null)
			{
				cb.domove( p,mbest,-1);
				if (iTurn == Piece.WHITE) cb.iHCBonus = mbest.ytar;
				else cb.iHCBonus = 9-mbest.ytar;
				return cb;
			}
			else return null;
		}
		
		//System.out.println("getting queen behind the pawn!");
		Vector v = q.moveVector(cb);
		move mbest = null;
		for (int i=0;i<v.size();i++)
		{
			move m = (move)v.elementAt(i);
			//System.out.println(m.moveStrLong());
			if (((!m.bRisky) && (m.xtar == p.xk)) && 
					(((iTurn== Piece.WHITE) && (m.ytar < p.yk)) ||
					 ((iTurn== Piece.BLACK) && (m.ytar > p.yk))))
			{		 
				mbest = m;
			}
		}
		if (mbest != null)
		{
			cb.domove( q,mbest,-1);
			if (iTurn == Piece.WHITE) cb.iHCBonus = pby;
			else cb.iHCBonus = 9-pby;
			return cb;
		}
		else return null;
		
		
		//return null;
	}
	
	static int iEKARea(int kx,int ky, int qx, int qy)
	{
		if ((kx>qx) && (ky>qy)) return (8-qx)*(8-qy);
		if ((kx>qx) && (ky==qy)) return (8-qx)*8;
		if ((kx>qx) && (ky<qy)) return (8-qx)*(qy-1);
		if ((kx==qx) && (ky<qy)) return 8*(qy-1);
		if ((kx<qx) && (ky<qy)) return (qx-1)*(qy-1);
		if ((kx<qx) && (ky==qy)) return (qx-1)*8;
		if ((kx<qx) && (ky>qy)) return (qx-1)*(8-qy);
		if ((kx==qx) && (ky>qy)) return 8*(8-qy);
		return -1;
	}
	
	static int inQuadrant (int x, int y)
	{
		if ((x > 4) && (y > 4)) return HCW_CORNER_UPPER_RIGHT;
		else if ((x > 4) && (y < 5)) return HCW_CORNER_LOWER_RIGHT;
		else if ((x < 4) && (y < 5)) return HCW_CORNER_LOWER_LEFT;
		else return HCW_CORNER_UPPER_LEFT;
	}
	
	static int inCorner(int x, int y)
	{
		if ((x > 6) && (y > 6)) return HCW_CORNER_UPPER_RIGHT;
		else if ((x > 6) && (y < 3)) return HCW_CORNER_LOWER_RIGHT;
		else if ((x < 3) && (y < 3)) return HCW_CORNER_LOWER_LEFT;
		else if ((x < 3) && (y > 6)) return HCW_CORNER_UPPER_LEFT;
		else return HCW_CORNER_NONE;
	}
	
	static int kingSweetSpotXByCorner(int iCorner)
	{
		if ((iCorner == HCW_CORNER_UPPER_RIGHT) || (iCorner == HCW_CORNER_LOWER_RIGHT)) return 6;
		if ((iCorner == HCW_CORNER_UPPER_LEFT) || (iCorner == HCW_CORNER_LOWER_LEFT)) return 3;
		return -1;
	}
	
	static int kingSweetSpotYByCorner(int iCorner)
	{
		if ((iCorner == HCW_CORNER_UPPER_RIGHT) || (iCorner == HCW_CORNER_UPPER_LEFT)) return 6;
		if ((iCorner == HCW_CORNER_LOWER_RIGHT) || (iCorner == HCW_CORNER_LOWER_LEFT)) return 3;
		return -1;
	}
	
	static int queenSweetSpotXByCorner(int iCorner)
	{
		if ((iCorner == HCW_CORNER_UPPER_RIGHT) || (iCorner == HCW_CORNER_LOWER_RIGHT)) return 7;
		if ((iCorner == HCW_CORNER_UPPER_LEFT) || (iCorner == HCW_CORNER_LOWER_LEFT)) return 2;
		return -1;
	}
	
	static int queenSweetSpotYByCorner(int iCorner)
	{
		if ((iCorner == HCW_CORNER_UPPER_RIGHT) || (iCorner == HCW_CORNER_UPPER_LEFT)) return 7;
		if ((iCorner == HCW_CORNER_LOWER_RIGHT) || (iCorner == HCW_CORNER_LOWER_LEFT)) return 2;
		return -1;
	}
	
}