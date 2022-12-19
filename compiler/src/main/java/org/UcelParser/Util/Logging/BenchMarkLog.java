package org.UcelParser.Util.Logging;

import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BenchMarkLog extends InfoLog {
    public BenchMarkLog(ArrayList<double[]> times) {
        super("");

        this.message = String.format("%nLexing/Parsing: %.2f +- %.2f%nReference Handler: %.2f +- %.2f%n" +
                "Type Checking: %.2f +- %.2f%nInterpreter: %.2f +- %.2f%nCode generation: %.2f +- %.2f%n" +
                "Project linking: %.2f +- %.2f%n",
                mean(times.get(0)), confidenceInterval(times.get(0), 0.05),
                mean(times.get(1)), confidenceInterval(times.get(1), 0.05),
                mean(times.get(2)), confidenceInterval(times.get(2), 0.05),
                mean(times.get(3)), confidenceInterval(times.get(3), 0.05),
                mean(times.get(4)), confidenceInterval(times.get(4), 0.05),
                mean(times.get(5)), confidenceInterval(times.get(5), 0.05)
                );
    }


    // From: https://github.com/marcelovca90/confidence-interval
    private static double confidenceInterval(double[] values, double significance)
    {
        DescriptiveStatistics statistics = new DescriptiveStatistics(values);
        TDistribution tDist = new TDistribution(statistics.getN() - 1);
        double a = tDist.inverseCumulativeProbability(1.0 - significance / 2);
        return a * statistics.getStandardDeviation() / Math.sqrt(statistics.getN());
    }

    private static double mean(double[] values) {
        double sum = 0.0;
        for (var val : values) {
            sum += val;
        }

        return sum / values.length;
    }

}
