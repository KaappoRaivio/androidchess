package kaappo.androidchess.askokaappochess;
import java.io.*;
import java.util.*;

public class engine
{
	static Vector mVec;
	static int iLastScore;
	static int iLastMateScore;
	static boolean bLastMate;
	
	static String sSTOCKFISH = "stockfish-6-64.exe";
	static String sABROK = "Abrok_5_0.exe";
	//static String sABROK = "ekipekka";
	
	public static void main (String args[]) throws Exception
	{
		System.out.println(getMoveByAlg(sSTOCKFISH, args[0],movevalue.ALG_ASK_FROM_ENGINE10));
		//System.out.println(getMoveByAlg(sABROK, args[0],movevalue.ALG_ASK_FROM_ABROK4));
		
		
		System.out.println("iLastScore:" + iLastScore);
		System.out.println("iLastMateScore:" + iLastMateScore);
	}
	
	engine()
	{
	}
	
	static String sEnginePerAlg(int iAlg)
	{
		if ((iAlg >= movevalue.ALG_ASK_FROM_ENGINE1) && (iAlg <= movevalue.ALG_ASK_FROM_ENGINE10)) return sSTOCKFISH;
		
		if ((iAlg >= movevalue.ALG_ASK_FROM_ABROK1) && (iAlg <= movevalue.ALG_ASK_FROM_ABROK4)) return sABROK;
		
		System.out.println("engine.sEnginePerAlg. Bad iAlg:" + iAlg);
		throw new RuntimeException("engine.sEnginePerAlg. Bad iAlg:" + iAlg);
	}
	
	static int iNodeLimPerAlg(int iAlg)
	{
		int iNodeLim = 1000;
		
		switch (iAlg)
		{
			case movevalue.ALG_ASK_FROM_ENGINE1:
			case movevalue.ALG_ASK_FROM_ABROK1:	
				iNodeLim = 1000;
				break;
				
			case movevalue.ALG_ASK_FROM_ENGINE2:
			case movevalue.ALG_ASK_FROM_ABROK2:	
				iNodeLim = 3000;
				break;	
				
			case movevalue.ALG_ASK_FROM_ENGINE3:
			case movevalue.ALG_ASK_FROM_ABROK3:	
				iNodeLim = 10000;
				break;

			case movevalue.ALG_ASK_FROM_ENGINE4:
			case movevalue.ALG_ASK_FROM_ABROK4:	
				iNodeLim = 30000;
				break;
			case movevalue.ALG_ASK_FROM_ENGINE5:
				iNodeLim = 100000;
				break;		

			case movevalue.ALG_ASK_FROM_ENGINE6:
				iNodeLim = 300000;
				break;
				
			case movevalue.ALG_ASK_FROM_ENGINE7:
				iNodeLim = 1000000;
				break;	
				
			case movevalue.ALG_ASK_FROM_ENGINE8:
				iNodeLim = 3000000;
				break;

			case movevalue.ALG_ASK_FROM_ENGINE9:
				iNodeLim = 10000000;
				break;
				
			case movevalue.ALG_ASK_FROM_ENGINE10:
				iNodeLim = 30000000;
				break;					
		}
		
		return iNodeLim;
	}
	
	static chessboard findAndDoMove(chessboard cb, int iAlg, boolean bSave, boolean bLoad) throws Exception
	{
		chessboard old_cb = cb.copy();
		String sFEN = old_cb.FEN();
		
		System.out.println("DBG 150507: engine asked :" + sFEN);
		
		if (iAlg == movevalue.ALG_ASK_FROM_ENGINE_RND)
		{
			if (Math.random() < 0.2) // ask from abrok occasionally too
			{
				iAlg = movevalue.ALG_ASK_FROM_ABROK1 + (int)(Math.random()*4);
				System.out.println("ENGINE: Random alg fixed to: " + iAlg + " (ABROK)");
				bSave = true;
				
			}
			else
			{
				iAlg = movevalue.ALG_ASK_FROM_ENGINE1 + (int)(Math.random()*10);
				System.out.println("ENGINE: Random alg fixed to: " + iAlg);
				bSave = true;
			}
		}
		
		if (bLoad)
		{
			chessboard cbr = anyokmove.db_anymoveget(cb.FEN(),iAlg+"", false);
			if (cbr != null) return cbr;
		}
		
		
		int iNodeLim = iNodeLimPerAlg(iAlg);
		
		String sMove = getMove(sEnginePerAlg(iAlg),sFEN,iNodeLim);
		
		if ((iAlg >= movevalue.ALG_ASK_FROM_ABROK1) && (iAlg <= movevalue.ALG_ASK_FROM_ABROK1))
		{
			System.out.println("Abrok engine operation just done. Smove="+sMove);
		}
		
		if (sMove == null) return null;
		
		sMove= sMove.toUpperCase();
		
		System.out.println("DBG 150507: engine responded :" + sMove);
		
		if (sMove.equals("MATE")) return null;
		
		int xk,yk;
		xk = (int)sMove.charAt(0)-64;
		yk = (int)sMove.charAt(1)-48;
		
		piece p = old_cb.blocks[xk][yk];
		
		chessboard new_cb = old_cb.copy();
		new_cb.domove(sMove,p.iColor);
		
		if (bSave) anyokmove.db_anymovesave(old_cb.FEN(),iAlg+"",new_cb.lastmoveString());
		
		return new_cb;
	}
	
	static String getMoveByAlg(String sEngine, String sFEN,int iAlg) throws Exception
	{
		mVec = new Vector();
		
		if (iAlg == movevalue.ALG_ASK_FROM_ENGINE_RND)
		{
			iAlg = movevalue.ALG_ASK_FROM_ENGINE1 + (int)(Math.random()*10);
			System.out.println("ENGINE: Random alg fixed to: " + iAlg);
		}
		
		int iNodeLimit = iNodeLimPerAlg(iAlg);
		return getMove(sEngine,sFEN,iNodeLimit);
	}
	
	static String getMove(String sEngine, String sFEN, int iNodeLimit) throws Exception
	{
		if (mVec == null) mVec = new Vector();
		iLastScore = 0;
		iLastMateScore = 0;
		bLastMate = false;
		
		
		String sMove = "";
		
		System.out.println("engine.getMove() starts: sEngine:" + sEngine + " iNodeLimit:" + iNodeLimit);
		
		long lStart = System.currentTimeMillis();
		
		
		//Process p = Runtime.getRuntime().exec("stockfish-6-64.exe");
		Process p = Runtime.getRuntime().exec(sEngine);
             
        BufferedReader stdInput = new BufferedReader(new
                 InputStreamReader(p.getInputStream()));
 
        BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(p.getErrorStream()));
				 
		OutputStream output = p.getOutputStream();		 
		PrintWriter pw = new PrintWriter(output);
		
		String ss = stdInput.readLine();
		
		pw.println("uci");
		pw.flush();
		pw.println("ucinewgame");
		pw.flush();
		pw.println("position fen " + sFEN);
		pw.flush();
		try
		{
			Thread.sleep(200);
		}
		catch (Exception e)
		{
		}
		pw.println("go infinite");
		pw.flush();
		
		System.out.print("DBG150516:ENGINE MOVING!");
		System.out.print(" sFEN:" + sFEN);
		System.out.println(" iNodeLimit:" + iNodeLimit);
		
		sMove = null;
		int iNodes = 0;
		int iDepth = 0;
		
		int iOutputLim = -1; 
		int iMoveIndPos = -1; 
		int iMovePos = -1;
		boolean bQuitAtFullRow = false;
		
		if (sEngine.equals(sSTOCKFISH))
		{
			iOutputLim = 20;
			iMoveIndPos = 18;
			iMovePos = 19;
		}
		else if (sEngine.equals(sABROK))
		{
			iOutputLim = 16;
			iMoveIndPos = 14;
			iMovePos = 15;
			bQuitAtFullRow = true;
		}
		else
		{			
			System.out.println("Bad engine type " + sEngine+ " Force exit");
			throw new RuntimeException("Bad engine type " + sEngine+ " Force exit");
		}	
		
		while (true)
		{
			ss = stdInput.readLine();
			if (ss != null)
			{	
			
				//System.out.println("#" + ss);
				String ssParts[] = ss.split(" ");
				
				if ((ss.indexOf("currmove") != -1) || (ss.indexOf("lowerbound nodes") !=-1) || (ss.indexOf("upperbound nodes") !=-1) )
				{
					System.out.println("#" + ss);
					pw.println("quit");
					pw.flush();
					return sMove;
				}
				
				if (ss.indexOf("bestmove") != -1)
				{
					System.out.println("#" + ss);
					ssParts = ss.split(" ");
					sMove = ssParts[1];
					pw.println("quit");
					pw.flush();
					return sMove;
				}
				
				if ((ss.indexOf("score mate") != -1) &&  (ss.indexOf("multipv") == -1))
				{
					System.out.println("#" + ss);
					sMove = "mate";
					pw.println("quit");
					pw.flush();
					bLastMate = true;
					return sMove;
				}
				
				if (ss.indexOf("info depth 0 score cp 0") != -1)
				{
					System.out.println("#" + ss);
					sMove = null;
					pw.println("quit");
					pw.flush();
					return sMove;
				}
				
				if ((ssParts.length >= 10) && (ssParts[8].equals("cp")))
				{
					iLastScore = new Integer(ssParts[9]).intValue();
				}
				
				if ((ssParts.length >= 10) && (ssParts[8].equals("mate")))
				{
					iLastMateScore = new Integer(ssParts[9]).intValue();
				}
				
				//System.out.println(ssParts.length);
				if (ssParts.length >= iOutputLim)
				{
					//System.out.println("SSP>20");
					//System.out.println(ssParts[11]+":"+ssParts[19]);
					iNodes = new Integer(ssParts[11]).intValue();
					iDepth = new Integer(ssParts[2]).intValue();
					//System.out.println("iNodes:" + iNodes);
					if ((iNodeLimit < iNodes) || (iDepth > 50))
					{
						String sMoveInd = ssParts[iMoveIndPos];
						if (sMoveInd.equals("pv")) ; //System.out.println("PVOK(A)");
						else 
						{
							System.out.println("BAD MOVEIND:" + sMoveInd);
							throw new RuntimeException("BAD MOVEIND:" + sMoveInd);
						} 
						
						if (sMove == null ) sMove = ssParts[iMovePos];  // just in case first info record exceeds the limit
						
						System.out.println("#" + ss);
						System.out.println("Move in "+ iNodeLimit + " nodes:" + sMove);
						pw.println("quit");
						pw.flush();
						long lEnd = System.currentTimeMillis();
						long lDur = lEnd - lStart;
						System.out.println("Duration: "+ lDur + " msec.");
						return sMove;
					}
					String sMoveInd = ssParts[iMoveIndPos];
					if (sMoveInd.equals("pv")); // System.out.println("PVOK(B)");
					else 
					{
						System.out.println("BAD MOVEIND:" + sMoveInd);
						throw new RuntimeException("BAD MOVEIND:" + sMoveInd);
					} 
						
					sMove = ssParts[iMovePos];
					//System.out.println("Smove(B):" + sMove+ " ssParts.length:" + ssParts.length);
					boolean bFound = false;
					for (int i=0;i<mVec.size();i++)
					{
						String sCand = (String)mVec.elementAt(i);
						if (sCand.equalsIgnoreCase(sMove)) bFound = true;
					}
					if (!bFound) mVec.addElement(sMove);
					
					if ((ssParts.length==(iMovePos+1)) && (bQuitAtFullRow))
					{
						//System.out.println("DBG180123: Q1");
						pw.println("quit");
						pw.flush();
						return sMove;
					}
					
					
				}
			

			}
		}  // while true
		
	}
}