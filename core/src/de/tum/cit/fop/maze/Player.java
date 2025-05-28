package de.tum.cit.fop.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player {

    // Character position and lives
    private float characterX;
    private float characterY;
    private int lives = 3;

    // Character animation
    private final Animation<TextureRegion> characterDownAnimation;
    private final Animation<TextureRegion> characterUpAnimation;
    private final Animation<TextureRegion> characterLeftAnimation;
    private final Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> currentCharacterAnimation;
    private float stateTime;

    private final int tileSize = 64;

    public Player(MazeRunnerGame game) {


        // Load character animations
        characterDownAnimation = game.getCharacterDownAnimation();
        characterUpAnimation = game.getCharacterUpAnimation();
        characterLeftAnimation = game.getCharacterLeftAnimation();
        characterRightAnimation = game.getCharacterRightAnimation();
        currentCharacterAnimation = characterDownAnimation;
        stateTime = 0;

        // Initialize character position
        characterX = 0;
        characterY = 0;

    }

    public void renderCharacter(MazeRunnerGame game) {
        TextureRegion currentFrame = currentCharacterAnimation.getKeyFrame(stateTime, true);
        game.getSpriteBatch().draw(currentFrame, characterX, characterY, tileSize, tileSize);
    }

    public void handleCharacterMovement(float delta, Maze maze) {
        float speed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 300 * delta : 150 * delta;

        float newX = characterX;
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

        if (canMoveTo(newX, characterY, maze)) {
            characterX = newX;
        }
        if (canMoveTo(characterX, newY, maze)) {
            characterY = newY;
        }
    }

        private boolean canMoveTo(float x, float y, Maze maze) {
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

    public float getCharacterX() {
        return characterX;
    }

    public void setCharacterX(float characterX) {
        this.characterX = characterX;
    }

    public float getCharacterY() {
        return characterY;
    }

    public void setCharacterY(float characterY) {
        this.characterY = characterY;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
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

    public Animation<TextureRegion> getCurrentCharacterAnimation() {
        return currentCharacterAnimation;
    }

    public void setCurrentCharacterAnimation(Animation<TextureRegion> currentCharacterAnimation) {
        this.currentCharacterAnimation = currentCharacterAnimation;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public int getTileSize() {
        return tileSize;
    }
}
