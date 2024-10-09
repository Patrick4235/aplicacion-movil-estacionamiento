package com.paquete.proyectomatematicat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.paquete.proyectomatematicat.databinding.FragmentAdditionSubtractionBinding
import com.paquete.proyectomatematicat.databinding.FragmentMissedNumberBinding
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.random.Random

class MissedNumberFragment : Fragment() {

    private val numbers: MutableList<Int> = mutableListOf()
    private var result: Int = 0

    private lateinit var answerButtons: MutableList<Button>

    private var _binding: FragmentMissedNumberBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentMissedNumberBinding.inflate(inflater, container, false)

        createRandomExercise()
        checkAnswer()

        binding.btnNextExercise.visibility = View.INVISIBLE


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNextExercise.setOnClickListener{
            nextExercise()
        }

    }

    private fun createRandomExercise(){
        numbers.clear()
        numbers.add(Random.nextInt(100,1000))
        numbers.add(Random.nextInt(100,numbers[0]))

        val randomMathematicalSymbol = createRandomMathematicalSymbol()

        val exercise = "${numbers[0]} $randomMathematicalSymbol ${numbers[1]}"

        result = ExpressionBuilder(exercise).build().evaluate().toInt();

        binding.txtExercise.text = "$exercise = $result"

        createRandomAnswers()

    }

    private fun createRandomMathematicalSymbol(): String{
        val number = Random.nextInt(101)

        val mathematicalSymbol = when{

            number <= 50 -> "+"
            else -> "-"
        }

        return mathematicalSymbol
    }

    private fun createRandomAnswers(){
        val randomAnswers = (0..5).shuffled()

        binding.btnAnswer1.text = (result + randomAnswers[0]).toString()
        binding.btnAnswer2.text = (result + randomAnswers[1]).toString()
        binding.btnAnswer3.text = (result + randomAnswers[2]).toString()
        binding.btnAnswer4.text = (result + randomAnswers[3]).toString()
        binding.btnAnswer5.text = (result + randomAnswers[4]).toString()
        binding.btnAnswer6.text = (result + randomAnswers[5]).toString()

    }

    private fun checkAnswer(){

        var yourAnswer = 0

        val clickListener = View.OnClickListener { view ->
            when(view){
                binding.btnAnswer1 -> {
                    yourAnswer = binding.btnAnswer1.text.toString().toInt()
                }
                binding.btnAnswer2 -> {
                    yourAnswer = binding.btnAnswer2.text.toString().toInt()
                }
                binding.btnAnswer3 -> {
                    yourAnswer = binding.btnAnswer3.text.toString().toInt()
                }
                binding.btnAnswer4 -> {
                    yourAnswer = binding.btnAnswer4.text.toString().toInt()
                }
                binding.btnAnswer5 -> {
                    yourAnswer = binding.btnAnswer5.text.toString().toInt()
                }
                binding.btnAnswer6 -> {
                    yourAnswer = binding.btnAnswer6.text.toString().toInt()
                }
            }

            binding.btnNextExercise.visibility = View.VISIBLE
            //Toast.makeText(requireContext(), "$yourAnswer = $result", Toast.LENGTH_SHORT).show()
        }

        binding.btnAnswer1.setOnClickListener(clickListener)
        binding.btnAnswer2.setOnClickListener(clickListener)
        binding.btnAnswer3.setOnClickListener(clickListener)
        binding.btnAnswer4.setOnClickListener(clickListener)
        binding.btnAnswer5.setOnClickListener(clickListener)
        binding.btnAnswer6.setOnClickListener(clickListener)
    }

    private fun nextExercise(){

        binding.btnNextExercise.visibility = View.INVISIBLE

        numbers.clear()

        val exercisesActivity = requireActivity() as ExercisesActivity  // El tipo de la actividad
        exercisesActivity.createRandomExercise()  // Llamada directa a la función de la actividad

    }


}