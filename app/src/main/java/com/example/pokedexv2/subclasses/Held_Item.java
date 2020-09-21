package com.example.pokedexv2.subclasses;

public class Held_Item {
// the held item class is defined by the data given by the pokeapi
    public Item item;
    public Version_Detail[] version_details;

    public Held_Item(Item item, Version_Detail[] version_details){
        this.item = item;
        this.version_details = version_details;
    }
}
