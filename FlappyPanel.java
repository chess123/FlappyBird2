import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * This is the bulk of the code - the game itself is displayed through this panel.
 * @author Kesav Viswanadha
 * @contributor Ofek Gila
 * @version 1.8
 * @lastedited May 24, 2015
*/

public class FlappyPanel extends JPanel implements ActionListener, KeyListener {
	
	private FlappyPipe[] pipes;
	private Timer movePipes;
	private boolean first;
	private Bird bird;
	private int invincibleTimes;
	private boolean firstPress;
	private ArrayList<String> previousScores;
	private boolean justDied;
	private ImageIcon theBird;
	private ImageIcon bird1, bird2;
	private boolean imgLoaded;
	private Timer invincibility;
	private Color invincibleColor;
	private AudioList songs;
	private ArrayList<String> songNames;
	private AudioClip clip;
	private ImageIcon thePipe;
	private int headBangs;
	private int score;
	private int count;

	public FlappyPanel() {
		clip = new AudioClip("MarioInvincible.mp3");
		songs = new AudioList(AudioList.INITIAL_SHUFFLE, AudioList.SUBFOLDERS_AND_CURRENT);
		songs.play();
		previousScores = new ArrayList<String>();
		movePipes = new Timer(20, this);
		invincibility = new Timer(25, new Handler());
		first = true;
		firstPress = true;
		headBangs = count = score = 0;
		bird = new Bird(this);
		addKeyListener(this);
		justDied = true;
		imgLoaded = true;
		invincibleColor = Color.BLACK;
		theBird = new ImageIcon(getClass().getResource("FlappyBirdOnline.png"));
		bird1 = new ImageIcon(getClass().getResource("bird1.png"));
		bird2 = new ImageIcon(getClass().getResource("bird2.png"));
		thePipe = new ImageIcon(getClass().getResource("Pipe.png"));
	}

	public int getScore()	{
		return score / 2;
	}

	private class Handler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			invincibleTimes++;
			if (!clip.isRunning()) {
				clip.play();
				songs.pause();
			}
			invincibleColor = new Color(255 - invincibleTimes, 0, 0);
			if (invincibleTimes == 255) {
				bird.setInvincible(false);
				invincibility.stop();
				invincibleTimes = 0;
				clip.stop();
				songs.play();
			}
		}
	}

	public void paintComponent(Graphics g) {
		requestFocus();
		if (first) {
			first = false;
			pipes = new FlappyPipe[4];
			for (int i = 0; i < pipes.length; i++) {
				pipes[i] = new FlappyPipe(this, 2, i * (2160 / 4) + 2160 / 4);
				pipes[i].setOscillation(0);
			}
		}
		super.paintComponent(g);
		setBackground(Color.BLACK);
		for (FlappyPipe fp : pipes) {
			g.setColor(fp.getColor());
			if (fp.isInvincible()) {
				g.fillRect(fp.getX(), 0, 50, fp.getY());
				g.fillRect(fp.getX(), fp.getY() + 200, 50, getHeight() - (fp.getY() + 200));
			}
			else {
				g.drawImage(thePipe.getImage(), fp.getX(), 0, 50, fp.getY(), this);
				g.drawImage(thePipe.getImage(), fp.getX(), fp.getY() + 200, 50, getHeight() - (fp.getY() + 200), this);
			}
		}
		if (bird.isInvincible())
			if (count % 2 == 0)
				g.drawImage(bird1.getImage(), 50, bird.getY(), 50, 50, this);
			else g.drawImage(bird2.getImage(), 50, bird.getY(), 50, 50, this);
		else
			if (count % 20 <= 10)
				 g.drawImage(bird1.getImage(), 50, bird.getY(), 50, 50, this);
			else g.drawImage(bird2.getImage(), 50, bird.getY(), 50, 50, this);
		if (dying() || dead()) {
			movePipes.stop();
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 96));
			g.drawString("YOU LOSE!", 800, 700);
			if (justDied) {
				saveScore();
				justDied = false;
			}
		}
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 96));
		g.drawString("SCORE: " + getScore(), 800, 800);
		if (firstPress) {
			g.setColor(new Color(0, 100, 0));
			g.drawString("GET READY!", 780, 700);
		}
	}

	public void actionPerformed(ActionEvent e) {
		count++;
		for (FlappyPipe fp : pipes) {
			fp.move();
		}
		if (getScore() != 0) {
			for (FlappyPipe fp : pipes) {
				fp.incVelocity(bird.isInvincible() ? (2*velocityInc(fp.getVelocity())):velocityInc(fp.getVelocity()));
			}
		}
		repaint();
	}

	public double velocityInc(double num)	{
		return 1 / (100 * Math.sqrt(num));
	}

	public void saveScore() {
		Scanner getScores = null;
		try {
			getScores = new Scanner(new File("scores.txt"));
		} catch (IOException e) {
			System.out.println("ERROR: scores.txt does not exist");
		}
		if (getScores != null) {
			while (getScores.hasNext()) {
				previousScores.add(getScores.nextLine());
			}
		}
		int i;
		for (i = 0; i < previousScores.size() && Integer.parseInt(previousScores.get(i).substring(0, previousScores.get(i).indexOf(" "))) > getScore(); i++) {}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
		previousScores.add(i, getScore() + " on " + sdf.format(date));
		PrintWriter writeScores = null;
		try {
			writeScores = new PrintWriter(new File("scores.txt"));
		} catch (IOException e) {
			System.out.println("ERROR: Could not open scores.txt for writing");
			return;
		}
		for (String s : previousScores) {
			writeScores.println(s);
		}
		writeScores.close();
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP && !dying() && !dead()) {
			if (firstPress) {
				movePipes.start();
				bird.startFalling();
				firstPress = false;
			}
			//clip.play(9000000);
			bird.setVelocity(13);
			//flap.loadNPlay();
			repaint();
		}
		if (e.getKeyChar() == 'r' && dead()) {
			bird = new Bird(this);
			for (int i = 0; i < pipes.length; i++) {
				pipes[i] = new FlappyPipe(this, 2, i * (2160 / 4) + 2160 / 4);
				pipes[i].setOscillation(0);
			}
			firstPress = true;
			justDied = true;
			score = 0;
			previousScores.clear();
			repaint();
		}
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public boolean dead() {
		if (bird.getY() + 50 > getHeight() || (bird.getY() < 0)) return true;
		for (FlappyPipe fp : pipes) {
			if (fp.isReset())
				score++;
			if (!bird.isInvincible() && fp.getX() >= 0 && fp.getX() <= 100 &&
				(fp.getY() <= bird.getY() - 150 || fp.getY() >= bird.getY())) {
				if (fp.isInvincible()) {
					bird.setInvincible(true);
					invincibility.start();
					invincibleColor = Color.RED;
					return false;
				}
				else return true;
			}
		}
		return false;
	}

	public boolean dying() {
		return bird.isFalling() && !movePipes.isRunning();
	}
}