package com.taskflow.persistence;

import com.taskflow.model.RootGroup;
import com.taskflow.model.Category;
import com.taskflow.model.Task;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

// Handler care exportă datele aplicației în format XML folosind DOM.
public class XmlDomHandler implements RootGroupPersistence {

    @Override
    public List<RootGroup> loadRootGroups(File file) throws Exception {
        // Importul prin DOM nu este suportat în această implementare
        throw new UnsupportedOperationException("DOM import not supported");
    }

    @Override
    public void saveRootGroups(List<RootGroup> roots, File file) throws Exception {
        // Creăm documentul XML nou
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder       = factory.newDocumentBuilder();
        Document doc                  = builder.newDocument();

        // Root element pentru toate grupurile
        Element rootsElem = doc.createElement("roots");
        doc.appendChild(rootsElem);

        // Formatter pentru date
        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd");

        // Iterăm prin fiecare RootGroup
        for (RootGroup rg : roots) {
            Element rootElem = doc.createElement("root");
            rootElem.setAttribute("name", rg.getName()); // Atribute pentru nume și culoare
            rootElem.setAttribute("color", rg.getColorHex());
            rootsElem.appendChild(rootElem);

            // Iterăm prin categorii
            for (Category c : rg.getCategories()) {
                Element catElem = doc.createElement("category");
                catElem.setAttribute("name", c.getName());
                catElem.setAttribute("color", c.getColor());
                rootElem.appendChild(catElem);

                // Iterăm prin task-uri
                for (Task t : c.getTasks()) {
                    Element taskElem = doc.createElement("task");
                    taskElem.setAttribute("id", t.getId());
                    taskElem.setAttribute("title", t.getTitle());
                    taskElem.setAttribute("completed", Boolean.toString(t.isCompleted()));
                    taskElem.setAttribute("color", t.getColorHex());
                    catElem.appendChild(taskElem);

                    // Descrierea task-ului
                    Element desc = doc.createElement("description");
                    desc.setTextContent(t.getDescription());
                    taskElem.appendChild(desc);

                    // Data-limită a task-ului
                    Element due = doc.createElement("dueDate");
                    Calendar cal = t.getDueDate();
                    due.setTextContent(dateFmt.format(cal.getTime()));
                    taskElem.appendChild(due);
                }
            }
        }

        // Transformăm documentul DOM în fișierul XML
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }
}
