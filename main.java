import java.util.*;

public class main {
    static void print_table(List<Player> Plist, Player dealer) {
        String d = dealer.printHand();
        for (Player p : Plist) {
            String h = p.printHand();
            System.out.println(p.name + " cards: " + h);
        }
        System.out.println("dealer has: " + d);
    }

    // Fills list of players with players
    static void initGame(List<Player> players) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter num of players: ");
        int p = sc.nextInt();
        // Max players is 4
        if (p > 4) {
            System.out.println("Players set to 4");
            p = 4;
        }

        for (int i = 0; i < p; i++) {
            Scanner sc1 = new Scanner(System.in);
            System.out.println("Enter player name: ");
            String name = sc1.nextLine();
            players.add(new Player(name));
        }
    }

    public static void main(String[] args) {
        Deck deck = new Deck();
        List<Player> players = new ArrayList<>();
        // Very start of game
        initGame(players);

        while (true) {
            // ask for wager
            for (Player player : players) {
                Scanner sc3 = new Scanner(System.in);

                // Can't bet more than balance
                while (true) {
                    System.out.println(player.name + ": Balance: " + player.getMoney());
                    System.out.println(player.name + ": how much would you like to wager?");
                    double w = sc3.nextDouble();
                    if (w > player.getMoney()) {
                        System.out.println("Wager above balance");
                        continue;
                    }
                    if (w <= player.getMoney()) {
                        player.setWager(w);
                        player.loseMoney(w);
                        break;
                    }
                    System.out.println(player.name + ": not enough money");
                }
            }
            // Start of round
            int round = 0;
            Player dealer = new Player("dealer");
            if (round == 0) {
                for (Player player : players) {
                    player.addHand();
                    System.out.println(player.name);
                    
                    for (Hand hand : player.getHands()) {
                        hand.addCard(deck.drawCard());
                        print_table(players,dealer);
                    }
                }
                dealer.addHand(); // dealer gets first and only hand
                dealer.getFirstHand().addCard(deck.drawCard());
                System.out.println("Dealer: ");
                System.out.println(dealer.getHands());

                for (Player player : players) {
                    System.out.println(player.name);
                    for (Hand hand : player.getHands()) {
                        hand.addCard(deck.drawCard());
                        print_table(players,dealer);
                    }
                }
                dealer.getFirstHand().addCard(deck.drawCard());
                System.out.println("Dealer: ");
                System.out.println(dealer.getHands().get(0));

            }

            // Go through every player until stand or bust
            for (Player player : players) {
                ListIterator<Hand> it = player.getHands().listIterator(); // to modify list during iteration
                while (it.hasNext()) {
                    int turn = 0;
                    Hand hand = it.next();
                    boolean stand = false;
                    boolean bust = false;
                    // amount of splits for naming purposes
                    Integer i = 0;

                    while ((!stand) && (!bust)) {
                        turn++;
                        print_table(players, dealer);
                        Scanner sc2 = new Scanner(System.in);
                        System.out.println(player.name + ": Would you like to (1) hit, (2) stand, (3) double,(4) split");
                        String choice = sc2.nextLine();
                        if (choice.equals("hit")) {
                            hand.addCard(deck.drawCard());
                        }
                        if (choice.equals("stand")) {
                            stand = true;
                        }

                        // player doubles wager and stands
                        if (choice.equals("double")) {
                            if (turn != 1) {
                                System.out.println("Can only double on turn 1");
                            } else if (player.getWager() > player.getMoney()) {
                                System.out.println("Not enough Money to double");
                            } else {
                                hand.addCard(deck.drawCard());
                                player.setWager(player.getWager() * 2);
                                stand = true;
                            }
                        }

                        // split logic
                        if (choice.equals("split")) {
                            if (turn != 1) {
                                System.out.println("Can only split on turn 1");
                            } else if (hand.getHand().get(0).getRank() != hand.getHand().get(1).getRank()) {
                                System.out.println("Have to be same card rank");
                            } else if (player.getWager() > player.getMoney()) {
                                System.out.println("Not enough Money to split");
                            } else {
                                // some weird iterator stuff I had to do to iterate over newly added items
                                it.add(new Hand(hand.handSplit(), deck.drawCard()));
                                it.previous();
                                hand.addCard(deck.drawCard());
                                i++;
                            }
                        }

                        // bust logic
                        if (hand.getHandVal() > 21) {
                            bust = true;
                            System.out.println(player.name + " busts " + player.printHand());
                        }
                    }
                }
            }
            // dealer bust
            boolean db = false;
            // dealer stand
            boolean ds = false;
            if (dealer.getFirstHand().getHandVal() >= 16 && dealer.getFirstHand().getHandVal() <= 21) {
                ds = true;
            }

            while (!db && !ds) {
                dealer.getFirstHand().addCard(deck.drawCard());
                if (dealer.getFirstHand().getHandVal() >= 16 || dealer.getFirstHand().getHandVal() == -1) {
                    db = true;
                }
            }
            System.out.println("dealer stops at " + dealer.getFirstHand().getHandVal() + " " + dealer.printHand());

            for (Player player : players) {
                for (Hand hand : player.getHands()) {
                    // TODO:
                    // ace always = 11?

                    if (dealer.getFirstHand().getHandVal() > 21) {
                        if (hand.getHandVal() <= 21) {
                            System.out.println(
                                    "Dealer bust. " + player.name + " wins " + hand + "; +" + player.getWager());
                            player.addMoney(player.getWager());
                        }
                    }

                    else if (hand.getHandVal() < dealer.getFirstHand().getHandVal()) {
                        System.out.println(player.name + " loses " + hand + "; +" + player.getWager());
                    } else if (hand.getHandVal() > dealer.getFirstHand().getHandVal() && hand.getHandVal() <= 21) {
                        System.out.println(player.name + " wins " + hand + "; +" + player.getWager());
                        player.addMoney(player.getWager() * 2);
                    } else if (hand.getHandVal() == dealer.getFirstHand().getHandVal()) {
                        System.out.println(player.name + " pushes " + hand);
                        player.addMoney(player.getWager());
                    }

                }
                player.clearHands();
            }
            dealer.clearHands();

            round++;
        }
    }

}
