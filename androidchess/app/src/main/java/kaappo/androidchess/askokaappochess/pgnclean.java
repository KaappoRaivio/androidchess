
package kaappo.androidchess.askokaappochess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;
import java.util.*;
import java.sql.*;

public class pgnclean
{
	public static void main (String args[]) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		
		String sOutFile = new String(args[0]);
		sOutFile = sOutFile.replaceAll("pgn","pgc");
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(sOutFile, true)));
		
		PrintWriter pwbat = new PrintWriter(new BufferedWriter(new FileWriter("play.bat", true)));
		
		String sctr = br.readLine();
		String ob = "";
		while (sctr != null)
		{
			sctr = sctr.trim();
			
			if ((sctr.indexOf("[") == -1) && (sctr.length() >0))
			{
				//System.out.println("A:"+sctr);
				String s2 = sctr.replaceAll("\\.",". ");
				String s3 = s2.replaceAll("\\s+", " ");
				String s4 = s3.replaceAll("x","");
				String s5 = s4.replaceAll("\\+","");
				s5 = s5.trim();
				
				//System.out.println("B:"+s5);
				if (s5.indexOf("1.") == 0)
				{
					if (ob.length() > 0) pw.println(ob);
					ob = s5;
				}
				else
				{
					ob = ob + " " + s5;
				}
			}
			
			sctr = br.readLine();
		}
		pw.flush();
		pw.close();
		pwbat.println("del sfinput.txt");
		pwbat.println("java pgnplay " + sOutFile);
		pwbat.println("java enginerunner");
		pwbat.flush();
		pwbat.close();
	}
}