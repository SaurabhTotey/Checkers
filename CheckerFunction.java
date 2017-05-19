
public enum CheckerFunction {
	Invisible("invisible"), P1Checker("player one checker"), P2Checker("player two checker"), P1Highlight("player one highlight"), P2Highlight("player two highlight");
	
	private String value;

    CheckerFunction(final String value) {
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
