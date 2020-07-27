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

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller  {

    private Clip song;
    private long clipTimePosition;
    private Song songObj;
    boolean playing = false;
    boolean loop = false;
    boolean shuffle = false;

    private static final double INIT_VOLUME = 1;


    @FXML
    Slider volSlider;

    @FXML
    Slider snSlider;

    @FXML
    Label lengthLabel;
    @FXML
    Label timerLabel;

    @FXML
    Label titleLabel;

    @FXML
    Label artistLabel;
    @FXML
    Label albumLabel;

    @FXML
    Button playBtn;

    @FXML
    Button pauseBtn;

    @FXML
    Button loopBtn;

    @FXML
    Button shuffleBtn;

    @FXML
    VBox head;

    @FXML
    HBox tableHead;

    @FXML
    Pane home;

    @FXML
    private TableView<Song> table;

    @FXML
    Label error;




    //instance of class handling database queries
    private  SongsQueries songQueries = new SongsQueries();
    /// observable list to store data base values and listen fir change
    private ObservableList<Song> list = FXCollections.observableArrayList();


    // Player Controls
    ////////////////////////////////////////

    // initialize method
    public void initialize() {

        // set initial slider value
        volSlider.setValue(INIT_VOLUME);


        //Send database query, store return result in a list.
        list.setAll(songQueries.getAllSongs());// populates list, which updates listView

        // Bind the list to table view,
        table.setItems(list);


        // create and populate table
        TableColumn<Song, Integer> idColumn = new TableColumn<Song, Integer>("id");
        TableColumn <Song, String> artistColumn = new TableColumn<Song, String>("Artist");
        TableColumn <Song, String>  titleColumn = new TableColumn<Song, String>("Title");
        TableColumn <Song, String> albumColumn = new TableColumn<Song, String>("Album");
        TableColumn <Song, String> pathColumn = new TableColumn<Song, String>("Path");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        table.getColumns().addAll(idColumn,artistColumn,titleColumn,albumColumn,pathColumn);

        ///////////



        setDoubleClick();


        this.songObj = list.get(0);// set song playing to first item in the list

        updateUI(this.songObj);


    }

    //set double click event of the table view rows
    public void setDoubleClick (){
        table.setRowFactory( tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {

                    Song songClicked = row.getItem();

                    try {

                        this.song.stop();
                        this.clipTimePosition = 0;
                        play(songClicked);
                        updateUI(songClicked);


                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });
    }

    // method create clip, play song, and update the GUI
    public void createClip(Song Obj){


        try {
            //Create file from path, check if file exist, if not show warning
            File music = new File(Obj.getPath());

            if(music.exists()){

                // if a song playing stop it
                if(this.song != null){
                    this.song.stop();
                    this.song.close();
                }

                // get audio input stream from file, copy audio stream in a clip. Set current song to
                // clip,
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(music);
                Clip clip = AudioSystem.getClip();
                this.song = clip;
                this.songObj = Obj;
                clip.open(audioInput);

                //if clip was paused to play from specific frame.
                clip.setMicrosecondPosition(this.clipTimePosition);

                //play song
                clip.start();

                //update control bar
                long songLength = this.song.getMicrosecondLength();
                long minutes = TimeUnit.MICROSECONDS.toMinutes(songLength);
                long seconds = TimeUnit.MICROSECONDS.toSeconds(songLength)%60;
                lengthLabel.setText(minutes +":"+seconds);
                playBtn.setVisible(false);
                pauseBtn.setVisible(true);

                //update the into bar
                updateUI(songObj);

                //highlight song playing row on the table
                table.getSelectionModel().select(list.indexOf(Obj));


            }else {
                System.out.println("not there");
            }

        }catch (Exception ex){
            System.out.println("Error!");
            ex.printStackTrace();
        }


    }

    // play clip
    public void play(Song songObj) throws LineUnavailableException {
        this.playing = true;
        createClip(songObj);
        timeControl();
    }

    @FXML
    //pause clip
    public void pause (MouseEvent mouseEvent) throws LineUnavailableException {

        playBtn.setVisible(true);
        pauseBtn.setVisible(false);
        playing = false;

        this.clipTimePosition = this.song.getMicrosecondPosition();
        this.song.stop();

    }

    @FXML
    //play current clip from specific frame
    public void resume (ActionEvent event) {

        createClip(this.songObj);
        timeControl();
    }
    @FXML
    // Mouse event to to get volume slider value
    public void getNewVolume (MouseEvent mouseEvent) {

        double vol =  volSlider.getValue();

        setVolume(this.song,vol);

    }

    // convert slider value to volume
    public void setVolume(Clip clip, double vol){
        //double ==> min 0.0001 || max 2.0

        FloatControl gain = (FloatControl)this.song.getControl(FloatControl.Type.MASTER_GAIN);
        float db = (float)(Math.log(vol)/Math.log(10) * 20);
        gain.setValue(db);
    }

    @FXML
   // Calculate next index in list if smaller than list size call create song
    public void playNext (ActionEvent event) throws LineUnavailableException {
        int nextIndex = list.indexOf(this.songObj) + 1;

        if(nextIndex < list.size()){
            this.clipTimePosition = 0;
            if(playing)this.song.stop();
            createClip(list.get(nextIndex));

        }else {
            System.out.println("Nooo Next");
        }

    }

    // Calculate prev index in list if bigger than zero call create song
    @FXML
    public void playPrev (ActionEvent event) throws LineUnavailableException {
        int prevIndex = list.indexOf(this.songObj) - 1;

        if(prevIndex >= 0){
            this.clipTimePosition = 0;
            if(playing)this.song.stop();
            createClip(list.get(prevIndex));

        }else {
            System.out.println("NO prev");
        }

    }
   
    //update Gui of shuffle toggle button and boolean values shuffle
    @FXML
    public void toggleShuffle (ActionEvent event) throws LineUnavailableException {
        if(shuffle){
            shuffle = false;
            shuffleBtn.setStyle("-fx-background-color: black");

        }else if (!shuffle && !loop){
            shuffle = true;
            loop =false;
            shuffleBtn.setStyle("-fx-background-color: #130f40");
        }
    }

    //update Gui of loop toggle button and boolean values loop
    @FXML
    public void toggleLoop (ActionEvent event) throws LineUnavailableException {
        if(loop){
            loop = false;
            loopBtn.setStyle("-fx-background-color: black");


        }else if (!loop && !shuffle){
            loop = true;
            shuffle = false;
            loopBtn.setStyle("-fx-background-color: #130f40");
        }
    }



    //Interaction with DataBase and Table
    /////////////////////////////////////


    // choose a file, add to database and update the GUI
    @FXML
    private void chooseFile (ActionEvent event) throws IOException {
        FileChooser fc = new FileChooser();
        File selectedFile = fc.showOpenDialog(null);
        String srcPath = selectedFile.getPath();
        String artist = "Artist";;
        String title = "Untitled";;
        String album = "Untitled";
        String dstPath = "/Users/admin/Desktop/MyMusicPlayer/sqlite/audioFiles/";
        int id = list.size() + 1;

        String input  = selectedFile.getName();;

        //RegEx Patters
        Pattern artistP = Pattern.compile("^(.*?)\\- ");
        Matcher artistM = artistP.matcher(input);
        Pattern titleP = Pattern.compile("\\-(.*?)\\(");
        Matcher titleM = titleP.matcher(input);
        Pattern albumP = Pattern.compile("\\((.*?)\\)");
        Matcher albumM = albumP.matcher(input);

        // check occurrence
        if (artistM.find()) artist = artistM.group(1);
        if (titleM.find()) title = titleM.group(1);
        if (albumM.find()) album =albumM.group(1);
        dstPath+=input;


        File dstFilePath = new File(dstPath);
        if (!dstFilePath.exists()){
            songQueries.addSong(id, artist, title, album, dstPath);
            updateTable();
            copyFile(srcPath,dstPath);


        }else {
            error.setVisible(true);


        }


    }

    // copy file selected to dest folder
    private void copyFile(String from, String to) throws IOException {
        Path src = Paths.get(from);
        Path dest = Paths.get(to);
        Files.copy(src,dest);
    }

    // delete entry from database and update the GUI
    public void deleteEntry(ActionEvent event){

        int selectedId = table.getSelectionModel().getSelectedItem().id;


        File fileToDelete = new File(table.getSelectionModel().getSelectedItem().path);
        fileToDelete.delete();


        songQueries.deleteSong(selectedId);
        System.out.println(table.getSelectionModel().getSelectedItem().path);
        updateTable();
        error.setVisible(false);

    }

    //update the view table
    private void updateTable(){

        list.removeAll();
        list.setAll(songQueries.getAllSongs());
        table.refresh();
        error.setVisible(false);


    }
    
    // GUI Controls 
    /////////////////////////////////////
    
    //every second get current song playing current frame position,
    // calculate progress and update the progress bar 
    // if song is over check shuffle, and loop booleans based on values
    // restart song or pick next and random play next in list,
    
    public void timeControl() {

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            long songLenght = this.song.getMicrosecondLength();
            long  microSeconds = this.song.getMicrosecondPosition();
            long minutes = TimeUnit.MICROSECONDS.toMinutes(microSeconds);
            long seconds = TimeUnit.MICROSECONDS.toSeconds(microSeconds)%60;

            String timeStr = minutes +":"+seconds;

            if(seconds<10) timeStr = minutes +":0"+seconds;

            timerLabel.setText(timeStr);

            //calculate progress % based on song length
            double songSliderValue = (double)(microSeconds*100)/songLenght;

            snSlider.setValue(songSliderValue);

             // when song is over
            if(microSeconds == songLenght){
                if(loop) createClip(this.songObj);
                if(shuffle){

                    Random rand = new Random();
                    int randomNum = rand.nextInt( list.size());
                    createClip(list.get(randomNum));

                    // if no loop  and no shuffle play next in the list if it exist
                }else if(!shuffle && !loop && (list.indexOf(this.songObj) + 1) < list.size()){
                    createClip(list.get(list.indexOf(this.songObj) + 1));


                }
            }



        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    //takes values from song playing and update UI (title, artist, album)
    private void updateUI(Song songObject){
        titleLabel.setText(songObject.getTitle());
        artistLabel.setText(songObject.getArtist());
        albumLabel.setText(songObject.getAlbum());

    }

    // show the table view on click
    public void showList (ActionEvent even){
        tableHead.setVisible(true);
        table.setVisible(true);
        head.setVisible(false);
        home.setVisible(false);


    }

    // show the home vew on click
    public void hideList (ActionEvent event){
        tableHead.setVisible(false);
        table.setVisible(false);
        head.setVisible(true);
        home.setVisible(true);
        error.setVisible(false);

    }



}
