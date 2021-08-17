package com.devkazonovic.projects.mytasks.presentation.reminder


/*
private const val KEY_TASK_ID = "Task ID"

@UninstallModules(RepositoryModule::class)
@HiltAndroidTest
class AlarmFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var navController: NavController

    @Before
    fun init() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun whenTaskReminderIsNull() {
        //Given
        val args = bundleOf(KEY_TASK_ID to 1L)
        launchFragmentInHiltContainer<ReminderFragment>(args, R.style.Theme_MyTasks) {
            this.also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        // The fragment’s view has just been created
                        navController.setGraph(R.navigation.main_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        //Asserts
        assertViewIsNotDisplayed(R.id.viewClearDate)
        assertViewIsNotDisplayed(R.id.viewClearTime)
    }

    @Test
    fun whenTaskReminderIsNotNull() {

        //Given
        val args = bundleOf(KEY_TASK_ID to 2L)
        launchFragmentInHiltContainer<ReminderFragment>(args, R.style.Theme_MyTasks) {
            this.also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        // The fragment’s view has just been created
                        navController.setGraph(R.navigation.main_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        //Assert
        assertViewIsDisplayed(R.id.textViewDatePicker)
        assertViewIsDisplayed(R.id.textViewTimePicker)

        assertViewIsDisplayed(R.id.viewClearDate)
        assertViewIsDisplayed(R.id.viewClearTime)

        onView(withId(R.id.textViewDatePicker))
            .check(matches(withText("FRIDAY, JULY 16, 2021\n")))

        onView(withId(R.id.textViewTimePicker))
            .check(matches(withText("11:00")))


    }

    @Test
    fun showClearIcons_whenDateIsSelected() {
        val args = bundleOf(KEY_TASK_ID to 1L)
        launchFragmentInHiltContainer<ReminderFragment>(args, R.style.Theme_MyTasks) {
            this.also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        // The fragment’s view has just been created
                        navController.setGraph(R.navigation.main_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        onView(withId(R.id.viewAddDate)).perform(click())

        onView(withText(equalToIgnoringCase(TAG_DATE_PICKER_CONFIRM_BUTTON_TAG)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

        assertViewIsDisplayed(R.id.viewClearDate)

    }

    @Test
    fun showClearIcons_whenTimeIsSelected() {
        val args = bundleOf(KEY_TASK_ID to 1L)
        launchFragmentInHiltContainer<ReminderFragment>(args, R.style.Theme_MyTasks) {
            this.also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        // The fragment’s view has just been created
                        navController.setGraph(R.navigation.main_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        onView(withId(R.id.viewAddTime)).perform(click())

        onView(withText(equalToIgnoringCase(TAG_TIME_PICKER_CONFIRM_BUTTON_TAG)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

        assertViewIsDisplayed(R.id.viewClearTime)
    }

    @Test
    fun hideClearIcons_whenDateIsNotSelected() {
        val args = bundleOf(KEY_TASK_ID to 1L)
        launchFragmentInHiltContainer<ReminderFragment>(args, R.style.Theme_MyTasks) {
            this.also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        // The fragment’s view has just been created
                        navController.setGraph(R.navigation.main_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        onView(withId(R.id.viewAddDate)).perform(click())

        onView(withText(equalToIgnoringCase(TAG_DATE_PICKER_CANCEL_BUTTON_TAG)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

        assertViewIsNotDisplayed(R.id.viewClearDate)

    }

    @Test
    fun hideClearIcons_whenTimeIsNotSelected() {
        val args = bundleOf(KEY_TASK_ID to 1L)
        launchFragmentInHiltContainer<ReminderFragment>(args, R.style.Theme_MyTasks) {
            this.also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        // The fragment’s view has just been created
                        navController.setGraph(R.navigation.main_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        onView(withId(R.id.viewAddTime)).perform(click())

        onView(withText(equalToIgnoringCase(TAG_TIME_PICKER_CANCEL_BUTTON_TAG)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

        assertViewIsNotDisplayed(R.id.viewClearTime)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryTestModule {
        @Singleton
        @Binds
        abstract fun provideTasksRepository(repository: FakeTasksRepositoryTaskTest): ITasksRepository

        @Singleton
        @Binds
        abstract fun provideMappers(mappers: Mappers): IMappers
    }


}

*/
