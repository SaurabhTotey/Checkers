import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.TextArea;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

public class Main {
	
	public static Color offTileColor = Color.WHITE;
	public static Color onTileColor = Color.BLACK;
	
	public static Color P1Color = Color.RED;
	public static Color P1Highlight = Color.PINK;
	
	public static Color P2Color = Color.GRAY;
	public static Color P2Highlight = Color.LIGHT_GRAY;
	
	public static JFrame window = new JFrame("Checkers Game - By S");
	public static Font defaultFont;
	public static JLabel title = new JLabel("Checkers");
	public static TextArea log = new TextArea();
	
	public static CheckerBoard board;
	public static JPanel checkerBoardRep = new JPanel(new MigLayout(new LC(), new AC().grow(), new AC().grow()));

	public static boolean allowOutput = true;
	
	public static void main(String[] args){
		redirectSystemStreams();
		setWindowPosition(window, 0);
		window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		window.getContentPane().setLayout(new MigLayout(new LC(), new AC().grow(), new AC()));
		
		log("Initializing game and making GUI");
		title.setFont(defaultFont);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		window.getContentPane().add(title, "dock north");
		
		board = new CheckerBoard();
		checkerBoardRep.add(board.toJPanel(), "grow, push");
		window.getContentPane().add(checkerBoardRep, "grow, push");
		
		log.setEditable(false);
		log.setFocusable(false);
		log.setBackground(Color.WHITE);
		window.getContentPane().add(new JScrollPane(log), "dock east");
		
		window.setVisible(true);
		board.updateGUI();
	}
	
	private static void setWindowPosition(JFrame window, int screen){        
	    GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] allDevices = env.getScreenDevices();
	    int topLeftX, topLeftY, screenX, screenY, windowPosX, windowPosY;
	    
	    screen = (screen < allDevices.length && screen > -1) ? screen : 0;

	    topLeftX = allDevices[screen].getDefaultConfiguration().getBounds().x;
	    topLeftY = allDevices[screen].getDefaultConfiguration().getBounds().y;
	    
        screenX  = allDevices[screen].getDefaultConfiguration().getBounds().width;
        screenY  = allDevices[screen].getDefaultConfiguration().getBounds().height;
	        
        window.setBounds(0, 0, allDevices[screen].getDefaultConfiguration().getBounds().width / 2, allDevices[screen].getDefaultConfiguration().getBounds().height / 2);
	    
	    windowPosX = ((screenX - window.getWidth())  / 2) + topLeftX;
	    windowPosY = ((screenY - window.getHeight()) / 2) + topLeftY;
	    
	    defaultFont = new Font("Roman Baseline", Font.ROMAN_BASELINE, screenX / 75);

	    window.setLocation(windowPosX, windowPosY);
	}
	
	private static void updateTextArea(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				log.append(text);
		    }
		});
	}
	
	private static void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
		    @Override
		    public void write(int b) throws IOException {
		    	updateTextArea(String.valueOf((char) b));
		    }
		 
		    @Override
		    public void write(byte[] b, int off, int len) throws IOException {
		    	updateTextArea(new String(b, off, len));
		    }
		 
		    @Override
		    public void write(byte[] b) throws IOException {
		    	write(b, 0, b.length);
		    }
		  };
		 
		  System.setOut(new PrintStream(out, true));
		  System.setErr(new PrintStream(out, true));
	}
	
	public static void log(String messageToLog){
		if(!allowOutput){
			return;
		}
		messageToLog = "[" + new SimpleDateFormat("dd/MM/yy HH:mm:ss").format(Calendar.getInstance().getTime()) + "] : " + messageToLog;
		System.out.println(messageToLog);
	}

}
