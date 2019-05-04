package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class TablutCristina extends TablutClient {

    private int game;


    public TablutCristina(String player, String name, int gameChosen) throws UnknownHostException, IOException {
        super(player, name);
        game = gameChosen;
    }


    public TablutCristina(String player) throws UnknownHostException, IOException {
        this(player, "random", 4);
    }

    public TablutCristina(String player, String name) throws UnknownHostException, IOException {
        this(player, name, 4);
    }

    public TablutCristina(String player, int gameChosen) throws UnknownHostException, IOException {
        this(player, "random", gameChosen);
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        int gametype = 4;
        String role = "";
        String name = "cristina_chiaBOT";
        // TODO: change the behavior?
        if (args.length < 1) {
            System.out.println("You must specify which player you are (WHITE or BLACK)");
            System.exit(-1);
        } else {
            System.out.println(args[0]);
            role = (args[0]);
        }
        if (args.length == 2) {
            System.out.println(args[1]);
            gametype = Integer.parseInt(args[1]);
        }
        if (args.length == 3) {
            name = args[2];
        }
        System.out.println("Selected client: " + args[0]);

        TablutCristina client = new TablutCristina(role, name, gametype);
        client.run();
    }

    @Override
    public void run() {

        try {
            this.declareName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        State state;

        Game rules = null;
        switch (this.game) {
            case 1:
                state = new StateTablut();
                rules = new GameTablut();
                break;
            case 2:
                state = new StateTablut();
                rules = new GameModernTablut();
                break;
            case 3:
                state = new StateBrandub();
                rules = new GameTablut();
                break;
            case 4:
                state = new StateTablut();
                state.setTurn(State.Turn.WHITE);
                rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
                System.out.println("Ashton Tablut game");
                break;
            default:
                System.out.println("Error in game selection");
                System.exit(4);
        }

        List<int[]> pawns = new ArrayList<int[]>();
        //List<int[]> empty = new ArrayList<int[]>();

        System.out.println("You are player " + this.getPlayer().toString() + "!");

        while (true) {
            try {
                this.read();
            } catch (ClassNotFoundException | IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                System.exit(1);
            }
            System.out.println("Current state:");
            state = this.getCurrentState();
            System.out.println(state.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            if (this.getPlayer().equals(Turn.WHITE)) {
                // � il mio turno
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
                    int[] buf;
                    for (int i = 0; i < state.getBoard().length; i++) {
                        for (int j = 0; j < state.getBoard().length; j++) {
                            if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
                                    || state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
                                buf = new int[2];
                                buf[0] = i;
                                buf[1] = j;
                                pawns.add(buf);
                            } /*else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
                                buf = new int[2];
                                buf[0] = i;
                                buf[1] = j;
                                empty.add(buf);
                            }*/
                        }
                    }

                    //int[] selected = null;


                    List<Action> actionList = getAllLegalMoves(state, pawns, Turn.WHITE);
                    Random rnd = new Random();
                    Action a = getBestAction(actionList, state);

                    /*boolean found = false;


                    try {
                        a = new Action("z0", "z0", State.Turn.WHITE);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    while (!found) {
                        if (pawns.size() > 1) {
                            selected = pawns.get(new Random().nextInt(pawns.size() - 1));
                        } else {
                            selected = pawns.get(0);
                        }

                        String from = this.getCurrentState().getBox(selected[0], selected[1]);

                        selected = empty.get(new Random().nextInt(empty.size() - 1));
                        String to = this.getCurrentState().getBox(selected[0], selected[1]);

                        try {
                            a = new Action(from, to, State.Turn.WHITE);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        System.out.println(actionList.size());
                        for(Action b : actionList) {
                            try {
                                System.out.println(state.getPawn(b.getRowFrom(),b.getColumnFrom()));
                                System.out.println(state.getBox(b.getRowFrom(), b.getColumnFrom()));
                                System.out.println("Action " + b.toString());
                                rules.checkMove(state, b);
                            } catch (Exception e) {

                            }
                        }
                        a = actionList.get(rnd.nextInt(actionList.size()));
                        found = true;
                        /*
                        try {
                            rules.checkMove(state, a);
                            found = true;
                        } catch (Exception e) {

                        }*//*

                    }*/

                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    pawns.clear();
                    //empty.clear();

                }
                // � il turno dell'avversario
                else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
                    System.out.println("Waiting for your opponent move... ");
                }
                // ho vinto
                else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                }
                // ho perso
                else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                }
                // pareggio
                else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            } else {

                // � il mio turno
                if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
                    int[] buf;
                    for (int i = 0; i < state.getBoard().length; i++) {
                        for (int j = 0; j < state.getBoard().length; j++) {
                            if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
                                buf = new int[2];
                                buf[0] = i;
                                buf[1] = j;
                                pawns.add(buf);
                            } /*else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
                                buf = new int[2];
                                buf[0] = i;
                                buf[1] = j;
                                empty.add(buf);
                            }*/
                        }
                    }

                    List<Action> actionList = getAllLegalMoves(state, pawns, Turn.BLACK);
                    Random rnd = new Random();
                    Action a = getBestAction(actionList, state);

                    /*

                    int[] selected = null;

                    boolean found = false;
                    Action a = null;
                    try {
                        a = new Action("z0", "z0", State.Turn.BLACK);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    ;
                    while (!found) {
                        selected = pawns.get(new Random().nextInt(pawns.size() - 1));
                        String from = this.getCurrentState().getBox(selected[0], selected[1]);

                        selected = empty.get(new Random().nextInt(empty.size() - 1));
                        String to = this.getCurrentState().getBox(selected[0], selected[1]);

                        try {
                            a = new Action(from, to, State.Turn.BLACK);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                        System.out.println("try: " + a.toString());
                        try {
                            rules.checkMove(state, a);
                            found = true;
                        } catch (Exception e) {

                        }

                    }*/

                    System.out.println("Mossa scelta: " + a.toString());
                    try {
                        this.write(a);
                    } catch (ClassNotFoundException | IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    pawns.clear();
                    //empty.clear();

                }

                else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
                    System.out.println("Waiting for your opponent move... ");
                } else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
                    System.out.println("YOU LOSE!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
                    System.out.println("YOU WIN!");
                    System.exit(0);
                } else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
                    System.out.println("DRAW!");
                    System.exit(0);
                }

            }
        }

    }

    private Action getBestAction(List<Action> actions, State state) {

        Evaluation eval = new Evaluation();
        TreeMap<Integer, Action> actionMap = new TreeMap<Integer, Action>();
        int i = 0;
        for(Action a : actions) {
            int value = eval.evaluateMock(a,state.getTurn(), state );
            actionMap.put(value + i, a);
            i++;
            if (value == 1000) {
                break;
            }
        }
        if(actionMap.descendingMap().firstEntry().getKey() < 500) {
            System.out.println("Mossa random");
            return actionMap.get(new Random().nextInt(actionMap.size()));
        }
        return actionMap.descendingMap().firstEntry().getValue();

    }


    private List<Action> getAllLegalMoves(State state, List<int[]> pawns, StateTablut.Turn turn) {
        List<Action> actions = new ArrayList<Action>();

        for(int[] coordPawn : pawns) {
            actions.addAll(getAllLegalMovesInDirection(state, coordPawn, "NORTH", turn));
            actions.addAll(getAllLegalMovesInDirection(state, coordPawn, "SOUTH", turn));
            actions.addAll(getAllLegalMovesInDirection(state, coordPawn, "WEST", turn));
            actions.addAll(getAllLegalMovesInDirection(state, coordPawn, "EAST", turn));
        }



        return actions;
    }


    private List<Action> getAllLegalMovesInDirection(State state, int[] coordPawn, String direction, StateTablut.Turn turn) {
        List<Action> actions = new ArrayList<Action>();

        int x = coordPawn[0];
        int y = coordPawn[1];



        switch (direction){
            case "EAST":
                for(int i = y + 1; i < 9; i++) {
                    //System.out.println("EAST x: " + x + " y: " + y + " i: " +i);
                    //System.out.println(state.getPawn(x,i) + " -- " + State.Pawn.EMPTY.toString() + " bool : " + state.getPawn(x,i).equalsPawn(State.Pawn.EMPTY.toString()));

                    if(state.getPawn(x,i).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{x,i})) {
                        //System.out.println("Aggiungo Mossa");
                        try {
                            //rules.checkMove(state,new Action(state.getBox(x, y), state.getBox(x, i), state.getTurn()));
                            actions.add(new Action(state.getBox(x, y), state.getBox(x, i), turn));
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //System.out.println("Break");
                        break;
                    }
                }
                break;
            case "WEST":
                for(int i = y - 1; i >= 0; i--) {
                    //System.out.println("WEST x: " + x + " y: " + y + " i: " +i);
                    //System.out.println(state.getPawn(x,i) + " -- " + State.Pawn.EMPTY.toString() + " bool : " + state.getPawn(x,i).equalsPawn(State.Pawn.EMPTY.toString()));
                    if(state.getPawn(x,i).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{x,i})) {
                        //System.out.println("Aggiungo Mossa");
                        try {
                            //rules.checkMove(state,new Action(state.getBox(x, y), state.getBox(x, i), state.getTurn()));
                            actions.add(new Action(state.getBox(x, y), state.getBox(x, i), turn));
                        } catch( Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //System.out.println("Break");
                        break;
                    }
                }
                break;
            case "NORTH":
                for(int i = x - 1; i >= 0; i--) {
                    //System.out.println("NORTH x: " + x + " y: " + y + " i: " +i);
                    //System.out.println(state.getPawn(i,y) + " -- " + State.Pawn.EMPTY.toString() + " bool : " + state.getPawn(i,y).equalsPawn(State.Pawn.EMPTY.toString()));
                    if(state.getPawn(i,y).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{i,y})) {
                        //System.out.println("Aggiungo Mossa");
                        try {
                            //rules.checkMove(state,new Action(state.getBox(x, y), state.getBox(i, y), state.getTurn()));
                            actions.add(new Action(state.getBox(x, y), state.getBox(i, y), turn));
                        } catch( Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //System.out.println("Break");
                        break;
                    }
                }
                break;
            case "SOUTH":
                for(int i = x + 1; i < 9; i++) {
                    //System.out.println("SOUTH x: " + x + " y: " + y + " i: " +i);
                    //System.out.println(state.getPawn(i,y) + " -- " + State.Pawn.EMPTY.toString() + " bool : " + state.getPawn(i,y).equalsPawn(State.Pawn.EMPTY.toString()));
                    if(state.getPawn(i, y).equalsPawn(State.Pawn.EMPTY.toString()) && !onCitadels(new int[]{i,y})) {
                        //System.out.println("Aggiungo Mossa");
                        try {
                            //rules.checkMove(state,new Action(state.getBox(x, y), state.getBox(i, y), state.getTurn()));
                            actions.add(new Action(state.getBox(x, y), state.getBox(i, y), turn));
                        } catch( Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //System.out.println("Break");
                        break;
                    }
                }
                break;

        }


        return actions;
    }

    private boolean onCitadels(int[] coord) {
        List<int[]> citadels = Stream.of(new int[]{3,0}, new int[]{4,0}, new int[]{5,0}, new int[]{4,1},
                new int[]{7,3}, new int[]{7,4}, new int[]{7,5}, new int[]{6,4},
                new int[]{3,7}, new int[]{4,7}, new int[]{5,7}, new int[]{4,6},
                new int[]{0,3}, new int[]{0,4}, new int[]{0,5}, new int[]{1,4}).collect(Collectors.toList());
        return citadels.parallelStream().anyMatch(a -> Arrays.equals(a, coord));
    }

    private static boolean isInList(
            final List<int[]> list, final int[] candidate) {

        return list.parallelStream().anyMatch(a -> Arrays.equals(a, candidate));
        //  ^-- or you may want to use .parallelStream() here instead
    }
}
