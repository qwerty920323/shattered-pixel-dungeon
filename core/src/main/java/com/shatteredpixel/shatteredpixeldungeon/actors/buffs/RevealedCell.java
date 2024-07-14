package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
// RevealedArea 와 같은 기능 but cell에서만 시야 제공
public class RevealedCell extends FlavourBuff{

    {
        type = Buff.buffType.POSITIVE;
        actPriority = BLOB_PRIO + 1;
    }

    public int depth, branch;
    public ArrayList <Integer> cells = new ArrayList<>();

    @Override
    public void detach() {
        for (int i : cells) {
            GameScene.updateFog(i, 2);
        }

        super.detach();
    }

    public void set (int pos, int depth, int branch){
        cells.add(pos);
        this.depth = depth;
        this.branch = branch;
    }

    public void blobsCheck (){
        WandOfPrismaticLight.FireFlyBlobs light
                = (WandOfPrismaticLight.FireFlyBlobs) Dungeon.level.blobs.get(WandOfPrismaticLight.FireFlyBlobs.class);

        for (int i = 0; i < cells.size(); i++) {
            int pos = cells.get(i);

            if (light != null) {
                if (light.volume > 0 && light.cur[pos] > 0) {

                } else {
                    cells.remove(i);
                }
            }

            GameScene.updateFog(pos, 2);
        }
    }

    private static final String BRANCH = "branch";
    private static final String DEPTH  = "depth";
    private static final String CELLS  = "cells";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DEPTH, depth);
        bundle.put(BRANCH, branch);

        int[] values = new int[cells.size()];
        for (int i = 0; i < values.length; i++)
            values[i] = cells.get(i);
        bundle.put(CELLS, values);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        depth = bundle.getInt(DEPTH);
        branch = bundle.getInt(BRANCH);

        int[] values = bundle.getIntArray( CELLS );
        for (int value : values)
            cells.add(value);
    }

}
