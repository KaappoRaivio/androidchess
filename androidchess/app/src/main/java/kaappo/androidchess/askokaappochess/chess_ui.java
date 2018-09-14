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
//				cw = new chesswindow (100,100,500,550,50,cb);
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

		if (tty != null) tty.updateData(cb);
	}
	
	void setMessage(String s)
	{

		if (tty != null) tty.setMessage(s);
	}
	
	void setTurn(int i)
	{

		if (tty != null) tty.setTurn(i);
	}
	
	void show()
	{	

		if (tty != null) tty.show();
	}
	
	String getMove()
	{

		if (tty!=null) return tty.getMove();
		
		return null;
	}
	
	void setLastMoveVector (Vector v)
	{

		if (tty != null) tty.setLastMoveVector(v);
	}
	
	void repaint()
	{

		if (tty != null) tty.repaint();
	}
	
	void enableUndo(boolean enable)
	{

		if (tty != null) tty.enableUndo(enable);
	}
	
	int getMaxThreads()
	{

		if (tty!=null) return tty.getMaxThreads();
		
		return 1;
	}
	
	int getUrgency()
	{

		if (tty!=null) return tty.mIUrgency;
		
		return 0;
	}
	
	void setgamehistory (gamehistory gh)
	{

		if (tty != null) tty.setgamehistory(gh);
	}
	
	void displayMsgDialog(String msg)
	{

		if (tty != null) tty.displayMsgDialog(msg);
	}
	
	void setLatencies(long[] lLatency)
	{

		if (tty != null) tty.setLatencies(lLatency);
	}
	
	static void setMonitorMode(boolean bMode)
	{

	}
	
	static int getiAnalStartLevel()
	{
		return -1;
		
	}
	
	static void setiAnalRoundsTotal(int iRounds)
	{

	}
	
	static int getiAnalStartColor ()
	{
		return -1;
	}
	
	static void setiPrelRoundsTotal(int iTot)
	{

	}
	
	static void setiAnalRoundsDone(int iDone)
	{

	}
	
	static void monRefresh(boolean bFlag)
	{

	}
	
	static void setBestMval(movevalue mv)
	{

	}
	
	static void setBestPreMval(movevalue mv)
	{

	}
	
	static void setAnalCurrent(String s)
	{

	}
	
	static void setAnalPreCurrent(String s)
	{

	}
	
	static void setiPrelRoundsDone(int iDone){
	}
	
	static int getiPrelRoundsTotal()
	{
		return -1;
	}
	
	static void setlAnalEndTime(long lTime)
	{

	}
	
	static void setAnalysisStart(int iLev, int iCol)
	{

	}
	
	static void setAnalysisStartMval(movevalue mv)
	{

	}
	
}