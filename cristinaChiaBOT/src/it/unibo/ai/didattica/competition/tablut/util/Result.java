package it.unibo.ai.didattica.competition.tablut.util;

import it.unibo.ai.didattica.competition.tablut.domain.Action;


public final class Result {

    private int value;
    private Action action;

    public Result(int value, Action action) {
        this.value = value;
        this.action = action;
    }

    public int getValue() {
        return value;
    }

    public Action getAction() {
        return action;
    }


}
