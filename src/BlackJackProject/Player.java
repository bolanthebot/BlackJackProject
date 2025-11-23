package src.BlackJackProject;
import java.util.*;
public class Player {
    private List<Hand> hands;
    private double money;
    protected final String name;
    private double wager;
    private int currHand;
    private int turn;

    public Player(String name){
        hands=new ArrayList<>();
        this.money=100.0;
        this.name=name;
        this.currHand=0;
        turn =1;
    }

    protected void addMoney(double m){money=money+m;}
    protected void loseMoney(double m){money=money-m;}
    protected double getMoney(){return money;}

    protected double getWager(){return wager;}
    protected void setWager(double w){wager=w;}

    protected List<Hand> getHands(){return hands;}
    protected Hand getFirstHand(){return hands.get(0);} // for dealer
    protected Hand getCurrHand(){return hands.get(currHand);}
    protected int getCurrHandIn(){return currHand;}
    protected void nextHand(){currHand++;}
    protected int getTurn(){return turn;}

    protected void addHand() {hands.add(new Hand());}

    protected String printHand(){
        String h="";
        for(Hand hand : hands){h+=hand;h+=";";}
        return h;
    }

    protected void clearHands(){hands.clear();}
}
