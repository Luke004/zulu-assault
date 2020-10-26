package level_editor.screens.toolbars.right.screens;

import level_editor.LevelEditor;
import level_editor.screens.toolbars.right.RightToolbar;
import level_editor.util.Elements;

public class ItemSelector extends ElementSelector {

    private static final String title = "ADD ITEMS";

    public ItemSelector(RightToolbar rightToolbar, LevelEditor levelEditor) {
        super(rightToolbar, levelEditor, title);
    }

    @Override
    protected String[] getElementNames() {
        return Elements.getAllItems();
    }
}
