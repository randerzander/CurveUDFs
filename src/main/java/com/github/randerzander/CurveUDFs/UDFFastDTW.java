package com.github.randerzander.CurveUDFs;
 
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.DoubleWritable;

import com.timeseries.TimeSeries;
import com.timeseries.TimeSeriesPoint;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;
import com.dtw.TimeWarpInfo;
import com.dtw.FastDTW;
import com.dtw.DTW;

import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class UDFFastDTW extends UDF {
  public Double evaluate(ArrayList<Double> x1, ArrayList<Double> y1, ArrayList<Double> x2, ArrayList<Double>y2, int length) {
    TimeSeries curve1 = new TimeSeries(1);
    TimeSeries curve2 = new TimeSeries(1);

    //turn arrays into TimeSeries 
    SortedMap<Double, Double> source_curve = new TreeMap<Double, Double>();
    SortedMap<Double, Double> test_curve = new TreeMap<Double, Double>();
    //sort points
    for(int i=0; i < x1.size(); i++){ source_curve.put(x1.get(i), y1.get(i)); }
    for(int i=0; i < x2.size(); i++){ test_curve.put(x2.get(i), y2.get(i)); }

    //convert to curve objects
    for(Map.Entry<Double, Double> entry : source_curve.entrySet()){
      curve1.addLast(entry.getKey(), new TimeSeriesPoint(new double[]{entry.getValue()}));
    }
    for(Map.Entry<Double, Double> entry : test_curve.entrySet()){
      curve2.addLast(entry.getKey(), new TimeSeriesPoint(new double[]{entry.getValue()}));
    }

    DistanceFunction distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
    TimeWarpInfo info;
    if (length > 0)
      info = FastDTW.getWarpInfoBetween(curve1, curve2, length, distFn);
    else //use actual DTW if arg is <= 0
      info = DTW.getWarpInfoBetween(curve1, curve2, distFn);
    return (Double)info.getDistance();
  }
}
