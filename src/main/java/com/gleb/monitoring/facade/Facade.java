package com.gleb.monitoring.facade;

import com.gleb.monitoring.dao.CarStateDao;
import com.gleb.monitoring.model.CarState;
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
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

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
    private final TextField filterTextField;

    @Autowired
    public Facade(CarStateDao carStateDao) {
        this.carStateDao = carStateDao;
        this.googleMap = new GoogleMap("AIzaSyAD2-p2YRYAtw3hlH44e8JGXQDdqxG0d1k", null, "english");
        this.grid = new Grid<>(CarState.class);
        this.filterTextField = new TextField();

    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        VerticalLayout content = new VerticalLayout();

        content.addComponent(new Label("TRUCK MANAGER"));

        filterTextFieldSetting();
        content.addComponent(filterTextField);

        HorizontalLayout menuview = new HorizontalLayout();
        menuview.addComponents(grid);
        listCarStates();

        menuview.addComponent(googleMap);
        googleMapSetting();
        menuview.setSizeFull();

        new FeederThread().start();
        content.addComponent(menuview);
        setContent(content);


    }

    private void googleMapSetting() {

        googleMap.setSizeFull();
        googleMap.setHeight("400");
        googleMap.addMarker("NOT DRAGGABLE: Iso-Heikkilä", new LatLon(
                59.994159, 30.3181678), false, null);
        googleMap.setMinZoom(4);
        googleMap.setMaxZoom(16);
    }

    private void listCarStates() {
        grid.setSizeFull();
        dataProvider = DataProvider.ofCollection(carStateDao.findAll());

        grid.setDataProvider(dataProvider);

        // grid.setItems(carStateDao.findAll());
    }

    private void filterTextFieldSetting() {
        filterTextField.setCaption("Filter by license plate:");
        filterTextField.setPlaceholder("license plate");
        dataProvider = DataProvider.ofCollection(carStateDao.findAll());
        filterTextField.addValueChangeListener(event -> {
            dataProvider.setFilter(CarState::getLicensePlate, lp -> {
                String lpLower = lp == null ? ""
                        : lp.toLowerCase(Locale.ENGLISH);
                String filterLower = event.getValue()
                        .toLowerCase(Locale.ENGLISH);
                return lpLower.contains(filterLower);
            });
        });

    }

    class FeederThread extends Thread {


        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);

                    access(() -> listCarStates());
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
