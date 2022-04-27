package com.sps.tictactoe

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sps.tictactoe.TicTacToeBindings.*
import com.sps.tictactoe.TicTacToeFeature.State.*
import com.sps.tictactoe.TicTacToeFeature.State.GameResult.*
import com.sps.tictactoe.TicTacToeUiEvent.*
import com.sps.tictactoe.composables.GameBoard
import com.sps.tictactoe.composables.GameCounter
import com.sps.tictactoe.composables.ResetButton
import com.sps.tictactoe.ui.theme.TicTacToeTheme
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.functions.Consumer
import io.reactivex.subjects.PublishSubject


class MainActivity : ComponentActivity(), ObservableSource<TicTacToeUiEvent>,
    Consumer<TicTacToeVM> {

    private val featureState = TicTacToeFeature()
    private val bindings = TicTacToeBindings(
        this, featureState,
        ViewModelTransformer, UiEventTransformer
    )
    private val subject: PublishSubject<TicTacToeUiEvent> = PublishSubject.create()
    private var viewModel: TicTacToeVM? by mutableStateOf(null)

    fun showResult(gameResult: GameResult) {
        when (gameResult) {
            XWINS -> Toast.makeText(
                this,
                "Game over, X wins!",
                Toast.LENGTH_SHORT
            )
                .show()
            OWINS -> Toast.makeText(
                this,
                "Game over, O wins!",
                Toast.LENGTH_SHORT
            )
                .show()
            DRAW -> Toast.makeText(
                this,
                "Game over - It's a draw!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindings()
        setContent {
            TicTacToeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        viewModel?.let{ vm ->
                            GameBoard(board = vm.board) {
                                onCellClicked(it)
                            }
                            ResetButton(::onResetClicked)
                            GameCounter(gameCounter = vm.gameCounter)
                        }
                    }

                }
            }
        }
    }

    private fun setBindings() {
        bindings.setup(this)
    }

    override fun subscribe(observer: Observer<in TicTacToeUiEvent>) {
        subject.subscribe(observer)
    }

    override fun accept(viewModel: TicTacToeVM) {
        this.viewModel = viewModel
    }

    private fun onResetClicked() {
        // invoke whatever you need here to reset the feature: featureState.accept(TicTacToeFeature.Wish.ResetGame)
        subject.onNext(ResetClicked)
    }

    private fun onCellClicked(cellIndex: Int) {
        // invoke whatever you need here to place a piece in the board: featureState.accept(TicTacToeFeature.Wish.MakeMove(cellIndex))
        subject.onNext(CellClicked(cellIndex))
    }

    private fun <T> ObservableSource<out T>.wrapToObservable(): Observable<T> =
        Observable.wrap(cast())

    private inline fun <reified T> Any?.cast(): T = this as T

}






