package kaappo.androidchess.askokaappochess;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;

import java.util.*;
import java.sql.*;

public class logsaver
{
	public static void main(String args[]) throws Exception
	{
		BufferedReader br;
		try
		{
			 br = new BufferedReader(new FileReader("dumplog.out"));
		}
		catch (Exception e)
		{
			System.out.println("No file. Exiting.");
			return;
		}
		
		String sctr = br.readLine();
		boolean bInside = false;
		boolean bRel = false;
		while (sctr != null)
		{
			if (sctr.indexOf("DBE_LOGREC_INSTUPLENOBLOBS_INT4") != -1)
			{
				bInside = true;
			}
			if (bInside && (sctr.indexOf("Relid") != -1))
			{
				bRel = true;
			}
			
			if (bRel && (sctr.indexOf("Vtpl") != -1))
			{
				System.out.println(sctr);
				String s2=sctr.substring(25);
				System.out.println(s2);
				System.out.println(s2.indexOf("\\2F"));
				//String s3 = s2.replaceAll("\\2F","ekipekka");
				String s3 = "";
				for (int i=0;i<s2.length();i++)
				{
					System.out.println(i + " " + s2.charAt(i) + " " );
					if (i +3 < s2.length()  ) 
					{
						String ss3 = s2.substring(i,i+3);
						System.out.println(ss3);
						if (ss3.equals("\\2F"))
						{
							s3 = s3 + "/";
							i = i + 3;
						}
						if (ss3.equals("\\2D"))
						{
							s3 = s3 + "-";
							i = i + 3;
						}
						if (ss3.equals("\\00"))
						{
							s3 = s3 + " ";
							i = i + 19;
						}
						else s3 = s3 + s2.charAt(i);
					}
					else s3 = s3 + s2.charAt(i);
					
					//System.out.println("S3:"+s3);
				}

				System.out.println("S3:"+s3);
				bRel = false;
				bInside = false;
			}
			
			
			sctr = br.readLine();
		}
	}
}