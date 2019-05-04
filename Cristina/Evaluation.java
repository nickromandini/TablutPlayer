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


    public int evaluateMock(Action a, State state) {
        if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
            if (state.getPawn(a.getRowFrom(), a.getColumnFrom()).equalsPawn("K")) {

                if (state.kingOnEscapePoint(new int[]{a.getRowTo(), a.getColumnTo()}))
                    return 1000;

                if (state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "NORTH") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "SOUTH") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "WEST") || state.kingCanEscape(new int[]{a.getRowTo(), a.getColumnTo()}, "EAST")) {
                    return 900;
                }
                return 500;


            } else {

            }
        }
        return 0;
    }

    public int evaluate(State state) {

        if(state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {



        } else if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {

                if (state.kingOnEscapePoint())
                    return 1000;

                if (state.kingCanEscape( "NORTH") || state.kingCanEscape("SOUTH") || state.kingCanEscape("WEST") || state.kingCanEscape("EAST")) {
                    return 900;
                }



        }

        return 0;
    }



}
