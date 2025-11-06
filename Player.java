import java.util.*;
public class Player {
    private List<Card> hand;
    private double money;
    protected String name;
    private double wager;

    public Player(String name){
        hand=new ArrayList<>();
        this.money=100.0;
        this.name=name;
        this.wager=wager;
    }

    protected int getHandVal(){
        int total=0;
        for(Card card : hand){
            total+=card.getRank().getValue();
        }
        return total;
    }

    protected void addCard(Card card){
        hand.add(card);
    }

    protected void addMoney(double m){money=money+m;}
    protected void loseMoney(double m){money=money-m;}
    protected double getWager(){return wager;}
    protected void setWager(double w){wager=w;}

    protected String printHand(){
        String h="";
        for(Card card : hand){h+=card.getRank();h+=",";}
        //h=h.substring(0,h.length()-1);
        return h;

    }
    protected void clearHand(){hand.clear();}
}
