package it.unibo.ai.didattica.competition.tablut.util;

import it.unibo.ai.didattica.competition.tablut.domain.Action;

import java.util.concurrent.atomic.AtomicInteger;

public final class Result {

    private int value;
    private Action action;
    private int ai;

    public Result(int value, Action action, int ai) {
        this.value = value;
        this.action = action;
        this.ai = ai;
    }

    public int getValue() {
        return value;
    }

    public Action getAction() {
        return action;
    }

    public int getAi() { return ai; }

}
