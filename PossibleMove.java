import java.util.ArrayList;
import java.util.List;

public class PossibleMove {
	
	public int[] newLocation;
	public List<Integer> jumpedOverCoords = new ArrayList<Integer>();

	public PossibleMove(int newX, int newY) {
		newLocation = new int[2];
		newLocation[0] = newX;
		newLocation[1] = newY;
	}
	
	@Override
	public PossibleMove clone(){
		PossibleMove toReturn = new PossibleMove(newLocation[0], newLocation[1]);
		toReturn.jumpedOverCoords.addAll(jumpedOverCoords);
		return toReturn;
	}

}
