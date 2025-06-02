package co.edu.uptc.model;

import java.util.ArrayList;
import java.util.List;

public class City {
    public String name;
    public String imagePath;
    public List<PathForCities> caminos;

    public City(String name, String imagePath) {
        this.name = name;
        this.imagePath = imagePath;
        this.caminos = new ArrayList<>();
    }

    public void addCamino(PathForCities camino) {
        this.caminos.add(camino);
    }

    public List<PathForCities> getCaminos() {
        return caminos;
    }
}
