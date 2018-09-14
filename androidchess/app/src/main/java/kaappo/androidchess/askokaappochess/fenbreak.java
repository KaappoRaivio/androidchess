package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class fenbreak
{
	public static int FENSPERFILE = 6000;
	
	public static void main (String args[]) throws Exception
	{
		String sInFile = args[0];
		String sFileBase = args[1];
		FENSPERFILE = new Integer(args[2]).intValue();
		
		BufferedReader br = new BufferedReader(new FileReader(sInFile));
		String sctr = br.readLine();
		if (sctr != null) sctr=sctr.trim();
		int lc = 1;
		int fileno = 1;
		
		PrintWriter pw = filename(sFileBase,fileno);
		boolean bAnyMove = true;
		
		while (sctr != null)
		{
			if (bAnyMove)
			{
				String sLine = "java anyokmove " + (char)34 + sctr + (char)34 ;
				pw.println(sLine);
			}
			else
			{
			String sLine = "java blana fen " + (char)34 + sctr + (char)34 + " Q2";
			pw.println(sLine);
			pw.println("call finaldeep Q2");
			}
			
			if ((lc % FENSPERFILE) == 0)
			{
				//pw.println("newenr");
				pw.println("call amru");
				pw.println("qenr");
				pw.println("rem FILE " + fileno + " DONE.");
				pw.flush();
				pw.close();
				fileno++;
				pw = filename(sFileBase,fileno);
				
			}
			
			sctr = br.readLine();
			if (sctr != null) sctr=sctr.trim();
			lc++;
		}
		pw.println("call amru");
		//pw.println("qenr");
		pw.println("rem FILE " + fileno + " DONE.");
		pw.flush();
		pw.close();
		
	}
	
	public static PrintWriter filename (String sBase,int fnr) throws Exception 
	{
		//String fname = "fen2b";
		String fname = sBase;
		if (fnr < 1000) fname = fname + "0";
		if (fnr < 100) fname = fname + "0";
		if (fnr < 10) fname = fname + "0";
		fname = fname + fnr;
		fname = fname + ".bat";
		
		return new PrintWriter(fname);
	}
}
