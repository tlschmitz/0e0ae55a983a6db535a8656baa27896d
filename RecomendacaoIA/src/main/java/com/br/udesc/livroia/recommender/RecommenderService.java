package com.br.udesc.livroia.recommender;

/**
 * "Recommendation system (Advanced)"
 *
 * Implement a recommendation system based on collaborative filtering.
 * http://en.wikipedia.org/wiki/Collaborative_filtering
 *
 * @author http://bostjankaluza.net
 */

import java.io.InputStream;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.LinearNNSearch;

import java.util.*;

public class RecommenderService {
	public static String run(InputStream crowdRatingData, InputStream userRatingData) throws Exception {
                String recomendations ="";

		// read learning dataset
		DataSource source = new DataSource(crowdRatingData);//"dataset/movieRatings.arff");
		Instances dataset = source.getDataSet();
		
		// read user data
		source = new DataSource(userRatingData);//"dataset/user.arff");
		Instances userRating = source.getDataSet();
		Instance userData = userRating.firstInstance();

		LinearNNSearch kNN = new LinearNNSearch(dataset);
		Instances neighbors = null;
		double[] distances = null;

		try {
			neighbors = kNN.kNearestNeighbours(userData, 5);
			distances = kNN.getDistances();
		} catch (Exception e) {
			System.out.println("Neighbors could not be found.");
			return null;
		}

		double[] similarities = new double[distances.length];
		for (int i = 0; i < distances.length; i++) {
			similarities[i] = 1.0 / distances[i];
			//System.out.println(similarities[i]);
		}

		Map<String, List<Integer>> recommendations = new HashMap<String, List<Integer>>();
		for(int i = 0; i < neighbors.numInstances(); i++){
			Instance currNeighbor = neighbors.instance(i);

			for (int j = 0; j < currNeighbor.numAttributes(); j++) {
				// item is not ranked by the user, but is ranked by neighbors 
				if (userData.value(j) < 1) {
					// retrieve the name of the movie
					String attrName = userData.attribute(j).name();
					List<Integer> lst = new ArrayList<Integer>();
					if (recommendations.containsKey(attrName)) {
						lst = recommendations.get(attrName);
					}
					
					lst.add((int)currNeighbor.value(j));
					recommendations.put(attrName, lst);
				}
			}

		}

		List<RecommendationRecord> finalRanks = new ArrayList<RecommendationRecord>();

		Iterator<String> it = recommendations.keySet().iterator();
		while (it.hasNext()) {
			String atrName = it.next();
			double totalImpact = 0;
			double weightedSum = 0;
			List<Integer> ranks = recommendations.get(atrName);
			for (int i = 0; i < ranks.size(); i++) {
				int val = ranks.get(i);
				totalImpact += similarities[i];
				weightedSum += (double) similarities[i] * val;
			}
			RecommendationRecord rec = new RecommendationRecord();
			rec.attributeName = atrName;
			rec.score = weightedSum / totalImpact;

			finalRanks.add(rec);
		}
		Collections.sort(finalRanks);

		// print top 3 recommendations
		System.out.println(finalRanks.get(0));
		System.out.println(finalRanks.get(1));
		System.out.println(finalRanks.get(2));
                recomendations+="1) "+finalRanks.get(0)+"\n";
                recomendations+="2) "+finalRanks.get(1)+"\n";
                recomendations+="3) "+finalRanks.get(2);
                return recomendations;
	}

	static class RecommendationRecord implements Comparable<RecommendationRecord> {
		public double score;
		public String attributeName;

		public int compareTo(RecommendationRecord other) {
			if (this.score > other.score)
				return -1;
			if (this.score < other.score)
				return 1;
			return 0;
		}

		public String toString() {
			return attributeName + ": " + score;
		}
	}

}
