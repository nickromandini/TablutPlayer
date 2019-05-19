package it.unibo.ai.didattica.competition.tablut.util;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;


public class Evaluation {



	// EVALUATE ACTION


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
				if(temp.kingCanBeEaten())
					return -1000;

				// Controllo se posso mangiare una pedina con la mossa da valutare
				if(blackPawnEatable(state, a, direction))
					if(state.differenceNumOfPawnsNormalized() <= -3)
						return 500;
					else
						return 100;

				return 0;
			}
		}
		else { // caso black

			if(kingEatableWithAction(state, a))
				return 1000;

			State temp = resultState(state,a);
			if(temp.kingCanEscapeInTwoMovesTerminal()) {
				return -1000;
			}

			if (isKingMarked(state, a))
				return 500;

			int value = 0;

			if (checkCaptureBlackPawnLeft(state, a) || checkCaptureBlackPawnRight(state, a) || checkCaptureBlackPawnUp(state, a) || checkCaptureBlackPawnDown(state, a))
				if(state.differenceNumOfPawnsNormalized() >= 7)
					return 500;
				else
					value += 100;

			if (enemiesNearKingCardinal(state, a))
				value += 50;

			if (enemiesNearKingDiagonal(state, a))
				value += 30;

			if (blackOnEscape(state, a))
				value += 20;

			return value;

		}


	}

	private static boolean kingEatableWithAction(State state, Action a) {

		int[] kingCoord = state.getKingCoord();
		int destRow = a.getRowTo();
		int destColumn = a.getColumnTo();

		State.Pawn north = state.getPawn(kingCoord[0] - 1, kingCoord[1]);

		State.Pawn south = state.getPawn(kingCoord[0] + 1, kingCoord[1]);

		State.Pawn west = state.getPawn(kingCoord[0], kingCoord[1] - 1);

		State.Pawn east = state.getPawn(kingCoord[0], kingCoord[1] + 1);



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
		} // Re vicino al trono
		else if(kingCoord[0] == 3 && kingCoord[1] == 4) {
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
		} // Campo aperto
		else if (((north.equalsPawn("B") || state.onCitadels(kingCoord[0] - 1, kingCoord[1])) && (kingCoord[0] + 1 == destRow && kingCoord[1] == destColumn))
				|| ((south.equalsPawn("B") || state.onCitadels(kingCoord[0] + 1, kingCoord[1])) && (kingCoord[0] - 1 == destRow && kingCoord[1] == destColumn))
				|| ((west.equalsPawn("B") || state.onCitadels(kingCoord[0], kingCoord[1] - 1)) && (kingCoord[0] == destRow && kingCoord[1] + 1 == destColumn))
				|| ((east.equalsPawn("B") || state.onCitadels(kingCoord[0], kingCoord[1] + 1)) && (kingCoord[0] == destRow && kingCoord[1] - 1 == destColumn)))
			return true;

		return false;

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
				if (kingCoord[1] > destCol && destCol >= eCoord[1] && isCleanHorizontal(state, kingCoord[0], eCoord[1], kingCoord[1]))
					return true;
				else if(kingCoord[1] < destCol && destCol <= eCoord[1] && isCleanHorizontal(state, kingCoord[0], kingCoord[1] + 1, eCoord[1]))
					return true;
			}
			else if (kingCoord[1] == eCoord[1] && eCoord[1] == destCol) {
				if (kingCoord[0] > destRow && destRow >= eCoord[0] && isCleanVertical(state, kingCoord[1], eCoord[0], kingCoord[0]))
					return true;
				else if(kingCoord[0] < destRow && destRow <= eCoord[0] && isCleanVertical(state, kingCoord[1], kingCoord[0] + 1, eCoord[0] + 1))
					return true;
			}
		return false;
	}

	private static boolean isCleanHorizontal(State state, int vertical, int start, int end) {
		for (int i=start; i<end; i++) {
			if(!state.getPawn(vertical, i).equalsPawn("O"))
				return false;
		}
		return true;
	}

	private static boolean isCleanVertical(State state, int horizontal, int start, int end) {
		for (int i=start; i<end; i++) {
			if(!state.getPawn(i, horizontal).equalsPawn("O"))
				return false;
		}
		return true;
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

	private static boolean blackOnEscape(State state, Action action) {
		int[] kingCoord = state.getKingCoord();
		int[][] escapePoints = state.getEscapePoints();
		int x = action.getRowTo();
		int y = action.getColumnTo();

		/*
		 * I: (0,6), (0,7), (1,8), (2,8)
		 * II: (6,8), (7,8), (8,6), (8,7)
		 * III: (8,2), (8,1), (6,0), (7,0)
		 * IV: (2,0), (1,0), (0,1), (0,2)
		 */

		if (kingCoord[0] == 4 && kingCoord[1] == 4) {
			for(int[] point : escapePoints) {
				if(point[0] == x && point[1] == y)
					return true;
			}
			return false;
		}
		else if (kingCoord[0] <= 4) {

			if (kingCoord[1] >= 4) {
				/*controllo I quadrante*/
				if((x==0 && y==6) || (x==0 && y==7) || (x==1 && y==8) || (x==2 && y==8))
					return true;
				return false;
			}
			else {
				/*controllo IV quadrante*/
				if((x==2 && y==0) || (x==1 && y==0) || (x==0 && y==1) || (x==0 && y==2))
					return true;
				return false;
			}

		}
		else { //kingCoord[0] > 4

			if (kingCoord[1] >= 4) {
				/*controllo II quadrante*/
				if((x==6 && y==8) || (x==7 && y==8) || (x==8 && y==6) || (x==8 && y==7))
					return true;
				return false;
			}
			else {
				/*controllo III quadrante*/
				if((x==8 && y==2) || (x==8 && y==1) || (x==6 && y==0) || (x==7 && y==0))
					return true;
				return false;
			}

		}

	}

	private static boolean enemiesNearKingCardinal(State state, Action action) {

		int[] kingCoord = state.getKingCoord();

		//Pedina nera sopra il re
		if (kingCoord[0]-1 >= 0 && kingCoord[0]-1 == action.getRowTo() && kingCoord[1] == action.getColumnTo())
			return true;

		//Pedina nera sotto il re
		if (kingCoord[0]+1 <= 8 && kingCoord[0]+1 == action.getRowTo() && kingCoord[1] == action.getColumnTo())
			return true;

		//Pedina nera a sinistra del re
		if (kingCoord[1]-1 >= 0 && kingCoord[0] == action.getRowTo() && kingCoord[1]-1 == action.getColumnTo())
			return true;

		//Pedina nera a destra del re
		if (kingCoord[1]+1 <= 8 && kingCoord[0] == action.getRowTo() && kingCoord[1]+1 == action.getColumnTo())
			return true;

		return false;

	}

	private static boolean enemiesNearKingDiagonal(State state, Action action) {

		int[] kingCoord = state.getKingCoord();

		//In alto a sinistra
		if (kingCoord[0]-1 >= 0 && kingCoord[1]-1 >= 0 && kingCoord[0]-1 == action.getRowTo() && kingCoord[1]-1 == action.getColumnTo())
			return true;

		//In alto a destra
		if (kingCoord[0]-1 >= 0 && kingCoord[1]+1 <= 8 && kingCoord[0]-1 == action.getRowTo() && kingCoord[1]+1 == action.getColumnTo())
			return true;

		//In basso a sinistra
		if (kingCoord[0]+1 <= 8 && kingCoord[1]-1 >= 0 && kingCoord[0]+1 == action.getRowTo() && kingCoord[1]-1 == action.getColumnTo())
			return true;

		//In basso a destra
		if (kingCoord[0]+1 <= 8 && kingCoord[1]+1 <= 8 && kingCoord[0]+1 == action.getRowTo() && kingCoord[1]+1 == action.getColumnTo())
			return true;

		return false;

	}



	// EVALUATE


	public static int evaluate(State state) {

		int value = 0;

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
			value += 7000;
		else
			value += 300;

		if(state.kingCanEscapeInTwoMoves()) {
			value += 250;
		}

		int pawnsOnEP = state.pawnsOnEscapePoint();
		if(pawnsOnEP > 0) {
			value -= 30 * pawnsOnEP;
		}

		if(state.enemyPawnCanBeEaten("W")) {
			value += 100;
		}

		// differenza compresa tra -9 e 13 ( punto medio 0 )
		// -9 caso favorevole black
		// 13 caso favorevole white
		value += 50 * state.differenceNumOfPawnsNormalized();

		//if il re sta per essere mangiato
		if(state.kingCanBeEaten()) {
			value -= 7000;
		}

		// Pedine nere che stanno marcando il re
		value -= 100 * state.numBlackBetweenKingAndEscape();

		if(state.enemyPawnCanBeEaten("B")) {
			value -= 100;
		}

		value -= (100 * state.enemiesNearKingCardinal());
		value -= (50 * state.enemiesNearKingDiagonal());

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
