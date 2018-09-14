package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class ensf
{
	public static void main (String[] args) throws Exception
	{
		String dbString = "jdbc:solid://localhost:2315/dba/dba";
		
		Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
		Connection c = DriverManager.getConnection(dbString);
		
		String sGetFEN = "select FEN, gmove from sflib where fen like '% 5'";
		
		Statement s = c.createStatement();
		
		ResultSet rs = s.executeQuery(sGetFEN);
		int rc = 0;
		
		while (rs.next()) 
		{
			rc++;
			
			if (Math.random() < 0.002)
			{
			
				String sFEN = rs.getString(1);
				String sMove = rs.getString(2);
				System.out.println("FEN INPUT"+rc+":"+sFEN+" -> "+sMove);
				
				chessboard cb = new chessboard();
				cb.init_from_FEN(sFEN);
				cb.dump();
				cb.domove(sMove,cb.iFileCol);
				cb.dump();
				System.out.println("L1FEN:" + cb.FEN());
				play.printFENEntry(cb.FEN());
				String sEngMove = engine.getMoveByAlg("",cb.FEN(),movevalue.ALG_ASK_FROM_ENGINE10);
				System.out.println("NEWENGMOVE:"+sEngMove);
				if (engine.mVec != null)
				{
					for (int i=0;i<engine.mVec.size();i++)
					{
						chessboard cb2 = cb.copy();
						String sMove2 = (String)engine.mVec.elementAt(i);
						System.out.println("ENGCAND:" + sMove2);
						cb2.domove(sMove2,1-cb.iFileCol);
						if (cb.iFileCol == 1) cb2.iMoveCounter++;
						String nFEN = cb2.FEN();
						System.out.println("L2FEN:" + nFEN);
						play.printFENEntry(nFEN);
					}
				}
			}
			
		}
		
	}
}