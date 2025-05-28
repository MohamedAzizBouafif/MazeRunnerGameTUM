package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;



import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private final OrthographicCamera hudCamera;
    private final Maze maze;

    // Character position and

    private float characterY;
    private int lives = 3;

    private Player player;

    // Timer
    private float elapsedTime = 0f;
    private boolean isPaused = false;

    // Damage cooldown
    private static final float DAMAGE_COOLDOWN = 1.0f;
    private float damageCooldown = 0f;

    // Character animation
    private final Animation<TextureRegion> characterDownAnimation;
    private final Animation<TextureRegion> characterUpAnimation;
    private final Animation<TextureRegion> characterLeftAnimation;
    private final Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> currentCharacterAnimation;
    private float stateTime;

    // HUD elements
    private final TextureRegion heartTexture;
    private final Texture keyIndicatorTexture;
    private boolean keyCollected = false;

    // Font for timer
    private final BitmapFont font;

    //Level
    private final int level;


    // Tile size
    private final int tileSize = 64;

    // Spritesheets
    private final Texture basictilesSheet;
    private final Texture mobsSheet;
    private final Texture objectsSheet;
    private final Texture thingsSheet;
    private final Texture keySheet;

    // Tile regions
    private final TextureRegion wallTexture;
    private final TextureRegion grassTexture;
    private final TextureRegion entryTexture;
    private final TextureRegion exitTexture;
    private final Animation<TextureRegion> trapAnimation;
    private final Animation<TextureRegion> monsterRightAnimation;
    private final Animation<TextureRegion> monsterLeftAnimation;
    private final TextureRegion keyTexture;

    private final List<Monster> monsters;

    private float cooldownTimer = 0f; // Tracks time left for cooldown


    public GameScreen(MazeRunnerGame game, int level) {
        this.game = game;
        this.level = level;

        // Game world camera
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);

        // HUD camera
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Load maze
        this.maze = new Maze();

        // Initialize character position
        player = new Player(game);

        player.setCharacterY(0);
        characterY = 0;


        // Load tilesheets
        basictilesSheet = new Texture(Gdx.files.internal("basictiles.png"));
        mobsSheet = new Texture(Gdx.files.internal("mobs.png"));
        objectsSheet = new Texture(Gdx.files.internal("objects.png"));
        thingsSheet = new Texture(Gdx.files.internal("things.png"));
        keySheet = new Texture(Gdx.files.internal("Key.png"));

        // HUD textures and font
        heartTexture = new TextureRegion(objectsSheet, 64, 0, 16, 16);
        keyIndicatorTexture = new Texture(Gdx.files.internal("key-indicator.png"));
        font = new BitmapFont();

        // Extract textures
        wallTexture = new TextureRegion(basictilesSheet, 0, 0, 16, 16);
        grassTexture = new TextureRegion(basictilesSheet, 48, 16, 16, 16);
        entryTexture = new TextureRegion(thingsSheet, 16, 0, 16, 16);
        exitTexture = new TextureRegion(thingsSheet, 32, 0, 16, 16);

        // Animated traps and monsters
        trapAnimation = new Animation<>(0.2f, new TextureRegion[]{
                new TextureRegion(thingsSheet, 96, 64, 16, 16),
                new TextureRegion(thingsSheet, 112, 64, 16, 16),
                new TextureRegion(thingsSheet, 128, 64, 16, 16)
        });

        monsterRightAnimation = new Animation<>(0.2f, new TextureRegion[]{
                new TextureRegion(mobsSheet, 0, 32, 16, 16),
                new TextureRegion(mobsSheet, 16, 32, 16, 16),
                new TextureRegion(mobsSheet, 32, 32, 16, 16)
        });

        monsterLeftAnimation = new Animation<>(0.2f, new TextureRegion[]{
                new TextureRegion(mobsSheet, 0, 16, 16, 16),
                new TextureRegion(mobsSheet, 16, 16, 16, 16),
                new TextureRegion(mobsSheet, 32, 16, 16, 16)
        });

        keyTexture = new TextureRegion(keySheet, 0, 0, 780, 750);

        // Load character animations
        characterDownAnimation = game.getCharacterDownAnimation();
        characterUpAnimation = game.getCharacterUpAnimation();
        characterLeftAnimation = game.getCharacterLeftAnimation();
        characterRightAnimation = game.getCharacterRightAnimation();
        currentCharacterAnimation = characterDownAnimation;
        stateTime = 0;

        // Initialize monsters
        monsters = new ArrayList<>();

        // Load the maze file
        try {
            maze.loadFromFile("maps/level-" + level + ".properties");

            // Set character position to the entry point
            for (int x = 0; x < maze.getWidth(); x++) {
                for (int y = 0; y < maze.getHeight(); y++) {
                    if (maze.getTileType(x, y) == 1) { // Entry point
                        player.setCharacterY(x * tileSize);
                        characterY = y * tileSize;
                    } else if (maze.getTileType(x, y) == 4) { // Monster
                        monsters.add(new Monster(x, y, monsterRightAnimation, monsterLeftAnimation));
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("Maze", "Failed to load maze file for level: " + level, e);
        }
    }



    private void loadLevel(int level) {
        try {
            maze.loadFromFile("maps/level-" + level + ".properties");

            for (int x = 0; x < maze.getWidth(); x++) {
                for (int y = 0; y < maze.getHeight(); y++) {
                    if (maze.getTileType(x, y) == 1) {
                        player.setCharacterY(x * tileSize);
                        characterY = y * tileSize;
                    } else if (maze.getTileType(x, y) == 4) {
                        monsters.add(new Monster(x, y, monsterRightAnimation, monsterLeftAnimation));
                    }
                }
            }
        } catch (Exception e) {
            Gdx.app.error("Maze", "Failed to load maze file for level: " + level, e);
        }
    }

    @Override
    public void render(float delta) {
        // Handle pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            isPaused = !isPaused;
            if (isPaused) {
                game.setScreen(new PauseScreen(game));
                return;
            }
        }

        if (!isPaused) {
            // Update cooldown timer for damage
            if (cooldownTimer > 0) {
                cooldownTimer -= delta;
            }

            // Handle character movement
            handleCharacterMovement(delta);

            // Update animation state time
            stateTime += delta;

            // Update monsters
            for (Monster monster : monsters) {
                monster.update(delta, maze);
            }

            // Update timer
            elapsedTime += delta;

            // Handle key collection
            handleKeyCollection();

            // Handle damage logic
            handleDamage();

            // Check for level completion
            int characterTileX = (int) (player.getCharacterY() / tileSize);
            int characterTileY = (int) (characterY / tileSize);
            if (maze.getTileType(characterTileX, characterTileY) == 2 && keyCollected && lives > 0) { // Exit tile
                Gdx.app.log("GameScreen", "Transitioning to winning screen...");
                Gdx.app.log("GameScreen", "Current Level: " + level + ", Is Final Level: " + game.isFinalLevel(level));

                try {
                    if (game.isFinalLevel(level)) {
                        Gdx.app.log("GameScreen", "Navigating to FinalWinningScreen");
                        game.setScreen(new FinalWinningScreen(game));
                    } else {
                        Gdx.app.log("GameScreen", "Navigating to WinningScreen with Level: " + (level + 1));
                        game.setScreen(new WinningScreen(game, level + 1));
                    }
                } catch (Exception e) {
                    Gdx.app.error("GameScreen", "Error transitioning to winning screen", e);
                }
                return;
            }
        }

        // Center the camera on the character
        camera.position.set(player.getCharacterY() + tileSize / 2f, characterY + tileSize / 2f, 0);
        camera.update();

        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Render the game world
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin();
        renderMaze();
        renderCharacter();
        game.getSpriteBatch().end();

        // Render the HUD
        renderHUD();
    }









    private void takeDamage() {
        lives--;
        damageCooldown = DAMAGE_COOLDOWN;

        if (lives <= 0) {
            game.setScreen(new GameOverScreen(game));
        }
    }
    private void transitionToGameOver() {
        Gdx.app.log("GameScreen", "Game Over!");
        game.setScreen(new GameOverScreen(game));
    }


    private boolean isCollidingWithMonster(Monster monster) {
        float monsterX = monster.getX() * tileSize;
        float monsterY = monster.getY() * tileSize;

        float collisionMargin = tileSize * 0.2f;

        return player.getCharacterY() + tileSize - collisionMargin > monsterX &&
                player.getCharacterY() + collisionMargin < monsterX + tileSize &&
                characterY + tileSize - collisionMargin > monsterY &&
                characterY + collisionMargin < monsterY + tileSize;
    }


    private void renderMaze() {
        for (int x = 0; x < maze.getWidth(); x++) {
            for (int y = 0; y < maze.getHeight(); y++) {
                TextureRegion texture = null;

                int tileType = maze.getTileType(x, y);
                switch (tileType) {
                    case 0:
                        texture = wallTexture;
                        break;
                    case 1:
                        texture = entryTexture;
                        break;
                    case 2:
                        texture = exitTexture;
                        break;
                    case 3:
                        game.getSpriteBatch().draw(grassTexture, x * tileSize, y * tileSize, tileSize, tileSize);
                        TextureRegion trapFrame = trapAnimation.getKeyFrame(stateTime, true);
                        game.getSpriteBatch().draw(trapFrame, x * tileSize, y * tileSize, tileSize, tileSize);
                        break;
                    case 5:
                        game.getSpriteBatch().draw(grassTexture, x * tileSize, y * tileSize, tileSize, tileSize);
                        texture = keyTexture;
                        break;
                    default:
                        texture = grassTexture;
                        break;
                }

                if (texture != null) {
                    game.getSpriteBatch().draw(texture, x * tileSize, y * tileSize, tileSize, tileSize);
                }
            }
        }

        for (Monster monster : monsters) {
            TextureRegion monsterFrame = monster.getCurrentFrame(stateTime);
            game.getSpriteBatch().draw(monsterFrame, monster.getX() * tileSize, monster.getY() * tileSize, tileSize, tileSize);
        }
    }

    private void renderCharacter() {
        TextureRegion currentFrame = currentCharacterAnimation.getKeyFrame(stateTime, true);
        game.getSpriteBatch().draw(currentFrame, player.getCharacterY(), characterY, tileSize, tileSize);
    }

    private void renderHUD() {
        SpriteBatch batch = game.getSpriteBatch();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        // Draw lives (scaled to be 3x bigger)
        for (int i = 0; i < lives; i++) {
            batch.draw(heartTexture, 10 + i * 120, hudCamera.viewportHeight - 150, 90, 90);
        }

        // Draw key indicator (scaled to be bigger)
        if (keyCollected) {
            batch.draw(keyIndicatorTexture, hudCamera.viewportWidth - 200, hudCamera.viewportHeight - 200, 120, 120); // Adjusted size
        }

        // Draw timer (scaled)
        font.getData().setScale(3); // Scale font to be bigger
        font.draw(batch, "Time: " + (int) elapsedTime, 10, hudCamera.viewportHeight - 20);

        batch.end();
    }


    private void handleCharacterMovement(float delta) {
        float speed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 300 * delta : 150 * delta;

        float newX = player.getCharacterY();
        float newY = characterY;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            newY += speed;
            currentCharacterAnimation = characterUpAnimation;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            newY -= speed;
            currentCharacterAnimation = characterDownAnimation;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newX -= speed;
            currentCharacterAnimation = characterLeftAnimation;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newX += speed;
            currentCharacterAnimation = characterRightAnimation;
        }

        if (canMoveTo(newX, characterY)) {
            player.setCharacterX(newX);
        }
        if (canMoveTo(player.getCharacterY(), newY)) {
            characterY = newY;
        }
    }
    private void handleDamage() {
        if (cooldownTimer > 0) return;

        int tileX = (int) (player.getCharacterY() / tileSize);
        int tileY = (int) (characterY / tileSize);

        // Check traps
        if (maze.getTileType(tileX, tileY) == 3) {
            applyDamage();
            return;
        }

        // Check monsters
        for (Monster monster : monsters) {
            if (isCollidingWithMonster(monster)) {
                applyDamage();
                return;
            }
        }
    }

    private void applyDamage() {
        lives--;
        cooldownTimer = DAMAGE_COOLDOWN; // Reset cooldown timer

        if (lives <= 0) {
            transitionToGameOver();
        }
    }

    private boolean isCollidingWithKey() {
        int characterTileX = (int) (player.getCharacterY() / tileSize);
        int characterTileY = (int) (characterY / tileSize);
        return maze.getTileType(characterTileX, characterTileY) == 5;
    }

    private void handleKeyCollection() {
        int characterTileX = (int) (player.getCharacterY() / tileSize);
        int characterTileY = (int) (characterY / tileSize);

        if (!keyCollected && maze.getTileType(characterTileX, characterTileY) == 5) {
            keyCollected = true;
            maze.setTileType(characterTileX, characterTileY, 6); // Replace key with grass
        }
    }




    private boolean canMoveTo(float x, float y) {
        float margin = tileSize * 0.1f;
        int tileXLeft = (int) ((x + margin) / tileSize);
        int tileXRight = (int) ((x + tileSize - margin - 1) / tileSize);
        int tileYBottom = (int) ((y + margin) / tileSize);
        int tileYTop = (int) ((y + tileSize - margin - 1) / tileSize);

        return maze.getTileType(tileXLeft, tileYBottom) != 0 &&
                maze.getTileType(tileXLeft, tileYTop) != 0 &&
                maze.getTileType(tileXRight, tileYBottom) != 0 &&
                maze.getTileType(tileXRight, tileYTop) != 0;
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width / 2f, height / 2f);
        hudCamera.setToOrtho(false, width, height);
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
        basictilesSheet.dispose();
        mobsSheet.dispose();
        objectsSheet.dispose();
        thingsSheet.dispose();
        keySheet.dispose();
    }

    private static class Monster {
        private int x, y;
        private int directionX = 1, directionY = 0;
        private float moveCooldown = 0;
        private final Animation<TextureRegion> rightAnimation;
        private final Animation<TextureRegion> leftAnimation;
        private Animation<TextureRegion> currentAnimation;

        public Monster(int x, int y, Animation<TextureRegion> rightAnimation, Animation<TextureRegion> leftAnimation) {
            this.x = x;
            this.y = y;
            this.rightAnimation = rightAnimation;
            this.leftAnimation = leftAnimation;
            this.currentAnimation = rightAnimation;
        }

        public void update(float delta, Maze maze) {
            moveCooldown -= delta;
            if (moveCooldown > 0) return;

            int nextX = x + directionX;
            int nextY = y + directionY;

            if (maze.getTileType(nextX, nextY) == 0) {
                directionX = -directionX;
                directionY = -directionY;
            } else {
                x = nextX;
                y = nextY;
            }

            currentAnimation = (directionX > 0) ? rightAnimation : leftAnimation;
            moveCooldown = 1f;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public TextureRegion getCurrentFrame(float stateTime) {
            return currentAnimation.getKeyFrame(stateTime, true);
        }
    }
}
