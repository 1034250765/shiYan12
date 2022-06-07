package jpacman.level;

import jpacman.level.CollisionMap;
import jpacman.level.Pellet;
import jpacman.level.Player;
import jpacman.npc.Ghost;
import jpacman.points.PointCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Tests for classes that implement the CollisionMap class.
 */
public abstract class CollisionMapTest {
    private PointCalculator pointCalculator;   //分数计算器
    private Player player;                     //玩家
    private Pellet pellet;                     //豆子
    private Ghost ghost;                       //鬼
    private CollisionMap playerCollisions;     //玩家碰撞

    /**
     * 设置点计算器
     * @param pointCalculator the point calculator to be set
     */
    public void setPointCalculator(PointCalculator pointCalculator) {
        this.pointCalculator = pointCalculator;
    }

    /**
     * 设置玩家
     * @param player the player to be set
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * 设置豆子
     * @param pellet the pellet to be set
     */
    public void setPellet(Pellet pellet) {
        this.pellet = pellet;
    }

    /**
     * 设置鬼
     * @param ghost the ghost to be set
     */
    public void setGhost(Ghost ghost) {
        this.ghost = ghost;
    }

    /**
     * 设置玩家碰撞
     * @param playerCollisions the player collisions to be set
     */
    public void setPlayerCollisions(CollisionMap playerCollisions) {
        this.playerCollisions = playerCollisions;
    }

    /**
     * 得到这个对象的点计算器。
     * @return the point calculator
     */
    public PointCalculator getPointCalculator() {
        return pointCalculator;
    }

    /**
     * 初始化测试用例的参数。
     */
    @BeforeEach
    abstract void init();

    /**
     * 玩家和玩家发生碰撞
     */
    @Test
    void testPlayerToPlayer() {
        Player player1 = Mockito.mock(Player.class);
        playerCollisions.collide(player, player1);
        //确保所有交互都得到验证。如果模拟对象上存在任何未验证的交互，它将使测试失败。
        Mockito.verifyZeroInteractions(player, player1);
    }

    /**
     * 玩家和豆子发生碰撞
     */
    @Test
    void testPlayerToPellet() {
        playerCollisions.collide(player, pellet);
        //consumedAPellet更新玩家分数
        Mockito.verify(pointCalculator, Mockito.times(1)).consumedAPellet(
            Mockito.eq(player),
            Mockito.eq(pellet) //等于给定值的对象参数
        );
        //用于测试方法调用的数量,leaveSquare()方法表示将豆子从棋盘上移除。
        Mockito.verify(pellet, Mockito.times(1)).leaveSquare();
        //检查任何给定的模拟是否有任何未验证的交互。
        Mockito.verifyNoMoreInteractions(player, pellet);
    }

    /**
     * 玩家和鬼发生碰撞
     */
    @Test
    void testPlayerToGhost() {
        playerCollisions.collide(player, ghost);

        Mockito.verify(pointCalculator, Mockito.times(1)).collidedWithAGhost(
            Mockito.eq(player),
            Mockito.eq(ghost)
        );
        //设置玩家死亡
        Mockito.verify(player, Mockito.times(1)).setAlive(false);
        //设置杀死玩家的杀手为鬼
        Mockito.verify(player, Mockito.times(1)).setKiller(Mockito.eq(ghost));
        Mockito.verifyNoMoreInteractions(player, ghost);
    }


    /**
     * 鬼和玩家发生碰撞
     */
    @Test
    void testGhostToPlayer() {
        playerCollisions.collide(ghost, player);

        Mockito.verify(pointCalculator, Mockito.times(1)).collidedWithAGhost(
            Mockito.eq(player),
            Mockito.eq(ghost)
        );

        Mockito.verify(player, Mockito.times(1)).setAlive(false);

        Mockito.verify(player, Mockito.times(1)).setKiller(Mockito.eq(ghost));

        Mockito.verifyNoMoreInteractions(player, ghost);
    }


    /**
     * 鬼和豆子发生碰撞
     */
    @Test
    void testGhostToPellet() {
        playerCollisions.collide(ghost, pellet);

        Mockito.verifyZeroInteractions(ghost, pellet);
    }


    /**
     * 鬼和鬼发生碰撞
     */
    @Test
    void testGhostToGhost() {
        Ghost ghost1 = Mockito.mock(Ghost.class);
        playerCollisions.collide(ghost, ghost1);

        Mockito.verifyZeroInteractions(ghost, ghost1);
    }


    /**
     * 豆子和玩家发生碰撞
     */
    @Test
    void testPelletToPlayer() {
        playerCollisions.collide(pellet, player);

        Mockito.verify(pointCalculator, Mockito.times(1)).consumedAPellet(
            Mockito.eq(player),
            Mockito.eq(pellet)
        );

        Mockito.verify(pellet, Mockito.times(1)).leaveSquare();

        Mockito.verifyNoMoreInteractions(pellet, player);
    }


    /**
     * 豆子和鬼发生碰撞
     */
    @Test
    void testPelletToGhost() {
        playerCollisions.collide(pellet, ghost);

        Mockito.verifyZeroInteractions(pellet, ghost);
    }


    /**
     * 豆子和豆子发生碰撞
     */
    @Test
    void testPelletToPellet() {
        Pellet pellet1 = Mockito.mock(Pellet.class);
        playerCollisions.collide(pellet, pellet1);

        Mockito.verifyZeroInteractions(pellet, pellet1);
    }
}
