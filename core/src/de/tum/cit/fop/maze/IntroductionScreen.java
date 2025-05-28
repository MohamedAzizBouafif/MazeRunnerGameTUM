package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * The IntroductionScreen displays the medieval story before transitioning to the menu.
 */
public class IntroductionScreen implements Screen {

    private final MazeRunnerGame game;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final BitmapFont titleFont;
    private final Texture background;

    private final String[] storyLines;
    private int currentStoryLineIndex;
    private float elapsedTime = 0;

    /**
     * Constructor for IntroductionScreen.
     *
     * @param game The main game class.
     */
    public IntroductionScreen(MazeRunnerGame game) {
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();

        // Initialize fonts
        this.font = new BitmapFont();
        this.font.getData().setScale(2); // Scale font for readability

        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(4); // Larger scale for title

        // Load background image
        this.background = new Texture(Gdx.files.internal("medieval_background.png"));

        // Define story text
        this.storyLines = new String[]{
                "Long ago, in a realm of mystic mazes,",
                "the Maze of Destiny awaited the brave and daring.",
                "Legends spoke of a Crown hidden deep within,",
                "granting unimaginable power to its finder.",
                "You are the chosen one. Will you conquer the maze,",
                "or will its traps and enemies claim your fate?"
        };
        this.currentStoryLineIndex = 0;
    }

    @Override
    public void render(float delta) {
        elapsedTime += delta;

        // Clear the screen and draw the background
        ScreenUtils.clear(0, 0, 0, 1);
        spriteBatch.begin();
        spriteBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Center title and story text
        float titleX = Gdx.graphics.getWidth() / 2f - 200; // Adjust for title centering
        float titleY = Gdx.graphics.getHeight() - 100; // Top of the screen

        float textX = Gdx.graphics.getWidth() / 2f - 400; // Center story text
        float textY = Gdx.graphics.getHeight() / 2f; // Middle of the screen
        float promptY = Gdx.graphics.getHeight() / 4f; // Lower for prompt

        // Draw the title
        titleFont.draw(spriteBatch, "The Maze Runner", titleX, titleY);

        // Draw the current story line
        font.draw(spriteBatch, storyLines[currentStoryLineIndex], textX, textY);

        // Show prompt to continue after some time
        if (elapsedTime > 2) {
            font.draw(spriteBatch, "Press ENTER to continue...", textX, promptY);
        }

        spriteBatch.end();

        // Handle input to progress the story
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            elapsedTime = 0; // Reset elapsed time
            currentStoryLineIndex++;
            if (currentStoryLineIndex >= storyLines.length) {
                game.goToMenu(); // Transition to menu
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        // Handle screen resizing if necessary
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        background.dispose();
        font.dispose();
        titleFont.dispose();
    }
}
