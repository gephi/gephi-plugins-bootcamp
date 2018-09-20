
// Appearance features for node weight plugin
// Submitted in partial fulfillment for the Imperial Computing MSc
// Authors : Julianne Joswiak <jj2816@imperial.ac.uk>

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





public class HeatmapFeatures {

  private static Color colorBlender(Color a, Color b, double proportion){
        float propA = (float) proportion;
        float propB = 1.0f - propA;

        float rgbA[] = new float[3];
        float rgbB[] = new float[3];

        a.getColorComponents(rgbA);
        b.getColorComponents(rgbB);

        Color blend_color = new Color (rgbA[0] * propA + rgbB[0] * propB,
        rgbA[1] * propA + rgbB[1] * propB, rgbA[2] * propA + rgbB[2] * propB);

        return blend_color;
      };

    private static void adjustColor(Node node, Double value, Color lo, Color md, Color hi){

      Color low = lo;
      Color mid = md;
      Color high = hi;

      if (value >= -3.0 && value < -2.5) {
        node.setColor(low);
      } if (value >= -2.5 && value < -2.0) {
        node.setColor(colorBlender(low, mid, (1-0.16667)));
      } if (value >= -2.0 && value < -1.5) {
        node.setColor(colorBlender(low, mid, (1-(2*0.16667))));
      } if (value >= -1.5 && value < -1.0) {
        node.setColor(colorBlender(low, mid, (1-(3*0.16667))));
      } if (value >= -1.0 && value < -0.5) {
        node.setColor(colorBlender(low, mid, (1-(4*0.16667))));
      } if (value >= -0.5 && value < 0) {
        node.setColor(colorBlender(low, mid, (1-(5*0.16667))));
      }

      if (value == 0.0) {
        node.setColor(mid);
      }

      if (value > 0.0 && value < 0.5) {
        node.setColor(colorBlender(mid, high, (1-0.16667)));
      } if (value >= 0.5 && value < 1.0) {
        node.setColor(colorBlender(mid, high, (1-(2*0.16667))));
      } if (value >= 1.0 && value < 1.5) {
        node.setColor(colorBlender(mid, high, (1-(3*0.16667))));
      } if (value >= 1.5 && value < 2.0) {
        node.setColor(colorBlender(mid, high, (1-(4*0.16667))));
      } if (value >= 2.0 && value < 2.5) {
        node.setColor(colorBlender(mid, high, (1-(5*0.16667))));
      } if (value >= 2.5 && value < 3) {
        node.setColor(high);
      }

      if (value < -3.0) {
        node.setColor(Color.gray);
      } if (value > 3.0) {
        node.setColor(Color.black);
      }
    };

      protected static void adjustColorWeight(Node node, Double weight){
        Color blue = new Color(0f, 51/255f, 153/255f);
        Color white = Color.white;
        Color red = new Color(153/255f, 0f, 0f);

        adjustColor(node, weight, blue, white, red);
      };

      protected static void adjustColorChange(Node node, Double diff){
        Color red = new Color(204/255f, 0f, 0f);
        Color white = Color.white;
        Color green = new Color(0f, 204/255f, 0f);

        if (diff != 0.0) {
          adjustColor(node, diff, red, white, green);
        }
      };

      protected static void adjustColorblindChange(Node node, Double diff){
        Color yellow = new Color(255/255f, 255/255f, 0f);
        Color white = Color.white;
        Color blue = new Color(0f, 0f, 255/255f);

        if (diff != 0.0) {
          adjustColor(node, diff, yellow, white, blue);
        }
      };


  //Converts the components of a color, as specified by the HSB model, to an equivalent set of values for the default RGB model.

      protected static void adjustOpacity(Node node, Double weight){
        if (weight >= -3.0 && weight < -2.5) {
          node.setAlpha(0.0833f*1f);

        } if (weight >= -2.5 && weight < -2.0) {
          node.setAlpha(0.0833f*2f);

        } if (weight >= -2.0 && weight < -1.5) {
          node.setAlpha(0.0833f*3f);

        } if (weight >= -1.5 && weight < -1.0) {
          node.setAlpha(0.0833f*4f);

        } if (weight >= -1.0 && weight < -0.5) {
          node.setAlpha(0.0833f*5f);

        } if (weight >= -0.5 && weight < 0) {
          node.setAlpha(0.0833f*6f);

        } if (weight >= 0.0 && weight < 0.5) {
          node.setAlpha(0.0833f*7f);

        } if (weight >= 0.5 && weight < 1.0) {
          node.setAlpha(0.0833f*8f);

        } if (weight >= 1.0 && weight < 1.5) {
          node.setAlpha(0.0833f*9f);

        } if (weight >= 1.5 && weight < 2.0) {
          node.setAlpha(0.0833f*10f);

        } if (weight >= 2.0 && weight < 2.5) {
          node.setAlpha(0.0833f*11f);

        } if (weight >= 2.5 && weight <= 3) {
         node.setAlpha(0.0833f*12f);
        }

        if (weight < -3) {
          node.setColor(Color.white);
        }
        if (weight > 3) {
          node.setColor(Color.black);
        }
      };

}





