package com.freesoft.websearch;

import java.util.ListIterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

public class HtmlParserTest extends TestCase {

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(HtmlParserTest.class);
	}
	
	public void testHtmlParser() throws ParserException {

		Parser parser = Parser.createParser("<html><head></head><body></body></html>", "UTF-8");
		NodeVisitor visitor = new  NodeVisitor() {
			@Override
			public void visitTag(Tag tag) {
				System.out.println("tagName=" + tag.getTagName());
				System.out.println("tagText=" + tag.getText());
//				for(ListIterator<Object> gg : tag.getAttributesEx().listIterator())
//					System.out.println("tagAttributes=" + tag.getAttributesEx().);
			}
		};
		parser.visitAllNodesWith(visitor);
	}
}
