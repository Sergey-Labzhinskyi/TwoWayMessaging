package com.example.twowaymessaging

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_handler_example.*

const val SHOW_PROGRESS_BAR = 1
 const val HIDE_PROGRESS_BAR = 0

class HandlerExampleActivity : AppCompatActivity() {

    private lateinit var backgroundThread: BackgroundThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LooperTest", "${this.javaClass.simpleName} onCreate() ${Thread.currentThread()}")
        setContentView(R.layout.activity_handler_example)

        backgroundThread = BackgroundThread()
        /**
         * Фоновый поток с очередью сообщений начинают работу при создании экземпляра класса
         * HandlerExampleActivity. Он обрабатывает задачи переданные с UI-потока
         */
        backgroundThread.start()

        /**
         * При щелчке в фоновый поток передается новая задача. Задачи выполняются последовательно.
         */
        btnButton.setOnClickListener {
            Log.d("LooperTest", "${this.javaClass.simpleName} setOnClickListener() ${Thread.currentThread()}")
            backgroundThread.doWork(uiHandler)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LooperTest", "${this.javaClass.simpleName} onDestroy() ${Thread.currentThread()}")
        /**
         * Фоновый поток прекращает работу при уничтожении HandlerExampleActivity
         */
        backgroundThread.exit()
    }

    override fun getLastNonConfigurationInstance(): Any? {
        return super.getLastNonConfigurationInstance()
    }

    /**
     * UI-поток определяет собственный обработчик Handler, который может принимать команды управления
     * индикатором выполнения и обновлять пользовательский интерфейс после получения результатов от
     * фонового потока.
     */
    val uiHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            Log.d("LooperTest", "${this.javaClass.simpleName} handleMessage() ${Thread.currentThread()}")
            when (msg.what){
                SHOW_PROGRESS_BAR -> {
                    Log.d("LooperTest", "${this.javaClass.simpleName} handleMessage() SHOW_PROGRESS_BAR ${msg.arg1}")
                    progressBar?.visibility = View.VISIBLE
                }
                HIDE_PROGRESS_BAR -> {
                    Log.d("LooperTest", "${this.javaClass.simpleName} handleMessage() HIDE_PROGRESS_BAR ${msg.arg1}")
                    tvText.text = msg.arg1.toString()
                    progressBar?.visibility = View.INVISIBLE
                }
            }
        }
    }
}