
package Model;

import Game.Game;
import GameEngine.Graphics.SpriteSheet;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Cell {
    
    private int x, y;
    private int tileSize;
    //
    private int nAdjacentMines;
    private boolean mine;
    private boolean revealed;
    private boolean flagged;
    private boolean questioned;
    private boolean triggered;
    //
    private boolean visited; //for 3BV
    //
    private SpriteSheet sprite;
    private BufferedImage img;
    //
    private Game game;
    
    public Cell(int x, int y, int tileSize, SpriteSheet sprite, Game game) {
        this.x = x;
        this.y = y;
        this.tileSize = tileSize;
        this.sprite = sprite;
        this.game = game;
        
        revealed = false;
        flagged = false;
        questioned = false;
        triggered = false;
        visited = false;
        nAdjacentMines = 0;
    }
    
    public void render(Graphics2D g) {
        if(flagged) img = sprite.cropImage(12, 1, tileSize, tileSize);
        else if(questioned) img = sprite.cropImage(15, 1, tileSize, tileSize);
        else if(revealed) img = sprite.cropImage(nAdjacentMines + 1, 1, tileSize, tileSize);
        else img = sprite.cropImage(11, 1, tileSize, tileSize);
        
        g.drawImage(img, x*tileSize, y*tileSize, null);
    }
    
    public void renderGameLost(Graphics2D g) {
        if(triggered) img = sprite.cropImage(13, 1, tileSize, tileSize);
        else if(flagged) {
            if(mine) img = sprite.cropImage(12, 1, tileSize, tileSize);
            else img = sprite.cropImage(14, 1, tileSize, tileSize);
        }
        else if(mine) img = sprite.cropImage(10, 1, tileSize, tileSize);
        else if(revealed) img = sprite.cropImage(nAdjacentMines + 1, 1, tileSize, tileSize);
        else img = sprite.cropImage(11, 1, tileSize, tileSize);
        
        g.drawImage(img, x*tileSize, y*tileSize, null);
    }
    
    public void renderGameWon(Graphics2D g) {
        if(mine) img = sprite.cropImage(12, 1, tileSize, tileSize);
        else img = sprite.cropImage(nAdjacentMines + 1, 1, tileSize, tileSize);
        
        g.drawImage(img, x*tileSize, y*tileSize, null);
    }

    public int getAdjacentMines() {
        return nAdjacentMines;
    }

    public void setAdjacentMines(int nSurroundingMines) {
        this.nAdjacentMines = nSurroundingMines;
    }
    
    public boolean hasNoAdjacentMines() {
        return nAdjacentMines==0;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
        nAdjacentMines = 9;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
    
    public void toggleFlags() {        
        if(flagged){
            flagged = false;
            questioned = true;
            game.getMines().setValue(game.getMines().getValue() + 1);
        }
        else if(questioned) {
            questioned = false;
        }
        else {
            flagged = true;
            game.getMines().setValue(game.getMines().getValue() - 1);
        }
    }

    public boolean isFlagged() {
        return flagged;
    }

    public boolean isQuestioned() {
        return questioned;
    }

    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }
   
    
    
}
