package kaappo.androidchess.askokaappochess;
import java.sql.*;
import java.util.*;

public class chtest
{
	public static void main (String args[]) throws Exception
	{
		int iWhitelevel = -1;
		int iBlacklevel = -1;
		
		boolean bInputOK = false;
		System.out.println("Askon shakkikone proto 4 / 17.1.2014");
		//System.out.println(args.length);
		
		String a0 = null;
		String a0beg = null;
		String a0file = null;
		
		if (args.length == 3)
		{
			a0 = args[0];
			
			String sParts[] = a0.split(":");
			a0beg = sParts[0];
			if (sParts.length > 1) a0file = sParts[1];
			System.out.println("a0beg:'"+a0beg+"'");
			System.out.println("a0file:'"+a0file+"'");
			
			if ((a0beg.equalsIgnoreCase("simu")) || (a0beg.equalsIgnoreCase("filesimu")))
			{
				iWhitelevel = new Integer(args[1]).intValue();
				iBlacklevel = new Integer(args[2]).intValue();
				bInputOK = true;
			}
			if ((a0beg.equalsIgnoreCase("play")) || (a0beg.equalsIgnoreCase("fileplay")))
			{
				if (args[1].equalsIgnoreCase("black"))
				{
					iWhitelevel = new Integer(args[2]).intValue();
					bInputOK = true;
				}
				if (args[1].equalsIgnoreCase("white"))
				{
					iBlacklevel = new Integer(args[2]).intValue();
					bInputOK = true;
				}
			}
		}
		
		//System.out.println(iWhitelevel + "," + iBlacklevel);
		
		if ((iWhitelevel > 3) || (iBlacklevel > 3)) bInputOK = false;
		if ((iWhitelevel < 1) && (iBlacklevel < 1)) bInputOK = false;
		
		if (!bInputOK)
		{
			System.out.println("Usage examples: ");
			System.out.println(" java chtest SIMU 3 2      -- simulate white on level 3 against black on level 2");
			System.out.println(" java chtest PLAY WHITE 3  -- simulate player on white against black on level 3");
			return;
		}
		
		chessboard cb = new chessboard();
		chessboard cb2 = null;
		chessboard ohoboard = null;
		
		String sPar1 = args[0].toUpperCase();
		System.out.println("sPar1="+sPar1);
		if (a0file != null)
		{
			System.out.println("File name :"+a0file);
			boolean br = cb.init_from_file(a0file);
			System.out.println("New board read from file.");
		}
		else
		{		

			cb.init();
		}
		cb.dump();
		
		int iMove = 1;
		
		chesswindow cw = new chesswindow (100,100,500,550,50, cb);
		cw.updateData(cb);
		cw.setMessage(" Move " +iMove+". Your move.");
		cw.show();
		if (iWhitelevel == -1) cw.setTurn(piece.WHITE);
		
		
		
		String inStr;
		
		java.io.InputStreamReader isr = new java.io.InputStreamReader( System.in );
		java.io.BufferedReader stdin = new java.io.BufferedReader( isr );
		
		while (iMove < 100)
		{
			cb.iMoveCounter = iMove;
			System.out.println("==============================");
			System.out.println("Move " + iMove + " at " + new Timestamp(System.currentTimeMillis()) + ".");
			
			if (iWhitelevel > 0) 
			{
				//movevalue mmval = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
				movevalue mmval = new movevalue("");
				mmval.setbase(0);
				
				cw.setMessage("... thinking move " + iMove);
				cb2 = cb.findAndDoBestMove(0,iWhitelevel,mmval,0,false, null, null,false, null,null,null,null, null);
				cw.setMessage(" Move " +(iMove+1)+". Your move.");
				cw.setTurn(piece.BLACK);
				
				if (cb2 == null)
				{
					System.out.println("cb2=Null");
					System.exit(0);
				}
				
				king pKing = cb2.locateKing(piece.BLACK);
				if (pKing == null) 
				{
					System.out.println("King missing!");
					System.exit(0);
				}
		
				int checkcount = cb2.iCountCheckers(pKing);
				//piece pChecker = locateFirstChecker(pKing);
				Vector vCheck = cb2.locateCheckersVector(pKing);
				if (checkcount > 0)
				{
					cw.setMessage(" Move " +(iMove+1)+". Your move. CHECK");
					int mc = cb.clearMoveVectorsUnderCheck(piece.BLACK,pKing,vCheck);
					if (mc == 0) System.out.println("CHECKMATE!!!!");
				}				
				
				
				if (cb2 == null)
				{
					System.out.println("BLACK HAS WON IN " + (iMove-1) + " MOVES. ");
					System.out.println("Returned mmval : " + mmval.dumpstr(0));
					inStr = stdin.readLine();
					System.exit(0);
				}
			}
			else
			{
				cb2 = cb.copy();
				boolean bDone = false;
				while (!bDone)
				{
					System.out.print("WHITE>");
					//inStr = stdin.readLine();
					inStr = cw.getMove();
					if (inStr.equalsIgnoreCase("oho"))
					{
						cb2 = ohoboard.copy();
						System.out.println("Revert one move");
						cb2.dump();
						cw.updateData(cb2);
						cw.setLastMoveVector(cb2.lastmoveVector());
						cw.repaint();
						iMove = iMove-1;
					}
					else
					{
						ohoboard = cb2.copy();
					}
					bDone = cb2.domove(inStr,0);   // WHITE MOVE HERE !!!!
				}				
			}
			cb2.prefixdump("PLAY:BLACK>", chessboard.DUMPMODE_SHORT);
			//cb2.dump();
			cw.updateData(cb2);
			cw.setLastMoveVector(cb2.lastmoveVector());
			cw.repaint();
			
			if (iBlacklevel > 0) 
			{
				//movevalue mmval = new movevalue("",0,0,0,0,0,0,0,0,false,false,false,false,false,false,0,0,0,0,0,0,0,0,false,false,false,0,0,0,0,0,0,0,0,0,0,0,0,0,false,false,false,false);
				movevalue mmval = new movevalue("");
				mmval.setbase(1);
				
				cw.setMessage("... thinking move " + iMove);
				cb = cb2.findAndDoBestMove(1,iBlacklevel,mmval,0,false, null, null,false, null,null,null,null, null);
				if (cb == null) System.out.println("cb = null!! ");
				cw.setMessage(" Move " +(iMove+1)+". Your move.");
				cw.setTurn(piece.WHITE);
				
				king pKing = cb.locateKing(piece.WHITE);
				if (pKing == null) 
				{
					System.out.println("King missing!");
					System.exit(0);
				}
		
				int checkcount = cb.iCountCheckers(pKing);
				//piece pChecker = locateFirstChecker(pKing);
				Vector vCheck = cb.locateCheckersVector(pKing);
				if (checkcount > 0)
				{
					cw.setMessage(" Move " +(iMove+1)+". Your move. CHECK");
					int mc = cb.clearMoveVectorsUnderCheck(piece.WHITE,pKing,vCheck);
					if (mc == 0) System.out.println("CHECKMATE!!!!");
				}				
				
				if (cb == null)
				{
					System.out.println("WHITE HAS WON IN " + (iMove-1) + " MOVES. ");
					inStr = stdin.readLine();
					System.exit(0);
				}
			}
			
			else
			{
				cb = cb2.copy();
				boolean bDone = false;
				while (!bDone)
				{
					System.out.print("BLACK>");
					//inStr = stdin.readLine();
					inStr = cw.getMove();
					if (inStr.equalsIgnoreCase("oho"))
					{
						cb = ohoboard.copy();
						System.out.println("Revert one move");
						cb.dump();
						cw.updateData(cb);
						cw.setLastMoveVector(cb.lastmoveVector());
						cw.repaint();
						iMove = iMove-1;
					}
					else
					{
						ohoboard = cb.copy();
					}
					bDone = cb.domove(inStr,1);   // BLACK MOVE HERE  
				}
			}
			//cb.dump();
			cb.prefixdump("PLAY:WHITE>",chessboard.DUMPMODE_SHORT);
			cw.updateData(cb);
			cw.setLastMoveVector(cb.lastmoveVector());
			cw.repaint();
			iMove++;
		}
	
		
	}
}