package calculate;

import javafx.application.Platform;
import javafx.concurrent.Task;
import timeutil.TimeStamp;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

public class GenerateEdges extends Task implements Runnable, Observer {
    private KochFractal koch;
    private String name;
    private CountDownLatch latch;
    private KochManager manager;
    private ArrayList<Edge> edges = new ArrayList<Edge>();
    private int maxEdges;
    private TimeStamp tsCalc = new TimeStamp();

    public GenerateEdges(String name, CountDownLatch latch, KochManager manager, int nxt) {
        this.koch = new KochFractal(this);
        koch.setLevel(nxt);
        koch.addObserver(this);
        koch.setLevel(nxt);
        this.name = name;
        this.latch = latch;
        this.manager = manager;
    }

    public synchronized void addEdge(Edge e) {
        edges.add(e);
    }

    @Override
    public Void call() {
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        maxEdges = koch.getNrOfEdges() / 3;

        if (name == "left") {
            koch.generateLeftEdge();
        } else if (name == "right") {
            koch.generateRightEdge();
        } else if (name == "bottom") {
            koch.generateBottomEdge();
        }
        manager.addAllEdges(edges);

        Platform.runLater(() -> {
            manager.getFractalFX().setTextNrEdges("" + manager.getEdges().size());
            tsCalc.setEnd("End calculating");
            manager.getFractalFX().setTextCalc(tsCalc.toString());
        });

        manager.getFractalFX().requestDrawEdges();

        latch.countDown();
        return null;
    }

    @Override
    public void update(Observable o, Object edge) {
        addEdge((Edge) edge);

        updateProgress(edges.size(), maxEdges);

        updateMessage("Nr of edges: " + edges.size());
    }
}
