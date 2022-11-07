package com.app.mysteryreels.presentation

import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.mysteryreels.base.BaseBindingFragment
import com.app.mysteryreels.base.collectLatestLifecycleAware
import com.app.mysteryreels.databinding.FragmentLoadingsBinding
import java.io.File

class LoadingFragment :
    BaseBindingFragment<FragmentLoadingsBinding>(FragmentLoadingsBinding::inflate) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (!load()) {
            val action = LoadingFragmentDirections.actionLoadingFragmentToMainFragment()
            findNavController().navigate(action)
            return
        }
        initUI()

        collectLatestLifecycleAware(viewModel.fileData) {
            if(it.isEmpty()) return@collectLatestLifecycleAware
            val action = LoadingFragmentDirections.actionLoadingFragmentToWVFragment(it)
            findNavController().navigate(action)
        }

        viewModel.init(requireActivity() as MainActivity)
    }

    private fun initUI() {
        binding.apply {
            progressBar.animate()
                .rotation(36000f)
                .setDuration(100000)
                .setInterpolator(LinearInterpolator())
                .start()
        }
    }

    private fun load(): Boolean {

        fun isNotADB(): Boolean =
            Settings.Global.getString(
                requireActivity().contentResolver,
                Settings.Global.ADB_ENABLED
            ) != "1"


        return isNotADB()
    }


}