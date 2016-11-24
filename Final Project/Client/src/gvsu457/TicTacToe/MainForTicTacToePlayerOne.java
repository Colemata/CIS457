package gvsu457.TicTacToe;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 11/23/2016.
 */
public class MainForTicTacToePlayerOne {

    public static void main(String[] args) throws IOException {

        ServerSocket OtherClient = new ServerSocket(8989);
        Player them = new Player("taylor", 1);
        Player me = new Player("dave", 2);
        TicTacToeGUI ticTacToeGame = new TicTacToeGUI(them, me, OtherClient.accept(), "taylor", true);
    }
}
