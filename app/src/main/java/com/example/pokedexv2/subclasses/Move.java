package com.example.pokedexv2.subclasses;

public class Move {
// the move class is defined by the data given by the pokeapi
    public Mmove mmove;
    public Version_Group_Detail[] version_group_details;

    public Move(Mmove mmove, Version_Group_Detail[] version_group_details){
        this.mmove = mmove;
        this.version_group_details = version_group_details;
    }
}
