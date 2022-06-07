package jpacman.level;

import jpacman.npc.Ghost;
import jpacman.points.PointCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

/**
 * Class to verify the collide method from the DefaultPlayerInteractionMap class.
 * 默认玩家互动地图测试
 */
public class DefaultPlayerInteractionMapTest extends CollisionMapTest {

    /**
     * Initialize the parameters used for the test cases.
     */
    @BeforeEach
    @Override
    void init() {
        this.setPointCalculator(Mockito.mock(PointCalculator.class));
        this.setPlayer(Mockito.mock(Player.class));
        this.setPellet(Mockito.mock(Pellet.class));
        this.setGhost(Mockito.mock(Ghost.class));
        this.setPlayerCollisions(new DefaultPlayerInteractionMap(this.getPointCalculator()));
    }
}
