package src.BlackJackProject;
import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> hand;

    public Hand() {
        hand = new ArrayList<>();
    }
    // Overloaded constructor
    public Hand(Card card1, Card card2) {
        hand = new ArrayList<>();
        hand.add(card1);
        hand.add(card2);
    }

    protected int getHandVal(){
        int total=0;
        int aceCount=0;
        for(Card card : hand){
            total+=card.getRank().getValue();
            if (card.getRank() == Rank.ACE) {
                aceCount++;
        }
        }
        // If we bust and have Aces, convert them from 11 to 1
        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        return total;
    }

    protected void addCard(Card card){
        hand.add(card);
    }

    protected List<Card> getHand() {
        return hand;
    }

    protected Card handSplit() {
        Card cardToMove = hand.get(1);
        hand.remove(cardToMove);
        return cardToMove;
    }

    @Override
    public String toString() {
        String o = "";
        for (Card card : hand) {
            o+=card; o+=",";
        }
        return o;
    }
    
}
