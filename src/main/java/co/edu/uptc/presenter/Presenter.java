package co.edu.uptc.presenter;

import co.edu.uptc.model.MyGraphs;
import co.edu.uptc.view.MainView;

public class Presenter {
    private MyGraphs graph;
    private MainView view;

    public Presenter() {
        graph = new MyGraphs();
        view = new MainView(this);
        view.setVisible(true);
        view.showMap();
    }

    public MyGraphs getGraph() {
        return graph;
    }

    // Aquí puedes agregar métodos para que la vista consulte datos o notifique acciones
}
