package ru.rikmasters.gilty.presentation.ui.presentation.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.rikmasters.gilty.R

class ModalBottomSheet(
    var fragment : Fragment? = null
) : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_modal_base, container, false)

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onStart() {
        super.onStart()

        fragment?.let {
            childFragmentManager.beginTransaction().add(R.id.base_fragment, it).commit()
        }

        (dialog as BottomSheetDialog).behavior.let {

            it.isDraggable = true

        }

    }

}