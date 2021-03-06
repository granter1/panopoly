package panopoly;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.io.FilenameUtils;
import javax.imageio.ImageIO;
import interfaces.Locatable;
import interfaces.Playable;
import locations.NamedLocation;
import locations.PrivateProperty;
import locations.PropertyGroup;


public class Player implements Playable {

	private String name = "";
	private int netWorth = 2500;
	private ArrayList<PrivateProperty> properties = new ArrayList<PrivateProperty>();
	private ArrayList<PropertyGroup> monopolies = new ArrayList<PropertyGroup>();
	private ArrayList<PrivateProperty> mortgages = new ArrayList<PrivateProperty>();
	private int numImprovements = 0;

	private String pathToCharacterIcon = "";
	private Locatable location = null;
	private boolean hasRolled = false;
	private boolean mustDeclareBankruptcy = false;
	private boolean isInJail = false;
	private boolean isPopUpOpen = false;
	private Jail jail;
	private BufferedImage characterIcon;

	public Player(String name,String pathToCharacterIcon) throws IOException, NullPointerException{
		this.name = name;
		this.pathToCharacterIcon = pathToCharacterIcon;
		isInJail = false;
		jail = null;
	}

	public String getName(){
		return name;
	}

	public BufferedImage getIcon() {
		return characterIcon;
	}

	public void setIcon() throws IOException {
		characterIcon = ImageIO.read(new File(this.pathToCharacterIcon));
	}

	public boolean mustDeclareBankruptcy(){
		return mustDeclareBankruptcy;
	}

	public boolean hasRolled() {
		return hasRolled;
	}

	public void setRolled(boolean roll) {
		hasRolled = roll;
	}

	public void setDeclareBankruptcy(boolean bankruptyStatus){
		mustDeclareBankruptcy = bankruptyStatus;
	}

	public int getNetWorth() {
		return netWorth;
	}

	//Property Actions
	public void buyProperty(PrivateProperty target) {
		this.deductFromBalance(target.getPrice());
		target.setOwner(this);
		properties.add(target);
		hasMonopoly(target);
	}

	public void hasMonopoly(PrivateProperty target) {
		boolean monopoly = target.getGroup().hasMonopoly(this);
		if(!monopoly&&monopolies.contains(target.getGroup())) {
			monopolies.remove(target.getGroup());
		}
		else if(monopoly&&!monopolies.contains(target.getGroup())) {
			monopolies.add(target.getGroup());
		}
	}

	//Used for Auctioning
	public void addProperty(PrivateProperty property){
		property.setOwner(this);
		properties.add(property);
		hasMonopoly(property);
	}

	public ArrayList<PrivateProperty> getProperties() {
		return properties;
	}

	public ArrayList<PropertyGroup> getMonopolies(){
		return monopolies;
	}

	public ArrayList<PrivateProperty> getMortgages(){
		return mortgages;
	}

	public String getPathForImageIcon(){
		return this.pathToCharacterIcon;
	}

	public void setPathForImageIcon(String pathToIcon){
		this.pathToCharacterIcon = pathToIcon;
	}

	public Locatable getLocation() {
		return location;
	}

	@Override
	public String getIdentifier() {
		return name;
	}

	public Locatable getNextLoc() {
		return location.getNextLoc();
	}

	public Locatable getPrev() {
		return location.getPrevLoc();
	}
	public void setLocation(Locatable loc) {
		location = loc;
	}

	public void addToBalance(int amount){
		netWorth += amount;
	}

	public void deductFromBalance(int amount){
		netWorth -= amount;
	}

	public void addImprovements(int amount) {
		numImprovements += amount;
	}

	public void removeImprovements(int amount) {
		numImprovements -= amount;
	}

	public int getNumImprovements() {
		return numImprovements;
	}

	public void setName(String filepath){
		name = FilenameUtils.getBaseName(filepath);	
	}
	public void payPlayer(Playable p, int amount) {
		addToBalance(-amount);
		((Player)p).addToBalance(amount);
	}

	public void setInJail(boolean b) {
		isInJail = b;
	}

	public boolean isInJail() {
		return isInJail;
	}

	public void setJail(Jail j) {
		jail = j;
	}

	public Jail getJail() {
		return jail;
	}

	public void revokeOwnership() {
		for(PrivateProperty p:properties) {
			p.reset();
		}
		properties.clear();
		mortgages.clear();
	}

	public void mortgageProperty(PrivateProperty prop) {
		mortgages.add(prop);
		netWorth+=prop.getMortgageAmount();
		prop.mortgage();
		hasMonopoly(prop);
	}

	public void redeemProperty(PrivateProperty prop) {
		mortgages.remove(prop);
		netWorth-=prop.getRedeemAmount();
		prop.unmortgage();
		hasMonopoly(prop);
	}

	public void setPopUpOpen(boolean answer) {
		isPopUpOpen=answer;
	}

	public boolean isPopUpOpen() {
		return isPopUpOpen;
	}

	public int getTotalWorth() {
		int score=netWorth;
		for(PrivateProperty p:properties) {
			score+=(p.getRedeemAmount()+p.getRentalAmount());
		}
		return score;
	}

	public PrivateProperty getRandomProperty(ArrayList<NamedLocation> locations) {
		PrivateProperty prop = null;
		for(int i=0; i<locations.size(); i++) {
			if(locations.get(i) instanceof PrivateProperty && ((PrivateProperty) locations.get(i)).getOwner()==null) {
				prop = ((PrivateProperty) locations.get(i));
			}
			if(prop!=null) {
				addProperty(prop);
			}

		}
		return prop;
	}

}

