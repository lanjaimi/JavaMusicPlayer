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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongsQueries {
    private static final String URL = "jdbc:sqlite:/Users/admin/Desktop/MyMusicPlayer/sqlite/mydb.db";

    private Connection connection; // manages connection
    private PreparedStatement selectAllSongs;
    private PreparedStatement insertNewSong;

    // select all of the songs in the database
    public SongsQueries() {

        try {

            // open connection
            connection = DriverManager.getConnection(URL);

            // create query that selects all entries in the database
            selectAllSongs = connection.prepareStatement("SELECT * FROM myTable ORDER BY id, artist, title, album, path");



            // create insert that adds a new entry into the database
            insertNewSong = connection.prepareStatement(
                    "INSERT INTO myTable " +
                            "(id, artist, title, album, path) " +
                            "VALUES ( ?, ?, ?, ?, ?)");
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            System.exit(1);
        }
    }

    // select all of the songs in the database
    public List<Song> getAllSongs() {
        // executeQuery returns ResultSet containing matching entries
        try (ResultSet resultSet = selectAllSongs.executeQuery()) {
            List<Song> results = new ArrayList<Song>();

            while (resultSet.next()) {
                results.add(new Song(
                        resultSet.getInt("id"),
                        resultSet.getString("artist"),
                        resultSet.getString("title"),
                        resultSet.getString("album"),
                        resultSet.getString("path")));
            }


            return results;
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return null;
    }

    // add an entry
    public int addSong( int id, String artist, String title, String album, String path) {

        // insert the new entry; returns # of rows updated
        try {
            // set parameters
            insertNewSong.setInt(1,id);
            insertNewSong.setString(2, artist);
            insertNewSong.setString(3, title);
            insertNewSong.setString(4, album);
            insertNewSong.setString(5, path);

            return insertNewSong.executeUpdate();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return 0;
        }
    }

    public int deleteSong(int id) {
        Statement stmt = null;
        String query = "delete from myTable where id = " + id;

        System.out.println("Query = " + query);
        try
        {
            stmt = connection.createStatement();
            stmt.execute(query);
            stmt.close();
            return 0;
        }
        catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
        return 0;

    }

    // close the database connection
    public void close() {
        try {
            connection.close();
        }
        catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


}
