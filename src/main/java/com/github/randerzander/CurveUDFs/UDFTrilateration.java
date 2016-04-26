package com.github.randerzander.CurveUDFs;
 
import org.apache.hadoop.hive.ql.exec.UDF;

import com.lemmingapex.trilateration.TrilaterationFunction;
import com.lemmingapex.trilateration.NonLinearLeastSquaresSolver;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealMatrix;

import org.apache.commons.lang3.ArrayUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class UDFTrilateration extends UDF {
  public double[] evaluate(ArrayList<Double> xs, ArrayList<Double> ys, ArrayList<Double> distances) {

    double[][] positions = new double[xs.size()][2];
    for (int i = 0; i < xs.size(); i++){
      positions[i] = new double[]{xs.get(i), ys.get(i)};
    }

    NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(
      new TrilaterationFunction(positions, ArrayUtils.toPrimitive(distances.toArray(new Double[distances.size()]))),
      new LevenbergMarquardtOptimizer()
    );
    Optimum optimum = solver.solve();

    // the answer
    double[] centroid = optimum.getPoint().toArray();

    // error and geometry information; may throw SingularMatrixException depending the threshold argument provided
    //RealVector standardDeviation = optimum.getSigma(0);
    //RealMatrix covarianceMatrix = optimum.getCovariances(0);

    return centroid;
  }
}
