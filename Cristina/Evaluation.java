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
                    return ThreadLocalRandom.current().nextInt(9500,  10001);;

                if (state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "NORTH") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "SOUTH") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "WEST") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "EAST")) {
                    return ThreadLocalRandom.current().nextInt(9000,  9500);
                }


            } else {
                try {
                    State.Pawn north = state.getPawn(a.getRowTo() - 1, a.getColumnTo());
                    State.Pawn south = state.getPawn(a.getRowTo() + 1, a.getColumnTo());
                    State.Pawn west = state.getPawn(a.getRowTo(), a.getColumnTo() - 1);
                    State.Pawn east = state.getPawn(a.getRowTo(), a.getColumnTo() + 1);

                    switch (direction) {
                        case "NORTH":
                            if (north.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W"))
                                    return ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (west.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (east.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            }
                        case "SOUTH":
                            if (south.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (west.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (east.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            }
                        case "WEST":
                            if (north.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (south.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (east.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            }
                        case "EAST":
                            if (north.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (south.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            } else if (west.equalsPawn("B")) {
                                if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W"))
                                    ThreadLocalRandom.current().nextInt(1000, 2001);
                            }

                    }
                }
                catch(Exception e) {

                }



            }
        }
        return ThreadLocalRandom.current().nextInt(0,  1000);
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

                if(enemyPawnEatable(state, "W")) {
                    return ThreadLocalRandom.current().nextInt(1000,  2001);
                }

                return ThreadLocalRandom.current().nextInt(0,  1000);



        }

        return 0;
    }

    private static boolean enemyPawnEatable(State state, String me) {

        List<int[]> enemyPawns = state.getEnemyPawnsCoord();

        for( int[] pawn : enemyPawns) {
            try {
                State.Pawn north = state.getPawn(pawn[0] - 1, pawn[1]);
                State.Pawn south = state.getPawn(pawn[0] + 1, pawn[1]);
                State.Pawn west = state.getPawn(pawn[0], pawn[1] - 1);
                State.Pawn east = state.getPawn(pawn[0], pawn[1] + 1);

                // Campo aperto

                if((north.equalsPawn(me) && south.equalsPawn(me))
                        || (west.equalsPawn(me) && east.equalsPawn(me)))
                    return true;

                // Vicinanza trono

                if((north.equalsPawn("T") && south.equalsPawn(me))
                        || (south.equalsPawn("T") && north.equalsPawn(me))
                        || (west.equalsPawn("T") && east.equalsPawn(me))
                        || (east.equalsPawn("T") && west.equalsPawn(me)))
                    return true;

                // Vicinanza cittadella

                if((state.onCitadels(new int[]{pawn[0] - 1, pawn[1]}) && south.equalsPawn(me))
                        || (state.onCitadels(new int[]{pawn[0] + 1, pawn[1]}) && north.equalsPawn(me))
                        || (state.onCitadels(new int[]{pawn[0], pawn[1] - 1}) && east.equalsPawn(me))
                        || (state.onCitadels(new int[]{pawn[0], pawn[1] + 1}) && west.equalsPawn(me)))
                    return true;


            } catch (Exception e) {}
        }

        return false;
    }



}
