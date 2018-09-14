package kaappo.androidchess.askokaappochess;
import java.util.*;

public class movelibrary
{
	static final int MODE_RANDOM = 1;
	static final int MODE_FIRST = 2;
	
	Vector vMoves;
	int iSeed;
	int iMode;
	
	public static void main (String args[])
	{
		movelibrary m = new movelibrary();
		m.init();
		m.setMode(MODE_FIRST);
		String sG = "";
		System.out.println(m.sNextMove(sG));	
		sG = "A1A1;B2B2";
		System.out.println(m.sNextMove(sG));
		sG = "A2A2";
		System.out.println(m.sNextMove(sG));
		System.out.println("Going to random mode.");
		m.setMode(MODE_RANDOM);
		System.out.println(m.sNextMove(sG));
		System.out.println(m.sNextMove(sG));
		System.out.println(m.sNextMove(sG));
		System.out.println("By Seed");
		m.setSeed(1);
		System.out.println(m.sMoveBySeed(1,0));
		System.out.println(m.sMoveBySeed(1,1));
		System.out.println(m.sMoveBySeed(2,0));
		System.out.println(m.sMoveBySeed(2,1));
		
	}
	
	
	movelibrary()
	{
	}
	
	void setSeed(int i)
	{
		iSeed = i;
	}
	
	int getSeed()
	{
		return iSeed;
	}
	
	void setMode (int i)
	{
		iMode = i;
	}
	
	int size()
	{
		return vMoves.size();
	}
		
	
	String sNextMove(String sGame)
	{
		System.out.println("sNextMove:"+sGame);
		if (iSeed == -2) return null;
		
		Vector vCand = new Vector();
		int retIdx = -1;
		
		for (int i=0;i<vMoves.size();i++)
		{
			String gs = (String)vMoves.elementAt(i);
			if (gs.indexOf(sGame) == 0)
			{
				int iBegInd = 0;
				if (sGame.length() > 0) iBegInd = sGame.length();
				
				vCand.addElement( gs.substring(iBegInd,iBegInd+4));
			}
		}
		
		if (iMode == MODE_FIRST) retIdx = 0;
		else retIdx = (int) (Math.random() * (float)vCand.size());
		
		if (vCand.size() == 0) return null;
		
		return (String)vCand.elementAt(retIdx);
		
	}
	
	String sMoveBySeed(int iMove, int clr)
	{
		//System.out.println("sMoveBySeed " + iSeed);
		
		if (iSeed < -0) return null;
		
		if (iSeed > vMoves.size()) return null;
		
		String sElem = (String)vMoves.elementAt(iSeed);
		int iBegInd = ((iMove-1)*2+clr) * 5;
		
		if ((iBegInd+4) > sElem.length()) return null;
		
		String sMove = sElem.substring(iBegInd, iBegInd +4);

		return sMove;
	}
	
	void init()
	{
		vMoves = new Vector();
		vMoves.addElement("E2E4;C7C5;G1F3;D7D6;D2D4;C5D4;F3D4;G8F6;B1C3;A7A6");
		vMoves.addElement("E2E4;C7C5;G1F3;B8C6;D2D4;C5D4;F3D4;G8F6");
		vMoves.addElement("E2E4;C7C5;G1F3;E7E6;D2D4;C5D4;F3D4");
		vMoves.addElement("E2E4;C7C5;B1C3");
		vMoves.addElement("E2E4;C7C5;C2C3");
		vMoves.addElement("E2E4;D7D6");
		vMoves.addElement("E2E4;D7D5");
		vMoves.addElement("E2E4;E7E5;G1F3;B8C6;F1B5;A7A6;B5A4");
		vMoves.addElement("E2E4;E7E5;G1F3;B8C6;F1C4");
		vMoves.addElement("E2E4;E7E6;D2D4;D7D5;B1C3");
		vMoves.addElement("E2E4;E7E6;D2D4;D7D5;B1D2");
		vMoves.addElement("E2E4;B8C6;D2D4;G8F6");
		vMoves.addElement("E2E4;B8C6;G1F3");
		vMoves.addElement("D2D4;G8F6;C2C4;E7E6;B1C3");
		vMoves.addElement("D2D4;G8F6;C2C4;E7E6;G1F3");
		vMoves.addElement("D2D4;G8F6;C2C4;G7G6");
		vMoves.addElement("D2D4;G8F6;G1F3");
		vMoves.addElement("D2D4;D7D5;C2C4;E7E6;B1C3");
		vMoves.addElement("D2D4;D7D5;C2C4;C7C6;G1F3");
		vMoves.addElement("D2D4;E7E6");
		vMoves.addElement("G1F3;G8F6;C2C4");
		vMoves.addElement("G1F3;G8F6;B1C3;D7D5");
		vMoves.addElement("G1F3;D7D5");
		vMoves.addElement("C2C4;G8F6;B1C3");
		vMoves.addElement("C2C4;E7E5");
		vMoves.addElement("B1C3;D7D5;G1F3;G8F6;D2D3");
		vMoves.addElement("B1C3;D7D5;G1F3;G8F6;D2D4;C8F5");
		
		// vMoves.addElement("F2F4;E7E6"); // DBG 141106
	}
}