package com.taskflow.persistence;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class XmlDomHandler {

    public static void saveCategories(List<Category> categories, File file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("categories");
        doc.appendChild(root);

        for (Category cat : categories) {
            Element catElem = doc.createElement("category");
            catElem.setAttribute("id", cat.getId().toString());
            catElem.setAttribute("name", cat.getName());
            catElem.setAttribute("color", cat.getColor());

            for (Task t : cat.getTasks()) {
                Element taskElem = doc.createElement("task");
                taskElem.setAttribute("id", t.getId().toString());
                taskElem.setAttribute("title", t.getTitle());
                taskElem.setAttribute("completed", Boolean.toString(t.isCompleted()));

                Element desc = doc.createElement("description");
                desc.setTextContent(t.getDescription());
                taskElem.appendChild(desc);

                Element due = doc.createElement("dueDate");
                Calendar cal = t.getDueDate();
                due.setTextContent(String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
                taskElem.appendChild(due);

                catElem.appendChild(taskElem);
            }
            root.appendChild(catElem);
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }

    public static List<Category> loadCategories(File file) throws Exception {
        List<Category> categories = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(file);
        Element root = doc.getDocumentElement();

        var catNodes = root.getElementsByTagName("category");
        for (int i = 0; i < catNodes.getLength(); i++) {
            var catElem = (Element) catNodes.item(i);
            Category cat = new Category();
            cat.setId(UUID.fromString(catElem.getAttribute("id")));
            cat.setName(catElem.getAttribute("name"));
            cat.setColor(catElem.getAttribute("color"));

            var taskNodes = catElem.getElementsByTagName("task");
            for (int j = 0; j < taskNodes.getLength(); j++) {
                var taskElem = (Element) taskNodes.item(j);
                Task t = new Task();
                t.setId(UUID.fromString(taskElem.getAttribute("id")));
                t.setTitle(taskElem.getAttribute("title"));
                t.setCompleted(Boolean.parseBoolean(taskElem.getAttribute("completed")));

                var descElem = (Element) taskElem.getElementsByTagName("description").item(0);
                t.setDescription(descElem.getTextContent());

                var dueElem = (Element) taskElem.getElementsByTagName("dueDate").item(0);
                String[] parts = dueElem.getTextContent().split("-");
                Calendar cal = Calendar.getInstance();
                cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
                t.setDueDate(cal);

                cat.addTask(t);
            }
            categories.add(cat);
        }
        return categories;
    }
}
