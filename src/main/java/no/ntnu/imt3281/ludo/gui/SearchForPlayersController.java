package no.ntnu.imt3281.ludo.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import no.ntnu.imt3281.ludo.client.ClientSocket;
import no.ntnu.imt3281.ludo.gui.ServerListeners.UsersListResponseListener;
import no.ntnu.imt3281.ludo.logic.messages.UserLeftChatRoom;
import no.ntnu.imt3281.ludo.logic.messages.UserWantsToCreateGame;
import no.ntnu.imt3281.ludo.logic.messages.UserWantsUsersList;
import no.ntnu.imt3281.ludo.logic.messages.UsersListResponse;

import java.util.ArrayList;

public class SearchForPlayersController implements UsersListResponseListener {

    @FXML
    private TextField searchTextField;

    @FXML
    private Text playersInvitedText;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> playersListView;

    @FXML
    private Button startButton;

    private ClientSocket clientSocket;
    ArrayList<String> playersInvited = new ArrayList<>();   // the arraylist holding players invited

    /**
     * Method to pass client socket from LudoController to setup listeners
     */
    public void setup(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
        clientSocket.addUsersListResponseListener(this);
    }

    @FXML
    public void initialize() {
        listViewSetup(false);
    }

    /**
     * Setup the listview
     *
     * @param areButtonsDisabled true = player can invite, false = player cannot invite anymore
     */
    private void listViewSetup(boolean areButtonsDisabled) {
        playersListView.setCellFactory(cell -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    this.setText(item);
                    this.setFont(Font.font(16));

                    // button next to each item in listview to invite players
                    Button button = new Button();

                    // in case where user has invited a maximum of 3 players or player has been invited already
                    if (areButtonsDisabled || playersInvited.contains(item)) {
                        button.setDisable(true);
                    }

                    button.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            invitePlayer(item, button);
                        }
                    });

                    this.setGraphic(button);
                }
            }
        });
    }

    @FXML
    void searchPlayersButton(ActionEvent event) {
        String queryText = searchTextField.getText();

        // search bar cannot be empty
        if (queryText.isEmpty()) {
            return;
        }

        // send query to server
        clientSocket.sendMessageToServer(new UserWantsUsersList("UserWantsUsersList", clientSocket.getUserId(), queryText));
    }

    /**
     * Gets called when user presses button in listview to invite a player
     */
    private void invitePlayer(String displayName, Button inviteButton) {
        if (playersInvited.size() < 3) {
            playersInvited.add(displayName);
            playersInvitedText.setText("Players Invited: " + playersInvited.size() + "/3");
        }

        // player already invited, we disable the invite button for that player
        inviteButton.setDisable(true);

        // at least 1 player invited, we enable "Start Game" button
        if (playersInvited.size() > 0) {
            startButton.setDisable(false);
        }

        // maximum of 3 players can be invited so we disable invite feature on all
        if (playersInvited.size() >= 3) {
            listViewSetup(true);
        }
    }

    /**
     * When the user presses the "Start Game" button.
     * Disabled on default. Will be enabled if at least 1 player has been invited
     *
     * @param event
     */
    @FXML
    void startGameButton(ActionEvent event) {
        // send message to server that we want to start the lobby and send invitations to invited players
        if (playersInvited.size() > 0) {
            clientSocket.sendMessageToServer(new UserWantsToCreateGame("UserWantsToCreateGame",
                    clientSocket.getUserId(), playersInvited.stream().toArray(String[]::new)));
        }
    }

    /**
     * Gets the list of players that the client requested
     *
     * @param response the message from the server containing all info about a request
     */
    @Override
    public void usersListResponseEvent(UsersListResponse response) {
        // needs to run on javafx thread
        Platform.runLater(() -> {
            ObservableList<String> items = FXCollections.observableArrayList(response.getDisplaynames());
            playersListView.setItems(items);
        });
    }

    /**
     * Called when the tab of this controller is closed.
     * Here we want to handle stuff like sending message to server.
     */
    public EventHandler<Event> onTabClose = new EventHandler<Event>() {
        @Override
        public void handle(Event arg0) {
            // remove this listener from clientsocket
            clientSocket.removeAddUsersListResponseListener(SearchForPlayersController.this);
        }
    };
}
