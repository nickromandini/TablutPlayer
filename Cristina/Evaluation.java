package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Evaluation {

    public Evaluation() {}


    public static int evaluateAction(Action a, State state, String direction) {
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
        return ThreadLocalRandom.current().nextInt(0,  1000);
    }

    private static boolean checkNorth(State.Pawn north, State state, Action a) {
        if (north.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo() - 1, a.getColumnTo()})) {
            if ((a.getRowTo() - 2 < 0) || state.onCitadelsBorder(new int[]{a.getRowTo() - 2, a.getColumnTo()}) || state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W"))
                return true;
        }
        return false;
    }

    private static boolean checkSouth(State.Pawn south, State state, Action a) {
        if(south.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo() + 1, a.getColumnTo()})) {
            if ((a.getRowTo() + 2 > 9) || state.onCitadelsBorder(new int[]{a.getRowTo() + 2, a.getColumnTo()}) || state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W"))
                return true;
        }
        return false;
    }

    private static boolean checkWest(State.Pawn west, State state, Action a) {
        if (west.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo(), a.getColumnTo() - 1})) {
            if ((a.getColumnTo() - 2 < 0) || state.onCitadelsBorder(new int[]{a.getRowTo(), a.getColumnTo() - 2}) || state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W"))
                return true;
        }
        return false;
    }

    private static boolean checkEast(State.Pawn east, State state, Action a) {
        if(east.equalsPawn("B") && !state.onCitadels(new int[]{a.getRowTo(), a.getColumnTo() + 1})) {
            if ((a.getColumnTo() + 2 > 9) || state.onCitadelsBorder(new int[]{a.getRowTo(), a.getColumnTo() + 2}) || state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W"))
                return true;
        }
        return false;
    }


    public static int evaluate(State state) {

        if(state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
            return ThreadLocalRandom.current().nextInt(0,  1000);
        } else if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {

                if (state.kingOnEscapePoint())
                    return ThreadLocalRandom.current().nextInt(9500,  10001);;

                if (state.kingCanEscape( "NORTH") || state.kingCanEscape("SOUTH") || state.kingCanEscape("WEST") || state.kingCanEscape("EAST")) {
                    return ThreadLocalRandom.current().nextInt(9000,  9500);
                }

                // Non funziona bene
                /*if(enemyPawnEatable(state, "W")) {
                    return ThreadLocalRandom.current().nextInt(1000,  2001);
                }*/

                return ThreadLocalRandom.current().nextInt(0,  1000);



        }

        return 0;
    }


    // Non funziona bene
    // Quella implementata nella evaluateAction funziona bene

    private static boolean enemyPawnEatable(State state, String me) {

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

            if(((northBorder || north.equalsPawn(me)) && (southBorder || south.equalsPawn(me)))
                    || ((westBorder || west.equalsPawn(me)) && (eastBorder || east.equalsPawn(me))))
                return true;

            // Vicinanza trono

            if((north.equalsPawn("T") && south.equalsPawn(me))
                    || (south.equalsPawn("T") && north.equalsPawn(me))
                    || (west.equalsPawn("T") && east.equalsPawn(me))
                    || (east.equalsPawn("T") && west.equalsPawn(me)))
                return true;

            // Vicinanza cittadella

            if((state.onCitadelsBorder(new int[]{pawn[0] - 1, pawn[1]}) && south.equalsPawn(me))
                    || (state.onCitadelsBorder(new int[]{pawn[0] + 1, pawn[1]}) && north.equalsPawn(me))
                    || (state.onCitadelsBorder(new int[]{pawn[0], pawn[1] - 1}) && east.equalsPawn(me))
                    || (state.onCitadelsBorder(new int[]{pawn[0], pawn[1] + 1}) && west.equalsPawn(me)))
                return true;



        }

        return false;
    }



}
