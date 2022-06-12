package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.R

class BlurWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    override fun doWork() : Result {

        val appContext = applicationContext
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        makeStatusNotification("Blurring image", appContext)
        sleep()
        return try {

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e("BlurWorker", "Invalid input uri")
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = appContext.contentResolver
            val picture = BitmapFactory.decodeStream(resolver.openInputStream(Uri.parse(resourceUri)))


//            val picture =
//                BitmapFactory.decodeResource(appContext.resources, R.drawable.android_cupcake)

            val output = blurBitmap(picture, appContext)

            val outputUri = writeBitmapToFile(appContext, output)

            makeStatusNotification(outputUri.toString(), appContext)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
            Result.success(outputData)
        } catch (t: Throwable) {

            Log.e("BlurWorker", "doWork: Error applying blur", t)
            Result.failure()
        }
    }
}