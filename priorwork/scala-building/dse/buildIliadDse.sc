import edu.holycross.shot.cite._
import scala.io.Source

/*

Read an index of Iliad nodes to image RoIs, and an ordered list of
iliad nodes to surfaces, and generate a resulting DSE file.

The URN and label properties of the DSE file are autogenerated.  The three
URNs for text, surface and image are filled in from the indices.
*/

// This is a growing index of iliad to image.  Update this file
// and rerun this script to build a new DSE.
val imgSrc =  "iliad-source/va-iliad2image-composite.cex"

// Set this to a desired value to start counting from in generating
// DSE record URNs distinguished by an integer.
val offset = 0

// This is a comprehensive index of Iliad lines to page.
val surfaceSrc = "iliad-source/venA-Iliad-surface.cex"

val imgLines = Source.fromFile(imgSrc).getLines.toVector

val imgPairings = for (l <- imgLines)  yield {
  val parts = l.split("#")
  val txt = CtsUrn(parts(0))
  val img = Cite2Urn(parts(1))
  (txt, img)
}
val imgIndex = imgPairings.toMap

val urnBase = "urn:cite2:hmt:va_dse.v1:il"




val surfaceLines = Source.fromFile(surfaceSrc).getLines.toVector
val cex = for ((l, count) <- surfaceLines.zipWithIndex) yield {
  val urn = s"${urnBase}${count + offset}"


  val parts = l.split("#")
  val txt = CtsUrn(parts(0))
  val surface = Cite2Urn(parts(1))


  val imgUrn = imgIndex.getOrElse(txt,None)
  imgUrn match {
    case img: Cite2Urn =>   s"${urn}#DSE record for Iliad ${txt.passageComponent}#${txt}#${imgUrn}#${surface}"
    case None => ""
  }
}


val hdr = "#!citedata\nurn#label#passage#imageroi#surface\n"

import java.io.PrintWriter
new PrintWriter("iliad-dse.cex"){write(hdr +cex.filter(_.nonEmpty).mkString("\n") + "\n"); close;  }
