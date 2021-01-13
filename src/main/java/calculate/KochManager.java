package calculate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import fun3kochfractalfx.FUN3KochFractalFX;
import timeutil.TimeStamp;


public class KochManager{

    private ArrayList<Edge> edges;
    private FUN3KochFractalFX application;
    private TimeStamp tsCalc;
    private TimeStamp tsDraw;
    public ExecutorService pool;
    public  List<Future<?>> futures;

    public KochManager(FUN3KochFractalFX application) {
        this.edges = new ArrayList<Edge>();
        this.application = application;
        this.tsCalc = new TimeStamp();
        this.tsDraw = new TimeStamp();
    }


    public void changeLevel(int nxt) {
        edges.clear();
        tsCalc.init();
        tsCalc.setBegin("Begin calculating");

        CountDownLatch latch = new CountDownLatch(3);
        edges.clear();


        GenerateEdges leftTask = new GenerateEdges("left", latch, this, nxt);
        GenerateEdges rightTask = new GenerateEdges("right", latch, this, nxt);
        GenerateEdges bottomTask = new GenerateEdges("bottom", latch, this, nxt);

        getFractalFX().getProgressBarLeft().progressProperty().bind(leftTask.progressProperty());
        getFractalFX().getProgressBarBottom().progressProperty().bind(bottomTask.progressProperty());
        getFractalFX().getProgressBarRight().progressProperty().bind(rightTask.progressProperty());


        futures = new ArrayList<Future<?>>();

        pool = Executors.newFixedThreadPool(3);
        Future<?> future = pool.submit(leftTask);
        futures.add(future);
        future = pool.submit(rightTask);
        futures.add(future);
        future = pool.submit(bottomTask);
        futures.add(future);
        pool.shutdown();
    }


    public synchronized void drawEdges() {
        tsDraw.init();
        tsDraw.setBegin("Begin drawing");
        application.clearKochPanel();
        for (Edge e : edges) {
            application.drawEdge(e);
        }
        tsDraw.setEnd("End drawing");
        application.setTextDraw(tsDraw.toString());
    }

    FUN3KochFractalFX getFractalFX() {
        return application;
    }

    ArrayList<Edge> getEdges() {
        return edges;
    }

    synchronized void addAllEdges(ArrayList<Edge> edges) {
        this.edges.addAll(edges);
    }
}
