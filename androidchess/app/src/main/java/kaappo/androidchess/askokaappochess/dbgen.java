package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;

import java.util.*;
import java.sql.*;

public class dbgen
{
	public static void main (String args[]) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader("deeps.txt"));
		PrintWriter pw = new PrintWriter("deeprun.bat");
			
		String sctr = br.readLine();
		String sEnd = "";
		if ((args.length>0) && (args[0].equalsIgnoreCase("Q2"))) sEnd = " Q2";
		
		while (sctr != null)
		{
			pw.println("java blana fen " + (char)34 + sctr + (char)34 + sEnd);
			sctr = br.readLine();
		}
		
		pw.flush();
		pw.close();
	}
}