package kaappo.androidchess.askokaappochess;
import java.awt.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JPanel; 
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

/*
public class AskoBorder {

    public static void main(String[] args) 
	{

		newgameFrame nf = new newgameFrame();
		nf.waitUntilClose();
		System.out.println("Code has returned:" + nf.getRetValue());
		System.out.println("White Level " + nf.getWhiteLevel());
		System.out.println("Black Level " + nf.getBlackLevel());
		System.out.println("Algorithm " + nf.getAlgorithm());
	}
}
*/

public class newgameFrame extends JFrame implements ActionListener
{
	int iRetValue = -1;
	
	JComboBox whiteList = null;
	JComboBox blackList = null;
	JComboBox algorithmList = null;
	JPanel panel = null;
	
	int iWhiteLev = 0;
	int iBlackLev = 2;
	
	newgameFrame()
	{
		this.setSize(400,200);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);       
		this.setTitle("AskoChess - Start new game");
		this.setResizable(false);
		
        Border etched = (Border) BorderFactory.createEtchedBorder();

		panel= new JPanel(new GridLayout(4, 2));
		
		String[] whiteLevels = { "Player" , "AskoChess Level 1", "AskoChess Level 2", "AskoChess Level 3" };

		String[] blackLevels = { "Player" , "AskoChess Level 1", "AskoChess Level 2", "AskoChess Level 3" };
		
		String[] algorithms = {"Standard"};
		
		whiteList = new JComboBox(whiteLevels);
		blackList = new JComboBox(blackLevels);
		whiteList.setSelectedIndex(iWhiteLev);
		blackList.setSelectedIndex(iBlackLev);
		algorithmList = new JComboBox(algorithms);
		
		JLabel whitelab = new JLabel("White level", JLabel.LEFT);
		JLabel blacklab = new JLabel("Black level", JLabel.LEFT);
		JLabel alglab = new JLabel("Algorithm", JLabel.LEFT);
		
		JButton startbutton = new JButton("Start new game");
		JButton cancelbutton = new JButton("Cancel");
		
		panel.add(whitelab);
		panel.add(whiteList);
		panel.add(blacklab);
		panel.add(blackList);
		panel.add(alglab);
		panel.add(algorithmList);
		panel.add(startbutton);
		panel.add(cancelbutton);
		
		// ===============================================================
		
        if (this.add(panel) == null) System.out.println("Adding panel to frame failed");
        //this.setVisible(true);
		
		//panel.revalidate();
		//this.revalidate();
		
		whiteList.addActionListener(this);
		blackList.addActionListener(this);
		startbutton.addActionListener(this);
		cancelbutton.addActionListener(this);
		
		pack();
		this.pack();
		this.setVisible(true);
		System.out.println("newgameFrame constructor finished.");
	}
	
	public void actionPerformed (ActionEvent ae)
	{
		//System.out.println("Action event:" + ae.toString());
		System.out.println(ae.getActionCommand());
		
		if (ae.getActionCommand().equals("Cancel")) iRetValue = 1;
		if (ae.getActionCommand().equals("Start new game")) iRetValue = 2;
		
		if (ae.getActionCommand().equals("comboBoxChanged"))
		{	
			System.out.println("Combo box change ....");
			System.out.println(ae.toString());
			System.out.println(ae.paramString());
			
			int iNewWhite = whiteList.getSelectedIndex();
			int iNewBlack = blackList.getSelectedIndex();
			
			if ((iNewWhite == 0) && (iBlackLev == 0))
			{
				iWhiteLev = iNewWhite;
				iBlackLev = 2;
			} 
			else if ((iNewBlack == 0) && (iWhiteLev == 0))
			{
				iBlackLev = iNewBlack;
				iWhiteLev = 2;
			}
			else if ((iNewWhite > 0) && (iBlackLev > 0))
			{
				iWhiteLev = iNewWhite;
				iBlackLev = 0;
			} 
			else if ((iNewBlack > 0) && (iWhiteLev > 0))
			{
				iBlackLev = iNewBlack;
				iWhiteLev = 0;
			} 
			else
			{
				iWhiteLev = iNewWhite;
				iBlackLev = iNewBlack;
			}
			
			whiteList.setSelectedIndex(iWhiteLev);
			blackList.setSelectedIndex(iBlackLev);
			
			System.out.println("WHITE:" +whiteList.getSelectedItem());
			System.out.println("BLACK:" +blackList.getSelectedItem());
			
			
		}
		
		if (iRetValue != -1) this.dispose();
		
	}
	
	public boolean waitUntilClose()
	{
		while (iRetValue == -1)
		{
			try { Thread.sleep(500); } catch (Exception e) {}
        }
		
		return true;
	}
	
	public int getRetValue()
	{
		return iRetValue;
	}
	
	public int getWhiteLevel()
	{
		return iWhiteLev;
	}
	
	public int getBlackLevel()
	{
		return iBlackLev;
	}
	
	public int getAlgorithm()
	{	
		return algorithmList.getSelectedIndex();
	}
}