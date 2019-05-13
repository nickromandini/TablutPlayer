package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class Evaluation {



	public static int evaluateAction(Action a, State state, String direction) {

		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (state.getPawn(a.getRowFrom(), a.getColumnFrom()).equalsPawn("K")) {

				if (state.kingOnEscapePoint(a.getRowTo(), a.getColumnTo()))
					return 10000;

				if (state.kingCanEscape(a.getRowTo(), a.getColumnTo(), "NORTH") || state.kingCanEscape(a.getRowTo(), a.getColumnTo(), "SOUTH") || state.kingCanEscape(a.getRowTo(), a.getColumnTo(), "WEST") || state.kingCanEscape(a.getRowTo(), a.getColumnTo(), "EAST")) {
					return 200;
				}

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

				int rowTo = a.getRowTo();
				int columnTo = a.getColumnTo();

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
			
			if (state.kingEatable())
				return 10000;
			
			int markers = numKingMarking(state, a);
			if (markers > 0)
				return 5000 * markers;
			
			if (checkCaptureBlackKingLeft(state, a) || checkCaptureBlackKingRight(state, a) || checkCaptureBlackKingUp(state, a) || checkCaptureBlackKingDown(state, a))
				return 1000;	
			
			if (checkCaptureBlackPawnLeft(state, a) || checkCaptureBlackPawnRight(state, a) || checkCaptureBlackPawnUp(state, a) || checkCaptureBlackPawnDown(state, a))
				return 100;

		}
		return 10;

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

		// Situazioni positive per Bionda
		int value = 0;

		if (state.isTerminalWhite())
			return 10000;

		if (state.kingCanEscape( "NORTH") || state.kingCanEscape("SOUTH") || state.kingCanEscape("WEST") || state.kingCanEscape("EAST")) {
			value += 500;
		}

		if(state.kingCanEscapeInTwoMoves())
			value += 70;

		// Non funziona bene
		if(state.enemyPawnEatable("W")) {
			value += 50 * (turn < 5 ? 2 : 1);
		}

		if(state.enemyPawnCanBeEaten("W")) {
			value += 30 * (turn < 5 ? 2 : 1);
		}


		value += (25 * 1.0/state.minKingDistanceFromSafe());


		
		//Situazioni favorevoli per la mora
		if(state.isTerminalBlack())
			return -10000;

		//if il re sta per essere mangiato
		if(state.kingCanBeEaten())
			value -= 2000;
		
		// Quante pedine sono a distanza <=2 dal re
		value -= 1000 * state.blackPawnCloseToKing();

		// Quante pedine bloccano il re per impedirgli la fuga
		value -= 1000 * state.numBlackBetweenKingAndEscape();

		if(state.enemyPawnEatable("B"))
			value -= 70 * (turn < 5 ? 3 : 2);

		if(state.enemyPawnCanBeEaten("B")) {
			value -= 50 * (turn < 5 ? 3 : 2);
		}

		value -= (5 * state.enemiesNearKing());

		
		
		
		return value;

	}
	
	

	private boolean enemyPawnEatable(State state, String me) {

		String enemy = me.equals("W") ? "BLACK" : "WHITE";

		List<int[]> enemyPawns = state.getEnemyPawnsCoord(enemy);

		boolean northBorder = false;
		boolean southBorder = false;
		boolean westBorder = false;
		boolean eastBorder = false;

		State.Pawn north = null;
		State.Pawn south = null;
		State.Pawn west = null;
		State.Pawn east = null;


		for( int[] pawn : enemyPawns) {

			// Non posso mangiare neri dentro accampamento
			if(state.onCitadels(pawn[0], pawn[1]) && me.equals("W"))
				continue;

			try {
				north = state.getPawn(pawn[0] - 1, pawn[1]);
			} catch (Exception e) {
				northBorder = true;
			}

			try {
				south = state.getPawn(pawn[0] + 1, pawn[1]);
			} catch (Exception e) {
				southBorder = true;
			}

			try {
				west = state.getPawn(pawn[0], pawn[1] - 1);
			} catch (Exception e) {
				westBorder = true;
			}

			try {
				east = state.getPawn(pawn[0], pawn[1] + 1);
			} catch (Exception e) {
				eastBorder = true;
			}


			// Campo aperto

			if(!northBorder && !southBorder && (north.equalsPawn(me) && south.equalsPawn(me)))
				return true;

			if(!westBorder && !eastBorder && (west.equalsPawn(me) && east.equalsPawn(me)))
				return true;


			// Vicinanza trono

			if(!northBorder && !southBorder && (north.equalsPawn("T") && south.equalsPawn(me))
					|| (!northBorder && !southBorder && south.equalsPawn("T") && north.equalsPawn(me))
					|| (!westBorder && !eastBorder && west.equalsPawn("T") && east.equalsPawn(me))
					|| (!westBorder && !eastBorder && east.equalsPawn("T") && west.equalsPawn(me)))
				return true;

			// Vicinanza cittadella

			if((!southBorder && state.onCitadelsBorder(pawn[0] - 1, pawn[1]) && south.equalsPawn(me))
					|| (!northBorder && state.onCitadelsBorder(pawn[0] + 1, pawn[1]) && north.equalsPawn(me))
					|| (!eastBorder && state.onCitadelsBorder(pawn[0], pawn[1] - 1) && east.equalsPawn(me))
					|| (!westBorder && state.onCitadelsBorder(pawn[0], pawn[1] + 1) && west.equalsPawn(me)))
				return true;



		}

		return false;
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
