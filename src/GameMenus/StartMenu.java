
package GameMenus;

import Game.Game;
import Game.Level;
import GameEngine.GameState.State;
import GameMenu.AbstractMenu;
import GameMenu.MenuItem;
import GameMenu.SubMenuInitializer;

public class StartMenu extends AbstractMenu {

    Game game;
    
    public StartMenu(Game game) {
        super(game);
        this.game = game;
    }

    @Override
    public void initiate() {
        
        addItem(new MenuItem("New Game") {
            @Override
            public void function() {
                game.reset();
                game.setState(State.inGame);
            }
        });
        
        addItem(new SubMenuInitializer("Level") {
            @Override
            public void initiate() {
                addSubMenuItem(new MenuItem("Beginner") {
                    @Override
                    public void function() {
                        game.setLevel(Level.Beginner);
                    }
                });
                
                addSubMenuItem(new MenuItem("Intermediate") {
                    @Override
                    public void function() {
                        game.setLevel(Level.intermediate);
                    }
                });
                
                addSubMenuItem(new MenuItem("Expert") {
                    @Override
                    public void function() {
                        game.setLevel(Level.expert);
                    }
                });
                
                addSubMenuItem(new MenuItem("Advanced") {
                    @Override
                    public void function() {
                        game.setLevel(Level.advanced);
                    }
                });
            }
        });
        
        addItem(new MenuItem("Exit") {
            @Override
            public void function() {
                System.exit(0);
            }
        });
        
    }
    
    
    
}
