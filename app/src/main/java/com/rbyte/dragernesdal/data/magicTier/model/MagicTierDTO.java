package com.rbyte.dragernesdal.data.magicTier.model;


public class MagicTierDTO {
    private int ID;
    private int lvl;
    private int spell1ID;
    private int spell2ID;
    private int spell3ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public int getSpell1ID() {
        return spell1ID;
    }

    public void setSpell1ID(int spell1ID) {
        this.spell1ID = spell1ID;
    }

    public int getSpell2ID() {
        return spell2ID;
    }

    public void setSpell2ID(int spell2ID) {
        this.spell2ID = spell2ID;
    }

    public int getSpell3ID() {
        return spell3ID;
    }

    public void setSpell3ID(int spell3ID) {
        this.spell3ID = spell3ID;
    }
}

