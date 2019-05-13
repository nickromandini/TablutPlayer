package it.unibo.ai.didattica.competition.tablut.client;

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
				int escapePaths = 0;

				if (state.kingOnEscapePoint(rowTo, columnTo))
					return 1000;

				if(state.kingCanEscape(rowTo, columnTo, "NORTH"))
					escapePaths++;

				if(state.kingCanEscape(rowTo, columnTo, "SOUTH"))
					escapePaths++;

				if(state.kingCanEscape(rowTo, columnTo, "WEST"))
					escapePaths++;

				if(state.kingCanEscape(rowTo, columnTo, "EAST"))
					escapePaths++;

				if(escapePaths > 0)
					return escapePaths * 250;

				return 150;


			} else {

				// Controllo se posso mangiare una pedina con la mossa da valutare

				boolean northBorder = false;
				boolean southBorder = false;
				boolean westBorder = false;
				boolean eastBorder = false;

				State.Pawn north = null;
				State.Pawn south = null;
				State.Pawn west = null;
				State.Pawn east = null;



				if(rowTo - 1 >= 0)
					north = state.getPawn(a.getRowTo() - 1, a.getColumnTo());
				else
					northBorder = true;


				if(rowTo + 1 < 9)
					south = state.getPawn(a.getRowTo() + 1, a.getColumnTo());
				else
					southBorder = true;


				if(columnTo - 1 >= 0)
					west = state.getPawn(a.getRowTo(), a.getColumnTo() - 1);
				else
					westBorder = true;


				if(columnTo + 1 < 9)
					east = state.getPawn(a.getRowTo(), a.getColumnTo() + 1);
				else
					eastBorder = true;




				switch (direction) {
					case "NORTH":
						if (!northBorder && checkNorth(north, state, rowTo, columnTo)) {
							return 100;
						}
						else if (!westBorder && checkWest(west, state, rowTo, columnTo)) {
							return 100;
						}
						else if (!eastBorder && checkEast(east, state, rowTo, columnTo)) {
							return 100;
						}
					case "SOUTH":
						if (!southBorder && checkSouth(south, state, rowTo, columnTo)) {
							return 100;
						}
						else if (!westBorder && checkWest(west, state, rowTo, columnTo)){
							return 100;
						}
						else if (!eastBorder && checkEast(east, state, rowTo, columnTo)) {
							return 100;
						}
					case "WEST":
						if (!northBorder && checkNorth(north, state, rowTo, columnTo)) {
							return 100;
						}
						else if (!southBorder && checkSouth(south, state, rowTo, columnTo)) {
							return 100;
						}
						else if (!eastBorder && checkEast(east, state, rowTo, columnTo)) {
							return 100;
						}

					case "EAST":
						if (!northBorder && checkNorth(north, state, rowTo, columnTo)) {
							return 100;
						} else if (!southBorder && checkSouth(south, state, rowTo, columnTo)) {
							return 100;
						} else if (!westBorder && checkWest(west, state, rowTo, columnTo)) {
							return 100;
						}

				}




			}
		}
		else { // caso black

			/*if(blockedKing(state, a))
				return 500;*/

			if(kingEatableWithAction(state, a))
				return 1000;

			int markers = numKingMarking(state, a);
			if (markers > 0)
				return 250 * markers;

			if (checkCaptureBlackKingLeft(state, a) || checkCaptureBlackKingRight(state, a) || checkCaptureBlackKingUp(state, a) || checkCaptureBlackKingDown(state, a))
				return 200;


			if (checkCaptureBlackPawnLeft(state, a) || checkCaptureBlackPawnRight(state, a) || checkCaptureBlackPawnUp(state, a) || checkCaptureBlackPawnDown(state, a))
				return 100;

		}
		return 10;

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

		if(((north.equalsPawn("B") || state.onCitadels(kingCoord[0] - 1, kingCoord[1])) && (kingCoord[0] + 1 == destRow && kingCoord[1] == destColumn ))
				|| ((south.equalsPawn("B") || state.onCitadels(kingCoord[0] + 1, kingCoord[1])) && (kingCoord[0] - 1 == destRow && kingCoord[1] == destColumn ))
				|| ((west.equalsPawn("B") || state.onCitadels(kingCoord[0], kingCoord[1] - 1)) && (kingCoord[0] == destRow && kingCoord[1] + 1 == destColumn ))
				|| ((east.equalsPawn("B") || state.onCitadels(kingCoord[0], kingCoord[1] + 1)) && (kingCoord[0] == destRow && kingCoord[1] - 1== destColumn )))
			return true;




		return false;

	}

	private static int numKingMarking(State state, Action a) {
		int[] kingCoord = state.getKingCoord();
		//indice 0 = row
		//indice 1 = col

		int result = 0;

		//Valuto se marco il re con la mossa attuale
		int destRow = a.getRowTo();
		int destCol = a.getColumnTo();
		for(int[] eCoord : state.getEscapePoints())
			if (kingCoord[0] == eCoord[0] && eCoord[0] == destRow) {
				if (kingCoord[1] > destCol && destCol >= eCoord[1] && isCleanHorizontal(state, kingCoord[0], eCoord[1],destCol) && isCleanHorizontal(state, kingCoord[0],destCol,kingCoord[1]))
					result++;
				else if(kingCoord[1] < destCol && destCol <= eCoord[1] && isCleanHorizontal(state, kingCoord[0], kingCoord[1], destCol) && isCleanHorizontal(state, kingCoord[0],destCol,eCoord[1]))
					result++;
			}
			else if (kingCoord[1] == eCoord[1] && eCoord[1] == destCol) {
				if (kingCoord[0] > destRow && destRow >= eCoord[0] && isCleanVertical(state, kingCoord[1],eCoord[0],destRow) && isCleanVertical(state, kingCoord[1],destRow,kingCoord[0]))
					result++;
				else if(kingCoord[0] < destRow && destRow <= eCoord[0] && isCleanVertical(state, kingCoord[1], kingCoord[0], destRow) && isCleanVertical(state, kingCoord[1],destRow,eCoord[0]))
					result++;
			}

		//Considero se il re sta già venendo marcato anche da altre pedine
		result += state.numBlackBetweenKingAndEscape();

		if (result>0)
			System.out.println("Marcatori del re: "+result);

		return result;
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


	public static int evaluate(State state, String me, int turn) {


		int value = 0;

		if (state.isTerminalWhite())
			return 10000;

		int escapePaths = 0;


		if(state.kingCanEscape("NORTH"))
			escapePaths++;

		if(state.kingCanEscape("SOUTH"))
			escapePaths++;

		if(state.kingCanEscape("WEST"))
			escapePaths++;

		if(state.kingCanEscape("EAST"))
			escapePaths++;

		if(escapePaths > 0)
			value += (escapePaths * 250);

		if(state.kingCanEscapeInTwoMoves())
			value += 70;

		if(state.enemyPawnEatable("W")) {
			value += 50 * (turn < 7 ? 5 : 3);
		}

		if(state.enemyPawnCanBeEaten("W")) {
			value += 30 * (turn < 7 ? 5 : 2);
		}

		int[] myAndEnemyPawns = state.numOfMyAndEnemyPawns(me);

		// white pawns
		value += myAndEnemyPawns[0] * 10 * (turn < 7 ? 5 : 2);

		// black pawns

		value -= myAndEnemyPawns[1] * 10 * (turn < 7 ? 5 : 2);



		if(state.isTerminalBlack())
			return -10000;

		//if il re sta per essere mangiato
		if(state.kingCanBeEaten())
			value -= 400;

		value -= 100 * state.numBlackBetweenKingAndEscape();

		if(state.enemyPawnEatable("B"))
			value -= 70 * (turn < 7 ? 4 : 3);

		if(state.enemyPawnCanBeEaten("B")) {
			value -= 50 * (turn < 7 ? 3 : 2);
		}

		value -= (5 * state.enemiesNearKing());

		//Altri casi
		return value;

	}


	private static boolean checkCaptureBlackKingLeft(State state, Action a){
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		//ho il re sulla sinistra
		if (a.getColumnTo()>1&&state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K"))
		{
			//re sul trono
			if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e5"))
			{
				if(state.getPawn(3, 4).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B"))
				{
					return true;

				}
			}
			//re adiacente al trono
			if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e4"))
			{
				if(state.getPawn(2, 4).equalsPawn("B")
						&& state.getPawn(3, 3).equalsPawn("B"))
				{
					return true;

				}
			}
			if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e6"))
			{
				if(state.getPawn(5, 3).equalsPawn("B")
						&& state.getPawn(6, 4).equalsPawn("B"))
				{
					return true;

				}
			}
			if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("f5"))
			{
				if(state.getPawn(3, 5).equalsPawn("B")
						&& state.getPawn(5, 5).equalsPawn("B"))
				{
					return true;

				}
			}
			//sono fuori dalle zone del trono
			if(!state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("f5"))
			{
				return (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()-2)));
			}
		}
		return false;
	}

	private static boolean checkCaptureBlackKingRight(State state, Action a){
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		//ho il re sulla destra
		if (a.getColumnTo()<state.getBoard().length-2&&(state.getPawn(a.getRowTo(),a.getColumnTo()+1).equalsPawn("K")))
		{
			//re sul trono
			if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e5"))
			{
				if(state.getPawn(3, 4).equalsPawn("B")
						&& state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(5, 4).equalsPawn("B"))
				{
					return true;
				}
			}
			//re adiacente al trono
			if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e4"))
			{
				if(state.getPawn(2, 4).equalsPawn("B")
						&& state.getPawn(3, 5).equalsPawn("B"))
				{
					return true;
				}
			}
			if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e6"))
			{
				if(state.getPawn(5, 5).equalsPawn("B")
						&& state.getPawn(6, 4).equalsPawn("B"))
				{
					return true;
				}
			}
			if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("d5"))
			{
				if(state.getPawn(3, 3).equalsPawn("B")
						&& state.getPawn(3, 5).equalsPawn("B"))
				{
					return true;
				}
			}
			//sono fuori dalle zone del trono
			if(!state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("d5")
					&& !state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e6")
					&& !state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e4")
					&& !state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e5"))
			{
				return (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()+2)));
			}
		}
		return false;
	}

	private static boolean checkCaptureBlackKingDown(State state, Action a){
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		//ho il re sotto
		if (a.getRowTo()<state.getBoard().length-2&&state.getPawn(a.getRowTo()+1,a.getColumnTo()).equalsPawn("K"))
		{
			//re sul trono
			if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e5"))
			{
				if(state.getPawn(5, 4).equalsPawn("B")
						&& state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B"))
				{
					return true;
				}
			}
			//re adiacente al trono
			if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e4"))
			{
				if(state.getPawn(3, 3).equalsPawn("B")
						&& state.getPawn(3, 5).equalsPawn("B"))
				{
					return true;
				}
			}
			if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("d5"))
			{
				if(state.getPawn(4, 2).equalsPawn("B")
						&& state.getPawn(5, 3).equalsPawn("B"))
				{
					return true;
				}
			}
			if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("f5"))
			{
				if(state.getPawn(4, 6).equalsPawn("B")
						&& state.getPawn(5, 5).equalsPawn("B"))
				{
					return true;
				}
			}
			//sono fuori dalle zone del trono
			if(!state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e4")
					&& !state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e5"))
			{
				return (state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo()+2, a.getColumnTo())));
			}
		}
		return false;
	}

	private static boolean checkCaptureBlackKingUp(State state, Action a){
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		//ho il re sopra
		if (a.getRowTo()>1&&state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K"))
		{
			//re sul trono
			if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e5"))
			{
				if(state.getPawn(3, 4).equalsPawn("B")
						&& state.getPawn(4, 5).equalsPawn("B")
						&& state.getPawn(4, 3).equalsPawn("B"))
				{
					return true;
				}
			}
			//re adiacente al trono
			if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e6"))
			{
				if(state.getPawn(5, 3).equalsPawn("B")
						&& state.getPawn(5, 5).equalsPawn("B"))
				{
					return true;
				}
			}
			if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("d5"))
			{
				if(state.getPawn(4, 2).equalsPawn("B")
						&& state.getPawn(3, 3).equalsPawn("B"))
				{
					return true;
				}
			}
			if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("f5"))
			{
				if(state.getPawn(4, 4).equalsPawn("B")
						&& state.getPawn(3, 5).equalsPawn("B"))
				{
					return true;
				}
			}
			//sono fuori dalle zone del trono
			if(!state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("d5")
					&& !state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e4")
					&& !state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("f5")
					&& !state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e5"))
			{
				return (state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo()-2, a.getColumnTo())));
			}
		}
		return false;
	}

	private static boolean checkCaptureBlackPawnRight(State state, Action a)	{
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		//mangio a destra
		if (a.getColumnTo() < state.getBoard().length - 2 && state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W"))
		{
			if(state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B"))
			{
				return true;
			}
			if(state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T"))
			{
				return true;
			}
			if(citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2)))
			{
				return true;
			}
			return (state.getBox(a.getRowTo(), a.getColumnTo()+2).equals("e5"));

		}

		return false;
	}

	private static boolean checkCaptureBlackPawnLeft(State state, Action a){
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		//mangio a sinistra
		return (a.getColumnTo() > 1
				&& state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
				|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
				|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
				|| (state.getBox(a.getRowTo(), a.getColumnTo()-2).equals("e5"))));
	}

	private static boolean checkCaptureBlackPawnUp(State state, Action a){
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		// controllo se mangio sopra
		return (a.getRowTo() > 1
				&& state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
				|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
				|| citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
				|| (state.getBox(a.getRowTo()-2, a.getColumnTo()).equals("e5"))));
	}

	private static boolean checkCaptureBlackPawnDown(State state, Action a){
		List<String> citadels = Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8");
		// controllo se mangio sotto
		return  (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
				|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
				|| citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
				|| (state.getBox(a.getRowTo()+2, a.getColumnTo()).equals("e5"))));
	}


}