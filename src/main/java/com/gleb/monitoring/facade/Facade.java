package com.gleb.monitoring.facade;

import com.gleb.monitoring.model.CarState;
import com.gleb.monitoring.service.CarStateService;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;

import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.vaadin.data.provider.DataProvider.ofCollection;

@SpringUI
@Push
@Theme("valo")
@Widgetset("springvaadin.widgetset.WidgetSet")

public class Facade extends UI {
    private static final String ICON_URI = "VAADIN/themes/icon.png";
    private static final String MAP_API_KEY = "AIzaSyAD2-p2YRYAtw3hlH44e8JGXQDdqxG0d1k";
    private static final String MAP_LANGUAGE = "english";

    private final CarStateService carStateService;
    private final Grid<CarState> grid;
    private final GoogleMap googleMap;
    private ListDataProvider<CarState> dataProvider;
    private final TextField filterTextField;

    @Autowired
    public Facade(CarStateService carStateService) {
        this.carStateService = carStateService;
        this.googleMap = new GoogleMap(MAP_API_KEY, null, MAP_LANGUAGE);
        this.grid = new Grid<>(CarState.class);
        this.filterTextField = new TextField();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        VerticalLayout mainComponent = new VerticalLayout();
        mainComponent.addComponent(new Label("MONITORING OF TRUCKS"));

        HorizontalLayout infoComponent = new HorizontalLayout();
        infoComponent.setSizeFull();
        configureGrid();
        addStatesToGrid();
        infoComponent.addComponents(grid);

        googleMap.setCenter(new LatLon(59.932393, 30.3596189));
        configureMap();
        addMarkersToMap();
        infoComponent.addComponent(googleMap);
        mainComponent.addComponent(infoComponent);
        setContent(mainComponent);

        new GridWorker().start();
        new MapWorker().start();
    }

    private void configureMap() {
        googleMap.setSizeFull();
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);
        googleMap.setHeight("400");
    }

    private void addMarkersToMap() {
        googleMap.clearMarkers();
        for (GoogleMapMarker marker : generateMarkers()) {
            googleMap.addMarker(marker);
        }
    }

    private List<GoogleMapMarker> generateMarkers() {
        ArrayList<GoogleMapMarker> markers = new ArrayList<>();
        List<CarState> states = carStateService.findLatestCarState();
        for (CarState state : states) {
            GoogleMapMarker marker = new GoogleMapMarker(state.getLicensePlate(), state.getGeolocation(), false, ICON_URI);
            marker.setAnimationEnabled(false);
            markers.add(marker);
        }
        return markers;
    }

    private void configureGrid() {
        grid.setSizeFull();
    }

    private void addStatesToGrid() {
        grid.setDataProvider(ofCollection(carStateService.findLatestCarState()));
    }

    private DataProvider<CarState, ?> filterCollection() {
        filterTextField.setCaption("Filter by license plate:");
        filterTextField.setPlaceholder("license plate");
        dataProvider = ofCollection(carStateService.findLatestCarState());
        filterTextField.addValueChangeListener(event -> {
            dataProvider.setFilter(CarState::getLicensePlate, lp -> {
                String lpLower = lp == null ? ""
                        : lp.toLowerCase(Locale.ENGLISH);
                String filterLower = event.getValue()
                        .toLowerCase(Locale.ENGLISH);
                return lpLower.contains(filterLower);
            });
        });
        return dataProvider;
    }

    class GridWorker extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    sleep(1000);
                    access(Facade.this::addStatesToGrid);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class MapWorker extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    sleep(5000);
                    access(Facade.this::addMarkersToMap);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
