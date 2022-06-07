package jpacman.game;

import jpacman.Launcher;
import jpacman.board.Direction;
import jpacman.board.Square;
import jpacman.game.Game;
import jpacman.level.Level;
import jpacman.level.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 在同一关卡中测试不同状态下的情况
 */
public class GameTest {

    private Launcher launcher;

    /**
     * 启动游戏
     * @param mapName 一个地图文件
     */
    public void init(String[] mapName) {
        launcher = new Launcher().withMapFile(mapName[0]);
    }

    /**
     * 设置一个新的启动器
     * @param launcher 被设置的启动器
     */
    public void setLauncher(Launcher launcher) {
        this.launcher = launcher;
    }

    /**
     * @return 一个启动器为了测试
     */
    public Launcher getLauncher() {
        return launcher;
    }

    /**
     * 胜利状态：测试消耗所有关卡中的所有小球，从而赢得游戏。
     */
    @Test
    void testWin() {
        String[] map = {"/mapTest.txt"};
        init(map);        //初始化地图文件
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);        //创建模拟的观察者对象进行测试
        launcher.launch();        //创建并启动一个游戏。
        Game game = launcher.getGame();        //启动游戏对象
        game.getLevel().addObserver(levelObserver);        //在该关卡上添加一个观察者，观察游戏的状态，胜利时通知该观察者
        assertThat(game.isInProgress()).isFalse();        //测试当前游戏状态为未开始
        game.start();            //启动游戏
        Player myPlayer = game.getPlayers().get(0);     //在该游戏关卡中获取玩家对象
        game.move(myPlayer, Direction.EAST);            //指定玩家myPlayer向东移动，吃掉豆子
        game.move(myPlayer, Direction.EAST);
        game.move(myPlayer, Direction.SOUTH);
        game.move(myPlayer, Direction.WEST);            //吃掉最后一个豆子
        assertThat(myPlayer.isAlive()).isTrue();        //测试当前玩家的生命状态为活着
        //调用Mockito.verify方法测试levelWon()方法应被执行过一次
        Mockito.verify(levelObserver, Mockito.times(1)).levelWon();//levelWon()方法为观察者通知该关卡中玩家胜利并应该停止游戏
        assertThat(game.isInProgress()).isFalse();           //测试当前游戏状态已停止
        game.stop();
    }


    /**
     * 胜利状态：尝试重新开始游戏进行测试。
     */
    @Test
    void testWinStart() {
        String[] map = {"/mapTest.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);
        launcher.launch();
        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);     //将观察者添加到当前游戏关卡
        Player player = game.getPlayers().get(0);       //获取当前关卡的玩家对象

        game.start();                                   //启动游戏
        assertThat(game.isInProgress()).isTrue();      //测试已成功启动
        game.move(player, Direction.EAST);
        game.move(player, Direction.EAST);
        game.move(player, Direction.SOUTH);
        game.move(player, Direction.WEST);              //玩家多次移动吃光所有豆子
        Mockito.verify(levelObserver, Mockito.times(1)).levelWon(); //测试观察者应收到通知
        game.start();                                   //胜利后尝试重新启动游戏
        assertThat(game.isInProgress()).isFalse();      //测试游戏启动失败
    }


    /**
     * 失败状态：玩家与幽灵相遇，被杀死，而失败
     */
    @Test
    void testLose() {
        String[] map = {"/mapLose.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);        //创建模拟的观察者对象进行测试
        launcher.launch();
        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);     //在该关卡上添加一个观察者，观察游戏的状态，失败时通知该观察者
        assertThat(game.isInProgress()).isFalse();

        game.start();
        Player player = game.getPlayers().get(0);       //在该游戏关卡获取玩家对象
        game.move(player, Direction.EAST);              //玩家向东移动一格，与幽灵相遇被杀死
        assertThat(player.isAlive()).isFalse();         //玩家被杀，测试玩家当前状态为死亡状态
        Mockito.verify(levelObserver, Mockito.times(1)).levelLost();    //玩家被杀，游戏失败，测试观察者应收到通知，游戏结束
        assertThat(game.isInProgress()).isFalse();      //测试游戏当前状态应为停止状态
        game.stop();
    }


    /**
     * 失败状态：尝试重新开始。
     */
    @Test
    void testLoseStart() {
        String[] map = {"/mapLose.txt"};
        init(map);
        Level.LevelObserver levelObserver = Mockito.mock(Level.LevelObserver.class);
        launcher.launch();
        Game game = launcher.getGame();
        game.getLevel().addObserver(levelObserver);
        Player player = game.getPlayers().get(0);

        game.start();                                  //启动游戏
        assertThat(game.isInProgress()).isTrue();      //测试游戏已成功启动
        game.move(player, Direction.EAST);             //玩家与幽灵相遇，玩家被杀死，游戏失败
        Mockito.verify(levelObserver, Mockito.times(1)).levelLost();    //玩家被杀，测试观察者应收到通知
        game.start();                                  //游戏失败，尝试重新启动游戏
        assertThat(game.isInProgress()).isFalse();      //测试游戏启动失败
    }


    /**
     *正在进行状态：测试玩家吃某一个豆子，但不是最后一个
     */
    @Test
    void testConsumePellet() {
        String[] map = {"/mapTest.txt"};
        init(map);              //初始化地图文件
        launcher.launch();      //创建并启动一个JPacman游戏
        final int score = 30;   //设置分数为30，且不可修改
        Game game = launcher.getGame();     //启动游戏对象
        assertThat(game.isInProgress()).isFalse();      //测试当前游戏状态为未开始

        game.start();                   //开始游戏
        Player myPlayer = game.getPlayers().get(0);
        game.move(myPlayer, Direction.EAST);
        game.move(myPlayer, Direction.SOUTH);
        game.move(myPlayer, Direction.EAST);        //移动了三个网格空间
        assertThat(myPlayer.getScore()).isEqualTo(score);   //测试玩家吃了三个豆子，应获得30分
        assertThat(game.isInProgress()).isTrue();       //玩家未吃完所有豆子，则测试当前游戏正在进行
        game.stop();                  //停止游戏
    }

    /**
     * 正在进行状态：测试移动到一个空网格，不改变游戏状态。
     */
    @Test
    void testMoveEmpty() {
        String[] map = {"/moveEmpty.txt"};
        init(map);
        launcher.launch();
        Game game = launcher.getGame();
        assertThat(game.isInProgress()).isFalse();      //测试还未开始游戏时，当前游戏状态为False

        game.start();
        Player player = game.getPlayers().get(0);       //获取玩家对象
        game.move(player, Direction.EAST);              //玩家向东移动一格（空格）
        assertThat(player.isAlive()).isTrue();          //应未遇见幽灵，则测试当前游戏玩家为活着
        assertThat(player.getScore()).isEqualTo(0);     //因未吃到豆子，则测试当前得分为0
        assertThat(game.isInProgress()).isTrue();       //测试当前游戏正在进行
        game.stop();
    }

    /**
     * 正在进行状态：测试移动到一堵墙，保持在相同的网格空间。
     */
    @Test
    void testMoveWall() {
        String[] map = {"/moveWall.txt"};
        init(map);
        launcher.launch();
        Game game = launcher.getGame();
        assertThat(game.isInProgress()).isFalse();

        game.start();
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();                 //获取当前占用网格的对象
        game.move(player, Direction.NORTH);                 //向北移动一格
        assertThat(player.getSquare()).isEqualTo(square);   //测试当前占用网格对象
        assertThat(player.isAlive()).isTrue();              //墙不会影响玩家游戏状态，则测试当前状态为活着
        assertThat(player.getScore()).isEqualTo(0);         //测试得分为0
        assertThat(game.isInProgress()).isTrue();           //测试当前游戏正在进行
        game.stop();
    }

    /**
     * 正在进行状态：再次按下start键进行测试。
     */
    @Test
    void testInGameStart() {
        String[] map = {"/mapTest.txt"};
        init(map);
        launcher.launch();
        Game game = launcher.getGame();
        //多次启动游戏，测试当前游戏依旧正在进行
        game.start();
        assertThat(game.isInProgress()).isTrue();
        game.start();
        assertThat(game.isInProgress()).isTrue();
    }

    /**
     * 停止游戏和恢复游戏状态测试
     */
    @Test
    void testStopStart() {
        String[] map = {"/sampleMap.txt"};
        init(map);

        launcher.launch();
        Game game = launcher.getGame();
        assertThat(game.isInProgress()).isFalse();          //测试未执行game.start()时，当前游戏状态为停止状态
        game.start();                                       //启动游戏
        assertThat(game.isInProgress()).isTrue();           //测试游戏正在进行
        game.stop();                                        //暂停游戏
        assertThat(game.isInProgress()).isFalse();          //测试游戏已被暂停
        game.start();                                       //启动恢复游戏
        assertThat(game.isInProgress()).isTrue();           //测试游戏正常恢复，正在进行
        game.stop();
        assertThat(game.isInProgress()).isFalse();

    }

    /**
     * 未开始状态：测试时，游戏还没有开始，想移动到一堵墙。
     */
    @Test
    void testNotStartedMoveWall() {
        String[] map = {"/moveWall.txt"};
        init(map);
        launcher.launch();

        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();
        assertThat(game.isInProgress()).isFalse();              //游戏未开始，测试当前游戏状态为停止状态
        game.move(player, Direction.NORTH);                     //玩家尝试向北移动
        assertThat(player.getSquare()).isEqualTo(square);       //测试占用当前网格的单元
        assertThat(game.isInProgress()).isFalse();              //尝试失败，测试当前当前游戏依旧为停止状态
    }


    /**
     *未开始状态：当游戏未开始并且您想移动到一个空的方块时进行测试。
     */
    @Test
    void testNotStartedMoveEmpty() {
        String[] map = {"/moveEmpty.txt"};
        init(map);
        launcher.launch();
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        assertThat(game.isInProgress()).isFalse();
        game.move(player, Direction.EAST);                  //玩家尝试向东移动
        assertThat(game.isInProgress()).isFalse();          //测试尝试失败，当前游戏依旧为停止状态
    }


    /**
     * 未开始状态：测试时，游戏还没有开始，你想移动到一个豆子。
     */
    @Test
    void testNotStartedMovePellet() {
        String[] map = {"/mapTest.txt"};
        init(map);
        launcher.launch();
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        assertThat(game.isInProgress()).isFalse();
        game.move(player, Direction.EAST);
        assertThat(player.getScore()).isEqualTo(0);     //游戏未启动，玩家尝试移动吃掉豆子，测试尝试失败，得分为0
        assertThat(game.isInProgress()).isFalse();      //测试游戏依旧为停止状态
    }


    /**
     * 停止状态：向墙移动时进行测试。
     */
    @Test
    void testSuspendMoveWall() {
        String[] map = {"/moveWall.txt"};
        init(map);
        launcher.launch();
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);
        Square square = player.getSquare();

        game.start();                               //启动游戏
        assertThat(game.isInProgress()).isTrue();   //测试当前游戏正在进行
        game.stop();                                //停止游戏
        assertThat(game.isInProgress()).isFalse();  //测试当前游戏已停止
        game.move(player, Direction.NORTH);         //停止状态下，玩家向北移动
        assertThat(player.getSquare()).isEqualTo(square);   //测试当前所占网格单元为wall
        assertThat(game.isInProgress()).isFalse();  //测试当前游戏状态已停止
    }


    /**
     * 停止状态：移动到一个空框时进行测试。
     */
    @Test
    void testSuspendMoveEmpty() {
        String[] map = {"/moveEmpty.txt"};
        init(map);
        launcher.launch();
        Game game = launcher.getGame();
        Player player = game.getPlayers().get(0);

        game.start();
        assertThat(game.isInProgress()).isTrue();
        game.stop();
        assertThat(game.isInProgress()).isFalse();      //测试当前为暂停状态
        game.move(player, Direction.EAST);              //玩家尝试向东移动
        assertThat(game.isInProgress()).isFalse();      //测试当前依旧为暂停状态
    }

}
