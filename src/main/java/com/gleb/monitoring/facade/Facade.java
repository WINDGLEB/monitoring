package com.gleb.monitoring.facade;

import com.gleb.monitoring.dao.CarStateDao;
import com.gleb.monitoring.model.CarState;
import com.google.gwt.maps.client.overlays.Marker;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;


import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Locale;


@SpringUI
@Push
@Theme("valo")
@Widgetset("springvaadin.widgetset.WidgetSet")

public class Facade extends UI {

    private final CarStateDao carStateDao;
    private final Grid<CarState> grid;
    private final GoogleMap googleMap;
    private ListDataProvider<CarState> dataProvider;
    // private final TextField filterTextField;

    @Autowired
    public Facade(CarStateDao carStateDao) {
        this.carStateDao = carStateDao;
        this.googleMap = new GoogleMap("AIzaSyAD2-p2YRYAtw3hlH44e8JGXQDdqxG0d1k", null, "english");
        this.grid = new Grid<>(CarState.class);
        // this.filterTextField = new TextField();

    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        VerticalLayout content = new VerticalLayout();

        content.addComponent(new Label("TRUCK MANAGER"));
        googleMap.setCenter(new LatLon(59.9342802, 30.3350986));
        //filterTextFieldSetting();
        //content.addComponent(filterTextField);

        HorizontalLayout menuview = new HorizontalLayout();
        menuview.addComponents(grid);
        listCarStates();

        menuview.addComponent(googleMap);
        googleMapSetting();
        menuview.setSizeFull();


        content.addComponent(menuview);
        setContent(content);

        new FeederThread().start();
        new FeederThread2().start();
    }

    private ArrayList<GoogleMapMarker> getMarkers() {
        ArrayList<GoogleMapMarker> listMarkers = new ArrayList<>();
        for (int i = 0; i < carStateDao.findAll().size(); i++) {

            String caption = carStateDao.findAll().get(i).getLicensePlate();
            LatLon latlon = carStateDao.findAll().get(i).getGeolocation();
            listMarkers.add(new GoogleMapMarker(caption, latlon, false, "VAADIN/themes/icon.png"));

        }
        return listMarkers;
    }

    private void googleMapSetting() {

        googleMap.setSizeFull();
        // googleMap.setCenter(new LatLon(59.9342802, 30.3350986));
        googleMap.setHeight("400");

        ArrayList<GoogleMapMarker> listMarker = getMarkers();
        for (GoogleMapMarker marker : listMarker) {
            marker.setAnimationEnabled(false);
            googleMap.addMarker(marker);

        }
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);
    }

    private void listCarStates() {
        grid.setSizeFull();
        dataProvider = DataProvider.ofCollection(carStateDao.findAll());

        grid.setDataProvider(dataProvider);

        // grid.setItems(carStateDao.findAll());
    }

//    private void filterTextFieldSetting() {
//        filterTextField.setCaption("Filter by license plate:");
//        filterTextField.setPlaceholder("license plate");
//        dataProvider = DataProvider.ofCollection(carStateDao.findAll());
//        filterTextField.addValueChangeListener(event -> {
//            dataProvider.setFilter(CarState::getLicensePlate, lp -> {
//                String lpLower = lp == null ? ""
//                        : lp.toLowerCase(Locale.ENGLISH);
//                String filterLower = event.getValue()
//                        .toLowerCase(Locale.ENGLISH);
//                return lpLower.contains(filterLower);
//            });
//        });
//
//    }

    class FeederThread extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);
                    access(() -> {
                        listCarStates();
//                        googleMap.clearMarkers();
//                        googleMapSetting();
                    });

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class FeederThread2 extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(5000);
                    access(() -> {

                        googleMap.clearMarkers();
                        googleMapSetting();
                    });

                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
