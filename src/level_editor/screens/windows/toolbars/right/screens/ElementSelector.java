package level_editor.screens.windows.toolbars.right.screens;

import game.audio.MenuSounds;
import level_editor.LevelEditor;
import level_editor.screens.windows.Window;
import level_editor.screens.elements.Button;
import level_editor.screens.windows.toolbars.right.RightToolbar;
import level_editor.util.MapElements;
import game.models.Element;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import settings.UserSettings;

import java.util.ArrayList;
import java.util.List;

public abstract class ElementSelector extends ToolbarScreen {

    protected List<SelectorSquare> selectorSquareList;

    protected Button backButton;

    protected LevelEditor levelEditor;

    public ElementSelector(RightToolbar rightToolbar, LevelEditor levelEditor, String title) {
        super(rightToolbar, title);
        selectorSquareList = new ArrayList<>();
        this.levelEditor = levelEditor;

        int backButtonStartY = initSuperElements();

        backButton = new Button("BACK",
                startX + Window.Props.calcMargin(rightToolbar.getWidth(), 0.5f, 1),
                backButtonStartY + Window.Props.DEFAULT_MARGIN,
                Window.Props.calcRectSize(rightToolbar.getWidth(), 0.5f, 1),
                Window.Props.calcRectSize(rightToolbar.getHeight() / 14, 0.5f, 1)
        );
    }

    protected abstract String[] getElementNames();

    protected int initSuperElements() {
        return initSelectorSquareList(getElementNames());
    }

    protected int initSelectorSquareList(String[] elementNames) {
        final int SELECTOR_SQUARES_PER_ROW = 4;
        int rowIdx = 0;

        int marginX = Window.Props.calcMargin(rightToolbar.getWidth(), 0.3f, SELECTOR_SQUARES_PER_ROW);
        int size = Window.Props.calcRectSize(rightToolbar.getWidth(), 0.3f, SELECTOR_SQUARES_PER_ROW);

        int currentX = rightToolbar.getX() + marginX;
        int currentY = startY;

        for (String s_element : elementNames) {
            selectorSquareList.add(new SelectorSquare(MapElements.getCopyByName(s_element),
                    currentX,
                    currentY,
                    size
            ));
            if (rowIdx < SELECTOR_SQUARES_PER_ROW - 1) {
                currentX += size + marginX;
                rowIdx++;
            } else {
                currentX = rightToolbar.getX() + marginX;
                currentY += size + marginX;
                rowIdx = 0;
            }
        }
        // tell the back button on which y coordinate the last selector square was drawn, so it can go below it
        SelectorSquare selectorSquare = selectorSquareList.get(selectorSquareList.size() - 1);
        return selectorSquare.yPos + selectorSquare.size + Window.Props.DEFAULT_MARGIN;
    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        super.render(gc, graphics);
        for (SelectorSquare selectorSquare : selectorSquareList) {
            selectorSquare.draw(graphics);
        }
        backButton.draw(graphics);
    }

    @Override
    public void update(GameContainer gc) {
        for (SelectorSquare selectorSquare : selectorSquareList) {
            selectorSquare.update(gc);
        }
        backButton.update(gc);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY) {
        for (SelectorSquare selectorSquare : selectorSquareList) {
            if (selectorSquare.mouseOver(mouseX, mouseY)) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                levelEditor.setElementToPlace(selectorSquare.getElement());
                return;
            }
        }

        if (backButton.isMouseOver(mouseX, mouseY)) {
            MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
            rightToolbar.goToLastScreen();
            levelEditor.setElementToPlace(null);
            // return;  // activate, if new code is added below
        }
    }

    protected static class SelectorSquare {

        private int size;
        private Element element;
        private int xPos, yPos;
        private boolean hover;

        SelectorSquare(Element element, int startX, int startY, int size) {
            this.element = element;
            this.xPos = startX;
            this.yPos = startY;
            this.size = size;

            this.element.setPosition(new Vector2f(xPos, yPos));
        }

        void draw(Graphics graphics) {
            graphics.setLineWidth(hover ? 2.f : 1.f);
            graphics.drawRect(xPos, yPos, size, size);
            graphics.setLineWidth(1.f);
            element.drawPreview(graphics);
        }

        void update(GameContainer gc) {
            hover = mouseOver(gc.getInput().getMouseX(), gc.getInput().getMouseY());
        }

        boolean mouseOver(int mouseX, int mouseY) {
            return (mouseX > xPos && mouseX < xPos + size && mouseY > yPos && mouseY < yPos + size);
        }

        Element getElement() {
            return MapElements.getCopyByName(element.getClass().getSimpleName(), EntityAdder.isHostile, EntityAdder.isDrivable);
        }

    }

}
