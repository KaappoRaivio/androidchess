package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class erfixer
{
	public static Connection dbconn;
	static String dbString = "jdbc:solid://localhost:2315/dba/dba";
	
	public static void main (String args[]) throws Exception
	{
		Vector vRec = new Vector();
		
		Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
		dbconn = DriverManager.getConnection(dbString);
		
		BufferedReader br = new BufferedReader(new FileReader("ermoves.out"));
		
		String sctr = br.readLine();
		gamerec gr = null;
		while (sctr != null)
		{
			if (sctr.equals("--NEW:"))
			{
				//System.out.println("new..");
				gr = new gamerec();
				vRec.addElement(gr);
			}
			else 
			{
				String[] sCP = sctr.split(":");
				String sComm = sCP[0];
				
				if (sComm.equals("SUCC")) gr.addMove(sCP[1]+":"+sCP[2]);
				if (sComm.equals("FAIL")) 
				{
					gr.szFail = sCP[1];
					//vRec.addElement(gr);
				}
				
			}
			
			sctr = br.readLine();
		}
		br.close();
		
		for (int i=0;i<vRec.size();i++)
		{
			gamerec g = (gamerec)vRec.elementAt(i);
			g.dump();
		}
		
		System.out.println("STARTING TO SCAN ********");
		
		br = new BufferedReader(new FileReader("erzfx.out"));
		
		sctr = br.readLine();
		while (sctr != null)
		{
			String sScan = new String(sctr);
			System.out.println("Scanning:" + sScan);
			
			for (int i=0;i<vRec.size();i++)
			{	
				gamerec g = (gamerec)vRec.elementAt(i);
				if (g.szFail != null)
				{
					if (g.szFail.equals(sScan))
					{
						g.dump();
						g.checkmoves();
					}
				}
				else System.out.println("gamerec with null szfail.");
			}
			
			sctr = br.readLine();
		}
		
	}
}

class gamerec
{
	String szFail;
	Vector vMoves;
	
	gamerec()
	{
		vMoves=new Vector();
	}
	
	void addMove(String s)
	{
		vMoves.addElement(s);
	}
	
	void dump()
	{
		for (int i=0;i<vMoves.size();i++)
		{
			String s = (String)vMoves.elementAt(i);
			System.out.println(s);
		}
		System.out.println("FINAL:"+szFail);
		System.out.println("done");
	}
	
	void checkmoves() throws Exception
	{
		for (int i=0;i<vMoves.size();i++)
		{
			String s = (String)vMoves.elementAt(i);
			String[] szC = s.split(":");
			String sMove = enginerunner.getMove(szC[0]);
			String sMove2 = engine.getMove(engine.sSTOCKFISH,szC[0],engine.iNodeLimPerAlg(movevalue.ALG_ASK_FROM_ENGINE10));
			System.out.println("Compare: "+ sMove + "," + szC[1]+","+sMove2.toUpperCase());
			if (!sMove.equals(szC[1]) || (!sMove.equals(sMove2.toUpperCase())))
			{
				System.out.println("Compare problem: "+szC[0]);
				PrintWriter pwprob = new PrintWriter(new BufferedWriter(new FileWriter("engprob.out", true)));
				pwprob.println("---");
				pwprob.println("Potential problem at: " + new Timestamp(System.currentTimeMillis()));
				pwprob.println("FEN: " + szC[0]);
				pwprob.println("Moves: " + sMove + "," + szC[1]+","+sMove2.toUpperCase());
				chessboard cb1 = new chessboard();
				cb1.init_from_FEN(szC[0]);
				chessboard cb2 = cb1.copy();
				chessboard cb3 = cb1.copy();
				
				cb1.domove(sMove,cb1.iFileCol);
				String sFEN1 = cb1.FEN();
				String sMoveNew = engine.getMove(engine.sSTOCKFISH,sFEN1,engine.iNodeLimPerAlg(movevalue.ALG_ASK_FROM_ENGINE10));
				int iScoreOld = engine.iLastScore;
				pwprob.println("Score for: "+sMove+ " " + iScoreOld + " (old)");
				
				cb2.domove(sMove2.toUpperCase(),cb2.iFileCol);
				String sFEN2 = cb2.FEN();
				sMoveNew = engine.getMove(engine.sSTOCKFISH,sFEN2,engine.iNodeLimPerAlg(movevalue.ALG_ASK_FROM_ENGINE10));
				int iScoreCheck = engine.iLastScore;
				pwprob.println("Score for: "+sMove2.toUpperCase()+ " " + iScoreCheck + " (checked)");
				
				if (iScoreCheck < iScoreOld-20) 
				{
					pwprob.println("REAL PROBLEM!");
					String sFEN = szC[0];
					String sCrit = sFEN.substring(0,sFEN.length()-2).trim();
					String sFixSQL = "update sflib set gmove='"+sMove2.toLowerCase()+"' where fen like '"+sCrit+"%'";
					pwprob.println("Recommend fix by: "+sFixSQL);
				}
				else pwprob.println("Looks ok.");
				
				pwprob.flush();
				pwprob.close();
			}
		}
			
			
	}
	
	
}