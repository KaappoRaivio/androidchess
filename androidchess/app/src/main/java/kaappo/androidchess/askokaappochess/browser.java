package kaappo.androidchess.askokaappochess;

import java.awt.Desktop;
import java.net.URI;
import java.io.*;

public class browser
{
	public static void main(String args[]) {
		if(Desktop.isDesktopSupported())
		{
		  File htmlFile = new File("askochess.html");
		  Desktop.getDesktop().open(htmlFile);
		}
	}
	
}