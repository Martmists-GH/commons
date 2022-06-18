package com.martmists.commons.database

import com.martmists.commons.logging.logger
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.util.concurrent.CompletableFuture
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import org.jetbrains.exposed.sql.transactions.transaction as dbTransaction


open class ThreadedDatabase(name: String? = "Database", createDb: () -> Database) : AutoCloseable {
    private val log by logger()
    private val queue = LinkedBlockingQueue<Entry<Any?>>()
    private var running = true
    private val db = createDb()
    private val sqlLogger = DBLogger()

    private val databaseThread = thread(start = true, name = "$name Thread", isDaemon = true) {
        while (running) {
            val entry = queue.poll(20, TimeUnit.MILLISECONDS) ?: continue

            try {
                val value = dbTransaction(db) {
                    addLogger(sqlLogger)
                    val x = entry.transaction(this)
                    x
                }

                entry.future.complete(value)
            } catch(e: Exception) {
                log.error("Error on Database Thread! Please report to the mod author if this is unexpected!", e)
                log.error("Stacktrace before database thread:\n" + entry.trace.joinToString("\n"))
                entry.future.completeExceptionally(e)
            }
        }
    }

    private class Entry<T>(val transaction: Transaction.() -> T, val future: CompletableFuture<T>, val trace: Array<StackTraceElement>)

    inner class DBLogger : SqlLogger {
        override fun log(context: StatementContext, transaction: Transaction) {
            log.debug(context.expandArgs(TransactionManager.current()))
        }
    }

    fun <T : Any?> transaction(callback: Transaction.() -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        val trace = Thread.currentThread().stackTrace
        queue.add(Entry(callback, future, trace) as Entry<Any?>)
        return future
    }

    override fun close() {
        running = false
        databaseThread.join()
    }
}
