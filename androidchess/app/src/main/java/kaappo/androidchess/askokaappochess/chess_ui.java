package kaappo.androidchess.askokaappochess;

import java.util.*;

public class chess_ui
{
	public static final int UI_TYPE_WINDOW = 1;
	public static final int UI_TYPE_TTY = 2;
	
	chesswindow cw;
	ttyui tty;
	
	chess_ui(int iType, chessboard cb)
	{
		switch (iType)
		{
			case UI_TYPE_WINDOW:
				cw = new chesswindow (100,100,500,550,50,cb);
				tty = null;
				break;
				
			case UI_TYPE_TTY:
				tty = new ttyui(cb);
				cw = null;
				break;
			
			default:
				break;
		}
	}
	
	void updateData(chessboard cb)
	{
		if (cw != null) cw.updateData(cb);
		if (tty != null) tty.updateData(cb);
	}
	
	void setMessage(String s)
	{
		if (cw != null) cw.setMessage(s);
		if (tty != null) tty.setMessage(s);
	}
	
	void setTurn(int i)
	{
		if (cw != null) cw.setTurn(i);
		if (tty != null) tty.setTurn(i);
	}
	
	void show()
	{	
		if (cw != null) cw.show();
		if (tty != null) tty.show();
	}
	
	String getMove()
	{
		if (cw!=null) return cw.getMove();
		if (tty!=null) return tty.getMove();
		
		return null;
	}
	
	void setLastMoveVector (Vector v)
	{
		if (cw!=null) cw.setLastMoveVector(v);
		if (tty != null) tty.setLastMoveVector(v);
	}
	
	void repaint()
	{
		if (cw != null) cw.repaint();
		if (tty != null) tty.repaint();
	}
	
	void enableUndo(boolean enable)
	{
		if (cw != null) cw.ch_item_undo.setEnabled(enable);
		if (tty != null) tty.enableUndo(enable);
	}
	
	int getMaxThreads()
	{
		if (cw!= null) return cw.mMaxThreads;
		if (tty!=null) return tty.getMaxThreads();
		
		return 1;
	}
	
	int getUrgency()
	{
		if (cw!= null) return cw.mIUrgency;
		if (tty!=null) return tty.mIUrgency;
		
		return 0;
	}
	
	void setgamehistory (gamehistory gh)
	{
		if (cw != null) cw.setgamehistory(gh);
		if (tty != null) tty.setgamehistory(gh);
	}
	
	void displayMsgDialog(String msg)
	{
		if (cw != null) cw.displayMsgDialog(msg);
		if (tty != null) tty.displayMsgDialog(msg);
	}
	
	void setLatencies(long[] lLatency)
	{
		if (cw != null) cw.setLatencies(lLatency);
		if (tty != null) tty.setLatencies(lLatency);
	}
	
	static void setMonitorMode(boolean bMode)
	{
		chesswindow.setMonitorMode(bMode);
	}
	
	static int getiAnalStartLevel()
	{
		return chesswindow.getiAnalStartLevel ();
		
	}
	
	static void setiAnalRoundsTotal(int iRounds)
	{
		chesswindow.setiAnalRoundsTotal(iRounds);
	}
	
	static int getiAnalStartColor ()
	{
		return chesswindow.getiAnalStartColor();
	}
	
	static void setiPrelRoundsTotal(int iTot)
	{
		chesswindow.setiPrelRoundsTotal(iTot);
	}
	
	static void setiAnalRoundsDone(int iDone)
	{
		chesswindow.setiAnalRoundsDone(iDone);
	}
	
	static void monRefresh(boolean bFlag)
	{
		chesswindow.monRefresh(bFlag);
	}
	
	static void setBestMval(movevalue mv)
	{
		chesswindow.setBestMval(mv);
	}
	
	static void setBestPreMval(movevalue mv)
	{
		chesswindow.setBestPreMval(mv);
	}
	
	static void setAnalCurrent(String s)
	{
		chesswindow.setAnalCurrent(s);
	}
	
	static void setAnalPreCurrent(String s)
	{
		chesswindow.setAnalPreCurrent(s);
	}
	
	static void setiPrelRoundsDone(int iDone)
	{
		chesswindow.setiPrelRoundsDone(iDone);
	}
	
	static int getiPrelRoundsTotal()
	{
		return chesswindow.getiPrelRoundsTotal();
	}
	
	static void setlAnalEndTime(long lTime)
	{
		chesswindow.setlAnalEndTime(lTime);
	}
	
	static void setAnalysisStart(int iLev, int iCol)
	{
		chesswindow.setAnalysisStart(iLev,iCol);
	}
	
	static void setAnalysisStartMval(movevalue mv)
	{
		chesswindow.setAnalysisStartMval(mv);
	}
	
}