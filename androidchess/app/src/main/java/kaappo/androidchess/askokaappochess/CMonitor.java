package kaappo.androidchess.askokaappochess;
public class CMonitor
{
	static long iFindMoveCnt;
	static long iChessboardInit;
	static long iChessboardCopy;
	static long iValueComp;
	static long iLevel4Moves;
	static long iMIndGetBest;
	static long iMoveCPPawnPProm;
	
	static long lastDumpTime;
	
	static int iMonLevel = -1;
	static String sDrawMove = "";
	
	static TimeLimControl tlc = null;
	
	static boolean bNoBlood = false;
	
	public static final int DUMP_INTERVAL = 60000;
	
	CMonitor()
	{
		lastDumpTime = System.currentTimeMillis();
	}
	
	static void incFindMoveCnt()
	{
		iFindMoveCnt++;
		checkLastDump();
	}
	
	static void incChessboardInit()
	{
		iChessboardInit++;
		checkLastDump();
	}
	
	static void incChessboardCopy()
	{
		iChessboardCopy++;
		checkLastDump();
	}
	
	static void incValueComp()
	{
		iValueComp++;
		checkLastDump();
	}
	
	static void incLevel4Moves()
	{	
		iLevel4Moves++;
		checkLastDump();
	}

	static void inciMIndGetBest()
	{	
		iMIndGetBest++;
		checkLastDump();
	}
	
	static void inciMoveCPPawnPProm()
	{
		iMoveCPPawnPProm++;
		checkLastDump();
	}
	
	public static void dumpValues()
	{
		System.out.println("CMonitor: " + new java.sql.Timestamp(System.currentTimeMillis()) + ": " + iFindMoveCnt + " " + iChessboardInit + " " + iChessboardCopy +" "+iValueComp+ " " + iLevel4Moves+ " " + iMIndGetBest + " " + iMoveCPPawnPProm);
	}
	
	static synchronized void checkLastDump()
	{
		long lNow = System.currentTimeMillis();
		if (lNow > lastDumpTime + DUMP_INTERVAL )
		{
			dumpValues();
			lastDumpTime = lNow;
			
		}
	}
	
	static int getTimeLim(int iRounds, int iTimeLim)
	{
		if (tlc == null) return iTimeLim;
		else return tlc.iCorrTimeLim(iRounds,iTimeLim);
	}
	
	public static void setTimeLimCtl(boolean bCt)
	{
		if (bCt)
		{			
			tlc = new TimeLimControl();
			tlc.iState = TimeLimControl.TLC_ACTIVE;
		}
		else tlc = null;
	}
	
	static void setUrgency(int iUrg)
	{
		if (tlc != null) tlc.iUrgency = iUrg;
	}
	
	static void setNoBlood(boolean b)
	{
		bNoBlood = b;
	}
	
	static boolean bNoBlood()
	{
		return bNoBlood;
	}
}

class TimeLimControl
{
	int iState;
	static final int TLC_INACTIVE = 0;
	static final int TLC_ACTIVE = 1;
	
	int iUrgency;
	int iTopLevel = -1;
	
	TimeLimControl()
	{
		iState = TLC_INACTIVE;
	}
	
	int iCorrTimeLim(int iRounds, int iTimeLim)
	{
		//System.out.println("DBG151224: TimeLimControl.iCorrTimeLim called.");
		if (iState == TLC_INACTIVE) return iTimeLim;
		switch (iUrgency)
		{
			case 0:
				//if (iRounds == 3) return 20;   160429 goodwhite34.dat ei toimi pienemmällä
				if (iRounds == 3) return 12;   // 160513
				if (iRounds == 4) return 55;
				break;
				
			case 1:	
				if (iRounds == 3) return 6;
				if (iRounds == 4) return 25;
				break;
		}
		return iTimeLim;
	}
	
	void setTopLevel(int iLev)
	{
		iTopLevel = iLev;
	}
}