package com.monochromatic.god_of_fire.entity;
import java.awt.*;

import javax.vecmath.Vector2d;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SpriteSheet;
import com.monochromatic.god_of_fire.enums.Direction;
import com.monochromatic.god_of_fire.state.GameState;

/**
 * Base entity class. All entities need to be registered with the
 * {@link EntityController} if they are to be interactive with the rest of the
 * game world.
 * 
 * @author calmattier
 */
public abstract class Entity {
	protected GameState gameState; /**Game State */
	
	protected Point location; /** Current location of the entity */
	private Point previous; /** Previous location of the entity */
	protected int level = 1; /**Current floor level. */
	protected Direction orientation; /** Orientation of the entity. */
	protected int movementSpeed = 0;
	protected Vector2d velocity = new Vector2d(0, 0);
	
	protected boolean hardCollision = false;
	protected boolean setForRemoval = false;
	protected boolean initComplete = false;
	protected boolean movedBack = false;
	
	/** Spritesheet for all images **/
	protected Image spriteSheet;
	/** Array of images for multidirectional movement **/
	protected SpriteSheet 	upwardsMovementImages, 
							leftMovementImages, 
							downwardMovementImages, 
							rightMovementImages;
	/** What animation is currently being used **/
	protected Animation currentAnimation;
	/** Animations for the entity **/
	protected Animation 	upwardsMovementAnimation, 
							downwardMovementAnimation, 
							leftMovementAnimation,
							rightMovementAnimation;
	
	/** Array of images and animation for when the entity is not moving */
	protected SpriteSheet stationaryImages;
	protected Animation stationaryAnimation;
	
	/** Array of images and animation for entities physical attacks */
	protected Image[] attackingImages, castingImages;
	protected Animation attackingAnimation, castingAnimation;
		
	
	public Entity(GameState g, int x, int y){
		this(g, x, y, Direction.DOWN);
	}
	
	public Entity(GameState g, int x, int y, Direction d){
		this(g, x, y, d, 0);
	}
	
	public Entity(GameState g, int x, int y, Direction d, int s){
		gameState = g;
		this.location = new Point(x, y);
		this.previous = new Point(x, y);
		this.orientation = d;
		this.movementSpeed = s;
	}
	
	/**
	 * Updates this entity. Movement and AI happen at this step.
	 */
	public abstract void update(GameContainer g);
	
	/**
	 * Renders this entity.
	 */
	public abstract void render();
	
	/**
	 * Applies the effects on the given entity, if it were to collide with
	 * this entity.
	 */
	public abstract void collide(Entity e);
	
	/**
	 * Looks like a clusterfuck now, but it will improve!
	 * Basically, if we can have the same format on all the sprite sheets, this one
	 * block of code will take care of it for every single entity.
	 * I just left it all as is for now as a place holder and so everyone can see whats to come
	 * @throws SlickException
	 */
	public void init() throws SlickException{
		// TODO
		downwardMovementImages=new SpriteSheet(spriteSheet.getSubImage(0, 0, 192, 62), 32, 62);
		leftMovementImages=new SpriteSheet(spriteSheet.getSubImage(0, 64, 192, 64), 32, 64);
		upwardsMovementImages=new SpriteSheet(spriteSheet.getSubImage(0, 128, 192, 64), 32, 64);
		rightMovementImages=new SpriteSheet(spriteSheet.getSubImage(0, 192, 192, 64),32, 64);
		
		/**
		stationaryImages = new Image[] { 
				spriteSheet.getSubImage(x, y),
				spriteSheet.getSubImage(x, y)};
		attackingImages = new Image[] { 
				spriteSheet.getSubImage(x, y),
				spriteSheet.getSubImage(x, y)};
		castingImages = new Image[] { 
				spriteSheet.getSubImage(x, y),
				spriteSheet.getSubImage(x, y)};
		*/

		upwardsMovementAnimation=new Animation(upwardsMovementImages, 100);
		downwardMovementAnimation=new Animation(downwardMovementImages, 100);
		rightMovementAnimation=new Animation(rightMovementImages, 100);
		leftMovementAnimation=new Animation(leftMovementImages, 100);
		currentAnimation=upwardsMovementAnimation;
	
		/**
		stationaryAnimation=new Animation(stationaryImages, 1, false);
		attackingAnimation=new Animation(attackingImages, 1, false);
		castingAnimation=new Animation(castingImages, 1, false);
		*/
		initComplete=true;
	}
	
	public void initSingleSpriteSheet(int height, int width) throws SlickException{
		stationaryImages = new SpriteSheet(spriteSheet, height, width);
		stationaryAnimation=new Animation(stationaryImages, 300);
		upwardsMovementAnimation=new Animation(stationaryImages, 300);
		downwardMovementAnimation=new Animation(stationaryImages, 300);
		rightMovementAnimation=new Animation(stationaryImages, 300);
		leftMovementAnimation=new Animation(stationaryImages, 300);
		currentAnimation=stationaryAnimation;
	}
	
	public void setImage(String filePath){
		try {
			spriteSheet=new SpriteSheet(filePath, 32, 64);
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Moves the entity in the given direction and changes to the corresponding
	 * animation.
	 */
	@Deprecated
	public void move(Direction d) {
		if (d == Direction.UP) move(new Vector2d(0, -movementSpeed));
		if (d == Direction.DOWN) move(new Vector2d(0, movementSpeed));
		if (d == Direction.LEFT) move(new Vector2d(-movementSpeed, 0));
		if (d == Direction.RIGHT) move(new Vector2d(movementSpeed, 0));
	}
	
	public void move() {
		move(velocity);
	}

	public void move(Vector2d v) {
		movedBack = false;
		Direction d;
		if (Math.abs(v.x) > Math.abs(v.y))
			if (v.x > 0) {
				moveRight();
				d = Direction.RIGHT;
			}
			else {
				moveLeft();
				d = Direction.LEFT;
			}
		else
			if (v.y > 0) {
				moveDown();
				d = Direction.DOWN;
			}
			else {
				moveUp();
				d = Direction.UP;
			}
		stopAnimations(d);
		previous.setLocation(location.getX(), location.getY());
		location.translate((int) v.x, (int) v.y);
	}
	
	public void stopAnimations(Direction d) {
		if(currentAnimation == null)
			return;
		if (!(d == Direction.UP) && !upwardsMovementAnimation.isStopped())
			upwardsMovementAnimation.stop();
		if (!(d == Direction.LEFT) && !leftMovementAnimation.isStopped())
			leftMovementAnimation.stop();
		if (!(d == Direction.DOWN) && !downwardMovementAnimation.isStopped())
			downwardMovementAnimation.stop();
		if (!(d == Direction.RIGHT) && !rightMovementAnimation.isStopped())
			rightMovementAnimation.stop();
	}
	
	/**
	 * Moves the entity up and changes corresponding animations.
	 */
	private void moveUp() {
		if(currentAnimation == null)
			return;
		orientation(Direction.UP);
		upwardsMovementAnimation.start();
		currentAnimation = upwardsMovementAnimation;
	}
	
	/**
	 * Moves the entity down and changes corresponding animations.
	 */
	private void moveDown() {
		if(currentAnimation == null)
			return;
		orientation(Direction.DOWN);
		downwardMovementAnimation.start();
		currentAnimation = downwardMovementAnimation;
	}
	
	/**
	 * Moves the entity to the left and changes corresponding animations.
	 */
	private void moveLeft() {
		if(currentAnimation == null)
			return;
		orientation(Direction.LEFT);
		leftMovementAnimation.start();
		currentAnimation = leftMovementAnimation;
	}
	
	/**
	 * Moves the entity to the right and changes corresponding animations.
	 */
	private void moveRight() {
		if(currentAnimation == null)
			return;
		orientation(Direction.RIGHT);
		rightMovementAnimation.start();
		currentAnimation = rightMovementAnimation;
	}
	
	/**
	 * Moves this entity back to the previous location.
	 */
	public void movePrevious() {
		double dX = previous.getX() - location.getX();
		double dY = previous.getY() - location.getY();
		location.translate((int) dX, (int) dY);
		movedBack = true;
	}
	
	/**
	 * Sets this entities location.
	 */
	public void location(Point p) {
		previous.setLocation(location.getX(), location.getY());
		location.setLocation(p.getX(), p.getY());
	}
	
	/**
	 * Returns a copy of this entities location.
	 */
	public Point location() {
		return (Point) location.clone();
	}
	
	/**
	 * Returns a copy of this entities previous location.
	 */
	public Point previous() {
		return (Point) previous.clone();
	}
	
	/**
	 * Gets orientation of the entity
	 */
	public Direction orientation(){
		return orientation;
	}
	
	/**
	 * Sets orientation of the entity
	 * @param d
	 */
	public void orientation(Direction d){
		orientation = d;
	}
	
	public Vector2d getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2d velocity) {
		this.velocity = velocity;
	}
	
	public int getSpeed() {
		return movementSpeed;
	}

	public void setSpeed(int movementSpeed) {
		this.movementSpeed = movementSpeed;
	}

	/**
	 * Retrieves the current room level for this entity.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the current room level for this entity.
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	/**
	 * Checks the hard collision value for this entity. If set to true, this
	 * entity cannot occupy the same tile as another entity and will act
	 * like a wall.
	 */
	public boolean isHardCollision() {
		return hardCollision;
	}

	/**
	 * Sets the hard collision value for this entity. If set to true, this
	 * entity cannot occupy the same tile as another entity and will act
	 * like a wall.
	 */
	public void setHardCollision(boolean hardCollision) {
		this.hardCollision = hardCollision;
	}
	
	/**
	 * Checks to see if this entity has been flagged for removal.
	 */
	public boolean isSetForRemoval() {
		return setForRemoval;
	}
	
	/**
	 * Sets this entities removal flag.
	 */
	public void setForRemoval(boolean b) {
		setForRemoval = b;
	}
	
	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
}