package kaappo.androidchess.askokaappochess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class sfiler
{
	public static Connection dbconn;
	static String dbString = "jdbc:solid://localhost:2315/dba/dba";
	
	static int iCount = 0;
	static int iAttCount = 0;
	static int iMaxScore = -500;
	static int iMinScore = 500;
	static int iCountAbove100 = 0;
	static int iCountAbove0 = 0;
	static int iCountBelowMinus100 = 0;
	static int iScoreTotal = 0;
	
	public static void main (String args[]) throws Exception
	{
		Driver d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
		
		Connection c = null;
		PreparedStatement pss = null;
		PreparedStatement psi = null ;
		
		c = DriverManager.getConnection(dbString);
		pss = c.prepareStatement("select * from sflib where fen like ?");
		psi = c.prepareStatement("insert into sflib values(?,?,?)");
		
		BufferedReader br;
		try
		{
			 br = new BufferedReader(new FileReader("sfileinput.txt"));
		}
		catch (Exception e)
		{
			System.out.println("No file sfileinput.txt. Exiting.");
			return;
		}
		
		String sctr = br.readLine();
		while (sctr != null)
		{
			sctr = sctr.trim();
			
			System.out.println(sctr);
			String sDQ = ""+(char)34;
			String scComp[] = sctr.split(sDQ);
			System.out.println(scComp[1]);
			String sMove = scComp[3];
			
			
			String sCrit=sctr.substring(1,scComp[1].length()-1).trim();
			//String sNewB=sctr.substring(1,scComp[1].length()).trim();
			String sNewB=scComp[1];
			System.out.println("C:<"+ sCrit + "> M:<" + sMove + "> N:<"+sNewB+">");
			
			//System.exit(0);
			
			ResultSet rs = null;
			pss.setString(1,sCrit+"%");
			rs = pss.executeQuery();
			iAttCount++;
			if (!rs.next())
			{
				psi.setString(1,sNewB);
				psi.setString(2,sMove);
				psi.setInt(3,0);
				//System.out.println("sctr: "+sctr);
				//System.out.println("CRIT:<"+sCrit+">");
				try
				{
					psi.executeUpdate();
					iCount++;
				}
				catch (SQLException sqle)
				{
					if (sqle.getErrorCode() == 10033) 
					{
						System.out.println("sctr: "+sctr);
						System.out.println("CRIT:<"+sCrit+">");
					}
					else 
					{
						System.out.println("Unexpected SQL Exception at sfiler");
						System.out.println(sqle.getMessage());
						System.out.println("sctr: "+sctr);
						System.out.println("CRIT:<"+sCrit+">");
						System.exit(0);	
					}
					
				}
			}
			sctr = br.readLine();
		}
		System.out.println("Done. iAttCount:" + iAttCount + " iCount:" + iCount);
		c.commit();
		
	}
}