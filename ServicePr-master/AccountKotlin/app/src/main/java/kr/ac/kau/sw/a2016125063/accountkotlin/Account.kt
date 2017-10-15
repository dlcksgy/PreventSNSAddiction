package kr.ac.kau.sw.a2016125063.accountkotlin

/**
 * Created by Arduino on 2017-10-15.
 */
abstract class Account(accountNumber: String, balance: Int = 0){
    val accountNumber = accountNumber
    var balance = balance
        protected set(newBalance){
            println("${balance} -> ${newBalance}")
            field = newBalance
        }

    override fun toString(): String {
        return "${accountNumber} : ${balance}"
    }
    abstract fun aMonthHasPassed()


}