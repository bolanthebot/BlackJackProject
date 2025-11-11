package src.BlackJackProject;
public enum Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES;

    @Override
    public String toString() {
        switch (this) {
            case HEARTS:   return "hearts";
            case DIAMONDS: return "diamonds";
            case CLUBS:    return "clubs";
            case SPADES:   return "spades";
            default:       return super.toString();
        }
    }
}