package com.app.mysteryreels.presentation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.mysteryreels.R
import com.app.mysteryreels.base.BaseBindingFragment
import com.app.mysteryreels.base.collectLatestLifecycleAware
import com.app.mysteryreels.base.collectLifecycleAware
import com.app.mysteryreels.databinding.FragmentGameBinding


class GameFragment : BaseBindingFragment<FragmentGameBinding>(FragmentGameBinding::inflate) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
        initObservers()
    }




    private fun initObservers() {
        collectLatestLifecycleAware(viewModel.slotState) {
            binding.ivFirst.setImageResource(it.slotLeft.resId)
            binding.ivSecond.setImageResource(it.slotCenter.resId)
            binding.ivThird.setImageResource(it.slotRight.resId)
        }
        collectLatestLifecycleAware(viewModel.isLoze) {
            if (it) {
                val action = GameFragmentDirections.actionGameFragmentToMainFragment()
                findNavController().navigate(action)
            }
        }
        collectLifecycleAware(viewModel.displayCredits) {
            binding.tvCredits.text = "Your credits: $it"
        }
    }

    private fun initUI() {
        binding.btnPlay.setOnClickListener { viewModel.runSlot() }
        val factory = ViewSwitcher.ViewFactory {
            val imageView = ImageView(requireContext())
            imageView.layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            imageView
        }
        binding.apply {
            ivFirst.setFactory(factory)
            ivFirst.setInAnimation(requireContext(), R.anim.from_top)
            ivFirst.setOutAnimation(requireContext(), R.anim.to_bottom)

            ivSecond.setFactory(factory)
            ivSecond.setInAnimation(requireContext(), R.anim.from_top)
            ivSecond.setOutAnimation(requireContext(), R.anim.to_bottom)

            ivThird.setFactory(factory)
            ivThird.setInAnimation(requireContext(), R.anim.from_top)
            ivThird.setOutAnimation(requireContext(), R.anim.to_bottom)
        }


    }

}