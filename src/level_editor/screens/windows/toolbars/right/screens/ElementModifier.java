package level_editor.screens.windows.toolbars.right.screens;

import game.models.Element;
import game.models.entities.Entity;
import game.models.entities.MovableEntity;
import game.models.interaction_circles.InteractionCircle;
import game.models.items.Item;
import level_editor.LevelEditor;
import level_editor.screens.elements.Button;
import level_editor.screens.elements.Checkbox;
import level_editor.screens.windows.Window;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import level_editor.util.MapElements;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class ElementModifier extends ToolbarScreen {

    private static String title_no_selection = "NO SELECTION";

    private Checkbox[] checkboxes;
    private Button[] buttons;

    private boolean display;

    private LevelEditor levelEditor;

    public ElementModifier(RightToolbar rightToolbar, LevelEditor levelEditor) {
        super(rightToolbar, title_no_selection);
        this.levelEditor = levelEditor;

        int checkbox_x = startX + Window.Props.calcMargin(rightToolbar.getWidth(), 0.1f, 1);
        int checkbox_width = Window.Props.calcRectSize(rightToolbar.getWidth(), 0.1f, 1);
        int checkbox_height = Window.Props.calcRectSize(rightToolbar.getHeight() / 14, 0.5f, 1);

        checkboxes = new Checkbox[3];
        checkboxes[0] = new Checkbox("Hostile",
                checkbox_x,
                startY + Window.Props.DEFAULT_MARGIN,
                checkbox_width,
                checkbox_height
        );
        checkboxes[1] = new Checkbox("Drivable",
                checkbox_x,
                startY + checkbox_height + Window.Props.DEFAULT_MARGIN,
                checkbox_width,
                checkbox_height
        );
        checkboxes[2] = new Checkbox("Mandatory",
                checkbox_x,
                startY + checkbox_height * 2 + Window.Props.DEFAULT_MARGIN,
                checkbox_width,
                checkbox_height
        );

        buttons = new Button[2];
        buttons[0] = new Button("MOVE",
                checkbox_x,
                startY + checkbox_height * 3 + Window.Props.DEFAULT_MARGIN * 4,
                checkbox_width,
                checkbox_height
        );
        buttons[1] = new Button("DELETE",
                checkbox_x,
                startY + checkbox_height * 4 + Window.Props.DEFAULT_MARGIN * 6,
                checkbox_width,
                checkbox_height
        );

    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        super.render(gc, graphics);
        if (!display) return;

        for (Checkbox checkbox : checkboxes) {
            checkbox.draw(graphics);
        }

        for (Button button : buttons) {
            button.draw(graphics);
        }
    }

    @Override
    public void update(GameContainer gc) {
        for (Button button : buttons) {
            button.update(gc);
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY) {
        Element elementToModify = levelEditor.getElementToModify();
        for (Checkbox checkbox : checkboxes) {
            if (checkbox.isMouseOver(mouseX, mouseY)) {
                if (checkbox.isDisabled()) return;
                checkbox.toggle();
                Element copy = null;
                switch (checkbox.getName()) {
                    case "Hostile":
                        checkboxes[2].setDisabled(!checkbox.isChecked());
                        copy = MapElements.getCopyByName(
                                elementToModify.getPosition(),
                                elementToModify.getClass().getSimpleName(),
                                checkbox.isChecked(),
                                checkboxes[1].isChecked()
                        );
                        if (copy != null) ((Entity) copy).setRotation(((Entity) elementToModify).getRotation());
                        break;
                    case "Drivable":
                        ((MovableEntity) elementToModify).isHostile = checkboxes[0].isChecked();
                        ((MovableEntity) elementToModify).isDrivable = checkbox.isChecked();
                        copy = MapElements.getDeepCopy(elementToModify);

                        /*
                        copy = MapElements.getCopyByName(
                                elementToModify.getPosition(),
                                elementToModify.getClass().getSimpleName(),
                                checkboxes[0].isChecked(),
                                checkbox.isChecked()
                        );
                        if (copy != null) ((Entity) copy).setRotation(((Entity) elementToModify).getRotation());
                         */
                        break;
                    case "Mandatory":
                        if (elementToModify instanceof MovableEntity) {
                            ((MovableEntity) elementToModify).isHostile = checkboxes[0].isChecked();
                            ((MovableEntity) elementToModify).isDrivable = checkboxes[1].isChecked();
                            ((MovableEntity) elementToModify).isMandatory = checkbox.isChecked();
                        } else if (elementToModify instanceof Item) {
                            ((Item) elementToModify).isMandatory = checkbox.isChecked();
                        }
                        copy = MapElements.getDeepCopy(elementToModify);

                        /*
                        copy = MapElements.getCopyByName(
                                elementToModify.getPosition(),
                                elementToModify.getClass().getSimpleName(),
                                checkboxes[0].isChecked(),
                                checkboxes[1].isChecked()
                        );
                         */
                        break;
                }
                if (copy != null) {
                    //copy.isMandatory = checkbox.isChecked();
                    levelEditor.replaceModifiedElement(copy);
                }
                return;
            }
        }
        for (Button button : buttons) {
            if (button.isMouseOver(mouseX, mouseY)) {
                switch (button.getName()) {
                    case "MOVE":
                        levelEditor.moveElement(elementToModify);
                        return;
                    case "DELETE":
                        levelEditor.removeElement(elementToModify);
                        this.setupElement(null);
                        return;
                }

            }
        }
    }

    public void setupElement(Element elementToModify) {
        if (elementToModify == null) {
            display = false;
            title_string = title_no_selection;
        } else {
            display = true;
            title_string = elementToModify.getClass().getSimpleName();
        }
        // center the title string
        titleStringX = startX + rightToolbar.getWidth() / 2 - title_string_drawer.getWidth(title_string) / 2;

        if (elementToModify == null) return;
        if (elementToModify instanceof Entity) {
            checkboxes[0].setDisabled(false);
            checkboxes[0].setChecked((((Entity) elementToModify).isHostile));
            checkboxes[2].setDisabled((!((Entity) elementToModify).isHostile));
            if (elementToModify instanceof MovableEntity) {
                checkboxes[1].setDisabled(false);
                checkboxes[1].setChecked((((MovableEntity) elementToModify).isDrivable));
            } else {
                checkboxes[1].setDisabled(true);
            }
            checkboxes[2].setChecked(((Entity) elementToModify).isMandatory);
        } else {
            if (elementToModify instanceof InteractionCircle) {
                checkboxes[2].setDisabled(true);
            } else if (elementToModify instanceof Item) {
                checkboxes[2].setDisabled(false);
                checkboxes[2].setChecked(((Item) elementToModify).isMandatory);
            }
            checkboxes[0].setDisabled(true);
            checkboxes[1].setDisabled(true);
        }
    }

}
