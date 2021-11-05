package ru.sber.rdbms

import java.sql.Connection
import java.sql.DriverManager

class TransferOptimisticLock {

    private val connection: Connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5433/db",
        "postgres",
        "postgres"
    )

    fun transfer(accountId1: Long, accountId2: Long, amount: Long) {
        var actualVersion: Int
        connection.use {
            connection ->  val autoCommit = connection.autoCommit
            try {
                connection.autoCommit = false
                val preparedStatement1 =
                    connection.prepareStatement("select * from account1 where id = $accountId1")
                preparedStatement1.use {
                    preparedStatement ->  preparedStatement.executeQuery().use {
                        resultSet ->  resultSet.next()
                        if (resultSet.getInt("amount") - amount < 0) {
                            throw MoneyOperationException("Insufficient balance")
                        }
                        actualVersion = resultSet.getInt("version")
                    }
                }
                val preparedStatement2 =
                    connection.prepareStatement("update account1 set amount = amount - $amount, version = version + 1 " +
                            "where id = $accountId1 and version = $actualVersion")
                var result = preparedStatement2.executeUpdate()
                if (result == 0) {
                    throw FaultyDataException("Faulty Data")
                }
                val preparedStatement3 =
                    connection.prepareStatement("select * from account1 where id = $accountId2")
                preparedStatement3.executeQuery().use {
                    resultSet ->  resultSet.next()
                    actualVersion = resultSet.getInt("version")
                }
                val preparedStatement4 =
                    connection.prepareStatement("update account1 set amount = amount + $amount, version = version + 1 " +
                            "where id = $accountId2 and version = $actualVersion")
                result = preparedStatement4.executeUpdate()
                if (result == 0) {
                    throw FaultyDataException("Faulty Data")
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
