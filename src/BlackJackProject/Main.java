package src.BlackJackProject;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.*;

public class Main extends Application {
    private Deck deck;
    private List<Player> players;
    private Player dealer;

    private VBox tableArea;
    private TextArea outputArea;
    private TextField wagerInput;
    private Button hitBtn, standBtn, doubleBtn, splitBtn, nextBtn;

    private int currentPlayerIndex = 0;
    private Hand currentHand;

    @Override
    public void start(Stage stage) {
        deck = new Deck();
        players = new ArrayList<>();
        dealer = new Player("Dealer");

        // --- Layout setup ---
        BorderPane root = new BorderPane();
        tableArea = new VBox(10);
        tableArea.setPadding(new Insets(10));
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        // --- Control buttons ---
        HBox controls = new HBox(10);
        hitBtn = new Button("Hit");
        standBtn = new Button("Stand");
        doubleBtn = new Button("Double");
        splitBtn = new Button("Split");
        nextBtn = new Button("Next");

        controls.getChildren().addAll(hitBtn, standBtn, doubleBtn, splitBtn, nextBtn);

        // --- Add wager input field ---
        wagerInput = new TextField();
        wagerInput.setPromptText("Enter wager");

        VBox bottom = new VBox(10, wagerInput, controls);
        bottom.setPadding(new Insets(10));

        root.setCenter(outputArea);
        root.setBottom(bottom);
        root.setTop(tableArea);

        // --- Initial scene ---
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Blackjack");
        stage.setScene(scene);
        stage.show();

        // --- Initialize game ---
        initGame();

        // --- Button actions ---
        hitBtn.setOnAction(e -> hitAction());
        standBtn.setOnAction(e -> standAction());
        doubleBtn.setOnAction(e -> doubleAction());
        splitBtn.setOnAction(e -> splitAction());
        nextBtn.setOnAction(e -> nextPlayer());
    }

    private void initGame() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter number of players (max 4):");
        Optional<String> result = dialog.showAndWait();

        int p = Integer.parseInt(result.orElse("1"));
        if (p > 4) p = 4;

        for (int i = 0; i < p; i++) {
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setHeaderText("Enter player " + (i + 1) + " name:");
            Optional<String> nameResult = nameDialog.showAndWait();
            players.add(new Player(nameResult.orElse("Player" + (i + 1))));
        }

        startRound();
    }

    private void startRound() {
        deck.shuffle();
        dealer.clearHands();
        dealer.addHand();
        dealer.getFirstHand().addCard(deck.drawCard());
        dealer.getFirstHand().addCard(deck.drawCard());

        for (Player player : players) {
            player.clearHands();
            player.addHand();
            player.getFirstHand().addCard(deck.drawCard());
            player.getFirstHand().addCard(deck.drawCard());
        }

        currentPlayerIndex = 0;
        currentHand = players.get(0).getFirstHand();

        updateTable();
        output("Starting new round. " + players.get(0).name + "'s turn.");
    }

    private void updateTable() {
        tableArea.getChildren().clear();
        for (Player p : players) {
            Label label = new Label(p.name + ": " + p.printHand() + " (" + p.getFirstHand().getHandVal() + ")");
            tableArea.getChildren().add(label);
        }
        Label dealerLabel = new Label("Dealer: " + dealer.getFirstHand().toString());
        tableArea.getChildren().add(dealerLabel);
    }

    private void output(String text) {
        outputArea.appendText(text + "\n");
    }

    // --- Button Actions ---
    private void hitAction() {
        Player player = players.get(currentPlayerIndex);
        currentHand.addCard(deck.drawCard());
        output(player.name + " hits.");
        updateTable();
        if (currentHand.getHandVal() > 21) {
            output(player.name + " busts!");
            nextPlayer();
        }
    }

    private void standAction() {
        output(players.get(currentPlayerIndex).name + " stands.");
        nextPlayer();
    }

    private void doubleAction() {
        Player player = players.get(currentPlayerIndex);
        output(player.name + " doubles down.");
        currentHand.addCard(deck.drawCard());
        player.loseMoney(player.getWager());
        player.setWager(player.getWager() * 2);
        updateTable();
        nextPlayer();
    }

    private void splitAction() {
        output("Split not implemented yet in GUI.");
    }

    private void nextPlayer() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= players.size()) {
            dealerPlay();
        } else {
            currentHand = players.get(currentPlayerIndex).getFirstHand();
            output(players.get(currentPlayerIndex).name + "'s turn.");
        }
    }

    private void dealerPlay() {
        output("Dealer's turn...");
        while (dealer.getFirstHand().getHandVal() < 17) {
            dealer.getFirstHand().addCard(deck.drawCard());
        }
        updateTable();
        checkResults();
    }

    private void checkResults() {
        int dealerVal = dealer.getFirstHand().getHandVal();
        if (dealerVal > 21) output("Dealer busts!");

        for (Player p : players) {
            int playerVal = p.getFirstHand().getHandVal();
            if (playerVal > 21) {
                output(p.name + " loses (bust).");
            } else if (dealerVal > 21 || playerVal > dealerVal) {
                output(p.name + " wins!");
                p.addMoney(p.getWager() * 2);
            } else if (playerVal == dealerVal) {
                output(p.name + " pushes.");
                p.addMoney(p.getWager());
            } else {
                output(p.name + " loses.");
            }
        }

        ButtonType playAgain = new Alert(Alert.AlertType.CONFIRMATION, "Play another round?", ButtonType.YES, ButtonType.NO).showAndWait().orElse(ButtonType.NO);
        if (playAgain == ButtonType.YES) {
            startRound();
        } else {
            output("Game Over.");
            hitBtn.setDisable(true);
            standBtn.setDisable(true);
            doubleBtn.setDisable(true);
            splitBtn.setDisable(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
