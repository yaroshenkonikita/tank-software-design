package ru.mipt.bit.platformer.model;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeModelTest {
    @Test
    void testBlocksItsOwnTile() {
        TreeModel tree = new TreeModel(new GridPoint2(2, 3));
        assertTrue(tree.blocks(new GridPoint2(2, 3)));
        assertFalse(tree.blocks(new GridPoint2(1, 3)));
    }
}

