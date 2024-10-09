package com.paquete.proyectomatematicat

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.paquete.proyectomatematicat.databinding.FragmentAdditionSubtractionBinding
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.random.Random
import kotlin.system.exitProcess

class Addition_SubtractionFragment : Fragment() {

    private val numbers: MutableList<String> = mutableListOf()
    private var result: Int = 0
    private var correctAnswers: Int = 0
    private var incorrectAnswers: Int = 0

    private var _binding: FragmentAdditionSubtractionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAdditionSubtractionBinding.inflate(inflater, container, false)

        binding.btnCheckAnswer.visibility = View.VISIBLE
        binding.btnNextExercise.visibility = View.INVISIBLE

        createRandomExercise()
        editTextModifier()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCheckAnswer.setOnClickListener{
            checkAnswer()
        }

        binding.btnNextExercise.setOnClickListener{
            nextExercise()
        }

    }

    private fun createRandomExercise(){
        numbers.add(Random.nextInt(1000,10000).toString())
        numbers.add(Random.nextInt(1000,numbers[0].toInt()).toString())

        binding.txtNumber1.text = numbers[0]
        binding.txtNumber2.text = numbers[1]

        val randomMathematicalSymbol = createRandomMathematicalSymbol()

        val exercise = "${numbers[0]} $randomMathematicalSymbol ${numbers[1]}"

        result = ExpressionBuilder(exercise).build().evaluate().toInt();

        setMaxLenghtToEditText(result.toString().length)
    }

    private fun createRandomMathematicalSymbol(): String{
        val number = Random.nextInt(101)

        val mathematicalSymbol = when{

            number <= 50 -> "+"
            else -> "-"
        }
        binding.txtMathematicalSymbol.text = mathematicalSymbol

        return mathematicalSymbol
    }
    private fun checkAnswer(){

        binding.btnCheckAnswer.visibility = View.INVISIBLE

        if(binding.etAnswer.text.toString() == result.toString()){
            correctAnswers++
            binding.txtCorrectAnswersCounter.text = correctAnswers.toString()
        }else{
            incorrectAnswers++
            binding.txtIncorrectAnswersCounter.text = incorrectAnswers.toString()
        }

        checkTotalAnswer()

        binding.txtCorrectAnswer.text = "Respuesta correcta: $result"
        binding.btnNextExercise.visibility = View.VISIBLE
    }

    private fun nextExercise(){

        binding.btnCheckAnswer.visibility = View.VISIBLE
        binding.btnNextExercise.visibility = View.INVISIBLE

        binding.etAnswer.setText("")
        binding.txtCorrectAnswer.setText("")

        numbers.clear()

        val exercisesActivity = requireActivity() as ExercisesActivity  // El tipo de la actividad
        exercisesActivity.createRandomExercise()  // Llamada directa a la función de la actividad

    }

    private fun checkTotalAnswer(){
        if(correctAnswers >= 3){
            System.exit(1)
            exitProcess(0)
        }
    }

    //LIMITE DE CARACTERES EN EL EDITTEXT DE RESPUESTA
    private fun setMaxLenghtToEditText(maxLength: Int){
        binding.etAnswer.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    //ESTE METODO FUE CREADO PARA MODIFICAR EL EDITTEXT DE RESPUESTA
    //LO QUE HACE ES ESCRIBIR DE DERECHA A IZQUIERDA
    private fun editTextModifier(){
        binding.etAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar

            }

            override fun afterTextChanged(s: Editable?) {
                // Mover el cursor al principio cuando el texto cambie
                binding.etAnswer.setSelection(0)
            }
        })
        //ESTA PARTE ELIMINA EL PRIMER CARACTER DEL EDITTEXT DE LA RESPUESTA
        binding.etAnswer.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val currentText = binding.etAnswer.text.toString()

                if (currentText.isNotEmpty()) {
                    // Borrar el último carácter ingresado
                    val updatedText = currentText.substring(1, currentText.length)
                    binding.etAnswer.setText(updatedText)
                    binding.etAnswer.setSelection(0)
                }
                true
            } else {
                false
            }
        }
    }

}