package gvsu457;

/**
 * Created by fletcher on 11/17/16.
 */
public class Player {

    /** Username for the Player */
    private String userName;

    /** The player number for the player */
    private int playerNumber;

    void setUserName(String username){

        this.userName = userName;
    }

    void setPlayerNumber(int playerNumber){

        this.playerNumber = playerNumber;
    }

    String getUserName(){

        return userName;
    }

    int getPlayerNumber(){

        return playerNumber;
    }
}
