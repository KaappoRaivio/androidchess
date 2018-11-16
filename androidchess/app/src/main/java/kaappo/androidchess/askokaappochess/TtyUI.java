package kaappo.androidchess.askokaappochess;

import java.util.*;
import java.sql.*;
import java.io.PrintWriter;
import java.util.concurrent.CyclicBarrier;

import kaappo.androidchess.MyDragListener;
import kaappo.androidchess.TtyuiActivity;

public class TtyUI
{

	public static String move;

	private int square[][];
	private Vector lMoveV;
	
	private chessboard mCb;
	
	private int mMaxThreads = 4;
	int mIUrgency;
	
	private gamehistory ghist;
	
	private boolean bUndoEnabled = true;
	
	private int iTurn = -1; // black or white

	private TtyuiActivity context;

	TtyUI(chessboard cb, TtyuiActivity context)
	{
		square = new int[8][8];
		
		mCb = cb;

		this.context = context;

		System.out.println("TTY UI CREATED FOR AskoChess");

		MyDragListener.ttyUI = this;
	}
	
	public void updateData(chessboard cb)
	{
		for (int i=0;i<8;i++)
			for (int j=0;j<8;j++)
				square[i][j] = cb.piecevalue(i,j);
		
		mCb = cb;	
	}
	
	void setMessage(final String s)
	{
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TtyuiActivity.setMessage(s, context);
			}
		});
//		System.out.println("MSG:"+s);
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
				inStr = null;

				while (inStr == null) {
					inStr = move;
					Thread.sleep(100);
				}

				TtyuiActivity.inputString = null;
				move = null;

				System.out.println("inStr: " + inStr);
			}
			catch (Exception ignored) {}
			
			inStr = inStr.toUpperCase();
			
			String inComp[] = inStr.split(":");
			
			if ((inComp[0].equals("PLAY")) && (inComp.length>2)) {
				int iLevel = Integer.valueOf(inComp[2]);

				if (inComp[1].equals("WHITE")) {
					sReturn = "PLAY:"+ play.PLAYER + ":" +iLevel+":0";
				} else if (inComp[1].equals("BLACK")) {
					sReturn = "PLAY:"+iLevel+":" + play.PLAYER +":0";
				}
				if (!sReturn.equals("")) {
					bReady = true;
				}
				
			}
			
			if (inStr.equals("EXIT")) {
				sReturn = inStr;
				bReady = true;
			}
			
			if (inStr.equals("UNDO")) {
				sReturn = "OHO";
				bReady = true;
			}
			
			if ((inStr.length() == 4) && !bReady) {
				int x1 = (int)inStr.charAt(0)-64;
				int y1 = (int)inStr.charAt(1)-48;
				int x2 = (int)inStr.charAt(2)-64;
				int y2 = (int)inStr.charAt(3)-48;
		
				if  ((x1 < 1) || (x2 < 1) || (y1 < 1) || ( y2 < 1) || (x1 > 8) || (x2 > 8) || (y1 > 8) || (y2 > 8)) {
					bReady = false;
				}
				
				sReturn = makeMove(inStr);
                bReady = sReturn.length() > 1;
				System.out.println("sReturn: " + sReturn);



			}
			
			if ((inStr.length() >= 2) && (inStr.substring(0,2).equalsIgnoreCase("T:")))
			{
				String sThr[]=inStr.split(":");
				int iNewThr = mMaxThreads;
				if (sThr.length>=2)
				{
					try
					{
						iNewThr = Integer.valueOf(sThr[1]);
					}
					catch (Exception ignored) {}
					
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
	
	void displayMsgDialog(final String msg)
	{
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TtyuiActivity.setMessage(msg, context);
			}
		});
//		System.out.println(msg);

	}
	
	private void dumpSquares()
	{
//		String tempString = "";
//
//		tempString += "  abcdefgh\n\n";
//		for (int j = 7;j >= 0;j--)
//		{
//			tempString += (j + 1) + " ";
//			for (int i = 0; i < 8; i++)
//			{
//				if (square[i][j]==-1) tempString += ".";
//
//				else if (square[i][j]>100)
//				{
//					switch(square[i][j]-100)
//					{
//						case piece.PAWN:
//							tempString += "P";
//							break;
//						case piece.BISHOP:
//							tempString += "B";
//							break;
//						case piece.KNIGHT:
//							tempString += "N";
//							break;
//						case piece.ROOK:
//							tempString += "R";
//							break;
//						case piece.KING:
//							tempString += "K";
//							break;
//						case piece.QUEEN:
//							tempString += "Q";
//							break;
//						default:
//							tempString += "X";
//							break;
//					}
//				}
//				else
//				{
//					switch(square[i][j])
//					{
//						case piece.PAWN:
//							tempString += "p";
//							break;
//						case piece.BISHOP:
//							tempString += "b";
//							break;
//						case piece.KNIGHT:
//							tempString += "n";
//							break;
//						case piece.ROOK:
//							tempString += "r";
//							break;
//						case piece.KING:
//							tempString += "k";
//							break;
//						case piece.QUEEN:
//							tempString += "q";
//							break;
//						default:
//							tempString += "x";
//							break;
//					}
//
//				}
//			}
//			tempString += " " + (j + 1);
//			tempString += "\n";
//		}
//		tempString += "\n  abcdefgh\n";
//		tempString += "\nLast move was: " + lastMoveString();
//
//		final String board = tempString;

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				TtyuiActivity.setBoard(getChessboardString(), context);
			}
		});

	}

	private String getChessboardString () {
	    String tempString = "";

        for (int j = 7;j >= 0;j--)
        {
            for (int i = 0; i < 8; i++)
            {
                if (square[i][j]==-1) tempString += ".";

                else if (square[i][j]>100)
                {
                    switch(square[i][j]-100)
                    {
                        case piece.PAWN:
                            tempString += "P";
                            break;
                        case piece.BISHOP:
                            tempString += "B";
                            break;
                        case piece.KNIGHT:
                            tempString += "N";
                            break;
                        case piece.ROOK:
                            tempString += "R";
                            break;
                        case piece.KING:
                            tempString += "K";
                            break;
                        case piece.QUEEN:
                            tempString += "Q";
                            break;
                        default:
                            tempString += "X";
                            break;
                    }
                }
                else
                {
                    switch(square[i][j])
                    {
                        case piece.PAWN:
                            tempString += "p";
                            break;
                        case piece.BISHOP:
                            tempString += "b";
                            break;
                        case piece.KNIGHT:
                            tempString += "n";
                            break;
                        case piece.ROOK:
                            tempString += "r";
                            break;
                        case piece.KING:
                            tempString += "k";
                            break;
                        case piece.QUEEN:
                            tempString += "q";
                            break;
                        default:
                            tempString += "x";
                            break;
                    }

                }
            }
            tempString += "\n";
        }

        return tempString;
    }
	
	private String lastMoveString()
	{
		if (lMoveV != null)
		{
			System.out.println("lastmoveString: " + lMoveV);
			if (lMoveV.size() == 5 || lMoveV.size() == 4)
			{
			
				int i1 = (int)lMoveV.elementAt(0);
				int j1 = (int)lMoveV.elementAt(1);
				int i2 = (int)lMoveV.elementAt(2);
				int j2 = (int)lMoveV.elementAt(3);

				System.out.println(i1 + j1 + i2 + j2 + "asd");
				return ""+(char)(96 + i1) + j1 + (char)(96 + i2) + j2;
		
			}
		}
	
		return "";
	
	}
	
	private String makeMove(String sMove)
	{
		boolean bFound = false;
		
		int x1 = (int)sMove.charAt(0)-64;
		int y1 = (int)sMove.charAt(1)-48;
		int x2 = (int)sMove.charAt(2)-64;
		int y2 = (int)sMove.charAt(3)-48;
		
		String spc = "";
		
		//System.out.println("DBG:makeMove:"+sMove + " " + x1 +","+y1+","+x2+","+y2);
		
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
	
	private int getPromotedPiece()
	{
		while(true)
		{
			String inStr = "";
			
			try
			{
				java.io.InputStreamReader isr = new java.io.InputStreamReader( System.in );
				java.io.BufferedReader stdin = new java.io.BufferedReader( isr );
				
				System.out.print("Promote to [QRBN]>");
				inStr = stdin.readLine();
			}
			catch (Exception e) {}

			inStr = inStr.toUpperCase();

			switch (inStr.charAt(1)) {
				case 'Q':
					return piece.QUEEN;
				case 'R':
					return piece.ROOK;
				case 'B':
					return piece.BISHOP;
				case 'N':
					return piece.KNIGHT;
				default:
					assert true;
			}
		}
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