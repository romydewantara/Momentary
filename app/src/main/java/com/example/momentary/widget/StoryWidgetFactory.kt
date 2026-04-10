package com.example.momentary.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.momentary.R
import com.example.momentary.data.model.Story
import com.example.momentary.di.entrypoint.WidgetEntryPoint
import com.example.momentary.ui.common.ResultState
import com.example.momentary.utils.ImageUtils.loadBitmap
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.runBlocking

class StoryWidgetFactory(
    private val context: Context
) : RemoteViewsService.RemoteViewsFactory {

    private val repository = EntryPointAccessors.fromApplication(
        context,
        WidgetEntryPoint::class.java
    ).repository()

    private var stories: List<Story> = emptyList()

    override fun onDataSetChanged() {
        runBlocking {
            stories = when (val result = repository.getStories(
                page = 1,
                size = 10,
                location = 0
            )
            ) {
                is ResultState.Success -> result.data
                else -> emptyList()
            }
        }
    }

    override fun getCount() = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val story = stories[position]

        val view = RemoteViews(context.packageName, R.layout.widget_story_item)

        val bitmap = loadBitmap(story.photoUrl)
        view.setImageViewBitmap(R.id.image, bitmap)

        return view
    }

    override fun getLoadingView() = null
    override fun getViewTypeCount() = 1
    override fun getItemId(position: Int) = position.toLong()
    override fun hasStableIds() = true

    override fun onCreate() {}
    override fun onDestroy() {}
}
