package org.example.divar.util;

import javafx.util.StringConverter;
import org.example.divar.model.City;

public class CityFormatter {

    public static StringConverter<City> createStringConverter() {
        return new StringConverter<City>() {
            @Override
            public String toString(City cityObject) {
                if (cityObject == null) {
                    return "";
                } else {
                    return cityObject.getName();
                }
            }

            @Override
            public City fromString(String string) {
                return null;
            }
        };
    }
}