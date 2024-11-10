package com.edigest.journalapp.responseDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class WeatherResponseDto {
    private Request request;
    private Location location;
    private Current current;


    @Getter
    @Setter
    public static class Current {
        private int temperature;
        @JsonProperty("feelslike")
        private int feelsLike;
        private int visibility;
        @JsonProperty("is_day")
        private String isDay;
    }

    @Getter
    @Setter
    public static class Location {
        private String name;
        private String country;
    }

    @Getter
    @Setter
    public static class Request {
        private  String type;
        private String query;
        private String language;
        private String unit;
    }

}




