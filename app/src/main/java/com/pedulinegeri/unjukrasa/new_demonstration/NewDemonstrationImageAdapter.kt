package com.pedulinegeri.unjukrasa.new_demonstration

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.pedulinegeri.unjukrasa.R
import com.pedulinegeri.unjukrasa.databinding.NewImageOrVideoItemBinding


class NewDemonstrationImageAdapter(private val fragmentManager: FragmentManager) : RecyclerView.Adapter<NewDemonstrationImageAdapter.ViewHolder>() {
    private val imagesUrl = arrayListOf("")

    var onVideoRemoved: ((String) -> Unit)? = null
    var onVideoLoaded: ((String) -> Unit)? = null

    inner class ViewHolder(private val binding: NewImageOrVideoItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String) {
            if (url.length == 11) {
                binding.youTubePlayerView.visibility = View.VISIBLE
                binding.ivDemonstration.visibility = View.GONE

                val youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance()

                // TODO: CHANGE API KEY TO OAUTH2
                youTubePlayerFragment.initialize("AIzaSyBi6OpyJy7zKffFtu7mfe1iknwALP3YyqI", object : YouTubePlayer.OnInitializedListener {
                    override fun onInitializationSuccess(
                        p0: YouTubePlayer.Provider?,
                        player: YouTubePlayer?,
                        p2: Boolean
                    ) {
                        Log.d("YOUTUBE", "Succeed to initialize youtube player")
                        player?.loadVideo(url)
                    }

                    override fun onInitializationFailure(
                        p0: YouTubePlayer.Provider?,
                        p1: YouTubeInitializationResult?
                    ) {
                        Log.d("YOUTUBE", "Failed to initialize youtube player")
                    }

                })

                binding.civCancel.visibility = View.VISIBLE
                binding.civCancel.setOnClickListener {
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.remove(youTubePlayerFragment).commit()

                    binding.youTubePlayerView.visibility = View.GONE
                    binding.civCancel.visibility = View.GONE

                    onVideoRemoved?.invoke(imagesUrl[absoluteAdapterPosition])
                }

                onVideoLoaded?.invoke(imagesUrl[absoluteAdapterPosition])

                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(R.id.youTubePlayerView, youTubePlayerFragment).commit()
            } else if (url.isNotEmpty()) {
                binding.youTubePlayerView.visibility = View.GONE
                binding.ivDemonstration.visibility = View.VISIBLE

                val bmImg = BitmapFactory.decodeFile(url)
                binding.ivDemonstration.setImageBitmap(bmImg)

                binding.civCancel.visibility = View.VISIBLE
                binding.civCancel.setOnClickListener {
                    imagesUrl.removeAt(absoluteAdapterPosition)
                    notifyItemRemoved(absoluteAdapterPosition)
                    binding.ivDemonstration.setImageResource(0)

                    binding.ivDemonstration.visibility = View.GONE
                    binding.civCancel.visibility = View.GONE
                }
            }
        }
    }

    fun changeYoutubeVideo(id: String) {
        imagesUrl[0] = id
        notifyItemChanged(0)
    }

    fun addImage(url: String) {
        imagesUrl.add(url)
        notifyItemInserted(itemCount-1)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = NewImageOrVideoItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(imagesUrl[position])
    }

    override fun getItemCount(): Int = imagesUrl.size
}