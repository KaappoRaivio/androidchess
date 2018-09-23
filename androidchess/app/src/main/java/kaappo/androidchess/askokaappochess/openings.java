package kaappo.androidchess.askokaappochess;

import android.content.Context;

import java.io.BufferedReader;
import java.io.*;
import java.util.*;

import kaappo.androidchess.R;

public class openings
{
	Vector oVec;
	
	openings(Context context)
	{
		try {
			InputStream input = context.getResources().openRawResource(R.raw.openings);
//			InputStream input = getClass().getResourceAsStream("raw/openings.opn");
//			InputStream input = new FileInputStream(new File(R.raw.openings))
			if (input == null) {
				System.out.println("fatal: Can't find openings.opn");
				throw new RuntimeException("Can't find openings.opn");
			}

			BufferedReader br;
			br = new BufferedReader(new InputStreamReader(input));

			//BufferedReader br = new BufferedReader(new FileReader("openings.opn"));
			oVec = new Vector();
			
			int rc = 0;
			String sctr = br.readLine();
			while (sctr != null)
			{
				//System.out.println("sctr:"+sctr);
				opening_entry oe = new opening_entry(sctr);
				oVec.addElement(oe);
				rc++;
				sctr = br.readLine();
			}
			System.out.println("Done. " + rc + " rows.");
		}
		catch (Exception e)
		{
			System.out.println("Exception occurred at openings() constructor." + e.getMessage());
		}
	}
	
	public static void main (String args[])
	{
		System.out.println("Starting openings test run.");
		openings o = new openings(null);
		System.out.println("Loaded.");
		//o.dump();
		o.searchByGame(" 1. a3");
		o.searchByGame(" 1. a4");
		o.searchByGame(" 1. b3");
		o.searchByGame(" 1. b4");
		o.searchByGame(" 1. c3");
		o.searchByGame(" 1. c4");
		o.searchByGame(" 1. d3");
		o.searchByGame(" 1. d4");
		o.searchByGame(" 1. e3");
		o.searchByGame(" 1. e4");
		o.searchByGame(" 1. f3");
		o.searchByGame(" 1. f4");
		o.searchByGame(" 1. g3");
		o.searchByGame(" 1. g4");
		o.searchByGame(" 1. h3");
		o.searchByGame(" 1. h4");
		o.searchByGame(" 1. Na3");
		o.searchByGame(" 1. Nc3");
		o.searchByGame(" 1. Nf3");
		o.searchByGame(" 1. Nh3");
		
		o.getFittingMove("1. d4 Nf6 2. c4 e6 3. Nc3",3,1);
	}
	
	void dump()
	{
		for (int i=0;i<oVec.size();i++)
		{
			opening_entry oe = (opening_entry)oVec.elementAt(i);
			oe.dump();
		}
	}
	
	void searchByGame(String s)
	{
		int iM = 0;
		System.out.println("--------------------------");
		System.out.println("Scanning for " + s + ".");
		
		for (int i=0;i<oVec.size();i++)
		{
			opening_entry oe = (opening_entry)oVec.elementAt(i);
			
			if (oe.sGame.indexOf(s) != -1) 
			{
				//oe.dump();
				/*
				System.out.println("Move 5w:" + oe.getMove(5,0));
				System.out.println("Move 5b:" + oe.getMove(5,1));
				System.out.println("Length: " + oe.getLength());
				*/
				iM++;
			}
		}
		
		System.out.println("Found " + iM + " matches");
	}
	
	int getSize()
	{
		return oVec.size();
	}
	
	String getMove(int iGame, int iMove, int iColor)
	{
		opening_entry oe = (opening_entry)oVec.elementAt(iGame);
		if (oe == null) return null;
		
		System.out.println("DBG: openings getMove(" + iMove + "," + iColor+ ") game: " + iGame);
		
		String sMove = oe.getMove(iMove,iColor);
		System.out.println(oe.sGame +":" + sMove);
		return sMove;
	}
	
	boolean isValid(int iGame)
	{
		opening_entry oe = (opening_entry)oVec.elementAt(iGame);
		if (oe == null) return false;
		
		return oe.isValid();
	}
	
	int getOeLength(int iGame)
	{
		opening_entry oe = (opening_entry)oVec.elementAt(iGame);
		if (oe == null) return -1;
		
		return oe.getLength();
	}

	
	String getFittingMove(String sCriteria, int iMove, int iColor)
	{
		int iFits = 0;
		
		System.out.println("DBG: ENTER: GETFITTINGMOVE:" + sCriteria);
		if (sCriteria.equals("")) sCriteria = "1.";
		
		System.out.println("oVec.size:" + oVec.size());
		for (int i=0;i<oVec.size();i++)
		{
			opening_entry oe = (opening_entry)oVec.elementAt(i);
			//System.out.println("scan:" + oe.sGame);
			
			if ((oe.sGame.indexOf(sCriteria) != -1) && (oe.isValid()) && (oe.isValidforColor(iColor)))iFits++;
		}
		System.out.println("iFits="+iFits);
		
		if (iFits == 0) return null;
		
		int iRandom = (int)(iFits * Math.random());
		
		System.out.println("DBG:getFittingMove: " + iRandom);
		
		iFits = 0;
		for (int i=0;i<oVec.size();i++)
		{
			opening_entry oe = (opening_entry)oVec.elementAt(i);
			
			if ((oe.sGame.indexOf(sCriteria) != -1) && (oe.isValid()) && ((oe.isValidforColor(iColor))))
			{
				if (iFits == iRandom)
				{
					System.out.println("Move found, i="+i+" iRandom:" + iRandom);
					System.out.println("crit:" + sCriteria);
					System.out.println("oegame:" + oe.sGame);
					System.out.println("indexof:" + oe.sGame.indexOf(sCriteria));
					return getMove(i,iMove,iColor);
				}
				iFits++;
			}
			
		}
		
		return null;
		
	}
	
	String dumpGame(int i)
	{
		opening_entry oe = (opening_entry)oVec.elementAt(i);
		return oe.sGame;
	}
	
	String dumpEntry(int i)
	{
		opening_entry oe = (opening_entry)oVec.elementAt(i);
		return oe.sName + oe.sGame;
	}
}

class opening_entry
{
	String sFull;
	String sName;
	String sGame;
	int iLength;
	
	opening_entry(String s)
	{
		//System.out.println("opening_entry:" + s);
		
		sFull = s;
		
		int iGameBeg = sFull.indexOf("1.");
		
		sName = sFull.substring(0,iGameBeg-1);
		sGame = " " + sFull.substring(iGameBeg);
		iLength = -1;
	}
	
	void dump()
	{
		System.out.println("Name : " + sName);
		System.out.println("Game : " + sGame);
	}
	
	String getMove(int iMove, int iColor)
	{
		String sCapt = iMove+".";
		String sCaptNext = (iMove+1)+".";
		
		int iBeg = sGame.indexOf(sCapt);
		if (iBeg == -1) return null;
		
		int iEnd = sGame.indexOf(sCaptNext);
		if (iEnd == -1) iEnd = sGame.length();
		
		String sMove = sGame.substring(iBeg,iEnd).trim();
		
		while (sMove.indexOf("  ") != -1) sMove = sMove.replaceAll("  "," ");
		
		String sMoveComp[] = sMove.split(" ");
		
		if ((iColor == piece.WHITE) && (sMoveComp.length > 1)) return sMoveComp[1];
		if ((iColor == piece.BLACK) && (sMoveComp.length > 2)) return sMoveComp[2];
		
		return null;
		
	}
	
	int getLength()
	{
		int iMove=1;
		int iBeg = 0;
		
		if (iLength != -1) return iLength;
		
		System.out.println("getLength: " + sGame);
		while (iBeg != -1)
		{
			String sCapt = iMove+".";
			iBeg = sGame.indexOf(sCapt);
			if (iBeg != -1) iMove++;
		}
		
		iLength = iMove-1;
		return iLength;
	}
	
	boolean isValid()
	{
		//System.out.println("isValid() called: " + sName.substring(0,3));

        return !sName.substring(0, 3).equals("rem");
	}
	
	boolean isValidforColor(int iColor)
	{
        return ((iColor != piece.WHITE) || (sName.indexOf("NOWHITE") == -1)) && ((iColor != piece.BLACK) || (sName.indexOf("NOBLACK") == -1));
    }
}