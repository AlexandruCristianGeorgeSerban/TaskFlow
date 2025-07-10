# 🚀 TaskFlow

Aplicație desktop Java Swing pentru gestionarea eficientă a sarcinălor personale și de echipă.

## 📝 Descriere

TaskFlow este o aplicație desktop scrisă în Java care permite utilizatorilor să își organizeze și să își urmărească task-urile într-un mod simplu și intuitiv. Funcționalitățile includ:

- ➕ **Adăugare** task-uri și categorii  
- ✏️ **Editare** titlu, descriere, dată-limită și culoare pentru task-uri și categorii  
- 🗑️ **Ștergere** task-uri, categorii sau root-uri cu confirmare  
- 📂 **Organizare** a task-urilor pe categorii și root-uri  
- 🔍 **Filtrare** în timp real a task-urilor în tabel  
- 📅 **Planificare** în calendar, cu validare pentru date în trecut  
- 🔄 **Export/Import** date în format XML (DOM sau SAX)  
- 📄 **Export în PDF** al task-urilor afișate, cu structură ierarhică (root, categorie, task)  
- 🔀 **Drag & Drop** în arbore pentru reordonare și mutare  
- 🎨 **Interfață grafică** modernă bazată pe Java Swing  

## ✅ Cerințe

- Java 8 sau mai recent

## ⚙️ Configurație

Creează fișierul `config.properties` în directorul `src/main/resources/` cu următorul conținut:

```properties
# Proprietăți de configurare pentru TaskFlow

# Clasă Look & Feel pentru interfața Swing
# Pentru look & feel implicit al sistemului
ui.lookAndFeel=SYSTEM
# Sau pentru Nimbus:
#ui.lookAndFeel=javax.swing.plaf.nimbus.NimbusLookAndFeel

# Calea către fișierul XML cu date (relativ la directorul de lucru)
data.file=src/test/resources/data.xml

# Calea către fontul Unicode (TTF)
pdf.font=src/main/resources/fonts/DejaVuSans.ttf
```

## 🛠️ Tehnologii și unelte folosite

- ☕ **Limbaj de programare:** Java  
- 🖥️ **GUI:** Swing (JFrame, JTable, JTree, JFileChooser, JColorChooser)  
- 💾 **Persistență:** XML (DOM & SAX) prin implementări `XmlDomHandler`, `XmlSaxHandler`  
- 📄 **Export PDF:** [iText 7](https://itextpdf.com/) pentru generarea de rapoarte PDF cu suport Unicode  
- 📚 **Structuri de date:** `List`, `Calendar`, `UUID`  

## 📂 Structura proiectului

```
com.taskflow
├─ action        # Clase Action (Add, Edit, Delete, Import, Export, Toggle, Reset)
├─ app           # Punctul de intrare și inițializare (TaskFlowApp)
├─ model         # Bean-uri: Task, Category, RootGroup
├─ persistence   # Interfețe și implementări XML (DOM, SAX)
├─ service       # TaskService și implementarea in-memory DefaultTaskService
├─ ui            # Clase Swing: MainFrame, TreeTransferHandler, renderere, modele
├─ util          # Clase utilitare (CalendarUtil, FileManager, PropertiesManager)
```

## 🚀 Instalare și rulare

1. 📥 Clonează acest repository:  
   ```bash
   git clone https://github.com/AlexandruCristianGeorgeSerban/TaskFlow.git
   cd TaskFlow/TaskFlow/taskflow
   ```
2. ⚙️ Compilează și împachetează proiectul cu Maven:  
   ```bash
   mvn clean package
   ```
3. ▶️ Rulează aplicația:  
   ```bash
   java -jar target/taskflow-2-PROIECT.jar
   ```

> 🔔 **Notă:** Asigură-te că fișierul `config.properties` și fontul TTF se află în folderul `resources` înainte de a genera JAR‑ul.
