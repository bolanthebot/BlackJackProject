import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> hand;

    public Hand() {
        hand = new ArrayList<>();
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

    protected void clearHand(){
        hand.clear();
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
