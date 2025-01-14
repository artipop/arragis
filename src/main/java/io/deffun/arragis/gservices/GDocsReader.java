package io.deffun.arragis.gservices;

import com.google.api.services.docs.v1.model.ParagraphElement;
import com.google.api.services.docs.v1.model.StructuralElement;
import com.google.api.services.docs.v1.model.TableCell;
import com.google.api.services.docs.v1.model.TableRow;
import com.google.api.services.docs.v1.model.TextRun;

import java.util.List;

// SEE: https://developers.google.com/docs/api/samples/extract-text
public final class GDocsReader {
    private GDocsReader() {
    }

    /**
     * Returns the text in the given ParagraphElement.
     *
     * @param element a ParagraphElement from a Google Doc
     */
    private static String readParagraphElement(ParagraphElement element) {
        TextRun run = element.getTextRun();
        if (run == null || run.getContent() == null) {
            // The TextRun can be null if there is an inline object.
            return "";
        }
        return run.getContent();
    }

    /**
     * Recurses through a list of Structural Elements to read a document's text where text may be in
     * nested elements.
     *
     * @param elements a list of Structural Elements
     */
    static String readStructuralElements(List<StructuralElement> elements) {
        StringBuilder sb = new StringBuilder();
        for (StructuralElement element : elements) {
            if (element.getParagraph() != null) {
                for (ParagraphElement paragraphElement : element.getParagraph().getElements()) {
                    sb.append(readParagraphElement(paragraphElement));
                }
            } else if (element.getTable() != null) {
                // The text in table cells are in nested Structural Elements and tables may be
                // nested.
                for (TableRow row : element.getTable().getTableRows()) {
                    for (TableCell cell : row.getTableCells()) {
                        sb.append(readStructuralElements(cell.getContent()));
                    }
                }
            } else if (element.getTableOfContents() != null) {
                // The text in the TOC is also in a Structural Element.
                sb.append(readStructuralElements(element.getTableOfContents().getContent()));
            }
        }
        return sb.toString();
    }
}
