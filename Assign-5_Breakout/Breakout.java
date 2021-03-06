import java.awt.Color;
import java.awt.event.MouseEvent;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;

/*
 * Simple Breakout Game
 * 
 * @author Daniel Reckziegel
 */

public class Breakout extends GraphicsProgram {

	private static final int NR_OF_ROWS = 5;
	private static final int NR_OF_COLUMS = 8;
	private static final int BRICK_HEIGHT = 20;
	private static final int BRICK_WIDTH = 40;
	private static final int CANVAS_HEIGHT = 800;
	private static final int CANVAS_WIDTH = 360;
	private static final double PADDLE_LENGTH = 80;
	private static final double PADDLE_HEIGTH = 10;
	private static final double BALL_SIZE = 20;
	private static final int START_WALL_X = 20;
	private static final int START_WALL_Y = 100;
	private static final int CANVAS_END = 80;

	GOval livePoint3;
	GOval livePoint2;
	GOval livePoint1;
	GOval ball;
	GRect paddle;
	GRect brick;
	GOval livePoint;
	GLabel labelScore;
	GLabel labelLive;
	int vx = 3;
	int vy = 3;
	int lives = 3;
	int score = 0;

	public void run() {
		setup();
		waitForClick();
		createCountdown();
		/* game loop */
		while (true) {
			moveBall();
			checkForCollisionBallWithWall();
			checkForCollisionBallWithPaddle();
			checkForCollisionBallWithBricks();
			checkScore();
			pause(10);
		}
	}

	/* setup method */
	private void setup() {
		setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		setBackground(Color.BLACK);
		createBall();
		createWall();
		createPaddle();
		createLives();
		createScore();
		addMouseListeners();
	}

	private void createScore() {
		labelScore = new GLabel("Score: " + score);
		labelScore.setColor(Color.WHITE);
		labelScore.setFont("SansSerif-15");
		add(labelScore, 250, 25);
	}

	private void checkScore() {
		if (score == 40) {
			removeAll();
			GLabel youWin = new GLabel("YOU WIN!");
			youWin.setColor(Color.WHITE);
			youWin.setFont("SansSerif-36");
			add(youWin, 65, 350);
		}
	}

	/* method to create the countdown */
	private void createCountdown() {
		GLabel ready = new GLabel("READY");
		ready.setColor(Color.WHITE);
		ready.setFont("SansSerif-36");
		add(ready, 130, 350);
		pause(500);
		remove(ready);
		pause(500);

		GLabel set = new GLabel("SET");
		set.setColor(Color.WHITE);
		set.setFont("SansSerif-36");
		add(set, 145, 350);
		pause(500);
		remove(set);
		pause(500);

		GLabel go = new GLabel("GO");
		go.setColor(Color.WHITE);
		go.setFont("SansSerif-36");
		add(go, 150, 350);
		pause(500);
		remove(go);
		pause(500);

	}

	/* method to create the 3 lifes in the top left corner */
	private void createLives() {
		int livePosX = 20;
		int livePosY = 35;

		labelLive = new GLabel("Lives:");
		labelLive.setColor(Color.WHITE);
		labelLive.setFont("SansSerif-15");
		add(labelLive, 20, 25);

		livePoint1 = new GOval(20, 20);
		livePoint1.setColor(Color.RED);
		livePoint1.setFilled(true);
		add(livePoint1, livePosX, livePosY);
		livePosX = livePosX + 20;

		livePoint2 = new GOval(20, 20);
		livePoint2.setColor(Color.RED);
		livePoint2.setFilled(true);
		add(livePoint2, livePosX, livePosY);
		livePosX = livePosX + 20;

		livePoint3 = new GOval(20, 20);
		livePoint3.setColor(Color.RED);
		livePoint3.setFilled(true);
		add(livePoint3, livePosX, livePosY);
		livePosX = livePosX + 20;
	}

	/* method for the collision with the bricks */
	private void checkForCollisionBallWithBricks() {
		double x = ball.getX();
		double y = ball.getY();
		GObject obj = getElementAt(x, y);
		if (obj != null) {
			if (obj == paddle) {
				vy = -vy;
			} else {
				score++;
				labelScore.setLabel("Score: " + score);
				remove(obj);
				vy = -vy;

			}
		}
	}

	/* method to check for collision with the paddle */
	private void checkForCollisionBallWithPaddle() {
		double x = ball.getX();
		double y = ball.getY();

		GObject obj = getElementAt(ball.getX(), ball.getY() + BALL_SIZE);
		if (obj != null) {
			if (obj == paddle) {
				vy = -vy;
			}
		}
	}

	/* method to check for collision with the walls, except the bottom wall */
	private void checkForCollisionBallWithWall() {
		if ((ball.getX() > CANVAS_WIDTH - BALL_SIZE) || (ball.getX() < 0)) {
			vx = -vx;
		}
		if ((ball.getY() < CANVAS_END)) {
			vy = -vy;
		}

		// If the ball goes out at the bottom it takes a life and it is removed until
		// you are GameOver
		if ((ball.getY() > CANVAS_HEIGHT)) {
			lives = lives - 1;
			if (lives == 2) {
				remove(livePoint3);
				threeLivesMethod();
			} else if (lives == 1) {
				remove(livePoint2);
				threeLivesMethod();
			} else if (lives == 0) {
				remove(livePoint1);
				threeLivesMethod();
			} else {
				removeAll();
				GLabel GameOver = new GLabel("GAME OVER!");
				GameOver.setColor(Color.WHITE);
				GameOver.setFont("SansSerif-36");
				add(GameOver, 65, 350);
			}
		}
	}

	/* method which restarts the game after a lost life (3 times) */
	private void threeLivesMethod() {
		createCountdown();
		createBall();
		moveBall();
	}

	/* method to create the paddle */
	private void createPaddle() {
		paddle = new GRect(PADDLE_LENGTH, PADDLE_HEIGTH);
		paddle.setColor(Color.WHITE);
		paddle.setFilled(true);
		add(paddle, 140, 600);
	}

	/* Method for generating the mouse tracker for the paddle */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		paddle.setLocation(x, 600);
	}

	/* Method for creating the wall with the individual bricks */
	private void createWall() {
		int y = START_WALL_Y;
		for (int i = 0; i < NR_OF_ROWS; i++) {
			drawOneRow(y);
			y = y + BRICK_HEIGHT;
		}
	}

	/* Method for creating a series of bricks for the wall */
	private void drawOneRow(int y) {
		int x = START_WALL_X;
		for (int i = 0; i < NR_OF_COLUMS; i++) {
			brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
			RandomGenerator rgen = new RandomGenerator();
			Color farbenz = rgen.nextColor();
			brick.setFillColor(farbenz);
			brick.setFilled(true);
			add(brick, x, y);
			x = x + 40;
		}
	}

	/* method to create the ball */
	public void createBall() {
		ball = new GOval(BALL_SIZE, BALL_SIZE);
		ball.setColor(Color.WHITE);
		ball.setFilled(true);
		add(ball, 180, 380);
	}

	/* method that makes the ball move */
	private void moveBall() {
		ball.move(vx, vy);
	}
}
