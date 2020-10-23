package level_editor.toolbar.screens;

import level_editor.toolbar.Toolbar;
import models.Element;

import java.awt.*;

public class ElementSelector {

    private Toolbar toolbar;

    private static final int SELECTOR_SQUARE_SIZE = 35;
    private static final int SELECTOR_SQUARE_MARGIN = 5;

    private int selector_squares_row_count;

    public ElementSelector(Toolbar toolbar) {
        this.toolbar = toolbar;
        selector_squares_row_count = (toolbar.getWidth() - SELECTOR_SQUARE_MARGIN) / (SELECTOR_SQUARE_SIZE + SELECTOR_SQUARE_MARGIN);
        System.out.println("row count:" + selector_squares_row_count);
    }

    public void draw(Graphics graphics) {
        //graphics.drawRect(this.toolbar.getX());
    }

    private void drawGrid(Graphics graphics) {
        for (int i = 0; i < selector_squares_row_count; ++i) {
            //graphics.drawRect(toolbar.getX() + SELECTOR_SQUARE_MARGIN + i * (SELECTOR_SQUARE_MARGIN + SELECTOR_SQUARE_SIZE));
        }
    }


    private static class SelectorSquare {

        private Element element;

    }


}
