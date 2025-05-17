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
```bash
Path\openlogic-openjdk-22.0.2+9-windows-x64\bin\java.exe Path\advanced-programming-computational-graph\src\project_biu\Main.java
```

When the server boots, it automatically opens your default browser at http://localhost:8080/app/index.html/ where the UI lives.

## 🚀 First Flight

Click on Choose file

![image](https://github.com/user-attachments/assets/ab8cf41c-ac34-4cb5-b138-0220529ee2d2)

Pick a blueprint (.config).

![image](https://github.com/user-attachments/assets/0a4d862a-9cde-41e1-9fb0-2b068b1400ed)

Hit Deploy to instantiate nodes and connectors.

![image](https://github.com/user-attachments/assets/235ff643-a0ee-4228-9360-70999cc2f755)

A canvas appears—zoom with the mouse wheel, drag to reposition, click any node to inspect its formula. The visual layer relies on the vis-network component of vis.js for instant layout updates.

![image](https://github.com/user-attachments/assets/53b9fe7d-a86a-43e5-8695-9372feb4501d)


Use the side panel to broadcast new numbers to any topic; changes propagate through every downstream agent instantly. Agents execute inside lightweight workers, similar to micro-servlets, ensuring isolation and responsiveness.

Once values are submitted to the topics, a chart displaying the updated topic values will appear on the right side of the screen.

![image](https://github.com/user-attachments/assets/53767120-6db0-4b8e-903e-50e68473792a)

Clicking the Show Equations button opens a new window in the url http://localhost:8080/features/eval that displays a comprehensive chart detailing all the computed equations and their current results.

![image](https://github.com/user-attachments/assets/9de41543-a40a-4e04-a766-e0fcf88515ca)


## 📂 Blueprint Grammar
| Class           | Math rule | Streams consumed | Stream produced |
| --------------- | --------- | ---------------- | --------------- |
| `IncAgent`      | `x + 1`   | 1                | 1               |
| `PlusAgent`     | `x + y`   | 2                | 1               |
| `MulAgent`      | `x * y`   | 2                | 1               |
| `DivAgent`      | `x / y`   | 2                | 1               |
| `ExponentAgent` | `x ^ y`   | 2                | 1               |



