# GraphLab ― Reactive Data-Flow Sandbox

A lightweight Java/HTTP playground that lets you upload a “blueprint” file, spin up cooperating math agents, and watch their values ripple across a browser-rendered network in real time.

## 🛠 Getting Ready

1. **Clone**
   ```bash
   git clone https://github.com/Roeyx/advanced-programming-computational-graph.git
   cd advanced-programming-computational-graph

The folder now contains all source, build scripts, and a pre-baked Javadoc bundle.

Compile & Launch

If java is not on PATH

bash
Copy
Edit
"C:\full\jdk\bin\java.exe" AdvancedProgrammingProject\src\project_biu\Main.java
If java is on PATH

bash
Copy
Edit
java AdvancedProgrammingProject/src/project_biu/Main.java
Java 11+ lets you run a single source file directly, so no explicit javac step is required.

When the server boots, it automatically opens your default browser at http://localhost:8080/app/ where the UI lives.

🚀 First Flight
Pick a blueprint (.txt or .cfg).

Hit Deploy to instantiate nodes and connectors.

A canvas appears—zoom with the mouse wheel, drag to reposition, click any node to inspect its formula. The visual layer relies on the vis-network component of vis.js for instant layout updates.

Use the side panel to broadcast new numbers to any topic; changes propagate through every downstream agent instantly. Agents execute inside lightweight workers, similar to micro-servlets, ensuring isolation and responsiveness.

📂 Blueprint Grammar
Each trio of lines defines one agent:

php-template
Copy
Edit
<fully.qualified.AgentClass>
<comma-separated input topics>
<single output topic>
Class	Math rule	Streams consumed	Stream produced
IncAgent	x + 1	1	1
PlusAgent	x + y	2	1
MulAgent	x * y	2	1
DivAgent	x / y	2	1
ExponentAgent	x ^ y	2	1

⚠️ Ensure the file length is a multiple of three and free of blank lines—otherwise the loader will reject it with a descriptive error page.

🔧 Interaction Tricks
Action	Effect
Tap node	Reveal its underlying expression plus latest numeric outcome.
Graph-wide summary	Click Show Formulas to open a table listing every agent and current value.
Scroll	Zoom in or out.
Drag & drop	Rearrange layout to your liking; positions are remembered until the next deployment.

💡 Why another graph tool?
Self-contained HTTP stack – sockets, parsing, and dispatch are implemented manually for educational clarity, not hidden behind a heavyweight container.

Multipart upload support – blueprints travel via standard multipart/form-data without external libraries.

Live visualization – vis.js animates topology changes smoothly, handling thousands of links on modest hardware.

Pluggable math nodes – drop in new agent classes; the framework auto-wires them through reflection.

📝 Contributing
Fork → feature branch → pull request.

Follow the style-guide in /JavaDoc/README for code comments; docs regenerate via mvn javadoc:javadoc.

Open issues for enhancement ideas—performance tweaks, extra maths ops, or UI polish welcome.

Enjoy experimenting with reactive computations!

yaml
Copy
Edit

---

You can save this content into a file named `README.md` at the root of your repository. This will ensure that GitHub displays it as the main description of your project. Feel free to customize further to match your project's specifics.:contentReference[oaicite:11]{index=11}
::contentReference[oaicite:12]{index=12}
