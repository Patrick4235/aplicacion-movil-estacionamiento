package com.paquete.proyectomatematicat

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.random.Random
import kotlin.system.exitProcess

class Addition_SubtractionActivity : AppCompatActivity() {

    private val numbers: MutableList<String> = mutableListOf()
    private var result: Int = 0
    private var correctAnswers: Int = 0
    private var incorrectAnswers: Int = 0

    private lateinit var txtExerciseNumber: TextView
    private lateinit var txtNumber1: TextView
    private lateinit var txtNumber2: TextView
    private lateinit var txtMathematicalSymbol: TextView
    private lateinit var txtCorrectAnswersCounter: TextView
    private lateinit var txtIncorrectAnswersCounter: TextView
    private lateinit var txtCorrectAnswer: TextView
    private lateinit var etAnswer: EditText
    private lateinit var btnNextExercise: Button
    private lateinit var btnCheckAnswer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addition_subtraction)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtExerciseNumber = findViewById(R.id.txtExerciseNumber)
        txtExerciseNumber = findViewById(R.id.txtExerciseNumber)
        txtNumber1 = findViewById(R.id.txtNumber1)
        txtNumber2 = findViewById(R.id.txtNumber2)
        txtMathematicalSymbol = findViewById(R.id.txtMathematicalSymbol)
        txtCorrectAnswersCounter = findViewById(R.id.txtCorrectAnswersCounter)
        txtIncorrectAnswersCounter = findViewById(R.id.txtIncorrectAnswersCounter)
        txtCorrectAnswer = findViewById(R.id.txtCorrectAnswer)
        txtIncorrectAnswersCounter = findViewById(R.id.txtIncorrectAnswersCounter)

        etAnswer = findViewById(R.id.etAnswer)

        btnNextExercise = findViewById(R.id.btnNextExercise)
        btnCheckAnswer = findViewById(R.id.btnCheckAnswer)

        btnCheckAnswer.visibility = View.VISIBLE
        btnNextExercise.visibility = View.INVISIBLE

        createRandomExercise()
        editTextModifier()



    }

    private fun createRandomExercise(){
        numbers.add(Random.nextInt(1000,10000).toString())
        numbers.add(Random.nextInt(1000,numbers[0].toInt()).toString())

        txtNumber1.text = numbers[0]
        txtNumber2.text = numbers[1]

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
        txtMathematicalSymbol.text = mathematicalSymbol

        return mathematicalSymbol
    }
    fun checkAnswer(view: View){

        btnCheckAnswer.visibility = View.INVISIBLE

        if(etAnswer.text.toString() == result.toString()){
            correctAnswers++
            txtCorrectAnswersCounter.text = correctAnswers.toString()
        }else{
            incorrectAnswers++
            txtIncorrectAnswersCounter.text = incorrectAnswers.toString()
        }

        checkTotalAnswer()

        txtCorrectAnswer.text = "Respuesta correcta: $result"
        btnNextExercise.visibility = View.VISIBLE
    }

    fun nextExercise(view: View){

        btnCheckAnswer.visibility = View.VISIBLE
        btnNextExercise.visibility = View.INVISIBLE

        etAnswer.setText("")
        txtCorrectAnswer.setText("")

        numbers.clear()


        //createRandomExercise()
    }
    // Supongamos que la actividad tiene una función llamada 'someFunction'
    fun callActivityDirectly() {
    }



    fun checkTotalAnswer(){
        if(correctAnswers >= 3){
            this.finish()
            exitProcess(0)
        }
    }

    //LIMITE DE CARACTERES EN EL EDITTEXT DE RESPUESTA
    private fun setMaxLenghtToEditText(maxLength: Int){
        etAnswer.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
    }

    //ESTE METODO FUE CREADO PARA MODIFICAR EL EDITTEXT DE RESPUESTA
    //LO QUE HACE ES ESCRIBIR DE DERECHA A IZQUIERDA
    private fun editTextModifier(){
        etAnswer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario implementar
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar

            }

            override fun afterTextChanged(s: Editable?) {
                // Mover el cursor al principio cuando el texto cambie
                etAnswer.setSelection(0)
            }
        })
    //ESTA PARTE ELIMINA EL PRIMER CARACTER DEL EDITTEXT DE LA RESPUESTA
        etAnswer.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                val currentText = etAnswer.text.toString()

                if (currentText.isNotEmpty()) {
                    // Borrar el último carácter ingresado
                    val updatedText = currentText.substring(1, currentText.length)
                    etAnswer.setText(updatedText)
                    etAnswer.setSelection(0)
                }
                true
            } else {
                false
            }
        }
    }

}