package com.olamachia.maptrackerweekeighttask

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.olamachia.maptrackerweekeighttask.retrofit.PokemonListInterface
import com.olamachia.maptrackerweekeighttask.retrofit.RetrofitClient
import com.olamachia.maptrackerweekeighttask.activities.adapters.PokemonAbilitiesAdapter
import com.olamachia.maptrackerweekeighttask.activities.adapters.PokemonStatAdapter
import com.olamachia.maptrackerweekeighttask.activities.adapters.PokemonTypeAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.pokemon_details_fragment.*
import retrofit2.Retrofit
import java.lang.Exception

class PokemonDetailsFragment : Fragment() {
    lateinit var pokemonName: TextView
    lateinit var pokemonheight: TextView
    lateinit var pokemonWeight: TextView
    lateinit var pokemonSprite: ImageView
    lateinit var typeRecyclerView: RecyclerView
    lateinit var abilitiesRecyclerView: RecyclerView
    private lateinit var statRecyclerView: RecyclerView
    private val args: PokemonDetailsFragmentArgs by navArgs()
    private var compositeDisposable = CompositeDisposable()
    private var pokemonListInterface: PokemonListInterface
















    init {
        val retrofit: Retrofit = RetrofitClient.instance
        pokemonListInterface = retrofit.create(PokemonListInterface::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Set Status bar Color to image vibrant swatch
        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.backgroudBlue)

        // Inflate the layout for this fragment
        val itemView = inflater.inflate(R.layout.pokemon_details_fragment, container, false)
        val pokemonUrl = args.pokemonUrl

        getPokemonDetails(pokemonUrl)

        // set the pokemon attributes names to their respective views to a variable
        pokemonName = itemView.findViewById(R.id.pokemon_nameTV)
        pokemonheight = itemView.findViewById(R.id.pokemon_height) as TextView
        pokemonWeight = itemView.findViewById(R.id.pokemon_weight) as TextView
        pokemonSprite = itemView.findViewById(R.id.pokemon_image) as ImageView
        typeRecyclerView = itemView.findViewById(R.id.type_recycler_view) as RecyclerView
        typeRecyclerView.setHasFixedSize(true)
        typeRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        statRecyclerView = itemView.findViewById(R.id.stat_recycler_view) as RecyclerView
        statRecyclerView.setHasFixedSize(true)
        statRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        abilitiesRecyclerView = itemView.findViewById(R.id.abilities_recycler_view) as RecyclerView
        abilitiesRecyclerView.setHasFixedSize(true)
        abilitiesRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        return itemView
    }

    // get the pokemon details data and populate in the abilities adapter
    @SuppressLint("SetTextI18n")
    private fun getPokemonDetails(pokemonUrl: String) {

        try {
            compositeDisposable.add(
                pokemonListInterface.getPokemon(pokemonUrl)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { pokemonDex ->

                        // set the image with glide function
                        Glide.with(requireContext()).load(pokemonDex.sprites.front_default)
                            .into(pokemon_image)
                        pokemon_nameTV.text = pokemonDex.name
                        pokemonheight.text = "Height: " + pokemonDex.height.toString()
                        pokemonWeight.text = "Weight: " + pokemonDex.weight.toString()

                        // populate the type recycler view
                        val typeAdapter = PokemonTypeAdapter(requireContext(), pokemonDex.types)
                        type_recycler_view.adapter = typeAdapter

                        // populate the stat recycler view
                        val statAdapter = PokemonStatAdapter(requireContext(), pokemonDex.stats)
                        stat_recycler_view.adapter = statAdapter

                        // populate the abilities recycler view
                        val abilitiesAdapter =
                            PokemonAbilitiesAdapter(requireContext(), pokemonDex.abilities)
                        abilities_recycler_view.adapter = abilitiesAdapter
                    }
            )
        } catch (ex: Exception) {
            Log.d("PokemonDetails", "getPokemonDetails: $ex ")
        }
    }
}