package level_editor.screens.windows.toolbars.right.screens;

import game.audio.MenuSounds;
import game.models.entities.MovableEntity;
import game.models.entities.soldiers.Soldier;
import level_editor.LevelEditor;
import level_editor.screens.windows.Window;
import level_editor.screens.elements.Checkbox;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import level_editor.util.MapElements;
import game.models.Element;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import settings.UserSettings;

public class EntityAdder extends ElementSelector {

    private static final String title = "ADD ENTITIES";

    private Checkbox[] checkboxes;

    public static boolean isHostile, isDrivable, isMandatory;

    public EntityAdder(RightToolbar rightToolbar, LevelEditor levelEditor) {
        super(rightToolbar, levelEditor, title);
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        super.render(gc, graphics);

        for (Checkbox checkbox : checkboxes) {
            checkbox.draw(graphics);
        }

    }

    @Override
    public void onMouseClick(int mouseX, int mouseY) {
        for (SelectorSquare selectorSquare : selectorSquareList) {
            if (selectorSquare.mouseOver(mouseX, mouseY)) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                levelEditor.setElementToPlace(selectorSquare.getElement());
                if (selectorSquare.getElement() instanceof MovableEntity && !(selectorSquare.getElement() instanceof Soldier)) {
                    checkboxes[1].setDisabled(false);
                } else {
                    checkboxes[1].setDisabled(true);
                    isDrivable = false;
                }
                return;
            }
        }

        if (backButton.isMouseOver(mouseX, mouseY)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            rightToolbar.goToLastScreen();
            levelEditor.setElementToPlace(null);
            return;
        }

        for (int i = 0; i < checkboxes.length; ++i) {
            if (checkboxes[i].isMouseOver(mouseX, mouseY)) {
                checkboxes[i].toggle();
                Element selectedElement = levelEditor.getElementToPlace();
                switch (i) {
                    case 0: // "isHostile" checkbox
                        isHostile = checkboxes[i].isChecked();
                        checkboxes[2].setDisabled(!isHostile);
                        isMandatory = false;
                        break;
                    case 1: // "isDrivable" checkbox
                        isDrivable = checkboxes[i].isChecked();
                        break;
                    case 2: // "isMandatory" checkbox
                        isMandatory = checkboxes[i].isChecked();
                        break;
                }
                if (selectedElement == null) return;
                Element replacement = MapElements.getCopyByName(selectedElement.getClass().getSimpleName(),
                        isHostile, isDrivable);
                if (replacement != null) {
                    levelEditor.setElementToPlace(replacement);
                }
                break;
            }
        }

    }

    @Override
    protected int initSuperElements() {
        int checkboxStartY = super.initSuperElements();

        int checkbox_x = startX + Window.Props.calcMargin(rightToolbar.getWidth(), 0.1f, 1);
        int checkbox_width = Window.Props.calcRectSize(rightToolbar.getWidth(), 0.1f, 1);
        int checkbox_height = Window.Props.calcRectSize(rightToolbar.getHeight() / 14, 0.5f, 1);

        checkboxes = new Checkbox[3];
        checkboxes[0] = new Checkbox("Hostile",
                checkbox_x,
                checkboxStartY + Window.Props.DEFAULT_MARGIN,
                checkbox_width,
                checkbox_height
        );
        checkboxes[1] = new Checkbox("Drivable",
                checkbox_x,
                checkboxStartY + checkbox_height + Window.Props.DEFAULT_MARGIN,
                checkbox_width,
                checkbox_height
        );
        checkboxes[2] = new Checkbox("Mandatory",
                checkbox_x,
                checkboxStartY + checkbox_height * 2 + Window.Props.DEFAULT_MARGIN,
                checkbox_width,
                checkbox_height
        );
        checkboxes[2].setDisabled(true);

        // tell the back button on which y coordinate the last selector square was drawn, so it can go below it
        return checkboxStartY + checkbox_height * 3 + Window.Props.DEFAULT_MARGIN;
    }

    @Override
    protected String[] getElementNames() {
        return MapElements.getAllEntities();
    }
}
