package com.app.mysteryreels.presentation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.mysteryreels.R
import com.app.mysteryreels.base.BaseBindingFragment
import com.app.mysteryreels.databinding.FragmentSplashScreenBinding
import java.util.*


class MainFragment :
    BaseBindingFragment<FragmentSplashScreenBinding>(FragmentSplashScreenBinding::inflate) {

    private val viewModel: MainViewModel by activityViewModels()
    lateinit var colorAnimation: ValueAnimator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNumberPicker()
        animateArrow()
    }

    private fun initNumberPicker() {
        val numberPicker = binding.numberPicker

        var start = 400
        val numbers = arrayOfNulls<String>(10)
        for (i in 0..9) {
            numbers[i] = start.toString()
            start += 100
        }

        numberPicker.minValue = 0
        numberPicker.maxValue = numbers.size - 1
        numberPicker.displayedValues = numbers
        numberPicker.value

        binding.btnStart.setOnClickListener {
            viewModel.setCredits(numbers[numberPicker.value]?.toInt() ?: 100)

            val action =
                MainFragmentDirections.actionMainFragmentToGameFragment()
            findNavController().navigate(action)
        }
    }


    private fun animateArrow() {
        val colorFrom = ContextCompat.getColor(requireContext(), R.color.white)
        val colorTo = ContextCompat.getColor(requireContext(), R.color.red)
        colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 500 // milliseconds

        colorAnimation.repeatMode = ValueAnimator.REVERSE
        colorAnimation.repeatCount = ValueAnimator.INFINITE
        colorAnimation.addUpdateListener { animator ->
            binding.imgArrowLeft.setColorFilter(animator.animatedValue as Int)
            binding.imgArrowRight.setColorFilter(animator.animatedValue as Int)
        }
        colorAnimation.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        colorAnimation.cancel()
    }
}