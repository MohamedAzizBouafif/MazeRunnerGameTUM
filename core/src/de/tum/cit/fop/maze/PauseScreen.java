package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final Stage stage;
    private final ShapeRenderer shapeRenderer;

    public PauseScreen(MazeRunnerGame game) {
        this.game = game;

        // Camera and stage setup
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.stage = new Stage(new ScreenViewport());
        this.shapeRenderer = new ShapeRenderer();

        // Set input processor to the stage
        Gdx.input.setInputProcessor(stage);

        // Create UI elements
        createPauseMenu();
    }

    private void createPauseMenu() {
        Skin skin = game.getSkin(); // Use the same skin as the MenuScreen

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Resume Button
        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(event -> {
            if (Gdx.input.isTouched()) {
                game.setScreen(game.getGameScreen()); // Go back to the game
                return true;
            }
            return false;
        });

        // Change Level Button
        TextButton changeLevelButton = new TextButton("Change Level", skin);
        changeLevelButton.addListener(event -> {
            if (Gdx.input.isTouched()) {
                game.goToLevelSelection(); // Navigate to level selection
                return true;
            }
            return false;
        });

        // Quit Button
        TextButton quitButton = new TextButton("Quit to Menu", skin);
        quitButton.addListener(event -> {
            if (Gdx.input.isTouched()) {
                game.goToMenu(); // Navigate to the main menu
                return true;
            }
            return false;
        });

        // Add buttons to the table
        table.add(resumeButton).pad(10).fillX().uniformX();
        table.row();
        table.add(changeLevelButton).pad(10).fillX().uniformX();
        table.row();
        table.add(quitButton).pad(10).fillX().uniformX();
    }

    @Override
    public void render(float delta) {
        // Render the semi-transparent overlay
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.5f)); // Semi-transparent black
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        // Render the UI stage
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Set input processor to the stage
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null); // Disable input processing when hidden
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
