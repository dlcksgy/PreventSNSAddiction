package kr.ac.kau.sw.a2016125063.accountkotlin

/**
 * Created by Arduino on 2017-10-15.
 */
class CheckingAccount(accountNumber: String, balance: Int = 0, interest: Double = 0.0) : Account(accountNumber, balance){
    val interest = interest

    fun withdrow(amount: Int){
        balance -= amount
    }

    fun deposit(amount: Int){
        balance += amount

    }

    override fun aMonthHasPassed() {
        balance = (balance*(1+interest)).toInt()
    }


}