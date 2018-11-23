package com.fintech.sst

import com.fintech.sst.data.db.Notice
import org.junit.Test
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
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
        val info = 1.00
        println((info?.toString()?.toFloatOrNull() ?: 0f) * 100)
    }

    @Test
    fun parseAmountAli() {
        val s1 = "风一样的男子通过扫码向你付款88.00元"
        val s2 = "成功收款3000.00元。:成功收款0.96元。享免费提现等更多专属服务，点击查看"

        var pattern = "付款([0-9]+.[0-9][0-9])元$"
        var r = Pattern.compile(pattern)
        val m = r.matcher(s2)
        if (m.find()) {
            val group = m.group(1)
            print(group)
        } else {
            pattern = "成功收款([0-9]+.[0-9][0-9])元。"
            r = Pattern.compile(pattern)
            val n = r.matcher(s2)
            var group: String? = null
            while (n.find()) {
                group = n.group(1)
            }
            if (group != null)
                print(group)
            else
                print(0)
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
