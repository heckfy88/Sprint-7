package ru.sber.rdbms

import java.sql.Connection
import java.sql.DriverManager

class TransferConstraint {

    private val connection: Connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5433/postgres",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use {
            connection -> try {
                val getMoneyStatement =
                    connection.prepareStatement("update account1 set amount = amount - $amount where id = $accountId1")
                getMoneyStatement.use {
                    preparedStatement ->  preparedStatement.executeUpdate()
                }
                val sendMoneyStatement =
                    connection.prepareStatement("update account1 set amount = amount + $amount where id = $accountId2")
                sendMoneyStatement.use {
                    preparedStatement ->  preparedStatement.executeUpdate()
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }



    }
}
