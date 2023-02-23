import java.awt.*; // Using AWT's Graphics and Color
import java.awt.event.*; // Using AWT event classes and listener interfaces
import java.util.Random;
import javax.swing.*; // Using Swing's components and containers

/** Custom Drawing Code Template */
// A Swing application extends javax.swing.JFrame
public class Bezier extends JFrame {
	// Define constants
	public static final int CANVAS_WIDTH = 640;
	public static final int CANVAS_HEIGHT = 480;
	public static final int POINTSN = 100;

	// Declare an instance of the drawing canvas,
	// which is an inner class called DrawCanvas extending javax.swing.JPanel.
	private DrawCanvas canvas;
	private Point mid;
	Point samples[];
    Point controlPoints[];

	// Constructor to set up the GUI components and event handlers
	public Bezier() {
		float delta = 1.0f / POINTSN;
		Random random = new Random();
		
		controlPoints = new Point[20];
		for(int j = 0; j < controlPoints.length; j++) {
			float x = j / (float)controlPoints.length;
			controlPoints[j] = new Point(x, random.nextFloat());
		}
		
	    samples = new Point[POINTSN + 1];
		for (int i = 0; i < POINTSN + 1; i++) {
			float t = i * delta;
			samples[i] = BezierInterpolate(controlPoints, t);
		}
		
		canvas = new DrawCanvas(); // Construct the drawing canvas
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent evt) {
				mid = new Point(evt.getX() / (float) CANVAS_WIDTH, evt.getY() / (float) CANVAS_HEIGHT);
				repaint(); // invoke paintComponent()
			}
		});
		
		// Set the Drawing JPanel as the JFrame's content-pane
		Container cp = getContentPane();
		cp.add(canvas);
		// or "setContentPane(canvas);"
		setDefaultCloseOperation(EXIT_ON_CLOSE); // Handle the CLOSE button
		pack(); // Either pack() the components; or setSize()
		setTitle("......"); // "super" JFrame sets the title
		setVisible(true); // "super" JFrame show
	    
	}

	/**
	 * Define inner class DrawCanvas, which is a JPanel used for custom drawing.
	 */
	private class DrawCanvas extends JPanel {
		// Override paintComponent to perform your own painting
		@Override
		public void paintComponent(Graphics g) {
	
			super.paintComponent(g); // paint parent's background

			PointerInfo pointerInfo = MouseInfo.getPointerInfo();
			java.awt.Point b = pointerInfo.getLocation();
			int x = (int) b.getX();
			int y = (int) b.getY();	
			
			float delta = 1.0f / POINTSN;
			Point P1 = new Point(0.1f, 0.5f);
			Point P2 = new Point(x / (float) CANVAS_WIDTH, y / (float) CANVAS_HEIGHT);
			Point P3 = new Point(0.9f, 0.5f);
			Point points[] = new Point[POINTSN + 1];
			for (int i = 0; i < POINTSN + 1; i++) {
				float t = i * delta;
				Point P1P2 = Interpolate(P1, P2, t);
				Point P2P3 = Interpolate(P2, P3, t);
				points[i] = Interpolate(P1P2, P2P3, t);
				// System.out.println(points[i].toString());
			}
			for (int i = 0; i < POINTSN; i++) {
				Point A = points[i];
				Point B = points[i + 1];
				g.drawLine((int) (A.x * CANVAS_WIDTH), (int) (A.y * CANVAS_HEIGHT), (int) (B.x * CANVAS_WIDTH),
						(int) (B.y * CANVAS_HEIGHT));
			}
			for (int i = 0; i < POINTSN; i++) {
				Point A = samples[i];
				Point B = samples[i + 1];
				g.drawLine((int) (A.x * CANVAS_WIDTH), (int) (A.y * CANVAS_HEIGHT), (int) (B.x * CANVAS_WIDTH),
						(int) (B.y * CANVAS_HEIGHT));
			}
			for (int i = 0; i < controlPoints.length; i++) {
				Point A = controlPoints[i];
				g.drawOval((int) (A.x * CANVAS_WIDTH), (int) (A.y * CANVAS_HEIGHT), 5, 5);
			}
		}
	}

	public static Point Interpolate(Point a, Point b, float t) {
		Point A = a.Mult(1 - t);
		Point B = b.Mult(t);
		return A.Add(B);
	}

	public class Point {
		float x, y;

		Point() {
			x = 0.0f;
			y = 0.0f;
		}

		Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public Point Add(Point other) {
			return new Point(x + other.x, y + other.y);
		}

		public Point Mult(float v) {
			return new Point(v * x, v * y);
		}

		public String toString() {
			return "(" + x + "," + y + ")";
		}
	}
	
	public static Point BezierInterpolate(Point controlPoints[], float t) {
			return BezierInterpolateHelper(controlPoints, t, 0, controlPoints.length - 1);
	}
	
	public static Point BezierInterpolateHelper(Point controlPoints[], float t, int begin, int end) {
		if (begin >= end) {
			return controlPoints[begin];
		} else {
			Point a = BezierInterpolateHelper(controlPoints, t, begin, end - 1);
			Point b = BezierInterpolateHelper(controlPoints, t, begin + 1, end);
			return Interpolate(a, b, t);
		}
	}

	// The entry main method
	public static void main(String[] args) {
		// Run the GUI codes on the Event-Dispatching thread for thread safety
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Bezier(); // Let the constructor do the job
			}
		});
	}
}