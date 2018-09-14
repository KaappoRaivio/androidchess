package kaappo.androidchess.askokaappochess;


public class opgen
{
	public static void main (String args[])
	{
		String[] wMoves = {"a3","a4","b3","b4","c3","c4","d3","d4","e3","e4","f3","f4","g3","g4","h3","h4","Na3","Nc3","Nf3","Nh3"};
		
		String[] bMoves = {"a6","a5","b6","b5","c6","c5","d6","d5","e6","e5","f6","f5","g6","g5","h6","h5","Na6","Nc6","Nf6","Nh6"};
		
		for (int i=0;i<wMoves.length;i++)
		{
			System.out.println("Gen"+i+" 1. " + wMoves[i]);
			for (int j=0;j<bMoves.length;j++)
			{
				System.out.println("Gen"+i+"/"+j+" 1. "+wMoves[i]+" "+bMoves[j]);
			}
		}
		
	}
}