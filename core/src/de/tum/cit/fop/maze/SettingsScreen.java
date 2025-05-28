package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The SettingsScreen class provides the settings menu for the game,
 * including music and volume controls, and a full-screen toggle.
 */
public class SettingsScreen implements Screen {

    private final MazeRunnerGame game;
    private final Stage stage;
    private final Music backgroundMusic;

    /**
     * Constructor for SettingsScreen.
     *
     * @param game The main game class.
     */
    public SettingsScreen(MazeRunnerGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport(), game.getSpriteBatch());
        this.backgroundMusic = game.getBackgroundMusic(); // Use shared background music

        // Ensure all UI elements use the game's skin
        var skin = game.getSkin();

        // Programmatically create CheckBoxStyle with fallback textures
        BitmapFont font = new BitmapFont();
        CheckBoxStyle checkBoxStyle = new CheckBoxStyle();
        checkBoxStyle.font = font;
        checkBoxStyle.checkboxOff = new TextureRegionDrawable(new TextureRegion(createColorTexture(40, 40, Color.DARK_GRAY)));
        checkBoxStyle.checkboxOn = new TextureRegionDrawable(new TextureRegion(createColorTexture(40, 40, Color.LIGHT_GRAY)));
        checkBoxStyle.fontColor = Color.WHITE;
        skin.add("default", checkBoxStyle);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Add a volume label and slider
        Label volumeLabel = new Label("Volume", skin);
        Slider volumeSlider = new Slider(0, 1, 0.1f, false, skin);
        volumeSlider.setValue(backgroundMusic.getVolume());
        table.add(volumeLabel).padBottom(20).row();
        table.add(volumeSlider).width(300).padBottom(20).row();
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                backgroundMusic.setVolume(volumeSlider.getValue());
            }
        });

        // Add a full-screen toggle
        CheckBox fullScreenCheckBox = new CheckBox("Full Screen", skin);
        fullScreenCheckBox.setChecked(Gdx.graphics.isFullscreen());
        table.add(fullScreenCheckBox).padBottom(20).row();
        fullScreenCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (fullScreenCheckBox.isChecked()) {
                    Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                } else {
                    Gdx.graphics.setWindowedMode(1280, 720); // Default windowed resolution
                }
            }
        });

        // Add a back button
        TextButton backButton = new TextButton("Back", skin);
        table.add(backButton).width(300).padBottom(20).row();
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToMenu(); // Return to the menu screen
            }
        });
    }

    private Texture createColorTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Clear the screen with a black color
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
