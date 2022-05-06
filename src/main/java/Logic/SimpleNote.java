package Logic;

public class SimpleNote {
    private String name;
    private String text;

    public SimpleNote(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
