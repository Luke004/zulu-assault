package game.logic;

import game.models.items.*;
import org.newdawn.slick.geom.Vector2f;

import java.util.Random;

public class RandomItemDropper {

    private static Random random;

    private int[] ITEM_DROP_CHANCES = new int[]{
            2,  // DROP_CHANCE_EMP  (-> 3 PERCENT !!!)
            2,  // DROP_CHANCE_MEGA_PULSE
            2,  // DROP_CHANCE_INVINCIBILITY
            2,  // DROP_CHANCE_EXPAND
            3,  // DROP_CHANCE_GOLDEN_WRENCH
            5   // DROP_CHANCE_SILVER_WRENCH
    };

    public RandomItemDropper() {
        random = new Random();
    }

    public Item dropItem(Vector2f item_position) {
        int percentage = random.nextInt(100);
        int drop_chance_holder = 0; // this gets counted up and checked if it's higher than the random number 0-100

        for (int idx = 0; idx < ITEM_DROP_CHANCES.length; ++idx) {
            drop_chance_holder += ITEM_DROP_CHANCES[idx];
            if (drop_chance_holder > percentage) {
                switch (idx) {
                    case 0:
                        return new EMPItem(item_position);
                    case 1:
                        return new MegaPulseItem(item_position);
                    case 2:
                        return new InvincibilityItem(item_position);
                    case 3:
                        return new ExpandItem(item_position);
                    case 4:
                        return new GoldenWrenchItem(item_position);
                    case 5:
                        return new SilverWrenchItem(item_position);
                }
            }
        }
        return null;
    }
}
