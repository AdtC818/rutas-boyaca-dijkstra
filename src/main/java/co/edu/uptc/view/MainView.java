package co.edu.uptc.view;

import co.edu.uptc.presenter.Presenter;
import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame{

    JPanel contentPanel;
    MyMap myMap;
    Parameters parameters;
    Presenter presenter;

    public MainView(Presenter presenter){
        this.presenter = presenter;
        this.setSize(800, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Mapa Tunja");
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        setContentPane(contentPanel);
        myMap = new MyMap(presenter.getGraph());
        parameters = new Parameters();
    }

    public void showMap(){
        contentPanel.add(myMap, BorderLayout.CENTER);
        contentPanel.add(parameters, BorderLayout.SOUTH);
    }
}
