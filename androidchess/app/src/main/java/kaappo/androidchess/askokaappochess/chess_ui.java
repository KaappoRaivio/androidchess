package kaappo.androidchess.askokaappochess;

import java.util.*;

import kaappo.androidchess.ChessActivity;

public class chess_ui
{
	public static final int UI_TYPE_WINDOW = 1;
	public static final int UI_TYPE_TTY = 2;
	public static final int UI_TYPE_ANDROID = 3;
	
	chesswindow cw;
	ttyui tty = null;
	AndroidUI androidUI;
	
	public chess_ui(int iType, chessboard cb, ChessActivity context) {
        this.androidUI = new AndroidUI(cb, context);
	}
	
	public void updateData(chessboard cb)
	{

		if (tty != null) tty.updateData(cb);
		if (androidUI != null) androidUI.updateData(cb);
	}
	
	public void setMessage(String s)
	{

		if (tty != null) tty.setMessage(s);
        if (androidUI != null) androidUI.setMessage(s);
    }
	
	public void setTurn(int i)
	{

		if (tty != null) tty.setTurn(i);
        if (androidUI != null) androidUI.setTurn(i);
    }
	
	public void show() throws Exception
	{	

		if (tty != null) tty.show();
        if (androidUI != null) androidUI.updateBoard();
    }
	
	public String getMove()
	{

		if (tty!=null) return tty.getMove();
        if (androidUI != null) return androidUI.getMove();


	}
	
	public void setLastMoveVector (Vector v)
	{

		if (tty != null) tty.setLastMoveVector(v);

    }
	
	void repaint() throws Exception
	{

		if (tty != null) tty.repaint();
        if (androidUI != null) androidUI.updateBoard();
    }
	
	void enableUndo(boolean enable)
	{

		if (tty != null) tty.enableUndo(enable);
        if (androidUI != null) androidUI.undoEnabled = enable;
    }
	
	public int getMaxThreads()
	{

		if (tty!=null) return tty.getMaxThreads();

        return 1;
	}
	
	int getUrgency()
	{

		if (tty!=null) return tty.mIUrgency;
        if (androidUI != null) return androidUI.getUrgency();

        return 0;
	}
	
	void setgamehistory (gamehistory gh)
	{

		if (tty != null) tty.setgamehistory(gh);

    }
	
	void displayMsgDialog(String msg)
	{

		if (tty != null) tty.displayMsgDialog(msg);
        if (androidUI != null) androidUI.displayMsgDialog(msg);
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