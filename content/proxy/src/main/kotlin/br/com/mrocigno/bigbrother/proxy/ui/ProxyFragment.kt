package br.com.mrocigno.bigbrother.proxy.ui

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import br.com.mrocigno.bigbrother.common.provider.id
import br.com.mrocigno.bigbrother.proxy.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ProxyFragment : Fragment(R.layout.bigbrother_fragment_proxy) {

    private val recycler: RecyclerView by id(R.id.proxy_recycler)
    private val searchViewLayout: TextInputLayout by id(R.id.proxy_search_layout)
    private val searchView: TextInputEditText by id(R.id.proxy_search_view)
    private val add: AppCompatImageView by id(R.id.proxy_add)
    private val emptyState: View by id(R.id.proxy_empty_state)

    private val createRuleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        add.setOnClickListener {
            createRuleLauncher.launch(ProxyActivity.intent(requireContext()))
        }
    }
}