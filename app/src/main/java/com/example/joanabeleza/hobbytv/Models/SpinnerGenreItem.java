package com.example.joanabeleza.hobbytv.Models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Project HobbyTV refactored by joanabeleza on 14/02/2018.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "name"
})
public class SpinnerGenreItem {
    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;

    @JsonIgnore
    private boolean selected;

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonAnyGetter
    public boolean isSelected() {
        return selected;
    }

    @JsonAnySetter
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
