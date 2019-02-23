
package Game;

import GameClock.StopWatch;
import GameEngine.AbstractGame;
import GameEngine.GameState.State;
import GameMenus.PostGameMenu;
import GameMenus.PauseMenu;
import GameMenus.StartMenu;
import GamePanel.GameData;
import GamePanel.IntegerPanelItem;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Game extends AbstractGame {

    private Controller controller;
    //
    private int rows, columns, nMines;
    //
    private IntegerPanelItem mines, moves;
    //
    private double score = 0;   // 3BV/s
    //
    private Level level;
    //
    private StopWatch stopWatch;
    //
    private PostGameMenu postGameMenu;
    //
    
    public Game(int width, int height, float scale) {
        super(width, height, scale);
    }
    
    @Override
    public void initiate() {
        setResizable(true);
        setDebugInfoDisplayed(false);
        setPausable(true);
        setFBSlimited(true);
        
        controller = new Controller(this);
        
        setLevel(Level.Beginner);
        
        stopWatch = new StopWatch(this);
        mines = new IntegerPanelItem("Mines", nMines);
        moves = new IntegerPanelItem("Moves", 0);
        
        addGamePanel(new GameData() {
            @Override
            public void initiate() {
                addItem(mines);
                addItem(stopWatch);
                addItem(moves);
            }
        }, Color.darkGray, Color.white, Color.lightGray, 17);
        
        setStartMenu(new StartMenu(this));
        setPauseMenu(new PauseMenu(this));
        
        postGameMenu = new PostGameMenu(this);
        
        setPostGameMenu(postGameMenu);

        
    }
    
    @Override
    public void reset() {
        controller.newGame(rows, columns, nMines);
        stopWatch.start();
        mines.setValue(nMines);
        moves.setValue(0);
    }

    @Override
    public void update() {
        stopWatch.update();
        controller.update();
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.gray);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.lightGray);
        int stroke = 4;
        g.setStroke(new BasicStroke(4));
        g.drawRect(controller.getTileSize()-stroke, controller.getTileSize()-stroke, getWidth() - 2*(controller.getTileSize()-stroke), getHeight() - 2*(controller.getTileSize()-stroke));
        controller.render(g);
        
        //g.setColor(Color.white);
        //g.drawRect(32, 32, getWidth() - (2*32) + stroke - 2, getHeight() - (2*32) + stroke - 2);
        
    }
    
    public void setLevel(Level level) {
        this.level = level;
        
        switch(level) {
            
            case Beginner: {
                rows = 12;
                columns = 12;
                nMines = 20;
                break;
            }
            
            case intermediate: {
                rows = 16;
                columns = 16;
                nMines = 40;
                break;
            }
            
            case expert: {
                rows = 16;
                columns = 30;
                nMines = 99;
                break;
            }
            
            case advanced: {
                rows = 20;
                columns = 36;
                nMines = 200;
                break;
            }
        }
        
    }
    
    public void gameWon(double BV) {
        score = BV / stopWatch.getTimeSeconds();
        postGameMenu.setTitle("You Win!");
        postGameMenu.setSubTitle("Score = " + Math.round(score * 1000d) / 1000d + " (3BV/s)");
        setState(State.postGame);
    }
    
    public void gameLost() {
        postGameMenu.setTitle("Game Over");
        postGameMenu.removeSubTitle();
        setState(State.postGame);
    }

    public IntegerPanelItem getMines() {
        return mines;
    }

    public IntegerPanelItem getMoves() {
        return moves;
    }

    public double getScore() {
        return score;
    }
    
}
