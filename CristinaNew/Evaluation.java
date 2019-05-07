package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Evaluation {

	private List<String> citadels = new ArrayList<String>();

	public Evaluation() {

		citadels.add("a4");
		citadels.add("a5");
		citadels.add("a6");
		citadels.add("b5");
		citadels.add("d1");
		citadels.add("e1");
		citadels.add("f1");
		citadels.add("e2");
		citadels.add("i4");
		citadels.add("i5");
		citadels.add("i6");
		citadels.add("h5");
		citadels.add("d9");
		citadels.add("e9");
		citadels.add("f9");
		citadels.add("e8");
	}




	public int evaluateAction(Action a, State state, String direction) {
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (state.getPawn(a.getRowFrom(), a.getColumnFrom()).equalsPawn("K")) {

				if (state.kingOnEscapePoint(new int[]{a.getRowTo(), a.getColumnTo()}))
					return 10000;

				if (state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "NORTH") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "SOUTH") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "WEST") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "EAST")) {
					return ThreadLocalRandom.current().nextInt(9000,  9500);
				}

				return ThreadLocalRandom.current().nextInt(5000,  6000);


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

				if(a.getRowTo() - 1 > 0)
					north = state.getPawn(a.getRowTo() - 1, a.getColumnTo());
				else
					northBorder = true;


				if(a.getRowTo() + 1 < 9)
					south = state.getPawn(a.getRowTo() + 1, a.getColumnTo());
				else
					southBorder = true;


				if(a.getColumnTo() - 1 > 0)
					west = state.getPawn(a.getRowTo(), a.getColumnTo() - 1);
				else
					westBorder = true;


				if(a.getColumnTo() + 1 < 9)
					east = state.getPawn(a.getRowTo(), a.getColumnTo() + 1);
				else
					eastBorder = true;


				switch (direction) {
					case "NORTH":
						if (!northBorder && checkNorth(north, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
						else if (!westBorder && checkWest(west, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
						else if (!eastBorder && checkEast(east, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
					case "SOUTH":
						if (!southBorder && checkSouth(south, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
						else if (!westBorder && checkWest(west, state, a)){
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
						else if (!eastBorder && checkEast(east, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
					case "WEST":
						if (!northBorder && checkNorth(north, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
						else if (!southBorder && checkSouth(south, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}
						else if (!eastBorder && checkEast(east, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}

					case "EAST":
						if (!northBorder && checkNorth(north, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						} else if (!southBorder && checkSouth(south, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						} else if (!westBorder && checkWest(west, state, a)) {
							return ThreadLocalRandom.current().nextInt(1000, 2001);
						}

				}




			}
		}
		else { // caso black
			if (checkCaptureBlackKingLeft(state, a) || checkCaptureBlackKingRight(state, a) || checkCaptureBlackKingUp(state, a) || checkCaptureBlackKingDown(state, a))
				return ThreadLocalRandom.current().nextInt(9000,  9500);

			if (checkCaptureBlackPawnLeft(state, a) || checkCaptureBlackPawnRight(state, a) || checkCaptureBlackPawnUp(state, a) || checkCaptureBlackPawnDown(state, a))
				return ThreadLocalRandom.current().nextInt(1000, 2001);

		}
		return ThreadLocalRandom.current().nextInt(0,  1000);

	}

	private boolean checkNorth(State.Pawn north, State state, Action a) {
		if (north.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo() - 1, a.getColumnTo()})) {
			if (state.onCitadelsBorder(new int[]{a.getRowTo() - 2, a.getColumnTo()}) || state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W"))
				return true;
		}
		return false;
	}

	private boolean checkSouth(State.Pawn south, State state, Action a) {
		if(south.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo() + 1, a.getColumnTo()})) {
			if (state.onCitadelsBorder(new int[]{a.getRowTo() + 2, a.getColumnTo()}) || state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W"))
				return true;
		}
		return false;
	}

	private boolean checkWest(State.Pawn west, State state, Action a) {
		if (west.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo(), a.getColumnTo() - 1})) {
			if (state.onCitadelsBorder(new int[]{a.getRowTo(), a.getColumnTo() - 2}) || state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W"))
				return true;
		}
		return false;
	}

	private boolean checkEast(State.Pawn east, State state, Action a) {
		if(east.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo(), a.getColumnTo() + 1})) {
			if (state.onCitadelsBorder(new int[]{a.getRowTo(), a.getColumnTo() + 2}) || state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W"))
				return true;
		}
		return false;
	}


	public int evaluate(State state) {

        /*if(state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {


        		//if il re è stato mangiato
        		if(state.isTerminalBlack())
        			return 10000;

        		//if il re sta per essere mangiato
        		if(state.kingEatable() || state.isKingEated())
         		return ThreadLocalRandom.current().nextInt(9000,  9500);

        		//Altri casi
        		return ThreadLocalRandom.current().nextInt(0,  1000);

        } else if (me.equals("W")) {*/



		// Situazioni positive per Bionda
		int value = 0;

		if (state.kingOnEscapePoint())
			return 10000;

		if (state.kingCanEscape( "NORTH") || state.kingCanEscape("SOUTH") || state.kingCanEscape("WEST") || state.kingCanEscape("EAST")) {
			return ThreadLocalRandom.current().nextInt(9000,  9500);
		}

		// Non funziona bene
		if(enemyPawnEatable(state, "W")) {
			value = ThreadLocalRandom.current().nextInt(1000,  2001);
		}

		value += (5000 * 1.0/state.minKingDistanceFromSafe());


		if(state.isTerminalBlack())
			return -10000;

		//if il re sta per essere mangiato
		if(state.kingCanBeEaten()/* || state.isKingEated()*/)
			return -ThreadLocalRandom.current().nextInt(9000,  9500);

		if(enemyPawnEatable(state, "B"))
			value -= ThreadLocalRandom.current().nextInt(800,  900);

		value -= (250 * state.enemiesNearKing());


		//Altri casi
		return value;





		//}

		//return 0;
	}


	private boolean enemyPawnEatable(State state, String me) {

		List<int[]> enemyPawns = state.getEnemyPawnsCoord();

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
			if(state.onCitadels(pawn) && me.equals("W"))
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

			if((!southBorder && state.onCitadelsBorder(new int[]{pawn[0] - 1, pawn[1]}) && south.equalsPawn(me))
					|| (!northBorder && state.onCitadelsBorder(new int[]{pawn[0] + 1, pawn[1]}) && north.equalsPawn(me))
					|| (!eastBorder && state.onCitadelsBorder(new int[]{pawn[0], pawn[1] - 1}) && east.equalsPawn(me))
					|| (!westBorder && state.onCitadelsBorder(new int[]{pawn[0], pawn[1] + 1}) && west.equalsPawn(me)))
				return true;



		}

		return false;
	}

	private boolean checkCaptureBlackKingLeft(State state, Action a){
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
				if(state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()-2)))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkCaptureBlackKingRight(State state, Action a){
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
				if(state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()+2)))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkCaptureBlackKingDown(State state, Action a){
		//ho il re sotto
		if (a.getRowTo()<state.getBoard().length-2&&state.getPawn(a.getRowTo()+1,a.getColumnTo()).equalsPawn("K"))
		{
			System.out.println("Ho il re sotto");
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
				if(state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo()+2, a.getColumnTo())))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkCaptureBlackKingUp(State state, Action a){
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
				if(state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")
						|| citadels.contains(state.getBox(a.getRowTo()-2, a.getColumnTo())))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkCaptureBlackPawnRight(State state, Action a)	{
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
			if(state.getBox(a.getRowTo(), a.getColumnTo()+2).equals("e5"))
			{
				return true;
			}

		}

		return false;
	}

	private boolean checkCaptureBlackPawnLeft(State state, Action a){
		//mangio a sinistra
		if (a.getColumnTo() > 1
				&& state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
				|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
				|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
				|| (state.getBox(a.getRowTo(), a.getColumnTo()-2).equals("e5"))))
		{
			return true;
		}
		return false;
	}

	private boolean checkCaptureBlackPawnUp(State state, Action a){
		// controllo se mangio sopra
		if (a.getRowTo() > 1
				&& state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
				|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
				|| citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
				|| (state.getBox(a.getRowTo()-2, a.getColumnTo()).equals("e5"))))
		{
			return true;
		}
		return false;
	}

	private boolean checkCaptureBlackPawnDown(State state, Action a){
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
				|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
				|| citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
				|| (state.getBox(a.getRowTo()+2, a.getColumnTo()).equals("e5"))))
		{
			return true;
		}
		return false;
	}


}