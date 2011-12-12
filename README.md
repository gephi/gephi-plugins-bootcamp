# Gephi Plugins Bootcamp

Get started with the [Gephi](http://gephi.org) Platform and start to create [Gephi Plugins](http://gephi.org/plugins) by looking at these examples.

The Gephi Plugins Bootcamp is the best sources of examples and good practices to create all types of plug-ins (layout, filter, io, visualization, ...). Consult the [**Javadoc**](http://gephi.org/docs) to discover the different APIs. Documentation is also available on the [Toolkit Portal](https://wiki.gephi.org/index.php/Plugins_portal) on the wiki.

## What's inside?

Complete list of the plug-ins examples included in the bootcamp:

#### Layout
<table>
    <tr><td><b>Grid Layout</b></td><td>Place all nodes in a simple grid. Users can configure the size of the area and the speed.</td></tr>
    <tr><td><b>Sorted Grid Layout</b></td><td>Same example as Grid Layout but users can sort nodes with an attribute column.</td></tr>
</table>

#### Filter
<table>
    <tr><td><b>Transform to Undirected</b></td><td>Edge filter to remove mutual edges in a directed graph.</td></tr>
	<tr><td><b>Top nodes</b></td><td>Keep the top K nodes using an attribute column.</td></tr>
    <tr><td><b>Remove Edge Crossing</b></td><td>Example of a complex filter implementation which removes edges until no crossing occurs.</td></tr>
</table>

#### Tool
<table>
    <tr><td><b>Find</b></td><td>Tool with a autocomplete text field to find any node based on labels and zoom by it.</td></tr>
	<tr><td><b>Add Nodes</b></td><td>Listen to mouse clicks and adds nodes. Also adds edges if selecting other nodes.</td></tr>
</table>

#### Export
<table>
    <tr><td><b>JPG Export</b></td><td>Vectorial export to the JPG image format. Contains a settings panel to set the width and height.</td></tr>
	<tr><td><b>SQLite Database Export</b></td><td>Current graph export to a SQLite Database file. A new sub-menu is added in the Export menu and an example of a custom exporter is shown.</td></tr>
</table>

#### Preview
<table>
    <tr><td><b>Highlight Mutual Edges</b></td><td>Colors differently mutual edges. Overwrites and extends the default edge renderer.</td></tr>
	<tr><td><b>Glow Renderer</b></td><td>Adds a new renderer for node items which draws a glow effect around nodes.</td></tr>
	<tr><td><b>Node Z-ordering</b></td><td>Extends the default node builder by reordeing the node items by size or any number columns. Also shows how to create complex Preview UI.</td></tr>
</table>

#### Import
<table>
    <tr><td><b>Matrix Market Importer</b></td><td>File importer for the Matrix Market format. Lot's of datasets [here](http://www2.research.att.com/~yifanhu/GALLERY/GRAPHS/index.html).</td></tr>
</table>

#### Statistic
<table>
    <tr><td><b>Count Self-Loop</b></td><td>Example of a statistics result at the global level. Simply counts the number of self-loop edges in the graph</td></tr>
	<tr><td><b>Average Euclidean Distance</b></td><td>Example of a per-node calculation. For a given node it calculates the average distance to others.</td></tr>
</table>

#### Plugins sub-menu
<table>
    <tr><td><b>Test action</b></td><td>Simple action which display a message and a dialog.</td></tr>
	<tr><td><b>Remove self loops</b></td><td>Action which accesses the graph and remove self-loops, if any.</td></tr>
	<tr><td><b>Using Progress and Cancel</b></td><td>Action which creates a long task and execute it with progress and cancel support.</td></tr>
</table>

#### Execute at startup
<table>
    <tr><td><b>When UI is ready</b></td><td>Do something when the UI finished loading.</td></tr>
	<tr><td><b>Workspace select events</b></td><td>Do something when a workspace is selected.</td></tr>
</table>

#### Processor
<table>
    <tr><td><b>Initial Position</b></td><td>Set up the nodes' initial position always the same. It calculates a hash with all nodes so the X/Y position is randomized always in the same way.</td></tr>
</table>

### New Panel
<table>
    <tr><td><b>New panel</b></td><td>Example of a new panel plugin set up at the ranking position.</td></tr>
</table>

## Installation

Follow the steps to get the right development environment for developing Gephi plug-ins. The bootcamp contains the latest version of the Gephi Platform plus the examples. 

- Download and install the latest version of [Netbeans IDE](http://netbeans.org).
- Checkout the latest version of the Gephi Plugins Bootcamp

        git@github.com:gephi/gephi-plugins-bootcamp.git

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



