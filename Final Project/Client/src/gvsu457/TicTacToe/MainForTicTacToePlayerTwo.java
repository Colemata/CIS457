package gvsu457.TicTacToe;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Administrator on 11/23/2016.
 */
public class MainForTicTacToePlayerTwo {

    public static void main(String[] args) throws IOException {

        Socket OtherClient = new Socket("localhost", 8989);
        Player them = new Player("taylor", 1);
        Player me = new Player("dave", 2);
        TicTacToeGUI ticTacToeGame = new TicTacToeGUI(them, me, OtherClient, "dave", false);
    }
}
