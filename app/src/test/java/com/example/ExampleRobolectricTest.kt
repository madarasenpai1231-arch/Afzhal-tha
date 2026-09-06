package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("RizzX", appName)
  }

  @Test
  fun `rizz engine generates starters and rescue strategies`() {
    val starters = com.example.data.repository.RizzEngine.getStartersForCategory("crush")
    assert(starters.isNotEmpty())
    val rescue = com.example.data.repository.RizzEngine.rescueSituations
    assert(rescue.isNotEmpty())
  }
}
