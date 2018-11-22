package kaappo.androidchess.askokaappochess;
import java.sql.*;

public class nalimov
{
	public static Connection dbconn;
	static String dbString = "jdbc:solid://localhost:2500/dba/dba";
	static PreparedStatement sInsNal;
	static PreparedStatement sUpdNal;
	
	public static void main (String args[]) throws Exception
	{
		Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
	
		dbconn = DriverManager.getConnection(dbString);
		dbconn.setAutoCommit(false);
		sInsNal = dbconn.prepareStatement("insert into nalimov (fen) values (?)");
		sUpdNal = dbconn.prepareStatement("update nalimov set dtm = ? where fen like ?");
		
		String sPar = args[0];
		
		if (sPar.equals("POP")) populate2Bishops();
		if (sPar.equals("DTM")) populateByDTM(args);
		//testIt();
	}
	
	public static void populate2Bishops () throws Exception
	{
		System.out.println("Nalimov 2BishPop starts at " + new Timestamp(System.currentTimeMillis()) + ".");
		
		int iMatePos = 0;
		int iBlackPos = 0;
		int iWhitePos = 0;
		
		for (int bKing = 0; bKing < 64; bKing++)
		{
			int bkx = ixk(bKing);
			int bky = iyk(bKing);
			System.out.println("bkx: " + bkx + " bky: " + bky);
			
			
			
			if (bBkingOK(bkx,bky)) 
			{
				System.out.println("OK for black king bkx: " + bkx + " bky: " + bky);
				for (int wKing = 0; wKing < 64; wKing++)
				{
					int wkx = ixk(wKing);
					int wky = iyk(wKing);
					
					if  (bWkingOK(wkx, wky, bkx, bky ))
					{
						System.out.println("OK for white king wkx: " + wkx + " wky: " + wky);
						for (int wBishopW = 0; wBishopW < 64; wBishopW++)
						{
							int wbx = ixk(wBishopW);
							int wby = iyk(wBishopW);
							
							if ((((wbx+wby) % 2) == 1) && ((wbx != wkx) || (wby != wky)) && ((wbx != bkx) || (wby != bky)) )
							{
								System.out.println("Wbishop OK " + wbx + "," + wby);
								for (int wBishopB = 0; wBishopB < 64; wBishopB++)
								{
									int bbx = ixk(wBishopB);
									int bby = iyk(wBishopB);
									if ((((bbx+bby) % 2) == 0) && ((bbx != wkx) || (bby != wky)) && ((bbx != bkx) || (bby != bky)) )
									{
										System.out.println("Position found:");
										System.out.println("POS: Black king: " + bkx + "," + bky + " White king: " + wkx +"," + wky + " BishopW: " + wbx + "," + wby + " BishopB: " + bbx + "," + bby);
										chessboard cb = new chessboard();
										king kb = new king(bkx,bky, Piece.BLACK);
										cb.putpiece(kb);
										king kw = new king(wkx,wky, Piece.WHITE);
										cb.putpiece(kw);
										bishop bw = new bishop(wbx,wby, Piece.WHITE);
										cb.putpiece(bw);
										bishop bb = new bishop(bbx,bby, Piece.WHITE);
										cb.putpiece(bb);
										
										/*
										cb.dump();
										chessboard cbf = cb.nali_flip(chessboard.NALI_VERT);
										System.out.println("Nali vert flip:");
										cbf.dump();
										cbf = cb.nali_flip(chessboard.NALI_HORI);
										System.out.println("Nali hori flip:");
										cbf.dump();
										cbf = cb.nali_flip(chessboard.NALI_DIAG);
										System.out.println("Nali diag flip:");
										cbf.dump();
										*/
										
										System.out.println("Black FEN:" + cb.FEN());
										iBlackPos++;
										String sBlackFEN = cb.FEN();
										cb.redoVectorsAndCoverages(Piece.WHITE,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
										
										sInsNal.setString(1,sBlackFEN);
										sInsNal.executeUpdate();
										dbconn.commit();
										
										
										//cb.prefixdump("",chessboard.DUMPMODE_FULL);
										if ((cb.bBlackKingThreat) && (cb.iBlackMoves == 0)) 
										{
											System.out.println("It's a checkmate: " + cb.FEN());
											iMatePos++;
											
											sUpdNal.setInt(1,0);
											sUpdNal.setString(2,cb.FEN());
											sUpdNal.executeUpdate();
											dbconn.commit();
											
											//processCheckMate(cb);
										}
										System.out.println("---");
										
										if ((((bkx - bky ) == (wbx-wby)) || ((bkx-bky) == (bbx-bby))) &&
										    (!Piece.directlyBetween(bkx,bky,wkx,wky,wbx,wby) &&
											 !Piece.directlyBetween(bkx,bky,wkx,wky,bbx,bby)))
										{
											System.out.println("White turn not ok! POS: Black king: " + bkx + "," + bky + " White king: " + wkx +"," + wky + " BishopW: " + wbx + "," + wby + " BishopB: " + bbx + "," + bby);
										}
										else
										{	
											String sWhiteFEN = cb.FEN().replaceAll(" b "," w ");
											System.out.println("White FEN:" + sWhiteFEN);
											
											sInsNal.setString(1,sWhiteFEN);
											sInsNal.executeUpdate();
											dbconn.commit();
											
											iWhitePos++;
										}
										
									}
									else System.out.println("Bbishop NOK : " +bbx +","+bby);
								}
								
							}
						}
					}
					//else System.out.println(".... NOK for white king wkx: " + wkx + " wky: " + wky);
				}
			}
		}
		System.out.println("Nalimov 2BishPop done at " + new Timestamp(System.currentTimeMillis()) + ".  iMatePos: " + iMatePos + " iBlackPos: " + iBlackPos + " iWhitePos: " + iWhitePos);
		

	}
	
	static int ixk(int coord)
	{
		return (coord%8)+1;
	}
	
	static int iyk(int coord)
	{
		return (coord / 8) + 1;
	}
	
	static boolean bBkingOK(int x, int y)
	{
        return (x <= 4) && (y <= 4) && (y <= x);
	}
	
	static boolean bWkingOK(int wkx, int wky, int bkx, int bky )
	{
        return (Math.abs(wkx - bkx) > 1) || (Math.abs(wky - bky) > 1);
	}

	static void processCheckMate(chessboard cb, int iDTM) throws Exception
	{
		System.out.println("processCheckMate called.");
		moveindex mi = cb.miWhiteMoveindex;
		
		
		cb.dump();
		mi.dump(true);
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			System.out.println("Processing move:" + m.moveStr());
			king kb = cb.locateKing(Piece.BLACK);
			if ((m.xtar == kb.xk) && (m.ytar == kb.yk)) System.out.println ("King cap!");
			else 
			{
				chessboard cb2 = cb.copy();
				cb2.domove(m.moveStrCaps(), Piece.WHITE);
				//cb2.dump();
				cb2.redoVectorsAndCoverages(Piece.BLACK,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
				if (cb2.bBlackKingThreat) System.out.println("Still checked");
				else 
				{
					System.out.println("Possibility. DTM = 1. FEN: "+ cb2.FEN());
					cb2.dump();
					
					sUpdNal.setInt(1,1+iDTM);
					String sFEN = cb2.FEN().replaceAll(" b "," w ");
					String sCrit = sFEN.substring(0,sFEN.length()-2).trim();
					sUpdNal.setString(2,sCrit+"%");
					System.out.println("sCrit"+ sCrit);
					sUpdNal.executeUpdate();
					dbconn.commit();
					
					//processMove(cb2,2,piece.BLACK);
				}
			}
		}
	}
	
	static void processMove(chessboard cb, int iDTM, int iColor)
	{
		System.out.println("processMove called iDTM:" + iDTM);
		
		if (iDTM == 3)
		{
			cb.dump();
		}
		
		moveindex mi;
		if (iColor == Piece.BLACK) mi = cb.miBlackMoveindex;
		else mi = cb.miWhiteMoveindex;
		
		mi.dump(true);
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			if (!m.bCapture)
			{
			
				System.out.println("Possibly found something: " + m.moveStr());
				chessboard cb2 = cb.copy();
				cb2.domove(m.moveStrCaps(),iColor);
				cb2.redoVectorsAndCoverages(1-iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
				cb2.dump();
				if (iDTM < 3) processMove(cb2,iDTM+1,1-iColor);
			}
		}
		
		if (iDTM == 3) {
			throw new RuntimeException("Exception on row 248 of nalimov.java");
		}
	}
	
	static void populateByDTM(String args[]) throws Exception
	{
		int iDTM = new Integer(args[1]).intValue();
		
		PreparedStatement psGetMates = dbconn.prepareStatement("select * from nalimov where dtm = ? and fen > ? order by fen");
		PreparedStatement psGetNulls = dbconn.prepareStatement("select * from nalimov where dtm is null and fen > ? order by fen");
		PreparedStatement psCheckFEN = dbconn.prepareStatement("select dtm from nalimov where fen like ?");
		
		if ((iDTM % 2) == 0)
		{
			System.out.println("****** DTM " + iDTM + " maj loop.");
			String sFen0 = "0";
			boolean bAgain = true;
			while (bAgain)
			{
				System.out.println("****** DTM " + iDTM + " min loop. sFen0:" + sFen0);
				psGetMates.setInt(1,iDTM);
				psGetMates.setString(2,sFen0);
				ResultSet rs = psGetMates.executeQuery();
				if (rs.next())
				{
					String sFEN = rs.getString(1);
					sFen0 = new String(sFEN);
					System.out.println("DTM " + iDTM + " FEN: " + sFEN);
					chessboard cb = new chessboard();
					cb.init_from_FEN(sFEN);
					cb.redoVectorsAndCoverages(Piece.WHITE,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
					processCheckMate(cb, iDTM);
					
				}
				else bAgain = false;
			}
		}
		else
		{
			System.out.println("DTM by " + iDTM + " starts");
			

			String sFen0 = "0";
			boolean bAgain = true;
			
			while (bAgain)
			{
				psGetNulls.setString(1,sFen0);
				ResultSet rs = psGetNulls.executeQuery();
				
				boolean bInLoop = true;
				while (rs.next() && bInLoop)
				{
					
					String sFEN = rs.getString(1);
					sFen0 = new String(sFEN);
					String sTestFEN = "8/8/8/8/8/K2B4/3B4/1k6 b - - 0 1";
					//if (sFEN.indexOf(" b ") != -1)
					if (sFEN.indexOf(sTestFEN) != -1)
					{
						//System.out.println("TEST FEN " +  sTestFEN + " found!");
						chessboard cb = new chessboard();
						cb.init_from_FEN(sFEN);
						System.out.println(sFEN);
						System.out.println("DTM by " + iDTM + " check board starts: ----");
						cb.dump();
						cb.redoVectorsAndCoverages(Piece.WHITE,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
						moveindex mi = cb.miBlackMoveindex;
						mi.dump(true);
						boolean bEscape = false;
						for (int i=0;i<mi.getSize();i++)
						{
							move m = mi.getMoveAt(i);
							chessboard cb2 = cb.copy();
							System.out.println("Starting move:" + m.moveStr());
							if (m.kingNalimov() != chessboard.NALI_NONE)
							{
								System.out.println("Nalimov move: " + m.moveStr());
								cb2.dump();
								cb2.domove(m.moveStrCaps(), Piece.BLACK);
								System.out.println("Before nali flip, king nalimov:" + m.kingNalimov());
								cb2.dump();
								chessboard cb3 = cb2.copy();
								switch (m.kingNalimov())
								{
									case chessboard.NALI_HORI:
									case chessboard.NALI_VERT:
									case chessboard.NALI_DIAG:
										cb3 = cb3.nali_flip(m.kingNalimov());
										break;
										
									case chessboard.NALI_HORI_VERT:
										cb3 = cb3.nali_flip(chessboard.NALI_HORI);
										cb3 = cb3.nali_flip(chessboard.NALI_VERT);
										break;
										
									case chessboard.NALI_VERT_DIAG:
										cb3 = cb3.nali_flip(chessboard.NALI_DIAG);
										cb3 = cb3.nali_flip(chessboard.NALI_VERT);
										break;
								}
								cb3.lm_vector = null;
								System.out.println("After nali flip:");
								cb3.dump();
								cb2=cb3.copy();
								System.out.println("cb2 dump");
								cb2.dump();
								cb2.redoVectorsAndCoverages(Piece.BLACK,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
							}
							else 
							{
								cb2.domove(m.moveStrCaps(), Piece.BLACK);
								System.out.println("moved:"+m.moveStrCaps());
							}
							System.out.println(cb2.FEN());
							String sFEN2 = cb2.FEN();
							String sCrit = sFEN2.substring(0,sFEN2.length()-2).trim();
							
							psCheckFEN.setString(1,sCrit+"%");
							System.out.println("sCrit: " + sCrit);
							ResultSet rs2 = psCheckFEN.executeQuery();
							if (rs2.next())
							{
								String ss = rs2.getString(1);
								System.out.println("ss:" + ss);
								if ((ss == null)) bEscape = true;
							}
							else
							{
								System.out.println("FEN not found! " + sFEN2);
								cb2.dump();
								System.out.println("m.bCapture:" + m.bCapture);
								if (!m.bCapture) {
									throw new RuntimeException("Exception on row 381 of nalimov.java");
								} else
								{
									System.out.println("can continue. capture is escape!");
									bEscape = true;
								}
							}
							
						}
						
						if ((!bEscape)  && (mi.getSize() > 0))
						{
							System.out.println("Possible dtm 2 candidate!. sFEN = " + sFEN);
							cb.dump();
							sUpdNal.setInt(1,iDTM+1);
							String sCrit = sFEN.substring(0,sFEN.length()-2).trim();
							sUpdNal.setString(2,sCrit+"%");
							sUpdNal.executeUpdate();
							bInLoop = false;
							sFen0 = new String(sFEN);
							//dbconn.commit();
							throw new RuntimeException("Possible dtm 2 candidate!. sFEN = " + sFEN);
						}
						else 
						{
							System.out.println("There was an escape!");
						}
					}
				}
				
			}
		}
		dbconn.commit();
		
	}
	
	static void testIt()
	{
		System.out.println("yes!");
	}
	
}