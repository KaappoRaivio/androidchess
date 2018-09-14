package kaappo.androidchess.askokaappochess;
import java.util.*;
import java.io.*;
import java.io.PrintWriter;
import java.sql.*;

public class libcat
{
	static String dbString = "jdbc:solid://localhost:2315/dba/dba";
	
	public static void main(String args[]) throws Exception 
	{
		long lStart = System.currentTimeMillis();
		
		Driver d = null;	
		Connection c = null;
		PreparedStatement pss = null;
		
		d = (Driver)Class.forName("solid.jdbc.SolidDriver").newInstance();
		c = DriverManager.getConnection(dbString);
		
		System.out.println("Libcat query starts at:" + new Timestamp(lStart));
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery("select fen from sflib");
		
		int[] iCArr;
		iCArr = new int[21];
		
		while (rs.next())
		{
			for (int lc = 2; lc <= 20; lc++)
			{
				String sLc = " "+lc+" ";
				String sRec = rs.getString(1)+" ";
				if (sRec.indexOf(sLc) != -1) iCArr[lc]++;
			}
		}
		
		rs.close();
		s.close();
		c.close();
		long lEnd = System.currentTimeMillis();
		
		for (int lc = 2; lc <= 20; lc++)
		{
			String sOut = "LC " + lc;
			while (sOut.length() < 18) sOut = sOut+ " ";
			String sCount = " " + iCArr[lc];
			while (sCount.length() < 10) sCount = " " + sCount;
			System.out.println(sOut+sCount);
		}
		
		System.out.println("Libcat query done at:" + new Timestamp(lEnd));
		
	}
	
}