import java.util.*;

public class main{
    static void print_table(List<Player> Plist,Player dealer){
        for(Player p : Plist){
            String h=p.printHand();
            String d=dealer.printHand();
            System.out.println(p.name+" cards: "+h);   
            System.out.println("dealer has: "+d);
        }
    }

    //Fills list of players with players
    static void initGame(List<Player> players){
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter num of players: ");
            int p = sc.nextInt();
            //Max players is 4
            if(p>4){System.out.println("Players set to 4");p=4;}

            for(int i=0;i<p;i++){
                Scanner sc1 = new Scanner(System.in);
                System.out.println("Enter player name: ");
                String name = sc1.nextLine();
                players.add(new Player(name));
            }
    }

    public static void main(String[] args) {
        Deck deck=new Deck();
        List<Player> players = new ArrayList<>();
        //Very start of game
        initGame(players);

        while(true){
            //ask for wager
            for(Player player : players){
                Scanner sc3=new Scanner(System.in);

                // Can't bet more than balance
                while (true){
                    System.out.println(player.name + ": Balance: " + player.getMoney());
                    System.out.println(player.name + ": how much would you like to wager?");
                    double w = sc3.nextDouble();
                    if (w <= player.getMoney()) {
                        player.setWager(w);
                        break;
                    }
                    System.out.println(player.name + ": not enough money");
                }
            }
            //Start of round
            int round = 0;
            Player dealer=new Player("dealer");
                if(round==0){
                    for(int i=0;i<players.size();i++){
                        players.get(i).addCard(deck.drawCard());
                        print_table(players,dealer);
                    }
                    dealer.addCard(deck.drawCard());
                    
                    for(int i=0;i<players.size();i++){
                        players.get(i).addCard(deck.drawCard());
                        print_table(players,dealer);
                    }
                    dealer.addCard(deck.drawCard());
                }

                //Go through every player until stand or bust
                for(Player player : players){
                    //TODO add split function
                    boolean stand=false;
                    boolean bust=false;
                    while((!stand)&&(!bust)){
                        print_table(players,dealer);
                        Scanner sc2=new Scanner(System.in);
                        System.out.println(player.name+": Would you like to (1) hit, (2) stand, (3) double,(4) split");
                        String choice = sc2.nextLine();
                        if(choice.equals("hit")) {player.addCard(deck.drawCard());;}
                        if(choice.equals("stand")) {stand=true;}
                        
                        // player doubles wager and stands
                        // TODO check if balance is high enough to double
                        if(choice.equals("double")) {player.addCard(deck.drawCard()); player.setWager(player.getWager()*2); stand=true;}
                      
                        if(player.getHandVal()>21){bust=true; System.out.println(player.name + " busts " + player.printHand());}
                    }
                }
                //dealer bust
                boolean db=false;
                if(dealer.getHandVal()>=16){db=true;}
                
                while(!db){
                    dealer.addCard(deck.drawCard());
                    if(dealer.getHandVal()>=16||dealer.getHandVal()==-1){db=true;}
                }
                System.out.println("dealer stops at " + dealer.getHandVal() + " " + dealer.printHand());

                for(Player player : players){
                    // rn if player busted previously they still win
                    // TODO: 
                    // - make the player lose if they busted
                    //   - should be done before dealer gets his cards
                    //   - maybe through a new variable in player.java?
                    // - also ace always = 11?

                    if (dealer.getHandVal()>21) {
                        System.out.println("Dealer bust. " + player.name + " wins. +" + player.getWager());
                        player.addMoney(player.getWager());
                    }

                    if(player.getHandVal()<dealer.getHandVal()){
                        System.out.println(player.name + " loses. -" + player.getWager());
                        player.loseMoney(player.getWager());
                    }
                    if(player.getHandVal()>dealer.getHandVal()){
                        System.out.println(player.name + " wins. +" + player.getWager());
                        player.addMoney(player.getWager());
                    }
                    if(player.getHandVal()==dealer.getHandVal()){
                        System.out.println(player.name + " pushes");
                        // as of right now, wagering doesn't take from total money
                        // player.addMoney(player.getWager());
                    }
                    player.clearHand();
                }
                dealer.clearHand();

            round++; 
        }
    }

}
