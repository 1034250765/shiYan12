package jpacman.points;

import jpacman.board.Direction;
import jpacman.level.Player;
import jpacman.level.Pellet;
import jpacman.npc.Ghost;

/**
 * The responsibility of the point calculator is to update the points
 * of the player when certain activities happen.
 * Different calculation strategies can be employed,
 * giving rise to different types of games, for example at different levels.
 */
public interface PointCalculator {

    /**
     * Method called whenever a player meets a ghost. 每当玩家遇到幽灵时调用。
     * It can be used to update the player's points accordingly. 它可以用来更新玩家的分数。
     *
     * @param player
     *            The player that will die. 即将死亡的玩家。
     * @param ghost
     *            The ghost causing the player to die. 导致玩家死亡的幽灵。
     */
    void collidedWithAGhost(Player player, Ghost ghost);

    /**
     * Method called whenever a player consumes a pellet.每当玩家消耗一个小球时调用。
     * It can be used to update the player's points accordingly. 它可以用来更新玩家的分数。
     *
     * @param player
     *            The player consuming a pellet  玩家消耗一个小球
     * @param pellet
     *            The pellet consumed.  颗粒消耗。
     */
    void consumedAPellet(Player player, Pellet pellet);

    /**
     * Method called whevener a player makes a successful move.
     * It can be used to update the player's points accordingly.
     *
     * @param player
     *           The player making a move.
     * @param direction
     *           The direction of the move.
     */
    void pacmanMoved(Player player, Direction direction);
}
