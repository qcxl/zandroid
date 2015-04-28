package com.zcj.util;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UtilMath {
	
	/** 计算百分比 */
	public static String percent(int value, int full) {
		if (full == 0 || value < 0 || full <= 0) {return null;}
		return percent(Double.valueOf(value), Double.valueOf(full));
	}
	
	/** 计算百分比 */
	public static String percent(Long value, Long full) {
		if (full == 0 || value < 0 || full <= 0) {return null;}
		return percent(Double.valueOf(value), Double.valueOf(full));
	}
	
	/** 计算百分比 */
	public static String percent(Double value, Double full) {
		if (value == null || full == null) {
			return null;
		}
		double percent = 0;
		if (value > 0 && full > 0) {
			percent = value / full;
		}
		NumberFormat nt = NumberFormat.getPercentInstance();
		nt.setMinimumFractionDigits(2);
		return nt.format(percent);
	}

	/** 计算平均值 */
	public static Double avgValue(Double[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		Double sum = sumValue(array);
		if (sum == null) {return null;}
		return sum / array.length;
	}
	
	/** 计算总分 */
	public static Double sumValue(Double[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		double sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	/** 计算极大值 */
	public static Double maxValue(Double[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		Arrays.sort(array);
		return array[array.length - 1];
	}

	/** 计算极小值 */
	public static Double minValue(Double[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		Arrays.sort(array);
		return array[0];
	}

	/** 计算中值 */
	public static Double midValue(Double[] array) {
		if (array == null || array.length == 0) {
			return null;
		}
		List<Double> list = Arrays.asList(array);
		Collections.sort(list);
		if (list.size() % 2 == 1) {
			return list.get((list.size() - 1) / 2);
		} else {
			return avgValue(new Double[] { list.get(list.size() / 2), list.get((list.size() / 2) - 1) });
		}
	}

	/** 计算方差 */
	public static Double variance(Double[] values) {
		if (values == null || values.length == 0) {
			return null;
		}
		Double avg = avgValue(values);
		if (avg == null) {return null;}

		double sum = 0D;
		for (int i = 0; i < values.length; i++) {
			sum += (values[i] - avg) * (values[i] - avg);
		}
		sum /= values.length;
		return sum;
	}
	
	/** 计算标准差 */
	public static Double standardDeviation(Double[] values) {
		if (values == null || values.length == 0) {
			return null;
		}
		Double variance = variance(values);
		if (variance == null) {return null;}
		return Math.sqrt(variance);
	}

	/** 计算标准分[原始分数-所有数据的平均分]/所有数据的标准差 */
	public static Double[] standardScore(Double[] all, Double[] myValues) {
		if (all == null || all.length == 0 || myValues == null || myValues.length == 0 || all.length < myValues.length) {
			return null;
		}
		Double[] s = new Double[myValues.length];
		Double scoreMean = avgValue(all);
		if (scoreMean == null) {return null;}
		Double standardDeviation = standardDeviation(all);
		if (standardDeviation == null || standardDeviation == 0) {return null;}
		for (int i = 0; i < myValues.length; i++) {
			s[i] = (myValues[i] - scoreMean) / standardDeviation;
		}
		return s;
	}
	
	/** 计算T总分均值（初中：70+15*标准分；小学：80+10*标准分） */
	public static Double tAvgValue(Double[] all, Double[] myValues, String type) {
		if (all == null || all.length == 0 || myValues == null || myValues.length == 0 || all.length < myValues.length) {
			return null;
		}
		Double[] s = standardScore(all, myValues);
		if (s == null) {return null;}
		Double avg = avgValue(s);
		if (avg == null) {return null;}
		if ("初中".equals(type)) {
			return 15*avg + 70D;
		} else if ("小学".equals(type)) {
			return 10*avg + 80D;
		} else {
			return null;
		}
	}

	/** 计算难度值 */
	public static Double difficultyValue(Double avg, Double full) {
		if (avg == null || full == null || full.floatValue() == 0) {
			return null;
		}
		return avg / full;
	}
	
	/** 计算满分数 */
	public static int fullCount(Double[] arrays, Double full) {
		int result = 0;
		if (arrays != null && arrays.length > 0 && full != null && full.floatValue() > 0) {
			for(Double s : arrays) {
				if (s.floatValue() == full.floatValue()) {
					result++;
				}
			}
		}
		return result;
	}
	
	/** 计算零分数 */
	public static int zeroCount(Double[] arrays) {
		int result = 0;
		if (arrays != null && arrays.length > 0) {
			for (Double s : arrays) {
				if (s.floatValue() == 0) {
					result++;
				}
			}
		}
		return result;
	}

	public static void main(String[] args) {
		System.out.println((int) 1.95);
	}

}
