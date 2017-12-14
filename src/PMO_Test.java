import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PMO_Test {

	//////////////////////////////////////////////////////////////////////////
	private static final Map<String, Double> tariff = new HashMap<>();

	static {
		// testy stare
		tariff.put("setPositionTest", 2.0);
		tariff.put("upTest", 2.0);
		tariff.put("downTest", 2.0);
		tariff.put("leftTest", 2.0);
		tariff.put("rightTest", 2.0);
		tariff.put("repeatTest", 1.0);
		tariff.put("undoTest1", 0.5);
		tariff.put("undoTest2", 0.5);
		tariff.put("undoTest3", 0.2);
		tariff.put("redoTest", 0.5);
		
		// nowe testy
		tariff.put("examplePath1", 2.0);
		tariff.put("examplePath2", 2.0);
		tariff.put("initalStateTest",2.0);
		
		tariff.put("dottedLineTest", 1.0 );
		tariff.put("setColorUndo", 1.0);
		tariff.put("setInitialStateUP", 1.0 );
		tariff.put("setFalse", 1.0 );
		tariff.put("setFalseUndo", 1.0 );
		tariff.put("setFalseUndoRedo", 1.0 );
		tariff.put("complexTest", 0.5);
	}

	public static double getTariff(String testName) {
		return tariff.get(testName);
	}
	//////////////////////////////////////////////////////////////////////////

	private ImageGeneratorConfigurationInterface configuration;
	private ImageGeneratorInterface generator;
	private ImageGeneratorPenInterface penControll;
	private final ImageGeneratorPenInterface.PenState PEN_UP = ImageGeneratorPenInterface.PenState.UP;
	private final ImageGeneratorPenInterface.PenState PEN_DOWN = ImageGeneratorPenInterface.PenState.DOWN;

	private boolean[][] canvas;
	private List<String> expected = new ArrayList<>();
	private boolean initialColor;
	private ImageGeneratorPenInterface.PenState initialPenState;

	private boolean firstPosition() {
		if ( initialPenState == PEN_UP ) return false;
		return initialColor;
	}
	
	private boolean[][] list2table(List<String> data) {
		int size = data.size();
		boolean[][] canvas = new boolean[size][size];

		for (int row = 0; row < size; row++) {
			String oneRow = data.get(size - row - 1);
			for (int col = 0; col < size; col++)
				if (oneRow.charAt(col) == '#') {
					canvas[col][row] = true;
				} 
				else if ( oneRow.charAt(col) == '?' ) {
					canvas[col][row] = firstPosition();
				} else {					
					canvas[col][row] = false;
				}
		}
		return canvas;
	}

	private void showCanvas(boolean[][] canvas) {
		System.out.println(table2String(canvas));
	}

	private boolean[][] createEmptyCanvas(int size) {
		return new boolean[size][size];
	}

	private String table2String(boolean[][] canvas) {
		StringBuilder sb = new StringBuilder();
		int size = canvas.length;

		for (int row = size - 1; row >= 0; row--) {
			for (int col = 0; col < size; col++)
				sb.append(canvas[col][row] ? " # " : " . ");
			sb.append("\n");
		}
		return sb.toString();
	}

	@Rule
	public TestName name = new TestName();

	private void setCanvas() {
		PMO_TestHelper.tryExecute(() -> {
			configuration.setCanvas(canvas);
		}, "setCanvas");
	}

	private void setInitialPosition(int col, int row) {
		PMO_TestHelper.tryExecute(() -> {
			configuration.setInitialPosition(col, row);
		}, "setInitialPosition");
	}
	
	private ImageGeneratorPenInterface.PenState getState() {
		return PMO_TestHelper.tryExecute(penControll::getPenState,
				"getPenState");
	}

	private void setStateAndTest(ImageGeneratorPenInterface.PenState state) {
		PMO_TestHelper.tryExecute(() -> {
			penControll.setPenState(state);
		}, "setPenState");

		ImageGeneratorPenInterface.PenState actualState = getState();
		assertEquals("Odczytano inny stan piora niz ustawiono", state, actualState);
	}

	private Boolean getColor() {
		return PMO_TestHelper.tryExecute(penControll::getColor, "getColor");
	}
	
	private void setColorAndTest(boolean color) {
		PMO_TestHelper.tryExecute(() -> {
			penControll.setColor(color);
		}, "setColor");
		Boolean actualColor = getColor();

		assertEquals("Odczytano inny kolor niz ustawiono ", color, actualColor);
	}

	private void setInitialState(int col, int row, ImageGeneratorPenInterface.PenState state, boolean penColor) {
		initialColor = getColor();
		initialPenState = getState();
		setInitialPosition(col, row);
		setStateAndTest(state);
		setColorAndTest(penColor);
	}

	private void setInitialState(int col, int row) {
		setInitialState(col, row, PEN_DOWN, true );
	}

	private void up(int steps) {
		PMO_TestHelper.tryExecute(() -> {
			generator.up(steps);
		}, "up");
	}

	private void down(int steps) {
		PMO_TestHelper.tryExecute(() -> {
			generator.down(steps);
		}, "down");
	}

	private void left(int steps) {
		PMO_TestHelper.tryExecute(() -> {
			generator.left(steps);
		}, "left");
	}

	private void right(int steps) {
		PMO_TestHelper.tryExecute(() -> {
			generator.right(steps);
		}, "right");
	}

	private void repeat(int steps) {
		PMO_TestHelper.tryExecute(() -> {
			generator.repeat(steps);
		}, "repeat");
	}

	private void undo(int steps) {
		PMO_TestHelper.tryExecute(() -> {
			generator.undo(steps);
		}, "undo");
	}

	private void redo(int steps) {
		PMO_TestHelper.tryExecute(() -> {
			generator.redo(steps);
		}, "redo");
	}

	private void setMaxUndoRedoRepeatCommands(int commands) {
		PMO_TestHelper.tryExecute(() -> {
			configuration.maxUndoRedoRepeatCommands(commands);
		}, "maxUndoRedoRepeatCommands");
	}

	private void test(String txt) {
		boolean[][] expectedCanvas = list2table(expected);
		int size = expectedCanvas.length;

		System.out.println("\n--------------------------------\nWynik testu " + name.getMethodName() + ":");
		showCanvas(canvas);
		System.out.println("Oczekiwano:");
		showCanvas(list2table(expected));

		for (int row = 0; row < size; row++)
			for (int col = 0; col < size; col++)
				assertEquals(txt + "; blad na pozycji canvas[" + col + "][" + row + "]", expectedCanvas[col][row],
						canvas[col][row]);
	}

	@Before
	public void create() {
		ImageGenerator ig = PMO_TestHelper.tryExecute(() -> {
			return new ImageGenerator();
		}, "Konstruktor ImageGenerator");
		configuration = ig;
		generator = ig;
		penControll = ig;
		setMaxUndoRedoRepeatCommands(25);
	}

	@Test(timeout = 500)
	public void setPositionTest() {
		canvas = createEmptyCanvas(4);
		setCanvas();
		setInitialState(1, 3);
		expected.add(".#..");
		expected.add("....");
		expected.add("....");
		expected.add("....");
		test("Test metody setPosition");
	}

	@Test(timeout = 500)
	public void downTest() {
		canvas = createEmptyCanvas(4);
		setCanvas();
		setInitialState(1, 3);
		expected.add(".#..");
		expected.add(".#..");
		expected.add(".#..");
		expected.add(".#..");
		down(3);
		test("Test metody down(3)");
	}

	@Test(timeout = 500)
	public void upTest() {
		canvas = createEmptyCanvas(5);
		setCanvas();
		setInitialState(2, 1);
		expected.add(".....");
		expected.add("..#..");
		expected.add("..#..");
		expected.add("..#..");
		expected.add(".....");
		up(2);
		test("Test metody up(2)");
	}

	@Test(timeout = 500)
	public void leftTest() {
		canvas = createEmptyCanvas(5);
		setCanvas();
		setInitialState(2, 1);
		expected.add(".....");
		expected.add(".....");
		expected.add(".....");
		expected.add("###..");
		expected.add(".....");
		left(2);
		test("Test metody left(2)");
	}

	@Test(timeout = 500)
	public void rightTest() {
		canvas = createEmptyCanvas(5);
		setCanvas();
		setInitialState(2, 1);
		expected.add(".....");
		expected.add(".....");
		expected.add(".....");
		expected.add("..###");
		expected.add(".....");
		right(2);
		test("Test metody right(2)");
	}

	@Test(timeout = 500)
	public void repeatTest() {
		canvas = createEmptyCanvas(6);
		setCanvas();
		setInitialState(2, 1);
		expected.add("...##.");
		expected.add("...#..");
		expected.add("..##..");
		expected.add("..#...");
		expected.add("..#...");
		expected.add("......");
		up(2);
		right(1);
		repeat(2);
		test("Test metody repeat(2)");
	}

	@Test(timeout = 500)
	public void undoTest1() {
		canvas = createEmptyCanvas(6);
		setCanvas();
		setInitialState(2, 1);
		expected.add("......");
		expected.add("......");
		expected.add("..##..");
		expected.add("..#...");
		expected.add("..#...");
		expected.add("......");
		up(2);
		right(1);
		repeat(2);
		undo(1);
		test("Test metody undo(1)");
	}

	@Test(timeout = 500)
	public void undoTest2() {
		canvas = createEmptyCanvas(6);
		setCanvas();
		setInitialState(2, 1);
		expected.add("......");
		expected.add("......");
		expected.add("..#...");
		expected.add("..#...");
		expected.add("..#...");
		expected.add("......");
		up(2);
		right(1);
		repeat(2);
		undo(2);
		test("Test metody undo(2)");
	}

	@Test(timeout = 500)
	public void undoTest3() {
		canvas = createEmptyCanvas(6);
		setCanvas();
		expected.add("......");
		expected.add("......");
		expected.add("..#...");
		expected.add("#.#...");
		expected.add("..#...");
		expected.add("......");
		setInitialState(2, 1);
		up(2);
		setInitialState(0, 2);
		right(4);
		undo(1);
		test("Test metody undo");
	}

	@Test(timeout = 500)
	public void redoTest() {
		canvas = createEmptyCanvas(6);
		setCanvas();
		expected.add("......");
		expected.add("#####.");
		expected.add("#...#.");
		expected.add("#...#.");
		expected.add("..###.");
		expected.add("......");
		setInitialState(0, 2);
		up(2);
		right(4);
		down(3);
		left(1);
		repeat(1);
		undo(2);
		undo(1);
		redo(2);
		redo(1);
		test("Test metody redo");
	}


	@Test(timeout = 500)
	public void examplePath1() {
		canvas = createEmptyCanvas(6);
		setCanvas();
		expected.add("......");
		expected.add("......");
		expected.add("......");
		expected.add("......");
		expected.add("..#...");
		expected.add("..#...");
		setInitialState(2, 0);
		up(4);
		setColorAndTest(false);
		down(2);
		test("Test wywolan wg. przykladu - sciezka 1");
	}

	@Test(timeout = 500)
	public void examplePath2() {
		canvas = createEmptyCanvas(6);
		setCanvas();
		expected.add("......");
		expected.add("..#...");
		expected.add("..#...");
		expected.add("......");
		expected.add("..#...");
		expected.add("..#...");
		setInitialState(2, 0);
		up(4);
		setStateAndTest(PEN_UP);
		setColorAndTest(false);
		down(2);
		setStateAndTest(PEN_DOWN);
		test("Test wywolan wg. przykladu - sciezka 2");
	}
	
	@Test(timeout = 500)
	public void dottedLineTest() {
		canvas = createEmptyCanvas(10);
		setCanvas();
		expected.add("..#.......");
		expected.add("..#.......");
		expected.add("..#.......");
		expected.add("..........");
		expected.add("..........");
		expected.add("..#.......");
		expected.add("..#.......");
		expected.add("..........");
		expected.add("..........");
		expected.add("..#.......");
		setInitialState(2, 0);
		up(1);
		setColorAndTest( false );
		up(2);
		setColorAndTest( true );
		up(2);
		repeat(4);
		test("Rysowanie linii przerywanej");
	}

	@Test(timeout = 500)
	public void setColorUndo() {
		canvas = createEmptyCanvas(10);
		setCanvas();
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..#####...");
		expected.add("..#.......");
		setInitialState(2, 0);
		up(1);
		setColorAndTest( false );
		up(2);
		setColorAndTest( true );
		up(2);
		undo(4);
		right(4);
		test("Test undo z setColor");
	}
	
	@Test(timeout = 500)
	public void initalStateTest() {
		canvas = createEmptyCanvas(5);
		setCanvas();
		setInitialState(2, 2, PEN_UP, true );
		boolean expected = firstPosition();
		boolean result = canvas[2][2];
		
		assertEquals( "Bledny znak na plotnie. Poczatkowy stan piora to " + initialPenState + "/" + initialColor, expected, result);
	}
	
	@Test(timeout = 500)
	public void setInitialStateUP() {
		canvas = createEmptyCanvas(10);
		setCanvas();
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add(".......#..");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..?.......");
		expected.add("..........");
		setInitialState(2, 1, PEN_UP, true );
		up(5);
		right(5);
		setStateAndTest(PEN_DOWN);
		test("Initial pen state set to UP");
	}

	@Test(timeout = 500)
	public void setFalse() {
		canvas = createEmptyCanvas(10);
		setCanvas();
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add(".......#..");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		setInitialState(2, 1, PEN_DOWN, false ); 
		// pioro jest opuszczone i setColor zmienia pozycje [2][1] na false
		up(5);
		right(5);
		setColorAndTest(true);
		test("Initial pen state set to DOWN/false");
	}

	@Test(timeout = 500)
	public void setFalseUndo() {
		canvas = createEmptyCanvas(10);
		setCanvas();
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..#.......");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		setInitialState(2, 1, PEN_DOWN, false ); 
		// pioro jest opuszczone i setColor zmienia pozycje [2][1] na false
		up(5);
		right(5);
		setColorAndTest(true);
		undo(1);
		left(5);
		setColorAndTest(true);
		test("Initial pen state set to DOWN/false");
	}

	@Test(timeout = 500)
	public void setFalseUndoRedo() {
		canvas = createEmptyCanvas(10);
		setCanvas();
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("...#####..");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		setInitialState(2, 1, PEN_DOWN, false ); 
		// pioro jest opuszczone i setColor zmienia pozycje [2][1] na false
		up(5);
		right(5);
		setColorAndTest(true);
		undo(1);
		redo(1);
		left(5);
		setColorAndTest(false);
		test("Initial pen state set to DOWN/false");
	}

	@Test(timeout = 500)
	public void complexTest() {
		canvas = createEmptyCanvas(10);
		setCanvas();
		expected.add("..........");
		expected.add("..........");
		expected.add("..........");
		expected.add("....#.....");
		expected.add("....#.....");
		expected.add("......#.#.");
		expected.add(".#..#...#.");
		expected.add("....#.....");
		expected.add("....#.....");
		expected.add("..........");
		setInitialState(4, 1 ); 
		// pioro jest opuszczone i setColor zmienia pozycje [2][1] na false
		up(5);
		setStateAndTest(PEN_UP);
		right(2);
		down(3);
		setColorAndTest(true);
		left(5);
		setStateAndTest(PEN_DOWN);
		up(1);
		setColorAndTest(false);
		right(5);
		setColorAndTest(true);
		setStateAndTest(PEN_UP);
		up(1);
		setStateAndTest(PEN_DOWN);
		undo(2);
		right(2);
		setStateAndTest(PEN_DOWN);
		setStateAndTest(PEN_UP);
		undo(1);
		down(1);
		test("Ciag operacji zmiany stanu/koloru");
	}
}
