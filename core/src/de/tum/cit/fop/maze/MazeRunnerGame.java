package de.tum.cit.fop.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    // Screens
    private ScreenType currentScreenType = ScreenType.NONE;
    private IntroductionScreen introductionScreen;
    private MenuScreen menuScreen;
    private LevelSelectionScreen levelSelectionScreen;
    private GameScreen gameScreen;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    // UI Skin
    private Skin skin;

    // Background music
    private Music backgroundMusic;

    // Character animations
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private Animation<TextureRegion> characterRightAnimation;

    // Current level
    private int currentLevel;

    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        this.loadCharacterAnimations(); // Load character animations

        // Load and play background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("background.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.play();

        // Set the game to full-screen mode
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());

        // Start with the IntroductionScreen
        this.goToIntroduction();
    }

    /**
     * Switches to the introduction screen.
     */
    public void goToIntroduction() {
        if (introductionScreen == null) {
            introductionScreen = new IntroductionScreen(this);
        }
        setScreen(introductionScreen);
        disposeOtherScreens(ScreenType.INTRODUCTION);
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        if (menuScreen == null) {
            menuScreen = new MenuScreen(this);
        }
        setScreen(menuScreen);
        disposeOtherScreens(ScreenType.MENU);
    }

    /**
     * Switches to the level selection screen.
     */
    public void goToLevelSelection() {
        if (levelSelectionScreen == null) {
            levelSelectionScreen = new LevelSelectionScreen(this);
        }
        setScreen(levelSelectionScreen);
        disposeOtherScreens(ScreenType.LEVEL_SELECTION);
    }

    /**
     * Switches to the game screen for a specific level.
     *
     * @param level The level number to load.
     */
    public void goToGameWithLevel(int level) {
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        gameScreen = new GameScreen(this, level); // Pass the level to the GameScreen
        setScreen(gameScreen);
    }


    /**
     * Restarts the current level.
     */
    public void restartLevel() {
        if (currentLevel > 0) { // Ensure there's a level to restart
            goToGameWithLevel(currentLevel);
        }
    }

    /**
     * Disposes of unused screens to free memory.
     */
    private void disposeOtherScreens(ScreenType keepScreenType) {
        if (keepScreenType != ScreenType.INTRODUCTION && introductionScreen != null) {
            introductionScreen.dispose();
            introductionScreen = null;
        }
        if (keepScreenType != ScreenType.MENU && menuScreen != null) {
            menuScreen.dispose();
            menuScreen = null;
        }
        if (keepScreenType != ScreenType.LEVEL_SELECTION && levelSelectionScreen != null) {
            levelSelectionScreen.dispose();
            levelSelectionScreen = null;
        }
        if (keepScreenType != ScreenType.GAME && gameScreen != null) {
            gameScreen.dispose();
            gameScreen = null;
        }
        currentScreenType = keepScreenType; // Update the current screen type
    }

    /**
     * Loads the character animations from the character.png file.
     */
    private void loadCharacterAnimations() {
        Texture walkSheet = new Texture(Gdx.files.internal("character.png"));

        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // Down animation
        Array<TextureRegion> downFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            downFrames.add(new TextureRegion(walkSheet, col * frameWidth, 0, frameWidth, frameHeight));
        }
        characterDownAnimation = new Animation<>(0.1f, downFrames);

        // Right animation
        Array<TextureRegion> upFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            upFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight, frameWidth, frameHeight));
        }
        characterRightAnimation = new Animation<>(0.1f, upFrames);

        // Up animation
        Array<TextureRegion> leftFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            leftFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight * 2, frameWidth, frameHeight));
        }
        characterUpAnimation = new Animation<>(0.1f, leftFrames);

        // Left animation
        Array<TextureRegion> rightFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < animationFrames; col++) {
            rightFrames.add(new TextureRegion(walkSheet, col * frameWidth, frameHeight * 3, frameWidth, frameHeight));
        }
        characterLeftAnimation = new Animation<>(0.1f, rightFrames);
    }

    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
        backgroundMusic.dispose(); // Dispose the music
    }

    // Getter methods
    public Skin getSkin() {
        return skin;
    }

    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }

    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }
    public boolean isFinalLevel(int currentLevel) {
        return currentLevel >= 5; // Ensure TOTAL_LEVELS is correctly defined
    }




    /**
     * Enum for tracking the active screen type.
     */
    private enum ScreenType {
        NONE,
        INTRODUCTION,
        MENU,
        LEVEL_SELECTION,
        GAME
    }
}
