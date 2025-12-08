// Compatibility wrapper: keep a `TutorialPanel` type that delegates to `StoryPanel`.
public class TutorialPanel extends StoryPanel {
    public TutorialPanel(javax.swing.JPanel screenManager, PlayPanel playPanel) {
        super(screenManager, playPanel);
    }
}
