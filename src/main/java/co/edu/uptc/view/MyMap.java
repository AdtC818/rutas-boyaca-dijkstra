package co.edu.uptc.view;

import co.edu.uptc.model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.Desktop;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MyMap extends JPanel {
    private MyGraphs graph;
    private Map<String, Point> cityPositions;
    private String selectedOrigin = null;
    private String selectedDest = null;
    private List<City> shortestPath = null;
    private BufferedImage backgroundImg;
    private Map<String, BufferedImage> cityImages;
    private double zoom = 1.0;
    private String hoveredCity = null;
    private int panX = 0, panY = 0;
    private int lastMouseX = 0, lastMouseY = 0;
    private boolean dragging = false;

    public MyMap(MyGraphs graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(600, 600));
        // Cargar imagen de fondo
        try {
            backgroundImg = ImageIO.read(new File("src/main/resources/images/mapaFondo.jpg"));
        } catch (IOException e) {
            backgroundImg = null;
        }
        cityPositions = new HashMap<>();
        // Posiciones de ejemplo para cada ciudad (ajusta según prefieras)
        cityPositions.put("Tunja", new Point(400, 300));
        cityPositions.put("Combita", new Point(400, 80));
        cityPositions.put("Oicata", new Point(500, 150));
        cityPositions.put("Chivata", new Point(600, 300));
        cityPositions.put("Soraca", new Point(550, 400));
        cityPositions.put("Samaca", new Point(200, 500));
        cityPositions.put("Sora", new Point(180, 280));
        cityPositions.put("Motavita", new Point(320, 220));

        cityImages = new HashMap<>();
        // Cargar imágenes de las ciudades usando el classpath
        for (String city : cityPositions.keySet()) {
            try {
                String imgPath = "/images/" + city + (city.equals("Tunja") || city.equals("Combita") || city.equals("Motavita") || city.equals("Samaca") ? ".png" : ".jpg");
                cityImages.put(city, ImageIO.read(getClass().getResourceAsStream(imgPath)));
            } catch (Exception e) {
                cityImages.put(city, null);
            }
        }

        // Listener para zoom con la rueda del mouse
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getPreciseWheelRotation() < 0) {
                    zoom = Math.min(2.5, zoom + 0.1);
                } else {
                    zoom = Math.max(0.5, zoom - 0.1);
                }
                repaint();
            }
        });
        // Listener para arrastrar el mapa con el mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragging = true;
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    panX += e.getX() - lastMouseX;
                    panY += e.getY() - lastMouseY;
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    repaint();
                }
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredCity = null;
                Point mouse = getUnzoomedPoint(e.getX(), e.getY());
                for (Map.Entry<String, Point> entry : cityPositions.entrySet()) {
                    Point p = entry.getValue();
                    int dx = mouse.x - p.x;
                    int dy = mouse.y - p.y;
                    if (dx * dx + dy * dy <= 20 * 20) {
                        hoveredCity = entry.getKey();
                        break;
                    }
                }
                repaint();
            }
        });
        // Listener para abrir Google Maps al hacer clic sobre una ciudad con zoom alto
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point mouse = getUnzoomedPoint(e.getX(), e.getY());
                if (zoom > 1.5 && hoveredCity != null) {
                    try {
                        String url = "https://www.google.com/maps/search/" + hoveredCity.replace(" ", "+");
                        Desktop.getDesktop().browse(new URI(url));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "No se pudo abrir Google Maps");
                    }
                } else {
                    for (Map.Entry<String, Point> entry : cityPositions.entrySet()) {
                        Point p = entry.getValue();
                        int dx = mouse.x - p.x;
                        int dy = mouse.y - p.y;
                        if (dx * dx + dy * dy <= 20 * 20) { // Clic dentro del nodo
                            if (selectedOrigin == null || (selectedOrigin != null && selectedDest != null)) {
                                selectedOrigin = entry.getKey();
                                selectedDest = null;
                                shortestPath = null;
                            } else if (selectedDest == null && !entry.getKey().equals(selectedOrigin)) {
                                selectedDest = entry.getKey();
                                shortestPath = graph.dijkstra(selectedOrigin, selectedDest);
                            }
                            repaint();
                            break;
                        }
                    }
                }
            }
        });
    }

    private Point getZoomedPoint(Point p) {
        int cx = getWidth() / 2 + panX;
        int cy = getHeight() / 2 + panY;
        int x = (int) ((p.x - cx) * zoom + cx);
        int y = (int) ((p.y - cy) * zoom + cy);
        return new Point(x, y);
    }
    private Point getUnzoomedPoint(int x, int y) {
        int cx = getWidth() / 2 + panX;
        int cy = getHeight() / 2 + panY;
        int px = (int) ((x - cx) / zoom + cx);
        int py = (int) ((y - cy) / zoom + cy);
        return new Point(px, py);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Dibujar fondo
        if (backgroundImg != null) {
            g2.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        }
        // Centrar, escalar y trasladar el zoom y el paneo
        int cx = getWidth() / 2 + panX;
        int cy = getHeight() / 2 + panY;
        g2.translate(cx, cy);
        g2.scale(zoom, zoom);
        g2.translate(-cx, -cy);
        g2.setStroke(new BasicStroke(2));
        // Dibujar caminos
        for (City city : graph.getCities().values()) {
            Point p1 = cityPositions.get(city.name);
            for (PathForCities path : city.getCaminos()) {
                Point p2 = cityPositions.get(path.getTo().name);
                // Evitar dibujar dos veces la misma arista (solo dibujar si nombre origen < nombre destino)
                if (p1 != null && p2 != null && city.name.compareTo(path.getTo().name) < 0) {
                    // Resaltar si es parte del camino más corto
                    boolean highlight = false;
                    if (shortestPath != null && shortestPath.size() > 1) {
                        for (int i = 0; i < shortestPath.size() - 1; i++) {
                            String from = shortestPath.get(i).name;
                            String to = shortestPath.get(i+1).name;
                            if ((from.equals(city.name) && to.equals(path.getTo().name)) ||
                                (from.equals(path.getTo().name) && to.equals(city.name))) {
                                highlight = true;
                                break;
                            }
                        }
                    }
                    g2.setColor(highlight ? Color.RED : Color.LIGHT_GRAY);
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    // Dibujar la distancia
                    int mx = (p1.x + p2.x) / 2;
                    int my = (p1.y + p2.y) / 2;
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawString(String.format("%.1f km", path.getDistance()), mx, my);
                }
            }
        }
        // Dibujar ciudades
        for (Map.Entry<String, Point> entry : cityPositions.entrySet()) {
            String name = entry.getKey();
            Point p = entry.getValue();
            BufferedImage img = cityImages.get(name);
            Point zp = getZoomedPoint(p);
            if (img != null) {
                g2.drawImage(img, p.x - 20, p.y - 20, 40, 40, null);
                // Dibujar borde verde o rojo si es origen o destino
                if (name.equals(selectedOrigin)) {
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(4));
                    g2.drawOval(p.x - 22, p.y - 22, 44, 44);
                } else if (name.equals(selectedDest)) {
                    g2.setColor(Color.RED);
                    g2.setStroke(new BasicStroke(4));
                    g2.drawOval(p.x - 22, p.y - 22, 44, 44);
                }
                g2.setStroke(new BasicStroke(2)); // Restaurar grosor
            } else {
                if (name.equals(selectedOrigin)) {
                    g2.setColor(Color.GREEN);
                } else if (name.equals(selectedDest)) {
                    g2.setColor(Color.RED);
                } else {
                    g2.setColor(Color.BLUE);
                }
                g2.fillOval(p.x - 15, p.y - 15, 30, 30);
            }
            // Dibujar el nombre debajo del nodo
            g2.setColor(name.equals(hoveredCity) ? Color.MAGENTA : Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            int textWidth = g2.getFontMetrics().stringWidth(name);
            g2.drawString(name, p.x - textWidth / 2, p.y + 30);
        }
        // Si hay ciudad bajo el mouse y zoom alto, mostrar tooltip
        if (hoveredCity != null && zoom > 1.5) {
            g2.setColor(new Color(255,255,255,200));
            g2.fillRoundRect(10, 10, 260, 40, 10, 10);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString("Haz clic para abrir Google Maps de " + hoveredCity, 20, 35);
        }
    }
}
