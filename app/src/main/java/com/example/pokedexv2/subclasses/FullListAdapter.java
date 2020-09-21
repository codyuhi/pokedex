package com.example.pokedexv2.subclasses;

// Import the necessary libraries/modules for the app to work
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedexv2.DisplayResult;
import com.example.pokedexv2.MainActivity;
import com.example.pokedexv2.R;

import java.util.ArrayList;

public class FullListAdapter extends RecyclerView.Adapter<FullListAdapter.FullListViewHolder>{

// create a arraylist for all the fulllist pokes
    ArrayList<FullListPoke> flpokes;
    public FullListAdapter(ArrayList<FullListPoke> flpokes){
        this.flpokes = flpokes;
    }

    @Override
    public FullListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
// initialize the context
        Context context = parent.getContext();
// create the itemview
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.full_list_item, parent, false);

        return new FullListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FullListViewHolder holder, int position) {
// bind the viewholder for the fulllistpoke
        FullListPoke fullListPoke = flpokes.get(position);
        holder.bind(fullListPoke);
    }

    @Override
    public int getItemCount() {
        return flpokes.size();
    }

    public class FullListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView fullListName;
        TextView fullListId;

        public FullListViewHolder(View itemView) {
            super(itemView);
// the fulllist name and id are found and set for the item
            fullListName = (TextView) itemView.findViewById(R.id.fullListName);
            fullListId = (TextView) itemView.findViewById(R.id.fullListId);
            itemView.setOnClickListener(this);
        }
        public void bind (FullListPoke fullListPoke){
            try{
                fullListName.setText(Character.toUpperCase(fullListPoke.name.charAt(0))
                        + fullListPoke.name.substring(1));
                fullListId.setText("Pokemon id: " + Integer.toString(fullListPoke.id));
            } catch(Exception e){
                Log.d("Error: ", e.getMessage());
            }

        }

        @Override
        public void onClick(View view) {
// when clicked, start a new intent for the pokemon name who occupied the cell that was clicked
            int position = getAdapterPosition();
            FullListPoke flpoke = flpokes.get(position);
            Intent intent = new Intent(view.getContext(), DisplayResult.class);
            intent.putExtra(MainActivity.EXTRA_MESSAGE, flpoke.name);
            view.getContext().startActivity(intent);
        }
    }
}
