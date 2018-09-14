package kaappo.androidchess.askokaappochess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;
public class numabreak
{
	public static void main (String args[]) throws Exception
	{
		String fname = args[0];
		int iBreakNo = new Integer(args[1]).intValue();
		
		if ((!fname.equals("farrer.bat")) && (!fname.equals("sfretr.bat")))
		{
			System.out.println("Wrong file name");
			return;
		}
		
		if ((iBreakNo < 1) || (iBreakNo > 5))
		{
			System.out.println("Bad breakno.");
			return;
		}
		
		BufferedReader br;
		try
		{
			 br = new BufferedReader(new FileReader(fname));
		}
		catch (Exception e)
		{
			System.out.println("No file sfileinput.txt. Exiting.");
			return;
		}
		
		Vector v = new Vector();
		String sctr = br.readLine();
		while (sctr != null)
		{
			if (sctr.indexOf("java anyokmove") != -1) v.addElement(sctr);
			sctr = br.readLine();
		}
		
		int iSetSize = v.size() / iBreakNo + 1;
		System.out.println(fname + " read " + v.size() + " records. set size: " + iSetSize);
		
		for (int i=0;i<iBreakNo;i++)
		{
			String fComp[] = fname.split("\\.");
			String sCompName = fComp[0]+(i+1)+".bat";
			
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(sCompName, true)));
			copytoPW(pw,"numapre.bat");
			
			int loopstart = i*iSetSize;
			int loopend = Math.min(i*iSetSize + iSetSize , v.size());
			System.out.println("Output to " + sCompName+ "  " + loopstart + "-" + loopend );
			for (int j=loopstart;j<loopend;j++)
			{
				pw.println((String)v.elementAt(j));
			}
			copytoPW(pw,"numapost.bat");
			pw.flush();
			pw.close();
		}
		
		
		
	}
	
	static void copytoPW(PrintWriter pw,String fname) throws Exception
	{
		BufferedReader br;
		try
		{
			 br = new BufferedReader(new FileReader(fname));
		}
		catch (Exception e)
		{
			System.out.println("No file " + fname + " Exiting.");
			return;
		}
		
		String sctr = br.readLine();
		while (sctr != null)
		{
			pw.println(sctr);
			sctr = br.readLine();
		}
		
	}
}