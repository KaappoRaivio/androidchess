package kaappo.androidchess.askokaappochess;
import java.util.*;

public class move
{
	int xtar;
	int ytar;
	boolean bCapture;
	boolean bCheck;
	boolean bRevCheck;
	int iCaptValue;
	piece p;
	boolean bRisky;
	boolean bPressure;
	boolean bKCS;
	int iNetCapture;
	int iNetPressure;
	boolean bProcessed;
	move mSkewerMove;
	boolean bPromPrevent;
	boolean bPawnFork;
	int iSafetyMargin;
	int iMMEnrMoveStat;
	int iPromTo;
	
	static final boolean bIsBetterMoveDebug = false;
	static final boolean bRiskAssDegug = false;
	
	move (int x, int y, boolean bc, int iC, piece pi)
	{
		xtar = x;
		ytar = y;
		bCapture = bc;
		bCheck = false;
		bRevCheck = false;
		iCaptValue = iC;
		p = pi;
		bRisky = false;
		bPressure = false;
		bKCS = false;
		iNetCapture = 0;
		iNetPressure = 0;
		bProcessed = false;
		bPromPrevent = false;
		iSafetyMargin = 0;
		iPromTo = -1;
		/*if (iC == 1000)
		{
			System.out.println("KING CAPTURE ATTEMPT!");
			System.exit(0);
		}
		*/
	}
	
	move copy()
	{
		move m = new move (xtar,ytar,bCapture,iCaptValue,p);
		m.bCheck = bCheck;
		
		m.bRevCheck = bRevCheck;

		m.bRisky = bRisky;
		m.bPressure =  bPressure;
		m.bKCS = bKCS;
		m.iNetCapture = iNetCapture;
		m.iNetPressure = iNetPressure;
		m.bProcessed = bProcessed;
		m.mSkewerMove = mSkewerMove;
		m.bPromPrevent = bPromPrevent;
		m.bPawnFork = bPawnFork;
		m.iSafetyMargin = iSafetyMargin;
		m.iMMEnrMoveStat = iMMEnrMoveStat;
		m.iPromTo = iPromTo;
		
		return m;
	}
	
	void setCheck(boolean b)
	{
		bCheck = b;
	}
	
	void setRevCheck(boolean b)
	{
		bRevCheck = b;
	}
	
	boolean isCheck()
	{
		return bCheck;
	}
	
	boolean isRevCheck()
	{
		return bRevCheck;
	}
	
	void setProcessed(boolean bValue)
	{
		bProcessed = bValue;
	}
	
	boolean isProcessed()
	{
		return bProcessed;
	}
	
	boolean equals(move m2)
	{
        return (xtar == m2.ytar) && (ytar == m2.ytar);
	}
	
	boolean samemove(move m2)
	{
        return moveStr().substring(0, 5).equals(m2.moveStr().substring(0, 5));
	}
	
	void analyzeCheck(piece p, chessboard cb)
	{
		//if ((p.iType == piece.KNIGHT) && (p.iColor == piece.BLACK)) 
		//System.out.println("DBG 151004: move.analyzeCheck() : "+ moveStr());
		int iColor = p.iColor;
		king kOpp = cb.locateKing(1-iColor);
		//System.out.println("DBG141221 (a)");
		if (p.iType != piece.KING)
		{
			if (p.canReach(xtar,ytar,kOpp,cb)) setCheck(true);
			//System.out.println("DBG141221 (b)");
		}
		if (p.revealsChecker(kOpp,cb,this)) setRevCheck(true);
		//System.out.println("DBG141221 (c)");
		
		if (p.iType == piece.KNIGHT)
		{
			if (bKCSCandidate(cb)) bKCS = true;
		}
		//if (isCheck() || (isRevCheck())) System.out.println("Check set! :" + isCheck() +" / " + isRevCheck());
	}
	
	boolean isSpecial()
	{
		if (bCheck || bRevCheck) return true;
		
		if ((p.iType == piece.PAWN) && ((ytar == 1) || (ytar == 8))) return true;

        return bKCS;

    }
	
	void analyzeRisk(piece p, chessboard cb)
	{
		boolean bOwnCoverage[][];
		boolean bOtherCoverage[][];
		moveindex miOther;
		moveindex miOwn;
		
		Vector vBCT;
		
		if (bRiskAssDegug)
		{
			System.out.println("DBG150124: enter move.analyzeRisk():" + moveStrLong() + " bEMS: " + this.p.bEscMoveisSafe(this) + " dir: " + this.p.iThrDirFlags);
			System.out.println("Enter analyze risk, piece at " + p.xk + "," + p.yk + " moveindex sizes w: " + cb.miWhiteMoveindex.getSize() + " b: " + cb.miBlackMoveindex.getSize());
		}
		
		if (!this.p.bEscMoveisSafe(this))
		{
			bRisky = true;
			return;
		}
		
		if (p.iColor == piece.WHITE)
		{
			bOwnCoverage=cb.bWhiteCoverage;
			bOtherCoverage=cb.bBlackCoverage;
			miOther = cb.miBlackMoveindex;
			miOwn = cb.miWhiteMoveindex;
		}
		else
		{
			bOwnCoverage=cb.bBlackCoverage;
			bOtherCoverage=cb.bWhiteCoverage;
			miOwn = cb.miBlackMoveindex;
			miOther = cb.miWhiteMoveindex;
		}
		
		if (cb.bMoveIsBCS(p, this)) bKCS = true;
		//System.out.println("DBG150128: risk ass: " + moveStr() + " bKCS:"+bKCS);
	
		if (p.bThreat)
		{
			//System.out.println("risk ass p.bThreat for:" + moveStr());
			if (!p.bProt)
			{
				bPressure = true;
				iNetPressure = p.pvalue();
				if (p.iType == piece.KING) iNetPressure = 0;
			}
			else
			{
				//System.out.println("DBG150304 (X) pressure for (thr+prot) " + moveStr() + " thrcount: " + p.iThreatCount + " protcount: " + p.iProtCount + " iminthreat:" + p.iMinThreat);
				if (p.iMinThreat < p.pvalue())
				{
					bPressure = true;
					iNetPressure = p.pvalue() - p.iMinThreat;
					if (p.iType == piece.KING) iNetPressure = 0;
				}
				if (p.iThreatCount > p.iProtCount)
				{
					bPressure = true;
					iNetPressure = 1; // $$$$ 150304 don't know natures of protecting pieces 
					if (p.iType == piece.KING) iNetPressure = 0;
				}
			}
			//System.out.println("risk ass p.bThreat done for:" + moveStrLong());
		}
		
		if (bCapture)
		{
			// check if there is risk in capture move
			//System.out.println("DBG150924: risk assesment " + moveStr() + " capture = true! pval:" + p.pvalue());
			//System.out.println("Moving piece pin: value:" + p.iPinValue + " direction: " + p.iPinDirection);
			//if (mSkewerMove != null) System.out.println("DBG150924: skewer: " + mSkewerMove.moveStr()); 
			
			piece pcap = cb.blocks[this.xtar][this.ytar];
			if (pcap == null)
			{
				if ((p.iType == piece.PAWN) && (((ytar == 6) && (p.iColor == piece.WHITE)) || ((ytar == 3) && (p.iColor == piece.BLACK)))) 
				{
					pcap = cb.blocks[this.xtar][p.yk];
				}
				else
				{
					cb.dump();
					System.out.println("MOVE: " + p.xk +"," + p.yk + " to " +xtar +"," +ytar);
					System.out.println("move.analyzeRisk() fails , bCapture = true, no piece");
					System.out.println(moveStrLong());
					new Exception().printStackTrace();
					System.exit(0);
				}
			}
			
			//if (pcap.pvalue() >= p.pvalue())   took too many risks :). ended to problems after 4 half-moves 141117
			// 141214: might be worthwhile to try with >= here and < in the condition below, too
			
			//System.out.println("DBG160124: AA1:" + moveStr());
			
			if (pcap == null)
			{
				cb.dump();
				System.out.println("DBG141227: move.analyzeRisk():pcap is null even though it shouldn't as this place. " + moveStr());
				System.out.println("Fatal error. Exiting.");
				System.exit(0);
			}
			
			//System.out.println("DBG160124: AA2:" + moveStr() + " pcap.pvalue(): " + pcap.pvalue() + " p.iPinValue:" + p.iPinValue);
			
			if ((pcap.pvalue() < p.iPinValue) && (p.iPinValue > 0))
			{
				if (((direction()-p.iPinDirection) % 4) != 0)
				{
					//System.out.println("DBG160302: " + moveStrLong() + " pinned XX move, could be risky!");
					boolean bPinValid = bPinIsValid(cb);
					if (bPinValid)
					{
						iNetCapture = 0;
						bRisky = true;
						//System.out.println("DBG160302: " + moveStrLong() + " pinned XX move, IS risky!");
					}
					else
					{
						if ((pcap.pvalue() < p.pvalue()) && (pcap.bProt))
						{
							iNetCapture = 0;
							bRisky = true;
						}
						else
						{
							iNetCapture = iCaptValue;
							if (iNetCapture > 0) p.iPosCaptureCount++;
							bRisky = false;
							//System.out.println("DBG160401: GGG:"+moveStrLong());
						}
					}
					return;
				}
			}
			
			//System.out.println("DBG160124: AA3:" + moveStr());
			
			if (pcap.pvalue() == p.iPinValue)
			{
				if (((direction()-p.iPinDirection) % 4) != 0)
				{
					boolean bPinValid = bPinIsValid(cb);
					if (bPinValid)
					{
						iNetCapture = 0;
						bRisky = true;
					}
					else
					{
						if ((pcap.pvalue() < p.pvalue()) && (pcap.bProt))
						{
							iNetCapture = 0;
							bRisky = true;
						}
						else
						{
							iNetCapture = iCaptValue;
							if (iNetCapture > 0) p.iPosCaptureCount++;
							bRisky = false;
							//System.out.println("DBG160401: GGG:"+moveStrLong());
						}
					}
					return;
				}
			}
			
			//System.out.println("DBG160124: AA4:" + moveStr());
			
			if (pcap.pvalue() > p.pvalue()) 
			{
				//System.out.println("DBG150609: BRANCH ZZ for: " + moveStr());
				bRisky = false;
				if (!pcap.bProt) 
				{
					iNetCapture = pcap.pvalue();
					if (iNetCapture > 0) p.iPosCaptureCount++;
					if (bKCS) addKCSToNetCapture(cb);
				}
				else
				{
					boolean bEPR = bEnemyPawnRisk(cb);
					boolean bOPP = bOwnPawnProt(cb);
					int iOwnPiece = miOwn.minOtherPieceHittingBlockVal(xtar,ytar,this.p, cb, this);
					int iEnemyPiece = miOther.minPieceHittingBlockVal(xtar,ytar, cb);
					
					//System.out.println("DBG150609: ZZ:"+ moveStr() +": bEPR:" + bEPR + " bOPP:" +bOPP + " iOwnPiece:" + iOwnPiece + " iEnemyPiece:" + iEnemyPiece+  " protcount:" + pcap.iProtCount );
					
					if (!bEPR && (iEnemyPiece >= iOwnPiece) && (iOwnPiece != 0))  iNetCapture = pcap.pvalue();
					else if ((pcap.iProtCount <=1) && (iOwnPiece > 0)) iNetCapture = pcap.pvalue();
					else iNetCapture = pcap.pvalue()-p.pvalue();
					if (iNetCapture > 0) p.iPosCaptureCount++;
				}
				return;
			}
			
			//System.out.println("DBG160124: kiekkuma!" + moveStrLong());
			
			if ((pcap.pvalue() <= p.pvalue()) && (!pcap.bProt))
			{
				bRisky = false;
				iNetCapture = pcap.pvalue();
				if ((p.iType == piece.PAWN) && ((ytar == 1) || (ytar ==8))) iNetCapture = 8;
				else if (bKCS) addKCSToNetCapture(cb);
				//System.out.println("DBG160125: risk ass GGG0 NOT RISKY!" + moveStrLong());
				if (iNetCapture > 0) p.iPosCaptureCount++;
				return;
			}
			
			if ((pcap.pvalue() == p.pvalue()) && (!pcap.bProt))
			{
				//System.out.println("DBG160125: risk ass GGG NOT RISKY!");
				iNetCapture = pcap.pvalue();
				bRisky = false;
				if ((p.iType == piece.PAWN) && ((ytar == 1) || (ytar ==8))) iNetCapture = 8;
				else if (bKCS) addKCSToNetCapture(cb);
				if (iNetCapture > 0) p.iPosCaptureCount++;
				return;
			}
			
			//if ((pcap.pvalue() < p.pvalue()) && (pcap.bProt)) took too many risks :). ended to problems after 4 half-moves 141117
			if ((pcap.pvalue() < p.pvalue()) && (pcap.bProt))
			{
				
				boolean bEPR = bEnemyPawnRisk(cb);
				boolean bOPP = bOwnPawnProt(cb);
				
				piece pOwn = miOwn.minOtherPieceHittingBlock(xtar,ytar,this.p, cb, this);
				int iOwnPiece = 0; 
				if (pOwn != null) iOwnPiece = pOwn.pvalue();
				piece pEnemy = miOther.minPieceHittingBlock(xtar,ytar, cb);
				int iEnemyPiece = 0;
				if (pEnemy != null) iEnemyPiece = pEnemy.pvalue();
				
				//System.out.println("DBG141225: iEnemyPiece ("+xtar+","+ytar+ ") = " + iEnemyPiece);
				
				//System.out.println("DBG xxxx risk ass. for prot small piece. Move:" + moveStr()+ " opp:"+iOwnPiece + " iep:" + iEnemyPiece + " bepr:" + bEPR + " bOPP:" + bOPP + " prot count: " + pcap.iProtCount + " prot flag:" + pcap.bProt);
				//if (pEnemy != null) System.out.println("pEnemy pinvalue:" + pEnemy.iPinValue);
				
				if ((!bOPP) && (iOwnPiece == 0))
				{
					//System.out.println("DBG150304 risk ass. for prot small piece. !bOPP branch Move:" + moveStr());
					bRisky = true;
					//System.out.println("DBG150207: it is risky (NO OWN PROT AT ALL) enemy prot count:" + pcap.iProtCount);
					if (pcap.iProtCount == 0)
					{
						//System.out.println("DBG150207: iProtCount ==0, probably pinned protector! NO RISK!");
						bRisky = false;
					}
					if ((pEnemy != null) && (pEnemy.iPinValue + pcap.pvalue() > p.pvalue()))
					{
						//System.out.println("DBG160124: " + moveStr() + " not risky because of pinned prot!");
						bRisky = false;
					}
					
					if (bRisky) iNetCapture = iCaptValue - p.pvalue();
					if (iNetCapture > 0) p.iPosCaptureCount++;
				}
				else if (!bEPR)
				{
					//System.out.println("DBG150304 risk ass. for prot small piece. !bEPR branch Move:" + moveStrLong() + " iOwnpiece:" + iOwnPiece + " bOPP:" + bOPP);
					if (iEnemyPiece == 0) 
					{
						System.out.println("CB MATE STATUS:");
						System.out.println("Moveindex sizes own: "+ miOwn.getSize() + ", other: " + miOther.getSize());
						System.out.println("Check states w: " + cb.bWhiteKingThreat + " b: " + cb.bBlackKingThreat);
						System.out.println(" FATAL ERROR. INCONSISTENCY @ move.analyzeRisk()");
						System.out.println("iEnemyPiece = " + iEnemyPiece);
						System.out.println("No piece protection found at " + xtar +"," + ytar + " for move " + moveStr());
						cb.dump();
						new Exception().printStackTrace();
						System.exit(0);
					}
					if (bOPP || (iOwnPiece > 3))
					{
						//System.out.println("DBG150609: OPP-branch!:" + iOwnPiece + " pcap.protcount:" + pcap.iProtCount);
						//if ((pcap.pvalue() + iOwnPiece) >= p.pvalue())
						if (pcap.iProtCount <= 1)
						{
							if ((!bOPP) && (iOwnPiece > pcap.pvalue() + iEnemyPiece))
							{
								int iStrikeBal = cb.iWhiteStrike[xtar][ytar]-cb.iBlackStrike[xtar][ytar];
								//System.out.println("DBG160203: risk ass FFF " + iStrikeBal + " iOwnPiece:" + iOwnPiece + " iEnemyPiece:" + iEnemyPiece+ " p.pvalue:" + p.pvalue());
								//System.out.println("Netcapt cand:" + (iCaptValue-p.pvalue()+iEnemyPiece));
								if (((p.iColor == piece.WHITE) && (iStrikeBal > 0)) || 
									((p.iColor == piece.BLACK) && (iStrikeBal < 0)))
								{
									iNetCapture = Math.min(iCaptValue,Math.max(0,iCaptValue-p.pvalue()+iEnemyPiece));
									if (iNetCapture > 0) p.iPosCaptureCount++;
									bRisky = false;
								}
								else
								{
									iNetCapture = 0;	
									bRisky = true;
								}
								
							}
							else
							{
								
								//bRisky = false;
								//System.out.println("DBG150207: is not risky. Capt can be protected. bOPP="+bOPP + " iOwnPiece="+iOwnPiece + " iCapt:" + iCaptValue + " p.pvalue():"+p.pvalue() + " iEnemyPiece:" + iEnemyPiece);
								//iNetCapture = iCaptValue - p.pvalue();
								//iNetCapture = iCaptValue;
								
								if (iCaptValue + iEnemyPiece < p.pvalue())
								{
									bRisky = true;
									iNetCapture = 0;
									//System.out.println("DBG160211: Setting risky true for RAMARAO!");
								}
								else
								{
									bRisky = false;
									iNetCapture = iCaptValue;
									if (iNetCapture > 0) p.iPosCaptureCount++;
								}
							}
						}
						else
						{
							//System.out.println("OPP-nonprocessed!!");
							//System.out.println("Enemypiece:" + iEnemyPiece);
							if ((bOPP) && (p.pvalue() < iEnemyPiece)) 
							{
								bRisky = false;
								iNetCapture = iCaptValue;
								if (iNetCapture > 0) p.iPosCaptureCount++;
							}
							else if ((iEnemyPiece > 0) && (p.pvalue() < iEnemyPiece))
							{
								//System.out.println("OPP new branch!");
								bRisky = true;
								iNetCapture = iCaptValue - p.pvalue();
								if (iNetCapture > 0) p.iPosCaptureCount++;
							}
						}
					}
					else if ((iOwnPiece > 1) && (p.pvalue() <= iEnemyPiece))
					{
						bRisky = false;
						iNetCapture = iCaptValue;
						if (iNetCapture > 0) p.iPosCaptureCount++;
						//System.out.println("DGB171106: opp>1 branch, iOwnPiece = "+iOwnPiece + " iEnemyPiece:"+iEnemyPiece + " p.pvalue():" + p.pvalue());
						if ((iCaptValue < iOwnPiece) && (cb.iWhiteStrike[xtar][ytar] == cb.iBlackStrike[xtar][ytar]))
						{
							//System.out.println("DBG171106: WE ARE HERE!!!");
							bRisky = true;
							iNetCapture = 0;
						}
						//pcap.bPressure = true;
						//pcap.iNetPressure = pcap.pvalue();
						// $$$ add pressure to all moves of pcap 150304
					}
					else
					{						
						bRisky = true;
						iNetCapture = iCaptValue - p.pvalue();
						//System.out.println(" is risky, prot not enough.");
					}
				}
				else if (pcap.iProtCount > 1)
				{
					//System.out.println("DBG150314: More than 1 protecting. Would it be risky?");
					bRisky = true;
					iNetCapture = iCaptValue - p.pvalue();
				}
				else if ((iOwnPiece > 0) && (pcap.iProtCount <= 1) && (!bEPR))
				{
					//System.out.println("XXXXX Branch for " + moveStr());
					bRisky = false;
					iNetCapture = iCaptValue;
					if (iNetCapture > 0) p.iPosCaptureCount++;
				}
				else if ((bEPR) && (iOwnPiece > 1))
				{
					bRisky = true;
					iNetCapture = iCaptValue - p.pvalue();
					//System.out.println("150603:NEW BRANCH!!!");
				}
				else 
				{
					bRisky = true;
					iNetCapture = iCaptValue - p.pvalue();
					//System.out.println(" is risky, EPR present.");
					//System.out.println("DBG150207: outside branches for "+moveStr()+ ". defaulting. protcount:" + pcap.iProtCount);
				}
				int iStrikeBal = cb.iWhiteStrike[xtar][ytar]-cb.iBlackStrike[xtar][ytar];
				//System.out.println("risk ass return ### for "+ moveStrLong() + "w:" + cb.iWhiteStrike[xtar][ytar]+ " b:" + cb.iBlackStrike[xtar][ytar]+" iStrikeBal:" +iStrikeBal);
				if (p.iColor == piece.WHITE) iSafetyMargin = iStrikeBal;
				else iSafetyMargin = -iStrikeBal;
				return;
			}
			//System.out.println("DBG160124: risk ass AXX for "+ moveStrLong());
			
			if ((pcap.pvalue() == p.pvalue())  && (pcap.bProt))
			{
				int iStrikeBal = cb.iWhiteStrike[xtar][ytar]-cb.iBlackStrike[xtar][ytar];
				//System.out.println("DBG151215: (riskass) " + moveStr() + " iStrikeBal:" + iStrikeBal + " whitestr:" + cb.iWhiteStrike[xtar][ytar] + " blackstr: " + cb.iBlackStrike[xtar][ytar]);
				
				if (p.iColor == piece.WHITE) iStrikeBal--;
				else iStrikeBal++;
				
				boolean bEPR = bEnemyPawnRisk(cb);
				boolean bOPP = bOwnPawnProt(cb);
				boolean bOwnBlock = false;
				
				if ((p.iColor == piece.WHITE) && (iStrikeBal > 0))  bOwnBlock = true;
				if ((p.iColor == piece.BLACK) && (iStrikeBal < 0))  bOwnBlock = true;
				
				/*if (miOwn==null) System.out.println("DBG150924: miOwn=null!" );
				if (this.p==null) System.out.println("DBG150924: this.p=null!");
				if (cb==null) System.out.println("DBG150924: cb==null"); */
				
				int iOwnPiece = miOwn.minOtherPieceHittingBlockVal(xtar,ytar,this.p, cb, this);
				int iEnemyPiece = miOther.minPieceHittingBlockVal(xtar,ytar, cb);
				
				
				//System.out.println("DBG150320: risk ass for loc XXXX " + moveStr() + " iStrikeBal:" + iStrikeBal + " epr: " + bEPR + " bOpp:" + bOPP + " bOwn:" + bOwnBlock + " iEnemyPiece:" + iEnemyPiece + " iOwnPiece:"+iOwnPiece);
				
				if ((bOwnBlock && !bEPR) || (!bEPR && (iEnemyPiece > iOwnPiece) && (iOwnPiece > 0)))  // condition 2 150530
				{
					if (iStrikeBal == 0)
					{
						//System.out.println(moveStr() + " COND A Strikebal 0");
					}
					
					//if (((p.iColor == piece.WHITE) && (iStrikeBal <= 0)) ||
					//((p.iColor == piece.BLACK) && (iStrikeBal >= 0)))
					if (((p.iColor == piece.WHITE) && (iStrikeBal >= 0)) ||    // were the <> signs wrong? 160331? 
						((p.iColor == piece.BLACK) && (iStrikeBal <= 0)))
					{
						iNetCapture = iCaptValue;
						bRisky = false;
						if (iStrikeBal == 0) iSafetyMargin = 1;
						//System.out.println(moveStr()+" COND A NO RISK");
					}
					else
					{
						//System.out.println(moveStr()+" COND RISK A, strikebal " + iStrikeBal);
						iNetCapture = 0;
						bRisky = false;  // 160415, dumb12bz.dat case
					}
					return;
				}
				if (!bOPP && !bEPR && (iEnemyPiece == iOwnPiece) && (iStrikeBal == 0))
				{
					iNetCapture = iCaptValue;
					bRisky = false;
					if (iNetCapture > 0) p.iPosCaptureCount++;
					//System.out.println(moveStr()+" COND Y");
					return;
				}
				
				if (!bOPP && !bEPR && (iStrikeBal == 0))
				{
					if (((p.iColor == piece.WHITE) && (cb.iBlackStrike[xtar][ytar] == 1)) ||
						((p.iColor == piece.BLACK) && (cb.iWhiteStrike[xtar][ytar] == 1)))
					iNetCapture = iCaptValue;
					bRisky = false;
					if (iNetCapture > 0) p.iPosCaptureCount++;
					//System.out.println(moveStr()+" COND Z");
					return;
				}
				
				if (!bOPP && !bEPR && 
					(((p.iColor == piece.WHITE) && (iStrikeBal > 0)) || 
					((p.iColor == piece.BLACK) && (iStrikeBal < 0)) ))
				{
					iNetCapture = iCaptValue;
					bRisky = false;
					if (iNetCapture > 0) p.iPosCaptureCount++;
					//System.out.println(moveStr()+" COND ZZ iStrikeBal:" + iStrikeBal);
					return;
				}
				
				if (((p.iColor == piece.WHITE) && (iStrikeBal < 0)) ||
					((p.iColor == piece.BLACK) && (iStrikeBal > 0)))
				{
					iNetCapture = iCaptValue - p.pvalue();
					bRisky = false;   // used to be true before, but painresolution conflicts with this one
					//System.out.println(moveStr()+" COND Risky by 160211 CHANGE iStrikeBal:" + iStrikeBal);
					if (iNetCapture > 0) p.iPosCaptureCount++;
					return;
				}
				
				//System.out.println(moveStr()+" nocond.");
				iNetCapture = iCaptValue;
				bRisky = false;
				if (iNetCapture > 0) p.iPosCaptureCount++;
				return;
				
			}
		}
		//System.out.println("DBG150403: risk ass for loc XX " + moveStr() + " bcheck:" + bCheck + " brevcheck:" + bRevCheck);
		
		if (bCheck || bRevCheck)
		{
			boolean bEPR = bEnemyPawnRisk(cb);
			boolean bOPP = bOwnPawnProt(cb);
			int iOwnPiece = miOwn.minOtherPieceHittingBlockVal(xtar,ytar,this.p, cb, this);
			int iEnemyPiece = miOther.minPieceHittingBlockVal(xtar,ytar, cb);
					
			int iStrikeBal = cb.iWhiteStrike[xtar][ytar]-cb.iBlackStrike[xtar][ytar];
			//System.out.println("DBG151215: (riskass at check) " + moveStr() + " iStrikeBal:" + iStrikeBal + " whitestr:" + cb.iWhiteStrike[xtar][ytar] + " blackstr: " + cb.iBlackStrike[xtar][ytar]);
				
			if (p.iColor == piece.WHITE) iStrikeBal--;
			else iStrikeBal++;		
					
			//System.out.println("DBG151005 CHECK:"+ moveStr() +": bEPR:" + bEPR + " bOPP:" +bOPP + " iOwnPiece:" + iOwnPiece + " iEnemyPiece:" + iEnemyPiece + " iStrikeBal: " + iStrikeBal);
			
			if (bEPR)
			{
				bRisky = true;
				iNetCapture = iCaptValue - p.pvalue();
				if (iNetCapture > 0) p.iPosCaptureCount++;
				return;
			}
			
			if (bOPP)
			{
				bRisky = false;
				iNetCapture = iCaptValue;
				if (iNetCapture > 0) p.iPosCaptureCount++;
				return;
			}
				
			if ((iOwnPiece > 0) && (iEnemyPiece > 0))
			{
				if (p.pvalue() < iEnemyPiece)
				{
					bRisky = false;
					if (iNetCapture > 0) p.iPosCaptureCount++;
					return;
				}
			}
			if (bKCS)
			{
				//System.out.println("KCS is there  for " + moveStrLong());
				if (((p.iColor == piece.WHITE) && (cb.iBlackStrike[xtar][ytar] == 0)) ||
				   ((p.iColor == piece.BLACK) && (cb.iWhiteStrike[xtar][ytar] == 0)))
				{
					bRisky = false;
					iNetCapture = 2;
					if (iNetCapture > 0) p.iPosCaptureCount++;
					return;
				}
				
			}
			//System.out.println("DBG kiihma:" + moveStr());	
			
			if (((p.iColor == piece.WHITE) && (iStrikeBal < 0)) ||
				((p.iColor == piece.BLACK) && (iStrikeBal > 0)))
			{
				bRisky = true;
				return;				
			}
			
			/*
			if (bOtherCoverage[this.xtar][this.ytar])
			{
				bRisky = true;
				iNetCapture = iCaptValue - p.pvalue();
				return;
			}
			*/
		}
		
		if (!bCapture && !bCheck && !bRevCheck)
		{
			//System.out.println("DBG150124: move.analyzeRisk() @ nocapture: " + moveStrLong());
			
			if (bOtherCoverage[this.xtar][this.ytar])
			{
				boolean bEPR = bEnemyPawnRisk(cb);
				boolean bOPP = bOwnPawnProt(cb);
				
				int minOth = miOther.minPieceHittingBlockVal(this.xtar,this.ytar, cb);
				int minOwn = miOwn.minOtherPieceHittingBlockVal(this.xtar,this.ytar,this.p, cb, this);
				
				//System.out.println("DBG150222: move.analyzeRisk() @ nocapture/othercovered. " + moveStr() + " ENTER: minOth = " + minOth + " minOwn: " + minOwn);
				
				if (bEPR) 
				{
					bRisky = true;
					return;
				}
				
				if ((bOPP) && (p.pvalue() < minOth))
				{
					if (bPawnFork)
					{
						//System.out.println("DBG 160113:SAFE PAWNFORK HERE!!! :" + moveStrLong());
						iNetCapture = 2;
					}
					//System.out.println("DBG160120: risk assesment GGG: " + moveStrLong());
					bRisky = false;
					if (iNetCapture > 0) p.iPosCaptureCount++;
					return;
				}
					
				if ((bPawnFork) && (p.iPinValue < 3))
				{
					int iStrikeBal = cb.iWhiteStrike[xtar][ytar]-cb.iBlackStrike[xtar][ytar];
					//System.out.println("DBG160214: PF check: iStrikeBal:" + iStrikeBal + " minOwn:" + minOwn + " minOth:" + minOth);
					pawn pa = (pawn)p;
					if (((p.iColor == piece.BLACK) && (iStrikeBal <= 0)) ||
					   ((p.iColor == piece.WHITE) && (iStrikeBal >= 0)) ||
					   (pa.bCoveredBehindbyQR(cb) && (Math.abs(iStrikeBal) <= 1))) 
					{
						iNetCapture = 2;
						bRisky = false;
						if (iNetCapture > 0) p.iPosCaptureCount++;
						//System.out.println("DBG160214: pawnfork detected for " + moveStrLong());
						return;
					}
				}		
					
				//System.out.println("DBG150222: move.analyzeRisk() @ nocapture/othercovered. " + moveStr() + " minOwn: " + minOwn);
				if (minOwn == 0)
				{
					bRisky = true;
					return;
				}
				
				if ((p.pvalue() > minOth) && (minOth > 0))
				{
					bRisky = true;
					return;
				}
				
				//System.out.println("DBG150124: risk assesment pawn risk!");
			
				/*
				if (bEnemyPawnRisk(cb)) 
				{
					bRisky = true;
					return;
				}
				
				if (miOther.minPieceHittingBlock(this.xtar,this.ytar) < miOwn.minOtherPieceHittingBlock(this.xtar,this.ytar,this.p))
					bRisky = true;
					return;
				*/
				
				
				
			}
			else if (bKCS) addKCSToNetCapture(cb);
			
			if ((p.iType == piece.PAWN) && ((ytar == 1) || (ytar ==8))) 
			{
				boolean bThreat = p.bThreat;
				
				//System.out.println("DBG160120: pawn prom risk ass:" + moveStr());
				
				if ((!bThreat) && (!bOtherCoverage[this.xtar][this.ytar])) iNetCapture = 8;
				else if (bOtherCoverage[this.xtar][this.ytar])
				{
					int minOth = miOther.minPieceHittingBlockVal(this.xtar,this.ytar, cb);
					int minOwn = miOwn.minOtherPieceHittingBlockVal(this.xtar,this.ytar,this.p, cb, this);
					//System.out.println("DBG150320: under threat pawn prom risk ass! " + moveStr() + " bThreat=" + bThreat + " bOtherCoverage: " + bOtherCoverage[this.xtar][this.ytar]+  " minOth: " + minOth + " minOwn: " + minOwn);
					if ((minOth > 0) && (minOwn > 0))
					{						
						if (minOth <= 9) iNetCapture = minOth - 1;
						else
						{
							iNetCapture = 8;
							//System.out.println("DBG160416: XXX");
							if (iNetCapture > 0) p.iPosCaptureCount++;
						}
					}  
					else if (minOwn > 0) iNetCapture = 8;
					//System.out.println("DBG160211: prom@riskass: inetcapture:" + iNetCapture);
				}
				else
				{
					boolean bThrBehind = false;
					if (p.yk == 7)
					{
						for (int j=6;j>=1;j--)
						{
							piece pp = cb.blocks[p.xk][j];
							if (pp!=null)
							{
								if ((pp.iColor != p.iColor) && ((pp.iType == piece.QUEEN) || (pp.iType == piece.ROOK))) bThrBehind = true;
								break;
							}
						}
					}
					else
					{
						for (int j=3;j<=8;j++)
						{
							piece pp = cb.blocks[p.xk][j];
							if (pp!=null)
							{
								if ((pp.iColor != p.iColor) && ((pp.iType == piece.QUEEN) || (pp.iType == piece.ROOK))) bThrBehind = true;
								break;
							}
						}
						
					}
					//System.out.println("DBG150320: behind threat:" + bThrBehind);
					if (bThrBehind) iNetCapture = 0;
					else iNetCapture = 8;
					if (iNetCapture > 0) p.iPosCaptureCount++;
				}
				
			}
			
		}
		
		//System.out.println("DBG150124: leave move.analyzeRisk() at end." + moveStrLong());
	}
	
	boolean isRisky ()
	{
		return bRisky;
	}
	
	String moveStr()
	{
		return p.dumpchr() + (char)(p.xk+96)+p.yk+(char)(xtar+96)+ytar ;
	}
	
	String moveStrCaps()
	{
		String sProm = "";
		switch (iPromTo)
		{
				
			case piece.QUEEN:
				sProm = "Q";
				break;
				
			case piece.ROOK:
				sProm = "R";
				break;
				
			case piece.BISHOP:
				sProm = "B";
				break;
				
			case piece.KNIGHT:
				sProm = "N";
				break;
			
			default:
				sProm = "";
				break;
		}
		
		return ""+(char)(p.xk+64)+p.yk+(char)(xtar+64)+ytar + sProm;
	}
	
	String moveStrLong()
	{
		char promc = ' ';
		switch (iPromTo)
		{
			case -1:
				promc = ' ';
				break;
				
			case piece.QUEEN:
				promc = 'Q';
				break;
				
			case piece.ROOK:
				promc = 'R';
				break;
				
			case piece.BISHOP:
				promc = 'B';
				break;
				
			case piece.KNIGHT:
				promc = 'N';
				break;
			
			default:
				promc = 'X';
				break;
		}
		
		String sRet = p.dumpchr() + (char)(p.xk+96)+p.yk+(char)(xtar+96)+ytar + promc + " ";
		if (bCheck) sRet = sRet + "X";
		else sRet = sRet + ".";
		if (bRevCheck) sRet = sRet + "X";
		else sRet = sRet + ".";
		if (isRisky()) sRet = sRet + "R";
		else sRet = sRet + ".";
		if (bPressure) sRet = sRet + "P";
		else sRet = sRet + ".";
		if (isSpecial()) sRet = sRet + "S";
		else sRet = sRet + ".";
		if (bKCS) sRet = sRet + "C";
		else sRet = sRet + ".";
		if (mSkewerMove != null) sRet = sRet + "s";
		else sRet = sRet + ".";
		if (bPawnFork) sRet = sRet + "f";
		else sRet = sRet + ".";
		
		sRet = sRet + " " + iCaptValue + " ";
		if (iNetCapture >= 0) sRet = sRet + " ";
		sRet = sRet + iNetCapture +"/"+ iNetPressure;
		sRet = sRet + "/" + iSafetyMargin;

		//if (mSkewerMove != null) sRet = sRet + "  " + mSkewerMove.moveStr();
		
		return sRet;
	}
	
	
	boolean bEnemyPawnRisk(chessboard cb)
	{
		int iEPy;
		piece p1 = null;
		piece p2 = null;
		
		if (p.iColor == piece.WHITE) iEPy = ytar+1;
		else iEPy = ytar-1;
		
		if ((iEPy < 1) || (iEPy > 8)) return false;
		
		if (xtar<8) p1 = cb.blocks[xtar+1][iEPy];
		if (xtar>1) p2 = cb.blocks[xtar-1][iEPy];
		
		if ((p1 != null) && (p1.iColor != p.iColor) && (p1.iType == piece.PAWN)) return true;
        return (p2 != null) && (p2.iColor != p.iColor) && (p2.iType == piece.PAWN);

    }
	
	boolean bOwnPawnProt(chessboard cb)
	{
		int iEPy;
		piece p1 = null;
		piece p2 = null;
		
		if (p.iColor == piece.WHITE) iEPy = ytar-1;
		else iEPy = ytar+1;
		
		//System.out.println("DBG 141227: move.bOwnPawnProt() to " + xtar + "," + ytar + " iEPy:" + iEPy + " piece from " + p.xk +"," + p.yk);
		
		if ((iEPy < 1) || (iEPy > 8)) return false;	
		
		if (xtar<8) p1 = cb.blocks[xtar+1][iEPy];
		if (xtar>1) p2 = cb.blocks[xtar-1][iEPy];
		
		if ((p1 != null) && (p1.xk == p.xk) && (p1.yk == p.yk)) p1 = null;
		if ((p2 != null) && (p2.xk == p.xk) && (p2.yk == p.yk)) p2 = null;
		
		//if ((p1 != null) || (p2 != null)) System.out.println("DBG 141227: move.bOwnPawnProt() non null p1 || p2 !!!!");
		
		if ((p1 != null) && (p1.iColor == p.iColor) && (p1.iType == piece.PAWN)) return true;
        return (p2 != null) && (p2.iColor == p.iColor) && (p2.iType == piece.PAWN);
		
		//System.out.println("DBG 141227: move.bOwnPawnProt() is false");

    }
	
	boolean bKCSCandidate(chessboard cb)
	{
		int iKCSP = 0;
		
		if (bKCSPieceAt(this.xtar+2,this.ytar+1, cb)) iKCSP++;
		if (bKCSPieceAt(this.xtar+2,this.ytar-1, cb)) iKCSP++;
		if (bKCSPieceAt(this.xtar-2,this.ytar+1, cb)) iKCSP++;
		if (bKCSPieceAt(this.xtar-2,this.ytar-1, cb)) iKCSP++;
		if (bKCSPieceAt(this.xtar+1,this.ytar+2, cb)) iKCSP++;
		if (bKCSPieceAt(this.xtar+1,this.ytar-2, cb)) iKCSP++;
		if (bKCSPieceAt(this.xtar-1,this.ytar+2, cb)) iKCSP++;
		if (bKCSPieceAt(this.xtar-1,this.ytar-2, cb)) iKCSP++;

        return iKCSP >= 2;
	}
	
	boolean bKCSPieceAt(int xk, int yk, chessboard cb)
	{
		if ((xk < 1) || (xk > 8) || (yk < 1) || (yk > 8)) return false;
        return (cb.blocks[xk][yk] != null) && (cb.blocks[xk][yk].iColor != this.p.iColor) && (cb.blocks[xk][yk].pvalue() >= 5);
	}
	
	boolean isBetterMove(move mOth, boolean bConsiderPressure, Vector vPain, chessboard cb, moveindex mi)
	{
		if (bIsBetterMoveDebug)
		{
			System.out.println("DBG150318: move.isBetterMove A: " + moveStrLong() + " / " + mOth.moveStrLong() + " //  " + (iNetCapture + iNetPressure) + " vs. " + (mOth.iNetCapture + mOth.iNetPressure ) +  " bCP:" + bConsiderPressure);
			System.out.println("mOth (capt:)" + mOth.iCaptValue + " netcapt:"+ mOth.iNetCapture +   " (press:) " + mOth.iNetPressure); 
		}
		
		//if (iNetPressure > 100) iNetPressure = 0;
		//if (mOth.iNetPressure > 100) mOth.iNetPressure = 0;
		
		if ((vPain != null) && (vPain.size() == 1) && (iNetCapture == 0) && (mOth.iNetCapture == 0) && (iNetPressure > 0) && (mOth.iNetPressure > 0))
		{
			move mP = (move)vPain.elementAt(0);
			boolean bResolve = mi.bMoveResolvesPain(mP,this,cb);
			boolean bCand = false;
			if ((xtar == mP.p.xk) && (ytar == mP.p.yk) && (!bRisky)) bCand = true;
			//System.out.println("DBG160212: SCENARIO HAPPENS, bResolve:" + bResolve+ " bCand:" + bCand);
			if (bResolve && bCand) return true;
		}
		
		//System.out.println("move.isBetterMove B: " + moveStrLong() + " vs. " +  mOth.moveStrLong());
		
		if (bConsiderPressure)
		{
			//System.out.println("DBG160113:move.isBetterMove A1 for :" + moveStr());
			if ((iNetCapture + iNetPressure) > (mOth.iNetCapture + mOth.iNetPressure)) return true;
			
			/*
			System.out.println("DBG160113:move.isBetterMove A2 for :" + moveStr());
			if (vPain == null) System.out.println("DBG160202:move.isBetterMove A2b vPain == null");
			else System.out.println("DBG160202:vPain.size=" + vPain.size() + " paincommtarget: " + commonPainVectorTarget(vPain));
			*/
			
			if ((vPain != null) && ((vPain.size() == 1) || (commonPainVectorTarget(vPain))))
			{
				
				move mP = (move)vPain.elementAt(0);
				//System.out.println("DBG160113:move.isBetterMove AA2 for :" + moveStr() + " mPain: " + mP.moveStrLong());
				if (vPain.size()==1)
				{
					if (!bRisky && piece.directlyBetween(mP.p.xk,mP.p.yk,xtar,ytar,mP.xtar, mP.ytar) && (mP.iNetCapture > mOth.iNetCapture)) return true;  // relieves pain by moving in between
					}
				
				if (!bRisky && (mP.bPawnProm() && (bCanPreventPawnProm(mP,cb)))) return true;  // relieves pain by preventing pawn prom
				
				//System.out.println("DBG160113:move.isBetterMove AA21 for :" + moveStr());
				//System.out.println("DBG160113:move.isBetterMove A2A for :" + moveStr() + " bR:" + bRisky + " bCR: " + p.canReach(xtar,ytar,tp,cb) + " " + xtar+","+ytar+"->"+mP.xtar+","+mP.ytar + " mP:" + mP.moveStr());
				if ((mOth.iNetCapture > mP.iNetCapture) && (iNetCapture <= mP.iNetCapture)) return false;
				
				//System.out.println("DBG160113:move.isBetterMove AA22 for :" + moveStr());
				
				// need to resolve pain by protecting piece under threat
				piece tp = cb.blocks[mP.xtar][mP.ytar];
				if (!bRisky && (tp != null) && p.canReach(xtar,ytar,tp,cb) && (!p.canReach(p.xk,p.yk,tp,cb)))
				{
					
					/*System.out.println("DBG160209: bettermove YUYU! " + moveStr() +" netcapture: " + iNetCapture);
					System.out.println("DBG160209: bettermove YUYU! " + mOth.moveStr() + " other netcapture: " + mOth.iNetCapture);
					System.out.println("DBG160209: bettermove YUYU! mP move: " + mP.moveStrLong() + " capt:"+mP.iNetCapture);
					*/	
					if ((mOth.xtar == mP.p.xk) && (mOth.ytar == mP.p.yk) && (mOth.iNetCapture + mP.iNetCapture > iNetCapture)) return false;
					//System.out.println("DBG160209: bettermove YUYU! X1");
					//if ((xtar == mP.p.xk) && (ytar == mP.p.yk) && (iNetCapture + mP.iNetCapture > mOth.iNetCapture)) return true;
					if (mP.iNetCapture + iNetCapture > Math.max(mOth.iNetCapture, mOth.iNetPressure)) return true;
					
					if ((mP.mSkewerMove != null) && (tp.pvalue() <= mP.p.pvalue())) return true;  // 160513 fiksi
					
				}
				
				//if (tp != null) System.out.println("DBG160113:move.isBetterMove A2B for :" + moveStr() + " bR:" + bRisky + " bCR: " + p.canReach(xtar,ytar,tp,cb) + " " + xtar+","+ytar+"->"+mP.xtar+","+mP.ytar);
				
				if ((xtar == mP.p.xk) && (ytar == mP.p.yk)) 
				{
					//System.out.println("DBG160209:" + moveStr() + " captures pain piece");
					//System.out.println("DBG160209:" + moveStr() + " inetcapt:" + iNetCapture + " mpinetcapt:" + mP.iNetCapture + " oth netcapt" + mOth.iNetCapture);
					if ((iNetCapture + mP.iNetCapture > mOth.iNetCapture) && ((mOth.xtar != mP.p.xk) || (mOth.ytar != mP.p.yk)))
					{
						//System.out.println("DBG160209:" + moveStr() + " and it's better move too.");
						return true;
					}
				}
			
				
			}
			
			//System.out.println("DBG160113:move.isBetterMove A3 for :" + moveStr());
			
			if ((iNetCapture + iNetPressure) < (mOth.iNetCapture + mOth.iNetPressure)) return false;
			
			//System.out.println("move.isBetterMove B: " + moveStr() + " / " + mOth.moveStr());
		}
		else
		{
			if (iNetCapture > mOth.iNetCapture) return true;
			if (iNetCapture < mOth.iNetCapture) return false;
		}
		if (!bRisky && mOth.bRisky) return true;
		if (bRisky && !mOth.bRisky) return false;
		//System.out.println("move.isBetterMove C: " + moveStr() + " / " + mOth.moveStr());
		if ((bCheck || bRevCheck) && !(mOth.bCheck || mOth.bRevCheck)) return true;
		if (!(bCheck || bRevCheck) && (mOth.bCheck || mOth.bRevCheck)) return false;
		
		if (iNetCapture > mOth.iNetCapture) return true;
		if (iNetCapture < mOth.iNetCapture) return false;
		
		if (iNetPressure > mOth.iNetPressure) return true;
		if (iNetPressure < mOth.iNetPressure) return false;
		
		//System.out.println("move.isBetterMove D: " + moveStr() + " / " + mOth.moveStr());
		if (p.iType == piece.PAWN) return true;
		if (mOth.p.iType == piece.PAWN) return false;
		
		//if (Math.random() > 0.5f) return false;
		piece pOwnCap = cb.blocks[xtar][ytar];
		piece pOtherCap = cb.blocks[mOth.xtar][mOth.ytar];

		if ((pOwnCap != null) && (pOtherCap!=null))
		{
			if (bIsBetterMoveDebug) 
			{
				System.out.println("move.isBetterMove owncap vs othercap check for: " + moveStr() + " vs." + mOth.moveStr());
				
				System.out.println("owncap iPosCaptureCount:" + pOwnCap.iPosCaptureCount);
				System.out.println("othercap iPosCaptureCount:" + pOtherCap.iPosCaptureCount);
			}
			
			if (pOwnCap.iPosCaptureCount > pOtherCap.iPosCaptureCount) return true;
			else return pOwnCap.iPosCaptureCount >= pOtherCap.iPosCaptureCount;
		}
		
		return true;
	}
	
	boolean bMoveLooksEquallyGood(move mOth)
	{
		//System.out.println("DBG151023: bMoveLooksEquallyGood " + moveStr() + " mOth:" + mOth.moveStr());
        return iNetCapture + iNetPressure == mOth.iNetCapture + mOth.iNetPressure;
	}
	
	boolean bPassesFilter (move mFilter, chessboard cb)
	{
		
		if (mFilter == null) return true;
		/*
		System.out.println("bPassesFilter, filter: " + mFilter.moveStr() + " bpromprev:" + mFilter.bPromPrevent + " for move: " + moveStr() + " pawnprom:" + bPawnProm());
		System.out.println("bPassesFilter, filter: " + mFilter.moveStr() + " bpromprev func:" + bCanPreventPawnProm(mFilter,cb) + " for move: " + moveStr() + " pawnprom:" + bPawnProm());
		*/
		
		//if ((mFilter.bPromPrevent) && bPawnProm()) return false;
		if ((bPawnProm()) &&((bCanPreventPawnProm(mFilter,cb) || (mFilter.bPromPrevent)) )) return false;
		
		//System.out.println("DBG 150909: bPassesFilter called for " + moveStrLong() + "  by Filter:" + mFilter.moveStr());
		
		// other side checked, we might not have moves
		if ((mFilter.bCheck) || (mFilter.bRevCheck)) return false;
		
		// other side captured us, we can't move this piece
		if ((p.xk == mFilter.xtar) && (p.yk == mFilter.ytar)) return false;
		
		// our target piece went away, this move doesn't pass
		if ((xtar == mFilter.p.xk) &&(ytar == mFilter.p.yk)) 
		{
			//System.out.println("DBG160125: bPassesFilter, parallel check");
			if (!bParallelWith(mFilter) || (p.iType == piece.PAWN) || (p.iType==piece.KING)) return false;
			//return false;
			//else  System.out.println("parallel escape attempt:" +mFilter.moveStr() + " move:" + moveStr()); 
			// target moved to become protected!!!! 160125
			
		}
		
		
		// low-value target piece became protected, change > to >=
		if (p.pvalue() >= iCaptValue) 
		{
			piece tp = cb.blocks[xtar][ytar];
			if ((tp != null) && (mFilter.p.canReach(mFilter.xtar,mFilter.ytar,tp,cb))) return false;
		}
		
		
		
		if ((p.iType == piece.PAWN) || (p.iType == piece.KNIGHT) || (p.iType == piece.KING)) return true; 
		
		if ((p.iType == piece.QUEEN) || (p.iType == piece.ROOK))
		{
			if ((p.xk == mFilter.xtar) && (p.xk == xtar) && bInMiddle(mFilter.ytar,p.yk,ytar)) return false;
			
			if ((p.yk == mFilter.ytar) && (p.yk == ytar) && bInMiddle(mFilter.xtar,p.xk,xtar)) return false;
		}

		if (iSafetyMargin == 1)
		{
			// figure out if filter killed crucial protection
			piece pd = cb.blocks[mFilter.xtar][mFilter.ytar];  // piece filter killed
			if ((pd != null) && (pd.iColor == p.iColor))	
			{
				piece pThr = cb.blocks[xtar][ytar]; // piece we try capture
				
				//System.out.println("DBG160209: SUSPICIOUS FILTER CHECK ***** for filter:" + mFilter.moveStr() + " move:" + moveStrLong() + " pd.itype:" + pd.iType + " CR: " + pd.canReach(pd.xk,pd.yk,pThr,cb));
				if ( pd.canReach(pd.xk,pd.yk,pThr,cb)) return false;
			}
		}

		
		if ((p.iType == piece.QUEEN) || (p.iType == piece.BISHOP))
		{
			//System.out.println("fb1");
			if ((!bInMiddle(mFilter.ytar,p.yk,ytar)) && (!bInMiddle(mFilter.xtar,p.xk,xtar))) return true;
			
			//System.out.println("fb2");
			
			if (Math.abs(mFilter.xtar-xtar) != Math.abs(mFilter.ytar-ytar)) return true;
			if (Math.abs(mFilter.xtar-p.xk) != Math.abs(mFilter.ytar-p.yk)) return true;
            return Math.abs(xtar - p.xk) != Math.abs(ytar - p.yk);
			//System.out.println("fb4");
        }
		//System.out.println("fx");
		

		//System.out.println("DBG160209: filter pass true for : " + moveStrLong());
		return true;
	}
	
	boolean bInMiddle(int iM, int i1, int i2)
	{
		if ((iM>i1) && (iM<i2)) return true;
        return (iM > i2) && (iM < i1);
    }
	
	boolean bParallelWith (move m)
	{
		if (m == null) return false;
		
		if ((p.iType == piece.KNIGHT) || (m.p.iType == piece.KNIGHT)) return false;
		
		int xd = xtar-p.xk;
		int yd = ytar-p.yk;
		int xmd = m.xtar-m.p.xk;
		int ymd = m.ytar-m.p.yk;
		
		if ((Math.signum(xd) == Math.signum(xmd)) && (Math.signum (yd) == Math.signum(ymd))) 
		{
			if ((xd == 0) || (yd== 0)) return true;
            return (Math.abs(xd) == Math.abs(yd)) && (Math.abs(xmd) == Math.abs(ymd));
		}
		return false;
	}
	
	boolean bPawnProm ()
	{
		if (p.iType != piece.PAWN) return false;
		if ((p.iColor == piece.WHITE) && (ytar ==8)) return true;
        return (p.iColor == piece.BLACK) && (ytar == 1);
    }
	
	boolean bCanPreventPawnProm(move mProm, chessboard cb)
	{
		//System.out.println("DBG150103: bCanPreventPawnProm enter for " + moveStr() + " prommove: " + mProm.moveStr());
		CMonitor.inciMoveCPPawnPProm();
		
		if ((xtar==mProm.xtar) && (ytar==mProm.ytar)) // return true;
		{
			//System.out.println("bCanPreventPawnProm: ret: true:");
			bPromPrevent = true;
			return true;
		}
		
		if ((xtar==mProm.p.xk) && (ytar==mProm.p.yk)) // return true;
		{
			//System.out.println("bCanPreventPawnProm by capture: ret: true:");
			bPromPrevent = true;
			return true;
		}
		
		
		//System.out.println("DBG150103: bCanPreventPawnProm (S2) enter for " + moveStr() + " prommove: " + mProm.moveStr());
		
		if (mProm == null) return true;
		pawn pa = new pawn(mProm.xtar, mProm.ytar,mProm.p.iColor);
		
		bPromPrevent = p.canReach(xtar,ytar,pa,cb);
		if ( !bPromPrevent && (xtar==mProm.xtar) && ((p.iType == piece.ROOK) || (p.iType == piece.QUEEN)))
		{
			bPromPrevent = p.canReach(xtar,ytar,mProm.p,cb);
			//System.out.println("DBG160417:Check again promprevent by new code. Now:" + bPromPrevent);
			
		}
		
		//System.out.println("DBG160210: bPromPrevent:" + bPromPrevent);
		if (bPromPrevent)
		{	
			// can reach prom square from target
			//System.out.println("bPromPrevent triv branch");
			// this branch will check protection from behind for promoting piece
			int iStep;
			if (mProm.p.iColor == piece.BLACK) iStep = 1;
			else iStep = -1;
			int jj = mProm.p.yk;
			boolean bCont = true;
			while ((bCont) && (jj > 1) && ( jj < 8))
			{
				jj = jj + iStep;
				piece p = cb.blocks[mProm.p.xk][jj];
				if (p != null)
				{
					if ((p.iColor == mProm.p.iColor) && 
						((p.iType == piece.ROOK) || (p.iType==piece.QUEEN)) &&
						(jj != ytar)) bPromPrevent = false; 
					bCont = false;
				}
			}
			//System.out.println("bPromPrevent triv branch ret:" + bPromPrevent);
			return bPromPrevent;
		}
		
		int iDir = piece.getDir(mProm.xtar,mProm.ytar,p.xk,p.yk);
		
		if (iDir == -1) return false;
		
		// check if move revealed another piece to protect promotion block
		boolean bMoverFound = false;
		//System.out.println("Looking for solution at dir: " + iDir + " from piece at " + p.xk + "," + p.yk);
		for (int i = 1; i <=7; i++)
		{
			int movX[] = {0,1,1,1,0,-1,-1,-1};
			int movY[] = {1,1,0,-1,-1,-1,0,1};
			
			int xs = mProm.xtar + i*movX[iDir];
			int ys = mProm.ytar + i*movY[iDir];
			
			if ((xs <1) || (xs > 8) || (ys < 1) || (ys > 8)) return false;
			
			piece px = cb.blocks[xs][ys];
			
			//System.out.println("...loop :" + xs +","+ys + " moverfound:" + bMoverFound);
			
			if (px!=null)
			{
				if (px.iColor == mProm.p.iColor) return false;
				
				if (bMoverFound && p.iColor != mProm.p.iColor)
				{
					if ((px.iType == piece.ROOK) && (iDir%2)==0) bPromPrevent = true;
					if ((px.iType == piece.BISHOP) && (iDir%2)==1) bPromPrevent = true;
					if (px.iType == piece.QUEEN) bPromPrevent = true;
					//System.out.println("Returning " + bPromPrevent + " from " + xs + "," + ys);
					return bPromPrevent;
				}
				
				if ((xs == p.xk) && (ys == p.yk)) bMoverFound = true;
			}
			
		}
		
		
		return bPromPrevent;
	}
	
	int iCoordHash()
	{
		int x1,y1,x2,y2;
		
		x1 = p.xk;
		x2 = xtar;
		if (p.iColor == piece.WHITE) 
		{
			y1 = p.yk;
			y2 = ytar;
		}
		else
		{			
			y1 = 9-p.yk;
			y2 = 9-ytar;
		}
		
		return x1 * 1000 + y1 * 100 + x2 * 10 + y2;
		
	}
	
	boolean fully_equals(move m2)
	{
		if (m2 == null) return false;
        return (p.xk == m2.p.xk) && (p.yk == m2.p.yk) && (xtar == m2.xtar) && (ytar == m2.ytar);
    }
	
	int direction ()
	{
		return piece.getDir(p.xk,p.yk,xtar,ytar);
	}
	
	boolean commonPainVectorTarget(Vector vPain)
	{
		if (vPain.size() == 1) return true;
		
		int xk, yk;
		
		move m0 = (move)vPain.elementAt(0);
		xk = m0.xtar;
		yk = m0.ytar;
		
		for (int ii=1;ii<vPain.size();ii++)
		{
			move mpt = (move)vPain.elementAt(ii);
			if ((mpt.xtar != xk) || (mpt.ytar != yk)) return false;
		}
		
		return true;
	}
	
	int iSkeweredNetCapture()
	{
		if (mSkewerMove == null) return iNetCapture;
		else return mSkewerMove.iNetCapture;
	}
	
	void addKCSToNetCapture(chessboard cb)
	{
		int iDiffX[] = {2,2,-2,-2,1,1,-1,-1};
		int iDiffY[] = {1,-1,1,-1,2,-2,2,-2};
		
		if (p.iType != piece.KNIGHT) return;
		
		//System.out.println("DBG160210: addKCSToNetCapture starts." + moveStrLong());
		
		int iKCSBon = 4;
		int iRooks = 0;
		int iQueens = 0;
		boolean bKing = false;
		
		for (int i=0;i<8;i++)
		{
			//int nx = p.xk+iDiffX[i];
			int nx = xtar+iDiffX[i];
			//int ny = p.yk+iDiffY[i];
			int ny = ytar+iDiffY[i];
			if ((nx>=1) && (nx <=8) && (ny >=1) && (ny <=8))
			{
				piece p1R = cb.blocks[nx][ny];
				if (p1R != null)
				{
					//System.out.println("DBG 160327: piece@"+nx+","+ny+ " = "+p1R.dumpchr());
					if  ((p1R.bProt) && (p1R.iType == piece.ROOK) && (p1R.iColor != p.iColor)) iKCSBon = 2;
					if ((p1R.iType == piece.ROOK) && (p1R.iColor != p.iColor)) iRooks++;
					if ((p1R.iType == piece.QUEEN) && (p1R.iColor != p.iColor)) iQueens++;
					if ((p1R.iType == piece.KING) && (p1R.iColor != p.iColor)) bKing = false;
				}
				//else System.out.println("DBG 160327: piece@"+nx+","+ny+ " is null");
			}
		}
		if (iRooks == 0) iKCSBon = 6;
		
		
		iNetCapture = iNetCapture+iKCSBon;
		
		
		//System.out.println("DBG160210: addKCSToNetCapture: bon:" + iKCSBon+ " iRooks:" + iRooks + " iQueens: " + iQueens + " bKing:" + bKing);
		//System.out.println("DBG160210: addKCSToNetCapture " + moveStrLong());
	}
	
	boolean bPinIsValid(chessboard cb)
	{
		int movX[] = {0,1,1,1,0,-1,-1,-1};
		int movY[] = {1,1,0,-1,-1,-1,0,1};
		
		boolean bRet = true;
		
		//System.out.println("DBG160210: bPinIsValid pindirection:" + p.iPinDirection);
		//System.out.println("DBG160210: bPinIsValid pinvalue:" + p.iPinValue);
		
		int ii = p.xk+movX[p.iPinDirection];
		int jj = p.yk+movY[p.iPinDirection];
        boolean bCont = true;
		
		piece pPin = null;
		piece pPinner = null;
		
		while (bCont && (ii>=1) && (ii<=8) && (jj>=1) && (jj<=8))
		{
			pPin = cb.blocks[ii][jj];
			if (pPin != null) 
			{
				//System.out.println("DBG160210: destination for pin found at:"+ii+","+jj);
				bCont = false;
			}
			ii = ii+movX[p.iPinDirection];
			jj = jj+movY[p.iPinDirection];
		}
		
		int iPinnerDirection = (p.iPinDirection + 4) % 8;
		
		ii = p.xk+movX[iPinnerDirection];
		jj = p.yk+movY[iPinnerDirection];
		bCont = true;
		
		while (bCont && (ii>=1) && (ii<=8) && (jj>=1) && (jj<=8))
		{
			pPinner = cb.blocks[ii][jj];
			if (pPinner != null) 
			{
				//System.out.println("DBG160210: pinning piece found at:"+ii+","+jj);
				bCont = false;
			}
			ii = ii+movX[iPinnerDirection];
			jj = jj+movY[iPinnerDirection];
		}
		
		if ((pPin.pvalue() <= pPinner.pvalue()) && (pPin.bProt)) bRet = false;
		
		//System.out.println("DBG160210: pindest value:" + pPin.pvalue() + " bProt:" + pPin.bProt + " pPinner value:" + pPinner.pvalue() + " returns: " + bRet);
		
		return bRet;
	}
	
	boolean bIsInteresting()
	{
		if (iNetCapture < 0) return false;
		if (iNetCapture > 0) return true;
		if (iNetPressure > 0) return true;
        return isCheck() || isRevCheck();
    }
	
	void processKCSAfterFilter(move mFilter, chessboard cb)
	{
		if (!bCapture && (iNetCapture > 0)) 
		{
			//System.out.println("DBG160411: processKCSAfterFilter move: " + moveStrLong() + " filter: " + mFilter.moveStrLong()+ " possibly work to do.");
			if ((p.canReach(p.xk,p.yk,mFilter.p,cb)) && (mFilter.p.pvalue() >= 5))
			{
				//System.out.println("YAY!!");
				iNetCapture = 0;
			}
		}
	}
	
	int kingNalimov()
	{
		if (p.iType != piece.KING) return chessboard.NALI_NONE;
		
		if ((xtar == 5) && (ytar <= 4)) return chessboard.NALI_VERT;
		if ((xtar == 5) && (ytar == 5)) return chessboard.NALI_HORI_VERT;
		if ((xtar == 4) && (ytar == 5)) return chessboard.NALI_HORI;
		if ((xtar == 3) && (ytar == 5)) return chessboard.NALI_VERT_DIAG;
		if ((xtar <= 3) && (ytar <= xtar+2) && (ytar > xtar)) return chessboard.NALI_DIAG;
		return chessboard.NALI_NONE;
	}
	
	boolean bIsMidPawnOpener()
	{
		if ((p.iType != piece.PAWN) || (p.xk <4) || (p.xk > 5)) return false;
		if ((p.iColor == piece.WHITE) && (p.yk != 2)) return false;
        return (p.iColor != piece.BLACK) || (p.yk == 7);
    }
	
	boolean bIsPawnPressureOpener(chessboard cb)
	{
		if ((p.iType != piece.PAWN) || ((p.yk != 2) && (p.yk != 7 ))) return false;
		if ((p.iColor == piece.WHITE) && (p.yk != 2)) return false;
		if ((p.iColor == piece.BLACK) && (p.yk != 7)) return false;
		
		int iYDest;
		if (p.iColor == piece.WHITE) iYDest = ytar+1;
		else iYDest =ytar-1;
		
		piece pl = null;
		piece pr = null;
		
		if (xtar > 1) pl = cb.blocks[xtar-1][iYDest];
		if (xtar < 8) pr = cb.blocks[xtar+1][iYDest];
		
		if ((pl != null) && (pl.iColor != p.iColor) && (pl.iType != piece.PAWN)) return true;
        return (pr != null) && (pr.iColor != p.iColor) && (pr.iType != piece.PAWN);

    }
	
	boolean bIsPrepForFianchetto(chessboard cb)
	{
		if (p.iType != piece.PAWN) return false;
		
		int reqYtar, reqRookX, reqRookY,reqBishX,reqBishY,reqKnightX,reqKnightY;
		
		if ((p.iColor == piece.WHITE) && (p.xk == 2))
		{
			reqYtar = 3;
			reqRookX = 1;
			reqRookY = 1;
			reqBishX = 3;
		}
		else if ((p.iColor == piece.WHITE) && (p.xk == 7))
		{
			reqYtar = 3;
			reqRookX = 8;
			reqRookY = 1;
			reqBishX = 6;
		}
		else if ((p.iColor == piece.BLACK) && (p.xk == 2))
		{
			reqYtar = 6;
			reqRookX = 1;
			reqRookY = 8;
			reqBishX = 3;
		}
		else if ((p.iColor == piece.BLACK) && (p.xk == 7))
		{
			reqYtar = 6;
			reqRookX = 8;
			reqRookY = 8;
			reqBishX = 6;
		}
		else return false;
		
		if (ytar != reqYtar) return false;
		
		reqBishY = reqRookY;
		reqKnightX = reqBishX;
		reqKnightY = reqYtar;
		
		piece prook = cb.blocks[reqRookX][reqRookY];
		piece pbish = cb.blocks[reqBishX][reqBishY];
		piece pknight = cb.blocks[reqKnightX][reqKnightY];
		
		if ((prook == null) || (prook.iType != piece.ROOK) || (prook.iColor != p.iColor)) return false;
		if ((pbish == null) || (pbish.iType != piece.BISHOP) || (pbish.iColor != p.iColor)) return false;
		if ((pknight == null) || (pknight.iType != piece.KNIGHT) || (pknight.iColor != p.iColor)) return false;
		
		for (int ix=reqRookX;ix<=reqBishX;ix++)
		{
			piece ppawn=cb.blocks[ix][p.yk];
			if ((ppawn == null) || (ppawn.iType != piece.PAWN) || (ppawn.iColor != p.iColor)) return false;
		}
		
		return true;
	}
	
	boolean bIsBishopE3Opener()
	{
		if (p.iType != piece.BISHOP) return false;
		if ((p.iColor == piece.WHITE) && (p.yk != 1)) return false;
		if ((p.iColor == piece.BLACK) && (p.yk != 8)) return false;
		if ((xtar != 4) && (xtar != 5)) return false;
		if ((p.iColor == piece.WHITE) && (ytar != 3)) return false;
        return (p.iColor != piece.BLACK) || (ytar == 6);
    }
	
	boolean bIsF2StepOpener(chessboard cb)
	{
		if (p.iType != piece.PAWN) return false;
		if (p.xk != 6) return false;
		if (((p.iColor == piece.WHITE) && (p.yk == 2) && (ytar == 4)) ||
		    ((p.iColor == piece.BLACK) && (p.yk == 7) && (ytar == 5)))
		{
			// it's a candidate!
			//System.out.println("** bIsF2StepOpener candidate:"+moveStr());
			//System.out.println("** Coverages: WHITE:" + cb.iWhiteStrike[p.xk][ytar] + " BLACK:" + cb.iBlackStrike[p.xk][ytar]);
			if ((p.iColor == piece.BLACK) && ((cb.iBlackStrike[p.xk][ytar] == 0) || (cb.iBlackStrike[p.xk][ytar] < cb.iWhiteStrike[p.xk][ytar]))) return false;
            return (p.iColor != piece.WHITE) || ((cb.iWhiteStrike[p.xk][ytar] != 0) && (cb.iBlackStrike[p.xk][ytar] <= cb.iWhiteStrike[p.xk][ytar]));
        }
		else return false;
	}
	
	boolean bIsPawnFrontOpener(chessboard cb)
	{
		if (p.iType != piece.PAWN) return false;
		if ((p.xk == 1) || (p.xk == 8)) return false;
		
		if ((ytar >= 7) || (ytar <= 2)) return false;
		
		piece pl = cb.blocks[p.xk-1][ytar-1];
		piece pr = cb.blocks[p.xk+1][ytar+1];
		if ((pl != null) && (pr != null) && 
		    (pl.iType == piece.PAWN) && (pl.iColor == p.iColor) &&
			(pr.iType == piece.PAWN) && (pr.iColor == p.iColor)) return true;
			
		pl = cb.blocks[p.xk-1][ytar+1];
		pr = cb.blocks[p.xk+1][ytar-1];
        return (pl != null) && (pr != null) &&
                (pl.iType == piece.PAWN) && (pl.iColor == p.iColor) &&
                (pr.iType == piece.PAWN) && (pr.iColor == p.iColor);

    }
	
	boolean bIsBackRowRook(chessboard cb)
	{
		if (cb.iMovedPiecesFromStart() < 12) return false;  // could be optimized and not done for all moves :) 180108
		
		if (p.iType != piece.ROOK) return false;
		if (p.yk != ytar) return false;
		if ((p.iColor == piece.WHITE) && (ytar != 1)) return false;
        return (p.iColor != piece.BLACK) || (ytar == 8);
    }
	
	boolean bIsKnightToMiddle()
	{
		if (p.iType != piece.KNIGHT) return false;
		if ((xtar < 4) || (xtar > 5)) return false;
        return (ytar >= 4) && (ytar <= 5);
    }
	
	boolean isQueenFirstMove(chessboard cb)
	{
		if (cb.iMovedPiecesFromStart() < 11) return false;  // could be optimized and not done for all moves :) 180116
		
		if (p.iType != piece.QUEEN) return false;
		if (p.xk != 4) return false;
		if ((p.iColor == piece.WHITE) && (p.yk != 1)) return false;
        return (p.iColor != piece.BLACK) || (p.yk == 8);

    }
	
	boolean bIsC2StepOpener(chessboard cb)
	{
		if (p.iType != piece.PAWN) return false;
		if (p.xk != 3) return false;
		if (((p.iColor == piece.WHITE) && (p.yk == 2) && (ytar == 4)) ||
		    ((p.iColor == piece.BLACK) && (p.yk == 7) && (ytar == 5)))
		{
			// it's a candidate!
			//System.out.println("** bIsF2StepOpener candidate:"+moveStr());
			//System.out.println("** Coverages: WHITE:" + cb.iWhiteStrike[p.xk][ytar] + " BLACK:" + cb.iBlackStrike[p.xk][ytar]);
			if ((p.iColor == piece.BLACK) && ((cb.iBlackStrike[p.xk][ytar] == 0) || (cb.iBlackStrike[p.xk][ytar] < cb.iWhiteStrike[p.xk][ytar]))) return false;
            return (p.iColor != piece.WHITE) || ((cb.iWhiteStrike[p.xk][ytar] != 0) && (cb.iBlackStrike[p.xk][ytar] <= cb.iWhiteStrike[p.xk][ytar]));
        }
		else return false;
	}
	
	boolean bIsBishopF4Opener(chessboard cb)
	{
		if (cb.iMovedPiecesFromStart() < 6) return false;
		
		if (p.iType != piece.BISHOP) return false;
		if ((p.iColor == piece.WHITE) && (p.yk != 1)) return false;
		if ((p.iColor == piece.BLACK) && (p.yk != 8)) return false;
		if ((xtar != 3) && (xtar != 6)) return false;
		if ((p.iColor == piece.WHITE) && (ytar != 4)) return false;
        return (p.iColor != piece.BLACK) || (ytar == 5);
    }
	
}	