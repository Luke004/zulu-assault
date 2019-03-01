import levels.Level_1;
import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        String gameName = "Zulu Assault";
        boolean fullscreen = true;

        System.setProperty("org.lwjgl.librarypath", new File(
                new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
        try {
            AppGameContainer app = new AppGameContainer(new Level_1(gameName));
            app.setDisplayMode(800, 600, fullscreen);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }
}
