package com.fintech.sst

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
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
}
