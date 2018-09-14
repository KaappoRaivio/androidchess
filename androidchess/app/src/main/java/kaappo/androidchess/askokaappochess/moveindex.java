package kaappo.androidchess.askokaappochess;
import java.util.*;

public class moveindex
{
	int iColor;
	Vector v;
	chessboard cb;
	int iSortedTo;
	static final boolean bDebug = false;
	
	moveindex ( int iC, chessboard c)
	{
		v = new Vector();
		iColor = iC;
		cb = c;
		iSortedTo = -1;
		//System.out.println("DBG160126: new moveindex created:" + iColor);
		//cb.dump();
	}
	
	boolean addMove(move m)
	{
		//System.out.println("adding " + m.moveStrLong() + " hash:" + m.iCoordHash());
		
		if (v.size() == 0)
		{
			v.addElement(m);
			return true;
		}
		
		
		for (int i=0;i<v.size(); i++)
		{
			move m1 = (move)v.elementAt(i);
			//if (m.iCaptValue > m1.iCaptValue)
			if (m.iNetCapture > m1.iNetCapture)
			{
				v.insertElementAt(m,i);
				return true;
			}
			//if (m.iCaptValue == m1.iCaptValue)
			if (m.iNetCapture == m1.iNetCapture)
			{
				if ((m1.bRisky) && (!m.bRisky))
				{
					v.insertElementAt(m,i);
					return true;
				}
				
				if (((!m.bRisky) && (m.bPressure)) && ((m1.bRisky) || (!m1.bPressure)))
				{
					v.insertElementAt(m,i);
					return true;
				}
				
				if (((m.isCheck() || m.isRevCheck())) && (!m1.isCheck() && (!m1.isRevCheck())))
				{
					//System.out.println("adding " + m.moveStrLong() + " hash:" + m.iCoordHash() + " before " + m1.moveStrLong() + " by check criteria");
					v.insertElementAt(m,i);
					return true;
				}
			
				if ((m.iCoordHash() < m1.iCoordHash()) &&
					(((m1.isCheck() || m1.isRevCheck())) && (!m.isCheck() && (!m.isRevCheck()))) &&
					(((!m1.bRisky) && (m1.bPressure)) && ((m.bRisky) || (!m.bPressure))) &&
					((m.bRisky) && (!m1.bRisky)))
				{
					//System.out.println("adding " + m.moveStrLong() + " hash:" + m.iCoordHash() + " before " + m1.moveStrLong() + " by hash criteria A");
					v.insertElementAt(m,i);
					return true;
				}
				
				if ((m.iCoordHash() < m1.iCoordHash()) &&
					(((!m1.isCheck() && !m1.isRevCheck())) && (!m.isCheck() && (!m.isRevCheck()))) &&
					(((!m1.bRisky) && (!m1.bPressure)) && ((!m.bRisky) && (!m.bPressure))) &&
					((!m.bRisky) && (!m1.bRisky)))
				{
					//System.out.println("adding " + m.moveStrLong() + " hash:" + m.iCoordHash() + " before " + m1.moveStrLong() + " by hash criteria B");
					v.insertElementAt(m,i);
					return true;
				}
				
			}

		}
		//System.out.println("adding " + m.moveStrLong() + " hash:" + m.iCoordHash() + " goes last.");
		v.addElement(m);
		return true;
	}
	
	boolean addMoveVector(Vector mv)
	{
		//System.out.println("moveindex.addMoveVector()");
		for (int i=0;i<mv.size(); i++)
		{
			addMove((move)mv.elementAt(i));
		}
		
		return true;
	}
	
	void dump(moveindex mio)
	{
		System.out.println("Check moves:" + iCheckMoveCount());
		for (int i=0;i<v.size();i++)
		{	
			move m1 = (move)v.elementAt(i);
			System.out.println(m1.moveStrLong() + " "  + m1.iCoordHash());
		}
		if (mio != null) 
		{
			if (v.size() > 0) System.out.println("Best Move: " + getBestMove(null,mio).moveStrLong());
			else System.out.println("No move.");
		}
	}
	
	void dump()
	{
		dump(false);
	}
	
	void dump(boolean bCR)
	{
		for (int i=0;i<v.size();i++)
		{	
			move m1 = (move)v.elementAt(i);
			System.out.print(m1.moveStrLong()+ ";");
			if (bCR) System.out.println();
		}
		System.out.println();		
	}
	
	String orderString()
	{
		String sRet = "";
		for (int i=0;i<v.size();i++)
		{	
			move m1 = (move)v.elementAt(i);
			sRet = sRet + m1.moveStrCaps() + " ";
		}
		return sRet.trim();
	}
	
	move getMoveAt(int i)
	{
		if (v.size() == 0) return null;
		return (move)v.elementAt(i);
	}
	
	int getSize()
	{
		return v.size();
	}
	
	int iMaxMoveVal()
	{
		if (v.size() == 0) return 0;
		else return ((move)v.elementAt(0)).iCaptValue;
	}
	
	int iMaxMoveVal(boolean bRiskAllowed)
	{
		//System.out.println("DBG150127: iMaxMoveVal() riskbits..");
		if (bRiskAllowed) return iMaxMoveVal();
		for (int i=0;i<v.size();i++)
		{	
			move m1 = (move)v.elementAt(i);
			if (!m1.isRisky()) return m1.iCaptValue;
		}
		
		return 0;
		
	}
	
	Vector getMoveVectorAt(int xk, int yk)
	{
		Vector mVecR = new Vector();
		
		for (int i=0;i<v.size();i++)
		{	
			move m1 = (move)v.elementAt(i);
			if ((m1.p.xk == xk) && (m1.p.yk == yk)) mVecR.addElement(m1);
		}
		
		return mVecR;
	}
	
	int iGetMaxPressure()
	{
		int iMaxP = 0;
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.bPressure)
			{
				if ((mm.p.pvalue() > iMaxP) && (!mm.bRisky)) iMaxP = mm.p.pvalue();
			}
		}
		
		return iMaxP;
	}
	
	moveindex goodMoveIndex(chessboard cb, king k)
	{
		moveindex mi = new moveindex (iColor, cb);
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			//if (mm.p.iColor == piece.BLACK) System.out.println("DBG 141220: cb.bMoveIsGood called for: " + mm.moveStr());
			if (cb.bMoveIsGood(mm,mm.p,k,cb.locateCheckersVector(k))) mi.addMove(mm);
			//else System.out.println("DBG 141220:NO GOOD!");
		}
		
		return mi;
	}
	
	moveindex sortedcopy()
	{
		moveindex mi = new moveindex (iColor, cb);
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			mi.addMove(mm);
		}
		
		return mi;
	}
		
	piece findOther(piece p)
	{
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if ((mm.p.iType == p.iType) && (mm.xtar == p.xk) && (mm.ytar == p.yk))
			{
				return mm.p;
			}
		}
		
		return null;
	}
	
	boolean setRiskBits(chessboard cb)
	{
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			//System.out.println("DBG160209: moveindex.setRiskBits (before ana): " + mm.moveStrLong());
			mm.analyzeRisk(mm.p, cb);
			//System.out.println("DBG160209: moveindex.setRiskBits (bef skew ana): " + mm.moveStrLong());
			if (mm.mSkewerMove != null) mm.mSkewerMove.analyzeRisk(mm.mSkewerMove.p,cb);
			if (mm.bKCS)
			{
				if (!mm.isRisky())
				{
					if (iColor == piece.WHITE) cb.iWhiteKCSMoves++;
					else cb.iBlackKCSMoves++;
				}
			}
			//System.out.println("DBG160209: moveindex.setRiskBits (after ana): " + mm.moveStrLong());
		}
		
		return true;
	}
	
	int iNonRiskyMoves()
	{
		int iRet = 0;
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (!mm.isRisky()) iRet++;
		}
		
		return iRet;
	}
	
	int iNonRiskyCapturesOrSpecials()
	{
		int iRet = 0;
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (!mm.isRisky() && ((mm.iCaptValue +1 >= iMaxMoveVal()) || mm.isSpecial()))
				iRet++;
		}
		return iRet;
	}
	
	int iMovesHittingBlock( int xk, int yk)
	{
		int iRet = 0;
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if ((mm.xtar == xk) && (mm.ytar == yk)) iRet++;
		}
		
		return iRet;
	}
	
	int iCheckMoveCount()
	{
		int iRet = 0;
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if ((mm.isCheck()) || (mm.isRevCheck())) iRet++;
		}
		
		return iRet;
	}
	
	int minPieceHittingBlockVal( int xk, int yk, chessboard cb)
	{
		piece pr = minPieceHittingBlock(xk,yk,cb);
		if (pr == null) return 0;
		else return pr.pvalue();
	}
	
	
	piece minPieceHittingBlock( int xk, int yk, chessboard cb)
	{
		int iRet = 0;
		piece pRet = null;
		
		//System.out.println("moveindex.minPieceHittingBlock("+xk+","+yk+") starts:"+iColor);
		
		if (cb.blocks[xk][yk] == null)
		{
			for (int i=0;i<getSize();i++)
			{
				move mm = getMoveAt(i);
				//System.out.println("DBG 141221: " + mm.moveStr());
				if ((mm.xtar == xk) && (mm.ytar == yk) && (mm.p.iType != piece.PAWN))
				{
					if (iRet == 0) 
					{
						//System.out.println("DBG: minPieceHittingBlock(A):" + mm.moveStr());
						iRet = mm.p.pvalue();
						pRet = mm.p;
					}
					else if (mm.p.pvalue() < iRet)
					{
						//System.out.println("DBG: minPieceHittingBlock(B):" + mm.moveStr());
						iRet = mm.p.pvalue();
						pRet = mm.p;
					}
				}
			}
		}
		else
		{
			/*move mx = getMoveAt(0);
			if (mx == null)
			{
				System.out.println("ret 0 @ branch A");
				return 0;
			}
			*/
			//int iC = mx.iColor;
			//piece p = mx.p;
			//if (p != null) 
			//{
				for (int i=1;i<=8;i++)
					for (int j=1;j<=8;j++)
					{
						piece p2 = cb.blocks[i][j];
						if ((p2 != null) && (p2.iColor == iColor) && ((i!=xk) || (j != yk)))
						{
							piece p3 = new piece(xk,yk,1-iColor);
							//if (p2.canReach(xk,yk,p3,cb))
							if (p2.canReach(i,j,p3,cb))
							{
								//System.out.println("DBG150530 can reach from:"+i+","+j + " piece:" + p2.dumpchr());
								if (iRet == 0) 
								{
									iRet = p2.pvalue();
									pRet = p2;
								}
								else if (p2.pvalue() < iRet) 
								{
									iRet = p2.pvalue();
									pRet = p2;
								}
							}
						}
					}
			/*}
			else
			{
				//System.out.println("ret 0 @ branch B");
				return 0;
			}*/
		}
		
		//System.out.println("moveindex.minPieceHittingBlock("+xk+","+yk+") returning: " + iRet);
		//return iRet;
		return pRet;
	}
	
	//int minOtherPieceHittingBlock( int xk, int yk, piece p, chessboard cb, move m)
	int minOtherPieceHittingBlockVal( int xk, int yk, piece p, chessboard cb, move m)
	{
		piece pr = minOtherPieceHittingBlock( xk, yk, p, cb, m);
		if (pr == null) return 0;
		else return pr.pvalue();
	}
	
	piece minOtherPieceHittingBlock( int xk, int yk, piece p, chessboard cb, move m)
	{
		int iRet = 0;
		piece pRet = null;
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			
			if ((mm.p.xk != p.xk) && (mm.p.yk != p.yk))
			{
				if ((mm.xtar == xk) && (mm.ytar == yk) && (mm.p.iType != piece.PAWN))
				{
					if (iRet == 0)
					{
						iRet = mm.p.pvalue();
						pRet = mm.p;
					}
					else if (mm.p.pvalue() < iRet) 
					{
						iRet = mm.p.pvalue();
						pRet = mm.p;
					}
					
				}
			}
		}
		
		//if (iRet != 0) return iRet;
		if (iRet != 0) return pRet;
		
		Vector vPieces;
		if (iColor == piece.WHITE) vPieces = cb.vWhites;
		else vPieces = cb.vBlacks;
		
		piece p3 = cb.blocks[xk][yk];
		cb.blocks[p.xk][p.yk]=null;
		if (p3==null) 
		{
			p3 = new pawn(xk,yk,p.iColor);
		}
		
		for (int i=0;i<vPieces.size();i++)
		{
			piece p2 = (piece)vPieces.elementAt(i);
			
			if (!((m.p.xk == p2.xk) && (m.p.yk == p2.yk)) && (p2.canReach(p2.xk,p2.yk,p3,cb)) && (!p2.bPinned)) 
			{
				//System.out.println("Can Reach" + p2.dumpStr() + " for move " + m.moveStr());
				if (iRet == 0)
				{
					iRet = p2.pvalue();
					pRet = p2;
				}
				else if (p2.pvalue() < iRet) 
				{
					iRet = p2.pvalue();
					pRet = p2;
				}
			}
		}
		
		//System.out.println("moveindex.minOtherPieceHittingBlock("+xk+","+yk+") returning: " + iRet);
		cb.blocks[p.xk][p.yk]=p;
		//return iRet;
		return pRet;
	}
	
	move getBestMove(move mFilter, moveindex mi_other)
	{
		move mBest = null;
		move mPain = null;
		Vector vPain = null;
		int iPressure = 0;
		boolean bPainResolved = false;
		
		CMonitor.inciMIndGetBest();
		
		
		
		if (bDebug)
		{
			if (mFilter != null) 
			{
				System.out.println("DBG150924: moveindex.getBestMove ENTER " + iColor + ". Filter:" + mFilter.moveStr());
			}
			else System.out.println("DBG150924: moveindex.getBestMove ENTER " + iColor + ". No filter.");
		}
		
		if (mi_other != null)
		{	
			mPain = mi_other.getBestCapture();
			if (mPain != null) iPressure = mPain.iNetCapture;
			
			if (bDebug) System.out.println("DBG150222: moveindex.getBestMove " + iColor +" mPain:" + mPain.moveStrLong());
			//if (mPain.bPawnProm()) System.out.println("PAWN PROM PAIN!!!!");
			
			vPain = mi_other.getBestCaptures();
			
			if (bDebug)
			{
				System.out.println("DBG150222: moveindex.getBestMove Pain vector size : " + vPain.size());
				if (vPain.size()>1)
				{
					for (int ii=0;ii<vPain.size();ii++)
					{
						move mpt = (move)vPain.elementAt(ii);
						System.out.println("DBG160202: pain vector @"+ii+":"+mpt.moveStr());
					}
				}
			}
		}
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			
			if (bDebug)
			{
				if (mPain == null) System.out.println("mPain == Null");
				else System.out.println("mpain: " + mPain.moveStrLong());
				
				
				System.out.print(mm.moveStr() + " @getBestMove, painresolved:" + bPainResolved );
				if (mBest != null) System.out.println(" mBest:" + mBest.moveStr());
				else System.out.println(" no mbest yet.");
			}
			
			if ((mm.bPassesFilter(mFilter,cb)) || 
			    (((mm.mSkewerMove != null) && ((mFilter.p.xk == mm.xtar) && (mFilter.p.yk == mm.ytar))) &&  // filter moves away 
				 ((mFilter.xtar != mm.p.xk) ||(mFilter.ytar != mm.p.yk)) &&  // filter doesn not kill moving piece
				 (mm.mSkewerMove.iNetCapture > 0))) // skewermove was better than filter
				 //(mFilter.iNetCapture < mm.iNetCapture))) // filter does not capture something valuable                            
			{
				if (bDebug) System.out.println("DBG151005: pfi:" +mm.moveStrLong() + " filterpass:" + mm.bPassesFilter(mFilter,cb));
				//if (mm.mSkewerMove!=null) System.out.println("DBG151217: skewer @ pfi: " + mm.mSkewerMove.moveStr());
				
				if (mm.mSkewerMove!=null)
				{
					if (!mm.bPassesFilter(mFilter,cb))
					{
						//System.out.println("DBG160201: Filter not passed, but skewer is there for " + mm.moveStr());
						piece tp = cb.blocks[mm.mSkewerMove.xtar][mm.mSkewerMove.ytar];
						if (mFilter.p.canReach(mFilter.xtar,mFilter.ytar,tp,cb))
						{
							//System.out.println("DBG160201: skewerproteceted as well for " + mm.moveStr());
							if (tp.pvalue() <= mm.p.pvalue()) mm.iNetCapture = 0;
							else
							{
								//System.out.println("DBG160302: case nloss52: tpvalue:" + tp.pvalue());
								mm.iNetCapture=Math.min(mm.iNetCapture,mm.mSkewerMove.iNetCapture);
							}
						}
						else
						{
							if (piece.directlyBetween(mm.p.xk,mm.p.yk,mFilter.xtar,mFilter.ytar,mm.mSkewerMove.xtar,mm.mSkewerMove.ytar)) mm.iNetCapture = 0;
							else mm.iNetCapture=Math.min(mm.iNetCapture,mm.mSkewerMove.iNetCapture);
							//System.out.println("DBG160208: should check XXXX for netcapture:" + mm.iNetCapture);
							//System.out.println("Move:" + mm.moveStr() + " skewer:" + mm.mSkewerMove.moveStr() + " filter: " + mFilter.moveStr());
							mm.mSkewerMove.moveStr();
						}
					}
				}
				
				if ((mm.bKCS) && (mFilter != null)) mm.processKCSAfterFilter(mFilter,cb);
				
				if (mBest == null)
				{
					mBest = mm;
					if (vPain != null) 
					{
						boolean bUnresolved = false;
						for (int j=0;j<vPain.size();j++)
						{
						
							move mP = (move)vPain.elementAt(j);
							
							if (!bMoveResolvesPain(mP,mm,cb))
							{
								bUnresolved = true;
							}
						
						}
						bPainResolved = !bUnresolved;
					}
				}
				else if (mm.isBetterMove(mBest, !bPainResolved, vPain, cb, this))
				{
					if (bDebug) System.out.println("bettermovepassed: " + mm.moveStr());
					if (iPressure == 0) mBest = mm;
					else
					{
						boolean bUnresolved = false;
						for (int j=0;j<vPain.size();j++)
						{
							move mP = (move)vPain.elementAt(j);
							
							if (!bMoveResolvesPain(mP,mm,cb))
							{
								bUnresolved = true;
								//System.out.println("unresolve pain for:" + mP.moveStr());
							}
							
						}
						//System.out.println("bUnresolved after loop:" + bUnresolved);
						if (!bUnresolved) 
						{
							bPainResolved = true;
							mBest = mm;
						}
					}
					
				}
				//else System.out.println("DBG160131: better move failed for :" + mm.moveStr());
			}
			/*
			{
				if (mm.mSkewerMove != null)
				{
					System.out.println("DBG150924: move failed filter but there's a skewer!");
					System.out.println("DBG150924: orignetcapt: " + mm.iNetCapture + "skewnetcapt: " + mm.mSkewerMove.iNetCapture);
				}
			}
			*/
		}
		//if (mBest == null) System.out.println("DBG150222: moveindex.getBestMove RETURN NULL MBEST!!!");
		//if (mBest != null )System.out.println("DBG150304: moveindex.getBestMove RETURN: " + mBest.moveStrLong() + " painresolved: " + bPainResolved);
		return mBest;
		
	}
	
	moveindex getEquallyGoodMoves(move mFilter, moveindex mi_other)
	{
		move mBest = getBestMove(mFilter,mi_other);
		moveindex mi_eqgood = new moveindex(iColor,cb);
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.bMoveLooksEquallyGood(mBest) && !(mm.bRisky && !mBest.bRisky)) mi_eqgood.addMove(mm);
		}
		
		return mi_eqgood;
	}
	
	move getAnyMove()
	{
		int count = getSize();
		int rand = (int)(Math.random()*count);
		
		return getMoveAt(rand);
	}
	
	move getBestCapture()
	{
		move mBest = null;
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			//System.out.println("DBG160208: getbestcapt:" + mm.moveStrLong());
			//if (mm.mSkewerMove != null) System.out.println("DBG160208:skewer is " + mm.mSkewerMove.moveStrLong());
			if (mBest == null) mBest = mm;
			else
			{	
				if ((mBest.iNetCapture > 100) && (mm.iNetCapture < 100))
				{
					if (mBest.mSkewerMove == null) mBest = mm;
					else if (mm.iNetCapture > mBest.mSkewerMove.iNetCapture) mBest = mm;
				}
				else if (mm.iNetCapture > mBest.iNetCapture) mBest = mm;
				
				/*
				System.out.println("netcapt:" + mm.iNetCapture);
				if ((mm.iNetCapture > mBest.iNetCapture) || ((mBest.iNetCapture > 100) && (mm.iNetCapture < 100))) mBest = mm;  // won't have king captures to baffle us here!	160124
				*/
			}
			
		}
		//System.out.println("DBG160124: moveindex.getBestCapture ret:" + mBest.iNetCapture + " of:" + mBest.moveStr());
		return mBest;
	}
	
	int getBestNetCapture()
	{
		move m = getBestCapture();
		if (m!=null) return m.iNetCapture;
		else return 0;
	}
	
	Vector getBestCaptures()
	{
		move mBest = null;
		Vector v = new Vector();
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (v.size() == 0)
			{
				mBest = mm;
				v.addElement(mm);
				//System.out.println("moveindex.getBestCaptures: first: " + mm.moveStrLong()+ " " + mm.iCoordHash());
			}
			//else if ((mm.iNetCapture > mBest.iNetCapture) || ((mBest.iNetCapture > 100) && (mm.iNetCapture < 100)))
			else if ((mBest.iNetCapture > 100) && (mm.iNetCapture < 100))
			{				
				if (mBest.mSkewerMove == null)
				{
					v = new Vector();
					mBest = mm;
					v.addElement(mm);
				}
				else if (mm.iNetCapture > mBest.iNetCapture)
				{
					v = new Vector();
					mBest = mm;
					v.addElement(mm);
				}
				//System.out.println("moveindex.getBestCaptures: newbest: " + mm.moveStrLong()+ " " + mm.iCoordHash());
			}
			else if (mm.iNetCapture > mBest.iNetCapture)
			{
				v = new Vector();
				mBest = mm;
				v.addElement(mm);
			}
			else if (mm.iNetCapture == mBest.iNetCapture)
			{
				mBest = mm;
				v.addElement(mm);
				//System.out.println("moveindex.getBestCaptures: equal: " + mm.moveStrLong() + " " + mm.iCoordHash());
			}
		}
		return v;
	}
	
	int getProcessedCount()
	{
		int iRet = 0;
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.isProcessed()) iRet++;
		}
		
		return iRet;
	}
	
	void resetSkewers()
	{
		//System.out.println("DBG150925: moveindex.resetSkewers: iColor:" + iColor);
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			mm.mSkewerMove = null;
		}
	}

	void sortByMoveOrder(String mOrder, boolean bRetainProc)
	{
		//System.out.println("DBG150312: moveindex.sortByMoveOrder() called: " + mOrder);
		String mOrdComp[] = mOrder.trim().split("\\s+");
		boolean bWarn = false;
		
		if (mOrdComp.length != getSize())
		{
			/*System.out.println("FATAL/W: moveindex.sortByMoveOrder() fatal error. size of sort string and index differ!" );
			System.out.println("FATAL/W: size:" + getSize());
			System.out.println("FATAL/W: mOrder:" + mOrder);
			System.out.println("FATAL/W: mOrder.length:" + mOrdComp.length);
			System.out.print("FATAL/W: ");
			dump();
			System.out.println(cb.FEN());
			System.exit(0);
			*/
			bWarn = true;
		}
		
		
		for (int i=0;i<mOrdComp.length;i++)
		{
			boolean bFound = false;
			for (int j=i;j<getSize();j++)
			{
				move mm = getMoveAt(j);
				if (mOrdComp[i].equals(mm.moveStrCaps()))
				{
					Collections.swap(v,i,j);
					bFound = true;
				}
				
			}
			if (!bFound) 
			{
				try { Thread.sleep(500); } catch  (Exception e) {}
				System.out.println("DBG151217: moveindex.sortByMoveOrder: FATAL/W: failed to find: >" + mOrdComp[i]+ "< , component:" +i);
				dump();
				System.out.println("DBG151217: Full morder: >" + mOrder+ "<");
				cb.dump();
				new Exception().printStackTrace();
				System.exit(0);
			}
		}
		iSortedTo = mOrdComp.length-1;
		/*
		if (bWarn)
		{
			System.out.println("DBG151130: WARNING! moveindex sort incomplete");
			System.out.println("DBG151130: " + mOrder);
			dump();
			System.exit(0);
		}*/
	}
	
	void mergeUnprocessedByOrder(String mOrder)
	{
		sortByMoveOrder(mOrder, true);
	}

	void sortByMoveOrder(String mOrder)
	{
		sortByMoveOrder(mOrder, false);
	}
	
	boolean bPreventPawnPromAt(int xpro)
	{
		int ypro;
		if (iColor == piece.WHITE) ypro = 1;
		else ypro = 8;
		
		//System.out.println("DBG151002: prev pawn prom at: " + xpro + " " + ypro);
		
		return false;
	}
	
	boolean bMoveResolvesPain(move mP, move mm, chessboard cb)
	{
		
		if (bDebug) 
		{
			System.out.println("DBG160113: bMoveResolvesPain called mP:" + mP.moveStrLong() + " mm: " + mm.moveStrLong());
			System.out.println("DBG160113: bMoveResolvesPain: escape:" + ((mm.p.xk == mP.xtar) && (mm.p.yk==mP.ytar)) );
		}
		
		
		if (mP.mSkewerMove != null) 
		{
			//System.out.print("DBG160201: " + mm.moveStr()+ " mSkewer:" + mP.mSkewerMove.moveStr());
			//System.out.println(": skewrespain: " + bMoveResolvesPain(mP.mSkewerMove,mm,cb));
			if ((mm.iNetCapture < mP.mSkewerMove.iNetCapture) && ((mm.p.xk == mP.xtar) && (mm.p.yk == mP.ytar)) && !bMoveResolvesPain(mP.mSkewerMove,mm,cb)) 
			{
				//System.out.println("DBG160322: bMoveResolvesPain ret:false @AAA for:" + mm.moveStr());
				return false;
			}
		}
		
		//boolean bPotReach = mP.p.bPotentiallyReachable(mP.xtar,mP.ytar,mm.xtar,mm.ytar);
		boolean bPotReach = mP.p.bPotentiallyReachable(mP.p.xk,mP.p.yk,mm.xtar,mm.ytar);
		
		//System.out.println("DBG160113: bMoveResolvesPain " + mm.moveStr() + ": pot reach:" + bPotReach );
		
		boolean bEscape = ((mm.p.xk == mP.xtar) && (mm.p.yk==mP.ytar));
		if (bEscape && bPotReach) 
		{
			//System.out.println("Not Setting netpressure to 0 for :" + mm.moveStrLong());
			//mm.iNetPressure = 0; 160415, dumb12bz (test076!)
		}
		if ((((mm.xtar == mP.p.xk) && (mm.ytar == mP.p.yk)) ||    // kill the pain
			(piece.directlyBetween(mP.p.xk,mP.p.yk,mm.xtar,mm.ytar,mP.xtar,mP.ytar)) ||   // move in between
			//((mP != null) && (mP.bPawnProm() && (mm.bCanPreventPawnProm(mP,cb)))) ||     // pawn prom
			(bEscape && !bPotReach))   // escape the pain
			&& !mm.bRisky)
		{
			//System.out.println("DBG160322: bMoveResolvesPain ret:true for:" + mm.moveStr());
			return true;
		}
		if ((mP != null) && mP.bPawnProm() && (mm.bCanPreventPawnProm(mP,cb)) && ((mm.p.pvalue() < 9) || (!mm.bRisky)))
		{
			// regardless of risk, prom is too big an issue!
			return true;
		}
		
		
		//if (mP!=null) System.out.println("DBG160210: bMoveResolvesPain:mP.bPawnProm():"+mP.bPawnProm()+ " can prevent:" + mm.bCanPreventPawnProm(mP,cb));
		
		//System.out.println("DBG160125: bMoveResolvesPain " + mm.moveStr() + " + still open A");	
		
		//if ((mm.xtar == 1) && (mm.ytar==4) && (mm.p.iType == piece.ROOK)) return true;
		piece tp = cb.blocks[mP.xtar][mP.ytar];
		if (tp == null) return false;
		/*
		System.out.println("DBG160125: bMoveResolvesPain XXXX:" + mm.moveStr() + " + still open B pain: " + mP.moveStr());
		System.out.println("DBG160322: bMRP: mp.capture:" + mP.iCaptValue);
		System.out.println("DBG160322: mp.p.pvalue:" + mP.p.pvalue());
		*/
		
		if ((mm.p.canReach(mm.xtar,mm.ytar,tp,cb)) &&  // add protection to threat
			((mm.p.xk != mP.xtar) || (mm.p.yk != mP.ytar)) && 
			(mP.p.pvalue() >= mP.iCaptValue )) return true;   
		
		//System.out.println("DBG160125: bMoveResolvesPain XXXX" + mm.moveStr() + " + still open C");

        return (mm.iNetCapture > 0) && (mm.iCaptValue > mP.iCaptValue);
		
		//System.out.println("DBG160113: bMoveResolvesPain called mP:" + mP.moveStr() + " mm: " + mm.moveStr() + " RETURN FALSE!");

    }
	
	int iLastInteresting()
	{
		int iRet = 0;
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.bIsInteresting()) iRet = i;
		}
		
		return iRet;
	}
	
	int iInterestingCount()
	{
		int iRet = 0;
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.bIsInteresting()) iRet++;
		}
		
		return iRet;
	}
	
	int iUnProcessedInterestingCount()
	{
		int iRet = 0;
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.bIsInteresting() && (!mm.isProcessed())) iRet++;
		}
		return iRet;
	}
	
	String dumpUnProcessedInteresting()
	{
		String sRet = "";
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.bIsInteresting() && (!mm.isProcessed())) sRet = sRet + mm.moveStrLong()+" ;";
		}
		return sRet;
	}
	
	moveindex getSortedByInterestCopy()
	{
		moveindex mi = new moveindex (iColor, cb);
		
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (mm.bIsInteresting())
			{
				mi.v.insertElementAt(mm,0);
			}
			else mi.v.addElement(mm);
		}
		
		return mi;
	}
	
	void raiseInterestingUnprocs()
	{
		if (iUnProcessedInterestingCount() == 0) return;
		System.out.println("DBG160308: raiseInterestingUnprocs() called. count:" + iUnProcessedInterestingCount() + " last interesting at:" + iLastInteresting());
		
		int iSwaps = 0;
		for (int i=0;i<getSize();i++)
		{
			move mm = getMoveAt(i);
			if (!mm.bIsInteresting() && (!mm.isProcessed())) 
			{
				for (int j=i;j<getSize();j++)
				{
					move m2 = getMoveAt(j);
					if (m2.bIsInteresting() && (!m2.isProcessed()))
					{
						System.out.println("DBG160308: RIO swapping " + i + "&" + j);
						Collections.swap(v,i,j);
						j=getSize();
						iSwaps++;
					}						
				}
			}
		}		
		System.out.println("DBG160308: raiseInterestingUnprocs() returns " + iSwaps + " swaps done.");
	}
	
	move findByString(String s)
	{
		for (int i=0;i<getSize();i++)
		{
			move mm=getMoveAt(i);
			if (mm.moveStr().substring(0,5).equals(s)) return mm;
		}
		
		return null;
	}
}