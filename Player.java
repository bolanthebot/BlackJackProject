import java.util.*;
public class Player {
    private List<Card> hand;
    private double money;
    protected String name;
    private double wager;
    private boolean isSplitPlayer;
    private List<Player> splits;

    public Player(String name){
        hand=new ArrayList<>();
        this.money=100.0;
        this.name=name;
        this.isSplitPlayer=false;
        this.splits=new ArrayList<>();
    }

    protected int getHandVal(){
        int total=0;
        for(Card card : hand){
            total+=card.getRank().getValue();
        }
        return total;
    }

    protected boolean IsSplitPlayer(){return isSplitPlayer;}
    protected void setSplitPlayer(boolean tf){isSplitPlayer=tf;}

    protected void addCard(Card card){
        hand.add(card);
    }

    protected void addMoney(double m){money=money+m;}
    protected void loseMoney(double m){money=money-m;}
    protected double getMoney(){return money;}

    protected double getWager(){return wager;}
    protected void setWager(double w){wager=w;}

    protected List<Card> getHand(){return hand;}

    protected String printHand(){
        String h="";
        for(Card card : hand){h+=card;h+=",";}
        //h=h.substring(0,h.length()-1);
        return h;

    }
    protected void clearHand(){hand.clear();}
}
