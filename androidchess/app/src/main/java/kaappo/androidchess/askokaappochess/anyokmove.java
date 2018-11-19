package kaappo.androidchess.askokaappochess;


import java.util.*;
import java.io.*;
import java.io.PrintWriter;
import java.sql.*;

public class anyokmove
{
	static String dbString = "jdbc:solid://localhost:2315/dba/dba";
	
	public static final int ANYMOVE_CUT = 50;
	
	static Connection dbconn = null;
	static PreparedStatement psAnySave = null;
	static Statement psAnyGet = null;
	
	anyokmove()
	{
	}
	
	public static void main(String args[]) throws Exception 
	{
		/*
		long lStart = System.currentTimeMillis();
		System.out.println("anyokmove starts for: "+ args[0]);
		chessboard cb = new chessboard();
		cb.init_from_file(args[0]);
		int iC = cb.iFileCol;
		fad_anymove(cb,iC);
		long lEnd = System.currentTimeMillis();
		System.out.println("Duration:" + (lEnd-lStart) + " msec.");
		*/
		long lStart = System.currentTimeMillis();
		
		Driver d = null;
		Connection c = null;
		PreparedStatement pss = null;
		String sParMove = null;
		
		if (args.length < 2)
		{
			try
			{
				d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
				c = DriverManager.getConnection(dbString);
				pss = c.prepareStatement("select * from sflib where fen like ?");
			}
			catch (Exception e)
			{
				System.out.println("No db present. ");
				if (args.length < 2) {
					throw new RuntimeException("Exception at row 54 of anyokmove.java");
				}
				sParMove = args[1];
				System.out.println("Continue with sParMove:" + sParMove);
			}
		}
		else
		{
			sParMove = args[1];
			System.out.println("Continue with sParMove:" + sParMove);
		}
		
		String sctr = args[0];
		sctr = sctr.trim();
		String sCrit=sctr.substring(0,sctr.length()-2);
		
		if (c != null) pss.setString(1,sCrit+"%");
		
		String sMCPart = sctr.substring(sctr.length()-2).trim();
		System.out.println(sMCPart);
		int iNewMove = (new Integer(sMCPart).intValue())+1;
		
		ResultSet rs = null;
		if (c != null) rs = pss.executeQuery();
		if ((sParMove != null) || (rs.next()))
		{
			String sMove = null;
			if (c!= null) 
			{
				System.out.println(rs.getString(2));
				sMove = rs.getString(2).toUpperCase();
			}
			else sMove = sParMove.toUpperCase();
			chessboard cb = new chessboard();
			cb.init_from_FEN(args[0]);
			chessboard cb2 = cb.copy();
			cb2.domove(sMove,cb2.iFileCol);
			System.out.println("Moving: " + sMove);
			cb2.dump();
			System.out.println("FEN now:" + cb2.FEN());
			
			//chessboard cbr = db_anymoveget(cb2.FEN(), ""+movevalue.ALG_ANY_OKMOVE, true);
			String sOut = cb2.FEN();
			String sMovedFEN = new String (cb2.FEN());
			chessboard cbMoved = cb2.copy();
			
			System.out.println("iFileCol:" + cb.iFileCol + " iNewMove" + iNewMove);
			if (cb2.iFileCol == piece.BLACK)
			{
				sOut = sOut.substring(0,sOut.length()-2);
				sOut = sOut+" " + iNewMove;
			}
			else 
			{
				sOut = sOut.substring(0,sOut.length()-2);
				sOut = sOut+" " + (iNewMove -1);
			}
			
			System.out.println("sOut now:"+sOut);
			
			
			chessboard cbr = null;
			if (c != null) cbr = db_anymoveget(sOut, ""+movevalue.ALG_ANY_OKMOVE, true);
			
			if (cbr == null) 
			{
				cbr = fad_anymove(cb2,1-cb2.iFileCol, iNewMove);
				cbr.dump();
			}
			else
			{
				System.out.println("Anymove was there");
			}
			
			// EXPANSION 180119
			if (c != null)
			{
				db_allmovesget(sOut);
			}
			
			if (c == null)
			{
				System.out.println("No db connection, expand for all engine moves.");
				
				doenginemoves(movevalue.ALG_ASK_FROM_ENGINE1,movevalue.ALG_ASK_FROM_ENGINE10, sMovedFEN,cbMoved,iNewMove);
				
				doenginemoves(movevalue.ALG_ASK_FROM_ABROK1,movevalue.ALG_ASK_FROM_ABROK4, sMovedFEN,cbMoved,iNewMove);
				
			}
		}
		else System.out.println("No original move found, skipping.");
		
		long lEnd = System.currentTimeMillis();
		System.out.println("Duration:" + (lEnd-lStart) + " msec.");
	}
	
	static chessboard fad_anymove(chessboard cb, int iColor, int NewMoveNr) throws Exception
	{
		cb.redoVectorsAndCoverages(iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
		cb.dump();
		
		System.out.println("anyokmove.fad_anymove() called!");

		moveindex mi;
		if (iColor == piece.WHITE) mi=cb.miWhiteMoveindex;
		else mi = cb.miBlackMoveindex;
		
		mi.dump();
		Vector v = new Vector();
		for (int i=0;i<mi.getSize();i++)
		{
			move m = mi.getMoveAt(i);
			System.out.println("Doing: " + m.moveStrLong());
			chessboard cb2=cb.copy();
			cb2.redoVectorsAndCoverages(iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
			cb2.domove(m.moveStrCaps(),iColor);
			//cb2.dump();
			chessboard cb3 = cb2.copy();
			cb3.redoVectorsAndCoverages(1-iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
			
			chessboard cb4 = engine.findAndDoMove(cb3,movevalue.ALG_ASK_FROM_ENGINE1, false, false);
			System.out.println("Score for: " + m.moveStrLong() + " :" + engine.iLastScore+":"+engine.iLastMateScore);
			v.addElement(new mventry(m,engine.iLastScore,engine.iLastMateScore,engine.bLastMate));
			
		}
		
		int iBest = 10000;
		int iMateBest = 0;
		
		System.out.println("---");
		for (int i=0;i<v.size();i++)
		{
			mventry mve = (mventry)v.elementAt(i);
			
			if (mve.imatevalue != 0)
			{
				System.out.println("anyokmove.fad_anymove(), mate in sight, mve.imatevalue:" + mve.imatevalue + " move:" + mve.mo.moveStrLong());
				cb.dump();
				if ((mve.imatevalue < 0) && ((iMateBest ==0)  || (mve.imatevalue > iMateBest )))
				{
					iMateBest = mve.imatevalue;
				}
				else if (mve.imatevalue > 0)
				{
					if (iBest == 10000) 
					{
						if (iMateBest == 0) iMateBest = mve.imatevalue;
						else if (mve.imatevalue > iMateBest) iMateBest = mve.imatevalue;
					}
					
				}
				
			}
			else
			{
				if (mve.value < iBest) iBest = mve.value;
				if (iMateBest > 0) iMateBest = 0;
				
			}
			System.out.println(mve.mo.moveStrLong() + "    " + mve.value + "  " + mve.imatevalue);
		}
		System.out.println("Best:" + iBest + " Matebest:" + iMateBest);
		System.out.println("---");
		System.out.println("The cut: ");
		
		int iMovesInCut = 0;
		
		for (int i=0;i<v.size();i++)
		{
			mventry mve = (mventry)v.elementAt(i);
			if (((iMateBest == 0) && (mve.value < iBest + ANYMOVE_CUT)) ||
				((iMateBest != 0) && (mve.imatevalue == iMateBest)))
			{
				System.out.println(mve.mo.moveStrLong() + "    " + mve.value + " " + mve.imatevalue);
				mve.bMadeCut = true;
				iMovesInCut++;
				
			}
		}
		
		
		System.out.println("---");
		System.out.println("The cut by FEN: (iColor: " +iColor + ") " + iMovesInCut + " moves.");
		
		if (iMovesInCut == 0) {
			throw new RuntimeException("Exception on row 239 of anyokmove.java");
		}
		
		int iRndMove = (int)(Math.random() * iMovesInCut);
		int iCutMoveCtr = 0;
		
		move mRnd = null;
		String sMoves = "";
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("anymove.txt", true)));
		for (int i=0;i<v.size();i++)
		{
			mventry mve = (mventry)v.elementAt(i);
			
			if (mve.bIsMate)
			{
				mRnd = mve.mo.copy();
				i=v.size()+1;
				sMoves = mve.mo.moveStrCaps();
			}
			
			if (mve.bMadeCut)
			{
				chessboard cb2=cb.copy();
				cb2.redoVectorsAndCoverages(iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
				cb2.domove(mve.mo.moveStrCaps(),iColor);
				/*
				if (iColor == piece.WHITE) cb2.iMoveCounter=cb2.iMoveCounter+2;
				else
				{
					cb2.iMoveCounter=cb2.iMoveCounter+2;
				}
				System.out.println(cb2.FEN());
				*/
				String sNewFen = cb2.FEN();
				sNewFen = sNewFen.substring(0,sNewFen.length()-2).trim();
				sNewFen = sNewFen + " " + NewMoveNr;
				System.out.println(sNewFen);
				pw.println(sNewFen);
				sMoves = sMoves + mve.mo.moveStrCaps() + " ";
				
				if (iRndMove == iCutMoveCtr)
				{
					mRnd = mve.mo.copy();
				}
				
				iCutMoveCtr++;
			}
		}
		pw.flush();
		pw.close();
		System.out.println("Anyokmove Chose:" + mRnd.moveStrLong()+ " ************");
		
		chessboard cbret = cb.copy();
		cbret.redoVectorsAndCoverages(iColor,movevalue.ALG_SUPER_PRUNING_KINGCFIX);
		cbret.domove(mRnd.moveStrCaps(),iColor);
		//Thread.sleep(5000);
		try
		{
			db_anymovesave(cb.FEN(),""+movevalue.ALG_ANY_OKMOVE,sMoves);
		}
		catch (Exception e)
		{
			System.out.println("Warning: Error in anymove.db_anymovesave() " + e.getMessage()+ " can continue.");
			PrintWriter pwany = new PrintWriter(new BufferedWriter(new FileWriter("anymovedb.txt", true)));
			pwany.println((char)34+cb.FEN()+(char)34+" "+movevalue.ALG_ANY_OKMOVE+" "+(char)34+sMoves+(char)34);
			pwany.flush();
			pwany.close();
		}
		

		return cbret;
	}
	
	
	static void db_anymovesave(String sFEN, String sAlg, String sMove) throws Exception
	{
		if (dbconn == null)
		{
			Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
			dbconn = DriverManager.getConnection(dbString);
			
		}
		
		System.out.println("DBG161104: db_anymovesave:" + sFEN + " : " + sMove + " starting.");
		
		if (psAnySave == null) psAnySave = dbconn.prepareStatement("insert into anymove values(?,?,?)");
		
		psAnySave.setString(1,sFEN);
		psAnySave.setString(2,sAlg);
		psAnySave.setString(3,sMove);
		psAnySave.executeUpdate();
		dbconn.commit();
		System.out.println("DBG161104: db_anymovesave:" + sFEN + " : " + sMove + " committed.");
		
	}
	
	static chessboard db_anymoveget(String sFEN, String sAlg, boolean bDumpCut) throws Exception
	{
		if (dbconn == null)
		{
			Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
			dbconn = DriverManager.getConnection(dbString);
		}
		
		if (psAnyGet == null) psAnyGet = dbconn.createStatement();
		
		String sFENCrit = sFEN.substring(0,sFEN.length()-2).trim();
		
		String sGet = "select movelist from anymove where fen like '"+sFENCrit + "%' and alg = '" + sAlg + "'";
		
		System.out.println("db_anymoveget:sGet=" + sGet);

		ResultSet rs = psAnyGet.executeQuery(sGet);
		if (rs.next())
		{
			System.out.println("DBG161104: db_anymoveget found a row. " + rs.getString(1));
			
			chessboard cb = new chessboard();
			cb.init_from_FEN(sFEN);
			String sComp[] = rs.getString(1).trim().split(" ");
			
			int iRnd = (int)(Math.random()*sComp.length);
			
			if (!bDumpCut)
			{
				String sMove = sComp[iRnd];
				cb.domove(sMove,cb.iFileCol);
				System.out.println("db_anymoveget found a move: " + sMove);
				System.out.println("sGet=" + sGet);
				System.out.println("movecounter:" + cb.iMoveCounter);
				cb.dump();
			}
			else
			{
				System.out.println("XMoves:" + rs.getString(1).trim());
				
				String sMCPart = sFEN.substring(sFEN.length()-2).trim();
				System.out.println(sMCPart);
				int iNewMove = (new Integer(sMCPart).intValue());
				if (cb.iFileCol == piece.BLACK) iNewMove++;
				
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("anymove.txt", true)));
				for (int i=0;i<sComp.length;i++)
				{
					chessboard cbn = cb.copy();
					cbn.domove(sComp[i],cb.iFileCol);
					String sOut = cbn.FEN();
					
					sOut = sOut.substring(0,sOut.length()-2).trim();
					sOut = sOut + " " + iNewMove;
					
					pw.println(sOut);
				}
				pw.close();
				pw.flush();
			}
			
			return cb;
		}
		else
		{
			System.out.println("DBG161104: db_anymoveget: no rec found.");
			return null;
		}
	}
	
	static void db_anymovelistdump(String sFEN) throws Exception
	{
		if (dbconn == null)
		{
			Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
			dbconn = DriverManager.getConnection(dbString);
		}
		
		if (psAnyGet == null) psAnyGet = dbconn.createStatement();
		
		String sFENCrit = sFEN.substring(0,sFEN.length()-2).trim();
		
		String sGet = "select alg,movelist from anymove where fen like '"+sFENCrit + "%' " ;
		
		System.out.println("db_anymoveget:sGet=" + sGet);

		ResultSet rs = psAnyGet.executeQuery(sGet);
		while(rs.next())
		{
			String sOut = "";
			sOut = rs.getString(1);
			while(sOut.length() < 8) sOut = sOut+" ";
			sOut = sOut + rs.getString(2);
			System.out.println(sOut);
		}
	}
	
	static void db_allmovesget(String sFEN)
	{
		System.out.println("DBG180119:db_allmovesget: sFEN :" + sFEN);
	}
	
	static void doenginemoves(int ibot, int itop, String sMovedFEN, chessboard cbMoved, int iNewMove) throws Exception
	{
		String sMove;
		
		for (int i=ibot;i<=itop;i++)
		{
		   sMove = engine.getMoveByAlg(engine.sEnginePerAlg(i),sMovedFEN,i).toUpperCase();
		   System.out.println("Alg:" + i + " ,move: " + sMove);
		   chessboard cbm = cbMoved.copy();
		   System.out.println("cbm.iFileCol:" + cbm.iFileCol);
		   cbm.domove(sMove,1-cbm.iFileCol);
		   cbm.dump();
		   //System.out.println("MovedFEN:" + cbm.FEN());
		   String sMovFEN = cbm.FEN();
		   System.out.println(sMovFEN+ " new move:" + iNewMove);
		   String sNewFEN = sMovFEN.substring(0,sMovFEN.length()-2).trim() + " " + iNewMove;
		   System.out.println(sNewFEN);
		   String sAMFEN = (char)34+sMovedFEN+(char)34+" "+i+" "+(char)34+sMove+(char)34;
		   System.out.println("anymovedbfen: " + sAMFEN);
		   
		   PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("anymove.txt", true)));
		   pw.println(sNewFEN);
		   pw.flush();
		   pw.close();
		   
		   PrintWriter pwany = new PrintWriter(new BufferedWriter(new FileWriter("anymovedb.txt", true)));
		   pwany.println(sAMFEN);
		   pwany.flush();
		   pwany.close();
		   
		}
	}
}

class mventry
{
	move mo;
	int value;
	int imatevalue;
	boolean bMadeCut;
	boolean bIsMate;
	
	mventry(move m, int iVal, int iMateVal, boolean bMate)
	{
		mo = m.copy();
		value = iVal;
		imatevalue = iMateVal;
		bIsMate = bMate;
		bMadeCut = false;
	}
}