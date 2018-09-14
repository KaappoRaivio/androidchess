package kaappo.androidchess.askokaappochess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.io.PrintWriter;

import java.util.*;
import java.sql.*;

public class farfix
{
	public static void main(String args[]) throws Exception
	{
		BufferedReader br;
		try
		{
			 br = new BufferedReader(new FileReader("farrerrec.txt"));
		}
		catch (Exception e)
		{
			System.out.println("No file. Exiting.");
			return;
		}
		
		String sctr = br.readLine();
		while (sctr != null)
		{
			if (sctr.indexOf("RETRIEVE") != -1) 
			{
				String sComp[] = sctr.split(":");
				//System.out.println(sComp[0]+"|"+sComp[1]+"|"+sComp[2]);
				System.out.println("java anyokmove "+(char)34+sComp[1]+(char)34+" "+(char)34+sComp[2]+(char)34);
			}
			else System.out.println(sctr);
			sctr = br.readLine();
		}
	}
}