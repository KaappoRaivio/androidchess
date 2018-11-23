package kaappo.androidchess.askokaappochess;

import java.sql.*;
import java.util.*;
import java.io.PrintWriter;

public class gamehistory
{
	Vector vMoves;
	Vector vTieCands;
	private Vector vNoteVector;
	
	public static final int DUMPSTR_ALG = 42;
	
	gamehistory()
	{
		vMoves = new Vector();
		vTieCands = new Vector();
		vNoteVector = new Vector();
	}
	
	void dump()
	{
		System.out.println("=======================================");
		System.out.println("    ASKOCHESS - GAME HISTORY DUMP");
		System.out.println();
		
		for (int i=0;i<vMoves.size();i++)
		{
			gamehistoryentry ghe = (gamehistoryentry)vMoves.elementAt(i);
			if ((i%2) == 0)
			{
				System.out.println("=======================================");
				System.out.println("Move " + (i/2) +1 + ":");
			}
			ghe.dump();
		}
	}
	
	void dump_to_file(PrintWriter pw)
	{
		pw.println("ASKOCHESS GAME DUMP AT " + new Timestamp(System.currentTimeMillis()));
		pw.println();
		for (int i=0;i<vMoves.size();i++)
		{
			gamehistoryentry ghe = (gamehistoryentry)vMoves.elementAt(i);
			if ((i%2) == 0)
			{
				pw.println("Move " + (i/2) +1 + ":");
			}
			ghe.dump_to_file(pw);
		}
		for(int i=0;i<vNoteVector.size();i++)
		{
			pw.println(vNoteVector.elementAt(i));
		}
	}
	
	String sMovehistory()
	{
		String sHist = "";
		
		for (int i=0;i<vMoves.size();i++)
		{
			gamehistoryentry ghe = (gamehistoryentry)vMoves.elementAt(i);
			sHist = sHist + ghe.cb.lastmoveString() + ";";
		}
		
		return sHist;
	}
	
	String sMovehistory_bylib()
	{
		String sHist = "";
		int iMc = 1;
		
		for (int i=0;i<vMoves.size();i++)
		{
			if ((i%2) == 0)
			{
				sHist = sHist + iMc + ". ";
				iMc++;
			}
			
			
			
			gamehistoryentry ghe = (gamehistoryentry)vMoves.elementAt(i);
			sHist = sHist + ghe.cb.lastmoveString_bylib() + " ";
			
			/*
			if (ghe.cb.miWhiteMoveindex == null) System.out.println("H:WHITEMOVEINDEX == NULL");
			if (ghe.cb.miBlackMoveindex == null) System.out.println("H:BLACKMOVEINDEX == NULL");
			*/
		}
		
		return sHist;
	}
	
	void addmove(chessboard cb, int icolor, movevalue mv)
	{
		gamehistoryentry ghe = new gamehistoryentry(cb,icolor, mv);
		vMoves.add(ghe);
		
		for (int i=0;i<vMoves.size()-1;i++)
		{
			gamehistoryentry ghx = (gamehistoryentry)vMoves.elementAt(i);
			if (ghe.equals(ghx)) 
			{
				System.out.println("Tiecandidate added. Tiecandidate list length: " + vTieCands.size());
				vTieCands.add(ghe);
			}
		}
		
	}
	
	void ohomove ()
	{
		System.out.println("DBG: gamehistory.ohomove() called. vMoves.size():" + vMoves.size());
		
		if (vMoves.size() >= 2)
		{
			vMoves.removeElementAt(vMoves.size()-1);
			vMoves.removeElementAt(vMoves.size()-1);
			System.out.println("DBG: gamehistory.ohomove(): removed 2 elements");
		}
		
		System.out.println("DBG: gamehistory.ohomove() exits. vMoves.size():" + vMoves.size());
	}
	
	boolean bIsTieCandidate(chessboard cb, int icolor)
	{
		if (cb==null) return false;
		gamehistoryentry ghe = new gamehistoryentry(cb,icolor,null);
		
		for (int i=0;i<vMoves.size()-1;i++)
		{
			gamehistoryentry ghx = (gamehistoryentry)vMoves.elementAt(i);
			if (ghe.equals(ghx)) return true;
		}
		//System.out.println("DBG160721: gamehistory.bIsTieCandidate() = false");
		return false;
		
	}
	
	int getLastColor()
	{
		gamehistoryentry ghe = (gamehistoryentry) vMoves.elementAt(vMoves.size()-2);
		return ghe.icolor;
	}
	
	boolean bRepetition()
	{
		if (vMoves.size() < 3) return false;
		int iRep = 0;
		
		gamehistoryentry ghlast = (gamehistoryentry)vMoves.elementAt(vMoves.size()-1);
		
		for (int i=0;i<vMoves.size()-1;i++)
		{
			gamehistoryentry ghe = (gamehistoryentry)vMoves.elementAt(i);
			if (ghe.equals(ghlast)) iRep++;
		}

        return iRep >= 3;
		
	}
	
	gamehistory copy()
	{
		gamehistory gh = new gamehistory();
		
		gh.vMoves = new Vector();
		gh.vTieCands = new Vector();
		
		for (int i=0; i<this.vMoves.size(); i++)
		{
			gh.vMoves.addElement(((gamehistoryentry)(this.vMoves.elementAt(i))).copy());
		}
		
		for (int i=0; i<this.vTieCands.size(); i++)
		{
			gh.vTieCands.addElement(((gamehistoryentry)(this.vTieCands.elementAt(i))).copy());
		}
		
		return gh;
	}
	
	void addNote(String sNote)
	{
		vNoteVector.addElement(sNote);
	}
}

class gamehistoryentry
{
	chessboard cb;
	int icolor;
	long ts;
	movevalue mval;
	
	gamehistoryentry(chessboard c, int col, movevalue mv)
	{
		cb = c.copy();
		icolor = col;
		ts = System.currentTimeMillis();
		if (mv != null) mval = mv.copy();
		else mval = null;
	}
	
	void dump()
	{
		System.out.println("==================");
		System.out.println("Move at " + new Timestamp(ts));
		cb.dump();
		if (mval != null) System.out.println(mval.dumpstr(gamehistory.DUMPSTR_ALG,movevalue.DUMPMODE_SHORT));
		System.out.println(cb.lastmoveString());
		System.out.println("==================");
	}
	
	void dump_to_file(PrintWriter p)
	{
		if (icolor == Piece.WHITE) p.print("WHITE");
		else p.print("BLACK");
		p.println(" move " + cb.lastmoveString() + " at " + new Timestamp(ts));
		cb.dump_to_file(p);
		if (mval != null) p.println(mval.dumpstr(gamehistory.DUMPSTR_ALG,movevalue.DUMPMODE_SHORT));
		p.println("===================");
	}
	
	boolean equals(gamehistoryentry ghe)
	{
		if (icolor != ghe.icolor) return false;
        return cb.equals(ghe.cb);
	}
	
	gamehistoryentry copy()
	{
		gamehistoryentry e = new gamehistoryentry(this.cb.copy(),this.icolor,mval);
		e.ts = this.ts;
		return e;
	}
}