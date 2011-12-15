# Gephi Plugins Bootcamp

Get started with the [Gephi](http://gephi.org) Platform and start to create [Gephi Plugins](http://gephi.org/plugins) by looking at these examples.

The Gephi Plugins Bootcamp is the best sources of examples and good practices to create all types of plug-ins (layout, filter, io, visualization, ...). Consult the [**Javadoc**](http://gephi.org/docs) to discover the different APIs. Documentation is also available on the [Toolkit Portal](https://wiki.gephi.org/index.php/Plugins_portal) on the wiki.

## What's inside?

Complete list of the plug-ins examples included in the bootcamp:

### Layout

* **Grid Layout**
	* Place all nodes in a simple grid. Users can configure the size of the area and the speed.

* **Sorted Grid Layout** 
	* Same example as Grid Layout but users can sort nodes with an attribute column.

### Filter

* **Transform to Undirected** 
	* Edge filter to remove mutual edges in a directed graph.

* **Top nodes** 
	* Keep the top K nodes using an attribute column.

* **Remove Edge Crossing** 
	* Example of a complex filter implementation which removes edges until no crossing occurs.

### Tool

* **Find** 
	* Tool with a autocomplete text field to find any node based on labels and zoom by it.

* **Add Nodes** 
	* Listen to mouse clicks and adds nodes. Also adds edges if selecting other nodes.

### Export

* **JPG Export** 
	* Vectorial export to the JPG image format. Contains a settings panel to set the width and height.

* **SQLite Database Export** 
	* Current graph export to a SQLite Database file. A new sub-menu is added in the Export menu and an example of a custom exporter is shown.

### Preview

* **Highlight Mutual Edges** 
	* Colors differently mutual edges. Overwrites and extends the default edge renderer.

* **Glow Renderer** 
	* Adds a new renderer for node items which draws a glow effect around nodes.

* **Node Z-ordering** 
	* Extends the default node builder by reordering the node items by size or any number columns. Also shows how to create complex Preview UI.

### Import

* **Matrix Market Importer** 
	* File importer for the Matrix Market format.

### Statistic

* **Count Self-Loop** 
	* Example of a statistics result at the global level. Simply counts the number of self-loop edges in the graph.

* **Average Euclidean Distance** 
	* Example of a per-node calculation. For a given node it calculates the average distance to others.

### Plugins sub-menu

* **Test action** 
	* Simple action which display a message and a dialog.

* **Remove self loops** 
	* Action which accesses the graph and remove self-loops, if any.

* **Using Progress and Cancel** 
	* Action which creates a long task and execute it with progress and cancel support.

### Execute at startup

* **When UI is ready** 
	* Do something when the UI finished loading.

* **Workspace select events** 
	* Do something when a workspace is selected.

### Processor

* **Initial Position** 
	* Set up the nodes' initial position always the same. It calculates a hash with all nodes so the X/Y position is randomized always in the same way.

### New Panel

* **New panel** 
	* Example of a new panel plugin set up at the ranking position.


## Installation

Follow the steps to get the right development environment for developing Gephi plug-ins. The bootcamp contains the latest version of the Gephi Platform plus the examples. 

- Download and install the latest version of [Netbeans IDE](http://netbeans.org).
- Checkout the latest version of the Gephi Plugins Bootcamp

        git clone git@github.com:gephi/gephi-plugins-bootcamp.git

- Start Netbeans and Open Project. The bootcamp is automatically recognized as a module suite.
- Right click on the project and do 'Run'. This starts Gephi with all the example plug-ins loaded.
- Expand the list of modules and double-click on each to open them and browse the sources.

Once you feel comfortable starting your own plug-in, follow the [Plugin Quick Start (5 minutes)](http://wiki.gephi.org/index.php/Plugin_Quick_Start_(5_minutes)).

Consult the [**Javadoc**](http://gephi.org/docs) to browse the APIs.

#### Without Netbeans

You can also see existing example and develop new plug-ins without Netbeans IDE.

At the root directory of the bootcamp just do

    ant run

to start Gephi with the plug-ins.



