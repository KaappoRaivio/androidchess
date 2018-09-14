package kaappo.androidchess.askokaappochess;


import java.util.*;
import java.sql.*;
import java.io.PrintWriter;

public class chesswindow extends Frame implements MouseListener, ActionListener, WindowListener
{
	int xk1, yk1, xk2, yk2;
	
	int bx1, by1, bx2, by2;
	
	int iSquareSizeX;
	int iSquareSizeY;
	
	int square[][];
	Vector lMoveV;
	
	chessboard mCb;
	
	int iMoverX, iMoverY;
	
	String sBaseTitle = "AskoChess";
	String sMessage = "";
	
	int iTurn = -1; // black or white
	
	String sReturnedMove = "";
	
	
	MenuBar ch_mbar;
	Menu ch_playmenu,ch_aboutmenu;
	MenuItem ch_item_new,ch_item_stop,ch_item_undo,ch_item_about,ch_item_exit, ch_item_dump, ch_item_settings, ch_item_speedup, ch_item_slowdown, ch_item_flip, ch_item_urgency;
	
	gamehistory ghist = null;
	
	int mMaxThreads = 8;
	int mIUrgency;
	
	public static final int UI_TYPE_WINDOW = 1;
	public static final int UI_TYPE_TTY = 2;
	
	public static final boolean STOCKFISH_ENABLE = true;

	int iUIType;
	
	boolean bFlipped = false;
	
	static boolean bMonitorMode;
	static chesswindow last_opened_cw;
	static long lAnalStartTime;
	static long lAnalEndTime = -1;
	static int iAnalStartLevel;
	static int iAnalStartColor;
	
	static int iPrelRoundsTotal;
	static int iPrelRoundsDone;
	
	static String sAnalStartMval;
	
	static int iAnalRoundsTotal;
	static int iAnalRoundsDone;
	
	static String sAnalBestCurrent = "none";
	static String sAnalBestSeq = "none";
	static String sAnalCurrent = "none";
	static String sAnalLast = "none";
	
	static String sAnalBestPreCurrent = "none";
	static String sAnalBestPreSeq = "none";
	static String sAnalPreCurrent = "none";
	static String sAnalPreLast = "none";
	static long lLastMonRefresh;
	long[] lLat;
	
	chesswindow()
	{
		iUIType = UI_TYPE_WINDOW;
		last_opened_cw = this;
	}
	
	chesswindow(int x1, int y1, int x2, int y2, int margin, chessboard cb)
	{
		setBounds(x1,y1,x2,y2);
		
		xk1 = x1;
		xk2 = x2;
		yk1 = y1;
		yk2 = y2;
		
		bx1 = margin;
		by1 = (int)(margin*1.5)+50;
		bx2 = x2-margin;
		by2 = y2-margin;
		
		iSquareSizeX = (bx2-bx1) / 8;
		iSquareSizeY = (by2-by1) / 8;
		
		square = new int[9][9];
		
		mCb = cb;
		
		iMoverX = -1;
		iMoverY = -1;
		
		ch_mbar = new MenuBar();
		ch_playmenu = new Menu("Play");
		ch_aboutmenu = new Menu("About");
		
		ch_item_new = new MenuItem("New Game");
		ch_item_new.addActionListener(this);
		ch_item_stop = new MenuItem("Finish Game");
		ch_item_stop.addActionListener(this);
		ch_item_undo = new MenuItem("Undo Move");
		ch_item_undo.addActionListener(this);
		ch_item_speedup = new MenuItem("Speed up");
		ch_item_speedup.addActionListener(this);
		ch_item_slowdown = new MenuItem("Slow down");
		ch_item_slowdown.addActionListener(this);
		ch_item_dump = new MenuItem("Dump Game History");
		ch_item_dump.addActionListener(this);
		ch_item_settings = new MenuItem("Settings");
		ch_item_settings.addActionListener(this);
		ch_item_flip = new MenuItem("Flip board");
		ch_item_flip.addActionListener(this);
		ch_item_urgency = new MenuItem("Urgency");
		ch_item_urgency.addActionListener(this);
		
		ch_item_exit = new MenuItem("Exit");
		ch_item_exit.addActionListener(this);
		ch_item_about = new MenuItem("About AskoChess");
		ch_item_about.addActionListener(this);
		
		ch_playmenu.add(ch_item_new);
		ch_playmenu.add(ch_item_stop);
		ch_playmenu.add(ch_item_undo);
		
		//ch_playmenu.add(ch_item_speedup);
		//ch_playmenu.add(ch_item_slowdown);
		
		ch_playmenu.add(ch_item_dump);
		ch_playmenu.add(ch_item_settings);
		ch_playmenu.add(ch_item_flip);
		ch_playmenu.add(ch_item_urgency);
		ch_playmenu.add(ch_item_exit);
		
		
		ch_aboutmenu.add(ch_item_about);	
		
		ch_mbar.add(ch_playmenu);
		ch_mbar.add(ch_aboutmenu);
		
		setMenuBar(ch_mbar);
		
		addMouseListener(this);
		addWindowListener(this);
		
		setResizable(false);
		
		iUIType = UI_TYPE_WINDOW;
		last_opened_cw = this;
		
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void actionPerformed (ActionEvent ae)
	{
        //System.out.println("Ae!!" + ae.toString());
		if (ae.getActionCommand().equals("About AskoChess"))
		{
			//System.out.println("About dialog needed here!");
			String sCopyRight =  "v 0.98  2016-04-18\n(c) by Asko Huumonen";
			if (STOCKFISH_ENABLE) sCopyRight = sCopyRight + "\n\nStockfish (c)\nby Tord Romstad, Marco Costalba and Joona Kiiski";
				
			JOptionPane.showMessageDialog(this,
    sCopyRight,"AskoChess",JOptionPane.PLAIN_MESSAGE);
	
		}
		
		if (ae.getActionCommand().equals("Finish Game"))
		{
			//Custom button text
			Object[] options = {"Yes",
							"No"};
			int n = JOptionPane.showOptionDialog(this,
			"Finish current game now?",
			"AskoChess",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);
			
			if (n==0) 
			{			
				System.out.println("Finished.");
				setMessage("Game Finished. Start new one from menu Play->New Game");
				iTurn = -1;
				sReturnedMove = "EXIT";
				
			}
		}
		
		if (ae.getActionCommand().equals("Undo Move"))
		{
			//Custom button text
			Object[] options = {"Yes",
							"No"};
			int n = JOptionPane.showOptionDialog(this,
			"Undo last move?",
			"AskoChess",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);
			if (n == 0)
			{
				sReturnedMove = "OHO";
			}
		}
		
		if (ae.getActionCommand().equals("New Game"))
		{
			randomize();
			System.out.println("iTurn = " + iTurn);
			if (iTurn != -1)
			{
				JOptionPane.showMessageDialog(this,
    "You have to finish current game first. Use menu Game->Finish Game","AskoChess",JOptionPane.PLAIN_MESSAGE);
			}
			else
			{
				newgameDialog ngd = new newgameDialog(this);
				ngd.show();
				System.out.println("After NGD show: " + ngd.getRetValue());
				System.out.println("White Level " + ngd.getWhiteLevel());
				System.out.println("Black Level " + ngd.getBlackLevel());
				
				if (ngd.getWhiteLevel() == play.PLAYER) bFlipped = true;
				if (ngd.getBlackLevel() == play.PLAYER) bFlipped = false;
				
				if (CMonitor.bNoBlood()) bFlipped = !bFlipped;
				System.out.println("bFlipped:" + bFlipped);
				
				System.out.println("Algorithm " + ngd.getAlgorithm());
				if (ngd.getRetValue() == 2)
				{
					sReturnedMove = "PLAY:"+ngd.getWhiteLevel()+":"+ngd.getBlackLevel()+":"+ngd.getAlgorithm();
					System.out.println("DBG150222: sReturnedMove:" + sReturnedMove);
				}
				mIUrgency = 0;
			}
			
		}
		
		if (ae.getActionCommand().equals("Exit"))
		{
			Object[] options = {"Yes",
							"No"};
			int n = JOptionPane.showOptionDialog(this,
			"Exit AskoChess program now?",
			"AskoChess",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[1]);
			//System.out.println("N = " +n);
			if (n == 0) System.exit(0);
			
		}
		
		if (ae.getActionCommand().equals("Settings"))
		{
			//System.out.println("Settings selected!" + mMaxThreads);
			int i=0;
			switch (mMaxThreads)
			{
				case 4:
					i = 0;
					break;
				case 8:
					i = 1;
					break;
				case 16:
					i = 2;
					break;
				case 32:
					i = 3;
					break;
				case 64:
					i = 4;
					break;
				default:
					i = 0;
					break;
			}
			Object[] possibleValues = { "4", "8", "16", "32", "64" };
			Object selectedValue = JOptionPane.showInputDialog(null,
			"Set Thread Count", "Settings",
			JOptionPane.INFORMATION_MESSAGE, null,
			possibleValues, possibleValues[i]);
			//System.out.println("Selected value=" + selectedValue);
			if (selectedValue != null)
			{
				String sVal = ""+selectedValue;
				mMaxThreads = new Integer(sVal).intValue();
			}
		}
		
		if (ae.getActionCommand().equals("Flip board"))
		{
			bFlipped = !bFlipped;
			System.out.println("Flip selected. FLIP flag is now:" + bFlipped);
			repaint();
		}
		
		if (ae.getActionCommand().equals("Urgency"))
		{
			System.out.println("Urgency selected.");
			Object[] possibleValues = { "Normal" , "Hurry" , "Panic"};
			//int i = 0;
			Object selectedValue = JOptionPane.showInputDialog(null,
			"Set Urgency Level", "Urgency level",
			JOptionPane.INFORMATION_MESSAGE, null,
			possibleValues, possibleValues[mIUrgency]);
			if (selectedValue != null)
			{
				String sVal = ""+selectedValue;
				if (sVal.equals("Normal")) mIUrgency = 0;
				if (sVal.equals("Hurry")) mIUrgency = 1;
				if (sVal.equals("Panic")) mIUrgency = 2;
			}
		}
		
		if (ae.getActionCommand().equals("Dump Game History"))
		{
			//if (ghist != null) ghist.dump();
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
		
	}
	
	public void menuCanceled(MenuEvent me) {}
	public void menuDeselected(MenuEvent me) {}
	
	public void menuSelected(MenuEvent me)
	{
		System.out.println("Menu: " + me.toString());
	}
	
	public void mouseClicked (MouseEvent me)
	{
        int iMEX = me.getX();
		int iMEY = me.getY();
		
		int ix = (iMEX - bx1) /  iSquareSizeX + 1;
		int iy = 8 - ((iMEY - by1) /  iSquareSizeY);
		
		if (bFlipped)
		{
			ix = 9 - ix;
			iy = 9 - iy;
		}
		
		
		System.out.println("Clicked!  : (" + iMEX + "," + iMEY + ") -> (" + ix + "," + iy + ") ... " +iMoverX +"," + iMoverY);
		
		if  ((ix > 0) && (ix < 9) && (iy > 0) && (iy <9)) 
		{
			if ((iMoverX == -1) && (iMoverY == -1) ) displayMoves(ix,iy);
			else
			{
				System.out.println("DBG160202: trying to domove!");
				if (!doMove(ix,iy))
				{
					System.out.println("DBG160202: Failed domove");
					//iMoverX = -1;
					//iMoverY = -1;
				}
			}
		}
		else System.out.println("Invalid.");
	}
	
	public void windowClosing(WindowEvent e) 
	{
        //dispose();
        //System.exit(0);
	}

	public void windowOpened(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	
	public void paint (Graphics g)
	{
		//System.out.println("Paint called " + iMoverX + "," + iMoverY);	
		setTitle (sBaseTitle + " " + sMessage);
		
		Rectangle rect = getBounds();
		if (bMonitorMode)
		{
			if (rect.width == 500) setBounds(rect.x, rect.y, 700, rect.height);
			
			String sCol = "";
			if (iAnalStartColor == piece.WHITE) sCol = "WHITE";
			else sCol = "BLACK";
			g.drawString("ANALYZING A " + sCol + " MOVE.",510,88);
			
			String sStart = ""+new Timestamp(lAnalStartTime);
			sStart = sStart.substring(10,19);
			g.drawString("ANALYSIS STARTED: " + sStart,510,100);
			
			long lNow = System.currentTimeMillis();
			
			String sTimeString;
			if (lAnalEndTime == -1) sTimeString = sFormatTime(lNow-lAnalStartTime);
			else sTimeString = sFormatTime(lAnalEndTime-lAnalStartTime);
			
			g.drawString("TIME CONSUMED: " + sTimeString,510,112);
			
			g.drawString("ANALYSIS LEVEL: " + iAnalStartLevel,510,124);
			g.drawString("STARTING AT: " + sAnalStartMval,510,136);
			
			g.drawString("LEVEL " + (iAnalStartLevel-1) + " PRE-ANALYSIS:",510,160);
			g.drawString(iPrelRoundsDone+"/" + iPrelRoundsTotal+" MOVES DONE.",510,172);
			g.drawString("BEST: " + sAnalBestPreCurrent ,510,184);
			g.drawString("LAST: " + sAnalPreLast,510,196);
			
			g.drawString("LEVEL " + iAnalStartLevel+ " ANALYSIS:",510,220);
			g.drawString(iAnalRoundsDone + "/" + iAnalRoundsTotal+ " MOVES DONE.",510,232);
			
			g.drawString("BEST: " +  sAnalBestCurrent,510,244);
			g.drawString("LAST: " + sAnalLast,510,256);
			g.drawString("IN PROGRESS: " + sAnalCurrent,510,268);
			
			if (lAnalEndTime != -1) g.drawString("COMPLETED.",510,292);
			
			g.drawString("BEST SCENARIO:",510,316);
			if (sAnalBestSeq.length() < 20)	g.drawString(sAnalBestSeq,510,328);
			else 
			{
				String sAna1 = sAnalBestSeq.substring(0,19);
				String sAna2 = sAnalBestSeq.substring(20);
				g.drawString(sAna1,510,328);
				g.drawString(sAna2,510,340);
			}
			
				
		}
		else
		{
			if (rect.width == 800) setBounds(rect.x, rect.y, 500, rect.height);
		}
		
		g.drawLine(bx1,by1,bx2,by1);
		g.drawLine(bx1,by1,bx1,by2);
		g.drawLine(bx2,by1,bx2,by2);
		g.drawLine(bx1,by2,bx2,by2);
		
		
		for (int i = 1; i <=8; i++)
			for ( int j = 1; j <= 8 ; j++)
			{
				if (((i % 2) == (j%2)))
				{
					g.setColor(Color.BLACK);
					g.fillRect(bx1-iSquareSizeX+(i*iSquareSizeX),by2-(j*iSquareSizeY),iSquareSizeX,iSquareSizeY);
				}
			}
		
		g.setColor(Color.BLACK);
		if (!bFlipped)
		{
			for (int i=1; i<=8;i++)
			{
				g.drawString((char)(i+64)+"",bx1-(int)(iSquareSizeX*0.7)+(i*iSquareSizeX),by2+(int)(iSquareSizeY*0.5));
				g.drawString((char)(i+64)+"",bx1-(int)(iSquareSizeX*0.7)+(i*iSquareSizeX),by1-(int)(iSquareSizeY*0.4));
				g.drawString(i+"",bx1-(int)(iSquareSizeX*0.4),by2-((i-1)*iSquareSizeY)-(int)(iSquareSizeY*0.4));
				g.drawString(i+"",bx2+(int)(iSquareSizeX*0.4),by2-((i-1)*iSquareSizeY)-(int)(iSquareSizeY*0.4));
			}
		} 
		else
		{
			for (int i=1; i<=8;i++)
			{
				g.drawString((char)(9-i+64)+"",bx1-(int)(iSquareSizeX*0.7)+(i*iSquareSizeX),by2+(int)(iSquareSizeY*0.5));
				g.drawString((char)(9-i+64)+"",bx1-(int)(iSquareSizeX*0.7)+(i*iSquareSizeX),by1-(int)(iSquareSizeY*0.4));
				g.drawString(9-i+"",bx1-(int)(iSquareSizeX*0.4),by2-((i-1)*iSquareSizeY)-(int)(iSquareSizeY*0.4));
				g.drawString(9-i+"",bx2+(int)(iSquareSizeX*0.4),by2-((i-1)*iSquareSizeY)-(int)(iSquareSizeY*0.4));
			}
		}
		
		if (lLat != null)
		{
			g.drawString("WHITE: "+sFormatTime(lLat[0]),30,70);	
			g.drawString("BLACK: "+sFormatTime(lLat[1]),250,70);	
		}
		
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
			{
				int v = square[i][j];
				int iColor;
				
				if (v > 100)
				{				
					iColor = 1;
					v = v-100;
				}						
				else iColor = 0;
				
				int ii, jj;
				if (!bFlipped)
				{
					ii=i;
					jj=j;
				}
				else
				{
					ii=9-i;
					jj=9-j;
				}
					
				
				switch (v)
				{
					case -1:
						break;
					
					case piece.PAWN:
						drawPawn(g,ii,jj,iColor);
						break;
						
					case piece.KING:
						drawKing(g,ii,jj,iColor);
						break;
					
					case piece.QUEEN:
						drawQueen(g,ii,jj,iColor);
						break;
						
					case piece.BISHOP:
						drawBishop(g,ii,jj,iColor);
						break;
						
					case piece.KNIGHT:
						drawKnight(g,ii,jj,iColor);
						break;

					case piece.ROOK:
						drawRook(g,ii,jj,iColor);
						break;	
						
					default:
						System.out.println(" wrong v!" + v);
						break;
						
				}
			}
		
		
		
		
		
		Vector v = lMoveV; 
		if (v != null) 
		{
			if ((v.size() == 5) || (v.size() == 4))
			{
			
				int i1 = (int)v.elementAt(0);
				int j1 = (int)v.elementAt(1);
				int i2 = (int)v.elementAt(2);
				int j2 = (int)v.elementAt(3);
				//int iCapt = (int)v.elementAt(4);
				
				if (bFlipped)
				{
					i1 = 9-i1;
					j1 = 9-j1;
					i2 = 9-i2;
					j2 = 9-j2;
				}
				
				drawBox(g,Color.RED,2,bx1-iSquareSizeX+(i1*iSquareSizeX),by2-(j1*iSquareSizeY),iSquareSizeX,iSquareSizeY);
				drawBox(g,Color.RED,2,bx1-iSquareSizeX+(i2*iSquareSizeX),by2-(j2*iSquareSizeY),iSquareSizeX,iSquareSizeY);
			}
		}
			
		//System.out.println("Mover coords : " + iMoverX + "," + iMoverY);	
		if ((iMoverX != -1) && (iMoverY != -1))
		{
			//System.out.println("Green Box");
			if (!bFlipped) drawBox(g,Color.GREEN,2,bx1-iSquareSizeX+(iMoverX*iSquareSizeX),by2-(iMoverY*iSquareSizeY),iSquareSizeX,iSquareSizeY);
			else drawBox(g,Color.GREEN,2,bx1-iSquareSizeX+((9-iMoverX)*iSquareSizeX),by2-((9-iMoverY)*iSquareSizeY),iSquareSizeX,iSquareSizeY);
			drawMoveListBoxes(g,iMoverX,iMoverY);
		}
		
	}

	public void updateData(chessboard cb)
	{
		for (int i=1;i<=8;i++) 
			for (int j=1;j<=8;j++)
				square[i][j] = cb.piecevalue(i,j);
		
		mCb = cb;	
	}
	
	public void setLastMoveVector (Vector v)
	{
		lMoveV = v;
	}
	
	void drawQueen (Graphics g, int i, int j, int iColor)
	{
		int iX = bx1-iSquareSizeX+(i*iSquareSizeX) + (int ) (0.1 * iSquareSizeX);
		int iY = by2-((j-1)*iSquareSizeY) - (int) (0.1 * iSquareSizeY);
		
		int qSize = (int) (iSquareSizeX * 0.8);
		
		// white = 0, black = 1
		
		Polygon p = new Polygon();
		p.addPoint(iX,iY);
		p.addPoint(iX+qSize,iY);
		p.addPoint(iX+qSize,iY-qSize);
		p.addPoint(iX+(int)(qSize*0.83),(int)(iY-qSize*0.5));
		p.addPoint(iX+(int)(qSize*0.67),(int)(iY-qSize));
		p.addPoint(iX+(int)(qSize*0.5),(int)(iY-qSize*0.5));
		p.addPoint(iX+(int)(qSize*0.33),(int)(iY-qSize));
		p.addPoint(iX+(int)(qSize*0.17),(int)(iY-qSize*0.5));
		p.addPoint(iX,iY-qSize);
		
		drawPiece(g,p,i,j,iColor);
		
	}
	
	void drawRook (Graphics g, int i, int j, int iColor)
	{
		int iX = bx1-iSquareSizeX+(i*iSquareSizeX) + (int ) (0.1 * iSquareSizeX);
		int iY = by2-((j-1)*iSquareSizeY) - (int) (0.1 * iSquareSizeY);
		
		int qSize = (int) (iSquareSizeX * 0.8);
		
		// white = 0, black = 1
		
		Polygon p = new Polygon();
		p.addPoint(iX,iY);
		p.addPoint(iX+qSize,iY);
		p.addPoint(iX+qSize,iY-(int)(qSize*0.2));
		p.addPoint(iX+(int)(qSize*0.7),iY-(int)(qSize*0.2));
		p.addPoint(iX+(int)(qSize*0.65),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.85),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.85),iY-(int)(qSize*0.80));
		p.addPoint(iX+(int)(qSize*0.71),iY-(int)(qSize*0.80));
		p.addPoint(iX+(int)(qSize*0.71),iY-(int)(qSize*0.70));
		p.addPoint(iX+(int)(qSize*0.57),iY-(int)(qSize*0.70));
		p.addPoint(iX+(int)(qSize*0.57),iY-(int)(qSize*0.80));
		p.addPoint(iX+(int)(qSize*0.43),iY-(int)(qSize*0.80));
		p.addPoint(iX+(int)(qSize*0.43),iY-(int)(qSize*0.70));
		p.addPoint(iX+(int)(qSize*0.29),iY-(int)(qSize*0.70));
		p.addPoint(iX+(int)(qSize*0.29),iY-(int)(qSize*0.80));
		p.addPoint(iX+(int)(qSize*0.15),iY-(int)(qSize*0.80));
		p.addPoint(iX+(int)(qSize*0.15),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.35),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.3),iY-(int)(qSize*0.2));
		p.addPoint(iX,iY-(int)(qSize*0.2));
		
		drawPiece(g,p,i,j,iColor);
		
	}
	
	void drawPawn (Graphics g, int i, int j, int iColor)
	{
		int iX = bx1-iSquareSizeX+(i*iSquareSizeX) + (int ) (0.1 * iSquareSizeX);
		int iY = by2-((j-1)*iSquareSizeY) - (int) (0.1 * iSquareSizeY);
		
		int qSize = (int) (iSquareSizeX * 0.8);
		
		// white = 0, black = 1
		
		Polygon p = new Polygon();
		p.addPoint(iX+(int)(qSize*0.2),iY);
		p.addPoint(iX+(int)(qSize*0.8),iY);
		p.addPoint(iX+(int)(qSize*0.6),iY-(int)(qSize*0.3));
		p.addPoint(iX+(int)(qSize*0.7),iY-(int)(qSize*0.4));
		p.addPoint(iX+(int)(qSize*0.5),iY-(int)(qSize*0.6));
		p.addPoint(iX+(int)(qSize*0.3),iY-(int)(qSize*0.4));
		p.addPoint(iX+(int)(qSize*0.4),iY-(int)(qSize*0.3));
		
		drawPiece(g,p,i,j,iColor);
		
	}

	void drawBishop (Graphics g, int i, int j, int iColor)
	{
		int iX = bx1-iSquareSizeX+(i*iSquareSizeX) + (int ) (0.1 * iSquareSizeX);
		int iY = by2-((j-1)*iSquareSizeY) - (int) (0.1 * iSquareSizeY);
		
		int qSize = (int) (iSquareSizeX * 0.8);
		
		// white = 0, black = 1
		
		Polygon p = new Polygon();
		p.addPoint(iX+(int)(qSize*0.15),iY);
		p.addPoint(iX+(int)(qSize*0.85),iY);
		p.addPoint(iX+(int)(qSize*0.6),iY-(int)(qSize*0.3));
		p.addPoint(iX+(int)(qSize*0.7),iY-(int)(qSize*0.6));
		p.addPoint(iX+(int)(qSize*0.5),iY-(int)(qSize*0.8));
		
		p.addPoint(iX+(int)(qSize*0.43),iY-(int)(qSize*0.73));
		p.addPoint(iX+(int)(qSize*0.48),iY-(int)(qSize*0.68));
		p.addPoint(iX+(int)(qSize*0.42),iY-(int)(qSize*0.62));
		p.addPoint(iX+(int)(qSize*0.37),iY-(int)(qSize*0.67));
		
		p.addPoint(iX+(int)(qSize*0.3),iY-(int)(qSize*0.6));
		p.addPoint(iX+(int)(qSize*0.4),iY-(int)(qSize*0.3));
		
		drawPiece(g,p,i,j,iColor);
		
	}	

	void drawKing (Graphics g, int i, int j, int iColor)
	{
		int iX = bx1-iSquareSizeX+(i*iSquareSizeX) + (int ) (0.1 * iSquareSizeX);
		int iY = by2-((j-1)*iSquareSizeY) - (int) (0.1 * iSquareSizeY);
		
		int qSize = (int) (iSquareSizeX * 0.8);
		
		// white = 0, black = 1
		
		Polygon p = new Polygon();
		p.addPoint(iX+(int)(qSize*0.1),iY);
		p.addPoint(iX+(int)(qSize*0.9),iY);
		p.addPoint(iX+qSize,iY-(int)(qSize*0.3));
		p.addPoint(iX+(int)(qSize*0.95),iY-(int)(qSize*0.5));
		p.addPoint(iX+(int)(qSize*0.75),iY-(int)(qSize*0.5));
		p.addPoint(iX+(int)(qSize*0.70),iY-(int)(qSize*0.4));
		p.addPoint(iX+(int)(qSize*0.60),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.52),iY-(int)(qSize*0.58));
		p.addPoint(iX+(int)(qSize*0.52),iY-(int)(qSize*0.78));
		p.addPoint(iX+(int)(qSize*0.62),iY-(int)(qSize*0.78));
		p.addPoint(iX+(int)(qSize*0.62),iY-(int)(qSize*0.82));
		p.addPoint(iX+(int)(qSize*0.52),iY-(int)(qSize*0.82));
		p.addPoint(iX+(int)(qSize*0.52),iY-(int)(qSize*0.86));
		p.addPoint(iX+(int)(qSize*0.48),iY-(int)(qSize*0.86));
		p.addPoint(iX+(int)(qSize*0.48),iY-(int)(qSize*0.82));
		p.addPoint(iX+(int)(qSize*0.38),iY-(int)(qSize*0.82));
		p.addPoint(iX+(int)(qSize*0.38),iY-(int)(qSize*0.78));
		p.addPoint(iX+(int)(qSize*0.48),iY-(int)(qSize*0.78));
		p.addPoint(iX+(int)(qSize*0.48),iY-(int)(qSize*0.58));
		p.addPoint(iX+(int)(qSize*0.40),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.30),iY-(int)(qSize*0.4));
		p.addPoint(iX+(int)(qSize*0.25),iY-(int)(qSize*0.5));
		p.addPoint(iX+(int)(qSize*0.05),iY-(int)(qSize*0.5));
		p.addPoint(iX,iY-(int)(qSize*0.3));
		
		drawPiece(g,p,i,j,iColor);
		
	}
	
	void drawKnight (Graphics g, int i, int j, int iColor)
	{
		int iX = bx1-iSquareSizeX+(i*iSquareSizeX) + (int ) (0.1 * iSquareSizeX);
		int iY = by2-((j-1)*iSquareSizeY) - (int) (0.1 * iSquareSizeY);
		
		int qSize = (int) (iSquareSizeX * 0.8);
		
		// white = 0, black = 1
		
		Polygon p = new Polygon();
		
		p.addPoint(iX+(int)(qSize*0.15),iY);
		p.addPoint(iX+(int)(qSize*0.85),iY);
		p.addPoint(iX+(int)(qSize*0.80),iY-(int)(qSize*0.20));
		p.addPoint(iX+(int)(qSize*0.65),iY-(int)(qSize*0.30));
		p.addPoint(iX+(int)(qSize*0.60),iY-(int)(qSize*0.50));
		p.addPoint(iX+(int)(qSize*0.65),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.75),iY-(int)(qSize*0.45));
		p.addPoint(iX+(int)(qSize*0.85),iY-(int)(qSize*0.55));
		p.addPoint(iX+(int)(qSize*0.50),iY-(int)(qSize*0.95));
		p.addPoint(iX+(int)(qSize*0.30),iY-(int)(qSize*0.75));
		
		p.addPoint(iX+(int)(qSize*0.35),iY-(int)(qSize*0.30));
		p.addPoint(iX+(int)(qSize*0.20),iY-(int)(qSize*0.20));
		
		drawPiece(g,p,i,j,iColor);
		
	}
	
	void drawPiece(Graphics g, Polygon p, int i, int j, int iColor)
	{
		if (((i % 2) == (j%2)))
		{
			// black square!!!!
			if (iColor == 0) 
			{
				g.setColor(Color.WHITE);
				g.fillPolygon(p);
			}
			else 
			{
				g.setColor(Color.WHITE);
				g.drawPolygon(p);
			}
		}
		else
		{
			if (iColor == 0) 
			{
				g.setColor(Color.BLACK);
				g.drawPolygon(p);
			}
			else
			{
				g.setColor(Color.BLACK);
				g.fillPolygon(p);
			}
		}
	}
	
	public void drawBox(Graphics g, Color c, int iPen, int x, int y, int xs, int ys)
	{
	
		g.setColor (c);
		
		g.fillRect(x-iPen,y-iPen,xs+iPen,iPen*2);
		g.fillRect(x-iPen,y+ys-iPen,xs+iPen,iPen*2);
		g.fillRect(x-iPen,y-iPen,iPen*2,ys+iPen*2);
		g.fillRect(x+xs-iPen,y-iPen,iPen*2,ys+iPen*2);
	
	/*
	g.drawRect(bx1-iSquareSizeX+(i1*iSquareSizeX),by2-(j1*iSquareSizeY),iSquareSizeX,iSquareSizeY);
		g.drawRect(bx1-iSquareSizeX+(i2*iSquareSizeX),by2-(j2*iSquareSizeY),iSquar
		eSizeX,iSquareSizeY);
		*/
	}
	
	void displayMoves(int ix, int iy)
	{
		System.out.println("DBG160202:displayMoves:" + ix +"," + iy);
		if (mCb == null) System.out.println("mCb = null");
		else 
		{
			System.out.println("Not null mCB!");
			mCb.dump();
		}
		piece p = mCb.blocks[ix][iy];
		if (p != null)
		{
			if (p.iColor == iTurn)
			{
				System.out.println("Piece at " + ix + "," + iy + " has moves :" );
				
				iMoverX = ix;
				iMoverY = iy;
				
				Vector mv = p.moveVector(mCb);
				for (int i = 0; i < mv.size(); i++)
				{
					move m = (move)mv.elementAt(i);
					System.out.println("M: " + m.xtar + "," + m.ytar);
				}
				System.out.println("Repainting...");
				repaint();
			}
		}
		else
		{
			System.out.println("Null piece at " + ix + "," + iy);
			iMoverX = -1;
			iMoverY = -1;
		}
	}
	
	void drawMoveListBoxes(Graphics g,int ix,int iy)
	{
		piece p = mCb.blocks[ix][iy];
		if (p == null) return;
		
		Vector mv = p.moveVector(mCb);
		for (int i = 0; i < mv.size(); i++)
		{
			move m = (move)mv.elementAt(i);
			if (!bFlipped) 	drawBox(g,Color.GREEN,1,bx1-iSquareSizeX+(m.xtar*iSquareSizeX),by2-(m.ytar*iSquareSizeY),iSquareSizeX,iSquareSizeY);
			else drawBox(g,Color.GREEN,1,bx1-iSquareSizeX+((9-m.xtar)*iSquareSizeX),by2-((9-m.ytar)*iSquareSizeY),iSquareSizeX,iSquareSizeY);
			
		}
		
	}
	
	void setMessage(String s)
	{
		sMessage = s;
	}
	
	void setTurn( int i)
	{
		iTurn = i;
	}
	
	boolean doMove(int ix, int iy)
	{
		boolean bFound = false;
		String spc = "";
		
		piece p = mCb.blocks[iMoverX][iMoverY];
		if (p == null) return false;
		
		Vector mv = p.moveVector(mCb);
		
		for (int i = 0; i < mv.size(); i++)
		{
			move m = (move)mv.elementAt(i);
			
			if ((m.xtar == ix) && (m.ytar == iy))
			{
				System.out.println("YIKES!!! THERE'S A MOVE : " + iMoverX + "," + iMoverY + "  to: " + ix + "," + iy);
				bFound = true;
				
				if ((p.iType == piece.PAWN) && (((p.iColor == piece.WHITE) && (m.ytar == 8)) || ((p.iColor == piece.BLACK) && (m.ytar == 1))))
				{
					// promotion code 
					int pq = getPromotedPiece();
					spc = "p"+pq;
				}
				
				sReturnedMove = "" + (char)(iMoverX+64)+iMoverY + (char)(ix+64)+ iy + spc;
				i=mv.size()+1;
			}
			
		}
		
		if (!bFound)
		{
			System.out.println("DBG160202:inside doMove("+ix+","+iy+")");
			
			piece p2 = mCb.blocks[ix][iy];
			
			if (p2 != null)
			{
				System.out.println("DBG160202:inside doMove("+ix+","+iy+"): not null p2 found.");
				if (p2.iColor == p.iColor) 
				{
					System.out.println("DBG160202:inside doMove("+ix+","+iy+"): not null p2 found. resetting imoverx & imovery");
					iMoverX = ix;
					iMoverY = iy;
					System.out.println("DBG160202: iMoverX:" + iMoverX + " iMoverY:" + iMoverY);
					displayMoves(iMoverX,iMoverY);
				}
			}
		}
		
		return bFound;
	}
	
	String getMove()
	{
		sReturnedMove = "";
		
		while (sReturnedMove.length() == 0)
		{
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
		
		iMoverX = -1;
		iMoverY = -1;
		
		System.out.println("DBG151025:chesswindow.getMove():" + sReturnedMove);
		return sReturnedMove;
	}
	
	void displayMsgDialog(String sMessage)
	{
		JOptionPane.showMessageDialog(this,sMessage,"AskoChess",JOptionPane.PLAIN_MESSAGE);
	}
	
	void setgamehistory (gamehistory gh)
	{
		ghist = gh;
	}
	
	void randomize()
	{
		long l = System.currentTimeMillis() / 1000;
		int lc = (int)(l % 60);
		for (int i = 0; i< lc; i++) 
		{
			//System.out.println("RANDOMIZE IN LOOP!");
			float f = (float)Math.random();
		}
		
	}
	
	int getPromotedPiece()
	{
		Object[] options = {"Queen",
                    "Rook",
                    "Bishop",
					"Knight"};
		int n = JOptionPane.showOptionDialog(this,
		"Pawn promoted at the end row. Choose the piece you want.",
		"Pawn promoted!",
		JOptionPane.YES_NO_CANCEL_OPTION,
		JOptionPane.QUESTION_MESSAGE,
		null,
		options,
		options[0]);
		
		System.out.println("Dialog returned: " + n);
		
		switch (n)
		{
			case 0:
				return piece.QUEEN;
			case 1:
				return piece.ROOK;
			case 2: 
				return piece.BISHOP;
			case 3:
				return piece.KNIGHT;
		}
		System.out.println ("Malfunction in chesswindow.getPromotedPiece()");
		return -1;
	}
	
	static void setMonitorMode (boolean bMode)
	{
		bMonitorMode = bMode;
		//System.out.println("chesswindow.setMonitorMode() called.");
		last_opened_cw.repaint();
	}
	
	static void setAnalysisStart(int iLevel, int iColor)
	{
		iAnalStartLevel = iLevel;
		iAnalStartColor = iColor;
		lAnalStartTime = System.currentTimeMillis();
	}
	
	static void setMonitorStartLevel(int iLevel, int iColor)
	{
		lAnalStartTime = System.currentTimeMillis();
		iAnalStartLevel = iLevel;
		iAnalStartColor = iColor;
	}
	
	static int getiAnalStartLevel ()
	{
		return iAnalStartLevel;
	}
	
	static void setiAnalRoundsTotal(int iRounds)
	{
		iAnalRoundsTotal = iRounds;
	}
	
	static int getiAnalStartColor ()
	{
		return iAnalStartColor;
	}
	
	static void setiPrelRoundsTotal(int iTot)
	{
		iPrelRoundsTotal = iTot;
	}
	
	static int getiPrelRoundsTotal()
	{
		return iPrelRoundsTotal;
	}
	
	
	static void setiAnalRoundsDone(int iDone)
	{
		iAnalRoundsDone = iDone;
	}
	
	static void setiPrelRoundsDone(int iDone)
	{
		iPrelRoundsDone = iDone;
	}
	
	static void setlAnalEndTime(long lTime)
	{
		lAnalEndTime = lTime;
	}
	
	static void monRefresh(boolean bForce)
	{
		if (bMonitorMode && ((System.currentTimeMillis() > lLastMonRefresh + 1000) || bForce))
		{
			last_opened_cw.repaint();
			lLastMonRefresh = System.currentTimeMillis();
		}
	}
	
	static void setBestMval(movevalue m)
	{
		if (m.sRoute.length() > 5) sAnalBestCurrent = new String(m.sRoute.substring(1,5));
		sAnalBestSeq = new String(m.sRoute);
	}
	
	static void setAnalCurrent(String s)
	{
		String sc = new String(s);
		sAnalLast = sAnalCurrent;
		sAnalCurrent = sc;
	}
	
	static void setBestPreMval(movevalue m)
	{
		if (m.sRoute.length() > 5) sAnalBestPreCurrent = new String(m.sRoute.substring(1,5));
		sAnalBestPreSeq = new String(m.sRoute);
	}
	
	static void setAnalPreCurrent (String s)
	{
		String sc = new String(s);
		sAnalPreLast = sAnalPreCurrent;
		sAnalPreCurrent = sc;
	}
	
	static void setAnalysisStartMval(movevalue mv)
	{
		int iCorrBal = 0;
		if (iAnalStartColor == piece.WHITE) iCorrBal = mv.iPieceBalCorrWhite;
		else iCorrBal = mv.iPieceBalCorrBlack;
		
		if ((mv.iPieceBalance == 0) && (iCorrBal == 0)) 
		{
			sAnalStartMval = "EQUAL";
			return;
		}
		if ((mv.iPieceBalance >= 0) && (iCorrBal >= 0)) 
		{
			sAnalStartMval = "WHITE " + mv.iPieceBalance + "("+iCorrBal+")";
			return;
		}
		if ((mv.iPieceBalance <= 0) && (iCorrBal <= 0)) 
		{
			sAnalStartMval = "BLACK " + -mv.iPieceBalance + "("+-iCorrBal+")";
			return;
		}
	}
	
	String sFormatTime(long msec)
	{
		String sTime = "";
		int hr = (int) (msec / 3600000);
		int min = (int)((msec - hr * 3600000)/60000);
		int sec = (int) ((msec - hr * 3600000 - min * 60000 ) / 1000);
		
		if (hr > 0) sTime = hr+":";
		
		if (min>9) sTime = sTime + min +":";
		else sTime = sTime + "0" + min + ":";
		
		if (sec >9) sTime = sTime + sec;
		else sTime = sTime + "0" + sec;
		
		//System.out.println("sFormatTime" + sTime + " msec:" + msec);
		//System.out.println("hr:" + hr + " min:" + min + " sec:" + sec);
		//System.exit(0);
		return sTime;
	}
	
	void setLatencies(long[] lLatency)
	{
		lLat = lLatency;
	}

}

class newgameDialog extends Dialog implements ActionListener
{
	int iRetValue = -1;
	
	JComboBox whiteList = null;
	JComboBox blackList = null;
	JComboBox algorithmList = null;
	//JPanel panel = null;
	
	int iWhiteLev = 0;
	int iBlackLev = 4;
	int iAlgLev = 0;
	
	String[] AskoChessLevels = { "Player" , "AskoChess Zero" , "AskoChess Level 0", "AskoChess Level 1", "AskoChess Level 2", "AskoChess Level 3", "AskoChess Level 4", "AskoChess 10 sec", "AskoChess 1 min","AskoChess 2 min", "Deep Depression 5 min"};
		
	String[] StockFishLevels = { "Player" , "Stockfish 1 (1k)" , "Stockfish 2 (3k)", "Stockfish 3 (10k)", "Stockfish 4 (30k)", "Stockfish 5 (100k)", "Stockfish 6 (300k)", "Stockfish 7 (1M)", "Stockfish 8 (3M)", "Stockfish 9 (10M)","Stockfish 10 (30M)" };
	
	boolean bActGate = false;
	
	public newgameDialog (Frame parent)
	{
		super (parent, true);
		
		setLayout(new GridLayout(4, 2));
		
		
		
		/*String[] whiteLevels = { "Player" , "AskoChess Zero" , "AskoChess Level 0", "AskoChess Level 1", "AskoChess Level 2", "AskoChess Level 3", "AskoChess Level 4", "AskoChess 10 sec", "AskoChess 45 sec", "Deep Depression 3 min", "AskoChess No Limit" };

		String[] blackLevels = { "Player" , "AskoChess Zero", "AskoChess Level 0", "AskoChess Level 1", "AskoChess Level 2", "AskoChess Level 3", "AskoChess Level 4", "AskoChess 10 sec", "AskoChess 45 sec", "Deep Depression 3 min", "AskoChess No Limit" };
		*/
		String [] whiteLevels = AskoChessLevels;
		String [] blackLevels = AskoChessLevels;
		
		String[] algorithms1 = {"AskoChess","Stockfish"};
		String[] algorithms2 = {"AskoChess"};
		String[] algorithms;
		if (chesswindow.STOCKFISH_ENABLE) algorithms = algorithms1;
		else algorithms = algorithms2;
		
		whiteList = new JComboBox(whiteLevels);
		blackList = new JComboBox(blackLevels);
		whiteList.setSelectedIndex(iWhiteLev);
		blackList.setSelectedIndex(iBlackLev);
		algorithmList = new JComboBox(algorithms);
		
		Label whitelab = new Label("White level", Label.LEFT);
		Label blacklab = new Label("Black level", Label.LEFT);
		Label alglab = new Label("Algorithm", Label.LEFT);
		
		Button startbutton = new Button("Start new game");
		Button cancelbutton = new Button("Cancel");
		
		add(whitelab);
		add(whiteList);
		add(blacklab);
		add(blackList);
		add(alglab);
		add(algorithmList);
		add(startbutton);
		add(cancelbutton);
		
		setTitle("AskoChess - Start new game");
		setSize(new Dimension(400,200));
		setLocation(200,200);	
		setResizable(false);
		
		whiteList.addActionListener(this);
		blackList.addActionListener(this);
		//algorithmList.addActionListener(this);
		AlgComboHandler ach = new AlgComboHandler(this);
		algorithmList.addActionListener(ach);
		
		
		startbutton.addActionListener(this);
		cancelbutton.addActionListener(this);
	}
	
	public void actionPerformed (ActionEvent ae)
	{
		//System.out.println("Action event:" + ae.toString());
		if (bActGate) return;
		else bActGate = true;	
			
		System.out.println(ae.getActionCommand());
		
		if (ae.getActionCommand().equals("Cancel")) iRetValue = 1;
		if (ae.getActionCommand().equals("Start new game")) iRetValue = 2;
		
		if (ae.getActionCommand().equals("comboBoxChanged"))
		{	
			System.out.println("Combo box change ....");
			//System.out.println(ae.toString());
			//System.out.println(ae.paramString());
			
			int iNewWhite = whiteList.getSelectedIndex() ;
			int iNewBlack = blackList.getSelectedIndex() ;
			int iNewAlg = algorithmList.getSelectedIndex();
			
			if ((iNewWhite == 0) && (iBlackLev == 0))
			{
				iWhiteLev = iNewWhite;
				iBlackLev = 4;
			} 
			else if ((iNewBlack == 0) && (iWhiteLev == 0))
			{
				iBlackLev = iNewBlack;
				iWhiteLev = 4;
			}
			else if ((iNewWhite > 0) && (iBlackLev > 0))
			{
				iWhiteLev = iNewWhite;
				iBlackLev = 0;
			} 
			else if ((iNewBlack > 0) && (iWhiteLev > 0))
			{
				iBlackLev = iNewBlack;
				iWhiteLev = 0;
			} 
			else
			{
				iWhiteLev = iNewWhite;
				iBlackLev = iNewBlack;
			}
			/*if (iNewAlg != iAlgLev)
			{
				System.out.println("DBG150606: ALG CHANGE START.");
				iWhiteLev = 0;
				iBlackLev = 4;
				
				if (iNewAlg == 0)
				{
					System.out.println("Algorithm: AskoChess!");
					whiteList.removeAllItems();
					blackList.removeAllItems();
					for (int i=0;i<AskoChessLevels.length;i++)
					{
						whiteList.addItem(AskoChessLevels[i]);
						blackList.addItem(AskoChessLevels[i]);
					}
				}
				else
				{
					System.out.println("Algorithm: Stockfish! Additems");
					whiteList.removeAllItems();
					blackList.removeAllItems();
					System.out.println("whitelist count:" + whiteList.getItemCount());
					for (int i=0;i<StockFishLevels.length;i++)
					{
						blackList.addItem(StockFishLevels[i]);
						whiteList.addItem(StockFishLevels[i]);
					}
				}
				
				iAlgLev = iNewAlg;
				System.out.println("DBG150606: ALG CHANGE END.");
			}
			*/
			
			whiteList.setSelectedIndex(iWhiteLev);
			blackList.setSelectedIndex(iBlackLev);
			//algorithmList.setSelectedIndex(iAlgLev);
			
			//System.out.println("WHITE:" +whiteList.getSelectedItem());
			//System.out.println("BLACK:" +blackList.getSelectedItem());
			
			bActGate = false;
		}
		
		if (iRetValue != -1) this.hide();
	}
	
	public int getRetValue()
	{
		return iRetValue;
	}
	
	public int getWhiteLevel()
	{
		if (iWhiteLev == 0) return play.PLAYER;
		else return iWhiteLev-1;
	}
	
	public int getBlackLevel()
	{
		if (iBlackLev == 0) return play.PLAYER;
		else return iBlackLev-1;
	}
	
	public int getAlgorithm()
	{	
		return algorithmList.getSelectedIndex();
	}
}

class AlgComboHandler implements ActionListener
{
	newgameDialog ngd;
	
	AlgComboHandler(newgameDialog n)
	{
		ngd = n;
	}
	
	public void actionPerformed (ActionEvent ae)
	{
		System.out.println("AlgComboHandler.actionPerformed()");
		
		if (ngd.bActGate) return;
		else ngd.bActGate = true;
		
		if (ae.getActionCommand().equals("comboBoxChanged"))
		{
			System.out.println("ACH:comboBoxChanged");
			int iNewAlg = ngd.algorithmList.getSelectedIndex();
			
			System.out.println("DBG150606: ALG CHANGE START.");
			int iWhiteLev = 0;
			int iBlackLev = 4;
			
			if (iNewAlg == 0)
			{
				System.out.println("Algorithm: AskoChess!");
				ngd.whiteList.removeAllItems();
				ngd.blackList.removeAllItems();
				for (int i=0;i<ngd.AskoChessLevels.length;i++)
				{
					ngd.whiteList.addItem(ngd.AskoChessLevels[i]);
					ngd.blackList.addItem(ngd.AskoChessLevels[i]);
				}
			}
			else
			{
				System.out.println("Algorithm: Stockfish! Additems");
				ngd.whiteList.removeAllItems();
				ngd.blackList.removeAllItems();
				System.out.println("whitelist count:" + ngd.whiteList.getItemCount());
				for (int i=0;i<ngd.StockFishLevels.length;i++)
				{
					ngd.blackList.addItem(ngd.StockFishLevels[i]);
					ngd.whiteList.addItem(ngd.StockFishLevels[i]);
				}
			}
			
			//iAlgLev = iNewAlg;
			ngd.whiteList.setSelectedIndex(iWhiteLev);
			ngd.blackList.setSelectedIndex(iBlackLev);
			
			System.out.println("DBG150606: ALG CHANGE END.");
			
			ngd.bActGate = false;
			
			
		}
		
		
	}
	
	
}