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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 对Inky类的nextAiMove()方法进行测试
 */
public class InkyTest {

    private static final PacManSprites SPRITES = new PacManSprites();
    private LevelFactory levelFactory;
    private GhostFactory ghostFactory;  //提供给LevelFactory
    private PlayerFactory playerFactory; //用于构造Player
    private BoardFactory boardFactory;   //游戏场景
    private GhostMapParser ghostMapParser; //地图解析
    private PointCalculator calculator;    //计算分数

    /**
     *初始化游戏。
     */
    @BeforeEach
    void setup() {
        calculator = new DefaultPointCalculator();
        playerFactory = new PlayerFactory(SPRITES);
        boardFactory = new BoardFactory(SPRITES);
        ghostFactory = new GhostFactory(SPRITES);
        levelFactory = new LevelFactory(SPRITES, ghostFactory, calculator);
        ghostMapParser = new GhostMapParser(levelFactory, boardFactory, ghostFactory);
    }


    /**
     * 如果测试用例中没有Blinky的洞察力，将返回一个空值。
     * Blinky直接以吃豆人为目标，不断追击吃豆人
     * Inky在追击过程中，需要以吃豆人和Blinky的位置来共同计算出它的追击目标
     */
    @Test
    void testNoBlinky() {

        Level level = ghostMapParser.parseMap(
            Lists.newArrayList("PI ", "   ", "   ")
        );

        //创建Player
        Player player = playerFactory.createPacMan();
        //定义Player即将运动的新方向
        player.setDirection(Direction.WEST);

        //注册player到地图中去
        level.registerPlayer(player);
        //在棋盘中找到魔鬼对象
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        //不存在blinky，返回空值
        assertThat(inky.nextAiMove()).isEqualTo(Optional.empty());
    }

    /**
     * 当Inky和Player之间没有路径时，将返回一个空值。
     */
    @Test
    void testNoPath() {

        List<String> grid = new ArrayList<>();
        grid.add("#######################");
        grid.add("#.........P#......I..B#");
        grid.add("#######################");

        Level level = ghostMapParser.parseMap(grid);
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.WEST);
        level.registerPlayer(player);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        assertThat(inky.nextAiMove()).isEqualTo(Optional.empty());

    }


    /**
     * 当棋盘上没有玩家时，返回一个空值。
     */
    @Test
    void testNoPlayer() {
        Level level = ghostMapParser.parseMap(
            Lists.newArrayList("####", "B  I", "####")
        );
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        assertThat(inky.nextAiMove()).isEqualTo(Optional.empty());
    }


    /**
     * 正常情况测试下，Inky跟着吃豆人，Blinky在吃豆人后面。根据Inky的运动规则，接下来应该向西移动
     */
    @Test
    void testGoTowardsPlayer() {

        List<String> grid = new ArrayList<>();
        grid.add("#######################");
        grid.add("#          P    B   I #");
        grid.add("#######################");

        Level level = ghostMapParser.parseMap(grid);
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());
        assertThat(inky.nextAiMove()).isEqualTo(Optional.of(Direction.WEST));
    }


    /**
     *当Inky站在吃豆人面前时，他会正确地离开。根据Inky的运动规则，接下来应该向东移动。
     */
    @Test
    void testInkyMovesAway() {

        List<String> grid = new ArrayList<>();
        grid.add("#######################");
        grid.add("#      B P  I         #");
        grid.add("#######################");

        Level level = ghostMapParser.parseMap(grid);
        Player player = playerFactory.createPacMan();
        player.setDirection(Direction.EAST);
        level.registerPlayer(player);
        Inky inky = Navigation.findUnitInBoard(Inky.class, level.getBoard());

        assertThat(inky.nextAiMove()).isEqualTo(Optional.of(Direction.EAST));

    }
}
