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
                        if(choice.equals("hit")){player.addCard(deck.drawCard());}
                        if(choice.equals("stand")){stand=true;}
                        if(player.getHandVal()>21){bust=true;}
                    }
                }
                boolean db=false;
                if(dealer.getHandVal()>=16){db=true;}
                
                while(!db){
                    dealer.addCard(deck.drawCard());
                    if(dealer.getHandVal()>=16||dealer.getHandVal()==-1){db=true;}
                }

                for(Player player : players){
                    if(player.getHandVal()>dealer.getHandVal()){
                        player.loseMoney(player.getWager());
                    }
                    if(player.getHandVal()<dealer.getHandVal()){
                        player.addMoney(player.getWager()*2);
                    }
                    if(player.getHandVal()==dealer.getHandVal()){
                        player.addMoney(player.getWager());
                    }
                    player.clearHand();
                }
                dealer.clearHand();

            round++; 
        }
    }

}
