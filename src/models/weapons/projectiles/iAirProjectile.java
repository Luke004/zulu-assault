package models.weapons.projectiles;

public interface iAirProjectile {
    public enum Target {
        WarAttenders, Tiles, Windmills
    }

    boolean hasHitGround();

    void setHitGround();

    boolean hasChecked(Target target);

    void setChecked(Target target);
}
