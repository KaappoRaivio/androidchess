package kaappo.androidchess.askokaappochess;

import java.util.*;
import java.sql.*;
import java.io.PrintWriter;

import kaappo.androidchess.ChessActivity;
import kaappo.androidchess.ChessRunner;
import kaappo.androidchess.GetPromotedPieceDialog;
import kaappo.androidchess.MyDragListener;

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

	private ChessActivity context;

	public chessboard getmCb () {
		return mCb;
	}

	public int getiTurn () {
		return iTurn;
	}

	TtyUI(chessboard cb, ChessActivity context)
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
				ChessActivity.setMessage(s, context);
			}
		});
//		System.out.println("MSG:"+s);
	}
	
	void setTurn(int i) {
		iTurn = i;
		MyDragListener.setiTurn(i);
		context.setTurn(i);
	}
	
	void show()
	{
		dumpSquares();
	}



	String getMove() {
		String inStr = "";
		boolean bReady = false;
		String sReturn = "";
		
		while (!bReady) {

			try {
				inStr = null;

				while (inStr == null) {
					inStr = move;
					Thread.sleep(100);
				}

				ChessActivity.inputString = null;
				move = null;

				System.out.println("inStr: " + inStr);
			} catch (Exception ignored) {}
			
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
	
	void displayMsgDialog(final String msg) {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChessActivity.setMessage(msg, context);
			}
		});
	}
	
	private void dumpSquares() {

		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ChessActivity.setBoard(getChessboardString(), context, lMoveV);
			}
		});

	}

	private String getChessboardString () {
	    StringBuilder tempString = new StringBuilder();

        for (int j = 7;j >= 0;j--)
        {
            for (int i = 0; i < 8; i++)
            {
                if (square[i][j]==-1) tempString.append(".");

                else if (square[i][j]>100)
                {
                    switch(square[i][j]-100)
                    {
                        case piece.PAWN:
                            tempString.append("P");
                            break;
                        case piece.BISHOP:
                            tempString.append("B");
                            break;
                        case piece.KNIGHT:
                            tempString.append("N");
                            break;
                        case piece.ROOK:
                            tempString.append("R");
                            break;
                        case piece.KING:
                            tempString.append("K");
                            break;
                        case piece.QUEEN:
                            tempString.append("Q");
                            break;
                        default:
                            tempString.append("X");
                            break;
                    }
                }
                else
                {
                    switch(square[i][j])
                    {
                        case piece.PAWN:
                            tempString.append("p");
                            break;
                        case piece.BISHOP:
                            tempString.append("b");
                            break;
                        case piece.KNIGHT:
                            tempString.append("n");
                            break;
                        case piece.ROOK:
                            tempString.append("r");
                            break;
                        case piece.KING:
                            tempString.append("k");
                            break;
                        case piece.QUEEN:
                            tempString.append("q");
                            break;
                        default:
                            tempString.append("x");
                            break;
                    }

                }
            }
            tempString.append("\n");
        }

        return tempString.toString();
    }
	
	private String lastMoveString()
	{
		if (lMoveV != null)
		{

			if (lMoveV.size() == 5 || lMoveV.size() == 4)
			{
			
				int x1 = (int)lMoveV.elementAt(0);
				int y1 = (int)lMoveV.elementAt(1);
				int x2 = (int)lMoveV.elementAt(2);
				int y2 = (int)lMoveV.elementAt(3);

				System.out.println(x1 + y1 + x2 + y2 + "asd");
				return ""+(char)(96 + x1) + y1 + (char)(96 + x2) + y2;
		
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
		

		if  ((x1 < 1) || (x2 < 1) || (y1 < 1) || ( y2 < 1) || (x1 > 8) || (x2 > 8) || (y1 > 8) || (y2 > 8)) return "";
		
		piece p = mCb.blocks[x1][y1];
		
		if (p == null) return "";
		if (p.iColor != iTurn) return "";
		
		Vector mv = p.moveVector(mCb);
		String sReturnedMove = "";
		
		//System.out.println("DBG:domove @a");
		
		for (int i = 0; i < mv.size(); i++)
		{
			move m = (move) mv.elementAt(i);
			
			if ((m.xtar == x2) && (m.ytar == y2))
			{
				//System.out.println("YIKES!!! THERE'S A MOVE : " + x1 + "," + y1  + "  to: " + x2 + "," + y2);
				bFound = true;
				
				if ((p.iType == piece.PAWN) && (((p.iColor == piece.WHITE) && (m.ytar == 8)) || ((p.iColor == piece.BLACK) && (m.ytar == 1))))
				{
					// promotion code 
					int pq = getPromotedPiece();
					System.out.println("pq: " + pq);
					spc = "p"+pq;
				}
				
				sReturnedMove = "" + (char)(x1+64)+y1 + (char)(x2+64)+ y2 + spc;
			}
			
		}

		context.setTurn(iTurn == piece.WHITE ? piece.BLACK : piece.WHITE);
		return sReturnedMove;
		//VALIDATE THAT MOVE EXISTS and do promotion too!!!!.
		// copy chesswindow.domove()
	}
	
	private int getPromotedPiece() {
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				GetPromotedPieceDialog getPromotedPieceDialog = new GetPromotedPieceDialog(context, ChessActivity.getPlayerSide());
				getPromotedPieceDialog.show();
			}
		});

		//noinspection StatementWithEmptyBody
		while (GetPromotedPieceDialog._piece == -1) {}

		int _piece = GetPromotedPieceDialog._piece;
		GetPromotedPieceDialog._piece = -1;

		System.out.println("_piece: " + _piece);

		return _piece;

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