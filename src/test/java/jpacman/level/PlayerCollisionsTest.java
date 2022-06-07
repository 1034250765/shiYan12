package jpacman.level;

import jpacman.npc.Ghost;
import jpacman.points.PointCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

/**
 * 用该类来验证是否正确处理了不同单元之间的冲突。
 */
public class PlayerCollisionsTest extends CollisionMapTest {

    /**
     * 初始化一个玩家，一个小球和一个幽灵用于测试。
     * 另外，初始化一个PlayerCollisions对象。
     */
    @BeforeEach
    @Override
    void init() {
        //PointCalculator.class)返回了一个属于PointCalculator这个类的一个mock对象
        this.setPointCalculator(Mockito.mock(PointCalculator.class));
        this.setPlayer(Mockito.mock(Player.class));
        this.setPellet(Mockito.mock(Pellet.class));
        this.setGhost(Mockito.mock(Ghost.class));
        this.setPlayerCollisions(new PlayerCollisions(this.getPointCalculator()));
    }
}
