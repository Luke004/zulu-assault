package level_editor.toolbars.right.screens;

import level_editor.LevelEditor;
import level_editor.toolbars.right.RightToolbar;
import level_editor.util.Elements;

public class EntitySelector extends ElementSelector {

    private static final String title = "ADD ENTITIES";

    public EntitySelector(RightToolbar rightToolbar, LevelEditor levelEditor) {
        super(rightToolbar, levelEditor, title);
    }

    @Override
    protected String[] getElementNames() {
        return Elements.getAllEntities();
    }
}
