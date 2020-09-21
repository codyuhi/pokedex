package com.example.pokedexv2;

// Import the necessary classes to get the app to work
import com.example.pokedexv2.subclasses.Ability;
import com.example.pokedexv2.subclasses.Form;
import com.example.pokedexv2.subclasses.Game_Index;
import com.example.pokedexv2.subclasses.Held_Item;
import com.example.pokedexv2.subclasses.Move;
import com.example.pokedexv2.subclasses.Species;
import com.example.pokedexv2.subclasses.Sprites;
import com.example.pokedexv2.subclasses.Stat;
import com.example.pokedexv2.subclasses.Type;

import java.io.Serializable;

public class Poke implements Serializable {
// all of these attributes are available in the json provided from the pokeapi
    public com.example.pokedexv2.subclasses.Ability[] abilities;
    public Integer base_experience;
    public com.example.pokedexv2.subclasses.Form[] forms;
    public com.example.pokedexv2.subclasses.Game_Index[] game_indices;
    public Integer height;
    public com.example.pokedexv2.subclasses.Held_Item[] held_items;
    public Integer id;
    public boolean is_default;
    public String location_area_encounters;
    public com.example.pokedexv2.subclasses.Move[] moves;
    public String name;
    public Integer order;
    public com.example.pokedexv2.subclasses.Species species;
    public com.example.pokedexv2.subclasses.Sprites sprites;
    public com.example.pokedexv2.subclasses.Stat[] stats;
    public com.example.pokedexv2.subclasses.Type[] types;
    public Integer weight;

    public Poke(
            Ability[] abilities,
            Integer base_experience,
            Form[] forms,
            Game_Index[] game_indices,
            Integer height,
            Held_Item[] held_items,
            Integer id,
            boolean is_default,
            String location_area_encounters,
            Move[] moves,
            String name,
            Integer order,
            Species species,
            Sprites sprites,
            Stat[] stats,
            Type[] types,
            Integer weight
    ){
        this.abilities = abilities;
        this.base_experience = base_experience;
        this.forms = forms;
        this.game_indices = game_indices;
        this.height = height;
        this.held_items = held_items;
        this.id = id;
        this.is_default = is_default;
        this.location_area_encounters = location_area_encounters;
        this.moves = moves;
        this.name = name;
        this.order = order;
        this.species = species;
        this.stats = stats;
        this.sprites = sprites;
        this.types = types;
        this.weight = weight;
    }
}
