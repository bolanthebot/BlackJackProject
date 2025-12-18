package src.BlackJackProject;
import java.util.function.Supplier;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Main extends Application{
    private Deck deck;
    private Player dealer;
    private List<Player> players;
    private int currentHand;

    private Pane table;         // where card images are placed
    private VBox controls;      // buttons and info
    private Label messageLabel;
    private StackPane tableContainer;


    private Button hitBtn, standBtn, splitBtn, doubleBtn;
    private int wager = 10;
    private Label wagerLabel;
    private Button increaseBtn;
    private Button decreaseBtn;
    private Button confirmWagerBtn;
    private int currentPlayer = 0;
    private Map<Card, ImageView> cardImages = new HashMap<>();
    // starting deck area (for animation start position)
    private final double deckX = 400, deckY = 80;
    int turn;


    @Override
    public void start(Stage stage) {
        turn=1;
        deck = new Deck();
        dealer = new Player("Dealer");
        players = new ArrayList<>();
        players.add(new Player("Player"));
        players.add(new Player("BasicBot"));
        players.add(new Player("SmartBot"));
        players.add(new Player("CheaterBot"));
        currentHand=0;
        
        // GUI layout
        BorderPane root = new BorderPane();
        table = new Pane();
        table.setPrefSize(800, 400);
        table.setStyle("-fx-background-color: darkgreen;");

        // Message label overlay
        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        messageLabel.setOpacity(0);

        // StackPane to overlay label on top of table
        tableContainer = new StackPane(table, messageLabel);
        tableContainer.setPrefSize(800, 400);
        root.setCenter(tableContainer);
        StackPane.setAlignment(messageLabel, Pos.TOP_LEFT);
        //messageLabel.setPadding(new Insets(15));

        table.setPrefSize(800, 400);
        table.setStyle("-fx-background-color: darkgreen;");

        hitBtn = new Button("Hit");
        standBtn = new Button("Stand");
        splitBtn=new Button("Split");
        doubleBtn=new Button("Double");

        // --- Wager Controls ---
        Label wagerText = new Label("Wager: $");
        wagerLabel = new Label(String.valueOf(wager));
        wagerLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-font-size: 18;");

        increaseBtn = new Button("+");
        decreaseBtn = new Button("-");
        confirmWagerBtn = new Button("Confirm Bet");

        increaseBtn.setOnAction(e -> {
            wager += 5;
            updateWagerLabel();
        });

        decreaseBtn.setOnAction(e -> {
            if (wager > 10) {
                wager -= 5;
                updateWagerLabel();
            }
        });

        confirmWagerBtn.setOnAction(e -> confirmWager());
        HBox wagerBox = new HBox(10, wagerText, wagerLabel, decreaseBtn, increaseBtn, confirmWagerBtn);
        wagerBox.setAlignment(Pos.CENTER);
        wagerBox.setPadding(new Insets(10));
        wagerBox.setStyle("-fx-background-color: darkgreen;");

        controls = new VBox(10, wagerBox, new HBox(10, hitBtn, standBtn, splitBtn, doubleBtn));
        controls.setAlignment(Pos.CENTER);

        controls.setPadding(new Insets(10));

        root.setBottom(controls);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Blackjack (Animated)");
        stage.setScene(scene);
        stage.show();

        // button actions
        hitBtn.setOnAction(e -> playerHit());
        standBtn.setOnAction(e -> playerStand());
        doubleBtn.setOnAction(e -> playerDouble());
        //splitBtn.setOnAction(e -> playerSplit());

        setButtonsEnabled(false);
    }

    private void startRound() {
        increaseBtn.setDisable(true);
        decreaseBtn.setDisable(true);
        confirmWagerBtn.setDisable(true);

        table.getChildren().clear();
        dealer.clearHands();
        for (Player p : players) p.clearHands();
        
        dealer.addHand();
        for (Player p : players) p.addHand();

        setButtonsEnabled(false);
        showMessage("Starting new round...");

        // Sequentially deal cards to each player and dealer
        SequentialTransition dealAnim = new SequentialTransition();

        for (int round = 0; round < 2; round++) {
            for (Player p : players) {
                Card c = deck.drawCard();
                p.getFirstHand().addCard(c);
                dealAnim.getChildren().add(dealCardAnimation(c, playerCardX(p), playerCardY(p, round)));
            }
            // dealer
            Card dc = deck.drawCard();
            dealer.getFirstHand().addCard(dc);
            dealAnim.getChildren().add(dealCardAnimation(dc, dealerCardX(), dealerCardY(round)));
        }

        dealAnim.setOnFinished(e -> {
            setButtonsEnabled(true);
            showMessage("Your turn!");
        });

        dealAnim.play();
    }

    private void playerDouble(){
        Player player = players.get(currentPlayer);

        if(player.getWager()>player.getMoney()){
                setButtonsEnabled(false);
                dealerPlay();
                return;
        }
        
        if (player.getTurn() != 1) {
            showMessage("Can only double on turn 1");
            return;
        } else if (player.getWager() > player.getMoney()) {
            showMessage("Not enough Money to double");
            return;}

        Card card = deck.drawCard();
        player.getHands().get(0).addCard(card);
        player.loseMoney(player.getWager());
        player.setWager(player.getWager() * 2);
        setButtonsEnabled(false);
        dealerPlay();
            

        dealCardAnimation(card,playerCardX(player),playerCardY(player, player.getFirstHand().getHand().size() - 1)).play();

        int val = player.getFirstHand().getHandVal();
        showMessage("You drew " + card + " (total: " + val + ")");

        if (val > 21) {
            showMessage("Busted!");
            setButtonsEnabled(false);
        }
    }
    
    private void dealerPlay() {
        runBots(() -> dealerTurn());
    }

    private void dealerTurn() {
        showMessage("Dealer's turn...");
        SequentialTransition dealerAnim = new SequentialTransition();

        while (dealer.getFirstHand().getHandVal() < 17) {
            Card c = deck.drawCard();
            dealer.getFirstHand().addCard(c);
            dealerAnim.getChildren().add(
                dealCardAnimation(
                    c,
                    dealerCardX(),
                    dealerCardY(dealer.getFirstHand().getHand().size() - 1)
                )
            );
        }

        dealerAnim.setOnFinished(e -> resolveRound());
        dealerAnim.play();
    }

    private void resolveRound() {
        int dealerVal = dealer.getFirstHand().getHandVal();
        int playerVal = players.get(0).getFirstHand().getHandVal();

        showMessage("Dealer: " + dealerVal + " | You: " + playerVal);

        Player p = players.get(0);

        if ((dealerVal > 21 || playerVal > dealerVal) && playerVal <= 21) {
            showMessage("You win!");
            p.addMoney(p.getWager() * 2);
        } else if (dealerVal == playerVal) {
            showMessage("Push.");
            p.addMoney(p.getWager());
        } else {
            showMessage("Dealer wins.");
        }

        showMessage("Count: " + deck.getCount());
        resetWagerControls();
    }

    private boolean basicBotStep(Player bot) {
        Hand h = bot.getFirstHand();
        int val = h.getHandVal();
        int dealerUp = dealer.getFirstHand().getHand().get(0).getValue();

        if (val <= 11) return hit(bot);
        if (val >= 17) return false;

        return !(dealerUp >= 2 && dealerUp <= 6) && hit(bot);
    }


    private void runBots(Runnable afterBots) {
        SequentialTransition bots = new SequentialTransition();

        for (Player p : players) {
            if (p.name.equals("BasicBot")) {
                bots.getChildren().add(botThinkingTurn(
                    p, "Bot 1 thinking...",
                    () -> basicBotStep(p)
                ));
            }
            else if (p.name.equals("SmartBot")) {
                bots.getChildren().add(botThinkingTurn(
                    p, "Bot 2 counting...",
                    () -> countingBotStep(p)
                ));
            }
            else if (p.name.equals("CheaterBot")) {
                bots.getChildren().add(botThinkingTurn(
                    p, "Bot 3 cheating...",
                    () -> cheaterBotStep(p)
                ));
            }
        }

        bots.setOnFinished(e -> afterBots.run());
        bots.play();
    }

    private boolean countingBotStep(Player bot) {
        Hand h = bot.getFirstHand();
        int val = h.getHandVal();
        int count = deck.getCount();

        if (val <= 11) return hit(bot);
        if (val >= 17) return false;

        return count < 2 && hit(bot);
    }
    private boolean cheaterBotStep(Player bot) {
        Hand h = bot.getFirstHand();
        Card next = deck.peekCard();

        return h.getHandVal() + next.getValue() <= 21 && hit(bot);
    }

    private Animation botThinkingTurn(Player bot, String msg, Supplier<Boolean> step) {
        SequentialTransition seq = new SequentialTransition();

        PauseTransition announce = new PauseTransition(Duration.seconds(0.6));
        announce.setOnFinished(e -> showMessage(msg));
        seq.getChildren().add(announce);

        Timeline act = new Timeline(
            new KeyFrame(Duration.seconds(0.8), e -> {
                boolean hitAgain = step.get();
                if (hitAgain) {
                    seq.getChildren().add(botThinkingTurn(bot, msg, step));
                }
            })
        );

        seq.getChildren().add(act);
        return seq;
    }

    private boolean hit(Player bot) {
        Card c = deck.drawCard();
        bot.getFirstHand().addCard(c);

        dealCardAnimation(
            c,
            playerCardX(bot),
            playerCardY(bot, bot.getFirstHand().getHand().size() - 1)
        ).play();

        return bot.getFirstHand().getHandVal() <= 21;
    }


    private void playerStand() {
        showMessage("You stand.");
        setButtonsEnabled(false);
        dealerPlay();
    }
    
    private void playerHit() {
        Player player = players.get(currentPlayer);
        Card card = deck.drawCard();
        player.getCurrHand().addCard(card);
        player.incrementTurn();


        dealCardAnimation(card, playerCardX(player), playerCardY(player, player.getFirstHand().getHand().size() - 1)).play();

        int val = player.getCurrHand().getHandVal();
        showMessage("You drew " + card + " (total: " + val + ")");

        if (val > 21) {
            showMessage("Busted!");
            setButtonsEnabled(false);
            dealerPlay();
        }
    }

    //Done - could use tweaks
    private void showMessage(String text) {
        messageLabel.setText(text);
        messageLabel.setOpacity(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), messageLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition stay = new PauseTransition(Duration.seconds(1.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), messageLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
        seq.play();
    }
    //Done
    private void updateWagerLabel() {
        wagerLabel.setText("$" + wager);
    }
    //Done
    private void confirmWager() {
        if(players.get(0).getMoney()>=players.get(0).getWager()){
            players.get(0).setWager(wager);
            players.get(0).loseMoney(wager);
            showMessage("Wager confirmed: $" + wager);
            startRound();
        }
        else{
            showMessage("Not enough money");
        }
    }
    //Done
    private void resetWagerControls() {
        increaseBtn.setDisable(false);
        decreaseBtn.setDisable(false);
        confirmWagerBtn.setDisable(false);
    }
    //Done
    private void setButtonsEnabled(boolean on) {
        hitBtn.setDisable(!on);
        standBtn.setDisable(!on);
        doubleBtn.setDisable(!on);
        splitBtn.setDisable(!on);
    }

    //Done - may need to impleemnt multiple player
    private Animation dealCardAnimation(Card card, double toX, double toY) {
        ImageView cardView = getCardImage(card);
        cardView.setLayoutX(deckX);
        cardView.setLayoutY(deckY);
        cardView.setOpacity(0);
        table.getChildren().add(cardView);

        TranslateTransition move = new TranslateTransition(Duration.millis(500), cardView);
        move.setToX(toX - deckX);
        move.setToY(toY - deckY);

        FadeTransition fade = new FadeTransition(Duration.millis(300), cardView);
        fade.setFromValue(0);
        fade.setToValue(1);

        return new ParallelTransition(move, fade);
    }
    //Done
    private ImageView getCardImage(Card card) {
        String filename = card.toString().toLowerCase() + ".png";
        String path = "/PNG-cards-1.3/" + filename;

        Image img;
        try {
            // getResource returns a URL, not an Image
            java.net.URL cardURL = getClass().getResource(path);

            if (cardURL == null) {
                throw new RuntimeException("Card not found: " + path);
            }

            img = new Image(cardURL.toExternalForm());
        } catch (Exception e) {
            System.out.println("Error loading card: " + e);
            img = new Image("https://upload.wikimedia.org/wikipedia/commons/5/54/Card_back_01.svg");
        }

        ImageView view = new ImageView(img);
        view.setFitWidth(100);
        view.setPreserveRatio(true);
        return view;
    }

    //
    private double playerCardX(Player p) {
        if(p.name.equals("Player"))
            return 500;
        if(p.name.equals("BasicBot"))
            return 350;
        if(p.name.equals("SmartBot"))
            return 200;
        if(p.name.equals("CheaterBot"))
            return 50;
        return 0;
    }

    private double playerCardY(Player p, int index) {
        return 300 + index * 30;
    }

    private double dealerCardX() {
        return 400;
    }

    private double dealerCardY(int index) {
        return 100 + index * 30;
    }

    public static void main(String[] args) {
        launch();
    }
}
