package ru.sber.rdbms

import java.sql.Connection
import java.sql.DriverManager

class TransferPessimisticLock {
    private val connection: Connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5433/postgres",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        connection.use {
                connection ->  val autoCommit = connection.autoCommit
            try {
                connection.autoCommit = false
                val preparedStatement1 =
                    connection.prepareStatement("select * from account1 where id = $accountId1")
                preparedStatement1.executeQuery().use {
                        resultSet ->  resultSet.next()
                    if (resultSet.getLong("amount") < amount)
                        throw MoneyOperationException("Insufficient balance")
                }
                val preparedStatement2 =
                    connection.prepareStatement("select * from account1 where id in ($accountId1, $accountId2) for update")
                preparedStatement2.use {
                    preparedStatement ->  preparedStatement.executeQuery()
                }
                val getMoneyStatement =
                    connection.prepareStatement("update account1 set amount = amount - $amount where id = $accountId1")
                getMoneyStatement.use {
                    preparedStatement ->  preparedStatement.executeQuery()
                }
                val sendMoneyStatement =
                    connection.prepareStatement("update account1 set amount = amount + $amount where id = $accountId2")
                sendMoneyStatement.use {
                        preparedStatement ->  preparedStatement.executeQuery()
                }
                connection.commit()
            } catch (e: Exception) {
                println(e.message)
                connection.rollback()
            } finally {
                connection.autoCommit = autoCommit
            }
        }

    }
}
