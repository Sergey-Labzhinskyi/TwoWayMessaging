package com.example.twowaymessaging

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.util.LogPrinter
import kotlin.random.Random

class BackgroundThread : Thread() {
    private lateinit var backgroundHandler: Handler

    override fun run() {
        Log.d("LooperTest", "${this.javaClass.simpleName} run() ${currentThread()}")
        /**
         * Связывает объетк Looper  с текущим потоком
         */
        Looper.prepare()

        /**
         * Этот экземпляр Handler обрабатывает только объекты, реализующие интерфейс Runnable
         * (то есть задачи). Поэтому для него не требуется реализация Handler.handlerMessage().
         */
        backgroundHandler = Handler()
        Looper.loop()
    }

    fun doWork(uiHandler: Handler) {
        Log.d("LooperTest", "${this.javaClass.simpleName} doWork() ${currentThread()}")
        /**
         * Передача задачи в фоновый поток для выполнения
         */
        backgroundHandler.post(object : Runnable {
            override fun run() {
                Log.d(
                    "LooperTest",
                    "${this.javaClass.simpleName} doWork() post ${currentThread()}"
                )
                /**
                 * Создание объекта Message, содержащего только аргумерт what с комендой SHOW_PROGRESS_BAR,
                 * для передачи в UI-поток, что бы он мог вывести на экран индикатор выполнения.
                 */
                var uiMsg = uiHandler.obtainMessage(
                    SHOW_PROGRESS_BAR,
                    0, 0, null
                )

                /**
                 * Отправка начального сообщения в UI-поток
                 */
                uiHandler.sendMessage(uiMsg)

                /**
                 * Имитация задачи случайной продолжительности
                 */
                val random = Random
                val randomInt = random.nextInt(5000)
                SystemClock.sleep(randomInt.toLong())

                /**
                 * Создание объекта Message с результатом randomInt, который передается в параметре arg1.
                 * В параметре what передается команда HIDE_PROGRESS_BAR для удаления с экрана индикатора
                 * выполнения.
                 */
                uiMsg = uiHandler.obtainMessage(
                    HIDE_PROGRESS_BAR,
                    randomInt, 0, null
                )

                /**
                 * Сообщение с конечным результатом информирует UI-поток о завершении фоновой задачи
                 * и доставляет результат ее выполнения.
                 */
                uiHandler.sendMessage(uiMsg)
            }
        })

        backgroundHandler.dump(LogPrinter(Log.DEBUG, "TAG"), "")
    }


    fun exit() {
        Log.d("LooperTest", "${this.javaClass.simpleName} exit() ${currentThread()}")
        /**
         * Завершение работы Looper, после чего может завершится и текущий поток.
         */
        backgroundHandler.looper.quit()
    }
}

