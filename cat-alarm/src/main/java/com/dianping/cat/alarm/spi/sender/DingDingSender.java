package com.dianping.cat.alarm.spi.sender;

import com.dianping.cat.alarm.sender.entity.Par;
import com.dianping.cat.alarm.spi.AlertChannel;
import com.dianping.cat.alarm.spi.dingtalk.DingtalkChatbotClient;
import com.dianping.cat.alarm.spi.dingtalk.message.MarkdownMessage;
import com.dianping.cat.message.Event;
import com.dianping.cat.support.LimitList;
import com.dianping.cat.support.ProblemErrorCache;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DingDingSender extends AbstractSender {
	public static final String ID = AlertChannel.DINGDING.getName();

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean send(SendMessageEntity message) {
		com.dianping.cat.alarm.sender.entity.Sender sender = querySender();
        String webhook = message.getReceiverString();
		return sendDingding(message, webhook, sender);
	}

	private boolean sendDingding(SendMessageEntity message, String webhook,
	      com.dianping.cat.alarm.sender.entity.Sender sender) {
		String domain = message.getGroup();
		String content = message.getContent().replaceAll(",", " ").replaceAll("<a href.*(?=</a>)</a>", "");
		String lastMinutes = new SimpleDateFormat("yyyyMMddHHmm").format(DateUtils.addMinutes(new Date(), -1));
		LimitList<Event> events = ProblemErrorCache.get(message.getGroup());
		List<String> exceptionList = new ArrayList<String>();

		String server = "";
		Integer excLength = 220;
		for (Par par : sender.getPars()) {
			if (par.getId().startsWith("server=")) {
				server = par.getId().replaceFirst("server=", "");
			} else if (par.getId().startsWith("excLength=")) {
				excLength = Integer.parseInt(par.getId().replaceFirst("excLength=", ""));
			}
		}

		if (events != null) {
			for (Event event : events.getList()) {
				Date date = new Date(event.getTimestamp());
				String excTime = new SimpleDateFormat("yyyyMMddHHmm").format(date);
				if (lastMinutes.equals(excTime)) {
					String exc = event.getName() + " " + event.getData();
					if (exc.length() > 500) {
						exc = exc.substring(0, excLength);
					}
					exceptionList.add(exc);
				}
			}
		}

		return sendDingTalk(content, exceptionList, server, domain, webhook);
	}

	private boolean sendDingTalk(String content, List<String> exc, String server, String domain, String webhook) {
		content = content.replaceAll("\n", "");
		MarkdownMessage markdownMessage = new MarkdownMessage();
		markdownMessage.setTitle("CAT告警" + " " + domain);
		markdownMessage.add(MarkdownMessage.getHeaderText(5, "CAT告警" + " " + domain));
		if (StringUtils.isNotBlank(content)) {
			markdownMessage.add(MarkdownMessage.getReferenceText(content));
			String linkUrl = server + "/cat/r/p";
			if (exc != null && !exc.isEmpty() && content.contains("CAT异常告警")) {
				markdownMessage.add(MarkdownMessage.getUnorderListText(exc));
				linkUrl = server + "/cat/r/p?domain=" + domain + "&date="
				      + new SimpleDateFormat("yyyyMMddHH").format(new Date());
			}
			markdownMessage.add(MarkdownMessage.getLinkText("查看", linkUrl));

			try {
				DingtalkChatbotClient.send(markdownMessage, webhook);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}

		}
		return true;
	}
}
