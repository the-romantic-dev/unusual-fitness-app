package org.theromanticdev.unusualfitnessapp.presentation.view.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.theromanticdev.unusualfitnessapp.R
import org.theromanticdev.unusualfitnessapp.appComponent
import org.theromanticdev.unusualfitnessapp.dagger.app.AppComponent
import org.theromanticdev.unusualfitnessapp.databinding.FragmentWorkoutsListBinding
import org.theromanticdev.unusualfitnessapp.domain.usecases.DeleteWorkoutInfoFromRepositoryUseCase
import org.theromanticdev.unusualfitnessapp.domain.usecases.GetAllWorkoutInfoFromRepositoryUseCase
import org.theromanticdev.unusualfitnessapp.presentation.view.adapters.WorkoutsListAdapter
import org.theromanticdev.unusualfitnessapp.presentation.viewmodel.ShowResultViewModel
import javax.inject.Inject


class WorkoutsListFragment : Fragment(R.layout.fragment_workouts_list) {

    @Inject
    lateinit var getAllWorkoutInfoFromRepositoryUseCase: GetAllWorkoutInfoFromRepositoryUseCase

    @Inject
    lateinit var deleteWorkoutInfoFromRepositoryUseCase: DeleteWorkoutInfoFromRepositoryUseCase

    private val showResultViewModel: ShowResultViewModel by activityViewModels()

    private var _binding: FragmentWorkoutsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var daggerComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        daggerComponent = requireContext().appComponent
        daggerComponent.inject(this)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val idToInfo = getAllWorkoutInfoFromRepositoryUseCase.execute()

        binding.workoutsList.adapter = WorkoutsListAdapter(idToInfo,
            {info ->
                showResultViewModel.workoutInfo = info
                val action =
                    WorkoutsListFragmentDirections.actionWorkoutsListFragmentToShowWorkoutResultFragment()
                findNavController().navigate(action)
            },
            { id ->
               AlertDialog.Builder(requireActivity()).setPositiveButton("Да") {_, _ ->
                   deleteWorkoutInfoFromRepositoryUseCase.execute(id)
               }.setNegativeButton("Нет") {_,_->}.setTitle("Вы хотите удалить тренировку?").create().show()
            })
        binding.workoutsList.layoutManager = LinearLayoutManager(requireActivity())
    }
}