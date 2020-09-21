package com.example.pokedexv2.subclasses;

public class Sprites {
// the sprites class is defined by the data given by the pokeapi
    public String back_default;
    public String back_female;
    public String back_shiny;
    public String back_shiny_female;
    public String front_default;
    public String front_female;
    public String front_shiny;
    public String front_shiny_female;

    public Sprites(
            String back_default,
            String back_female,
            String back_shiny,
            String back_shiny_female,
            String front_default,
            String front_female,
            String front_shiny,
            String front_shiny_female){
        this.back_default = back_default;
        this.back_female = back_female;
        this.back_shiny = back_shiny;
        this.back_shiny_female = back_shiny_female;
        this.front_default = front_default;
        this.front_female = front_female;
        this.front_shiny = front_shiny;
        this.front_shiny_female = front_shiny_female;
    }
}
