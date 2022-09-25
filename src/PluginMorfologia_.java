/* Desenvolver um plugin para a aplica��o de opera��es morfol�gicas em imagens bin�rias. 
Implementar as opera��es de dilate, erode, close, open e outline (outline) para imagens bin�rias.
Dever� ser criada uma interface gr�fica contendo bot�es de r�dio para a escolha da t�cnica a ser utilizada.
O elemento estruturante a ser utilizado ser� quadrado com dimens�es 3 x 3 para todas as t�cnicas. */

import ij.plugin.PlugIn;
import ij.ImagePlus;
import ij.gui.GenericDialog;

import ij.IJ;
import ij.process.ImageProcessor;

public class PluginMorfologia_ implements PlugIn{		
	private ImagePlus image;
	private ImageProcessor processorManipulate, processorOriginal;
	private int[][] structure = {{1,1,1},{1,1,1},{1,1,1}};	
	
	public void run(String arg) {		
		image = IJ.getImage();		
		if (image.getType() != ImagePlus.GRAY8) {
			IJ.error("In order to run this plugin, the image must be Type GRAY8");
		}		
		else {					
			processorOriginal = image.getProcessor();	
			convertToBinary();
			processorManipulate = image.duplicate().getProcessor();				
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					processorManipulate.putPixel(x, y, 255);
				}
			}
			this.imageChange();	
		}				
	}	

	public String showGUIReturnMethod(){
		GenericDialog gui = new GenericDialog("T�cnicas de Morfologia");	
		String[] methods = {"dilate","erode","open","close","outline"};		
		gui.addRadioButtonGroup("M�todos dispon�veis", methods, methods.length, 1, methods[0]);		
		gui.showDialog();		
		if (gui.wasOKed()) {			
			return gui.getNextRadioButton();
		}
		else return "exit";		
	}
	
	public void imageChange() {		
		String method = this.showGUIReturnMethod();		
		switch (method) {
		case "dilate":
			dilate();
			break;
		case "erode":
			erode();
			break;
		case "open":
			open();
			break;
		case "close":
			close();
			break;		
		case "outline":
			outline();
			break;
		default:
			IJ.log("Plugin cancelled");
			processorManipulate = processorOriginal.duplicate();
			break;			
		}
		image.setProcessor(processorManipulate);
		image.updateAndDraw();
	}
	
	public void outline() {
		int pixelOriginal, pixelManipulated;
		erode();
		for (int x = 0; x < image.getWidth(); x++) 
			for (int y = 0; y < image.getHeight(); y++) {
				pixelOriginal = processorOriginal.getPixel(x,y);
				pixelManipulated = processorManipulate.getPixel(x, y);
				if (pixelOriginal == 0 && pixelManipulated == 255)
					processorManipulate.putPixel(x, y, 0);						
				else processorManipulate.putPixel(x, y, 255);	
			}				
	}
	
	public void open() {
		erode();
		processorOriginal = processorManipulate.duplicate();
		dilate();
	}
	
	public void close() {
		dilate();		
		processorOriginal = processorManipulate.duplicate();
		erode();
	}
	
	public void dilate() {
		int width = image.getWidth(), height = image.getHeight();		
		for (int x = 1; x < width-1; x++)
			for (int y = 1; y < height-1; y++) 
				if (processorOriginal.getPixel(x,y) == 0) 
					for (int i=0;i<3;i++)
						for (int j=0;j<3;j++) 
							if (structure[i][j] == 1)
								processorManipulate.putPixel(x-1+i, y-1+j, 0);				
	}
	
	public void erode() {
		int countBlack, countOne = 0, width = image.getWidth(), height = image.getHeight();
		for (int[] row : structure)			
		    for (int value : row)			    
		         if (value == 1)
		        	 countOne++;
		for (int x = 1; x < width-1; x++)
			for (int y = 1; y < height-1; y++) {
				if (processorOriginal.getPixel(x,y) == 0) {
					countBlack = 0;
					for (int i=0;i<3;i++)
						for (int j=0;j<3;j++) {
							if (processorOriginal.getPixel(x-1+i, y-1+j) == 0 && structure[i][j] == 1) {
								countBlack++;
							}
						}
					if (countBlack == countOne)
						processorManipulate.putPixel(x, y, 0);
				}
			}				
	}
	
	public void convertToBinary() {		
		int pixel, countNotBin=0, width = image.getWidth(), height = image.getHeight();		
		for (int x = 1; x < width-1; x++)
			for (int y = 1; y < height-1; y++) {
				pixel = processorOriginal.getPixel(x, y);
				if (pixel != 0 &&  pixel != 255)
					countNotBin++;
			}
		if (countNotBin > 0)
			IJ.run(image, "Convert to Mask", "");
	}
	
	
	
}
	