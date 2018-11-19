package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class fulfiller
{
	public static Connection dbconn;
	static String dbString = "jdbc:solid://localhost:2315/dba/dba";
	
	int iChecks = 0;
	int iNewMoves = 0;
	
	public static void main(String[] args) throws Exception
	{
		fulfiller fufi = new fulfiller();
		int lev = new Integer(args[1]).intValue();
		
		PreparedStatement pss = dbconn.prepareStatement("select * from sflib where fen like ?");
		
		String sctr = args[0];
		sctr = sctr.trim();
		String sCrit=sctr.substring(0,sctr.length()-2);
		
		pss.setString(1,sCrit+"%");
		ResultSet rs = null;
		rs = pss.executeQuery();
		String sMove = null;
		if (rs.next())
		{
			System.out.println(rs.getString(2));
			sMove = rs.getString(2).toUpperCase();
		}
		
		chessboard cb = new chessboard();
		cb.init_from_FEN(args[0]);
		chessboard cb2 = cb.copy();
		cb2.domove(sMove,cb2.iFileCol);
		System.out.println("Moving: " + sMove);
		
		System.out.println("FEN now:" + cb2.FEN());
		
		fufi.fulfill(cb2.FEN(),lev);
	}
	
	
	fulfiller() throws Exception
	{
		Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
		dbconn = DriverManager.getConnection(dbString);
	}
	
	public void fulfill(String sFEN, int iTurn) throws Exception
	{
		String sCrit = sFEN.substring(0,sFEN.length()-2).trim();
		String sSQL = "select alg,movelist from anymove where fen like '" + sCrit + "%'";
		Vector vAlg = new Vector();
		Vector vTodo = new Vector();
		Vector vWeirdos = new Vector();
		
		iChecks++;
		
		Statement sta = dbconn.createStatement();
		
		System.out.println("fulfiller: SQL :" + sSQL + " iTurn:" + iTurn);
		ResultSet rs = sta.executeQuery(sSQL);
		while (rs.next())
		{
			System.out.println("Found:" + rs.getString(1)+ " " + rs.getString(2));
			vAlg.addElement(rs.getString(1));
			
			int iWAlg = -1;
			try
			{
				iWAlg = new Integer(rs.getString(1)).intValue();
				if ((iWAlg >= movevalue.ALG_FIRST_WEIRD_OPENING) && (iWAlg <= movevalue.ALG_LAST_WEIRD_OPENING))
				{
					weirdmove wm = new weirdmove(iWAlg,rs.getString(2));
					vWeirdos.addElement(wm);
				}
			}
			catch (Exception e)
			{
				
			}
		}
		
		System.out.println("fulfiller: done");
		
		System.out.println("vAlg contents are:");
		for (int i=0;i<vAlg.size();i++)
		{
			String s = (String)vAlg.elementAt(i);
			System.out.println(s);
		}
		
		System.out.println("Todo analysis");
		if (!vContainsAlg(vAlg,"1097")) vTodo.addElement("1097");
		
		for (int i=1001;i<=1010;i++)
		{
			if (!vContainsAlg(vAlg,i+"")) vTodo.addElement(i+"");
		}
		
		for (int i=1021;i<=1024;i++)
		{
			if (!vContainsAlg(vAlg,i+"")) vTodo.addElement(i+"");
		}
		
		for (int i=0;i<=3;i++)
		{
			if (!vContainsAlg(vAlg,i+":42")) vTodo.addElement(i+":42");
		}
		
		if (vTodo.size() != 0)
		{
			String sTodo = "";
			for (int i=0;i<vTodo.size();i++)
			{
				String s = (String)vTodo.elementAt(i);
				sTodo = sTodo + " " + s;
			}
			ffdebug("Todo contents: " + sTodo);
			
			System.out.println("Work to do by fulfiller.");
			System.out.println("vTodo processing starts:");
			for (int i=0;i<vTodo.size();i++)
			{
				String s = (String)vTodo.elementAt(i);
				System.out.println(s);
				if (s.indexOf(":") == -1)
				{
					int iAlg = new Integer(s).intValue();
					
					if (iAlg != 1097)
					{
						String sMove = engine.getMoveByAlg(engine.sEnginePerAlg(iAlg),sFEN,iAlg).toUpperCase();
						System.out.println("Engine by alg "+iAlg + " responded: " + sMove);
						
						try 
						{
							Statement stains = dbconn.createStatement();
							String sIns = "insert into anymove (alg,fen,movelist) values(" + iAlg+ ",'" +sFEN+ "','" + sMove+ "')";
							System.out.println(sIns);
							stains.executeUpdate(sIns);
							dbconn.commit();
						}
						catch (Exception e)
						{
							System.out.println("Warning: exception at insertion:" + e.getMessage());
						}
						
						chessboard cbx = new chessboard();
						cbx.init_from_FEN(sFEN);
						
						int iColor = -1;
						if (sFEN.indexOf(" w" ) != -1) iColor = piece.WHITE;
						else iColor = piece.BLACK;
						
						System.out.println("iTurn:" + iTurn + " iColor: " + iColor);
						cbx.domove(sMove,iColor);
						cbx.dump();
						String sFENnew = cbx.FEN();
						System.out.println("New FEN:" + sFENnew);
						
						int iNewTurn = iTurn;
						if (iColor == piece.BLACK) iNewTurn++;
						System.out.println("iNewTurn:" + iNewTurn);
						
						sFENnew = sFENnew.substring(0,sFENnew.length()-2).trim() + " "+iNewTurn;
						System.out.println("Fixed New FEN(SF&AB):" + sFENnew);
						

						PrintWriter pw = null;
			
						if (pw == null)
						{
							pw = new PrintWriter(new BufferedWriter(new FileWriter("anymove.txt", true)));
						}
						
						pw.println(sFENnew);
						pw.flush();
						pw.close();
						
						iNewMoves++;
					}
					else
					{
						System.out.println("Alg 1097 processing.");	
						chessboard cbx = new chessboard();
						cbx.init_from_FEN(sFEN);
						
						movevalue mmval = new movevalue("");
						mostore mos = new mostore();
						
						chessboard cb2 = cbx.findAndDoBestMove(cbx.iFileCol,1,mmval,iAlg,true,null,null,false, null,null,null,null,chessboard.CB_MAXTIME,true,null, mos);
						iNewMoves++;
						//dump();
					}
				}
				else
				{
					System.out.println("AskoChess alg processing: " + s);
					
					String[] sComp = s.split(":");
					int iLev = new Integer(sComp[0]).intValue();
					int iACAlg = new Integer(sComp[1]).intValue();
					
					chessboard cbx = new chessboard();
					cbx.init_from_FEN(sFEN);
						
					movevalue mmval = new movevalue("");
					mostore mos = new mostore();
					
					chessboard cb2 = cbx.findAndDoBestMove(cbx.iFileCol,iLev,mmval,iACAlg,false,null,null,false, null,null,null,null,chessboard.CB_MAXTIME,true,null, mos);
					iNewMoves++;
					
					String sMove = cb2.lastmoveString();
					
					System.out.println("AC Move was:" + sMove);
					
					try 
					{
						Statement stains = dbconn.createStatement();
						String sIns = "insert into anymove (alg,fen,movelist) values('" + s+ "','" +sFEN+ "','" + sMove+ "')";
						System.out.println(sIns);
						stains.executeUpdate(sIns);
						dbconn.commit();
					}
					catch (Exception e)
					{
						System.out.println("Warning: exception at insertion:" + e.getMessage());
					}
					
					String sNewFEN = cb2.FEN();
					System.out.println("New FEN is:" + sNewFEN);
					
					int iColor = -1;
					if (sFEN.indexOf(" w" ) != -1) iColor = piece.WHITE;
					else iColor = piece.BLACK;
					
					int iNewTurn = iTurn;
					if (iColor == piece.BLACK) iNewTurn++;
					
					System.out.println("iTurn:" + iTurn + " iColor: " + iColor);
					
					
					System.out.println("iNewTurn:" + iNewTurn);
					
					
					
					sNewFEN = sNewFEN.substring(0,sNewFEN.length()-2).trim() + " "+iNewTurn;
					System.out.println("Fixed New FEN(AC):" + sNewFEN);
					
					//dump();

					PrintWriter pw = null;
			
					if (pw == null)
					{
						pw = new PrintWriter(new BufferedWriter(new FileWriter("anymove.txt", true)));
					}
					
					pw.println(sNewFEN);
					pw.flush();
					pw.close();
					
					//dump();

					
				}
			}
			//dump();
		}  // end todo vector processing
		
		if (vWeirdos.size() > 0)
		{
			System.out.println("Weirdo moves found:");
			for (int i=0;i<vWeirdos.size();i++)
			{
				weirdmove wm = (weirdmove)vWeirdos.elementAt(i);
				wm.dump();
				String[] sWMComp = wm.sMovelist.split(" ");
				
				for (int j=0;j<sWMComp.length;j++)
				{
					System.out.println("move"+j+":" + sWMComp[j]);
					chessboard cbx = new chessboard();
					cbx.init_from_FEN(sFEN);
					
					int iColor = -1;
					if (sFEN.indexOf(" w" ) != -1) iColor = piece.WHITE;
					else iColor = piece.BLACK;
					
					cbx.domove(sWMComp[j],iColor);
					cbx.dump();
					
					String sNewFEN = cbx.FEN();
					
					int iNewTurn = iTurn;
					if (iColor == piece.BLACK) iNewTurn++;
					System.out.println("iNewTurn:" + iNewTurn);
					
					sNewFEN = sNewFEN.substring(0,sNewFEN.length()-2).trim() + " "+iNewTurn;
					System.out.println("Fixed New FEN:" + sNewFEN);
					PrintWriter pw = null;
			
					if (pw == null)
					{
						pw = new PrintWriter(new BufferedWriter(new FileWriter("anymove.txt", true)));
					}
					
					pw.println(sNewFEN);
					pw.flush();
					pw.close();
					
				}
			}
		}  // end weirdo processing
		
		for (int i=movevalue.ALG_FIRST_WEIRD_OPENING;i<=movevalue.ALG_LAST_WEIRD_OPENING;i++)
		{
			if (!vContainsAlg(vAlg,i+"")) 
			{
				System.out.println("We should look for weirdo#:" + i);

				chessboard cbx = new chessboard();
				cbx.init_from_FEN(sFEN);
				
				int iC = -1;
				if (sFEN.indexOf(" w" ) != -1) iC = piece.WHITE;
				else iC = piece.BLACK;
				
				cbx.redoVectorsAndCoverages(iC, movevalue.ALG_SUPER_PRUNING_KINGCFIX);
				
				movevalue mmval = new movevalue("");
				mostore mos = new mostore();
				
				boolean bYes = false;
				
				switch (i)
				{
					case movevalue.ALG_GET_MIDPAWN_OPENING:
						bYes = cbx.bMidPawnOpenings(iC);
						break;
					
					case movevalue.ALG_GET_PAWNPRESS_OPENING:
						bYes = cbx.bPawnPressureOpenings(iC);
						break;
					
					case movevalue.ALG_GET_FIANCHETTOPREP_OPENING:
						bYes = cbx.bFianchettoPrepOpenings(iC);
						break;
					
					case movevalue.ALG_GET_BISHOPE3_OPENING:
						bYes = cbx.bBishopE3Openers(iC);
						break;
					
					case movevalue.ALG_GET_F2STEP_OPENING:
						bYes = cbx.bF2StepOpeners(iC);
						break;
					
					case movevalue.ALG_GET_PAWNFRONT_OPENING:
						bYes = cbx.bPawnFrontOpeners(iC);
						break;
					
					case movevalue.ALG_GET_BACKROWROOK_OPENING:
						bYes = cbx.bBackRowRookOpeners(iC);
						break;
					
					case movevalue.ALG_GET_KNIGHTTOMIDDLE_OPENING:
						bYes = cbx.bKnightToMiddleMoves(iC);
						break;
					
					case movevalue.ALG_GET_QUEENFIRSTMOVE_OPENING:
						bYes = cbx.bQueenFirstMoves(iC);
						break;
					
					case movevalue.ALG_GET_C2STEP_OPENING:
						bYes = cbx.bC2StepOpeners(iC);
						break;
					
					case movevalue.ALG_GET_BISHOPF4_OPENING:
						bYes = cbx.bBishopF4Openers(iC);
						break;
					
					default:
						System.out.println("Illegal weirdo code:" + i);
						throw new RuntimeException("Illegal weirdo code (row 393 of fulfiller.java):" + i);
				}
				
				if (bYes)
				{	
					chessboard cb2 = cbx.findAndDoBestMove(cbx.iFileCol,0,mmval,i,false,null,null,false, null,null,null,null,chessboard.CB_MAXTIME,true,null, mos);
					
					String sMpSugg = cb2.sMoveOrder.trim();
					
					String[] sMPC = sMpSugg.split(" ");
					
					System.out.println("Weirdo:" + i + " suggs:" + sMpSugg);
					System.out.println("sMpSugg.length==0:"+(sMpSugg.length()==0));
					
					if ((sMpSugg.length()!=0)) ffdebug("New weirdo: " + i + " suggs:" + sMpSugg);
					
					for (int j=0;j<sMPC.length;j++)
					{
						if (sMPC[j].trim().length() > 0)
						{
							System.out.println("Doing: " + sMPC[j]);
							
							cbx = new chessboard();
							cbx.init_from_FEN(sFEN);
							
							int iColor = -1;
							if (sFEN.indexOf(" w" ) != -1) iColor = piece.WHITE;
							else iColor = piece.BLACK;
							
							cbx.domove(sMPC[j],iColor);
							cbx.dump();
							
							String sNewFEN = cbx.FEN();
							
							int iNewTurn = iTurn;
							if (iColor == piece.BLACK) iNewTurn++;
							System.out.println("iNewTurn:" + iNewTurn);
							
							sNewFEN = sNewFEN.substring(0,sNewFEN.length()-2).trim() + " "+iNewTurn;
							System.out.println("Fixed New FEN:" + sNewFEN);
							PrintWriter pw = null;
					
							if (pw == null)
							{
								pw = new PrintWriter(new BufferedWriter(new FileWriter("anymove.txt", true)));
							}
							
							pw.println(sNewFEN);
							pw.flush();
							pw.close();
						}
						
					}
					
					if ((sMpSugg.length()!=0)) 
					{
						try 
						{
							Statement stains = dbconn.createStatement();
							String sIns = "insert into anymove (alg,fen,movelist) values(" + i+ ",'" +sFEN+ "','" + sMpSugg+ "')";
							System.out.println(sIns);
							stains.executeUpdate(sIns);
							dbconn.commit();
							iNewMoves++;
						}
						catch (Exception e)
						{
							System.out.println("Warning: exception at insertion:" + e.getMessage());
						}
					}
				}
			}
		}

		
	}
	
	
	private boolean vContainsAlg(Vector v, String s)
	{
		for (int i=0;i<v.size();i++)
		{
			String s2 = (String)v.elementAt(i);
			if (s2.equals(s)) return true;
		}
		
		return false;
	}
	
	public void dump() throws Exception
	{
		PrintWriter pw = null;
			
		if (pw == null)
		{
			pw = new PrintWriter(new BufferedWriter(new FileWriter("fulfiller.out", true)));
		}
		
		pw.println(new Timestamp(System.currentTimeMillis())+ " iChecks:"+iChecks + " iNewMoves:"+iNewMoves);
		pw.flush();
		pw.close();
	}
	
	public void ffdebug(String sMes) throws Exception
	{
		PrintWriter pw = null;
			
		if (pw == null)
		{
			pw = new PrintWriter(new BufferedWriter(new FileWriter("fulfiller.out", true)));
		}
		
		pw.println(sMes);
		pw.flush();
		pw.close();
	}
}

class weirdmove
{
	int iWeirdoAlg; 
	String sMovelist;
	
	weirdmove(int iWeirdoAlg, String sMovelist)
	{
		this.iWeirdoAlg = iWeirdoAlg;
		this.sMovelist = sMovelist;
	}
	
	void dump()
	{
		System.out.println("weirdmove: " + iWeirdoAlg + " " + sMovelist );
	}
}