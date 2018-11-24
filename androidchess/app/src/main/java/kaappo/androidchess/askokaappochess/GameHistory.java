package kaappo.androidchess.askokaappochess;

import java.sql.*;
import java.util.*;
import java.io.PrintWriter;

public class GameHistory
{
	Vector<GameHistoryEntry> vMoves;
	Vector vTieCands;
	private Vector vNoteVector;
	
	public static final int DUMPSTR_ALG = 42;
	
	GameHistory()
	{
		vMoves = new Vector<>();
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
			GameHistoryEntry ghe = (GameHistoryEntry)vMoves.elementAt(i);
			if ((i%2) == 0) {
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
			GameHistoryEntry ghe = (GameHistoryEntry)vMoves.elementAt(i);
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
		StringBuilder sHist = new StringBuilder();
		
		for (int i=0;i<vMoves.size();i++)
		{
			GameHistoryEntry ghe = (GameHistoryEntry)vMoves.elementAt(i);
			sHist.append(ghe.cb.lastmoveString()).append(";");
		}
		
		return sHist.toString();
	}
	
	public String sMovehistory_bylib() {
		StringBuilder sHist = new StringBuilder();
		int iMc = 1;

		for (int i=0;i<vMoves.size();i++)
		{
			if ((i%2) == 0)
			{
				sHist.append(iMc).append(". ");
				iMc++;
			}



			GameHistoryEntry ghe = (GameHistoryEntry) vMoves.elementAt(i);
			sHist.append(ghe.cb.lastmoveString_bylib()).append(" ");

			/*
			if (ghe.cb.miWhiteMoveindex == null) System.out.println("H:WHITEMOVEINDEX == NULL");
			if (ghe.cb.miBlackMoveindex == null) System.out.println("H:BLACKMOVEINDEX == NULL");
			*/
		}

		return sHist.toString();
	}

	public String sMovehistory_bylib_newline () {
		StringBuilder sHist = new StringBuilder();
		int iMc = 1;

		for (int i=0;i<vMoves.size();i++)
		{
			if ((i%2) == 0)
			{
				sHist.append(iMc).append(". ");
				iMc++;
			}



			GameHistoryEntry ghe = (GameHistoryEntry) vMoves.elementAt(i);
			sHist.append(ghe.cb.lastmoveString_bylib()).append("\n");

			/*
			if (ghe.cb.miWhiteMoveindex == null) System.out.println("H:WHITEMOVEINDEX == NULL");
			if (ghe.cb.miBlackMoveindex == null) System.out.println("H:BLACKMOVEINDEX == NULL");
			*/
		}

		return sHist.toString();
	}

	
	void addmove(chessboard cb, int icolor, MoveValue mv)
	{
		GameHistoryEntry ghe = new GameHistoryEntry(cb,icolor, mv);
		vMoves.add(ghe);
		
		for (int i=0;i<vMoves.size()-1;i++)
		{
			GameHistoryEntry ghx = (GameHistoryEntry)vMoves.elementAt(i);
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
		GameHistoryEntry ghe = new GameHistoryEntry(cb,icolor,null);
		
		for (int i=0;i<vMoves.size()-1;i++)
		{
			GameHistoryEntry ghx = (GameHistoryEntry)vMoves.elementAt(i);
			if (ghe.equals(ghx)) return true;
		}
		//System.out.println("DBG160721: gamehistory.bIsTieCandidate() = false");
		return false;
		
	}
	
	int getLastColor()
	{
		GameHistoryEntry ghe = (GameHistoryEntry) vMoves.elementAt(vMoves.size()-2);
		return ghe.icolor;
	}
	
	boolean bRepetition()
	{
		if (vMoves.size() < 3) return false;
		int iRep = 0;
		
		GameHistoryEntry ghlast = (GameHistoryEntry)vMoves.elementAt(vMoves.size()-1);
		
		for (int i=0;i<vMoves.size()-1;i++)
		{
			GameHistoryEntry ghe = (GameHistoryEntry)vMoves.elementAt(i);
			if (ghe.equals(ghlast)) iRep++;
		}

        return iRep >= 3;
		
	}
	
	GameHistory copy()
	{
		GameHistory gh = new GameHistory();
		
		gh.vMoves = new Vector();
		gh.vTieCands = new Vector();
		
		for (int i=0; i<this.vMoves.size(); i++)
		{
			gh.vMoves.addElement(((GameHistoryEntry)(this.vMoves.elementAt(i))).copy());
		}
		
		for (int i=0; i<this.vTieCands.size(); i++)
		{
			gh.vTieCands.addElement(((GameHistoryEntry)(this.vTieCands.elementAt(i))).copy());
		}
		
		return gh;
	}
	
	void addNote(String sNote)
	{
		vNoteVector.addElement(sNote);
	}
}

class GameHistoryEntry
{
	chessboard cb;
	int icolor;
	private long ts;
	private MoveValue mval;
	
	GameHistoryEntry(chessboard c, int col, MoveValue mv)
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
		if (mval != null) System.out.println(mval.dumpstr(GameHistory.DUMPSTR_ALG, MoveValue.DUMPMODE_SHORT));
		System.out.println(cb.lastmoveString());
		System.out.println("==================");
	}
	
	void dump_to_file(PrintWriter p)
	{
		if (icolor == Piece.WHITE) p.print("WHITE");
		else p.print("BLACK");
		p.println(" move " + cb.lastmoveString() + " at " + new Timestamp(ts));
		cb.dump_to_file(p);
		if (mval != null) p.println(mval.dumpstr(GameHistory.DUMPSTR_ALG, MoveValue.DUMPMODE_SHORT));
		p.println("===================");
	}
	
	boolean equals(GameHistoryEntry ghe)
	{
		if (icolor != ghe.icolor) return false;
        return cb.equals(ghe.cb);
	}
	
	GameHistoryEntry copy()
	{
		GameHistoryEntry e = new GameHistoryEntry(this.cb.copy(),this.icolor,mval);
		e.ts = this.ts;
		return e;
	}
}