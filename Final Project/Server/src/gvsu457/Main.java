package gvsu457;

/**
 * Created by fletcher on 11/17/16.
 */
public class Main {

    public static void main(String[] args){

        Player p1 = new Player();
        p1.setUserName("Dave");
        Player p2 = new Player();
        p2.setUserName("Taylor");
        new TicTacToeGUI(p1, p2);
    }

}
