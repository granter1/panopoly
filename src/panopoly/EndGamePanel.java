package panopoly;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.io.FileUtils;


public class EndGamePanel extends JPanel{

	private static final long serialVersionUID = 4940008794098536402L;

	private int currentPlayerNumber = 0;

	public EndGamePanel(ArrayList<Player> players, Board board, Frame frame) throws IOException{

		JPanel characterPanel = new JPanel(new GridBagLayout());

		//   	BufferedImage endImage = ImageIO.read(new File("gameImages/endGameGif.gif"));
		//    	ImageIcon endImage = new ImageIcon(this.getClass().getResource("gameImages/endGameGif.gif"));
		//
		//   	JLabel picLabel = new JLabel(endImage); 

		JLabel[] imageLabels = new JLabel[players.size()];
		JLabel[] position = new JLabel[players.size()];
		JFrame selectionPanel = new JFrame();

		selectionPanel.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		//Set the frame icon to an image loaded from a file.
		BufferedImage myPhoto = ImageIO.read(new File("gameImages/trophy.png"));
		Image myGameIcon = myPhoto.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
		selectionPanel.setIconImage(myGameIcon);
		JLabel informationArea = new JLabel("The label",SwingConstants.CENTER);


		selectionPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		//https://stackoverflow.com/questions/3360255/how-to-get-a-single-file-from-a-folder-in-java
		//        File dir = new File("savedImages");
		//        File[] children = dir.listFiles();

		//Ensures all images are resized evenly.
		c.weightx = .5;
		c.weighty = .5;

		/*
		 * Images are obtained from /savedImages.
		 * These are then resized and added to the buttons.
		 * Each button added to the JPanel.
		 */
		int place;
		for(int i=players.size()-1;i>=0;i--) {
			place = players.size()-i;
			if(place==1) {
				position[i] = new JLabel(place+"st");
			}
			else if(place == 2) {
				position[i] = new JLabel(place+"nd");
			}else if(place == 3) {
				position[i] = new JLabel(place+"rd");
			}else {
				position[i] = new JLabel(place+"th");
			}
			c.fill = GridBagConstraints.HORIZONTAL;
			c.ipadx = 5;
			c.gridx = place-1;
			c.gridy = 0;
			characterPanel.add(position[i],c);
		}
		int j=0;
		for(int i=players.size()-1;i>=0;i--){
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = j;
			c.gridy = 1;
			imageLabels[j] = new JLabel();
			BufferedImage myPicture = ImageIO.read(new File(players.get(i).getPathForImageIcon()));
			Image myResizedPicture = myPicture.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
			imageLabels[j].setIcon(new ImageIcon(myResizedPicture));
			characterPanel.add(imageLabels[j],c);
			j++;
		}

		//		c.gridx = 3;
		//		c.gridy = 2;
		//		characterPanel.add(picLabel,c);

		//Constraints for JLabel that presents character selection info.
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;

		//Ensures the JLabel spans all columns when beneath the images.
		c.gridwidth = players.size();


		characterPanel.add(informationArea,c);//Add information about selecting characters under buttons with images
		selectionPanel.add(characterPanel);//Add this to JFrame.

		selectionPanel.setPreferredSize(new Dimension((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth(),(int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()));
		selectionPanel.pack();
		selectionPanel.setLocationRelativeTo(null);//Centers JFrame on users screen.
		selectionPanel.setVisible(true);
		//selectionPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selectionPanel.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				try {
					FileUtils.cleanDirectory(new File("savedImages"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				selectionPanel.dispose();
				frame.dispose();		
			}
		}); 
	}

	public int getCurrentPlayerNumber(){
		return this.currentPlayerNumber;
	}

}
