package gvsu457;

/**
 * Created by Administrator on 11/13/2016.
 */
public class TicTacToeGame {

    private int playersTurn;
    private int[][] gameBoard;
    private Player player1;
    private Player player2;

    public TicTacToeGame(Player player1, Player player2){

        randomizeFirstTurn();
        this.player1 = player1;
        this.player2 = player2;

        for(int i = 0; i < 3; i++){
            for(int k = 0; k < 3; k++){
                gameBoard = new int[3][3];
                gameBoard[i][k] = -1;
            }
        }

    }


    int changePlayer() {

        if(playersTurn == 1)
            playersTurn = 2;

        else
            playersTurn = 1;

        return playersTurn;
    }

    void updateGameBoard(int row, int col){

        gameBoard[row][col] = playersTurn;
    }

    boolean spaceTaken(int row, int col){

        if(gameBoard[row][col] > 0)
            return true;

        return false;
    }

    int getPlayersTurn(){

        return playersTurn;
    }

    boolean checkIfWinner(int player){

        for(int i = 0; i < 3; i++){
            for(int k = 0; k < 3; k++){
                if((gameBoard[i][0] == player && gameBoard[i][1] == player && gameBoard[i][2] == player) ||
                   (gameBoard[0][k] == player && gameBoard[1][k] == player && gameBoard[2][k] == player)) {
                    System.out.println("true");
                    return true;
                }

            }
        }
        if((gameBoard[0][0] == player && gameBoard[1][1] == player && gameBoard[2][2] == player) ||
           (gameBoard[2][0] == player && gameBoard[1][1] == player && gameBoard[0][2] == player))
                return true;
        return false;
    }

    void resetGame(){

        for(int i = 0; i < 3; i++){
            for(int k = 0; k < 3; k++){
                gameBoard[i][k] = -1;
            }
        }
    }

    void randomizeFirstTurn(){

        playersTurn = 1 + (int) (Math.random() * 2);
    }

    boolean gameBoardFull(){

        for(int i = 0; i < 3; i++){
            for(int k = 0; k < 3; k++){
                if(gameBoard[i][k] < 0)
                    return false;
            }
        }
        return true;
    }



}
