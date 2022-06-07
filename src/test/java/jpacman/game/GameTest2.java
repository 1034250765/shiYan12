package jpacman.game;

import jpacman.Launcher;
import jpacman.board.Direction;
import jpacman.level.Player;
import jpacman.npc.ghost.Navigation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GameTest2 {
    private Launcher launcher = new Launcher();

    /**
     * 对开始游戏进行测试
     * 通过调用isInProgress()判断游戏是否成功启动。
     * 如果游戏已经开始并且正在进行中,则返回true
     * */
    @Test
    @DisplayName("开始游戏")
    void startGame() {
        launcher.withMapFile("/board.txt");
        //创建并启动一个JPac-Man游戏。
        launcher.launch();
        //模拟点击开始
        launcher.getGame().start();
        //如果游戏已经开始并且正在进行中,则返回true。
        assertThat(launcher.getGame().isInProgress()).isTrue();
    }

    /**
     * 对暂停游戏进行测试
     * 通过调用isInProgress()判断游戏是否成功启动。
     * 如果游戏已经开始并且正在进行中,则返回true
     * 如果游戏暂停，则返回false
     * */
    @Test
    @DisplayName("暂停游戏")
    void pauseGame() {
        launcher.withMapFile("/board.txt");
        launcher.launch();
        //模拟点击开始
        launcher.getGame().start();
        assertThat(launcher.getGame().isInProgress()).isTrue();
        //模拟点击暂停
        launcher.getGame().stop();
        assertThat(launcher.getGame().isInProgress()).isFalse();
    }

    /**
     * 当游戏暂停后，让游戏继续
     * */
    @Test
    @DisplayName("暂停游戏后让游戏继续")
    void GameContinue() {
        launcher.withMapFile("/board.txt");
        launcher.launch();
        //模拟点击开始
        launcher.getGame().start();
        assertThat(launcher.getGame().isInProgress()).isTrue();
        //模拟点击暂停
        launcher.getGame().stop();
        assertThat(launcher.getGame().isInProgress()).isFalse();
        //模拟点击继续
        launcher.getGame().start();
        assertThat(launcher.getGame().isInProgress()).isTrue();
    }

    /**
     * 当玩家吃完地图中的所有豆子后，就能获得游戏的胜利
     * */
    @Test
    @DisplayName("游戏胜利")
    void winGame(){
        //设计只有一个豆子的地图
        launcher.withMapFile("/test1.txt");
        launcher.launch();
        //模拟点击开始
        launcher.getGame().start();
        assertThat(launcher.getGame().isInProgress()).isTrue();
        //在棋盘中找到玩家
        Player player = Navigation.findUnitInBoard(Player.class,launcher.getGame().getLevel().getBoard());
        //模拟玩家吃完豆子
        launcher.getGame().getLevel().move(player, Direction.WEST);
        assertThat(launcher.getGame().isInProgress()).isFalse();
    }

    /**
     * 当玩家在移动过程中，与魔鬼发生碰撞后，玩家死亡，游戏失败
     * */
    @Test
    @DisplayName("游戏失败")
    void loseGame(){
        //设计只有魔鬼和玩家的地图
        launcher.withMapFile("/test2.txt");
        launcher.launch();
        //模拟点击开始
        launcher.getGame().start();
        assertThat(launcher.getGame().isInProgress()).isTrue();
        //在棋盘中找到玩家
        Player player = Navigation.findUnitInBoard(Player.class,launcher.getGame().getLevel().getBoard());
        //模拟魔鬼与玩家相碰撞
        launcher.getGame().getLevel().move(player, Direction.WEST);
        assertThat(launcher.getGame().isInProgress()).isFalse();
    }

}
