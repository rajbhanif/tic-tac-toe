package com.example.finalttt

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var gridLayout: GridLayout
    private lateinit var gameStatusTextView: TextView
    private lateinit var buttons: Array<Array<ImageButton>>
    private var boardState: Array<Array<Int>> = Array(3) { Array(3) { 0 } } // 0=empty, 1=X, 2=O
    private var playerTurn = true // true for player X, false for player O (AI)
    private var gameOver = false
    private var pseudo: String = "Joueur" // Player name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get player name from WelcomeActivity
        pseudo = intent.getStringExtra("pseudo") ?: pseudo

        gridLayout = findViewById(R.id.gridLayout)
        gameStatusTextView = findViewById(R.id.gameStatusTextView)

        initializeButtons()
        resetGame()
    }

    private fun initializeButtons() {
        buttons = Array(3) { row ->
            Array(3) { col ->
                val buttonId = resources.getIdentifier("button$row$col", "id", packageName)
                val button = findViewById<ImageButton>(buttonId).apply {
                    setOnClickListener { onButtonClick(this) }
                    tag = Pair(row, col)
                    setBackgroundResource(R.drawable.square_purple)
                }
                button
            }
        }
    }

    private fun resetGame() {
        for (i in 0..2) {
            for (j in 0..2) {
                boardState[i][j] = 0
            }
        }

        // Reset all buttons
        for (row in buttons.indices) {
            for (col in buttons[row].indices) {
                buttons[row][col].apply {
                    isEnabled = true
                    setImageResource(0)
                    setBackgroundResource(R.drawable.square_purple)
                }
            }
        }

        playerTurn = true
        gameOver = false

        val statusText = findViewById<TextView>(R.id.gameStatusTextView)
        statusText.text = "Tour de $pseudo"
    }

    private fun onButtonClick(button: ImageButton) {
        if (gameOver) return

        val (row, col) = button.tag as Pair<Int, Int>
        boardState[row][col] = if (playerTurn) 1 else 2

        // Add X or O image
        button.setImageResource(if (playerTurn) R.drawable.x_symbol else R.drawable.o_symbol)
        button.isEnabled = false

        if (checkWin()) {
            gameOver = true
            val winner = if (playerTurn) "$pseudo" else "Joueur O"
            gameStatusTextView.text = if (playerTurn) "$pseudo a gagné!" else "Joueur O a gagné!"
            disableAllButtons()
            highlightWinningLine()

            // pop up delay
            Handler(Looper.getMainLooper()).postDelayed({
                showBeautifulGameOverDialog(
                    isWin = true,
                    isPlayer = playerTurn,
                    playerName = pseudo
                )
            }, 500)

        } else if (checkDraw()) {
            gameOver = true
            gameStatusTextView.text = "Match Nul!"

            // pop up draw
            Handler(Looper.getMainLooper()).postDelayed({
                showBeautifulGameOverDialog(
                    isWin = false,
                    isPlayer = false,
                    playerName = ""
                )
            }, 500)

        } else {
            playerTurn = !playerTurn
            gameStatusTextView.text = if (playerTurn) "Tour de $pseudo" else "Tour du Joueur O"
            if (!playerTurn) {
                Handler(Looper.getMainLooper()).postDelayed({
                    makeCPUMove()
                }, 500)
            }
        }
    }

    private fun checkWin(): Boolean {
        // Check rows, columns, and diagonals
        for (i in 0..2) {
            if ((boardState[i][0] == boardState[i][1]) && (boardState[i][1] == boardState[i][2]) && boardState[i][0] != 0) return true
            if ((boardState[0][i] == boardState[1][i]) && (boardState[1][i] == boardState[2][i]) && boardState[0][i] != 0) return true
        }
        if ((boardState[0][0] == boardState[1][1]) && (boardState[1][1] == boardState[2][2]) && boardState[0][0] != 0) return true
        if ((boardState[0][2] == boardState[1][1]) && (boardState[1][1] == boardState[2][0]) && boardState[0][2] != 0) return true
        return false
    }

    private fun highlightWinningLine() {
        val lightPurple = ContextCompat.getColor(this, R.color.light_purple)

        // Check rows
        for (i in 0..2) {
            if ((boardState[i][0] == boardState[i][1]) && (boardState[i][1] == boardState[i][2]) && boardState[i][0] != 0) {
                buttons[i][0].setBackgroundColor(lightPurple)
                buttons[i][1].setBackgroundColor(lightPurple)
                buttons[i][2].setBackgroundColor(lightPurple)
                return
            }
        }

        // Check columns
        for (j in 0..2) {
            if ((boardState[0][j] == boardState[1][j]) && (boardState[1][j] == boardState[2][j]) && boardState[0][j] != 0) {
                buttons[0][j].setBackgroundColor(lightPurple)
                buttons[1][j].setBackgroundColor(lightPurple)
                buttons[2][j].setBackgroundColor(lightPurple)
                return
            }
        }

        // Check diagonals
        if ((boardState[0][0] == boardState[1][1]) && (boardState[1][1] == boardState[2][2]) && boardState[0][0] != 0) {
            buttons[0][0].setBackgroundColor(lightPurple)
            buttons[1][1].setBackgroundColor(lightPurple)
            buttons[2][2].setBackgroundColor(lightPurple)
            return
        }

        if ((boardState[0][2] == boardState[1][1]) && (boardState[1][1] == boardState[2][0]) && boardState[0][2] != 0) {
            buttons[0][2].setBackgroundColor(lightPurple)
            buttons[1][1].setBackgroundColor(lightPurple)
            buttons[2][0].setBackgroundColor(lightPurple)
            return
        }
    }

    private fun checkDraw(): Boolean = boardState.all { row -> row.all { it != 0 } }

    private fun makeCPUMove() {
        val move = findBestMove()
        buttons[move.first][move.second].performClick()
    }

    private fun findBestMove(): Pair<Int, Int> {
        // 1. Check for a winning move for the CPU
        findWinningMove(2)?.let { return it }

        // 2. Block the player's winning move
        findWinningMove(1)?.let { return it }

        // 3. Choose a random empty square
        return findRandomEmptySquare()
    }

    private fun findWinningMove(player: Int): Pair<Int, Int>? {
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (boardState[i][j] == 0) {
                    boardState[i][j] = player
                    val win = checkWin()
                    boardState[i][j] = 0 // Undo the move
                    if (win) {
                        return Pair(i, j)
                    }
                }
            }
        }
        return null
    }

    private fun findRandomEmptySquare(): Pair<Int, Int> {
        val emptySquares = mutableListOf<Pair<Int, Int>>()
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                if (boardState[i][j] == 0) {
                    emptySquares.add(Pair(i, j))
                }
            }
        }
        return if (emptySquares.isNotEmpty()) emptySquares.random() else Pair(0, 0)
    }

    private fun disableAllButtons() {
        buttons.forEach { row ->
            row.forEach { button ->
                button.isEnabled = false
            }
        }
    }

    // popup
    private fun showBeautifulGameOverDialog(isWin: Boolean, isPlayer: Boolean, playerName: String) {
        val title: String
        val message: String
        val iconResId: Int

        if (isWin) {
            if (isPlayer) {
                // Player wins
                title = "FÉLICITATIONS !"
                message = "$playerName, tu as gagné !\n\nTu es un vrai champion(ne) du Tic Tac Toe !"
            } else {
                // AI wins
                title = "L'IA a gagné"
                message = "Player O a remporté la partie.\n\nL'intelligence artificielle a été plus forte cette fois !"
            }
        } else {
            // Draw
            title = "MATCH NUL"
            message = "C'est une égalité parfaite !\n\nAucun gagnant cette fois, mais quelle belle partie !"
        }

        val builder = AlertDialog.Builder(this, R.style.BeautifulDialogTheme)

        val dialogView = layoutInflater.inflate(R.layout.dialog_game_over, null)
        builder.setView(dialogView)

        val titleView = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val messageView = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val iconView = dialogView.findViewById<TextView>(R.id.dialogIcon) // Using TextView for emoji

        titleView.text = title
        messageView.text = message
        iconView.text = when {
            isWin && isPlayer -> "🏆"
            isWin && !isPlayer -> "🤖"
            else -> "⚖️"
        }

        val dialog = builder.create()

        // Set button click listeners
        dialogView.findViewById<TextView>(R.id.btnRestart).setOnClickListener {
            restartGame(null)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun restartGame(view: View?) {
        resetGame()
    }
}