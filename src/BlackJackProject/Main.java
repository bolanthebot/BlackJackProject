package src.BlackJackProject;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
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
    private TextArea log;       // output messages

    private Button hitBtn, standBtn, dealBtn;
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

        hitBtn = new Button("Hit");
        standBtn = new Button("Stand");
        dealBtn = new Button("Deal");

        log = new TextArea();
        log.setEditable(false);
        log.setPrefHeight(150);

        controls = new VBox(10, new HBox(10, hitBtn, standBtn, dealBtn), log);
        controls.setPadding(new Insets(10));

        root.setCenter(table);
        root.setBottom(controls);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Blackjack (Animated)");
        stage.setScene(scene);
        stage.show();

        // button actions
        dealBtn.setOnAction(e -> startRound());
        hitBtn.setOnAction(e -> playerHit());
        standBtn.setOnAction(e -> playerStand());

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
        log.clear();
        log.appendText("Starting new round...\n");

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
            log.appendText("Your turn!\n");
        });

        dealAnim.play();
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
        log.appendText("You drew " + card + " (total: " + val + ")\n");

        if (val > 21) {
            log.appendText("Busted!\n");
            setButtonsEnabled(false);
        }
    }

    private void playerStand() {
        log.appendText("You stand.\n");
        setButtonsEnabled(false);
        dealerPlay();
    }

    private void dealerPlay() {
        log.appendText("Dealer's turn...\n");
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
            log.appendText("Dealer total: " + dealerVal + "\n");
            log.appendText("Your total: " + playerVal + "\n");
            if (dealerVal > 21 || playerVal > dealerVal && playerVal <= 21)
                log.appendText("You win!\n");
            else if (dealerVal == playerVal)
                log.appendText("Push.\n");
            else
                log.appendText("Dealer wins.\n");
        });

        dealerAnim.play();
    }

    private void setButtonsEnabled(boolean on) {
        hitBtn.setDisable(!on);
        standBtn.setDisable(!on);
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
