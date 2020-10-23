package level_editor.toolbar.screens;

import level_editor.LevelEditor;
import level_editor.toolbar.Toolbar;
import level_editor.util.Elements;
import models.Element;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ElementSelector extends ElementScreen implements iToolbarState {

    private LevelEditor levelEditor;

    private int selector_squares_row_count;
    private List<SelectorSquare> selectorSquareList;

    public ElementSelector(Toolbar toolbar, LevelEditor levelEditor) {
        super(toolbar);
        this.levelEditor = levelEditor;
        selectorSquareList = new ArrayList<>();

        selector_squares_row_count = (toolbar.getWidth() - SelectorSquare.SIZE) / (SelectorSquare.SIZE_AND_MARGIN);
        System.out.println("row count:" + selector_squares_row_count);

        // init the selector square list with all elements
        int rowIdx = 0;
        int colIdx = 0;
        String[] allElements = Elements.getAllElements();

        for (int i = 0; i < allElements.length; ++i) {
            selectorSquareList.add(new SelectorSquare(Elements.getCopyByName(allElements[i]),
                    (int) toolbar.getX() + SelectorSquare.MARGIN + rowIdx * SelectorSquare.SIZE_AND_MARGIN,
                    (int) toolbar.getY() + SelectorSquare.MARGIN + colIdx * SelectorSquare.SIZE_AND_MARGIN));

            if (rowIdx < selector_squares_row_count) {
                rowIdx++;
            } else {
                rowIdx = 0;
                colIdx++;
            }
        }

    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame) {

    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) {
        for (SelectorSquare selectorSquare : selectorSquareList) {
            selectorSquare.draw(graphics);
        }
    }

    @Override
    public void onMouseClick(GameContainer gameContainer, StateBasedGame stateBasedGame, int mouseX, int mouseY) {
        for (SelectorSquare selectorSquare : selectorSquareList) {
            if (selectorSquare.clicked(mouseX, mouseY)) {
                levelEditor.setSelectedElement(selectorSquare.getElement());
                break;
            }
        }
    }


    private static class SelectorSquare {

        private static final int SIZE = 35;
        private static final int MARGIN = 5;
        private static final int SIZE_AND_MARGIN = SIZE + MARGIN;

        private Element element;
        private int xPos, yPos;

        SelectorSquare(Element element, int xPos, int yPos) {
            this.element = element;
            this.xPos = xPos;
            this.yPos = yPos;
        }

        void draw(Graphics graphics) {
            graphics.drawRect(xPos, yPos, SIZE, SIZE);
        }

        boolean clicked(int mouseX, int mouseY) {
            return (mouseX > xPos && mouseX < xPos + SIZE && mouseY > yPos && mouseY < yPos + SIZE);
        }

        Element getElement() {
            return element;
        }

    }


}
