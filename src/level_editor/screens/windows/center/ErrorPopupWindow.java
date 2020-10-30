package level_editor.screens.windows.center;

import level_editor.LevelEditor;
import level_editor.screens.elements.Button;
import level_editor.screens.windows.CenterPopupWindow;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import java.util.LinkedList;
import java.util.List;

import static game.util.StringUtil.getLineSplitMessageList;


public class ErrorPopupWindow extends CenterPopupWindow {

    private Button okBtn;
    private int btnHeight;
    private List<String> message;
    private int messageX, buttonStartY;

    public ErrorPopupWindow(GameContainer gc, LevelEditor levelEditor) {
        super(gc, levelEditor);
        message = new LinkedList<>();

        int btnWidth = 100;     // TODO: 30.10.2020 add relative size
        btnHeight = 20;

        messageX = windowX + Props.DEFAULT_MARGIN;
        int buttonX = windowX + windowWidth / 2 - btnWidth / 2;

        okBtn = new Button("OK",
                buttonX,
                buttonStartY + Props.DEFAULT_MARGIN * 2,
                btnWidth,
                btnHeight
        );
    }

    @Override
    public void draw(GameContainer gc, Graphics graphics) {
        super.draw(gc, graphics);
        if (!isActive) return;

        for (int idx = 0; idx < message.size(); ++idx) {
            text_drawer.drawString(
                    messageX,
                    startYSuper + idx * text_drawer.getLineHeight(),
                    message.get(idx),
                    Color.lightGray
            );
        }
        okBtn.draw(graphics);

    }

    @Override
    public void update(GameContainer gc) {
        if (!this.isActive) return;
        okBtn.update(gc);
    }

    @Override
    public void onMouseClick(int button, int mouseX, int mouseY) {
        if (okBtn.isMouseOver(mouseX, mouseY)) {
            this.isActive = false;
            levelEditor.setPopupWindow(null);
        }
    }

    public void setMessage(String msg) {
        this.message.clear();
        this.message = getLineSplitMessageList(msg, windowWidth - Props.DEFAULT_MARGIN * 2, text_drawer);
        // calculate height of window
        int messageHeight = message.size() * text_drawer.getLineHeight() + Props.DEFAULT_MARGIN * 2;
        super.initHeight(messageHeight + (btnHeight + Props.DEFAULT_MARGIN * 2));
        // set button y-pos
        this.buttonStartY = startYSuper + message.size() * text_drawer.getLineHeight() + Props.DEFAULT_MARGIN * 2;
        this.okBtn.setYPos(buttonStartY);
    }

}
