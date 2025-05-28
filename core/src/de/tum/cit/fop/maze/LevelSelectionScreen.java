package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The LevelSelectionScreen allows the player to select a level to play.
 */
public class LevelSelectionScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;

    /**
     * Constructor for LevelSelectionScreen.
     *
     * @param game The main game class.
     */
    public LevelSelectionScreen(MazeRunnerGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.getSpriteBatch());

        var skin = game.getSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create buttons for levels
        for (int i = 1; i <= 5; i++) {
            final int level = i; // To use in the listener
            TextButton levelButton = new TextButton("Level " + i, skin);
            table.add(levelButton).width(300).pad(10).row();
            levelButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // Transition to the game screen with the selected level
                    game.goToGameWithLevel(level);
                }
            });
        }

        // Add a back button to return to the menu
        TextButton backButton = new TextButton("Back", skin);
        table.add(backButton).width(300).pad(10).row();
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Clear the screen with black
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
