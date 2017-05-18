/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 *
 * @author Corentin
 */
public class GUI extends Application {
    
    private Client client;
    private ObservableList<Node> messagesList;
    private ScrollPane spReceive, spSend;
    private FlowPane fpReceive, fpSend;
    private Label IPAddressLabelSend, IPAddressSend, portTextLabelSend,
            portLabelSend, fileLabelSend, IPAddressLabelReceive, IPAddressReceive,
            portTextLabelReceive,portLabelReceive, fileLabelReceive;
    private Button clearChatButtonSend, logOutButtonSend, openFileChooserSend,
            clearChatButtonReceive, logOutButtonReceive, openFileChooserReceive,
            sendFile, receiveFile;
    private TextField fileNameOnServerTextField, fileNameOnClientTextField;
    private File receivedFile, downloadPath;
    private FileChooser fileChooserSend;
    private DirectoryChooser directoryChooser;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(GUI.class, args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        // Connexion
        
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(25, 25, 25, 25));

        Text loginSceneTitle = new Text("Log in");
        loginSceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        loginGrid.add(loginSceneTitle, 1, 0, 2, 1);
        
        Label serverAddress = new Label("Server address:");
        loginGrid.add(serverAddress, 0, 1);
        
        TextField serverAddressTextField = new TextField();
        loginGrid.add(serverAddressTextField, 1, 1);

        Button signInButton = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(signInButton);
        loginGrid.add(hbBtn, 1, 3);

        final Text actiontarget = new Text();
        actiontarget.setFill(Color.FIREBRICK);
        loginGrid.add(actiontarget, 1, 5);
        
        Scene loginScene = new Scene(loginGrid, 300, 200);
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Fenêtre principale
        
        this.fpReceive = new FlowPane();
        this.messagesList = this.fpReceive.getChildren();
        this.spReceive = new ScrollPane();
        this.spReceive.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.spReceive.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.spReceive.setContent(this.fpReceive);
        
        this.fpSend = new FlowPane();
        this.messagesList = this.fpSend.getChildren();
        this.spSend = new ScrollPane();
        this.spSend.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.spSend.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.spSend.setContent(this.fpSend);
        
        
        
        this.IPAddressLabelSend = new Label("IP address:");
        this.IPAddressLabelSend.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        
        this.IPAddressSend = new Label();
        this.IPAddressSend.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        this.IPAddressSend.setTextFill(Color.GREEN);
        
        this.portTextLabelSend = new Label("Port:");
        this.portTextLabelSend.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        
        this.portLabelSend = new Label();
        this.portLabelSend.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        this.portLabelSend.setTextFill(Color.GREEN);
        
        this.clearChatButtonSend = new Button("Clear log");
        this.clearChatButtonSend.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        
        this.logOutButtonSend = new Button("Log out");
        this.logOutButtonSend.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        
        this.fileChooserSend = new FileChooser();
        this.fileChooserSend.setTitle("Chosir un fichier à envoyer");
        
        this.sendFile = new Button("Envoyer");
        this.receiveFile = new Button("Recevoir");
        this.sendFile.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        
        this.openFileChooserSend = new Button("Télécharger");
        
        this.IPAddressLabelReceive = new Label("IP address:");
        this.IPAddressLabelReceive.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        
        this.IPAddressReceive = new Label();
        this.IPAddressReceive.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        this.IPAddressReceive.setTextFill(Color.GREEN);
        
        this.portTextLabelReceive = new Label("Port:");
        this.portTextLabelReceive.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        
        this.portLabelReceive = new Label();
        this.portLabelReceive.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        this.portLabelReceive.setTextFill(Color.GREEN);
        
        this.clearChatButtonReceive = new Button("Clear log");
        this.clearChatButtonReceive.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        
        this.logOutButtonReceive = new Button("Log out");
        this.logOutButtonReceive.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        
        this.directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chosir l'emplacement de sauvegarde");
        
        this.sendFile = new Button("Envoyer");
        this.receiveFile = new Button("Recevoir");
        this.sendFile.setFont(Font.font("Tahoma", FontWeight.NORMAL, 13));
        
        this.openFileChooserReceive = new Button("Choisir");
        
        TabPane tabs = new TabPane();
        Tab sendTab = new Tab();
        sendTab.setText("Envoyer");
        sendTab.setContent(sendFileTab());
        sendTab.setClosable(false);
        
        Tab receiveTab = new Tab();
        receiveTab.setText("Recevoir");
        receiveTab.setContent(receiveFileTab());
        receiveTab.setClosable(false);
        
        tabs.getTabs().addAll(sendTab, receiveTab);
        
        Scene mainScene = new Scene(tabs, 500, 450); // Manage scene size
        primaryStage.setTitle("Client TFTP");
        
       
        
        signInButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {
                if(serverAddressTextField.getText().equals(""))
                    actiontarget.setText("L'adresse IP doit être saisie.");
                else{
                    if (!serverAddressTextField.getText().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
                        actiontarget.setText("L'adresse IP n'est pas valide.");
                    }else{
                        int bufferSize = 2000;
                        InetAddress iaServer;
                        //IPAddress.setText(serverAddressTextField.getText());
                       // portLabel.setText(serverPortTextField.getText());
                        try {
                            
                            client = new Client();
                            IPAddressSend.setText(serverAddressTextField.getText());
                            IPAddressReceive.setText(serverAddressTextField.getText());
                            portLabelSend.setText(String.valueOf(client.getPort()));
                            portLabelReceive.setText(String.valueOf(client.getPort()));
                            //client.sendFile("fichier.txt");
        
                            //iaServer = InetAddress.getByName(serverAddressTextField.getText());
                            //client = new Client(iaServer, userNameTextField.getText(), port, bufferSize);
                            //client.setName(userNameTextField.getText());
                            //client.addObserver(ClientMain.this);
                            //Thread clientThread = new Thread(client);
                            //clientThread.start();
                            primaryStage.setScene(mainScene);
                        } catch (Exception ex) {
                            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                            actiontarget.setText("An error occured.");
                        }
                    }
                }
            }
        });
        
        // Validation adresse IP
        serverAddressTextField.textProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                
                if (!newValue.matches("\\d\\.*")) {
                    serverAddressTextField.setText(newValue.replaceAll("[^\\d\\.]", ""));
                }

            }
        });
        
        this.logOutButtonSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               messagesList.clear();
               serverAddressTextField.setText("");
               actiontarget.setText("");
               primaryStage.setScene(loginScene);
            }
        });
        
        this.logOutButtonReceive.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               messagesList.clear();
               serverAddressTextField.setText("");
               actiontarget.setText("");
               primaryStage.setScene(loginScene);
            }
        });
        
        this.clearChatButtonSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               messagesList.clear();
               spSend.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        });
        
        this.clearChatButtonReceive.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               messagesList.clear();
               spReceive.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
        });
        
        this.openFileChooserSend.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
               receivedFile = fileChooserSend.showOpenDialog(primaryStage);
                    if (receivedFile != null) {
                        fileLabelSend.setText(receivedFile.getName());;
                    }
            }
        });
        
        this.openFileChooserReceive.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                downloadPath = directoryChooser.showDialog(primaryStage);
                    if (downloadPath != null) {
                        fileLabelReceive.setText(downloadPath.getName());;
                    }
            }
        });
        
        this.sendFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    client.sendFile(serverAddressTextField.getText(), 69, receivedFile.getName());
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        this.receiveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    client.receiveFile(serverAddressTextField.getText(), 69, fileNameOnServerTextField.getText(), downloadPath.getPath() + downloadPath.getName());
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        serverAddressTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    signInButton.fire();
                }
            }
        });
        
        fileNameOnServerTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    sendFile.fire();
                }
            }
        });
        
        fileNameOnClientTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)  {
                    sendFile.fire();
                }
            }
        });
    }


    private Pane sendFileTab() {
        
        GridPane sendFilePane = new GridPane();
        sendFilePane.setHgap(10);
        sendFilePane.setVgap(10);
        sendFilePane.setPadding(new Insets(10, 10, 10, 10));
        
        
        sendFilePane.add(this.IPAddressLabelSend, 0, 0);
        sendFilePane.add(this.IPAddressSend, 1, 0);
        sendFilePane.add(this.clearChatButtonSend, 4, 0);
        sendFilePane.add(this.logOutButtonSend, 5, 0);

        sendFilePane.add(this.openFileChooserSend, 0, 1, 2, 1);
        this.fileLabelSend = new Label();
        this.fileLabelSend.setTextFill(Color.GREEN);
        sendFilePane.add(this.fileLabelSend, 2, 1,  3, 1);
        
        sendFilePane.add(this.sendFile, 0, 2);
        
        
        sendFilePane.add(this.spSend, 0, 3, 6, 30);

        return sendFilePane;
    }
       
/*
 * Creates the UI for the alignment sample, which demonstrates ways to manage
 * the alignment of controls when you don't want the default alignment.
 */
    private Pane receiveFileTab() {
        
        GridPane receiveFilePane = new GridPane();
        receiveFilePane.setHgap(10);
        receiveFilePane.setVgap(10);
        receiveFilePane.setPadding(new Insets(10, 10, 10, 10));
        
        
        receiveFilePane.add(this.IPAddressLabelReceive, 0, 0);
        receiveFilePane.add(this.IPAddressReceive, 1, 0);
        receiveFilePane.add(this.clearChatButtonReceive, 4, 0);
        receiveFilePane.add(this.logOutButtonReceive, 5, 0);

        Label fileNameOnServer = new Label("Nom du fichier distant:");
        receiveFilePane.add(fileNameOnServer, 0, 1, 3, 1);
        
        this.fileNameOnServerTextField = new TextField();
        receiveFilePane.add(fileNameOnServerTextField, 3, 1, 3, 1);
        
        Label fileNameOnClient = new Label("Nom du fichier local:");
        receiveFilePane.add(fileNameOnClient, 0, 2, 3, 1);
        
        this.fileNameOnClientTextField = new TextField();
        receiveFilePane.add(fileNameOnClientTextField, 3, 2, 3, 1);
        
        Label filePathOnClient = new Label("Dossier de téléchargement:");
        receiveFilePane.add(filePathOnClient, 0, 3, 2, 1);
        receiveFilePane.add(this.openFileChooserReceive, 2, 3, 2, 1);
        this.fileLabelReceive = new Label();
        this.fileLabelReceive.setTextFill(Color.GREEN);
        receiveFilePane.add(this.fileLabelReceive, 4, 3,  3, 1);
        
        receiveFilePane.add(this.receiveFile, 3, 4);
        
        receiveFilePane.add(this.spReceive, 0, 5, 6, 20);

        return receiveFilePane;
    }  
}
