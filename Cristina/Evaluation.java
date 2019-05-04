package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Evaluation {

    public Evaluation() {}


    public int evaluateMock(Action a, StateTablut.Turn turn, State state) {
        if (turn.equalsTurn(State.Turn.WHITE.toString())) {
            if (state.getPawn(a.getRowFrom(), a.getColumnFrom()).equalsPawn("K")) {

                if (kingOnEscapePoint(new int[]{a.getRowTo(), a.getColumnTo()}))
                    return 1000;

                if (kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "NORTH", state) || kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "SOUTH", state) || kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "WEST", state) || kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "EAST", state)) {
                    return 900;
                }
                return 500;


            } else {

            }
        }
        return 0;
    }

    public int evaluate(State state, State.Turn turn) {

        if(turn.equalsTurn(State.Turn.BLACK.toString())) {



        } else if (turn.equalsTurn(State.Turn.WHITE.toString())) {

            if(kingOnEscapePoint(getKingCoord(state)))
                return 1000;

            if(kingCanEscape(getKingCoord(state), "NORTH", state) || kingCanEscape(getKingCoord(state), "SOUTH", state) || kingCanEscape(getKingCoord(state), "WEST", state) || kingCanEscape(getKingCoord(state), "EAST", state)) {
                return 900;
            }


        }

        return 0;
    }




    private int[] getKingCoord(State state) {
        int[] kingCoord = new int[2];
        for (int i = 0; i < state.getBoard().length; i++) {
            for (int j = 0; j < state.getBoard().length; j++) {
                if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
                        || state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                    kingCoord[0] = i;
                    kingCoord[1] = j;
                }
            }
        }
        return kingCoord;
    }

    private boolean kingCanEscape(int[] kingCoord, String direction, State state) {

        int x = kingCoord[0];
        int y = kingCoord[1];

        switch (direction) {
            case "NORTH":
                if(onCitadels(new int[]{0,y})) {
                    System.out.println("Re non può scappare a NORD cittadella");
                    return false;
                }
                for(int i = x - 1; i >= 0; i--) {
                    if (!state.getPawn(i,y).equalsPawn("O")) {
                        System.out.println("Re non può scappare a NORD");
                        return false;
                    }
                }
                break;
            case "SOUTH":
                if(onCitadels(new int[]{8,y})) {
                    System.out.println("Re non può scappare a SUD cittadella");
                    return false;
                }
                for(int i = x + 1; i < 9; i++) {
                    if (!state.getPawn(i,y).equalsPawn("O")) {
                        System.out.println("Re non può scappare a SUD");
                        return false;
                    }
                }
                break;
            case "WEST":
                if(onCitadels(new int[]{x,0})) {
                    System.out.println("Re non può scappare a WEST cittadella");
                    return false;
                }
                for(int i = y - 1; i >= 0; i--) {
                    if (!state.getPawn(x,i).equalsPawn("O")) {
                        System.out.println("Re non può scappare a WEST");
                        return false;
                    }
                }
                break;
            case "EAST":
                if(onCitadels(new int[]{x,8})) {
                    System.out.println("Re non può scappare a EAST cittadella");
                    return false;
                }
                for(int i = y + 1; i < 9; i++) {
                    if (!state.getPawn(x,i).equalsPawn("O")) {
                        System.out.println("Re non può scappare a EAST");
                        return false;
                    }
                }
                break;
        }



        return true;
    }

    private boolean onCitadels(int[] coord) {
        List<int[]> citadels = Stream.of(new int[]{3,0}, new int[]{4,0}, new int[]{5,0}, new int[]{4,1},
                new int[]{7,3}, new int[]{7,4}, new int[]{7,5}, new int[]{6,4},
                new int[]{3,7}, new int[]{4,7}, new int[]{5,7}, new int[]{4,6},
                new int[]{0,3}, new int[]{0,4}, new int[]{0,5}, new int[]{1,4}).collect(Collectors.toList());
        return citadels.parallelStream().anyMatch(a -> Arrays.equals(a, coord));
    }

    private boolean kingOnEscapePoint(int[] coord) {
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
