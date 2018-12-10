//Katie Gardner, Basketball Statistics
//Program utilizes binary and text serialization
//Program creates two types of points (make and miss)
//Make is filled in circle, miss is outline of circle
//Every time there is a click, the number of shots is record

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class Shot implements Serializable {
	private int x;
	private int y;
	private int shape;
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Shot() {
		x = 0;
		y = 0;
	}
	public Shot(int x, int y) {
		setX(x);
		setY(y);
	}
	@Override
	public String toString() {
		return String.format("%d %d",x,y);
	}
}

@SuppressWarnings("unchecked")
class ShotSerialization {
	public static void main(String[] args) {
		ArrayList<Shot> shots = new ArrayList<Shot>();
		shots.add(new Shot(7,12));
		shots.add(new Shot(13,11));
		shots.add(new Shot(5,10));
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
				new File("shots.bin")));
			oos.writeObject(shots);
			oos.close();
		} catch (Exception ex) {

		}
		System.out.println("Will now read them back in: ");
		ArrayList<Shot> readShots;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				new File("shots.bin")));
			readShots = (ArrayList<Shot>)(ois.readObject());
			ois.close();
			for (Shot s : readShots) {
				System.out.println(s);
			}
		} catch (Exception ex) {

		}
	}
}

class ShotPanel extends JPanel implements MouseListener {
	private ArrayList<Shot> shots;
	private String message;
	private int pointSize;
	private int shape;
	private int makeshot;
	private int totalshot;
	
	public int getPointSize() {
		return pointSize;
	}
	public void setPointSize(int pointSize) {
		this.pointSize = pointSize;
	}
	public int getShape() {
		return shape;
	}
	public void setShape(int shape) {
		this.shape = shape;
	}
	
	//MOUSE EVENTS
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		shots.add(new Shot(x,y));
		repaint();
	}
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		shots.add(new Shot(x,y));
		if (shape == 0 ) {
			makeshot = makeshot + 1;
		}
		totalshot = totalshot + 1;
		message = "Current Stats: " +makeshot+ "/" +totalshot;
		repaint();
	}
	public void mouseReleased(MouseEvent e) {}
	
	public ShotPanel(ArrayList<Shot>shots) {
		this.shots = shots;
		setFocusable(true);
		addMouseListener(this);
		message = "Welcome to Shot Tracker";
		pointSize = 25;
	}
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawRect(10,10,500,470);
		g.drawRect(180,10,165,210);
		g.drawArc(40, -290, 440, 600, 0, -180);
		for (Shot s : shots) {
			if (shape == 0) { 
				//MAKE
				g.fillOval(s.getX(), s.getY(), pointSize, pointSize);
			} else {
				//MISS
				g.drawOval(s.getX(), s.getY(), pointSize, pointSize);
			}	
		}
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString(message, 180, 505);
	}
}

class ShotFrame extends JFrame implements ActionListener {
	private ArrayList<Shot> shots;
	private ShotPanel span;
	private int makeshot;
	private int totalshot;
	
	public void configureMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu mnuFile = new JMenu("File");
		JMenuItem miOpen = new JMenuItem("Open");
		miOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				String line;
				String[] parts;
				if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					try {
						File f = jfc.getSelectedFile();
						Scanner sc = new Scanner(f);
						shots.clear();
						while(sc.hasNextLine()) {
							line = sc.nextLine().trim();
							parts = line.split(" ");
							shots.add(new Shot(Integer.parseInt(parts[0]),Integer.parseInt(parts[1])));
						}
						sc.close();
						repaint();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,"Could not open the file.");
					}
				}
			}
		});
		JMenuItem miSave = new JMenuItem("Save");
		miSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser();
				if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					try {
						File f = jfc.getSelectedFile();
						PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(f)));
						for (Shot s : shots) {
							pw.println(s);
						}
						pw.close();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null,"Could not save file.");
					}
				}
			}
		});

		JMenuItem miExit = new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnuFile.add(miOpen);
		mnuFile.add(miSave);
		mnuFile.add(miExit);
		bar.add(mnuFile);
		setJMenuBar(bar);

	}
	
	public void configureUI() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100,100,540,620);
		setTitle("Statistic Recorder");
		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		span = new ShotPanel(shots);
		c.add(span,BorderLayout.CENTER);
		JPanel panSouth = new JPanel();
		panSouth.setLayout(new FlowLayout());
		JButton btnMake = new JButton("Make");
		btnMake.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						span.setShape(0);
						repaint();
					}
				}
		);
		JButton btnMiss = new JButton("Miss");
		btnMiss.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						span.setShape(5);
						repaint();
					}
				}
		);
		panSouth.add(btnMake);
		panSouth.add(btnMiss);
		c.add(panSouth,BorderLayout.SOUTH);
		configureMenu();
	}
	public ShotFrame(ArrayList<Shot> shots) {
		this.shots = shots;
		configureUI();
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
public class Basketball {
	public static void main(String[] args) {
		ArrayList<Shot> shots = new ArrayList<Shot>();
		ShotFrame sf = new ShotFrame(shots);
		sf.setVisible(true);
	}
}
