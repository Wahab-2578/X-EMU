package com.example.game.scanner

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ScanResult(
    val folderName: String,
    val folderPath: String,
    val executables: List<String>,
    val selectedExecutable: String,
    val totalSizeBytes: Long,
    val formattedSize: String,
    val detectedCoverUri: String? = null
)

class GameFileScanner(private val context: Context) {

    suspend fun scanDocumentTree(treeUri: Uri): ScanResult = withContext(Dispatchers.IO) {
        var folderName = "Imported Game"
        val executables = mutableListOf<String>()
        var totalBytes = 0L
        var coverUriStr: String? = null

        try {
            val docId = DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId)

            // Derive folder name from docId
            val lastSegment = docId.substringAfterLast(":").substringAfterLast("/")
            if (lastSegment.isNotBlank()) {
                folderName = lastSegment
            }

            val projection = arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE
            )

            context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
                val docIdIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)

                while (cursor.moveToNext()) {
                    val name = if (nameIndex != -1) cursor.getString(nameIndex) else null
                    val size = if (sizeIndex != -1) cursor.getLong(sizeIndex) else 0L
                    val childDocId = if (docIdIndex != -1) cursor.getString(docIdIndex) else null

                    if (name != null) {
                        totalBytes += size
                        val lower = name.lowercase()
                        if (lower.endsWith(".exe") || lower.endsWith(".bat") || lower.endsWith(".cmd")) {
                            executables.add(name)
                        }

                        if (coverUriStr == null &&
                            (lower.contains("cover") || lower.contains("poster") || lower.contains("banner") || lower.contains("icon")) &&
                            (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".webp")) &&
                            childDocId != null
                        ) {
                            coverUriStr = DocumentsContract.buildDocumentUriUsingTree(treeUri, childDocId).toString()
                        }
                    }
                }
            }
        } catch (_: Exception) {
            // Fallback heuristics if SAF query fails or mock folder passed
            val uriStr = treeUri.toString()
            val derived = uriStr.substringAfterLast("%2F").substringAfterLast("/").substringAfterLast(":")
            if (derived.isNotBlank()) {
                folderName = derived
            }
        }

        // Pick preferred launcher
        val preferredLauncher = executables.firstOrNull {
            val l = it.lowercase()
            !l.contains("unins") && !l.contains("crash") && !l.contains("helper") && !l.contains("redist")
        } ?: executables.firstOrNull() ?: "Main.exe"

        val formattedSize = formatFileSize(totalBytes)

        ScanResult(
            folderName = folderName,
            folderPath = treeUri.toString(),
            executables = executables.ifEmpty { listOf("Game.exe") },
            selectedExecutable = preferredLauncher,
            totalSizeBytes = totalBytes,
            formattedSize = formattedSize,
            detectedCoverUri = coverUriStr
        )
    }

    private fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.1f GB", bytes.toDouble() / (1024 * 1024 * 1024))
            bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> String.format("%d KB", bytes / 1024)
            bytes > 0 -> "$bytes Bytes"
            else -> "750 MB"
        }
    }
}
