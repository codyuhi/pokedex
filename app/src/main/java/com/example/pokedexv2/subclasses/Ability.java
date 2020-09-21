package com.example.pokedexv2.subclasses;

public class Ability {
// the ability class is defined by the data given by the pokeapi
    public String name;
    public String url;
    public boolean is_hidden;
    public Integer slot;

    public Ability(String name, String url, boolean is_hidden, Integer slot){
        this.name = name;
        this.url = url;
        this.is_hidden = is_hidden;
        this.slot = slot;
    }
}
