/**
 * Library from HTML parsing : https://jsoup.org
 * 
 * Code based on : 
 * - http://stackoverflow.com/a/14226456/214898
 * - http://elliotth.blogspot.ca/2005/01/copying-html-to-clipboard-from-java.html
 */

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ClipBoardListener extends Thread implements ClipboardOwner {
	Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

	private static DataFlavor HTML_FLAVOR = new DataFlavor("text/html;class=java.io.Reader", "HTML");
	private int nbImagesConverted = 0;
	private Transferable currentTransferable;
	
	@Override
	public void run() {
		Transferable trans = sysClip.getContents(this);
		TakeOwnership(trans);
	}

	@Override
	public void lostOwnership(Clipboard c, Transferable t) {

		System.out.println("Copy to clipboard detected");
		try {
			ClipBoardListener.sleep(250); // waiting e.g for loading huge
											// elements like word's etc.
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		Transferable contents = sysClip.getContents(this);
		try {
			process_clipboard(contents, c);
		} catch (Exception ex) {
			Logger.getLogger(ClipBoardListener.class.getName()).log(Level.SEVERE, null, ex);
		}
		TakeOwnership(currentTransferable);

	}

	void TakeOwnership(Transferable t) {
		sysClip.setContents(t, this);
	}
	
	private void getHtmlDataFlavor(Transferable t) {
		DataFlavor df = null;
		
		for (DataFlavor tDf : t.getTransferDataFlavors()) {
			if (tDf.getMimeType().contains("text/html")) {
				if (tDf.getRepresentationClass() == java.io.Reader.class) {
					df = tDf;
					break;
				}
			}
		}
		
		HTML_FLAVOR = df;
	}

	public void process_clipboard(Transferable t, Clipboard c) {
																
		String tempText = "";
		Transferable trans = t;
		currentTransferable = t;
		
		getHtmlDataFlavor(t);
		if (HTML_FLAVOR == null) {
			System.out.println("No HTML flavor detected");
			return;	
		}
		
		nbImagesConverted = 0;
		try {
			if (trans != null ? trans.isDataFlavorSupported(HTML_FLAVOR) : false) {
				if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
					tempText = (String) trans.getTransferData(DataFlavor.stringFlavor);
				}
				java.io.Reader r = (java.io.Reader) trans.getTransferData(HTML_FLAVOR);
				
				StringBuilder content = getReaderContent(r);
				String newHtml = changeImages(content);
				
				currentTransferable = new HtmlSelection(newHtml, tempText);
				System.out.println("Converted " + nbImagesConverted + " images");
			} else {
				System.out.println("Not converted:" + trans.isDataFlavorSupported(HTML_FLAVOR));
				System.out.println(trans.getTransferData(HTML_FLAVOR));
				/*
				for (DataFlavor tt : trans.getTransferDataFlavors()) {
					if (tt.getMimeType().contains("text/html")) {
						System.out.println("-------");
						System.out.println(tt.toString());
					}
				}
				*/
			}

		} catch (Exception e) {
			currentTransferable = t;
			System.out.println("Conversion error");
			e.printStackTrace();
		}
	}
	
	private String changeImages(StringBuilder content) throws RuntimeException, IOException {
		Document doc = Jsoup.parse(content.toString());
		Elements imgs = doc.select("img");
		for (Element img : imgs) {
			String filePath = img.attr("src");
			filePath = filePath.replace("file:///", "");
			filePath = filePath.replace("file://", "");
			
			File file = new File(filePath);
			if (file.exists()) {
				String encoded = Base64.encodeBase64String(FileUtils.readFileToByteArray(file));
				String extension = file.getName();
				extension = extension.substring(extension.lastIndexOf(".") + 1);
				String dataURL = "data:image/" + extension + ";base64," + encoded;
				
		    	img.attr("src", dataURL); // or whatever
		    	nbImagesConverted++;
			}
		}

		String html = doc.outerHtml();
		html = html.replaceAll("(?s)<!--.*?-->", ""); //Remove html comments
		return html; // returns the modified HTML
	}
	
	
	private StringBuilder getReaderContent(java.io.Reader r) throws IOException {
	    char[] arr = new char[8 * 1024];
	    StringBuilder buffer = new StringBuilder();
	    int numCharsRead;
	    while ((numCharsRead = r.read(arr, 0, arr.length)) != -1) {
	        buffer.append(arr, 0, numCharsRead);
	    }
	    r.close();
	    return buffer;
	}
	
    private static class HtmlSelection implements Transferable {

        private String html;
        private String plainText;



        public HtmlSelection(String html, String plainText) {

            this.html = html;
            this.plainText = plainText;

        }



        public DataFlavor[] getTransferDataFlavors() {

        	DataFlavor[] dfs = {HTML_FLAVOR, DataFlavor.stringFlavor};
            return dfs;

        }



        public boolean isDataFlavorSupported(DataFlavor flavor) {

            return flavor.getMimeType().contains("text/html") || flavor.getMimeType().contains("text/plain");

        }



        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {

            if (flavor.getMimeType().contains("text/html")) {
            	if (flavor.getRepresentationClass() == java.io.Reader.class) {
                    return new StringReader(html);	
            	} else {
            		return html;
            	}
             } else {
               return plainText;
            }
            
            //throw new UnsupportedFlavorException(flavor);
        }

    } 

}