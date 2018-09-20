/*
 Copyright 2008-2011 Gephi
 Authors : Mathieu Jacomy <mathieu.jacomy@gmail.com>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */

// all code attributable to Julianne Joswiak lies between tags: //jj2018 and //

package org.gephi.layout.plugins.layout.NodeWeight;

import java.awt.Color;


import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.gephi.graph.api.Estimator;
import org.gephi.graph.api.Interval;
import org.gephi.graph.api.types.IntervalMap;
import org.gephi.graph.api.types.TimeMap;
import org.gephi.graph.api.types.TimestampMap;
import org.gephi.graph.api.types.TimestampDoubleMap;
import org.gephi.graph.api.Interval;

import org.gephi.layout.plugins.layout.NodeWeight.HeatmapFeatures;
import org.gephi.layout.plugins.layout.NodeWeight.ForceFactory.AttractionForce;
import org.gephi.layout.plugins.layout.NodeWeight.ForceFactory.RepulsionForce;


import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Comparator;
import java.util.Map;
import java.util.Arrays;



import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;

import org.gephi.layout.plugins.layout.NodeWeight.AbstractLayout;



/**
 * Node Weight Influence added to ForceAtlas 2 Layout, manages each step of the computations.
 *
 * @author Mathieu Jacomy
 */
public class ForceAtlas2 implements Layout {

    private GraphModel graphModel;
    private Graph graph;
    private final ForceAtlas2Builder layoutBuilder;
    private double edgeWeightInfluence;
    private double jitterTolerance;
    private double scalingRatio;
    private double gravity;
    private double speed;
    private double speedEfficiency;
    private boolean outboundAttractionDistribution;
    private boolean adjustSizes;
    private boolean barnesHutOptimize;
    private double barnesHutTheta;
    private boolean linLogMode;
    private boolean strongGravityMode;
    private boolean heatMapChangeMode;
    private boolean heatMapChangeModeCB;
    private boolean heatMapMode;
    private boolean opacityMode;
    private int threadCount;
    private int currentThreadCount;
    private Region rootRegion;
    double outboundAttCompensation = 1;
    private ExecutorService pool;
    private double minWeight;
    private double maxWeight;
    private String nodeWeightColumnName;
    private double nodeWeightScaling;



    //by jj2018
    private void setNodeWeightColumnName(Table table) {
      if (table.hasColumn("weight")) {
        this.nodeWeightColumnName = "weight";
      }
      if (table.hasColumn("weight_dynamic")) {
        this.nodeWeightColumnName = "weight_dynamic";
      }
    }
    //


    public ForceAtlas2(ForceAtlas2Builder layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
        this.threadCount = Math.min(4, Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
    }

    @Override
    public void initAlgo() {
        AbstractLayout.ensureSafeLayoutNodePositions(graphModel);

        speed = 1.;
        speedEfficiency = 1.;


        Table table = graphModel.getNodeTable();

        //by jj2018
        if (!(table.hasColumn("weight") || table.hasColumn("weight_dynamic"))) {
          table.addColumn("weight", Double.class);
        }

        setNodeWeightColumnName(table);

        if (!(table.getColumn(nodeWeightColumnName).getTypeClass()==Double.class || (table.getColumn(nodeWeightColumnName).getTypeClass()==TimestampDoubleMap.class))) {

          NotifyDescriptor d = new NotifyDescriptor.Message("The Node Weight plugin requires types\n Double or TimestampDoubleMap", NotifyDescriptor.INFORMATION_MESSAGE);
          DialogDisplayer.getDefault().notify(d);


        }
        //



        graph = graphModel.getGraphVisible();

        graph.readLock();
        try {
            Node[] nodes = graph.getNodes().toArray();

            // Initialise layout data
            for (Node n : nodes) {
                if (n.getLayoutData() == null || !(n.getLayoutData() instanceof ForceAtlas2LayoutData)) {
                    ForceAtlas2LayoutData nLayout = new ForceAtlas2LayoutData();
                    n.setLayoutData(nLayout);
                }
                ForceAtlas2LayoutData nLayout = n.getLayoutData();
                nLayout.mass = 1 + graph.getDegree(n);
                nLayout.old_dx = 0;
                nLayout.old_dy = 0;
                nLayout.dx = 0;
                nLayout.dy = 0;
            }




            pool = Executors.newFixedThreadPool(threadCount);
            currentThreadCount = threadCount;
        } finally {
            graph.readUnlockAll();
        }

}




    private double getEdgeWeight(Edge edge, boolean isDynamicWeight, Interval interval) {
        if (isDynamicWeight) {
            return edge.getWeight(interval);
        } else {
            return edge.getWeight();
        }
    }
    // jj2018
    // hasNodeWeightStop refers to 'has node weight.' (full stop)
    private boolean hasNodeWeightStop(Node node) {
      if (graphModel.getNodeTable().hasColumn("weight") && node.getAttribute("weight") != null) {
        return true;
      } else {
        return false;
      }
    }


    private boolean hasNodeWeightDynamic(Node node) {
      if (graphModel.getNodeTable().hasColumn("weight_dynamic") && node.getAttribute("weight_dynamic") != null) {
        return true;
      } else {
        return false;
      }
    }


    private boolean hasNodeWeight(Node node) {
      if (hasNodeWeightStop(node) || hasNodeWeightDynamic(node)){
        return true;
      } else {
        return false;
      }
    }

    private double getNodeWeightChange(Node node, boolean isDynamicNodeWeight, Interval interval, Double weight) {
      if (interval.getLow() != Interval.INFINITY_INTERVAL.getLow()) {
        TimestampMap map = (TimestampMap) node.getAttribute(this.nodeWeightColumnName);
        Interval prev_interval = new Interval (Interval.INFINITY_INTERVAL.getLow(), interval.getLow());
        Double last_value = (Double) map.get(prev_interval, Estimator.LAST);
        Double change = weight - last_value;
        return change;
      }
      return 0.0;
    }

    private double getNodeWeight(Node node, boolean isDynamicNodeWeight, Interval interval) {

        if (isDynamicNodeWeight) {
            // node_weight = (Double) n.getAttribute("gravity_x");
            TimestampMap map = (TimestampMap) node.getAttribute(this.nodeWeightColumnName);
            // Estimator estimator = (Estimator) AVERAGE;

            Double value = (Double) map.get(interval, Estimator.AVERAGE);
            if (value == null) {
              Interval prev_interval = new Interval (Interval.INFINITY_INTERVAL.getLow(), interval.getLow());
              value = (Double) map.get(prev_interval, Estimator.LAST);
              if (value == null) {
                value = 0.0;
              }
            }
            return value;
        } else {
          return (Double) node.getAttribute(this.nodeWeightColumnName);
        }
    }
    //




    @Override
    public void goAlgo() {
        // Initialize graph data
        if (graphModel == null) {
            return;
        }

        graph = graphModel.getGraphVisible();

        graph.readLock();
        boolean isDynamicWeight = graphModel.getEdgeTable().getColumn("weight").isDynamic();

        //by jj2018
        boolean isDynamicNodeWeight = graphModel.getNodeTable().getColumn(this.nodeWeightColumnName).isDynamic();
        //

        Interval interval = graph.getView().getTimeInterval();

        try {
            Node[] nodes = graph.getNodes().toArray();
            Edge[] edges = graph.getEdges().toArray();

            //by jj2018
            //minimums of node averages and of edges
            Double min_avg_wt_n = 0.0;
            Double min_wt_e = 0.0;
            //

            for (Edge e : edges) {

            //by jj2018
            // calculate minimum of edge weights
            if (getEdgeWeightInfluence() != 0){
                Double edge_wt = e.getWeight();
                if (edge_wt < min_wt_e) {
                  min_wt_e = edge_wt;
                }
              }


              // calculate minimum of node weight averages
              Node a = e.getSource();
              Node b = e.getTarget();
              if (hasNodeWeight(a) && hasNodeWeight(b)){
                Double wta = getNodeWeight(a, isDynamicNodeWeight, interval);
                Double wtb = getNodeWeight(b, isDynamicNodeWeight, interval);
                Double avg_wt_n = (wta + wtb)/2;
                if (avg_wt_n < min_avg_wt_n) {
                  min_avg_wt_n = avg_wt_n;
                }
              }

              // anchor nodes: counteract weight negativity;
              // negative attraction acts as repulsion and nodes can be pushed out of bounds

              // account for minimum node weight average value
              Double potential_gravity_e = 2*(-(min_wt_e) + 10);
              if (min_wt_e < 0.0 && getGravity() < potential_gravity_e) {
                  setGravity(potential_gravity_e);
              }
              // account for minimum edge weight average value
              Double potential_gravity_n = -(min_avg_wt_n) + 10;
              if (min_avg_wt_n < 0.0 && getGravity() < potential_gravity_n) {
                  setGravity(potential_gravity_n);
              }

              // combine the adjusted gravity for if both edges and nodes have
              // significant negatives
              if (min_wt_e < 0 && min_avg_wt_n < 0){
                Double potential_gravity_combined = (potential_gravity_n + potential_gravity_e);
                if (getGravity() < potential_gravity_combined) {
                  setGravity(potential_gravity_combined);
                }
              }


            }
            //


            // Initialise layout data
            for (Node n : nodes) {
                if (n.getLayoutData() == null || !(n.getLayoutData() instanceof ForceAtlas2LayoutData)) {
                    ForceAtlas2LayoutData nLayout = new ForceAtlas2LayoutData();
                    n.setLayoutData(nLayout);
                }
                ForceAtlas2LayoutData nLayout = n.getLayoutData();
                nLayout.mass = 1 + graph.getDegree(n);
                nLayout.old_dx = nLayout.dx;
                nLayout.old_dy = nLayout.dy;
                nLayout.dx = 0;
                nLayout.dy = 0;
            }

            // If Barnes Hut active, initialize root region
            if (isBarnesHutOptimize()) {
                rootRegion = new Region(nodes);
                rootRegion.buildSubRegions();
            }

            // If outboundAttractionDistribution active, compensate.
            if (isOutboundAttractionDistribution()) {
                outboundAttCompensation = 0;
                for (Node n : nodes) {
                    ForceAtlas2LayoutData nLayout = n.getLayoutData();
                    outboundAttCompensation += nLayout.mass;
                }
                outboundAttCompensation /= nodes.length;
            }

            // Repulsion (and gravity)
            // NB: Muti-threaded
            RepulsionForce Repulsion = ForceFactory.builder.buildRepulsion(isAdjustSizes(), getScalingRatio());

            int taskCount = 8 * currentThreadCount;  // The threadPool Executor Service will manage the fetching of tasks and threads.
            // We make more tasks than threads because some tasks may need more time to compute.
            ArrayList<Future> threads = new ArrayList();
            for (int t = taskCount; t > 0; t--) {
                int from = (int) Math.floor(nodes.length * (t - 1) / taskCount);
                int to = (int) Math.floor(nodes.length * t / taskCount);
                Future future = pool.submit(new NodesThread(nodes, from, to, isBarnesHutOptimize(), getBarnesHutTheta(), getGravity(), (isStrongGravityMode()) ? (ForceFactory.builder.getStrongGravity(getScalingRatio())) : (Repulsion), getScalingRatio(), rootRegion, Repulsion));
                threads.add(future);
            }
            for (Future future : threads) {
                try {
                    future.get();
                } catch (Exception e) {
                    throw new RuntimeException("Unable to layout " + this.getClass().getSimpleName() + ".", e);
                }
            }

          // Attraction
            // Edge Weight
            AttractionForce Attraction = ForceFactory.builder.buildAttraction(isLinLogMode(), isOutboundAttractionDistribution(), isAdjustSizes(), 1 * ((isOutboundAttractionDistribution()) ? (outboundAttCompensation) : (1)));
            if (getEdgeWeightInfluence() == 0) {
                for (Edge e : edges) {
                    Attraction.apply(e.getSource(), e.getTarget(), 1);
                }
            } else if (getEdgeWeightInfluence() == 1) {
                for (Edge e : edges) {
                    Attraction.apply(e.getSource(), e.getTarget(), getEdgeWeight(e, isDynamicWeight, interval));
                }
            } else {
                for (Edge e : edges) {
                    Attraction.apply(e.getSource(), e.getTarget(), Math.pow(getEdgeWeight(e, isDynamicWeight, interval), getEdgeWeightInfluence()));
                }
            }

            //by jj2018
            // Node Weight
            AttractionForce NodeAttraction = ForceFactory.builder.buildAttraction(isLinLogMode(), isOutboundAttractionDistribution(), isAdjustSizes(), 1 * ((isOutboundAttractionDistribution()) ? (outboundAttCompensation) : (1)));


            for (Edge e : edges) {
              Node node_a = e.getSource();
              Node node_b = e.getTarget();


              if (hasNodeWeight(node_a) && hasNodeWeight(node_b)) {
                Double wt_a = 0.0;
                Double wt_b = 0.0;
                wt_a = getNodeWeight(node_a, isDynamicNodeWeight, interval);
                wt_b = getNodeWeight(node_b, isDynamicNodeWeight, interval);



                // red-blue heat map mode
                if (isHeatMapMode()) {

                  HeatmapFeatures.adjustColorWeight(node_a, wt_a);
                  HeatmapFeatures.adjustColorWeight(node_b, wt_b);
                }

                // red-green change-oriented heat map mode
                if (isHeatMapChangeMode() && isDynamicNodeWeight) {


                  Double change_a = getNodeWeightChange(node_a, isDynamicNodeWeight, interval, wt_a);
                  Double change_b = getNodeWeightChange(node_b, isDynamicNodeWeight, interval, wt_b);
                  HeatmapFeatures.adjustColorChange(node_a, change_a);
                  HeatmapFeatures.adjustColorChange(node_b, change_b);


                }
                // blue-yellow change-oriented heat map mode
                if (isHeatMapChangeModeCB() && isDynamicNodeWeight) {

                  Double change_a = getNodeWeightChange(node_a, isDynamicNodeWeight, interval, wt_a);
                  Double change_b = getNodeWeightChange(node_b, isDynamicNodeWeight, interval, wt_b);
                  HeatmapFeatures.adjustColorblindChange(node_a, change_a);
                  HeatmapFeatures.adjustColorblindChange(node_b, change_b);


                }

                // Opacity applied last so both can be used
                if (isOpacityMode()){
                  HeatmapFeatures.adjustOpacity(node_a, wt_a);
                  HeatmapFeatures.adjustOpacity(node_b, wt_b);
                }



                // Node forces applied
                NodeAttraction.apply(node_a, node_b, (getNodeWeightScaling()*(wt_a + wt_b)/2)); }

            }
            //



            if (getEdgeWeightInfluence() == 0) {
                for (Edge e : edges) {
                    Attraction.apply(e.getSource(), e.getTarget(), 1);
                }
            } else if (getEdgeWeightInfluence() == 1) {
                for (Edge e : edges) {
                    Attraction.apply(e.getSource(), e.getTarget(), getEdgeWeight(e, isDynamicWeight, interval));
                }
            } else {
                for (Edge e : edges) {
                    Attraction.apply(e.getSource(), e.getTarget(), Math.pow(getEdgeWeight(e, isDynamicWeight, interval), getEdgeWeightInfluence()));
                }
            }

            // Auto adjust speed
            double totalSwinging = 0d;  // How much irregular movement
            double totalEffectiveTraction = 0d;  // Hom much useful movement
            for (Node n : nodes) {
                ForceAtlas2LayoutData nLayout = n.getLayoutData();
                if (!n.isFixed()) {
                    double swinging = Math.sqrt(Math.pow(nLayout.old_dx - nLayout.dx, 2) + Math.pow(nLayout.old_dy - nLayout.dy, 2));
                    totalSwinging += nLayout.mass * swinging;   // If the node has a burst change of direction, then it's not converging.
                    totalEffectiveTraction += nLayout.mass * 0.5 * Math.sqrt(Math.pow(nLayout.old_dx + nLayout.dx, 2) + Math.pow(nLayout.old_dy + nLayout.dy, 2));
                }
            }
            // We want that swingingMovement < tolerance * convergenceMovement

            // Optimize jitter tolerance
            // The 'right' jitter tolerance for this network. Bigger networks need more tolerance. Denser networks need less tolerance. Totally empiric.
            double estimatedOptimalJitterTolerance = 0.05 * Math.sqrt(nodes.length);
            double minJT = Math.sqrt(estimatedOptimalJitterTolerance);
            double maxJT = 10;
            double jt = jitterTolerance * Math.max(minJT, Math.min(maxJT, estimatedOptimalJitterTolerance * totalEffectiveTraction / Math.pow(nodes.length, 2)));

            double minSpeedEfficiency = 0.05;

            // Protection against erratic behavior
            if (totalSwinging / totalEffectiveTraction > 2.0) {
                if (speedEfficiency > minSpeedEfficiency) {
                    speedEfficiency *= 0.5;
                }
                jt = Math.max(jt, jitterTolerance);
            }

            double targetSpeed = jt * speedEfficiency * totalEffectiveTraction / totalSwinging;

            // Speed efficiency is how the speed really corresponds to the swinging vs. convergence tradeoff
            // We adjust it slowly and carefully
            if (totalSwinging > jt * totalEffectiveTraction) {
                if (speedEfficiency > minSpeedEfficiency) {
                    speedEfficiency *= 0.7;
                }
            } else if (speed < 1000) {
                speedEfficiency *= 1.3;
            }

            // But the speed shoudn't rise too much too quickly, since it would make the convergence drop dramatically.
            double maxRise = 0.5;   // Max rise: 50%
            speed = speed + Math.min(targetSpeed - speed, maxRise * speed);

            // Apply forces
            if (isAdjustSizes()) {
                // If nodes overlap prevention is active, it's not possible to trust the swinging mesure.
                for (Node n : nodes) {
                    ForceAtlas2LayoutData nLayout = n.getLayoutData();
                    if (!n.isFixed()) {

                        // Adaptive auto-speed: the speed of each node is lowered
                        // when the node swings.
                        double swinging = nLayout.mass * Math.sqrt((nLayout.old_dx - nLayout.dx) * (nLayout.old_dx - nLayout.dx) + (nLayout.old_dy - nLayout.dy) * (nLayout.old_dy - nLayout.dy));
                        double factor = 0.1 * speed / (1f + Math.sqrt(speed * swinging));

                        double df = Math.sqrt(Math.pow(nLayout.dx, 2) + Math.pow(nLayout.dy, 2));
                        factor = Math.min(factor * df, 10.) / df;

                        double x = n.x() + nLayout.dx * factor;
                        double y = n.y() + nLayout.dy * factor;

                        n.setX((float) x);
                        n.setY((float) y);
                    }
                }
            } else {
                for (Node n : nodes) {
                    ForceAtlas2LayoutData nLayout = n.getLayoutData();
                    if (!n.isFixed()) {

                        // Adaptive auto-speed: the speed of each node is lowered
                        // when the node swings.
                        double swinging = nLayout.mass * Math.sqrt((nLayout.old_dx - nLayout.dx) * (nLayout.old_dx - nLayout.dx) + (nLayout.old_dy - nLayout.dy) * (nLayout.old_dy - nLayout.dy));
                        //double factor = speed / (1f + Math.sqrt(speed * swinging));
                        double factor = speed / (1f + Math.sqrt(speed * swinging));

                        double x = n.x() + nLayout.dx * factor;
                        double y = n.y() + nLayout.dy * factor;

                        n.setX((float) x);
                        n.setY((float) y);
                    }
                }
            }
        } finally {
            graph.readUnlockAll();
        }
    }


    @Override
    public boolean canAlgo() {
        return graphModel != null;
    }

    @Override
    public void endAlgo() {
        graph.readLock();
        try {
            for (Node n : graph.getNodes()) {
                n.setLayoutData(null);
            }
            pool.shutdown();
        } finally {
            graph.readUnlockAll();
        }
    }



    @Override
    public LayoutProperty[] getProperties() {
        List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
        final String FORCEATLAS2_TUNING = NbBundle.getMessage(getClass(), "ForceAtlas2.tuning");
        final String FORCEATLAS2_BEHAVIOR = NbBundle.getMessage(getClass(), "ForceAtlas2.behavior");
        final String FORCEATLAS2_PERFORMANCE = NbBundle.getMessage(getClass(), "ForceAtlas2.performance");
        final String FORCEATLAS2_THREADS = NbBundle.getMessage(getClass(), "ForceAtlas2.threads");

        try {
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.scalingRatio.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.scalingRatio.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.scalingRatio.desc"),
                    "getScalingRatio", "setScalingRatio"));
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.nodeWeightScaling.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.nodeWeightScaling.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.nodeWeightScaling.desc"),
                    "getNodeWeightScaling", "setNodeWeightScaling"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.strongGravityMode.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.strongGravityMode.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.strongGravityMode.desc"),
                    "isStrongGravityMode", "setStrongGravityMode"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.opacityMode.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.opacityMode.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.opacityMode.desc"),
                    "isOpacityMode", "setOpacityMode"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.heatMapMode.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.heatMapMode.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.heatMapMode.desc"),
                    "isHeatMapMode", "setHeatMapMode"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.heatMapChangeMode.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.heatMapChangeMode.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.heatMapChangeMode.desc"),
                    "isHeatMapChangeMode", "setHeatMapChangeMode"));
            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.heatMapChangeModeCB.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.heatMapChangeModeCB.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.heatMapChangeModeCB.desc"),
                    "isHeatMapChangeModeCB", "setHeatMapChangeModeCB"));
            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.gravity.name"),
                    FORCEATLAS2_TUNING,
                    "ForceAtlas2.gravity.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.gravity.desc"),
                    "getGravity", "setGravity"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.distributedAttraction.name"),
                    FORCEATLAS2_BEHAVIOR,
                    "ForceAtlas2.distributedAttraction.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.distributedAttraction.desc"),
                    "isOutboundAttractionDistribution", "setOutboundAttractionDistribution"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.linLogMode.name"),
                    FORCEATLAS2_BEHAVIOR,
                    "ForceAtlas2.linLogMode.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.linLogMode.desc"),
                    "isLinLogMode", "setLinLogMode"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.adjustSizes.name"),
                    FORCEATLAS2_BEHAVIOR,
                    "ForceAtlas2.adjustSizes.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.adjustSizes.desc"),
                    "isAdjustSizes", "setAdjustSizes"));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.edgeWeightInfluence.name"),
                    FORCEATLAS2_BEHAVIOR,
                    "ForceAtlas2.edgeWeightInfluence.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.edgeWeightInfluence.desc"),
                    "getEdgeWeightInfluence", "setEdgeWeightInfluence"));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.jitterTolerance.name"),
                    FORCEATLAS2_PERFORMANCE,
                    "ForceAtlas2.jitterTolerance.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.jitterTolerance.desc"),
                    "getJitterTolerance", "setJitterTolerance"));

            properties.add(LayoutProperty.createProperty(
                    this, Boolean.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutOptimization.name"),
                    FORCEATLAS2_PERFORMANCE,
                    "ForceAtlas2.barnesHutOptimization.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutOptimization.desc"),
                    "isBarnesHutOptimize", "setBarnesHutOptimize"));

            properties.add(LayoutProperty.createProperty(
                    this, Double.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutTheta.name"),
                    FORCEATLAS2_PERFORMANCE,
                    "ForceAtlas2.barnesHutTheta.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.barnesHutTheta.desc"),
                    "getBarnesHutTheta", "setBarnesHutTheta"));

            properties.add(LayoutProperty.createProperty(
                    this, Integer.class,
                    NbBundle.getMessage(getClass(), "ForceAtlas2.threads.name"),
                    FORCEATLAS2_THREADS,
                    "ForceAtlas2.threads.name",
                    NbBundle.getMessage(getClass(), "ForceAtlas2.threads.desc"),
                    "getThreadsCount", "setThreadsCount"));

        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }

        return properties.toArray(new LayoutProperty[0]);
    }

    @Override
    public void resetPropertiesValues() {
        int nodesCount = 0;

        if (graphModel != null) {
            nodesCount = graphModel.getGraphVisible().getNodeCount();
        }

        // Tuning
        if (nodesCount >= 100) {
            setScalingRatio(2.0);
        } else {
            setScalingRatio(10.0);
        }
        // strong gravity a default to counteract runaway nodes
        //by jj2018
        setStrongGravityMode(true);
        setNodeWeightScaling(1.);
        setHeatMapMode(false);
        setHeatMapChangeMode(false);
        setHeatMapChangeModeCB(false);
        setOpacityMode(false);
        //
        setGravity(1.);



        // Behavior
        setOutboundAttractionDistribution(false);
        setLinLogMode(false);
        setAdjustSizes(false);
        setEdgeWeightInfluence(1.);

        // Performance
        setJitterTolerance(1d);
        if (nodesCount >= 1000) {
            setBarnesHutOptimize(true);
        } else {
            setBarnesHutOptimize(false);
        }
        setBarnesHutTheta(1.2);
        setThreadsCount(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
    }



    @Override
    public LayoutBuilder getBuilder() {
        return layoutBuilder;
    }

    @Override
    public void setGraphModel(GraphModel graphModel) {
        this.graphModel = graphModel;
        // Trick: reset here to take the profile of the graph in account for default values
        resetPropertiesValues();
    }

    public Double getBarnesHutTheta() {
        return barnesHutTheta;
    }

    public void setBarnesHutTheta(Double barnesHutTheta) {
        this.barnesHutTheta = barnesHutTheta;
    }

    public Double getEdgeWeightInfluence() {
        return edgeWeightInfluence;
    }

    public void setEdgeWeightInfluence(Double edgeWeightInfluence) {
        this.edgeWeightInfluence = edgeWeightInfluence;
    }

    public Double getJitterTolerance() {
        return jitterTolerance;
    }

    public void setJitterTolerance(Double jitterTolerance) {
        this.jitterTolerance = jitterTolerance;
    }

    public Boolean isLinLogMode() {
        return linLogMode;
    }

    public void setLinLogMode(Boolean linLogMode) {
        this.linLogMode = linLogMode;
    }

    public Double getScalingRatio() {
        return scalingRatio;
    }

    public void setScalingRatio(Double scalingRatio) {
        this.scalingRatio = scalingRatio;
    }
    //by jj2018
    public Boolean isStrongGravityMode() {
        return strongGravityMode;
    }

    public void setStrongGravityMode(Boolean strongGravityMode) {
        this.strongGravityMode = strongGravityMode;
    }

    public Boolean isOpacityMode() {
        return opacityMode;
    }

    public void setOpacityMode(Boolean opacityMode) {
        this.opacityMode = opacityMode;
    }

    public Boolean isHeatMapMode() {
        return heatMapMode;
    }

    public void setHeatMapMode(Boolean heatMapMode) {
        this.heatMapMode = heatMapMode;
    }

    public Boolean isHeatMapChangeMode() {
        return heatMapChangeMode;
    }

    public void setHeatMapChangeMode(Boolean heatMapChangeMode) {
        this.heatMapChangeMode = heatMapChangeMode;
    }

    public Boolean isHeatMapChangeModeCB() {
        return heatMapChangeModeCB;
    }

    public void setHeatMapChangeModeCB(Boolean heatMapChangeModeCB) {
        this.heatMapChangeModeCB = heatMapChangeModeCB;
    }

    public Double getNodeWeightScaling() {
      return nodeWeightScaling;
    }

    public void setNodeWeightScaling(Double factor) {
      this.nodeWeightScaling = factor;
    }

    //

    public Double getGravity() {
        return gravity;
    }

    public void setGravity(Double gravity) {
        this.gravity = gravity;
    }

    public Integer getThreadsCount() {
        return threadCount;
    }

    public void setThreadsCount(Integer threadCount) {
        this.threadCount = Math.max(1, threadCount);
    }

    public Boolean isOutboundAttractionDistribution() {
        return outboundAttractionDistribution;
    }

    public void setOutboundAttractionDistribution(Boolean outboundAttractionDistribution) {
        this.outboundAttractionDistribution = outboundAttractionDistribution;
    }

    public Boolean isAdjustSizes() {
        return adjustSizes;
    }

    public void setAdjustSizes(Boolean adjustSizes) {
        this.adjustSizes = adjustSizes;
    }

    public Boolean isBarnesHutOptimize() {
        return barnesHutOptimize;
    }

    public void setBarnesHutOptimize(Boolean barnesHutOptimize) {
        this.barnesHutOptimize = barnesHutOptimize;
    }
}
