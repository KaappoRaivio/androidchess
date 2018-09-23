package kaappo.androidchess.askokaappochess;

import java.util.*;
import java.sql.*;
import java.io.PrintWriter;

public class ttyui
{
	int square[][];
	Vector lMoveV;
	
	chessboard mCb;
	
	int mMaxThreads = 4;
	int mIUrgency;
	
	gamehistory ghist;
	
	boolean bUndoEnabled = true;
	
	int iTurn = -1; // black or white
	
	ttyui(chessboard cb)
	{
		square = new int[9][9];
		
		mCb = cb;
		
		System.out.println("TTY UI CREATED FOR AskoChess");
	}
	
	public void updateData(chessboard cb)
	{
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
				square[i][j] = cb.piecevalue(i,j);
		
		mCb = cb;	
	}
	
	void setMessage(String s)
	{
		System.out.println("MSG:"+s);
	}
	
	void setTurn(int i)
	{
		iTurn = i;
	}
	
	void show()
	{
		dumpSquares();
	}
	
	String getMove()
	{
		String inStr = "";
		boolean bReady = false;
		String sReturn = "";
		
		while (!bReady)
		{
			try
			{
				java.io.InputStreamReader isr = new java.io.InputStreamReader( System.in );
				java.io.BufferedReader stdin = new java.io.BufferedReader( isr );
				
				System.out.print(">");
				inStr = stdin.readLine();
			}
			catch (Exception e)
			{
			}
			
			inStr = inStr.toUpperCase();
			
			String inComp[] = inStr.split(":");
			
			if ((inComp[0].equals("PLAY")) && (inComp.length>2)) 
			{
				int iLevel = Integer.valueOf(inComp[2]);
				if (inComp[1].equals("WHITE")) sReturn = "PLAY:"+ play.PLAYER + ":" +iLevel+":0";
				else if (inComp[1].equals("BLACK")) sReturn = "PLAY:"+iLevel+":" + play.PLAYER +":0";
				if (!sReturn.equals("")) bReady = true;
				
			}
			
			if (inStr.equals("EXIT")) 
			{
				sReturn = inStr;
				bReady = true;
			}
			
			if (inStr.equals("UNDO")) 
			{
				sReturn = "OHO";
				bReady = true;
			}
			
			if ((inStr.length() == 4) && !bReady)
			{
				int x1 = (int)inStr.charAt(0)-64;
				int y1 = (int)inStr.charAt(1)-48;
				int x2 = (int)inStr.charAt(2)-64;
				int y2 = (int)inStr.charAt(3)-48;
		
				if  ((x1 < 1) || (x2 < 1) || (y1 < 1) || ( y2 < 1) || (x1 > 8) || (x2 > 8) || (y1 > 8) || (y2 > 8)) bReady = false;
				
				sReturn = doMove(inStr);
                bReady = sReturn.length() > 1;
				
			}
			
			if ((inStr.length() >= 2) && (inStr.substring(0,2).equalsIgnoreCase("T:")))
			{
				String sThr[]=inStr.split(":");
				int iNewThr = mMaxThreads;
				if (sThr.length>=2)
				{
					try
					{
						iNewThr = new Integer(sThr[1]).intValue();
					}
					catch (Exception e)
					{
					}
					
				}
				if ((iNewThr >= 1) && (iNewThr <= 64))
				{
					mMaxThreads = iNewThr;
					System.out.println("Setting Threads to " + mMaxThreads);
				}
			}
			
			if (inStr.equals("DUMP")) 
			{
				dumptofile();
				System.out.println("Dumped game to a file.");
			}
				
		}
		
		return sReturn;
	}
	
	void setLastMoveVector (Vector v)
	{
		lMoveV = v;
	}
	
	void repaint()
	{
		dumpSquares();
	}
	
	void enableUndo (boolean enable)
	{
		bUndoEnabled = enable;
	}

	int getMaxThreads()
	{
		return mMaxThreads;
	}
	
	void setgamehistory (gamehistory gh)
	{
		ghist = gh;
	}
	
	void displayMsgDialog(String msg)
	{
		System.out.println(msg);
	}
	
	void dumpSquares()
	{
		System.out.println("  abcdefgh");
		for (int j=8;j>=1;j--)
		{
			System.out.print(" "+j);
			for (int i=1;i<=8;i++)
			{
				if (square[i][j]==-1) System.out.print(".");
				else if (square[i][j]>100)
				{
					switch(square[i][j]-100)
					{
						case piece.PAWN:
							System.out.print("P");
							break;
						case piece.BISHOP:
							System.out.print("B");
							break;
						case piece.KNIGHT:
							System.out.print("N");
							break;
						case piece.ROOK:
							System.out.print("R");
							break;
						case piece.KING:
							System.out.print("K");
							break;
						case piece.QUEEN:
							System.out.print("Q");
							break;
						default:
							System.out.print("X");
							break;
					}
				}
				else
				{
					switch(square[i][j])
					{
						case piece.PAWN:
							System.out.print("p");
							break;
						case piece.BISHOP:
							System.out.print("b");
							break;
						case piece.KNIGHT:
							System.out.print("n");
							break;
						case piece.ROOK:
							System.out.print("r");
							break;
						case piece.KING:
							System.out.print("k");
							break;
						case piece.QUEEN:
							System.out.print("q");
							break;
						default:
							System.out.print("x");
							break;
					}
					
				}
			}
			System.out.print(j);
			System.out.println();
		}
		System.out.println("  abcdefgh");
		System.out.println("Last move was: " + lastMoveString());
	}
	
	String lastMoveString()
	{
		Vector v = lMoveV; 
		if (v != null) 
		{
			if (v.size() == 5) 
			{
			
				int i1 = (int)v.elementAt(0);
				int j1 = (int)v.elementAt(1);
				int i2 = (int)v.elementAt(2);
				int j2 = (int)v.elementAt(3);
				int iCapt = (int)v.elementAt(4);
			
				return ""+(char)(64+i1)+j1+(char)(64+i2)+j2;
		
			}
		}
	
		return "";
	
	}
	
	String doMove(String sMove)
	{
		boolean bFound = false;
		
		int x1 = (int)sMove.charAt(0)-64;
		int y1 = (int)sMove.charAt(1)-48;
		int x2 = (int)sMove.charAt(2)-64;
		int y2 = (int)sMove.charAt(3)-48;
		
		String spc = "";
		
		//System.out.println("DBG:doMove:"+sMove + " " + x1 +","+y1+","+x2+","+y2);
		
		if  ((x1 < 1) || (x2 < 1) || (y1 < 1) || ( y2 < 1) || (x1 > 8) || (x2 > 8) || (y1 > 8) || (y2 > 8)) return "";
		
		piece p = mCb.blocks[x1][y1];
		
		if (p == null) return "";
		if (p.iColor != iTurn) return "";
		
		Vector mv = p.moveVector(mCb);
		String sReturnedMove = "";
		
		//System.out.println("DBG:domove @a");
		
		for (int i = 0; i < mv.size(); i++)
		{
			move m = (move)mv.elementAt(i);
			
			if ((m.xtar == x2) && (m.ytar == y2))
			{
				//System.out.println("YIKES!!! THERE'S A MOVE : " + x1 + "," + y1  + "  to: " + x2 + "," + y2);
				bFound = true;
				
				if ((p.iType == piece.PAWN) && (((p.iColor == piece.WHITE) && (m.ytar == 8)) || ((p.iColor == piece.BLACK) && (m.ytar == 1))))
				{
					// promotion code 
					int pq = getPromotedPiece();
					spc = "p"+pq;
				}
				
				sReturnedMove = "" + (char)(x1+64)+y1 + (char)(x2+64)+ y2 + spc;
			}
			
		}
		
		return sReturnedMove;
		//VALIDATE THAT MOVE EXISTS and do promotion too!!!!.
		// copy chesswindow.domove()
	}
	
	int getPromotedPiece()
	{
		boolean bReady = false;
		while(!bReady)
		{
			String inStr ="";
			
			try
			{
				java.io.InputStreamReader isr = new java.io.InputStreamReader( System.in );
				java.io.BufferedReader stdin = new java.io.BufferedReader( isr );
				
				System.out.print("Promote to [QRBN]>");
				inStr = stdin.readLine();
			}
			catch (Exception e)
			{
			}
			
			inStr = inStr.toUpperCase();
			if (inStr.charAt(1) == 'Q') return piece.QUEEN;
			if (inStr.charAt(1) == 'R') return piece.ROOK;
			if (inStr.charAt(1) == 'B') return piece.BISHOP;
			if (inStr.charAt(1) == 'N') return piece.KNIGHT;
		}
		return -1;
	}
	
	void dumptofile()
	{
		if (ghist != null)
		{
			System.out.println("going to print...");
			String sFile = "AskoChess_Gameat_";
			try 
			{
				sFile = sFile + new Timestamp(System.currentTimeMillis()) + ".out";
				String sFile2 = sFile.substring(0,21) + sFile.substring(22,24) + sFile.substring(25,27)+sFile.substring(28,30)+sFile.substring(31,33) + ".out";
				System.out.println("File name <" + sFile2 + ">");
				PrintWriter pw = new PrintWriter(sFile2);
				
				ghist.dump_to_file(pw);
				pw.close();
				System.out.println("Printing done.");
			}
			catch (Exception e)
			{
				System.out.println("Exception at print: " + e.getMessage());
			}
		}
	}
	
	void setLatencies(long[] lLatency)
	{
	}
}