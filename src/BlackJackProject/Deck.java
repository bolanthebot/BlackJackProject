package src.BlackJackProject;
import java.util.*;
public class Deck {
    private List<Card> cards;
    private int numDecks=1;
    private int count=0;

    public Deck(){
        cards=new ArrayList<>();
        createDeck();
        shuffle();
    }

    private void createDeck(){
        for(int i=0;i<numDecks;i++)
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public Card drawCard(){
        if(numDecks/3>cards.size()){shuffle();}
        if(cards.size()==0){shuffle();}

        Card drawnCard = cards.remove(cards.size()-1);

        // update count when drawing a card
        updateCount(drawnCard);
        return drawnCard;
    }

    private void updateCount(Card c) {this.count += c.getCount();}
    private void resetCount() {count = 0;}
    public int getCount() {return count;}

    public void shuffle(){
        Collections.shuffle(cards);
        resetCount();
        System.out.println("shuffled");
    }
    
    public int size(){return cards.size();}
}
