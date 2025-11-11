package src.BlackJackProject;

public class Card {
    private Rank rank;
    private Suit suit;
    public final int count;

    public Card(Suit suit,Rank rank){
        this.suit=suit;
        this.rank=rank;
        this.count=rank.getCount();
    }

    public Suit getSuit() {
        return suit;
    }

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