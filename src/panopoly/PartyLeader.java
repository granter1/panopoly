package panopoly;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import interfaces.Mortgageable;
import interfaces.Ownable;
import locations.*;

public class PartyLeader {
	
	//Booleans for game control
	private boolean boughtProperty = false;

	private HistoryLog history = null;
	private static ArrayList<NamedLocation> locations = (ArrayList<NamedLocation>) SetupGame.getLocationList();
	private static ArrayList<Player> players = SetupGame.getPlayers();
	private Board board;
	private static Dice normalDice = new Dice();
	private static Random rand = new Random();
	private JFrame mainFrame;

	public PartyLeader(HistoryLog history, Board board, JFrame frame){
		this.history = history;
		this.board = board;
		this.mainFrame = frame;
	}

	public void roll(Player player) throws InvalidFormatException, IOException {
		int moveCount;
		String str = "";

		moveCount = normalDice.rollDice(2, 6);
		str += moveCount;
		history.getTextArea().setText("You have rolled a "+str+".\n");	
		player.rolled();
		BufferedImage myImage = ImageIO.read(new File("savedImages/Borat.jpg"));

		for(int i=0;i<moveCount;i++) {
			player.setLocation((NamedLocation)player.getLeft());
			history.getTextArea().append("You have rolled onto "+player.getLocation().getIdentifier()+".\n");
			board.updateIcons(player);
			board.repaint();
			mainFrame.revalidate();
		}
		history.getTextArea().append("Roll Over.\n");

		//After roll
		if(player.getLocation() instanceof MCQLocation) { // TODO get rid of !
			MCQ mcq = new MCQ();
			mcq.createMCQPanel(player,  history);
		}
		if(!(player.getLocation() instanceof CardLocation)) {
			CardGenerator.createCard(player, history);
		}
	}
	public void buy(Player player) {
		if(player.getLocation() instanceof PrivateProperty) {
			if(((PrivateProperty)player.getLocation()).getOwner()==null) {
				if(player.getNetWorth()>=(((PrivateProperty)player.getLocation()).getPrice())) {
					player.buyProperty((PrivateProperty)player.getLocation());
					((PrivateProperty)player.getLocation()).setOwner(player);
					history.getTextArea().append("You have bought "+player.getLocation().getIdentifier()+".\n");
					boughtProperty = true;
				}else {
					history.getTextArea().append("This property is too expensive.\n");	
				}
			}
			else {
				history.getTextArea().append("This property is already purchased.\n");
			}
			
		}else {
			history.getTextArea().append("You cannot purchase this property.\n");
		}
	}

	public void sell(Player player) {
		// TODO
	}

	public void mortgage(Player player){
		//TODO
		//Get list of players properties.
		//Check what properties are mortgageable
		//Pass information to JPanel
		//Allow them to select which properties to mortgage.
		//Ensure balance is updated accordingly
		ArrayList<PrivateProperty> mortgageableProperties = new ArrayList<PrivateProperty>();
		JFrame mortgageFrame = new JFrame("Mortgage Choices");
		JLabel userDirections = new JLabel("Choose which properties to mortgage then hit 'Confirm'");



		for(int i=0;i<player.getProperties().size();i++){
			//If property is mortgageable and is not already mortgaged
			if(player.getProperties().get(i) instanceof Mortgageable && player.getProperties().get(i).isMortgaged() == false){
				mortgageableProperties.add(player.getProperties().get(i));
			}
		}

		/*
		 * Create an array list of buttons. One for each mortgageable property.
		 */
		ArrayList<JRadioButton> mortgageableRadioButtons = new ArrayList<JRadioButton>();
		for(int i=0;i<mortgageableProperties.size();i++){
			mortgageableRadioButtons.add(new JRadioButton("Property: "+mortgageableProperties.get(i).getIdentifier()
					+" Mortgage Amount: "+mortgageableProperties.get(i).getMortgageAmount()));
		}

		//Create the button for choice confirmation.
	    JButton confirmationButton = new JButton("Confirm");
	    
	    /* 1. Check which buttons are selected
	     * 2. Count the total amount of mortgages selected.
	     * 3. Add this to players account
	     * 4. Set isMortgaged to true for all properties that are mortgaged
	    */
		 confirmationButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	               
	                    try {	               
	                		int totalMortgageValue = 0;
	                    	int j=0;
	                    	//This list is used to create a string that will be appended to text area on the GUI
		            		ArrayList<String> mortgageableIdentifiers = new ArrayList<String>();
		            		ArrayList<String> unmortgagableIdentifiers = new ArrayList<String>();
		            		
	                    	while(j < mortgageableRadioButtons.size()){
	                    		
	                    		if(mortgageableRadioButtons.get(j).isSelected()){
	                    			
	                    			//Check that the user can mortgage the property
	                    			if(mortgageableProperties.get(j) instanceof ImprovableProperty){
	                    				if(((ImprovableProperty)mortgageableProperties.get(j)).getNumHouses()==0){
	                    					totalMortgageValue += mortgageableProperties.get(j).getMortgageAmount();
	                    					mortgageableIdentifiers.add(mortgageableProperties.get(j).getIdentifier());
	                    				}
	                    				
	                    				//Can't be mortgaged. Add to list.
	                    				else{
		                    				unmortgagableIdentifiers.add(mortgageableProperties.get(j).getIdentifier());
	                    				}
	                    			}

	                    		}
	                    		j++;
	                    	}
	                    	
	                    	
	                    	//No unmortgageable properties were selected. Allow them to mortgage.
	                    	if(unmortgagableIdentifiers.size() == 0){
	                    		
	                    		//Mortgage the correct properties.
	                    		for(int x=0;x<mortgageableIdentifiers.size();x++){
	                    			for(int y=0; y<player.getProperties().size();y++){
	                    				if(mortgageableIdentifiers.get(x).equals(player.getProperties().get(y).getIdentifier())){
	                    					player.getProperties().get(y).mortgage();
	                    				}
	                    			}
	                    		}

	                    		//Add the total value to the players balance.
	                    		player.addToBalance(totalMortgageValue);
	                    		history.getTextArea().append("Properties Mortgaged: "+buildString(mortgageableIdentifiers)+" for total: "+totalMortgageValue+" \n");
		                    	mortgageFrame.dispose();//Exit JFrame, player has selected the properties they want to redeem
		                    	
	                    	}
	                    	
	                    	else{
	                    		history.getTextArea().append("Can't mortgage the following properties"
	                    				+ " due to houses on locations: "+buildString(unmortgagableIdentifiers)+"\n");
	                    	}
                    		
						} 
	                    catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
	                } 
	            
	      });
		 
		 
		 //Add all choices to JPanel
		 JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		 
		 //Add label that contains directions for user onto panel
		 radioPanel.add(userDirections);
		 
		 for(int i=0;i<mortgageableProperties.size();i++){
		     radioPanel.add(mortgageableRadioButtons.get(i));
		 }
		 
		 //Add button below choices
		 radioPanel.add(confirmationButton);
		 
		 radioPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	     mortgageFrame.add(radioPanel);
	     mortgageFrame.setVisible(true);
	     mortgageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	     mortgageFrame.pack();
	     mortgageFrame.setLocationRelativeTo(null);
	     
	}

	public void redeem(Player player){
		//TODO

		ArrayList<PrivateProperty> redeemableProperties = new ArrayList<PrivateProperty>();
		JFrame redeemFrame = new JFrame("Mortgage Redemption Choices");
		JLabel userDirections = new JLabel("Choose which mortgages to redeem then hit 'Confirm'");
		JLabel userAlert = new JLabel("You currently have enough to redeem these mortgages.");

		for(int i=0;i<player.getProperties().size();i++){
			//If property is mortgageable and is not already mortgaged
			if(player.getProperties().get(i) instanceof Mortgageable && player.getProperties().get(i).isMortgaged() == true){
				redeemableProperties.add(player.getProperties().get(i));	
			}

		}

		/*
		 * Create an array list of buttons. One for each mortgageable property.
		 */
		ArrayList<JRadioButton> redeemableRadioButtons = new ArrayList<JRadioButton>();
		for(int i=0;i<redeemableProperties.size();i++){
			redeemableRadioButtons.add(new JRadioButton("Property: "+redeemableProperties.get(i).getIdentifier()
					+" Redemption Amount: "+redeemableProperties.get(i).getMortgageAmount()));
		}

		//Create the button for choice confirmation.
	    JButton confirmationButton = new JButton("Confirm");
	    
	    /* 1. Check which buttons are selected
	     * 2. Count the total redemption amount of mortgages selected.
	     * 3. Remove this from players account if player can afford it
	     * 4. Unmortgage all properties that are selected.
	    */
		 confirmationButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            		//This list is used to create a string that will be appended to text area on the GUI
	            		ArrayList<String> redeemedPropertiesToPrint = new ArrayList<String>();
	            		boolean mortgagedSelected = false;
	                    try {	               
	                    	
	                		int totalRedemptionValue = 0;
	                		
	                    	int j=0;
	                    
	                    	while(j < redeemableRadioButtons.size()){
	                    		if(redeemableRadioButtons.get(j).isSelected()){
	                    			
	                    			mortgagedSelected = true;
	                    			
	                    			//Start totalling value of mortgages.
	                    			totalRedemptionValue += redeemableProperties.get(j).getRedeemAmount();
	                    			
	                    			//Find index of property to unmortgage then unmortgage this property in players list.
	                    			int indexToRedeem = player.getProperties().indexOf(redeemableProperties.get(j));
	                    			
	                    			//Unmortgage the correct properties
	                    			player.getProperties().get(indexToRedeem).unmortgage();
	                    			
	                    			//Add property name to list of Stringst that we'll print
	                    			redeemedPropertiesToPrint.add(player.getProperties().get(indexToRedeem).getIdentifier());
	                    			
	                    		}
	                    		j++;
	                    	}
	                    	
	                    	//Make sure player has enough funds to redeem mortgages and has actually chosen one.
	                    	if(player.getNetWorth() >= totalRedemptionValue && mortgagedSelected){
	                    		player.deductFromBalance(totalRedemptionValue);
	                    		history.getTextArea().append("\nRedeeemed Mortgages: "+buildString(redeemedPropertiesToPrint)+" for total: "+totalRedemptionValue+"\n");
		                    	redeemFrame.dispose();//Exit JFrame, player has selected the properties they want to redeem	
	                    	}
	                    	else if(player.getNetWorth() < totalRedemptionValue && mortgagedSelected){
	                    		userAlert.setForeground(Color.red);
	                    		userAlert.setText("Insufficient funds. Choose Less Mortgages.");
	                    	}
						} 
	                    catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}	
	                } 
	            
	      });
		
		 //Add all choices to JPanel
		 JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		 
		 //Add label that contains directions for user onto panel
		 radioPanel.add(userDirections);
		 
		 for(int i=0;i<redeemableProperties.size();i++){
		     radioPanel.add(redeemableRadioButtons.get(i));
		 }
		 
		 //Tell user if they have enough money to mortgage the property.
		 radioPanel.add(userAlert);
		 
		 //Add button below choices
		 radioPanel.add(confirmationButton);
		 
		 radioPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	     redeemFrame.add(radioPanel);
	     redeemFrame.setVisible(true);
	     redeemFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	     redeemFrame.pack();
	     redeemFrame.setLocationRelativeTo(null);
		

	}

	public void auction(Player player){
		//TODO
		Auction auction = new Auction(player.getLocation().getIdentifier());
	}

	public int finishTurn(Player player, int currentPlayerNumber, JLabel characterImage){
		//TODO
		// check for in jail too long, unpaid rent, etc.
		
		//If player is on an ownable property that is unowned and hasn't bought it
		//Force them to auction.
		if(player.getLocation() instanceof Ownable && !boughtProperty && ((Ownable)player.getLocation()).getOwner() == null ){
			history.getTextArea().append("Either 'Buy' or 'Auction' this property before finishing your turn.\n");
		}
		
		else{
				boughtProperty = false;
				
				if (currentPlayerNumber == players.size()-1) {
					currentPlayerNumber = 0;
				} else {
					currentPlayerNumber++;
				}
				try {
					BufferedImage myPicture = ImageIO
							.read(new File(players.get(currentPlayerNumber).getPathForImageIcon()));
					characterImage.setIcon(new ImageIcon(myPicture));
					history.getTextArea()
							.append("Current Player is now: " + players.get(currentPlayerNumber).getName() + "\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		return currentPlayerNumber;
		
	}
	
	private String buildString(ArrayList<String> properties){
		String stringToBuild = "";
		
		if(properties.size() == 1){
			stringToBuild = properties.get(0);
		}
		
		else if(properties.size() > 2){
			for(int i=0;i<properties.size()-2;i++){
				stringToBuild += properties.get(i)+",";
			}
			
			//Insert 'and' between the last two properties
			stringToBuild += properties.get(properties.size()-2)+" and "+properties.get(properties.size()-1);
		}
		
		else if(properties.size() == 2){
			stringToBuild += properties.get(properties.size()-2)+" and "+properties.get(properties.size()-1);

		}
		
		return stringToBuild;
	}
}
