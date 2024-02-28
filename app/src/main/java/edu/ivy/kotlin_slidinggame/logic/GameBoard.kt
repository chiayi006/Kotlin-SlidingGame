package edu.ivy.kotlin_slidinggame.logic

import kotlin.random.Random

class GameBoard {
    private val grid = arrayOf(
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK),
        arrayOf(Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK, Player.BLANK)
    )
    private val DIM = 5
    private var currentPlayer: Player = Player.X

    fun aiMove(): Int {
        // check rows
        for (i in 0 until DIM){
            for (j in 0 .. 2) {
                var threeInARow = true
                val firstElement = grid[i][j]
                for (k in j .. j + 2) {
                    if (grid[i][k] != firstElement) {
                        threeInARow = false
                        break
                    }
                }
                if (threeInARow && firstElement == Player.X) {
                    return i + 5
                }
            }
        }

        // check columns
        for (i in 0 until DIM){
            for (j in 0 .. 2) {
                var threeInARow = true
                val firstElement = grid[j][i]
                for (k in j .. j + 2) {
                    if (grid[k][i] != firstElement) {
                        threeInARow = false
                        break
                    }
                }
                if (threeInARow && firstElement == Player.X) {
                    return i
                }
            }
        }

        // no three in a row
        return Random.nextInt(0,10)
    }

    fun submitMove(move: Char) {
        if (move in '1'..'5') {
            val column = move.toString().toInt() - 1
            val neighbors = ArrayList<Player>()
            for (row in 0 until DIM) {
                if (grid[row][column] != Player.BLANK) {
                    neighbors.add(grid[row][column])
                } else {
                    break
                }
            }
            for (index in neighbors.indices) {
                if (index + 1 < DIM) {
                    grid[index + 1][column] = neighbors[index]
                }
            }
            grid[0][column] = currentPlayer
        } else {
            val row = (move.code - 'A'.code)
            val neighbors = ArrayList<Player>()
            for (column in 0 until DIM) {
                if (grid[row][column] != Player.BLANK) {
                    neighbors.add(grid[row][column])
                } else {
                    break
                }
            }
            for (index in neighbors.indices) {
                if (index + 1 < DIM) {
                    grid[row][index + 1] = neighbors[index]
                }
            }
            grid[row][0] = currentPlayer
        }

        currentPlayer = if (currentPlayer == Player.X) Player.O else Player.X
//        for (arr in grid) {
//            println("")
//            for (element in arr) {
//                print("$element, ")
//            }
//            println("")
//        }
//        println("=====================================")
    }

    fun checkWinners(): Player {
        val winners: ArrayList<Player> = ArrayList()

        // check all rows
        for (i in 0 until DIM) {
            if (grid[i][0] != Player.BLANK) {
                val firstElement = grid[i][0]
                var allTheSame = true
                for (j in 0 until DIM) {
                    if (grid[i][j] != firstElement) {
                        allTheSame = false
                        break
                    }
                }
                if (allTheSame && !winners.contains(firstElement)) {
                    winners.add(firstElement)
                }
            }
        }
        if (winners.size == 1) {
            return winners[0]
        } else if (winners.size > 1) {
            return Player.TIE
        }

        // check all columns
        for (i in 0 until DIM) {
            if (grid[0][i] != Player.BLANK) {
                val firstElement = grid[0][i]
                var allTheSame = true
                for (j in 0 until DIM) {
                    if (grid[j][i] != firstElement) {
                        allTheSame = false
                        break
                    }
                }
                if (allTheSame && !winners.contains(firstElement)) {
                    winners.add(firstElement)
                }

            }
        }
        if (winners.size == 1) {
            return winners[0]
        } else if (winners.size > 1) {
            return Player.TIE
        }

        // check diagonals
        if (grid[0][0] != Player.BLANK) {
            val firstElement = grid[0][0]
            var allTheSame = true
            for (i in 0 until DIM) {
                if (grid[i][i] != firstElement) {
                    allTheSame = false
                    break
                }
            }
            if (allTheSame) {
                return firstElement
            }
        }

        if (grid[DIM - 1][0] != Player.BLANK) {
            val firstElement = grid[DIM - 1][0]
            var allTheSame = true
            for (i in 0 until DIM) {
                if (grid[DIM - 1 - i][i] != firstElement) {
                    allTheSame = false
                    break
                }
            }
            if (allTheSame) {
                return firstElement
            }
        }

        return Player.BLANK
    }
}