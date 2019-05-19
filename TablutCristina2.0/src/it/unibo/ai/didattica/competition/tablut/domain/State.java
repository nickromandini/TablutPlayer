package it.unibo.ai.didattica.competition.tablut.domain;

import it.unibo.ai.didattica.competition.tablut.util.Evaluation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class State {

	private int[] kingCoord = new int[]{-1,-1};
	private int[][] citadels = {new int[]{3,0}, new int[]{4,0}, new int[]{5,0}, new int[]{4,1},
			new int[]{8,3}, new int[]{8,4}, new int[]{8,5}, new int[]{7,4},
			new int[]{3,8}, new int[]{4,8}, new int[]{5,8}, new int[]{4,7},
			new int[]{0,3}, new int[]{0,4}, new int[]{0,5}, new int[]{1,4}};
	private int[][] citadelsBorders = {new int[]{3,0}, new int[]{5,0}, new int[]{4,1},
			new int[]{8,3}, new int[]{8,5}, new int[]{7,4},
			new int[]{3,8}, new int[]{5,8}, new int[]{4,7},
			new int[]{0,3}, new int[]{0,5}, new int[]{1,4}};
	private int[][] escapePoints = {new int[]{1,0}, new int[]{2,0},
			new int[]{6,0}, new int[]{7,0},
			new int[]{0,1}, new int[]{0,2},
			new int[]{0,6}, new int[]{0,7},
			new int[]{1,8}, new int[]{2,8},
			new int[]{6,8}, new int[]{7,8},
			new int[]{8,7}, new int[]{8,6},
			new int[]{8,2}, new int[]{8,1}};
	public enum Turn {
		WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
		private final String turn;

		Turn(String s) {
			turn = s;
		}

		public boolean equalsTurn(String otherName) {
			return (otherName != null) && turn.equals(otherName);
		}

		public String toString() {
			return turn;
		}
	}
	public enum Pawn {
		EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
		private final String pawn;

		Pawn(String s) {
			pawn = s;
		}

		public boolean equalsPawn(String otherPawn) {
			return (otherPawn != null) && pawn.equals(otherPawn);
		}

		public String toString() {
			return pawn;
		}

	}

	protected Pawn[][] board;
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
		result.append(this.boardString().replace("\n", ""));
		//result.append(this.turn.toString());

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
		return this.turn == other.turn;
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
			result = cons.newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}

		Pawn[][] oldboard = this.getBoard();
		Pawn[][] newboard = result.getBoard();

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
	public void move(Action a) {
		Pawn pawn = getPawn(a.getRowFrom(), a.getColumnFrom());
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			board[a.getRowFrom()][a.getColumnFrom()] = Pawn.THRONE;
		} else {
			board[a.getRowFrom()][a.getColumnFrom()] = Pawn.EMPTY;
		}
		board[a.getRowTo()][a.getColumnTo()] = pawn;

		if(getTurn().equalsTurn(Turn.WHITE.toString()))
			checkCaptureWhite(a);
		if(getTurn().equalsTurn(Turn.BLACK.toString()))
			checkCaptureBlack(a);

		if (getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			setTurn(Turn.BLACK);
		} else {
			setTurn(Turn.WHITE);
		}

	}

	private void checkCaptureWhite(Action a) {
		if (a.getColumnTo() < this.getBoard().length - 2
				&& this.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
				&& (this.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K"))) {
			this.removePawn(a.getRowTo(), a.getColumnTo() + 1);
		}
		if (a.getColumnTo() > 1 && this.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
				&& (this.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K"))) {
			this.removePawn(a.getRowTo(), a.getColumnTo() - 1);
		}
		if (a.getRowTo() > 1 && this.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
				&& (this.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
				|| this.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
				|| this.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K"))) {
			this.removePawn(a.getRowTo() - 1, a.getColumnTo());
		}
		if (a.getRowTo() < this.getBoard().length - 2
				&& this.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
				&& (this.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
				|| this.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
				|| this.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K"))) {
			this.removePawn(a.getRowTo() + 1, a.getColumnTo());
		}
	}


	private void checkCaptureBlack(Action a) {
		if (a.getColumnTo() < this.getBoard().length - 2
				&& (this.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K"))
				&& (this.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T"))) {
			if (this.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
				this.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			}
		}
		if (a.getColumnTo() > 1
				&& (this.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K"))
				&& (this.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
				|| this.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T"))) {
			if (this.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")) {
				this.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			}
		}
		if (a.getRowTo() > 1
				&& (this.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				|| this.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K"))
				&& (this.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
				|| this.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T"))) {
			if (this.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")) {
				this.removePawn(a.getRowTo() - 1, a.getColumnTo());
			}
		}
		if (a.getRowTo() < this.getBoard().length - 2
				&& (this.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				|| this.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K"))
				&& (this.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
				|| this.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T"))) {
			if (this.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")) {
				this.removePawn(a.getRowTo() + 1, a.getColumnTo());
			}
		}
		if (this.getBoard().length == 9) {
			if (a.getColumnTo() == 4 && a.getRowTo() == 2) {
				if (this.getPawn(3, 4).equalsPawn("W") && this.getPawn(4, 4).equalsPawn("K")
						&& this.getPawn(4, 3).equalsPawn("B") && this.getPawn(4, 5).equalsPawn("B")
						&& this.getPawn(5, 4).equalsPawn("B")) {
					this.removePawn(3, 4);
				}
			}
			if (a.getColumnTo() == 4 && a.getRowTo() == 6) {
				if (this.getPawn(5, 4).equalsPawn("W") && this.getPawn(4, 4).equalsPawn("K")
						&& this.getPawn(4, 3).equalsPawn("B") && this.getPawn(4, 5).equalsPawn("B")
						&& this.getPawn(3, 4).equalsPawn("B")) {
					this.removePawn(5, 4);
				}
			}
			if (a.getColumnTo() == 2 && a.getRowTo() == 4) {
				if (this.getPawn(4, 3).equalsPawn("W") && this.getPawn(4, 4).equalsPawn("K")
						&& this.getPawn(3, 4).equalsPawn("B") && this.getPawn(5, 4).equalsPawn("B")
						&& this.getPawn(4, 5).equalsPawn("B")) {
					this.removePawn(4, 3);
				}
			}
			if (a.getColumnTo() == 6 && a.getRowTo() == 4) {
				if (this.getPawn(4, 5).equalsPawn("W") && this.getPawn(4, 4).equalsPawn("K")
						&& this.getPawn(4, 3).equalsPawn("B") && this.getPawn(5, 4).equalsPawn("B")
						&& this.getPawn(3, 4).equalsPawn("B")) {
					this.removePawn(4, 5);
				}
			}
		}
	}


	public int[][] getEscapePoints() {
		return escapePoints;
	}


	/*
	 * Verifica che lo stato sia terminale
	 */
	public boolean isTerminalWhite() {
		return kingOnEscapePoint();
	}

	public boolean isTerminalBlack() { return kingEaten(); }


	public List<Action> getAllLegalMoves(boolean strict) {
		List<Action> actions = new ArrayList<>();


		for(int[] coordPawn : this.getPawnsCoord(getTurn().toString())) {
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "NORTH", strict));
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "SOUTH", strict));
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "WEST", strict));
			actions.addAll(getAllLegalMovesInDirection(coordPawn, "EAST", strict));
		}
		Collections.sort(actions);
		return actions;
	}


	private List<Action> getAllLegalMovesInDirection(int[] coordPawn, String direction, boolean strict) {

		List<Action> actionsEvaluated = new ArrayList<>();

		int x = coordPawn[0];
		int y = coordPawn[1];
		int value;

		switch (direction){
			case "EAST":
				for(int i = y + 1; i < 9; i++) {
					if(this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(x,i)) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(x,y) && this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString())){
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
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
					if(this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(x,i)) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(x,y) && this.getPawn(x,i).equalsPawn(Pawn.EMPTY.toString())) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(x, i), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
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
					if(this.getPawn(i,y).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(i,y)) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(x,y) && this.getPawn(i,y).equalsPawn(Pawn.EMPTY.toString())){
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
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
					if(this.getPawn(i, y).equalsPawn(Pawn.EMPTY.toString()) && !onCitadels(i,y)) {
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
						} catch( Exception e) {
							e.printStackTrace();
						}
					} else if(turn.equalsTurn(Turn.BLACK.toString()) && onCitadels(x,y) && this.getPawn(i, y).equalsPawn(Pawn.EMPTY.toString())){
						try {
							Action newAction = new Action(this.getBox(x, y), this.getBox(i, y), this.turn);
							value = Evaluation.evaluateAction(newAction, this, direction);
							if(strict) {
								if (value >= 0) {
									newAction.setValue(value);
									actionsEvaluated.add(newAction);
								}
							} else {
								newAction.setValue(value);
								actionsEvaluated.add(newAction);
							}
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


	public List<int[]> getPawnsCoord(String me) {
		List<int[]> pawns = new ArrayList<>();
		int[] buf;
		if (me.equals("W")) {
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
		} else if(me.equals("B")){
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
		} else
			throw new InputMismatchException();

		return pawns;
	}

	// result[0] = white pawns
	// result[1] = black pawns
	public int[] numOfMyAndEnemyPawns() {
		int[] result = new int[2];
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (this.getPawn(i, j).equalsPawn(Pawn.WHITE.toString())
						|| this.getPawn(i, j).equalsPawn(Pawn.KING.toString())) {
					result[0]++;
				} else if(this.getPawn(i,j).equalsPawn(Pawn.BLACK.toString())){
					result[1]++;
				}
			}
		}

		return result;
	}

	public int numOfPawns(String player) {
		int result = 0;
		if (player.equals("W")) {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(Pawn.WHITE.toString())
							|| this.getPawn(i, j).equalsPawn(Pawn.KING.toString())) {
						result++;
					}
				}
			}
		} else {
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board.length; j++) {
					if (this.getPawn(i, j).equalsPawn(Pawn.BLACK.toString()))
						result++;
				}
			}
		}

		return result;
	}




	public boolean kingCanEscapeInTwoMoves() {
		if(kingCoord[0] == -1)
			kingCoord = getKingCoord();

		return checkNorth(kingCoord[0], kingCoord[1]) || checkSouth(kingCoord[0], kingCoord[1]) || checkWest(kingCoord[0], kingCoord[1]) || checkEast(kingCoord[0], kingCoord[1]);

	}

    private boolean checkNorth(int x, int y) {
        for(int i = x - 1; i >= 1; i--) {
            if(!getPawn(i,y).equalsPawn("O") || onCitadels(i,y))
                return false;
            if(kingCanEscape(i,y, "EAST") || kingCanEscape(i,y, "WEST"))
                return true;
        }
        return false;
    }

    private boolean checkSouth(int x, int y) {
        for(int i = x + 1; i <= 7; i++) {
            if(!getPawn(i,y).equalsPawn("O") || onCitadels(i,y))
                return false;
            if(kingCanEscape(i,y, "EAST") || kingCanEscape(i,y, "WEST"))
                return true;
        }
        return false;
    }

    private boolean checkWest(int x, int y) {
        for(int i = y - 1; i >= 1; i--) {
            if(!getPawn(x,i).equalsPawn("O") || onCitadels(x,i))
                return false;
            if(kingCanEscape(x, i,"NORTH") || kingCanEscape(x, i, "SOUTH"))
                return true;
        }
        return false;
    }

    private boolean checkEast(int x, int y) {
        for(int i = y + 1; i <= 7; i++) {
            if(!getPawn(x,i).equalsPawn("O")  || onCitadels(x,i))
                return false;
            if(kingCanEscape(x, i,"NORTH") || kingCanEscape(x, i,"SOUTH"))
                return true;
        }
        return false;
    }

    public boolean kingCanEscapeInTwoMovesTerminal() {
        if(kingCoord[0] == -1)
            kingCoord = getKingCoord();

        return checkNorthStrict(kingCoord[0], kingCoord[1]) || checkSouthStrict(kingCoord[0], kingCoord[1]) || checkWestStrict(kingCoord[0], kingCoord[1]) || checkEastStrict(kingCoord[0], kingCoord[1]);

    }


    private boolean checkNorthStrict(int x, int y) {
        for(int i = x - 1; i >= 2; i--) {
            if(!getPawn(i,y).equalsPawn("O") || onCitadels(i,y))
                return false;
            if(kingCanEscape(i,y, "EAST") && kingCanEscape(i,y, "WEST"))
                return true;
        }
        return false;
    }

    private boolean checkSouthStrict(int x, int y) {
        for(int i = x + 1; i <= 6; i++) {
            if(!getPawn(i,y).equalsPawn("O") || onCitadels(i,y))
                return false;
            if(kingCanEscape(i,y, "EAST") && kingCanEscape(i,y, "WEST"))
                return true;
        }
        return false;
    }

    private boolean checkWestStrict(int x, int y) {
        for(int i = y - 1; i >= 2; i--) {
            if(!getPawn(x,i).equalsPawn("O") || onCitadels(x,i))
                return false;
            if(kingCanEscape(x, i,"NORTH") && kingCanEscape(x, i, "SOUTH"))
                return true;
        }
        return false;
    }

    private boolean checkEastStrict(int x, int y) {
        for(int i = y + 1; i <= 6; i++) {
            if(!getPawn(x,i).equalsPawn("O") || onCitadels(x,i))
                return false;
            if(kingCanEscape(x, i,"NORTH") && kingCanEscape(x, i,"SOUTH"))
                return true;
        }
        return false;
    }


	public boolean kingCanEscape(String direction) {
		if(kingCoord[0] == -1)
			getKingCoord();
		return kingCanEscape(kingCoord[0], kingCoord[1], direction);
	}

	public boolean kingCanEscape(int x, int y, String direction) {
		switch (direction) {
			case "NORTH":
				if(onCitadels(0,y)) {
					return false;
				}
				for(int i = x - 1; i >= 0; i--) {
					if (!this.getPawn(i,y).equalsPawn("O") || onCitadels(i,y)) {
						return false;
					}
				}
				break;
			case "SOUTH":
				if(onCitadels(8,y)) {
					return false;
				}
				for(int i = x + 1; i <= 8; i++) {
					if (!this.getPawn(i,y).equalsPawn("O") || onCitadels(i,y)) {
						return false;
					}
				}
				break;
			case "WEST":
				if(onCitadels(x,0)) {
					return false;
				}
				for(int i = y - 1; i >= 0; i--) {
					if (!this.getPawn(x,i).equalsPawn("O") || onCitadels(x,i)) {
						return false;
					}
				}
				break;
			case "EAST":
				if(onCitadels(x,8)) {
					return false;
				}
				for(int i = y + 1; i <= 8; i++) {
					if (!this.getPawn(x,i).equalsPawn("O") || onCitadels(x,i)) {
						return false;
					}
				}
				break;
		}



		return true;
	}

	public boolean onCitadelsBorder(int x, int y) {
		for(int[] border : citadelsBorders) {
			if(border[0] == x  && border[1] == y) {
				return true;
			}
		}
		return false;
	}

	public boolean onCitadels(int x, int y) {
		for(int[] citadel : citadels) {
			if(citadel[0] == x  && citadel[1] == y) {
				return true;
			}
		}
		return false;
	}

	public int pawnsOnEscapePoint() {
		int result = 0;
		for(int[] escapePoint : escapePoints)
			if(!getPawn(escapePoint[0], escapePoint[1]).equalsPawn("O"))
				result++;
		return result;
	}

	public boolean kingOnEscapePoint() {
		if(kingCoord[0]==-1)
			kingCoord = getKingCoord();
		return kingOnEscapePoint(kingCoord[0], kingCoord[1]);
	}

	public boolean kingOnEscapePoint(int x, int y) {
		for(int[] point : escapePoints) {
			if(point[0] == x && point[1] == y)
				return true;
		}
		return false;
	}



	public boolean kingEaten() {

		if(kingCoord[0] == -1) {
			kingCoord = getKingCoord();
		}

		Pawn north = this.getPawn(kingCoord[0] - 1, kingCoord[1]);
		Pawn south = this.getPawn(kingCoord[0] + 1, kingCoord[1]);
		Pawn west = this.getPawn(kingCoord[0], kingCoord[1] - 1);
		Pawn east = this.getPawn(kingCoord[0], kingCoord[1] + 1);



		// Re sul trono

		if(kingCoord[0] == 4 && kingCoord[1] == 4) {
			return north.equalsPawn("B") && south.equalsPawn("B") && west.equalsPawn("B")
					&& east.equalsPawn("B");

		}// Re vicino al trono
		else if(kingCoord[0] == 3 && kingCoord[1] == 4) {
			return north.equalsPawn("B") && west.equalsPawn("B")
					&& east.equalsPawn("B");
		} else if(kingCoord[0] == 4 && kingCoord[1] == 3) {
			return north.equalsPawn("B") && west.equalsPawn("B")
					&& south.equalsPawn("B");
		} else if(kingCoord[0] == 4 && kingCoord[1] == 5) {
			return north.equalsPawn("B") && east.equalsPawn("B")
					&& south.equalsPawn("B");
		} else if(kingCoord[0] == 5 && kingCoord[1] == 4) {
			return south.equalsPawn("B") && west.equalsPawn("B")
					&& east.equalsPawn("B");
		} // campo aperto
		else return ((north.equalsPawn("B") || onCitadels(kingCoord[0] - 1, kingCoord[1])) && south.equalsPawn("B"))
					|| ((south.equalsPawn("B") || onCitadels(kingCoord[0] + 1, kingCoord[1])) && north.equalsPawn("B"))
					|| ((west.equalsPawn("B") || onCitadels(kingCoord[0], kingCoord[1] - 1)) && east.equalsPawn("B"))
					|| ((east.equalsPawn("B") || onCitadels(kingCoord[0], kingCoord[1] + 1)) && west.equalsPawn("B"));

	}

	public int numBlackBetweenKingAndEscape() {
		if(kingCoord[0] == -1) {
			kingCoord = getKingCoord();
		}
		int result = 0;
		for (int[] pCoord : getPawnsCoord("B"))
			for(int[] eCoord : escapePoints)
				if (kingCoord[0] == eCoord[0] && eCoord[0] == pCoord[0]) {
					if (kingCoord[1] > pCoord[1] && pCoord[1] >= eCoord[1] && isCleanHorizontal(kingCoord[0], eCoord[1],pCoord[1]) && isCleanHorizontal(kingCoord[0],pCoord[1],kingCoord[1]))
						result++;
					else if(kingCoord[1] < pCoord[1] && pCoord[1] <= eCoord[1] && isCleanHorizontal(kingCoord[0], kingCoord[1], pCoord[1]) && isCleanHorizontal(kingCoord[0],pCoord[1],eCoord[1]))
						result++;
				}
				else if (kingCoord[1] == eCoord[1] && eCoord[1] == pCoord[1]) {
					if (kingCoord[0] > pCoord[0] && pCoord[0] >= eCoord[0] && isCleanVertical(kingCoord[1],eCoord[0],pCoord[0]) && isCleanVertical(kingCoord[1],pCoord[0],kingCoord[0]))
						result++;
					else if(kingCoord[0] < pCoord[0] && pCoord[0] <= eCoord[0] && isCleanVertical(kingCoord[1], kingCoord[0], pCoord[0]) && isCleanVertical(kingCoord[1],pCoord[0],eCoord[0]))
						result++;
				}

		return result;
	}

	private boolean isCleanHorizontal(int vertical, int start, int end) {
		if (start == end)
			return true;
		for (int i=start+1; i<end; i++) {
			if(!getPawn(vertical, i).equalsPawn("O"))
				return false;
		}
		return true;
	}

	private boolean isCleanVertical(int horizontal, int start, int end) {
		if (start == end)
			return true;
		for (int i=start+1; i<end; i++) {
			if(!getPawn(i, horizontal).equalsPawn("O"))
				return false;
		}
		return true;
	}


	public boolean kingCanBeEaten() {
		if(kingCoord[0] == -1) {
			kingCoord = getKingCoord();
		}



		Pawn north = this.getPawn(kingCoord[0] - 1, kingCoord[1]);
		Pawn south = this.getPawn(kingCoord[0] + 1, kingCoord[1]);
		Pawn west = this.getPawn(kingCoord[0], kingCoord[1] - 1);
		Pawn east = this.getPawn(kingCoord[0], kingCoord[1] + 1);



		// Re sul trono

		if(kingCoord[0] == 4 && kingCoord[1] == 4) {
			if(north.equalsPawn("B") && south.equalsPawn("B") && west.equalsPawn("B")
				&& anEnemyCanArriveEast(kingCoord, "B"))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "B"))
				return true;
			else if(north.equalsPawn("B") && west.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "B"))
				return true;
			else return south.equalsPawn("B") && west.equalsPawn("B") && east.equalsPawn("B")
						&& anEnemyCanArriveNorth(kingCoord, "B");

		}// Re vicino al trono
		else if(kingCoord[0] == 3 && kingCoord[1] == 4) {
			if(north.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveEast(kingCoord, "B"))
				return true;
			else if(north.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "B"))
				return true;
			else return east.equalsPawn("B") && west.equalsPawn("B")
						&& anEnemyCanArriveNorth(kingCoord, "B");
		} else if(kingCoord[0] == 4 && kingCoord[1] == 3) {
			if(north.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "B"))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "B"))
				return true;
			else return south.equalsPawn("B") && west.equalsPawn("B")
						&& anEnemyCanArriveNorth(kingCoord, "B");
		} else if(kingCoord[0] == 4 && kingCoord[1] == 5) {
			if(north.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveSouth(kingCoord, "B"))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B")
					&& anEnemyCanArriveEast(kingCoord, "B"))
				return true;
			else return south.equalsPawn("B") && east.equalsPawn("B")
						&& anEnemyCanArriveNorth(kingCoord, "B");
		} else if(kingCoord[0] == 5 && kingCoord[1] == 4) {
			if(south.equalsPawn("B") && west.equalsPawn("B")
					&& anEnemyCanArriveEast(kingCoord, "B"))
				return true;
			else if(south.equalsPawn("B") && east.equalsPawn("B")
					&& anEnemyCanArriveWest(kingCoord, "B"))
				return true;
			else return east.equalsPawn("B") && west.equalsPawn("B")
						&& anEnemyCanArriveSouth(kingCoord, "B");
		} // campo aperto
		else
			return ((north.equalsPawn("B") || onCitadels(kingCoord[0] - 1, kingCoord[1])) && anEnemyCanArriveSouth(kingCoord, "B"))
					|| ((south.equalsPawn("B") || onCitadels(kingCoord[0] + 1, kingCoord[1])) && anEnemyCanArriveNorth(kingCoord, "B"))
					|| ((west.equalsPawn("B") || onCitadels(kingCoord[0], kingCoord[1] - 1)) && anEnemyCanArriveEast(kingCoord, "B"))
					|| ((east.equalsPawn("B") || onCitadels(kingCoord[0], kingCoord[1] + 1)) && anEnemyCanArriveWest(kingCoord, "B"));


	}

	private boolean anEnemyCanArriveNorth(int[] coord, String enemy) {
		return (checkNorthEnemyMovement(coord[0] - 1, coord[1], enemy) || checkWestEnemyMovement(coord[0] - 1, coord[1], enemy) || checkEastEnemyMovement(coord[0] - 1, coord[1], enemy));
	}

	private boolean anEnemyCanArriveSouth(int[] coord, String enemy) {
		return (checkSouthEnemyMovement(coord[0] + 1, coord[1], enemy) || checkWestEnemyMovement(coord[0] + 1, coord[1], enemy) || checkEastEnemyMovement(coord[0] + 1, coord[1], enemy));
	}

	private boolean anEnemyCanArriveWest(int[] coord, String enemy) {
		return (checkNorthEnemyMovement(coord[0], coord[1] - 1, enemy) || checkSouthEnemyMovement(coord[0], coord[1] - 1, enemy) || checkWestEnemyMovement(coord[0], coord[1] - 1, enemy));
	}

	private boolean anEnemyCanArriveEast(int[] coord, String enemy){
		return (checkNorthEnemyMovement(coord[0], coord[1] + 1, enemy) || checkSouthEnemyMovement(coord[0], coord[1] + 1, enemy) || checkEastEnemyMovement(coord[0], coord[1] + 1, enemy));
	}


	private boolean checkNorthEnemyMovement(int x, int y, String enemy) {
		for(int i = x; i>=0; i--) {
			Pawn up = this.getPawn(i,y);
			if(!up.equalsPawn("O"))
				if(enemy.equals("B") && up.equalsPawn("B"))
					return true;
				else return enemy.equals("W") && (up.equalsPawn("W") || up.equalsPawn("K"));

		}

		return false;

	}

	private boolean checkSouthEnemyMovement(int x, int y, String enemy) {
		for(int i = x; i<9; i++) {
			Pawn down = this.getPawn(i,y);
			if(!down.equalsPawn("O"))
				if(enemy.equals("B") && down.equalsPawn("B"))
					return true;
				else return enemy.equals("W") && (down.equalsPawn("W") || down.equalsPawn("K"));

		}

		return false;

	}

	private boolean checkWestEnemyMovement(int x, int y, String enemy) {
		for(int i = y; i>=0; i--) {
			Pawn left = this.getPawn(x,i);
			if(!left.equalsPawn("O"))
				if(enemy.equals("B") && left.equalsPawn("B"))
					return true;
				else return enemy.equals("W") && (left.equalsPawn("W") || left.equalsPawn("K"));

		}

		return false;
	}

	private boolean checkEastEnemyMovement(int x, int y, String enemy) {
		for(int i = y; i<9; i++) {
			Pawn right = this.getPawn(x,i);
			if(!right.equalsPawn("O"))
				if(enemy.equals("B") && right.equalsPawn("B"))
					return true;
				else return enemy.equals("W") && (right.equalsPawn("W") || right.equalsPawn("K"));

		}

		return false;
	}

    public int enemiesNearKingCardinal() {

        int enemies = 0;

        if(kingCoord[0] == -1) {
            kingCoord = getKingCoord();
        }

        //Pedina nera sopra il re
        if (kingCoord[0]-1 >= 0 && this.getPawn(kingCoord[0]-1, kingCoord[1]).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        //Pedina nera sotto il re
        if (kingCoord[0]+1 <= 8 && this.getPawn(kingCoord[0]+1, kingCoord[1]).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        //Pedina nera a sinistra del re
        if (kingCoord[1]-1 >= 0 && this.getPawn(kingCoord[0], kingCoord[1]-1).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        //Pedina nera a destra del re
        if (kingCoord[1]+1 <= 8 && this.getPawn(kingCoord[0], kingCoord[1]+1).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        return enemies;

    }



    public int enemiesNearKingDiagonal() {

        int enemies = 0;

        if(kingCoord[0] == -1) {
            kingCoord = getKingCoord();
        }

        //In alto a sinistra
        if (kingCoord[0]-1 >= 0 && kingCoord[1]-1 >= 0 && this.getPawn(kingCoord[0]-1, kingCoord[1]-1).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        //In alto a destra
        if (kingCoord[0]-1 >= 0 && kingCoord[1]+1 <= 8 && this.getPawn(kingCoord[0]-1, kingCoord[1]+1).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        //In basso a sinistra
        if (kingCoord[0]+1 <= 8 && kingCoord[1]-1 >= 0 && this.getPawn(kingCoord[0]+1, kingCoord[1]-1).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        //In basso a destra
        if (kingCoord[0]+1 <= 8 && kingCoord[1]+1 <= 8 &&this.getPawn(kingCoord[0]+1, kingCoord[1]+1).equalsPawn(Pawn.BLACK.toString()))
            enemies++;

        return enemies;

    }


    public boolean enemyPawnCanBeEaten(String me) {

		String enemy = me.equals("W") ? "B" : "W";

		List<int[]> enemyPawns = getPawnsCoord(enemy);

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
			if(onCitadels(pawn[0], pawn[1]) && enemy.equals("B"))
				continue;

			if(pawn[0] - 1 >= 0)
				north = getPawn(pawn[0] - 1, pawn[1]);
			else
				northBorder = true;

			if(pawn[0] + 1 < 9)
				south = getPawn(pawn[0] + 1, pawn[1]);
			else
				southBorder = true;

			if(pawn[1] - 1 >= 0)
				west = getPawn(pawn[0], pawn[1] - 1);
			else
				westBorder = true;

			if(pawn[1] + 1 < 9)
				east = getPawn(pawn[0], pawn[1] + 1);
			else
				eastBorder = true;


			// Campo aperto
			if (!northBorder && !southBorder)
				if (north.equalsPawn(me) && anEnemyCanArriveSouth(pawn, enemy))
					return true;

			if (!northBorder && !southBorder)
				if (south.equalsPawn(me) && anEnemyCanArriveNorth(pawn, enemy))
					return true;

			if (!westBorder && !eastBorder)
				if (west.equalsPawn(me) && anEnemyCanArriveEast(pawn, enemy))
					return true;

			if (!westBorder && !eastBorder)
				if (east.equalsPawn(me) && anEnemyCanArriveWest(pawn, enemy))
					return true;


			// Vicinanza trono
			if (pawn[0] - 1 == 4 && pawn[1] == 4 && anEnemyCanArriveSouth(pawn, enemy))
				return true;
			if (pawn[0] + 1 == 4 && pawn[1] == 4 && anEnemyCanArriveNorth(pawn, enemy))
				return true;
			if (pawn[0] == 4 && pawn[1] - 1 == 4 && anEnemyCanArriveEast(pawn, enemy))
				return true;
			if (pawn[0] == 4 && pawn[1] + 1 == 4 && anEnemyCanArriveWest(pawn, enemy))
				return true;

			// Vicinanza cittadella
			if ((!southBorder && onCitadelsBorder(pawn[0] - 1, pawn[1]) && anEnemyCanArriveSouth(pawn, enemy))
					|| (!northBorder && onCitadelsBorder(pawn[0] + 1, pawn[1]) && anEnemyCanArriveNorth(pawn, enemy))
					|| (!eastBorder && onCitadelsBorder(pawn[0], pawn[1] - 1) && anEnemyCanArriveEast(pawn, enemy))
					|| (!westBorder && onCitadelsBorder(pawn[0], pawn[1] + 1) && anEnemyCanArriveWest(pawn, enemy)))
				return true;



		}

		return false;
	}

	public int differenceNumOfPawnsNormalized() {
		int numOfPawn[] = this.numOfMyAndEnemyPawns();
		return numOfPawn[0] - numOfPawn[1] + 7;
	}

}
