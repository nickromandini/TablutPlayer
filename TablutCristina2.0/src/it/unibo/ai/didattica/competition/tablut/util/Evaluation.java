package it.unibo.ai.didattica.competition.tablut.util;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Evaluation {



	public static int evaluateAction(Action a, State state, String direction) {

		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {

			int rowTo = a.getRowTo();
			int columnTo = a.getColumnTo();
			if (state.getPawn(a.getRowFrom(), a.getColumnFrom()).equalsPawn("K")) {

				if (state.kingOnEscapePoint(rowTo, columnTo))
					return 1000;

				State temp = resultState(state,a);
				if(temp.kingCanBeEaten()) {
					return -1000;
				}

				int escapePaths = 0;

				if(state.kingCanEscape(rowTo, columnTo, "NORTH"))
					escapePaths++;

				if(state.kingCanEscape(rowTo, columnTo, "SOUTH"))
					escapePaths++;

				if(state.kingCanEscape(rowTo, columnTo, "WEST"))
					escapePaths++;

				if(state.kingCanEscape(rowTo, columnTo, "EAST"))
					escapePaths++;

				if(escapePaths > 1)
					return 600;
				else if(escapePaths > 0)
					return 250;

				return 150;
			} else {
				State temp = resultState(state,a);
				if(temp.kingCanBeEaten()) {
					/*System.err.println("---------");
					System.err.println("non metto azione");
					System.err.println(temp.toString());
					System.err.println("---------");*/
					return -1000;
				}
				// Controllo se posso mangiare una pedina con la mossa da valutare
				if(blackPawnEatable(state, a, direction))
					if(state.differenceNumOfPawns("W") < -10)
						return 500;
					else
						return 100;

				return 0;
			}
		}
		else { // caso black

			if(kingEatableWithAction(state, a))
				return 1000;


			/*if(blockedKing(state, a))
				return 500;*/

			if (isKingMarked(state, a))
				return 500;

			if (checkCaptureBlackPawnLeft(state, a) || checkCaptureBlackPawnRight(state, a) || checkCaptureBlackPawnUp(state, a) || checkCaptureBlackPawnDown(state, a))
				return 100;

			return 0;

		}


	}

	private static boolean kingEatableWithAction(State state, Action a) {

		int[] kingCoord = state.getKingCoord();
		int destRow = a.getRowTo();
		int destColumn = a.getColumnTo();

		boolean northBorder = false;
		boolean southBorder = false;
		boolean westBorder = false;
		boolean eastBorder = false;

		State.Pawn north = null;
		State.Pawn south = null;
		State.Pawn west = null;
		State.Pawn east = null;


		if(kingCoord[0] - 1 >= 0)
			north = state.getPawn(kingCoord[0] - 1, kingCoord[1]);
		else
			northBorder = true;


		if(kingCoord[0] + 1 < 9)
			south = state.getPawn(kingCoord[0] + 1, kingCoord[1]);
		else
			southBorder = true;


		if(kingCoord[1] - 1 >= 0)
			west = state.getPawn(kingCoord[0], kingCoord[1] - 1);
		else
			westBorder = true;

		if(kingCoord[1] + 1 < 9)
			east = state.getPawn(kingCoord[0], kingCoord[1] + 1);
		else
			eastBorder = true;


		// Re sul trono

		if(kingCoord[0] == 4 && kingCoord[1] == 4) {
			if(north.equalsPawn("B") && south.equalsPawn("B") && west.equalsPawn("B")
					&& (destRow == 4 && destColumn == 5))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B") && east.equalsPawn("B")
					&& (destRow == 4 && destColumn == 3))
				return true;
			else if(north.equalsPawn("B") && west.equalsPawn("B") && east.equalsPawn("B")
					&& (destRow == 5 && destColumn == 4))
				return true;
			else if(south.equalsPawn("B") && west.equalsPawn("B") && east.equalsPawn("B")
					&& (destRow == 3 && destColumn == 4))
				return true;

		}

		// Re vicino al trono

		if(kingCoord[0] == 3 && kingCoord[1] == 4) {
			if(north.equalsPawn("B") && west.equalsPawn("B")
					&& (destRow == 3 && destColumn == 5))
				return true;
			else if(north.equalsPawn("B") && east.equalsPawn("B")
					&& (destRow == 3 && destColumn == 3))
				return true;
			else if(east.equalsPawn("B") && west.equalsPawn("B")
					&& (destRow == 2 && destColumn == 4))
				return true;
		} else if(kingCoord[0] == 4 && kingCoord[1] == 3) {
			if(north.equalsPawn("B") && west.equalsPawn("B")
					&& (destRow == 5 && destColumn == 3))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B")
					&& (destRow == 4 && destColumn == 2))
				return true;
			else if(south.equalsPawn("B") && west.equalsPawn("B")
					&& (destRow == 3 && destColumn == 3))
				return true;
		} else if(kingCoord[0] == 4 && kingCoord[1] == 5) {
			if(north.equalsPawn("B") && east.equalsPawn("B")
					&& (destRow == 5 && destColumn == 5))
				return true;
			else if(north.equalsPawn("B") && south.equalsPawn("B")
					&& (destRow == 4 && destColumn == 6))
				return true;
			else if(south.equalsPawn("B") && east.equalsPawn("B")
					&& (destRow == 3 && destColumn == 5))
				return true;
		} else if(kingCoord[0] == 5 && kingCoord[1] == 4) {
			if(south.equalsPawn("B") && west.equalsPawn("B")
					&& (destRow == 5 && destColumn == 5))
				return true;
			else if(east.equalsPawn("B") && south.equalsPawn("B")
					&& (destRow == 5 && destColumn == 3))
				return true;
			else if(east.equalsPawn("B") && west.equalsPawn("B")
					&& (destRow == 6 && destColumn == 4))
				return true;
		}

		// Campo aperto

		return ((north.equalsPawn("B") || state.onCitadels(kingCoord[0] - 1, kingCoord[1])) && (kingCoord[0] + 1 == destRow && kingCoord[1] == destColumn))
				|| ((south.equalsPawn("B") || state.onCitadels(kingCoord[0] + 1, kingCoord[1])) && (kingCoord[0] - 1 == destRow && kingCoord[1] == destColumn))
				|| ((west.equalsPawn("B") || state.onCitadels(kingCoord[0], kingCoord[1] - 1)) && (kingCoord[0] == destRow && kingCoord[1] + 1 == destColumn))
				|| ((east.equalsPawn("B") || state.onCitadels(kingCoord[0], kingCoord[1] + 1)) && (kingCoord[0] == destRow && kingCoord[1] - 1 == destColumn));


	}

	private static boolean isKingMarked(State state, Action a) {
		int[] kingCoord = state.getKingCoord();
		//indice 0 = row
		//indice 1 = col



		//Valuto se marco il re con la mossa attuale
		int destRow = a.getRowTo();
		int destCol = a.getColumnTo();
		for(int[] eCoord : state.getEscapePoints())
			if (kingCoord[0] == eCoord[0] && eCoord[0] == destRow) {
				if (kingCoord[1] > destCol && destCol >= eCoord[1] && isCleanHorizontal(state, kingCoord[0], eCoord[1],destCol) && isCleanHorizontal(state, kingCoord[0],destCol,kingCoord[1]))
					return true;
				else if(kingCoord[1] < destCol && destCol <= eCoord[1] && isCleanHorizontal(state, kingCoord[0], kingCoord[1], destCol) && isCleanHorizontal(state, kingCoord[0],destCol,eCoord[1]))
					return true;
			}
			else if (kingCoord[1] == eCoord[1] && eCoord[1] == destCol) {
				if (kingCoord[0] > destRow && destRow >= eCoord[0] && isCleanVertical(state, kingCoord[1],eCoord[0],destRow) && isCleanVertical(state, kingCoord[1],destRow,kingCoord[0]))
					return true;
				else if(kingCoord[0] < destRow && destRow <= eCoord[0] && isCleanVertical(state, kingCoord[1], kingCoord[0], destRow) && isCleanVertical(state, kingCoord[1],destRow,eCoord[0]))
					return true;
			}



		return false;
	}


	private static boolean isCleanHorizontal(State state, int vertical, int start, int end) {
		if (start == end)
			return true;

		for (int i=start+1; i<end; i++) {
			if(!state.getPawn(vertical, i).equalsPawn("O"))
				return false;
		}
		return true;
	}

	private static boolean isCleanVertical(State state, int horizontal, int start, int end) {
		if (start == end)
			return true;

		for (int i=start+1; i<end; i++) {
			if(!state.getPawn(i, horizontal).equalsPawn("O"))
				return false;
		}
		return true;
	}

	private static boolean blockedKing(State state, Action a) {
		int destRow = a.getRowTo();
		int destColumn = a.getColumnTo();
		int startRow = a.getRowFrom();
		int startColumn = a.getColumnFrom();
		int[] kingCoord = state.getKingCoord();

		if(state.kingCanEscape("NORTH") && kingCoord[0] > destRow && (kingCoord[1] == destColumn || kingCoord[1] == startColumn) )
			return true;
		else if(state.kingCanEscape("SOUTH") && kingCoord[0] < destRow && (kingCoord[1] == destColumn || kingCoord[1] == startColumn))
			return true;
		else if(state.kingCanEscape("WEST") && kingCoord[1] > destColumn && (kingCoord[0] == destRow || kingCoord[0] == startRow))
			return true;
		else
			return state.kingCanEscape("EAST") && kingCoord[1] < destColumn && (kingCoord[0] == destRow || kingCoord[0] == startRow);

	}


	private static boolean blackPawnEatable(State state, Action a, String direction) {

		boolean northBorder = false;
		boolean southBorder = false;
		boolean westBorder = false;
		boolean eastBorder = false;
		State.Pawn north = null;
		State.Pawn south = null;
		State.Pawn west = null;
		State.Pawn east = null;
		int rowTo = a.getRowTo();
		int columnTo = a.getColumnTo();

		if(rowTo - 1 >= 0)
			north = state.getPawn(rowTo - 1, columnTo);
		else
			northBorder = true;

		if(rowTo + 1 < 9)
			south = state.getPawn(rowTo + 1, columnTo);
		else
			southBorder = true;

		if(columnTo - 1 >= 0)
			west = state.getPawn(rowTo, columnTo - 1);
		else
			westBorder = true;

		if(columnTo + 1 < 9)
			east = state.getPawn(rowTo, columnTo + 1);
		else
			eastBorder = true;

		switch (direction) {
			case "NORTH":
				if (!northBorder && checkNorth(north, state, rowTo, columnTo)) {
					return true;
				}
				else if (!westBorder && checkWest(west, state, rowTo, columnTo)) {
					return true;
				}
				else if (!eastBorder && checkEast(east, state, rowTo, columnTo)) {
					return true;
				}
			case "SOUTH":
				if (!southBorder && checkSouth(south, state, rowTo, columnTo)) {
					return true;
				}
				else if (!westBorder && checkWest(west, state, rowTo, columnTo)){
					return true;
				}
				else if (!eastBorder && checkEast(east, state, rowTo, columnTo)) {
					return true;
				}
			case "WEST":
				if (!northBorder && checkNorth(north, state, rowTo, columnTo)) {
					return true;
				}
				else if (!southBorder && checkSouth(south, state, rowTo, columnTo)) {
					return true;
				}
				else if (!eastBorder && checkEast(east, state, rowTo, columnTo)) {
					return true;
				}
			case "EAST":
				if (!northBorder && checkNorth(north, state, rowTo, columnTo)) {
					return true;
				} else if (!southBorder && checkSouth(south, state, rowTo, columnTo)) {
					return true;
				} else if (!westBorder && checkWest(west, state, rowTo, columnTo)) {
					return true;
				}
		}
		return false;
	}

	private static boolean checkNorth(State.Pawn north, State state, int rowTo, int columnTo) {

		boolean onBorderWhite = false;
		if(rowTo - 2 >= 0) {
			State.Pawn p = state.getPawn(rowTo - 2, columnTo);
			onBorderWhite = p.equalsPawn("W") || p.equalsPawn("K");
		}
		if (north.equalsPawn("B") && !state.onCitadels(rowTo - 1, columnTo)) {
			return (onBorderWhite || state.onCitadelsBorder(rowTo - 2, columnTo));
		}
		return false;
	}

	private static boolean checkSouth(State.Pawn south, State state, int rowTo, int columnTo) {
		boolean onBorderWhite = false;
		if(rowTo + 2 < 9) {
			State.Pawn p = state.getPawn(rowTo + 2, columnTo);
			onBorderWhite = p.equalsPawn("W") || p.equalsPawn("K");
		}
		if(south.equalsPawn("B") && !state.onCitadels(rowTo + 1, columnTo)) {
			return (onBorderWhite || state.onCitadelsBorder(rowTo + 2, columnTo));
		}
		return false;
	}

	private static boolean checkWest(State.Pawn west, State state, int rowTo, int columnTo) {
		boolean onBorderWhite = false;
		if(columnTo - 2 >= 0) {
			State.Pawn p = state.getPawn(rowTo, columnTo - 2);
			onBorderWhite = p.equalsPawn("W") || p.equalsPawn("K");
		}
		if (west.equalsPawn("B") && !state.onCitadels(rowTo, columnTo - 1)) {
			return (onBorderWhite || state.onCitadelsBorder(rowTo, columnTo - 2));
		}
		return false;
	}

	private static boolean checkEast(State.Pawn east, State state, int rowTo, int columnTo) {
		boolean onBorderWhite = false;
		if(columnTo + 2 < 9) {
			State.Pawn p = state.getPawn(rowTo, columnTo + 2);
			onBorderWhite = p.equalsPawn("W") || p.equalsPawn("K");
		}
		if(east.equalsPawn("B") && !state.onCitadels(rowTo, columnTo + 1)) {
			return (onBorderWhite || state.onCitadelsBorder(rowTo, columnTo + 2));
		}
		return false;
	}




	public static int evaluate(State state, int turn) {




		int value = 0;
		StringBuffer motivation = new StringBuffer();
		motivation.append("---------------------\n");

		motivation.append(state.toString());
		motivation.append("\n");


		/*if (state.isTerminalWhite()) {
			System.err.println("Trovato terminal white evaluation");
			return 10000;
		}*/

		int escapePaths = 0;


		if(state.kingCanEscape("NORTH"))
			escapePaths++;

		if(state.kingCanEscape("SOUTH"))
			escapePaths++;

		if(state.kingCanEscape("WEST"))
			escapePaths++;

		if(state.kingCanEscape("EAST"))
			escapePaths++;

		if(escapePaths > 1)
			return 7000;

		if(escapePaths > 0) {
			value += (escapePaths * 300);
			motivation.append("trovato almeno un escape point; ");
		}

		if(state.kingCanEscapeInTwoMoves()) {
			motivation.append("king puo scappare in due mosse; ");
			value += 250;
		}

		/*if(state.enemyPawnEatable("W")) {
			motivation = motivation + "enemy pawn eatable White; ";
			value += 50 * (turn < 7 && !kingOnEscapePath ? 10 : 3);
		}*/

		if(state.pawnsOnEscapePoint() > 0) {
			motivation.append("trovate " + state.pawnsOnEscapePoint() + " pedine escape points; ");
			value -= 30 * state.pawnsOnEscapePoint();
		}

		if(state.enemyPawnCanBeEaten("W")) {
			motivation.append("enemy pawn can be eaten White; ");
			value += 50 * (turn < 7 ? 5 : 2);
		}

		int[] myAndEnemyPawns = state.numOfMyAndEnemyPawns();

		motivation.append(" mie pedine " + myAndEnemyPawns[0] + " pedine nemiche " + myAndEnemyPawns[1] + "; ");

		// white pawns
		value += myAndEnemyPawns[0] * 8 * (turn < 7 ? 5 : 2);

		// black pawns

		value -= myAndEnemyPawns[1] * 4 * (turn < 7 ? 5 : 2);



		/*if(state.isTerminalBlack()) {
			System.err.println("Trovato terminal black evaluation");
			return -10000;
		}*/

		//if il re sta per essere mangiato
		if(state.kingCanBeEaten()) {
			motivation.append("re mangiabile; ");
			return -7000;

			//value -= 700;
		}

		// Pedine nere che stanno marcando il re
		value -= 100 * state.numBlackBetweenKingAndEscape();

		/*if(state.enemyPawnEatable("B")) {
			motivation = motivation + "enemy pawn eatable Black; ";
			value -= 70 * (turn < 7 ? 6 : 3);
		}*/

		if(state.enemyPawnCanBeEaten("B")) {
			motivation.append("enemy pawn can be eaten Black; ");
			value -= 50 * (turn < 7 ? 3 : 2);
		}

		value -= (50 * state.enemiesNearKing());
		motivation.append(state.enemiesNearKing() + " enemy near king ; ");

		motivation.append("\nVALUE: " + value + "\n---------------------\n");
		//System.err.println(motivation.toString());

		//Altri casi
		return value;

	}



	private static boolean checkCaptureBlackPawnRight(State state, Action a)	{

		int destRow = a.getRowTo();
		int destCol = a.getColumnTo();
		//mangio a destra
		if (destCol < 7 && state.getPawn(destRow, destCol + 1).equalsPawn("W"))
		{
			if(state.getPawn(destRow, destCol + 2).equalsPawn("B"))
			{
				return true;
			}
			if(state.getPawn(destRow, destCol + 2).equalsPawn("T"))
			{
				return true;
			}
			if (state.onCitadels(destRow, destCol + 2))
			{
				return true;
			}
			return (destRow==4 && destCol-1==4 /*e5*/);

		}

		return false;
	}

	private static boolean checkCaptureBlackPawnLeft(State state, Action a){

		int destRow = a.getRowTo();
		int destCol = a.getColumnTo();
		//mangio a sinistra
		return (destCol > 1
				&& state.getPawn(destRow, destCol - 1).equalsPawn("W")
				&& (state.getPawn(destRow, destCol - 2).equalsPawn("B")
				|| state.getPawn(destRow, destCol - 2).equalsPawn("T")
				|| state.onCitadels(destRow, destCol - 2)
				|| (destRow==4 && destCol-1==4 /*e5*/)));
	}

	private static boolean checkCaptureBlackPawnUp(State state, Action a){

		int destRow = a.getRowTo();
		int destCol = a.getColumnTo();
		// controllo se mangio sopra
		return (destRow > 1
				&& state.getPawn(destRow - 1, destCol).equalsPawn("W")
				&& (state.getPawn(destRow - 2, destCol).equalsPawn("B")
				|| state.getPawn(destRow - 2, destCol).equalsPawn("T")
				|| state.onCitadels(destRow - 2, destCol)
				|| (destRow==4 && destCol-1==4 /*e5*/)));
	}

	private static boolean checkCaptureBlackPawnDown(State state, Action a){

		int destRow = a.getRowTo();
		int destCol = a.getColumnTo();
		// controllo se mangio sotto
		return  (destRow < 7
				&& state.getPawn(destRow + 1, destCol).equalsPawn("W")
				&& (state.getPawn(destRow + 2, destCol).equalsPawn("B")
				|| state.getPawn(destRow + 2, destCol).equalsPawn("T")
				|| state.onCitadels(destRow + 2, destCol)
				|| (destRow==4 && destCol-1==4 /*e5*/)));
	}


	private static State resultState(State state, Action action) {
		State returnState = state.clone();
		returnState.move(action);
		return returnState;
	}


}
