import java.util.*;
public class Deck {
    private List<Card> cards;
    private int numDecks=1;

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
        return cards.remove(cards.size()-1);
    }

    public void shuffle(){
        Collections.shuffle(cards);
        System.out.println("shuffled");
    }
    
    public int size(){return cards.size();}


}
