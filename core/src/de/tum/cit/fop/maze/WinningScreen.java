package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WinningScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Skin skin;
    private final int nextLevel;

    public WinningScreen(MazeRunnerGame game, int nextLevel) {
        this.game = game;
        this.nextLevel = nextLevel;
        this.skin = game.getSkin(); // Use the skin from the main game
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set input processor for this screen

        createUI();
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Winning Label
        BitmapFont font = new BitmapFont();
        font.getData().setScale(3);
        font.setColor(Color.GREEN);

        TextButton.TextButtonStyle winningStyle = new TextButton.TextButtonStyle();
        winningStyle.font = font;
        TextButton winningLabel = new TextButton("You Won!", winningStyle);
        winningLabel.setDisabled(true); // Make it a static label
        table.add(winningLabel).padBottom(40).row();

        // Next Level Button
        TextButton nextLevelButton = new TextButton("Next Level", skin);
        nextLevelButton.addListener(event -> {
            if (Gdx.input.isTouched()) {
                game.goToGameWithLevel(nextLevel);
                return true;
            }
            return false;
        });
        table.add(nextLevelButton).pad(10).fillX().uniformX().row();

        // Main Menu Button
        TextButton menuButton = new TextButton("Main Menu", skin);
        menuButton.addListener(event -> {
            if (Gdx.input.isTouched()) {
                game.goToMenu();
                return true;
            }
            return false;
        });
        table.add(menuButton).pad(10).fillX().uniformX().row();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(Gdx.gl.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
