public class SubTask extends Task {

    public final int parentId; // id epyc

    public SubTask(Epic epic, String text) {
        super(text);
        parentId = epic.id;
    }
}