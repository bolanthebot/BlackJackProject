package src.BlackJackProject;
public enum Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;

    public int getValue() {
        switch (this) {
            case TWO: return 2;
            case THREE: return 3;
            case FOUR: return 4;
            case FIVE: return 5;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 9;
            case TEN:
            case JACK:
            case QUEEN:
            case KING:
                return 10;
            case ACE:
                return 11; // You can treat Ace as 11 by default
            default:
                throw new IllegalArgumentException("Unknown rank: " + this);
        }
    }

    public int getCount() {
        int value = this.getValue();
        if (value < 7) {
            return 1;
        } else if (value < 10) {
            return 0;
        } else {
            return -1;
        }

    }
}
