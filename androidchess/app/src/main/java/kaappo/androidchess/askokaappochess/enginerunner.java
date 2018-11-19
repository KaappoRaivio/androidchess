package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class enginerunner
{
	public static Connection dbconn;
	static String dbString = "jdbc:solid://localhost:2315/dba/dba";
	
	static int iCount = 0;
	static int iAttCount = 0;
	static int iMaxScore = -500;
	static int iMinScore = 500;
	static int iCountAbove100 = 0;
	static int iCountAbove0 = 0;
	static int iCountBelowMinus100 = 0;
	static int iScoreTotal = 0;
	
	static int iZeroFixLevel = 25;
	
	static int ZERO_LIMIT = 0;
	//static int MOVE_LIMIT = 7;
	//static int MOVE_LIMIT = 6;
	static int MOVE_LIMIT = 5;   
	//static int MOVE_LIMIT = 4;      // final goal for local enhancement 4 -> 5
	//static int MOVE_LIMIT = 6;    // final goal for local enhancement 6 -> 7
	
	static int SZ9_LEVEL = MOVE_LIMIT + 1;
	static int FAROUT_TOPLEVEL = 19;
	
	static int FARTEN_MOVELEVEL = SZ9_LEVEL + 1;
	static int FARTEN_CPLIMIT = 50;
	
	static int iMode;
	static int MODE_NORMAL = 0;
	static int MODE_FILE = 1;
	static int MODE_RETRIEVE = 2;
	
	
	public static void main (String args[]) throws Exception
	{
		iMode = MODE_NORMAL;
		
		if ((args.length > 0) && (args[0].equalsIgnoreCase("REMDUP")))
		{
			removeDuplicates(args[1],args[2]);
			throw new RuntimeException("Exception in 52 of enginerunner.java");
		}
		
		if ((args.length > 0) && (args[0].equalsIgnoreCase("FILE"))) iMode = MODE_FILE;
		if ((args.length > 0) && (args[0].equalsIgnoreCase("RETRIEVE"))) iMode = MODE_RETRIEVE;
		
		
		Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
		
		Connection c = null;
		PreparedStatement pss = null;
		PreparedStatement psi = null ;
		
		PrintWriter pwmf = null;
		
		if ((iMode == MODE_NORMAL) || (iMode == MODE_RETRIEVE))
		{
			c = DriverManager.getConnection(dbString);
			pss = c.prepareStatement("select * from sflib where fen like ?");
			psi = c.prepareStatement("insert into sflib values(?,?,?)");
		}
		
		BufferedReader br;
		try
		{
			 br = new BufferedReader(new FileReader("sfinput.txt"));
		}
		catch (Exception e)
		{
			System.out.println("No file sfinput.txt. Exiting.");
			return;
		}
		
		
		int lc = 0;
		String sctr = br.readLine();
		while (sctr != null)
		{
			lc++;
			sctr = sctr.trim();
			String sCrit=sctr.substring(0,sctr.length()-2).trim();
			//System.out.println("C:"+ sCrit);
			ResultSet rs = null;
			if ((iMode == MODE_NORMAL) || (iMode == MODE_RETRIEVE))
			{
				pss.setString(1,sCrit+"%");
				rs = pss.executeQuery();
			}
			
			if ((iMode == MODE_NORMAL) && (!rs.next()))
			{
				//String sMove = engine.getMoveByAlg("stockfish-6-64.exe", sctr,movevalue.ALG_ASK_FROM_ENGINE10);
				String sMove = engine.getMoveByAlg(engine.sEnginePerAlg(movevalue.ALG_ASK_FROM_ENGINE10), sctr,movevalue.ALG_ASK_FROM_ENGINE10);
				int iScore = engine.iLastScore;
				int iMateScore = engine.iLastMateScore;
				//System.out.println("ENGMOVE DEBUG: cp:" + iScore + " iMate:" + iMateScore);
				if (iMateScore != 0) System.out.println("ENGMOVE:" + sctr + " -> " + sMove + " score(mate):" + iMateScore);
				else System.out.println("ENGMOVE:" + sctr + " -> " + sMove + " score(cp):" + iScore);
				regscore(iScore);
				if ( (iMode == MODE_NORMAL) && (sMove != null) && (!sMove.equalsIgnoreCase("mate")))
				{
					psi.setString(1,sctr);
					psi.setString(2,sMove);
					psi.setInt(3,0);
					//System.out.println("sctr: "+sctr);
					//System.out.println("CRIT:<"+sCrit+">");
					try
					{
						psi.executeUpdate();
					}
					catch (SQLException sqle)
					{
						if (sqle.getErrorCode() == 10033) 
						{
							System.out.println("sctr: "+sctr);
							System.out.println("CRIT:<"+sCrit+">");
						}
						else 
						{
							System.out.println("Unexpected SQL Exception at enginerunner");
							System.out.println(sqle.getMessage());
							System.out.println("sctr: "+sctr);
							System.out.println("CRIT:<"+sCrit+">");
							throw new RuntimeException(sqle);
						}
						
					}
					if (iMateScore == 0)
					{
						int iMoveNr = new Integer(sctr.substring(sctr.length()-2,sctr.length()).trim()).intValue();
						
						int iDiscount = 0;
						
						if (iMoveNr <= 4) iDiscount = 350;
						if (iMoveNr ==5) iDiscount = 250;
						if (iMoveNr ==6) iDiscount = 200;
						if (iMoveNr ==7) iDiscount = 100;
						if (iMoveNr ==SZ9_LEVEL) iDiscount = 100;
						if ((iMoveNr > SZ9_LEVEL) && (iMoveNr <= FAROUT_TOPLEVEL)) iDiscount = 75;
						
						if ((iScore < (ZERO_LIMIT + iDiscount )) && (iMoveNr <= MOVE_LIMIT))
						{
							
							
							PrintWriter pwz = null;
			
							if (pwz == null)
							{
								pwz = new PrintWriter(new BufferedWriter(new FileWriter("subzero.out", true)));
							}
							
							pwz.println(sctr);
							pwz.close();
							pwz = null;
							
							System.out.println("subzero sfinp:" + sctr + " iMoveNr:" + iMoveNr);
						}
						
						if ((iScore < (ZERO_LIMIT + iDiscount )) && (iMoveNr == SZ9_LEVEL))
						{
							PrintWriter pwz = null;
			
							if (pwz == null)
							{
								pwz = new PrintWriter(new BufferedWriter(new FileWriter("sz9.out", true)));
							}
							
							pwz.println(sctr);
							pwz.close();
							pwz = null;
						}
						
						if ((iScore < (ZERO_LIMIT + iDiscount ) && (iMoveNr > SZ9_LEVEL) && (iMoveNr <= FAROUT_TOPLEVEL)))
						{
							PrintWriter pwz = null;
			
							if (pwz == null)
							{
								pwz = new PrintWriter(new BufferedWriter(new FileWriter("far.out", true)));
							}
							
							pwz.println(sctr);
							pwz.close();
							pwz = null;
						}
						
						if (iScore < iZeroFixLevel)
						{
							PrintWriter pwz = null;
			
							if (pwz == null)
							{
								pwz = new PrintWriter(new BufferedWriter(new FileWriter("erzfx.out", true)));
							}
							
							pwz.println(sctr);
							pwz.close();
							pwz = null;
	
						}
						
						
					}
				}
			}
			else if (iMode == MODE_FILE)
			{
				if (pwmf == null)
				{
					pwmf = new PrintWriter(new BufferedWriter(new FileWriter("sfileinput.txt", true)));
				}
				
				System.out.println("It's file-based!");
				//String sMove = engine.getMoveByAlg("stockfish-6-64.exe", sctr,movevalue.ALG_ASK_FROM_ENGINE10);
				String sMove = engine.getMoveByAlg(engine.sEnginePerAlg(movevalue.ALG_ASK_FROM_ENGINE10), sctr,movevalue.ALG_ASK_FROM_ENGINE10);
				int iScore = engine.iLastScore;
				int iMateScore = engine.iLastMateScore;
				//System.out.println("ENGMOVE DEBUG: cp:" + iScore + " iMate:" + iMateScore);
				if (iMateScore != 0) System.out.println("ENGMOVE:" + sctr + " -> " + sMove + " score(mate):" + iMateScore);
				else System.out.println("ENGMOVE:" + sctr + " -> " + sMove + " score(cp):" + iScore);
				regscore(iScore);
				if ( (sMove != null) && (!sMove.equalsIgnoreCase("mate")))
				{
					String sFinp = (char)34+sctr + (char)34 +" " + (char)34 + sMove + (char)34;
					System.out.print("TOFILE("+lc+"):"+sFinp);
					//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("sfileinput.txt", true)));
					//pw.println(sFinp);
					//pw.flush();
					//pw.close();
					pwmf.println(sFinp);
					pwmf.flush();
					//pwmf.close();
					System.out.println(" .. OK");
					
					int iMoveNr = new Integer(sctr.substring(sctr.length()-2,sctr.length()).trim()).intValue();
					if (iMoveNr == FARTEN_MOVELEVEL)
					{
						if (iScore < FARTEN_CPLIMIT)
						{
							PrintWriter pwf10 = new PrintWriter(new BufferedWriter(new FileWriter("f10.bat", true)));
							pwf10.println("java anyokmove "+(char)34+sctr+(char)34+" "+(char)34+sMove+(char)34);
							pwf10.flush();
							pwf10.close();
						}
					}
				}
			}
			else if ((iMode == MODE_RETRIEVE) && (rs.next()))
			{
				String sMove = rs.getString(2);
				System.out.println("RETRIEVE for:" + sctr + ":" + sMove);
				String sRetr = "java anyokmove "+(char)34+sctr + (char)34 +" " + (char)34 + sMove + (char)34;
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("sfretr.bat", true)));
				pw.println(sRetr);
				pw.flush();
				pw.close();
				
			}
			else iAttCount++;
			
			sctr = br.readLine();
		}
		if (pwmf != null) pwmf.close();
		
		
		printscore();
		throw new RuntimeException("Exception on row 278 of enginerunner.java");
	}
	
	public static String getMove(String sFEN) 
	{
		try
		{
		
			String sRet = null;
			
			Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
			
			if (dbconn == null) dbconn = DriverManager.getConnection(dbString);
			PreparedStatement ps = dbconn.prepareStatement("select * from sflib where fen like ?");
			
			sFEN = sFEN.trim();
			String sCrit=sFEN.substring(0,sFEN.length()-2);
			ps.setString(1,sCrit+"%");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) 
			{
				String sPos = rs.getString(1);
				if (!sPos.contains(sCrit))
				{
					System.out.println("Fatal data problem at enginerunner.getMove!!!");
					System.out.println("sPos:<"+sPos+">");
					System.out.println("sCrit:<"+sCrit+">");
					throw new RuntimeException("Fatal data problem at enginerunner.getMove!!!");
				}
				sRet = rs.getString(2).toUpperCase();
			}
			return sRet;
		}
		catch (Exception e)
		{
			System.out.println("WARNING: enginerunner.getMove() could not access database");
			return null;
		}
		
	}
	
	public static void regscore(int iScore)
	{
		
		iCount++;
		iAttCount++;
		if (iScore > iMaxScore) iMaxScore = iScore;
		if (iScore < iMinScore) iMinScore = iScore;
		if (iScore > 100) iCountAbove100++;
		if (iScore > 0) iCountAbove0++;
		if (iScore < -100) iCountBelowMinus100++;
		iScoreTotal = iScoreTotal + iScore;
	}
	
	public static void printscore()
	{
		String sScore = "Enginerunner score: c: " + iCount;
		sScore = sScore + " att: " + iAttCount;
		sScore = sScore + " max: " + iMaxScore;
		sScore = sScore + " min: " + iMinScore;
		sScore = sScore + " >100: " + iCountAbove100;
		sScore = sScore + " >0: " + iCountAbove0;
		sScore = sScore + " <-100: " + iCountBelowMinus100;
		if (iCount > 0) sScore = sScore + " ave: " + (iScoreTotal/iCount);
		sScore = "" + new Timestamp(System.currentTimeMillis()) + " " + sScore;
		//System.out.println(new Timestamp(System.currentTimeMillis()) + " " + sScore);
		try
		{
			PrintWriter pw = null;
			
			if (pw == null)
			{
				pw = new PrintWriter(new BufferedWriter(new FileWriter("enginerunner.out", true)));
			}
			//System.out.println("regbest:printdbg a");
			pw.println(sScore);
			pw.close();
			pw = null;
			//System.out.println("regbest:printdbg b");			
		}
		catch (IOException e) 
		{
			//exception handling left as an exercise for the reader
			System.out.println("IOException at regbest.printDbg() (A)");
			throw new RuntimeException(e);
		}
		
	}
	
	static int getFENMoveCount(String sFEN)
	{
		String sComp[] = sFEN.split(" ");
		int iMCount = -1;
		try 
		{
			iMCount = new Integer(sComp[sComp.length-1]).intValue();
		}
		catch (Exception e)
		{
			
		}
		
		
		return iMCount;	
	}
	
	public static void removeDuplicates (String sFileIn, String sFileOut) throws Exception
	{
		BufferedReader br;
		try
		{
			 br = new BufferedReader(new FileReader(sFileIn));
		}
		catch (Exception e)
		{
			System.out.println("No file " + sFileIn + ". Exiting.");
			return;
		}
		
		Vector vIn = new Vector();
		Vector vOut = new Vector();
		
		String sIn = br.readLine();
		int ic = 0;
		while (sIn != null)
		{
			vIn.addElement(sIn);
			sIn = br.readLine();
			ic++;
		}
		System.out.println("enginerunner.removeDuplicates(): input size:" + ic + " removing duplicates.");
		
		long lStart = System.currentTimeMillis();
		
		for (int i=0;i<vIn.size();i++)
		{
			String sCand = (String)vIn.elementAt(i);
			boolean bOK = true;
			for (int j=0;j<vOut.size();j++)
			{
				String sComp = (String)vOut.elementAt(j);
				if (sCand.equals(sComp))
				{					
					bOK = false;
					break;
				}
			}
			if (bOK) vOut.addElement(sCand);
		}
		
		long lEnd = System.currentTimeMillis();
		long lDur = (lEnd-lStart);
		
		System.out.println("enginerunner.removeDuplicates(): reduced size:" + vOut.size() + " duration:" + lDur + " msec");

		PrintWriter pwOut = new PrintWriter(new BufferedWriter(new FileWriter(sFileOut, false)));
		for (int j=0;j<vOut.size();j++)
		{
			String sComp = (String)vOut.elementAt(j);
			pwOut.println(sComp);
		}
		pwOut.flush();
		pwOut.close();
		
		
	}
}