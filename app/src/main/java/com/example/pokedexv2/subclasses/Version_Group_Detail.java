package com.example.pokedexv2.subclasses;

public class Version_Group_Detail {
// the version group detail class is defined by the data given by the pokeapi
    public Integer level_learned_at;
    public Move_Learn_Method move_learn_method;
    public Version_Group version_group;

    public Version_Group_Detail(Integer level_learned_at, Move_Learn_Method move_learn_method, Version_Group version_group){

        this.level_learned_at = level_learned_at;
        this.move_learn_method = move_learn_method;
        this.version_group = version_group;
    }
}
