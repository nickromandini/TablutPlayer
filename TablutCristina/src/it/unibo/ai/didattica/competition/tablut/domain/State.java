package it.unibo.ai.didattica.competition.tablut.domain;

import it.unibo.ai.didattica.competition.tablut.client.Evaluation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Abstract class for a State of a game We have a representation of the board
 * and the turn
 *
 * @author Andrea Piretti
 *
 */
public abstract class State {

	private int[] kingCoord = new int[]{-1,-1};
	private List<int[]> citadels = Stream.of(new int[]{3,0}, new int[]{4,0}, new int[]{5,0}, new int[]{4,1},
			new int[]{7,3}, new int[]{7,4}, new int[]{7,5}, new int[]{6,4},
			new int[]{3,7}, new int[]{4,7}, new int[]{5,7}, new int[]{4,6},
			new int[]{0,3}, new int[]{0,4}, new int[]{0,5}, new int[]{1,4}).collect(Collectors.toList());
	private List<int[]> citadelsBorders = Stream.of(new int[]{3,0}, new int[]{5,0}, new int[]{4,1},
			new int[]{7,3}, new int[]{7,5}, new int[]{6,4},
			new int[]{3,7}, new int[]{5,7}, new int[]{4,6},
			new int[]{0,3}, new int[]{0,5}, new int[]{1,4}).collect(Collectors.toList());
	List<int[]> escapePoints = Stream.of(new int[]{0,0}, new int[]{1,0}, new int[]{2,0},
			new int[]{6,0}, new int[]{7,0}, new int[]{8,0},
			new int[]{0,1}, new int[]{0,2},
			new int[]{0,6}, new int[]{0,7}, new int[]{0,8},
			new int[]{1,8}, new int[]{2,8},
			new int[]{6,8}, new int[]{7,8}, new int[]{8,8},
			new int[]{8,7}, new int[]{8,6},
			new int[]{8,2}, new int[]{8,1}, new int[]{8,0}).collect(Collectors.toList());

	/**
	 * Turn represent the player that has to move or the end of the game(A win
	 * by a player or a draw)
	 *
	 * @author A.Piretti
	 */
	public enum Turn {
		WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
		private final String turn;

		private Turn(String s) {
			turn = s;
		}

		public boolean equalsTurn(String otherName) {
			return (otherName == null) ? false : turn.equals(otherName);
		}

		public String toString() {
			return turn;
		}
	}

	/**
	 *
	 * Pawn represents the content of a box in the board
	 *
	 * @author A.Piretti
	 *
	 */
	public enum Pawn {
		EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
		private final String pawn;

		private Pawn(String s) {
			pawn = s;
		}

		public boolean equalsPawn(String otherPawn) {
			return (otherPawn == null) ? false : pawn.equals(otherPawn);
		}

		public String toString() {
			return pawn;
		}

	}

	protected Pawn board[][];
	protected Turn turn;

	public State() {
		super();
	}

	public Pawn[][] getBoard() {
		return board;
	}

	public String boardString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board.length; j++) {
				result.append(this.board[i][j].toString());
				if (j == 8) {
					result.append("\n");
				}
			}
		}
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString());

		result.append("-");
		result.append("\n");

		// TURNO
		result.append(this.turn.toString());

		return result.toString();
	}

	public String toLinearString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString().replace("\n", ""));
		result.append(this.turn.toString());

		return result.toString();
	}

	/**
	 * this function tells the pawn inside a specific box on the board
	 *
	 * @param row
	 *            represents the row of the specific box
	 * @param column
	 *            represents the column of the specific box
	 * @return is the pawn of the box
	 */
	public Pawn getPawn(int row, int column) {
		return this.board[row][column];
	}

	/**
	 * this function remove a specified pawn from the board
	 *
	 * @param row
	 *            represents the row of the specific box
	 * @param column
	 *            represents the column of the specific box
	 *
	 */
	public void removePawn(int row, int column) {
		this.board[row][column] = Pawn.EMPTY;
	}

	public void setBoard(Pawn[][] board) {
		this.board = board;
	}

	public Turn getTurn() {
		return turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (this.board == null) {
			if (other.board != null)
				return false;
		} else {
			if (other.board == null)
				return false;
			if (this.board.length != other.board.length)
				return false;
			if (this.board[0].length != other.board[0].length)
				return false;
			for (int i = 0; i < other.board.length; i++)
				for (int j = 0; j < other.board[i].length; j++)
					if (!this.board[i][j].equals(other.board[i][j]))
						return false;
		}
		if (this.turn != other.turn)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.board == null) ? 0 : this.board.hashCode());
		result = prime * result + ((this.turn == null) ? 0 : this.turn.hashCode());
		return result;
	}

	public String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}

	public State clone() {
		Class<? extends State> stateclass = this.getClass();
		Constructor<? extends State> cons = null;
		State result = null;
		try {
			cons = stateclass.getConstructor(stateclass);
			result = cons.newInstance(new Object[0]);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		Pawn oldboard[][] = this.getBoard();
		Pawn newboard[][] = result.getBoard();

		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				newboard[i][j] = oldboard[i][j];
			}
		}

		result.setBoard(newboard);
		result.setTurn(this.turn);
		return result;
	}



	/*****************************************************************************/
	/*
	 * Sposta la pedina bianca/nera a seconda che il turno sia di White o di Black
	 * e cambia il turno corrente
	 */
	/*public void move(Action action) {
		this.board[action.getRowFrom()][action.getColumnFrom()] = Pawn.EMPTY;
		if (this.turn.equalsTurn("WHITE")) {
			this.board[action.getRowTo()][action.getColumnTo()] = Pawn.WHITE;
			this.setTurn(turn.BLACK);
		}
		else {
			this.board[action.getRowTo()][action.getColumnTo()] = Pawn.BLACK;
			this.setTurn(turn.WHITE);
		}
	}*/
	public void move(Action a) {
		Pawn pawn = getPawn(a.getRowFrom(), a.getColumnFrom());

		// libero il trono o una casella qualunque
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			board[a.getRowFrom()][a.getColumnFrom()] = Pawn.THRONE;
		} else {
			board[a.getRowFrom()][a.getColumnFrom()] = Pawn.EMPTY;
		}

		// metto nel nuovo tabellone la pedina mossa
		board[a.getRowTo()][a.getColumnTo()] = pawn;
		// aggiorno il tabellone
		// cambio il turno
		if (getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			setTurn(Turn.BLACK);
		} else {
			setTurn(Turn.WHITE);
		}

	}


	/*
	 * Verifica che lo stato sia terminale
	 */
	public boolean isTerminalWhite() {
		return kingOnEscapePoint();
	}

	public boolean isTerminalBlack() {
		return kingEatable();
	}


	public TreeMap<Integer,Action> getAllLegalMoves() {
		TreeMap<Integer,Action> actions = new TreeMap<>();

		for(int[] coordPawn : this.getPawnsCoord()) {
			actions.putAll(getAllLegalMovesInDirection(coordPawn, "NORTH"));
			actions.putAll(getAllLegalMovesInDirection(coordPawn, "SOUTH"));
			actions.putAll(getAllLegalMovesInDirection(coordPawn, "WEST"));
			actions.putAll(getAllLegalMovesInDirection(coordPawn, "EAST"));
		}
		return actions;
	}


	private TreeMap<Integer,Action> getAllLegalMovesInDirection(int[] coordPawn, String direction) {

		TreeMap<Integer,Action> actionsEvaluated = new TreeMap<>();
		int x = coordPawn[0];
		int y = coordPawn[1];
		Evaluation evaluator = new Evaluation();

		switch (direction){
			case "EAST":
				for(int i = y + 1; i < 9; i++) {
					if(this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(new int[]{x,i})) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(new int[]{x,y}) && this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString())){
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
				break;
			case "WEST":
				for(int i = y - 1; i >= 0; i--) {
					if(this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(new int[]{x,i})) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(new int[]{x,y}) && this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString())) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
				break;
			case "NORTH":
				for(int i = x - 1; i >= 0; i--) {
					if(this.getPawn(i,y).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(new int[]{i,y})) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(new int[]{x,y}) && this.getPawn(i,y).equalsPawn(Pawn.EMPTY.toString())){
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
				break;
			case "SOUTH":
				for(int i = x + 1; i < 9; i++) {
					if(this.getPawn(i, y).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(new int[]{i,y})) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(new int[]{x,y}) && this.getPawn(i, y).equalsPawn(Pawn.EMPTY.toString())){
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							actionsEvaluated.put(evaluator.evaluateAction(newAction, this, direction), newAction);
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
				break;

		}


		return actionsEvaluated;
	}

	public int[] getKingCoord() {

		if(kingCoord[0] == -1) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(Pawn.KING.toString())) {
						kingCoord[0] = i;
						kingCoord[1] = j;
						break;
					}
				}
			}
		}
		return kingCoord;
	}

	public List<int[]> getEnemyPawnsCoord(String enemy) {
		List<int[]> pawns = new ArrayList<>();
		int[] buf;
		if (enemy.equals("BLACK")) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(Pawn.BLACK.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						pawns.add(buf);
					}
				}
			}
		} else if(enemy.equals("WHITE")){
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(Pawn.WHITE.toString())
							|| this.getPawn(i, j).equalsPawn(Pawn.KING.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						pawns.add(buf);
					}
				}
			}
		}

		return pawns;
	}

	public List<int[]> getPawnsCoord() {
		List<int[]> pawns = new ArrayList<>();
		int[] buf;
		if (this.getTurn().equals(Turn.WHITE)) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(Pawn.WHITE.toString())
							|| this.getPawn(i, j).equalsPawn(Pawn.KING.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						if(this.getPawn(i, j).equalsPawn(Pawn.KING.toString())) {
							kingCoord[0] = i;
							kingCoord[1] = j;
						}
						pawns.add(buf);
					}
				}
			}
		} else {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(Pawn.BLACK.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						pawns.add(buf);
					} else if(this.getPawn(i, j).equalsPawn(Pawn.KING.toString())) {
						kingCoord[0] = i;
						kingCoord[1] = j;
					}
				}
			}
		}

		return pawns;
	}


	public boolean kingCanEscapeInTwoMoves() {
		if(kingCoord[0] == -1)
			kingCoord = getKingCoord();

		return checkNorth(kingCoord[0], kingCoord[1]) || checkSouth(kingCoord[0], kingCoord[1]) || checkWest(kingCoord[0], kingCoord[1]) || checkEast(kingCoord[0], kingCoord[1]);

	}

	private boolean checkNorth(int x, int y) {
		for(int i = x - 1; i >= 0; i--) {
			if(!getPawn(i,y).equalsPawn("O"))
				return false;
			if(kingCanEscape(i,x, "EAST") || kingCanEscape(i,x, "WEST"))
				return true;
		}
		return false;
	}

	private boolean checkSouth(int x, int y) {
		for(int i = x + 1; i < 9; i++) {
			if(!getPawn(i,y).equalsPawn("O"))
				return false;
			if(kingCanEscape(i,x, "EAST") || kingCanEscape(i,x, "WEST"))
				return true;
		}
		return false;
	}

	private boolean checkWest(int x, int y) {
		for(int i = y - 1; i >= 0; i--) {
			if(!getPawn(x,i).equalsPawn("O"))
				return false;
			if(kingCanEscape(x, i,"NORTH") || kingCanEscape(x, i, "SOUTH"))
				return true;
		}
		return false;
	}

	private boolean checkEast(int x, int y) {
		for(int i = y + 1; i < 9; i++) {
			if(!getPawn(x,i).equalsPawn("O"))
				return false;
			if(kingCanEscape(x, i,"NORTH") || kingCanEscape(x, i,"SOUTH"))
				return true;
		}
		return false;
	}


	public boolean kingCanEscape() {
		if(kingCoord[0] == -1)
			kingCoord = getKingCoord();
		return (kingCanEscape(kingCoord[0], kingCoord[1],"NORTH") || kingCanEscape(kingCoord[0], kingCoord[1],"SOUTH")
					|| kingCanEscape(kingCoord[0], kingCoord[1],"EAST") || kingCanEscape(kingCoord[0], kingCoord[1],"WEST"));
	}

	public boolean kingCanEscape(int x, int y) {
		return (kingCanEscape(x,y,"NORTH") || kingCanEscape(x,y,"SOUTH")
					|| kingCanEscape(x,y,"EAST") || kingCanEscape(x,y,"WEST"));
	}


	public boolean kingCanEscape(String direction) {
		if(kingCoord[0] == -1)
			getKingCoord();
		return kingCanEscape(kingCoord[0], kingCoord[1], direction);
	}

	public boolean kingCanEscape(int x, int y, String direction) {
		switch (direction) {
			case "NORTH":
				if(onCitadels(new int[]{0,y})) {
					return false;
				}
				for(int i = x - 1; i >= 0; i--) {
					if (!this.getPawn(i,y).equalsPawn("O")) {
						return false;
					}
				}
				break;
			case "SOUTH":
				if(onCitadels(new int[]{8,y})) {
					return false;
				}
				for(int i = x + 1; i < 9; i++) {
					if (!this.getPawn(i,y).equalsPawn("O")) {
						return false;
					}
				}
				break;
			case "WEST":
				if(onCitadels(new int[]{x,0})) {
					return false;
				}
				for(int i = y - 1; i >= 0; i--) {
					if (!this.getPawn(x,i).equalsPawn("O")) {
						return false;
					}
				}
				break;
			case "EAST":
				if(onCitadels(new int[]{x,8})) {
					return false;
				}
				for(int i = y + 1; i < 9; i++) {
					if (!this.getPawn(x,i).equalsPawn("O")) {
						return false;
					}
				}
				break;
		}



		return true;
	}

	public boolean onCitadelsBorder(int[] coord) {
		return citadelsBorders.parallelStream().anyMatch(a -> Arrays.equals(a, coord));
	}

	public boolean onCitadels(int[] coord) {
		return citadels.parallelStream().anyMatch(a -> Arrays.equals(a, coord));
	}

	public boolean kingOnEscapePoint() {
		if(kingCoord[0]==-1) {
			return kingOnEscapePoint(getKingCoord());
		} else
			return kingOnEscapePoint(kingCoord);
	}

	public boolean kingOnEscapePoint(int[] coord) {
		List<int[]> escapePoints = Stream.of(new int[]{0,0}, new int[]{1,0}, new int[]{2,0},
				new int[]{6,0}, new int[]{7,0}, new int[]{8,0},
				new int[]{0,1}, new int[]{0,2},
				new int[]{0,6}, new int[]{0,7}, new int[]{0,8},
				new int[]{1,8}, new int[]{2,8},
				new int[]{6,8}, new int[]{7,8}, new int[]{8,8},
				new int[]{8,7}, new int[]{8,6},
				new int[]{8,2}, new int[]{8,1}, new int[]{8,0}).collect(Collectors.toList());
		return escapePoints.parallelStream().anyMatch(a -> Arrays.equals(a, coord));
	}


	public boolean kingEatable() {

		return kingEatable(kingCoord[0], kingCoord[1]);

	}

	// Fatta male

	public boolean kingEatable(int x, int y) {

		try {
			Pawn north = this.getPawn(x - 1, y);
			Pawn south = this.getPawn(x + 1, y);
			Pawn west = this.getPawn(x, y - 1);
			Pawn east = this.getPawn(x, y + 1);

			if (x == 4 && y == 4) {
				if (north.equalsPawn("B") && south.equalsPawn("B") && west.equalsPawn("B") && east.equalsPawn("B")) {
					return true;
				} else {
					return false;
				}
			} else if (((north.equalsPawn("B") && south.equalsPawn("B")) || (west.equalsPawn("B") && east.equalsPawn("B")))
					&& !north.equalsPawn("T") && !south.equalsPawn("T") && !west.equalsPawn("T") && !east.equalsPawn("T"))
				return true;
			else if ((north.equalsPawn("T") || north.equalsPawn("B")) &&
					(south.equalsPawn("T") || south.equalsPawn("B")) &&
					(east.equalsPawn("T") || east.equalsPawn("B")) &&
					(west.equalsPawn("T") || west.equalsPawn("B"))) {
				return true;
			}
		}catch (Exception e) {}

		return false;

	}

	public boolean isKingEated() {
		try {
			if(getPawn(kingCoord[0], kingCoord[1]) != null)
				return false;
		} catch (Exception e) {

		}
		return true;
	}

	public int minKingDistanceFromSafe() {

		if(kingCoord[0] == -1) {
			kingCoord = getKingCoord();
		}

		double minDist = 100;
		double temp;

		escapePoints = escapePoints.stream().filter(coord -> getPawn(coord[0], coord[1]).equalsPawn("O")).collect(Collectors.toList());

		for(int[] coord : escapePoints) {
			temp = Math.sqrt(Math.pow(Math.abs(this.kingCoord[0] - coord[0]), 2) + Math.pow(Math.abs(this.kingCoord[1] - coord[1]), 2));
			if (temp < minDist)
				minDist = temp;
		}
		return (int)minDist;
	}


	public boolean kingCanBeEaten() {
		if(kingCoord[0] == -1) {
			kingCoord = getKingCoord();
		}

		boolean northBorder = false;
		boolean southBorder = false;
		boolean westBorder = false;
		boolean eastBorder = false;

		Pawn north = null;
		Pawn south = null;
		Pawn west = null;
		Pawn east = null;


		try {
			north = this.getPawn(kingCoord[0] - 1, kingCoord[1]);
		} catch (Exception e) {
			northBorder = true;
		}

		try {
			south = this.getPawn(kingCoord[0] + 1, kingCoord[1]);
		} catch (Exception e) {
			southBorder = true;
		}

		try {
			west = this.getPawn(kingCoord[0], kingCoord[1] - 1);
		} catch (Exception e) {
			westBorder = true;
		}

		try {
			east = this.getPawn(kingCoord[0], kingCoord[1] + 1);
		} catch (Exception e) {
			eastBorder = true;
		}


		// Re sul trono

		if(kingCoord[0] == 4 && kingCoord[1] == 4) {
			if(north.equalsPawn("B") && south.equalsPawn("B") && west.equalsPawn("B")
				&& anEnemyCanArriveEast(kingCoord, "BLACK"))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "BLACK"))
				return true;
			else if(north.equalsPawn("B") && west.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "BLACK"))
				return true;
			else if(south.equalsPawn("B") && west.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveNorth(kingCoord, "BLACK"))
				return true;

		}

		// Re vicino al trono

		if(kingCoord[0] == 3 && kingCoord[1] == 4) {
			if(north.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveEast(kingCoord, "BLACK"))
				return true;
			else if(north.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "BLACK"))
				return true;
			else if(east.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveNorth(kingCoord, "BLACK"))
				return true;
		} else if(kingCoord[0] == 4 && kingCoord[1] == 3) {
			if(north.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "BLACK"))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "BLACK"))
				return true;
			else if(south.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveNorth(kingCoord, "BLACK"))
				return true;
		} else if(kingCoord[0] == 4 && kingCoord[1] == 5) {
			if(north.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "BLACK"))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B")
					&& anEnemyCanArriveEast(kingCoord, "BLACK"))
				return true;
			else if(south.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveNorth(kingCoord, "BLACK"))
				return true;
		} else if(kingCoord[0] == 5 && kingCoord[1] == 4) {
			if(south.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "BLACK"))
				return true;
			else if(south.equalsPawn("B") && south.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "BLACK"))
				return true;
			else if(south.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "BLACK"))
				return true;
		}

		// Campo aperto

		if(((north.equalsPawn("B") || onCitadels(new int[]{kingCoord[0] - 1, kingCoord[1]})) && anEnemyCanArriveSouth(kingCoord, "BLACK"))
				|| ((south.equalsPawn("B") || onCitadels(new int[]{kingCoord[0] + 1, kingCoord[1]})) && anEnemyCanArriveNorth(kingCoord, "BLACK"))
				|| ((west.equalsPawn("B") || onCitadels(new int[]{kingCoord[0], kingCoord[1] - 1})) && anEnemyCanArriveEast(kingCoord, "BLACK"))
				|| ((east.equalsPawn("B") || onCitadels(new int[]{kingCoord[0], kingCoord[1] + 1})) && anEnemyCanArriveWest(kingCoord, "BLACK")))
			return true;




		return false;

	}

	private boolean anEnemyCanArriveNorth(int[] coord, String enemy) {
		coord[0]--;
		return (checkNorthEnemyMovement(coord, enemy) || checkWestEnemyMovement(coord, enemy) || checkEastEnemyMovement(coord, enemy));
	}

	private boolean anEnemyCanArriveSouth(int[] coord, String enemy) {
		coord[0]++;
		return (checkSouthEnemyMovement(coord, enemy) || checkWestEnemyMovement(coord, enemy) || checkEastEnemyMovement(coord, enemy));
	}

	private boolean anEnemyCanArriveWest(int[] coord, String enemy) {
		coord[1]--;
		return (checkNorthEnemyMovement(coord, enemy) || checkSouthEnemyMovement(coord, enemy) || checkWestEnemyMovement(coord, enemy));
	}

	private boolean anEnemyCanArriveEast(int[] coord, String enemy) {
		coord[1]++;
		return (checkNorthEnemyMovement(coord, enemy) || checkSouthEnemyMovement(coord, enemy) || checkEastEnemyMovement(coord, enemy));
	}


	/*private boolean anEnemyCanMoveHere(int[] coord) {
		return (checkNorthEnemyMovement(coord) || checkSouthEnemyMovement(coord)
				|| checkWestEnemyMovement(coord) || checkEastEnemyMovement(coord));
	}*/

	private boolean checkNorthEnemyMovement(int[] coord, String enemy) {
		int x = coord[0];
		int y = coord[1];
		for(int i = x; i>=0; i--) {
			Pawn up = this.getPawn(i,y);
			if(!up.equalsPawn("O"))
				if(enemy.equals("BLACK") && up.equalsPawn("B"))
					return true;
				else if(enemy.equals("WHITE") && (up.equalsPawn("W") || up.equalsPawn("K")))
					return true;
				else return false;

		}

		return false;

	}

	private boolean checkSouthEnemyMovement(int[] coord, String enemy) {
		int x = coord[0];
		int y = coord[1];

		for(int i = x; i<9; i++) {
			Pawn down = this.getPawn(i,y);
			if(!down.equalsPawn("O"))
				if(enemy.equals("BLACK") && down.equalsPawn("B"))
					return true;
				else if(enemy.equals("WHITE") && (down.equalsPawn("W") || down.equalsPawn("K")))
					return true;
				else return false;

		}

		return false;

	}

	private boolean checkWestEnemyMovement(int[] coord, String enemy) {
		int x = coord[0];
		int y = coord[1];

		for(int i = y; i>=0; i--) {
			Pawn left = this.getPawn(x,i);
			if(!left.equalsPawn("O"))
				if(enemy.equals("BLACK") && left.equalsPawn("B"))
					return true;
				else if(enemy.equals("WHITE") && (left.equalsPawn("W") || left.equalsPawn("K")))
					return true;
				else return false;

		}

		return false;
	}

	private boolean checkEastEnemyMovement(int[] coord, String enemy) {
		int x = coord[0];
		int y = coord[1];

		for(int i = y; i<9; i++) {
			Pawn right = this.getPawn(x,i);
			if(enemy.equals("BLACK") && right.equalsPawn("B"))
				return true;
			else if(enemy.equals("WHITE") && (right.equalsPawn("W") || right.equalsPawn("K")))
				return true;
			else return false;

		}

		return false;
	}

	public int enemiesNearKing() {

		int enemies = 0;

		if(kingCoord[0] == -1) {
			kingCoord = getKingCoord();
		}

		int x = kingCoord[0] - 2 >= 0 ? kingCoord[0] - 2 : (kingCoord[0] - 1 >= 0 ? kingCoord[0] - 1 : kingCoord[0]);
		int y = kingCoord[1] - 2 >= 0 ? kingCoord[1] - 2 : (kingCoord[1] - 1 >= 0 ? kingCoord[1] - 1 : kingCoord[1]);

		for (int i = x; i < x + 5 && i < board.length; i++) {
			for (int j = y; j < y + 5 && j < board.length; j++) {
				if (this.getPawn(i, j).equalsPawn(Pawn.BLACK.toString())) {
					enemies++;
				}
			}
		}

		return enemies;

	}

	public boolean enemyPawnCanBeEaten(String me) {

		String enemy = me.equals("W") ? "BLACK" : "WHITE";

		List<int[]> enemyPawns = getEnemyPawnsCoord(enemy);

		boolean northBorder = false;
		boolean southBorder = false;
		boolean westBorder = false;
		boolean eastBorder = false;

		Pawn north = null;
		Pawn south = null;
		Pawn west = null;
		Pawn east = null;


		for( int[] pawn : enemyPawns) {

			// Non posso mangiare neri dentro accampamento
			if(onCitadels(pawn) && enemy.equals("BLACK"))
				continue;

			try {
				north = getPawn(pawn[0] - 1, pawn[1]);
			} catch (Exception e) {
				northBorder = true;
			}

			try {
				south = getPawn(pawn[0] + 1, pawn[1]);
			} catch (Exception e) {
				southBorder = true;
			}

			try {
				west = getPawn(pawn[0], pawn[1] - 1);
			} catch (Exception e) {
				westBorder = true;
			}

			try {
				east = getPawn(pawn[0], pawn[1] + 1);
			} catch (Exception e) {
				eastBorder = true;
			}


			// Campo aperto

			if(!northBorder && !southBorder && (north.equalsPawn(me) && anEnemyCanArriveSouth(pawn, enemy)))
				return true;

			if(!northBorder && !southBorder && (south.equalsPawn(me) && anEnemyCanArriveNorth(pawn, enemy)))
				return true;

			if(!westBorder && !eastBorder && (west.equalsPawn(me) && anEnemyCanArriveEast(pawn, enemy)))
				return true;

			if(!westBorder && !eastBorder && (east.equalsPawn(me) && anEnemyCanArriveWest(pawn, enemy)))
				return true;


			// Vicinanza trono

			if((!northBorder && !southBorder && pawn[0] - 1 == 4 && pawn[1] == 4 && anEnemyCanArriveSouth(pawn, enemy))
					|| (!northBorder && !southBorder && pawn[0] + 1 == 4 && pawn[1] == 4 && anEnemyCanArriveNorth(pawn, enemy))
					|| (!westBorder && !eastBorder && pawn[0] == 4 && pawn[1] - 1 == 4 && anEnemyCanArriveEast(pawn, enemy))
					|| (!westBorder && !eastBorder && pawn[0] == 4 && pawn[1] + 1 == 4 && anEnemyCanArriveWest(pawn, enemy)))
				return true;

			// Vicinanza cittadella

			if((!southBorder && onCitadelsBorder(new int[]{pawn[0] - 1, pawn[1]}) && anEnemyCanArriveSouth(pawn, enemy))
					|| (!northBorder && onCitadelsBorder(new int[]{pawn[0] + 1, pawn[1]}) && anEnemyCanArriveNorth(pawn, enemy))
					|| (!eastBorder && onCitadelsBorder(new int[]{pawn[0], pawn[1] - 1}) && anEnemyCanArriveEast(pawn, enemy))
					|| (!westBorder && onCitadelsBorder(new int[]{pawn[0], pawn[1] + 1}) && anEnemyCanArriveWest(pawn, enemy)))
				return true;



		}

		return false;
	}

	public boolean enemyPawnEatable(String me) {

		String enemy = me.equals("W") ? "BLACK" : "WHITE";

		List<int[]> enemyPawns = getEnemyPawnsCoord(enemy);

		boolean northBorder = false;
		boolean southBorder = false;
		boolean westBorder = false;
		boolean eastBorder = false;

		Pawn north = null;
		Pawn south = null;
		Pawn west = null;
		Pawn east = null;


		for( int[] pawn : enemyPawns) {

			// Non posso mangiare neri dentro accampamento
			if(onCitadels(pawn) && me.equals("W"))
				continue;

			try {
				north = getPawn(pawn[0] - 1, pawn[1]);
			} catch (Exception e) {
				northBorder = true;
			}

			try {
				south = getPawn(pawn[0] + 1, pawn[1]);
			} catch (Exception e) {
				southBorder = true;
			}

			try {
				west = getPawn(pawn[0], pawn[1] - 1);
			} catch (Exception e) {
				westBorder = true;
			}

			try {
				east = getPawn(pawn[0], pawn[1] + 1);
			} catch (Exception e) {
				eastBorder = true;
			}


			// Campo aperto

			if(!northBorder && !southBorder && (north.equalsPawn(me) && south.equalsPawn(me)))
				return true;

			if(!westBorder && !eastBorder && (west.equalsPawn(me) && east.equalsPawn(me)))
				return true;


			// Vicinanza trono

			if((!northBorder && !southBorder && pawn[0] - 1 == 4 && pawn[1] == 4  && south.equalsPawn(me))
					|| (!northBorder && !southBorder && pawn[0] + 1 == 4 && pawn[1] == 4  && north.equalsPawn(me))
					|| (!westBorder && !eastBorder && pawn[0] == 4 && pawn[1] - 1 == 4  && east.equalsPawn(me))
					|| (!westBorder && !eastBorder && pawn[0] == 4 && pawn[1] + 1 == 4  && west.equalsPawn(me)))
				return true;

			// Vicinanza cittadella

			if((!southBorder && onCitadelsBorder(new int[]{pawn[0] - 1, pawn[1]}) && south.equalsPawn(me))
					|| (!northBorder && onCitadelsBorder(new int[]{pawn[0] + 1, pawn[1]}) && north.equalsPawn(me))
					|| (!eastBorder && onCitadelsBorder(new int[]{pawn[0], pawn[1] - 1}) && east.equalsPawn(me))
					|| (!westBorder && onCitadelsBorder(new int[]{pawn[0], pawn[1] + 1}) && west.equalsPawn(me)))
				return true;



		}

		return false;
	}



}