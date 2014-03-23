package programs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.mahout.clustering.WeightedVectorWritable;
import org.apache.mahout.clustering.kmeans.Cluster;
import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;

public class KMeansCluster {
  
  public static void writePointsToFile(List<Vector> points,
                                       String fileName,
                                       FileSystem fs,
                                       Configuration conf) throws IOException {
    Path path = new Path(fileName);
        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf,
            path, LongWritable.class, VectorWritable.class);
        long recNum = 0;
        VectorWritable vec = new VectorWritable();
        for (Vector point : points) {
            vec.set(point);
            writer.append(new LongWritable(recNum++), vec);
        }
        writer.close();
  }
  
  public static List<Vector> getPoints(File file) throws IOException {
    List<Vector> points = new ArrayList<Vector>();
        
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        StringTokenizer st;
        double[] fr = new double[6];
        
        while ((line = br.readLine()) != null) {
            st = new StringTokenizer(line.replace(",", " "));
            while (st.hasMoreTokens()) {
                fr[0] = Double.parseDouble(st.nextToken());
                fr[1] = Double.parseDouble(st.nextToken());
                fr[2] = Double.parseDouble(st.nextToken());
                fr[3] = Double.parseDouble(st.nextToken());
                fr[4] = Double.parseDouble(st.nextToken());
                fr[5] = Double.parseDouble(st.nextToken());
                
                Vector vec = new RandomAccessSparseVector(fr.length);
                vec.assign(fr);
                points.add(vec);
                
            }
            
        }
        
        br.close();
        return points;
  }
  
  
  public static void main(String args[]) throws Exception {
    
        File modelFile1 = null;
        File modelFile2 = null;
	if (args.length > 0){
            modelFile1 = new File(args[0]);
            modelFile2 = new File(args[1]);
        }
	if((modelFile1 == null || !modelFile1.exists()) && (modelFile2 == null || !modelFile2.exists())) {
		modelFile1 = new File("CombinedLogs1.csv");
                modelFile2 = new File("Vector.csv");
        }
	if(!modelFile1.exists() && !modelFile2.exists()) {
		System.err.println("Please, specify name of file, or put file 'input.csv' into current directory!");
		System.exit(1);
        }
      
    PreProcessor preprocess = new PreProcessor((modelFile1));
    preprocess.logToVector();
        
    int k = 18;
    
    List<Vector> vectors = getPoints(modelFile2);
    File testData = new File("testdata");
    if (!testData.exists()) {
      testData.mkdir();
    }
    testData = new File("testdata/points");
    if (!testData.exists()) {
      testData.mkdir();
    }
    
    Configuration conf = new Configuration();
    FileSystem fs = FileSystem.get(conf);
    writePointsToFile(vectors, "testdata/points/file1", fs, conf);
    
    Path path = new Path("testdata/clusters/part-00000");
    SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf,
        path, Text.class, Cluster.class);
    
    for (int i = 0; i < k; i++) {
      Vector vec = vectors.get(i);
      Cluster cluster = new Cluster(vec, i, new EuclideanDistanceMeasure());
      writer.append(new Text(cluster.getIdentifier()), cluster);
    }
    writer.close();
    
    KMeansDriver.run(conf, new Path("testdata/points"), new Path("testdata/clusters"),
      new Path("output"), new EuclideanDistanceMeasure(), 0.001, 10,
      true, false);
    
    SequenceFile.Reader reader = new SequenceFile.Reader(fs,
        new Path("output/" + Cluster.CLUSTERED_POINTS_DIR
                 + "/part-m-00000"), conf);
    
    IntWritable key = new IntWritable();
    WeightedVectorWritable value = new WeightedVectorWritable();
    while (reader.next(key, value)) {
      System.out.println(value.toString() + " belongs to cluster "
                         + key.toString());
    }
    reader.close();
  }
  
}
