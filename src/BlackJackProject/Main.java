package src.BlackJackProject;

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

public class Main extends Application {
    private Deck deck;
    private Player dealer;
    private List<Player> players;

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


    @Override
    public void start(Stage stage) {
        deck = new Deck();
        dealer = new Player("Dealer");
        players = new ArrayList<>();
        players.add(new Player("Player"));
        
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
        confirmWagerBtn.setOnAction(e -> startRound());
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
        table.getChildren().clear();
        deck.shuffle();
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

    private void updateWagerLabel() {
        wagerLabel.setText("$" + wager);
    }

    private void confirmWager() {
        showMessage("Wager confirmed: $" + wager);
        increaseBtn.setDisable(true);
        decreaseBtn.setDisable(true);
        confirmWagerBtn.setDisable(true);
    }

    private void resetWagerControls() {
        increaseBtn.setDisable(false);
        decreaseBtn.setDisable(false);
        confirmWagerBtn.setDisable(false);
    }



    private void playerHit() {
        Player player = players.get(currentPlayer);
        Card card = deck.drawCard();
        player.getFirstHand().addCard(card);

        dealCardAnimation(card,
                playerCardX(player),
                playerCardY(player, player.getFirstHand().getHand().size() - 1)
        ).play();

        int val = player.getFirstHand().getHandVal();
        showMessage("You drew " + card + " (total: " + val + ")");

        if (val > 21) {
            showMessage("Busted!");
            setButtonsEnabled(false);
        }
    }

    private void playerDouble(){
        Player player = players.get(currentPlayer);
        Card card = deck.drawCard();
        player.getFirstHand().addCard(card);

        if(player.getWager()>player.getMoney()){
                setButtonsEnabled(false);
                dealerPlay();
                return;
        }
        int turn=1;
        if (turn != 1) {
            showMessage("Can only double on turn 1");
            return;
        } else if (player.getWager() > player.getMoney()) {
            showMessage("Not enough Money to double");
            return;
        } else {
            player.getHands().get(0).addCard(deck.drawCard());
            player.loseMoney(player.getWager());
            player.setWager(player.getWager() * 2);
            setButtonsEnabled(false);
            dealerPlay();
            
        }
        dealCardAnimation(card,playerCardX(player),playerCardY(player, player.getFirstHand().getHand().size() - 1)).play();

        int val = player.getFirstHand().getHandVal();
        showMessage("You drew " + card + " (total: " + val + ")");

        if (val > 21) {
            showMessage("Busted!");
            setButtonsEnabled(false);
        }
    }

    private void playerStand() {
        showMessage("You stand.");
        setButtonsEnabled(false);
        dealerPlay();
    }

    private void dealerPlay() {
        showMessage("Dealer's turn...");
        SequentialTransition dealerAnim = new SequentialTransition();

        while (dealer.getFirstHand().getHandVal() < 17) {
            Card c = deck.drawCard();
            dealer.getFirstHand().addCard(c);
            dealerAnim.getChildren().add(dealCardAnimation(c,
                    dealerCardX(),
                    dealerCardY(dealer.getFirstHand().getHand().size() - 1)));
        }

        dealerAnim.setOnFinished(e -> {
            int dealerVal = dealer.getFirstHand().getHandVal();
            int playerVal = players.get(0).getFirstHand().getHandVal();
            showMessage("Dealer total: " + dealerVal);
            showMessage("Your total: " + playerVal);
            if (dealerVal > 21 || playerVal > dealerVal && playerVal <= 21){
                showMessage("You win!");
                Player p=players.get(0);
                p.loseMoney(wager);
            }
            else if (dealerVal == playerVal){
                showMessage("Push.");
            }
            else
                showMessage("Dealer wins.");
        });

        dealerAnim.play();
    }

    private void setButtonsEnabled(boolean on) {
        hitBtn.setDisable(!on);
        standBtn.setDisable(!on);
        doubleBtn.setDisable(!on);
        splitBtn.setDisable(!on);
    }

    // 🔹 ANIMATION LOGIC BELOW 🔹
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

    private ImageView getCardImage(Card card) {
        String filename = card.toString().toLowerCase() + ".png";
        String path = "/PNG-cards-1.3/" + filename;
        System.out.println("Loading: " + path);

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


    // 🔹 CARD PLACEMENT HELPERS 🔹
    private double playerCardX(Player p) {
        return 200;
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
