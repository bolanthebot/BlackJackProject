package src.BlackJackProject;

public class Card {
    private Rank rank;
    private Suit suit;
    private final int count;

    public Card(Suit suit,Rank rank){
        this.suit=suit;
        this.rank=rank;
        this.count=rank.getCount();
    }

    public Suit getSuit() {
        return suit;
    }

    public int getCount(){return count;}

    public Rank getRank() {
        return rank;
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        return rank.toNum()+"_of_"+getSuit();
    }
}