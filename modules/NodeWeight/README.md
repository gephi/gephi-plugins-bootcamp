# User Guide for Node Weight Plugin

1. Launch Gephi
2. Install Node Weight plugin from Plugin menu in Gephi
3. Import data or a .gexf file
  * Dynamic data should be in string form like  [Timestamp: data], [timestamp: data].
    e.g. : “[2018-07-21T00:00:00.000Z, 0.21100000000000002]; [2018-07-21T00:30:00.000Z, 0.429]; [2018-07-21T03:00:00.000Z, 0.525]; [2018-07-21T06:00:00.000Z, 0.465]; [2018-07-21T12:00:00.000Z, 0.545]”
  * Edges should have an ID in ‘Source’ and an ID in ‘Target’ that corresponds to the correct node IDs
  * To use the Node Weight plugin functionality, you should have data in the nodes table in a column entitled ‘weight’ or ‘weight_dynamic’ (either will accept dynamic and non dynamic data)
4. In Data Observatory, designate dynamic data columns as appropriate with your data.
5. Go to Overview
6. In the Layout tab select ‘Node Weight’ from the drop-down list
7. Important fields for the Node Layout are:
  * Node Weight Influence: Adjust scaling on node weight attraction. More creates larger distance between nodes.
  * Opacity mode: Requires data scaled to its standard deviation. Opacity changes according to weight (in preview / export), turn on per-node opacity in preview tab to activate.
  * Heat Map: Checkbox to turn on heat map based on normalized node weights. Requires data scaled to its standard deviation.
  * Change Heat Map: Checkbox to turn on heat map to show change of normalized node weights between time slices in dynamic data. Red-green.
  * Alternative Change Heat Map: Checkbox to turn on heat map to show change of normalized node weights between time slices in dynamic data Requires data scaled to its standard deviation. Blue-yellow.
  * Gravity: Attracts nodes to the center. Prevents islands from drifting away. If negative weight averages in your network, minimum gravity set to avoid nodes floating out of graph space.
  * Stronger Gravity: A stronger gravity law. Highly recommended to keep nodes in graph space if there are negative weights.
  * Scaling: Because of the stronger gravity anchoring nodes with negative weights, you will probably want to turn this up.

8. Experiment with features. Select one of the heat maps. Select opacity. Change the Node Weight influence.
  Dynamic-specific
    A. Turn on the Timeline feature: Select Timeline from the Window drop-down menu. Go to the bottom of the Gephi interface and click ‘Enable Timeline’
    B. Adjust the intervals by sliding the edges of the translucent blue bar.
    C. Drag your interval to the 1 marker and press the play button or manually drag the interval around. The interval for which you have data must be completed: e.g. if you want data for hour 3, cover the whole of 3 with an hour-long interval or with a smaller time-slice go to the next hour area not covered by your data. Mouse over the interval for a tooltip telling the duration and bounds of your interval. For the change heat maps, offset your interval an hour (or whatever other unit your timeline is using) later to see the change.




