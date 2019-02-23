
package Game;

import GameEngine.Graphics.BufferedImageLoader;
import GameEngine.Graphics.SpriteSheet;
import Model.Cell;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {
    
    private Game game;
    //
    private Cell[][] cells;
    private int rows, columns;
    //
    private int nMines;
    //
    private final int tileSize = 32;
    //
    private SpriteSheet tilesSprite;
    //
    private Random random;
    //
    private boolean firstMove;
    private boolean gameWon;
    private boolean gameLost;
    //

    public Controller(Game game) {
        this.game = game;
        
        try {
            BufferedImageLoader loader = new BufferedImageLoader();
            tilesSprite = new SpriteSheet(loader.loadImage("/img/tiles32.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void newGame(int rows, int columns, int nMines) {
        
        this.rows = rows;
        this.columns = columns;
        this.nMines = nMines;
                
        game.setSize((columns+2)*tileSize, (rows+2)*tileSize);
        
        random = new Random();
        
        createBoard();
        firstMove = true;
        gameWon = false;
        gameLost = false;
    }
    
    public void update() {
        if(gameWon) {
            game.gameWon(calculate3BV());
        }        
        else if(gameLost) game.gameLost();
        else {
            if(game.getInput().isButtonDown(MouseEvent.BUTTON1)) {
                primaryClick(getIndex(game.getInput().getMouseY()), getIndex(game.getInput().getMouseX()));
                game.getMoves().setValue(game.getMoves().getValue() + 1);
            }
            if(game.getInput().isButtonDown(MouseEvent.BUTTON3)) {
                secondaryClick(getIndex(game.getInput().getMouseY()), getIndex(game.getInput().getMouseX()));
                game.getMoves().setValue(game.getMoves().getValue() + 1);
            }
            gameWon = isGameWon();
        }
    }
    
    public void render(Graphics2D g) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                if(gameWon) cells[i][j].renderGameWon(g);
                else if(gameLost) cells[i][j].renderGameLost(g);
                else cells[i][j].render(g);
            }
        }
    }
    
    private void revealAll() {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j].setRevealed(true);
            }
        }
    }
    
    public void primaryClick(int row, int column) {
        if(cells[row][column].isRevealed()) {
            int adjacentFlags = 0;
            if(isValidCell(row + 1, column + 1))    if(cells[row + 1][column + 1].isFlagged()) adjacentFlags++; 
            if(isValidCell(row + 1, column - 1))    if(cells[row + 1][column - 1].isFlagged()) adjacentFlags++;
            if(isValidCell(row + 1, column))    if(cells[row + 1][column].isFlagged()) adjacentFlags++;
            if(isValidCell(row - 1, column + 1))    if(cells[row - 1][column + 1].isFlagged()) adjacentFlags++;
            if(isValidCell(row - 1, column - 1))    if(cells[row - 1][column - 1].isFlagged()) adjacentFlags++;
            if(isValidCell(row - 1, column))    if(cells[row - 1][column].isFlagged()) adjacentFlags++;
            if(isValidCell(row, column + 1))    if(cells[row][column + 1].isFlagged()) adjacentFlags++;
            if(isValidCell(row, column - 1))    if(cells[row][column - 1].isFlagged()) adjacentFlags++;
            
            if(cells[row][column].getAdjacentMines() == adjacentFlags) {
                if(isValidCell(row + 1, column + 1) && !cells[row + 1][column + 1].isFlagged())  reveal(row + 1, column + 1);
                if(isValidCell(row + 1, column - 1) && !cells[row + 1][column - 1].isFlagged())  reveal(row + 1, column - 1);
                if(isValidCell(row + 1, column) && !cells[row + 1][column].isFlagged())   reveal(row + 1, column);
                if(isValidCell(row - 1, column + 1) && !cells[row - 1][column + 1].isFlagged())    reveal(row - 1, column + 1);
                if(isValidCell(row - 1, column - 1) && !cells[row - 1][column - 1].isFlagged())    reveal(row - 1, column - 1);
                if(isValidCell(row - 1, column) && !cells[row - 1][column].isFlagged())   reveal(row - 1, column);
                if(isValidCell(row, column + 1) && !cells[row][column + 1].isFlagged())   reveal(row, column + 1);
                if(isValidCell(row, column - 1) && !cells[row][column - 1].isFlagged())   reveal(row, column - 1);
            }
            
        }
        else
            reveal(row, column);
    }
    
    public void secondaryClick(int row, int column) {        
        if(!cells[row][column].isRevealed()) {
            cells[row][column].toggleFlags();   
        }
    }
    
    private void createBoard() {
        cells = new Cell[rows][columns];
        
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                cells[i][j] = new Cell(j+1, i+1, tileSize, tilesSprite, game);
            }
        }
    }
    
    private void scatterMines(int cellRow, int cellColumn) {
        for(int i = 0; i < nMines; i++) {            
            int row, column;
            
            do {
                
                do {
                    row = random.nextInt(rows);
                }while(row == cellRow || row == cellRow - 1 || row == cellRow + 1);
                
                do {
                    column = random.nextInt(columns);
                }while(column == cellColumn || column == cellColumn - 1 || column == cellColumn + 1);
                
            }while(cells[row][column].isMine());
            
            cells[row][column].setMine(true);            
        }
        countAdjacentMines();
    }
    
    private void countAdjacentMines() {       
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {

                if(!cells[row][column].isMine()) {
                    int adjacentMines = 0;
                    if(isValidCell(row + 1, column + 1))    if(cells[row + 1][column + 1].isMine()) adjacentMines++; 
                    if(isValidCell(row + 1, column - 1))    if(cells[row + 1][column - 1].isMine()) adjacentMines++;
                    if(isValidCell(row + 1, column))    if(cells[row + 1][column].isMine()) adjacentMines++;
                    if(isValidCell(row - 1, column + 1))    if(cells[row - 1][column + 1].isMine()) adjacentMines++;
                    if(isValidCell(row - 1, column - 1))    if(cells[row - 1][column - 1].isMine()) adjacentMines++;
                    if(isValidCell(row - 1, column))    if(cells[row - 1][column].isMine()) adjacentMines++;
                    if(isValidCell(row, column + 1))    if(cells[row][column + 1].isMine()) adjacentMines++;
                    if(isValidCell(row, column - 1))    if(cells[row][column - 1].isMine()) adjacentMines++;

                    cells[row][column].setAdjacentMines(adjacentMines);
                }
            }
        }        
    }
    
    public void reveal(int row, int column) {
        
        Cell cell = cells[row][column];
        
        if(firstMove) {
            scatterMines(row, column);
            firstMove = false;
            reveal(row, column);
        }
        
        if(cell.isMine()) {
            cell.setTriggered(true);
            gameLost = true;
        }
        else {
            if(!cell.isRevealed() && !cell.isFlagged() && !cell.isQuestioned()) {           
                cell.setRevealed(true);

                if(cell.hasNoAdjacentMines()) {
                    if(isValidCell(row + 1, column + 1))  reveal(row + 1, column + 1);
                    if(isValidCell(row + 1, column - 1))  reveal(row + 1, column - 1);
                    if(isValidCell(row + 1, column))   reveal(row + 1, column);
                    if(isValidCell(row - 1, column + 1))    reveal(row - 1, column + 1);
                    if(isValidCell(row - 1, column - 1))    reveal(row - 1, column - 1);
                    if(isValidCell(row - 1, column))   reveal(row - 1, column);
                    if(isValidCell(row, column + 1))   reveal(row, column + 1);
                    if(isValidCell(row, column - 1))   reveal(row, column - 1);
                }
            }
        }
    }
    
    private int getIndex(double px) {
        return (int)(px/tileSize) - 1;
    }
    
    private boolean isValidCell(int row, int column) {
        return (row >= 0) && (column >= 0) && (row < rows) && (column < columns);
    }
    
    private boolean isGameWon() {
        
        for(int row = 0; row < rows; row++) 
            for(int column = 0; column < columns; column++) 
                if(!cells[row][column].isMine() && !cells[row][column].isRevealed()) return false; 
         
        return true;
    }
    
    private double calculate3BV() {
        int n = 0; //minimum number of clicks required to clear the board
        
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                if(cells[row][column].hasNoAdjacentMines() && !cells[row][column].isVisited()) {
                    cells[row][column].setVisited(true);
                    n++;
                    floodFillVisit(row, column);
                }
            }
        }
        
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < columns; column++) {
                if(!cells[row][column].isMine()&& !cells[row][column].isVisited()) {
                    cells[row][column].setVisited(true);
                    n++;
                }
            }
        }
        
        return n;
    }
    
    private void floodFillVisit(int row, int column) {
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(i==0 && j==0 || !isValidCell(row + i, column + j)) continue;
                
                if(!cells[row + i][column + j].isVisited()) {
                    cells[row + i][column + j].setVisited(true);

                    if(cells[row + i][column + j].hasNoAdjacentMines()) floodFillVisit(row + i, column + j);
                }
            }
        } 
    }

    public int getTileSize() {
        return tileSize;
    }
    
    
    
    
    
}
