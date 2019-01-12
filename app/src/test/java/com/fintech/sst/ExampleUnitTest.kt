package com.fintech.sst

import com.fintech.sst.data.db.Notice
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
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
        val s3 = "您尾号7981的储蓄卡账户01月2日21时14分支付机构提现收入人民币699.38元,活期余额753.07元。[建设银行]"
        val s4 = "您尾号*6791的卡于12月25日21:50支付平台网银转入3.00元,交易后余额为173128.54元。【交通银行】"
        var s5 = "您尾号0891卡12月26日14:55快捷支付收入(钟宇支付宝转账支付宝)1元，余额4元。【工商银行】"
        s5 = "您尾号0891卡12月26日14:55快捷支付收入(钟宇支付宝转账支付宝)1,999,999,998元，余额411.50元。【工商银行】"
        val s6 = "【中国农业银行】您尾号8114账户01月03日15:07完成代付交易人民币10.00，余额565.74。"
        var s7 = "尾号4501账户23:15存入99.99元，余额6062.71元，摘要:胡亮支付宝转账 胡亮支付宝转账。[光大银行]"
        s7 = "尾号4501账户23:15存入100元，余额7726.7元，摘要:胡亮支付宝转账 胡亮支付宝转账。[光大银行]"
        val s8 = "【邮储银行】19年01月05日09:45您尾号044账户提现金额30.00元，余额10512.01元。"
        val s9 = "【湖南农信】您尾号1554的卡于12月27日11时28分转入人民币5.00元，当前余额为25.00元。如有疑问请详询0731-96518。"
        val s10 = "账户*6338于01月11日10:41存入￥15.00元，可用余额1031.00元。罗生伟支付宝转账-罗生伟支付宝转账-支付宝（中国）网络技术有限公司。【民生银行】"
        val s11 = "您尾号2523的账户于1月12日10:28付款业务转入人民币800.00元,存款账户余额人民币34850.04元。详询95511-3【平安银行】"
        val s12 = "11日19:39账户*4574*网联付款收入199.97元，余额98342.14元[兴业银行]"
        val s13 = "您的借记卡账户2926，于01月11日网上支付收入人民币284.00元，交易后余额9634.31【中国银行】"

//        var pattern = "(\\d{1,2})月(\\d{1,2})日(\\d{1,2})[时:](\\d{1,2})分?[^\\d]+(?:[\\d]+[^\\d]+)?[币入额\\)￥](\\d+(,\\d+)*(\\.\\d{0,2})?)(?:元,|元，|,)"
//        var pattern = "(\\d{1,2})日(\\d{1,2})[时:](\\d{1,2})分?[^\\d]+(?:[\\d]+[^\\d]+)?[币入额\\)￥](\\d+(,\\d+)*(\\.\\d{0,2})?)(?:元,|元，|,)"
//        var pattern = "(\\d{1,2})[时:](\\d{1,2})分?[^\\d]+[币入额\\)￥](\\d+(,\\d+)*(\\.\\d{0,2})?)(?:元,|元，|,)"
        var pattern = "(?:.*)[币入额\\)￥](\\d+(,\\d+)*(\\.\\d{0,2})?)(?:元,|元，|,)"
        var r = Pattern.compile(pattern)
        val m = r.matcher(s13)
        if (m.find()) {
            println(m)
            val month = m.group(1).toInt()
            println(month)
            val day = m.group(2).toInt()
            println(day)
            val hour = m.group(3).toInt()
            println(hour)
            val min = m.group(4).toInt()
            println(min)

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            val date = Calendar.getInstance()
            println(sdf.format(date.time))
            date.set(date.get(Calendar.YEAR),month - 1,day,hour,min,0)
            println(sdf.format(date.time))

            val amount = m.group(5)
            println(amount.replace(",",""))
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
