import javax.swing.JButton;

@SuppressWarnings("serial")
public class Checker extends JButton {

	public int x;
	public int y;
	public boolean isKing = false;
	public CheckerFunction function;
	
	public Checker(int x, int y) {
		this.x = x;
		this.y = y;
		Main.log("Making checker at (" + x + ", " + y + ")");
		this.kill();
		this.setFont(Main.defaultFont);
	}
	
	public void updateChecker() {
		Main.log("Updating checker at (" + x + ", " + y + ")");
		setChecker(this.function);
		Main.log("Checking if checker at (" + x + ", " + y + ") is a king");
		if(!this.isKing && (this.y == 0 && this.function == CheckerFunction.P1Checker || this.y == Main.board.length - 1 && this.function == CheckerFunction.P2Checker)){
			Main.log("Making checker at (" + x + ", " + y + ") a king!");
			this.isKing = true;
			this.setText("K");
		}
	}
	
	public void setChecker(CheckerFunction newFunction){
		switch(newFunction){
			case P1Highlight : 
				this.setBackground(Main.P1Highlight);
				this.setVisible(true);
		    	break;
			case P2Highlight : 
				this.setBackground(Main.P2Highlight);
				this.setVisible(true);
				break;
			case P1Checker : 
				this.setBackground(Main.P1Color);
				this.setVisible(true);
				break;
			case P2Checker : 
				this.setBackground(Main.P2Color);
				this.setVisible(true);
				break;
			case Invisible : 
				this.kill();
				break;
		}
		this.function = newFunction;
		Main.log("Set checker at at (" + x + ", " + y + ") to status of " + this.function.toString());
	}
	
	public void kill(){
		Main.log("Killing checker at at (" + x + ", " + y + ")");
		this.setText("");
		this.setVisible(false);
		this.function = CheckerFunction.Invisible;
	}
	
	@Override
	public Checker clone(){
		Checker toReturn = new Checker(this.x, this.y);
		toReturn.setChecker(this.function);
		toReturn.isKing = this.isKing;
		toReturn.setText(this.getText());
		toReturn.updateChecker();
		return toReturn;
	}
	
}
