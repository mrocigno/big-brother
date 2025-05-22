package br.com.mrocigno.bigbrother.ui_automator.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.core.utils.closeBigBrotherBubble
import br.com.mrocigno.bigbrother.core.utils.getBigBrotherTask
import br.com.mrocigno.bigbrother.ui_automator.R
import br.com.mrocigno.bigbrother.ui_automator.UiAutomatorHolder
import br.com.mrocigno.bigbrother.ui_automator.UiAutomatorTask

class UiAutomatorFragment : Fragment(R.layout.bigbrother_fragment_ui_automator) {

    private val toggle: AppCompatButton by id(R.id.automator_toggle)
    private val play: AppCompatButton by id(R.id.automator_play)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggle.setOnClickListener {
            val task = getBigBrotherTask(UiAutomatorTask::class) ?: return@setOnClickListener
            if (task.isRecording) {
                task.stopRecording(requireActivity())
                toggle.text = "Start recording"
            } else {
                task.startRecording(requireActivity())
                toggle.text = "Stop recording"
                requireActivity().closeBigBrotherBubble()
            }
        }

        play.setOnClickListener {
            UiAutomatorHolder.play(requireActivity())
        }
    }
}