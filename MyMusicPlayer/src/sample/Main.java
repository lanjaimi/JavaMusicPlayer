//
//My JAVA Music Player
//
//
// Author: Lahcen Anjaimi
// Date: 04/30/2020
// Class: MET CS 422
// Issues: None known
//
// Description:
// A music player with SQLite database able to play wave audio clips.Show songs info on GUI (Title, Artist, album)
// list of features include
//Show song length
//Show song progress (progress bar, and timer)
//Play, pause songs
//Adjust volume
//Play a song on loop
//Shuffle play
//Skip to next song
//Skip to Previous song
//Display song playlist in a table
//Add songs to playlist
//Delete songs from playlist
//Play song with double click on the list
//Transition between two GUI
//-------------------------------------------------------------------------------------------


package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("My Music Player!");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


















}
