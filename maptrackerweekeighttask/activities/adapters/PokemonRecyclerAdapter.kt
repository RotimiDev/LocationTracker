package com.olamachia.maptrackerweekeighttask.activities.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.olamachia.maptrackerweekeighttask.R
import com.olamachia.maptrackerweekeighttask.`interface`.IItemClickListener
import com.olamachia.maptrackerweekeighttask.model.Result
import com.olamachia.maptrackerweekeighttask.PokemonListDirections

class PokemonRecyclerAdapter(internal val context: Context, internal var pokemonList: List<Result>) : RecyclerView.Adapter<PokemonRecyclerAdapter.MyViewHolder>() {

    // create a view holder class for the recycler view
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var pokemonImageView: ImageView = itemView.findViewById(R.id.pokemon_image_view)
        internal var pokemonNameTextView: TextView = itemView.findViewById(R.id.pokemon_name_TV)

        // interface to handle the onclick listener
        private var itemClickListener: IItemClickListener? = null

        fun setItemClickListener(iItemClickListener: IItemClickListener) {
            this.itemClickListener = iItemClickListener
        }

        init {
            itemView.setOnClickListener { view -> itemClickListener?.onClick(view, adapterPosition) }
        }
    }

    // inflate the layout to be used by each item on the recycler view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.pokemon_list_items, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pokemonList.size
    }

    // binds the data and displays it in the specified location
    // specify the position where each property should be displayed
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val url = pokemonList[position].url
        val imageNo = url.split("https://pokeapi.co/api/v2/pokemon/")[1].split("/")[0]
        Glide.with(context).load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$imageNo.png").into(holder.pokemonImageView)
        holder.pokemonNameTextView.text = pokemonList[position].name

        // listen for user click events
        holder.setItemClickListener(object : IItemClickListener {
            override fun onClick(view: View, position: Int) {
                val pokeUrl = pokemonList[position].url

                // use actions to pass data from one fragment to the other
                val action = PokemonListDirections.actionPokemonList2ToPokemonDetailsFragment(pokeUrl)
                view.findNavController().navigate(action) // find a navigation controller associated with a view
            }
        })
    }

}
