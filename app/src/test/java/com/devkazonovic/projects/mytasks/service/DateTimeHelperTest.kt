package com.devkazonovic.projects.mytasks.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.devkazonovic.projects.mytasks.data.local.preference.ISettingSharedPreference
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.mockito.kotlin.mock
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

private const val july_22_2021 = 1626912000000L //(GMT)Thursday, July 22, 2021 0:00:00
private val time_9_00 = Pair(9, 0)

private const val july_22_2021_9_00_GMT = 1626944400000L //=> Thursday, July 22, 2021 9:00:00 GMT
private const val july_22_2021_9_00_GMTplus1 =
    1626940800000L //=> Thursday, July 22, 2021 9:00:00 GMT+1
private const val july_22_2021_9_00_01_GMTplus1 =
    1626940801000L //=> Thursday, July 22, 2021 9:00:01 GMT+1

private const val july_22_2021_0_00_GMT = 1626912000000L //=> Thursday, July 22, 2021 0:00:00 GMT
private const val july_22_2021_0_00_GMTplus1 =
    1626908400000L //=> Thursday, July 22, 2021 0:00:00 GMT+1


class DateTimeHelperTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val mainSettingPreference : ISettingSharedPreference = mock()
    private val dateTimeHelper = DateTimeHelper(
        context,
        Clock.system(ZoneId.systemDefault()),
        mainSettingPreference
    )

    /** BASE RULE:
     * groupDateTime return TimeStamp
     * When Converting This TimeStamp to LocalDateTime,
    we should get Date&Time in User Current Time-Zone & not UTC*/
    @Test
    fun groupDateTime_withSampleTime() {
        val expected = july_22_2021_9_00_GMTplus1
        val actual = dateTimeHelper.groupDateTime(july_22_2021, 9, 0)
        Truth.assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun groupDateTime_withFirstHour_shouldNotMoveToTheNextDay() {
        val expected = july_22_2021_0_00_GMTplus1
        val actual = dateTimeHelper.groupDateTime(july_22_2021, 0, 0)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun getDateFromTimeStamp() {
        val expected = july_22_2021

        val actual1 = dateTimeHelper.getDateInMillis(july_22_2021_9_00_GMTplus1)
        val actual2 = dateTimeHelper.getDateInMillis(july_22_2021_9_00_GMTplus1)

        assertThat(actual1).isEqualTo(expected)
        assertThat(actual2).isEqualTo(expected)

    }

    @Test
    fun getTimeFromTimeStamp() {
        val expected = time_9_00
        val actual1 = dateTimeHelper.getTimeInHourMinute(july_22_2021_9_00_GMTplus1)
        Truth.assertThat(actual1).isEqualTo(expected)
    }

    @Test
    fun isNow() {
        val now = july_22_2021_9_00_GMTplus1
        val dateTimeHelper = DateTimeHelper(
            context,
            Clock.fixed(
                Instant.ofEpochMilli(now),
                ZoneId.systemDefault()
            ),
            mainSettingPreference
        )

        assertThat(dateTimeHelper.isNow(july_22_2021_9_00_GMTplus1)).isTrue()
    }

    @Test
    fun isNotAfter_isBefore_now() {
        val now = july_22_2021_9_00_01_GMTplus1
        val dateTimeHelper = DateTimeHelper(
            context,
            Clock.fixed(
                Instant.ofEpochMilli(now),
                ZoneId.systemDefault()
            ),
            mainSettingPreference
        )

        val actual1 = dateTimeHelper.isAfterNow(july_22_2021_9_00_GMTplus1)
        Truth.assertThat(actual1).isFalse()

        val actual2 = dateTimeHelper.isBeforeNow(july_22_2021_9_00_GMTplus1)
        Truth.assertThat(actual2).isTrue()
    }

    @Test
    fun isAfter_isNotBefore_now() {
        val now = july_22_2021_9_00_GMTplus1
        val dateTimeHelper = DateTimeHelper(
            context,
            Clock.fixed(
                Instant.ofEpochMilli(now),
                ZoneId.systemDefault()
            ),
            mainSettingPreference
        )


        assertThat(dateTimeHelper.isAfterNow(july_22_2021_9_00_01_GMTplus1))
            .isTrue()
        assertThat(dateTimeHelper.isBeforeNow(july_22_2021_9_00_01_GMTplus1))
            .isFalse()
    }

}