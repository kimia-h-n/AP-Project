package org.example.divar.util;

import javafx.util.StringConverter;
import org.example.divar.model.City;

/**
 * Utility class providing a JavaFX StringConverter for City objects to display their names in UI controls.
 */
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