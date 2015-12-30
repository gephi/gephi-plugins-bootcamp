# Gephi Plugins Bootcamp

Get started with the [Gephi](http://gephi.org) Platform and start to create [Gephi Plugins](http://gephi.org/plugins) by looking at these examples.

The Gephi Plugins Bootcamp is the best sources of examples and good practices to create all types of plug-ins (layout, filter, io, visualization, ...). Consult the [**Javadoc**](https://gephi.org/gephi/0.9.0/apidocs/) to discover the different APIs. Documentation is also available on the [Gephi Plugins](https://github.com/gephi/gephi-plugins) repository.

![Gephi Plugins Bootcamp](http://gephi.org/images/plugins_ribbon.png)

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
	
* **Square shaped nodes** 
	* Demonstrates how to extend and replace a default renderer. Extends node default renderer to support square shaped nodes.

### Import

* **Matrix Market Importer** 
	* File importer for the Matrix Market format.

### Statistic

* **Count Self-Loop** 
	* Example of a statistics result at the global level. Simply counts the number of self-loop edges in the graph.

* **Average Euclidean Distance** 
	* Example of a per-node calculation. For a given node it calculates the average distance to others.

### Generator

* **Simple generator**
	* Hello world generator which creates a two nodes network.

* **Streaming generator**
	* Shows how to create a continuous generator using threads.
	
### Data laboratory

* **Interactive sparkline**
	* Table cell action that shows an interactive sparkline of a number list or dynamic number.
	
* **Convert column to dynamic**
	* Column action that replaces a column with its dynamic equivalent with a defined interval.
	
* **Invert row selection**
	* General action (plugin) that inverts the current table row selection.
	
* **Equal values merge strategy**
	* Column merge strategy that creates a new boolean column with values indicating if the two given columns have the same value.
	
* **Set node(s) color**
	* Nodes action that edits the color of one or more nodes

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
