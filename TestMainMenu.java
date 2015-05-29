import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Color;

public class TestMainMenu extends JPanel implements ActionListener {

	private JButton play;
	private JButton howToPlay;
	private CardLayout cards;
	private FlappyPanel gamePanel;
	private MainMenu menu;
	private InstructionPanel instructions;

	public TestMainMenu() {
		cards = new CardLayout();
		setLayout(cards);
		System.out.println(getLayout());
		play = new JButton("PLAY");
		howToPlay = new JButton("HOW TO PLAY");
		menu = new MainMenu();
		gamePanel = new FlappyPanel();
		instructions = new InstructionPanel();
		add(menu, "Main Menu");
		add(gamePanel, "Game");
		add(instructions, "Instructions");
		menu.add(play);
		play.setSize(1000, 200);
		play.setLocation(580, 520);
		play.addActionListener(this);
		menu.add(howToPlay);
		howToPlay.setSize(1000, 200);
		howToPlay.setLocation(580, 820);
		howToPlay.addActionListener(new ShowInstructions());
		cards.show(this, "Main Menu");
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		frame.getContentPane().add(new TestMainMenu());
		frame.setVisible(true);
		frame.setSize(2160, 1440);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
	}

	private class ShowInstructions implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			show("Instructions");
		}
	}

	public void show(String s) {
		cards.show(this, s);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.BLACK);
	}

	public void actionPerformed(ActionEvent e) {
		show("Game");
	}
}