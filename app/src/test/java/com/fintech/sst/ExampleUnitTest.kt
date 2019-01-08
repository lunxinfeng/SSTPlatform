package com.fintech.sst

import com.fintech.sst.data.db.Notice
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test(){
        Observable.interval(10 * 1000, TimeUnit.MILLISECONDS)
                .map {
                    println("map1 $it")
                    "map1 $it"
                }
                .delay(5000, TimeUnit.MILLISECONDS)
                .subscribe{ listSql ->
                    Observable.just(233)
                            .subscribeOn(Schedulers.io())
                            .map { it ->
                                println("map2 $it")
                                "map2 $it"
                            }
                            .subscribe { it ->
                                println("自动补单$listSql\t$it")
                            }
                }

        Thread.sleep(20000)
    }

    @Test
    fun addition_test() {
        var notice1 = Notice()
        notice1.amount = "5.0"
//        SystemClock.sleep(1000)
        var i = 0
        while (i<10000){
            i++
        }
        var notice2 = Notice()
        notice2.amount = "6.0"
        println(notice1)
        println(notice2)
        notice1 = notice2
        println(notice1)
        println(notice2)
    }

    @Test
    fun addition_isCorrect() {
//        val info = 1.00
//        println((info?.toString()?.toFloatOrNull() ?: 0f) * 100)
        val list1 = mutableListOf(1,2)
        val list2 = mutableListOf(2,3,4,5,6)
        list1.removeAll(list2)
        println(list1)

        val s1 = "1.0000"
        val s2 = "1"
        println(s1.toFloat() == s2.toFloat())
    }

    @Test
    fun parseAmountAli() {
        val s3 = "您尾号7981的储蓄卡账户1月2日21时14分支付机构提现收入人民币699.38元,活期余额753.07元。[建设银行]"
        val s4 = "您尾号*6791的卡于12月25日21:50支付平台网银转入3.00元,交易后余额为173128.54元。【交通银行】"
        var s5 = "您尾号0891卡12月26日14:55快捷支付收入(钟宇支付宝转账支付宝)1元，余额4元。【工商银行】"
        s5 = "您尾号0891卡12月26日14:55快捷支付收入(钟宇支付宝转账支付宝)1,999,999,998元，余额411.50元。【工商银行】"
        val s6 = "【中国农业银行】您尾号8114账户01月03日15:07完成代付交易人民币10.00，余额565.74。"
        var s7 = "尾号4501账户23:15存入99.99元，余额6062.71元，摘要:胡亮支付宝转账 胡亮支付宝转账。[光大银行]"
        s7 = "尾号4501账户23:15存入100元，余额7726.7元，摘要:胡亮支付宝转账 胡亮支付宝转账。[光大银行]"
        val s8 = "【邮储银行】19年01月05日09:45您尾号044账户提现金额30.00元，余额10512.01元。"

//        var pattern = "人民币(\\d+\\.?\\d{0,2})元,活期余额(\\d+\\.?\\d{0,2})元。"
//        var pattern = "(.*)转入(\\d+\\.?\\d{0,2})元,交易后余额为(\\d+\\.?\\d{0,2})元。【交通银行】"
//        var pattern = "收入(.*)\\)(\\d+(,\\d+)*(\\.\\d{0,2})?)元，余额(\\d+(,\\d+)*(\\.\\d{0,2})?)元。【工商银行】"
//        var pattern = "(.*)人民币(\\d+\\.?\\d{0,2})，余额(\\d+\\.?\\d{0,2})。"
        var pattern = "(.*)存入(\\d+\\.?\\d{0,2})元，余额(\\d+\\.?\\d{0,2})元"
        var r = Pattern.compile(pattern)
        val m = r.matcher(s7)
        if (m.find()) {
            println(m)
            val group = m.group(2)
            println(group.replace(",",""))
        }else{
            println(0)
        }
    }

    @Test
    fun parse() {
        for (i in 0..9) {
            val text = if (i == 3 || i == 5) "收款5.00元" else "1"
            val amount = parseAmountWeChat(text)
            println(amount)
        }
    }

    fun parseAmountWeChat(text: String): Float {

        val pattern = "收款([0-9]+.[0-9][0-9])元$"
        val r = Pattern.compile(pattern)
        val m = r.matcher(text)
        if (m.find()) {
            val group = m.group(1)
            return java.lang.Float.parseFloat(group)
        }
        return 0f
    }
}
