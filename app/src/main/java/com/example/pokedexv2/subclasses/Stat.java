package com.example.pokedexv2.subclasses;

public class Stat {
// the stat class is defined by the data given by the pokeapi
    public Integer base_stat;
    public Integer effort;
    public Sstat sstat;

    public Stat(Integer base_stat, Integer effort, Sstat sstat){
        this.base_stat = base_stat;
        this.effort = effort;
        this.sstat = sstat;
    }
}
