package com.example.pokedexv2.subclasses;

public class Game_Index {
// the game index class is defined by the data given by the pokeapi
    public Integer game_index;
    public Version version;

    public Game_Index(Integer game_index, Version version){
        this.game_index = game_index;
        this.version = version;
    }
}
