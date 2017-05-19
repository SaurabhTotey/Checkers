import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class CheckerBoard {
	
	public int length = 8;
	public int startingRows = 3;
	public Checker[][] checkers = new Checker[length][length];
	public BoardStatus status;
	public int selectedX = -1;
	public int selectedY = -1;
	public List<PossibleMove> possibleNewDestinations = new ArrayList<PossibleMove>();

	public CheckerBoard() {
		assert(length % 2 == 0): "Board length is not even";
		Main.log("Constructing board with length " + length);
		for(int i = 0; i < length; i++){
			for(int j = 0; j < length; j++){
				checkers[j][i] = new Checker(j, i);
				checkers[j][i].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int clickedX = ((Checker) e.getSource()).x;
						int clickedY = ((Checker) e.getSource()).y;
						Main.log("Checker at (" + clickedX + ", " + clickedY + ") was clicked");
						if((status == BoardStatus.P1Wait || status == BoardStatus.P2Wait) && clickedX == selectedX && clickedY == selectedY){
							Main.log("Previous selection was cancelled");
							killAllHighlights();
							status = (status == BoardStatus.P1Wait)? BoardStatus.P1Turn : BoardStatus.P2Turn;
						}else if((status == BoardStatus.P1Turn || status == BoardStatus.P1Wait) && checkers[clickedX][clickedY].function == CheckerFunction.P1Checker){
							Main.log("Player One is looking for possible moves from (" + clickedX + ", " + clickedY + ")");
							killAllHighlights();
							possibleNewDestinations = getValidMovesFor(checkers[clickedX][clickedY], null);
							for(int i = 0; i < possibleNewDestinations.size(); i++){
								checkers[possibleNewDestinations.get(i).newLocation[0]][possibleNewDestinations.get(i).newLocation[1]].setChecker(CheckerFunction.P1Highlight);
							}
							selectedX = clickedX;
							selectedY = clickedY;
							status = BoardStatus.P1Wait;
						}else if((status == BoardStatus.P2Turn || status == BoardStatus.P2Wait) && checkers[clickedX][clickedY].function == CheckerFunction.P2Checker){
							Main.log("Player Two is looking for possible moves from (" + clickedX + ", " + clickedY + ")");
							killAllHighlights();
							possibleNewDestinations = getValidMovesFor(checkers[clickedX][clickedY], null);
							for(int i = 0; i < possibleNewDestinations.size(); i++){
								checkers[possibleNewDestinations.get(i).newLocation[0]][possibleNewDestinations.get(i).newLocation[1]].setChecker(CheckerFunction.P2Highlight);
							}
							selectedX = clickedX;
							selectedY = clickedY;
							status = BoardStatus.P2Wait;
						}else if(status == BoardStatus.P1Wait && checkers[clickedX][clickedY].function == CheckerFunction.P1Highlight){
							Main.log("Player One is moving from (" + selectedX + ", " + selectedY + ") to (" + clickedX + ", " + clickedY + ")");
							moveChecker(checkers[selectedX][selectedY], checkers[clickedX][clickedY]);
							killAllJumped(clickedX, clickedY);
							status = BoardStatus.P2Turn;
							checkForWinners();
							Main.log(status.toString());
						}else if(status == BoardStatus.P2Wait && checkers[clickedX][clickedY].function == CheckerFunction.P2Highlight){
							Main.log("Player Two is moving from (" + selectedX + ", " + selectedY + ") to (" + clickedX + ", " + clickedY + ")");
							moveChecker(checkers[selectedX][selectedY], checkers[clickedX][clickedY]);
							killAllJumped(clickedX, clickedY);
							status = BoardStatus.P1Turn;
							checkForWinners();
							Main.log(status.toString());
						}else{
							Main.log("Invalid move attempted at (" + clickedX + ", " + clickedY + ")");
						}
						
						updateGUI();
					}
				});
				if(i < startingRows && (i + j) % 2 == 1){
					checkers[j][i].setChecker(CheckerFunction.P2Checker);
				}else if(i >= length - startingRows && (i + j) % 2 == 1){
					checkers[j][i].setChecker(CheckerFunction.P1Checker);
				}
			}
		}
		this.status = BoardStatus.P1Turn;
		Main.log(this.status.toString());
	}
	
	public CheckerBoard(BoardStatus status){
		this.status = status;
	}
	
	public JPanel toJPanel(){
		JPanel toReturn = new JPanel();
		toReturn.setLayout(new GridLayout(this.length, this.length));
		JPanel tile;
		for(int i = 0; i < this.length; i++){
			for(int j = 0; j < this.length; j++){
				tile = new JPanel(new MigLayout("fill"));
				if((i + j) % 2 == 0){
					tile.setBackground(Main.offTileColor);
				}else{
					tile.setBackground(Main.onTileColor);
				}
				tile.add(checkers[j][i], "grow");
				toReturn.add(tile);
			}
		}
		return toReturn;
	}
	
	public void moveChecker(Checker original, Checker newLocation){
		Main.log("Moving checker");
		newLocation.setChecker(original.function);
		newLocation.isKing = original.isKing;
		newLocation.setText(original.getText());
		newLocation.updateChecker();
		original.kill();
		killAllHighlights();
	}
	
	public void killAllHighlights(){
		Main.log("Killing all highlights");
		for(int i = 0; i < length; i++){
			for(int j = 0; j < length; j++){
				if(checkers[i][j].function == CheckerFunction.P1Highlight || checkers[i][j].function == CheckerFunction.P2Highlight){
					checkers[i][j].kill();
				}
			}
		}
		selectedX = -1;
		selectedY = -1;
	}
	
	public void killAllJumped(int jumpedToX, int jumpedToY){
		Main.log("Killing all jumped checkers");
		for(PossibleMove move : possibleNewDestinations){
			if(move.newLocation[0] == jumpedToX && move.newLocation[1] == jumpedToY){
				for(int i = 0; i < move.jumpedOverCoords.size(); i += 2){
					checkers[move.jumpedOverCoords.get(i)][move.jumpedOverCoords.get(i + 1)].kill();
				}
				break;
			}
		}
		possibleNewDestinations.clear();
	}
	
	public List<PossibleMove> getValidMovesFor(Checker source, PossibleMove jumpFrom){
		List<PossibleMove> toReturn = new ArrayList<PossibleMove>();
		int[] movementDirection = new int[0];
		int[] xMovementDir = new int[2];
		xMovementDir[0] = 1;
		xMovementDir[1] = -1;
		CheckerFunction oppositePiece = (source.function == CheckerFunction.P1Checker)? CheckerFunction.P2Checker : CheckerFunction.P1Checker;
		if(source.isKing){
			movementDirection = new int[2];
			movementDirection[0] = -1;
			movementDirection[1] = 1;
		}else if(source.function == CheckerFunction.P1Checker){
			movementDirection = new int[1];
			movementDirection[0] = -1;
		}else if(source.function == CheckerFunction.P2Checker){
			movementDirection = new int[1];
			movementDirection[0] = 1;
		}
		if(jumpFrom == null){
			Main.log("Finding valid moves for clicked checker");
			for(int i = 0; i < movementDirection.length; i++){
				if(source.y + movementDirection[i] < length && source.y + movementDirection[i] >= 0){
					if(source.x + 1 < length && checkers[source.x + 1][source.y + movementDirection[i]].function == CheckerFunction.Invisible){
						toReturn.add(new PossibleMove(source.x + 1, source.y + movementDirection[i]));
					}
					if(source.x - 1 >= 0 && checkers[source.x - 1][source.y + movementDirection[i]].function == CheckerFunction.Invisible){
						toReturn.add(new PossibleMove(source.x - 1, source.y + movementDirection[i]));
					}
				}
			}
		}
		PossibleMove newMove;
		Main.allowOutput = false;
		for(int i = 0; i < movementDirection.length; i++){
			for(int j = 0; j < xMovementDir.length; j++){
				if(0 <= source.x + 2 * xMovementDir[j] && source.x + 2 * xMovementDir[j] < length && 0 <= source.y + 2 * movementDirection[i] && source.y + 2 * movementDirection[i] < length && checkers[source.x + xMovementDir[j]][source.y + movementDirection[i]].function == oppositePiece && checkers[source.x + 2 * xMovementDir[j]][source.y + 2 * movementDirection[i]].function == CheckerFunction.Invisible){
					newMove = new PossibleMove(source.x + 2 * xMovementDir[j], source.y + 2 * movementDirection[i]);
					newMove.jumpedOverCoords.add(source.x + xMovementDir[j]);
					newMove.jumpedOverCoords.add(source.y + movementDirection[i]);
					if(jumpFrom != null){
						newMove.jumpedOverCoords.addAll(jumpFrom.jumpedOverCoords);
					}
					toReturn.add(newMove);
					CheckerBoard boardClone = this.clone();
					boardClone.moveChecker(boardClone.checkers[source.x][source.y], boardClone.checkers[source.x + 2 * xMovementDir[j]][source.y + 2 * movementDirection[i]]);
					for(int k = 0; k < newMove.jumpedOverCoords.size(); k += 2){
						boardClone.checkers[newMove.jumpedOverCoords.get(k)][newMove.jumpedOverCoords.get(k + 1)].kill();
					}
					toReturn.addAll(boardClone.getValidMovesFor(boardClone.checkers[source.x + 2 * xMovementDir[j]][source.y + 2 * movementDirection[i]], newMove.clone()));
				}
			}
		}
		Main.allowOutput = jumpFrom == null;
		return toReturn;
	}
	
	public void checkForWinners(){
		Main.log("Checking for winners");
		int P1CheckerCount = 0;
		int P2CheckerCount = 0;
		for(int i = 0; i < length; i++){
			for(int j = 0; j < length; j++){
				if(checkers[j][i].function != CheckerFunction.Invisible){
					P1CheckerCount += (checkers[j][i].function == CheckerFunction.P1Checker)? 1 : 0;
					P2CheckerCount += (checkers[j][i].function == CheckerFunction.P2Checker)? 1 : 0;
				}
			}
		}
		Main.log("P1 has " + P1CheckerCount + " checkers; P2 has " + P2CheckerCount + " checkers");
		if(P1CheckerCount == 0){
			status = BoardStatus.P2Won;
		}else if(P2CheckerCount == 0){
			status = BoardStatus.P1Won;
		}
	}
	
	@Override
	public CheckerBoard clone(){
		CheckerBoard toReturn = new CheckerBoard(this.status);
		for(int i = 0; i < length; i++){
			for(int j = 0; j < length; j++){
				toReturn.checkers[j][i] = checkers[j][i].clone();
			}
		}
		return toReturn;
	}

	public void updateGUI(){
		Main.title.setText("Checkers - " + this.status.toString());
		Main.window.getContentPane().repaint();
		Main.window.getContentPane().revalidate();
	}
	

}
