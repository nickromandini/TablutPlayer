package it.unibo.ai.didattica.competition.tablut.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	public void move(Action action) {
		this.board[action.getRowFrom()][action.getColumnFrom()] = Pawn.EMPTY;
		if (this.turn.equalsTurn("WHITE")) {
			this.board[action.getRowTo()][action.getColumnTo()] = Pawn.WHITE;
			this.setTurn(turn.BLACK);
		}
		else {
			this.board[action.getRowTo()][action.getColumnTo()] = Pawn.BLACK;
			this.setTurn(turn.WHITE);
		}
	}

	/*
	 * Verifica che lo stato sia terminale
	 */
	public void isTerminal() {
		//Vince white se il re si trova su un punto di fuga

		//Vince black se il re viene mangiato

		//Pareggio ??

	}


	public List<Action> getAllLegalMoves() {
		List<Action> actions = new ArrayList<Action>();

		for(int[] coordPawn : this.getPawnsCoord()) {
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "NORTH"));
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "SOUTH"));
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "WEST"));
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "EAST"));
		}

		return actions;
	}


	private List<Action> getAllLegalMovesInDirection(int[] coordPawn, String direction) {

		List<Action> actions = new ArrayList<Action>();
		int x = coordPawn[0];
		int y = coordPawn[1];

		switch (direction){
			case "EAST":
				for(int i = y + 1; i < 9; i++) {
					if(this.getPawn(x,i).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{x,i})) {
						try {
							actions.add(new Action(this.getBox(x, y), this.getBox(x, i), this.turn));
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
					if(this.getPawn(x,i).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{x,i})) {
						try {
							actions.add(new Action(this.getBox(x, y), this.getBox(x, i), this.turn));
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
					if(this.getPawn(i,y).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{i,y})) {
						try {
							actions.add(new Action(this.getBox(x, y), this.getBox(i, y), this.turn));
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
					if(this.getPawn(i, y).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{i,y})) {
						try {
							actions.add(new Action(this.getBox(x, y), this.getBox(i, y), this.turn));
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else {
						break;
					}
				}
				break;

		}


		return actions;
	}

	public int[] getKingCoord() {

		if(kingCoord[0] == -1) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
						kingCoord[0] = i;
						kingCoord[1] = j;
						break;
					}
				}
			}
		}
		return kingCoord;
	}

	public List<int[]> getPawnsCoord() {
		List<int[]> pawns = new ArrayList<>();
		int[] buf;
		if (this.getTurn().equals(StateTablut.Turn.WHITE)) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
							|| this.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
						buf = new int[2];
						buf[0] = i;
						buf[1] = j;
						if(this.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
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
					} else if(this.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
						kingCoord[0] = i;
						kingCoord[1] = j;
					}
				}
			}
		}

		return pawns;
	}
	public boolean kingCanEscape(String direction) {
		return kingCanEscape(kingCoord, direction);
	}

	public boolean kingCanEscape(int[] coord, String direction) {

		int x = coord[0];
		int y = coord[1];

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

	public boolean onCitadels(int[] coord) {
		List<int[]> citadels = Stream.of(new int[]{3,0}, new int[]{4,0}, new int[]{5,0}, new int[]{4,1},
				new int[]{7,3}, new int[]{7,4}, new int[]{7,5}, new int[]{6,4},
				new int[]{3,7}, new int[]{4,7}, new int[]{5,7}, new int[]{4,6},
				new int[]{0,3}, new int[]{0,4}, new int[]{0,5}, new int[]{1,4}).collect(Collectors.toList());
		return citadels.parallelStream().anyMatch(a -> Arrays.equals(a, coord));
	}

	public boolean kingOnEscapePoint() {
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

}