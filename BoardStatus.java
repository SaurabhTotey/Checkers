
public enum BoardStatus {
	P1Turn("Player One's Turn"), P2Turn("Player Two's Turn"), P1Wait("Player One's Turn"), P2Wait("Player Two's Turn"), P1Won("Player One has Won!"), P2Won("Player Two has Won!"), Stalemate("The Game is a Stalemate");
	
	private String value;

    BoardStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
