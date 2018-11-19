package kaappo.androidchess.askokaappochess;
public class hcdrawbon
{
	public static final int NO_CASE = 0;
	public static final int CASE_ROOK_VS_PAWN = 1;
	public static final int CASE_BISHOP_VS_BISHOPPAWN = 2;
	
	public static final int ROOK_COMPENSATION = 4;
	
	public static void main (String args[]) throws Exception
	{
		chessboard cb;
		
		if (args.length>0)
		{
			cb = new chessboard();
			cb.init_from_file(args[0], null);
			chessboard cb2 = cb.copy();
			engine.findAndDoMove(cb2,movevalue.ALG_ASK_FROM_ENGINE10, false, false);
			int iEngScore = engine.iLastScore;
			int iEngMateScore = engine.iLastMateScore;
				
			setHCDrawBon(cb, cb.iFileCol);
				
			System.out.print(args[0]+":DRAWBONRES: iEngScore:" + iEngScore + " iEngMateScore:" +iEngMateScore+ "  cb.iHCDrawBonusBal:"+ cb.iHCDrawBonusBal);
			if ((Math.abs(cb.iHCDrawBonusBal) > 0) && ((iEngScore >250) || (iEngMateScore > 0))) System.out.print(" ERROR");
			if ((Math.abs(cb.iHCDrawBonusBal) > 0) && ((iEngScore <-250) || (iEngMateScore < 0))) System.out.print(" ERROR");
			if ((cb.iHCDrawBonusBal == 0) && ((Math.abs(iEngScore) < 250) && (iEngMateScore == 0))) System.out.print(" ERROR");
			
			System.out.println();
			
			return;
		}
		
		
		while (true)
		{
			for (int iColor = 0; iColor <= 1; iColor++)
				for (int iTurn = 0; iTurn <= 1; iTurn++)
			{
				cb = random_init(iColor, iTurn, CASE_ROOK_VS_PAWN);
				cb.dump();
				
				chessboard cb2 = cb.copy();
				engine.findAndDoMove(cb2,movevalue.ALG_ASK_FROM_ENGINE10, false, false);
				int iEngScore = engine.iLastScore;
				int iEngMateScore = engine.iLastMateScore;
				
				setHCDrawBon(cb, iTurn);
				
				System.out.println("iEngScore:" + iEngScore + " iEngMateScore:" +iEngMateScore+ "  cb.iHCDrawBonusBal:"+ cb.iHCDrawBonusBal);
				

				if ((iEngScore == 0) && (iEngMateScore == 0) && (cb.iHCDrawBonusBal == 0)) {
					throw new RuntimeException("Error on row 55 of hcdrawbon.java");
				}
				
				switch (iColor)
				{
					case piece.WHITE:
						if ((cb.iHCDrawBonusBal < 0) && ((iEngScore >0) || (iEngMateScore > 0))) {
							throw new RuntimeException("Exception on row 62 of hcdrawbon.java");
						}
						if ((cb.iHCDrawBonusBal > 0) && ((iEngScore <0) || (iEngMateScore < 0))) {
							throw new RuntimeException("Exception on row 65 of hcdrawbon.java");
						}
						break;
						
					case piece.BLACK:
						if ((cb.iHCDrawBonusBal > 0) && ((iEngScore > 0) || (iEngMateScore > 0))) {
							throw new RuntimeException("Exception on row 71 of hcdrawbon.java");
						}
						if ((cb.iHCDrawBonusBal < 0) && ((iEngScore < 0) || (iEngMateScore < 0))) {
							throw new RuntimeException("Exception on row 74 of hcdrawbon.java");
						}
						break;	
					
				}
			}
		
		}
	}
	
	public static int HCDrawBonType(chessboard cb)
	{
		if ((cb.iBlackPieceCount[piece.QUEEN] == 0) && 
			(cb.iWhitePieceCount[piece.QUEEN] == 0) &&
			(cb.iBlackPieceCount[piece.BISHOP] == 0) && 
			(cb.iWhitePieceCount[piece.BISHOP] == 0) &&
			(cb.iBlackPieceCount[piece.KNIGHT] == 0) && 
			(cb.iWhitePieceCount[piece.KNIGHT] == 0) &&
			(cb.iWhitePieceCount[piece.ROOK] + cb.iBlackPieceCount[piece.ROOK] == 1) &&
			(cb.iWhitePieceCount[piece.PAWN] + cb.iBlackPieceCount[piece.PAWN] == 1)) return CASE_ROOK_VS_PAWN;
		
		return NO_CASE;
	}
	
	public static void setHCDrawBon(chessboard cb, int iTurn)
	{
		System.out.println("DBG160206:setHCDrawBon starts. iTurn:" + iTurn);
		//cb.redoVectorsAndCoverages(cb.iFileCol,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
		int iColor = cb.iFileCol;
		
		int iType = HCDrawBonType(cb);
		System.out.println("DBG160206:setHCDrawBon starts. iType:" + iType);
		switch (iType)
		{
			case CASE_ROOK_VS_PAWN:
				System.out.println("DBG160206:setHCDrawBon starts CASE_ROOK_VS_PAWN");
				
				if (cb.iWhitePieceCount[piece.ROOK] == 1) iColor = piece.WHITE;
				else iColor = piece.BLACK;
				
				king kOwn, kEnemy;
				rook rr = null;
				pawn pp = null;
				
				if (iColor == piece.WHITE)
				{
					kOwn = cb.locateKing(piece.WHITE);
					kEnemy = cb.locateKing(piece.BLACK);
				}
				else 
				{
					kOwn = cb.locateKing(piece.BLACK);
					kEnemy = cb.locateKing(piece.WHITE);
				}
				
				for (int i=1;i<=8;i++)
					for (int j=1;j<=8;j++)
				{
					if (cb.blocks[i][j] != null)
					{
						piece p = cb.blocks[i][j];
						if (p.iType == piece.ROOK) rr = (rook)cb.blocks[i][j];
						if (p.iType == piece.PAWN) pp = (pawn)cb.blocks[i][j];
					}
				}
				
				if ((iColor == iTurn) && (pp.bThreat) && (!pp.bProt)) return;
				if ((iColor != iTurn) && (rr.bThreat) && (!rr.bProt)) 
				{
					cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
					return;
				}
				
				System.out.println("kEnemy @" + kEnemy.xk +","+kEnemy.yk);
				
				if ((Math.abs(kEnemy.xk-pp.xk) <= 1) && (Math.abs(kEnemy.yk-pp.yk) <= 1)) 
				{
					System.out.println("setHCDrawBon / A");
					if (Math.signum(kEnemy.xk-pp.xk) != Math.signum(kOwn.xk-pp.xk)) 
					{
						System.out.println("setHCDrawBon / A0 (kenemy-pp <= 1), kEnemy.yk:" + kEnemy.yk);
						if (kEnemy.xk == pp.xk)
						{
							
							if (((iColor == piece.WHITE) && (kEnemy.yk<pp.yk) && (kEnemy.yk<=3)) ||
							    ((iColor == piece.BLACK) && (kEnemy.yk>pp.yk) && (kEnemy.yk >=6))) 
							{
								System.out.println("setHCDrawBon / AA");
								if (((Math.abs(kOwn.xk-pp.xk) >= 2) && (Math.abs(kOwn.yk-pp.yk) >= 2)) ||
									((pp.xk != rr.xk) && (pp.yk != rr.yk)))
								{
									int iEnturn = 0;
									if (iColor == iTurn) iEnturn = 1;
									
									System.out.println("setHCDrawBon / AA inside, iEnturn:" + iEnturn);
									// (Math.abs(kOwn.yk-pp.yk) > 1) lis hcd14.dat
									if ((Math.abs(rr.xk-pp.xk) == 1) && (Math.abs(pp.yk-kEnemy.yk) <= 1) && (Math.abs(kEnemy.xk-pp.xk) <= 2-iEnturn) && 
									((Math.abs(kOwn.yk-pp.yk) > 1) || (iEnturn == 0)))
									cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
									else System.out.println("no aai");
								}
								else System.out.println("... but no :(");
							}
							else
							{
								System.out.println("hcd91 branch");
								if (Math.abs(kOwn.xk-kEnemy.xk) > 3) cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
								else System.out.println("... but no a0");
							}
						}
						else
						{
							System.out.println("kenemy xk!=ppxk, diff sides, icolor:" + iColor + " iturn:" + iTurn+ " pp.yk:" + pp.yk);
							if (((iColor == piece.BLACK) && (pp.yk == 7)) ||
								((iColor == piece.WHITE) && (pp.yk == 2)))
							{
								System.out.println("But close to promo. icolor:" + iColor + " iTurn:" + iTurn);
								int iPromDist, pynext, pynext2;
								if (iColor==piece.BLACK) 
								{
									iPromDist = 8-pp.yk;
									pynext = pp.yk+1;
									pynext2= pp.yk+2;
									
								}
								else
								{
									iPromDist=pp.yk-1;
									pynext = pp.yk-1;
									pynext2 = pp.yk-2;
								}
								
								if ((iColor == iTurn) && 
									(Math.abs(kOwn.xk-pp.xk) <= 1) && 
									(Math.abs(kOwn.yk-pp.yk) <= 1) &&
									(rr.xk == pp.xk) || (rr.yk == pp.yk))
								{
									System.out.println("no (hcd68.dat).");
								}
								else
								{
									// hcd65 condition.
									if (iColor != iTurn) cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
									else if ((rr.xk != pp.xk) || (kEnemy.yk==pynext) || (Math.abs(kOwn.yk-pynext) > 3)) cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
									else System.out.println(" nopro (hcd65) ");
								}
										 
							}
							else
							{
								
								int iEnturn = 0;
								if (iColor != iTurn) iEnturn = 1;
								int iPromDist,pynext,pynext2;
								
								if (iColor==piece.BLACK) 
								{
									iPromDist = 8-pp.yk;
									pynext = pp.yk+1;
									pynext2= pp.yk+2;
									
								}
								else
								{
									iPromDist=pp.yk-1;
									pynext = pp.yk-1;
									pynext2 = pp.yk-2;
								}
								
								System.out.println("hcd92, hcd2b branch, iEnturn:" + iEnturn + " kOwn.yk:"+kOwn.yk+ " kEnemy.yk:" + kEnemy.yk);
								// 2-> 3-iEnturn, hcd92!
								if (Math.abs(kOwn.yk-kEnemy.yk) >= 3-iEnturn) cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
								else if ((pynext==kEnemy.yk) && 
										 ((iEnturn == 1) || (Math.abs(kOwn.xk-pp.xk) > 2)))
										 cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
								else
								{
								
									System.out.println("But no a000");
									cb.iHCDrawBonusBal = 0;
								}
							}
						}
					}
					else 
					{
						// both kings on same side
						
						int iPawnDistance;
						if (iColor == piece.WHITE) iPawnDistance = (pp.yk-1)*2;
						else iPawnDistance=(8-pp.yk) * 2;
						
						System.out.println("setHCDrawBon / B, pawndistance:" + iPawnDistance + " kEnDist:"+Math.abs(kEnemy.xk-pp.xk));
						
						if (iPawnDistance <= Math.max(Math.abs(kOwn.xk-pp.xk),Math.abs(kOwn.yk-pp.yk)))  cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
						else 
						{
							int iPromDist;
							int pynext, pynext2;
							if (iColor==piece.BLACK) 
							{
								iPromDist = 8-pp.yk;
								pynext = pp.yk+1;
								pynext2= pp.yk+2;
								
							}
							else
							{
								iPromDist=pp.yk-1;
								pynext = pp.yk-1;
								pynext2 = pp.yk-2;
							}
							if ((iPromDist == 1) && (iColor != iTurn) && (Math.abs(kEnemy.xk-pp.xk) == 1) && (Math.abs(kEnemy.yk-pynext) <= 1))
							{
								System.out.println(",case hcd11.dat");
								cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
							}
							else if ((iPromDist == 2) && (kEnemy.yk == pynext) && (Math.abs(kEnemy.xk-pp.xk) == 1))
							{
								System.out.println("Case hcd81.dat");
								cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
							}
						}
					}
				}
				else
				{
					
					int kEnDist = Math.max(Math.abs(kEnemy.xk-pp.xk),Math.abs(kEnemy.yk-pp.yk));
					int kOwnDist = Math.max(Math.abs(kOwn.xk-pp.xk),Math.abs(kOwn.yk-pp.yk));
					int iPromDist;
					int pynext, pynext2;
					if (iColor==piece.BLACK) 
					{
						iPromDist = 8-pp.yk;
						pynext = pp.yk+1;
						pynext2= pp.yk+2;
						
					}
					else
					{
						iPromDist=pp.yk-1;
						pynext = pp.yk-1;
						pynext2 = pp.yk-2;
					}
					System.out.println("kEnemy far far away...kEnDist:" + kEnDist + " kOwnDist:" + kOwnDist+ " iPromDist:" + iPromDist);
					if (iTurn != iColor) System.out.println("Enemy turn!");
					if (((kEnDist <= 3) && (iTurn != iColor)) ||(kEnDist <= 2)) 
					{
						
						int iEnturn = 0;
						if (iColor != iTurn) iEnturn = 1;
						System.out.println(".. there's hope  xxx!, enturn:"+iEnturn);
						
						if ((rr.xk != pp.xk) && 
							(Math.signum(rr.xk-pp.xk) == Math.signum(kEnemy.xk-pp.xk)) &&
							(Math.abs(rr.xk-pp.xk) > Math.abs(kEnemy.xk-pp.xk)) &&
							(iPromDist <= 3) && (iColor != iTurn))
						{
							System.out.println("eeek!");
							cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
						}
						else if ((((kEnDist-iEnturn <= 2) && (kOwnDist > kEnDist-iEnturn)) || 
								((kEnDist-iEnturn <= 3) && (kOwnDist > kEnDist+1-iEnturn))) &&
								(iPromDist <= 3) &&
								(Math.abs(pp.yk-kOwn.yk) >= 2-iEnturn))
						{
							System.out.println(",hope by proximity XXXa!");
							// hcd17.dat condition
							if ((Math.abs(kOwn.yk-pp.yk) > 1) || // hcd17
							    (Math.abs(kOwn.xk-pp.xk) > 1+iEnturn) || // hcd12
								(pp.xk != kEnemy.xk))   // hcd13
								cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
							else if ((iPromDist == 2) && (kEnemy.yk==pynext2) && (kEnemy.xk==pp.xk) && (Math.abs(kOwn.yk-pynext2) >= 3)) cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
							else System.out.println("but no! (hcd17/12/73 rule)");
						}
						//else if ((iColor != iTurn) && (kEnemy.xk == pp.xk) && (iPromDist == 2))
						// remove iColor != iTurn, HCD2E.dat 
						else if ((kEnemy.xk == pp.xk) && (iPromDist == 2) && 
								((iColor != iTurn) || (Math.abs(kOwn.xk-pp.xk) >= 3) ))
						{
							System.out.println(",hope draw threat.");
							cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
						}
						else if ((iColor != iTurn) && (pynext2 == rr.yk) && (Math.abs(pp.xk-rr.xk) ==1) && 
								 (Math.abs(kEnemy.xk-rr.xk) == 1) && ((Math.abs(kEnemy.yk-pynext) == 1)))
						{
							System.out.println(",case hcd4.dat");
							cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
						}
						else if ((kEnemy.yk == pynext2) && 
								((iColor != iTurn) || (Math.abs(kOwn.yk-pynext2) >= 3)))
						{
							System.out.println("hcd67 case");	
							cb.iHCDrawBonusBal = -ROOK_COMPENSATION;
						}	
						else System.out.println("...but no.");	
					}
				}
				return;
		}
		
	}

	static chessboard random_init(int iColor, int iTurn, int iMode)
	{
		System.out.println("Random_init: iColor:" + iColor + " iTurn:" + iTurn);
		chessboard cb = new chessboard();
		
		int okx = 1 + (int)(Math.random()*8);
		int oky = 1 + (int)(Math.random()*8);
		king k = new king (okx,oky,iColor);
		cb.putpiece(k);
		
		boolean bOK = false;
		int ekx = -1;
		int eky = -1;
		while (!bOK)
		{
			ekx = 1 + (int)(Math.random()*8);
			eky = 1 + (int)(Math.random()*8);
			if ((Math.abs(okx-ekx)>1) || (Math.abs(oky-eky)>1)) bOK = true;
		}
		
		k = new king (ekx,eky,1-iColor);
		cb.putpiece(k);
		
		switch (iMode)
		{
			case CASE_ROOK_VS_PAWN: 
				
				int rx = 1 + (int)(Math.random()*8);
				int ry = 1 + (int)(Math.random()*8);
				
				bOK = false;
				while (!bOK)
				{
					rx = 1 + (int)(Math.random()*8);
					ry = 1 + (int)(Math.random()*8);
					if (cb.blocks[rx][ry]==null) 
					{
						if (iTurn != iColor) bOK = true;
						else if ((rx != ekx) && (ry != eky)) bOK = true;
					}
				}
				
				rook r = new rook (rx,ry,iColor);
				cb.putpiece(r);
				
				int px = -1;
				int py = -1;
				
				bOK = false;
				while (!bOK)
				{
					px = 1 + (int)(Math.random()*8);
					py = 2 + (int)(Math.random()*6);
					if (cb.blocks[px][py]==null) 
					{
						if (iTurn == iColor) bOK = true;
						else 
						{
							//if (Math.abs(py-oky) > 1) bOK = true;
							if ((Math.abs(px-okx) > 1) || (Math.abs(px-okx) == 0)) bOK = true;
							if ((iTurn == piece.WHITE) && (py != oky-1)) bOK = true;
							if ((iTurn == piece.BLACK) && (py != oky+1)) bOK = true;
						}
						
					}
				}
				
				System.out.println("Putting pawn to: " + px +","+py + " ok@:" + okx + "," + oky);
				
				pawn p = new pawn (px,py,1-iColor);
				cb.putpiece(p);
				
				
				cb.iFileCol = iTurn;
				return cb;
				
			case CASE_BISHOP_VS_BISHOPPAWN:
				break;
				
		}
		
		return null;
	}
	
}