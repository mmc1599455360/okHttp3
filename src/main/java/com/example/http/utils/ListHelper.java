package com.example.http.utils;

import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListHelper {

	/**
	 * 去除list集合中重复项
	 *
	 * @param list
	 *
	 * @return
	 *
	 * @throws @author
	 * @since 2016-8-17 下午3:14:01
	 */
	public static List<String> removeDuplicate(List<String> list) {
		if (isBlank(list)) {
			return list;
		}
		Set<String> set = new HashSet<String>();
		for (String str : list) {
			if (StringUtils.isNotBlank(str)) {
				set.add(str.trim());
			}
		}
		list = new ArrayList<>(set);
		return list;
	}

	public static List<Long> stringToLongList(List<String> inList) {
		val iList = new ArrayList<Long>(inList.size());
		inList.forEach(x -> iList.add(Long.parseLong(x)));
		return iList;
	}

	public static List<String> longToLongString(List<Long> inList) {
		val iList = new ArrayList<String>(inList.size());
		inList.forEach(x -> iList.add(String.valueOf(x)));
		return iList;
	}

	/**
	 * 返回重复的元素
	 *
	 */
	public static List<String> returnRepeat(List<String> list) {
		Integer count = 1;
		Set<String> set = new HashSet<String>();
		List<String> repeats = new ArrayList<String>();
		for (String str : list) {
			if (StringUtils.isNotBlank(str)) {
				set.add(str.trim());
				if (set.size() == count) {
					count++;
				}
				else {
					repeats.add(str.trim());
				}
			}
		}
		return repeats;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isBlank(List list) {
		if (list == null || list.size() == 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isNotBlank(List list) {
		return !isBlank(list);
	}

	public static List<Integer> change(Object obj) {
		if (obj == null) {
			return null;
		}
		String str = obj.toString();
		if (StringUtils.isBlank(str)) {
			return null;
		}
		try {
			str = str.replace("[", "");
			str = str.replace("]", "");
			str = str.replace(" ", "");
			String[] nums = str.split(",");
			List<Integer> l = new ArrayList<>();
			for (String num : nums) {
				l.add(Integer.parseInt(num));
			}
			return l;
		}
		catch (Exception e) {
		}
		return null;
	}
}
