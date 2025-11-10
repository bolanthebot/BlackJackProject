import java.util.*;
public class Player {
    private List<Hand> hands;
    private double money;
    protected String name;
    private double wager;

    public Player(String name){
        hands=new ArrayList<>();
        this.money=100.0;
        this.name=name;
    }

    protected void addMoney(double m){money=money+m;}
    protected void loseMoney(double m){money=money-m;}
    protected double getMoney(){return money;}

    protected double getWager(){return wager;}
    protected void setWager(double w){wager=w;}

    protected List<Hand> getHands(){return hands;}
    protected Hand getFirstHand(){return hands.get(0);} // for dealer

    protected void addHand() {hands.add(new Hand());}

    protected String printHand(){
        String h="";
        for(Hand hand : hands){h+=hand;h+=";";}
        return h;
    }

    protected void clearHands(){hands.clear();}
}
