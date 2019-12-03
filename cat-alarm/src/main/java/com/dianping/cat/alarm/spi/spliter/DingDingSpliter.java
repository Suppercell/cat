package com.dianping.cat.alarm.spi.spliter;

import com.dianping.cat.alarm.spi.AlertChannel;

import java.util.regex.Pattern;

public class DingDingSpliter implements Spliter {
	public static final String ID = AlertChannel.DINGDING.getName();

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String process(String content) {
		String dingdingContent = content.replaceAll("<br/>", "\n");
		dingdingContent = Pattern.compile("<div.*(?=</div>)</div>", Pattern.DOTALL).matcher(dingdingContent).replaceAll("");
		dingdingContent = Pattern.compile("<table.*(?=</table>)</table>", Pattern.DOTALL).matcher(dingdingContent).replaceAll("");

		return dingdingContent;
	}
}
