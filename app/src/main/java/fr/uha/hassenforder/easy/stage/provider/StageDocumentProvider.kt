package fr.uha.hassenforder.easy.stage.provider

import android.content.Context
import android.net.Uri
import fr.uha.hassenforder.easy.android.provider.DocumentProvider
import fr.uha.hassenforder.easy.stage.R

class StageDocumentProvider : DocumentProvider(R.xml.file_paths) {

    companion object {

        fun getResourceDocumentUri(context: Context, id: Int): Uri {
            return DocumentProvider.getResourceDocumentUri(context, id)
        }

        fun createExternalImageFilename(
            prefix: String = "photo",
            extension: String? = "jpg"
        ): String {
            return DocumentProvider.createExternalImageFilename(prefix, extension)
        }

        fun createExternalImageUri(
            context: Context,
            prefix : String = "photo",
            extension : String? = "jpg"
        ): Uri {
            return DocumentProvider.createExternalImageUri(context, prefix, extension)
        }

    }
}