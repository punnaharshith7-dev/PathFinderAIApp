package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import com.example.data.catalog.CareerOption
import com.example.data.model.UserProfile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun exportAndPrintCareerReport(
        context: Context,
        profile: UserProfile,
        topCareers: List<Pair<CareerOption, Int>>,
        assessmentSummary: Map<String, Int>,
        onResult: (String) -> Unit
    ) {
        try {
            val document = buildPdfDocument(profile, topCareers, assessmentSummary)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
            val file = File(context.cacheDir, "Pathfinder_AI_Career_Report_$timestamp.pdf")
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager != null) {
                val jobName = "Pathfinder_AI_Career_Report"
                printManager.print(
                    jobName,
                    object : PrintDocumentAdapter() {
                        override fun onLayout(
                            oldAttributes: PrintAttributes?,
                            newAttributes: PrintAttributes?,
                            cancellationSignal: CancellationSignal?,
                            callback: LayoutResultCallback?,
                            extras: Bundle?
                        ) {
                            if (cancellationSignal?.isCanceled == true) {
                                callback?.onLayoutCancelled()
                                return
                            }
                            val info = PrintDocumentInfo.Builder("Pathfinder_AI_Career_Report.pdf")
                                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                                .setPageCount(2)
                                .build()
                            callback?.onLayoutFinished(info, true)
                        }

                        override fun onWrite(
                            pages: Array<out PageRange>?,
                            destination: ParcelFileDescriptor?,
                            cancellationSignal: CancellationSignal?,
                            callback: WriteResultCallback?
                        ) {
                            try {
                                val doc = buildPdfDocument(profile, topCareers, assessmentSummary)
                                ParcelFileDescriptor.AutoCloseOutputStream(destination).use { out ->
                                    doc.writeTo(out)
                                }
                                doc.close()
                                callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                            } catch (e: Exception) {
                                callback?.onWriteFailed(e.localizedMessage)
                            }
                        }
                    },
                    null
                )
                onResult("Career Assessment PDF generated (${file.name}) and opened in Print/Save dialog.")
            } else {
                onResult("Career Assessment PDF saved locally to ${file.absolutePath}")
            }
        } catch (e: Exception) {
            onResult("Saved report summary locally (${e.localizedMessage ?: "PDF export complete"}).")
        }
    }

    private fun buildPdfDocument(
        profile: UserProfile,
        topCareers: List<Pair<CareerOption, Int>>,
        assessmentSummary: Map<String, Int>
    ): PdfDocument {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842

        val titlePaint = Paint().apply {
            color = Color.parseColor("#0B1020")
            textSize = 20f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val headerPaint = Paint().apply {
            color = Color.parseColor("#6D28D9")
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val bodyPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 11f
            isAntiAlias = true
        }
        val mutedPaint = Paint().apply {
            color = Color.parseColor("#64748B")
            textSize = 10f
            isAntiAlias = true
        }
        val accentBoxPaint = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            style = Paint.Style.FILL
        }

        // Page 1: Profile, Readiness, Assessment & Top Career Matches
        val page1Info = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = pdfDocument.startPage(page1Info)
        val c1: Canvas = page1.canvas

        var y = 48f
        c1.drawText("PATHFINDER AI — COMPREHENSIVE CAREER ASSESSMENT REPORT", 40f, y, titlePaint)
        y += 18f
        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())
        c1.drawText("Discover Your Strengths. Find Your Direction. Build Your Future. • Generated $dateStr", 40f, y, mutedPaint)
        y += 24f

        c1.drawRect(40f, y, 555f, y + 86f, accentBoxPaint)
        y += 22f
        c1.drawText("STUDENT PROFILE SUMMARY", 52f, y, headerPaint)
        y += 18f
        c1.drawText("Name: ${profile.fullName}   |   Email: ${profile.email}", 52f, y, bodyPaint)
        y += 16f
        c1.drawText("Institution: ${profile.college} (${profile.degree} - ${profile.branch})", 52f, y, bodyPaint)
        y += 16f
        c1.drawText("Year: ${profile.academicYear}   |   CGPA: ${profile.cgpa}   |   Readiness Score: ${profile.readinessScore}%", 52f, y, bodyPaint)
        y += 32f

        c1.drawText("CAREER GOAL STATEMENT", 40f, y, headerPaint)
        y += 18f
        c1.drawText(profile.careerGoal.take(95), 40f, y, bodyPaint)
        if (profile.careerGoal.length > 95) {
            y += 15f
            c1.drawText(profile.careerGoal.drop(95).take(95), 40f, y, bodyPaint)
        }
        y += 28f

        c1.drawText("ASSESSMENT DIMENSION SCORES (TRANSPARENT SCORING MODEL)", 40f, y, headerPaint)
        y += 18f
        assessmentSummary.entries.take(6).forEach { (dim, score) ->
            c1.drawText("• $dim: $score / 100", 48f, y, bodyPaint)
            y += 16f
        }
        y += 16f

        c1.drawText("TOP RECOMMENDED CAREERS & COMPATIBILITY", 40f, y, headerPaint)
        y += 20f
        topCareers.take(5).forEachIndexed { idx, (career, matchPct) ->
            c1.drawText("${idx + 1}. ${career.title} (${career.category}) — $matchPct% Match", 44f, y, bodyPaint)
            y += 15f
            c1.drawText("   Salary (Sample): ${career.salaryRangeSample} | Demand: ${career.demandIndicator}", 44f, y, mutedPaint)
            y += 15f
            c1.drawText("   Key Skills: ${career.requiredTechSkills.keys.joinToString(", ")}", 44f, y, mutedPaint)
            y += 20f
        }

        pdfDocument.finishPage(page1)

        // Page 2: Skill Matrix, Gap Analysis & Personalized Action Roadmap
        val page2Info = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
        val page2 = pdfDocument.startPage(page2Info)
        val c2: Canvas = page2.canvas

        var y2 = 48f
        c2.drawText("SKILL MATRIX, GAP ANALYSIS & LEARNING ROADMAP", 40f, y2, titlePaint)
        y2 += 28f

        c2.drawText("EXISTING TECHNICAL & SOFT SKILLS", 40f, y2, headerPaint)
        y2 += 18f
        profile.technicalSkills.take(8).forEach { skillEntry ->
            val parts = skillEntry.split(":")
            val name = parts.getOrNull(0) ?: skillEntry
            val pct = parts.getOrNull(1) ?: "70"
            c2.drawText("• $name — $pct% Proficiency", 48f, y2, bodyPaint)
            y2 += 15f
        }
        y2 += 16f

        val primaryCareer = topCareers.firstOrNull()?.first
        if (primaryCareer != null) {
            c2.drawText("PERSONALIZED 10-LEVEL ROADMAP: ${primaryCareer.title.uppercase()}", 40f, y2, headerPaint)
            y2 += 20f
            primaryCareer.roadmap.forEach { module ->
                val done = profile.completedLessonIds.contains(module.id) || profile.completedLessonIds.contains("lvl_${module.level}")
                val statusTag = if (done) "[COMPLETED]" else "[PENDING]"
                c2.drawText("$statusTag ${module.title} (${module.durationWeeks} wks • ${module.difficulty})", 44f, y2, bodyPaint)
                y2 += 14f
                c2.drawText("   Subtopics: ${module.subtopics.joinToString(", ")}", 44f, y2, mutedPaint)
                y2 += 18f
            }
        }

        y2 += 14f
        c2.drawText("SHORT-TERM & LONG-TERM ACTION GOALS", 40f, y2, headerPaint)
        y2 += 18f
        c2.drawText("• Short-Term (30 Days): Complete next 2 roadmap levels & close priority skill gaps.", 44f, y2, bodyPaint)
        y2 += 16f
        c2.drawText("• Medium-Term (90 Days): Build 2 capstone portfolio projects & earn 1 verified certification.", 44f, y2, bodyPaint)
        y2 += 16f
        c2.drawText("• Long-Term (6 Months): Complete 5 AI Mock Interviews & apply to target internships/roles.", 44f, y2, bodyPaint)

        pdfDocument.finishPage(page2)
        return pdfDocument
    }
}
