package com.github.randerzander.CurveUDFs;
 
import org.apache.hadoop.hive.ql.exec.UDF;

import org.apache.commons.math3.analysis.interpolation.HermiteInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

import org.apache.commons.lang3.ArrayUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class UDFInterpolate extends UDF {
  public ArrayList<Double> evaluate(ArrayList<Double> x1, ArrayList<Double> y1, String method) {
    //Use a sorted treemap to eliminate duplicate points
    SortedMap<Double, Double> curve = new TreeMap<Double, Double>();
    for(int i=0; i < x1.size(); i++){ curve.put(x1.get(i), y1.get(i)); }
    double[] _x1 = new double[curve.size()];
    double[] _y1 = new double[curve.size()];
    int i = 0;
    for(Map.Entry<Double, Double> entry : curve.entrySet()){ 
      _x1[i] = entry.getKey();
      _y1[i++] = entry.getValue();
    }

    PolynomialFunction[] functions = null;
    switch(method){
      case "spline": functions = new SplineInterpolator().interpolate(_x1, _y1).getPolynomials();
        break;
      //case "hermite": functions = new HermiteInterpolator().interpolate(_x1, _y1).getPolynomials();
      //  break;
    }

    ArrayList<Double> ret = new ArrayList();
    for(Double d : functions[functions.length-1].getCoefficients()){ ret.add(d); }
    return ret;
  }
}
