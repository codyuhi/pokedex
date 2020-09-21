package com.example.pokedexv2.subclasses;

public class FullListPoke {
// the fulllistpoke class is defined by the data given by the pokeapi
    public String name;
    public String url;
    public int id;

    public FullListPoke(
            String name,
            String url,
            int id
    ){
        this.name = name;
        this.url = url;
        this.id = id;
    }
}
