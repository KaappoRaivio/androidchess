package kaappo.androidchess.askokaappochess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class szanal
{
	public static void main (String args[]) throws Exception
	{
		int[] cArr;
		cArr = new int[20];
		
		try
		{
		BufferedReader br = new BufferedReader(new FileReader("subzero.out"));
			
			String sctr = br.readLine();
			
			while (sctr != null)
			{
				for (int i=0;i<20;i++)
				{
					String sMatch = " "+i;
					if (sctr.indexOf(sMatch) != -1) cArr[i]++;
				}
				sctr = br.readLine();
			}
		}
		catch (Exception e)
		{
			System.out.println("No file.");
		}
		
		String szCat = "Szcat:";
		
		for (int i=3;i<14;i++)
		{
			String sAdd = " "+i;
			while (sAdd.length() < 6) sAdd = " " + sAdd;
			szCat = szCat+sAdd;
		}
		
		PrintWriter pw = null;
			
		if (pw == null)
		{
			pw = new PrintWriter(new BufferedWriter(new FileWriter("szanal.out", true)));
		}
		szCat = szCat + "  " + new Timestamp(System.currentTimeMillis());
		
		pw.println(szCat);
		
		szCat = "      ";
		
		for (int i=3;i<14;i++)
		{
			String sAdd = " "+cArr[i];
			while (sAdd.length() < 6) sAdd = " " + sAdd;
			szCat = szCat+sAdd;
		}
		pw.println(szCat);
		pw.println("--------------------------------------------------------------------------------");
		pw.flush();	
		pw.close();
	}
	
}