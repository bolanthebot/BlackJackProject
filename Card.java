public class Card {
    private Rank rank;
    private Suit suit;
    private int count;

    public Card(Suit suit,Rank rank){
        this.suit=suit;
        this.rank=rank;

    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public int getC() {
        return count;
    }
}