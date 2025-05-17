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
java AdvancedProgrammingProject/src/project_biu/Main.java

Java 11+ lets you run a single source file directly, so no explicit javac step is required.

When the server boots, it automatically opens your default browser at http://localhost:8080/app/ where the UI lives.

🚀 First Flight

Pick a blueprint (.txt or .cfg).

Hit Deploy to instantiate nodes and connectors.

A canvas appears—zoom with the mouse wheel, drag to reposition, click any node to inspect its formula. The visual layer relies on the vis-network component of vis.js for instant layout updates.

Use the side panel to broadcast new numbers to any topic; changes propagate through every downstream agent instantly. Agents execute inside lightweight workers, similar to micro-servlets, ensuring isolation and responsiveness.
📂 Blueprint Grammar
| Class           | Math rule | Streams consumed | Stream produced |
| --------------- | --------- | ---------------- | --------------- |
| `IncAgent`      | `x + 1`   | 1                | 1               |
| `PlusAgent`     | `x + y`   | 2                | 1               |
| `MulAgent`      | `x * y`   | 2                | 1               |
| `DivAgent`      | `x / y`   | 2                | 1               |
| `ExponentAgent` | `x ^ y`   | 2                | 1               |



