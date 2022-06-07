package jpacman.npc.ghost;

import jpacman.board.BoardFactory;
import jpacman.board.Direction;
import jpacman.level.Level;
import jpacman.level.LevelFactory;
import jpacman.level.Player;
import jpacman.level.PlayerFactory;
import jpacman.points.DefaultPointCalculator;
import jpacman.points.PointCalculator;
import jpacman.sprite.PacManSprites;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;


/**
 * 对Clyde类中的nextAiMove()方法进行测试。
 */
public class ClydeTest {

    private PacManSprites pacManSprites;  //用于角色显示
    private BoardFactory boardFactory;    //游戏场景
    private GhostFactory ghostFactory;    //提供给LevelFactory
    private PointCalculator pointCalculator;
    private LevelFactory levelFactory;
    private GhostMapParser ghostMapParser; //地图解析
    private PlayerFactory playerFactory;  //用于构造Player

    /**
     * 实例化GhostMapParser
     */
    @BeforeEach
    void init() {
        pacManSprites = new PacManSprites();
        boardFactory = new BoardFactory(pacManSprites);
        ghostFactory = new GhostFactory(pacManSprites);
        pointCalculator = new DefaultPointCalculator();
        levelFactory = new LevelFactory(pacManSprites, ghostFactory, pointCalculator);
        playerFactory = new PlayerFactory(pacManSprites);

        ghostMapParser = new GhostMapParser(levelFactory, boardFactory, ghostFactory);
    }


    /**
     * 当Clyde的位置与吃豆人的距离小于或等于8个方格时，它会开始逃跑远离吃豆人；
     * 此时吃豆人在Clyde的西侧，所以Clyde应该向东方逃跑。
     */
    @Test
    void testTooClose() {
        Level level = ghostMapParser.parseMap(
            Lists.newArrayList("############", "P       C###", "############")
        );

        //创建Player
        Player player = playerFactory.createPacMan();

        //定义Player即将运动的新方向
        player.setDirection(Direction.WEST);
        //注册player到地图中去
        level.registerPlayer(player);

        //在棋盘中找到魔鬼对象
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());

        //判断clyde逃跑的方向是否与预测方向相同
        assertThat(clyde.nextAiMove()).isEqualTo(Optional.of(Direction.EAST));
    }


    /**
     * 当Clyde的位置与吃豆人的距离大于8个方格时(比如在9个方格的距离上)，它会向吃豆人靠近。
     * 此时吃豆人在Clyde的西侧，所以Clyde应该向西方运动靠近吃豆人。
     */
    @Test
    void testTooFar() {
        Level level = ghostMapParser.parseMap(
            Lists.newArrayList("############", "P        C##", "############")
        );
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());

        assertThat(clyde.nextAiMove()).isEqualTo(Optional.of(Direction.WEST));
    }

    /**
     * 测试是否有玩家在玩当前关卡，没有则返回一个空值。
     */
    @Test
    void testNoPlayer() {
        Level level = ghostMapParser.parseMap(
            Lists.newArrayList("#####", "##C  ", "     ")
        );
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());

        assertThat(clyde.nextAiMove()).isEqualTo(Optional.empty());
    }


    /**
     * 测试吃豆人和Clyde之间是否存在有效的路径，如果没有则返回一个空值。
     */
    @Test
    void testNoPathBetweenPlayerAndClyde() {
        Level level = ghostMapParser.parseMap(
            Lists.newArrayList("######", "#P##C ", " ###  ")
        );
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);
        Clyde clyde = Navigation.findUnitInBoard(Clyde.class, level.getBoard());

        assertThat(clyde.nextAiMove()).isEqualTo(Optional.empty());
    }
}
