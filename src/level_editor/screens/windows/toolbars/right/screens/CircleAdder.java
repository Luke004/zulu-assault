package level_editor.screens.windows.toolbars.right.screens;

import level_editor.LevelEditor;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import level_editor.util.MapElements;

public class CircleAdder extends ElementSelector {

    private static final String title = "ADD CIRCLES";

    public CircleAdder(RightToolbar rightToolbar, LevelEditor levelEditor) {
        super(rightToolbar, levelEditor, title);
    }

    @Override
    protected String[] getElementNames() {
        return MapElements.getAllCircles();
    }
}
