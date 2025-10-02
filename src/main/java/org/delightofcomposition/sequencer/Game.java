package org.delightofcomposition.sequencer;

import java.util.ArrayList;

public class Game extends ArrayList<Board> {
    public int[][] grid;
    public int minSyntacticDistance, totalTriadCount;
    public double aveSyntacticDistance, aveParadigmaticDistance;
    public static final boolean SHOW_GRID = true;
    public Sequencer seq;

    public Game(Board initialBoard, Sequencer s) {
        seq = s;
        grid = new int[s.triadDictionary.length][s.TET];
        for (Triad t : initialBoard) {
            ArrayList<Integer> symmetries = t.getTypeSymmetries();
            for (int sym : symmetries)
                grid[t.type][(t.root + sym) % s.TET]++;
        }
        add(initialBoard);
        minSyntacticDistance = initialBoard.getMinSyntacticDistance();
        totalTriadCount = initialBoard.size();
        aveSyntacticDistance = initialBoard.getAveSyntacticDistance();
        aveParadigmaticDistance = 0;
    }

    public Game(Game g, Sequencer s) {
        seq = s;
        for (Board b : g)
            add(new Board(b));
        grid = new int[s.triadDictionary.length][s.TET];
        for (int i = 0; i < grid.length; i++)
            for (int n = 0; n < grid[i].length; n++)
                grid[i][n] = g.grid[i][n];
        minSyntacticDistance = g.minSyntacticDistance;
        aveSyntacticDistance = g.aveSyntacticDistance;
        totalTriadCount = g.totalTriadCount;
    }

    public Board getLastBoard() {
        return get(size() - 1);
    }

    public boolean notUsed(Triad t) {
        return grid[t.type][t.root] == 0;
    }

    public void printGame() {
        for (Board b : this)
            b.print();
    }

    // @Precondition b.size() > 0
    public void makeMove(Board b) {
        int transitionDistance = getLastBoard().getDistanceTo(b.get(0));
        aveSyntacticDistance = ((aveSyntacticDistance * (totalTriadCount - 1))
                + (b.getAveSyntacticDistance() * (b.size() - 1)) + transitionDistance)
                / (double) (totalTriadCount + b.size() - 1);
        // sum of distances divided by number of distances

        minSyntacticDistance = Math.min(minSyntacticDistance, transitionDistance);
        for (Triad t : b) {
            ArrayList<Integer> symmetries = t.getTypeSymmetries();
            for (int sym : symmetries)
                grid[t.type][(t.root + sym) % seq.TET]++;
            totalTriadCount++;
        }
        add(b);
        // System.out.println("MAKING MOVE: " + b);
        minSyntacticDistance = Math.min(minSyntacticDistance, b.getMinSyntacticDistance());
        aveParadigmaticDistance = 0;
    }

    public double getMinLocalAverage() {
        double min = Double.MAX_VALUE;
        for (Board b : this)
            min = Math.min(min, b.getAveSyntacticDistance());
        return min;
    }

    public double getAveParadigmaticDistance() {
        if (aveParadigmaticDistance > 0)
            return aveParadigmaticDistance;
        Board lastBoard = getLastBoard();
        double average = 0;
        double numberOfDistances = 0;
        for (int i = 0; i < lastBoard.size(); i++) {
            for (int n = 0; n < size() - 1; n++) {
                Board b = get(n);
                for (Triad t : b) {
                    numberOfDistances++;
                    average += lastBoard.get(i).findShortestPath(t);
                }
            }
            for (int n = i + 1; n < lastBoard.size() + i + 1; n++) {
                numberOfDistances++;
                average += lastBoard.get(i).findShortestPath(lastBoard.get(n % lastBoard.size()));
            }
        }
        average /= numberOfDistances;
        aveParadigmaticDistance = average;
        return aveParadigmaticDistance;
    }

    @Override
    public String toString() {
        String name = "GAME{\n";
        for (Board b : this)
            name += "       " + b + "\n";
        name += "} AVE_SYN_DIST = " + aveSyntacticDistance;
        if (SHOW_GRID) {
            name += "\nGRID:";
            for (int[] row : grid) {
                name += "\n       ";
                for (int i = 0; i < row.length; i++) {
                    name += row[i] + ",";
                }
            }
        }
        return name;
    }
}