package level_editor.toolbar.screens;

import audio.MenuSounds;
import level_editor.LevelEditor;
import level_editor.toolbar.Toolbar;
import level_editor.util.Elements;
import models.Element;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import settings.UserSettings;

import javax.tools.Tool;
import java.util.ArrayList;
import java.util.List;

public class ElementSelector extends ToolbarScreen {

    private static final String title = "SELECT ELEMENT";

    private LevelEditor levelEditor;

    private static final int SELECTOR_SQUARES_PER_ROW = 4;

    private List<SelectorSquare> selectorSquareList;

    public ElementSelector(Toolbar toolbar, LevelEditor levelEditor) {
        super(toolbar, title);
        this.levelEditor = levelEditor;
        selectorSquareList = new ArrayList<>();

        SelectorSquare.calcSize(toolbar.getWidth());

        System.out.println("square size:" + SelectorSquare.size);

        startX += SelectorSquare.margin;
        startY += SelectorSquare.margin;

        // init the selector square list with all elements
        String[] allElements = Elements.getAllElements();
        for (String allElement : allElements) {
            selectorSquareList.add(new SelectorSquare(Elements.getCopyByName(allElement), startX, startY));
        }

    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void render(GameContainer gc, Graphics graphics) {
        super.render(gc, graphics);
        for (SelectorSquare selectorSquare : selectorSquareList) {
            selectorSquare.draw(graphics);
        }
    }

    @Override
    public void onMouseClick(GameContainer gc, StateBasedGame sbg, int mouseX, int mouseY) {
        for (SelectorSquare selectorSquare : selectorSquareList) {
            if (selectorSquare.clicked(mouseX, mouseY)) {
                MenuSounds.CLICK_SOUND.play(1.f, UserSettings.soundVolume);
                levelEditor.setSelectedElement(selectorSquare.getElement());
                break;
            }
        }
    }


    private static class SelectorSquare {

        private static int size;
        private static int margin;
        private static final float RELATIVE_MARGIN = 0.2f;
        private static int sizeAndMargin;

        private Element element;
        private int xPos, yPos;

        private static int rowIdx = 0;
        private static int colIdx = 0;

        SelectorSquare(Element element, int startX, int startY) {
            this.element = element;
            this.xPos = startX + rowIdx * sizeAndMargin;
            this.yPos = startY + margin + colIdx * sizeAndMargin;

            this.element.setPosition(new Vector2f(xPos, yPos));

            if (rowIdx < SELECTOR_SQUARES_PER_ROW - 1) {
                rowIdx++;
            } else {
                rowIdx = 0;
                colIdx++;
            }
        }

        public static void calcSize(int toolbarWidth) {
            margin = (int) (toolbarWidth / (SELECTOR_SQUARES_PER_ROW + 1) * RELATIVE_MARGIN);
            size = (toolbarWidth - (SELECTOR_SQUARES_PER_ROW + 1) * margin) / 4;
            sizeAndMargin = margin + size;
        }

        void draw(Graphics graphics) {
            graphics.drawRect(xPos, yPos, size, size);
            element.drawPreview(graphics);
        }

        boolean clicked(int mouseX, int mouseY) {
            return (mouseX > xPos && mouseX < xPos + size && mouseY > yPos && mouseY < yPos + size);
        }

        Element getElement() {
            return Elements.getCopyByName(element.getClass().getSimpleName());
        }

    }


}
