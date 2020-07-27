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

public class Song {
    String artist;
    String title;
    String album;
    String path;
    int id;


    public Song(){

    }

    public Song(int id, String artist, String title, String album, String path) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.album = album;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
