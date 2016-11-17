package gvsu457;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

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


    public TicTacToeGUI(Player p1, Player p2){

        ticTacToe = new TicTacToeGame(p1, p2);

        try {
            xImg = ImageIO.read(getClass().getResource("x.png"));
            xImg = xImg.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
            oImg = ImageIO.read(getClass().getResource("o.png"));
            oImg = oImg.getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
        }
        catch(IOException ex) {
            System.out.println("IOException");
        }


        playersTurnLabel = new JLabel("Players Turn: " + ticTacToe.getPlayersTurn());
        mainPanel = new JPanel(new BorderLayout());
        bottomPanel = new JPanel(new GridLayout(3, 3));
        topPanel = new JPanel();
        gameBoard = new JButton[3][3];

        topPanel.add(playersTurnLabel);

        for(int i = 0; i < 3; i++){
            for(int k = 0; k < 3; k++){
                gameBoard[i][k] = new JButton();
                gameBoard[i][k].addActionListener(this);
                gameBoard[i][k].setPreferredSize(new Dimension(100,100));
                bottomPanel.add(gameBoard[i][k]);
            }
        }

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setTitle("Tic Tac Toe");

    }

    private Image getImage(){

        if(ticTacToe.getPlayersTurn() == 1)
            return xImg;
        else
            return oImg;
    }

    @Override
    public void actionPerformed(ActionEvent event){

        JComponent e = (JComponent) event.getSource();

        for(int i = 0; i < 3; i++){
            for(int k = 0; k < 3; k++){
                if(e == gameBoard[i][k]) {
                    System.out.println(i + ":" + k);
                    if(ticTacToe.spaceTaken(i, k))
                        JOptionPane.showMessageDialog(this, "Space Taken");
                    else{
                        ticTacToe.updateGameBoard(i, k);
                        gameBoard[i][k].setIcon(new ImageIcon(getImage()));
                        if(ticTacToe.checkIfWinner(ticTacToe.getPlayersTurn()))
                            JOptionPane.showMessageDialog(this, "Winner!");
                        ticTacToe.changePlayer();
                        playersTurnLabel.setText("Players Turn: " + ticTacToe.getPlayersTurn());

                        System.out.println("getPlayersTurn: " + ticTacToe.getPlayersTurn());


                    }
                }
            }
        }
    }


}
