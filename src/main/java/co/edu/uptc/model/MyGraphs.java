package co.edu.uptc.model;

import java.util.*;

public class MyGraphs {

    private Map<String, City> cities;

    public MyGraphs() {
        cities = new HashMap<>();
        loadGraph();
    }

    private void loadGraph() {
        // Cargar nodos con imágenes de ejemplo (puedes cambiar las rutas luego)
        addCity("Tunja", "/images/tunja.jpg");
        addCity("Combita", "/images/combita.jpg");
        addCity("Oicata", "/images/oicata.jpg");
        addCity("Chivata", "/images/chivata.jpg");
        addCity("Soraca", "/images/soraca.jpg");
        addCity("Samaca", "/images/samaca.jpg");
        addCity("Sora", "/images/sora.jpg");
        addCity("Motavita", "/images/motavita.jpg");

        // Cargar aristas (unidireccionales)
        addPath("Tunja", "Combita", 19);
        addPath("Tunja", "Oicata", 11.9);
        addPath("Tunja", "Chivata", 11.8);
        addPath("Tunja", "Soraca", 8.5);
        addPath("Tunja", "Samaca", 30.2);
        addPath("Tunja", "Sora", 14.4);
        addPath("Tunja", "Motavita", 4.7);
        addPath("Combita", "Oicata", 8.7);
        addPath("Oicata", "Chivata", 5.8);
        addPath("Chivata", "Soraca", 10.7);
        addPath("Soraca", "Samaca", 29.9);
        addPath("Samaca", "Sora", 13.6);
        addPath("Sora", "Motavita", 17.8);
        addPath("Motavita", "Combita", 13.4);
    }

    public void addCity(String name, String imagePath) {
        City city = new City(name, imagePath);
        cities.put(name, city);
    }

    public void addPath(String from, String to, double distance) {
        City cFrom = cities.get(from);
        City cTo = cities.get(to);
        if (cFrom != null && cTo != null) {
            cFrom.addCamino(new PathForCities(cTo, distance));
            cTo.addCamino(new PathForCities(cFrom, distance)); // Agrega el camino inverso
        }
    }

    public Map<String, City> getCities() {
        return cities;
    }

    public List<City> dijkstra(String origen, String destino) {
        City start = cities.get(origen);
        City end = cities.get(destino);
        if (start == null || end == null) return Collections.emptyList();

        Map<City, Double> dist = new HashMap<>();
        Map<City, City> prev = new HashMap<>();
        Set<City> visited = new HashSet<>();
        PriorityQueue<City> queue = new PriorityQueue<>(Comparator.comparingDouble(dist::get));

        for (City city : cities.values()) {
            dist.put(city, Double.POSITIVE_INFINITY);
            prev.put(city, null);
        }
        dist.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            City current = queue.poll();
            if (current.equals(end)) break;
            if (!visited.add(current)) continue;
            for (PathForCities edge : current.getCaminos()) {
                City neighbor = edge.getTo();
                double alt = dist.get(current) + edge.getDistance();
                if (alt < dist.get(neighbor)) {
                    dist.put(neighbor, alt);
                    prev.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // Reconstruir el camino
        List<City> path = new ArrayList<>();
        for (City at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        if (path.size() == 1 && !path.get(0).equals(start)) return Collections.emptyList();
        return path;
    }
}
