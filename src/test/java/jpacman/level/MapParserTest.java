package jpacman.level;

import jpacman.PacmanConfigurationException;
import jpacman.board.BoardFactory;
import jpacman.board.Square;
import jpacman.level.LevelFactory;
import jpacman.level.MapParser;
import jpacman.level.Pellet;
import jpacman.npc.Ghost;
import org.junit.jupiter.api.*;

import java.io.IOException;


import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.BDDAssertions.then;

import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MapParserTest {

	private MapParser mapParser;
	private final LevelFactory levelFactory = mock(LevelFactory.class);
	private final BoardFactory boardFactory = mock(BoardFactory.class);
	
	@BeforeEach
	void setup() {
		mapParser = new MapParser(levelFactory, boardFactory);
		
		when(boardFactory.createGround()).thenReturn(mock(Square.class));
		when(boardFactory.createWall()).thenReturn(mock(Square.class));
		
		when(levelFactory.createGhost()).thenReturn(mock(Ghost.class));
		when(levelFactory.createPellet()).thenReturn(mock(Pellet.class));
	}
	
	@Test
	@Order(1)
	@DisplayName("null文件名")
	void nullFile() {
		Assertions.assertThatThrownBy(()->{
			mapParser.parseMap((String)null);
		}).isInstanceOf(NullPointerException.class);
	}
	
	@Test
	@Order(2)
	@DisplayName("读取不存在的文件")
	void notExistFile() {
		String file = "/notExistMap.txt";
		assertThatThrownBy(()->{
			mapParser.parseMap(file);
		}).isInstanceOf(PacmanConfigurationException.class)
		.hasMessage("Could not get resource for: "+file);
	}
	
	@Test
	@Order(3)
	@DisplayName("存在的文件")
	void exitsFile() throws IOException{
		String file = "/simpleMap.txt";
		mapParser.parseMap(file);
		
		verify(boardFactory,times(4)).createGround();
		verify(boardFactory,times(2)).createWall();
		verify(levelFactory).createGhost();
	}
	
	@Test
	@Order(4)
	@DisplayName("不识别的地图文件")
	void unRecognizeMap() {
		String file = "/unRecognizeMap.txt";
		assertThatThrownBy(()->{
			mapParser.parseMap(file);
		}).isInstanceOf(PacmanConfigurationException.class)
		.hasMessage("Invalid character at 0,0: c");
	}
}
