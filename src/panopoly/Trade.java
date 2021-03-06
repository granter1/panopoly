package panopoly;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import locations.PrivateProperty;

/* 
 * This class works by iterating through panels
 * on the same JFrame.
 * 1. Player is presented with property choices to trade, panel is disposed of.
 * 2. Player then selects the asset they want to trade for, panel is disposed of.
 * 3. Player then selects the player they want to trade with, panel is disposed of.
 * 4. The chosen player has to accept the trade. If rejected, return to step 3, if accepted 
 * dispose of panel and continue
 * 5. Chosen player then enters the amount/property they'll trade for. If accepted by current player
 * the trading of assets takes place and trading is complete. If rejected continue.
 * 6. Return to step 3.
 */
public class Trade extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();

	private JFrame tradingHouse = new JFrame("Trading house");
	private Player player;
	private ArrayList<Player> players;
	private HistoryLog history;

	//Components for first panel
	private JPanel propertyPanel = new JPanel(new GridLayout(0,1));
	private JLabel propertyDirections = new JLabel("Choose which properties you want to trade then hit 'Confirm'");
	private JPanel propertyRadioButtonPanel = new JPanel(new GridLayout(0,3));
	private ArrayList<JRadioButton> propertyRadioButtons = new ArrayList<JRadioButton>();
	private ArrayList<String> propertiesWishingToTrade = new ArrayList<String>();

	//Components for second panel
	private JPanel assetsPanel = new JPanel(new GridLayout(0,1));
	private JLabel assetsDirections = new JLabel("Choose what asset you'd like to trade for.");
	private JRadioButton propertyChoice = new JRadioButton("Property");
	private JRadioButton cashChoice = new JRadioButton("Cash");
	private ButtonGroup assetButtonGrouping = new ButtonGroup();
	private boolean cashChosen = false;
	private boolean propertyChosen = false;
	
	//Components for third panel
	private JPanel playerChoicesPanel = new JPanel(new GridLayout(0,1));
	private JLabel playerChoiceDirections = new JLabel("Choose what player you'd like to trade with.");
	private JPanel playerRadioButtonPanel = new JPanel(new GridLayout(0,1));
	private ArrayList<JRadioButton> playerRadioButtons = new ArrayList<JRadioButton>();
	private ButtonGroup playerButtonGroup = new ButtonGroup();
	
	//Components for fourth panel
	private JLabel acceptOrDeclineQuestion = new JLabel();
	private JPanel playerOfferPanel = new JPanel(new GridLayout(0,1));
	
	//Components for fifth panel.
	private JLabel tradingQuestion = new JLabel();
	private JPanel tradingPanel = new JPanel(new GridLayout(0,1));
	private JTextArea cashBidArea = new JTextArea();
	private ArrayList<JRadioButton> propertiesOpponentMayTrade = new ArrayList<JRadioButton>();
	private int bid = 0;
	
	//Components for sixth panel.
	private JLabel acceptDeclineOpponentsOffer = new JLabel();
	private JPanel acceptDeclineOpponentsPanel = new JPanel(new GridLayout(0,1));
	private ArrayList<String> propertiesOpponentWantsToTrade = new ArrayList<String>();
	

	//Button panel is common to all panels.
	private JPanel buttonPanel = new JPanel(new FlowLayout());
	private JButton confirmationButton = new JButton("Confirm");
	private JButton cancelButton = new JButton("Cancel");
	
	//Control flow of panels using booleans.
	private boolean propertiesChosen = false;
	private boolean assetsChosen = false;
	private boolean playerChosen = false;
	private boolean playerAccepted = false;
	private boolean offerGiven = false;
	private boolean offerAccepted = false;
	
	//Information to complete trade.
	String playerToTradeWith = "";
	

	public Trade(Player player, ArrayList<Player> players, HistoryLog history){
				
		this.player = player;
		this.players = players;
		this.history = history;
		
		addConfirmationActionListener();
		addCancelButtonActionListener();
		
		updateFrame();
		
		/* 
		 * Add the initial property panel.
		 * This will be swapped out later
		 * for the other panels described above.
		 */
        tradingHouse.setLocation((int)(dimensions.getWidth()/4.5),(int)(dimensions.getHeight()/2.25));
        tradingHouse.setVisible(true);
        tradingHouse.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        tradingHouse.setResizable(true);
        tradingHouse.pack();
		
		
	}
	
	private void createPropertyPanel(Player player){
		
		//Add question for user.
		propertyPanel.add(propertyDirections);
		
		//Add a radio button for each property.
		for(int i=0;i<player.getProperties().size();i++){
			propertyRadioButtons.add(new JRadioButton(player.getProperties().get(i).getIdentifier()));
			propertyRadioButtonPanel.add(propertyRadioButtons.get(i));
		}
		
		//Create panel of buttons.
		buttonPanel.add(confirmationButton);
		buttonPanel.add(cancelButton);
		
		//Add the remaining components
		propertyPanel.add(propertyRadioButtonPanel);
		propertyPanel.add(buttonPanel);
		
	}
	
	private void createAssetsPanel(){
		
		//Add user directions.
		assetsPanel.add(assetsDirections);
		
		//Add buttons to group so only one is selectable
		assetButtonGrouping.add(propertyChoice);
		assetButtonGrouping.add(cashChoice);
		
		//Add the buttons to the panel
		assetsPanel.add(propertyChoice);
		assetsPanel.add(cashChoice);
		
		
		//Add the final component to the button panel
		assetsPanel.add(buttonPanel);
		
	}
	
	private void createPlayerChoicesPanel(){
		
		//Add user directions
		playerChoicesPanel.add(playerChoiceDirections);
		
		//Add a players name to each radio button
		for(int i=0;i<players.size();i++){
			
			//Make sure we don't add the player wishing to trade.
			if(!players.get(i).getIdentifier().equals(player.getIdentifier())){
				playerRadioButtons.add(new JRadioButton(players.get(i).getIdentifier()));
			}
		}
		
		//Add radio buttons to their panel and to a group so only one can be chosen.
		for(int i=0;i<playerRadioButtons.size();i++){
			playerRadioButtonPanel.add(playerRadioButtons.get(i));
			playerButtonGroup.add(playerRadioButtons.get(i));
		}
		
		playerChoicesPanel.add(playerRadioButtonPanel);
		
		playerChoicesPanel.add(buttonPanel);
			
	}
	
	private void createPresentOfferPanel(){
		
		String assetWanted = "";
		
		if(cashChosen){
			assetWanted = "cash";
		}
		else if(propertyChosen){
			assetWanted = "property";
		}
	
		
		acceptOrDeclineQuestion.setText(playerToTradeWith+", "+player.getIdentifier()+" wants to trade the following for "+assetWanted);
		//Add Question
		playerOfferPanel.add(acceptOrDeclineQuestion);
		
		//Present player with properties the player wants to trade.
		for(int i=0;i<propertiesWishingToTrade.size();i++){
			playerOfferPanel.add(new JLabel(propertiesWishingToTrade.get(i)));
		}
		
		//Change button text for clarity
		confirmationButton.setText("ACCEPT");
		cancelButton.setText("REJECT");
		
		playerOfferPanel.add(buttonPanel);
		
	}
	
	/*
	 * If the player accepts the offer they now say
	 * how much they'll offer the player wanting to trade.
	 */
	private void createPlayerBidPanel(){
		
		if(cashChosen){
			tradingQuestion.setText("What do you want to offer for these properties?");
			tradingPanel.add(tradingQuestion);
			tradingPanel.add(cashBidArea);
		}
		
		else if(propertyChosen){
			
			tradingPanel.add(tradingQuestion);
			tradingQuestion.setText("What properties do you want to trade?");
			
			for(int i=0;i<players.get(getPlayersIndex()).getProperties().size();i++){
				propertiesOpponentMayTrade.add(new JRadioButton(players.get(getPlayersIndex()).getProperties().get(i).getIdentifier()));
				tradingPanel.add(propertiesOpponentMayTrade.get(i));
			}
			
		}
		
		confirmationButton.setText("Confirm");
		cancelButton.setText("Cancel");
	
		tradingPanel.add(buttonPanel);
	}
	
	private void createAcceptDeclineOpponentsOfferPanel(){
		
		if(cashChosen){
			acceptDeclineOpponentsOffer.setText(player.getIdentifier()+", "+playerToTradeWith+" is offering "
					+bid+". Accept or Reject");
			acceptDeclineOpponentsPanel.add(acceptDeclineOpponentsOffer);
		}
		else if(propertyChosen){
			acceptDeclineOpponentsOffer.setText(playerToTradeWith+" has offered the following properties for "+
					" your offered properties: "+String.join(", ", propertiesOpponentWantsToTrade));
			acceptDeclineOpponentsPanel.add(acceptDeclineOpponentsOffer);
		}
		
		confirmationButton.setText("Accept");
		cancelButton.setText("Reject");
		
		acceptDeclineOpponentsPanel.add(buttonPanel);
		
	}
	
	private void updateFrame(){
		if(!offerAccepted){
			if(!offerGiven){
				if(!playerAccepted){
					if(!playerChosen){
						if(!assetsChosen){
							if(!propertiesChosen){
								
								createPropertyPanel(player);
								tradingHouse.add(propertyPanel);
								tradingHouse.revalidate();
								tradingHouse.repaint();
								tradingHouse.pack();
								
							}else{//Properties chosen, set up assets
								
								/*
								 * Create a list of properties they want to trade.
								 */
								createAssetsPanel();
								tradingHouse.remove(propertyPanel);
								tradingHouse.add(assetsPanel);
								tradingHouse.revalidate();
								tradingHouse.repaint();
								tradingHouse.pack();
								
								
							}
						}else{//Assets chosen, set up player to offer to
							
							/*
							 * Create a list of players to choose from
							 * that a player wants to trade with.
							 */
							clearInformationTo(1);
							createPlayerChoicesPanel();
							tradingHouse.remove(assetsPanel);
							tradingHouse.add(playerChoicesPanel);
							tradingHouse.revalidate();
							tradingHouse.repaint();
							tradingHouse.pack();
						}
					}else{//Player chosen, ask player to accept
						
						createPresentOfferPanel();
						tradingHouse.remove(playerChoicesPanel);
						tradingHouse.add(playerOfferPanel);
						tradingHouse.revalidate();
						tradingHouse.repaint();
						tradingHouse.pack();
						
					}
				}else{//Player has accepted or declined, proceed to players offer or return to choosing another
					
					createPlayerBidPanel();
					tradingHouse.remove(playerOfferPanel);
					tradingHouse.add(tradingPanel);
					tradingHouse.revalidate();
					tradingHouse.repaint();
					tradingHouse.pack();
					
				}
			}else{//Chosen player has given offer, proceed to accepting/declining
				
				createAcceptDeclineOpponentsOfferPanel();
				tradingHouse.remove(tradingPanel);
				tradingHouse.add(acceptDeclineOpponentsPanel);
				tradingHouse.revalidate();
				tradingHouse.repaint();
				tradingHouse.pack();
				
			}
		}else{//Offer has been accepted/declined. Accept -> dispose of frame. Decline -> Return to choosing another player
			
			tradingHouse.dispose();			
			tradeAssets();
			
		}
	}
	
	
	
	private void addConfirmationActionListener(){
		confirmationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//Check player has chosen properties and update list and JFrame.
				if(!propertiesChosen) {
					if(findPropertiesWishingToTrade()){
						propertiesChosen = true;
						updateFrame();
					}
				}
				
				else if(!assetsChosen){
					if(assetChoiceSelected()){
						assetsChosen = true;
						updateFrame();
					}
				}
				
				else if(!playerChosen){
					if(playerButtonSelected()){
						playerChosen = true;
						updateFrame();
					}
				}
				
				else if(!playerAccepted){
						playerAccepted = true;
						updateFrame();
				}
				
				else if(!offerGiven){
						
					if(cashChosen){
						if(validBid()){
							offerGiven = true;
							updateFrame();
						}
					}
					
					else if(propertyChosen){
						if(hasOpponentChosenProperties()){
							offerGiven = true;
							updateFrame();
						}
					}
						
				}
						
				else if(!offerAccepted){
							offerAccepted = true;
							updateFrame();
					}
				}
			
		});
	}
	
	private void addCancelButtonActionListener(){
		
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(!offerAccepted){
					if(!offerGiven){
						if(!playerAccepted){
							if(!playerChosen){
								if(!assetsChosen){
									if(!propertiesChosen){
										tradingHouse.dispose();
									}
									else{
										propertiesChosen = false;
										clearInformationTo(0);
										tradingHouse.remove(assetsPanel);
										updateFrame();
									}
								}else{
		
									assetsChosen = false;
									clearInformationTo(1);
									tradingHouse.remove(playerChoicesPanel);
									updateFrame();
									
								}
							}else{
								
								playerChosen = false;
								clearInformationTo(2);
								tradingHouse.remove(playerOfferPanel);
								updateFrame();
								
							}
						}else{
							
							playerAccepted = false;
							clearInformationTo(3);
							tradingHouse.remove(tradingPanel);
							updateFrame();
							
						}
					}else{
						
						offerGiven = false;
						clearInformationTo(4);
						tradingHouse.remove(acceptDeclineOpponentsPanel);
						updateFrame();
						
					}	
				}
			}
		});
			
	}
	
	/*
	 * Executed when both parties to the trade accept.
	 * Bid from player is put into the requesting players 
	 * bank account.
	 * The player that bid will recieve the properties that 
	 * were offered up.
	 */
	private void tradeAssets(){
				
		/*
		 Iterate through all the properties the player wishes to trade.
		 Match by the identifiers.
		 If it matches, remove it and add it to the properties of the player that accepted offer.
		 */
		if(cashChosen){
			for(int i=0;i<propertiesWishingToTrade.size();i++){
				for(int j=0;j<player.getProperties().size();j++){
					
					if(propertiesWishingToTrade.get(i).equals(player.getProperties().get(j).getIdentifier())){
						PrivateProperty propertyToTrade = player.getProperties().get(j);
						players.get(getPlayersIndex()).addProperty(propertyToTrade);
						player.getProperties().remove(j);
						System.out.println("Removed "+propertyToTrade.getIdentifier());
					}
					
				}
			}
			
			//Trade the money
			players.get(getPlayersIndex()).deductFromBalance(bid);
			player.addToBalance(bid);
			history.getTextArea().append("-> "+player.getIdentifier()+" traded "+String.join(",", propertiesWishingToTrade)+" with "+playerToTradeWith+" for "+bid+"\n\n");

		}
		
		else if(propertyChosen){
			
			//Move properties from current player to opponent
			for(int i=0;i<propertiesWishingToTrade.size();i++){
				for(int j=0;j<player.getProperties().size();j++){
					
					if(propertiesWishingToTrade.get(i).equals(player.getProperties().get(j).getIdentifier())){
						PrivateProperty propertyToTrade = player.getProperties().get(j);
						players.get(getPlayersIndex()).addProperty(propertyToTrade);
						player.getProperties().remove(j);
					}
					
				}
			}
			
			//Move properties from opponent to current player.
			for(int i=0;i<propertiesOpponentWantsToTrade.size();i++){
				for(int j=0;j<players.get(getPlayersIndex()).getProperties().size();j++){
					
					if(propertiesOpponentWantsToTrade.get(i).equals(players.get(getPlayersIndex()).getProperties().get(j).getIdentifier())){
						PrivateProperty propertyToTrade = players.get(getPlayersIndex()).getProperties().get(j);
						player.addProperty(propertyToTrade);
						players.get(getPlayersIndex()).getProperties().remove(j);
					}
				}
			}
			history.getTextArea().append("-> "+player.getIdentifier()+" has traded "+String.join(",", propertiesWishingToTrade)+" for "+
			String.join(", ", propertiesOpponentWantsToTrade)+" with "+playerToTradeWith+"\n\n");

		}
			
	}
	
		
	
	
	private boolean hasOpponentChosenProperties(){
		boolean chosenPropertiesToTrade = false;
		
		for(int i=0;i<propertiesOpponentMayTrade.size();i++){
			if(propertiesOpponentMayTrade.get(i).isSelected()){
				
				chosenPropertiesToTrade = true;
				
				propertiesOpponentWantsToTrade.add(propertiesOpponentMayTrade.get(i).getText());
				
			}
		}
		
		return chosenPropertiesToTrade;
	}
	
	private boolean findPropertiesWishingToTrade(){
		for(int i=0;i<propertyRadioButtons.size();i++){
			if(propertyRadioButtons.get(i).isSelected()){
				propertiesWishingToTrade.add(propertyRadioButtons.get(i).getText());
			}
		}
		
		return propertiesWishingToTrade.size() > 0;
	}
	
	private boolean assetChoiceSelected(){
		boolean chosenAnAsset = false;
		
		if(propertyChoice.isSelected()){
			propertyChosen = true;
			chosenAnAsset = true;
		}
		
		else if(cashChoice.isSelected()){
			cashChosen = true;
			chosenAnAsset = true;
		}
		
		return chosenAnAsset;
		
	}
	
	private boolean playerButtonSelected(){
		
		boolean playerChosen = false;
		int i=0;
		
		/*
		 * If a player is selected, get their name.
		 */
		while(i<playerRadioButtons.size() && !playerChosen){
			if(playerRadioButtons.get(i).isSelected()){
				playerChosen  = true;
				playerToTradeWith = playerRadioButtons.get(i).getText();
			}
			else{
				i++;
			}
			
		}
		
		return playerChosen;
	}
	
	private boolean validBid(){
		
		boolean validBid = false;
		
		try{
			
			 bid = Integer.parseInt(cashBidArea.getText());
			 
			 //Make sure player can afford the bid.
			 if(players.get(getPlayersIndex()).getNetWorth() < bid){
				 cashBidArea.setText("Your balance is only: "+players.get(getPlayersIndex()).getNetWorth());
			 }
			 else{
				 validBid = true;
			 }
			 
		}catch(Exception e){
			
			cashBidArea.setText("Not a valid bid. Enter an Integer. If you entered an Integer, "
					+ "it may have been out of range.");
			
		}
		return validBid;
	}
	
	private int getPlayersIndex(){
		int index=0;
		boolean indexFound = false;
		
		//Find players index that we want to trade with by name.
		while(!indexFound){
			if(!players.get(index).getIdentifier().equals(playerToTradeWith)){
					index++;
			}
			else{
				indexFound = true;
			}
		}
		
		return index;
			
	}
	
	private void clearInformationTo(int stepOfProcess){
		
	
		switch(stepOfProcess){
		case 0:
			assetsPanel.removeAll();
			propertiesWishingToTrade.clear();
			propertyChosen = false;
			cashChosen = false;
			break;
		case 1:
			playerChoicesPanel.removeAll();	
			playerRadioButtonPanel.removeAll();
			playerRadioButtons.clear();
			break;
		case 2:		
			playerOfferPanel.removeAll();
			playerOfferPanel.remove(acceptOrDeclineQuestion);
			confirmationButton.setText("Confirm");
			cancelButton.setText("Cancel");
			break;
		case 3:
			tradingPanel.removeAll();
			propertiesOpponentMayTrade.clear();
			break;
		case 4:
			acceptDeclineOpponentsPanel.removeAll();
			propertiesOpponentWantsToTrade.clear();
			confirmationButton.setText("Confirm");
			cancelButton.setText("Cancel");
			break;
		}
		
	}
	
}
	
	
	

