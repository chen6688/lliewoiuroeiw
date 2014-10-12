/**
 * 
 */
package com.freesoft.websearch.crawl;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.CustomFetchStatus;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author chen
 * 
 */
public class WebCrawlerExtend extends WebCrawler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.crawler4j.crawler.WebCrawler#init(int,
	 * edu.uci.ics.crawler4j.crawler.CrawlController)
	 */
	@Override
	public void init(int id, CrawlController crawlController) {
		myId = id;
		pageFetcher = crawlController.getPageFetcher();
		this.robotstxtServer = crawlController.getRobotstxtServer();
		this.docIdServer = crawlController.getDocIdServer();
		this.frontier = crawlController.getFrontier();
		this.myController = crawlController;
		this.isWaitingForNewURLs = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uci.ics.crawler4j.crawler.WebCrawler#processPage(edu.uci.ics.crawler4j
	 * .url.WebURL)
	 */
	@Override
	protected void processPage(WebURL curURL) {
		if (curURL == null) {
			return;
		}
		PageFetchResult fetchResult = null;
		try {
			fetchResult = pageFetcher.fetchHeader(curURL);
			int statusCode = fetchResult.getStatusCode();
			String statusDesc = CustomFetchStatus.getStatusDescription(statusCode);
			handlePageStatusCode(curURL, statusCode, statusDesc);
			if (statusCode != HttpStatus.SC_OK) {
				if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
					if (myController.getConfig().isFollowRedirects()) {
						String movedToUrl = fetchResult.getMovedToUrl();
						if (movedToUrl == null) {
							return;
						}
						int newDocId = docIdServer.getDocId(movedToUrl);
						if (newDocId > 0) {
							// Redirect page is already seen
							return;
						}

						WebURL webURL = new WebURL();
						webURL.setURL(movedToUrl);
						webURL.setParentDocid(curURL.getParentDocid());
						webURL.setParentUrl(curURL.getParentUrl());
						webURL.setDepth(curURL.getDepth());
						webURL.setDocid(-1);
						webURL.setAnchor(curURL.getAnchor());
						if (shouldVisit(webURL) && robotstxtServer.allows(webURL)) {
							webURL.setDocid(docIdServer.getNewDocID(movedToUrl));
							frontier.schedule(webURL);
						}
					}
				} else if (fetchResult.getStatusCode() == CustomFetchStatus.PageTooBig) {
					logger.info("Skipping a page which was bigger than max allowed size: " + curURL.getURL());
				} else {
					onErrorPageStatus(curURL, statusCode, statusDesc);
				}
				return;
			}

			if (!curURL.getURL().equals(fetchResult.getFetchedUrl())) {
				if (docIdServer.isSeenBefore(fetchResult.getFetchedUrl())) {
					// Redirect page is already seen
					return;
				}
				curURL.setURL(fetchResult.getFetchedUrl());
				curURL.setDocid(docIdServer.getNewDocID(fetchResult.getFetchedUrl()));
			}

			Page page = new Page(curURL);

			if (!fetchResult.fetchContent(page)) {
				onContentFetchError(curURL);
				return;
			}
			
			Object pageObj = fetchResult.getOtherObj();
			if (!parsePage(page, curURL.getURL(), pageObj)) {
				onParseError(curURL);
				return;
			}
			
			page.setOtherObj(pageObj);
			if (pageObj instanceof HtmlPage) {
				HtmlPage hp = (HtmlPage) pageObj;

				List<WebURL> toSchedule = new ArrayList<WebURL>();
				int maxCrawlDepth = myController.getConfig().getMaxDepthOfCrawling();
				List<HtmlAnchor> anchorList = hp.getAnchors();
				WebURL webURL = null;
				int docid = curURL.getDocid();
				for (HtmlAnchor anchor : anchorList) {
					webURL = new WebURL();
					webURL.setURL(anchor.getHrefAttribute());
					webURL.setParentDocid(docid);
					webURL.setParentUrl(curURL.getURL());
					int newdocid = docIdServer.getDocId(webURL.getURL());
					if (newdocid > 0) {
						// This is not the first time that this Url is
						// visited. So, we set the depth to a negative
						// number.
						webURL.setDepth((short) -1);
						webURL.setDocid(newdocid);
					} else {
						webURL.setDocid(-1);
						webURL.setDepth((short) (curURL.getDepth() + 1));
						if (maxCrawlDepth == -1 || curURL.getDepth() < maxCrawlDepth) {
							if (shouldVisit(webURL) && robotstxtServer.allows(webURL)) {
								webURL.setDocid(docIdServer.getNewDocID(webURL.getURL()));
								toSchedule.add(webURL);
							}
						}
					}
				}
				frontier.scheduleAll(toSchedule);
			}
			try {
				visit(page);
			} catch (Exception e) {
				logger.error("Exception while running the visit method. Message: '" + e.getMessage() + "' at " + e.getStackTrace()[0]);
			}

		} catch (Exception e) {
			logger.error(e.getMessage() + ", while processing: " + curURL.getURL());
		} finally {
			if (fetchResult != null) {
				fetchResult.discardContentIfNotConsumed();
			}
			onBeforeExit();
		}
	}

	/**
	 * 
	 * @param page
	 * @param contextURL
	 * @param OtherObj
	 * @return
	 */
	protected boolean parsePage(Page page, String contextURL, Object OtherObj) {
		 if (OtherObj instanceof UnexpectedPage) {
			return false;
		}
		return true;
	}
}
