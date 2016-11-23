package gvsu457;

import com.sun.corba.se.spi.activation.Server;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by fletcher on 11/17/16.
 */
public class TicTacToeGUI extends JFrame implements ActionListener {

    private JButton[][] gameBoard;
    private JPanel mainPanel;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private TicTacToeGame ticTacToe;
    private JLabel playersTurnLabel;
    private Image xImg;
    private Image oImg;

    public Socket connection;

    public Socket server;

    public String ourName;

    public DataInputStream in;

    public DataOutputStream out;

    public boolean isServer;

    /**
     * A new Thread Pool
     */
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    public TicTacToeGUI(Player p1, Player p2, Socket socket, String ourname, boolean isServer) {

        TicTacToeCommThread commThread = new TicTacToeCommThread(socket);
        executorService.submit(commThread);

        this.isServer = isServer;

        this.ourName = ourname;
        this.connection = socket;

        //set up the streams using the global socket.
        try {
            in = new DataInputStream(new BufferedInputStream(connection.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(connection.getOutputStream()));

            ticTacToe = new TicTacToeGame(p1, p2, connection, ourName);

            try {
                xImg = ImageIO.read(getClass().getResource("x.png"));
                xImg = xImg.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
                oImg = ImageIO.read(getClass().getResource("o.png"));
                oImg = oImg.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            } catch (IOException ex) {
                System.out.println("IOException");
            }

            playersTurnLabel = new JLabel("Players Turn: " + ticTacToe.getPlayersTurn());
            mainPanel = new JPanel(new BorderLayout());
            bottomPanel = new JPanel(new GridLayout(3, 3));
            topPanel = new JPanel();
            gameBoard = new JButton[3][3];

            topPanel.add(playersTurnLabel);

            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    gameBoard[i][k] = new JButton();
                    gameBoard[i][k].addActionListener(this);
                    gameBoard[i][k].setPreferredSize(new Dimension(100, 100));
                    bottomPanel.add(gameBoard[i][k]);
                }
            }

            mainPanel.add(topPanel, BorderLayout.NORTH);
            mainPanel.add(bottomPanel, BorderLayout.SOUTH);

            add(mainPanel);
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            pack();
            setVisible(true);

            //JOptionPane.showMessageDialog(this, "Player " + ticTacToe.getPlayersTurn() + " will go first.");

            setTitle("Tic Tac Toe");


            playGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void playGame() {
        try {
            String currentPlayersTurn = ticTacToe.getPlayerNameForTurn();

            if (currentPlayersTurn.equalsIgnoreCase(ourName)) {
                //make a selection
                unlockAllButtons();
            } else {
                lockAllButtons();
                int i = in.readInt();
                int k = in.readInt();
                ticTacToe.updateGameBoard(i, k);
                gameBoard[i][k].setIcon(new ImageIcon(getImage()));
                if (ticTacToe.gameBoardFull()) {
                    JOptionPane.showMessageDialog(this, "Tie Game!");
                    resetGame();
                    ticTacToe.resetGame();
                    ticTacToe.randomizeFirstTurn();
                    JOptionPane.showMessageDialog(this, "Player " + ticTacToe.getPlayersTurn() + " will go first.");

                }
                if (ticTacToe.checkIfWinner(ticTacToe.getPlayersTurn())) {
                    JOptionPane.showMessageDialog(this, "Player " + ticTacToe.getPlayersTurn() + " Wins!");
                    resetGame();
                    ticTacToe.resetGame();
                    ticTacToe.randomizeFirstTurn();
                    JOptionPane.showMessageDialog(this, "Player " + ticTacToe.getPlayersTurn() + " will go first.");
                    playersTurnLabel.setText("Players Turn: " + ticTacToe.getPlayersTurn());
                } else {
                    ticTacToe.changePlayer();
                    playersTurnLabel.setText("Players Turn: " + ticTacToe.getPlayersTurn());
                }
                playGame();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Image getImage() {

        if (ticTacToe.getPlayersTurn() == 1)
            return xImg;
        else
            return oImg;
    }

    private void resetGame() {

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                gameBoard[i][k].setIcon(null);
            }
        }
    }

    private void lockAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                gameBoard[i][k].setEnabled(false);
            }
        }
    }

    private void unlockAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                gameBoard[i][k].setEnabled(true);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            JComponent e = (JComponent) event.getSource();

            for (int i = 0; i < 3; i++) {
                for (int k = 0; k < 3; k++) {
                    if (e == gameBoard[i][k]) {
                        if (ticTacToe.spaceTaken(i, k))
                            JOptionPane.showMessageDialog(this, "Space Taken");
                        else {
                            ticTacToe.updateGameBoard(i, k);
                            out.writeInt(i);
                            out.writeInt(k);
                            out.flush();
                            gameBoard[i][k].setIcon(new ImageIcon(getImage()));
                            if (ticTacToe.gameBoardFull()) {
                                JOptionPane.showMessageDialog(this, "Tie Game!");
                                resetGame();
                                ticTacToe.resetGame();
                                ticTacToe.randomizeFirstTurn();
                                JOptionPane.showMessageDialog(this, "Player " + ticTacToe.getPlayersTurn() + " will go first.");

                            }
                            if (ticTacToe.checkIfWinner(ticTacToe.getPlayersTurn())) {
                                JOptionPane.showMessageDialog(this, "Player " + ticTacToe.getPlayersTurn() + " Wins!");
                                resetGame();
                                ticTacToe.resetGame();
                                ticTacToe.randomizeFirstTurn();
                                JOptionPane.showMessageDialog(this, "Player " + ticTacToe.getPlayersTurn() + " will go first.");
                                playersTurnLabel.setText("Players Turn: " + ticTacToe.getPlayersTurn());
                            } else {
                                ticTacToe.changePlayer();
                                playersTurnLabel.setText("Players Turn: " + ticTacToe.getPlayersTurn());
                            }
                        }
                    }
                }
            }
            playGame();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}
